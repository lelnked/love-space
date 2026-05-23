package com.loves.space.infrastructure.storage;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import org.springframework.stereotype.Component;

/**
 * 调阿里云 STS {@code AssumeRole} 下发临时凭证；inline policy 将权限收敛到单一 objectKey。
 */
@Component
public class AliyunStsCredentialIssuer implements StsCredentialIssuer {

    private static final String POLICY_TEMPLATE = """
            {"Version":"1","Statement":[{"Effect":"Allow","Action":["oss:PutObject"],\
            "Resource":["acs:oss:*:*:%s/%s"]}]}""";

    private final IAcsClient acsClient;
    private final StsProperties stsProperties;
    private final OssProperties ossProperties;

    public AliyunStsCredentialIssuer(IAcsClient acsClient,
                                     StsProperties stsProperties,
                                     OssProperties ossProperties) {
        this.acsClient = acsClient;
        this.stsProperties = stsProperties;
        this.ossProperties = ossProperties;
    }

    @Override
    public StsCredential issueFor(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new IllegalArgumentException("objectKey 不能为空");
        }
        AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRoleArn(stsProperties.roleArn());
        request.setRoleSessionName(stsProperties.roleSessionName());
        request.setDurationSeconds(stsProperties.durationSeconds());
        request.setPolicy(POLICY_TEMPLATE.formatted(ossProperties.bucket(), objectKey));
        try {
            AssumeRoleResponse response = acsClient.getAcsResponse(request);
            AssumeRoleResponse.Credentials credentials = response.getCredentials();
            return new StsCredential(
                    credentials.getAccessKeyId(),
                    credentials.getAccessKeySecret(),
                    credentials.getSecurityToken(),
                    credentials.getExpiration()
            );
        } catch (ClientException e) {
            throw new IllegalStateException("STS AssumeRole 失败", e);
        }
    }
}
