# TC-featured-IT-025 GET /api/app/featured-cycle-items?period=&type= 周期与类型同时过滤 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: 含 MENSTRUAL 的 ACTIVITY 与 ARTICLE 条目各 1，另 1 个 FOLLICULAR 的 ARTICLE 条目

## Step 2: GET ?period=MENSTRUAL&type=ARTICLE
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL&type=ARTICLE" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-ed02-705a-ad2e-03a6c6d2a576","period":["MENSTRUAL","LUTEAL"],"type":"ARTICLE","banner":{"id":"bound/b-bn-m9pf90c47.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c47.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-ecf5-78f8-856d-376f1f7becb8","target":{"id":"01a0622e-ecf5-78f8-856d-376f1f7becb8","title":"art-m9pf90c44","coverTitle":null,"image":{"id":"bound/art-m9pf90c44.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/art-m9pf90c44.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}},"title":"文章主标题-m9pf90c48","subtitle":null,"description":null,"note":null}]
```
