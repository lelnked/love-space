# TC-featured-IT-034 请求/响应存证

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

## step 1 前置：上线活动（含图片、标题、副标题、难度等级）+ MENSTRUAL 下的上线 ACTIVITY 条目

活动 `01a0608e-9b39-7d5c-8cfa-5fb3fedd3879`（title="副标题活动A-0902"，subtitle="山野轻装"，level=L2，1 张图片，本轮由 TC-activity-IT-024 前置创建）。

```bash
curl -s -X POST "http://localhost:21423/api/admin/featured-cycle-items" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "banner": "images/f034-banner.png", "sortOrder": 1, "online": true, "targetId": "01a0608e-9b39-7d5c-8cfa-5fb3fedd3879", "description": "周期推荐说明-034", "note": "活动说明-034"}'
```

HTTP 200
```json
{
 "id": "01a06090-346f-7598-a210-ae6e36b7f5ab",
 "phase": "MENSTRUAL",
 "type": "ACTIVITY",
 "sortOrder": 1,
 "online": true,
 "targetId": "01a0608e-9b39-7d5c-8cfa-5fb3fedd3879",
 "relatedTitle": "副标题活动A-0902",
 "title": null,
 "subtitle": null,
 "description": "周期推荐说明-034",
 "note": "活动说明-034",
 "banner": {
  "id": "bound/f034-banner.png",
  "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/f034-banner.png?Expires=1788328258&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=eTQFKCyC%2BoagyIbdOC3e%2BN2Ha5E%3D"
 },
 "createdAt": "2026-09-02T05:20:58.478066891Z",
 "updatedAt": "2026-09-02T05:20:58.478066891Z"
}
```

## step 2 app 周期推荐查询（type=ACTIVITY）

```bash
curl -s "http://localhost:8081/api/app/featured-cycle-items?type=ACTIVITY" -H "X-API-Key: $APP_API_KEY"
```

HTTP 200 / `Content-Type: application/json`。命中条目：
```json
{
 "id": "01a06090-346f-7598-a210-ae6e36b7f5ab",
 "period": [
  "MENSTRUAL"
 ],
 "type": "ACTIVITY",
 "banner": {
  "id": "bound/f034-banner.png",
  "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/f034-banner.png?Expires=1788328258&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=eTQFKCyC%2BoagyIbdOC3e%2BN2Ha5E%3D"
 },
 "targetId": "01a0608e-9b39-7d5c-8cfa-5fb3fedd3879",
 "target": {
  "id": "01a0608e-9b39-7d5c-8cfa-5fb3fedd3879",
  "title": "副标题活动A-0902",
  "subtitle": "山野轻装",
  "cover": {
   "id": "bound/a024-A.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a024-A.png?Expires=1788328258&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=m4mXy0eYB5Za4FmgZ3kqzAjJE%2FE%3D"
  },
  "level": "L2"
 },
 "title": null,
 "subtitle": null,
 "description": "周期推荐说明-034",
 "note": "活动说明-034"
}
```
