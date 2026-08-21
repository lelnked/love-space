# TC-featured-IT-012 PUT /api/admin/featured-cycle-items/{id} 周期与类型创建后不可变 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。
本轮按**修正后的步骤定义**执行：步骤 2 其余必填字段仍按原类型（ACTIVITY）形态提供，保留合法 activityId。

夹具（复用库中已有实体）：
- 活动 activityId=`01a01f6c-2cce-7249-bc5c-082116a400c2`（标题「活动T019」）
- 文章 articleId=`01a01f6b-5203-79fd-b344-1180a4fc1237`（标题「文章T016」）

## Step 0: 登录取 token

```bash
curl -s -X POST "http://localhost:21423/api/admin/auth/login" -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
# → HTTP 200，返回 token（记为 $TOKEN）
```

## Step 1: 前置：创建 phase=MENSTRUAL / type=ACTIVITY 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a01f6c-2cce-7249-bc5c-082116a400c2","description":"原说明","banner":"images/15f0a855-6d98-4212-89d0-3f009e7d165b.png"}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6f-ae12-7f90-96fc-f541ddbd7dbc",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": false,
  "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019",
  "title": null,
  "subtitle": null,
  "description": "原说明",
  "note": null,
  "banner": {
    "id": "bound/15f0a855-6d98-4212-89d0-3f009e7d165b.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/15f0a855-6d98-4212-89d0-3f009e7d165b.png?Expires=1787235607&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=GaQ9iovt5lfRemmaH%2FeCcBTzOLA%3D"
  },
  "createdAt": "2026-08-20T13:50:07.890915926Z",
  "updatedAt": "2026-08-20T13:50:07.890915926Z"
}
```

## Step 2: PUT 传 phase=LUTEAL / type=ARTICLE / articleId / title「改名」并改 description，同时按原类型 ACTIVITY 形态保留合法 activityId

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a01f6f-ae12-7f90-96fc-f541ddbd7dbc" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"LUTEAL","type":"ARTICLE","activityId":"01a01f6c-2cce-7249-bc5c-082116a400c2","articleId":"01a01f6b-5203-79fd-b344-1180a4fc1237","title":"改名","description":"改后的说明","banner":"images/d3702d4c-285b-4c7e-8e9f-daf6c79fdecd.png"}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6f-ae12-7f90-96fc-f541ddbd7dbc",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": false,
  "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019",
  "title": null,
  "subtitle": null,
  "description": "改后的说明",
  "note": null,
  "banner": {
    "id": "bound/d3702d4c-285b-4c7e-8e9f-daf6c79fdecd.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d3702d4c-285b-4c7e-8e9f-daf6c79fdecd.png?Expires=1787235616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=jjt9XPMW%2F5uok0Z54bm4ZWQHIhM%3D"
  },
  "createdAt": "2026-08-20T13:50:07.890916Z",
  "updatedAt": "2026-08-20T13:50:16.498969533Z"
}
```

## Step 3: GET 详情复查

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a01f6f-ae12-7f90-96fc-f541ddbd7dbc" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6f-ae12-7f90-96fc-f541ddbd7dbc",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": false,
  "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019",
  "title": null,
  "subtitle": null,
  "description": "改后的说明",
  "note": null,
  "banner": {
    "id": "bound/d3702d4c-285b-4c7e-8e9f-daf6c79fdecd.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d3702d4c-285b-4c7e-8e9f-daf6c79fdecd.png?Expires=1787235616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=jjt9XPMW%2F5uok0Z54bm4ZWQHIhM%3D"
  },
  "createdAt": "2026-08-20T13:50:07.890916Z",
  "updatedAt": "2026-08-20T13:50:16.500469Z"
}
```
