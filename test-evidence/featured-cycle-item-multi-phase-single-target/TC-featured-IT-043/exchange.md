# TC-featured-IT-043 PUT /api/admin/featured-cycle-items/{id} 更新条目自身不触发唯一冲突 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 对活动 A 创建 phases=["MENSTRUAL"]、type=ACTIVITY 条目
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ACTIVITY", "targetId": "01a0622d-61de-757c-be5e-dddacc479c5b", "description": "原文案", "banner": "images/b043.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-61e8-7804-aa1c-9e4a18c9bc7c","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622d-61de-757c-be5e-dddacc479c5b","relatedTitle":"act-m9p22","title":null,"subtitle":null,"description":"原文案","note":null,"banner":{"id":"bound/b043.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b043.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.488470287Z","updatedAt":"2026-09-02T12:52:16.488470287Z"}
```

## Step 2: PUT targetId 仍为活动 A、phases=["OVULATION","LUTEAL"]、description 改新文案
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-61e8-7804-aa1c-9e4a18c9bc7c" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["OVULATION", "LUTEAL"], "type": "ACTIVITY", "targetId": "01a0622d-61de-757c-be5e-dddacc479c5b", "description": "新文案", "banner": "images/b043.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-61e8-7804-aa1c-9e4a18c9bc7c","phases":["OVULATION","LUTEAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622d-61de-757c-be5e-dddacc479c5b","relatedTitle":"act-m9p22","title":null,"subtitle":null,"description":"新文案","note":null,"banner":{"id":"bound/b043.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b043.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.48847Z","updatedAt":"2026-09-02T12:52:16.494549392Z"}
```

## Step 3: GET 详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-61e8-7804-aa1c-9e4a18c9bc7c" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-61e8-7804-aa1c-9e4a18c9bc7c","phases":["OVULATION","LUTEAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622d-61de-757c-be5e-dddacc479c5b","relatedTitle":"act-m9p22","title":null,"subtitle":null,"description":"新文案","note":null,"banner":{"id":"bound/b043.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b043.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.48847Z","updatedAt":"2026-09-02T12:52:16.496417Z"}
```

## Step 4: 幂等重复提交同一 body
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a0622d-61e8-7804-aa1c-9e4a18c9bc7c" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["OVULATION", "LUTEAL"], "type": "ACTIVITY", "targetId": "01a0622d-61de-757c-be5e-dddacc479c5b", "description": "新文案", "banner": "images/b043.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622d-61e8-7804-aa1c-9e4a18c9bc7c","phases":["OVULATION","LUTEAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622d-61de-757c-be5e-dddacc479c5b","relatedTitle":"act-m9p22","title":null,"subtitle":null,"description":"新文案","note":null,"banner":{"id":"bound/b043.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b043.png?Expires=1788355336&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:52:16.48847Z","updatedAt":"2026-09-02T12:52:16.496417Z"}
```
