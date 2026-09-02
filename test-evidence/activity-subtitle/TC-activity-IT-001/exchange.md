# TC-activity-IT-001 请求/响应存证

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

## step 1 创建活动（不含 cityId）

```bash
curl -s -X POST "http://localhost:21423/api/admin/activities" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"images": ["images/a001-1.png", "images/a001-2.png"], "title": "海岛露营节", "tags": ["露营", "海岛"], "periods": ["FOLLICULAR", "OVULATION"], "level": "L2", "introduction": "简介文本", "editorNote": "编辑说文本", "gatheringPlace": "集合地A", "dismissalPlace": "解散地B", "transportation": "大巴", "visa": "无需签证", "itinerary": [{"title": "I1", "content": "第一天"}, {"title": "I2", "content": "第二天"}], "detailHtml": "<p>纯文本段落</p>", "online": true}'
```

HTTP 200
```json
{
 "id": "01a0608d-9cb6-7b38-b0b9-25a197ef8064",
 "images": [
  {
   "id": "bound/a001-1.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a001-1.png?Expires=1788328088&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VIPAwD%2BGssGa89eNammNOwjepWY%3D"
  },
  {
   "id": "bound/a001-2.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a001-2.png?Expires=1788328088&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=YhSuXVeTpSqDa9vdqZHMSacgBGg%3D"
  }
 ],
 "title": "海岛露营节",
 "subtitle": null,
 "tags": [
  "露营",
  "海岛"
 ],
 "periods": [
  "FOLLICULAR",
  "OVULATION"
 ],
 "level": "L2",
 "introduction": "简介文本",
 "editorNote": "编辑说文本",
 "gatheringPlace": "集合地A",
 "dismissalPlace": "解散地B",
 "transportation": "大巴",
 "visa": "无需签证",
 "landscape": null,
 "itinerary": [
  {
   "title": "I1",
   "content": "第一天"
  },
  {
   "title": "I2",
   "content": "第二天"
  }
 ],
 "detailHtml": "<p>纯文本段落</p>",
 "online": true,
 "createdAt": "2026-09-02T05:18:08.553933646Z",
 "updatedAt": "2026-09-02T05:18:08.553933646Z"
}
```

## step 2 查询详情

```bash
curl -s -X GET "http://localhost:21423/api/admin/activities/01a0608d-9cb6-7b38-b0b9-25a197ef8064" \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200 / `Content-Type: application/json`
```json
{
 "id": "01a0608d-9cb6-7b38-b0b9-25a197ef8064",
 "images": [
  {
   "id": "bound/a001-1.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a001-1.png?Expires=1788328094&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=PQblaxFS1viXMa3wBTB1xXFHZCA%3D"
  },
  {
   "id": "bound/a001-2.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a001-2.png?Expires=1788328094&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=orF6iy7NYedWdGkWRZuORb4ujD8%3D"
  }
 ],
 "title": "海岛露营节",
 "subtitle": null,
 "tags": [
  "露营",
  "海岛"
 ],
 "periods": [
  "FOLLICULAR",
  "OVULATION"
 ],
 "level": "L2",
 "introduction": "简介文本",
 "editorNote": "编辑说文本",
 "gatheringPlace": "集合地A",
 "dismissalPlace": "解散地B",
 "transportation": "大巴",
 "visa": "无需签证",
 "landscape": null,
 "itinerary": [
  {
   "title": "I1",
   "content": "第一天"
  },
  {
   "title": "I2",
   "content": "第二天"
  }
 ],
 "detailHtml": "<p>纯文本段落</p>",
 "online": true,
 "createdAt": "2026-09-02T05:18:08.553934Z",
 "updatedAt": "2026-09-02T05:18:08.553934Z"
}
```
