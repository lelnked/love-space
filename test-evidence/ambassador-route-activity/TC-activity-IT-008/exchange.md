# TC-activity-IT-008 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"活动城8-162755","englishName":"City1627551404","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b9b8-70f0-9b88-ff61e31b38d5",
  "chineseName": "活动城8-162755",
  "englishName": "City1627551404",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:56.727983746Z",
  "updatedAt": "2026-08-16T16:27:56.727983746Z"
}
```

## Step 2: 前置：创建上线活动

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-b9b8-70f0-9b88-ff61e31b38d5","images":["images/d774ed9e-8117-4cd8-af54-728572931186.png","images/d8bdf46d-e2d4-48e1-a7f4-8281adfcceba.jpg"],"title":"隐身活动-162755","tags":["露营","观星"],"periods":["FOLLICULAR","OVULATION"],"level":"L2","introduction":"海岛露营介绍","editorNote":"编辑寄语","gatheringPlace":"成都天府机场","dismissalPlace":"市区解散","transportation":"大巴接驳","visa":"无需签证","itinerary":[{"title":"Day1","content":"到成都天府机场集合"},{"title":"Day2","content":"露营与观星"}],"detailHtml":"<p>活动详情段落文本</p>","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b9e4-75b6-97dc-31bdc372126a",
  "cityId": "01a00b66-b9b8-70f0-9b88-ff61e31b38d5",
  "images": [
    {
      "id": "bound/d774ed9e-8117-4cd8-af54-728572931186.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d774ed9e-8117-4cd8-af54-728572931186.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cGdQEwdhbFxdSOnWmrtvvQbPEIE%3D"
    },
    {
      "id": "bound/d8bdf46d-e2d4-48e1-a7f4-8281adfcceba.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d8bdf46d-e2d4-48e1-a7f4-8281adfcceba.jpg?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=eeEMriEWBXtrrp1OiqKoj3CjqUY%3D"
    }
  ],
  "title": "隐身活动-162755",
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
  "createdAt": "2026-08-16T16:27:56.77226554Z",
  "updatedAt": "2026-08-16T16:27:56.77226554Z"
}
```

## Step 3: GET /api/app/activities 下线前可见

```bash
curl -s -i -X GET "http://localhost:8081/api/app/activities?cityId=01a00b66-b9b8-70f0-9b88-ff61e31b38d5" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

[
  {
    "id": "01a00b66-b9e4-75b6-97dc-31bdc372126a",
    "title": "隐身活动-162755",
    "images": [
      {
        "id": "bound/d774ed9e-8117-4cd8-af54-728572931186.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d774ed9e-8117-4cd8-af54-728572931186.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cGdQEwdhbFxdSOnWmrtvvQbPEIE%3D"
      },
      {
        "id": "bound/d8bdf46d-e2d4-48e1-a7f4-8281adfcceba.jpg",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d8bdf46d-e2d4-48e1-a7f4-8281adfcceba.jpg?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=eeEMriEWBXtrrp1OiqKoj3CjqUY%3D"
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

## Step 4: admin PUT /activities/{id}/online 下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a00b66-b9e4-75b6-97dc-31bdc372126a/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":false}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b9e4-75b6-97dc-31bdc372126a",
  "cityId": "01a00b66-b9b8-70f0-9b88-ff61e31b38d5",
  "images": [
    {
      "id": "bound/d774ed9e-8117-4cd8-af54-728572931186.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d774ed9e-8117-4cd8-af54-728572931186.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cGdQEwdhbFxdSOnWmrtvvQbPEIE%3D"
    },
    {
      "id": "bound/d8bdf46d-e2d4-48e1-a7f4-8281adfcceba.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d8bdf46d-e2d4-48e1-a7f4-8281adfcceba.jpg?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=eeEMriEWBXtrrp1OiqKoj3CjqUY%3D"
    }
  ],
  "title": "隐身活动-162755",
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
  "online": false,
  "createdAt": "2026-08-16T16:27:56.772266Z",
  "updatedAt": "2026-08-16T16:27:56.772266Z"
}
```

## Step 5: GET /api/app/activities 下线后列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/activities?cityId=01a00b66-b9b8-70f0-9b88-ff61e31b38d5" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

[]
```

## Step 6: GET /api/app/activities/{id} 下线后详情（应 404）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/activities/01a00b66-b9e4-75b6-97dc-31bdc372126a" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 404）:

```
HTTP/1.1 404
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "activity not found: 01a00b66-b9e4-75b6-97dc-31bdc372126a",
  "path": "/api/app/activities/01a00b66-b9e4-75b6-97dc-31bdc372126a"
}
```

