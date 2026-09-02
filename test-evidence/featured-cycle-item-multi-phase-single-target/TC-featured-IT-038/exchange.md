# TC-featured-IT-038 GET /api/app/featured-cycle-items 活动未填副标题时 target.subtitle 为 null — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 上线活动（含标题与图片，不填 subtitle）01a06230-2917-715e-9c69-578275dea9cf；OVULATION 上线 ACTIVITY 条目 01a06230-291b-7b7a-95f9-6ba8351754a0，description="限时开团"

## Step 2: GET ?type=ACTIVITY&period=OVULATION
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?type=ACTIVITY&period=OVULATION" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a06230-291b-7b7a-95f9-6ba8351754a0","period":["OVULATION"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9p158c4.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9p158c4.png?Expires=1788355518&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a06230-2917-715e-9c69-578275dea9cf","target":{"id":"01a06230-2917-715e-9c69-578275dea9cf","title":"act-m9p158c3","subtitle":null,"cover":{"id":"bound/act-m9p158c3.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9p158c3.png?Expires=1788355518&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"限时开团","note":null}]
```
