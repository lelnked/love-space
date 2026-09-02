# TC-featured-IT-010 POST /api/admin/featured-cycle-items 类型必填项缺失被拒绝 — 请求/响应存证

执行日期: 2026-09-02 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `m9p` 防撞名。清理请求（DELETE 夹具）不入存证。

## 前置: 三个互不相同且未被引用的关联实体
活动 01a0622c-3357-7d67-a93c-865d8caa8871 / 路线 01a0622c-3367-7d2b-ba0c-c922b895ce56 / 文章 01a0622c-336d-76ca-a6ad-6d74b0eda717

### 基线：分页总数
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=0&size=200" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[],"page":1,"size":30,"totalElements":0,"totalPages":0}
```

## Step 2: ROUTE 缺 subtitle
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ROUTE", "targetId": "01a0622c-3367-7d2b-ba0c-c922b895ce56", "title": "T", "description": "D", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"副标题不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 3: ACTIVITY 缺 description
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ACTIVITY", "targetId": "01a0622c-3357-7d67-a93c-865d8caa8871", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"推荐说明不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 4: ARTICLE 缺 banner
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "type": "ARTICLE", "targetId": "01a0622c-336d-76ca-a6ad-6d74b0eda717", "title": "T"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"banner 图片不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 5: 缺 type
```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phases": ["MENSTRUAL"], "targetId": "01a0622c-3357-7d67-a93c-865d8caa8871", "description": "D", "banner": "images/x.png"}'
```
实际响应: HTTP 400（Content-Type: application/json）
```json
{"status":400,"error":"Bad Request","message":"内容类型不能为空","path":"/api/admin/featured-cycle-items"}
```

## Step 6: 复核分页总数不变
```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=0&size=200" -H "Authorization: Bearer $TOKEN"
```
实际响应: HTTP 200（Content-Type: application/json）
```json
{"content":[],"page":1,"size":30,"totalElements":0,"totalPages":0}
```
