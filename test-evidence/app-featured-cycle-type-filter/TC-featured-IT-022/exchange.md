# TC-featured-IT-022 GET /api/app/featured-cycle-items?type=ROUTE 过滤后周期为空仍返回空数组 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 export 后下列 curl 可原样执行。

> 前置：库为空库（本轮开始时 admin 分页 totalElements=0，app 四分组全空），故本用例天然隔离。
> objectKey 为真实 OSS 直传所得（POST /api/admin/files/upload-credentials → 表单直传，带 success_action_status=200）。

## Step 0: 获取上传凭证并直传 1×1 PNG（取真实 objectKey）

```bash
curl -s -X POST "http://localhost:8080/api/admin/files/upload-credentials" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"contentType":"image/png"}'
```

响应（HTTP 200）含 `host` / `objectKey` / `policy` / `signature` / `x-oss-*` 等签名字段，随后表单直传：

```bash
curl -s -o /dev/null -w '%{http_code}\n' -X POST "$host" \
  -F "key=$objectKey" -F "policy=$policy" -F "x-oss-signature=$signature" \
  -F "x-oss-signature-version=$signatureVersion" -F "x-oss-credential=$xOssCredential" \
  -F "x-oss-date=$xOssDate" -F "x-oss-security-token=$securityToken" \
  -F "success_action_status=200" -F "file=@/tmp/px.png;type=image/png"
```

实际响应: `200`。本用例取得 objectKey：
- 活动图片 `images/01a034d8-ecb1-7478-a7e9-7e344f3bd1e6.png`
- 条目 banner `images/01a034d8-f012-703c-bbbe-7e5549f3318d.png`

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"过滤城022","englishName":"FilterCity022","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{"id":"01a034d8-f442-71e0-8ff3-f2aea9e7e981","chineseName":"过滤城022","englishName":"FilterCity022","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":true,"createdAt":"2026-08-24T17:37:08.672562598Z","updatedAt":"2026-08-24T17:37:08.672562598Z"}
```

## Step 1b: 创建上线活动

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a034d8-f442-71e0-8ff3-f2aea9e7e981","images":["images/01a034d8-ecb1-7478-a7e9-7e344f3bd1e6.png"],"title":"过滤活动022","tags":["露营"],"periods":["MENSTRUAL"],"level":"L2","introduction":"介绍","editorNote":"寄语","gatheringPlace":"集合","dismissalPlace":"解散","transportation":"大巴","visa":"无需签证","itinerary":[{"title":"Day1","content":"集合"}],"detailHtml":"<p>详情</p>","online":true}'
```

实际响应（HTTP 200）:

```json
{"id":"01a034d8-f5f0-7f6c-b136-6a0e28ac0bf3","cityId":"01a034d8-f442-71e0-8ff3-f2aea9e7e981","title":"过滤活动022","periods":["MENSTRUAL"],"online":true,"createdAt":"2026-08-24T17:37:09.103361014Z"}
```

## Step 1c: MENSTRUAL 下建上线 ACTIVITY 条目（库中唯一条目，无任何 ROUTE 类条目）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a034d8-f5f0-7f6c-b136-6a0e28ac0bf3","description":"活动条目022","banner":"images/01a034d8-f012-703c-bbbe-7e5549f3318d.png","online":true}'
```

实际响应（HTTP 200）:

```json
{"id":"01a034d8-f6d8-7fee-96f7-da0b76e03f66","phase":"MENSTRUAL","type":"ACTIVITY","sortOrder":0,"online":true,"activityId":"01a034d8-f5f0-7f6c-b136-6a0e28ac0bf3","routeId":null,"articleId":null,"relatedTitle":"过滤活动022","description":"活动条目022","banner":{"id":"bound/01a034d8-f012-703c-bbbe-7e5549f3318d.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034d8-f012-703c-bbbe-7e5549f3318d.png?Expires=1787594829&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=xQviAFYfcpAfapFGme2%2BZqCH%2Bxw%3D"},"createdAt":"2026-08-24T17:37:09.335399826Z"}
```

## Step 2: app 端按 type=ROUTE 过滤（无任何可见 ROUTE 条目）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?type=ROUTE" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json，Content-Length: 59）:

```json
{"MENSTRUAL":[],"FOLLICULAR":[],"OVULATION":[],"LUTEAL":[]}
```

## Step 3（对照）: 不传 type，确认数据确实存在

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200）:

```json
{"MENSTRUAL":[{"id":"01a034d8-f6d8-7fee-96f7-da0b76e03f66","type":"ACTIVITY","banner":{"id":"bound/01a034d8-f012-703c-bbbe-7e5549f3318d.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034d8-f012-703c-bbbe-7e5549f3318d.png?Expires=1787594834&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=txRN0a1GX%2FMf5EfxwkDlg%2FQ7K7I%3D"},"activityId":"01a034d8-f5f0-7f6c-b136-6a0e28ac0bf3","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目022","note":null}],"FOLLICULAR":[],"OVULATION":[],"LUTEAL":[]}
```
