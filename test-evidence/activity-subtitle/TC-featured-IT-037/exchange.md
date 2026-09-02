# TC-featured-IT-037 请求/响应存证

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

## step 1 前置：无图片的上线活动 + FOLLICULAR 下的上线 ACTIVITY 条目

`ActivityUpsertRequest.images` 有 `@NotEmpty` 约束，接口无法直接建出无图活动；故先按常规建活动再用一条 SQL 清空图片列（纯测试数据准备，不涉被测行为）：

```bash
curl -s -X POST "http://localhost:21423/api/admin/activities" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"images": ["images/f037-tmp.png"], "title": "无图活动-0902", "tags": ["无图"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "itinerary": [], "detailHtml": "<p>d</p>", "online": true}'

PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space \
  -c "UPDATE loves_activity SET images='[]'::jsonb WHERE id='01a06090-6abb-746e-b621-959ac912e3f4';"
```

```bash
curl -s -X POST "http://localhost:21423/api/admin/featured-cycle-items" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"phase": "FOLLICULAR", "type": "ACTIVITY", "banner": "images/f037-banner.png", "sortOrder": 2, "online": true, "targetId": "01a06090-6abb-746e-b621-959ac912e3f4", "description": "周期推荐说明-037"}'
```

HTTP 200
```json
{
 "id": "01a06090-6aec-7c45-aabc-2cabfd9000c6",
 "phase": "FOLLICULAR",
 "type": "ACTIVITY",
 "sortOrder": 2,
 "online": true,
 "targetId": "01a06090-6abb-746e-b621-959ac912e3f4",
 "relatedTitle": "无图活动-0902",
 "title": null,
 "subtitle": null,
 "description": "周期推荐说明-037",
 "note": null,
 "banner": {
  "id": "bound/f037-banner.png",
  "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/f037-banner.png?Expires=1788328272&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=UZL048L72e6lbe90NEQPO5mP%2BZo%3D"
 },
 "createdAt": "2026-09-02T05:21:12.428721763Z",
 "updatedAt": "2026-09-02T05:21:12.428721763Z"
}
```

## step 2 app 周期推荐查询（type=ACTIVITY&period=FOLLICULAR）

```bash
curl -s "http://localhost:8081/api/app/featured-cycle-items?type=ACTIVITY&period=FOLLICULAR" -H "X-API-Key: $APP_API_KEY"
```

HTTP 200。命中条目：
```json
{
 "id": "01a06090-6aec-7c45-aabc-2cabfd9000c6",
 "period": [
  "FOLLICULAR"
 ],
 "type": "ACTIVITY",
 "banner": {
  "id": "bound/f037-banner.png",
  "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/f037-banner.png?Expires=1788328272&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=UZL048L72e6lbe90NEQPO5mP%2BZo%3D"
 },
 "targetId": "01a06090-6abb-746e-b621-959ac912e3f4",
 "target": {
  "id": "01a06090-6abb-746e-b621-959ac912e3f4",
  "title": "无图活动-0902",
  "subtitle": null,
  "cover": null,
  "level": "L1"
 },
 "title": null,
 "subtitle": null,
 "description": "周期推荐说明-037",
 "note": null
}
```
