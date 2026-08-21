# TC-featured-IT-007 POST /api/admin/featured-cycle-items 创建活动类周期推荐 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: POST /api/admin/auth/login 获取 JWT token

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/auth/login" -H 'Content-Type: application/json' -d '{"username": "admin", "password": "8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP 200，Content-Type: application/json）:

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

## Step 2: 前置：创建活动（记 id 与 title）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId": "01a01f67-15d5-719b-98c5-5e09893345ed", "title": "周期活动ACT007", "images": ["images/4cb639c6-3dd9-4bdd-ac7f-bae801debf25.png"], "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f68-f051-70cf-b504-7bbaba40314a",
  "cityId": "01a01f67-15d5-719b-98c5-5e09893345ed",
  "images": [
    {
      "id": "bound/4cb639c6-3dd9-4bdd-ac7f-bae801debf25.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/4cb639c6-3dd9-4bdd-ac7f-bae801debf25.png?Expires=1787235166&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=F%2Fu5Qghzt4F%2BqGOOrJYTXsKjNt8%3D"
    }
  ],
  "title": "周期活动ACT007",
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
  "createdAt": "2026-08-20T13:42:46.096964062Z",
  "updatedAt": "2026-08-20T13:42:46.096964062Z"
}
```

## Step 3: 创建活动类周期推荐

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "01a01f68-f051-70cf-b504-7bbaba40314a", "description": "经期慢下来", "note": "周末两日", "banner": "images/bf6bdb71-6f89-480c-8e1b-b03737e7bc07.png", "sortOrder": 1}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f68-f05b-70ec-bd4a-c170637cf48a",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 1,
  "online": false,
  "activityId": "01a01f68-f051-70cf-b504-7bbaba40314a",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "周期活动ACT007",
  "title": null,
  "subtitle": null,
  "description": "经期慢下来",
  "note": "周末两日",
  "banner": {
    "id": "bound/bf6bdb71-6f89-480c-8e1b-b03737e7bc07.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/bf6bdb71-6f89-480c-8e1b-b03737e7bc07.png?Expires=1787235166&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Bcly0RUCLj3%2BHftn5j44pE6cgtQ%3D"
  },
  "createdAt": "2026-08-20T13:42:46.106968329Z",
  "updatedAt": "2026-08-20T13:42:46.106968329Z"
}
```

## Step 4: GET 详情确认

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a01f68-f05b-70ec-bd4a-c170637cf48a" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f68-f05b-70ec-bd4a-c170637cf48a",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 1,
  "online": false,
  "activityId": "01a01f68-f051-70cf-b504-7bbaba40314a",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "周期活动ACT007",
  "title": null,
  "subtitle": null,
  "description": "经期慢下来",
  "note": "周末两日",
  "banner": {
    "id": "bound/bf6bdb71-6f89-480c-8e1b-b03737e7bc07.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/bf6bdb71-6f89-480c-8e1b-b03737e7bc07.png?Expires=1787235166&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Bcly0RUCLj3%2BHftn5j44pE6cgtQ%3D"
  },
  "createdAt": "2026-08-20T13:42:46.106968Z",
  "updatedAt": "2026-08-20T13:42:46.106968Z"
}
```

