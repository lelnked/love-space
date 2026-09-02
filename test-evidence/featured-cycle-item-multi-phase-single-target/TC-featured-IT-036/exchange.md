# TC-featured-IT-036 GET /api/app/featured-cycle-items 文章类条目下发文章基础信息 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 上线文章（title=art-m9pf43c20、coverTitle=封面标题036、image），LUTEAL 上线 ARTICLE 条目 01a0622f-dcdb-72d7-911d-e8eb656e8338

## Step 2: GET ?type=ARTICLE
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?type=ARTICLE" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622f-dcdb-72d7-911d-e8eb656e8338","period":["LUTEAL"],"type":"ARTICLE","banner":{"id":"bound/b-bn-m9pf43c21.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf43c21.png?Expires=1788355499&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622f-dcd7-7496-a019-762e8efbe4dd","target":{"id":"01a0622f-dcd7-7496-a019-762e8efbe4dd","title":"art-m9pf43c20","coverTitle":"封面标题036","image":{"id":"bound/art-m9pf43c20.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/art-m9pf43c20.png?Expires=1788355499&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}},"title":"文章主标题-m9pf43c22","subtitle":null,"description":null,"note":null}]
```
