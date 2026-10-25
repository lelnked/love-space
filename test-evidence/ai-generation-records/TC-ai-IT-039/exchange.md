# TC-ai-IT-039 请求/响应存证

> 生成成功写完整生成记录与调用明细
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 执行一次生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-gr-1d0c8fc6' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_3f05e38d", "clientRequestId": "c-c603cd7d-8c88-4030-a873-83b42e3e5de8", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```
{
 "requestId": "01a17341-5b19-7e6b-b151-a4be9a0e6ca9",
 "clientRequestId": "c-c603cd7d-8c88-4030-a873-83b42e3e5de8",
 "sceneId": "gr_3f05e38d",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 2 查生成记录（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'SELECT status, attempt_count, completed_at IS NOT NULL, template_id, template_seq, anonymous_user_id FROM loves_ai_generation WHERE id='"'"'01a17341-5b19-7e6b-b151-a4be9a0e6ca9'"'"';'
```

```
SUCCESS|1|t|01a17341-31b2-7887-a69c-5ae04440c5fb|1|anon-gr-1d0c8fc6
```

## step 3 查调用明细（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'SELECT seq, trigger_reason FROM loves_ai_generation_attempt WHERE generation_id='"'"'01a17341-5b19-7e6b-b151-a4be9a0e6ca9'"'"';'
```

```
1|INITIAL
```
