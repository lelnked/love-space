# TC-featured-IT-019 GET /api/app/featured-cycle-items 按排序号升序 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 直接接受的占位 key（该实例不校验 OSS 对象存在），fixture 名带本轮后缀 `48l1` 防撞名。

## 前置核对: app 周期推荐当前为空（保证"恰含"断言不受历史数据污染）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[]
```

## 前置: 上线文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-1.png","contentHtml":"<p>正文</p>","title":"文章019-48l1","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","image":{"id":"bound/art-1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-1.png?Expires=1787664335&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=wSdEwZniUIjut4OHvsXJmW%2F7%2Fe4%3D"},"title":"文章019-48l1","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-25T12:55:35.925795489Z","updatedAt":"2026-08-25T12:55:35.925795489Z"}
```

## 前置: sortOrder=2 (第1建)

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ARTICLE.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","title":"条目019-第1建","sortOrder":2}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-8d01-7937-8bac-52346297fe5e","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":2,"online":true,"activityId":null,"routeId":null,"articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","relatedTitle":"文章019-48l1","title":"条目019-第1建","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664335&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ngdyd9As6h%2B4z5NC%2BXQKHpEXIqU%3D"},"createdAt":"2026-08-25T12:55:35.93754551Z","updatedAt":"2026-08-25T12:55:35.93754551Z"}
```

## 前置: sortOrder=1 (第2建)

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ARTICLE.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","title":"条目019-第2建","sortOrder":1}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-8d2f-7914-987c-4520663d4ee9","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":1,"online":true,"activityId":null,"routeId":null,"articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","relatedTitle":"文章019-48l1","title":"条目019-第2建","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664335&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ngdyd9As6h%2B4z5NC%2BXQKHpEXIqU%3D"},"createdAt":"2026-08-25T12:55:35.983523754Z","updatedAt":"2026-08-25T12:55:35.983523754Z"}
```

## 前置: sortOrder=3 (第3建)

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ARTICLE.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","title":"条目019-第3建","sortOrder":3}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-8d5a-7da1-ba5e-02f429cc4375","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":3,"online":true,"activityId":null,"routeId":null,"articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","relatedTitle":"文章019-48l1","title":"条目019-第3建","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664336&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=wp3MEPzEXwQ3oFqxWzLPUW7TIEE%3D"},"createdAt":"2026-08-25T12:55:36.026819821Z","updatedAt":"2026-08-25T12:55:36.026819821Z"}
```

## 前置: sortOrder=1 (第4建)

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ARTICLE.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","title":"条目019-第4建","sortOrder":1}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-8d85-7af3-9d26-fa99c63c460b","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":1,"online":true,"activityId":null,"routeId":null,"articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","relatedTitle":"文章019-48l1","title":"条目019-第4建","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664336&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=wp3MEPzEXwQ3oFqxWzLPUW7TIEE%3D"},"createdAt":"2026-08-25T12:55:36.069654576Z","updatedAt":"2026-08-25T12:55:36.069654576Z"}
```

## 前置: sortOrder=1 (第5建)

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ARTICLE.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","title":"条目019-第5建","sortOrder":1}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-8db0-7d39-b76c-586612a179b7","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":1,"online":true,"activityId":null,"routeId":null,"articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","relatedTitle":"文章019-48l1","title":"条目019-第5建","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664336&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=wp3MEPzEXwQ3oFqxWzLPUW7TIEE%3D"},"createdAt":"2026-08-25T12:55:36.112797996Z","updatedAt":"2026-08-25T12:55:36.112797996Z"}
```

## Step 2: GET ?period=MENSTRUAL

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-8db0-7d39-b76c-586612a179b7","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664336&OSSAccessKeyId=placeholder&Signature=iACmtzHThj%2FMXg00VwA%2BOfsMymI%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","title":"条目019-第5建","subtitle":null,"description":null,"note":null},{"id":"01a038fd-8d85-7af3-9d26-fa99c63c460b","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664336&OSSAccessKeyId=placeholder&Signature=iACmtzHThj%2FMXg00VwA%2BOfsMymI%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","title":"条目019-第4建","subtitle":null,"description":null,"note":null},{"id":"01a038fd-8d2f-7914-987c-4520663d4ee9","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664336&OSSAccessKeyId=placeholder&Signature=iACmtzHThj%2FMXg00VwA%2BOfsMymI%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","title":"条目019-第2建","subtitle":null,"description":null,"note":null},{"id":"01a038fd-8d01-7937-8bac-52346297fe5e","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664336&OSSAccessKeyId=placeholder&Signature=iACmtzHThj%2FMXg00VwA%2BOfsMymI%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","title":"条目019-第1建","subtitle":null,"description":null,"note":null},{"id":"01a038fd-8d5a-7da1-ba5e-02f429cc4375","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664336&OSSAccessKeyId=placeholder&Signature=iACmtzHThj%2FMXg00VwA%2BOfsMymI%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-8cf5-7d62-b8c0-6b5dbdf340e4","title":"条目019-第3建","subtitle":null,"description":null,"note":null}]
```

## 清理: DELETE 周期条目 01a038fd-8d01-7937-8bac-52346297fe5e

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-8d01-7937-8bac-52346297fe5e" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-8d2f-7914-987c-4520663d4ee9

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-8d2f-7914-987c-4520663d4ee9" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-8d5a-7da1-ba5e-02f429cc4375

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-8d5a-7da1-ba5e-02f429cc4375" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-8d85-7af3-9d26-fa99c63c460b

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-8d85-7af3-9d26-fa99c63c460b" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-8db0-7d39-b76c-586612a179b7

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-8db0-7d39-b76c-586612a179b7" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理后核对 app 周期推荐为空

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[]
```

清理 DELETE 状态码: 200, 200, 200, 200, 200；清理后 app 端返回 []
