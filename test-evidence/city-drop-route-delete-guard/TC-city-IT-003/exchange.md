# TC-city-IT-003 请求/响应存证（回归确认）

执行时间：2026-08-25 11:46 UTC ｜ cityId=01a038be-64a7-7626-9736-553563ebe6dd（editorNote 现为「编」×200）

## step 1 — PUT editorNote = "编" × 201

```bash
curl -s -i -X PUT http://localhost:8080/api/admin/cities/01a038be-64a7-7626-9736-553563ebe6dd -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d "$(python3 -c 'import json;print(json.dumps({"chineseName":"编辑说城市1787658319","englishName":"NoteCity1787658319","chineseProvince":"测试省","englishProvince":"TestProv","editorNote":"编"*201,"online":False},ensure_ascii=False))')"
```

HTTP/1.1 400，Content-Type: application/json
```json
{"status":400,"error":"Bad Request","message":"编辑说长度不能超过 200 个字符","path":"/api/admin/cities/01a038be-64a7-7626-9736-553563ebe6dd"}
```

## step 2 — 查询详情确认未变更

```bash
curl -s http://localhost:8080/api/admin/cities/01a038be-64a7-7626-9736-553563ebe6dd -H "Authorization: Bearer $TOKEN" \
  | python3 -c "import sys,json;print(len(json.load(sys.stdin)['editorNote']))"
# 输出: 200
```

## 清理

删除本轮夹具城市 cities/01a038be-64a7-7626-9736-553563ebe6dd → 200。
