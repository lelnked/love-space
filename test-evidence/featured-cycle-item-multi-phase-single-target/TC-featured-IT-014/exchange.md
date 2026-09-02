# TC-featured-IT-014 PUT /api/admin/featured-cycle-items/{id}/online 上下线切换 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: online=true 的条目 01a0622c-3438-7b8e-beb9-3f8fa8c72887

## Step 2: PUT online=false
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-3438-7b8e-beb9-3f8fa8c72887/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": false}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-3438-7b8e-beb9-3f8fa8c72887","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622c-3433-74eb-ac7e-d06a2741e150","relatedTitle":"act-m9p23","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p24.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p24.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.256667Z","updatedAt":"2026-09-02T12:50:59.256667Z"}
```

## Step 3: GET 详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-3438-7b8e-beb9-3f8fa8c72887" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-3438-7b8e-beb9-3f8fa8c72887","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622c-3433-74eb-ac7e-d06a2741e150","relatedTitle":"act-m9p23","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p24.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p24.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.256667Z","updatedAt":"2026-09-02T12:50:59.26518Z"}
```

## Step 4: PUT online=true 后复查
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-3438-7b8e-beb9-3f8fa8c72887/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": true}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-3438-7b8e-beb9-3f8fa8c72887","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a0622c-3433-74eb-ac7e-d06a2741e150","relatedTitle":"act-m9p23","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p24.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p24.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.256667Z","updatedAt":"2026-09-02T12:50:59.26518Z"}
```
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-3438-7b8e-beb9-3f8fa8c72887" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-3438-7b8e-beb9-3f8fa8c72887","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a0622c-3433-74eb-ac7e-d06a2741e150","relatedTitle":"act-m9p23","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p24.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p24.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.256667Z","updatedAt":"2026-09-02T12:50:59.274634Z"}
```
