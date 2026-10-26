# TC-ai-IT-097 请求/响应存证

> 配置行缺失时生成接口返回 500 且不写记录
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 psql 前置：删除 GLOBAL_DAILY 行

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'DELETE FROM loves_ai_limit WHERE limit_key='"'"'GLOBAL_DAILY'"'"';'
```

```
DELETE 1
```

## step 2 提交生成 c-m-032ed5f9-a12e-42b3-bdda-e6522595147a

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-m-c015d5fa' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_lcc015d5fa", "clientRequestId": "c-m-032ed5f9-a12e-42b3-bdda-e6522595147a", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 500（Content-Type: application/json）

```json
{
 "status": 500,
 "error": "Internal Server Error",
 "message": "Internal server error",
 "path": "/api/app/ai/generations"
}
```

## step 3 psql 统计 anon-m-c015d5fa 记录数

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT count(*) FROM loves_ai_generation WHERE anonymous_user_id='"'"'anon-m-c015d5fa'"'"';'
```

```
0
```

## step 4 psql 收尾：重新插入 GLOBAL_DAILY

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'INSERT INTO loves_ai_limit (limit_key, name, limit_value) VALUES ('"'"'GLOBAL_DAILY'"'"', '"'"'全站每日上限'"'"', 5000);'
```

```
INSERT 0 1
```

## step 5 收尾后 GET 复核

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
  "updatedBy": "admin",
  "updatedAt": "2026-10-26T08:08:01.175131Z"
 }
]
```
