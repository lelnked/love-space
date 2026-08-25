# TC-city-IT-008 exchange

环境：admin `http://localhost:8080`，app `http://localhost:8081`；`$TOKEN`/`$APIKEY` 脱敏。执行时间：2026-08-25

> ⚠️ 步骤适配说明：用例步骤写的是 `GET /api/app/routes?cityId={cityId}`，但自 change
> `route-remove-city-id` 起路线不再持有 `cityId`，admin 侧改为自由文本 `cityName`，app 侧
> 列表过滤参数为 `?cityName=`。本次按现行接口用 `?cityName=` 等价执行，断言语义不变。

```bash
export ADMIN=http://localhost:8080 APP=http://localhost:8081
export TOKEN=... APIKEY=...
export CITYD=01a038c4-d2d6-79cb-a4c3-a730dd11dcb5
export CITYD_NAME='回归城Dcdrdg8251'
export AMBID=01a038c4-d2e9-74a1-8046-e398a173b87b
export RTID=01a038c4-d308-7441-a660-c7894e2d4754
```

## step 0 — 前置夹具：上架城市 + 上线大使 + 该城市名下的路线

```bash
curl -s -X POST $ADMIN/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"回归城Dcdrdg8251","englishName":"RegCityDcdrdg8251","chineseProvince":"回归省","englishProvince":"RegProv","online":true}'
curl -s -X POST $ADMIN/api/admin/ambassadors -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"avatar":"images/regamb-cdrdg8251.png","name":"回归大使cdrdg8251","tags":["回归"],"weight":0,"online":true}'
curl -s -X POST $ADMIN/api/admin/routes -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"title":"回归路线cdrdg8251","cityName":"回归城Dcdrdg8251","thumbnail":"images/regrt-cdrdg8251.png","images":["images/regrt-cdrdg8251.png"],"ambassadorId":"'"$AMBID"'","sortOrder":0}'
```
路线创建 HTTP 200：
```json
{"id":"01a038c4-d308-7441-a660-c7894e2d4754","sortOrder":0,"title":"回归路线cdrdg8251","cityName":"回归城Dcdrdg8251","ambassadorId":"01a038c4-d2e9-74a1-8046-e398a173b87b","ambassadorName":"回归大使cdrdg8251","spots":[]}
```

## step 1 — 城市上架时 app 端可见（基线）

```bash
curl -s -G "$APP/api/app/routes" --data-urlencode "cityName=$CITYD_NAME" -H "X-API-Key: $APIKEY"
curl -s "$APP/api/app/routes/$RTID" -H "X-API-Key: $APIKEY"
```
列表 HTTP 200，n=1，含目标路线；详情 HTTP 200。

## step 2 — admin 侧下架该城市

```bash
curl -s -X PUT "$ADMIN/api/admin/cities/$CITYD/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```
HTTP 200

## step 3 — 下架后路线列表

```bash
curl -s -G "$APP/api/app/routes" --data-urlencode "cityName=$CITYD_NAME" -H "X-API-Key: $APIKEY"
```
HTTP 200，n=1，仍含目标路线。

## step 4 — 下架后路线详情

```bash
curl -s "$APP/api/app/routes/$RTID" -H "X-API-Key: $APIKEY"
```
HTTP 200

## step 5 — 大使下线后重复步骤 3、4

```bash
curl -s -X PUT "$ADMIN/api/admin/ambassadors/$AMBID/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
curl -s -G "$APP/api/app/routes" --data-urlencode "cityName=$CITYD_NAME" -H "X-API-Key: $APIKEY"
curl -s "$APP/api/app/routes/$RTID" -H "X-API-Key: $APIKEY"
```
大使下线 HTTP 200；列表 HTTP 200，n=0；详情 HTTP 404：
```json
{"status":404,"error":"Not Found","message":"route not found: 01a038c4-d308-7441-a660-c7894e2d4754","path":"/api/app/routes/01a038c4-d308-7441-a660-c7894e2d4754"}
```
