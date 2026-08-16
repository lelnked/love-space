# TC-route-IT-011 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"路线城11-162725","englishName":"City16272531189","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4a6a-7609-b01a-0744d998c8b5",
  "chineseName": "路线城11-162725",
  "englishName": "City16272531189",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:28.234280799Z",
  "updatedAt": "2026-08-16T16:27:28.234280799Z"
}
```

## Step 2: 前置：创建上线大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/259f8bf8-a2be-4e15-9ad9-ac11553d994a.png","name":"路线大使11-162725","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4a91-79f3-8e7f-d70e89fc17e1",
  "avatar": {
    "id": "bound/259f8bf8-a2be-4e15-9ad9-ac11553d994a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/259f8bf8-a2be-4e15-9ad9-ac11553d994a.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=gPGX%2FgH7X6FMhGC8AV%2B80HxMcLQ%3D"
  },
  "name": "路线大使11-162725",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:28.273538207Z",
  "updatedAt": "2026-08-16T16:27:28.273538207Z"
}
```

## Step 3: 前置：创建含 2 地点的路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-4a6a-7609-b01a-0744d998c8b5","sortOrder":1,"title":"待删路线-162725","ambassadorNote":"大使推荐语","thumbnail":"images/c35030f8-646e-4931-8f8d-b385ad7bb8da.png","images":["images/20114cbf-f0ef-4a71-9028-61a2cfde81ee.png","images/d9dd0c6c-2442-4072-a453-0f202d59eef0.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a00b66-4a91-79f3-8e7f-d70e89fc17e1","spots":[{"name":"S1 江畔步道","image":"images/3afc7268-6513-4d35-85c7-d3d745bf7909.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4abf-74b3-a681-b412b51c2fd8",
  "cityId": "01a00b66-4a6a-7609-b01a-0744d998c8b5",
  "sortOrder": 1,
  "title": "待删路线-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/c35030f8-646e-4931-8f8d-b385ad7bb8da.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c35030f8-646e-4931-8f8d-b385ad7bb8da.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=peyQwHr0Km3IpxWuAKs7B6WRYTs%3D"
  },
  "images": [
    {
      "id": "bound/20114cbf-f0ef-4a71-9028-61a2cfde81ee.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/20114cbf-f0ef-4a71-9028-61a2cfde81ee.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=YOVl5xGSNs7O4wzueXk46MuboPo%3D"
    },
    {
      "id": "bound/d9dd0c6c-2442-4072-a453-0f202d59eef0.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d9dd0c6c-2442-4072-a453-0f202d59eef0.jpg?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=x7KBZyYRcMzb%2FulU1Fnwg0k8%2Fdo%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a00b66-4a91-79f3-8e7f-d70e89fc17e1",
  "ambassadorName": "路线大使11-162725",
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
  "createdAt": "2026-08-16T16:27:28.319192589Z",
  "updatedAt": "2026-08-16T16:27:28.319192589Z"
}
```

## Step 4: DELETE /api/admin/routes/{id}

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/routes/01a00b66-4abf-74b3-a681-b412b51c2fd8" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200



```

## Step 5: GET 已删除路线详情（应 400 中文口径）

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/routes/01a00b66-4abf-74b3-a681-b412b51c2fd8" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "路线不存在：01a00b66-4abf-74b3-a681-b412b51c2fd8",
  "path": "/api/admin/routes/01a00b66-4abf-74b3-a681-b412b51c2fd8"
}
```

## Step 6: GET /api/admin/routes/page?cityId= 确认列表不含该路线

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/routes/page?cityId=01a00b66-4a6a-7609-b01a-0744d998c8b5&page=0&size=50" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [],
  "page": 1,
  "size": 30,
  "totalElements": 0,
  "totalPages": 0
}
```

