# TC-featured-IT-018 GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 上架城市 01a0622e-eb43-7ec3-9742-c62457ebed84 下路线 01a0622e-eb4d-7bc8-9459-1339f7e281f9（大使 01a0622e-eb48-774d-9675-3e26dd068f88 online=true），OVULATION 上线 ROUTE 条目 01a0622e-eb54-71e0-ac1e-5381cf9451f7

## Step 2: GET
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-eb54-71e0-ac1e-5381cf9451f7","period":["OVULATION"],"type":"ROUTE","banner":{"id":"bound/b-bn-m9pf90c4.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c4.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-eb4d-7bc8-9459-1339f7e281f9","target":{"id":"01a0622e-eb4d-7bc8-9459-1339f7e281f9","title":"route-m9pf90c3","thumbnail":{"id":"bound/route-m9pf90c3-t.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/route-m9pf90c3-t.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"cityName":"city-m9pf90c1","ambassadorName":"amb-m9pf90c2"},"title":"主标题-m9pf90c5","subtitle":"副标题","description":"推荐说明","note":null}]
```

## Step 3: 大使下线后 GET

### admin: 大使下线
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a0622e-eb48-774d-9675-3e26dd068f88/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": false}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622e-eb48-774d-9675-3e26dd068f88","avatar":{"id":"bound/amb-m9pf90c2.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb-m9pf90c2.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"name":"amb-m9pf90c2","tags":["向导"],"weight":0,"online":false,"createdAt":"2026-09-02T12:53:57.192432Z","updatedAt":"2026-09-02T12:53:57.192432Z"}
```
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[]
```

## Step 4: 恢复大使上线后 GET

### admin: 大使上线
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a0622e-eb48-774d-9675-3e26dd068f88/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": true}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622e-eb48-774d-9675-3e26dd068f88","avatar":{"id":"bound/amb-m9pf90c2.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/amb-m9pf90c2.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"name":"amb-m9pf90c2","tags":["向导"],"weight":0,"online":true,"createdAt":"2026-09-02T12:53:57.192432Z","updatedAt":"2026-09-02T12:53:57.225524Z"}
```
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-eb54-71e0-ac1e-5381cf9451f7","period":["OVULATION"],"type":"ROUTE","banner":{"id":"bound/b-bn-m9pf90c4.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c4.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-eb4d-7bc8-9459-1339f7e281f9","target":{"id":"01a0622e-eb4d-7bc8-9459-1339f7e281f9","title":"route-m9pf90c3","thumbnail":{"id":"bound/route-m9pf90c3-t.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/route-m9pf90c3-t.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"cityName":"city-m9pf90c1","ambassadorName":"amb-m9pf90c2"},"title":"主标题-m9pf90c5","subtitle":"副标题","description":"推荐说明","note":null}]
```
