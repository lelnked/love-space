# Contract: `POST /api/admin/files/upload-credentials`

服务端用单 key 范围的 STS 临时凭证计算 OSS 表单直传（PostObject）的 V4 签名（`OSS4-HMAC-SHA256`），连同预生成的 objectKey 一并下发；前端用 `multipart/form-data` 表单 POST 直传 OSS。**响应中不含 AccessKeySecret，浏览器不接触任何密钥。**

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
  "host": "https://love-space-dev.oss-cn-shanghai.aliyuncs.com",
  "objectKey": "images/0193f5bd-7c43-7c2e-9211-2c5b3c4f9ee0.png",
  "policy": "eyJleHBpcmF0aW9uIjoi...",
  "signature": "8f3b1c...e2a9",
  "signatureVersion": "OSS4-HMAC-SHA256",
  "xOssCredential": "STS.NTcWXXXXX/20260523/cn-shanghai/oss/aliyun_v4_request",
  "xOssDate": "20260523T080000Z",
  "securityToken": "CAISxxxxxx",
  "expiration": "2026-05-23T08:15:00Z"
}
```

| Field | 含义 |
|---|---|
| `host` | 表单提交地址（带 bucket 的虚拟主机域名），由 `app.storage.oss.endpoint` 插入 bucket 拼成 |
| `objectKey` | 目标 key；表单 `key` 字段 MUST 与之相等 |
| `policy` | Base64 编码的 Policy（即 StringToSign）；表单 `policy` 字段 |
| `signature` | V4 签名（hex）；表单 `x-oss-signature` 字段 |
| `signatureVersion` | 固定 `OSS4-HMAC-SHA256`；表单 `x-oss-signature-version` 字段 |
| `xOssCredential` | `<临时AccessKeyId>/<yyyyMMdd>/<region>/oss/aliyun_v4_request`；表单 `x-oss-credential` 字段 |
| `xOssDate` | `yyyyMMdd'T'HHmmss'Z'`（UTC）；表单 `x-oss-date` 字段 |
| `securityToken` | STS 会话令牌；表单 `x-oss-security-token` 字段 |
| `expiration` | 签名 / 凭证过期时间（ISO-8601 UTC），供前端参考 |

- `objectKey`：MUST 以 `app.storage.oss.upload-key-prefix` 开头；扩展名按 `contentType` 反查（png→`png`、jpeg→`jpg`、webp→`webp`）。
- 同一 `contentType` 多次调用 MUST 返回不同 `objectKey`。
- Policy `expiration` 直接取 STS 凭证过期时间（≤ `app.storage.sts.duration-seconds`，默认 900），保证签名不比凭证活得更久。
- Policy conditions 固定：`eq $key <objectKey>`、`content-length-range 1 <app.storage.oss.max-image-bytes>`、`eq $success_action_status 200`、以及 `bucket` / `x-oss-security-token` / `x-oss-signature-version` / `x-oss-credential` / `x-oss-date`。

## 前端表单直传

前端拿到响应后构造 `multipart/form-data` POST 到 `host`，表单域如下（`file` MUST 为最后一个表单域）：

```
key                       = objectKey
policy                    = policy
x-oss-signature           = signature
x-oss-signature-version   = signatureVersion
x-oss-credential          = xOssCredential
x-oss-date                = xOssDate
x-oss-security-token      = securityToken
success_action_status     = 200
file                      = <文件二进制>
```

上传成功 OSS 返回 200（因 `success_action_status=200`）；前端据此判定成功并把 `objectKey` 回传业务表单。

## Error Responses

| 状态 | 触发 |
|---|---|
| 400 | `contentType` 校验失败 |
| 401 | 未登录 / JWT 失效 |
| 500 | STS AssumeRole 调用失败；错误信息脱敏 |
| 503 | 启动期 STS 自检失败 → 服务不健康（理论上启动失败应已阻止启动） |

## 行为契约

- 服务端 MUST 在 STS AssumeRole 时内嵌 policy 限制本次凭证仅能 `oss:PutObject` 到本次返回的 `objectKey`（单 key 最小授权）；详见 research R3。
- 服务端 MUST 用 STS 临时凭证在服务端计算 PostObject 签名；**MUST NOT 把 `accessKeySecret` 下发到浏览器**。
- MUST NOT 在响应中泄露任何 service / role / 主账号 AK 信息。
- MUST 不缓存凭证；每次请求独立生成签名（前端可缓存返回值至 expiration 前）。

## 取消的旧端点

`POST /api/admin/files/upload`（multipart 服务端代理上传）MUST 移除；旧路径返回 404 / 405。前端 / 自动化脚本如有引用 MUST 迁移到 upload-credentials。
