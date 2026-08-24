# TC-featured-IT-016 GET /api/app/featured-cycle-items 四周期分组齐全且只含上线条目 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；
app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 均为真实 OSS 直传所得（POST /api/admin/files/upload-credentials → 表单直传，带 `success_action_status=200`）。
前置：本用例开始时 loves_featured_cycle_item 为空（app 四分组均为 `[]`），保证分组断言不受其他用例污染。

## Step 0: 登录取 JWT

```bash
curl -s -X POST "http://localhost:8080/api/admin/auth/login" -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

HTTP 200，返回体含 `token`（三段式 JWT，长度 251）与 `manager` 字段，记为 `$TOKEN`。

## Step 0b: 取真实 objectKey（每张图各跑一次）

```bash
curl -s -X POST "http://localhost:8080/api/admin/files/upload-credentials" -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' -d '{"contentType":"image/png"}'
# → 用返回的 host/policy/signature/x-oss-* 表单直传 1×1 PNG
curl -s -o /dev/null -w '%{http_code}\n' -X POST "$host" \
  -F "key=$objectKey" -F "policy=$policy" -F "x-oss-signature=$signature" \
  -F "x-oss-signature-version=$signatureVersion" -F "x-oss-credential=$xOssCredential" \
  -F "x-oss-date=$xOssDate" -F "x-oss-security-token=$securityToken" \
  -F "success_action_status=200" -F "file=@/tmp/px.png;type=image/png"
```

直传均返回 `200`。本用例取得 5 个 objectKey：
`images/01a034e1-d091-...png`（活动图）、`images/01a034e1-d377-...png`（文章图）、
`images/01a034e1-d588-...png` / `images/01a034e1-da3e-...png` / `images/01a034e1-dd70-...png`（3 个条目 banner）。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"周期城016","englishName":"CycleCity016","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```json
{"id":"01a034e1-df5c-704b-88f0-ffd11fb08a23","chineseName":"周期城016","englishName":"CycleCity016","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":true,"createdAt":"2026-08-24T17:46:53.146433855Z","updatedAt":"2026-08-24T17:46:53.146433855Z"}
```

## Step 1b: 创建上线活动（挂在上架城市下）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a034e1-df5c-704b-88f0-ffd11fb08a23","images":["images/01a034e1-d091-71ed-9b73-99dde1495ba3.png"],"title":"周期活动016","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
```

实际响应（HTTP 200）: `id=01a034e1-e1e3-7fa4-aa38-6fb973fa83dc`，`online=true`，`cityId=01a034e1-df5c-...`。

## Step 1c: 创建上线文章

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/01a034e1-d377-757f-b967-89a1fa02506b.png","title":"周期文章016","subtitle":"副标题","contentHtml":"<p>正文</p>","online":true}'
```

实际响应（HTTP 200）: `id=01a034e1-e2d9-7ec6-81c4-c1661bf3c370`，`online=true`。

## Step 1d: 建三个周期条目（MENSTRUAL 上线 ACTIVITY / OVULATION 上线 ARTICLE / LUTEAL 下线 ACTIVITY；FOLLICULAR 不建）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a034e1-e1e3-7fa4-aa38-6fb973fa83dc","description":"活动条目016","banner":"images/01a034e1-d588-7620-87e8-c019d8f93ffd.png","online":true}'

curl -s -i -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"OVULATION","type":"ARTICLE","articleId":"01a034e1-e2d9-7ec6-81c4-c1661bf3c370","title":"文章条目016","banner":"images/01a034e1-da3e-7687-b61e-7dc0495eda3f.png","online":true}'

curl -s -i -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"LUTEAL","type":"ACTIVITY","activityId":"01a034e1-e1e3-7fa4-aa38-6fb973fa83dc","description":"下线条目016","banner":"images/01a034e1-dd70-7488-a8b4-f75ef4956aa7.png","online":false}'
```

三次均 HTTP 200，分别得 `01a034e1-e3bb-...475f`(MENSTRUAL/online=true)、`01a034e1-e478-...f865`(OVULATION/online=true)、`01a034e1-e527-...1add`(LUTEAL/online=false)。

## Step 2: app 端查询四周期分组

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

实际响应:

```
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
Content-Type: application/json
Content-Length: 1052
Date: Mon, 24 Aug 2026 17:46:54 GMT
```

```json
{"MENSTRUAL":[{"id":"01a034e1-e3bb-7840-9af1-f6b79554475f","type":"ACTIVITY","banner":{"id":"bound/01a034e1-d588-7620-87e8-c019d8f93ffd.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034e1-d588-7620-87e8-c019d8f93ffd.png?Expires=1787595414&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=uzHPm7siQFeDYEwc7MZlDYa6WaQ%3D"},"activityId":"01a034e1-e1e3-7fa4-aa38-6fb973fa83dc","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目016","note":null}],"FOLLICULAR":[],"OVULATION":[{"id":"01a034e1-e478-76a1-b59f-f9e22809f865","type":"ARTICLE","banner":{"id":"bound/01a034e1-da3e-7687-b61e-7dc0495eda3f.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034e1-da3e-7687-b61e-7dc0495eda3f.png?Expires=1787595414&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=S0UDICyDFSawNOy8zO2OSzLF5v8%3D"},"activityId":null,"routeId":null,"articleId":"01a034e1-e2d9-7ec6-81c4-c1661bf3c370","title":"文章条目016","subtitle":null,"description":null,"note":null}],"LUTEAL":[]}
```

## Step 3: 收尾清理（供后续用例隔离）

```bash
for id in 01a034e1-e3bb-7840-9af1-f6b79554475f 01a034e1-e478-76a1-b59f-f9e22809f865 01a034e1-e527-7c93-8d61-9405e32aa1dd; do
  curl -s -o /dev/null -w '%{http_code}\n' -X DELETE "http://localhost:8080/api/admin/featured-cycle-items/$id" -H "Authorization: Bearer $TOKEN"
done
```

三次均 `200`；清理后 app 端返回 `{"MENSTRUAL":[],"FOLLICULAR":[],"OVULATION":[],"LUTEAL":[]}`。
