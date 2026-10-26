# TC-ai-IT-092 请求/响应存证

> PUT 不存在的限制返回 404
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 PUT 限流 NOT_EXIST=10

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/NOT_EXIST' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value": 10}'
```

HTTP 404（Content-Type: application/json）

```json
{
 "status": 404,
 "error": "Not Found",
 "message": "限制不存在：NOT_EXIST",
 "path": "/api/admin/ai/limits/NOT_EXIST"
}
```

## step 2 GET 限流配置列表

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

## step 3 psql 查 NOT_EXIST 行

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT count(*) FROM loves_ai_limit WHERE limit_key='"'"'NOT_EXIST'"'"';'
```

```
0
```
