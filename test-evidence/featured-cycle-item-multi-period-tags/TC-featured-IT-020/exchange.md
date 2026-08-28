# TC-featured-IT-020 GET /api/app/featured-cycle-items 城市未上架不影响路线类条目 — 请求/响应存证

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

## 前置: 城市 city020

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"city020-a10076","englishName":"city020-a10076","chineseProvince":"测试省","englishProvince":"Test Province","online":false}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-eecb-72aa-8980-1f6a203ef8e8","chineseName":"city020-a10076","englishName":"city020-a10076","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":false,"createdAt":"2026-08-28T13:16:41.035136635Z","updatedAt":"2026-08-28T13:16:41.035136635Z"}
```

## 前置: 大使 amb020

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/amb020.png","name":"amb020-a10076","tags":["向导"],"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-eed2-7a3c-a69e-234b8c4f485d","avatar":{"id":"bound/amb020.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb020.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"name":"amb020-a10076","tags":["向导"],"weight":0,"online":true,"createdAt":"2026-08-28T13:16:41.042611619Z","updatedAt":"2026-08-28T13:16:41.042611619Z"}
```

## 前置: 路线 route020

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"city020-a10076","sortOrder":1,"title":"route020-a10076","ambassadorNote":"大使推荐语","thumbnail":"images/route020-t.png","images":["images/route020-1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a04883-eed2-7a3c-a69e-234b8c4f485d","spots":[{"name":"S1","image":"images/route020-s1.png","introduction":"步道"}]}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-eedb-75bf-81f0-29623ad88529","sortOrder":1,"title":"route020-a10076","cityName":"city020-a10076","ambassadorNote":"大使推荐语","thumbnail":{"id":"bound/route020-t.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route020-t.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"images":[{"id":"bound/route020-1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route020-1.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a04883-eed2-7a3c-a69e-234b8c4f485d","ambassadorName":"amb020-a10076","spots":[{"name":"S1","image":{"id":"bound/route020-s1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route020-s1.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"introduction":"步道"}],"createdAt":"2026-08-28T13:16:41.051330541Z","updatedAt":"2026-08-28T13:16:41.051330541Z"}
```

## 前置: 活动 act020

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act020.png"],"title":"act020-a10076","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-eee3-7cac-aa4f-59a46c37d472","images":[{"id":"bound/act020.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act020.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act020-a10076","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:41.059700033Z","updatedAt":"2026-08-28T13:16:41.059700033Z"}
```

## 前置: OVULATION 上线 ROUTE（其城市下架）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b020a.png","online":true,"phase":"OVULATION","type":"ROUTE","targetId":"01a04883-eedb-75bf-81f0-29623ad88529","title":"路线条目020","subtitle":"副标题","description":"描述"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-eeec-7773-a308-61344526d8aa","phase":"OVULATION","type":"ROUTE","sortOrder":0,"online":true,"targetId":"01a04883-eedb-75bf-81f0-29623ad88529","relatedTitle":"route020-a10076","title":"路线条目020","subtitle":"副标题","description":"描述","note":null,"banner":{"id":"bound/b020a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b020a.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.068390258Z","updatedAt":"2026-08-28T13:16:41.068390258Z"}
```

## 前置: OVULATION 上线 ACTIVITY（活动不关联城市）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b020b.png","online":true,"phase":"OVULATION","type":"ACTIVITY","targetId":"01a04883-eee3-7cac-aa4f-59a46c37d472","description":"活动条目020"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-eef5-786e-8daf-04d9c327c123","phase":"OVULATION","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-eee3-7cac-aa4f-59a46c37d472","relatedTitle":"act020-a10076","title":null,"subtitle":null,"description":"活动条目020","note":null,"banner":{"id":"bound/b020b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b020b.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.077505131Z","updatedAt":"2026-08-28T13:16:41.077505131Z"}
```

## Step 2: GET（城市仍下架）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-eef5-786e-8daf-04d9c327c123","period":["OVULATION"],"type":"ACTIVITY","banner":{"id":"bound/b020b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b020b.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-eee3-7cac-aa4f-59a46c37d472","title":null,"subtitle":null,"description":"活动条目020","note":null},{"id":"01a04883-eeec-7773-a308-61344526d8aa","period":["OVULATION"],"type":"ROUTE","banner":{"id":"bound/b020a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b020a.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-eedb-75bf-81f0-29623ad88529","title":"路线条目020","subtitle":"副标题","description":"描述","note":null}]
```

## Step 3: admin 将该城市上架

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a04883-eecb-72aa-8980-1f6a203ef8e8/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-eecb-72aa-8980-1f6a203ef8e8","chineseName":"city020-a10076","englishName":"city020-a10076","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":true,"createdAt":"2026-08-28T13:16:41.035137Z","updatedAt":"2026-08-28T13:16:41.035137Z"}
```

## Step 3: GET（城市已上架）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-eef5-786e-8daf-04d9c327c123","period":["OVULATION"],"type":"ACTIVITY","banner":{"id":"bound/b020b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b020b.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-eee3-7cac-aa4f-59a46c37d472","title":null,"subtitle":null,"description":"活动条目020","note":null},{"id":"01a04883-eeec-7773-a308-61344526d8aa","period":["OVULATION"],"type":"ROUTE","banner":{"id":"bound/b020a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b020a.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-eedb-75bf-81f0-29623ad88529","title":"路线条目020","subtitle":"副标题","description":"描述","note":null}]
```
