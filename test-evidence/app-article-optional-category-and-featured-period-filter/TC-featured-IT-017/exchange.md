# TC-featured-IT-017 GET /api/app/featured-cycle-items 关联实体不可见时条目不下发 — 请求/响应存证

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
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act017.png"],"title":"act017-3u50","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-45e4-7fef-bfab-c4a5644465e1","images":[{"id":"bound/act017.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act017.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Vymw70BYDKCby%2BaTnJppBpBLrkA%3D"}],"title":"act017-3u50","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-25T12:55:17.732933028Z","updatedAt":"2026-08-25T12:55:17.732933028Z"}
```

## 前置: 上线文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-11.png","contentHtml":"<p>正文</p>","title":"文章017-3u50","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-45f0-7633-bf42-06d0cb9d5735","image":{"id":"bound/art-11.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-11.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=QWEa5ojqISoXMOlmshdcV%2FTKWr8%3D"},"title":"文章017-3u50","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-25T12:55:17.74435188Z","updatedAt":"2026-08-25T12:55:17.74435188Z"}
```

## 前置: MENSTRUAL ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ACTIVITY.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a038fd-45e4-7fef-bfab-c4a5644465e1","description":"活动条目017"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-45fb-7b1c-95f5-d84ee22ce8c7","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"activityId":"01a038fd-45e4-7fef-bfab-c4a5644465e1","routeId":null,"articleId":null,"relatedTitle":"act017-3u50","title":null,"subtitle":null,"description":"活动条目017","note":null,"banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NaNGnDDa33rnXFFqipKoRHDTAmM%3D"},"createdAt":"2026-08-25T12:55:17.755659477Z","updatedAt":"2026-08-25T12:55:17.755659477Z"}
```

## 前置: MENSTRUAL ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ARTICLE.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","articleId":"01a038fd-45f0-7633-bf42-06d0cb9d5735","title":"文章条目017"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4607-7c95-93f5-d8390d15d7ba","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":0,"online":true,"activityId":null,"routeId":null,"articleId":"01a038fd-45f0-7633-bf42-06d0cb9d5735","relatedTitle":"文章017-3u50","title":"文章条目017","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=MGJf7cHCSI%2F1%2BRJbZ2Y7aF5gNE4%3D"},"createdAt":"2026-08-25T12:55:17.767749834Z","updatedAt":"2026-08-25T12:55:17.767749834Z"}
```

## Step 1: 初始

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-4607-7c95-93f5-d8390d15d7ba","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=nwk9y4ezAnkmgmccIsgBxAdmtZo%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-45f0-7633-bf42-06d0cb9d5735","title":"文章条目017","subtitle":null,"description":null,"note":null},{"id":"01a038fd-45fb-7b1c-95f5-d84ee22ce8c7","period":"MENSTRUAL","type":"ACTIVITY","banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=6Hb8qv42DGWy9nXhAX5cayVocGs%3D"},"activityId":"01a038fd-45e4-7fef-bfab-c4a5644465e1","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目017","note":null}]
```

## Step 2: 活动下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a038fd-45e4-7fef-bfab-c4a5644465e1/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-45e4-7fef-bfab-c4a5644465e1","images":[{"id":"bound/act017.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act017.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Vymw70BYDKCby%2BaTnJppBpBLrkA%3D"}],"title":"act017-3u50","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":false,"createdAt":"2026-08-25T12:55:17.732933Z","updatedAt":"2026-08-25T12:55:17.732933Z"}
```

## Step 2: 活动下线后

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-4607-7c95-93f5-d8390d15d7ba","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=nwk9y4ezAnkmgmccIsgBxAdmtZo%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-45f0-7633-bf42-06d0cb9d5735","title":"文章条目017","subtitle":null,"description":null,"note":null}]
```

## Step 3: 活动恢复上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a038fd-45e4-7fef-bfab-c4a5644465e1/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-45e4-7fef-bfab-c4a5644465e1","images":[{"id":"bound/act017.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act017.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Vymw70BYDKCby%2BaTnJppBpBLrkA%3D"}],"title":"act017-3u50","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-25T12:55:17.732933Z","updatedAt":"2026-08-25T12:55:17.797133Z"}
```

## Step 3: 活动恢复后

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-4607-7c95-93f5-d8390d15d7ba","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=nwk9y4ezAnkmgmccIsgBxAdmtZo%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-45f0-7633-bf42-06d0cb9d5735","title":"文章条目017","subtitle":null,"description":null,"note":null},{"id":"01a038fd-45fb-7b1c-95f5-d84ee22ce8c7","period":"MENSTRUAL","type":"ACTIVITY","banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=6Hb8qv42DGWy9nXhAX5cayVocGs%3D"},"activityId":"01a038fd-45e4-7fef-bfab-c4a5644465e1","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目017","note":null}]
```

## Step 4: 文章下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a038fd-45f0-7633-bf42-06d0cb9d5735/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-45f0-7633-bf42-06d0cb9d5735","image":{"id":"bound/art-11.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-11.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=QWEa5ojqISoXMOlmshdcV%2FTKWr8%3D"},"title":"文章017-3u50","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":false,"createdAt":"2026-08-25T12:55:17.744352Z","updatedAt":"2026-08-25T12:55:17.744352Z"}
```

## Step 4: 文章下线后

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-45fb-7b1c-95f5-d84ee22ce8c7","period":"MENSTRUAL","type":"ACTIVITY","banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=6Hb8qv42DGWy9nXhAX5cayVocGs%3D"},"activityId":"01a038fd-45e4-7fef-bfab-c4a5644465e1","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目017","note":null}]
```

## Step 5a: 文章恢复上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a038fd-45f0-7633-bf42-06d0cb9d5735/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-45f0-7633-bf42-06d0cb9d5735","image":{"id":"bound/art-11.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-11.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=QWEa5ojqISoXMOlmshdcV%2FTKWr8%3D"},"title":"文章017-3u50","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-25T12:55:17.744352Z","updatedAt":"2026-08-25T12:55:17.85166Z"}
```

## Step 5b: 删除文章

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/articles/01a038fd-45f0-7633-bf42-06d0cb9d5735" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## Step 5: 文章删除后

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-45fb-7b1c-95f5-d84ee22ce8c7","period":"MENSTRUAL","type":"ACTIVITY","banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=6Hb8qv42DGWy9nXhAX5cayVocGs%3D"},"activityId":"01a038fd-45e4-7fef-bfab-c4a5644465e1","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目017","note":null}]
```

## 清理: DELETE 周期条目 01a038fd-45fb-7b1c-95f5-d84ee22ce8c7

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-45fb-7b1c-95f5-d84ee22ce8c7" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-4607-7c95-93f5-d8390d15d7ba

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4607-7c95-93f5-d8390d15d7ba" -H "Authorization: Bearer $TOKEN"
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
