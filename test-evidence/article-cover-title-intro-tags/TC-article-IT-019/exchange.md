# TC-article-IT-019 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## 前置: 创建栏目 IT019栏目（sortOrder=1）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT019栏目","icon":"images/it019-a.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-511c-7e75-aedb-8423b6ff1499",
  "name": "IT019栏目",
  "icon": {
    "id": "bound/it019-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it019-a.png?Expires=1787651672&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=KNPAU0kd0bkohos1SRnmpgJgcNc%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:24:32.15683383Z",
  "updatedAt": "2026-08-25T09:24:32.15683383Z"
}
```

## 前置: 文章甲（intro「这是引言」+ tags[恋爱,指南]）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it019-1.png","title":"IT019有引言","intro":"这是引言","tags":["恋爱","指南"],"contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a0383c-511c-7e75-aedb-8423b6ff1499"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-513a-7b5a-b7fd-af991beaa6cf",
  "image": {
    "id": "bound/it019-1.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it019-1.png?Expires=1787651672&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=XX0L0NiROqxuhgk5GNkc9aLpuyY%3D"
  },
  "title": "IT019有引言",
  "coverTitle": null,
  "subtitle": null,
  "intro": "这是引言",
  "tags": [
    "恋爱",
    "指南"
  ],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383c-511c-7e75-aedb-8423b6ff1499"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:24:32.186663278Z",
  "updatedAt": "2026-08-25T09:24:32.186663278Z"
}
```

## 前置: 文章乙（不设 intro 与 tags）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it019-2.png","title":"IT019无引言","contentHtml":"<p>正文</p>","sortOrder":2,"categoryIds":["01a0383c-511c-7e75-aedb-8423b6ff1499"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-5155-7d64-abba-edfb0428fc10",
  "image": {
    "id": "bound/it019-2.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it019-2.png?Expires=1787651672&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tUpf8quaCT78sW7mEzp1DPDs%2BUY%3D"
  },
  "title": "IT019无引言",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 2,
  "categoryIds": [
    "01a0383c-511c-7e75-aedb-8423b6ff1499"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:24:32.213772128Z",
  "updatedAt": "2026-08-25T09:24:32.213772128Z"
}
```

## Step 2: GET /api/app/articles/{设了的 id}

```bash
curl -s -i "http://localhost:8081/api/app/articles/01a0383c-513a-7b5a-b7fd-af991beaa6cf" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-513a-7b5a-b7fd-af991beaa6cf",
  "image": {
    "id": "bound/it019-1.png",
    "url": "https://love-space-test-0524.oss-test.example.com/bound/it019-1.png?Expires=1787651672&OSSAccessKeyId=test-ak&Signature=SVYauCBk2RLwmKTJ6CDU%2BprncP0%3D"
  },
  "title": "IT019有引言",
  "subtitle": null,
  "intro": "这是引言",
  "tags": [
    "恋爱",
    "指南"
  ],
  "contentHtml": "<p>正文</p>",
  "categoryIds": [
    "01a0383c-511c-7e75-aedb-8423b6ff1499"
  ]
}
```

## Step 3: GET /api/app/articles/{未设的 id}

```bash
curl -s -i "http://localhost:8081/api/app/articles/01a0383c-5155-7d64-abba-edfb0428fc10" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-5155-7d64-abba-edfb0428fc10",
  "image": {
    "id": "bound/it019-2.png",
    "url": "https://love-space-test-0524.oss-test.example.com/bound/it019-2.png?Expires=1787651672&OSSAccessKeyId=test-ak&Signature=LwgkuhCjiRbaJFhq%2BELyTpe%2BJPs%3D"
  },
  "title": "IT019无引言",
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "categoryIds": [
    "01a0383c-511c-7e75-aedb-8423b6ff1499"
  ]
}
```
