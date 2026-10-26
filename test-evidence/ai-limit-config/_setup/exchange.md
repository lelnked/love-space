# 共享前置请求/响应存证

> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。

## step 1 登录 admin

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{"username": "admin", "password": "8@y2eoRLyStM*UVU"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "token": "$TOKEN",
 "manager": {
  "id": "019794b6-b400-7000-8000-000000000001",
  "username": "admin",
  "nickname": "管理员",
  "role": "ADMIN"
 }
}
```

## step 2 创建 MEMBER u_lc25cc4459

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/managers' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"username": "u_lc25cc4459", "password": "Member#2026x", "nickname": "限流成员"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a17742-7cd1-7999-a981-2ce40440ca65",
 "username": "u_lc25cc4459",
 "nickname": "限流成员",
 "role": "MEMBER",
 "enable": true,
 "createdAt": "2026-10-26T08:10:08.449335306Z"
}
```

## step 3 创建场景 t_lc25cc4459

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_lc25cc4459", "name": "限流配置用例"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a17742-7d03-753d-b4d0-65f5f871a247",
 "sceneId": "t_lc25cc4459",
 "name": "限流配置用例",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-26T08:10:08.513442107Z",
 "updatedAt": "2026-10-26T08:10:08.513442107Z"
}
```

## step 4 配置主模型 mock-ok

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a17742-7d03-753d-b4d0-65f5f871a247/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-ok"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 5 新建模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_lc25cc4459", "name": "模板", "systemPrompt": "你是助手，只输出 JSON", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a17742-7d68-7052-aab3-e4a752e6685e",
 "sceneId": "t_lc25cc4459",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON",
 "userPromptTemplate": "年龄{{age}}",
 "variables": [
  {
   "name": "age",
   "required": true,
   "description": "年龄"
  }
 ],
 "remark": null,
 "active": false,
 "tested": false,
 "lastTestInputs": null,
 "lastTestOutput": null,
 "lastTestContent": null,
 "lastTestModel": null,
 "lastTestSuccess": null,
 "lastTestError": null,
 "lastTestLatencyMs": null,
 "lastTestAt": null,
 "lastTestBy": null,
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-26T08:10:08.613491452Z",
 "updatedAt": "2026-10-26T08:10:08.613491452Z",
 "version": 0
}
```

## step 6 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17742-7d68-7052-aab3-e4a752e6685e/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "success": true,
 "model": "mock-ok",
 "output": "{\"echo\":\"年龄28\"}",
 "content": {
  "echo": "年龄28"
 },
 "errorCode": null,
 "errorMessage": null,
 "latencyMs": 76,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-26T08:10:08.733422718Z"
}
```

## step 7 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a17742-7d68-7052-aab3-e4a752e6685e' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a17742-7d68-7052-aab3-e4a752e6685e",
 "sceneId": "t_lc25cc4459",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON",
 "userPromptTemplate": "年龄{{age}}",
 "variables": [
  {
   "name": "age",
   "required": true,
   "description": "年龄"
  }
 ],
 "remark": null,
 "active": false,
 "tested": true,
 "lastTestInputs": {
  "age": 28
 },
 "lastTestOutput": "{\"echo\":\"年龄28\"}",
 "lastTestContent": {
  "echo": "年龄28"
 },
 "lastTestModel": "mock-ok",
 "lastTestSuccess": true,
 "lastTestError": null,
 "lastTestLatencyMs": 76,
 "lastTestAt": "2026-10-26T08:10:08.733423Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-26T08:10:08.613491Z",
 "updatedAt": "2026-10-26T08:10:08.613491Z",
 "version": 0
}
```

## step 8 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17742-7d68-7052-aab3-e4a752e6685e/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 9 psql 查模板 seq

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT seq FROM loves_ai_prompt_template WHERE id='"'"'01a17742-7d68-7052-aab3-e4a752e6685e'"'"';'
```

```
1
```
