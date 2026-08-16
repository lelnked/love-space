# TC-article-IT-006 POST /api/admin/articles 缺必填或栏目不存在被拒绝 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> token 复用本轮统一登录；合法栏目取 TC-article-IT-005 的栏目 B（id=01a00b98-4c92-70b3-be6d-42e449c9bdfb）

## Step 1: POST /api/admin/articles 缺 title

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it006-cover-172204.png","subtitle":"无标题","contentHtml":"<p>x</p>","sortOrder":1,"categoryIds":["01a00b98-4c92-70b3-be6d-42e449c9bdfb"],"online":true}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "文章标题不能为空",
  "path": "/api/admin/articles"
}
```

## Step 2: POST /api/admin/articles 缺 image

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"title":"无封面文章","subtitle":"x","contentHtml":"<p>x</p>","sortOrder":1,"categoryIds":["01a00b98-4c92-70b3-be6d-42e449c9bdfb"],"online":true}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "文章图片不能为空",
  "path": "/api/admin/articles"
}
```

## Step 3: POST /api/admin/articles categoryIds 含不存在的 UUID

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it006-cover2-172204.png","title":"栏目不存在文章","subtitle":"x","contentHtml":"<p>x</p>","sortOrder":1,"categoryIds":["00000000-0000-0000-0000-000000000999"],"online":true}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "关联栏目不存在：00000000-0000-0000-0000-000000000999",
  "path": "/api/admin/articles"
}
```

## Step 4: GET /api/admin/articles/page?size=100 确认文章均未创建

```bash
curl -s -i "http://localhost:21423/api/admin/articles/page?page=0&size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "content": [
    {
      "id": "01a00b98-4caf-786b-adf8-211ef7eac794",
      "image": {
        "id": "bound/it005-cover-172204.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-cover-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DhC%2BdHVVc2HznYFJn5yg3TPpXx4%3D"
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
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004-cover-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=QWb8R7L4WmyEc8w%2Bnr8Mo%2FZkeW4%3D"
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
    }
  ],
  "page": 1,
  "size": 30,
  "totalElements": 2,
  "totalPages": 1
}
```
