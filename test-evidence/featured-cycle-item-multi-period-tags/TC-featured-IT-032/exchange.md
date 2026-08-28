# TC-featured-IT-032 GET /api/app/featured-cycle-items?period= 不同 target 的周期集合互不影响 — 请求/响应存证

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

## 前置: 活动 act032a

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act032a.png"],"title":"act032a-a1baf3","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f241-7e19-bbac-4eb878c42977","images":[{"id":"bound/act032a.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act032a.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act032a-a1baf3","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:41.921847607Z","updatedAt":"2026-08-28T13:16:41.921847607Z"}
```

## 前置: 活动 act032b

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act032b.png"],"title":"act032b-a1baf3","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f249-79de-baa0-ab864b823fa2","images":[{"id":"bound/act032b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act032b.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act032b-a1baf3","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:41.929578223Z","updatedAt":"2026-08-28T13:16:41.929578223Z"}
```

## 前置: A 在 MENSTRUAL 上线条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b032am.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-f241-7e19-bbac-4eb878c42977","description":"A 经期"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f251-786a-9fda-f6ce35912295","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-f241-7e19-bbac-4eb878c42977","relatedTitle":"act032a-a1baf3","title":null,"subtitle":null,"description":"A 经期","note":null,"banner":{"id":"bound/b032am.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b032am.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.937504528Z","updatedAt":"2026-08-28T13:16:41.937504528Z"}
```

## 前置: A 在 LUTEAL 上线条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b032al.png","online":true,"phase":"LUTEAL","type":"ACTIVITY","targetId":"01a04883-f241-7e19-bbac-4eb878c42977","description":"A 黄体期"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f259-7371-b715-6652dbff3016","phase":"LUTEAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-f241-7e19-bbac-4eb878c42977","relatedTitle":"act032a-a1baf3","title":null,"subtitle":null,"description":"A 黄体期","note":null,"banner":{"id":"bound/b032al.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b032al.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.945195436Z","updatedAt":"2026-08-28T13:16:41.945195436Z"}
```

## 前置: B 仅在 MENSTRUAL 上线条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b032bm.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-f249-79de-baa0-ab864b823fa2","description":"B 经期"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f260-7be8-8cb6-1a51ad20c7f2","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-f249-79de-baa0-ab864b823fa2","relatedTitle":"act032b-a1baf3","title":null,"subtitle":null,"description":"B 经期","note":null,"banner":{"id":"bound/b032bm.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b032bm.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.952726097Z","updatedAt":"2026-08-28T13:16:41.952726097Z"}
```

## Step 2: GET ?period=MENSTRUAL

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-f260-7be8-8cb6-1a51ad20c7f2","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b032bm.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b032bm.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-f249-79de-baa0-ab864b823fa2","title":null,"subtitle":null,"description":"B 经期","note":null},{"id":"01a04883-f251-786a-9fda-f6ce35912295","period":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","banner":{"id":"bound/b032am.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b032am.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-f241-7e19-bbac-4eb878c42977","title":null,"subtitle":null,"description":"A 经期","note":null}]
```
