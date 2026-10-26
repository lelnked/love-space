# TC-ai-IT-094 请求/响应存证

> 修改限流配置产生 ai-limit:update 留痕
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
 "totalElements": 8,
 "totalPages": 1
}
```

## step 2 PUT 限流 GLOBAL_DAILY=4000

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/GLOBAL_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value": 4000}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "GLOBAL_DAILY",
 "name": "全站每日上限",
 "value": 4000,
 "updatedBy": "admin",
 "updatedAt": "2026-10-26T16:07:58.190498827+08:00"
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
 "totalElements": 9,
 "totalPages": 1
}
```

## step 4 步骤4 GET 查看

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/limits' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
[
 {
  "key": "GLOBAL_DAILY",
  "name": "全站每日上限",
  "value": 4000,
  "updatedBy": "admin",
  "updatedAt": "2026-10-26T08:07:58.190499Z"
 },
 {
  "key": "PER_ANON_DAILY",
  "name": "单个匿名 ID 每日上限",
  "value": 50,
  "updatedBy": "admin",
  "updatedAt": "2026-10-26T08:07:57.915148Z"
 }
]
```

## step 5 步骤4 2s 后复查日志总数

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=ai-limit&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
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
 "totalElements": 9,
 "totalPages": 1
}
```

## step 6 收尾 PUT PER_ANON_DAILY=50

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/limits/PER_ANON_DAILY' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"value": 50}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "PER_ANON_DAILY",
 "name": "单个匿名 ID 每日上限",
 "value": 50,
 "updatedBy": "admin",
 "updatedAt": "2026-10-26T16:08:00.268815096+08:00"
}
```

## step 7 收尾 PUT GLOBAL_DAILY=5000

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
 "updatedAt": "2026-10-26T16:08:00.283173836+08:00"
}
```
