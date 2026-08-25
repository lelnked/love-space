# TC-recommend-list-IT-001 请求/响应存证

用例: POST /api/admin/recommend-lists 创建清单成功
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A001143752", "englishName": "CityA001143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-2f20-78d4-b766-0b7f3acbf1da",
  "chineseName": "测城A001143752",
  "englishName": "CityA001143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:52.288483016Z",
  "updatedAt": "2026-08-25T14:37:52.288483016Z"
}
```

## Step 2: POST /api/admin/recommend-lists

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "江畔约会精选", "introduction": "沿江十家小店", "cityId": "01a0395b-2f20-78d4-b766-0b7f3acbf1da", "sortOrder": 3}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-2f2c-79cc-b74b-c73bc70ea291",
  "title": "江畔约会精选",
  "introduction": "沿江十家小店",
  "cityId": "01a0395b-2f20-78d4-b766-0b7f3acbf1da",
  "sortOrder": 3,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:52.300563732Z",
  "updatedAt": "2026-08-25T14:37:52.300563732Z",
  "status": "ONLINE"
}
```

## Step 3: GET /api/admin/recommend-lists/{id}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-2f2c-79cc-b74b-c73bc70ea291" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-2f2c-79cc-b74b-c73bc70ea291",
  "title": "江畔约会精选",
  "introduction": "沿江十家小店",
  "cityId": "01a0395b-2f20-78d4-b766-0b7f3acbf1da",
  "sortOrder": 3,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:52.300564Z",
  "updatedAt": "2026-08-25T14:37:52.300564Z",
  "status": "ONLINE"
}
```
