# TC-city-IT-004 exchange

环境：admin `http://localhost:8080`，app `http://localhost:8081`；`$TOKEN` = admin 登录 JWT，`$APIKEY` = app 端 API Key（存证脱敏）。
执行时间：2026-08-25

```bash
export ADMIN=http://localhost:8080 APP=http://localhost:8081
export TOKEN=... APIKEY=...
```

## step 1 — 登录（前置依赖：admin JWT）

```bash
curl -s -X POST $ADMIN/api/admin/auth/login -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

```json
{"token":"$TOKEN","manager":{"id":"019794b6-b400-7000-8000-000000000001","username":"admin","nickname":"管理员","role":"ADMIN"}}
```

## step 2 — 前置：创建上架城市，editorNote = 「山与湖之间的浪漫」

```bash
curl -s -X POST $ADMIN/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"回归城Acdrdg8251","englishName":"RegCityAcdrdg8251","chineseProvince":"回归省","englishProvince":"RegProv","backgroundImage":"images/regbg-cdrdg8251.png","editorNote":"山与湖之间的浪漫","online":true}'
```

```json
{"id":"01a038c3-c603-7bec-9598-2e48b274bd4c","chineseName":"回归城Acdrdg8251","englishName":"RegCityAcdrdg8251","chineseProvince":"回归省","englishProvince":"RegProv","backgroundImage":{"id":"bound/regbg-cdrdg8251.png","url":"...oss signed url..."},"editorNote":"山与湖之间的浪漫","online":true,"createdAt":"2026-08-25T11:52:29.443666361Z","updatedAt":"2026-08-25T11:52:29.443666361Z"}
```

HTTP 200。

## step 3 — GET /api/app/cities（app 端列表）

```bash
curl -s $APP/api/app/cities -H "X-API-Key: $APIKEY"
```

HTTP 200，列表 39 条，其中目标城市项：

```json
{
 "id": "01a038c3-c603-7bec-9598-2e48b274bd4c",
 "chineseName": "回归城Acdrdg8251",
 "englishName": "RegCityAcdrdg8251",
 "chineseProvince": "回归省",
 "englishProvince": "RegProv",
 "backgroundImage": {"id": "bound/regbg-cdrdg8251.png", "url": "https://love-space-test-0524.oss-test.example.com/bound/regbg-cdrdg8251.png?Expires=...&OSSAccessKeyId=test-ak&Signature=..."},
 "editorNote": "山与湖之间的浪漫"
}
```
