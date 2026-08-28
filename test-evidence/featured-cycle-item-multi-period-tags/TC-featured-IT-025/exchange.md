# TC-featured-IT-025 GET /api/app/featured-cycle-items?period=&type= 周期与类型同时过滤 — 请求/响应存证

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

## 前置: 活动 act025

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act025.png"],"title":"act025-a1cc85","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f089-74ee-b0ad-65fb0c55e7df","images":[{"id":"bound/act025.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act025.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act025-a1cc85","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:41.481281821Z","updatedAt":"2026-08-28T13:16:41.481281821Z"}
```

## 前置: 文章 art025a

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art025a.png","contentHtml":"<p>正文</p>","title":"art025a-a1cc85","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f090-7c6d-8846-5f06f011fd52","image":{"id":"bound/art025a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art025a.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art025a-a1cc85","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-28T13:16:41.488753715Z","updatedAt":"2026-08-28T13:16:41.488753715Z"}
```

## 前置: 文章 art025b

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art025b.png","contentHtml":"<p>正文</p>","title":"art025b-a1cc85","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f098-7373-b587-20bd2699c02d","image":{"id":"bound/art025b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art025b.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art025b-a1cc85","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-28T13:16:41.496192197Z","updatedAt":"2026-08-28T13:16:41.496192197Z"}
```

## 前置: MENSTRUAL 上线 ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b025a.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-f089-74ee-b0ad-65fb0c55e7df","description":"活动条目025"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f0a0-7769-baaa-5fa9655a8c74","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-f089-74ee-b0ad-65fb0c55e7df","relatedTitle":"act025-a1cc85","title":null,"subtitle":null,"description":"活动条目025","note":null,"banner":{"id":"bound/b025a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b025a.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.504439153Z","updatedAt":"2026-08-28T13:16:41.504439153Z"}
```

## 前置: MENSTRUAL 上线 ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b025b.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","targetId":"01a04883-f090-7c6d-8846-5f06f011fd52","title":"文章条目025a"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f0a8-79b9-962f-f9f3bda3c779","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":0,"online":true,"targetId":"01a04883-f090-7c6d-8846-5f06f011fd52","relatedTitle":"art025a-a1cc85","title":"文章条目025a","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b025b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b025b.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.512585411Z","updatedAt":"2026-08-28T13:16:41.512585411Z"}
```

## 前置: FOLLICULAR 上线 ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b025c.png","online":true,"phase":"FOLLICULAR","type":"ARTICLE","targetId":"01a04883-f098-7373-b587-20bd2699c02d","title":"文章条目025b"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f0b0-7b4d-ac1c-2b2f7b404a7c","phase":"FOLLICULAR","type":"ARTICLE","sortOrder":0,"online":true,"targetId":"01a04883-f098-7373-b587-20bd2699c02d","relatedTitle":"art025b-a1cc85","title":"文章条目025b","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b025c.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b025c.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.520683898Z","updatedAt":"2026-08-28T13:16:41.520683898Z"}
```

## Step 2: GET ?period=MENSTRUAL&type=ARTICLE

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL&type=ARTICLE" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-f0a8-79b9-962f-f9f3bda3c779","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b025b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b025b.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-f090-7c6d-8846-5f06f011fd52","title":"文章条目025a","subtitle":null,"description":null,"note":null}]
```
