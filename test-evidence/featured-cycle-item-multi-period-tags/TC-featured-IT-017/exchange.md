# TC-featured-IT-017 GET /api/app/featured-cycle-items 关联实体不可见时条目不下发 — 请求/响应存证

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

## 前置: 活动 act017

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act017.png"],"title":"act017-a1d7c3","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ecb3-76a3-8c09-71453e0c395e","images":[{"id":"bound/act017.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act017.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act017-a1d7c3","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:40.499385273Z","updatedAt":"2026-08-28T13:16:40.499385273Z"}
```

## 前置: 文章 art017

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art017.png","contentHtml":"<p>正文</p>","title":"art017-a1d7c3","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ecbb-7cc2-bf93-3ab1ebded596","image":{"id":"bound/art017.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art017.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art017-a1d7c3","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-28T13:16:40.507756019Z","updatedAt":"2026-08-28T13:16:40.507756019Z"}
```

## 前置: MENSTRUAL 上线 ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b017a.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-ecb3-76a3-8c09-71453e0c395e","description":"活动条目017"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ecc4-77be-aba5-9854394db0a7","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-ecb3-76a3-8c09-71453e0c395e","relatedTitle":"act017-a1d7c3","title":null,"subtitle":null,"description":"活动条目017","note":null,"banner":{"id":"bound/b017a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b017a.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:40.516454434Z","updatedAt":"2026-08-28T13:16:40.516454434Z"}
```

## 前置: MENSTRUAL 上线 ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b017b.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","targetId":"01a04883-ecbb-7cc2-bf93-3ab1ebded596","title":"文章条目017"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ecce-7232-bd5a-ff0d8b30bfa6","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":0,"online":true,"targetId":"01a04883-ecbb-7cc2-bf93-3ab1ebded596","relatedTitle":"art017-a1d7c3","title":"文章条目017","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b017b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b017b.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:40.526082479Z","updatedAt":"2026-08-28T13:16:40.526082479Z"}
```

## Step 1: GET 确认两条均在

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-ecce-7232-bd5a-ff0d8b30bfa6","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b017b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b017b.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ecbb-7cc2-bf93-3ab1ebded596","title":"文章条目017","subtitle":null,"description":null,"note":null},{"id":"01a04883-ecc4-77be-aba5-9854394db0a7","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b017a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b017a.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ecb3-76a3-8c09-71453e0c395e","title":null,"subtitle":null,"description":"活动条目017","note":null}]
```

## Step 2: admin 将该活动下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a04883-ecb3-76a3-8c09-71453e0c395e/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ecb3-76a3-8c09-71453e0c395e","images":[{"id":"bound/act017.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act017.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act017-a1d7c3","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":false,"createdAt":"2026-08-28T13:16:40.499385Z","updatedAt":"2026-08-28T13:16:40.499385Z"}
```

## Step 2: GET

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-ecce-7232-bd5a-ff0d8b30bfa6","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b017b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b017b.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ecbb-7cc2-bf93-3ab1ebded596","title":"文章条目017","subtitle":null,"description":null,"note":null}]
```

## Step 3: 恢复活动上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a04883-ecb3-76a3-8c09-71453e0c395e/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ecb3-76a3-8c09-71453e0c395e","images":[{"id":"bound/act017.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act017.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act017-a1d7c3","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:40.499385Z","updatedAt":"2026-08-28T13:16:40.563302Z"}
```

## Step 3: GET（活动不关联城市，无城市下架步骤）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-ecce-7232-bd5a-ff0d8b30bfa6","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b017b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b017b.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ecbb-7cc2-bf93-3ab1ebded596","title":"文章条目017","subtitle":null,"description":null,"note":null},{"id":"01a04883-ecc4-77be-aba5-9854394db0a7","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b017a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b017a.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ecb3-76a3-8c09-71453e0c395e","title":null,"subtitle":null,"description":"活动条目017","note":null}]
```

## Step 4: 将该文章下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a04883-ecbb-7cc2-bf93-3ab1ebded596/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ecbb-7cc2-bf93-3ab1ebded596","image":{"id":"bound/art017.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art017.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art017-a1d7c3","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":false,"createdAt":"2026-08-28T13:16:40.507756Z","updatedAt":"2026-08-28T13:16:40.507756Z"}
```

## Step 4: GET

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-ecc4-77be-aba5-9854394db0a7","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b017a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b017a.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ecb3-76a3-8c09-71453e0c395e","title":null,"subtitle":null,"description":"活动条目017","note":null}]
```

## Step 5: 恢复文章上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a04883-ecbb-7cc2-bf93-3ab1ebded596/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ecbb-7cc2-bf93-3ab1ebded596","image":{"id":"bound/art017.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art017.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art017-a1d7c3","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-28T13:16:40.507756Z","updatedAt":"2026-08-28T13:16:40.618118Z"}
```

## Step 5: admin 删除该文章

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/articles/01a04883-ecbb-7cc2-bf93-3ab1ebded596" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## Step 5: GET

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-ecc4-77be-aba5-9854394db0a7","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b017a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b017a.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ecb3-76a3-8c09-71453e0c395e","title":null,"subtitle":null,"description":"活动条目017","note":null}]
```
