# TC-article-IT-006 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## Step 1: POST /api/admin/articles 缺 title

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it006-cover.png","subtitle":"副标题","sortOrder":1,"categoryIds":["01a0383a-d6b8-7523-a3ab-68fffb104604"],"online":true}'
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
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"IT006无图","subtitle":"副标题","sortOrder":1,"categoryIds":["01a0383a-d6b8-7523-a3ab-68fffb104604"],"online":true}'
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

## Step 3: POST /api/admin/articles categoryIds 含不存在 UUID

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it006-cover.png","title":"IT006坏栏目","sortOrder":1,"categoryIds":["00000000-0000-4000-8000-000000000999"],"online":true}'
```

实际响应（HTTP 400，Content-Type: application/json）:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "关联栏目不存在：00000000-0000-4000-8000-000000000999",
  "path": "/api/admin/articles"
}
```

## Step 4: GET /api/admin/articles/page 确认均未创建

```bash
curl -s -i "http://localhost:8080/api/admin/articles/page?page=0&size=50" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "content": [
    {
      "id": "01a0383a-d6f4-7472-86af-0a33fb35aaaf",
      "image": {
        "id": "bound/it005-cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-cover.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZFm7IwDAekh%2B6Xt%2BRGPnkvjLOnk%3D"
      },
      "title": "海岛两日游",
      "coverTitle": null,
      "subtitle": "附完整行程",
      "intro": null,
      "tags": [],
      "sortOrder": 1,
      "categoryIds": [
        "01a0383a-d6b8-7523-a3ab-68fffb104604",
        "01a0383a-d6d4-7424-8fc1-93a5af080a9f"
      ],
      "online": true,
      "createdAt": "2026-08-25T09:22:55.348223Z",
      "updatedAt": "2026-08-25T09:22:55.348223Z"
    },
    {
      "id": "01a0383a-d639-7df3-be78-0b5feba27a03",
      "image": {
        "id": "bound/it004-cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004-cover.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2BIgBY31YIlq%2BMRzaLtOUT166a%2BE%3D"
      },
      "title": "IT004文章",
      "coverTitle": null,
      "subtitle": "副标题",
      "intro": null,
      "tags": [],
      "sortOrder": 1,
      "categoryIds": [
        "01a0383a-d61b-7c12-ad4f-c906e5ed7051"
      ],
      "online": true,
      "createdAt": "2026-08-25T09:22:55.161823Z",
      "updatedAt": "2026-08-25T09:22:55.161823Z"
    },
    {
      "id": "01a03839-e2b9-7228-8ae6-f0a10bdf0300",
      "image": {
        "id": "bound/it015-cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-cover.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=FQLMr3DptAeWo0Wap9irySrqAw8%3D"
      },
      "title": "详情页标题",
      "coverTitle": "封面标题",
      "subtitle": "副标题",
      "intro": "这是引言",
      "tags": [
        "约会",
        "周末"
      ],
      "sortOrder": 1,
      "categoryIds": [
        "01a03839-e266-7cd8-bf57-e3b11d986c45"
      ],
      "online": true,
      "createdAt": "2026-08-25T09:21:52.823854Z",
      "updatedAt": "2026-08-25T09:21:52.823854Z"
    }
  ],
  "page": 1,
  "size": 30,
  "totalElements": 3,
  "totalPages": 1
}
```
