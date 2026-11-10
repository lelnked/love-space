# TC-operation-log-IT-020 请求/响应存证

> 修改系统配置后异步产生 system-config:update 留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 POST 登录取 token

```bash
curl -s -i -X POST http://localhost:21423/api/admin/auth/login \
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

## step 2 GET page 查 ios_llm_popup

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=ios_llm_popup&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "019794b6-b400-7000-8000-000000000031",
   "key": "ios_llm_popup",
   "name": "iOS LLM 弹窗开关",
   "value": {
    "enabled": true
   },
   "description": "控制 iOS 系统调用 LLM 时是否需要弹窗",
   "createdAt": "2026-11-10T15:56:56.951563Z",
   "updatedAt": "2026-11-10T16:00:15.45278Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 3 PUT 编辑（值同初始）

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/system-configs/019794b6-b400-7000-8000-000000000031 \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name": "iOS LLM 弹窗开关", "value": {"enabled": true}, "description": "控制 iOS 系统调用 LLM 时是否需要弹窗"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "019794b6-b400-7000-8000-000000000031",
 "key": "ios_llm_popup",
 "name": "iOS LLM 弹窗开关",
 "value": {
  "enabled": true
 },
 "description": "控制 iOS 系统调用 LLM 时是否需要弹窗",
 "createdAt": "2026-11-10T15:56:56.951563Z",
 "updatedAt": "2026-11-10T16:00:15.704108332Z"
}
```

## step 4 轮询 GET logs/page（第 1 次）

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=system-config&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a1c630-495b-720f-8746-81344e534b29",
   "username": "admin",
   "module": "system-config",
   "action": "update",
   "target": "019794b6-b400-7000-8000-000000000031",
   "createdAt": "2026-11-10T16:00:15.707085Z"
  },
  {
   "id": "01a1c630-485f-763d-836a-4775cca75d3e",
   "username": "admin",
   "module": "system-config",
   "action": "update",
   "target": "019794b6-b400-7000-8000-000000000031",
   "createdAt": "2026-11-10T16:00:15.45532Z"
  },
  {
   "id": "01a1c630-47cb-7135-bf3b-4efb7fd591f6",
   "username": "admin",
   "module": "system-config",
   "action": "update",
   "target": "019794b6-b400-7000-8000-000000000031",
   "createdAt": "2026-11-10T16:00:15.30703Z"
  },
  {
   "id": "01a1c630-4616-7eff-942b-882c17ed3b8a",
   "username": "admin",
   "module": "system-config",
   "action": "update",
   "target": "019794b6-b400-7000-8000-000000000031",
   "createdAt": "2026-11-10T16:00:14.870889Z"
  },
  {
   "id": "01a1c630-460a-7489-ac18-f1d60eb774b2",
   "username": "admin",
   "module": "system-config",
   "action": "update",
   "target": "019794b6-b400-7000-8000-000000000031",
   "createdAt": "2026-11-10T16:00:14.84682Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 5,
 "totalPages": 1
}
```
