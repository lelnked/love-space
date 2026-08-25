# TC-recommend-list-IT-010 请求/响应存证

用例: PUT /api/admin/recommend-lists/{id} merchantIds 去掉商户即移除且不影响商户本身
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A010143752", "englishName": "CityA010143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3207-72fa-b45b-a563d7a53bf7",
  "chineseName": "测城A010143752",
  "englishName": "CityA010143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:53.031135824Z",
  "updatedAt": "2026-08-25T14:37:53.031135824Z"
}
```

## Step 2: 前置：POST /api/admin/merchants 创建商户 M1（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户M1010143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-3207-72fa-b45b-a563d7a53bf7", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 10, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3211-725c-9216-7636c7375ecf",
  "name": "商户M1010143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3207-72fa-b45b-a563d7a53bf7",
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
  "createdAt": "2026-08-25T14:37:53.041080137Z",
  "updatedAt": "2026-08-25T14:37:53.041080137Z"
}
```

## Step 3: 前置：POST /api/admin/merchants 创建商户 M2（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户M2010143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-3207-72fa-b45b-a563d7a53bf7", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 10, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-321e-704f-b3dc-fb2bad5d8871",
  "name": "商户M2010143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3207-72fa-b45b-a563d7a53bf7",
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
  "createdAt": "2026-08-25T14:37:53.053984182Z",
  "updatedAt": "2026-08-25T14:37:53.053984182Z"
}
```

## Step 4: 前置：POST /api/admin/recommend-lists 创建清单 移除清单

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "移除清单143752", "cityId": "01a0395b-3207-72fa-b45b-a563d7a53bf7"}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3229-7af1-989e-8dab04153562",
  "title": "移除清单143752",
  "introduction": null,
  "cityId": "01a0395b-3207-72fa-b45b-a563d7a53bf7",
  "sortOrder": 0,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:53.065633717Z",
  "updatedAt": "2026-08-25T14:37:53.065633717Z",
  "status": "ONLINE"
}
```

## Step 5: 前置：GET /api/admin/merchants/{M1} 记录字段

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/merchants/01a0395b-3211-725c-9216-7636c7375ecf" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3211-725c-9216-7636c7375ecf",
  "name": "商户M1010143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3207-72fa-b45b-a563d7a53bf7",
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
  "createdAt": "2026-08-25T14:37:53.04108Z",
  "updatedAt": "2026-08-25T14:37:53.04108Z"
}
```

## Step 6: PUT /api/admin/recommend-lists/{id} merchantIds=[M1, M2]

```bash
curl -s -i -X "PUT" "http://localhost:21423/api/admin/recommend-lists/01a0395b-3229-7af1-989e-8dab04153562" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "移除清单143752", "merchantIds": ["01a0395b-3211-725c-9216-7636c7375ecf", "01a0395b-321e-704f-b3dc-fb2bad5d8871"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3229-7af1-989e-8dab04153562",
  "title": "移除清单143752",
  "introduction": null,
  "cityId": "01a0395b-3207-72fa-b45b-a563d7a53bf7",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-3211-725c-9216-7636c7375ecf",
      "name": "商户M1010143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    },
    {
      "merchantId": "01a0395b-321e-704f-b3dc-fb2bad5d8871",
      "name": "商户M2010143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 2
    }
  ],
  "createdAt": "2026-08-25T14:37:53.065634Z",
  "updatedAt": "2026-08-25T14:37:53.065634Z",
  "status": "ONLINE"
}
```

## Step 7: PUT /api/admin/recommend-lists/{id} merchantIds=[M2]

```bash
curl -s -i -X "PUT" "http://localhost:21423/api/admin/recommend-lists/01a0395b-3229-7af1-989e-8dab04153562" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "移除清单143752", "merchantIds": ["01a0395b-321e-704f-b3dc-fb2bad5d8871"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3229-7af1-989e-8dab04153562",
  "title": "移除清单143752",
  "introduction": null,
  "cityId": "01a0395b-3207-72fa-b45b-a563d7a53bf7",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-321e-704f-b3dc-fb2bad5d8871",
      "name": "商户M2010143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:53.065634Z",
  "updatedAt": "2026-08-25T14:37:53.065634Z",
  "status": "ONLINE"
}
```

## Step 8: GET /api/admin/recommend-lists/{id}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-3229-7af1-989e-8dab04153562" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3229-7af1-989e-8dab04153562",
  "title": "移除清单143752",
  "introduction": null,
  "cityId": "01a0395b-3207-72fa-b45b-a563d7a53bf7",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-321e-704f-b3dc-fb2bad5d8871",
      "name": "商户M2010143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:53.065634Z",
  "updatedAt": "2026-08-25T14:37:53.065634Z",
  "status": "ONLINE"
}
```

## Step 9: GET /api/admin/merchants/{M1}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/merchants/01a0395b-3211-725c-9216-7636c7375ecf" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3211-725c-9216-7636c7375ecf",
  "name": "商户M1010143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3207-72fa-b45b-a563d7a53bf7",
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
  "createdAt": "2026-08-25T14:37:53.04108Z",
  "updatedAt": "2026-08-25T14:37:53.04108Z"
}
```
