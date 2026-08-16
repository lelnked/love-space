# TC-article-IT-010 POST /api/admin/articles 富文本 img src 存 objectKey、admin 读时替换签名 URL — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> token 复用本轮统一登录；栏目复用 TC-article-IT-005 的栏目 B（id=01a00b98-4c92-70b3-be6d-42e449c9bdfb）

## Step 1: POST /api/admin/articles contentHtml 含 2 个 objectKey img 与段落文本

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it010-cover-172204.png","title":"富文本图片文章","subtitle":"x","contentHtml":"<p>图文混排段落一</p><img src=\"images/it010-a-172204.png\"><p>图文混排段落二</p><img src=\"images/it010-b-172204.png\">","sortOrder":1,"categoryIds":["01a00b98-4c92-70b3-be6d-42e449c9bdfb"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4f27-79f6-b156-59d03351d6ed",
  "image": {
    "id": "bound/it010-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zlTxXA4Ho3UN3bCVgsdRJ%2FZWB38%3D"
  },
  "title": "富文本图片文章",
  "subtitle": "x",
  "contentHtml": "<p>图文混排段落一</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-a-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=wdSQw8jUwAajfx60299rjV8XoVs%3D\"><p>图文混排段落二</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-b-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=FgJ2HyvPXa6NlwCKj0H%2BQKbDmOE%3D\">",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:06.247556492Z",
  "updatedAt": "2026-08-16T17:22:06.247556492Z"
}
```

## Step 2: GET /api/admin/articles/{id}

```bash
curl -s -i "http://localhost:21423/api/admin/articles/01a00b98-4f27-79f6-b156-59d03351d6ed" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4f27-79f6-b156-59d03351d6ed",
  "image": {
    "id": "bound/it010-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zlTxXA4Ho3UN3bCVgsdRJ%2FZWB38%3D"
  },
  "title": "富文本图片文章",
  "subtitle": "x",
  "contentHtml": "<p>图文混排段落一</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-a-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=wdSQw8jUwAajfx60299rjV8XoVs%3D\"><p>图文混排段落二</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-b-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=FgJ2HyvPXa6NlwCKj0H%2BQKbDmOE%3D\">",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:06.247556Z",
  "updatedAt": "2026-08-16T17:22:06.247556Z"
}
```

## Step 3: PUT /api/admin/articles/{id} contentHtml 改为纯文本后 GET 详情

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a00b98-4f27-79f6-b156-59d03351d6ed" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it010-cover-172204.png","title":"富文本图片文章","subtitle":"x","contentHtml":"<p>纯文本无图片</p>","sortOrder":1,"categoryIds":["01a00b98-4c92-70b3-be6d-42e449c9bdfb"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4f27-79f6-b156-59d03351d6ed",
  "image": {
    "id": "bound/it010-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zlTxXA4Ho3UN3bCVgsdRJ%2FZWB38%3D"
  },
  "title": "富文本图片文章",
  "subtitle": "x",
  "contentHtml": "<p>纯文本无图片</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:06.247556Z",
  "updatedAt": "2026-08-16T17:22:06.247556Z"
}
```

## Step 4: GET /api/admin/articles/{id}（更新后）

```bash
curl -s -i "http://localhost:21423/api/admin/articles/01a00b98-4f27-79f6-b156-59d03351d6ed" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4f27-79f6-b156-59d03351d6ed",
  "image": {
    "id": "bound/it010-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it010-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zlTxXA4Ho3UN3bCVgsdRJ%2FZWB38%3D"
  },
  "title": "富文本图片文章",
  "subtitle": "x",
  "contentHtml": "<p>纯文本无图片</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:06.247556Z",
  "updatedAt": "2026-08-16T17:22:06.313919Z"
}
```
