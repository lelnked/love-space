# TC-city-IT-006 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市 C

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"级联城6-162755","englishName":"City16275527070","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-bb20-7e59-97e3-2e3e9a055ebf",
  "chineseName": "级联城6-162755",
  "englishName": "City16275527070",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:57.088822224Z",
  "updatedAt": "2026-08-16T16:27:57.088822224Z"
}
```

## Step 2: 前置：创建上线大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/31ed7ddd-df20-4f94-b961-c7dac8209574.png","name":"级联大使-162755","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-bb43-7b25-859e-616b82cb4e6f",
  "avatar": {
    "id": "bound/31ed7ddd-df20-4f94-b961-c7dac8209574.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/31ed7ddd-df20-4f94-b961-c7dac8209574.png?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lttxuaw3A89CNqVxHpPNz6UvnZU%3D"
  },
  "name": "级联大使-162755",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:57.123629985Z",
  "updatedAt": "2026-08-16T16:27:57.123629985Z"
}
```

## Step 3: 前置：创建路线（大使上线）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-bb20-7e59-97e3-2e3e9a055ebf","sortOrder":1,"title":"级联路线","thumbnail":"images/8b23da6f-63c7-4260-87e1-3a549b3216f4.png","images":["images/38f13c69-fe1a-4a61-90aa-c20b01995d2f.png"],"ambassadorId":"01a00b66-bb43-7b25-859e-616b82cb4e6f","spots":[{"name":"S1","image":"images/5ddee749-8820-438d-9c75-cb7abe00858d.png","introduction":"i1"},{"name":"S2","image":"images/58506e77-cbe2-4ac2-9cbf-b5b3ffb1c504.png","introduction":"i2"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-bb72-7d98-b5f2-57752fad80f0",
  "cityId": "01a00b66-bb20-7e59-97e3-2e3e9a055ebf",
  "sortOrder": 1,
  "title": "级联路线",
  "ambassadorNote": null,
  "thumbnail": {
    "id": "bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=UjWlA2iLtVBlufvawIPCKmTMICY%3D"
  },
  "images": [
    {
      "id": "bound/38f13c69-fe1a-4a61-90aa-c20b01995d2f.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/38f13c69-fe1a-4a61-90aa-c20b01995d2f.png?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Lv9S4FFHtIwIFEDzbJ4Jh%2FMOhtI%3D"
    }
  ],
  "travelTime": null,
  "season": null,
  "travelStatus": null,
  "ambassadorId": "01a00b66-bb43-7b25-859e-616b82cb4e6f",
  "ambassadorName": "级联大使-162755",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/5ddee749-8820-438d-9c75-cb7abe00858d.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/5ddee749-8820-438d-9c75-cb7abe00858d.png?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qv2ynYNc0WlQ3qe7A5W2lXX3iTo%3D"
      },
      "introduction": "i1"
    },
    {
      "name": "S2",
      "image": {
        "id": "bound/58506e77-cbe2-4ac2-9cbf-b5b3ffb1c504.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/58506e77-cbe2-4ac2-9cbf-b5b3ffb1c504.png?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=h1b7XDBRts8JxfQDn7y1xW8dr8U%3D"
      },
      "introduction": "i2"
    }
  ],
  "createdAt": "2026-08-16T16:27:57.170772288Z",
  "updatedAt": "2026-08-16T16:27:57.170772288Z"
}
```

## Step 4: 前置：创建上线活动

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-bb20-7e59-97e3-2e3e9a055ebf","images":["images/0aab47a1-44ee-4ba7-95dc-279403be840b.png","images/43ecd414-86c0-4899-b6f5-641a6ba25358.jpg"],"title":"级联活动-162755","tags":["露营","观星"],"periods":["FOLLICULAR","OVULATION"],"level":"L2","introduction":"海岛露营介绍","editorNote":"编辑寄语","gatheringPlace":"成都天府机场","dismissalPlace":"市区解散","transportation":"大巴接驳","visa":"无需签证","itinerary":[{"title":"Day1","content":"到成都天府机场集合"},{"title":"Day2","content":"露营与观星"}],"detailHtml":"<p>活动详情段落文本</p>","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-bb9e-7181-a4d7-2448ec1fb8dd",
  "cityId": "01a00b66-bb20-7e59-97e3-2e3e9a055ebf",
  "images": [
    {
      "id": "bound/0aab47a1-44ee-4ba7-95dc-279403be840b.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0aab47a1-44ee-4ba7-95dc-279403be840b.png?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=AnK9gEyAuo5%2FnbqtTUdTYvkAwy8%3D"
    },
    {
      "id": "bound/43ecd414-86c0-4899-b6f5-641a6ba25358.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/43ecd414-86c0-4899-b6f5-641a6ba25358.jpg?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZmYAwhnFV1AqGIite9VZb9sDBF0%3D"
    }
  ],
  "title": "级联活动-162755",
  "tags": [
    "露营",
    "观星"
  ],
  "periods": [
    "FOLLICULAR",
    "OVULATION"
  ],
  "level": "L2",
  "introduction": "海岛露营介绍",
  "editorNote": "编辑寄语",
  "gatheringPlace": "成都天府机场",
  "dismissalPlace": "市区解散",
  "transportation": "大巴接驳",
  "visa": "无需签证",
  "itinerary": [
    {
      "title": "Day1",
      "content": "到成都天府机场集合"
    },
    {
      "title": "Day2",
      "content": "露营与观星"
    }
  ],
  "detailHtml": "<p>活动详情段落文本</p>",
  "online": true,
  "createdAt": "2026-08-16T16:27:57.214023915Z",
  "updatedAt": "2026-08-16T16:27:57.214023915Z"
}
```

## Step 5: GET /api/app/routes 下架前路线可见

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a00b66-bb20-7e59-97e3-2e3e9a055ebf" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

[
  {
    "id": "01a00b66-bb72-7d98-b5f2-57752fad80f0",
    "title": "级联路线",
    "thumbnail": {
      "id": "bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=UjWlA2iLtVBlufvawIPCKmTMICY%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "级联大使-162755"
  }
]
```

## Step 6: GET /api/app/activities 下架前活动可见

```bash
curl -s -i -X GET "http://localhost:8081/api/app/activities?cityId=01a00b66-bb20-7e59-97e3-2e3e9a055ebf" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

[
  {
    "id": "01a00b66-bb9e-7181-a4d7-2448ec1fb8dd",
    "title": "级联活动-162755",
    "images": [
      {
        "id": "bound/0aab47a1-44ee-4ba7-95dc-279403be840b.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0aab47a1-44ee-4ba7-95dc-279403be840b.png?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=AnK9gEyAuo5%2FnbqtTUdTYvkAwy8%3D"
      },
      {
        "id": "bound/43ecd414-86c0-4899-b6f5-641a6ba25358.jpg",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/43ecd414-86c0-4899-b6f5-641a6ba25358.jpg?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZmYAwhnFV1AqGIite9VZb9sDBF0%3D"
      }
    ],
    "tags": [
      "露营",
      "观星"
    ],
    "periods": [
      "FOLLICULAR",
      "OVULATION"
    ],
    "level": "L2",
    "introduction": "海岛露营介绍"
  }
]
```

## Step 7: admin PUT /cities/{id}/online 下架城市

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a00b66-bb20-7e59-97e3-2e3e9a055ebf/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":false}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-bb20-7e59-97e3-2e3e9a055ebf",
  "chineseName": "级联城6-162755",
  "englishName": "City16275527070",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-16T16:27:57.088822Z",
  "updatedAt": "2026-08-16T16:27:57.088822Z"
}
```

## Step 8: GET /api/app/routes 下架后路线列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a00b66-bb20-7e59-97e3-2e3e9a055ebf" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

[]
```

## Step 9: GET /api/app/routes/{id} 下架后路线详情（应 404）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a00b66-bb72-7d98-b5f2-57752fad80f0" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 404）:

```
HTTP/1.1 404
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "route not found: 01a00b66-bb72-7d98-b5f2-57752fad80f0",
  "path": "/api/app/routes/01a00b66-bb72-7d98-b5f2-57752fad80f0"
}
```

## Step 10: GET /api/app/activities 下架后活动列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/activities?cityId=01a00b66-bb20-7e59-97e3-2e3e9a055ebf" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

[]
```

## Step 11: GET /api/app/activities/{id} 下架后活动详情（应 404）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/activities/01a00b66-bb9e-7181-a4d7-2448ec1fb8dd" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 404）:

```
HTTP/1.1 404
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "activity not found: 01a00b66-bb9e-7181-a4d7-2448ec1fb8dd",
  "path": "/api/app/activities/01a00b66-bb9e-7181-a4d7-2448ec1fb8dd"
}
```

