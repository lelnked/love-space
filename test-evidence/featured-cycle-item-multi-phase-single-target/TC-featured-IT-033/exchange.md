# TC-featured-IT-033 POST /api/admin/featured-cycle-items 缺 targetId 被拒绝 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: 已存在条目 01a0622d-60a7-70f6-87db-67fbee57ff1b（phases=["MENSTRUAL","LUTEAL"]，关联活动 01a0622d-60a0-7e87-a43d-6e7e26e8f2b2）

### 基线分页
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=0&size=200" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[{"id":"01a0622d-60a7-70f6-87db-67fbee57ff1b","phases":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","sortOrder":3,"online":true,"targetId":"01a0622d-60a0-7e87-a43d-6e7e26e8f2b2","relatedTitle":"act-m9p1","title":null,"subtitle":null,"description":"原说明","note":null,"banner":{"id":"bound/b-bn-m9p2.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p2.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.167028Z","updatedAt":"2026-09-02T12:52:16.167028Z"}],"page":1,"size":30,"totalElements":1,"totalPages":1}
```

## Step 2: ACTIVITY 无 targetId
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ACTIVITY", "description": "D", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"关联实体不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 3: ROUTE 无 targetId
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["OVULATION"], "type": "ROUTE", "title": "T", "subtitle": "S", "description": "D", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"关联实体不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 4: ARTICLE 无 targetId
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["LUTEAL"], "type": "ARTICLE", "title": "T", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"关联实体不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 5: PUT targetId=null
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-60a7-70f6-87db-67fbee57ff1b" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL", "LUTEAL"], "type": "ACTIVITY", "targetId": null, "description": "新说明", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"关联实体不能为空","path":"/api/admin/featured-cycle-items/01a0622d-60a7-70f6-87db-67fbee57ff1b"}
```

## Step 6: 复核分页总数与被 PUT 条目详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=0&size=200" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[{"id":"01a0622d-60a7-70f6-87db-67fbee57ff1b","phases":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","sortOrder":3,"online":true,"targetId":"01a0622d-60a0-7e87-a43d-6e7e26e8f2b2","relatedTitle":"act-m9p1","title":null,"subtitle":null,"description":"原说明","note":null,"banner":{"id":"bound/b-bn-m9p2.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p2.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.167028Z","updatedAt":"2026-09-02T12:52:16.167028Z"}],"page":1,"size":30,"totalElements":1,"totalPages":1}
```
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-60a7-70f6-87db-67fbee57ff1b" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-60a7-70f6-87db-67fbee57ff1b","phases":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","sortOrder":3,"online":true,"targetId":"01a0622d-60a0-7e87-a43d-6e7e26e8f2b2","relatedTitle":"act-m9p1","title":null,"subtitle":null,"description":"原说明","note":null,"banner":{"id":"bound/b-bn-m9p2.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p2.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.167028Z","updatedAt":"2026-09-02T12:52:16.167028Z"}
```
