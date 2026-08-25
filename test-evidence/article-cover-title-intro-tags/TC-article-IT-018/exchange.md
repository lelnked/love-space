# TC-article-IT-018 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## 前置: 创建栏目 IT018栏目（sortOrder=1）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT018栏目","icon":"images/it018-a.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-505d-7096-81c9-dc00dc0e38e2",
  "name": "IT018栏目",
  "icon": {
    "id": "bound/it018-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it018-a.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Fn1ApEYYuJUsPMC7Q%2BFmHBmt1i0%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:24:31.964969893Z",
  "updatedAt": "2026-08-25T09:24:31.964969893Z"
}
```

## 前置: 文章甲（coverTitle「封面甲」+ tags[约会]）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it018-1.png","title":"文章甲","coverTitle":"封面甲","subtitle":"副标题甲","tags":["约会"],"sortOrder":1,"categoryIds":["01a0383c-505d-7096-81c9-dc00dc0e38e2"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-5091-7733-b397-1f7cdfc9fcb9",
  "image": {
    "id": "bound/it018-1.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it018-1.png?Expires=1787651672&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DPDs5Z%2BKAb8Fezv85CMsFx%2FescM%3D"
  },
  "title": "文章甲",
  "coverTitle": "封面甲",
  "subtitle": "副标题甲",
  "intro": null,
  "tags": [
    "约会"
  ],
  "contentHtml": null,
  "sortOrder": 1,
  "categoryIds": [
    "01a0383c-505d-7096-81c9-dc00dc0e38e2"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:24:32.017260236Z",
  "updatedAt": "2026-08-25T09:24:32.017260236Z"
}
```

## 前置: 文章乙（不设 coverTitle 与 tags）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it018-2.png","title":"文章乙","subtitle":"副标题乙","sortOrder":2,"categoryIds":["01a0383c-505d-7096-81c9-dc00dc0e38e2"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-50c4-7c84-a544-ca88b7fdb448",
  "image": {
    "id": "bound/it018-2.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it018-2.png?Expires=1787651672&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=YnG7EdqI0EXXhW5NKmRSP9iun8g%3D"
  },
  "title": "文章乙",
  "coverTitle": null,
  "subtitle": "副标题乙",
  "intro": null,
  "tags": [],
  "contentHtml": null,
  "sortOrder": 2,
  "categoryIds": [
    "01a0383c-505d-7096-81c9-dc00dc0e38e2"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:24:32.068708005Z",
  "updatedAt": "2026-08-25T09:24:32.068708005Z"
}
```

## Step 2: GET /api/app/articles?categoryId={id}

```bash
curl -s -i "http://localhost:8081/api/app/articles?categoryId=01a0383c-505d-7096-81c9-dc00dc0e38e2" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
[
  {
    "id": "01a0383c-5091-7733-b397-1f7cdfc9fcb9",
    "image": {
      "id": "bound/it018-1.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it018-1.png?Expires=1787651672&OSSAccessKeyId=test-ak&Signature=cYG9tnR0jRCW9s2pwyiIPElpGo8%3D"
    },
    "coverTitle": "封面甲",
    "title": "文章甲",
    "subtitle": "副标题甲",
    "tags": [
      "约会"
    ]
  },
  {
    "id": "01a0383c-50c4-7c84-a544-ca88b7fdb448",
    "image": {
      "id": "bound/it018-2.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it018-2.png?Expires=1787651672&OSSAccessKeyId=test-ak&Signature=MxT019BlCT7woXJuTrJIEGmxxJ4%3D"
    },
    "coverTitle": "文章乙",
    "title": "文章乙",
    "subtitle": "副标题乙",
    "tags": []
  }
]
```
