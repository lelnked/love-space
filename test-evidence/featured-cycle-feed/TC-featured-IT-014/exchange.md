# TC-featured-IT-014 PUT /api/admin/featured-cycle-items/{id}/online 上下线切换 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 前置：创建 online=true 的周期推荐条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "01a01f6a-1730-71ad-aa47-6f98f2e6d5c8", "description": "上下线", "banner": "images/d49e315c-84b5-4392-a73f-13a8987a8def.png", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6a-1739-79e4-b35a-8dafbb9d95d3",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": true,
  "activityId": "01a01f6a-1730-71ad-aa47-6f98f2e6d5c8",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T014",
  "title": null,
  "subtitle": null,
  "description": "上下线",
  "note": null,
  "banner": {
    "id": "bound/d49e315c-84b5-4392-a73f-13a8987a8def.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d49e315c-84b5-4392-a73f-13a8987a8def.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tYLPsRMnN2P4mXIKeYhJ47xYeLQ%3D"
  },
  "createdAt": "2026-08-20T13:44:01.59354501Z",
  "updatedAt": "2026-08-20T13:44:01.59354501Z"
}
```

## Step 2: PUT /online 置 false

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a01f6a-1739-79e4-b35a-8dafbb9d95d3/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6a-1739-79e4-b35a-8dafbb9d95d3",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": false,
  "activityId": "01a01f6a-1730-71ad-aa47-6f98f2e6d5c8",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T014",
  "title": null,
  "subtitle": null,
  "description": "上下线",
  "note": null,
  "banner": {
    "id": "bound/d49e315c-84b5-4392-a73f-13a8987a8def.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d49e315c-84b5-4392-a73f-13a8987a8def.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tYLPsRMnN2P4mXIKeYhJ47xYeLQ%3D"
  },
  "createdAt": "2026-08-20T13:44:01.593545Z",
  "updatedAt": "2026-08-20T13:44:01.593545Z"
}
```

## Step 3: GET 详情确认 online=false

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a01f6a-1739-79e4-b35a-8dafbb9d95d3" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6a-1739-79e4-b35a-8dafbb9d95d3",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": false,
  "activityId": "01a01f6a-1730-71ad-aa47-6f98f2e6d5c8",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T014",
  "title": null,
  "subtitle": null,
  "description": "上下线",
  "note": null,
  "banner": {
    "id": "bound/d49e315c-84b5-4392-a73f-13a8987a8def.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d49e315c-84b5-4392-a73f-13a8987a8def.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tYLPsRMnN2P4mXIKeYhJ47xYeLQ%3D"
  },
  "createdAt": "2026-08-20T13:44:01.593545Z",
  "updatedAt": "2026-08-20T13:44:01.610391Z"
}
```

## Step 4: PUT /online 置 true

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a01f6a-1739-79e4-b35a-8dafbb9d95d3/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6a-1739-79e4-b35a-8dafbb9d95d3",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": true,
  "activityId": "01a01f6a-1730-71ad-aa47-6f98f2e6d5c8",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T014",
  "title": null,
  "subtitle": null,
  "description": "上下线",
  "note": null,
  "banner": {
    "id": "bound/d49e315c-84b5-4392-a73f-13a8987a8def.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d49e315c-84b5-4392-a73f-13a8987a8def.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tYLPsRMnN2P4mXIKeYhJ47xYeLQ%3D"
  },
  "createdAt": "2026-08-20T13:44:01.593545Z",
  "updatedAt": "2026-08-20T13:44:01.610391Z"
}
```

## Step 5: GET 详情确认 online=true

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a01f6a-1739-79e4-b35a-8dafbb9d95d3" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6a-1739-79e4-b35a-8dafbb9d95d3",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": true,
  "activityId": "01a01f6a-1730-71ad-aa47-6f98f2e6d5c8",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T014",
  "title": null,
  "subtitle": null,
  "description": "上下线",
  "note": null,
  "banner": {
    "id": "bound/d49e315c-84b5-4392-a73f-13a8987a8def.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d49e315c-84b5-4392-a73f-13a8987a8def.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tYLPsRMnN2P4mXIKeYhJ47xYeLQ%3D"
  },
  "createdAt": "2026-08-20T13:44:01.593545Z",
  "updatedAt": "2026-08-20T13:44:01.632427Z"
}
```

