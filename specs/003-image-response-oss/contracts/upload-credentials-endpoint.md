# Contract: `POST /api/admin/files/upload-credentials`

下发单 key 范围的 STS 临时凭证 + 服务端预生成的 objectKey，供前端 `ali-oss` SDK 直传 OSS。

## 鉴权

- 沿用现有 admin JWT 鉴权（`OperatingContext` 已注入）；未登录 / token 失效 → 401。
- MUST 写入 `OperationLog`（操作类型 `file:credentials`）。

## Request

```http
POST /api/admin/files/upload-credentials
Content-Type: application/json
Authorization: Bearer <admin JWT>

{
  "contentType": "image/png"
}
```

| Field | Type | Validation |
|---|---|---|
| `contentType` | `String` | `@NotBlank`、`@Pattern("^image/(png\|jpeg\|webp)$")` |

非白名单 MIME → 400 + 校验错误。

## Response 200

```json
{
  "accessKeyId": "STS.NTcWXXXXX",
  "accessKeySecret": "xxx",
  "securityToken": "CAISxxxxxx",
  "expiration": "2026-05-23T08:15:00Z",
  "objectKey": "images/0193f5bd-7c43-7c2e-9211-2c5b3c4f9ee0.png",
  "region": "oss-cn-shanghai",
  "bucket": "love-space-dev"
}
```

- `expiration`：ISO-8601 UTC，相对发出时刻 ≤ `app.storage.sts.duration-seconds`（默认 900）。
- `objectKey`：MUST 以 `app.storage.oss.upload-key-prefix` 开头；扩展名按 `contentType` 反查（png→`png`、jpeg→`jpg`、webp→`webp`）。
- 同一 `contentType` 多次调用 MUST 返回不同 `objectKey`。

## Error Responses

| 状态 | 触发 |
|---|---|
| 400 | `contentType` 校验失败 |
| 401 | 未登录 / JWT 失效 |
| 500 | STS AssumeRole 调用失败；错误信息脱敏 |
| 503 | 启动期 STS 自检失败 → 服务不健康（理论上启动失败应已阻止启动） |

## 行为契约

- 服务端 MUST 在 STS AssumeRole 时内嵌 policy 限制本次凭证仅能 `oss:PutObject` 到本次返回的 `objectKey`（单 key 最小授权）；详见 research R3。
- MUST NOT 在响应中泄露任何 service / role / 主账号 AK 信息。
- MUST 不缓存凭证；每次请求独立生成（前端可缓存返回值至 expiration 前）。

## 取消的旧端点

`POST /api/admin/files/upload`（multipart 服务端代理上传）MUST 移除；旧路径返回 404 / 405。前端 / 自动化脚本如有引用 MUST 迁移到 upload-credentials。
