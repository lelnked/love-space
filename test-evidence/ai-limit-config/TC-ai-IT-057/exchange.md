# TC-ai-IT-057 请求/响应存证

> 北京时间昨日记录不计入今日额度
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 PUT 限流 PER_ANON_DAILY=2

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/PER_ANON_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value": 2}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "PER_ANON_DAILY",
 "name": "单个匿名 ID 每日上限",
 "value": 2,
 "updatedBy": "admin",
 "updatedAt": "2026-10-26T16:08:01.528120446+08:00"
}
```

## step 2 psql 为 anon-y-c015d5fa 直插 2 条记录

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) SELECT gen_random_uuid(), '"'"'fill-anon-y-c015d5fa-'"'"' || g, '"'"'anon-y-c015d5fa'"'"', '"'"'t_lcc015d5fa'"'"', '"'"'01a17740-7cd0-7896-984f-a931fd3cf6e7'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, ((date_trunc('"'"'day'"'"', now() AT TIME ZONE '"'"'Asia/Shanghai'"'"') AT TIME ZONE '"'"'Asia/Shanghai'"'"') - interval '"'"'30 minutes'"'"'), ((date_trunc('"'"'day'"'"', now() AT TIME ZONE '"'"'Asia/Shanghai'"'"') AT TIME ZONE '"'"'Asia/Shanghai'"'"') - interval '"'"'30 minutes'"'"') FROM generate_series(1,2) g;'
```

```
INSERT 0 2
```

## step 3 psql 统计 anon-y-c015d5fa 今日记录数

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT count(*) FROM loves_ai_generation WHERE anonymous_user_id='"'"'anon-y-c015d5fa'"'"' AND created_at >= (date_trunc('"'"'day'"'"', now() AT TIME ZONE '"'"'Asia/Shanghai'"'"') AT TIME ZONE '"'"'Asia/Shanghai'"'"');'
```

```
0
```

## step 4 提交生成 c-y-bd679411-2ae5-48c9-ac00-beae93909687

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-y-c015d5fa' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_lcc015d5fa", "clientRequestId": "c-y-bd679411-2ae5-48c9-ac00-beae93909687", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a17740-8d36-72fe-8c61-3ae39a71ecca",
 "clientRequestId": "c-y-bd679411-2ae5-48c9-ac00-beae93909687",
 "sceneId": "t_lcc015d5fa",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 5 收尾 PUT PER_ANON_DAILY=50

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
 "updatedAt": "2026-10-26T16:08:01.617933058+08:00"
}
```
