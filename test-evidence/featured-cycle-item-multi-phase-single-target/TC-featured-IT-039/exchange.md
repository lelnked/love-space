# TC-featured-IT-039 POST /api/admin/featured-cycle-items 创建多周期条目 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: 两条未被引用的路线 01a0622d-60d8-708b-8e27-746792c0f2bc / 01a0622d-60e6-79d8-a5b4-ed1223b81053（大使上线）

## Step 3: POST phases=["LUTEAL","MENSTRUAL"]（乱序传入）
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["LUTEAL", "MENSTRUAL"], "type": "ROUTE", "targetId": "01a0622d-60d8-708b-8e27-746792c0f2bc", "title": "多周期主标题", "subtitle": "副标题", "description": "说明", "banner": "images/b039.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-60ed-7a75-a960-4b35d10193e0","phases":["MENSTRUAL","LUTEAL"],"type":"ROUTE","sortOrder":0,"online":false,"targetId":"01a0622d-60d8-708b-8e27-746792c0f2bc","relatedTitle":"route-m9p5","title":"多周期主标题","subtitle":"副标题","description":"说明","note":null,"banner":{"id":"bound/b039.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b039.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.237616021Z","updatedAt":"2026-09-02T12:52:16.237616021Z"}
```

## Step 4: GET 详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-60ed-7a75-a960-4b35d10193e0" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-60ed-7a75-a960-4b35d10193e0","phases":["MENSTRUAL","LUTEAL"],"type":"ROUTE","sortOrder":0,"online":false,"targetId":"01a0622d-60d8-708b-8e27-746792c0f2bc","relatedTitle":"route-m9p5","title":"多周期主标题","subtitle":"副标题","description":"说明","note":null,"banner":{"id":"bound/b039.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b039.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.237616Z","updatedAt":"2026-09-02T12:52:16.237616Z"}
```

## Step 5: GET page?phase=MENSTRUAL
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?phase=MENSTRUAL&page=0&size=50" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[{"id":"01a0622d-60ed-7a75-a960-4b35d10193e0","phases":["MENSTRUAL","LUTEAL"],"type":"ROUTE","sortOrder":0,"online":false,"targetId":"01a0622d-60d8-708b-8e27-746792c0f2bc","relatedTitle":"route-m9p5","title":"多周期主标题","subtitle":"副标题","description":"说明","note":null,"banner":{"id":"bound/b039.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b039.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.237616Z","updatedAt":"2026-09-02T12:52:16.237616Z"}],"page":1,"size":30,"totalElements":1,"totalPages":1}
```

## Step 6: GET page?phase=LUTEAL
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?phase=LUTEAL&page=0&size=50" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[{"id":"01a0622d-60ed-7a75-a960-4b35d10193e0","phases":["MENSTRUAL","LUTEAL"],"type":"ROUTE","sortOrder":0,"online":false,"targetId":"01a0622d-60d8-708b-8e27-746792c0f2bc","relatedTitle":"route-m9p5","title":"多周期主标题","subtitle":"副标题","description":"说明","note":null,"banner":{"id":"bound/b039.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b039.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.237616Z","updatedAt":"2026-09-02T12:52:16.237616Z"}],"page":1,"size":30,"totalElements":1,"totalPages":1}
```

## Step 7: POST phases=["MENSTRUAL","MENSTRUAL","LUTEAL"]（含重复值）
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL", "MENSTRUAL", "LUTEAL"], "type": "ROUTE", "targetId": "01a0622d-60e6-79d8-a5b4-ed1223b81053", "title": "去重主标题", "subtitle": "副标题", "description": "说明", "banner": "images/b039b.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-6101-73d7-ab9a-f3cb5b94ef9a","phases":["MENSTRUAL","LUTEAL"],"type":"ROUTE","sortOrder":0,"online":false,"targetId":"01a0622d-60e6-79d8-a5b4-ed1223b81053","relatedTitle":"route-m9p8","title":"去重主标题","subtitle":"副标题","description":"说明","note":null,"banner":{"id":"bound/b039b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b039b.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.257208809Z","updatedAt":"2026-09-02T12:52:16.257208809Z"}
```

### Step 7: GET 详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-6101-73d7-ab9a-f3cb5b94ef9a" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-6101-73d7-ab9a-f3cb5b94ef9a","phases":["MENSTRUAL","LUTEAL"],"type":"ROUTE","sortOrder":0,"online":false,"targetId":"01a0622d-60e6-79d8-a5b4-ed1223b81053","relatedTitle":"route-m9p8","title":"去重主标题","subtitle":"副标题","description":"说明","note":null,"banner":{"id":"bound/b039b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b039b.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.257209Z","updatedAt":"2026-09-02T12:52:16.257209Z"}
```
