# TC-route-IT-010 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"路线城10-162725","englishName":"City16272529904","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4923-77a9-a58f-cff00b2ff57f",
  "chineseName": "路线城10-162725",
  "englishName": "City16272529904",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:27.90739834Z",
  "updatedAt": "2026-08-16T16:27:27.90739834Z"
}
```

## Step 2: 前置：创建上线大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/3fc9e9cc-073f-487b-8921-f02054629fbd.png","name":"路线大使10-162725","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4946-7873-aebb-9e897fb355ff",
  "avatar": {
    "id": "bound/3fc9e9cc-073f-487b-8921-f02054629fbd.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3fc9e9cc-073f-487b-8921-f02054629fbd.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=O4qvyR5R3yS%2BLzB%2F20gFcS9wnNA%3D"
  },
  "name": "路线大使10-162725",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:27.942451674Z",
  "updatedAt": "2026-08-16T16:27:27.942451674Z"
}
```

## Step 3: 前置：路线 sortOrder=5

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-4923-77a9-a58f-cff00b2ff57f","sortOrder":5,"title":"排序五-162725","ambassadorNote":"大使推荐语","thumbnail":"images/02a642b3-5651-4ba7-b710-2dcb0d93af52.png","images":["images/878c66ac-bea8-44e6-abac-59958870501b.png","images/8ffc7eb1-566e-442e-a165-989271599f86.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a00b66-4946-7873-aebb-9e897fb355ff","spots":[{"name":"S1 江畔步道","image":"images/3afc7268-6513-4d35-85c7-d3d745bf7909.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4972-7ce8-9026-3bb38ea2d277",
  "cityId": "01a00b66-4923-77a9-a58f-cff00b2ff57f",
  "sortOrder": 5,
  "title": "排序五-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/02a642b3-5651-4ba7-b710-2dcb0d93af52.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/02a642b3-5651-4ba7-b710-2dcb0d93af52.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Z56LxnDUjhGIXFfek6wXe%2BiCJ5c%3D"
  },
  "images": [
    {
      "id": "bound/878c66ac-bea8-44e6-abac-59958870501b.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/878c66ac-bea8-44e6-abac-59958870501b.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=4ytoNlyLnmknOjksdRfEAEPCGpY%3D"
    },
    {
      "id": "bound/8ffc7eb1-566e-442e-a165-989271599f86.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/8ffc7eb1-566e-442e-a165-989271599f86.jpg?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=M%2BMn9HDdXnG6NkPMO69mQj7EQL8%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a00b66-4946-7873-aebb-9e897fb355ff",
  "ambassadorName": "路线大使10-162725",
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=aRTaQsNGr68qOR%2Fa%2Bb6GmO%2BZPds%3D"
      },
      "introduction": "清晨散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6t7O36v87Qre5Izrwv9gRbZXz3I%3D"
      },
      "introduction": "午后咖啡"
    }
  ],
  "createdAt": "2026-08-16T16:27:27.985972251Z",
  "updatedAt": "2026-08-16T16:27:27.985972251Z"
}
```

## Step 4: 前置：路线 sortOrder=1（标题含「江畔」）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-4923-77a9-a58f-cff00b2ff57f","sortOrder":1,"title":"江畔慢行-162725","ambassadorNote":"大使推荐语","thumbnail":"images/9dc268e9-7a17-4b3d-b1e4-e0fb7cc501d8.png","images":["images/4427c718-6c79-4476-8c15-47dd837fdc73.png","images/694e7df5-646b-4092-ac2e-8c12f9cee5a1.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a00b66-4946-7873-aebb-9e897fb355ff","spots":[{"name":"S1 江畔步道","image":"images/3afc7268-6513-4d35-85c7-d3d745bf7909.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-499c-7af2-a3b7-6b4d9a815072",
  "cityId": "01a00b66-4923-77a9-a58f-cff00b2ff57f",
  "sortOrder": 1,
  "title": "江畔慢行-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/9dc268e9-7a17-4b3d-b1e4-e0fb7cc501d8.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/9dc268e9-7a17-4b3d-b1e4-e0fb7cc501d8.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=4AZpawUELIXmDA7bARUh9XZHZXI%3D"
  },
  "images": [
    {
      "id": "bound/4427c718-6c79-4476-8c15-47dd837fdc73.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/4427c718-6c79-4476-8c15-47dd837fdc73.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=z%2FosN%2BRIsLgUcBy3WTfuY7Omh8s%3D"
    },
    {
      "id": "bound/694e7df5-646b-4092-ac2e-8c12f9cee5a1.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/694e7df5-646b-4092-ac2e-8c12f9cee5a1.jpg?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=wXzmnX%2B4W%2BdQUi6pyumALPK%2BtLs%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a00b66-4946-7873-aebb-9e897fb355ff",
  "ambassadorName": "路线大使10-162725",
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tRrjbKvFX8p6S6Fx8my5zhCsT5s%3D"
      },
      "introduction": "清晨散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=4AX4nXbJdUxIDZsoyNhJHEAbHIo%3D"
      },
      "introduction": "午后咖啡"
    }
  ],
  "createdAt": "2026-08-16T16:27:28.028610009Z",
  "updatedAt": "2026-08-16T16:27:28.028610009Z"
}
```

## Step 5: 前置：路线 sortOrder=3

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-4923-77a9-a58f-cff00b2ff57f","sortOrder":3,"title":"排序三-162725","ambassadorNote":"大使推荐语","thumbnail":"images/ee2b10dc-7953-4cf1-8f7d-f51f68c7fab4.png","images":["images/b82a68cb-c31b-4123-92c6-5962d3dca3bf.png","images/3564c85f-4280-4857-818c-bdddff6e8628.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a00b66-4946-7873-aebb-9e897fb355ff","spots":[{"name":"S1 江畔步道","image":"images/3afc7268-6513-4d35-85c7-d3d745bf7909.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-49ca-713f-946b-98d91821882a",
  "cityId": "01a00b66-4923-77a9-a58f-cff00b2ff57f",
  "sortOrder": 3,
  "title": "排序三-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/ee2b10dc-7953-4cf1-8f7d-f51f68c7fab4.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/ee2b10dc-7953-4cf1-8f7d-f51f68c7fab4.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tTLIhn8cx0Til%2B0KQdYTktfvVpU%3D"
  },
  "images": [
    {
      "id": "bound/b82a68cb-c31b-4123-92c6-5962d3dca3bf.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b82a68cb-c31b-4123-92c6-5962d3dca3bf.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=akDkK48Nr%2BhM2XIhLeXWF0597YQ%3D"
    },
    {
      "id": "bound/3564c85f-4280-4857-818c-bdddff6e8628.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3564c85f-4280-4857-818c-bdddff6e8628.jpg?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RDmqfL4E2XiwBoao8ON7wJTjidc%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a00b66-4946-7873-aebb-9e897fb355ff",
  "ambassadorName": "路线大使10-162725",
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tRrjbKvFX8p6S6Fx8my5zhCsT5s%3D"
      },
      "introduction": "清晨散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=4AX4nXbJdUxIDZsoyNhJHEAbHIo%3D"
      },
      "introduction": "午后咖啡"
    }
  ],
  "createdAt": "2026-08-16T16:27:28.07400577Z",
  "updatedAt": "2026-08-16T16:27:28.07400577Z"
}
```

## Step 6: GET /api/admin/routes/page?cityId= 全量

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/routes/page?cityId=01a00b66-4923-77a9-a58f-cff00b2ff57f&page=0&size=10" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [
    {
      "id": "01a00b66-499c-7af2-a3b7-6b4d9a815072",
      "cityId": "01a00b66-4923-77a9-a58f-cff00b2ff57f",
      "sortOrder": 1,
      "title": "江畔慢行-162725",
      "thumbnail": {
        "id": "bound/9dc268e9-7a17-4b3d-b1e4-e0fb7cc501d8.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/9dc268e9-7a17-4b3d-b1e4-e0fb7cc501d8.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=4AZpawUELIXmDA7bARUh9XZHZXI%3D"
      },
      "ambassadorId": "01a00b66-4946-7873-aebb-9e897fb355ff",
      "ambassadorName": "路线大使10-162725",
      "spotCount": 2,
      "createdAt": "2026-08-16T16:27:28.02861Z",
      "updatedAt": "2026-08-16T16:27:28.02861Z"
    },
    {
      "id": "01a00b66-49ca-713f-946b-98d91821882a",
      "cityId": "01a00b66-4923-77a9-a58f-cff00b2ff57f",
      "sortOrder": 3,
      "title": "排序三-162725",
      "thumbnail": {
        "id": "bound/ee2b10dc-7953-4cf1-8f7d-f51f68c7fab4.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/ee2b10dc-7953-4cf1-8f7d-f51f68c7fab4.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tTLIhn8cx0Til%2B0KQdYTktfvVpU%3D"
      },
      "ambassadorId": "01a00b66-4946-7873-aebb-9e897fb355ff",
      "ambassadorName": "路线大使10-162725",
      "spotCount": 2,
      "createdAt": "2026-08-16T16:27:28.074006Z",
      "updatedAt": "2026-08-16T16:27:28.074006Z"
    },
    {
      "id": "01a00b66-4972-7ce8-9026-3bb38ea2d277",
      "cityId": "01a00b66-4923-77a9-a58f-cff00b2ff57f",
      "sortOrder": 5,
      "title": "排序五-162725",
      "thumbnail": {
        "id": "bound/02a642b3-5651-4ba7-b710-2dcb0d93af52.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/02a642b3-5651-4ba7-b710-2dcb0d93af52.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=CyUZP9J0A3GQN2Q0LeRkvz%2BAosM%3D"
      },
      "ambassadorId": "01a00b66-4946-7873-aebb-9e897fb355ff",
      "ambassadorName": "路线大使10-162725",
      "spotCount": 2,
      "createdAt": "2026-08-16T16:27:27.985972Z",
      "updatedAt": "2026-08-16T16:27:27.985972Z"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 3,
  "totalPages": 1
}
```

## Step 7: GET /api/admin/routes/page?cityId=&keyword=江畔

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/routes/page?cityId=01a00b66-4923-77a9-a58f-cff00b2ff57f&keyword=%E6%B1%9F%E7%95%94&page=0&size=10" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [
    {
      "id": "01a00b66-499c-7af2-a3b7-6b4d9a815072",
      "cityId": "01a00b66-4923-77a9-a58f-cff00b2ff57f",
      "sortOrder": 1,
      "title": "江畔慢行-162725",
      "thumbnail": {
        "id": "bound/9dc268e9-7a17-4b3d-b1e4-e0fb7cc501d8.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/9dc268e9-7a17-4b3d-b1e4-e0fb7cc501d8.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=4AZpawUELIXmDA7bARUh9XZHZXI%3D"
      },
      "ambassadorId": "01a00b66-4946-7873-aebb-9e897fb355ff",
      "ambassadorName": "路线大使10-162725",
      "spotCount": 2,
      "createdAt": "2026-08-16T16:27:28.02861Z",
      "updatedAt": "2026-08-16T16:27:28.02861Z"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

