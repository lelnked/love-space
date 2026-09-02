# TC-featured-IT-017 GET /api/app/featured-cycle-items 关联实体不可见时条目不下发 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1: 前置 + GET 确认数组含两条
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-c4c8-7cc9-ba58-8abf6d757b8b","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b-bn-m9p11.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9p11.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-c4bf-7ccf-a5f3-22d1914f6999","target":{"id":"01a0622e-c4bf-7ccf-a5f3-22d1914f6999","title":"art-m9p9","coverTitle":null,"image":{"id":"bound/art-m9p9.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/art-m9p9.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}},"title":"文章主标题-m9p12","subtitle":null,"description":null,"note":null},{"id":"01a0622e-c4c4-72dd-8507-969e0b231195","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9p10.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9p10.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-c4bc-76c0-8be5-d9e4df503922","target":{"id":"01a0622e-c4bc-76c0-8be5-d9e4df503922","title":"act-m9p8","subtitle":null,"cover":{"id":"bound/act-m9p8.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9p8.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null}]
```

## Step 2: 活动下线后 GET

### admin: 活动下线
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a0622e-c4bc-76c0-8be5-d9e4df503922/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": false}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622e-c4bc-76c0-8be5-d9e4df503922","images":[{"id":"bound/act-m9p8.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act-m9p8.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act-m9p8","subtitle":null,"tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":false,"createdAt":"2026-09-02T12:53:47.324399Z","updatedAt":"2026-09-02T12:53:47.324399Z"}
```
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-c4c8-7cc9-ba58-8abf6d757b8b","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b-bn-m9p11.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9p11.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-c4bf-7ccf-a5f3-22d1914f6999","target":{"id":"01a0622e-c4bf-7ccf-a5f3-22d1914f6999","title":"art-m9p9","coverTitle":null,"image":{"id":"bound/art-m9p9.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/art-m9p9.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}},"title":"文章主标题-m9p12","subtitle":null,"description":null,"note":null}]
```

## Step 3: 恢复活动上线后 GET

### admin: 活动上线
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a0622e-c4bc-76c0-8be5-d9e4df503922/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": true}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622e-c4bc-76c0-8be5-d9e4df503922","images":[{"id":"bound/act-m9p8.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/act-m9p8.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}],"title":"act-m9p8","subtitle":null,"tags":[],"periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","editorNote":null,"gatheringPlace":null,"dismissalPlace":null,"transportation":null,"visa":null,"landscape":null,"itinerary":[],"detailHtml":null,"online":true,"createdAt":"2026-09-02T12:53:47.324399Z","updatedAt":"2026-09-02T12:53:47.360027Z"}
```
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-c4c8-7cc9-ba58-8abf6d757b8b","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b-bn-m9p11.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9p11.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-c4bf-7ccf-a5f3-22d1914f6999","target":{"id":"01a0622e-c4bf-7ccf-a5f3-22d1914f6999","title":"art-m9p9","coverTitle":null,"image":{"id":"bound/art-m9p9.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/art-m9p9.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}},"title":"文章主标题-m9p12","subtitle":null,"description":null,"note":null},{"id":"01a0622e-c4c4-72dd-8507-969e0b231195","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9p10.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9p10.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-c4bc-76c0-8be5-d9e4df503922","target":{"id":"01a0622e-c4bc-76c0-8be5-d9e4df503922","title":"act-m9p8","subtitle":null,"cover":{"id":"bound/act-m9p8.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9p8.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null}]
```

## Step 4: 文章下线后 GET

### admin: 文章下线
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a0622e-c4bf-7ccf-a5f3-22d1914f6999/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": false}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622e-c4bf-7ccf-a5f3-22d1914f6999","image":{"id":"bound/art-m9p9.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-m9p9.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art-m9p9","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":false,"createdAt":"2026-09-02T12:53:47.327778Z","updatedAt":"2026-09-02T12:53:47.327778Z"}
```
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-c4c4-72dd-8507-969e0b231195","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9p10.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9p10.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-c4bc-76c0-8be5-d9e4df503922","target":{"id":"01a0622e-c4bc-76c0-8be5-d9e4df503922","title":"act-m9p8","subtitle":null,"cover":{"id":"bound/act-m9p8.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9p8.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null}]
```

## Step 5: 恢复文章上线后删除文章，再 GET

### admin: 文章上线
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a0622e-c4bf-7ccf-a5f3-22d1914f6999/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": true}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622e-c4bf-7ccf-a5f3-22d1914f6999","image":{"id":"bound/art-m9p9.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-m9p9.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art-m9p9","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-09-02T12:53:47.327778Z","updatedAt":"2026-09-02T12:53:47.401877Z"}
```

### admin: 删除文章
```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/articles/01a0622e-c4bf-7ccf-a5f3-22d1914f6999" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: -）
```json
(empty)
```
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-c4c4-72dd-8507-969e0b231195","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9p10.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9p10.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-c4bc-76c0-8be5-d9e4df503922","target":{"id":"01a0622e-c4bc-76c0-8be5-d9e4df503922","title":"act-m9p8","subtitle":null,"cover":{"id":"bound/act-m9p8.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9p8.png?Expires=1788355427&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null}]
```
