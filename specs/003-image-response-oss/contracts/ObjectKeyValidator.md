# Contract: `ObjectKeyValidator`

包：`com.loves.space.infrastructure.storage.ObjectKeyValidator`（仅 admin）。

## 接口

```java
public interface ObjectKeyValidator {
    /**
     * 校验客户端提交的 objectKey，并在业务实体绑定时把对象迁移到 bound 前缀。
     *
     * @param rawObjectKey 客户端提交的 objectKey；可以是 {@code images/<uuid>.<ext>}（新上传）
     *                     或 {@code bound/<uuid>.<ext>}（旧图保留）
     * @return 最终持久化值：始终是 {@code bound/<uuid>.<ext>}
     * @throws ValidationException 任一校验失败；统一错误文案 "图片对象不可用"
     */
    String validateAndBind(String rawObjectKey);
}
```

## 实现：`AliyunOssObjectKeyValidator`

依赖：`OSS` bean + `OssProperties`。

### 流程

1. **字符串前置校验**：
   - 必须匹配 `^(images|bound)/[\w-]+\.(png|jpg|webp)$`；否则 `ValidationException`。
   - 不允许包含 `..` / `\` / 空白（regex 自然排除）。

2. **head 校验**：`oss.getObjectMetadata(bucket, rawObjectKey)`：
   - 抛 `OSSException`（含 NoSuchKey）→ `ValidationException`。
   - `metadata.getContentType()` ∉ `{image/png, image/jpeg, image/webp}` → `ValidationException`。
   - `metadata.getContentLength() > maxImageBytes` → `ValidationException`。

3. **分支处理**：
   - 若 `rawObjectKey` 已是 `bound/...` 形态：返回 `rawObjectKey`（不做 copy）。
   - 若是 `images/...` 形态：
     a. `boundKey = ossProperties.boundKeyPrefix + "/" + uuidPart + "." + ext`（uuidPart 与 ext 从 rawObjectKey 解析）。
     b. `oss.copyObject(bucket, rawObjectKey, bucket, boundKey)`。失败 → `IllegalStateException`（500）。
     c. `oss.deleteObject(bucket, rawObjectKey)` —— best-effort，失败仅记日志，依靠 lifecycle 兜底。
     d. 返回 `boundKey`。

### 并发与一致性

- 同一 `images/<uuid>` 在并发情况下被两个请求同时绑定：CopyObject 会成功两次（无锁），DeleteObject 第二次返回 NoSuchKey 但不影响主流程；两条业务记录都会持有同一 `bound/<uuid>`。本特性允许该行为（图片去重不在范围内）。

### 错误信息脱敏

对外抛出的 `ValidationException` message 统一为 "图片对象不可用"；具体原因（不存在 / MIME / 大小 / copy 失败）只记入 service 层日志。

## 测试要求

- 单元测试用 stub 实现 OSS 客户端（持有 `Map<key, ObjectMetadata>` + `Set<key>`），断言：
  - 不存在 → throws
  - MIME 错 → throws
  - 太大 → throws
  - 路径穿越 → throws
  - 合法 images/ → 返回 bound/，map 内 src 被删
  - 已是 bound/ + 存在 → 返回原值
  - 已是 bound/ + 不存在 → throws
