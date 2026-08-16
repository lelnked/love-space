# TC-article-IT-012 GET /api/app/articles 下线文章不可见、详情 404 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> 前置数据复用 TC-article-IT-011：栏目 B（id=01a00b98-4fc2-77d8-8cc4-3a7d210602bc）下上线文章「文章权重1」（id=01a00b98-5009-71d8-a0bf-5925e6dc8f63）；token 复用本轮统一登录

## Step 1: 前置确认：GET /api/app/articles?categoryId={B} 含该文章

```bash
curl -s -i "http://localhost:8081/api/app/articles?categoryId=01a00b98-4fc2-77d8-8cc4-3a7d210602bc" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a00b98-5009-71d8-a0bf-5925e6dc8f63",
    "image": {
      "id": "bound/it011-y-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-y-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2BSmIO%2FjrTr1oBNenwTh0vCrNUFA%3D"
    },
    "title": "文章权重1",
    "subtitle": "权重1副标题"
  },
  {
    "id": "01a00b98-4fe5-7ffd-8ddb-7efdf855f8e0",
    "image": {
      "id": "bound/it011-x-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-x-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=a0aaF65KvvkAtZ1bp%2FPnzgdghwk%3D"
    },
    "title": "文章权重3",
    "subtitle": "权重3副标题"
  }
]
```

## Step 2: PUT /api/admin/articles/{id}/online {"online": false}

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a00b98-5009-71d8-a0bf-5925e6dc8f63/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5009-71d8-a0bf-5925e6dc8f63",
  "image": {
    "id": "bound/it011-y-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-y-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2BSmIO%2FjrTr1oBNenwTh0vCrNUFA%3D"
  },
  "title": "文章权重1",
  "subtitle": "权重1副标题",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4fc2-77d8-8cc4-3a7d210602bc"
  ],
  "online": false,
  "createdAt": "2026-08-16T17:22:06.47301Z",
  "updatedAt": "2026-08-16T17:22:06.47301Z"
}
```

## Step 3: GET /api/app/articles?categoryId={B}（X-API-Key）

```bash
curl -s -i "http://localhost:8081/api/app/articles?categoryId=01a00b98-4fc2-77d8-8cc4-3a7d210602bc" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a00b98-4fe5-7ffd-8ddb-7efdf855f8e0",
    "image": {
      "id": "bound/it011-x-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-x-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=a0aaF65KvvkAtZ1bp%2FPnzgdghwk%3D"
    },
    "title": "文章权重3",
    "subtitle": "权重3副标题"
  }
]
```

## Step 4: GET /api/app/articles/{id}（X-API-Key）

```bash
curl -s -i "http://localhost:8081/api/app/articles/01a00b98-5009-71d8-a0bf-5925e6dc8f63" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 404，Content-Type: application/json）:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "article not found: 01a00b98-5009-71d8-a0bf-5925e6dc8f63",
  "path": "/api/app/articles/01a00b98-5009-71d8-a0bf-5925e6dc8f63"
}
```
