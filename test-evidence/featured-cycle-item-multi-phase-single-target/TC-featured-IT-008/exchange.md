# TC-featured-IT-008 POST /api/admin/featured-cycle-items 创建路线类周期推荐 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: 路线 01a0622c-3307-75d5-9d23-53ec7fae0afe（未被引用），主标题=route-m9p4
路线 id=01a0622c-3307-75d5-9d23-53ec7fae0afe，主标题=route-m9p4；大使 amb-m9p3、城市 city-m9p2

## Step 2: POST 路线类周期推荐
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["OVULATION"], "type": "ROUTE", "targetId": "01a0622c-3307-75d5-9d23-53ec7fae0afe", "title": "排卵期就该出门", "subtitle": "三天两夜", "description": "体力最好的几天", "banner": "images/b008.png"}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-3319-7a92-9475-2542b721eeac","phases":["OVULATION"],"type":"ROUTE","sortOrder":0,"online":false,"targetId":"01a0622c-3307-75d5-9d23-53ec7fae0afe","relatedTitle":"route-m9p4","title":"排卵期就该出门","subtitle":"三天两夜","description":"体力最好的几天","note":null,"banner":{"id":"bound/b008.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b008.png?Expires=1788355258&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:58.96960618Z","updatedAt":"2026-09-02T12:50:58.96960618Z"}
```

## Step 3: GET 详情
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/01a0622c-3319-7a92-9475-2542b721eeac" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622c-3319-7a92-9475-2542b721eeac","phases":["OVULATION"],"type":"ROUTE","sortOrder":0,"online":false,"targetId":"01a0622c-3307-75d5-9d23-53ec7fae0afe","relatedTitle":"route-m9p4","title":"排卵期就该出门","subtitle":"三天两夜","description":"体力最好的几天","note":null,"banner":{"id":"bound/b008.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b008.png?Expires=1788355258&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"createdAt":"2026-09-02T12:50:58.969606Z","updatedAt":"2026-09-02T12:50:58.969606Z"}
```
