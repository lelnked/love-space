# Contract: App 读响应 schema 变更

仅读路径；mobile app 不参与上传（本特性中）。

## City（已有）

### `GET /api/app/cities`、`GET /api/app/cities/{id}`

`backgroundImage`: `String` → `ImageResponse`（可空）。

## Banner（已有）

### `GET /api/app/banners`（首页 banner 列表）

`images`: `List<String>` → `List<ImageResponse>`。

## Merchant（已有）

### `GET /api/app/merchants`、`GET /api/app/merchants/{id}`

- `logo`: `String` → `ImageResponse`。
- `images`: `List<String>` → `List<ImageResponse>`。

## 共享行为

- 每次响应中所有 `ImageResponse.url` 都是当次新生成的签名 URL（默认 30 分钟过期）。
- DB 列存的是 `bound/<uuid>.<ext>`；响应字段 `id` = 该 boundKey。
- app 端 `ImageUrlSigner` 与 admin 共享同一签名逻辑（不同包路径），但 app 后端只读 OSS（无写权限的 AK / 无 STS 配置）。
- 配置项 `app.storage.oss.urlExpirationSeconds` 在 app 与 admin 可独立配置。
