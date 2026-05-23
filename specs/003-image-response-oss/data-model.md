# Data Model: STS 直传 OSS + 统一 ImageResponse

本特性不引入新的 JPA 实体；改动集中在：
1. 新增/保留的对外 DTO（含 `UploadCredentialResponse`、`ImageResponse`）。
2. 配置对象（`OssProperties`、`StsProperties`）。
3. 内部接口（`StsCredentialIssuer`、`ObjectKeyValidator`、`ImageUrlSigner`）。
4. 现有业务实体列的**语义变更**（持久化值由 URL 改为 OSS 对象 key，落在 `bound/<uuid>.<ext>`）。

## 1. 对外 DTO

### 1.1 `ImageResponse`（已存在）

| Field | Type | Nullability | 说明 |
|---|---|---|---|
| `id` | `String` | `@NotNull` | OSS 对象 key（绑定后形态：`bound/<uuidv7>.<ext>`）。 |
| `url` | `String` | `@NotNull` | 当次签名生成的 GET URL；含签名 query 参数。 |

定义位置：admin `com.loves.space.common.dto.ImageResponse`（已存在）；app `com.space.app.common.dto.ImageResponse`（新增，字段一致）。

### 1.2 `UploadCredentialRequest`（新增，admin）

| Field | Type | Validation | 说明 |
|---|---|---|---|
| `contentType` | `String` | `@NotBlank` + `@Pattern("^image/(png\|jpeg\|webp)$")` | 客户端声明的图片 MIME。 |

### 1.3 `UploadCredentialResponse`（新增，admin）

| Field | Type | Nullability | 说明 |
|---|---|---|---|
| `accessKeyId` | `String` | 非空 | STS 临时 AK。 |
| `accessKeySecret` | `String` | 非空 | STS 临时 SK。 |
| `securityToken` | `String` | 非空 | STS 临时 Token，配合 SDK 使用。 |
| `expiration` | `String` | 非空 | ISO-8601 UTC 时间，凭证失效时刻。 |
| `objectKey` | `String` | 非空 | 服务端预生成的目标 key，形如 `images/<uuidv7>.<ext>`；客户端 PUT 时 MUST 使用此 key，不得改动。 |
| `region` | `String` | 非空 | OSS region，如 `oss-cn-shanghai`，供 `ali-oss` SDK 使用。 |
| `bucket` | `String` | 非空 | OSS bucket 名。 |

### 1.4 `ImageResponses`（工具类，admin + app 各一份）

| Method | 行为 |
|---|---|
| `from(objectKey, signer)` | objectKey 为 `null` / 空 → 返回 `null`；否则返回 `new ImageResponse(objectKey, signer.sign(objectKey))`。 |
| `fromList(objectKeys, signer)` | 列表为 `null` → 返回空列表；否则按顺序 map。 |

## 2. 配置对象

### 2.1 `OssProperties`（admin + app）

`@ConfigurationProperties("app.storage.oss")` + `@Validated`：

| Field | Type | Validation | Default | 说明 |
|---|---|---|---|---|
| `endpoint` | `String` | `@NotBlank` | — | OSS 接入域名。 |
| `bucket` | `String` | `@NotBlank` | — | bucket 名。 |
| `region` | `String` | `@NotBlank` | — | OSS region。 |
| `accessKeyId` | `String` | `@NotBlank` | — | 主 AK（服务端读签名 + 绑定时操作 + headObject 用）。 |
| `accessKeySecret` | `String` | `@NotBlank` | — | 主 SK。 |
| `uploadKeyPrefix` | `String` | `@NotBlank` | `images` | 直传落点前缀；lifecycle 在此前缀 24h 清理。 |
| `boundKeyPrefix` | `String` | `@NotBlank` | `bound` | 绑定后归档前缀；不被 lifecycle 影响。 |
| `urlExpirationSeconds` | `int` | `@Min(60)` | `1800` | 读签名 URL 有效期（秒）。 |
| `maxImageBytes` | `long` | `@Min(1024)` | `20971520` | 业务绑定时校验的最大 Content-Length（默认 20MB）。 |

### 2.2 `StsProperties`（仅 admin）

`@ConfigurationProperties("app.storage.sts")` + `@Validated`：

| Field | Type | Validation | Default | 说明 |
|---|---|---|---|---|
| `endpoint` | `String` | `@NotBlank` | `https://sts.aliyuncs.com` | STS 接入域名。 |
| `roleArn` | `String` | `@NotBlank` | — | 要 AssumeRole 的 RAM Role ARN。 |
| `roleSessionName` | `String` | `@NotBlank` | `love-space-admin-upload` | 凭证 session 标识。 |
| `durationSeconds` | `int` | `@Min(900)` + `@Max(3600)` | `900` | STS 凭证有效期，阿里硬限制 900–3600。 |
| `accessKeyId` | `String` | `@NotBlank` | — | 主账号下用于 AssumeRole 的 AK（与 OSS 主 AK 可不同；最小权限：仅 `sts:AssumeRole`）。 |
| `accessKeySecret` | `String` | `@NotBlank` | — | 上述 AK 的 SK。 |

## 3. 内部接口

### 3.1 `StsCredentialIssuer`（admin）

```java
public interface StsCredentialIssuer {
    /**
     * 为指定 objectKey 申请单 key 范围的 STS 凭证。
     */
    StsCredential issueFor(String objectKey);
}

public record StsCredential(
    String accessKeyId, String accessKeySecret, String securityToken, String expiration
) {}
```

实现：`AliyunStsCredentialIssuer`，构造时持有 `IAcsClient` + `StsProperties`；`issueFor` 内嵌单 key inline policy（见 research R3）调 `AssumeRole`。

### 3.2 `ObjectKeyValidator`（admin）

```java
public interface ObjectKeyValidator {
    /**
     * 校验客户端提交的 objectKey 真实可用，并把对象迁移到 bound 前缀。
     *
     * @return 迁移后的最终 key（如 {@code bound/<uuidv7>.<ext>}），用于业务表持久化
     */
    String validateAndBind(String objectKey);
}
```

实现：`AliyunOssObjectKeyValidator`：

1. 前置 `@Pattern("^images/[\\w-]+\\.(png|jpg|webp)$")` 失败 → `ValidationException("图片对象不可用")`。
2. `oss.getObjectMetadata(bucket, objectKey)`：
   - 抛 OSSException → `ValidationException`。
   - `Content-Type` 不在白名单 → `ValidationException`。
   - `Content-Length > maxImageBytes` → `ValidationException`。
3. `oss.copyObject(bucket, srcKey, bucket, boundKey)`，`boundKey = boundKeyPrefix + "/" + uuidPart + "." + ext`（uuidPart 取自 srcKey）。
4. `oss.deleteObject(bucket, srcKey)`（best-effort；失败仅记日志，srcKey 反正会被 lifecycle 24h 删）。
5. 返回 `boundKey`。

### 3.3 `ImageUrlSigner`（admin + app）

```java
public interface ImageUrlSigner {
    /**
     * 对给定 objectKey 即时生成带签名的 GET URL。null / 空 → null。
     */
    String sign(String objectKey);
}
```

实现：`AliyunOssImageUrlSigner`，调 `oss.generatePresignedUrl(bucket, objectKey, expirationDate, HttpMethod.GET)`。

## 4. 业务实体列语义变更

DDL 不变；列值含义变更为最终 boundKey（如 `bound/01abc….png`）。

| 模块 | 实体 | 列 | 旧含义 | 新含义 |
|---|---|---|---|---|
| banner (admin+app) | `Banner` | `image_urls` (jsonb) | URL 数组 | boundKey 数组 |
| merchant (admin+app) | `Merchant` | `logo` | URL | boundKey |
| merchant (admin+app) | `MerchantImage` | `image_url` | URL | boundKey |
| city (admin+app) | `City` | `background_image` | URL（可空） | boundKey（可空） |

DTO 字段名（`imageUrls` / `logo` / `images` / `backgroundImage`）保持不变；中文 JavaDoc MUST 更新说明 "对象 key (`bound/<uuid>.<ext>`)"。

## 5. 请求 / 响应 DTO 矩阵

### 5.1 请求 DTO（接收**客户端直传后**的 `images/<uuid>.<ext>` objectKey）

| Module | DTO | 字段 | 类型 | 校验 |
|---|---|---|---|---|
| banner | `BannerCreateRequest` / `BannerUpdateRequest` | `imageUrls` | `List<@Pattern("^images/[\w-]+\.(png\|jpg\|webp)$") String>` | `@NotEmpty` |
| merchant | `MerchantUpsertRequest` | `logo` | `String` | `@NotBlank` + `@Pattern(...)` |
| merchant | `MerchantUpsertRequest` | `images` | `List<...>` | 同 banner |
| city | `CityCreateRequest` / `CityUpdateRequest` | `backgroundImage` | `String` | 可空，非空时 `@Pattern(...)` |

注：客户端提交的总是 `images/<uuid>.<ext>` 形态；服务端 `ObjectKeyValidator#validateAndBind` 返回 `bound/<uuid>.<ext>` 并持久化。

### 5.2 响应 DTO（输出 `ImageResponse`）

| Module | DTO | 字段 | 类型 |
|---|---|---|---|
| file (admin) | `FileController.issueUploadCredential` | — | `UploadCredentialResponse` |
| banner (admin) | `BannerDetailResponse` / `BannerListItemResponse` | `imageUrls` | `List<ImageResponse>` |
| banner (app) | `BannerItemResponse` | `image` / `imageUrls` | `List<ImageResponse>` |
| merchant (admin) | `MerchantDetailResponse` | `logo`, `images` | `ImageResponse`, `List<ImageResponse>` |
| merchant (admin) | `MerchantAdminItem` | `logo` | `ImageResponse` |
| merchant (app) | `MerchantListItemResponse` | `logo` | `ImageResponse` |
| merchant (app) | `MerchantDetailResponse` | `logo`, `images` | `ImageResponse`, `List<ImageResponse>` |
| city (admin) | `CityItemResponse` / `CityDetailResponse` | `backgroundImage` | `ImageResponse`（可空） |
| city (app) | `CityItemResponse` | `backgroundImage` | `ImageResponse` |

## 6. 状态机

OSS 对象在系统内的"状态"由前缀决定：

```text
                client PUT                 server validate + copy + delete
  (不存在) ─────────────────► images/<uuid>.<ext> ───────────────────────► bound/<uuid>.<ext>
                                       │                                       │
                                       │ 24h 未绑定                            │ 永不过期（lifecycle 不覆盖此前缀）
                                       ▼                                       ▼
                                  lifecycle 删除                          业务表持有
```

- 业务删除（如删 Banner）：本特性内**不**联动删 OSS 对象（保留以备审计 / 历史回溯）；后续若需"删 banner 即删图"另开工单。
