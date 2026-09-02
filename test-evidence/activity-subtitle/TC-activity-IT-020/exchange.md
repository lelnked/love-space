# TC-activity-IT-020 请求/响应存证

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

## step 1 创建活动（landscape="海岸线景观"）

```bash
curl -s -X POST "http://localhost:21423/api/admin/activities" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"images": ["images/a020-1.png"], "title": "景观字段验证活动-0902", "tags": ["景观"], "periods": ["FOLLICULAR"], "level": "L2", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "大巴", "visa": "无需签证", "landscape": "海岸线景观", "itinerary": [{"title": "D1", "content": "内容"}], "detailHtml": "<p>详情</p>", "online": true}'
```

HTTP 200，`landscape` = `海岸线景观`，`subtitle` = `null`
```json
{
 "id": "01a0608e-0e63-76d8-a602-0fcbe2d60c26",
 "images": [
  {
   "id": "bound/a020-1.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a020-1.png?Expires=1788328117&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=O7YOI5W5sUpx00pFMmOBLYyyqPY%3D"
  }
 ],
 "title": "景观字段验证活动-0902",
 "subtitle": null,
 "tags": [
  "景观"
 ],
 "periods": [
  "FOLLICULAR"
 ],
 "level": "L2",
 "introduction": "简介",
 "editorNote": "编辑说",
 "gatheringPlace": "集合地",
 "dismissalPlace": "解散地",
 "transportation": "大巴",
 "visa": "无需签证",
 "landscape": "海岸线景观",
 "itinerary": [
  {
   "title": "D1",
   "content": "内容"
  }
 ],
 "detailHtml": "<p>详情</p>",
 "online": true,
 "createdAt": "2026-09-02T05:18:37.66736193Z",
 "updatedAt": "2026-09-02T05:18:37.66736193Z"
}
```

## step 2 admin 查询详情

```bash
curl -s -X GET "http://localhost:21423/api/admin/activities/01a0608e-0e63-76d8-a602-0fcbe2d60c26" \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200，`landscape` = `海岸线景观`
```json
{
 "id": "01a0608e-0e63-76d8-a602-0fcbe2d60c26",
 "images": [
  {
   "id": "bound/a020-1.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a020-1.png?Expires=1788328117&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=O7YOI5W5sUpx00pFMmOBLYyyqPY%3D"
  }
 ],
 "title": "景观字段验证活动-0902",
 "subtitle": null,
 "tags": [
  "景观"
 ],
 "periods": [
  "FOLLICULAR"
 ],
 "level": "L2",
 "introduction": "简介",
 "editorNote": "编辑说",
 "gatheringPlace": "集合地",
 "dismissalPlace": "解散地",
 "transportation": "大巴",
 "visa": "无需签证",
 "landscape": "海岸线景观",
 "itinerary": [
  {
   "title": "D1",
   "content": "内容"
  }
 ],
 "detailHtml": "<p>详情</p>",
 "online": true,
 "createdAt": "2026-09-02T05:18:37.667362Z",
 "updatedAt": "2026-09-02T05:18:37.667362Z"
}
```

## step 3 更新 landscape 为「火山地貌」

```bash
curl -s -X PUT "http://localhost:21423/api/admin/activities/01a0608e-0e63-76d8-a602-0fcbe2d60c26" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"images": ["images/a020-1.png"], "title": "景观字段验证活动-0902", "tags": ["景观"], "periods": ["FOLLICULAR"], "level": "L2", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "大巴", "visa": "无需签证", "landscape": "火山地貌", "itinerary": [{"title": "D1", "content": "内容"}], "detailHtml": "<p>详情</p>", "online": true}'
```

HTTP 200，`landscape` = `火山地貌`

## step 4 app 端查询详情

```bash
curl -s "http://localhost:8081/api/app/activities/01a0608e-0e63-76d8-a602-0fcbe2d60c26" -H "X-API-Key: $APP_API_KEY"
```

HTTP 200
```json
{
 "id": "01a0608e-0e63-76d8-a602-0fcbe2d60c26",
 "images": [
  {
   "id": "bound/a020-1.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a020-1.png?Expires=1788328117&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=O7YOI5W5sUpx00pFMmOBLYyyqPY%3D"
  }
 ],
 "title": "景观字段验证活动-0902",
 "subtitle": null,
 "tags": [
  "景观"
 ],
 "periods": [
  "FOLLICULAR"
 ],
 "level": "L2",
 "introduction": "简介",
 "editorNote": "编辑说",
 "gatheringPlace": "集合地",
 "dismissalPlace": "解散地",
 "transportation": "大巴",
 "visa": "无需签证",
 "landscape": "火山地貌",
 "itinerary": [
  {
   "title": "D1",
   "content": "内容"
  }
 ],
 "detailHtml": "<p>详情</p>"
}
```
