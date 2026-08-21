# TC-route-IT-015 GET /api/app/routes 未上架城市的路线仍可见且详情返回 cityName — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1a: 创建下架城市「未上线城」

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"未上线城","englishName":"OfflineCity015X","chineseProvince":"测试省","englishProvince":"Test Province","online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb4-7342-7b70-bbdf-f929624c09af",
  "chineseName": "未上线城",
  "englishName": "OfflineCity015X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-20T15:05:14.805237912Z",
  "updatedAt": "2026-08-20T15:05:14.805237912Z"
}
```

## Step 1b: 创建 online=true 的大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/it015-avatar.png","name":"路线大使015","tags":["向导"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb4-73a0-7621-8ed4-21ec464f84df",
  "avatar": {
    "id": "bound/it015-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-avatar.png?Expires=1787240114&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=5%2FRuHvXndRFTOxJZeC9FTmjgBTA%3D"
  },
  "name": "路线大使015",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:05:14.91124391Z",
  "updatedAt": "2026-08-20T15:05:14.91124391Z"
}
```

## Step 1c: 在下架城市下创建路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb4-7342-7b70-bbdf-f929624c09af","sortOrder":1,"title":"下架城路线015","ambassadorNote":"大使推荐语","thumbnail":"images/it015-thumb.png","images":["images/it015-img1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a01fb4-73a0-7621-8ed4-21ec464f84df","spots":[{"name":"S1 江畔步道","image":"images/it015-s1.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/it015-s2.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb4-7408-7570-b53e-09f17f1ed84b",
  "cityId": "01a01fb4-7342-7b70-bbdf-f929624c09af",
  "sortOrder": 1,
  "title": "下架城路线015",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/it015-thumb.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-thumb.png?Expires=1787240115&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=96Y4FT0VmKVOd9a0gErp33edSog%3D"
  },
  "images": [
    {
      "id": "bound/it015-img1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-img1.png?Expires=1787240115&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tsejGfPV2UPlUUU3dieCiavYmYU%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a01fb4-73a0-7621-8ed4-21ec464f84df",
  "ambassadorName": "路线大使015",
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/it015-s1.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-s1.png?Expires=1787240115&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=hjWzpuAK3Xau0ziJLi4N4hCLXZ8%3D"
      },
      "introduction": "清晨散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/it015-s2.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-s2.png?Expires=1787240115&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=sdUIqZXYQK7k88TrWYV3FThQw%2Fg%3D"
      },
      "introduction": "午后咖啡"
    }
  ],
  "createdAt": "2026-08-20T15:05:15.014659583Z",
  "updatedAt": "2026-08-20T15:05:15.014659583Z"
}
```

## Step 2: app 端查询该城市路线列表（城市仍为下架）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a01fb4-7342-7b70-bbdf-f929624c09af" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a01fb4-7408-7570-b53e-09f17f1ed84b",
    "title": "下架城路线015",
    "thumbnail": {
      "id": "bound/it015-thumb.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it015-thumb.png?Expires=1787240115&OSSAccessKeyId=test-oss-ak&Signature=bAMFmraHNP3D%2FCmJupzKKViHvW8%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "路线大使015"
  }
]
```

## Step 3: app 端查询路线详情（城市仍为下架）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a01fb4-7408-7570-b53e-09f17f1ed84b" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb4-7408-7570-b53e-09f17f1ed84b",
  "cityId": "01a01fb4-7342-7b70-bbdf-f929624c09af",
  "cityName": "未上线城",
  "sortOrder": 1,
  "title": "下架城路线015",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/it015-thumb.png",
    "url": "https://love-space-test.oss-test.example.com/bound/it015-thumb.png?Expires=1787240115&OSSAccessKeyId=test-oss-ak&Signature=bAMFmraHNP3D%2FCmJupzKKViHvW8%3D"
  },
  "images": [
    {
      "id": "bound/it015-img1.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it015-img1.png?Expires=1787240115&OSSAccessKeyId=test-oss-ak&Signature=f7pX%2BlZAuHPsRoBw%2BJ6zhbnjV84%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassador": {
    "name": "路线大使015",
    "avatar": {
      "id": "bound/it015-avatar.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it015-avatar.png?Expires=1787240115&OSSAccessKeyId=test-oss-ak&Signature=rp%2B9Zuu5avwm4Ku6j%2BxOlY1T%2Fb8%3D"
    },
    "tags": [
      "向导"
    ]
  },
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/it015-s1.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it015-s1.png?Expires=1787240115&OSSAccessKeyId=test-oss-ak&Signature=x06VfCLlfDoqIasLi7WkHO%2Fz8CI%3D"
      },
      "introduction": "清晨散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/it015-s2.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it015-s2.png?Expires=1787240115&OSSAccessKeyId=test-oss-ak&Signature=jSu8b2ThGs7LIfc4dQp7BWYG7go%3D"
      },
      "introduction": "午后咖啡"
    }
  ]
}
```

## Step 4a: 将该城市上架

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a01fb4-7342-7b70-bbdf-f929624c09af/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb4-7342-7b70-bbdf-f929624c09af",
  "chineseName": "未上线城",
  "englishName": "OfflineCity015X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:05:14.805238Z",
  "updatedAt": "2026-08-20T15:05:14.805238Z"
}
```

## Step 4b: 重复列表查询（城市已上架）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a01fb4-7342-7b70-bbdf-f929624c09af" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a01fb4-7408-7570-b53e-09f17f1ed84b",
    "title": "下架城路线015",
    "thumbnail": {
      "id": "bound/it015-thumb.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it015-thumb.png?Expires=1787240115&OSSAccessKeyId=test-oss-ak&Signature=bAMFmraHNP3D%2FCmJupzKKViHvW8%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "路线大使015"
  }
]
```

## Step 4c: 重复详情查询（城市已上架）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a01fb4-7408-7570-b53e-09f17f1ed84b" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb4-7408-7570-b53e-09f17f1ed84b",
  "cityId": "01a01fb4-7342-7b70-bbdf-f929624c09af",
  "cityName": "未上线城",
  "sortOrder": 1,
  "title": "下架城路线015",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/it015-thumb.png",
    "url": "https://love-space-test.oss-test.example.com/bound/it015-thumb.png?Expires=1787240115&OSSAccessKeyId=test-oss-ak&Signature=bAMFmraHNP3D%2FCmJupzKKViHvW8%3D"
  },
  "images": [
    {
      "id": "bound/it015-img1.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it015-img1.png?Expires=1787240115&OSSAccessKeyId=test-oss-ak&Signature=f7pX%2BlZAuHPsRoBw%2BJ6zhbnjV84%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassador": {
    "name": "路线大使015",
    "avatar": {
      "id": "bound/it015-avatar.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it015-avatar.png?Expires=1787240115&OSSAccessKeyId=test-oss-ak&Signature=rp%2B9Zuu5avwm4Ku6j%2BxOlY1T%2Fb8%3D"
    },
    "tags": [
      "向导"
    ]
  },
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/it015-s1.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it015-s1.png?Expires=1787240115&OSSAccessKeyId=test-oss-ak&Signature=x06VfCLlfDoqIasLi7WkHO%2Fz8CI%3D"
      },
      "introduction": "清晨散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/it015-s2.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it015-s2.png?Expires=1787240115&OSSAccessKeyId=test-oss-ak&Signature=jSu8b2ThGs7LIfc4dQp7BWYG7go%3D"
      },
      "introduction": "午后咖啡"
    }
  ]
}
```
