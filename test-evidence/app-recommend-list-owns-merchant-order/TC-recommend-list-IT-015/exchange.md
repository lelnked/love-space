# TC-recommend-list-IT-015 请求/响应存证

用例: GET /api/app/merchants/page 商户列表不受清单影响
执行日期: 2026-08-25 ｜ change: app-recommend-list-owns-merchant-order ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 X-API-Key: test-api-key
说明: admin 侧 `PUT /api/admin/recommend-lists/{id}/merchants` 已删除，清单内商户改由 POST/PUT body 的 `merchantIds`（有序 UUID 数组）整体替换；图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）。

## Step 1: 前置：POST /api/admin/cities 创建上架城市 D

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"测城D140542","englishName":"CityD140542","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393d-bff0-76d8-8485-96e1f5251ec2",
  "chineseName": "测城D140542",
  "englishName": "CityD140542",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:05:43.280360589Z",
  "updatedAt": "2026-08-25T14:05:43.280360589Z"
}
```

## Step 2: 前置：POST /api/admin/merchants 商户甲（weight=10，上架）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"商户甲140600","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.30,"latitude":30.59,"cityId":"01a0393d-bff0-76d8-8485-96e1f5251ec2","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":10,"online":true,"images":["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-04f8-7993-9e22-0e0502e7fb57",
  "name": "商户甲140600",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=P0gPelfaYHiT2K59%2BJ1FyNyIlAc%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0393d-bff0-76d8-8485-96e1f5251ec2",
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
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=rkhUI%2FHlOovzRgQvYbGzqDb5tmQ%3D"
    }
  ],
  "createdAt": "2026-08-25T14:06:00.952542931Z",
  "updatedAt": "2026-08-25T14:06:00.952542931Z"
}
```

## Step 3: 前置：POST /api/admin/merchants 商户乙（weight=30，上架）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"商户乙140600","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.30,"latitude":30.59,"cityId":"01a0393d-bff0-76d8-8485-96e1f5251ec2","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":30,"online":true,"images":["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-050e-7c3c-b1e3-e9ae4adf31b7",
  "name": "商户乙140600",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=P0gPelfaYHiT2K59%2BJ1FyNyIlAc%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0393d-bff0-76d8-8485-96e1f5251ec2",
  "categoryId": null,
  "safetyEnvironmentScore": 25,
  "businessRightsScore": 20,
  "experienceFriendlyScore": 20,
  "socialContributionScore": 15,
  "story": null,
  "recommendReason": null,
  "weight": 30,
  "online": true,
  "periods": [],
  "tagIds": [],
  "images": [
    {
      "id": "bound/img-test-1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=rkhUI%2FHlOovzRgQvYbGzqDb5tmQ%3D"
    }
  ],
  "createdAt": "2026-08-25T14:06:00.974716682Z",
  "updatedAt": "2026-08-25T14:06:00.974716682Z"
}
```

## Step 4: 前置：POST /api/admin/merchants 商户丙（weight=20，上架）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"商户丙140600","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.30,"latitude":30.59,"cityId":"01a0393d-bff0-76d8-8485-96e1f5251ec2","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":20,"online":true,"images":["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-0523-714b-a802-1307cf0efc93",
  "name": "商户丙140600",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=P0gPelfaYHiT2K59%2BJ1FyNyIlAc%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0393d-bff0-76d8-8485-96e1f5251ec2",
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
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=rkhUI%2FHlOovzRgQvYbGzqDb5tmQ%3D"
    }
  ],
  "createdAt": "2026-08-25T14:06:00.995028566Z",
  "updatedAt": "2026-08-25T14:06:00.995028566Z"
}
```

## Step 5: 前置：POST /api/admin/recommend-lists 清单 L，merchantIds=[甲,乙]（与 weight 排序相反）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"商户列表清单140600","introduction":"L","cityId":"01a0393d-bff0-76d8-8485-96e1f5251ec2","sortOrder":1,"merchantIds":["01a0393e-04f8-7993-9e22-0e0502e7fb57","01a0393e-050e-7c3c-b1e3-e9ae4adf31b7"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-0535-7b86-bcc3-c79d390dc27d",
  "title": "商户列表清单140600",
  "introduction": "L",
  "cityId": "01a0393d-bff0-76d8-8485-96e1f5251ec2",
  "sortOrder": 1,
  "merchants": [
    {
      "merchantId": "01a0393e-04f8-7993-9e22-0e0502e7fb57",
      "name": "商户甲140600",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787668561&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=xRmV2kBVt283z690celPXYafE4I%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    },
    {
      "merchantId": "01a0393e-050e-7c3c-b1e3-e9ae4adf31b7",
      "name": "商户乙140600",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787668561&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=xRmV2kBVt283z690celPXYafE4I%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 2
    }
  ],
  "createdAt": "2026-08-25T14:06:01.013663115Z",
  "updatedAt": "2026-08-25T14:06:01.013663115Z",
  "status": "ONLINE"
}
```

## Step 6: GET /api/app/merchants/page?cityId=D（X-API-Key，不带 recommendListId）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/merchants/page?cityId=01a0393d-bff0-76d8-8485-96e1f5251ec2" -H "X-API-Key: test-api-key"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [
    {
      "id": "01a0393e-050e-7c3c-b1e3-e9ae4adf31b7",
      "name": "商户乙140600",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://placeholder.oss-cn-placeholder.aliyuncs.com/bound/logo-test.png?Expires=1787668561&OSSAccessKeyId=placeholder&Signature=nFqENzk2dsL04bkdv%2F7%2FL37nzIs%3D"
      },
      "address": "测试路 1 号",
      "tags": [],
      "scores": {
        "safetyEnvironmentPercent": 83,
        "businessRightsPercent": 80,
        "experienceFriendlyPercent": 80,
        "socialContributionPercent": 75
      },
      "loveIndex": {
        "total": 80,
        "level": 8
      }
    },
    {
      "id": "01a0393e-0523-714b-a802-1307cf0efc93",
      "name": "商户丙140600",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://placeholder.oss-cn-placeholder.aliyuncs.com/bound/logo-test.png?Expires=1787668561&OSSAccessKeyId=placeholder&Signature=nFqENzk2dsL04bkdv%2F7%2FL37nzIs%3D"
      },
      "address": "测试路 1 号",
      "tags": [],
      "scores": {
        "safetyEnvironmentPercent": 83,
        "businessRightsPercent": 80,
        "experienceFriendlyPercent": 80,
        "socialContributionPercent": 75
      },
      "loveIndex": {
        "total": 80,
        "level": 8
      }
    },
    {
      "id": "01a0393e-04f8-7993-9e22-0e0502e7fb57",
      "name": "商户甲140600",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://placeholder.oss-cn-placeholder.aliyuncs.com/bound/logo-test.png?Expires=1787668561&OSSAccessKeyId=placeholder&Signature=nFqENzk2dsL04bkdv%2F7%2FL37nzIs%3D"
      },
      "address": "测试路 1 号",
      "tags": [],
      "scores": {
        "safetyEnvironmentPercent": 83,
        "businessRightsPercent": 80,
        "experienceFriendlyPercent": 80,
        "socialContributionPercent": 75
      },
      "loveIndex": {
        "total": 80,
        "level": 8
      }
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 3,
  "totalPages": 1
}
```

## Step 7: GET /api/app/merchants/page?cityId=D&recommendListId=L（X-API-Key）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/merchants/page?cityId=01a0393d-bff0-76d8-8485-96e1f5251ec2&recommendListId=01a0393e-0535-7b86-bcc3-c79d390dc27d" -H "X-API-Key: test-api-key"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [
    {
      "id": "01a0393e-050e-7c3c-b1e3-e9ae4adf31b7",
      "name": "商户乙140600",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://placeholder.oss-cn-placeholder.aliyuncs.com/bound/logo-test.png?Expires=1787668561&OSSAccessKeyId=placeholder&Signature=nFqENzk2dsL04bkdv%2F7%2FL37nzIs%3D"
      },
      "address": "测试路 1 号",
      "tags": [],
      "scores": {
        "safetyEnvironmentPercent": 83,
        "businessRightsPercent": 80,
        "experienceFriendlyPercent": 80,
        "socialContributionPercent": 75
      },
      "loveIndex": {
        "total": 80,
        "level": 8
      }
    },
    {
      "id": "01a0393e-0523-714b-a802-1307cf0efc93",
      "name": "商户丙140600",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://placeholder.oss-cn-placeholder.aliyuncs.com/bound/logo-test.png?Expires=1787668561&OSSAccessKeyId=placeholder&Signature=nFqENzk2dsL04bkdv%2F7%2FL37nzIs%3D"
      },
      "address": "测试路 1 号",
      "tags": [],
      "scores": {
        "safetyEnvironmentPercent": 83,
        "businessRightsPercent": 80,
        "experienceFriendlyPercent": 80,
        "socialContributionPercent": 75
      },
      "loveIndex": {
        "total": 80,
        "level": 8
      }
    },
    {
      "id": "01a0393e-04f8-7993-9e22-0e0502e7fb57",
      "name": "商户甲140600",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://placeholder.oss-cn-placeholder.aliyuncs.com/bound/logo-test.png?Expires=1787668561&OSSAccessKeyId=placeholder&Signature=nFqENzk2dsL04bkdv%2F7%2FL37nzIs%3D"
      },
      "address": "测试路 1 号",
      "tags": [],
      "scores": {
        "safetyEnvironmentPercent": 83,
        "businessRightsPercent": 80,
        "experienceFriendlyPercent": 80,
        "socialContributionPercent": 75
      },
      "loveIndex": {
        "total": 80,
        "level": 8
      }
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 3,
  "totalPages": 1
}
```
