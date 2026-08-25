# TC-article-IT-007 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## 前置: 创建栏目 IT007栏目A

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT007栏目A","icon":"images/it007-a.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7432-744e-9fa7-ca73c500c48c",
  "name": "IT007栏目A",
  "icon": {
    "id": "bound/it007-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-a.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=kTqyb%2FkLMPjHiaRCuEvgjBuO2ao%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:23:35.602191181Z",
  "updatedAt": "2026-08-25T09:23:35.602191181Z"
}
```

## 前置: 创建栏目 IT007栏目B

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT007栏目B","icon":"images/it007-b.png","sortOrder":2}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7455-75bb-8cd9-615192b0e940",
  "name": "IT007栏目B",
  "icon": {
    "id": "bound/it007-b.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-b.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=H%2BIBToNpmYRgTZieCvDBO3QqptU%3D"
  },
  "sortOrder": 2,
  "createdAt": "2026-08-25T09:23:35.637280655Z",
  "updatedAt": "2026-08-25T09:23:35.637280655Z"
}
```

## 前置: 创建关联 [A] 的文章

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it007-cover.png","title":"IT007原标题","subtitle":"原副标题","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a0383b-7432-744e-9fa7-ca73c500c48c"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7474-754e-8f3e-cb8149392078",
  "image": {
    "id": "bound/it007-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-cover.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=9w5NqAwYdDtf6qAS6NRXCQIGI1c%3D"
  },
  "title": "IT007原标题",
  "coverTitle": null,
  "subtitle": "原副标题",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383b-7432-744e-9fa7-ca73c500c48c"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:35.668269548Z",
  "updatedAt": "2026-08-25T09:23:35.668269548Z"
}
```

## Step 2: PUT /api/admin/articles/{id} 改 title/subtitle/sortOrder/categoryIds=[B]

```bash
curl -s -i -X PUT "http://localhost:8080/api/admin/articles/01a0383b-7474-754e-8f3e-cb8149392078" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it007-cover.png","title":"IT007新标题","subtitle":"新副标题","contentHtml":"<p>正文</p>","sortOrder":9,"categoryIds":["01a0383b-7455-75bb-8cd9-615192b0e940"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7474-754e-8f3e-cb8149392078",
  "image": {
    "id": "bound/it007-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-cover.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=9w5NqAwYdDtf6qAS6NRXCQIGI1c%3D"
  },
  "title": "IT007新标题",
  "coverTitle": null,
  "subtitle": "新副标题",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 9,
  "categoryIds": [
    "01a0383b-7455-75bb-8cd9-615192b0e940"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:35.66827Z",
  "updatedAt": "2026-08-25T09:23:35.700285439Z"
}
```

## Step 3: GET /api/admin/articles/{id}

```bash
curl -s -i "http://localhost:8080/api/admin/articles/01a0383b-7474-754e-8f3e-cb8149392078" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7474-754e-8f3e-cb8149392078",
  "image": {
    "id": "bound/it007-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-cover.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=9w5NqAwYdDtf6qAS6NRXCQIGI1c%3D"
  },
  "title": "IT007新标题",
  "coverTitle": null,
  "subtitle": "新副标题",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 9,
  "categoryIds": [
    "01a0383b-7455-75bb-8cd9-615192b0e940"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:35.66827Z",
  "updatedAt": "2026-08-25T09:23:35.700972Z"
}
```
