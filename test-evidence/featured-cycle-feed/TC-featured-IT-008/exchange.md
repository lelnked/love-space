# TC-featured-IT-008 POST /api/admin/featured-cycle-items 创建路线类周期推荐 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 创建路线类周期推荐

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "OVULATION", "type": "ROUTE", "routeId": "01a01f69-4ac0-7563-a630-b071dd3f5432", "title": "排卵期就该出门", "subtitle": "三天两夜", "description": "体力最好的几天", "banner": "images/0c6eb3f1-f547-4b85-8014-32f6c5687a38.png"}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f69-4acb-753c-946c-d88c40869746",
  "phase": "OVULATION",
  "type": "ROUTE",
  "sortOrder": 0,
  "online": false,
  "activityId": null,
  "routeId": "01a01f69-4ac0-7563-a630-b071dd3f5432",
  "articleId": null,
  "relatedTitle": "路线主标题R008",
  "title": "排卵期就该出门",
  "subtitle": "三天两夜",
  "description": "体力最好的几天",
  "note": null,
  "banner": {
    "id": "bound/0c6eb3f1-f547-4b85-8014-32f6c5687a38.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0c6eb3f1-f547-4b85-8014-32f6c5687a38.png?Expires=1787235189&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=eHjK5hS50R1AF3OxtuoCsCla478%3D"
  },
  "createdAt": "2026-08-20T13:43:09.259257768Z",
  "updatedAt": "2026-08-20T13:43:09.259257768Z"
}
```

## Step 2: GET 详情确认

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a01f69-4acb-753c-946c-d88c40869746" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f69-4acb-753c-946c-d88c40869746",
  "phase": "OVULATION",
  "type": "ROUTE",
  "sortOrder": 0,
  "online": false,
  "activityId": null,
  "routeId": "01a01f69-4ac0-7563-a630-b071dd3f5432",
  "articleId": null,
  "relatedTitle": "路线主标题R008",
  "title": "排卵期就该出门",
  "subtitle": "三天两夜",
  "description": "体力最好的几天",
  "note": null,
  "banner": {
    "id": "bound/0c6eb3f1-f547-4b85-8014-32f6c5687a38.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0c6eb3f1-f547-4b85-8014-32f6c5687a38.png?Expires=1787235189&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=eHjK5hS50R1AF3OxtuoCsCla478%3D"
  },
  "createdAt": "2026-08-20T13:43:09.259258Z",
  "updatedAt": "2026-08-20T13:43:09.259258Z"
}
```

