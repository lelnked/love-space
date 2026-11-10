# TC-system-config-IT-009 请求/响应存证

> GET /page 按 keyword 匹配 key 或名称（不区分大小写）
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 前置 新增 1

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "home.notice_t792qv", "name": "首页公告t792qv", "value": {"text": "hi"}}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d58c-793c-8d2b-a7a85f7ce63d",
 "key": "home.notice_t792qv",
 "name": "首页公告t792qv",
 "value": {
  "text": "hi"
 },
 "description": null,
 "createdAt": "2026-11-10T05:05:29.996519015Z",
 "updatedAt": "2026-11-10T05:05:29.996519015Z"
}
```

## step 2 前置 新增 2

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "cycle_default_days_t792qv", "name": "默认周期t792qv", "value": 7}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d598-7261-b20f-0d2f60bb3bbb",
 "key": "cycle_default_days_t792qv",
 "name": "默认周期t792qv",
 "value": 7,
 "description": null,
 "createdAt": "2026-11-10T05:05:30.008102329Z",
 "updatedAt": "2026-11-10T05:05:30.008102329Z"
}
```

## step 3 GET /page keyword=notice_t792qv

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=notice_t792qv&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a1c3d8-d58c-793c-8d2b-a7a85f7ce63d",
   "key": "home.notice_t792qv",
   "name": "首页公告t792qv",
   "value": {
    "text": "hi"
   },
   "description": null,
   "createdAt": "2026-11-10T05:05:29.996519Z",
   "updatedAt": "2026-11-10T05:05:29.996519Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 4 GET /page keyword=NOTICE_T792QV

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=NOTICE_T792QV&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a1c3d8-d58c-793c-8d2b-a7a85f7ce63d",
   "key": "home.notice_t792qv",
   "name": "首页公告t792qv",
   "value": {
    "text": "hi"
   },
   "description": null,
   "createdAt": "2026-11-10T05:05:29.996519Z",
   "updatedAt": "2026-11-10T05:05:29.996519Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 5 GET /page keyword=默认周期t792qv

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=%E9%BB%98%E8%AE%A4%E5%91%A8%E6%9C%9Ft792qv&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a1c3d8-d598-7261-b20f-0d2f60bb3bbb",
   "key": "cycle_default_days_t792qv",
   "name": "默认周期t792qv",
   "value": 7,
   "description": null,
   "createdAt": "2026-11-10T05:05:30.008102Z",
   "updatedAt": "2026-11-10T05:05:30.008102Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 6 GET /page keyword=zz_nomatch_t792qv

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=zz_nomatch_t792qv&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [],
 "page": 1,
 "size": 20,
 "totalElements": 0,
 "totalPages": 0
}
```

## step 7 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d58c-793c-8d2b-a7a85f7ce63d \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 8 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d598-7261-b20f-0d2f60bb3bbb \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
