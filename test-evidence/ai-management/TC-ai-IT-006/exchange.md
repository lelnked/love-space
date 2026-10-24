# TC-ai-IT-006 请求/响应存证

> GET /api/admin/ai/scenes/page 按关键字与状态筛选
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

> 备注: 为避免库内既有「情绪回应」测试场景干扰「仅返回」断言，禁用场景名取「情绪回应-13909147」，关键字同名（仍走名称匹配）。

## step 1（前置） 创建场景 cycle_13909147

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"cycle_13909147","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-20ba-731e-a363-6eb8f29e6812",
 "sceneId": "cycle_13909147",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.146124685Z",
 "updatedAt": "2026-10-24T17:51:37.146124685Z"
}
```

## step 2（前置） 创建场景 mood_13909147

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"mood_13909147","name":"情绪回应-13909147"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-20ca-7131-9a4f-bcf2d2a653ed",
 "sceneId": "mood_13909147",
 "name": "情绪回应-13909147",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.162008786Z",
 "updatedAt": "2026-10-24T17:51:37.162008786Z"
}
```

## step 3（前置） 禁用场景 mood_13909147

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-20ca-7131-9a4f-bcf2d2a653ed/enabled' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"enabled":false}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 4 关键字=sceneId

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/page?keyword=cycle_13909147&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a16f0a-20ba-731e-a363-6eb8f29e6812",
   "sceneId": "cycle_13909147",
   "name": "周期建议",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:51:37.146125Z",
   "updatedAt": "2026-10-24T17:51:37.146125Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 5 关键字=名称 + enabled=false

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/page?keyword=%E6%83%85%E7%BB%AA%E5%9B%9E%E5%BA%94-13909147&enabled=false&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a16f0a-20ca-7131-9a4f-bcf2d2a653ed",
   "sceneId": "mood_13909147",
   "name": "情绪回应-13909147",
   "description": null,
   "enabled": false,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:51:37.162009Z",
   "updatedAt": "2026-10-24T17:51:37.180665Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 6 无筛选

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/page?page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a16f0a-20ca-7131-9a4f-bcf2d2a653ed",
   "sceneId": "mood_13909147",
   "name": "情绪回应-13909147",
   "description": null,
   "enabled": false,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:51:37.162009Z",
   "updatedAt": "2026-10-24T17:51:37.180665Z"
  },
  {
   "id": "01a16f0a-20ba-731e-a363-6eb8f29e6812",
   "sceneId": "cycle_13909147",
   "name": "周期建议",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:51:37.146125Z",
   "updatedAt": "2026-10-24T17:51:37.146125Z"
  },
  {
   "id": "01a16f0a-2070-7033-be03-6a2b38231328",
   "sceneId": "cycle_advice_19eabb3a",
   "name": "周期建议-改",
   "description": "新说明",
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:51:37.071948Z",
   "updatedAt": "2026-10-24T17:51:37.094761Z"
  },
  {
   "id": "01a16f0a-204c-78cc-be3e-818c312d7318",
   "sceneId": "x380f660fyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy",
   "name": "边界64",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:51:37.036485Z",
   "updatedAt": "2026-10-24T17:51:37.036485Z"
  },
  {
   "id": "01a16f0a-203c-7d05-abb8-0996436dc47f",
   "sceneId": "z0",
   "name": "边界2",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:51:37.020712Z",
   "updatedAt": "2026-10-24T17:51:37.020712Z"
  },
  {
   "id": "01a16f0a-1f31-77d4-ab39-b89fc5a8f487",
   "sceneId": "t_4b27b399",
   "name": "周期建议",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:51:36.753423Z",
   "updatedAt": "2026-10-24T17:51:36.753423Z"
  },
  {
   "id": "01a16f0a-1f14-7ab3-b418-bd4622371eda",
   "sceneId": "cycle_advice_55c74ed7",
   "name": "周期建议",
   "description": "按周期给建议",
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:51:36.724599Z",
   "updatedAt": "2026-10-24T17:51:36.724599Z"
  },
  {
   "id": "01a16f09-f724-7ae0-b492-6ee844fa10b6",
   "sceneId": "t_b9c1919c",
   "name": "周期建议",
   "description": null,
   "enabled": true,
   "primaryModel": "mock-ok",
   "backupModel": null,
   "activeTemplate": {
    "id": "01a16f09-f772-7f0c-bd00-fd6244daada0",
    "seq": 1,
    "name": "模板",
    "publishedAt": "2026-10-24T17:51:26.738918Z",
    "publishedBy": "admin"
   },
   "createdAt": "2026-10-24T17:51:26.50061Z",
   "updatedAt": "2026-10-24T17:51:26.739425Z"
  },
  {
   "id": "01a16f09-f6ec-77c9-b35b-0db40c6017ce",
   "sceneId": "cycle_advice_946e3298",
   "name": "周期建议",
   "description": "按周期给建议",
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:51:26.430912Z",
   "updatedAt": "2026-10-24T17:51:26.430912Z"
  },
  {
   "id": "01a16f01-26d1-714a-b3c5-8992ca2186f6",
   "sceneId": "t_011b9845d41f",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:41:48.881029Z",
   "updatedAt": "2026-10-24T17:41:48.881029Z"
  },
  {
   "id": "01a16f01-26c1-7410-8702-704c178be11e",
   "sceneId": "t_f6150df7e870",
   "name": "周期建议",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:41:48.865207Z",
   "updatedAt": "2026-10-24T17:41:48.865207Z"
  },
  {
   "id": "01a16f01-26a5-7981-b595-8fa89d48f6c5",
   "sceneId": "t_6e39be5fc55d",
   "name": "新名称",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:41:48.837546Z",
   "updatedAt": "2026-10-24T17:41:48.851492Z"
  },
  {
   "id": "01a16f01-2684-7cdf-9e05-02a89db04a17",
   "sceneId": "t_e7906151e315",
   "name": "测试场景",
   "description": null,
   "enabled": false,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:41:48.804733Z",
   "updatedAt": "2026-10-24T17:41:48.826318Z"
  },
  {
   "id": "01a16f01-2669-77b9-b9c9-65efe8a5af49",
   "sceneId": "t_3b4ac6bd70d8_off",
   "name": "禁用场景",
   "description": null,
   "enabled": false,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:41:48.777378Z",
   "updatedAt": "2026-10-24T17:41:48.781438Z"
  },
  {
   "id": "01a16f01-2663-77cf-bac7-4c8fb4cce170",
   "sceneId": "t_3b4ac6bd70d8_on",
   "name": "启用场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:41:48.77133Z",
   "updatedAt": "2026-10-24T17:41:48.77133Z"
  },
  {
   "id": "01a16f01-262a-7735-aa30-9c5e38dd42ea",
   "sceneId": "t_9e12a89495e5",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:41:48.7144Z",
   "updatedAt": "2026-10-24T17:41:48.7144Z"
  },
  {
   "id": "01a16f01-25fc-7f5e-8998-d2bb5986a044",
   "sceneId": "t_b000a460ad6c",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:41:48.668911Z",
   "updatedAt": "2026-10-24T17:41:48.668911Z"
  },
  {
   "id": "01a16f01-25ef-7091-b5e0-0a025808e1f2",
   "sceneId": "t_4f484bb98321",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-24T17:41:48.654985Z",
  
...(截断)
```
