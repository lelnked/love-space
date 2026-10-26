# TC-operation-log-IT-007 请求/响应存证

> 嵌套资源（商户评价）的 target 取父级商户 id
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 psql 取已有商户 id M

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -At -c 'SELECT id FROM loves_merchant ORDER BY id LIMIT 1;'
```

```
01a17344-1338-79b7-a888-3221df236f96
```

## step 2 创建评价 R

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/merchants/01a17344-1338-79b7-a888-3221df236f96/reviews' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"nickname": "张三", "title": "很好", "content": "体验不错", "sortOrder": 0, "recommended": false}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a17740-9050-795b-945f-c1caf9c633e8",
 "merchantId": "01a17344-1338-79b7-a888-3221df236f96",
 "nickname": "张三",
 "title": "很好",
 "content": "体验不错",
 "sortOrder": 0,
 "recommended": false,
 "createdAt": "2026-10-26T08:08:02.383016542Z",
 "updatedAt": "2026-10-26T08:08:02.383016542Z"
}
```

## step 3 基线日志

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=merchant-review&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a17740-905a-7541-a0e4-c76014ac8ae2",
   "username": "admin",
   "module": "merchant-review",
   "action": "create",
   "target": "01a17344-1338-79b7-a888-3221df236f96",
   "createdAt": "2026-10-26T08:08:02.394274Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 4 更新评价 R

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/merchants/01a17344-1338-79b7-a888-3221df236f96/reviews/01a17740-9050-795b-945f-c1caf9c633e8' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"nickname": "张三", "title": "更好", "content": "体验不错", "sortOrder": 0, "recommended": false}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a17740-9050-795b-945f-c1caf9c633e8",
 "merchantId": "01a17344-1338-79b7-a888-3221df236f96",
 "nickname": "张三",
 "title": "更好",
 "content": "体验不错",
 "sortOrder": 0,
 "recommended": false,
 "createdAt": "2026-10-26T08:08:02.383017Z",
 "updatedAt": "2026-10-26T08:08:02.383017Z"
}
```

## step 5 轮询日志（第 1 次命中）module=merchant-review

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=merchant-review&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a17740-907c-71df-8352-6edf614ae4d7",
   "username": "admin",
   "module": "merchant-review",
   "action": "update",
   "target": "01a17344-1338-79b7-a888-3221df236f96",
   "createdAt": "2026-10-26T08:08:02.428056Z"
  },
  {
   "id": "01a17740-905a-7541-a0e4-c76014ac8ae2",
   "username": "admin",
   "module": "merchant-review",
   "action": "create",
   "target": "01a17344-1338-79b7-a888-3221df236f96",
   "createdAt": "2026-10-26T08:08:02.394274Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 2,
 "totalPages": 1
}
```

## step 6 收尾删除评价 R

```bash
curl -s -i -X DELETE 'http://localhost:21423/api/admin/merchants/01a17344-1338-79b7-a888-3221df236f96/reviews/01a17740-9050-795b-945f-c1caf9c633e8' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: -）

```json
(空)
```
