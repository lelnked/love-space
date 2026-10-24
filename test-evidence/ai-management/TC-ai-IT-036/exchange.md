# TC-ai-IT-036 请求/响应存证

> MEMBER 角色可访问 AI 管理接口
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） ADMIN 创建 MEMBER 账号

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/managers' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"username":"u_ai253257a9","password":"Member#2026x","nickname":"AI成员"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-4023-703c-ad44-33fb68af4f93",
 "username": "u_ai253257a9",
 "nickname": "AI成员",
 "role": "MEMBER",
 "enable": true,
 "createdAt": "2026-10-24T17:51:45.184986578Z"
}
```

## step 2 MEMBER 登录

```bash
curl -s -X POST 'http://localhost:21423/api/admin/auth/login' -H 'Content-Type: application/json' -d '{"username":"u_ai253257a9","password":"Member#2026x"}'
```

HTTP 200，role=MEMBER，token 记为 `$MEMBER_TOKEN`（脱敏）

## step 3 MEMBER GET 场景分页

```bash
curl -s -i 'http://localhost:21423/api/admin/ai/scenes/page?page=1&size=20' -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 200

```json
{"content":[{"id":"01a16f0a-377a-7186-9c51-8ff06b60d694","sceneId":"t_d3b16183","name":"周期建议","description":null,"enabled":true,"primaryModel":"mock-ok","backupModel":null,"activeTemplate":null,"createdAt":"2026-10-24T17:51:42.970046Z","updatedAt":"2026-10-24T17:51:42.984862Z"},{"id":"01a16f0a-36ff-7201-8bf7-44c212153cde","sceneId":"t_7831acd1","name":"周期建议","description":null,"enabled":true,"primaryModel":"mock-ok","backupModel":null,"activeTemplate":{"id":"01a16f0a-371b-7373-909e-e8854d534476","seq":1,"name":"模板","publishedAt":"2026-10-24T17:51:42.919625Z","publishedBy":"admin"},"createdAt":"2026-10-24T17:51:42.847079Z","updatedAt":"2026-10-24T17:51:42.91994Z"},{"id":"01a16f0a-367e-737d-85cd-f9a0b7e90884","sceneId":"t_fa58ef44","name":"周期建议","description":null,"enabled":true,"primaryMode
```

## step 4 MEMBER GET 模型列表

```bash
curl -s -i 'http://localhost:21423/api/admin/ai/models' -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 200

```json
["mock-ok","mock-fenced","mock-format-error","mock-empty","mock-refused","mock-429","mock-500","mock-timeout","mock-flaky","mock-slow"]
```
