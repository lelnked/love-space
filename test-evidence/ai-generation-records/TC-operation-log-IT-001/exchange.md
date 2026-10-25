# TC-operation-log-IT-001 请求/响应存证

> 创建城市后异步产生 city:create 留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。

## step 1 记录服务器当前时刻（判定新记录用）（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'SELECT to_char(now() AT TIME ZONE '"'"'UTC'"'"', '"'"'YYYY-MM-DD"T"HH24:MI:SS'"'"');'
```

```
2026-10-25T13:30:59
```

## step 2 创建城市

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/cities' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"chineseName": "留痕城市5468dcb7", "englishName": "LogCity", "chineseProvince": "湖北", "englishProvince": "Hubei"}'
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "01a17341-df3e-7338-873c-397410b75055",
 "chineseName": "留痕城市5468dcb7",
 "englishName": "LogCity",
 "chineseProvince": "湖北",
 "englishProvince": "Hubei",
 "backgroundImage": null,
 "secondaryBackgroundImage": null,
 "editorNote": null,
 "online": false,
 "createdAt": "2026-10-25T13:30:59.262146367Z",
 "updatedAt": "2026-10-25T13:30:59.262146367Z"
}
```

## step 3 轮询 city 日志（最多 20 次/100ms，存证为最后一次）

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=city&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "01a17341-df42-7583-9526-491483210f94",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:59.266278Z"
  },
  {
   "id": "01a17341-5b6b-70ed-92c3-5b1e5e093abc",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:25.515Z"
  },
  {
   "id": "46a8ca8c-f3e4-46eb-9d25-aa230f744226",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:16:15.839224Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 3,
 "totalPages": 1
}
```
