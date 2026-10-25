# TC-ai-IT-077 请求/响应存证

> GET /api/admin/ai/generations/{id} 记录不存在返回 404
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。

## step 1 GET 随机 uuid 详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/cc123310-5325-4260-8e47-5ece2f6f20e5' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 404（Content-Type: application/json）

```
{
 "status": 404,
 "error": "Not Found",
 "message": "生成记录不存在：cc123310-5325-4260-8e47-5ece2f6f20e5",
 "path": "/api/admin/ai/generations/cc123310-5325-4260-8e47-5ece2f6f20e5"
}
```
