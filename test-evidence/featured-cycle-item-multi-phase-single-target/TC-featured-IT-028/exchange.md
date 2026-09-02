# TC-featured-IT-028 GET /api/app/featured-cycle-items 多周期条目在 period 数组中下发全部周期 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 上线活动 A=01a0622f-dbd1-78f8-ba5d-425cd6740c0f；创建一条 phases=["MENSTRUAL","LUTEAL"] 的上线 ACTIVITY 条目
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL", "LUTEAL"], "type": "ACTIVITY", "targetId": "01a0622f-dbd1-78f8-ba5d-425cd6740c0f", "description": "多周期说明", "banner": "images/b028.png", "online": true}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622f-dbd6-7651-ba5e-bc4b304d69b7","phases":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a0622f-dbd1-78f8-ba5d-425cd6740c0f","relatedTitle":"act-m9pf43c1","title":null,"subtitle":null,"description":"多周期说明","note":null,"banner":{"id":"bound/b028.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b028.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:54:58.774367232Z","updatedAt":"2026-09-02T12:54:58.774367232Z"}
```

## Step 2: GET（不带参数）
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622f-dbd6-7651-ba5e-bc4b304d69b7","period":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","banner":{"id":"bound/b028.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b028.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622f-dbd1-78f8-ba5d-425cd6740c0f","target":{"id":"01a0622f-dbd1-78f8-ba5d-425cd6740c0f","title":"act-m9pf43c1","subtitle":null,"cover":{"id":"bound/act-m9pf43c1.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9pf43c1.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"多周期说明","note":null}]
```
