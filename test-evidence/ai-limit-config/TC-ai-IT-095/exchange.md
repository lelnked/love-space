# TC-ai-IT-095 请求/响应存证

> 两项额度均为 -1 时不做额度检查
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 PUT 限流 PER_ANON_DAILY=-1

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/PER_ANON_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value": -1}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "PER_ANON_DAILY",
 "name": "单个匿名 ID 每日上限",
 "value": -1,
 "updatedBy": "admin",
 "updatedAt": "2026-10-26T16:08:00.470485493+08:00"
}
```

## step 2 PUT 限流 GLOBAL_DAILY=-1

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/GLOBAL_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value": -1}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "GLOBAL_DAILY",
 "name": "全站每日上限",
 "value": -1,
 "updatedBy": "admin",
 "updatedAt": "2026-10-26T16:08:00.484604587+08:00"
}
```

## step 3 psql 为 anon-u-c015d5fa 直插 100 条记录

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) SELECT gen_random_uuid(), '"'"'fill-anon-u-c015d5fa-'"'"' || g, '"'"'anon-u-c015d5fa'"'"', '"'"'t_lcc015d5fa'"'"', '"'"'01a17740-7cd0-7896-984f-a931fd3cf6e7'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now(), now() FROM generate_series(1,100) g;'
```

```
INSERT 0 100
```

## step 4 提交生成 c-u-638da893-de7d-4e26-a9b9-575166f498ac

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-u-c015d5fa' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_lcc015d5fa", "clientRequestId": "c-u-638da893-de7d-4e26-a9b9-575166f498ac", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a17740-8a20-73cd-89d9-a6d77468feee",
 "clientRequestId": "c-u-638da893-de7d-4e26-a9b9-575166f498ac",
 "sceneId": "t_lcc015d5fa",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 5 psql 统计 anon-u-c015d5fa 今日记录数

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT count(*) FROM loves_ai_generation WHERE anonymous_user_id='"'"'anon-u-c015d5fa'"'"' AND created_at >= (date_trunc('"'"'day'"'"', now() AT TIME ZONE '"'"'Asia/Shanghai'"'"') AT TIME ZONE '"'"'Asia/Shanghai'"'"');'
```

```
101
```

## step 6 收尾 PUT PER_ANON_DAILY=50

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/PER_ANON_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value": 50}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "PER_ANON_DAILY",
 "name": "单个匿名 ID 每日上限",
 "value": 50,
 "updatedBy": "admin",
 "updatedAt": "2026-10-26T16:08:00.963460741+08:00"
}
```

## step 7 收尾 PUT GLOBAL_DAILY=5000

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
 "updatedAt": "2026-10-26T16:08:00.977916218+08:00"
}
```
