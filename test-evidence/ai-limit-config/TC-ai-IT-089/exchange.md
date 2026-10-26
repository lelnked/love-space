# TC-ai-IT-089 请求/响应存证

> PUT 修改单项只记录该项修改人
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 psql 前置：执行恢复语句

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'UPDATE loves_ai_limit SET limit_value = CASE limit_key WHEN '"'"'PER_ANON_DAILY'"'"' THEN 50 ELSE 5000 END, updated_by = NULL, updated_at = NULL;'
```

```
UPDATE 2
```

## step 2 PUT 限流 PER_ANON_DAILY=-1

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
 "updatedAt": "2026-10-26T16:07:57.70866494+08:00"
}
```

## step 3 GET 限流配置列表

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
  "value": -1,
  "updatedBy": "admin",
  "updatedAt": "2026-10-26T08:07:57.708665Z"
 }
]
```

## step 4 收尾 PUT PER_ANON_DAILY=50

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
 "updatedAt": "2026-10-26T16:07:57.737215739+08:00"
}
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
 "updatedAt": "2026-10-26T16:07:57.751622557+08:00"
}
```

## step 6 psql 收尾：执行恢复语句清空修改人

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'UPDATE loves_ai_limit SET limit_value = CASE limit_key WHEN '"'"'PER_ANON_DAILY'"'"' THEN 50 ELSE 5000 END, updated_by = NULL, updated_at = NULL;'
```

```
UPDATE 2
```
