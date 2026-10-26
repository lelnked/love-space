# TC-ai-IT-090 请求/响应存证

> PUT 非法取值返回 400 且数据不变
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lc25cc4459`（fixture 密码 `Member#2026x`）；场景 `t_lc25cc4459`（主模型 mock-ok，模板 `01a17742-7d68-7052-aab3-e4a752e6685e` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 GET 基线

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/limits' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
[
 {
  "key": "GLOBAL_DAILY",
  "name": "全站每日上限",
  "value": 5000,
  "updatedBy": null,
  "updatedAt": null
 },
 {
  "key": "PER_ANON_DAILY",
  "name": "单个匿名 ID 每日上限",
  "value": 50,
  "updatedBy": null,
  "updatedAt": null
 }
]
```

## step 2 PUT GLOBAL_DAILY body {"value":0}

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/GLOBAL_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value":0}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "取值须为 -1 或正整数",
 "path": "/api/admin/ai/limits/GLOBAL_DAILY"
}
```

## step 3 PUT GLOBAL_DAILY body {"value":-2}

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/GLOBAL_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value":-2}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "取值须为 -1 或正整数",
 "path": "/api/admin/ai/limits/GLOBAL_DAILY"
}
```

## step 4 PUT GLOBAL_DAILY body {}

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/GLOBAL_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "取值须为 -1 或正整数",
 "path": "/api/admin/ai/limits/GLOBAL_DAILY"
}
```

## step 5 PUT GLOBAL_DAILY body {"value":1.5}

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/GLOBAL_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value":1.5}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "取值须为 -1 或正整数",
 "path": "/api/admin/ai/limits/GLOBAL_DAILY"
}
```

## step 6 GET 复核

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/limits' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
[
 {
  "key": "GLOBAL_DAILY",
  "name": "全站每日上限",
  "value": 5000,
  "updatedBy": null,
  "updatedAt": null
 },
 {
  "key": "PER_ANON_DAILY",
  "name": "单个匿名 ID 每日上限",
  "value": 50,
  "updatedBy": null,
  "updatedAt": null
 }
]
```
