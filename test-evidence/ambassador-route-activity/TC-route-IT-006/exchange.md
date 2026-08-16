# TC-route-IT-006 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"路线城6-162725","englishName":"City1627256324","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4435-7cd2-8ef8-0a8cc1ac8a10",
  "chineseName": "路线城6-162725",
  "englishName": "City1627256324",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:26.640572739Z",
  "updatedAt": "2026-08-16T16:27:26.640572739Z"
}
```

## Step 2: 前置：创建上线大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/8d44238e-1f5e-4e5f-a2d1-145d0915cb7b.png","name":"路线大使6-162725","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4466-7b1e-9abd-ce222f9f75b3",
  "avatar": {
    "id": "bound/8d44238e-1f5e-4e5f-a2d1-145d0915cb7b.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/8d44238e-1f5e-4e5f-a2d1-145d0915cb7b.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=o7MHWTSqYka1UWbJIWuVEkdXAc0%3D"
  },
  "name": "路线大使6-162725",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:26.694612015Z",
  "updatedAt": "2026-08-16T16:27:26.694612015Z"
}
```

## Step 3: POST /api/admin/routes 创建路线（全字段，spots S1→S2）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-4435-7cd2-8ef8-0a8cc1ac8a10","sortOrder":2,"title":"江畔一日线-162725","ambassadorNote":"大使推荐语","thumbnail":"images/52fda58a-e6f5-49c5-9c51-6195404676d6.png","images":["images/d4885edf-aff8-4c04-b401-d9c2e9850dfd.png","images/a491161e-46d6-46f2-b5a0-24308be0bb41.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a00b66-4466-7b1e-9abd-ce222f9f75b3","spots":[{"name":"S1 江畔步道","image":"images/2ad5d1b2-13dd-43bf-a991-feed4d124053.png","introduction":"清晨沿江散步"},{"name":"S2 咖啡小馆","image":"images/1c9454c0-465f-4e5a-b3f4-1b7903dc6cd7.png","introduction":"午后咖啡歇脚"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4518-7ed5-ad91-944e5bc22fe9",
  "cityId": "01a00b66-4435-7cd2-8ef8-0a8cc1ac8a10",
  "sortOrder": 2,
  "title": "江畔一日线-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/52fda58a-e6f5-49c5-9c51-6195404676d6.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/52fda58a-e6f5-49c5-9c51-6195404676d6.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qVxA95rgkAR24LH%2B%2Bi4G%2B33CXWM%3D"
  },
  "images": [
    {
      "id": "bound/d4885edf-aff8-4c04-b401-d9c2e9850dfd.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d4885edf-aff8-4c04-b401-d9c2e9850dfd.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=TaMgS%2BHwCmDQo9PdG2U%2BZcrvPbA%3D"
    },
    {
      "id": "bound/a491161e-46d6-46f2-b5a0-24308be0bb41.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a491161e-46d6-46f2-b5a0-24308be0bb41.jpg?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=5Q71kud2b5SbZopa4Ag%2F4GY8uOY%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a00b66-4466-7b1e-9abd-ce222f9f75b3",
  "ambassadorName": "路线大使6-162725",
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/2ad5d1b2-13dd-43bf-a991-feed4d124053.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/2ad5d1b2-13dd-43bf-a991-feed4d124053.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=s07azabMv2C6ukfzb1Gy0ffgz6M%3D"
      },
      "introduction": "清晨沿江散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/1c9454c0-465f-4e5a-b3f4-1b7903dc6cd7.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/1c9454c0-465f-4e5a-b3f4-1b7903dc6cd7.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ppx9ZTCiOz1qRBZwElShgFk6cJ8%3D"
      },
      "introduction": "午后咖啡歇脚"
    }
  ],
  "createdAt": "2026-08-16T16:27:26.86897075Z",
  "updatedAt": "2026-08-16T16:27:26.86897075Z"
}
```

## Step 4: GET /api/admin/routes/{id} 详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/routes/01a00b66-4518-7ed5-ad91-944e5bc22fe9" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4518-7ed5-ad91-944e5bc22fe9",
  "cityId": "01a00b66-4435-7cd2-8ef8-0a8cc1ac8a10",
  "sortOrder": 2,
  "title": "江畔一日线-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/52fda58a-e6f5-49c5-9c51-6195404676d6.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/52fda58a-e6f5-49c5-9c51-6195404676d6.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=r0MZkIgYCuG7pyFvAhvDLOW6%2B7Q%3D"
  },
  "images": [
    {
      "id": "bound/d4885edf-aff8-4c04-b401-d9c2e9850dfd.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d4885edf-aff8-4c04-b401-d9c2e9850dfd.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=fDVm5bXywGjiJCuuD5%2BoOGzQwkc%3D"
    },
    {
      "id": "bound/a491161e-46d6-46f2-b5a0-24308be0bb41.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a491161e-46d6-46f2-b5a0-24308be0bb41.jpg?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Ym0N4KxY3ROxSCLUdkkheulCmpg%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a00b66-4466-7b1e-9abd-ce222f9f75b3",
  "ambassadorName": "路线大使6-162725",
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/2ad5d1b2-13dd-43bf-a991-feed4d124053.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/2ad5d1b2-13dd-43bf-a991-feed4d124053.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ImfwiJ%2FUZVqhvWSH9pXBv5G6EL4%3D"
      },
      "introduction": "清晨沿江散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/1c9454c0-465f-4e5a-b3f4-1b7903dc6cd7.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/1c9454c0-465f-4e5a-b3f4-1b7903dc6cd7.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=5gKOgYxqrQp7IodWCi1mLrx6aIo%3D"
      },
      "introduction": "午后咖啡歇脚"
    }
  ],
  "createdAt": "2026-08-16T16:27:26.868971Z",
  "updatedAt": "2026-08-16T16:27:26.868971Z"
}
```

