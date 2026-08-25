# TC-recommend-list-IT-017 请求/响应存证

用例: POST /api/admin/recommend-lists status 默认 ONLINE 且可带 status/merchantIds 创建
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A017143752", "englishName": "CityA017143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-32c0-71a0-9a5f-24d14cb39519",
  "chineseName": "测城A017143752",
  "englishName": "CityA017143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:53.216056454Z",
  "updatedAt": "2026-08-25T14:37:53.216056454Z"
}
```

## Step 2: 前置：POST /api/admin/merchants 创建商户 M1（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户M1017143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-32c0-71a0-9a5f-24d14cb39519", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 10, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-32c9-7cb9-b756-7a8ca2673460",
  "name": "商户M1017143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-32c0-71a0-9a5f-24d14cb39519",
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
  "createdAt": "2026-08-25T14:37:53.225760069Z",
  "updatedAt": "2026-08-25T14:37:53.225760069Z"
}
```

## Step 3: POST /api/admin/recommend-lists（不传 status）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "默认上架清单", "cityId": "01a0395b-32c0-71a0-9a5f-24d14cb39519"}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-32d5-79ba-b9cf-7aeb332c62f9",
  "title": "默认上架清单",
  "introduction": null,
  "cityId": "01a0395b-32c0-71a0-9a5f-24d14cb39519",
  "sortOrder": 0,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:53.237570956Z",
  "updatedAt": "2026-08-25T14:37:53.237570956Z",
  "status": "ONLINE"
}
```

## Step 4: GET /api/admin/recommend-lists/{id1}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-32d5-79ba-b9cf-7aeb332c62f9" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-32d5-79ba-b9cf-7aeb332c62f9",
  "title": "默认上架清单",
  "introduction": null,
  "cityId": "01a0395b-32c0-71a0-9a5f-24d14cb39519",
  "sortOrder": 0,
  "merchants": [],
  "createdAt": "2026-08-25T14:37:53.237571Z",
  "updatedAt": "2026-08-25T14:37:53.237571Z",
  "status": "ONLINE"
}
```

## Step 5: POST /api/admin/recommend-lists status=OFFLINE merchantIds=[M1]

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "下架带商户清单", "cityId": "01a0395b-32c0-71a0-9a5f-24d14cb39519", "status": "OFFLINE", "merchantIds": ["01a0395b-32c9-7cb9-b756-7a8ca2673460"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-32e8-726c-8b23-4f86b712e165",
  "title": "下架带商户清单",
  "introduction": null,
  "cityId": "01a0395b-32c0-71a0-9a5f-24d14cb39519",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-32c9-7cb9-b756-7a8ca2673460",
      "name": "商户M1017143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:53.256112409Z",
  "updatedAt": "2026-08-25T14:37:53.256112409Z",
  "status": "OFFLINE"
}
```

## Step 6: GET /api/admin/recommend-lists/{id2}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-32e8-726c-8b23-4f86b712e165" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-32e8-726c-8b23-4f86b712e165",
  "title": "下架带商户清单",
  "introduction": null,
  "cityId": "01a0395b-32c0-71a0-9a5f-24d14cb39519",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-32c9-7cb9-b756-7a8ca2673460",
      "name": "商户M1017143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:53.256112Z",
  "updatedAt": "2026-08-25T14:37:53.256112Z",
  "status": "OFFLINE"
}
```
