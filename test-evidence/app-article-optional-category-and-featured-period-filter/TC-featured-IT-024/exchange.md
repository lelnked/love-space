# TC-featured-IT-024 GET /api/app/featured-cycle-items?period= 按周期过滤 — 请求/响应存证

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
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act024.png"],"title":"act024-3u50","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4a92-7c74-ad29-96b38ded7b30","images":[{"id":"bound/act024.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act024.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=h%2Fm6nyKXEKxjVGY4iIGYrjMR0VE%3D"}],"title":"act024-3u50","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-25T12:55:18.930722617Z","updatedAt":"2026-08-25T12:55:18.930722617Z"}
```

## 前置: 上线文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-14.png","contentHtml":"<p>正文</p>","title":"文章024-3u50","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4a9d-7db9-bd6d-f6c04557aa0d","image":{"id":"bound/art-14.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-14.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RqxgtihA%2FSU4FrOQ4T1OH5yfyDs%3D"},"title":"文章024-3u50","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-25T12:55:18.941831115Z","updatedAt":"2026-08-25T12:55:18.941831115Z"}
```

## 前置: MENSTRUAL ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ACTIVITY.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a038fd-4a92-7c74-ad29-96b38ded7b30","description":"活动条目024"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4aa8-764c-9350-da73ef4aad1f","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"activityId":"01a038fd-4a92-7c74-ad29-96b38ded7b30","routeId":null,"articleId":null,"relatedTitle":"act024-3u50","title":null,"subtitle":null,"description":"活动条目024","note":null,"banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=u%2FghYIk2oTC5%2FlgR0LWwKplvAh8%3D"},"createdAt":"2026-08-25T12:55:18.952345154Z","updatedAt":"2026-08-25T12:55:18.952345154Z"}
```

## 前置: MENSTRUAL ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ARTICLE.png","online":true,"phase":"MENSTRUAL","type":"ARTICLE","articleId":"01a038fd-4a9d-7db9-bd6d-f6c04557aa0d","title":"文章条目024"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4ab2-7df5-8321-d8357f9bcf86","phase":"MENSTRUAL","type":"ARTICLE","sortOrder":0,"online":true,"activityId":null,"routeId":null,"articleId":"01a038fd-4a9d-7db9-bd6d-f6c04557aa0d","relatedTitle":"文章024-3u50","title":"文章条目024","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2FDa6HZQ%2FZ6j4mi4LjyeBRxKgHw8%3D"},"createdAt":"2026-08-25T12:55:18.962849863Z","updatedAt":"2026-08-25T12:55:18.962849863Z"}
```

## 前置: FOLLICULAR ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-FOLLICULAR-ARTICLE.png","online":true,"phase":"FOLLICULAR","type":"ARTICLE","articleId":"01a038fd-4a9d-7db9-bd6d-f6c04557aa0d","title":"卵泡期文章条目024"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4abd-7fd5-a821-74441227b3a4","phase":"FOLLICULAR","type":"ARTICLE","sortOrder":0,"online":true,"activityId":null,"routeId":null,"articleId":"01a038fd-4a9d-7db9-bd6d-f6c04557aa0d","relatedTitle":"文章024-3u50","title":"卵泡期文章条目024","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/banner-FOLLICULAR-ARTICLE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-FOLLICULAR-ARTICLE.png?Expires=1787664318&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=p1E3lROGqUImkRl2EckquHibiQw%3D"},"createdAt":"2026-08-25T12:55:18.973962774Z","updatedAt":"2026-08-25T12:55:18.973962774Z"}
```

## Step 2: GET ?period=MENSTRUAL

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-4ab2-7df5-8321-d8357f9bcf86","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ARTICLE.png?Expires=1787664318&OSSAccessKeyId=placeholder&Signature=2Xmf0khY35AZTkjK2tcoCkTp1m0%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-4a9d-7db9-bd6d-f6c04557aa0d","title":"文章条目024","subtitle":null,"description":null,"note":null},{"id":"01a038fd-4aa8-764c-9350-da73ef4aad1f","period":"MENSTRUAL","type":"ACTIVITY","banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664318&OSSAccessKeyId=placeholder&Signature=rgcKwbOdV5Y5t1bc8W257v9dXXM%3D"},"activityId":"01a038fd-4a92-7c74-ad29-96b38ded7b30","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目024","note":null}]
```

## Step 3: GET ?period=FOLLICULAR

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=FOLLICULAR" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-4abd-7fd5-a821-74441227b3a4","period":"FOLLICULAR","type":"ARTICLE","banner":{"id":"bound/banner-FOLLICULAR-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-FOLLICULAR-ARTICLE.png?Expires=1787664319&OSSAccessKeyId=placeholder&Signature=OY%2FAiDTOXdnK5mLVnG%2Bm4Lh88B0%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-4a9d-7db9-bd6d-f6c04557aa0d","title":"卵泡期文章条目024","subtitle":null,"description":null,"note":null}]
```

## 清理: DELETE 周期条目 01a038fd-4aa8-764c-9350-da73ef4aad1f

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4aa8-764c-9350-da73ef4aad1f" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-4ab2-7df5-8321-d8357f9bcf86

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4ab2-7df5-8321-d8357f9bcf86" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-4abd-7fd5-a821-74441227b3a4

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4abd-7fd5-a821-74441227b3a4" -H "Authorization: Bearer $TOKEN"
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
