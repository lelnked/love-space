# TC-featured-IT-010 POST /api/admin/featured-cycle-items 类型必填项缺失被拒绝 — 请求/响应存证

执行日期: 2026-08-28 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `b3` 防撞名。清理请求（DELETE）不入存证。

## 前置: 活动 act010

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act010.png"],"title":"act010-b36bf1","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04882-0b94-747f-8d05-a5b9698fe4a4","images":[{"id":"bound/act010.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act010.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act010-b36bf1","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:14:37.33225237Z","updatedAt":"2026-08-28T13:14:37.33225237Z"}
```

## 前置: 文章 art010

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art010.png","contentHtml":"<p>正文</p>","title":"art010-b36bf1","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04882-0b9c-75b1-b119-38963f501bc5","image":{"id":"bound/art010.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art010.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art010-b36bf1","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-28T13:14:37.340326814Z","updatedAt":"2026-08-28T13:14:37.340326814Z"}
```

## 前置: 城市 city010

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"city010-b36bf1","englishName":"city010-b36bf1","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04882-0ba5-7ede-b24c-784953cbd1c8","chineseName":"city010-b36bf1","englishName":"city010-b36bf1","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":true,"createdAt":"2026-08-28T13:14:37.349882318Z","updatedAt":"2026-08-28T13:14:37.349882318Z"}
```

## 前置: 大使 amb010

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/amb010.png","name":"amb010-b36bf1","tags":["向导"],"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04882-0bad-79f9-93bf-f928462df472","avatar":{"id":"bound/amb010.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb010.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"name":"amb010-b36bf1","tags":["向导"],"weight":0,"online":true,"createdAt":"2026-08-28T13:14:37.357564896Z","updatedAt":"2026-08-28T13:14:37.357564896Z"}
```

## 前置: 路线 route010

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"city010-b36bf1","sortOrder":1,"title":"route010-b36bf1","ambassadorNote":"大使推荐语","thumbnail":"images/route010-t.png","images":["images/route010-1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a04882-0bad-79f9-93bf-f928462df472","spots":[{"name":"S1","image":"images/route010-s1.png","introduction":"步道"}]}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04882-0bb6-7d3b-ad3c-663022ad6c66","sortOrder":1,"title":"route010-b36bf1","cityName":"city010-b36bf1","ambassadorNote":"大使推荐语","thumbnail":{"id":"bound/route010-t.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route010-t.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"images":[{"id":"bound/route010-1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route010-1.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a04882-0bad-79f9-93bf-f928462df472","ambassadorName":"amb010-b36bf1","spots":[{"name":"S1","image":{"id":"bound/route010-s1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route010-s1.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"introduction":"步道"}],"createdAt":"2026-08-28T13:14:37.366788823Z","updatedAt":"2026-08-28T13:14:37.366788823Z"}
```

## 前置: GET page 基线计数

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=1&size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"content":[],"page":1,"size":30,"totalElements":0,"totalPages":0}
```

## Step 1: ROUTE 缺 subtitle

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"OVULATION","type":"ROUTE","targetId":"01a04882-0bb6-7d3b-ad3c-663022ad6c66","title":"t","description":"d","banner":"images/b.png"}'
```

实际响应: HTTP 400（Content-Type: application/json）

```json
{"status":400,"error":"Bad Request","message":"副标题不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 2: ACTIVITY 缺 description

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04882-0b94-747f-8d05-a5b9698fe4a4","banner":"images/b.png"}'
```

实际响应: HTTP 400（Content-Type: application/json）

```json
{"status":400,"error":"Bad Request","message":"推荐说明不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 3: ARTICLE 缺 banner

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"LUTEAL","type":"ARTICLE","targetId":"01a04882-0b9c-75b1-b119-38963f501bc5","title":"t"}'
```

实际响应: HTTP 400（Content-Type: application/json）

```json
{"status":400,"error":"Bad Request","message":"banner 图片不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 4: 缺 phase

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"type":"ACTIVITY","targetId":"01a04882-0b94-747f-8d05-a5b9698fe4a4","description":"d","banner":"images/b.png"}'
```

实际响应: HTTP 400（Content-Type: application/json）

```json
{"status":400,"error":"Bad Request","message":"所属周期不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 5: 缺 type

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","targetId":"01a04882-0b94-747f-8d05-a5b9698fe4a4","description":"d","banner":"images/b.png"}'
```

实际响应: HTTP 400（Content-Type: application/json）

```json
{"status":400,"error":"Bad Request","message":"内容类型不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 6: GET page 复核计数

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=1&size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"content":[],"page":1,"size":30,"totalElements":0,"totalPages":0}
```
