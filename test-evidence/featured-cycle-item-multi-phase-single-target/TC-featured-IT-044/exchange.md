# TC-featured-IT-044 PUT /api/admin/featured-cycle-items/{id} 更新指向已被占用的实体被拒绝 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 活动 A 有条目 CA=01a0622d-6237-733d-85f4-0425587ec109，活动 B 有条目 CB=01a0622d-623c-7bc4-831a-8484ff11aa25

## Step 2: PUT CA 的 targetId 改为活动 B
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-6237-733d-85f4-0425587ec109" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["OVULATION"], "type": "ACTIVITY", "targetId": "01a0622d-6232-73fd-b962-10ca3a478053", "description": "被拒文案", "banner": "images/b044.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"该活动已存在周期推荐","path":"/api/admin/featured-cycle-items/01a0622d-6237-733d-85f4-0425587ec109"}
```

## Step 3: GET CA 与 CB 详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-6237-733d-85f4-0425587ec109" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-6237-733d-85f4-0425587ec109","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":1,"online":true,"targetId":"01a0622d-622d-7d96-9b1f-51841a6ccdcb","relatedTitle":"act-m9p25","title":null,"subtitle":null,"description":"CA 文案","note":null,"banner":{"id":"bound/b-bn-m9p27.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p27.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.567176Z","updatedAt":"2026-09-02T12:52:16.567176Z"}
```
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-623c-7bc4-831a-8484ff11aa25" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-623c-7bc4-831a-8484ff11aa25","phases":["LUTEAL"],"type":"ACTIVITY","sortOrder":2,"online":true,"targetId":"01a0622d-6232-73fd-b962-10ca3a478053","relatedTitle":"act-m9p26","title":null,"subtitle":null,"description":"CB 文案","note":null,"banner":{"id":"bound/b-bn-m9p28.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p28.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.572708Z","updatedAt":"2026-09-02T12:52:16.572708Z"}
```
