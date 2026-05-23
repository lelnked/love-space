# Contract: Admin 读响应 schema 变更

所有路径不变；仅响应 body 的图片字段类型变化。

## Banner

### `GET /api/admin/banners`、`GET /api/admin/banners/{id}`

`imageUrls`: `List<String>` → `List<ImageResponse>`。每项含 `id`（典型为 `bound/<uuid>.<ext>`）+ 当次签名 `url`。

## Merchant

### `GET /api/admin/merchants`（列表 `MerchantAdminItem`）

`logo`: `String` → `ImageResponse`。

### `GET /api/admin/merchants/{id}`（详情 `MerchantDetailResponse`）

- `logo`: `String` → `ImageResponse`。
- `images`: `List<String>` → `List<ImageResponse>`。

## City

### `GET /api/admin/cities`（`CityItemResponse`）、`GET /api/admin/cities/{id}`（`CityDetailResponse`）

`backgroundImage`: `String` → `ImageResponse`（可空；DB 中为 null 时响应也为 null）。

## File（写路径）

### `POST /api/admin/files/upload-credentials`

见 `upload-credentials-endpoint.md`。

### `POST /api/admin/files/upload`（已删除）

返回 404 / 405。前端不应再调用。
