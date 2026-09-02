# TC-featured-IT-012 PUT /api/admin/featured-cycle-items/{id} phases 可改而 type 创建后不可变 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: phases=["MENSTRUAL"]、type=ACTIVITY 的条目 01a0622c-33b1-75e6-acb9-0bb074eb897a（关联活动 01a0622c-33a7-7175-82e2-cc13602b39ed）

## Step 2: PUT 传 phases=["FOLLICULAR","OVULATION"]、type=ARTICLE、title「改名」
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-33b1-75e6-acb9-0bb074eb897a" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["FOLLICULAR", "OVULATION"], "type": "ARTICLE", "targetId": "01a0622c-33a7-7175-82e2-cc13602b39ed", "title": "改名", "description": "新说明", "banner": "images/b012.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-33b1-75e6-acb9-0bb074eb897a","phases":["FOLLICULAR","OVULATION"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622c-33a7-7175-82e2-cc13602b39ed","relatedTitle":"act-m9p12","title":null,"subtitle":null,"description":"新说明","note":null,"banner":{"id":"bound/b012.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b012.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.121326Z","updatedAt":"2026-09-02T12:50:59.131911248Z"}
```

## Step 3: GET 详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-33b1-75e6-acb9-0bb074eb897a" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-33b1-75e6-acb9-0bb074eb897a","phases":["FOLLICULAR","OVULATION"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622c-33a7-7175-82e2-cc13602b39ed","relatedTitle":"act-m9p12","title":null,"subtitle":null,"description":"新说明","note":null,"banner":{"id":"bound/b012.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b012.png?Expires=1788355259&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:59.121326Z","updatedAt":"2026-09-02T12:50:59.13822Z"}
```

## Step 4: 再 PUT，targetId 改传文章 id（type 仍传 ARTICLE）
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-33b1-75e6-acb9-0bb074eb897a" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["FOLLICULAR", "OVULATION"], "type": "ARTICLE", "targetId": "01a0622c-33ab-7c20-aa16-ff6be2f7576f", "title": "改名", "description": "新说明", "banner": "images/b012.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"关联活动不存在：01a0622c-33ab-7c20-aa16-ff6be2f7576f","path":"/api/admin/featured-cycle-items/01a0622c-33b1-75e6-acb9-0bb074eb897a"}
```
