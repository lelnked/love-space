# TC-ai-IT-055 请求/响应存证

> 匿名 ID 超出每日次数返回 429
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
 "updatedAt": "2026-10-26T16:08:01.298680357+08:00"
}
```

## step 2 （前置）生成第 1 次

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-c015d5fa' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_lcc015d5fa", "clientRequestId": "c-1-97435fcd-02f7-469b-b744-c853f537c3f2", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a17740-8c2d-7cd4-89f6-e24021ac5490",
 "clientRequestId": "c-1-97435fcd-02f7-469b-b744-c853f537c3f2",
 "sceneId": "t_lcc015d5fa",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 3 （前置）生成第 2 次

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-c015d5fa' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_lcc015d5fa", "clientRequestId": "c-2-90570265-518d-4429-965c-368568f494b4", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a17740-8c50-7475-a50d-64c8ba0d1b86",
 "clientRequestId": "c-2-90570265-518d-4429-965c-368568f494b4",
 "sceneId": "t_lcc015d5fa",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 4 提交生成 c-3-65d82484-f8d5-4830-80ed-e6af6a03ce21

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-c015d5fa' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_lcc015d5fa", "clientRequestId": "c-3-65d82484-f8d5-4830-80ed-e6af6a03ce21", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 429（Content-Type: application/json）

```json
{
 "status": 429,
 "error": "Too Many Requests",
 "message": "今日生成次数已达上限",
 "path": "/api/app/ai/generations"
}
```

## step 5 psql 统计 anon-c015d5fa 今日记录数

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT count(*) FROM loves_ai_generation WHERE anonymous_user_id='"'"'anon-c015d5fa'"'"' AND created_at >= (date_trunc('"'"'day'"'"', now() AT TIME ZONE '"'"'Asia/Shanghai'"'"') AT TIME ZONE '"'"'Asia/Shanghai'"'"');'
```

```
2
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
 "updatedAt": "2026-10-26T16:08:01.421287131+08:00"
}
```
