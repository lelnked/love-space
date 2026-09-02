# TC-featured-IT-020 GET /api/app/featured-cycle-items 城市未上架不影响路线类条目 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 下架城市 01a0622e-ebdd-76e3-a077-f3591e13e7b1 下路线 01a0622e-ebe5-71b7-830c-af763a35b7c4（大使上线），OVULATION 上线 ROUTE 条目 + 上线 ACTIVITY 条目

## Step 2: GET
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-ebf1-7df6-bfa8-833d5dc5c80d","period":["OVULATION"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9pf90c22.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c22.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-ebed-7a5e-870f-a6a757dac86e","target":{"id":"01a0622e-ebed-7a5e-870f-a6a757dac86e","title":"act-m9pf90c21","subtitle":null,"cover":{"id":"bound/act-m9pf90c21.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9pf90c21.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null},{"id":"01a0622e-ebea-710b-b71e-b73792c9da9b","period":["OVULATION"],"type":"ROUTE","banner":{"id":"bound/b-bn-m9pf90c19.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c19.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-ebe5-71b7-830c-af763a35b7c4","target":{"id":"01a0622e-ebe5-71b7-830c-af763a35b7c4","title":"route-m9pf90c18","thumbnail":{"id":"bound/route-m9pf90c18-t.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/route-m9pf90c18-t.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"cityName":"city-m9pf90c16","ambassadorName":"amb-m9pf90c17"},"title":"主标题-m9pf90c20","subtitle":"副标题","description":"推荐说明","note":null}]
```

## Step 3: 城市上架后再 GET

### admin: 城市上架
```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a0622e-ebdd-76e3-a077-f3591e13e7b1" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName": "city-m9pf90c16", "englishName": "city-m9pf90c16", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"id":"01a0622e-ebdd-76e3-a077-f3591e13e7b1","chineseName":"city-m9pf90c16","englishName":"city-m9pf90c16","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":true,"createdAt":"2026-09-02T12:53:57.341396Z","updatedAt":"2026-09-02T12:53:57.341396Z"}
```
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622e-ebf1-7df6-bfa8-833d5dc5c80d","period":["OVULATION"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9pf90c22.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c22.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-ebed-7a5e-870f-a6a757dac86e","target":{"id":"01a0622e-ebed-7a5e-870f-a6a757dac86e","title":"act-m9pf90c21","subtitle":null,"cover":{"id":"bound/act-m9pf90c21.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9pf90c21.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null},{"id":"01a0622e-ebea-710b-b71e-b73792c9da9b","period":["OVULATION"],"type":"ROUTE","banner":{"id":"bound/b-bn-m9pf90c19.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf90c19.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622e-ebe5-71b7-830c-af763a35b7c4","target":{"id":"01a0622e-ebe5-71b7-830c-af763a35b7c4","title":"route-m9pf90c18","thumbnail":{"id":"bound/route-m9pf90c18-t.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/route-m9pf90c18-t.png?Expires=1788355437&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"cityName":"city-m9pf90c16","ambassadorName":"amb-m9pf90c17"},"title":"主标题-m9pf90c20","subtitle":"副标题","description":"推荐说明","note":null}]
```
