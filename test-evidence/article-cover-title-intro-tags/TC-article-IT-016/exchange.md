# TC-article-IT-016 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## 前置: 创建栏目 IT016栏目

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT016栏目","icon":"images/it016-a.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7677-7027-b6ab-cb82492c4d95",
  "name": "IT016栏目",
  "icon": {
    "id": "bound/it016-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it016-a.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cY6EM4razR2Cs%2FUaO22%2FOJrlqT8%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:23:36.182953892Z",
  "updatedAt": "2026-08-25T09:23:36.182953892Z"
}
```

## Step 1: POST /api/admin/articles 不带 coverTitle/intro/tags

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it016-cover.png","title":"IT016最简文章","categoryIds":["01a0383b-7677-7027-b6ab-cb82492c4d95"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7690-7d74-8756-ae6586cada57",
  "image": {
    "id": "bound/it016-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it016-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=KXaum3DNmjcInTprvDq6fyAvHKM%3D"
  },
  "title": "IT016最简文章",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": null,
  "sortOrder": 0,
  "categoryIds": [
    "01a0383b-7677-7027-b6ab-cb82492c4d95"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:36.208785819Z",
  "updatedAt": "2026-08-25T09:23:36.208785819Z"
}
```

## Step 2: GET /api/admin/articles/{id}

```bash
curl -s -i "http://localhost:8080/api/admin/articles/01a0383b-7690-7d74-8756-ae6586cada57" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-7690-7d74-8756-ae6586cada57",
  "image": {
    "id": "bound/it016-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it016-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=KXaum3DNmjcInTprvDq6fyAvHKM%3D"
  },
  "title": "IT016最简文章",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": null,
  "sortOrder": 0,
  "categoryIds": [
    "01a0383b-7677-7027-b6ab-cb82492c4d95"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:36.208786Z",
  "updatedAt": "2026-08-25T09:23:36.208786Z"
}
```

## Step 3: GET /api/admin/articles/page 查该文章列表项

```bash
curl -s -i "http://localhost:8080/api/admin/articles/page?page=0&size=50" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "content": [
    {
      "id": "01a0383b-7690-7d74-8756-ae6586cada57",
      "image": {
        "id": "bound/it016-cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it016-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=KXaum3DNmjcInTprvDq6fyAvHKM%3D"
      },
      "title": "IT016最简文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [
        "01a0383b-7677-7027-b6ab-cb82492c4d95"
      ],
      "online": true,
      "createdAt": "2026-08-25T09:23:36.208786Z",
      "updatedAt": "2026-08-25T09:23:36.208786Z"
    },
    {
      "id": "01a0383b-7613-791a-b382-15e45f3d2b3b",
      "image": {
        "id": "bound/it010-cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=JkZF64FPRmOfN9Rl3gV7951o9PY%3D"
      },
      "title": "IT010富文本",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 1,
      "categoryIds": [
        "01a0383b-75f7-70f3-bc45-5c5dca552b76"
      ],
      "online": true,
      "createdAt": "2026-08-25T09:23:36.08349Z",
      "updatedAt": "2026-08-25T09:23:36.136807Z"
    },
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
  "totalElements": 7,
  "totalPages": 1
}
```
