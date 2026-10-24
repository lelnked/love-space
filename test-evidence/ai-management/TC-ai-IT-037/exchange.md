# TC-ai-IT-037 请求/响应存证

> 未登录访问 AI 管理接口返回 401
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1 psql no_auth 场景数（基线）

```sql
select count(*) from loves_ai_scene where scene_id='no_auth'
```

```
0
```

## step 2 无 Authorization GET 场景分页

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/page'
```

HTTP 401（Content-Type: application/json）

```json
{
 "status": 401,
 "error": "Unauthorized",
 "message": "未登录或登录已过期",
 "path": "/api/admin/ai/scenes/page"
}
```

## step 3 无 Authorization POST 创建场景

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"no_auth","name":"x"}'
```

HTTP 401（Content-Type: application/json）

```json
{
 "status": 401,
 "error": "Unauthorized",
 "message": "未登录或登录已过期",
 "path": "/api/admin/ai/scenes"
}
```

## step 4 psql no_auth 场景数

```sql
select count(*) from loves_ai_scene where scene_id='no_auth'
```

```
0
```
