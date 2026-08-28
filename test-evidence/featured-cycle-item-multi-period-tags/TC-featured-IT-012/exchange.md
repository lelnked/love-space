# TC-featured-IT-012 PUT /api/admin/featured-cycle-items/{id} 周期与类型创建后不可变 — 请求/响应存证

执行日期: 2026-08-28 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `g8k` 防撞名。清理请求（DELETE）不入存证。

## 前置: 活动 act012

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act012.png"],"title":"act012-g8k","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04880-e34f-7ddb-bae7-590e3cbbc333","images":[{"id":"bound/act012.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act012.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act012-g8k","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:13:21.487827849Z","updatedAt":"2026-08-28T13:13:21.487827849Z"}
```

## 前置: 文章 art012

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art012.png","contentHtml":"<p>正文</p>","title":"art012-g8k","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04880-e358-7f3a-9e73-2a1a93b314ac","image":{"id":"bound/art012.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art012.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art012-g8k","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-28T13:13:21.496909446Z","updatedAt":"2026-08-28T13:13:21.496909446Z"}
```

## Step 1 前置: MENSTRUAL/ACTIVITY 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b012.png","phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04880-e34f-7ddb-bae7-590e3cbbc333","description":"原始描述"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04880-e362-794f-adec-701338c8bea6","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a04880-e34f-7ddb-bae7-590e3cbbc333","relatedTitle":"act012-g8k","title":null,"subtitle":null,"description":"原始描述","note":null,"banner":{"id":"bound/b012.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b012.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:13:21.506545671Z","updatedAt":"2026-08-28T13:13:21.506545671Z"}
```

## Step 2: PUT 传 phase=LUTEAL/type=ARTICLE/title，targetId 按持久化类型给活动 id

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a04880-e362-794f-adec-701338c8bea6" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"LUTEAL","type":"ARTICLE","title":"改名","description":"更新后的描述","targetId":"01a04880-e34f-7ddb-bae7-590e3cbbc333","banner":"images/b012.png"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04880-e362-794f-adec-701338c8bea6","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a04880-e34f-7ddb-bae7-590e3cbbc333","relatedTitle":"act012-g8k","title":null,"subtitle":null,"description":"更新后的描述","note":null,"banner":{"id":"bound/b012.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b012.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:13:21.506546Z","updatedAt":"2026-08-28T13:13:21.506546Z"}
```

## Step 3: GET 详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a04880-e362-794f-adec-701338c8bea6" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04880-e362-794f-adec-701338c8bea6","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a04880-e34f-7ddb-bae7-590e3cbbc333","relatedTitle":"act012-g8k","title":null,"subtitle":null,"description":"更新后的描述","note":null,"banner":{"id":"bound/b012.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b012.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:13:21.506546Z","updatedAt":"2026-08-28T13:13:21.519194Z"}
```

## Step 4: PUT targetId 改传文章 id（type 仍传 ARTICLE）

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a04880-e362-794f-adec-701338c8bea6" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ARTICLE","description":"再更新","targetId":"01a04880-e358-7f3a-9e73-2a1a93b314ac","banner":"images/b012.png"}'
```

实际响应: HTTP 400（Content-Type: application/json）

```json
{"status":400,"error":"Bad Request","message":"关联活动不存在：01a04880-e358-7f3a-9e73-2a1a93b314ac","path":"/api/admin/featured-cycle-items/01a04880-e362-794f-adec-701338c8bea6"}
```
