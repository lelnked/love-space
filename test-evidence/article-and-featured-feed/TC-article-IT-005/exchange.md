# TC-article-IT-005 POST /api/admin/articles 创建关联多栏目的完整文章 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> token 复用本轮统一登录

## Step 1: 前置 1/2：创建栏目 A

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"栏目A-005","icon":"images/it005-a-172204.png","sortOrder":21}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4c76-7393-a249-e392b12b12e2",
  "name": "栏目A-005",
  "icon": {
    "id": "bound/it005-a-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-a-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=5Qwg%2BrXfY4QO%2BW0jcWyJ2USEecQ%3D"
  },
  "sortOrder": 21,
  "createdAt": "2026-08-16T17:22:05.558157078Z",
  "updatedAt": "2026-08-16T17:22:05.558157078Z"
}
```

## Step 2: 前置 2/2：创建栏目 B

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"栏目B-005","icon":"images/it005-b-172204.png","sortOrder":22}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4c92-70b3-be6d-42e449c9bdfb",
  "name": "栏目B-005",
  "icon": {
    "id": "bound/it005-b-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-b-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=eHYq2DYpM%2FEtNxzhb7lw8lCqnRQ%3D"
  },
  "sortOrder": 22,
  "createdAt": "2026-08-16T17:22:05.585959624Z",
  "updatedAt": "2026-08-16T17:22:05.585959624Z"
}
```

## Step 3: POST /api/admin/articles 创建「海岛两日游」关联 A、B

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it005-cover-172204.png","title":"海岛两日游","subtitle":"附完整行程","contentHtml":"<p>第一天：出海浮潜</p><p>第二天：环岛骑行</p>","sortOrder":1,"categoryIds":["01a00b98-4c76-7393-a249-e392b12b12e2","01a00b98-4c92-70b3-be6d-42e449c9bdfb"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4caf-786b-adf8-211ef7eac794",
  "image": {
    "id": "bound/it005-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-cover-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DhC%2BdHVVc2HznYFJn5yg3TPpXx4%3D"
  },
  "title": "海岛两日游",
  "subtitle": "附完整行程",
  "contentHtml": "<p>第一天：出海浮潜</p><p>第二天：环岛骑行</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c76-7393-a249-e392b12b12e2",
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:05.61546789Z",
  "updatedAt": "2026-08-16T17:22:05.61546789Z"
}
```

## Step 4: GET /api/admin/articles/{id}

```bash
curl -s -i "http://localhost:21423/api/admin/articles/01a00b98-4caf-786b-adf8-211ef7eac794" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4caf-786b-adf8-211ef7eac794",
  "image": {
    "id": "bound/it005-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-cover-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DhC%2BdHVVc2HznYFJn5yg3TPpXx4%3D"
  },
  "title": "海岛两日游",
  "subtitle": "附完整行程",
  "contentHtml": "<p>第一天：出海浮潜</p><p>第二天：环岛骑行</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c76-7393-a249-e392b12b12e2",
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:05.615468Z",
  "updatedAt": "2026-08-16T17:22:05.615468Z"
}
```
