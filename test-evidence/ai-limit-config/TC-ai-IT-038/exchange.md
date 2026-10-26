# TC-ai-IT-038 请求/响应存证

> POST /api/app/ai/generations 生成成功
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 提交生成 c-426f3f4b-978c-4bca-a5e2-ad2cc3ec589a

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-s-c015d5fa' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_lcc015d5fa", "clientRequestId": "c-426f3f4b-978c-4bca-a5e2-ad2cc3ec589a", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a17740-8dcf-7ba5-a9d4-8569452fdfd0",
 "clientRequestId": "c-426f3f4b-978c-4bca-a5e2-ad2cc3ec589a",
 "sceneId": "t_lcc015d5fa",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```
