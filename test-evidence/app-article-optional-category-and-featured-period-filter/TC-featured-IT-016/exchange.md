# TC-featured-IT-016 GET /api/app/featured-cycle-items 扁平数组带 period 字段且只含上线条目 — 请求/响应存证

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
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act016.png"],"title":"act016-3u50","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4546-7064-9bc2-032b5141522a","images":[{"id":"bound/act016.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act016.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=2pALE8Zgq9n2rd22kOYxfrO0s7Q%3D"}],"title":"act016-3u50","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-25T12:55:17.573937681Z","updatedAt":"2026-08-25T12:55:17.573937681Z"}
```

## 前置: 上线文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-10.png","contentHtml":"<p>正文</p>","title":"文章016-3u50","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4553-74fc-8301-f8bff6878367","image":{"id":"bound/art-10.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-10.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cWskMjCsNITtz3VTwnjgqKBXwdA%3D"},"title":"文章016-3u50","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-25T12:55:17.587234049Z","updatedAt":"2026-08-25T12:55:17.587234049Z"}
```

## 前置: MENSTRUAL 上线 ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-MENSTRUAL-ACTIVITY.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a038fd-4546-7064-9bc2-032b5141522a","description":"活动条目016"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4563-7601-879c-40c8f628ef67","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"activityId":"01a038fd-4546-7064-9bc2-032b5141522a","routeId":null,"articleId":null,"relatedTitle":"act016-3u50","title":null,"subtitle":null,"description":"活动条目016","note":null,"banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NaNGnDDa33rnXFFqipKoRHDTAmM%3D"},"createdAt":"2026-08-25T12:55:17.60331556Z","updatedAt":"2026-08-25T12:55:17.60331556Z"}
```

## 前置: OVULATION 上线 ARTICLE

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-OVULATION-ARTICLE.png","online":true,"phase":"OVULATION","type":"ARTICLE","articleId":"01a038fd-4553-74fc-8301-f8bff6878367","title":"文章条目016"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4577-7683-9167-91acaf3d57e6","phase":"OVULATION","type":"ARTICLE","sortOrder":0,"online":true,"activityId":null,"routeId":null,"articleId":"01a038fd-4553-74fc-8301-f8bff6878367","relatedTitle":"文章016-3u50","title":"文章条目016","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/banner-OVULATION-ARTICLE.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-OVULATION-ARTICLE.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=5CZ5Eu6k76M4k3BhKOVsegfGL5Q%3D"},"createdAt":"2026-08-25T12:55:17.623357595Z","updatedAt":"2026-08-25T12:55:17.623357595Z"}
```

## 前置: LUTEAL 下线 ACTIVITY

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/banner-LUTEAL-ACTIVITY.png","online":false,"phase":"LUTEAL","type":"ACTIVITY","activityId":"01a038fd-4546-7064-9bc2-032b5141522a","description":"下线条目016"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4585-7a0a-bc9a-50b96cee4609","phase":"LUTEAL","type":"ACTIVITY","sortOrder":0,"online":false,"activityId":"01a038fd-4546-7064-9bc2-032b5141522a","routeId":null,"articleId":null,"relatedTitle":"act016-3u50","title":null,"subtitle":null,"description":"下线条目016","note":null,"banner":{"id":"bound/banner-LUTEAL-ACTIVITY.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/banner-LUTEAL-ACTIVITY.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=4XgdS4p3dDx4frLTMfjQahUpucs%3D"},"createdAt":"2026-08-25T12:55:17.637583224Z","updatedAt":"2026-08-25T12:55:17.637583224Z"}
```

## Step 2: GET 不带参数

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-4577-7683-9167-91acaf3d57e6","period":"OVULATION","type":"ARTICLE","banner":{"id":"bound/banner-OVULATION-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-OVULATION-ARTICLE.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=cFqn0RDFtRjrlVYmikDueaHYxzM%3D"},"activityId":null,"routeId":null,"articleId":"01a038fd-4553-74fc-8301-f8bff6878367","title":"文章条目016","subtitle":null,"description":null,"note":null},{"id":"01a038fd-4563-7601-879c-40c8f628ef67","period":"MENSTRUAL","type":"ACTIVITY","banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-MENSTRUAL-ACTIVITY.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=6Hb8qv42DGWy9nXhAX5cayVocGs%3D"},"activityId":"01a038fd-4546-7064-9bc2-032b5141522a","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目016","note":null}]
```

## 清理: DELETE 周期条目 01a038fd-4563-7601-879c-40c8f628ef67

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4563-7601-879c-40c8f628ef67" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-4577-7683-9167-91acaf3d57e6

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4577-7683-9167-91acaf3d57e6" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## 清理: DELETE 周期条目 01a038fd-4585-7a0a-bc9a-50b96cee4609

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a038fd-4585-7a0a-bc9a-50b96cee4609" -H "Authorization: Bearer $TOKEN"
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
