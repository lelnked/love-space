# TC-featured-IT-016 GET /api/app/featured-cycle-items 扁平数组带 period 周期数组且只含上线条目 — 请求/响应存证

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

## 前置: 活动 act016

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act016.png"],"title":"act016-a1c511","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ec2c-7975-8969-60e21f50fd8e","images":[{"id":"bound/act016.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act016.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act016-a1c511","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:40.364532479Z","updatedAt":"2026-08-28T13:16:40.364532479Z"}
```

## 前置: 文章 art016

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art016.png","contentHtml":"<p>正文</p>","title":"art016-a1c511","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ec36-77f2-8124-6d94fa75909f","image":{"id":"bound/art016.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art016.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art016-a1c511","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-28T13:16:40.37446707Z","updatedAt":"2026-08-28T13:16:40.37446707Z"}
```

## 前置: 活动 act016b

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act016b.png"],"title":"act016b-a1c511","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ec3e-7acd-9875-64b3bea7d1b2","images":[{"id":"bound/act016b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act016b.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act016b-a1c511","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:40.38263202Z","updatedAt":"2026-08-28T13:16:40.38263202Z"}
```

## 前置: MENSTRUAL 上线 ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b016a.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-ec2c-7975-8969-60e21f50fd8e","description":"活动条目016"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ec47-7a78-965c-b6a5d87868c9","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-ec2c-7975-8969-60e21f50fd8e","relatedTitle":"act016-a1c511","title":null,"subtitle":null,"description":"活动条目016","note":null,"banner":{"id":"bound/b016a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b016a.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:40.391612215Z","updatedAt":"2026-08-28T13:16:40.391612215Z"}
```

## 前置: OVULATION 上线 ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b016b.png","online":true,"phase":"OVULATION","type":"ARTICLE","targetId":"01a04883-ec36-77f2-8124-6d94fa75909f","title":"文章条目016"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ec51-7107-ba5c-e9aea3a6c814","phase":"OVULATION","type":"ARTICLE","sortOrder":0,"online":true,"targetId":"01a04883-ec36-77f2-8124-6d94fa75909f","relatedTitle":"art016-a1c511","title":"文章条目016","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b016b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b016b.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:40.401036934Z","updatedAt":"2026-08-28T13:16:40.401036934Z"}
```

## 前置: LUTEAL 下线 ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b016c.png","online":false,"phase":"LUTEAL","type":"ACTIVITY","targetId":"01a04883-ec3e-7acd-9875-64b3bea7d1b2","description":"下线条目016"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ec5a-7a1f-a8d8-553091e0f4da","phase":"LUTEAL","type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a04883-ec3e-7acd-9875-64b3bea7d1b2","relatedTitle":"act016b-a1c511","title":null,"subtitle":null,"description":"下线条目016","note":null,"banner":{"id":"bound/b016c.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b016c.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:40.410598952Z","updatedAt":"2026-08-28T13:16:40.410598952Z"}
```

## Step 2: GET 不带参数

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-ec51-7107-ba5c-e9aea3a6c814","period":["OVULATION"],"type":"ARTICLE","banner":{"id":"bound/b016b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b016b.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ec36-77f2-8124-6d94fa75909f","title":"文章条目016","subtitle":null,"description":null,"note":null},{"id":"01a04883-ec47-7a78-965c-b6a5d87868c9","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b016a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b016a.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ec2c-7975-8969-60e21f50fd8e","title":null,"subtitle":null,"description":"活动条目016","note":null}]
```
