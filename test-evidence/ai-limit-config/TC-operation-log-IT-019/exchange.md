# TC-operation-log-IT-019 请求/响应存证

> 字符串路径参数作为 target
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 基线日志

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=ai-limit&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a17740-886b-760c-92e2-507aa0e70100",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "PER_ANON_DAILY",
   "createdAt": "2026-10-26T08:08:00.363323Z"
  },
  {
   "id": "01a17740-883f-703a-8ca8-163991f4b376",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "PER_ANON_DAILY",
   "createdAt": "2026-10-26T08:08:00.318938Z"
  },
  {
   "id": "01a17740-881e-7d43-9c9e-ce6c1877a88d",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:08:00.286716Z"
  },
  {
   "id": "01a17740-8810-765d-b1a5-e05543de8b5b",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "PER_ANON_DAILY",
   "createdAt": "2026-10-26T08:08:00.272333Z"
  },
  {
   "id": "01a17740-7ff2-7a64-b37a-9118c8c19264",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:07:58.19458Z"
  },
  {
   "id": "01a17740-7eef-7331-8e33-d77b4a7ae3d8",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:07:57.935135Z"
  },
  {
   "id": "01a17740-7ede-7d1f-bd06-1f1740690f83",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "PER_ANON_DAILY",
   "createdAt": "2026-10-26T08:07:57.918731Z"
  },
  {
   "id": "01a17740-7ece-750c-8411-6246643d174b",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:07:57.902239Z"
  },
  {
   "id": "01a17740-7ebf-7323-a9a7-ca433f169115",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:07:57.887114Z"
  },
  {
   "id": "01a17740-7ea0-7500-aaea-f40c2475988e",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:07:57.856231Z"
  },
  {
   "id": "01a17740-7e3b-7621-910a-977327b06abd",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:07:57.755311Z"
  },
  {
   "id": "01a17740-7e2c-7849-af15-e9572b2a4c06",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "PER_ANON_DAILY",
   "createdAt": "2026-10-26T08:07:57.740454Z"
  },
  {
   "id": "01a17740-7e11-74ac-bba7-b17a9981865c",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "PER_ANON_DAILY",
   "createdAt": "2026-10-26T08:07:57.713215Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 13,
 "totalPages": 1
}
```

## step 2 PUT 限流 GLOBAL_DAILY=4500

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/GLOBAL_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value": 4500}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "GLOBAL_DAILY",
 "name": "全站每日上限",
 "value": 4500,
 "updatedBy": "admin",
 "updatedAt": "2026-10-26T16:08:00.390532622+08:00"
}
```

## step 3 轮询日志（第 1 次命中）module=ai-limit

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=ai-limit&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a17740-888a-7479-a6e3-4441a5f58e94",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:08:00.394219Z"
  },
  {
   "id": "01a17740-886b-760c-92e2-507aa0e70100",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "PER_ANON_DAILY",
   "createdAt": "2026-10-26T08:08:00.363323Z"
  },
  {
   "id": "01a17740-883f-703a-8ca8-163991f4b376",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "PER_ANON_DAILY",
   "createdAt": "2026-10-26T08:08:00.318938Z"
  },
  {
   "id": "01a17740-881e-7d43-9c9e-ce6c1877a88d",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:08:00.286716Z"
  },
  {
   "id": "01a17740-8810-765d-b1a5-e05543de8b5b",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "PER_ANON_DAILY",
   "createdAt": "2026-10-26T08:08:00.272333Z"
  },
  {
   "id": "01a17740-7ff2-7a64-b37a-9118c8c19264",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:07:58.19458Z"
  },
  {
   "id": "01a17740-7eef-7331-8e33-d77b4a7ae3d8",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:07:57.935135Z"
  },
  {
   "id": "01a17740-7ede-7d1f-bd06-1f1740690f83",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "PER_ANON_DAILY",
   "createdAt": "2026-10-26T08:07:57.918731Z"
  },
  {
   "id": "01a17740-7ece-750c-8411-6246643d174b",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:07:57.902239Z"
  },
  {
   "id": "01a17740-7ebf-7323-a9a7-ca433f169115",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:07:57.887114Z"
  },
  {
   "id": "01a17740-7ea0-7500-aaea-f40c2475988e",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:07:57.856231Z"
  },
  {
   "id": "01a17740-7e3b-7621-910a-977327b06abd",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "GLOBAL_DAILY",
   "createdAt": "2026-10-26T08:07:57.755311Z"
  },
  {
   "id": "01a17740-7e2c-7849-af15-e9572b2a4c06",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "PER_ANON_DAILY",
   "createdAt": "2026-10-26T08:07:57.740454Z"
  },
  {
   "id": "01a17740-7e11-74ac-bba7-b17a9981865c",
   "username": "admin",
   "module": "ai-limit",
   "action": "update",
   "target": "PER_ANON_DAILY",
   "createdAt": "2026-10-26T08:07:57.713215Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 14,
 "totalPages": 1
}
```

## step 4 psql 查该日志 payload

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT payload FROM loves_operation_log WHERE id='"'"'01a17740-888a-7479-a6e3-4441a5f58e94'"'"';'
```

```
{"value": 4500}
```

## step 5 收尾 PUT GLOBAL_DAILY=5000

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/GLOBAL_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value": 5000}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "GLOBAL_DAILY",
 "name": "全站每日上限",
 "value": 5000,
 "updatedBy": "admin",
 "updatedAt": "2026-10-26T16:08:00.453726696+08:00"
}
```
