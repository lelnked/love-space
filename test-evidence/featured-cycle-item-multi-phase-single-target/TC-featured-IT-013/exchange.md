# TC-featured-IT-013 GET /api/admin/featured-cycle-items/page phase 参数按「包含」过滤并按排序号升序 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: X1(FOLLICULAR,so=2) X2(FOLLICULAR+LUTEAL,so=1) X3(FOLLICULAR,so=3) Y(MENSTRUAL)
X1=01a0622c-33ee-7f44-b942-ea049997b6e6 X2=01a0622c-33f5-7a94-98f3-fc6cbb2c34c5 X3=01a0622c-33fc-7958-aa5f-4ff42e09536a Y=01a0622c-3402-7c0a-b79c-a9e99d66bbbb

## Step 2: GET page?phase=FOLLICULAR
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?phase=FOLLICULAR&page=0&size=50" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[{"id":"01a0622c-33f5-7a94-98f3-fc6cbb2c34c5","phases":["FOLLICULAR","LUTEAL"],"type":"ACTIVITY","sortOrder":1,"online":true,"targetId":"01a0622c-33de-7ffe-9816-ca2c731197d2","relatedTitle":"act-m9p16","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p20.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p20.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.189614Z","updatedAt":"2026-09-02T12:50:59.189614Z"},{"id":"01a0622c-33ee-7f44-b942-ea049997b6e6","phases":["FOLLICULAR"],"type":"ACTIVITY","sortOrder":2,"online":true,"targetId":"01a0622c-33d9-7ad0-9221-da8166753a9c","relatedTitle":"act-m9p15","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p19.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p19.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.182915Z","updatedAt":"2026-09-02T12:50:59.182915Z"},{"id":"01a0622c-33fc-7958-aa5f-4ff42e09536a","phases":["FOLLICULAR"],"type":"ACTIVITY","sortOrder":3,"online":true,"targetId":"01a0622c-33e4-7936-ad52-1d6fa44cc4e5","relatedTitle":"act-m9p17","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p21.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p21.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.196551Z","updatedAt":"2026-09-02T12:50:59.196551Z"}],"page":1,"size":30,"totalElements":3,"totalPages":1}
```

## Step 3: GET page?phase=LUTEAL
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?phase=LUTEAL&page=0&size=50" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[{"id":"01a0622c-33f5-7a94-98f3-fc6cbb2c34c5","phases":["FOLLICULAR","LUTEAL"],"type":"ACTIVITY","sortOrder":1,"online":true,"targetId":"01a0622c-33de-7ffe-9816-ca2c731197d2","relatedTitle":"act-m9p16","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p20.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p20.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.189614Z","updatedAt":"2026-09-02T12:50:59.189614Z"}],"page":1,"size":30,"totalElements":1,"totalPages":1}
```

## Step 4: GET page?phase=MENSTRUAL
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?phase=MENSTRUAL&page=0&size=50" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[{"id":"01a0622c-3402-7c0a-b79c-a9e99d66bbbb","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":5,"online":true,"targetId":"01a0622c-33e9-717d-b913-b2d966432521","relatedTitle":"act-m9p18","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p22.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p22.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.202702Z","updatedAt":"2026-09-02T12:50:59.202702Z"}],"page":1,"size":30,"totalElements":1,"totalPages":1}
```
