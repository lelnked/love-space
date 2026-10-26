# TC-ai-IT-093 请求/响应存证

> MEMBER 可查看与修改限流配置，未登录 401
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 登录 u_lcc015d5fa

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{"username": "u_lcc015d5fa", "password": "Member#2026x"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "token": "$TOKEN",
 "manager": {
  "id": "01a17740-7c39-7f46-aa1e-e91e67673bf7",
  "username": "u_lcc015d5fa",
  "nickname": "限流成员",
  "role": "MEMBER"
 }
}
```

## step 2 MEMBER GET 限流配置

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/limits' \
  -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
[
 {
  "key": "GLOBAL_DAILY",
  "name": "全站每日上限",
  "value": 5000,
  "updatedBy": "admin",
  "updatedAt": "2026-10-26T08:07:57.931018Z"
 },
 {
  "key": "PER_ANON_DAILY",
  "name": "单个匿名 ID 每日上限",
  "value": 50,
  "updatedBy": "admin",
  "updatedAt": "2026-10-26T08:07:57.915148Z"
 }
]
```

## step 3 MEMBER PUT GLOBAL_DAILY=5000

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/GLOBAL_DAILY' \
  -H "Authorization: Bearer $MEMBER_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value": 5000}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "GLOBAL_DAILY",
 "name": "全站每日上限",
 "value": 5000,
 "updatedBy": "u_lcc015d5fa",
 "updatedAt": "2026-10-26T16:07:58.11011093+08:00"
}
```

## step 4 无 Authorization GET

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/limits'
```

HTTP 401（Content-Type: application/json）

```json
{
 "status": 401,
 "error": "Unauthorized",
 "message": "未登录或登录已过期",
 "path": "/api/admin/ai/limits"
}
```

## step 5 无 Authorization PUT GLOBAL_DAILY=1

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/GLOBAL_DAILY' \
  -H 'Content-Type: application/json' \
  -d '{"value": 1}'
```

HTTP 401（Content-Type: application/json）

```json
{
 "status": 401,
 "error": "Unauthorized",
 "message": "未登录或登录已过期",
 "path": "/api/admin/ai/limits/GLOBAL_DAILY"
}
```

## step 6 复核 GLOBAL_DAILY

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
  "updatedBy": "u_lcc015d5fa",
  "updatedAt": "2026-10-26T08:07:58.110111Z"
 },
 {
  "key": "PER_ANON_DAILY",
  "name": "单个匿名 ID 每日上限",
  "value": 50,
  "updatedBy": "admin",
  "updatedAt": "2026-10-26T08:07:57.915148Z"
 }
]
```
