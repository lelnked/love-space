# TC-operation-log-IT-001 请求/响应存证

> 创建城市后异步产生 city:create 留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1 创建城市

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/cities' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"chineseName":"留痕城市93c4056b","englishName":"LogCity","chineseProvince":"湖北","englishProvince":"Hubei"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-6fc6-7a09-9601-94a770b029a8",
 "chineseName": "留痕城市93c4056b",
 "englishName": "LogCity",
 "chineseProvince": "湖北",
 "englishProvince": "Hubei",
 "backgroundImage": null,
 "secondaryBackgroundImage": null,
 "editorNote": null,
 "online": false,
 "createdAt": "2026-10-24T17:51:57.381064624Z",
 "updatedAt": "2026-10-24T17:51:57.381064624Z"
}
```

## step 2 轮询 city 日志（存证为最后一次）

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=city&page=1&size=20' \
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
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 2,
 "totalPages": 1
}
```
