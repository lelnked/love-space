# TC-featured-IT-024 GET /api/app/featured-cycle-items?period= 按周期过滤 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: M1(MENSTRUAL/ACTIVITY) M2(MENSTRUAL/ARTICLE) F1(FOLLICULAR/ARTICLE)，各关联不同 target

## Step 2: GET ?period=MENSTRUAL
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-ecc2-72a3-b234-313286181059","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b-bn-m9pf90c39.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c39.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-ecb6-7ece-8c54-6331f330869c","target":{"id":"01a0622e-ecb6-7ece-8c54-6331f330869c","title":"art-m9pf90c36","coverTitle":null,"image":{"id":"bound/art-m9pf90c36.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/art-m9pf90c36.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}},"title":"文章主标题-m9pf90c40","subtitle":null,"description":null,"note":null},{"id":"01a0622e-ecbd-7d05-bb4b-40ffa1bc0fc1","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9pf90c38.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c38.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-ecb3-7fd7-8585-9fbcc1139fda","target":{"id":"01a0622e-ecb3-7fd7-8585-9fbcc1139fda","title":"act-m9pf90c35","subtitle":null,"cover":{"id":"bound/act-m9pf90c35.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9pf90c35.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null}]
```

## Step 3: GET ?period=FOLLICULAR
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=FOLLICULAR" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-ecc6-7547-97a1-db9d2b6a113a","period":["FOLLICULAR"],"type":"ARTICLE","banner":{"id":"bound/b-bn-m9pf90c41.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c41.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-ecb9-7c5a-b02b-166d374a178c","target":{"id":"01a0622e-ecb9-7c5a-b02b-166d374a178c","title":"art-m9pf90c37","coverTitle":null,"image":{"id":"bound/art-m9pf90c37.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/art-m9pf90c37.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}},"title":"文章主标题-m9pf90c42","subtitle":null,"description":null,"note":null}]
```
