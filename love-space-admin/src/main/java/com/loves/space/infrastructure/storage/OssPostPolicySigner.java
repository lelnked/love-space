package com.loves.space.infrastructure.storage;

import com.aliyun.oss.common.utils.BinaryUtil;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
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

    /** SigningKey 派生用的固定段（OSS V4 规范）。 */
    private static final String SIGNING_KEY_SECRET_PREFIX = "aliyun_v4";
    private static final String SIGNING_SERVICE = "oss";
    private static final String SIGNING_KEY_TERMINATOR = "aliyun_v4_request";

    private final StorageProperties storageProperties;
    private final ObjectMapper objectMapper;

    public OssPostPolicySigner(StorageProperties storageProperties, ObjectMapper objectMapper) {
        this.storageProperties = storageProperties;
        this.objectMapper = objectMapper;
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
        String region = storageProperties.signingRegion();
        String xOssCredential = credential.accessKeyId() + "/" + date + "/" + region
                + "/" + SIGNING_SERVICE + "/" + SIGNING_KEY_TERMINATOR;

        // 步骤 1：构造 Policy JSON（Jackson 负责转义与类型，OSS 解码 Base64 后自行解析）。
        String policyJson = buildPolicyJson(objectKey, expiration, xOssCredential, xOssDate, credential);

        // 步骤 2：Base64(Policy JSON) 即 StringToSign。
        String stringToSign = Base64.getEncoder()
                .encodeToString(policyJson.getBytes(StandardCharsets.UTF_8));

        // 步骤 3：派生 SigningKey。
        byte[] dateKey = hmacSha256(
                (SIGNING_KEY_SECRET_PREFIX + credential.accessKeySecret()).getBytes(StandardCharsets.UTF_8), date);
        byte[] dateRegionKey = hmacSha256(dateKey, region);
        byte[] dateRegionServiceKey = hmacSha256(dateRegionKey, SIGNING_SERVICE);
        byte[] signingKey = hmacSha256(dateRegionServiceKey, SIGNING_KEY_TERMINATOR);

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

    /**
     * 把 OSS endpoint 拼成带 bucket 的虚拟主机地址，例如 {@code https://my-bucket.oss-cn-shanghai.aliyuncs.com}。
     *
     * <p>兼容 endpoint 配置带或不带协议头：缺失时补 {@code https://}，确保返回的是浏览器可直接 POST 的绝对地址。
     */
    private String buildHost() {
        String endpoint = storageProperties.oss().endpoint();
        String scheme = "https://";
        String hostWithoutScheme = endpoint;
        int schemeEnd = endpoint.indexOf("://");
        if (schemeEnd >= 0) {
            scheme = endpoint.substring(0, schemeEnd + "://".length());
            hostWithoutScheme = endpoint.substring(schemeEnd + "://".length());
        }
        return scheme + storageProperties.oss().bucket() + "." + hostWithoutScheme;
    }

    /**
     * 构造 PostObject Policy JSON。
     *
     * <p>{@code conditions} 里既有对象条件也有数组条件；{@code key} 用 {@code eq} 锁死，
     * 表单只能上传到服务端预生成的这个 key。Jackson 负责所有转义与数字类型。
     */
    private String buildPolicyJson(String objectKey, Instant expiration,
                                   String xOssCredential, String xOssDate, StsCredential credential) {
        ObjectNode policy = objectMapper.createObjectNode();
        policy.put("expiration", POLICY_EXPIRATION_FORMATTER.format(expiration));
        ArrayNode conditions = policy.putArray("conditions");
        conditions.addObject().put("bucket", storageProperties.oss().bucket());
        conditions.addObject().put("x-oss-security-token", credential.securityToken());
        conditions.addObject().put("x-oss-signature-version", SIGNATURE_VERSION);
        conditions.addObject().put("x-oss-credential", xOssCredential);
        conditions.addObject().put("x-oss-date", xOssDate);
        conditions.addArray().add("content-length-range").add(1).add(storageProperties.oss().maxImageBytes());
        conditions.addArray().add("eq").add("$success_action_status").add("200");
        conditions.addArray().add("eq").add("$key").add(objectKey);
        return objectMapper.writeValueAsString(policy);
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
