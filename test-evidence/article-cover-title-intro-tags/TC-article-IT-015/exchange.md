# TC-article-IT-015 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## Step 1: POST /api/admin/auth/login 获取 JWT token

```bash
curl -s -i -X POST "$BASE/api/admin/auth/login" -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"8@y2eoRLyStM*UVU\"}"
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

## Step 2: POST /api/admin/article-categories 创建备用栏目

```bash
curl -s -i -X POST "$BASE/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"name\":\"IT015栏目\",\"icon\":\"images/it015-icon.png\",\"sortOrder\":1}"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a03839-e266-7cd8-bf57-e3b11d986c45",
  "name": "IT015栏目",
  "icon": {
    "id": "bound/it015-icon.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-icon.png?Expires=1787651512&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=nHiW45sJnH2gNQcB3YrZYAwk%2FOQ%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:21:52.729297246Z",
  "updatedAt": "2026-08-25T09:21:52.729297246Z"
}
```

## Step 3: POST /api/admin/articles 创建带 coverTitle/intro/tags 的文章

```bash
curl -s -i -X POST "$BASE/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it015-cover.png","title":"详情页标题","coverTitle":"封面标题","subtitle":"副标题","intro":"这是引言","tags":["约会","周末"],"contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a03839-e266-7cd8-bf57-e3b11d986c45"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a03839-e2b9-7228-8ae6-f0a10bdf0300",
  "image": {
    "id": "bound/it015-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-cover.png?Expires=1787651512&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=hvl3CIZOmiIj0aGGaeUQkd7S3uA%3D"
  },
  "title": "详情页标题",
  "coverTitle": "封面标题",
  "subtitle": "副标题",
  "intro": "这是引言",
  "tags": [
    "约会",
    "周末"
  ],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a03839-e266-7cd8-bf57-e3b11d986c45"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:21:52.823853848Z",
  "updatedAt": "2026-08-25T09:21:52.823853848Z"
}
```

## Step 4: GET /api/admin/articles/{id} 查详情

```bash
curl -s -i "$BASE/api/admin/articles/01a03839-e2b9-7228-8ae6-f0a10bdf0300" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a03839-e2b9-7228-8ae6-f0a10bdf0300",
  "image": {
    "id": "bound/it015-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-cover.png?Expires=1787651512&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=hvl3CIZOmiIj0aGGaeUQkd7S3uA%3D"
  },
  "title": "详情页标题",
  "coverTitle": "封面标题",
  "subtitle": "副标题",
  "intro": "这是引言",
  "tags": [
    "约会",
    "周末"
  ],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a03839-e266-7cd8-bf57-e3b11d986c45"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:21:52.823854Z",
  "updatedAt": "2026-08-25T09:21:52.823854Z"
}
```
