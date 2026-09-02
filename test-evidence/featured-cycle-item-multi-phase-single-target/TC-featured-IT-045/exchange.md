# TC-featured-IT-045 GET /api/admin/featured-cycle-items/page 不传周期返回全部条目 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: 3 条条目（sortOrder 1 / 0 / 1，按此顺序创建）
i1=01a0622d-6271-727d-8637-31ad84277b22(MENSTRUAL,so=1) i2=01a0622d-6276-7a95-8c1f-cdfdbd1bf6c9(FOLLICULAR+LUTEAL,so=0) i3=01a0622d-627d-77c5-a7c9-d15ebc82c254(OVULATION,so=1)

## Step 2: GET page（不带 phase 参数）
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=0&size=50" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[{"id":"01a0622d-6276-7a95-8c1f-cdfdbd1bf6c9","phases":["FOLLICULAR","LUTEAL"],"type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a0622d-6265-78c6-8cca-37402f4c9eb4","relatedTitle":"act-m9p30","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p33.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p33.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.630629Z","updatedAt":"2026-09-02T12:52:16.630629Z"},{"id":"01a0622d-627d-77c5-a7c9-d15ebc82c254","phases":["OVULATION"],"type":"ACTIVITY","sortOrder":1,"online":true,"targetId":"01a0622d-626a-77b2-999b-09ad97305257","relatedTitle":"act-m9p31","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p34.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p34.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.637322Z","updatedAt":"2026-09-02T12:52:16.637322Z"},{"id":"01a0622d-6271-727d-8637-31ad84277b22","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":1,"online":true,"targetId":"01a0622d-625f-7014-a24c-66d6c15db282","relatedTitle":"act-m9p29","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p32.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p32.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.625127Z","updatedAt":"2026-09-02T12:52:16.625127Z"}],"page":1,"size":30,"totalElements":3,"totalPages":1}
```
