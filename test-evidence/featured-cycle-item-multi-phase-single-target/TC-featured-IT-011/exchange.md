# TC-featured-IT-011 POST /api/admin/featured-cycle-items 关联实体不存在被拒绝 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: 一篇真实文章 01a0622c-338d-7791-976a-4e296213855f + 不存在的 UUID 16243e82-9e60-4bb8-a417-b38caf5f1ef4

## Step 1: ACTIVITY + 不存在 UUID
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ACTIVITY", "targetId": "16243e82-9e60-4bb8-a417-b38caf5f1ef4", "description": "D", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"关联活动不存在：16243e82-9e60-4bb8-a417-b38caf5f1ef4","path":"/api/admin/featured-cycle-items"}
```

## Step 2: ROUTE + 不存在 UUID
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["OVULATION"], "type": "ROUTE", "targetId": "16243e82-9e60-4bb8-a417-b38caf5f1ef4", "title": "T", "subtitle": "S", "description": "D", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"关联路线不存在：16243e82-9e60-4bb8-a417-b38caf5f1ef4","path":"/api/admin/featured-cycle-items"}
```

## Step 3: ARTICLE + 不存在 UUID
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["LUTEAL"], "type": "ARTICLE", "targetId": "16243e82-9e60-4bb8-a417-b38caf5f1ef4", "title": "T", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"关联文章不存在：16243e82-9e60-4bb8-a417-b38caf5f1ef4","path":"/api/admin/featured-cycle-items"}
```

## Step 4: ACTIVITY + 文章 id（跨表不命中）
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ACTIVITY", "targetId": "01a0622c-338d-7791-976a-4e296213855f", "description": "D", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"关联活动不存在：01a0622c-338d-7791-976a-4e296213855f","path":"/api/admin/featured-cycle-items"}
```

## 复核：分页总数
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=0&size=200" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[],"page":1,"size":30,"totalElements":0,"totalPages":0}
```
