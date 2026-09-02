# TC-featured-IT-015 DELETE /api/admin/featured-cycle-items/{id} 物理删除 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: 条目 01a0622c-3461-7ca1-a4d9-97c1e2ef7c42（关联活动 01a0622c-345b-7f2e-9d08-64b72aa88f77）

## Step 2: DELETE
```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-3461-7ca1-a4d9-97c1e2ef7c42" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: -）
```json
(empty)
```

## Step 3: GET 详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-3461-7ca1-a4d9-97c1e2ef7c42" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"周期推荐不存在：01a0622c-3461-7ca1-a4d9-97c1e2ef7c42","path":"/api/admin/featured-cycle-items/01a0622c-3461-7ca1-a4d9-97c1e2ef7c42"}
```

## Step 4: 再删一次
```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-3461-7ca1-a4d9-97c1e2ef7c42" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"周期推荐不存在：01a0622c-3461-7ca1-a4d9-97c1e2ef7c42","path":"/api/admin/featured-cycle-items/01a0622c-3461-7ca1-a4d9-97c1e2ef7c42"}
```

## 复核: 关联活动仍在
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/activities/01a0622c-345b-7f2e-9d08-64b72aa88f77" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-345b-7f2e-9d08-64b72aa88f77","images":[{"id":"bound/act-m9p25.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act-m9p25.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act-m9p25","subtitle":null,"tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-09-02T12:50:59.291912Z","updatedAt":"2026-09-02T12:50:59.291912Z"}
```
