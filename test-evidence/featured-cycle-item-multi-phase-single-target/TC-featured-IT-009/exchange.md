# TC-featured-IT-009 POST /api/admin/featured-cycle-items 创建文章类周期推荐 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: 文章 art-m9p5（未被引用）
文章 id=01a0622c-3336-7480-b659-112e4e8d1fc1，title=art-m9p5

## Step 2: POST 文章类周期推荐
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["LUTEAL"], "type": "ARTICLE", "targetId": "01a0622c-3336-7480-b659-112e4e8d1fc1", "title": "黄体期生活法", "banner": "images/b009.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-3341-7dad-accd-9578c97170dc","phases":["LUTEAL"],"type":"ARTICLE","sortOrder":0,"online":false,"targetId":"01a0622c-3336-7480-b659-112e4e8d1fc1","relatedTitle":"art-m9p5","title":"黄体期生活法","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b009.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b009.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.009818075Z","updatedAt":"2026-09-02T12:50:59.009818075Z"}
```

## Step 3: GET 详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-3341-7dad-accd-9578c97170dc" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-3341-7dad-accd-9578c97170dc","phases":["LUTEAL"],"type":"ARTICLE","sortOrder":0,"online":false,"targetId":"01a0622c-3336-7480-b659-112e4e8d1fc1","relatedTitle":"art-m9p5","title":"黄体期生活法","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b009.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b009.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.009818Z","updatedAt":"2026-09-02T12:50:59.009818Z"}
```
