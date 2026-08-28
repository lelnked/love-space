# TC-featured-IT-029 GET /api/app/featured-cycle-items?period= 过滤后 period 数组仍含其他周期 — 请求/响应存证

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

## 前置: 活动 act029

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act029.png"],"title":"act029-a1f0f5","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f144-7358-81da-60ed4ca4c419","images":[{"id":"bound/act029.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act029.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act029-a1f0f5","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:41.668185693Z","updatedAt":"2026-08-28T13:16:41.668185693Z"}
```

## 前置: MENSTRUAL 上线 ACTIVITY（关联活动 A）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b029-m.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-f144-7358-81da-60ed4ca4c419","description":"经期描述","title":"经期主标题"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f14c-76c8-bf4e-203e48c5dc51","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-f144-7358-81da-60ed4ca4c419","relatedTitle":"act029-a1f0f5","title":null,"subtitle":null,"description":"经期描述","note":null,"banner":{"id":"bound/b029-m.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b029-m.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.676400306Z","updatedAt":"2026-08-28T13:16:41.676400306Z"}
```

## 前置: LUTEAL 上线 ACTIVITY（关联同一活动 A）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b029-l.png","online":true,"phase":"LUTEAL","type":"ACTIVITY","targetId":"01a04883-f144-7358-81da-60ed4ca4c419","description":"黄体期描述","title":"黄体期主标题"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f154-797c-93b8-05934aedea4b","phase":"LUTEAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-f144-7358-81da-60ed4ca4c419","relatedTitle":"act029-a1f0f5","title":null,"subtitle":null,"description":"黄体期描述","note":null,"banner":{"id":"bound/b029-l.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b029-l.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.684570873Z","updatedAt":"2026-08-28T13:16:41.684570873Z"}
```

## Step 2: GET ?period=LUTEAL

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=LUTEAL" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-f154-797c-93b8-05934aedea4b","period":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","banner":{"id":"bound/b029-l.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b029-l.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-f144-7358-81da-60ed4ca4c419","title":null,"subtitle":null,"description":"黄体期描述","note":null}]
```
