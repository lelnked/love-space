# TC-article-IT-008 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## 前置: 创建栏目 IT008栏目

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT008栏目","icon":"images/it008-a.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-74c3-74e1-8980-96f6b01dfd77",
  "name": "IT008栏目",
  "icon": {
    "id": "bound/it008-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-a.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=3Kd81sjfmoxePCAYoDRcmk77DG4%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:23:35.747254496Z",
  "updatedAt": "2026-08-25T09:23:35.747254496Z"
}
```

## 前置: 创建 online=true 的文章

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it008-cover.png","title":"IT008文章","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a0383b-74c3-74e1-8980-96f6b01dfd77"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-74df-73f3-acc3-781f1d117f5f",
  "image": {
    "id": "bound/it008-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-cover.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cdj7xGNcYMGfx9dL5IWubgIZ3Dw%3D"
  },
  "title": "IT008文章",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383b-74c3-74e1-8980-96f6b01dfd77"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:35.775189212Z",
  "updatedAt": "2026-08-25T09:23:35.775189212Z"
}
```

## Step 2: PUT /api/admin/articles/{id}/online 下线

```bash
curl -s -i -X PUT "http://localhost:8080/api/admin/articles/01a0383b-74df-73f3-acc3-781f1d117f5f/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-74df-73f3-acc3-781f1d117f5f",
  "image": {
    "id": "bound/it008-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-cover.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cdj7xGNcYMGfx9dL5IWubgIZ3Dw%3D"
  },
  "title": "IT008文章",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383b-74c3-74e1-8980-96f6b01dfd77"
  ],
  "online": false,
  "createdAt": "2026-08-25T09:23:35.775189Z",
  "updatedAt": "2026-08-25T09:23:35.809513061Z"
}
```

## Step 3: GET 详情确认 online=false

```bash
curl -s -i "http://localhost:8080/api/admin/articles/01a0383b-74df-73f3-acc3-781f1d117f5f" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-74df-73f3-acc3-781f1d117f5f",
  "image": {
    "id": "bound/it008-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-cover.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cdj7xGNcYMGfx9dL5IWubgIZ3Dw%3D"
  },
  "title": "IT008文章",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383b-74c3-74e1-8980-96f6b01dfd77"
  ],
  "online": false,
  "createdAt": "2026-08-25T09:23:35.775189Z",
  "updatedAt": "2026-08-25T09:23:35.810373Z"
}
```

## Step 4a: PUT /api/admin/articles/{id}/online 上线

```bash
curl -s -i -X PUT "http://localhost:8080/api/admin/articles/01a0383b-74df-73f3-acc3-781f1d117f5f/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-74df-73f3-acc3-781f1d117f5f",
  "image": {
    "id": "bound/it008-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-cover.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cdj7xGNcYMGfx9dL5IWubgIZ3Dw%3D"
  },
  "title": "IT008文章",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383b-74c3-74e1-8980-96f6b01dfd77"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:35.775189Z",
  "updatedAt": "2026-08-25T09:23:35.860852533Z"
}
```

## Step 4b: GET 详情确认 online=true

```bash
curl -s -i "http://localhost:8080/api/admin/articles/01a0383b-74df-73f3-acc3-781f1d117f5f" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-74df-73f3-acc3-781f1d117f5f",
  "image": {
    "id": "bound/it008-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-cover.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cdj7xGNcYMGfx9dL5IWubgIZ3Dw%3D"
  },
  "title": "IT008文章",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383b-74c3-74e1-8980-96f6b01dfd77"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:35.775189Z",
  "updatedAt": "2026-08-25T09:23:35.861569Z"
}
```
