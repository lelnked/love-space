# TC-ai-IT-056 请求/响应存证

> 全站超出每日总量返回 429
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 PUT 限流 GLOBAL_DAILY=3

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/GLOBAL_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value": 3}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "GLOBAL_DAILY",
 "name": "全站每日上限",
 "value": 3,
 "updatedBy": "admin",
 "updatedAt": "2026-10-26T16:08:01.437744649+08:00"
}
```

## step 2 psql 今日全站记录数

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT count(*) FROM loves_ai_generation WHERE created_at >= (date_trunc('"'"'day'"'"', now() AT TIME ZONE '"'"'Asia/Shanghai'"'"') AT TIME ZONE '"'"'Asia/Shanghai'"'"');'
```

```
106
```

## step 3 提交生成 c-g-81bba81f-5cc4-4d9b-8122-545d38533d64

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-g-c015d5fa' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_lcc015d5fa", "clientRequestId": "c-g-81bba81f-5cc4-4d9b-8122-545d38533d64", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 429（Content-Type: application/json）

```json
{
 "status": 429,
 "error": "Too Many Requests",
 "message": "服务繁忙，请稍后再试",
 "path": "/api/app/ai/generations"
}
```

## step 4 psql 统计 anon-g-c015d5fa 记录数

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT count(*) FROM loves_ai_generation WHERE anonymous_user_id='"'"'anon-g-c015d5fa'"'"';'
```

```
0
```

## step 5 收尾 PUT GLOBAL_DAILY=5000

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/GLOBAL_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value": 5000}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "GLOBAL_DAILY",
 "name": "全站每日上限",
 "value": 5000,
 "updatedBy": "admin",
 "updatedAt": "2026-10-26T16:08:01.511259835+08:00"
}
```
