# TC-article-IT-009 DELETE /api/admin/articles/{id} 物理删除文章 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> token 复用本轮统一登录；栏目复用 TC-article-IT-005 的栏目 B（id=01a00b98-4c92-70b3-be6d-42e449c9bdfb）

## Step 1: 前置：POST /api/admin/articles 创建文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it009-cover-172204.png","title":"待删除文章","subtitle":"x","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a00b98-4c92-70b3-be6d-42e449c9bdfb"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4ebb-7f46-9acc-220f217a6108",
  "image": {
    "id": "bound/it009-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it009-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=SMy9Dfmy4KvF%2FHwYcm03I7SPvH8%3D"
  },
  "title": "待删除文章",
  "subtitle": "x",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:06.13988373Z",
  "updatedAt": "2026-08-16T17:22:06.13988373Z"
}
```

## Step 2: DELETE /api/admin/articles/{id}

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/articles/01a00b98-4ebb-7f46-9acc-220f217a6108" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，无 Content-Type）:

```json

```

## Step 3: GET /api/admin/articles/{id}

```bash
curl -s -i "http://localhost:21423/api/admin/articles/01a00b98-4ebb-7f46-9acc-220f217a6108" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "文章不存在：01a00b98-4ebb-7f46-9acc-220f217a6108",
  "path": "/api/admin/articles/01a00b98-4ebb-7f46-9acc-220f217a6108"
}
```

## Step 4: GET /api/admin/articles/page?size=100

```bash
curl -s -i "http://localhost:21423/api/admin/articles/page?page=0&size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "content": [
    {
      "id": "01a00b98-4e23-72d8-8d7c-fea9e55f868f",
      "image": {
        "id": "bound/it008-cover-172204.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=y3mmm5flQIjul4DgRV1HgzBWkpQ%3D"
      },
      "title": "上下线切换文章",
      "subtitle": "x",
      "sortOrder": 1,
      "categoryIds": [
        "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
      ],
      "online": true,
      "createdAt": "2026-08-16T17:22:05.987105Z",
      "updatedAt": "2026-08-16T17:22:06.082943Z"
    },
    {
      "id": "01a00b98-4caf-786b-adf8-211ef7eac794",
      "image": {
        "id": "bound/it005-cover-172204.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BzFSL7fZ7Z5xaCotfWsBKPhVzBA%3D"
      },
      "title": "海岛两日游",
      "subtitle": "附完整行程",
      "sortOrder": 1,
      "categoryIds": [
        "01a00b98-4c76-7393-a249-e392b12b12e2",
        "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
      ],
      "online": true,
      "createdAt": "2026-08-16T17:22:05.615468Z",
      "updatedAt": "2026-08-16T17:22:05.615468Z"
    },
    {
      "id": "01a00b98-4bff-797e-b2d5-87bcf3a842b8",
      "image": {
        "id": "bound/it004-cover-172204.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=GjQsCXKErTVh8u54EduSREyG3Pw%3D"
      },
      "title": "删除栏目验证文章",
      "subtitle": "关联 A、B 双栏目",
      "sortOrder": 1,
      "categoryIds": [
        "01a00b98-4bc5-7f85-b32f-8b71e09d50ce",
        "01a00b98-4be1-7d73-8c88-4ffd50cde113"
      ],
      "online": true,
      "createdAt": "2026-08-16T17:22:05.439515Z",
      "updatedAt": "2026-08-16T17:22:05.439515Z"
    },
    {
      "id": "01a00b98-4dc1-72a7-9141-7ea6ad0368c6",
      "image": {
        "id": "bound/it007-cover-172204.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=yGJXvlSO8tWsAH%2FVXCQfEnJzMP0%3D"
      },
      "title": "更新后标题",
      "subtitle": "更新后副标题",
      "sortOrder": 9,
      "categoryIds": [
        "01a00b98-4da3-77be-8a25-7118fbe9b7df"
      ],
      "online": true,
      "createdAt": "2026-08-16T17:22:05.8891Z",
      "updatedAt": "2026-08-16T17:22:05.922094Z"
    }
  ],
  "page": 1,
  "size": 30,
  "totalElements": 4,
  "totalPages": 1
}
```
