# TC-article-IT-009 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## 前置: 创建栏目 IT009栏目

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT009栏目","icon":"images/it009-a.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7562-71d8-a9d7-776c897873ee",
  "name": "IT009栏目",
  "icon": {
    "id": "bound/it009-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it009-a.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=wG6m7hcxA4CEvEZpzaAlC%2BYRzaw%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:23:35.906071644Z",
  "updatedAt": "2026-08-25T09:23:35.906071644Z"
}
```

## 前置: 创建一篇文章

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it009-cover.png","title":"IT009待删除","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a0383b-7562-71d8-a9d7-776c897873ee"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-758c-7c79-9e12-07fa2306c1a6",
  "image": {
    "id": "bound/it009-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it009-cover.png?Expires=1787651615&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=3UQ8DIoX2VphCjerIAZ68itWnxE%3D"
  },
  "title": "IT009待删除",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383b-7562-71d8-a9d7-776c897873ee"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:35.94871972Z",
  "updatedAt": "2026-08-25T09:23:35.94871972Z"
}
```

## Step 2: DELETE /api/admin/articles/{id}

```bash
curl -s -i -X DELETE "http://localhost:8080/api/admin/articles/01a0383b-758c-7c79-9e12-07fa2306c1a6" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，）:
```json
```

## Step 3: GET /api/admin/articles/{id} 已删除

```bash
curl -s -i "http://localhost:8080/api/admin/articles/01a0383b-758c-7c79-9e12-07fa2306c1a6" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400，Content-Type: application/json）:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "文章不存在：01a0383b-758c-7c79-9e12-07fa2306c1a6",
  "path": "/api/admin/articles/01a0383b-758c-7c79-9e12-07fa2306c1a6"
}
```

## Step 4: GET /api/admin/articles/page 不含该文章

```bash
curl -s -i "http://localhost:8080/api/admin/articles/page?page=0&size=50" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "content": [
    {
      "id": "01a0383b-74df-73f3-acc3-781f1d117f5f",
      "image": {
        "id": "bound/it008-cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=nwNMb5iVhhY%2FhkIc%2BN7LmnPCDxM%3D"
      },
      "title": "IT008文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 1,
      "categoryIds": [
        "01a0383b-74c3-74e1-8980-96f6b01dfd77"
      ],
      "online": true,
      "createdAt": "2026-08-25T09:23:35.775189Z",
      "updatedAt": "2026-08-25T09:23:35.861569Z"
    },
    {
      "id": "01a0383a-d6f4-7472-86af-0a33fb35aaaf",
      "image": {
        "id": "bound/it005-cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=4ojBHCS1SgFXTWVbt%2BOo71JHd9U%3D"
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
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=J8Z1hFBQEPmBSctqLGyYqhd%2BEhI%3D"
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
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Xnn7B7WKqRVjo%2BKtpfJLGzcKcZ4%3D"
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
    },
    {
      "id": "01a0383b-7474-754e-8f3e-cb8149392078",
      "image": {
        "id": "bound/it007-cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Li3cPkABE%2FhSsvN%2BA08zFZGrqyU%3D"
      },
      "title": "IT007新标题",
      "coverTitle": null,
      "subtitle": "新副标题",
      "intro": null,
      "tags": [],
      "sortOrder": 9,
      "categoryIds": [
        "01a0383b-7455-75bb-8cd9-615192b0e940"
      ],
      "online": true,
      "createdAt": "2026-08-25T09:23:35.66827Z",
      "updatedAt": "2026-08-25T09:23:35.700972Z"
    }
  ],
  "page": 1,
  "size": 30,
  "totalElements": 5,
  "totalPages": 1
}
```
