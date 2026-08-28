# TC-featured-IT-018 GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目 — 请求/响应存证

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

## 前置: 城市 city018

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"city018-a17523","englishName":"city018-a17523","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ed83-708d-bd70-27ffcdc587a7","chineseName":"city018-a17523","englishName":"city018-a17523","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":true,"createdAt":"2026-08-28T13:16:40.707004038Z","updatedAt":"2026-08-28T13:16:40.707004038Z"}
```

## 前置: 大使 amb018

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/amb018.png","name":"amb018-a17523","tags":["向导"],"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ed8a-7a12-bf48-4ee1de295e65","avatar":{"id":"bound/amb018.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb018.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"name":"amb018-a17523","tags":["向导"],"weight":0,"online":true,"createdAt":"2026-08-28T13:16:40.714600147Z","updatedAt":"2026-08-28T13:16:40.714600147Z"}
```

## 前置: 路线 route018

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"city018-a17523","sortOrder":1,"title":"route018-a17523","ambassadorNote":"大使推荐语","thumbnail":"images/route018-t.png","images":["images/route018-1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a04883-ed8a-7a12-bf48-4ee1de295e65","spots":[{"name":"S1","image":"images/route018-s1.png","introduction":"步道"}]}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ed93-7d0f-8812-71645cc0072c","sortOrder":1,"title":"route018-a17523","cityName":"city018-a17523","ambassadorNote":"大使推荐语","thumbnail":{"id":"bound/route018-t.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route018-t.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"images":[{"id":"bound/route018-1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route018-1.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a04883-ed8a-7a12-bf48-4ee1de295e65","ambassadorName":"amb018-a17523","spots":[{"name":"S1","image":{"id":"bound/route018-s1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route018-s1.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"introduction":"步道"}],"createdAt":"2026-08-28T13:16:40.723787394Z","updatedAt":"2026-08-28T13:16:40.723787394Z"}
```

## 前置: OVULATION 上线 ROUTE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b018.png","online":true,"phase":"OVULATION","type":"ROUTE","targetId":"01a04883-ed93-7d0f-8812-71645cc0072c","title":"路线条目018","subtitle":"副标题","description":"描述"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ed9d-714a-a6d0-a5a6569087b5","phase":"OVULATION","type":"ROUTE","sortOrder":0,"online":true,"targetId":"01a04883-ed93-7d0f-8812-71645cc0072c","relatedTitle":"route018-a17523","title":"路线条目018","subtitle":"副标题","description":"描述","note":null,"banner":{"id":"bound/b018.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b018.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:40.733051878Z","updatedAt":"2026-08-28T13:16:40.733051878Z"}
```

## Step 2: GET

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-ed9d-714a-a6d0-a5a6569087b5","period":["OVULATION"],"type":"ROUTE","banner":{"id":"bound/b018.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b018.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ed93-7d0f-8812-71645cc0072c","title":"路线条目018","subtitle":"副标题","description":"描述","note":null}]
```

## Step 3: admin 将该大使下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a04883-ed8a-7a12-bf48-4ee1de295e65/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ed8a-7a12-bf48-4ee1de295e65","avatar":{"id":"bound/amb018.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb018.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"name":"amb018-a17523","tags":["向导"],"weight":0,"online":false,"createdAt":"2026-08-28T13:16:40.7146Z","updatedAt":"2026-08-28T13:16:40.7146Z"}
```

## Step 3: GET

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[]
```

## Step 4: 恢复大使上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a04883-ed8a-7a12-bf48-4ee1de295e65/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ed8a-7a12-bf48-4ee1de295e65","avatar":{"id":"bound/amb018.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb018.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"name":"amb018-a17523","tags":["向导"],"weight":0,"online":true,"createdAt":"2026-08-28T13:16:40.7146Z","updatedAt":"2026-08-28T13:16:40.758876Z"}
```

## Step 4: GET

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-ed9d-714a-a6d0-a5a6569087b5","period":["OVULATION"],"type":"ROUTE","banner":{"id":"bound/b018.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b018.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ed93-7d0f-8812-71645cc0072c","title":"路线条目018","subtitle":"副标题","description":"描述","note":null}]
```
