# TC-ai-IT-037 请求/响应存证

> 未登录访问 AI 管理接口返回 401
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。

## step 1 无 Authorization GET 场景分页

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/page'
```

HTTP 401（Content-Type: application/json）

```
{
 "status": 401,
 "error": "Unauthorized",
 "message": "未登录或登录已过期",
 "path": "/api/admin/ai/scenes/page"
}
```

## step 2 无 Authorization POST 场景

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "no_auth", "name": "x"}'
```

HTTP 401（Content-Type: application/json）

```
{
 "status": 401,
 "error": "Unauthorized",
 "message": "未登录或登录已过期",
 "path": "/api/admin/ai/scenes"
}
```

## step 3 核对 no_auth 未创建（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'SELECT count(*) FROM loves_ai_scene WHERE scene_id='"'"'no_auth'"'"';'
```

```
0
```
