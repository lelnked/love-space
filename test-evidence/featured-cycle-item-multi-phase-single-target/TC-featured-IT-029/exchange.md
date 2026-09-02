# TC-featured-IT-029 GET /api/app/featured-cycle-items?period= 过滤后 period 数组仍含其他周期 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 活动 A 的唯一条目 01a0622f-dbf5-7319-8188-3d9cf229c0b2，phases=["MENSTRUAL","LUTEAL"]，上线

## Step 2: GET ?period=LUTEAL
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=LUTEAL" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622f-dbf5-7319-8188-3d9cf229c0b2","period":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9pf43c3.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf43c3.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622f-dbf1-7205-a6f3-1d8a868b7149","target":{"id":"01a0622f-dbf1-7205-a6f3-1d8a868b7149","title":"act-m9pf43c2","subtitle":null,"cover":{"id":"bound/act-m9pf43c2.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9pf43c2.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null}]
```

## Step 3: GET ?period=MENSTRUAL
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622f-dbf5-7319-8188-3d9cf229c0b2","period":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9pf43c3.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf43c3.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622f-dbf1-7205-a6f3-1d8a868b7149","target":{"id":"01a0622f-dbf1-7205-a6f3-1d8a868b7149","title":"act-m9pf43c2","subtitle":null,"cover":{"id":"bound/act-m9pf43c2.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9pf43c2.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null}]
```
