# TC-ai-IT-085 请求/响应存证

> PUT /api/admin/ai/generations/{id}/rating 记录不存在返回 404
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。

## step 1 PUT 随机 uuid 评价

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/generations/8a885bd7-98b9-4864-ae5f-86b5e84f0ca6/rating' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"rating": "USABLE"}'
```

HTTP 404（Content-Type: application/json）

```
{
 "status": 404,
 "error": "Not Found",
 "message": "生成记录不存在：8a885bd7-98b9-4864-ae5f-86b5e84f0ca6",
 "path": "/api/admin/ai/generations/8a885bd7-98b9-4864-ae5f-86b5e84f0ca6/rating"
}
```
