# TC-featured-IT-026 GET /api/app/featured-cycle-items?period= 周期过滤后无条目返回空数组 — 请求/响应存证

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

## 前置: 上线活动

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act026.png"],"title":"act026-3u50","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4b9e-7d3d-8bf0-e4dc3301ac28","images":[{"id":"bound/act026.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act026.png?Expires=1787664319&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=YsGIbmoMt%2B%2F3Pf9W8WYd%2FqbsOYs%3D"}],"title":"act026-3u50","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-25T12:55:19.198773255Z","updatedAt":"2026-08-25T12:55:19.198773255Z"}
```

## 前置: MENSTRUAL ACTIVITY（库中唯一条目）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ACTIVITY.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a038fd-4b9e-7d3d-8bf0-e4dc3301ac28","description":"活动条目026"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4baa-773a-9c2c-06127998073b","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"activityId":"01a038fd-4b9e-7d3d-8bf0-e4dc3301ac28","routeId":null,"articleId":null,"relatedTitle":"act026-3u50","title":null,"subtitle":null,"description":"活动条目026","note":null,"banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664319&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=jlKlVsr98x92FOKtEw1qeI14mQo%3D"},"createdAt":"2026-08-25T12:55:19.210425292Z","updatedAt":"2026-08-25T12:55:19.210425292Z"}
```

## Step 2: GET ?period=LUTEAL

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=LUTEAL" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[]
```

## 清理: DELETE 周期条目 01a038fd-4baa-773a-9c2c-06127998073b

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4baa-773a-9c2c-06127998073b" -H "Authorization: Bearer $TOKEN"
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
