# TC-city-IT-002 请求/响应存证（回归确认）

执行时间：2026-08-25 11:46 UTC ｜ cityId=01a038be-64a7-7626-9736-553563ebe6dd（承 TC-001 创建的城市）

## step 1 — PUT editorNote = "编" × 200

```bash
curl -s -i -X PUT http://localhost:8080/api/admin/cities/01a038be-64a7-7626-9736-553563ebe6dd -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d "$(python3 -c 'import json;print(json.dumps({"chineseName":"编辑说城市1787658319","englishName":"NoteCity1787658319","chineseProvince":"测试省","englishProvince":"TestProv","editorNote":"编"*200,"online":False},ensure_ascii=False))')"
```

HTTP/1.1 200

## step 2 — 查询详情校验长度

```bash
curl -s http://localhost:8080/api/admin/cities/01a038be-64a7-7626-9736-553563ebe6dd -H "Authorization: Bearer $TOKEN" \
  | python3 -c "import sys,json;d=json.load(sys.stdin);print(len(d['editorNote']), set(d['editorNote'])=={'编'})"
# 输出: 200 True
```
