# TC-featured-IT-019 GET /api/app/featured-cycle-items 按排序号升序 — 请求/响应存证

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

## 前置: 活动 act019n0

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act019n0.png"],"title":"act019n0-a1f2b5","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ee11-7f12-90c4-b97e4e29effe","images":[{"id":"bound/act019n0.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act019n0.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act019n0-a1f2b5","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:40.849909404Z","updatedAt":"2026-08-28T13:16:40.849909404Z"}
```

## 前置: MENSTRUAL 上线条目 #1 sortOrder=2

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b0190.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-ee11-7f12-90c4-b97e4e29effe","description":"条目1","sortOrder":2}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ee1a-7bd0-bf96-85e7bd7a93c5","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":2,"online":true,"targetId":"01a04883-ee11-7f12-90c4-b97e4e29effe","relatedTitle":"act019n0-a1f2b5","title":null,"subtitle":null,"description":"条目1","note":null,"banner":{"id":"bound/b0190.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b0190.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:40.858709432Z","updatedAt":"2026-08-28T13:16:40.858709432Z"}
```

## 前置: 活动 act019n1

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act019n1.png"],"title":"act019n1-a1f2b5","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ee23-759d-860d-3e46e785317e","images":[{"id":"bound/act019n1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act019n1.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act019n1-a1f2b5","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:40.867318852Z","updatedAt":"2026-08-28T13:16:40.867318852Z"}
```

## 前置: MENSTRUAL 上线条目 #2 sortOrder=1

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b0191.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-ee23-759d-860d-3e46e785317e","description":"条目2","sortOrder":1}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ee2c-7146-a413-55ab8ae93996","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":1,"online":true,"targetId":"01a04883-ee23-759d-860d-3e46e785317e","relatedTitle":"act019n1-a1f2b5","title":null,"subtitle":null,"description":"条目2","note":null,"banner":{"id":"bound/b0191.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b0191.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:40.876049178Z","updatedAt":"2026-08-28T13:16:40.876049178Z"}
```

## 前置: 活动 act019n2

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act019n2.png"],"title":"act019n2-a1f2b5","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ee34-74a6-9c44-db1a77365ba1","images":[{"id":"bound/act019n2.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act019n2.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act019n2-a1f2b5","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:40.884264358Z","updatedAt":"2026-08-28T13:16:40.884264358Z"}
```

## 前置: MENSTRUAL 上线条目 #3 sortOrder=3

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b0192.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-ee34-74a6-9c44-db1a77365ba1","description":"条目3","sortOrder":3}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ee3c-7dcc-aa9c-97c042294584","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":3,"online":true,"targetId":"01a04883-ee34-74a6-9c44-db1a77365ba1","relatedTitle":"act019n2-a1f2b5","title":null,"subtitle":null,"description":"条目3","note":null,"banner":{"id":"bound/b0192.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b0192.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:40.892828869Z","updatedAt":"2026-08-28T13:16:40.892828869Z"}
```

## 前置: 活动 act019n3

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act019n3.png"],"title":"act019n3-a1f2b5","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ee44-7b67-96d4-2f1982b18d18","images":[{"id":"bound/act019n3.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act019n3.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act019n3-a1f2b5","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:40.900686158Z","updatedAt":"2026-08-28T13:16:40.900686158Z"}
```

## 前置: MENSTRUAL 上线条目 #4 sortOrder=1

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b0193.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-ee44-7b67-96d4-2f1982b18d18","description":"条目4","sortOrder":1}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ee4c-7ef3-8ee7-0adc73e7d163","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":1,"online":true,"targetId":"01a04883-ee44-7b67-96d4-2f1982b18d18","relatedTitle":"act019n3-a1f2b5","title":null,"subtitle":null,"description":"条目4","note":null,"banner":{"id":"bound/b0193.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b0193.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:40.908906827Z","updatedAt":"2026-08-28T13:16:40.908906827Z"}
```

## 前置: 活动 act019n4

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act019n4.png"],"title":"act019n4-a1f2b5","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ee54-7cd1-9e36-bc867e6d43c9","images":[{"id":"bound/act019n4.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act019n4.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act019n4-a1f2b5","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:16:40.916774217Z","updatedAt":"2026-08-28T13:16:40.916774217Z"}
```

## 前置: MENSTRUAL 上线条目 #5 sortOrder=1

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b0194.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04883-ee54-7cd1-9e36-bc867e6d43c9","description":"条目5","sortOrder":1}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04883-ee5c-7a60-8fe8-13a861242489","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":1,"online":true,"targetId":"01a04883-ee54-7cd1-9e36-bc867e6d43c9","relatedTitle":"act019n4-a1f2b5","title":null,"subtitle":null,"description":"条目5","note":null,"banner":{"id":"bound/b0194.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b0194.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:16:40.92462286Z","updatedAt":"2026-08-28T13:16:40.92462286Z"}
```

## Step 2: GET ?period=MENSTRUAL

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04883-ee5c-7a60-8fe8-13a861242489","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b0194.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b0194.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ee54-7cd1-9e36-bc867e6d43c9","title":null,"subtitle":null,"description":"条目5","note":null},{"id":"01a04883-ee4c-7ef3-8ee7-0adc73e7d163","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b0193.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b0193.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ee44-7b67-96d4-2f1982b18d18","title":null,"subtitle":null,"description":"条目4","note":null},{"id":"01a04883-ee2c-7146-a413-55ab8ae93996","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b0191.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b0191.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ee23-759d-860d-3e46e785317e","title":null,"subtitle":null,"description":"条目2","note":null},{"id":"01a04883-ee1a-7bd0-bf96-85e7bd7a93c5","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b0190.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b0190.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ee11-7f12-90c4-b97e4e29effe","title":null,"subtitle":null,"description":"条目1","note":null},{"id":"01a04883-ee3c-7dcc-aa9c-97c042294584","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b0192.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b0192.png?Expires=1787924800&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04883-ee34-74a6-9c44-db1a77365ba1","title":null,"subtitle":null,"description":"条目3","note":null}]
```
