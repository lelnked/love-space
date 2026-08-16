# TC-route-IT-012 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"路线城12-162725","englishName":"City1627258839","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4b55-7949-80ab-e6ca6148bbda",
  "chineseName": "路线城12-162725",
  "englishName": "City1627258839",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:28.469505745Z",
  "updatedAt": "2026-08-16T16:27:28.469505745Z"
}
```

## Step 2: 前置：创建上线大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/6a4a8539-b837-4c9d-8561-974dbc5bf5da.png","name":"路线大使12-162725","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4b7c-7de1-9598-b0bb0a36ae2d",
  "avatar": {
    "id": "bound/6a4a8539-b837-4c9d-8561-974dbc5bf5da.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/6a4a8539-b837-4c9d-8561-974dbc5bf5da.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qeSM3lYlchW8udJEgijthvvIHyw%3D"
  },
  "name": "路线大使12-162725",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:28.508727966Z",
  "updatedAt": "2026-08-16T16:27:28.508727966Z"
}
```

## Step 3: 前置：路线 sortOrder=5

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-4b55-7949-80ab-e6ca6148bbda","sortOrder":5,"title":"序五-162725","ambassadorNote":"大使推荐语","thumbnail":"images/2945211e-b77b-4caa-9be1-b21cd897058e.png","images":["images/02982432-057f-4efb-9cf7-3f5533f7e597.png","images/e1e453f6-8992-41cb-822d-79bf3be50bc2.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a00b66-4b7c-7de1-9598-b0bb0a36ae2d","spots":[{"name":"S1 江畔步道","image":"images/3afc7268-6513-4d35-85c7-d3d745bf7909.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4bac-7ee8-926c-065736a3c7b9",
  "cityId": "01a00b66-4b55-7949-80ab-e6ca6148bbda",
  "sortOrder": 5,
  "title": "序五-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/2945211e-b77b-4caa-9be1-b21cd897058e.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/2945211e-b77b-4caa-9be1-b21cd897058e.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2Bx5uJ5e1b2fPQafbKHERGNnRnDc%3D"
  },
  "images": [
    {
      "id": "bound/02982432-057f-4efb-9cf7-3f5533f7e597.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/02982432-057f-4efb-9cf7-3f5533f7e597.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=bdXecwqG5yI%2FyeDku%2Fo9%2B57fS8U%3D"
    },
    {
      "id": "bound/e1e453f6-8992-41cb-822d-79bf3be50bc2.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/e1e453f6-8992-41cb-822d-79bf3be50bc2.jpg?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=yRteewfcSheamwxZItwhqg2hXjk%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a00b66-4b7c-7de1-9598-b0bb0a36ae2d",
  "ambassadorName": "路线大使12-162725",
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
  "createdAt": "2026-08-16T16:27:28.55685344Z",
  "updatedAt": "2026-08-16T16:27:28.55685344Z"
}
```

## Step 4: 前置：路线 sortOrder=1

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-4b55-7949-80ab-e6ca6148bbda","sortOrder":1,"title":"序一-162725","ambassadorNote":"大使推荐语","thumbnail":"images/054ff0b0-f16a-4e3a-b604-f20e5dc72e31.png","images":["images/dfea0b5b-f347-4ffa-b125-9fb04bcd5d7c.png","images/901529cc-d225-4e15-8f90-38aeabe60f66.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a00b66-4b7c-7de1-9598-b0bb0a36ae2d","spots":[{"name":"S1 江畔步道","image":"images/3afc7268-6513-4d35-85c7-d3d745bf7909.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4bdd-711a-a63d-ee54d9808fbf",
  "cityId": "01a00b66-4b55-7949-80ab-e6ca6148bbda",
  "sortOrder": 1,
  "title": "序一-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/054ff0b0-f16a-4e3a-b604-f20e5dc72e31.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/054ff0b0-f16a-4e3a-b604-f20e5dc72e31.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=UuJEqhaDNK0wI%2FgmGYFahIwys8k%3D"
  },
  "images": [
    {
      "id": "bound/dfea0b5b-f347-4ffa-b125-9fb04bcd5d7c.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/dfea0b5b-f347-4ffa-b125-9fb04bcd5d7c.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=GXSeSktMEaohvQOFar01RYp4dRs%3D"
    },
    {
      "id": "bound/901529cc-d225-4e15-8f90-38aeabe60f66.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/901529cc-d225-4e15-8f90-38aeabe60f66.jpg?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=JKN7cG5d1T%2FzlWVd%2BY2raZ3WowU%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a00b66-4b7c-7de1-9598-b0bb0a36ae2d",
  "ambassadorName": "路线大使12-162725",
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
  "createdAt": "2026-08-16T16:27:28.604998624Z",
  "updatedAt": "2026-08-16T16:27:28.604998624Z"
}
```

## Step 5: 前置：路线 sortOrder=3

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-4b55-7949-80ab-e6ca6148bbda","sortOrder":3,"title":"序三-162725","ambassadorNote":"大使推荐语","thumbnail":"images/57123d31-e147-42b8-8164-65d4e24e7c69.png","images":["images/44c5c036-5486-4741-bd42-5193e231f30e.png","images/439c9990-c9ee-4e49-88d7-a19d4a1a260c.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a00b66-4b7c-7de1-9598-b0bb0a36ae2d","spots":[{"name":"S1 江畔步道","image":"images/3afc7268-6513-4d35-85c7-d3d745bf7909.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4c0d-78a9-852d-8cb2c421afa8",
  "cityId": "01a00b66-4b55-7949-80ab-e6ca6148bbda",
  "sortOrder": 3,
  "title": "序三-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/57123d31-e147-42b8-8164-65d4e24e7c69.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/57123d31-e147-42b8-8164-65d4e24e7c69.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=HN5jfCpOXnmHg9vpqRdtZcgj1%2Bc%3D"
  },
  "images": [
    {
      "id": "bound/44c5c036-5486-4741-bd42-5193e231f30e.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/44c5c036-5486-4741-bd42-5193e231f30e.png?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=D%2FAX8o6gMEJcHd34tNzhioNxLz8%3D"
    },
    {
      "id": "bound/439c9990-c9ee-4e49-88d7-a19d4a1a260c.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/439c9990-c9ee-4e49-88d7-a19d4a1a260c.jpg?Expires=1786899448&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=FLEaID44myt5ulwqxeLVZg7cVFI%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a00b66-4b7c-7de1-9598-b0bb0a36ae2d",
  "ambassadorName": "路线大使12-162725",
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
  "createdAt": "2026-08-16T16:27:28.653458727Z",
  "updatedAt": "2026-08-16T16:27:28.653458727Z"
}
```

## Step 6: GET /api/app/routes?cityId= app 端路线列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a00b66-4b55-7949-80ab-e6ca6148bbda" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

[
  {
    "id": "01a00b66-4bdd-711a-a63d-ee54d9808fbf",
    "title": "序一-162725",
    "thumbnail": {
      "id": "bound/054ff0b0-f16a-4e3a-b604-f20e5dc72e31.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/054ff0b0-f16a-4e3a-b604-f20e5dc72e31.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=3mvUQ7VZK7ZRzO%2F30USTHsSEvS8%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "路线大使12-162725"
  },
  {
    "id": "01a00b66-4c0d-78a9-852d-8cb2c421afa8",
    "title": "序三-162725",
    "thumbnail": {
      "id": "bound/57123d31-e147-42b8-8164-65d4e24e7c69.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/57123d31-e147-42b8-8164-65d4e24e7c69.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=gCL7YYA9eckVtmcgho9eCASrPzo%3D"
    },
    "sortOrder": 3,
    "ambassadorName": "路线大使12-162725"
  },
  {
    "id": "01a00b66-4bac-7ee8-926c-065736a3c7b9",
    "title": "序五-162725",
    "thumbnail": {
      "id": "bound/2945211e-b77b-4caa-9be1-b21cd897058e.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/2945211e-b77b-4caa-9be1-b21cd897058e.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=q6Ueh44of4SCcP5ae7r5%2F%2Ff0gfw%3D"
    },
    "sortOrder": 5,
    "ambassadorName": "路线大使12-162725"
  }
]
```

