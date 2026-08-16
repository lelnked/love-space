# TC-article-IT-003 PUT /api/admin/article-categories/{id} 更新栏目 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> 前置：复用 TC-article-IT-001 创建的栏目（id=01a00b98-4acc-787f-9be2-c8d843d7ec05，name「行程攻略」、sortOrder=1）；token 复用本轮统一登录

## Step 1: PUT /api/admin/article-categories/{id} 改名「美食攻略」、换 icon、sortOrder=5

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/article-categories/01a00b98-4acc-787f-9be2-c8d843d7ec05" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"美食攻略","icon":"images/it003-icon-172204.png","sortOrder":5}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4acc-787f-9be2-c8d843d7ec05",
  "name": "美食攻略",
  "icon": {
    "id": "bound/it003-icon-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it003-icon-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Zkag2yhbCPesV4OJLyiFqcTBMf4%3D"
  },
  "sortOrder": 5,
  "createdAt": "2026-08-16T17:22:05.132459Z",
  "updatedAt": "2026-08-16T17:22:05.132459Z"
}
```

## Step 2: GET /api/admin/article-categories

```bash
curl -s -i "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a00b98-4acc-787f-9be2-c8d843d7ec05",
    "name": "美食攻略",
    "icon": {
      "id": "bound/it003-icon-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it003-icon-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Zkag2yhbCPesV4OJLyiFqcTBMf4%3D"
    },
    "sortOrder": 5,
    "createdAt": "2026-08-16T17:22:05.132459Z",
    "updatedAt": "2026-08-16T17:22:05.317892Z"
  }
]
```
