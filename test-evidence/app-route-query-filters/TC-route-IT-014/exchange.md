# TC-route-IT-014 GET /api/app/routes/{id} 路线详情返回地点明细与大使信息 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
app 端请求头 `X-API-Key: $APP_API_KEY`。shell 中 `export TOKEN=<登录返回 token>`、`export APP_API_KEY=<app API key>` 后下列 curl 可原样执行。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"详情城014","englishName":"DetailCity014","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b2-3560-7463-b1b2-94bcdd89b627",
  "chineseName": "详情城014",
  "englishName": "DetailCity014",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-24T16:54:49.440196975Z",
  "updatedAt": "2026-08-24T16:54:49.440196975Z"
}
```

## Step 1b: 创建带头像/名称/标签的 online 大使

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/01a034b2-356f-7dc4-a0ef-3ce369e82021.png","name":"详情大使014","tags":["古着","咖啡"],"online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b2-38ad-761d-a208-d764e1e3006e",
  "avatar": {
    "id": "bound/01a034b2-356f-7dc4-a0ef-3ce369e82021.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-356f-7dc4-a0ef-3ce369e82021.png?Expires=1787592290&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dRYHmtOV6qGS1xCwDzoQ3J3X0a4%3D"
  },
  "name": "详情大使014",
  "tags": [
    "古着",
    "咖啡"
  ],
  "online": true,
  "createdAt": "2026-08-24T16:54:50.285321054Z",
  "updatedAt": "2026-08-24T16:54:50.285321054Z"
}
```

## Step 1c: 创建含 2 个地点（S1、S2）的路线

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"详情城014","sortOrder":1,"title":"详情路线014","ambassadorNote":"大使说","thumbnail":"images/01a034b2-38c1-7101-b080-ed30a4430245.png","images":["images/01a034b2-3b1b-773d-aa88-a3a5b39272d0.png","images/01a034b2-3e81-748b-adc8-acee1fadc516.png"],"travelTime":"2 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b2-38ad-761d-a208-d764e1e3006e","spots":[{"name":"S1","image":"images/01a034b2-41e5-75e7-9960-5ca5d85afab8.png","introduction":"介绍1"},{"name":"S2","image":"images/01a034b2-43e0-7054-a4ad-abeaeaeedd00.png","introduction":"介绍2"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b2-4a1e-7951-a9ac-69b149cbddde",
  "sortOrder": 1,
  "title": "详情路线014",
  "cityName": "详情城014",
  "ambassadorNote": "大使说",
  "thumbnail": {
    "id": "bound/01a034b2-38c1-7101-b080-ed30a4430245.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-38c1-7101-b080-ed30a4430245.png?Expires=1787592294&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mXHDhYbB5pKgo18A3QbpSJLSkkM%3D"
  },
  "images": [
    {
      "id": "bound/01a034b2-3b1b-773d-aa88-a3a5b39272d0.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-3b1b-773d-aa88-a3a5b39272d0.png?Expires=1787592294&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lq5pkoCA55wIg7bd9v38%2BBUv2cg%3D"
    },
    {
      "id": "bound/01a034b2-3e81-748b-adc8-acee1fadc516.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-3e81-748b-adc8-acee1fadc516.png?Expires=1787592294&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cvfM8vzk4%2FEO4q1ejw%2BsrcNMfu0%3D"
    }
  ],
  "travelTime": "2 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "详情大使014",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b2-41e5-75e7-9960-5ca5d85afab8.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-41e5-75e7-9960-5ca5d85afab8.png?Expires=1787592294&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=HqLnOF8%2Bxu7LNKhFQkZUSxS30Tg%3D"
      },
      "introduction": "介绍1"
    },
    {
      "name": "S2",
      "image": {
        "id": "bound/01a034b2-43e0-7054-a4ad-abeaeaeedd00.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-43e0-7054-a4ad-abeaeaeedd00.png?Expires=1787592294&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=PgSexEGzdGOBEGTl1waQzZ05kRs%3D"
      },
      "introduction": "介绍2"
    }
  ],
  "createdAt": "2026-08-24T16:54:54.75050454Z",
  "updatedAt": "2026-08-24T16:54:54.75050454Z"
}
```

## Step 2: app 端查询路线详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a034b2-4a1e-7951-a9ac-69b149cbddde" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "cityName": "详情城014",
  "sortOrder": 1,
  "title": "详情路线014",
  "ambassadorNote": "大使说",
  "thumbnail": {
    "id": "bound/01a034b2-38c1-7101-b080-ed30a4430245.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-38c1-7101-b080-ed30a4430245.png?Expires=1787592294&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mXHDhYbB5pKgo18A3QbpSJLSkkM%3D"
  },
  "images": [
    {
      "id": "bound/01a034b2-3b1b-773d-aa88-a3a5b39272d0.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-3b1b-773d-aa88-a3a5b39272d0.png?Expires=1787592294&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lq5pkoCA55wIg7bd9v38%2BBUv2cg%3D"
    },
    {
      "id": "bound/01a034b2-3e81-748b-adc8-acee1fadc516.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-3e81-748b-adc8-acee1fadc516.png?Expires=1787592294&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cvfM8vzk4%2FEO4q1ejw%2BsrcNMfu0%3D"
    }
  ],
  "travelTime": "2 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassador": {
    "name": "详情大使014",
    "avatar": {
      "id": "bound/01a034b2-356f-7dc4-a0ef-3ce369e82021.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-356f-7dc4-a0ef-3ce369e82021.png?Expires=1787592294&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=B4yUpqCQt2tluCIMjF1LopnFIPg%3D"
    },
    "tags": [
      "古着",
      "咖啡"
    ]
  },
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b2-41e5-75e7-9960-5ca5d85afab8.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-41e5-75e7-9960-5ca5d85afab8.png?Expires=1787592294&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=HqLnOF8%2Bxu7LNKhFQkZUSxS30Tg%3D"
      },
      "introduction": "介绍1"
    },
    {
      "name": "S2",
      "image": {
        "id": "bound/01a034b2-43e0-7054-a4ad-abeaeaeedd00.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-43e0-7054-a4ad-abeaeaeedd00.png?Expires=1787592294&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=PgSexEGzdGOBEGTl1waQzZ05kRs%3D"
      },
      "introduction": "介绍2"
    }
  ],
  "city": {
    "id": "01a034b2-3560-7463-b1b2-94bcdd89b627",
    "name": "详情城014"
  },
  "createdAt": "2026-08-24T16:54:54.750505Z",
  "updatedAt": "2026-08-24T16:54:54.750505Z"
}
```
