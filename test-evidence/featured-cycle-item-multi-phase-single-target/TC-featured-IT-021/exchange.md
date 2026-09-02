# TC-featured-IT-021 GET /api/app/featured-cycle-items?type= 按内容类型过滤 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: MENSTRUAL 下 ACTIVITY/ROUTE/ARTICLE 上线条目各 1（各关联不同实体）

## Step 2: GET ?type=ARTICLE
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?type=ARTICLE" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-ec5b-7a27-8d9f-03d0b1954b04","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b-bn-m9pf90c31.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c31.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-ec3f-7623-9f93-6c030305511a","target":{"id":"01a0622e-ec3f-7623-9f93-6c030305511a","title":"art-m9pf90c24","coverTitle":null,"image":{"id":"bound/art-m9pf90c24.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/art-m9pf90c24.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}},"title":"文章主标题-m9pf90c32","subtitle":null,"description":null,"note":null}]
```

## Step 3: GET 不带参数
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-ec5b-7a27-8d9f-03d0b1954b04","period":["MENSTRUAL"],"type":"ARTICLE","banner":{"id":"bound/b-bn-m9pf90c31.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c31.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-ec3f-7623-9f93-6c030305511a","target":{"id":"01a0622e-ec3f-7623-9f93-6c030305511a","title":"art-m9pf90c24","coverTitle":null,"image":{"id":"bound/art-m9pf90c24.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/art-m9pf90c24.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"}},"title":"文章主标题-m9pf90c32","subtitle":null,"description":null,"note":null},{"id":"01a0622e-ec56-7c6b-8f14-cca34ae1f99e","period":["MENSTRUAL"],"type":"ROUTE","banner":{"id":"bound/b-bn-m9pf90c29.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c29.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-ec4c-7645-aebc-196b4704503c","target":{"id":"01a0622e-ec4c-7645-aebc-196b4704503c","title":"route-m9pf90c27","thumbnail":{"id":"bound/route-m9pf90c27-t.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/route-m9pf90c27-t.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"cityName":"city-m9pf90c25","ambassadorName":"amb-m9pf90c26"},"title":"主标题-m9pf90c30","subtitle":"副标题","description":"推荐说明","note":null},{"id":"01a0622e-ec52-71be-9e0b-4f0af04d7f0b","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9pf90c28.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c28.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-ec3b-7ea3-a3f7-6c9780870859","target":{"id":"01a0622e-ec3b-7ea3-a3f7-6c9780870859","title":"act-m9pf90c23","subtitle":null,"cover":{"id":"bound/act-m9pf90c23.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9pf90c23.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null}]
```
