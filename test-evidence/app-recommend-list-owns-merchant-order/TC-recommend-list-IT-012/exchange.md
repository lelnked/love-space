# TC-recommend-list-IT-012 请求/响应存证

用例: GET /api/app/recommend-lists/{id} 详情按清单保存顺序返回上架商户四字段
执行日期: 2026-08-25 ｜ change: app-recommend-list-owns-merchant-order ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 X-API-Key: test-api-key
说明: admin 侧 `PUT /api/admin/recommend-lists/{id}/merchants` 已删除，清单内商户改由 POST/PUT body 的 `merchantIds`（有序 UUID 数组）整体替换；图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）。

## Step 1: 前置：POST /api/admin/cities 创建上架城市 C

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"测城C140542","englishName":"CityC140542","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393d-bf3a-785c-aac9-328e65eab147",
  "chineseName": "测城C140542",
  "englishName": "CityC140542",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:05:43.098464367Z",
  "updatedAt": "2026-08-25T14:05:43.098464367Z"
}
```

## Step 2: 前置：POST /api/admin/merchants 商户甲（weight=10，上架）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"商户甲140600","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.30,"latitude":30.59,"cityId":"01a0393d-bf3a-785c-aac9-328e65eab147","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":10,"online":true,"images":["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-03b5-7805-8e9d-d002feb4a774",
  "name": "商户甲140600",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=P0gPelfaYHiT2K59%2BJ1FyNyIlAc%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0393d-bf3a-785c-aac9-328e65eab147",
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
  "createdAt": "2026-08-25T14:06:00.628004616Z",
  "updatedAt": "2026-08-25T14:06:00.628004616Z"
}
```

## Step 3: 前置：POST /api/admin/merchants 商户乙（weight=30，上架）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"商户乙140600","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.30,"latitude":30.59,"cityId":"01a0393d-bf3a-785c-aac9-328e65eab147","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":30,"online":true,"images":["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-03e4-7f68-bbda-d59acd96c825",
  "name": "商户乙140600",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=P0gPelfaYHiT2K59%2BJ1FyNyIlAc%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0393d-bf3a-785c-aac9-328e65eab147",
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
  "createdAt": "2026-08-25T14:06:00.676907732Z",
  "updatedAt": "2026-08-25T14:06:00.676907732Z"
}
```

## Step 4: 前置：POST /api/admin/merchants 商户丙（weight=20，上架；稍后下架）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"商户丙140600","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.30,"latitude":30.59,"cityId":"01a0393d-bf3a-785c-aac9-328e65eab147","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":20,"online":true,"images":["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-03fb-724e-9cfb-7845133a8277",
  "name": "商户丙140600",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=P0gPelfaYHiT2K59%2BJ1FyNyIlAc%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0393d-bf3a-785c-aac9-328e65eab147",
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
  "createdAt": "2026-08-25T14:06:00.699082889Z",
  "updatedAt": "2026-08-25T14:06:00.699082889Z"
}
```

## Step 5: 前置：POST /api/admin/recommend-lists 创建清单，merchantIds=[甲,乙,丙]（数组顺序 = 清单保存顺序）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"详情清单140600","introduction":"含商户明细","cityId":"01a0393d-bf3a-785c-aac9-328e65eab147","sortOrder":2,"merchantIds":["01a0393e-03b5-7805-8e9d-d002feb4a774","01a0393e-03e4-7f68-bbda-d59acd96c825","01a0393e-03fb-724e-9cfb-7845133a8277"]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-0410-7035-a5b9-2ad802ff5a3b",
  "title": "详情清单140600",
  "introduction": "含商户明细",
  "cityId": "01a0393d-bf3a-785c-aac9-328e65eab147",
  "sortOrder": 2,
  "merchants": [
    {
      "merchantId": "01a0393e-03b5-7805-8e9d-d002feb4a774",
      "name": "商户甲140600",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=P0gPelfaYHiT2K59%2BJ1FyNyIlAc%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    },
    {
      "merchantId": "01a0393e-03e4-7f68-bbda-d59acd96c825",
      "name": "商户乙140600",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=P0gPelfaYHiT2K59%2BJ1FyNyIlAc%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 2
    },
    {
      "merchantId": "01a0393e-03fb-724e-9cfb-7845133a8277",
      "name": "商户丙140600",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=P0gPelfaYHiT2K59%2BJ1FyNyIlAc%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 3
    }
  ],
  "createdAt": "2026-08-25T14:06:00.719960628Z",
  "updatedAt": "2026-08-25T14:06:00.719960628Z",
  "status": "ONLINE"
}
```

## Step 6: 前置：PUT /api/admin/merchants/{丙}/online {"online":false} 下架商户丙

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/merchants/01a0393e-03fb-724e-9cfb-7845133a8277/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":false}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-03fb-724e-9cfb-7845133a8277",
  "name": "商户丙140600",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=P0gPelfaYHiT2K59%2BJ1FyNyIlAc%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.3,
  "latitude": 30.59,
  "cityId": "01a0393d-bf3a-785c-aac9-328e65eab147",
  "categoryId": null,
  "safetyEnvironmentScore": 25,
  "businessRightsScore": 20,
  "experienceFriendlyScore": 20,
  "socialContributionScore": 15,
  "story": null,
  "recommendReason": null,
  "weight": 20,
  "online": false,
  "periods": [],
  "tagIds": [],
  "images": [
    {
      "id": "bound/img-test-1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1787668560&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=rkhUI%2FHlOovzRgQvYbGzqDb5tmQ%3D"
    }
  ],
  "createdAt": "2026-08-25T14:06:00.699083Z",
  "updatedAt": "2026-08-25T14:06:00.773510926Z"
}
```

## Step 7: GET /api/app/recommend-lists/{id}（X-API-Key）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists/01a0393e-0410-7035-a5b9-2ad802ff5a3b" -H "X-API-Key: test-api-key"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-0410-7035-a5b9-2ad802ff5a3b",
  "title": "详情清单140600",
  "introduction": "含商户明细",
  "cityId": "01a0393d-bf3a-785c-aac9-328e65eab147",
  "sortOrder": 2,
  "merchants": [
    {
      "id": "01a0393e-03b5-7805-8e9d-d002feb4a774",
      "name": "商户甲140600",
      "address": "测试路 1 号",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://placeholder.oss-cn-placeholder.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=placeholder&Signature=hRhRM7TG9enuWN4fzXuSxuF4YWg%3D"
      }
    },
    {
      "id": "01a0393e-03e4-7f68-bbda-d59acd96c825",
      "name": "商户乙140600",
      "address": "测试路 1 号",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://placeholder.oss-cn-placeholder.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=placeholder&Signature=hRhRM7TG9enuWN4fzXuSxuF4YWg%3D"
      }
    }
  ]
}
```
