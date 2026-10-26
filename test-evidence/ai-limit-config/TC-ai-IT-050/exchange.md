# TC-ai-IT-050 请求/响应存证

> 重复提交返回原结果且不再调用模型
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
> 备注: 假方舟无调用计数接口；以该生成的调用明细（loves_ai_generation_attempt）条数不变作为未再调用模型的代理证据。

## step 1 （前置）首次生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-r-c015d5fa' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_lcc015d5fa", "clientRequestId": "c-1-rc015d5fa", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a17740-8df7-7dba-a96a-45e783f307a1",
 "clientRequestId": "c-1-rc015d5fa",
 "sceneId": "t_lcc015d5fa",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 2 psql 首次后调用明细数

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT count(*) FROM loves_ai_generation_attempt a JOIN loves_ai_generation g ON a.generation_id=g.id WHERE g.client_request_id='"'"'c-1-rc015d5fa'"'"';'
```

```
1
```

## step 3 重复提交

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-r-c015d5fa' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_lcc015d5fa", "clientRequestId": "c-1-rc015d5fa", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a17740-8df7-7dba-a96a-45e783f307a1",
 "clientRequestId": "c-1-rc015d5fa",
 "sceneId": "t_lcc015d5fa",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 4 psql 该 clientRequestId 记录数

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT count(*) FROM loves_ai_generation WHERE client_request_id='"'"'c-1-rc015d5fa'"'"';'
```

```
1
```

## step 5 psql 重复后调用明细数

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT count(*) FROM loves_ai_generation_attempt a JOIN loves_ai_generation g ON a.generation_id=g.id WHERE g.client_request_id='"'"'c-1-rc015d5fa'"'"';'
```

```
1
```
