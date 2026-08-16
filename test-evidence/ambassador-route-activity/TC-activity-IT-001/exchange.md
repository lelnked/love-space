# TC-activity-IT-001 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"活动城1-162755","englishName":"City1627551674","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b44b-7697-b066-1b95ec619ee4",
  "chineseName": "活动城1-162755",
  "englishName": "City1627551674",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:55.339343245Z",
  "updatedAt": "2026-08-16T16:27:55.339343245Z"
}
```

## Step 2: POST /api/admin/activities 创建完整活动

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-b44b-7697-b066-1b95ec619ee4","images":["images/d2bdc19d-2648-4de2-b179-d09fbb0b064c.png","images/98112d8b-16be-40b8-876e-a2dc4831cb43.jpg"],"title":"海岛露营节-162755","tags":["露营","观星"],"periods":["FOLLICULAR","OVULATION"],"level":"L2","introduction":"海岛露营介绍","editorNote":"编辑寄语","gatheringPlace":"成都天府机场","dismissalPlace":"市区解散","transportation":"大巴接驳","visa":"无需签证","itinerary":[{"title":"Day1","content":"到成都天府机场集合"},{"title":"Day2","content":"露营与观星"}],"detailHtml":"<p>活动详情段落文本</p>","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b49e-7105-96e1-7aa33d054c6a",
  "cityId": "01a00b66-b44b-7697-b066-1b95ec619ee4",
  "images": [
    {
      "id": "bound/d2bdc19d-2648-4de2-b179-d09fbb0b064c.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d2bdc19d-2648-4de2-b179-d09fbb0b064c.png?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=eFXBpJHbPgoyH%2BT%2BQDh%2B47MRf5s%3D"
    },
    {
      "id": "bound/98112d8b-16be-40b8-876e-a2dc4831cb43.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/98112d8b-16be-40b8-876e-a2dc4831cb43.jpg?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=9m4VX8E2VhbC%2F6SIp0amsA13NSQ%3D"
    }
  ],
  "title": "海岛露营节-162755",
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
  "createdAt": "2026-08-16T16:27:55.416093881Z",
  "updatedAt": "2026-08-16T16:27:55.416093881Z"
}
```

## Step 3: GET /api/admin/activities/{id} 详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/activities/01a00b66-b49e-7105-96e1-7aa33d054c6a" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b49e-7105-96e1-7aa33d054c6a",
  "cityId": "01a00b66-b44b-7697-b066-1b95ec619ee4",
  "images": [
    {
      "id": "bound/d2bdc19d-2648-4de2-b179-d09fbb0b064c.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d2bdc19d-2648-4de2-b179-d09fbb0b064c.png?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=eFXBpJHbPgoyH%2BT%2BQDh%2B47MRf5s%3D"
    },
    {
      "id": "bound/98112d8b-16be-40b8-876e-a2dc4831cb43.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/98112d8b-16be-40b8-876e-a2dc4831cb43.jpg?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=9m4VX8E2VhbC%2F6SIp0amsA13NSQ%3D"
    }
  ],
  "title": "海岛露营节-162755",
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
  "createdAt": "2026-08-16T16:27:55.416094Z",
  "updatedAt": "2026-08-16T16:27:55.416094Z"
}
```

