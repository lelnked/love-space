# TC-featured-IT-021 GET /api/app/featured-cycle-items?type= 按内容类型过滤 — 请求/响应存证

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

## 前置: 活动 act021

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act021.png"],"title":"act021-a1f640","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ef61-707d-90d5-2c512b8286f0","images":[{"id":"bound/act021.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act021.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act021-a1f640","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:41.185005976Z","updatedAt":"2026-08-28T13:16:41.185005976Z"}
```

## 前置: 文章 art021

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art021.png","contentHtml":"<p>正文</p>","title":"art021-a1f640","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ef68-7fbf-af22-6d5df3059b1b","image":{"id":"bound/art021.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art021.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art021-a1f640","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-28T13:16:41.192958489Z","updatedAt":"2026-08-28T13:16:41.192958489Z"}
```

## 前置: 城市 city021

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"city021-a1f640","englishName":"city021-a1f640","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ef71-7027-b2d0-30592288bf74","chineseName":"city021-a1f640","englishName":"city021-a1f640","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":true,"createdAt":"2026-08-28T13:16:41.200985727Z","updatedAt":"2026-08-28T13:16:41.200985727Z"}
```

## 前置: 大使 amb021

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/amb021.png","name":"amb021-a1f640","tags":["向导"],"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ef78-7001-b31d-5f432aee11f7","avatar":{"id":"bound/amb021.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb021.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"name":"amb021-a1f640","tags":["向导"],"weight":0,"online":true,"createdAt":"2026-08-28T13:16:41.207977143Z","updatedAt":"2026-08-28T13:16:41.207977143Z"}
```

## 前置: 路线 route021

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"city021-a1f640","sortOrder":1,"title":"route021-a1f640","ambassadorNote":"大使推荐语","thumbnail":"images/route021-t.png","images":["images/route021-1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a04883-ef78-7001-b31d-5f432aee11f7","spots":[{"name":"S1","image":"images/route021-s1.png","introduction":"步道"}]}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ef7f-7f67-9b0f-61e7843e6c8d","sortOrder":1,"title":"route021-a1f640","cityName":"city021-a1f640","ambassadorNote":"大使推荐语","thumbnail":{"id":"bound/route021-t.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route021-t.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"images":[{"id":"bound/route021-1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route021-1.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a04883-ef78-7001-b31d-5f432aee11f7","ambassadorName":"amb021-a1f640","spots":[{"name":"S1","image":{"id":"bound/route021-s1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route021-s1.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"introduction":"步道"}],"createdAt":"2026-08-28T13:16:41.215933456Z","updatedAt":"2026-08-28T13:16:41.215933456Z"}
```

## 前置: MENSTRUAL 上线 ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b021a.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-ef61-707d-90d5-2c512b8286f0","description":"活动条目021"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ef88-7a3a-9d7c-2fce83304473","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-ef61-707d-90d5-2c512b8286f0","relatedTitle":"act021-a1f640","title":null,"subtitle":null,"description":"活动条目021","note":null,"banner":{"id":"bound/b021a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b021a.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.224616002Z","updatedAt":"2026-08-28T13:16:41.224616002Z"}
```

## 前置: MENSTRUAL 上线 ROUTE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b021b.png","online":true,"phase":"MENSTRUAL","type":"ROUTE","targetId":"01a04883-ef7f-7f67-9b0f-61e7843e6c8d","title":"路线条目021","subtitle":"副标题","description":"描述"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ef90-7c67-8a27-ae8d6379b466","phase":"MENSTRUAL","type":"ROUTE","sortOrder":0,"online":true,"targetId":"01a04883-ef7f-7f67-9b0f-61e7843e6c8d","relatedTitle":"route021-a1f640","title":"路线条目021","subtitle":"副标题","description":"描述","note":null,"banner":{"id":"bound/b021b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b021b.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.232753202Z","updatedAt":"2026-08-28T13:16:41.232753202Z"}
```

## 前置: MENSTRUAL 上线 ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b021c.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","targetId":"01a04883-ef68-7fbf-af22-6d5df3059b1b","title":"文章条目021"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ef99-7792-931e-b452da856911","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":0,"online":true,"targetId":"01a04883-ef68-7fbf-af22-6d5df3059b1b","relatedTitle":"art021-a1f640","title":"文章条目021","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b021c.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b021c.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.241451288Z","updatedAt":"2026-08-28T13:16:41.241451288Z"}
```

## Step 2: GET ?type=ARTICLE

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?type=ARTICLE" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-ef99-7792-931e-b452da856911","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b021c.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b021c.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ef68-7fbf-af22-6d5df3059b1b","title":"文章条目021","subtitle":null,"description":null,"note":null}]
```

## Step 3: GET 不带参数

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-ef99-7792-931e-b452da856911","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b021c.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b021c.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ef68-7fbf-af22-6d5df3059b1b","title":"文章条目021","subtitle":null,"description":null,"note":null},{"id":"01a04883-ef90-7c67-8a27-ae8d6379b466","period":["MENSTRUAL"],"type":"ROUTE","banner":{"id":"bound/b021b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b021b.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ef7f-7f67-9b0f-61e7843e6c8d","title":"路线条目021","subtitle":"副标题","description":"描述","note":null},{"id":"01a04883-ef88-7a3a-9d7c-2fce83304473","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b021a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b021a.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ef61-707d-90d5-2c512b8286f0","title":null,"subtitle":null,"description":"活动条目021","note":null}]
```
