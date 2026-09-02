# TC-featured-IT-042 POST /api/admin/featured-cycle-items 下线条目同样占用唯一位 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 对文章 T 创建 ARTICLE 条目并置 online=false
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["LUTEAL"], "type": "ARTICLE", "targetId": "01a0622d-6193-78fa-a44c-dd81610cfd3c", "title": "T1", "banner": "images/b042.png", "online": true}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-61a3-7d26-a7f9-38fff0883010","phases":["LUTEAL"],"type":"ARTICLE","sortOrder":0,"online":true,"targetId":"01a0622d-6193-78fa-a44c-dd81610cfd3c","relatedTitle":"art-m9p21","title":"T1","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b042.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b042.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.419717346Z","updatedAt":"2026-09-02T12:52:16.419717346Z"}
```
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-61a3-7d26-a7f9-38fff0883010/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": false}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-61a3-7d26-a7f9-38fff0883010","phases":["LUTEAL"],"type":"ARTICLE","sortOrder":0,"online":false,"targetId":"01a0622d-6193-78fa-a44c-dd81610cfd3c","relatedTitle":"art-m9p21","title":"T1","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b042.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b042.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.419717Z","updatedAt":"2026-09-02T12:52:16.419717Z"}
```

## Step 2: 对同一文章 T 再 POST（phases=["FOLLICULAR"]）
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["FOLLICULAR"], "type": "ARTICLE", "targetId": "01a0622d-6193-78fa-a44c-dd81610cfd3c", "title": "T2", "banner": "images/b042b.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"该文章已存在周期推荐","path":"/api/admin/featured-cycle-items"}
```

## Step 3: DELETE 下线条目后重试同一 POST
```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-61a3-7d26-a7f9-38fff0883010" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: -）
```json
(empty)
```
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["FOLLICULAR"], "type": "ARTICLE", "targetId": "01a0622d-6193-78fa-a44c-dd81610cfd3c", "title": "T2", "banner": "images/b042b.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-61b9-769d-b23c-b3a00b3edb69","phases":["FOLLICULAR"],"type":"ARTICLE","sortOrder":0,"online":false,"targetId":"01a0622d-6193-78fa-a44c-dd81610cfd3c","relatedTitle":"art-m9p21","title":"T2","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b042b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b042b.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.44138564Z","updatedAt":"2026-09-02T12:52:16.44138564Z"}
```

### Step 3: GET 详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-61b9-769d-b23c-b3a00b3edb69" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-61b9-769d-b23c-b3a00b3edb69","phases":["FOLLICULAR"],"type":"ARTICLE","sortOrder":0,"online":false,"targetId":"01a0622d-6193-78fa-a44c-dd81610cfd3c","relatedTitle":"art-m9p21","title":"T2","subtitle":null,"description":null,"note":null,"banner":{"id":"bound/b042b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b042b.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.441386Z","updatedAt":"2026-09-02T12:52:16.441386Z"}
```
