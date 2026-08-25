# TC-article-IT-017 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## 前置: 创建栏目 IT017栏目

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT017栏目","icon":"images/it017-a.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-76e3-71c2-a86c-60a715b0a528",
  "name": "IT017栏目",
  "icon": {
    "id": "bound/it017-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017-a.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6iMq3N%2FAQngRIgMI0E0cXTWj%2F30%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:23:36.291048784Z",
  "updatedAt": "2026-08-25T09:23:36.291048784Z"
}
```

## 前置: 创建设置了 coverTitle/intro/tags 的文章

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it017-cover.png","title":"IT017文章","coverTitle":"原封面标题","intro":"原引言","tags":["原标签"],"categoryIds":["01a0383b-76e3-71c2-a86c-60a715b0a528"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-76fd-71ff-8e27-0fc08e24c23d",
  "image": {
    "id": "bound/it017-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lHEptwRvcgCsIhYIoXBD3aTF0LU%3D"
  },
  "title": "IT017文章",
  "coverTitle": "原封面标题",
  "subtitle": null,
  "intro": "原引言",
  "tags": [
    "原标签"
  ],
  "contentHtml": null,
  "sortOrder": 0,
  "categoryIds": [
    "01a0383b-76e3-71c2-a86c-60a715b0a528"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:36.317070845Z",
  "updatedAt": "2026-08-25T09:23:36.317070845Z"
}
```

## Step 2: PUT coverTitle="  "、intro=" "、tags=[" 甲 ","","乙"]

```bash
curl -s -i -X PUT "http://localhost:8080/api/admin/articles/01a0383b-76fd-71ff-8e27-0fc08e24c23d" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it017-cover.png","title":"IT017文章","coverTitle":"  ","intro":" ","tags":[" 甲 ","","乙"],"categoryIds":["01a0383b-76e3-71c2-a86c-60a715b0a528"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-76fd-71ff-8e27-0fc08e24c23d",
  "image": {
    "id": "bound/it017-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lHEptwRvcgCsIhYIoXBD3aTF0LU%3D"
  },
  "title": "IT017文章",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [
    "甲",
    "乙"
  ],
  "contentHtml": null,
  "sortOrder": 0,
  "categoryIds": [
    "01a0383b-76e3-71c2-a86c-60a715b0a528"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:36.317071Z",
  "updatedAt": "2026-08-25T09:23:36.347638747Z"
}
```

## Step 3: GET /api/admin/articles/{id}

```bash
curl -s -i "http://localhost:8080/api/admin/articles/01a0383b-76fd-71ff-8e27-0fc08e24c23d" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383b-76fd-71ff-8e27-0fc08e24c23d",
  "image": {
    "id": "bound/it017-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017-cover.png?Expires=1787651616&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lHEptwRvcgCsIhYIoXBD3aTF0LU%3D"
  },
  "title": "IT017文章",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [
    "甲",
    "乙"
  ],
  "contentHtml": null,
  "sortOrder": 0,
  "categoryIds": [
    "01a0383b-76e3-71c2-a86c-60a715b0a528"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:23:36.317071Z",
  "updatedAt": "2026-08-25T09:23:36.348185Z"
}
```
