# TC-featured-IT-015 DELETE /api/admin/featured-cycle-items/{id} 物理删除 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 前置：创建一个周期推荐条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "01a01f6a-fd0a-7924-a3c5-92fa13822d68", "description": "待删除", "banner": "images/7bf55197-27af-458f-b3a4-40755e9f1ed5.png"}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6a-fd12-706e-b914-4decc2f0b745",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": false,
  "activityId": "01a01f6a-fd0a-7924-a3c5-92fa13822d68",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T015",
  "title": null,
  "subtitle": null,
  "description": "待删除",
  "note": null,
  "banner": {
    "id": "bound/7bf55197-27af-458f-b3a4-40755e9f1ed5.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/7bf55197-27af-458f-b3a4-40755e9f1ed5.png?Expires=1787235300&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=30yHl7rMI8WUXrEu6BSHAWFU29M%3D"
  },
  "createdAt": "2026-08-20T13:45:00.433976905Z",
  "updatedAt": "2026-08-20T13:45:00.433976905Z"
}
```

## Step 2: DELETE 条目

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a01f6a-fd12-706e-b914-4decc2f0b745" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: ）:

```json
null
```

## Step 3: GET 已删除条目详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a01f6a-fd12-706e-b914-4decc2f0b745" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "周期推荐不存在：01a01f6a-fd12-706e-b914-4decc2f0b745",
  "path": "/api/admin/featured-cycle-items/01a01f6a-fd12-706e-b914-4decc2f0b745"
}
```

## Step 4: DELETE 同一 id 再删一次

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a01f6a-fd12-706e-b914-4decc2f0b745" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "周期推荐不存在：01a01f6a-fd12-706e-b914-4decc2f0b745",
  "path": "/api/admin/featured-cycle-items/01a01f6a-fd12-706e-b914-4decc2f0b745"
}
```

## Step 5: 确认被关联的活动实体本身不受影响

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/activities/01a01f6a-fd0a-7924-a3c5-92fa13822d68" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6a-fd0a-7924-a3c5-92fa13822d68",
  "cityId": "01a01f67-15d5-719b-98c5-5e09893345ed",
  "images": [
    {
      "id": "bound/8dbdea30-a1e2-488c-b090-f51c84e226be.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/8dbdea30-a1e2-488c-b090-f51c84e226be.png?Expires=1787235300&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=XGFfQvHk4khrSeAOuFGnvAObjOc%3D"
    }
  ],
  "title": "活动T015",
  "tags": [],
  "periods": [],
  "level": null,
  "introduction": null,
  "editorNote": null,
  "gatheringPlace": null,
  "dismissalPlace": null,
  "transportation": null,
  "visa": null,
  "itinerary": [],
  "detailHtml": null,
  "online": true,
  "createdAt": "2026-08-20T13:45:00.426509Z",
  "updatedAt": "2026-08-20T13:45:00.426509Z"
}
```

