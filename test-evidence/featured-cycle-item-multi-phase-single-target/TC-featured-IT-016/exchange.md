# TC-featured-IT-016 GET /api/app/featured-cycle-items 扁平数组带 period 周期数组且只含上线条目 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置（admin 端建数据，各条关联不同实体）
上线 ACTIVITY 条目 01a0622e-c48a-7fa7-8ab5-a713d2c6ba06（活动 01a0622e-c47d-7d2c-abed-dc6c95d0000f 上线）、上线 ARTICLE 条目 01a0622e-c48f-7bd1-b9a7-1addd03d1755（文章 01a0622e-c481-7e1d-bac8-89642242d969 上线）、下线条目 01a0622e-c494-74c0-b950-b59cc5bbe6fc

## Step 2: GET /api/app/featured-cycle-items（不带参数）
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-c48f-7bd1-b9a7-1addd03d1755","period":["OVULATION"],"type":"ARTICLE","banner":{"id":"bound/b-bn-m9p5.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9p5.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-c481-7e1d-bac8-89642242d969","target":{"id":"01a0622e-c481-7e1d-bac8-89642242d969","title":"art-m9p2","coverTitle":null,"image":{"id":"bound/art-m9p2.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/art-m9p2.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}},"title":"文章主标题-m9p6","subtitle":null,"description":null,"note":null},{"id":"01a0622e-c48a-7fa7-8ab5-a713d2c6ba06","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9p4.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9p4.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-c47d-7d2c-abed-dc6c95d0000f","target":{"id":"01a0622e-c47d-7d2c-abed-dc6c95d0000f","title":"act-m9p1","subtitle":null,"cover":{"id":"bound/act-m9p1.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9p1.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null}]
```
