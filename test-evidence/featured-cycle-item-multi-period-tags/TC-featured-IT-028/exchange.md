# TC-featured-IT-028 GET /api/app/featured-cycle-items 同一 target 跨周期时两条均下发全部周期 — 请求/响应存证

执行日期: 2026-08-28 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `a2` 防撞名。清理请求（DELETE）不入存证。

## 前置核对: 周期条目表为空（保证「恰含 N 条」断言不受历史数据污染）

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=1&size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"content":[],"page":1,"size":30,"totalElements":0,"totalPages":0}
```

## 前置: 活动 act028

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act028.png"],"title":"act028-a28389","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04884-716a-7456-bc1b-63558e6f8318","images":[{"id":"bound/act028.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act028.png?Expires=1787924834&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act028-a28389","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:17:14.474237174Z","updatedAt":"2026-08-28T13:17:14.474237174Z"}
```

## 前置: MENSTRUAL 上线 ACTIVITY（关联活动 A）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b028-m.png","online":true,"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04884-716a-7456-bc1b-63558e6f8318","description":"经期描述","title":"经期主标题"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04884-7172-7926-b3bb-c475e8b1778b","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04884-716a-7456-bc1b-63558e6f8318","relatedTitle":"act028-a28389","title":null,"subtitle":null,"description":"经期描述","note":null,"banner":{"id":"bound/b028-m.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b028-m.png?Expires=1787924834&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:17:14.482549411Z","updatedAt":"2026-08-28T13:17:14.482549411Z"}
```

## 前置: LUTEAL 上线 ACTIVITY（关联同一活动 A）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b028-l.png","online":true,"phase":"LUTEAL","type":"ACTIVITY","targetId":"01a04884-716a-7456-bc1b-63558e6f8318","description":"黄体期描述","title":"黄体期主标题"}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04884-717a-7b48-b57f-0862f316510e","phase":"LUTEAL","type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a04884-716a-7456-bc1b-63558e6f8318","relatedTitle":"act028-a28389","title":null,"subtitle":null,"description":"黄体期描述","note":null,"banner":{"id":"bound/b028-l.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b028-l.png?Expires=1787924834&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:17:14.490684646Z","updatedAt":"2026-08-28T13:17:14.490684646Z"}
```

## Step 2: GET 不带参数

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a04884-717a-7b48-b57f-0862f316510e","period":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","banner":{"id":"bound/b028-l.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b028-l.png?Expires=1787924834&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04884-716a-7456-bc1b-63558e6f8318","title":null,"subtitle":null,"description":"黄体期描述","note":null},{"id":"01a04884-7172-7926-b3bb-c475e8b1778b","period":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","banner":{"id":"bound/b028-m.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b028-m.png?Expires=1787924834&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a04884-716a-7456-bc1b-63558e6f8318","title":null,"subtitle":null,"description":"经期描述","note":null}]
```
