# TC-route-IT-007 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"路线城7-162725","englishName":"City16272525429","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4611-7701-a65a-f84f0e76f4ca",
  "chineseName": "路线城7-162725",
  "englishName": "City16272525429",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:27.121352999Z",
  "updatedAt": "2026-08-16T16:27:27.121352999Z"
}
```

## Step 2: 前置：创建上线大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/abb5e7de-2792-4b37-b832-2f4cd1e56bbd.png","name":"路线大使7-162725","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4635-7fe6-b492-97cf016dd30a",
  "avatar": {
    "id": "bound/abb5e7de-2792-4b37-b832-2f4cd1e56bbd.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/abb5e7de-2792-4b37-b832-2f4cd1e56bbd.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BwTeog5zIK8qtEFvPHj7isn8%2FE0%3D"
  },
  "name": "路线大使7-162725",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:27.15789164Z",
  "updatedAt": "2026-08-16T16:27:27.15789164Z"
}
```

## Step 3: POST /api/admin/routes 缺 title

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-4611-7701-a65a-f84f0e76f4ca","thumbnail":"images/f6114665-af34-4817-ac57-4ce8cd8efc56.png","images":["images/9386f8ff-21e6-44f5-a7a9-8b9f22335817.png"],"ambassadorId":"01a00b66-4635-7fe6-b492-97cf016dd30a"}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "路线标题不能为空",
  "path": "/api/admin/routes"
}
```

## Step 4: POST /api/admin/routes cityId 不存在

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"a391d366-030c-4eec-9591-ad4f033919fb","sortOrder":1,"title":"坏城路线-162725","ambassadorNote":"大使推荐语","thumbnail":"images/1d6adf46-43db-4854-8f5c-9163ac4a57a8.png","images":["images/b837b927-12fa-41a9-98fc-a483b14cab4c.png","images/f490736a-448c-4790-b794-73672f414810.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a00b66-4635-7fe6-b492-97cf016dd30a","spots":[{"name":"S1 江畔步道","image":"images/3afc7268-6513-4d35-85c7-d3d745bf7909.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "所属城市不存在：a391d366-030c-4eec-9591-ad4f033919fb",
  "path": "/api/admin/routes"
}
```

## Step 5: POST /api/admin/routes ambassadorId 不存在

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-4611-7701-a65a-f84f0e76f4ca","sortOrder":1,"title":"坏使路线-162725","ambassadorNote":"大使推荐语","thumbnail":"images/39bc8f3b-6418-4a43-b4d6-e2ac985d3168.png","images":["images/f90c58a7-2a62-49d7-bc46-43740dd283f8.png","images/27ec91ae-8478-4772-a3c4-7a9e097db8ba.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"a391d366-030c-4eec-9591-ad4f033919fb","spots":[{"name":"S1 江畔步道","image":"images/3afc7268-6513-4d35-85c7-d3d745bf7909.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "关联大使不存在：a391d366-030c-4eec-9591-ad4f033919fb",
  "path": "/api/admin/routes"
}
```

## Step 6: GET /api/admin/routes/page?cityId= 确认均未创建

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/routes/page?cityId=01a00b66-4611-7701-a65a-f84f0e76f4ca&page=0&size=50" -H "Authorization: Bearer $TOKEN"
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

