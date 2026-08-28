# TC-featured-IT-007 POST /api/admin/featured-cycle-items 创建活动类周期推荐 — 请求/响应存证

执行日期: 2026-08-28 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `g8k` 防撞名。清理请求（DELETE）不入存证。

## 前置: 活动 act007

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"images":["images/act007.png"],"title":"act007-g8k","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04880-e252-7623-b3c6-d8cb3a33352e","images":[{"id":"bound/act007.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act007.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act007-g8k","tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-08-28T13:13:21.234316036Z","updatedAt":"2026-08-28T13:13:21.234316036Z"}
```

## Step 3: POST 活动类周期推荐

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/b007.png","phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04880-e252-7623-b3c6-d8cb3a33352e","description":"经期慢下来","note":"周末两日","sortOrder":1}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04880-e260-7cb0-88f1-cedf7b9093a0","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":1,"online":false,"targetId":"01a04880-e252-7623-b3c6-d8cb3a33352e","relatedTitle":"act007-g8k","title":null,"subtitle":null,"description":"经期慢下来","note":"周末两日","banner":{"id":"bound/b007.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b007.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:13:21.248746495Z","updatedAt":"2026-08-28T13:13:21.248746495Z"}
```

## Step 4: GET 详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a04880-e260-7cb0-88f1-cedf7b9093a0" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04880-e260-7cb0-88f1-cedf7b9093a0","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":1,"online":false,"targetId":"01a04880-e252-7623-b3c6-d8cb3a33352e","relatedTitle":"act007-g8k","title":null,"subtitle":null,"description":"经期慢下来","note":"周末两日","banner":{"id":"bound/b007.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b007.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-08-28T13:13:21.248746Z","updatedAt":"2026-08-28T13:13:21.248746Z"}
```
