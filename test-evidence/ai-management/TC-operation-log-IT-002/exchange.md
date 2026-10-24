# TC-operation-log-IT-002 请求/响应存证

> 业务校验失败（400）时不产生留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1 基线 T0

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=city&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a16f0a-6fcd-7052-8bab-024dcadb5052",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:57.388974Z"
  },
  {
   "id": "f602fc35-2917-429a-8412-8b2195e1a53d",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:41:40.465669Z"
  },
  {
   "id": "5ffd043e-f4d3-4bc5-811a-8e8c74a08344",
   "username": "alice",
   "module": "city",
   "action": "update",
   "target": null,
   "createdAt": "2026-10-24T17:41:40.465669Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 3,
 "totalPages": 1
}
```

## step 2 创建城市（中文名为空）

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/cities' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"chineseName":"","englishName":"X","chineseProvince":"X","englishProvince":"X"}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "中文名不能为空",
 "path": "/api/admin/cities"
}
```

## step 3 2s 后

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=city&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a16f0a-6fcd-7052-8bab-024dcadb5052",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:57.388974Z"
  },
  {
   "id": "f602fc35-2917-429a-8412-8b2195e1a53d",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:41:40.465669Z"
  },
  {
   "id": "5ffd043e-f4d3-4bc5-811a-8e8c74a08344",
   "username": "alice",
   "module": "city",
   "action": "update",
   "target": null,
   "createdAt": "2026-10-24T17:41:40.465669Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 3,
 "totalPages": 1
}
```

> 备注: 用例写 `total`，实际分页字段为 `totalElements`（PageResponse），按 totalElements 核对。
