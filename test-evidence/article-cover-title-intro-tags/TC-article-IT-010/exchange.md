# TC-article-IT-010 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## 前置: 创建栏目 IT010栏目

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT010栏目","icon":"images/it010-a.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-75f7-70f3-bc45-5c5dca552b76",
  "name": "IT010栏目",
  "icon": {
    "id": "bound/it010-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-a.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=w%2Bz5IlwaZIK2706%2BXI8ouhrCNQ8%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:23:36.055003975Z",
  "updatedAt": "2026-08-25T09:23:36.055003975Z"
}
```

## Step 1: POST /api/admin/articles contentHtml 含 2 个 img(objectKey)

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it010-cover.png","title":"IT010富文本","contentHtml":"<p>段落一</p><img src=\"images/it010-p1.png\"><p>段落二</p><img src=\"images/it010-p2.png\">","sortOrder":1,"categoryIds":["01a0383b-75f7-70f3-bc45-5c5dca552b76"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7613-791a-b382-15e45f3d2b3b",
  "image": {
    "id": "bound/it010-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=JkZF64FPRmOfN9Rl3gV7951o9PY%3D"
  },
  "title": "IT010富文本",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>段落一</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-p1.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=w%2FYgm33Q0ZNlRsKA6Z%2FNj%2BFnwW0%3D\"><p>段落二</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-p2.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qRtde9Dku%2F3jTyXOoOwyZVo%2By5c%3D\">",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383b-75f7-70f3-bc45-5c5dca552b76"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:36.08348975Z",
  "updatedAt": "2026-08-25T09:23:36.08348975Z"
}
```

## Step 2: GET /api/admin/articles/{id}

```bash
curl -s -i "http://localhost:8080/api/admin/articles/01a0383b-7613-791a-b382-15e45f3d2b3b" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7613-791a-b382-15e45f3d2b3b",
  "image": {
    "id": "bound/it010-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=JkZF64FPRmOfN9Rl3gV7951o9PY%3D"
  },
  "title": "IT010富文本",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>段落一</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-p1.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=w%2FYgm33Q0ZNlRsKA6Z%2FNj%2BFnwW0%3D\"><p>段落二</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-p2.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qRtde9Dku%2F3jTyXOoOwyZVo%2By5c%3D\">",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383b-75f7-70f3-bc45-5c5dca552b76"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:36.08349Z",
  "updatedAt": "2026-08-25T09:23:36.08349Z"
}
```

## Step 3a: PUT 改为纯文本 HTML

```bash
curl -s -i -X PUT "http://localhost:8080/api/admin/articles/01a0383b-7613-791a-b382-15e45f3d2b3b" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it010-cover.png","title":"IT010富文本","contentHtml":"<p>只剩纯文本</p>","sortOrder":1,"categoryIds":["01a0383b-75f7-70f3-bc45-5c5dca552b76"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7613-791a-b382-15e45f3d2b3b",
  "image": {
    "id": "bound/it010-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=JkZF64FPRmOfN9Rl3gV7951o9PY%3D"
  },
  "title": "IT010富文本",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>只剩纯文本</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383b-75f7-70f3-bc45-5c5dca552b76"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:36.08349Z",
  "updatedAt": "2026-08-25T09:23:36.136176398Z"
}
```

## Step 3b: GET 详情

```bash
curl -s -i "http://localhost:8080/api/admin/articles/01a0383b-7613-791a-b382-15e45f3d2b3b" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7613-791a-b382-15e45f3d2b3b",
  "image": {
    "id": "bound/it010-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=JkZF64FPRmOfN9Rl3gV7951o9PY%3D"
  },
  "title": "IT010富文本",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>只剩纯文本</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383b-75f7-70f3-bc45-5c5dca552b76"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:36.08349Z",
  "updatedAt": "2026-08-25T09:23:36.136807Z"
}
```
