# TC-system-config-IT-012 请求/响应存证

> 批量查询 keys 缺失、为空与 50/51 边界
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 app GET 不带 keys

```bash
curl -s -i -X GET http://localhost:8081/api/app/system-configs \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "缺少必填参数 'keys'",
 "path": "/api/app/system-configs"
}
```

## step 2 app GET keys=%20,%20,

```bash
curl -s -i -X GET 'http://localhost:8081/api/app/system-configs?keys=%20,%20,' \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "keys 须为 1~50 个",
 "path": "/api/app/system-configs"
}
```

## step 3 app GET 50 个 key

```bash
curl -s -i -X GET 'http://localhost:8081/api/app/system-configs?keys=k1,k2,k3,k4,k5,k6,k7,k8,k9,k10,k11,k12,k13,k14,k15,k16,k17,k18,k19,k20,k21,k22,k23,k24,k25,k26,k27,k28,k29,k30,k31,k32,k33,k34,k35,k36,k37,k38,k39,k40,k41,k42,k43,k44,k45,k46,k47,k48,k49,k50' \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 200（Content-Type: application/json）

```json
[]
```

## step 4 app GET 51 个 key

```bash
curl -s -i -X GET 'http://localhost:8081/api/app/system-configs?keys=k1,k2,k3,k4,k5,k6,k7,k8,k9,k10,k11,k12,k13,k14,k15,k16,k17,k18,k19,k20,k21,k22,k23,k24,k25,k26,k27,k28,k29,k30,k31,k32,k33,k34,k35,k36,k37,k38,k39,k40,k41,k42,k43,k44,k45,k46,k47,k48,k49,k50,k51' \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "keys 须为 1~50 个",
 "path": "/api/app/system-configs"
}
```
