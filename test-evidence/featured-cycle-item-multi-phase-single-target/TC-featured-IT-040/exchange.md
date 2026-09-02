# TC-featured-IT-040 POST /api/admin/featured-cycle-items phases 为空或缺省被拒绝 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: 已存在多周期条目 01a0622d-611e-75f1-9fe0-a1fa565a8e12（phases=["MENSTRUAL","OVULATION"]）

### 基线分页
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=0&size=200" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[{"id":"01a0622d-611e-75f1-9fe0-a1fa565a8e12","phases":["MENSTRUAL","OVULATION"],"type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a0622d-6119-7386-a8a7-33db49daf542","relatedTitle":"act-m9p10","title":null,"subtitle":null,"description":"原说明","note":null,"banner":{"id":"bound/b-bn-m9p11.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p11.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.286326Z","updatedAt":"2026-09-02T12:52:16.286326Z"}],"page":1,"size":30,"totalElements":1,"totalPages":1}
```

## Step 2: phases=[] 空数组
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"type": "ACTIVITY", "targetId": "01a0622d-6114-7fb3-a67a-f83f65f76ea2", "description": "D", "banner": "images/x.png", "phases": []}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"投放周期不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 3: 完全不带 phases 字段
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"type": "ACTIVITY", "targetId": "01a0622d-6114-7fb3-a67a-f83f65f76ea2", "description": "D", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"投放周期不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 4: phases=null
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"type": "ACTIVITY", "targetId": "01a0622d-6114-7fb3-a67a-f83f65f76ea2", "description": "D", "banner": "images/x.png", "phases": null}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"投放周期不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 5: PUT 已存在条目 phases=[]
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-611e-75f1-9fe0-a1fa565a8e12" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": [], "type": "ACTIVITY", "targetId": "01a0622d-6119-7386-a8a7-33db49daf542", "description": "改后说明", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"投放周期不能为空","path":"/api/admin/featured-cycle-items/01a0622d-611e-75f1-9fe0-a1fa565a8e12"}
```

## Step 6: 复核分页总数与被 PUT 条目详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=0&size=200" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[{"id":"01a0622d-611e-75f1-9fe0-a1fa565a8e12","phases":["MENSTRUAL","OVULATION"],"type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a0622d-6119-7386-a8a7-33db49daf542","relatedTitle":"act-m9p10","title":null,"subtitle":null,"description":"原说明","note":null,"banner":{"id":"bound/b-bn-m9p11.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p11.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.286326Z","updatedAt":"2026-09-02T12:52:16.286326Z"}],"page":1,"size":30,"totalElements":1,"totalPages":1}
```
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-611e-75f1-9fe0-a1fa565a8e12" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-611e-75f1-9fe0-a1fa565a8e12","phases":["MENSTRUAL","OVULATION"],"type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a0622d-6119-7386-a8a7-33db49daf542","relatedTitle":"act-m9p10","title":null,"subtitle":null,"description":"原说明","note":null,"banner":{"id":"bound/b-bn-m9p11.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9p11.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.286326Z","updatedAt":"2026-09-02T12:52:16.286326Z"}
```
