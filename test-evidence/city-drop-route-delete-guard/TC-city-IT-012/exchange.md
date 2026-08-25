# TC-city-IT-012 exchange

环境：app `http://localhost:8081`；`$APIKEY` 脱敏。执行时间：2026-08-25

## 前置：创建一个 online=false 的城市

```bash
curl -s -X POST $ADMIN/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"回归城Offcdrdg8251","englishName":"RegCityOffcdrdg8251","chineseProvince":"回归省","englishProvince":"RegProv","online":false}'
```
```json
{"id":"01a038c3-fd52-7973-8422-2b826ed14546","chineseName":"回归城Offcdrdg8251","backgroundImage":null,"editorNote":null,"online":false}
```

## step 1 — 未上架城市详情

```bash
curl -s $APP/api/app/cities/01a038c3-fd52-7973-8422-2b826ed14546 -H "X-API-Key: $APIKEY"
```
HTTP 404
```json
{"status":404,"error":"Not Found","message":"city not found: 01a038c3-fd52-7973-8422-2b826ed14546","path":"/api/app/cities/01a038c3-fd52-7973-8422-2b826ed14546"}
```

## step 2 — 不存在的随机 UUID

```bash
curl -s $APP/api/app/cities/00000000-0000-4000-8000-0000000c0de1 -H "X-API-Key: $APIKEY"
```
HTTP 404
```json
{"status":404,"error":"Not Found","message":"city not found: 00000000-0000-4000-8000-0000000c0de1","path":"/api/app/cities/00000000-0000-4000-8000-0000000c0de1"}
```
