# TC-recommend-list-IT-018 请求/响应存证

用例: POST /api/admin/recommend-lists/{id}/online 人工恢复清单（含下架商户拒绝、成功、幂等）
执行日期: 2026-08-25 ｜ change: recommend-list-align-spec-to-merchant-ids ｜ admin=http://localhost:21423（test profile）
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）
说明: 图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）；`PUT /api/admin/merchants/{id}/online` 未登记于 api-spec.json，按 ⚠️ 契约漂移记录不判失败。

## Step 1: 前置：POST /api/admin/cities 创建城市 A

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName": "测城A018143752", "englishName": "CityA018143752", "chineseProvince": "测试省", "englishProvince": "Test Province", "online": true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3300-7c12-9725-bfc9affc3a6d",
  "chineseName": "测城A018143752",
  "englishName": "CityA018143752",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:37:53.28071828Z",
  "updatedAt": "2026-08-25T14:37:53.28071828Z"
}
```

## Step 2: 前置：POST /api/admin/merchants 创建商户 M1（online=true）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name": "商户M1018143752", "logo": "bound/logo-test.png", "address": "测试路 1 号", "longitude": 114.3, "latitude": 30.59, "cityId": "01a0395b-3300-7c12-9725-bfc9affc3a6d", "safetyEnvironmentScore": 25, "businessRightsScore": 20, "experienceFriendlyScore": 20, "socialContributionScore": 15, "weight": 10, "online": true, "images": ["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3309-7bd5-bab0-e1fe3c259fb9",
  "name": "商户M1018143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3300-7c12-9725-bfc9affc3a6d",
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
  "createdAt": "2026-08-25T14:37:53.289700174Z",
  "updatedAt": "2026-08-25T14:37:53.289700174Z"
}
```

## Step 3: 前置：POST /api/admin/recommend-lists 创建清单 OFFLINE清单

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title": "OFFLINE清单143752", "cityId": "01a0395b-3300-7c12-9725-bfc9affc3a6d", "status": "OFFLINE", "merchantIds": ["01a0395b-3309-7bd5-bab0-e1fe3c259fb9"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3314-7bf1-98e6-9cca9dfe3832",
  "title": "OFFLINE清单143752",
  "introduction": null,
  "cityId": "01a0395b-3300-7c12-9725-bfc9affc3a6d",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-3309-7bd5-bab0-e1fe3c259fb9",
      "name": "商户M1018143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:53.300713726Z",
  "updatedAt": "2026-08-25T14:37:53.300713726Z",
  "status": "OFFLINE"
}
```

## Step 4: PUT /api/admin/merchants/{id}/online online=false

```bash
curl -s -i -X "PUT" "http://localhost:21423/api/admin/merchants/01a0395b-3309-7bd5-bab0-e1fe3c259fb9/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online": false}'
```

> ⚠️ 契约漂移：PUT /api/admin/merchants/{id}/online 未登记于 api-spec.json（归 merchant 域，本 change 不补），不判失败

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3309-7bd5-bab0-e1fe3c259fb9",
  "name": "商户M1018143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3300-7c12-9725-bfc9affc3a6d",
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
  "createdAt": "2026-08-25T14:37:53.2897Z",
  "updatedAt": "2026-08-25T14:37:53.315547057Z"
}
```

## Step 5: POST /api/admin/recommend-lists/{L}/online（含下架商户）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists/01a0395b-3314-7bf1-98e6-9cca9dfe3832/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "清单内存在未上架商户，请先清理后再恢复清单",
  "path": "/api/admin/recommend-lists/01a0395b-3314-7bf1-98e6-9cca9dfe3832/online"
}
```

## Step 6: GET /api/admin/recommend-lists/{L}

```bash
curl -s -i -X "GET" "http://localhost:21423/api/admin/recommend-lists/01a0395b-3314-7bf1-98e6-9cca9dfe3832" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3314-7bf1-98e6-9cca9dfe3832",
  "title": "OFFLINE清单143752",
  "introduction": null,
  "cityId": "01a0395b-3300-7c12-9725-bfc9affc3a6d",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-3309-7bd5-bab0-e1fe3c259fb9",
      "name": "商户M1018143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
      },
      "address": "测试路 1 号",
      "online": false,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:53.300714Z",
  "updatedAt": "2026-08-25T14:37:53.300714Z",
  "status": "OFFLINE"
}
```

## Step 7: PUT /api/admin/merchants/{id}/online online=true

```bash
curl -s -i -X "PUT" "http://localhost:21423/api/admin/merchants/01a0395b-3309-7bd5-bab0-e1fe3c259fb9/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online": true}'
```

> ⚠️ 契约漂移：PUT /api/admin/merchants/{id}/online 未登记于 api-spec.json（归 merchant 域，本 change 不补），不判失败

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3309-7bd5-bab0-e1fe3c259fb9",
  "name": "商户M1018143752",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0395b-3300-7c12-9725-bfc9affc3a6d",
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
  "createdAt": "2026-08-25T14:37:53.2897Z",
  "updatedAt": "2026-08-25T14:37:53.35553197Z"
}
```

## Step 8: POST /api/admin/recommend-lists/{L}/online

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists/01a0395b-3314-7bf1-98e6-9cca9dfe3832/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3314-7bf1-98e6-9cca9dfe3832",
  "title": "OFFLINE清单143752",
  "introduction": null,
  "cityId": "01a0395b-3300-7c12-9725-bfc9affc3a6d",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-3309-7bd5-bab0-e1fe3c259fb9",
      "name": "商户M1018143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:53.300714Z",
  "updatedAt": "2026-08-25T14:37:53.371697227Z",
  "status": "ONLINE"
}
```

## Step 9: POST /api/admin/recommend-lists/{L}/online（再次调用，幂等）

```bash
curl -s -i -X "POST" "http://localhost:21423/api/admin/recommend-lists/01a0395b-3314-7bf1-98e6-9cca9dfe3832/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0395b-3314-7bf1-98e6-9cca9dfe3832",
  "title": "OFFLINE清单143752",
  "introduction": null,
  "cityId": "01a0395b-3300-7c12-9725-bfc9affc3a6d",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a0395b-3309-7bd5-bab0-e1fe3c259fb9",
      "name": "商户M1018143752",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787670473&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mDj6u9MOjYngFGgOy1T4ACdXcYs%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-25T14:37:53.300714Z",
  "updatedAt": "2026-08-25T14:37:53.372645Z",
  "status": "ONLINE"
}
```
