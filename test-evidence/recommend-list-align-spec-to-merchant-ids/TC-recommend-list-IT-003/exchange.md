# TC-recommend-list-IT-003 请求/响应存证

用例: POST /api/admin/recommend-lists 不传 sortOrder 默认 0
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A003143752", "englishName": "CityA003143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-2f93-7bf6-aec2-4e4fcb61a4d2",
  "chineseName": "测城A003143752",
  "englishName": "CityA003143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:52.403684738Z",
  "updatedAt": "2026-08-25T14:37:52.403684738Z"
}
```

## Step 2: POST /api/admin/recommend-lists（不含 sortOrder）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "默认排序清单", "cityId": "01a0395b-2f93-7bf6-aec2-4e4fcb61a4d2"}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-2f9e-7d7c-a6f4-d9498f286da9",
  "title": "默认排序清单",
  "introduction": null,
  "cityId": "01a0395b-2f93-7bf6-aec2-4e4fcb61a4d2",
  "sortOrder": 0,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:52.414784352Z",
  "updatedAt": "2026-08-25T14:37:52.414784352Z",
  "status": "ONLINE"
}
```

## Step 3: GET /api/admin/recommend-lists/{id}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-2f9e-7d7c-a6f4-d9498f286da9" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-2f9e-7d7c-a6f4-d9498f286da9",
  "title": "默认排序清单",
  "introduction": null,
  "cityId": "01a0395b-2f93-7bf6-aec2-4e4fcb61a4d2",
  "sortOrder": 0,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:52.414784Z",
  "updatedAt": "2026-08-25T14:37:52.414784Z",
  "status": "ONLINE"
}
```
