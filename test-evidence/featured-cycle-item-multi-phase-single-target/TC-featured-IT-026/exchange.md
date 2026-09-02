# TC-featured-IT-026 GET /api/app/featured-cycle-items?period= 周期过滤后无条目返回空数组 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: 库中仅 MENSTRUAL 下 1 个 ACTIVITY 上线条目

## Step 2: GET ?period=LUTEAL
```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=LUTEAL" -H "X-API-Key: $APP_API_KEY"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
[]
```
