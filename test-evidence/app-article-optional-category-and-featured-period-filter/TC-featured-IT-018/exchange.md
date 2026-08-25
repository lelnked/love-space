# TC-featured-IT-018 GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 直接接受的占位 key（该实例不校验 OSS 对象存在），fixture 名带本轮后缀 `3u50` 防撞名。

## 前置核对: app 周期推荐当前为空（保证"恰含"断言不受历史数据污染）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[]
```

## 前置: 上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"city018-3u50","englishName":"city018-3u50","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-46d2-7734-b731-5e5815a13242","chineseName":"city018-3u50","englishName":"city018-3u50","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":true,"createdAt":"2026-08-25T12:55:17.970402376Z","updatedAt":"2026-08-25T12:55:17.970402376Z"}
```

## 前置: 上线大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/amb018.png","name":"amb018-3u50","tags":["户外"],"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-46dc-7b72-8905-b52cbf4bfc76","avatar":{"id":"bound/amb018.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb018.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=8ZxTz%2FSEQfy%2FIa8w%2Fw958YvtK0M%3D"},"name":"amb018-3u50","tags":["户外"],"weight":0,"online":true,"createdAt":"2026-08-25T12:55:17.980675866Z","updatedAt":"2026-08-25T12:55:17.980675866Z"}
```

## 前置: 路线（cityName=上架城市名）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"city018-3u50","title":"route018-3u50","thumbnail":"images/route018.png","images":["images/route018.png"],"ambassadorId":"01a038fd-46dc-7b72-8905-b52cbf4bfc76","ambassadorNote":"大使说","travelTime":"3天","season":"春","travelStatus":"轻松"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-46eb-7563-972d-68dcae666fd8","sortOrder":0,"title":"route018-3u50","cityName":"city018-3u50","ambassadorNote":"大使说","thumbnail":{"id":"bound/route018.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route018.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=PzlQOFeUmZE4%2BIUjZ14qbnH7OXA%3D"},"images":[{"id":"bound/route018.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route018.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=PzlQOFeUmZE4%2BIUjZ14qbnH7OXA%3D"}],"travelTime":"3天","season":"春","travelStatus":"轻松","ambassadorId":"01a038fd-46dc-7b72-8905-b52cbf4bfc76","ambassadorName":"amb018-3u50","spots":[],"createdAt":"2026-08-25T12:55:17.994059503Z","updatedAt":"2026-08-25T12:55:17.994059503Z"}
```

## 前置: OVULATION ROUTE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-OVULATION-ROUTE.png","online":true,"phase":"OVULATION","type":"ROUTE","routeId":"01a038fd-46eb-7563-972d-68dcae666fd8","title":"路线条目018","subtitle":"副","description":"说明"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4700-7dc1-b9d2-b3457d8afd71","phase":"OVULATION","type":"ROUTE","sortOrder":0,"online":true,"activityId":null,"routeId":"01a038fd-46eb-7563-972d-68dcae666fd8","articleId":null,"relatedTitle":"route018-3u50","title":"路线条目018","subtitle":"副","description":"说明","note":null,"banner":{"id":"bound/banner-OVULATION-ROUTE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-OVULATION-ROUTE.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=jPkmeZK1QmTKt2L0ALJ2%2F61xiyc%3D"},"createdAt":"2026-08-25T12:55:18.016815411Z","updatedAt":"2026-08-25T12:55:18.016815411Z"}
```

## Step 2: 初始

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-4700-7dc1-b9d2-b3457d8afd71","period":"OVULATION","type":"ROUTE","banner":{"id":"bound/banner-OVULATION-ROUTE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-OVULATION-ROUTE.png?Expires=1787664318&OSSAccessKeyId=placeholder&Signature=H1M0sMrdQpDWVsQCg7UM%2BfcDOf0%3D"},"activityId":null,"routeId":"01a038fd-46eb-7563-972d-68dcae666fd8","articleId":null,"title":"路线条目018","subtitle":"副","description":"说明","note":null}]
```

## Step 3: 大使下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a038fd-46dc-7b72-8905-b52cbf4bfc76/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-46dc-7b72-8905-b52cbf4bfc76","avatar":{"id":"bound/amb018.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb018.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=R3Gg5aAUtNQv6pdsfskp9HWXhFw%3D"},"name":"amb018-3u50","tags":["户外"],"weight":0,"online":false,"createdAt":"2026-08-25T12:55:17.980676Z","updatedAt":"2026-08-25T12:55:17.980676Z"}
```

## Step 3: 大使下线后

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[]
```

## Step 4: 大使恢复上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a038fd-46dc-7b72-8905-b52cbf4bfc76/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-46dc-7b72-8905-b52cbf4bfc76","avatar":{"id":"bound/amb018.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb018.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=R3Gg5aAUtNQv6pdsfskp9HWXhFw%3D"},"name":"amb018-3u50","tags":["户外"],"weight":0,"online":true,"createdAt":"2026-08-25T12:55:17.980676Z","updatedAt":"2026-08-25T12:55:18.049997Z"}
```

## Step 4: 大使恢复后

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-4700-7dc1-b9d2-b3457d8afd71","period":"OVULATION","type":"ROUTE","banner":{"id":"bound/banner-OVULATION-ROUTE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-OVULATION-ROUTE.png?Expires=1787664318&OSSAccessKeyId=placeholder&Signature=H1M0sMrdQpDWVsQCg7UM%2BfcDOf0%3D"},"activityId":null,"routeId":"01a038fd-46eb-7563-972d-68dcae666fd8","articleId":null,"title":"路线条目018","subtitle":"副","description":"说明","note":null}]
```

## 清理: DELETE 周期条目 01a038fd-4700-7dc1-b9d2-b3457d8afd71

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4700-7dc1-b9d2-b3457d8afd71" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理后核对 app 周期推荐为空

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[]
```

清理 DELETE 状态码: 200；清理后 app 端返回 []
