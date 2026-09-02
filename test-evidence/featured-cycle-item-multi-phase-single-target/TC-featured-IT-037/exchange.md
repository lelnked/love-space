# TC-featured-IT-037 GET /api/app/featured-cycle-items 活动无图片时 target.cover 为 null — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 上线活动（图片清空为无图）01a06230-287e-71b5-9692-494dd7c5c1f6；FOLLICULAR 上线 ACTIVITY 条目 01a06230-28fc-7246-8eee-a4a1f78106cc
`ActivityUpsertRequest.images` 有 @NotEmpty 约束，接口建不出无图活动；先常规建活动再用一条 SQL 清空图片列（纯测试数据准备，不涉被测行为）：

```bash
PGPASSWORD=$PGPASSWORD psql -h localhost -p 25432 -U iris -d love_space -c "UPDATE loves_activity SET images='[]'::jsonb WHERE id='01a06230-287e-71b5-9692-494dd7c5c1f6';"
```

## Step 2: GET ?type=ACTIVITY&period=FOLLICULAR
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?type=ACTIVITY&period=FOLLICULAR" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a06230-28fc-7246-8eee-a4a1f78106cc","period":["FOLLICULAR"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9p158c2.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9p158c2.png?Expires=1788355518&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a06230-287e-71b5-9692-494dd7c5c1f6","target":{"id":"01a06230-287e-71b5-9692-494dd7c5c1f6","title":"act-m9p158c1","subtitle":null,"cover":null,"level":"L1"},"title":null,"subtitle":null,"description":"推荐说明","note":null}]
```
