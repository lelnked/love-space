# TC-featured-IT-024 GET /api/app/featured-cycle-items?period= 按周期过滤 — 请求/响应存证

执行日期: 2026-08-28 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `a1` 防撞名。清理请求（DELETE）不入存证。

## 前置核对: 周期条目表为空（保证「恰含 N 条」断言不受历史数据污染）

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=1&size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"content":[],"page":1,"size":30,"totalElements":0,"totalPages":0}
```

## 前置: 活动 act024

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act024.png"],"title":"act024-a1d143","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f007-759c-acd8-962211809ded","images":[{"id":"bound/act024.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act024.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act024-a1d143","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:41.351326483Z","updatedAt":"2026-08-28T13:16:41.351326483Z"}
```

## 前置: 文章 art024a

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art024a.png","contentHtml":"<p>正文</p>","title":"art024a-a1d143","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f00e-7beb-a685-4cfbd1feb8fc","image":{"id":"bound/art024a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art024a.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art024a-a1d143","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-28T13:16:41.358721885Z","updatedAt":"2026-08-28T13:16:41.358721885Z"}
```

## 前置: 文章 art024b

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art024b.png","contentHtml":"<p>正文</p>","title":"art024b-a1d143","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f016-73fd-bb19-d048997f9241","image":{"id":"bound/art024b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art024b.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art024b-a1d143","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-28T13:16:41.366226208Z","updatedAt":"2026-08-28T13:16:41.366226208Z"}
```

## 前置: MENSTRUAL 上线 ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b024a.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-f007-759c-acd8-962211809ded","description":"活动条目024"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f01e-7676-bbfe-2beece42b303","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-f007-759c-acd8-962211809ded","relatedTitle":"act024-a1d143","title":null,"subtitle":null,"description":"活动条目024","note":null,"banner":{"id":"bound/b024a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b024a.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.374379895Z","updatedAt":"2026-08-28T13:16:41.374379895Z"}
```

## 前置: MENSTRUAL 上线 ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b024b.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","targetId":"01a04883-f00e-7beb-a685-4cfbd1feb8fc","title":"文章条目024a"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f026-7d63-86bc-e433e6429494","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":0,"online":true,"targetId":"01a04883-f00e-7beb-a685-4cfbd1feb8fc","relatedTitle":"art024a-a1d143","title":"文章条目024a","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b024b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b024b.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.382814103Z","updatedAt":"2026-08-28T13:16:41.382814103Z"}
```

## 前置: FOLLICULAR 上线 ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b024c.png","online":true,"phase":"FOLLICULAR","type":"ARTICLE","targetId":"01a04883-f016-73fd-bb19-d048997f9241","title":"文章条目024b"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f02f-735e-bc6c-6e5773319335","phase":"FOLLICULAR","type":"ARTICLE","sortOrder":0,"online":true,"targetId":"01a04883-f016-73fd-bb19-d048997f9241","relatedTitle":"art024b-a1d143","title":"文章条目024b","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b024c.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b024c.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.391188814Z","updatedAt":"2026-08-28T13:16:41.391188814Z"}
```

## Step 2: GET ?period=MENSTRUAL

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-f026-7d63-86bc-e433e6429494","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b024b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b024b.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-f00e-7beb-a685-4cfbd1feb8fc","title":"文章条目024a","subtitle":null,"description":null,"note":null},{"id":"01a04883-f01e-7676-bbfe-2beece42b303","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b024a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b024a.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-f007-759c-acd8-962211809ded","title":null,"subtitle":null,"description":"活动条目024","note":null}]
```

## Step 3: GET ?period=FOLLICULAR

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=FOLLICULAR" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-f02f-735e-bc6c-6e5773319335","period":["FOLLICULAR"],"type":"ARTICLE","banner":{"id":"bound/b024c.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b024c.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-f016-73fd-bb19-d048997f9241","title":"文章条目024b","subtitle":null,"description":null,"note":null}]
```
