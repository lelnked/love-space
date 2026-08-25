# TC-recommend-list-IT-005 请求/响应存证

用例: DELETE /api/admin/recommend-lists/{id} 物理删除含商户关联的清单
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A005143752", "englishName": "CityA005143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3089-73a7-85c0-4bd5acd36a08",
  "chineseName": "测城A005143752",
  "englishName": "CityA005143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:52.64915978Z",
  "updatedAt": "2026-08-25T14:37:52.64915978Z"
}
```

## Step 2: 前置：POST /api/admin/merchants 创建商户 M1（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户M1005143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-3089-73a7-85c0-4bd5acd36a08", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 10, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3095-7c3f-b1b3-1917efa9c519",
  "name": "商户M1005143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3089-73a7-85c0-4bd5acd36a08",
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
  "createdAt": "2026-08-25T14:37:52.661715849Z",
  "updatedAt": "2026-08-25T14:37:52.661715849Z"
}
```

## Step 3: 前置：POST /api/admin/recommend-lists 创建清单 待删清单

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "待删清单143752", "cityId": "01a0395b-3089-73a7-85c0-4bd5acd36a08", "merchantIds": ["01a0395b-3095-7c3f-b1b3-1917efa9c519"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-30a4-762a-9d06-546b25f2ae51",
  "title": "待删清单143752",
  "introduction": null,
  "cityId": "01a0395b-3089-73a7-85c0-4bd5acd36a08",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-3095-7c3f-b1b3-1917efa9c519",
      "name": "商户M1005143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:52.676345563Z",
  "updatedAt": "2026-08-25T14:37:52.676345563Z",
  "status": "ONLINE"
}
```

## Step 4: 前置：GET /api/admin/merchants/{M1} 记录删除前字段

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/merchants/01a0395b-3095-7c3f-b1b3-1917efa9c519" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3095-7c3f-b1b3-1917efa9c519",
  "name": "商户M1005143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3089-73a7-85c0-4bd5acd36a08",
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
  "createdAt": "2026-08-25T14:37:52.661716Z",
  "updatedAt": "2026-08-25T14:37:52.661716Z"
}
```

## Step 5: DELETE /api/admin/recommend-lists/{id}

```bash
curl -s -i -X "DELETE" "http://localhost:21423/api/admin/recommend-lists/01a0395b-30a4-762a-9d06-546b25f2ae51" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: 

<empty>
```

## Step 6: GET /api/admin/recommend-lists/{id} 已删除

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-30a4-762a-9d06-546b25f2ae51" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "推荐清单不存在：01a0395b-30a4-762a-9d06-546b25f2ae51",
  "path": "/api/admin/recommend-lists/01a0395b-30a4-762a-9d06-546b25f2ae51"
}
```

## Step 7: GET /api/admin/merchants/{M1}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/merchants/01a0395b-3095-7c3f-b1b3-1917efa9c519" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3095-7c3f-b1b3-1917efa9c519",
  "name": "商户M1005143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670472&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BjyPuHBGcjZCkWwsqYhIuyRVCDw%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3089-73a7-85c0-4bd5acd36a08",
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
  "createdAt": "2026-08-25T14:37:52.661716Z",
  "updatedAt": "2026-08-25T14:37:52.661716Z"
}
```
