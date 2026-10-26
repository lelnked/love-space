# TC-operation-log-IT-006 请求/响应存证

> 更新类操作的 target 为目标城市 id
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 创建城市 C

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/cities' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"chineseName": "留痕城市Uc015d5fa", "englishName": "LogCityU", "chineseProvince": "湖北", "englishProvince": "Hubei"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a17740-8fdc-7968-b096-0ffd2f178a89",
 "chineseName": "留痕城市Uc015d5fa",
 "englishName": "LogCityU",
 "chineseProvince": "湖北",
 "englishProvince": "Hubei",
 "backgroundImage": null,
 "secondaryBackgroundImage": null,
 "editorNote": null,
 "online": false,
 "createdAt": "2026-10-26T08:08:02.268534185Z",
 "updatedAt": "2026-10-26T08:08:02.268534185Z"
}
```

## step 2 基线日志

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=city&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a17740-8fe0-78ae-8aec-a4904f9ce152",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-26T08:08:02.272491Z"
  },
  {
   "id": "01a17740-8fb6-70f6-af0e-7f209dd4c40f",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-26T08:08:02.230002Z"
  },
  {
   "id": "01a1773a-5412-7f0b-b179-67ad143c201e",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-26T08:01:13.745019Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 3,
 "totalPages": 1
}
```

## step 3 更新城市 C

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/cities/01a17740-8fdc-7968-b096-0ffd2f178a89' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"chineseName": "留痕城市U改c015d5fa", "englishName": "LogCityU", "chineseProvince": "湖北", "englishProvince": "Hubei"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a17740-8fdc-7968-b096-0ffd2f178a89",
 "chineseName": "留痕城市U改c015d5fa",
 "englishName": "LogCityU",
 "chineseProvince": "湖北",
 "englishProvince": "Hubei",
 "backgroundImage": null,
 "secondaryBackgroundImage": null,
 "editorNote": null,
 "online": false,
 "createdAt": "2026-10-26T08:08:02.268534Z",
 "updatedAt": "2026-10-26T08:08:02.268534Z"
}
```

## step 4 轮询日志（第 1 次命中）module=city

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=city&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a17740-9007-7aba-8031-b3e9eec143ee",
   "username": "admin",
   "module": "city",
   "action": "update",
   "target": "01a17740-8fdc-7968-b096-0ffd2f178a89",
   "createdAt": "2026-10-26T08:08:02.311606Z"
  },
  {
   "id": "01a17740-8fe0-78ae-8aec-a4904f9ce152",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-26T08:08:02.272491Z"
  },
  {
   "id": "01a17740-8fb6-70f6-af0e-7f209dd4c40f",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-26T08:08:02.230002Z"
  },
  {
   "id": "01a1773a-5412-7f0b-b179-67ad143c201e",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-26T08:01:13.745019Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 4,
 "totalPages": 1
}
```
