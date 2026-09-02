# TC-featured-IT-007 POST /api/admin/featured-cycle-items 创建活动类周期推荐 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: 活动 act-m9p1（尚未被任何周期推荐引用）
活动 id=01a0622c-32b2-7df8-9bfb-3bfe7426a956，title=act-m9p1（经 admin POST /api/admin/activities 创建，库中周期推荐已清空，故未被引用）

## Step 3: POST 活动类周期推荐
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ACTIVITY", "targetId": "01a0622c-32b2-7df8-9bfb-3bfe7426a956", "description": "经期慢下来", "note": "周末两日", "banner": "images/b007.png", "sortOrder": 1}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-32bc-76d3-802c-047e5413a23c","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":1,"online":false,"targetId":"01a0622c-32b2-7df8-9bfb-3bfe7426a956","relatedTitle":"act-m9p1","title":null,"subtitle":null,"description":"经期慢下来","note":"周末两日","banner":{"id":"bound/b007.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b007.png?Expires=1788355258&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:58.876379988Z","updatedAt":"2026-09-02T12:50:58.876379988Z"}
```

## Step 4: GET 详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-32bc-76d3-802c-047e5413a23c" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-32bc-76d3-802c-047e5413a23c","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":1,"online":false,"targetId":"01a0622c-32b2-7df8-9bfb-3bfe7426a956","relatedTitle":"act-m9p1","title":null,"subtitle":null,"description":"经期慢下来","note":"周末两日","banner":{"id":"bound/b007.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b007.png?Expires=1788355258&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:58.87638Z","updatedAt":"2026-09-02T12:50:58.87638Z"}
```
