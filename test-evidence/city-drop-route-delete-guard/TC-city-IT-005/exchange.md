# TC-city-IT-005 exchange

环境：admin `http://localhost:8080`，app `http://localhost:8081`；`$TOKEN`/`$APIKEY` 脱敏。执行时间：2026-08-25

```bash
export ADMIN=http://localhost:8080 APP=http://localhost:8081
export TOKEN=... APIKEY=...
export CITYB=01a038c4-3d37-7128-b488-947afad9fc27
export RLID=01a038c4-3d4d-7461-9ff3-6392771b43f1
```

## step 0 — 前置夹具：上架城市 + 其下推荐清单

```bash
curl -s -X POST $ADMIN/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"回归城Bcdrdg8251","englishName":"RegCityBcdrdg8251","chineseProvince":"回归省","englishProvince":"RegProv","online":true}'
curl -s -X POST $ADMIN/api/admin/recommend-lists -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"title":"回归清单cdrdg8251","introduction":"regression fixture","cityId":"'"$CITYB"'","sortOrder":0}'
```

```json
{"id":"01a038c4-3d4d-7461-9ff3-6392771b43f1","title":"回归清单cdrdg8251","introduction":"regression fixture","cityId":"01a038c4-3d37-7128-b488-947afad9fc27","sortOrder":0,"merchants":[],"status":"ONLINE"}
```

## step 1 — 下架前 app 端可见

```bash
curl -s "$APP/api/app/recommend-lists?cityId=$CITYB" -H "X-API-Key: $APIKEY"
```
HTTP 200
```json
[{"id":"01a038c4-3d4d-7461-9ff3-6392771b43f1","title":"回归清单cdrdg8251","introduction":"regression fixture","cityId":"01a038c4-3d37-7128-b488-947afad9fc27","sortOrder":0}]
```

```bash
curl -s "$APP/api/app/recommend-lists/$RLID" -H "X-API-Key: $APIKEY"
```
HTTP 200
```json
{"id":"01a038c4-3d4d-7461-9ff3-6392771b43f1","title":"回归清单cdrdg8251","introduction":"regression fixture","cityId":"01a038c4-3d37-7128-b488-947afad9fc27","sortOrder":0,"merchants":[]}
```

## step 2 — admin 侧下架该城市

```bash
curl -s -X PUT "$ADMIN/api/admin/cities/$CITYB/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```
HTTP 200
```json
{"id":"01a038c4-3d37-7128-b488-947afad9fc27","chineseName":"回归城Bcdrdg8251","...":"...","online":false}
```

## step 3 — 下架后清单列表

```bash
curl -s "$APP/api/app/recommend-lists?cityId=$CITYB" -H "X-API-Key: $APIKEY"
```
HTTP 200
```json
[]
```

## step 4 — 下架后清单详情

```bash
curl -s "$APP/api/app/recommend-lists/$RLID" -H "X-API-Key: $APIKEY"
```
HTTP 404
```json
{"status":404,"error":"Not Found","message":"recommend list not found: 01a038c4-3d4d-7461-9ff3-6392771b43f1","path":"/api/app/recommend-lists/01a038c4-3d4d-7461-9ff3-6392771b43f1"}
```
