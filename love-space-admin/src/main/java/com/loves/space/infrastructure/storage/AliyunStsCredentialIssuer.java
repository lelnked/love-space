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
    private final StorageProperties storageProperties;

    public AliyunStsCredentialIssuer(IAcsClient acsClient,
                                     StorageProperties storageProperties) {
        this.acsClient = acsClient;
        this.storageProperties = storageProperties;
    }

    @Override
    public StsCredential issueFor(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new IllegalArgumentException("objectKey 不能为空");
        }
        StorageProperties.Sts sts = storageProperties.sts();
        AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRoleArn(sts.roleArn());
        request.setRoleSessionName(sts.roleSessionName());
        request.setDurationSeconds(sts.durationSeconds());
        request.setPolicy(POLICY_TEMPLATE.formatted(storageProperties.oss().bucket(), objectKey));
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
