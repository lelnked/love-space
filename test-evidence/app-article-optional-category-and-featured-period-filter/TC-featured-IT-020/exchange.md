# TC-featured-IT-020 GET /api/app/featured-cycle-items 城市未上架不影响路线类条目 — 请求/响应存证

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

## 前置: 下架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"city020-3u50","englishName":"city020-3u50","chineseProvince":"测试省","englishProvince":"Test Province","online":false}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-48cb-70cc-9351-ee3e6a54c40c","chineseName":"city020-3u50","englishName":"city020-3u50","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":false,"createdAt":"2026-08-25T12:55:18.47500006Z","updatedAt":"2026-08-25T12:55:18.47500006Z"}
```

## 前置: 上线大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/amb020.png","name":"amb020-3u50","tags":["户外"],"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-48d6-7f5e-9be5-6e6b7ad54aee","avatar":{"id":"bound/amb020.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb020.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=bzsk3%2FikSdcZqS28PKETTuIOPio%3D"},"name":"amb020-3u50","tags":["户外"],"weight":0,"online":true,"createdAt":"2026-08-25T12:55:18.486914083Z","updatedAt":"2026-08-25T12:55:18.486914083Z"}
```

## 前置: 下架城市下的路线（cityName=该下架城市名）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"city020-3u50","title":"route020-3u50","thumbnail":"images/route020.png","images":["images/route020.png"],"ambassadorId":"01a038fd-48d6-7f5e-9be5-6e6b7ad54aee","ambassadorNote":"大使说","travelTime":"3天","season":"春","travelStatus":"轻松"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-48e3-77d9-b478-0a2b143fa278","sortOrder":0,"title":"route020-3u50","cityName":"city020-3u50","ambassadorNote":"大使说","thumbnail":{"id":"bound/route020.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route020.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6426pBCIjbakjr8LG1FB900fXFU%3D"},"images":[{"id":"bound/route020.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route020.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6426pBCIjbakjr8LG1FB900fXFU%3D"}],"travelTime":"3天","season":"春","travelStatus":"轻松","ambassadorId":"01a038fd-48d6-7f5e-9be5-6e6b7ad54aee","ambassadorName":"amb020-3u50","spots":[],"createdAt":"2026-08-25T12:55:18.499455677Z","updatedAt":"2026-08-25T12:55:18.499455677Z"}
```

## 前置: 上线活动（活动已无城市关联，无法挂到该下架城市）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act020.png"],"title":"act020-3u50","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-48ee-777e-9b66-09ab83572b10","images":[{"id":"bound/act020.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act020.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=surgaGVkTpWMxVhjh0oZEcYHvCI%3D"}],"title":"act020-3u50","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-25T12:55:18.510437911Z","updatedAt":"2026-08-25T12:55:18.510437911Z"}
```

## 前置: OVULATION ROUTE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-OVULATION-ROUTE.png","online":true,"phase":"OVULATION","type":"ROUTE","routeId":"01a038fd-48e3-77d9-b478-0a2b143fa278","title":"路线条目020","subtitle":"副","description":"说明"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-48f9-7578-a037-79c0e0979a00","phase":"OVULATION","type":"ROUTE","sortOrder":0,"online":true,"activityId":null,"routeId":"01a038fd-48e3-77d9-b478-0a2b143fa278","articleId":null,"relatedTitle":"route020-3u50","title":"路线条目020","subtitle":"副","description":"说明","note":null,"banner":{"id":"bound/banner-OVULATION-ROUTE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-OVULATION-ROUTE.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=jPkmeZK1QmTKt2L0ALJ2%2F61xiyc%3D"},"createdAt":"2026-08-25T12:55:18.52130351Z","updatedAt":"2026-08-25T12:55:18.52130351Z"}
```

## 前置: OVULATION ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-OVULATION-ACTIVITY.png","online":true,"phase":"OVULATION","type":"ACTIVITY","activityId":"01a038fd-48ee-777e-9b66-09ab83572b10","description":"活动条目020"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4904-786f-9aed-71cfec4b83ea","phase":"OVULATION","type":"ACTIVITY","sortOrder":0,"online":true,"activityId":"01a038fd-48ee-777e-9b66-09ab83572b10","routeId":null,"articleId":null,"relatedTitle":"act020-3u50","title":null,"subtitle":null,"description":"活动条目020","note":null,"banner":{"id":"bound/banner-OVULATION-ACTIVITY.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-OVULATION-ACTIVITY.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=fSN7QBw%2FmEueNE1H%2BCCL4VTpmQY%3D"},"createdAt":"2026-08-25T12:55:18.532498957Z","updatedAt":"2026-08-25T12:55:18.532498957Z"}
```

## Step 2: 城市下架时 GET

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-4904-786f-9aed-71cfec4b83ea","period":"OVULATION","type":"ACTIVITY","banner":{"id":"bound/banner-OVULATION-ACTIVITY.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-OVULATION-ACTIVITY.png?Expires=1787664318&OSSAccessKeyId=placeholder&Signature=AgWxPlyQKGO6MKnxtOWDXF%2BR9c0%3D"},"activityId":"01a038fd-48ee-777e-9b66-09ab83572b10","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目020","note":null},{"id":"01a038fd-48f9-7578-a037-79c0e0979a00","period":"OVULATION","type":"ROUTE","banner":{"id":"bound/banner-OVULATION-ROUTE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-OVULATION-ROUTE.png?Expires=1787664318&OSSAccessKeyId=placeholder&Signature=H1M0sMrdQpDWVsQCg7UM%2BfcDOf0%3D"},"activityId":null,"routeId":"01a038fd-48e3-77d9-b478-0a2b143fa278","articleId":null,"title":"路线条目020","subtitle":"副","description":"说明","note":null}]
```

## Step 3: 城市上架

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a038fd-48cb-70cc-9351-ee3e6a54c40c/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-48cb-70cc-9351-ee3e6a54c40c","chineseName":"city020-3u50","englishName":"city020-3u50","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":true,"createdAt":"2026-08-25T12:55:18.475Z","updatedAt":"2026-08-25T12:55:18.475Z"}
```

## Step 3: 城市上架后 GET

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-4904-786f-9aed-71cfec4b83ea","period":"OVULATION","type":"ACTIVITY","banner":{"id":"bound/banner-OVULATION-ACTIVITY.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-OVULATION-ACTIVITY.png?Expires=1787664318&OSSAccessKeyId=placeholder&Signature=AgWxPlyQKGO6MKnxtOWDXF%2BR9c0%3D"},"activityId":"01a038fd-48ee-777e-9b66-09ab83572b10","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目020","note":null},{"id":"01a038fd-48f9-7578-a037-79c0e0979a00","period":"OVULATION","type":"ROUTE","banner":{"id":"bound/banner-OVULATION-ROUTE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-OVULATION-ROUTE.png?Expires=1787664318&OSSAccessKeyId=placeholder&Signature=H1M0sMrdQpDWVsQCg7UM%2BfcDOf0%3D"},"activityId":null,"routeId":"01a038fd-48e3-77d9-b478-0a2b143fa278","articleId":null,"title":"路线条目020","subtitle":"副","description":"说明","note":null}]
```

## 清理: DELETE 周期条目 01a038fd-48f9-7578-a037-79c0e0979a00

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-48f9-7578-a037-79c0e0979a00" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-4904-786f-9aed-71cfec4b83ea

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4904-786f-9aed-71cfec4b83ea" -H "Authorization: Bearer $TOKEN"
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

清理 DELETE 状态码: 200, 200；清理后 app 端返回 []
