# TC-operation-log-IT-020 请求/响应存证

> 系统配置新增、编辑、删除后异步产生 system-config 三条留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 登录

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

## step 2 基线：留痕前查询 system-config 日志

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=system-config&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a1c3d8-da06-723b-8f13-93b2fe1dae7f",
   "username": "admin",
   "module": "system-config",
   "action": "delete",
   "target": "01a1c3d8-d9dc-7c5c-931b-87ec201c61cf",
   "createdAt": "2026-11-10T05:05:31.142087Z"
  },
  {
   "id": "01a1c3d8-d9fd-7658-8308-9f2551e65a6e",
   "username": "admin",
   "module": "system-config",
   "action": "delete",
   "target": "01a1c3d8-d9d2-716d-8b78-12799c7e2de6",
   "createdAt": "2026-11-10T05:05:31.133339Z"
  },
  {
   "id": "01a1c3d8-d9de-75e3-b02c-99e6ffb990c6",
   "username": "admin",
   "module": "system-config",
   "actio
... <截断，原长 17304 字符> ...
admin",
   "module": "system-config",
   "action": "create",
   "target": null,
   "createdAt": "2026-11-10T05:04:49.857692Z"
  }
 ],
 "page": 1,
 "size": 200,
 "totalElements": 95,
 "totalPages": 1
}
```

## step 3 POST 新增

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "log.k_37irru", "name": "留痕测试", "value": 1}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-dac8-7d78-a64c-f49e25b59d6e",
 "key": "log.k_37irru",
 "name": "留痕测试",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:31.336796228Z",
 "updatedAt": "2026-11-10T05:05:31.336796228Z"
}
```

## step 4 PUT 编辑

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/system-configs/01a1c3d8-dac8-7d78-a64c-f49e25b59d6e \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name": "留痕测试-改", "value": 2}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-dac8-7d78-a64c-f49e25b59d6e",
 "key": "log.k_37irru",
 "name": "留痕测试-改",
 "value": 2,
 "description": null,
 "createdAt": "2026-11-10T05:05:31.336796Z",
 "updatedAt": "2026-11-10T05:05:31.347361274Z"
}
```

## step 5 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-dac8-7d78-a64c-f49e25b59d6e \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 6 轮询 logs/page 第 1 次

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=system-config&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a1c3d8-dadd-7ca3-a71e-4b8125a72fd8",
   "username": "admin",
   "module": "system-config",
   "action": "delete",
   "target": "01a1c3d8-dac8-7d78-a64c-f49e25b59d6e",
   "createdAt": "2026-11-10T05:05:31.357741Z"
  },
  {
   "id": "01a1c3d8-dad5-7584-b8a3-0cee9b9817b1",
   "username": "admin",
   "module": "system-config",
   "action": "update",
   "target": "01a1c3d8-dac8-7d78-a64c-f49e25b59d6e",
   "createdAt": "2026-11-10T05:05:31.349317Z"
  },
  {
   "id": "01a1c3d8-daca-7f19-a654-dda5326cc57d",
   "username": "admin",
   "module": "system-config",
   "actio
... <截断，原长 3720 字符> ...
"admin",
   "module": "system-config",
   "action": "create",
   "target": null,
   "createdAt": "2026-11-10T05:05:30.819832Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 98,
 "totalPages": 5
}
```
