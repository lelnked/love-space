# TC-city-IT-001 请求/响应存证（新增第二背景图列后回归确认）

执行时间：2026-09-08 12:44 UTC ｜ baseUrl `http://localhost:21423`（admin，test profile）
cityId = `01a0810b-f880-7919-94f9-fa0b1cb26c78` ｜ JWT 脱敏为 `$TOKEN`

## step 1 — 登录（同 TC-015 step 1）

## step 2 — 创建城市（含 editorNote，不带任何图片字段）

```bash
curl -s -X POST http://localhost:21423/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"编辑说城市1851215334","englishName":"NoteCity1851215334","chineseProvince":"测试省","englishProvince":"TestProv","editorNote":"江城夜景是这座城市的灵魂","online":false}'
```

HTTP/1.1 200
```json
{"id":"01a0810b-f880-7919-94f9-fa0b1cb26c78","chineseName":"编辑说城市1851215334","englishName":"NoteCity1851215334","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":null,"secondaryBackgroundImage":null,"editorNote":"江城夜景是这座城市的灵魂","online":false,"createdAt":"2026-09-08T12:44:00.512484271Z","updatedAt":"2026-09-08T12:44:00.512484271Z"}
```

## step 3 — 查询详情

```bash
curl -s -i http://localhost:21423/api/admin/cities/01a0810b-f880-7919-94f9-fa0b1cb26c78 -H "Authorization: Bearer $TOKEN"
```

HTTP/1.1 200
```json
{"id":"01a0810b-f880-7919-94f9-fa0b1cb26c78","chineseName":"编辑说城市1851215334","englishName":"NoteCity1851215334","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":null,"secondaryBackgroundImage":null,"editorNote":"江城夜景是这座城市的灵魂","online":false,"createdAt":"2026-09-08T12:44:00.512484Z","updatedAt":"2026-09-08T12:44:00.512484Z"}
```
