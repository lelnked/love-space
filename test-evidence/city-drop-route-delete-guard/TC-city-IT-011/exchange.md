# TC-city-IT-011 exchange

环境：app `http://localhost:8081`；`$APIKEY` 脱敏。执行时间：2026-08-25

前置城市与 TC-city-IT-004 同一夹具：`01a038c3-c603-7bec-9598-2e48b274bd4c`（online=true，含
backgroundImage 与 editorNote），创建请求见 `../TC-city-IT-004/exchange.md` step 2。

```bash
export APP=http://localhost:8081 APIKEY=...
curl -s -i $APP/api/app/cities/01a038c3-c603-7bec-9598-2e48b274bd4c -H "X-API-Key: $APIKEY"
```

```
HTTP/1.1 200
Vary: Origin
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
X-Frame-Options: DENY
Content-Type: application/json
Content-Length: 435
Date: Tue, 25 Aug 2026 11:52:43 GMT
```
```json
{"id":"01a038c3-c603-7bec-9598-2e48b274bd4c","chineseName":"回归城Acdrdg8251","englishName":"RegCityAcdrdg8251","chineseProvince":"回归省","englishProvince":"RegProv","backgroundImage":{"id":"bound/regbg-cdrdg8251.png","url":"https://love-space-test-0524.oss-test.example.com/bound/regbg-cdrdg8251.png?Expires=...&OSSAccessKeyId=test-ak&Signature=..."},"editorNote":"山与湖之间的浪漫"}
```
