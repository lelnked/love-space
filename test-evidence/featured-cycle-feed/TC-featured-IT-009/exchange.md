# TC-featured-IT-009 POST /api/admin/featured-cycle-items 创建文章类周期推荐 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 创建文章类周期推荐

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "LUTEAL", "type": "ARTICLE", "articleId": "01a01f69-4aec-7f76-923f-db6e93d69028", "title": "黄体期生活法", "banner": "images/87a3ebd5-9620-4823-9518-a8ab4f848d5f.png"}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f69-4af6-731f-9849-b37c3c680fe2",
  "phase": "LUTEAL",
  "type": "ARTICLE",
  "sortOrder": 0,
  "online": false,
  "activityId": null,
  "routeId": null,
  "articleId": "01a01f69-4aec-7f76-923f-db6e93d69028",
  "relatedTitle": "周期文章T009",
  "title": "黄体期生活法",
  "subtitle": null,
  "description": null,
  "note": null,
  "banner": {
    "id": "bound/87a3ebd5-9620-4823-9518-a8ab4f848d5f.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/87a3ebd5-9620-4823-9518-a8ab4f848d5f.png?Expires=1787235189&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NxQsXffFXyDnJEVghPw3lYdjUTo%3D"
  },
  "createdAt": "2026-08-20T13:43:09.302136036Z",
  "updatedAt": "2026-08-20T13:43:09.302136036Z"
}
```

## Step 2: GET 详情确认

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a01f69-4af6-731f-9849-b37c3c680fe2" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f69-4af6-731f-9849-b37c3c680fe2",
  "phase": "LUTEAL",
  "type": "ARTICLE",
  "sortOrder": 0,
  "online": false,
  "activityId": null,
  "routeId": null,
  "articleId": "01a01f69-4aec-7f76-923f-db6e93d69028",
  "relatedTitle": "周期文章T009",
  "title": "黄体期生活法",
  "subtitle": null,
  "description": null,
  "note": null,
  "banner": {
    "id": "bound/87a3ebd5-9620-4823-9518-a8ab4f848d5f.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/87a3ebd5-9620-4823-9518-a8ab4f848d5f.png?Expires=1787235189&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NxQsXffFXyDnJEVghPw3lYdjUTo%3D"
  },
  "createdAt": "2026-08-20T13:43:09.302136Z",
  "updatedAt": "2026-08-20T13:43:09.302136Z"
}
```

