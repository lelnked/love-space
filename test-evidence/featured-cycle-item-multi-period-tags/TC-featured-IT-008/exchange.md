# TC-featured-IT-008 POST /api/admin/featured-cycle-items 创建路线类周期推荐 — 请求/响应存证

执行日期: 2026-08-28 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `b3` 防撞名。清理请求（DELETE）不入存证。

## 前置: 城市 city008

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"city008-b3dacc","englishName":"city008-b3dacc","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04882-0b37-7d12-be87-4d88b31593f8","chineseName":"city008-b3dacc","englishName":"city008-b3dacc","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":true,"createdAt":"2026-08-28T13:14:37.239759856Z","updatedAt":"2026-08-28T13:14:37.239759856Z"}
```

## 前置: 大使 amb008

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/amb008.png","name":"amb008-b3dacc","tags":["向导"],"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04882-0b41-7452-acd6-0f3578363cb1","avatar":{"id":"bound/amb008.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb008.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"name":"amb008-b3dacc","tags":["向导"],"weight":0,"online":true,"createdAt":"2026-08-28T13:14:37.249240541Z","updatedAt":"2026-08-28T13:14:37.249240541Z"}
```

## 前置: 路线 route008

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"city008-b3dacc","sortOrder":1,"title":"路线主标题008-b3dacc","ambassadorNote":"大使推荐语","thumbnail":"images/route008-t.png","images":["images/route008-1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a04882-0b41-7452-acd6-0f3578363cb1","spots":[{"name":"S1","image":"images/route008-s1.png","introduction":"步道"}]}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04882-0b4e-78d4-a5d5-db7bd9d9ed43","sortOrder":1,"title":"路线主标题008-b3dacc","cityName":"city008-b3dacc","ambassadorNote":"大使推荐语","thumbnail":{"id":"bound/route008-t.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route008-t.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"images":[{"id":"bound/route008-1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route008-1.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a04882-0b41-7452-acd6-0f3578363cb1","ambassadorName":"amb008-b3dacc","spots":[{"name":"S1","image":{"id":"bound/route008-s1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route008-s1.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"introduction":"步道"}],"createdAt":"2026-08-28T13:14:37.261177757Z","updatedAt":"2026-08-28T13:14:37.261177757Z"}
```

## Step 2: POST 路线类周期推荐

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b008.png","phase":"OVULATION","type":"ROUTE","targetId":"01a04882-0b4e-78d4-a5d5-db7bd9d9ed43","title":"排卵期就该出门","subtitle":"三天两夜","description":"体力最好的几天"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04882-0b5d-79b6-9d23-9f2f732b151f","phase":"OVULATION","type":"ROUTE","sortOrder":0,"online":false,"targetId":"01a04882-0b4e-78d4-a5d5-db7bd9d9ed43","relatedTitle":"路线主标题008-b3dacc","title":"排卵期就该出门","subtitle":"三天两夜","description":"体力最好的几天","note":null,"banner":{"id":"bound/b008.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b008.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:14:37.277578257Z","updatedAt":"2026-08-28T13:14:37.277578257Z"}
```

## Step 3: GET 详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a04882-0b5d-79b6-9d23-9f2f732b151f" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04882-0b5d-79b6-9d23-9f2f732b151f","phase":"OVULATION","type":"ROUTE","sortOrder":0,"online":false,"targetId":"01a04882-0b4e-78d4-a5d5-db7bd9d9ed43","relatedTitle":"路线主标题008-b3dacc","title":"排卵期就该出门","subtitle":"三天两夜","description":"体力最好的几天","note":null,"banner":{"id":"bound/b008.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b008.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:14:37.277578Z","updatedAt":"2026-08-28T13:14:37.277578Z"}
```
