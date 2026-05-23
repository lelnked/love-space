# Contract: `ImageUrlSigner`

包路径：
- admin: `com.loves.space.infrastructure.storage.ImageUrlSigner`
- app: `com.space.app.infrastructure.storage.ImageUrlSigner`

## 接口

```java
public interface ImageUrlSigner {
    /**
     * 对给定 objectKey（即 ImageResponse.id；典型为 {@code bound/<uuid>.<ext>}）
     * 即时生成带签名的 GET URL。
     *
     * @param objectKey 图片 id / OSS 对象 key；为 {@code null} / 空白时返回 {@code null}。
     * @return 包含签名参数的绝对 URL；调用方将其放入 {@link ImageResponse#url}。
     */
    String sign(String objectKey);
}
```

## 行为契约

| 输入 | 期望输出 |
|---|---|
| 有效 imageId | 绝对 https URL，含 `OSSAccessKeyId`、`Expires`、`Signature` 三个 query 参数。 |
| `null` | 返回 `null`（用于可空字段如 `City.backgroundImage`）。 |
| 空白字符串 | 返回 `null`。 |
| OSS 对象不存在 | **照常签名**，不做存在性探测；客户端访问时由 OSS 返回 404 / NoSuchKey，**不是** signer 的职责。 |

## 实现：`AliyunOssImageUrlSigner`

- 依赖 `OSS` bean + `OssProperties`。
- 过期时间：`new Date(System.currentTimeMillis() + ossProperties.urlExpirationSeconds() * 1000L)`。
- 调用 `oss.generatePresignedUrl(bucket, objectKey, expiration, HttpMethod.GET).toString()`。
- MUST NOT 缓存签名结果（每次调用重新计算）。
- MUST 线程安全（`OSSClient` 自身线程安全）。

## 非目标

- 不提供"批量签名"接口；调用方用 `ImageResponses.fromList` 在 service 层做批量循环即可（开销可忽略）。
- 不提供"刷新签名"专用端点；客户端通过重新调用列表 / 详情拿到新签名。
