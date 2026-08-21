# TC-route-IT-014 GET /api/app/routes/{id} 路线详情返回地点明细与大使信息 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"详情城014","englishName":"DetailCity014X","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-60f1-7e29-9e5d-3eaf99e99c22",
  "chineseName": "详情城014",
  "englishName": "DetailCity014X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:08:26.737822272Z",
  "updatedAt": "2026-08-20T15:08:26.737822272Z"
}
```

## Step 1b: 创建配有头像/名称/标签的大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/it014-avatar.png","name":"路线大使014","tags":["向导","咖啡"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-612b-701f-9aaf-f5e3a98464fe",
  "avatar": {
    "id": "bound/it014-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-avatar.png?Expires=1787240306&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZG%2FCEbIYkTe%2Fp%2FheMlq%2Fc6in0PA%3D"
  },
  "name": "路线大使014",
  "tags": [
    "向导",
    "咖啡"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:08:26.794943318Z",
  "updatedAt": "2026-08-20T15:08:26.794943318Z"
}
```

## Step 1c: 创建含 2 个地点（S1、S2）的路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb7-60f1-7e29-9e5d-3eaf99e99c22","sortOrder":1,"title":"详情路线014","ambassadorNote":"大使推荐语","thumbnail":"images/it014-thumb.png","images":["images/it014-img1.png","images/it014-img2.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a01fb7-612b-701f-9aaf-f5e3a98464fe","spots":[{"name":"S1 江畔步道","image":"images/it014-s1.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/it014-s2.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-6167-74ba-a7ed-3f48e055585e",
  "cityId": "01a01fb7-60f1-7e29-9e5d-3eaf99e99c22",
  "sortOrder": 1,
  "title": "详情路线014",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/it014-thumb.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-thumb.png?Expires=1787240306&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zfz%2BXV0XFDnGeG%2BaQ%2FI4RjoCAnU%3D"
  },
  "images": [
    {
      "id": "bound/it014-img1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-img1.png?Expires=1787240306&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=LZjyuZVutemrxeKVMmA%2B2WPXNTM%3D"
    },
    {
      "id": "bound/it014-img2.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-img2.jpg?Expires=1787240306&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NIAgG5oV2tOBZ0TiXFVgN4R5o4o%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a01fb7-612b-701f-9aaf-f5e3a98464fe",
  "ambassadorName": "路线大使014",
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/it014-s1.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-s1.png?Expires=1787240306&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=bgtDmVzLjfNaffsbxfD%2FuhKXgYs%3D"
      },
      "introduction": "清晨散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/it014-s2.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-s2.png?Expires=1787240306&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=U6ifGRAjA%2BN5iyXVlK2pSdSY6Ek%3D"
      },
      "introduction": "午后咖啡"
    }
  ],
  "createdAt": "2026-08-20T15:08:26.855236574Z",
  "updatedAt": "2026-08-20T15:08:26.855236574Z"
}
```

## Step 2: app 端查询路线详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a01fb7-6167-74ba-a7ed-3f48e055585e" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-6167-74ba-a7ed-3f48e055585e",
  "cityId": "01a01fb7-60f1-7e29-9e5d-3eaf99e99c22",
  "cityName": "详情城014",
  "sortOrder": 1,
  "title": "详情路线014",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/it014-thumb.png",
    "url": "https://love-space-test.oss-test.example.com/bound/it014-thumb.png?Expires=1787240306&OSSAccessKeyId=test-oss-ak&Signature=JtLASIaoZT8N1ElKtQyLHnRwtTg%3D"
  },
  "images": [
    {
      "id": "bound/it014-img1.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it014-img1.png?Expires=1787240306&OSSAccessKeyId=test-oss-ak&Signature=kLk3%2Byy%2Br0qOkDx%2B%2Bs9fp8oLhvk%3D"
    },
    {
      "id": "bound/it014-img2.jpg",
      "url": "https://love-space-test.oss-test.example.com/bound/it014-img2.jpg?Expires=1787240306&OSSAccessKeyId=test-oss-ak&Signature=MeaimI9oowk60SCtks%2FdfbRHVPg%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassador": {
    "name": "路线大使014",
    "avatar": {
      "id": "bound/it014-avatar.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it014-avatar.png?Expires=1787240306&OSSAccessKeyId=test-oss-ak&Signature=UHgq5jzBcFwdlrVm2dwVjXBwwV4%3D"
    },
    "tags": [
      "向导",
      "咖啡"
    ]
  },
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/it014-s1.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it014-s1.png?Expires=1787240306&OSSAccessKeyId=test-oss-ak&Signature=GTFHAsak8AioMvOx2f32EvVre54%3D"
      },
      "introduction": "清晨散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/it014-s2.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it014-s2.png?Expires=1787240306&OSSAccessKeyId=test-oss-ak&Signature=Th6AOqaQsADgEemEfQcyxZhkuvE%3D"
      },
      "introduction": "午后咖啡"
    }
  ]
}
```
