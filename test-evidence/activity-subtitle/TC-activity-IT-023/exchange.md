# TC-activity-IT-023 请求/响应存证

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

## step 1 创建活动（subtitle="一日徒步"）

```bash
curl -s -X POST "http://localhost:21423/api/admin/activities" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"images": ["images/a023-1.png"], "title": "副标题验证活动-0902", "tags": ["副标题"], "periods": ["LUTEAL"], "level": "L1", "introduction": "简介", "itinerary": [{"title": "D1", "content": "C"}], "detailHtml": "<p>d</p>", "online": true, "subtitle": "一日徒步"}'
```

HTTP 200，`subtitle` = `"一日徒步"`
```json
{
 "id": "01a0608e-531e-78f2-bb62-23c025fc49f4",
 "images": [
  {
   "id": "bound/a023-1.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a023-1.png?Expires=1788328135&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Big3WG2AXEWoWesla2NP%2BVCgVDg%3D"
  }
 ],
 "title": "副标题验证活动-0902",
 "subtitle": "一日徒步",
 "tags": [
  "副标题"
 ],
 "periods": [
  "LUTEAL"
 ],
 "level": "L1",
 "introduction": "简介",
 "editorNote": null,
 "gatheringPlace": null,
 "dismissalPlace": null,
 "transportation": null,
 "visa": null,
 "landscape": null,
 "itinerary": [
  {
   "title": "D1",
   "content": "C"
  }
 ],
 "detailHtml": "<p>d</p>",
 "online": true,
 "createdAt": "2026-09-02T05:18:55.2624893Z",
 "updatedAt": "2026-09-02T05:18:55.2624893Z"
}
```

## step 2 查询详情

```bash
curl -s -X GET "http://localhost:21423/api/admin/activities/01a0608e-531e-78f2-bb62-23c025fc49f4" \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200，`subtitle` = `"一日徒步"`

## step 3 更新 subtitle 为「两日徒步」并重查详情

```bash
curl -s -X PUT "http://localhost:21423/api/admin/activities/01a0608e-531e-78f2-bb62-23c025fc49f4" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"images": ["images/a023-1.png"], "title": "副标题验证活动-0902", "tags": ["副标题"], "periods": ["LUTEAL"], "level": "L1", "introduction": "简介", "itinerary": [{"title": "D1", "content": "C"}], "detailHtml": "<p>d</p>", "online": true, "subtitle": "两日徒步"}'
curl -s "http://localhost:21423/api/admin/activities/01a0608e-531e-78f2-bb62-23c025fc49f4" -H "Authorization: Bearer $TOKEN"
```

PUT HTTP 200 → `subtitle` = `"两日徒步"`；GET HTTP 200 → `subtitle` = `"两日徒步"`

## step 4 更新时 body 不带 subtitle 字段并重查详情

```bash
curl -s -X PUT "http://localhost:21423/api/admin/activities/01a0608e-531e-78f2-bb62-23c025fc49f4" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"images": ["images/a023-1.png"], "title": "副标题验证活动-0902", "tags": ["副标题"], "periods": ["LUTEAL"], "level": "L1", "introduction": "简介", "itinerary": [{"title": "D1", "content": "C"}], "detailHtml": "<p>d</p>", "online": true}'
```

PUT HTTP 200（不报 400）→ `subtitle` = `null`；GET HTTP 200 → `subtitle` = `null`

## step 5 更新 subtitle="" 空串并重查详情

```bash
curl -s -X PUT "http://localhost:21423/api/admin/activities/01a0608e-531e-78f2-bb62-23c025fc49f4" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"images": ["images/a023-1.png"], "title": "副标题验证活动-0902", "tags": ["副标题"], "periods": ["LUTEAL"], "level": "L1", "introduction": "简介", "itinerary": [{"title": "D1", "content": "C"}], "detailHtml": "<p>d</p>", "online": true, "subtitle": ""}'
```

PUT HTTP 200 → `subtitle` = `""`；GET HTTP 200 → `subtitle` = `""`（后端原样保存，不做 trim 归一）

## step 6 后台分页列表定位该活动

```bash
curl -s "http://localhost:21423/api/admin/activities/page?page=0&size=200" -H "Authorization: Bearer $TOKEN"
```

HTTP 200，列表项：
```json
{
 "id": "01a0608e-531e-78f2-bb62-23c025fc49f4",
 "cover": {
  "id": "bound/a023-1.png",
  "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a023-1.png?Expires=1788328135&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Big3WG2AXEWoWesla2NP%2BVCgVDg%3D"
 },
 "title": "副标题验证活动-0902",
 "subtitle": "",
 "tags": [
  "副标题"
 ],
 "periods": [
  "LUTEAL"
 ],
 "level": "L1",
 "online": true,
 "createdAt": "2026-09-02T05:18:55.262489Z",
 "updatedAt": "2026-09-02T05:18:55.460975Z"
}
```
