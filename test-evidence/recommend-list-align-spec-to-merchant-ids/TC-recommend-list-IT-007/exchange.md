# TC-recommend-list-IT-007 请求/响应存证

用例: PUT /api/admin/recommend-lists/{id} merchantIds 整体替换本城市商户并按数组顺序回显
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A007143752", "englishName": "CityA007143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3129-79b8-8601-998318d02081",
  "chineseName": "测城A007143752",
  "englishName": "CityA007143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:52.809551515Z",
  "updatedAt": "2026-08-25T14:37:52.809551515Z"
}
```

## Step 2: 前置：POST /api/admin/merchants 创建商户 M1（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户M1007143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-3129-79b8-8601-998318d02081", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 10, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3133-7f32-8454-673423a9ba5d",
  "name": "商户M1007143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3129-79b8-8601-998318d02081",
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
  "createdAt": "2026-08-25T14:37:52.819911368Z",
  "updatedAt": "2026-08-25T14:37:52.819911368Z"
}
```

## Step 3: 前置：POST /api/admin/merchants 创建商户 M2（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户M2007143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-3129-79b8-8601-998318d02081", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 20, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3140-7a09-a1eb-103c426caf66",
  "name": "商户M2007143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3129-79b8-8601-998318d02081",
  "categoryId": null,
  "safetyEnvironmentScore": 25,
  "businessRightsScore": 20,
  "experienceFriendlyScore": 20,
  "socialContributionScore": 15,
  "story": null,
  "recommendReason": null,
  "weight": 20,
  "online": true,
  "periods": [],
  "tagIds": [],
  "images": [
    {
      "id": "bound/img-test-1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lLQx5xvoswux3Zag6LClTV6B4JU%3D"
    }
  ],
  "createdAt": "2026-08-25T14:37:52.832591551Z",
  "updatedAt": "2026-08-25T14:37:52.832591551Z"
}
```

## Step 4: 前置：POST /api/admin/recommend-lists 创建清单 顺序清单

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "顺序清单143752", "cityId": "01a0395b-3129-79b8-8601-998318d02081"}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-314c-7767-9217-75bc8ddf8c97",
  "title": "顺序清单143752",
  "introduction": null,
  "cityId": "01a0395b-3129-79b8-8601-998318d02081",
  "sortOrder": 0,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:52.844425696Z",
  "updatedAt": "2026-08-25T14:37:52.844425696Z",
  "status": "ONLINE"
}
```

## Step 5: PUT /api/admin/recommend-lists/{id} merchantIds=[M2, M1]

```bash
curl -s -i -X "PUT" "http://localhost:21423/api/admin/recommend-lists/01a0395b-314c-7767-9217-75bc8ddf8c97" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "顺序清单143752", "merchantIds": ["01a0395b-3140-7a09-a1eb-103c426caf66", "01a0395b-3133-7f32-8454-673423a9ba5d"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-314c-7767-9217-75bc8ddf8c97",
  "title": "顺序清单143752",
  "introduction": null,
  "cityId": "01a0395b-3129-79b8-8601-998318d02081",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-3140-7a09-a1eb-103c426caf66",
      "name": "商户M2007143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    },
    {
      "merchantId": "01a0395b-3133-7f32-8454-673423a9ba5d",
      "name": "商户M1007143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 2
    }
  ],
  "createdAt": "2026-08-25T14:37:52.844426Z",
  "updatedAt": "2026-08-25T14:37:52.844426Z",
  "status": "ONLINE"
}
```

## Step 6: GET /api/admin/recommend-lists/{id}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-314c-7767-9217-75bc8ddf8c97" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-314c-7767-9217-75bc8ddf8c97",
  "title": "顺序清单143752",
  "introduction": null,
  "cityId": "01a0395b-3129-79b8-8601-998318d02081",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-3140-7a09-a1eb-103c426caf66",
      "name": "商户M2007143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    },
    {
      "merchantId": "01a0395b-3133-7f32-8454-673423a9ba5d",
      "name": "商户M1007143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 2
    }
  ],
  "createdAt": "2026-08-25T14:37:52.844426Z",
  "updatedAt": "2026-08-25T14:37:52.844426Z",
  "status": "ONLINE"
}
```
