# TC-featured-IT-025 GET /api/app/featured-cycle-items?period=&type= 周期与类型同时过滤 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 直接接受的占位 key（该实例不校验 OSS 对象存在），fixture 名带本轮后缀 `3u50` 防撞名。

## 前置核对: app 周期推荐当前为空（保证"恰含"断言不受历史数据污染）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[]
```

## 前置: 上线活动

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act025.png"],"title":"act025-3u50","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4b1e-7243-bbc5-e9a9537a5b20","images":[{"id":"bound/act025.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act025.png?Expires=1787664319&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=5OIw0NC%2BDoiiGhojUmmQsHOwEUA%3D"}],"title":"act025-3u50","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-25T12:55:19.070097141Z","updatedAt":"2026-08-25T12:55:19.070097141Z"}
```

## 前置: 上线文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-15.png","contentHtml":"<p>正文</p>","title":"文章025-3u50","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4b28-7722-b7ad-6f23355f2de6","image":{"id":"bound/art-15.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-15.png?Expires=1787664319&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=aSizrplrDbV5OHTnOIzr%2BiDoAog%3D"},"title":"文章025-3u50","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-25T12:55:19.080417472Z","updatedAt":"2026-08-25T12:55:19.080417472Z"}
```

## 前置: MENSTRUAL ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ACTIVITY.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a038fd-4b1e-7243-bbc5-e9a9537a5b20","description":"活动条目025"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4b32-7a68-8c76-91933cbb9afe","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"activityId":"01a038fd-4b1e-7243-bbc5-e9a9537a5b20","routeId":null,"articleId":null,"relatedTitle":"act025-3u50","title":null,"subtitle":null,"description":"活动条目025","note":null,"banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664319&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=jlKlVsr98x92FOKtEw1qeI14mQo%3D"},"createdAt":"2026-08-25T12:55:19.09062478Z","updatedAt":"2026-08-25T12:55:19.09062478Z"}
```

## 前置: MENSTRUAL ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ARTICLE.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","articleId":"01a038fd-4b28-7722-b7ad-6f23355f2de6","title":"文章条目025"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4b3e-74b1-9f40-77e16520eb1c","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":0,"online":true,"activityId":null,"routeId":null,"articleId":"01a038fd-4b28-7722-b7ad-6f23355f2de6","relatedTitle":"文章025-3u50","title":"文章条目025","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664319&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ewmQv%2BGtr%2BPsmzTzVXsDaQCF9Fw%3D"},"createdAt":"2026-08-25T12:55:19.102268007Z","updatedAt":"2026-08-25T12:55:19.102268007Z"}
```

## 前置: FOLLICULAR ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-FOLLICULAR-ARTICLE.png","online":true,"phase":"FOLLICULAR","type":"ARTICLE","articleId":"01a038fd-4b28-7722-b7ad-6f23355f2de6","title":"卵泡期文章条目025"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4b48-796e-816a-c731b0498cde","phase":"FOLLICULAR","type":"ARTICLE","sortOrder":0,"online":true,"activityId":null,"routeId":null,"articleId":"01a038fd-4b28-7722-b7ad-6f23355f2de6","relatedTitle":"文章025-3u50","title":"卵泡期文章条目025","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/banner-FOLLICULAR-ARTICLE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-FOLLICULAR-ARTICLE.png?Expires=1787664319&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=jswkz%2FOjCo44agcUibgK0J3%2BJqA%3D"},"createdAt":"2026-08-25T12:55:19.112561961Z","updatedAt":"2026-08-25T12:55:19.112561961Z"}
```

## Step 2: GET ?period=MENSTRUAL&type=ARTICLE

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL&type=ARTICLE" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-4b3e-74b1-9f40-77e16520eb1c","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664319&OSSAccessKeyId=placeholder&Signature=u5ZbwIqJm5KOYfkOD49NOstGzoo%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-4b28-7722-b7ad-6f23355f2de6","title":"文章条目025","subtitle":null,"description":null,"note":null}]
```

## 清理: DELETE 周期条目 01a038fd-4b32-7a68-8c76-91933cbb9afe

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4b32-7a68-8c76-91933cbb9afe" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-4b3e-74b1-9f40-77e16520eb1c

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4b3e-74b1-9f40-77e16520eb1c" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-4b48-796e-816a-c731b0498cde

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4b48-796e-816a-c731b0498cde" -H "Authorization: Bearer $TOKEN"
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

清理 DELETE 状态码: 200, 200, 200；清理后 app 端返回 []
