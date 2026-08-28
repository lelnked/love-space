# TC-featured-IT-031 GET /api/app/featured-cycle-items 不可下发条目不贡献周期 — 请求/响应存证

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

## 前置: 活动 act031

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act031.png"],"title":"act031-a19d47","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f1d1-7592-9a1b-6a70cc959f6b","images":[{"id":"bound/act031.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act031.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act031-a19d47","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:41.809318836Z","updatedAt":"2026-08-28T13:16:41.809318836Z"}
```

## 前置: MENSTRUAL 上线 ACTIVITY（关联活动 A）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b031m.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-f1d1-7592-9a1b-6a70cc959f6b","description":"经期描述"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f1d9-77bf-9423-c225fcf150f9","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-f1d1-7592-9a1b-6a70cc959f6b","relatedTitle":"act031-a19d47","title":null,"subtitle":null,"description":"经期描述","note":null,"banner":{"id":"bound/b031m.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b031m.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.817462269Z","updatedAt":"2026-08-28T13:16:41.817462269Z"}
```

## 前置: LUTEAL 下线 ACTIVITY（关联活动 A）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b031l.png","online":false,"phase":"LUTEAL","type":"ACTIVITY","targetId":"01a04883-f1d1-7592-9a1b-6a70cc959f6b","description":"黄体期描述"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f1e1-7b45-a0e1-8941200bdf63","phase":"LUTEAL","type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a04883-f1d1-7592-9a1b-6a70cc959f6b","relatedTitle":"act031-a19d47","title":null,"subtitle":null,"description":"黄体期描述","note":null,"banner":{"id":"bound/b031l.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b031l.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.82568388Z","updatedAt":"2026-08-28T13:16:41.82568388Z"}
```

## Step 2: GET 不带参数

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-f1d9-77bf-9423-c225fcf150f9","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b031m.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b031m.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-f1d1-7592-9a1b-6a70cc959f6b","title":null,"subtitle":null,"description":"经期描述","note":null}]
```

## Step 3: admin 将 LUTEAL 那条上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a04883-f1e1-7b45-a0e1-8941200bdf63/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f1e1-7b45-a0e1-8941200bdf63","phase":"LUTEAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04883-f1d1-7592-9a1b-6a70cc959f6b","relatedTitle":"act031-a19d47","title":null,"subtitle":null,"description":"黄体期描述","note":null,"banner":{"id":"bound/b031l.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b031l.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:41.825684Z","updatedAt":"2026-08-28T13:16:41.825684Z"}
```

## Step 3: GET 不带参数

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-f1e1-7b45-a0e1-8941200bdf63","period":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","banner":{"id":"bound/b031l.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b031l.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-f1d1-7592-9a1b-6a70cc959f6b","title":null,"subtitle":null,"description":"黄体期描述","note":null},{"id":"01a04883-f1d9-77bf-9423-c225fcf150f9","period":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","banner":{"id":"bound/b031m.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b031m.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-f1d1-7592-9a1b-6a70cc959f6b","title":null,"subtitle":null,"description":"经期描述","note":null}]
```

## Step 4: 将活动 A 下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a04883-f1d1-7592-9a1b-6a70cc959f6b/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-f1d1-7592-9a1b-6a70cc959f6b","images":[{"id":"bound/act031.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act031.png?Expires=1787924801&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act031-a19d47","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":false,"createdAt":"2026-08-28T13:16:41.809319Z","updatedAt":"2026-08-28T13:16:41.809319Z"}
```

## Step 4: GET 不带参数

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[]
```
