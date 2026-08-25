# TC-recommend-list-IT-008 请求/响应存证

用例: PUT /api/admin/recommend-lists/{id} merchantIds 含跨城市商户被拒绝
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A008143752", "englishName": "CityA008143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3173-7336-92f7-e276b70dd07d",
  "chineseName": "测城A008143752",
  "englishName": "CityA008143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:52.883162807Z",
  "updatedAt": "2026-08-25T14:37:52.883162807Z"
}
```

## Step 2: 前置：POST /api/admin/cities 创建城市 B

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城B008143752", "englishName": "CityB008143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-317c-7819-9fb9-13728d951e00",
  "chineseName": "测城B008143752",
  "englishName": "CityB008143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:52.892466711Z",
  "updatedAt": "2026-08-25T14:37:52.892466711Z"
}
```

## Step 3: 前置：POST /api/admin/merchants 创建商户 M1（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户M1008143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-3173-7336-92f7-e276b70dd07d", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 10, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3186-71ee-a151-a2395cd254c0",
  "name": "商户M1008143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3173-7336-92f7-e276b70dd07d",
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
  "createdAt": "2026-08-25T14:37:52.902083924Z",
  "updatedAt": "2026-08-25T14:37:52.902083924Z"
}
```

## Step 4: 前置：POST /api/admin/merchants 创建商户 Mx（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户Mx008143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-317c-7819-9fb9-13728d951e00", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 10, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3192-7725-94f5-9416e0c1fdb0",
  "name": "商户Mx008143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-317c-7819-9fb9-13728d951e00",
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
  "createdAt": "2026-08-25T14:37:52.914408497Z",
  "updatedAt": "2026-08-25T14:37:52.914408497Z"
}
```

## Step 5: 前置：POST /api/admin/recommend-lists 创建清单 城市A清单

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "城市A清单143752", "cityId": "01a0395b-3173-7336-92f7-e276b70dd07d", "merchantIds": ["01a0395b-3186-71ee-a151-a2395cd254c0"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-319e-7483-9c9e-5bc4fffad45e",
  "title": "城市A清单143752",
  "introduction": null,
  "cityId": "01a0395b-3173-7336-92f7-e276b70dd07d",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-3186-71ee-a151-a2395cd254c0",
      "name": "商户M1008143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:52.926244246Z",
  "updatedAt": "2026-08-25T14:37:52.926244246Z",
  "status": "ONLINE"
}
```

## Step 6: PUT /api/admin/recommend-lists/{id} merchantIds=[Mx(城市B)]

```bash
curl -s -i -X "PUT" "http://localhost:21423/api/admin/recommend-lists/01a0395b-319e-7483-9c9e-5bc4fffad45e" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "城市A清单143752", "merchantIds": ["01a0395b-3192-7725-94f5-9416e0c1fdb0"]}'
```

实际响应（HTTP/1.1 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "商户「商户Mx008143752」不属于清单所属城市，不能加入清单",
  "path": "/api/admin/recommend-lists/01a0395b-319e-7483-9c9e-5bc4fffad45e"
}
```

## Step 7: GET /api/admin/recommend-lists/{id}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-319e-7483-9c9e-5bc4fffad45e" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-319e-7483-9c9e-5bc4fffad45e",
  "title": "城市A清单143752",
  "introduction": null,
  "cityId": "01a0395b-3173-7336-92f7-e276b70dd07d",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-3186-71ee-a151-a2395cd254c0",
      "name": "商户M1008143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:52.926244Z",
  "updatedAt": "2026-08-25T14:37:52.926244Z",
  "status": "ONLINE"
}
```
