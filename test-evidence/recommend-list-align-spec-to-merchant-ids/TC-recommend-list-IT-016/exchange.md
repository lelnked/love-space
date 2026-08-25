# TC-recommend-list-IT-016 请求/响应存证

用例: PUT /api/admin/recommend-lists/{id} merchantIds 含已下架商户被拒绝
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A016143752", "englishName": "CityA016143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3271-7c6a-a395-9d78dc747579",
  "chineseName": "测城A016143752",
  "englishName": "CityA016143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:53.137737529Z",
  "updatedAt": "2026-08-25T14:37:53.137737529Z"
}
```

## Step 2: 前置：POST /api/admin/merchants 创建商户 Mo（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户Mo016143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-3271-7c6a-a395-9d78dc747579", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 10, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-327b-798b-ac7b-e691ea41549d",
  "name": "商户Mo016143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3271-7c6a-a395-9d78dc747579",
  "categoryId": null,
  "safetyEnvironmentScore": 25,
  "businessRightsScore": 20,
  "experienceFriendlyScore": 20,
  "socialContributionScore": 15,
  "story": null,
  "recommendReason": null,
  "weight": 10,
  "online": true,
  "periods": [],
  "tagIds": [],
  "images": [
    {
      "id": "bound/img-test-1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=1MHuBnqzfyBvl2jBU8WBx8zN2h4%3D"
    }
  ],
  "createdAt": "2026-08-25T14:37:53.147560806Z",
  "updatedAt": "2026-08-25T14:37:53.147560806Z"
}
```

## Step 3: 前置：POST /api/admin/recommend-lists 创建清单 下架校验清单

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "下架校验清单143752", "cityId": "01a0395b-3271-7c6a-a395-9d78dc747579"}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3287-735b-ac19-c9af4d8f5525",
  "title": "下架校验清单143752",
  "introduction": null,
  "cityId": "01a0395b-3271-7c6a-a395-9d78dc747579",
  "sortOrder": 0,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:53.159163175Z",
  "updatedAt": "2026-08-25T14:37:53.159163175Z",
  "status": "ONLINE"
}
```

## Step 4: PUT /api/admin/merchants/{id}/online online=false

```bash
curl -s -i -X "PUT" "http://localhost:21423/api/admin/merchants/01a0395b-327b-798b-ac7b-e691ea41549d/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online": false}'
```

> ⚠️ 契约漂移：PUT /api/admin/merchants/{id}/online 未登记于 api-spec.json（归 merchant 域，本 change 不补），不判失败

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-327b-798b-ac7b-e691ea41549d",
  "name": "商户Mo016143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3271-7c6a-a395-9d78dc747579",
  "categoryId": null,
  "safetyEnvironmentScore": 25,
  "businessRightsScore": 20,
  "experienceFriendlyScore": 20,
  "socialContributionScore": 15,
  "story": null,
  "recommendReason": null,
  "weight": 10,
  "online": false,
  "periods": [],
  "tagIds": [],
  "images": [
    {
      "id": "bound/img-test-1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=1MHuBnqzfyBvl2jBU8WBx8zN2h4%3D"
    }
  ],
  "createdAt": "2026-08-25T14:37:53.147561Z",
  "updatedAt": "2026-08-25T14:37:53.175588621Z"
}
```

## Step 5: PUT /api/admin/recommend-lists/{id} merchantIds=[Mo(已下架)]

```bash
curl -s -i -X "PUT" "http://localhost:21423/api/admin/recommend-lists/01a0395b-3287-735b-ac19-c9af4d8f5525" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "下架校验清单143752", "merchantIds": ["01a0395b-327b-798b-ac7b-e691ea41549d"]}'
```

实际响应（HTTP/1.1 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "商户「商户Mo016143752」已下架，不能加入清单",
  "path": "/api/admin/recommend-lists/01a0395b-3287-735b-ac19-c9af4d8f5525"
}
```

## Step 6: GET /api/admin/recommend-lists/{id}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-3287-735b-ac19-c9af4d8f5525" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3287-735b-ac19-c9af4d8f5525",
  "title": "下架校验清单143752",
  "introduction": null,
  "cityId": "01a0395b-3271-7c6a-a395-9d78dc747579",
  "sortOrder": 0,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:53.159163Z",
  "updatedAt": "2026-08-25T14:37:53.159163Z",
  "status": "ONLINE"
}
```
