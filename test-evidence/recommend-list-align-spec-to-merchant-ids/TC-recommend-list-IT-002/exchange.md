# TC-recommend-list-IT-002 请求/响应存证

用例: POST /api/admin/recommend-lists 缺少必填项被拒绝
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A002143752", "englishName": "CityA002143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-2f41-77ca-982a-ec3e62f7fd84",
  "chineseName": "测城A002143752",
  "englishName": "CityA002143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:52.321434253Z",
  "updatedAt": "2026-08-25T14:37:52.321434253Z"
}
```

## Step 2: 前置：GET /api/admin/recommend-lists/page?cityId=A 记录数量

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/page?cityId=01a0395b-2f41-77ca-982a-ec3e62f7fd84&page=0&size=50" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [],
  "page": 1,
  "size": 30,
  "totalElements": 0,
  "totalPages": 0
}
```

## Step 3: POST /api/admin/recommend-lists 缺 title

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId": "01a0395b-2f41-77ca-982a-ec3e62f7fd84"}'
```

实际响应（HTTP/1.1 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "清单标题不能为空",
  "path": "/api/admin/recommend-lists"
}
```

## Step 4: POST /api/admin/recommend-lists 缺 cityId

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "无城市清单"}'
```

实际响应（HTTP/1.1 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "所属城市不能为空",
  "path": "/api/admin/recommend-lists"
}
```

## Step 5: GET /api/admin/recommend-lists/page?cityId=A 复核未创建

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/page?cityId=01a0395b-2f41-77ca-982a-ec3e62f7fd84&page=0&size=50" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [],
  "page": 1,
  "size": 30,
  "totalElements": 0,
  "totalPages": 0
}
```

## Step 6: GET /api/admin/recommend-lists/page?keyword=无城市清单 复核未创建

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/page?keyword=%E6%97%A0%E5%9F%8E%E5%B8%82%E6%B8%85%E5%8D%95&page=0&size=50" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [],
  "page": 1,
  "size": 30,
  "totalElements": 0,
  "totalPages": 0
}
```
