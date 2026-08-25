# TC-recommend-list-IT-009 请求/响应存证

用例: PUT /api/admin/recommend-lists/{id} merchantIds 重复商户被拒绝
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A009143752", "englishName": "CityA009143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-31c1-7d01-98a0-b7d7c0ff401d",
  "chineseName": "测城A009143752",
  "englishName": "CityA009143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:52.96177435Z",
  "updatedAt": "2026-08-25T14:37:52.96177435Z"
}
```

## Step 2: 前置：POST /api/admin/merchants 创建商户 M1（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户M1009143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-31c1-7d01-98a0-b7d7c0ff401d", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 10, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-31cb-7446-a097-7cc6383f989d",
  "name": "商户M1009143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-31c1-7d01-98a0-b7d7c0ff401d",
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
  "createdAt": "2026-08-25T14:37:52.971225663Z",
  "updatedAt": "2026-08-25T14:37:52.971225663Z"
}
```

## Step 3: 前置：POST /api/admin/merchants 创建商户 M2（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户M2009143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-31c1-7d01-98a0-b7d7c0ff401d", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 10, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-31d7-7606-b558-a0484f406edd",
  "name": "商户M2009143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-31c1-7d01-98a0-b7d7c0ff401d",
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
  "createdAt": "2026-08-25T14:37:52.983342195Z",
  "updatedAt": "2026-08-25T14:37:52.983342195Z"
}
```

## Step 4: 前置：POST /api/admin/recommend-lists 创建清单 重复校验清单

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "重复校验清单143752", "cityId": "01a0395b-31c1-7d01-98a0-b7d7c0ff401d", "merchantIds": ["01a0395b-31d7-7606-b558-a0484f406edd"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-31e3-75e9-86dc-f41079895c8e",
  "title": "重复校验清单143752",
  "introduction": null,
  "cityId": "01a0395b-31c1-7d01-98a0-b7d7c0ff401d",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-31d7-7606-b558-a0484f406edd",
      "name": "商户M2009143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:52.995332975Z",
  "updatedAt": "2026-08-25T14:37:52.995332975Z",
  "status": "ONLINE"
}
```

## Step 5: PUT /api/admin/recommend-lists/{id} merchantIds=[M1, M1]

```bash
curl -s -i -X "PUT" "http://localhost:21423/api/admin/recommend-lists/01a0395b-31e3-75e9-86dc-f41079895c8e" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "重复校验清单143752", "merchantIds": ["01a0395b-31cb-7446-a097-7cc6383f989d", "01a0395b-31cb-7446-a097-7cc6383f989d"]}'
```

实际响应（HTTP/1.1 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "同一商户不能重复添加到清单",
  "path": "/api/admin/recommend-lists/01a0395b-31e3-75e9-86dc-f41079895c8e"
}
```

## Step 6: GET /api/admin/recommend-lists/{id}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-31e3-75e9-86dc-f41079895c8e" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-31e3-75e9-86dc-f41079895c8e",
  "title": "重复校验清单143752",
  "introduction": null,
  "cityId": "01a0395b-31c1-7d01-98a0-b7d7c0ff401d",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-31d7-7606-b558-a0484f406edd",
      "name": "商户M2009143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:52.995333Z",
  "updatedAt": "2026-08-25T14:37:52.995333Z",
  "status": "ONLINE"
}
```
