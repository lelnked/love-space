# TC-featured-IT-035 GET /api/app/featured-cycle-items 路线类条目下发路线基础信息且不覆盖手填文案 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 上线大使 amb-m9pf43c15、路线 01a0622f-dcbb-739e-bc59-1d68dfe796be（含缩略图、cityName=city-m9pf43c14、标题 T1=route-m9pf43c16）；OVULATION 上线 ROUTE 条目手填 T2=条目手填主标题-t-m9pf43c17

## Step 2: GET ?type=ROUTE
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?type=ROUTE" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622f-dcc0-70ef-b13b-caeda3c78219","period":["OVULATION"],"type":"ROUTE","banner":{"id":"bound/b-bn-m9pf43c18.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf43c18.png?Expires=1788355499&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622f-dcbb-739e-bc59-1d68dfe796be","target":{"id":"01a0622f-dcbb-739e-bc59-1d68dfe796be","title":"route-m9pf43c16","thumbnail":{"id":"bound/route-m9pf43c16-t.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/route-m9pf43c16-t.png?Expires=1788355499&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"cityName":"city-m9pf43c14","ambassadorName":"amb-m9pf43c15"},"title":"条目手填主标题-t-m9pf43c17","subtitle":"副标题","description":"推荐说明","note":null}]
```
