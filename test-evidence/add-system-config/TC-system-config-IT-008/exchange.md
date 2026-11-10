# TC-system-config-IT-008 请求/响应存证

> 编辑 value 后 App 立即读到新值
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
   "updatedAt": "2026-11-10T16:00:14.868605Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 3 admin PUT enabled=false

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/system-configs/019794b6-b400-7000-8000-000000000031 \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name": "iOS LLM 弹窗开关", "value": {"enabled": false}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "019794b6-b400-7000-8000-000000000031",
 "key": "ios_llm_popup",
 "name": "iOS LLM 弹窗开关",
 "value": {
  "enabled": false
 },
 "description": null,
 "createdAt": "2026-11-10T15:56:56.951563Z",
 "updatedAt": "2026-11-10T16:00:15.30454829Z"
}
```

## step 4 app GET ios_llm_popup

```bash
curl -s -i -X GET http://localhost:8081/api/app/system-configs/ios_llm_popup \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "ios_llm_popup",
 "value": {
  "enabled": false
 }
}
```

## step 5 收尾 PUT 恢复初始值

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
 "updatedAt": "2026-11-10T16:00:15.452779568Z"
}
```
