# TC-featured-IT-034 GET /api/app/featured-cycle-items 活动类条目下发活动基础信息 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## Step 1 前置: 上线活动（含图片、标题、subtitle=山野轻装、level），MENSTRUAL 上线 ACTIVITY 条目
活动 id=01a0622f-dc97-70b5-93a0-ac37dc5efcb1 title=act-m9pf43c12 subtitle=山野轻装 level=L1；条目 id=01a0622f-dc9b-702a-9722-c80f2e25bb98

## Step 2: GET ?type=ACTIVITY
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?type=ACTIVITY" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[{"id":"01a0622f-dc9b-702a-9722-c80f2e25bb98","period":["MENSTRUAL"],"type":"ACTIVITY","banner":{"id":"bound/b-bn-m9pf43c13.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/b-bn-m9pf43c13.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"targetId":"01a0622f-dc97-70b5-93a0-ac37dc5efcb1","target":{"id":"01a0622f-dc97-70b5-93a0-ac37dc5efcb1","title":"act-m9pf43c12","subtitle":"山野轻装","cover":{"id":"bound/act-m9pf43c12.png","url":"https://placeholder.oss-cn-chengdu.aliyuncs.com/bound/act-m9pf43c12.png?Expires=1788355498&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"level":"L1"},"title":null,"subtitle":null,"description":"条目推荐说明","note":null}]
```
