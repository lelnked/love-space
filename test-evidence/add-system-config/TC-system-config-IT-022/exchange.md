# TC-system-config-IT-022 请求/响应存证

> 批量查询 keys 缺失、为空与 50/51 边界
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 不带 keys

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

## step 2 keys=

```bash
curl -s -i -X GET 'http://localhost:8081/api/app/system-configs?keys=' \
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

## step 3 keys=%20,%20,

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

## step 4 50 个 key

```bash
curl -s -i -X GET 'http://localhost:8081/api/app/system-configs?keys=k1_m3pzjm,k2_m3pzjm,k3_m3pzjm,k4_m3pzjm,k5_m3pzjm,k6_m3pzjm,k7_m3pzjm,k8_m3pzjm,k9_m3pzjm,k10_m3pzjm,k11_m3pzjm,k12_m3pzjm,k13_m3pzjm,k14_m3pzjm,k15_m3pzjm,k16_m3pzjm,k17_m3pzjm,k18_m3pzjm,k19_m3pzjm,k20_m3pzjm,k21_m3pzjm,k22_m3pzjm,k23_m3pzjm,k24_m3pzjm,k25_m3pzjm,k26_m3pzjm,k27_m3pzjm,k28_m3pzjm,k29_m3pzjm,k30_m3pzjm,k31_m3pzjm,k32_m3pzjm,k33_m3pzjm,k34_m3pzjm,k35_m3pzjm,k36_m3pzjm,k37_m3pzjm,k38_m3pzjm,k39_m3pzjm,k40_m3pzjm,k41_m3pzjm,k42_m3pzjm,k43_m3pzjm,k44_m3pzjm,k45_m3pzjm,k46_m3pzjm,k47_m3pzjm,k48_m3pzjm,k49_m3pzjm,k50_m3pzjm' \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 200（Content-Type: application/json）

```json
[]
```

## step 5 51 个 key

```bash
curl -s -i -X GET 'http://localhost:8081/api/app/system-configs?keys=k1_m3pzjm,k2_m3pzjm,k3_m3pzjm,k4_m3pzjm,k5_m3pzjm,k6_m3pzjm,k7_m3pzjm,k8_m3pzjm,k9_m3pzjm,k10_m3pzjm,k11_m3pzjm,k12_m3pzjm,k13_m3pzjm,k14_m3pzjm,k15_m3pzjm,k16_m3pzjm,k17_m3pzjm,k18_m3pzjm,k19_m3pzjm,k20_m3pzjm,k21_m3pzjm,k22_m3pzjm,k23_m3pzjm,k24_m3pzjm,k25_m3pzjm,k26_m3pzjm,k27_m3pzjm,k28_m3pzjm,k29_m3pzjm,k30_m3pzjm,k31_m3pzjm,k32_m3pzjm,k33_m3pzjm,k34_m3pzjm,k35_m3pzjm,k36_m3pzjm,k37_m3pzjm,k38_m3pzjm,k39_m3pzjm,k40_m3pzjm,k41_m3pzjm,k42_m3pzjm,k43_m3pzjm,k44_m3pzjm,k45_m3pzjm,k46_m3pzjm,k47_m3pzjm,k48_m3pzjm,k49_m3pzjm,k50_m3pzjm,k51_m3pzjm' \
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

## step 6 51 项去重后 50

```bash
curl -s -i -X GET 'http://localhost:8081/api/app/system-configs?keys=k1_m3pzjm,k2_m3pzjm,k3_m3pzjm,k4_m3pzjm,k5_m3pzjm,k6_m3pzjm,k7_m3pzjm,k8_m3pzjm,k9_m3pzjm,k10_m3pzjm,k11_m3pzjm,k12_m3pzjm,k13_m3pzjm,k14_m3pzjm,k15_m3pzjm,k16_m3pzjm,k17_m3pzjm,k18_m3pzjm,k19_m3pzjm,k20_m3pzjm,k21_m3pzjm,k22_m3pzjm,k23_m3pzjm,k24_m3pzjm,k25_m3pzjm,k26_m3pzjm,k27_m3pzjm,k28_m3pzjm,k29_m3pzjm,k30_m3pzjm,k31_m3pzjm,k32_m3pzjm,k33_m3pzjm,k34_m3pzjm,k35_m3pzjm,k36_m3pzjm,k37_m3pzjm,k38_m3pzjm,k39_m3pzjm,k40_m3pzjm,k41_m3pzjm,k42_m3pzjm,k43_m3pzjm,k44_m3pzjm,k45_m3pzjm,k46_m3pzjm,k47_m3pzjm,k48_m3pzjm,k49_m3pzjm,k50_m3pzjm,k1_m3pzjm' \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 200（Content-Type: application/json）

```json
[]
```
