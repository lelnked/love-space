# TC-featured-IT-041 POST /api/admin/featured-cycle-items 同一关联实体重复创建被拒绝 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 2: 前置——对活动 A 创建 phases=["MENSTRUAL"] 的 ACTIVITY 条目
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ACTIVITY", "targetId": "01a0622d-6144-7a53-8649-a2ac06a50c61", "description": "D", "banner": "images/b041.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-615c-7a2f-95ea-cf9f3f6ca637","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622d-6144-7a53-8649-a2ac06a50c61","relatedTitle":"act-m9p12","title":null,"subtitle":null,"description":"D","note":null,"banner":{"id":"bound/b041.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b041.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.348606058Z","updatedAt":"2026-09-02T12:52:16.348606058Z"}
```
另前置：路线 01a0622d-6152-7abc-b305-32c93e31f7a3 的条目 01a0622d-6162-7f8f-b4eb-c1df9d45ccd0、文章 01a0622d-6157-7940-88ac-7883523453d0 的条目 01a0622d-6168-7640-8baa-ff83e1f4f975（经同一接口创建，未入存证）

### 基线分页
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=0&size=200" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[{"id":"01a0622d-6168-7640-8baa-ff83e1f4f975","phases":["LUTEAL"],"type":"ARTICLE","sortOrder":0,"online":true,"targetId":"01a0622d-6157-7940-88ac-7883523453d0","relatedTitle":"art-m9p16","title":"文章主标题-m9p20","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b-bn-m9p19.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p19.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.360303Z","updatedAt":"2026-09-02T12:52:16.360303Z"},{"id":"01a0622d-6162-7f8f-b4eb-c1df9d45ccd0","phases":["OVULATION"],"type":"ROUTE","sortOrder":0,"online":true,"targetId":"01a0622d-6152-7abc-b305-32c93e31f7a3","relatedTitle":"route-m9p15","title":"主标题-m9p18","subtitle":"副标题","description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p17.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p17.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.354941Z","updatedAt":"2026-09-02T12:52:16.354941Z"},{"id":"01a0622d-615c-7a2f-95ea-cf9f3f6ca637","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622d-6144-7a53-8649-a2ac06a50c61","relatedTitle":"act-m9p12","title":null,"subtitle":null,"description":"D","note":null,"banner":{"id":"bound/b041.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b041.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.348606Z","updatedAt":"2026-09-02T12:52:16.348606Z"}],"page":1,"size":30,"totalElements":3,"totalPages":1}
```

## Step 3: 对活动 A 再 POST（phases=["LUTEAL"]）
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["LUTEAL"], "type": "ACTIVITY", "targetId": "01a0622d-6144-7a53-8649-a2ac06a50c61", "description": "D", "banner": "images/b041b.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"该活动已存在周期推荐","path":"/api/admin/featured-cycle-items"}
```

## Step 4a: 对已被引用的路线 R 再 POST
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ROUTE", "targetId": "01a0622d-6152-7abc-b305-32c93e31f7a3", "title": "T", "subtitle": "S", "description": "D", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"该路线已存在周期推荐","path":"/api/admin/featured-cycle-items"}
```

## Step 4b: 对已被引用的文章 T 再 POST
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ARTICLE", "targetId": "01a0622d-6157-7940-88ac-7883523453d0", "title": "T", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"该文章已存在周期推荐","path":"/api/admin/featured-cycle-items"}
```

## Step 5: 复核分页总数
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=0&size=200" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[{"id":"01a0622d-6168-7640-8baa-ff83e1f4f975","phases":["LUTEAL"],"type":"ARTICLE","sortOrder":0,"online":true,"targetId":"01a0622d-6157-7940-88ac-7883523453d0","relatedTitle":"art-m9p16","title":"文章主标题-m9p20","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b-bn-m9p19.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p19.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.360303Z","updatedAt":"2026-09-02T12:52:16.360303Z"},{"id":"01a0622d-6162-7f8f-b4eb-c1df9d45ccd0","phases":["OVULATION"],"type":"ROUTE","sortOrder":0,"online":true,"targetId":"01a0622d-6152-7abc-b305-32c93e31f7a3","relatedTitle":"route-m9p15","title":"主标题-m9p18","subtitle":"副标题","description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9p17.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p17.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.354941Z","updatedAt":"2026-09-02T12:52:16.354941Z"},{"id":"01a0622d-615c-7a2f-95ea-cf9f3f6ca637","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622d-6144-7a53-8649-a2ac06a50c61","relatedTitle":"act-m9p12","title":null,"subtitle":null,"description":"D","note":null,"banner":{"id":"bound/b041.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b041.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.348606Z","updatedAt":"2026-09-02T12:52:16.348606Z"}],"page":1,"size":30,"totalElements":3,"totalPages":1}
```
