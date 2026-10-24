# TC-ai-IT-004 请求/响应存证

> POST /api/admin/ai/scenes sceneId 格式与长度边界校验
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1 非法创建 sceneId=Cycle-Advice nameLen=2

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"Cycle-Advice","name":"周期"}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "scene_id 须以小写字母开头，仅含小写字母、数字、下划线，长度 2~64",
 "path": "/api/admin/ai/scenes"
}
```

## step 2 非法创建 sceneId=a nameLen=2

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"a","name":"周期"}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "scene_id 须以小写字母开头，仅含小写字母、数字、下划线，长度 2~64",
 "path": "/api/admin/ai/scenes"
}
```

## step 3 非法创建 sceneId=abbbbbbbbbbb…(65) nameLen=2

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"abbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb","name":"周期"}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "scene_id 须以小写字母开头，仅含小写字母、数字、下划线，长度 2~64",
 "path": "/api/admin/ai/scenes"
}
```

## step 4 非法创建 sceneId=name_len_380f660f nameLen=65

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"name_len_380f660f","name":"名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名"}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "场景名称长度不能超过 64 个字符",
 "path": "/api/admin/ai/scenes"
}
```

## step 5 psql 非法 sceneId 均未创建

```sql
select count(*) from loves_ai_scene where scene_id in ('Cycle-Advice','a','abbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb','name_len_380f660f')
```

```
0
```

## step 6 psql 已存在的 2 字符 z? sceneId

```sql
select scene_id from loves_ai_scene where scene_id ~ '^z[a-z0-9_]$'
```

```
(空)
```

## step 7 2 字符 sceneId=z0

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"z0","name":"边界2"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-203c-7d05-abb8-0996436dc47f",
 "sceneId": "z0",
 "name": "边界2",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.020711931Z",
 "updatedAt": "2026-10-24T17:51:37.020711931Z"
}
```

## step 8 64 字符 sceneId

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"x380f660fyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy","name":"边界64"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-204c-78cc-be3e-818c312d7318",
 "sceneId": "x380f660fyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy",
 "name": "边界64",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.036485373Z",
 "updatedAt": "2026-10-24T17:51:37.036485373Z"
}
```
