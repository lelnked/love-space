# TC-recommend-list-IT-006 请求/响应存证

用例: GET /api/admin/recommend-lists/page 按 sortOrder 升序并支持过滤
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A006143752", "englishName": "CityA006143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-30e2-71ef-9143-d8cd4fe9f3f2",
  "chineseName": "测城A006143752",
  "englishName": "CityA006143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:52.738079273Z",
  "updatedAt": "2026-08-25T14:37:52.738079273Z"
}
```

## Step 2: 前置：POST /api/admin/recommend-lists 创建清单 清单五

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "清单五143752", "cityId": "01a0395b-30e2-71ef-9143-d8cd4fe9f3f2", "sortOrder": 5}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-30eb-78cd-9e83-5d1c397cfec8",
  "title": "清单五143752",
  "introduction": null,
  "cityId": "01a0395b-30e2-71ef-9143-d8cd4fe9f3f2",
  "sortOrder": 5,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:52.747510636Z",
  "updatedAt": "2026-08-25T14:37:52.747510636Z",
  "status": "ONLINE"
}
```

## Step 3: 前置：POST /api/admin/recommend-lists 创建清单 精选清单一

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "精选清单一143752", "cityId": "01a0395b-30e2-71ef-9143-d8cd4fe9f3f2", "sortOrder": 1}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-30f6-71a7-9003-fbaa2b2f3d53",
  "title": "精选清单一143752",
  "introduction": null,
  "cityId": "01a0395b-30e2-71ef-9143-d8cd4fe9f3f2",
  "sortOrder": 1,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:52.758030777Z",
  "updatedAt": "2026-08-25T14:37:52.758030777Z",
  "status": "ONLINE"
}
```

## Step 4: 前置：POST /api/admin/recommend-lists 创建清单 清单三

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "清单三143752", "cityId": "01a0395b-30e2-71ef-9143-d8cd4fe9f3f2", "sortOrder": 3}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3100-793b-b7ed-c88e6c7d0890",
  "title": "清单三143752",
  "introduction": null,
  "cityId": "01a0395b-30e2-71ef-9143-d8cd4fe9f3f2",
  "sortOrder": 3,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:52.768540769Z",
  "updatedAt": "2026-08-25T14:37:52.768540769Z",
  "status": "ONLINE"
}
```

## Step 5: GET /api/admin/recommend-lists/page?cityId=A&page=0&size=10

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/page?cityId=01a0395b-30e2-71ef-9143-d8cd4fe9f3f2&page=0&size=10" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [
    {
      "id": "01a0395b-30f6-71a7-9003-fbaa2b2f3d53",
      "title": "精选清单一143752",
      "introduction": null,
      "cityId": "01a0395b-30e2-71ef-9143-d8cd4fe9f3f2",
      "sortOrder": 1,
      "merchantCount": 0,
      "createdAt": "2026-08-25T14:37:52.758031Z",
      "updatedAt": "2026-08-25T14:37:52.758031Z",
      "status": "ONLINE"
    },
    {
      "id": "01a0395b-3100-793b-b7ed-c88e6c7d0890",
      "title": "清单三143752",
      "introduction": null,
      "cityId": "01a0395b-30e2-71ef-9143-d8cd4fe9f3f2",
      "sortOrder": 3,
      "merchantCount": 0,
      "createdAt": "2026-08-25T14:37:52.768541Z",
      "updatedAt": "2026-08-25T14:37:52.768541Z",
      "status": "ONLINE"
    },
    {
      "id": "01a0395b-30eb-78cd-9e83-5d1c397cfec8",
      "title": "清单五143752",
      "introduction": null,
      "cityId": "01a0395b-30e2-71ef-9143-d8cd4fe9f3f2",
      "sortOrder": 5,
      "merchantCount": 0,
      "createdAt": "2026-08-25T14:37:52.747511Z",
      "updatedAt": "2026-08-25T14:37:52.747511Z",
      "status": "ONLINE"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 3,
  "totalPages": 1
}
```

## Step 6: GET /api/admin/recommend-lists/page?cityId=A&keyword=精选

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/page?cityId=01a0395b-30e2-71ef-9143-d8cd4fe9f3f2&keyword=%E7%B2%BE%E9%80%89" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [
    {
      "id": "01a0395b-30f6-71a7-9003-fbaa2b2f3d53",
      "title": "精选清单一143752",
      "introduction": null,
      "cityId": "01a0395b-30e2-71ef-9143-d8cd4fe9f3f2",
      "sortOrder": 1,
      "merchantCount": 0,
      "createdAt": "2026-08-25T14:37:52.758031Z",
      "updatedAt": "2026-08-25T14:37:52.758031Z",
      "status": "ONLINE"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```
