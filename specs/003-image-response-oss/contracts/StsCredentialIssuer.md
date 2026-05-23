# Contract: `StsCredentialIssuer`

包：`com.loves.space.infrastructure.storage.StsCredentialIssuer`（仅 admin）。

## 接口

```java
public interface StsCredentialIssuer {
    /**
     * 为指定 objectKey 申请单 key 范围的 STS 临时凭证。
     *
     * @param objectKey 服务端已预生成的 {@code images/<uuidv7>.<ext>}；MUST NOT 为 null / 空
     * @return STS 凭证（含 expiration ISO-8601 UTC）
     * @throws IllegalStateException AssumeRole 调用失败（网络 / 配置错）
     */
    StsCredential issueFor(String objectKey);
}

public record StsCredential(
    String accessKeyId, String accessKeySecret, String securityToken, String expiration
) {}
```

## 实现：`AliyunStsCredentialIssuer`

- 持有 `IAcsClient`（由 `StsClientConfig` 注入） + `StsProperties` + `OssProperties`。
- `issueFor` 流程：
  1. 构造 inline policy（限定 `oss:PutObject` 到 `acs:oss:*:*:${ossProperties.bucket}/${objectKey}` 单一资源）。
  2. 调 `AssumeRoleRequest`：`RoleArn = sts.roleArn`、`RoleSessionName = sts.roleSessionName`、`DurationSeconds = sts.durationSeconds`、`Policy = <inline JSON>`。
  3. 取响应 `Credentials.AccessKeyId / AccessKeySecret / SecurityToken / Expiration`，包装成 `StsCredential`。

## 行为契约

| 场景 | 行为 |
|---|---|
| 合法 objectKey | 返回非空 `StsCredential`；`expiration` 为 ISO-8601 UTC。 |
| `objectKey` 为 null / 空 | 抛 `IllegalArgumentException`（service 层应已校验，理论不可达）。 |
| STS 接入失败（网络 / role ARN 错） | 抛 `IllegalStateException`；上层 controller 返回 500。 |
| 凭证生成时 RAM Role policy 与 inline policy 交集为空 | AssumeRole 仍会成功（policy 是 AND 关系，但 inline 不能超出 role policy 边界）；这是配置问题，由启动期 sanity check 兜底。 |

## 测试要求

- 单元测试用 stub 实现返回固定 `StsCredential`，验证 `FileService.issueUploadCredential` 把入参 `contentType` 正确映射到 objectKey 扩展名。
- 不在 CI 跑真实 AssumeRole。
