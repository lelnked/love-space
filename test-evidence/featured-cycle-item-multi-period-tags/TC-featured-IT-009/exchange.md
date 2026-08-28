# TC-featured-IT-009 POST /api/admin/featured-cycle-items 创建文章类周期推荐 — 请求/响应存证

执行日期: 2026-08-28 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `g8k` 防撞名。清理请求（DELETE）不入存证。

## 前置: 文章 art009

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art009.png","contentHtml":"<p>正文</p>","title":"art009-g8k","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04880-e295-7a16-8a63-159af5b1c3b3","image":{"id":"bound/art009.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art009.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art009-g8k","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-28T13:13:21.301585095Z","updatedAt":"2026-08-28T13:13:21.301585095Z"}
```

## Step 2: POST 文章类周期推荐

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b009.png","phase":"LUTEAL","type":"ARTICLE","targetId":"01a04880-e295-7a16-8a63-159af5b1c3b3","title":"黄体期生活法"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04880-e2a2-7699-a877-d5a9b75af010","phase":"LUTEAL","type":"ARTICLE","sortOrder":0,"online":false,"targetId":"01a04880-e295-7a16-8a63-159af5b1c3b3","relatedTitle":"art009-g8k","title":"黄体期生活法","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b009.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b009.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:13:21.314361427Z","updatedAt":"2026-08-28T13:13:21.314361427Z"}
```

## Step 3: GET 详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a04880-e2a2-7699-a877-d5a9b75af010" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04880-e2a2-7699-a877-d5a9b75af010","phase":"LUTEAL","type":"ARTICLE","sortOrder":0,"online":false,"targetId":"01a04880-e295-7a16-8a63-159af5b1c3b3","relatedTitle":"art009-g8k","title":"黄体期生活法","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b009.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b009.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:13:21.314361Z","updatedAt":"2026-08-28T13:13:21.314361Z"}
```
