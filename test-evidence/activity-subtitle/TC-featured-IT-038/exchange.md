# TC-featured-IT-038 请求/响应存证

> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`
> 执行时间: 2026-09-02　执行器: api-test-runner（用例定义订正后重跑：条目手填文案由 subtitle 改为 description）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；
> app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> objectKey 为 test profile 下的直传绑定键（`images/xxx.png` → 绑定后 `bound/xxx.png`）。

## step 0 登录（admin 步骤共用）

```bash
curl -s -X POST "http://localhost:21423/api/admin/auth/login" -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

HTTP 200，返回体含三段式 JWT `token`（长度 251，`.` 分隔 3 段）与 `manager`，记为 `$TOKEN`。

## step 1a 前置：创建上线活动（含标题、1 张图片，**不填** subtitle）

```bash
curl -s -X POST "http://localhost:21423/api/admin/activities" \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"images": ["images/f038-cover.png"], "title": "无副标题活动-0902", "tags": ["无副标题"], "periods": ["OVULATION"], "level": "L2", "introduction": "简介", "itinerary": [], "detailHtml": "<p>d</p>", "online": true}'
```

HTTP 200
```json
{
 "id": "01a06095-c3a6-7f38-91b4-7d44a22624b0",
 "images": [{"id": "bound/f038-cover.png", "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/f038-cover.png?Expires=1788328622&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=aXj5HFnLCqs3yx83G3fYTPXXApU%3D"}],
 "title": "无副标题活动-0902",
 "subtitle": null,
 "tags": ["无副标题"],
 "periods": ["OVULATION"],
 "level": "L2",
 "introduction": "简介",
 "editorNote": null, "gatheringPlace": null, "dismissalPlace": null,
 "transportation": null, "visa": null, "landscape": null,
 "itinerary": [], "detailHtml": "<p>d</p>", "online": true,
 "createdAt": "2026-09-02T05:27:02.822886013Z",
 "updatedAt": "2026-09-02T05:27:02.822886013Z"
}
```

## step 1b 前置：OVULATION 下创建上线 ACTIVITY 条目，条目自身手填 `description`="限时开团"

（ACTIVITY 类条目不持有 subtitle 文案，请求体不带 `subtitle`）

```bash
curl -s -X POST "http://localhost:21423/api/admin/featured-cycle-items" \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"phase": "OVULATION", "type": "ACTIVITY", "banner": "images/f038-banner.png", "sortOrder": 3, "online": true, "targetId": "01a06095-c3a6-7f38-91b4-7d44a22624b0", "description": "限时开团"}'
```

HTTP 200
```json
{
 "id": "01a06095-d8f0-713f-9f1e-e77a85416296",
 "phase": "OVULATION",
 "type": "ACTIVITY",
 "sortOrder": 3,
 "online": true,
 "targetId": "01a06095-c3a6-7f38-91b4-7d44a22624b0",
 "relatedTitle": "无副标题活动-0902",
 "title": null,
 "subtitle": null,
 "description": "限时开团",
 "note": null,
 "banner": {"id": "bound/f038-banner.png", "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/f038-banner.png?Expires=1788328628&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6fpYOqVnJaneJfKcEhKAD6GNFSE%3D"},
 "createdAt": "2026-09-02T05:27:08.272022681Z",
 "updatedAt": "2026-09-02T05:27:08.272022681Z"
}
```

## step 2 app 周期推荐查询（type=ACTIVITY&period=OVULATION）

```bash
curl -s -i "http://localhost:8081/api/app/featured-cycle-items?type=ACTIVITY&period=OVULATION" \
  -H "X-API-Key: $APP_API_KEY"
```

```
HTTP/1.1 200
Content-Type: application/json
Content-Length: 1558
```

返回扁平数组共 2 条（另一条为同域历史数据），本用例命中条目：
```json
{
 "id": "01a06095-d8f0-713f-9f1e-e77a85416296",
 "period": ["OVULATION"],
 "type": "ACTIVITY",
 "banner": {"id": "bound/f038-banner.png", "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/f038-banner.png?Expires=1788328636&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=a9GGJilSocMMcoVFKbyVpxcjJCc%3D"},
 "targetId": "01a06095-c3a6-7f38-91b4-7d44a22624b0",
 "target": {
  "id": "01a06095-c3a6-7f38-91b4-7d44a22624b0",
  "title": "无副标题活动-0902",
  "subtitle": null,
  "cover": {"id": "bound/f038-cover.png", "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/f038-cover.png?Expires=1788328636&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=aK1hSrSd9UpiXqKeruqOb%2FxrA4Y%3D"},
  "level": "L2"
 },
 "title": null,
 "subtitle": null,
 "description": "限时开团",
 "note": null
}
```
