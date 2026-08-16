# TC-route-IT-013 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"路线城13-162725","englishName":"City16272531260","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4ee4-734b-a0cd-5f4a1a239e6a",
  "chineseName": "路线城13-162725",
  "englishName": "City16272531260",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:29.380121084Z",
  "updatedAt": "2026-08-16T16:27:29.380121084Z"
}
```

## Step 2: 前置：创建上线大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/d93ef6db-d8bc-44e1-9c15-0b8fbef1ede8.png","name":"路线大使13-162725","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4f0e-77f0-9c5f-be11f0a44944",
  "avatar": {
    "id": "bound/d93ef6db-d8bc-44e1-9c15-0b8fbef1ede8.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d93ef6db-d8bc-44e1-9c15-0b8fbef1ede8.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=peZzVt1jTlTpoAA20ZVfUqmlRhg%3D"
  },
  "name": "路线大使13-162725",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:29.422421401Z",
  "updatedAt": "2026-08-16T16:27:29.422421401Z"
}
```

## Step 3: 前置：创建路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-4ee4-734b-a0cd-5f4a1a239e6a","sortOrder":1,"title":"隐身路线-162725","ambassadorNote":"大使推荐语","thumbnail":"images/b938bc0d-92ce-4bab-b4a1-b888bd0bb3ec.png","images":["images/d1b66bb7-3ab3-41e1-be07-e3e23527ce82.png","images/dc0b294a-2b37-4f86-9a2d-eafd70a4bfa8.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a00b66-4f0e-77f0-9c5f-be11f0a44944","spots":[{"name":"S1 江畔步道","image":"images/3afc7268-6513-4d35-85c7-d3d745bf7909.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4f49-7b47-9a4a-9fc1828a36dc",
  "cityId": "01a00b66-4ee4-734b-a0cd-5f4a1a239e6a",
  "sortOrder": 1,
  "title": "隐身路线-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/b938bc0d-92ce-4bab-b4a1-b888bd0bb3ec.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b938bc0d-92ce-4bab-b4a1-b888bd0bb3ec.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6ybkWpBpx08f7qJ5JME2HEDPWvg%3D"
  },
  "images": [
    {
      "id": "bound/d1b66bb7-3ab3-41e1-be07-e3e23527ce82.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d1b66bb7-3ab3-41e1-be07-e3e23527ce82.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=feXxeLehKK6NIrepSwXWZ%2BuFaMg%3D"
    },
    {
      "id": "bound/dc0b294a-2b37-4f86-9a2d-eafd70a4bfa8.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/dc0b294a-2b37-4f86-9a2d-eafd70a4bfa8.jpg?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=iNmHK82KYmTXL0aIxTlqKLZIbcE%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a00b66-4f0e-77f0-9c5f-be11f0a44944",
  "ambassadorName": "路线大使13-162725",
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Edj83bv9q5XLMs6PBvV56uAVvBk%3D"
      },
      "introduction": "清晨散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=sutl5Dp5AjG%2BYwRl%2FlIq%2Fbrrygo%3D"
      },
      "introduction": "午后咖啡"
    }
  ],
  "createdAt": "2026-08-16T16:27:29.481634787Z",
  "updatedAt": "2026-08-16T16:27:29.481634787Z"
}
```

## Step 4: GET /api/app/routes 下线前列表可见

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a00b66-4ee4-734b-a0cd-5f4a1a239e6a" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

[
  {
    "id": "01a00b66-4f49-7b47-9a4a-9fc1828a36dc",
    "title": "隐身路线-162725",
    "thumbnail": {
      "id": "bound/b938bc0d-92ce-4bab-b4a1-b888bd0bb3ec.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b938bc0d-92ce-4bab-b4a1-b888bd0bb3ec.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6ybkWpBpx08f7qJ5JME2HEDPWvg%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "路线大使13-162725"
  }
]
```

## Step 5: admin PUT /ambassadors/{id}/online 下线大使

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a00b66-4f0e-77f0-9c5f-be11f0a44944/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":false}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4f0e-77f0-9c5f-be11f0a44944",
  "avatar": {
    "id": "bound/d93ef6db-d8bc-44e1-9c15-0b8fbef1ede8.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d93ef6db-d8bc-44e1-9c15-0b8fbef1ede8.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=peZzVt1jTlTpoAA20ZVfUqmlRhg%3D"
  },
  "name": "路线大使13-162725",
  "tags": [
    "向导"
  ],
  "online": false,
  "createdAt": "2026-08-16T16:27:29.422421Z",
  "updatedAt": "2026-08-16T16:27:29.422421Z"
}
```

## Step 6: GET /api/app/routes 下线后列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a00b66-4ee4-734b-a0cd-5f4a1a239e6a" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

[]
```

## Step 7: GET /api/app/routes/{id} 下线后详情（应 404）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a00b66-4f49-7b47-9a4a-9fc1828a36dc" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 404）:

```
HTTP/1.1 404
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "route not found: 01a00b66-4f49-7b47-9a4a-9fc1828a36dc",
  "path": "/api/app/routes/01a00b66-4f49-7b47-9a4a-9fc1828a36dc"
}
```

