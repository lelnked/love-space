# TC-featured-IT-031 GET /api/app/featured-cycle-items 下线条目整条不下发 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 上线活动 A=01a0622f-dc35-7978-b3b8-288a6fa43268；phases=["MENSTRUAL","LUTEAL"] 的 ACTIVITY 条目 01a0622f-dc39-7b34-94b1-0d267b13bb0c 先置下线

## Step 2: GET（不带参数）
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[]
```

## Step 3: 条目上线后 GET

### admin: 条目上线
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-cycle-items/01a0622f-dc39-7b34-94b1-0d267b13bb0c/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": true}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622f-dc39-7b34-94b1-0d267b13bb0c","phases":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","sortOrder":0,"online":true,"targetId":"01a0622f-dc35-7978-b3b8-288a6fa43268","relatedTitle":"act-m9pf43c6","title":null,"subtitle":null,"description":"推荐说明","note":null,"banner":{"id":"bound/b-bn-m9pf43c7.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b-bn-m9pf43c7.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:54:58.873679Z","updatedAt":"2026-09-02T12:54:58.873679Z"}
```
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622f-dc39-7b34-94b1-0d267b13bb0c","period":["MENSTRUAL","LUTEAL"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9pf43c7.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf43c7.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622f-dc35-7978-b3b8-288a6fa43268","target":{"id":"01a0622f-dc35-7978-b3b8-288a6fa43268","title":"act-m9pf43c6","subtitle":null,"cover":{"id":"bound/act-m9pf43c6.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9pf43c6.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null}]
```

## Step 4: 活动 A 下线后 GET

### admin: 活动下线
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a0622f-dc35-7978-b3b8-288a6fa43268/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": false}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622f-dc35-7978-b3b8-288a6fa43268","images":[{"id":"bound/act-m9pf43c6.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act-m9pf43c6.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act-m9pf43c6","subtitle":null,"tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":false,"createdAt":"2026-09-02T12:54:58.869571Z","updatedAt":"2026-09-02T12:54:58.869571Z"}
```
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[]
```
