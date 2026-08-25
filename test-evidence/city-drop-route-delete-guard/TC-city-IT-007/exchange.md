# TC-city-IT-007 exchange

环境：admin `http://localhost:8080`，app `http://localhost:8081`；`$TOKEN`/`$APIKEY` 脱敏。执行时间：2026-08-25

```bash
export ADMIN=http://localhost:8080 APP=http://localhost:8081
export TOKEN=... APIKEY=...
export CITYC=01a038c4-7af3-790c-89cd-7db229bb8e54
export FIID=01a038c4-7b0a-7010-9388-ab94ba16ac0c
```

## step 0 — 前置夹具：上架城市 + 关联的上线精选推荐

```bash
curl -s -X POST $ADMIN/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"回归城Ccdrdg8251","englishName":"RegCityCcdrdg8251","chineseProvince":"回归省","englishProvince":"RegProv","online":true}'
curl -s -X POST $ADMIN/api/admin/featured-items -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"cityId":"'"$CITYC"'","banner":"images/regfi-cdrdg8251.png","description":"回归精选cdrdg8251","online":true}'
```
HTTP 200
```json
{"id":"01a038c4-7b0a-7010-9388-ab94ba16ac0c","cityId":"01a038c4-7af3-790c-89cd-7db229bb8e54","banner":{"id":"bound/regfi-cdrdg8251.png","url":"...oss..."},"description":"回归精选cdrdg8251","online":true}
```

## step 1 — 下架前 app 端信息流含该条目

```bash
curl -s "$APP/api/app/featured-items" -H "X-API-Key: $APIKEY"
```
HTTP 200，共 3 条，含：
```json
{"id":"01a038c4-7b0a-7010-9388-ab94ba16ac0c","banner":{"id":"bound/regfi-cdrdg8251.png","url":"..."},"description":"回归精选cdrdg8251","city":{"id":"01a038c4-7af3-790c-89cd-7db229bb8e54","name":"回归城Ccdrdg8251"}}
```

## step 2 — admin 侧下架该城市

```bash
curl -s -X PUT "$ADMIN/api/admin/cities/$CITYC/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```
HTTP 200

## step 3 — 下架后再查信息流

```bash
curl -s "$APP/api/app/featured-items" -H "X-API-Key: $APIKEY"
```
HTTP 200，共 2 条，不含 `cityId=$CITYC` / `id=$FIID` 的条目（匹配结果为空）。
