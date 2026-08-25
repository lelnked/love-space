# TC-city-IT-001 请求/响应存证（回归确认）

执行时间：2026-08-25 11:46 UTC ｜ baseUrl `http://localhost:8080` ｜ cityId=01a038be-64a7-7626-9736-553563ebe6dd

## step 1 — 登录（同 TC-013，token 脱敏为 $TOKEN）

## step 2 — 创建城市（含 editorNote）

```bash
curl -s -X POST http://localhost:8080/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"编辑说城市1787658319","englishName":"NoteCity1787658319","chineseProvince":"测试省","englishProvince":"TestProv","editorNote":"江城夜景是这座城市的灵魂","online":false}'
```

HTTP/1.1 200
```json
{"id":"01a038be-64a7-7626-9736-553563ebe6dd","chineseName":"编辑说城市1787658319","englishName":"NoteCity1787658319","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":null,"editorNote":"江城夜景是这座城市的灵魂","online":false,"createdAt":"2026-08-25T11:46:36.839319345Z","updatedAt":"2026-08-25T11:46:36.839319345Z"}
```

## step 3 — 查询详情

```bash
curl -s -i http://localhost:8080/api/admin/cities/01a038be-64a7-7626-9736-553563ebe6dd -H "Authorization: Bearer $TOKEN"
```

HTTP/1.1 200，`editorNote` = "江城夜景是这座城市的灵魂"
```json
{"id":"01a038be-64a7-7626-9736-553563ebe6dd","chineseName":"编辑说城市1787658319","englishName":"NoteCity1787658319","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":null,"editorNote":"江城夜景是这座城市的灵魂","online":false,"createdAt":"2026-08-25T11:46:36.839319Z","updatedAt":"2026-08-25T11:46:36.839319Z"}
```
