# TC-article-IT-002 POST /api/admin/article-categories 缺必填被拒绝 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> 登录 token 复用本轮统一登录（获取过程见 TC-article-IT-001 Step 1）

## Step 1: 前置采样：GET /api/admin/article-categories 记录当前栏目数

```bash
curl -s -i "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a00b98-4acc-787f-9be2-c8d843d7ec05",
    "name": "行程攻略",
    "icon": {
      "id": "bound/it001-icon-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it001-icon-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DwY4k5UerZPo8X8ABBHRT00%2FOTg%3D"
    },
    "sortOrder": 1,
    "createdAt": "2026-08-16T17:22:05.132459Z",
    "updatedAt": "2026-08-16T17:22:05.132459Z"
  }
]
```

## Step 2: POST /api/admin/article-categories 缺 name

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"icon":"images/it002-icon-172204.png","sortOrder":1}'
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

## Step 3: POST /api/admin/article-categories 缺 icon

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"缺图标栏目","sortOrder":1}'
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

## Step 4: GET /api/admin/article-categories 确认栏目均未创建

```bash
curl -s -i "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a00b98-4acc-787f-9be2-c8d843d7ec05",
    "name": "行程攻略",
    "icon": {
      "id": "bound/it001-icon-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it001-icon-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DwY4k5UerZPo8X8ABBHRT00%2FOTg%3D"
    },
    "sortOrder": 1,
    "createdAt": "2026-08-16T17:22:05.132459Z",
    "updatedAt": "2026-08-16T17:22:05.132459Z"
  }
]
```
