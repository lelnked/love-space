# TC-article-IT-004 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## Step 1a: 创建栏目 A

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"name\":\"IT004栏目A\",\"icon\":\"images/it004-a.png\",\"sortOrder\":1}"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383a-d600-7561-806f-e9176c014320",
  "name": "IT004栏目A",
  "icon": {
    "id": "bound/it004-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004-a.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qSpF%2FbDozvDdI3fA9fujgcqXSKI%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:22:55.104275957Z",
  "updatedAt": "2026-08-25T09:22:55.104275957Z"
}
```

## Step 1b: 创建栏目 B

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"name\":\"IT004栏目B\",\"icon\":\"images/it004-b.png\",\"sortOrder\":2}"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383a-d61b-7c12-ad4f-c906e5ed7051",
  "name": "IT004栏目B",
  "icon": {
    "id": "bound/it004-b.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004-b.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ONg6gWCCUpKXxUpP37wuv2LN300%3D"
  },
  "sortOrder": 2,
  "createdAt": "2026-08-25T09:22:55.131701959Z",
  "updatedAt": "2026-08-25T09:22:55.131701959Z"
}
```

## Step 1c: 创建同时关联 A、B 的文章

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it004-cover.png","title":"IT004文章","subtitle":"副标题","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a0383a-d600-7561-806f-e9176c014320","01a0383a-d61b-7c12-ad4f-c906e5ed7051"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
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
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383a-d600-7561-806f-e9176c014320",
    "01a0383a-d61b-7c12-ad4f-c906e5ed7051"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:22:55.161823038Z",
  "updatedAt": "2026-08-25T09:22:55.161823038Z"
}
```

## Step 2: DELETE /api/admin/article-categories/{A}

```bash
curl -s -i -X DELETE "http://localhost:8080/api/admin/article-categories/01a0383a-d600-7561-806f-e9176c014320" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，）:
```json
```

## Step 3: GET /api/admin/article-categories 确认 A 不在列表

```bash
curl -s -i "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
[
  {
    "id": "01a0383a-d51a-7b64-bc09-95fe39882697",
    "name": "行程攻略",
    "icon": {
      "id": "bound/it001-icon.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it001-icon.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=LrbSB%2BhnWKxp3W8MfDWUiwvKOSU%3D"
    },
    "sortOrder": 1,
    "createdAt": "2026-08-25T09:22:54.874649Z",
    "updatedAt": "2026-08-25T09:22:54.874649Z"
  },
  {
    "id": "01a03839-e266-7cd8-bf57-e3b11d986c45",
    "name": "IT015栏目",
    "icon": {
      "id": "bound/it015-icon.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-icon.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2FdBMRlJ4Fsmk8zxqTjyGTkrzUac%3D"
    },
    "sortOrder": 1,
    "createdAt": "2026-08-25T09:21:52.729297Z",
    "updatedAt": "2026-08-25T09:21:52.729297Z"
  },
  {
    "id": "01a0383a-d61b-7c12-ad4f-c906e5ed7051",
    "name": "IT004栏目B",
    "icon": {
      "id": "bound/it004-b.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004-b.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ONg6gWCCUpKXxUpP37wuv2LN300%3D"
    },
    "sortOrder": 2,
    "createdAt": "2026-08-25T09:22:55.131702Z",
    "updatedAt": "2026-08-25T09:22:55.131702Z"
  },
  {
    "id": "01a0383a-d5ab-7119-9a32-e0c5e7c4f6a1",
    "name": "IT003美食攻略",
    "icon": {
      "id": "bound/it003-icon-new.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it003-icon-new.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=yOCL3FhdAJsW3XF6%2FrrqMTr3h1U%3D"
    },
    "sortOrder": 5,
    "createdAt": "2026-08-25T09:22:55.019004Z",
    "updatedAt": "2026-08-25T09:22:55.048247Z"
  }
]
```

## Step 4: GET /api/admin/articles/{id} 查文章详情

```bash
curl -s -i "http://localhost:8080/api/admin/articles/01a0383a-d639-7df3-be78-0b5feba27a03" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
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
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383a-d61b-7c12-ad4f-c906e5ed7051"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:22:55.161823Z",
  "updatedAt": "2026-08-25T09:22:55.161823Z"
}
```
