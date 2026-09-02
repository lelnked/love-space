# TC-featured-IT-046 PUT /api/admin/featured-cycle-items/{id} 更新关联实体 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 活动 A=01a0622d-6204-7dad-9509-6a6e01d9f011、活动 B=01a0622d-6208-7da5-b0f3-eed02674d621（B 未被引用）；对 A 创建 ACTIVITY 条目
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ACTIVITY", "targetId": "01a0622d-6204-7dad-9509-6a6e01d9f011", "description": "D", "banner": "images/b046.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-620d-78f3-8188-40e667d19d62","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622d-6204-7dad-9509-6a6e01d9f011","relatedTitle":"act-m9p23","title":null,"subtitle":null,"description":"D","note":null,"banner":{"id":"bound/b046.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b046.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.52553193Z","updatedAt":"2026-09-02T12:52:16.52553193Z"}
```

## Step 2: PUT targetId 改为活动 B
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-620d-78f3-8188-40e667d19d62" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ACTIVITY", "targetId": "01a0622d-6208-7da5-b0f3-eed02674d621", "description": "D", "banner": "images/b046.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-620d-78f3-8188-40e667d19d62","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622d-6208-7da5-b0f3-eed02674d621","relatedTitle":"act-m9p24","title":null,"subtitle":null,"description":"D","note":null,"banner":{"id":"bound/b046.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b046.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.525532Z","updatedAt":"2026-09-02T12:52:16.525532Z"}
```

### Step 2: GET 详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-620d-78f3-8188-40e667d19d62" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-620d-78f3-8188-40e667d19d62","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622d-6208-7da5-b0f3-eed02674d621","relatedTitle":"act-m9p24","title":null,"subtitle":null,"description":"D","note":null,"banner":{"id":"bound/b046.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b046.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.525532Z","updatedAt":"2026-09-02T12:52:16.533738Z"}
```

## Step 3: 新建一条关联活动 A 的条目
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["LUTEAL"], "type": "ACTIVITY", "targetId": "01a0622d-6204-7dad-9509-6a6e01d9f011", "description": "D", "banner": "images/b046b.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-621d-76a2-98be-3a61c2311020","phases":["LUTEAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622d-6204-7dad-9509-6a6e01d9f011","relatedTitle":"act-m9p23","title":null,"subtitle":null,"description":"D","note":null,"banner":{"id":"bound/b046b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b046b.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.541380397Z","updatedAt":"2026-09-02T12:52:16.541380397Z"}
```
