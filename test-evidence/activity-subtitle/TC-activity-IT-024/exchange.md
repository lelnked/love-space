# TC-activity-IT-024 请求/响应存证

> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，库 `jdbc:postgresql://localhost:25432/love_space`
> 执行时间: 2026-09-02　执行器: api-test-runner
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；
> app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> objectKey 为 test profile 下的直传绑定键（`images/xxx.png` → 绑定后 `bound/xxx.png`）。

## step 0 登录（所有 admin 步骤共用）

```bash
curl -s -X POST "http://localhost:21423/api/admin/auth/login" -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

HTTP 200，返回体含三段式 JWT `token`（长度 251）与 `manager`，记为 `$TOKEN`。

## step 1 前置：admin 侧创建两个上线活动

活动 A（subtitle="山野轻装"）id = `01a0608e-9b39-7d5c-8cfa-5fb3fedd3879`；活动 B（不填 subtitle）id = `01a0608e-9b58-7503-95ee-0dea4ec91bc1`

```bash
curl -s -X POST "http://localhost:21423/api/admin/activities" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"images": ["images/a024-A.png"], "title": "副标题活动A-0902", "subtitle": "山野轻装", "tags": ["徒步"], "periods": ["FOLLICULAR"], "level": "L2", "introduction": "简介A", "itinerary": [{"title": "D1", "content": "CA"}], "detailHtml": "<p>A</p>", "online": true}'

curl -s -X POST "http://localhost:21423/api/admin/activities" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"images": ["images/a024-B.png"], "title": "无副标题活动B-0902", "tags": ["徒步"], "periods": ["FOLLICULAR"], "level": "L2", "introduction": "简介B", "itinerary": [{"title": "D1", "content": "CB"}], "detailHtml": "<p>B</p>", "online": true}'
```

两次均 HTTP 200。

## step 2 app 活动列表

```bash
curl -s "http://localhost:8081/api/app/activities" -H "X-API-Key: $APP_API_KEY"
```

HTTP 200 / `Content-Type: application/json`，列表共 52 项，其中：
```json
{
 "id": "01a0608e-9b39-7d5c-8cfa-5fb3fedd3879",
 "title": "副标题活动A-0902",
 "subtitle": "山野轻装",
 "images": [
  {
   "id": "bound/a024-A.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a024-A.png?Expires=1788328153&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DWpUTeibjGUJyKGvxLs4R82EWeY%3D"
  }
 ],
 "tags": [
  "徒步"
 ],
 "periods": [
  "FOLLICULAR"
 ],
 "level": "L2",
 "introduction": "简介A"
}

{
 "id": "01a0608e-9b58-7503-95ee-0dea4ec91bc1",
 "title": "无副标题活动B-0902",
 "subtitle": null,
 "images": [
  {
   "id": "bound/a024-B.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a024-B.png?Expires=1788328153&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=S0XkbZzccThCSfi%2BQkW9PeC1ykY%3D"
  }
 ],
 "tags": [
  "徒步"
 ],
 "periods": [
  "FOLLICULAR"
 ],
 "level": "L2",
 "introduction": "简介B"
}
```

## step 3 app 活动 A 详情

```bash
curl -s "http://localhost:8081/api/app/activities/01a0608e-9b39-7d5c-8cfa-5fb3fedd3879" -H "X-API-Key: $APP_API_KEY"
```

HTTP 200，`subtitle` = `"山野轻装"`

## step 4 app 活动 B 详情

```bash
curl -s "http://localhost:8081/api/app/activities/01a0608e-9b58-7503-95ee-0dea4ec91bc1" -H "X-API-Key: $APP_API_KEY"
```

HTTP 200，`subtitle` = `null`
```json
{
 "id": "01a0608e-9b58-7503-95ee-0dea4ec91bc1",
 "images": [
  {
   "id": "bound/a024-B.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a024-B.png?Expires=1788328164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qs3qMYmGGAMeTbevKuxXdfmTbY0%3D"
  }
 ],
 "title": "无副标题活动B-0902",
 "subtitle": null,
 "tags": [
  "徒步"
 ],
 "periods": [
  "FOLLICULAR"
 ],
 "level": "L2",
 "introduction": "简介B",
 "editorNote": null,
 "gatheringPlace": null,
 "dismissalPlace": null,
 "transportation": null,
 "visa": null,
 "landscape": null,
 "itinerary": [
  {
   "title": "D1",
   "content": "CB"
  }
 ],
 "detailHtml": "<p>B</p>"
}
```
