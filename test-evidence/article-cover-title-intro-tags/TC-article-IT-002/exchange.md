# TC-article-IT-002 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## Step 1: POST /api/admin/article-categories 缺 name

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"icon\":\"images/it002-icon.png\",\"sortOrder\":1}"
```

实际响应（HTTP 400，Content-Type: application/json）:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "栏目名称不能为空",
  "path": "/api/admin/article-categories"
}
```

## Step 2: POST /api/admin/article-categories 缺 icon

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"name\":\"IT002栏目\",\"sortOrder\":1}"
```

实际响应（HTTP 400，Content-Type: application/json）:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "栏目 icon 不能为空",
  "path": "/api/admin/article-categories"
}
```

## Step 3: GET /api/admin/article-categories 确认均未创建

```bash
curl -s -i "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
[
  {
    "id": "01a0383a-d51a-7b64-bc09-95fe39882697",
    "name": "行程攻略",
    "icon": {
      "id": "bound/it001-icon.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it001-icon.png?Expires=1787651574&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=oKrupqSf62EReVvNwan5d9KTzDk%3D"
    },
    "sortOrder": 1,
    "createdAt": "2026-08-25T09:22:54.874649Z",
    "updatedAt": "2026-08-25T09:22:54.874649Z"
  },
  {
    "id": "01a03839-e266-7cd8-bf57-e3b11d986c45",
    "name": "IT015栏目",
    "icon": {
      "id": "bound/it015-icon.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-icon.png?Expires=1787651574&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=OHvTkFXmIMhS9ZlgbNuXMCkNljQ%3D"
    },
    "sortOrder": 1,
    "createdAt": "2026-08-25T09:21:52.729297Z",
    "updatedAt": "2026-08-25T09:21:52.729297Z"
  }
]
```
