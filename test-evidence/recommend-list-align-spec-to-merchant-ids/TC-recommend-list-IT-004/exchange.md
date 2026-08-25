# TC-recommend-list-IT-004 请求/响应存证

用例: PUT /api/admin/recommend-lists/{id} 修改所属城市需清单内商户同属新城市
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A004143752", "englishName": "CityA004143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-2fb3-7847-b29c-6934e43d8b87",
  "chineseName": "测城A004143752",
  "englishName": "CityA004143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:52.435476027Z",
  "updatedAt": "2026-08-25T14:37:52.435476027Z"
}
```

## Step 2: 前置：POST /api/admin/cities 创建城市 B

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城B004143752", "englishName": "CityB004143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-2fbd-7ff1-908b-f0081eed86fd",
  "chineseName": "测城B004143752",
  "englishName": "CityB004143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:52.445949292Z",
  "updatedAt": "2026-08-25T14:37:52.445949292Z"
}
```

## Step 3: 前置：POST /api/admin/merchants 创建商户 M1（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户M1004143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-2fb3-7847-b29c-6934e43d8b87", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 10, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-2ff5-795d-aa6b-4511a8469d63",
  "name": "商户M1004143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-2fb3-7847-b29c-6934e43d8b87",
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
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lLQx5xvoswux3Zag6LClTV6B4JU%3D"
    }
  ],
  "createdAt": "2026-08-25T14:37:52.49841693Z",
  "updatedAt": "2026-08-25T14:37:52.49841693Z"
}
```

## Step 4: 前置：POST /api/admin/recommend-lists 创建清单 L1含M1

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "L1含M1143752", "cityId": "01a0395b-2fb3-7847-b29c-6934e43d8b87", "merchantIds": ["01a0395b-2ff5-795d-aa6b-4511a8469d63"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3024-7b4c-badc-0977ca1c43df",
  "title": "L1含M1143752",
  "introduction": null,
  "cityId": "01a0395b-2fb3-7847-b29c-6934e43d8b87",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-2ff5-795d-aa6b-4511a8469d63",
      "name": "商户M1004143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:52.54864358Z",
  "updatedAt": "2026-08-25T14:37:52.54864358Z",
  "status": "ONLINE"
}
```

## Step 5: 前置：POST /api/admin/recommend-lists 创建清单 L2无商户

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "L2无商户143752", "cityId": "01a0395b-2fb3-7847-b29c-6934e43d8b87"}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-303f-7d21-a856-abfd91b5e5b3",
  "title": "L2无商户143752",
  "introduction": null,
  "cityId": "01a0395b-2fb3-7847-b29c-6934e43d8b87",
  "sortOrder": 0,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:52.5757422Z",
  "updatedAt": "2026-08-25T14:37:52.5757422Z",
  "status": "ONLINE"
}
```

## Step 6: PUT /api/admin/recommend-lists/{L1} cityId=B（含 A 商户）

```bash
curl -s -i -X "PUT" "http://localhost:21423/api/admin/recommend-lists/01a0395b-3024-7b4c-badc-0977ca1c43df" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "改名后的清单", "introduction": "新介绍", "cityId": "01a0395b-2fbd-7ff1-908b-f0081eed86fd", "sortOrder": 9}'
```

实际响应（HTTP/1.1 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "清单内商户「商户M1004143752」不属于新城市，请先移除后再修改所属城市",
  "path": "/api/admin/recommend-lists/01a0395b-3024-7b4c-badc-0977ca1c43df"
}
```

## Step 7: GET /api/admin/recommend-lists/{L1}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-3024-7b4c-badc-0977ca1c43df" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3024-7b4c-badc-0977ca1c43df",
  "title": "L1含M1143752",
  "introduction": null,
  "cityId": "01a0395b-2fb3-7847-b29c-6934e43d8b87",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-2ff5-795d-aa6b-4511a8469d63",
      "name": "商户M1004143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:52.548644Z",
  "updatedAt": "2026-08-25T14:37:52.548644Z",
  "status": "ONLINE"
}
```

## Step 8: PUT /api/admin/recommend-lists/{L2} cityId=B（无商户）

```bash
curl -s -i -X "PUT" "http://localhost:21423/api/admin/recommend-lists/01a0395b-303f-7d21-a856-abfd91b5e5b3" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "换城市的清单", "cityId": "01a0395b-2fbd-7ff1-908b-f0081eed86fd", "sortOrder": 9}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-303f-7d21-a856-abfd91b5e5b3",
  "title": "换城市的清单",
  "introduction": null,
  "cityId": "01a0395b-2fbd-7ff1-908b-f0081eed86fd",
  "sortOrder": 9,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:52.575742Z",
  "updatedAt": "2026-08-25T14:37:52.624356373Z",
  "status": "ONLINE"
}
```

## Step 9: GET /api/admin/recommend-lists/{L2}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-303f-7d21-a856-abfd91b5e5b3" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-303f-7d21-a856-abfd91b5e5b3",
  "title": "换城市的清单",
  "introduction": null,
  "cityId": "01a0395b-2fbd-7ff1-908b-f0081eed86fd",
  "sortOrder": 9,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:52.575742Z",
  "updatedAt": "2026-08-25T14:37:52.624985Z",
  "status": "ONLINE"
}
```
