# TC-operation-log-IT-004 请求/响应存证

> 创建运营账号的 payload 中 password 被脱敏
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 基线日志

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=manager&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a17740-7c57-7c06-b1a3-c5ab91eeda36",
   "username": "admin",
   "module": "manager",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-26T08:07:57.270334Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 2 创建运营账号

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/managers' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"username": "logredactc015d5fa", "password": "P@ssw0rd123", "nickname": "脱敏用例"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a17740-8f58-7187-bec3-d1ce92fd4e9b",
 "username": "logredactc015d5fa",
 "nickname": "脱敏用例",
 "role": "MEMBER",
 "enable": true,
 "createdAt": "2026-10-26T08:08:02.136026931Z"
}
```

## step 3 轮询日志（第 1 次命中）module=manager

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=manager&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a17740-8f5b-7be4-8ce9-f46bd920bae5",
   "username": "admin",
   "module": "manager",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-26T08:08:02.13969Z"
  },
  {
   "id": "01a17740-7c57-7c06-b1a3-c5ab91eeda36",
   "username": "admin",
   "module": "manager",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-26T08:07:57.270334Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 2,
 "totalPages": 1
}
```

## step 4 psql 查最新 manager:create payload

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT payload FROM loves_operation_log WHERE module='"'"'manager'"'"' AND action='"'"'create'"'"' ORDER BY created_at DESC LIMIT 1'
```

```
{"nickname": "脱敏用例", "password": "[REDACTED]", "username": "logredactc015d5fa"}
```
