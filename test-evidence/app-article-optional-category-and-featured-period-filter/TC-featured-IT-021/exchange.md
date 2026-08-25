# TC-featured-IT-021 GET /api/app/featured-cycle-items?type= 按内容类型过滤 — 请求/响应存证

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
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"city021-3u50","englishName":"city021-3u50","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4976-749e-9152-9bd484116ff8","chineseName":"city021-3u50","englishName":"city021-3u50","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":true,"createdAt":"2026-08-25T12:55:18.646235596Z","updatedAt":"2026-08-25T12:55:18.646235596Z"}
```

## 前置: 上线活动

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act021.png"],"title":"act021-3u50","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4980-760f-aca7-4b40648ee703","images":[{"id":"bound/act021.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act021.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=g%2B19IiTa9x9OqhQrZ8G83vv4Ppg%3D"}],"title":"act021-3u50","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-25T12:55:18.656344793Z","updatedAt":"2026-08-25T12:55:18.656344793Z"}
```

## 前置: 上线大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/amb021.png","name":"amb021-3u50","tags":["户外"],"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-498f-7c5f-bc1e-5579c0b91e2c","avatar":{"id":"bound/amb021.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb021.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NKj6HcUEfevse4wxV5%2BR16R9Xek%3D"},"name":"amb021-3u50","tags":["户外"],"weight":0,"online":true,"createdAt":"2026-08-25T12:55:18.671564176Z","updatedAt":"2026-08-25T12:55:18.671564176Z"}
```

## 前置: 路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"city021-3u50","title":"route021-3u50","thumbnail":"images/route021.png","images":["images/route021.png"],"ambassadorId":"01a038fd-498f-7c5f-bc1e-5579c0b91e2c","ambassadorNote":"大使说","travelTime":"3天","season":"春","travelStatus":"轻松"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-49a1-7dce-a4e3-10c559c41d3f","sortOrder":0,"title":"route021-3u50","cityName":"city021-3u50","ambassadorNote":"大使说","thumbnail":{"id":"bound/route021.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route021.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NsV7HNYEctLs8V02SwT3vy9zJLE%3D"},"images":[{"id":"bound/route021.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/route021.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NsV7HNYEctLs8V02SwT3vy9zJLE%3D"}],"travelTime":"3天","season":"春","travelStatus":"轻松","ambassadorId":"01a038fd-498f-7c5f-bc1e-5579c0b91e2c","ambassadorName":"amb021-3u50","spots":[],"createdAt":"2026-08-25T12:55:18.689831141Z","updatedAt":"2026-08-25T12:55:18.689831141Z"}
```

## 前置: 上线文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-13.png","contentHtml":"<p>正文</p>","title":"文章021-3u50","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-49ba-7b47-8ddc-a91957f0a137","image":{"id":"bound/art-13.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-13.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=vdtMfTRx64mdK5ct%2BD85f9qG0uA%3D"},"title":"文章021-3u50","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-25T12:55:18.714566652Z","updatedAt":"2026-08-25T12:55:18.714566652Z"}
```

## 前置: MENSTRUAL ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ACTIVITY.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a038fd-4980-760f-aca7-4b40648ee703","description":"活动条目021"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-49c9-7841-8590-f22ab02b6776","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"activityId":"01a038fd-4980-760f-aca7-4b40648ee703","routeId":null,"articleId":null,"relatedTitle":"act021-3u50","title":null,"subtitle":null,"description":"活动条目021","note":null,"banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=u%2FghYIk2oTC5%2FlgR0LWwKplvAh8%3D"},"createdAt":"2026-08-25T12:55:18.729482576Z","updatedAt":"2026-08-25T12:55:18.729482576Z"}
```

## 前置: MENSTRUAL ROUTE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ROUTE.png","online":true,"phase":"MENSTRUAL","type":"ROUTE","routeId":"01a038fd-49a1-7dce-a4e3-10c559c41d3f","title":"路线条目021","subtitle":"副","description":"说明"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-49d4-74fd-b88c-d4c42448d409","phase":"MENSTRUAL","type":"ROUTE","sortOrder":0,"online":true,"activityId":null,"routeId":"01a038fd-49a1-7dce-a4e3-10c559c41d3f","articleId":null,"relatedTitle":"route021-3u50","title":"路线条目021","subtitle":"副","description":"说明","note":null,"banner":{"id":"bound/banner-MENSTRUAL-ROUTE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ROUTE.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RBmnFrAYiQWqH48R63KOqlboxV4%3D"},"createdAt":"2026-08-25T12:55:18.740284012Z","updatedAt":"2026-08-25T12:55:18.740284012Z"}
```

## 前置: MENSTRUAL ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ARTICLE.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","articleId":"01a038fd-49ba-7b47-8ddc-a91957f0a137","title":"文章条目021"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-49df-7768-b4a9-6d66165bd80f","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":0,"online":true,"activityId":null,"routeId":null,"articleId":"01a038fd-49ba-7b47-8ddc-a91957f0a137","relatedTitle":"文章021-3u50","title":"文章条目021","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2FDa6HZQ%2FZ6j4mi4LjyeBRxKgHw8%3D"},"createdAt":"2026-08-25T12:55:18.751437106Z","updatedAt":"2026-08-25T12:55:18.751437106Z"}
```

## Step 2: GET ?type=ARTICLE

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?type=ARTICLE" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-49df-7768-b4a9-6d66165bd80f","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664318&OSSAccessKeyId=placeholder&Signature=2Xmf0khY35AZTkjK2tcoCkTp1m0%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-49ba-7b47-8ddc-a91957f0a137","title":"文章条目021","subtitle":null,"description":null,"note":null}]
```

## Step 3: GET 不带参数

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-49df-7768-b4a9-6d66165bd80f","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664318&OSSAccessKeyId=placeholder&Signature=2Xmf0khY35AZTkjK2tcoCkTp1m0%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-49ba-7b47-8ddc-a91957f0a137","title":"文章条目021","subtitle":null,"description":null,"note":null},{"id":"01a038fd-49d4-74fd-b88c-d4c42448d409","period":"MENSTRUAL","type":"ROUTE","banner":{"id":"bound/banner-MENSTRUAL-ROUTE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ROUTE.png?Expires=1787664318&OSSAccessKeyId=placeholder&Signature=lojO3jE07TMd0v7labNW9JN1d2A%3D"},"activityId":null,"routeId":"01a038fd-49a1-7dce-a4e3-10c559c41d3f","articleId":null,"title":"路线条目021","subtitle":"副","description":"说明","note":null},{"id":"01a038fd-49c9-7841-8590-f22ab02b6776","period":"MENSTRUAL","type":"ACTIVITY","banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664318&OSSAccessKeyId=placeholder&Signature=rgcKwbOdV5Y5t1bc8W257v9dXXM%3D"},"activityId":"01a038fd-4980-760f-aca7-4b40648ee703","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目021","note":null}]
```

## 清理: DELETE 周期条目 01a038fd-49c9-7841-8590-f22ab02b6776

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-49c9-7841-8590-f22ab02b6776" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-49d4-74fd-b88c-d4c42448d409

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-49d4-74fd-b88c-d4c42448d409" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-49df-7768-b4a9-6d66165bd80f

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-49df-7768-b4a9-6d66165bd80f" -H "Authorization: Bearer $TOKEN"
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

清理 DELETE 状态码: 200, 200, 200；清理后 app 端返回 []
