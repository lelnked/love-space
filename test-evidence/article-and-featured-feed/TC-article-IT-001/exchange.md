# TC-article-IT-001 POST /api/admin/article-categories 创建栏目 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: POST /api/admin/auth/login 获取 JWT token

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/auth/login" -H 'Content-Type: application/json' -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
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

## Step 2: POST /api/admin/article-categories 创建「行程攻略」

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"行程攻略","icon":"images/it001-icon-172204.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4acc-787f-9be2-c8d843d7ec05",
  "name": "行程攻略",
  "icon": {
    "id": "bound/it001-icon-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it001-icon-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DwY4k5UerZPo8X8ABBHRT00%2FOTg%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-16T17:22:05.132459227Z",
  "updatedAt": "2026-08-16T17:22:05.132459227Z"
}
```

## Step 3: GET /api/admin/article-categories 确认列表含该栏目

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
