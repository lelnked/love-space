package com.loves.space.infrastructure.storage;

import com.aliyun.oss.common.utils.BinaryUtil;
import com.loves.space.infrastructure.storage.StsCredentialIssuer.StsCredential;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * 计算 OSS 表单直传（PostObject）的 V4 签名（{@code OSS4-HMAC-SHA256}）。
 *
 * <p>用 STS 临时凭证在服务端完成 Policy 构造与签名，浏览器只拿到
 * policy / signature / securityToken，永远不接触 AccessKeySecret。
 * 算法参考阿里云官方「服务端签名 + 前端直传」示例。
 */
@Component
public class OssPostPolicySigner {

    /** {@code x-oss-credential} 与 SigningKey 中的日期段，格式 {@code yyyyMMdd}（UTC）。 */
    private static final DateTimeFormatter CREDENTIAL_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    /** {@code x-oss-date} 格式 {@code yyyyMMdd'T'HHmmss'Z'}（UTC）。 */
    private static final DateTimeFormatter OSS_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);

    /** Policy {@code expiration} 字段格式 {@code yyyy-MM-dd'T'HH:mm:ss.SSS'Z'}（UTC）。 */
    private static final DateTimeFormatter POLICY_EXPIRATION_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    private static final String SIGNATURE_VERSION = "OSS4-HMAC-SHA256";

    private final OssProperties ossProperties;

    public OssPostPolicySigner(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    /**
     * 为指定 objectKey 计算 PostObject 表单签名。
     *
     * <p>Policy 过期时间直接取 STS 凭证过期时间，确保签名不会比凭证活得更久。
     *
     * @param objectKey  服务端预生成的目标 key（表单的 {@code key} 字段必须与之完全相等）
     * @param credential STS 临时凭证（提供 accessKeyId / accessKeySecret / securityToken）
     * @return 前端 PostObject 所需的全部表单字段
     */
    public OssPostSignature sign(String objectKey, StsCredential credential) {
        Instant now = Instant.now();
        Instant expiration = Instant.parse(credential.expiration());
        String date = CREDENTIAL_DATE_FORMATTER.format(now);
        String xOssDate = OSS_DATE_FORMATTER.format(now);
        // 签名 scope 里的 region 必须不带 oss- 前缀，例如 cn-shanghai。
        String region = ossProperties.region().startsWith("oss-")
                ? ossProperties.region().substring("oss-".length())
                : ossProperties.region();
        String xOssCredential = credential.accessKeyId() + "/" + date + "/" + region + "/oss/aliyun_v4_request";

        // 步骤 1：构造 Policy JSON。
        String policyJson = "{"
                + "\"expiration\":\"" + POLICY_EXPIRATION_FORMATTER.format(expiration) + "\","
                + "\"conditions\":["
                + "{\"bucket\":\"" + escapeJson(ossProperties.bucket()) + "\"},"
                + "{\"x-oss-security-token\":\"" + escapeJson(credential.securityToken()) + "\"},"
                + "{\"x-oss-signature-version\":\"" + SIGNATURE_VERSION + "\"},"
                + "{\"x-oss-credential\":\"" + escapeJson(xOssCredential) + "\"},"
                + "{\"x-oss-date\":\"" + xOssDate + "\"},"
                + "[\"content-length-range\",1," + ossProperties.maxImageBytes() + "],"
                + "[\"eq\",\"$success_action_status\",\"200\"],"
                // 服务端已固定完整 key，表单只能上传到这个 key。
                + "[\"eq\",\"$key\",\"" + escapeJson(objectKey) + "\"]"
                + "]}";

        // 步骤 2：Base64(Policy JSON) 即 StringToSign。
        String stringToSign = Base64.getEncoder()
                .encodeToString(policyJson.getBytes(StandardCharsets.UTF_8));

        // 步骤 3：派生 SigningKey。
        byte[] dateKey = hmacSha256(
                ("aliyun_v4" + credential.accessKeySecret()).getBytes(StandardCharsets.UTF_8), date);
        byte[] dateRegionKey = hmacSha256(dateKey, region);
        byte[] dateRegionServiceKey = hmacSha256(dateRegionKey, "oss");
        byte[] signingKey = hmacSha256(dateRegionServiceKey, "aliyun_v4_request");

        // 步骤 4：计算 Signature（hex）。
        String signature = BinaryUtil.toHex(hmacSha256(signingKey, stringToSign));

        return new OssPostSignature(
                buildHost(),
                objectKey,
                stringToSign,
                signature,
                SIGNATURE_VERSION,
                xOssCredential,
                xOssDate,
                credential.securityToken(),
                credential.expiration()
        );
    }

    /** 把 OSS endpoint 拼成带 bucket 的虚拟主机地址，例如 {@code https://my-bucket.oss-cn-shanghai.aliyuncs.com}。 */
    private String buildHost() {
        return ossProperties.endpoint().replaceFirst("://", "://" + ossProperties.bucket() + ".");
    }

    /** 转义 JSON 字符串值中的反斜杠与双引号（Policy 字段值均为 ASCII，无需处理控制字符）。 */
    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static byte[] hmacSha256(byte[] key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("计算 HMAC-SHA256 失败", e);
        }
    }

    /**
     * PostObject 表单签名结果。
     *
     * @param host             表单 action 地址（带 bucket 的虚拟主机域名）
     * @param objectKey        目标 key，表单 {@code key} 字段须与之相等
     * @param policy           Base64 编码的 Policy（表单 {@code policy} 字段）
     * @param signature        V4 签名（表单 {@code x-oss-signature} 字段）
     * @param signatureVersion 签名版本，固定 {@code OSS4-HMAC-SHA256}
     * @param xOssCredential   {@code x-oss-credential} 字段
     * @param xOssDate         {@code x-oss-date} 字段
     * @param securityToken    {@code x-oss-security-token} 字段
     * @param expiration       凭证 / 签名过期时间（ISO-8601 UTC，仅供前端参考）
     */
    public record OssPostSignature(
            String host,
            String objectKey,
            String policy,
            String signature,
            String signatureVersion,
            String xOssCredential,
            String xOssDate,
            String securityToken,
            String expiration
    ) {
    }
}
