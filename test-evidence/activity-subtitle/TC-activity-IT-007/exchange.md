# TC-activity-IT-007 请求/响应存证

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

## step 1 前置核对

- 上线活动：admin 分页（3 页 × 30）共 83 条，其中 online=true 共 52 条；含已填副标题的活动 A `01a0608e-9b39-7d5c-8cfa-5fb3fedd3879`（subtitle="山野轻装"，本轮由 TC-activity-IT-024 前置创建）
- 已下架城市：存在 1 个（如 `01a0608a-2dfe-74f7-b7f3-abee95a40a10`），用于验证列表不因城市上架状态被筛掉

```bash
curl -s "http://localhost:21423/api/admin/activities/page?page=1&size=30" -H "Authorization: Bearer $TOKEN"   # 同理 page=2 / page=3
curl -s "http://localhost:21423/api/admin/cities/page?page=0&size=200" -H "Authorization: Bearer $TOKEN"
```

## step 2 app 活动列表（不带任何查询参数）

```bash
curl -s "http://localhost:8081/api/app/activities" -H "X-API-Key: $APP_API_KEY"
```

HTTP 200 / `Content-Type: application/json`，返回扁平数组共 52 项。含副标题的活动 A 列表项：
```json
{
 "id": "01a0608e-9b39-7d5c-8cfa-5fb3fedd3879",
 "title": "副标题活动A-0902",
 "subtitle": "山野轻装",
 "images": [
  {
   "id": "bound/a024-A.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a024-A.png?Expires=1788328184&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=jcMn5vD%2B4YNuzouhIvilVOXFO1A%3D"
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
```

列表项字段集合（并集）：`id`, `images`, `introduction`, `level`, `periods`, `subtitle`, `tags`, `title`
