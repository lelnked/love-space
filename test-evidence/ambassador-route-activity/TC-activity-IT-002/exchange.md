# TC-activity-IT-002 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"活动城2-162755","englishName":"City16275511496","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b534-7f1a-b139-5a2c08aa030c",
  "chineseName": "活动城2-162755",
  "englishName": "City16275511496",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:55.572870807Z",
  "updatedAt": "2026-08-16T16:27:55.572870807Z"
}
```

## Step 2: POST /api/admin/activities 缺 title

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-b534-7f1a-b139-5a2c08aa030c","images":["images/c450cceb-5f91-4ec6-ae13-d18f79f3202a.png"]}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "活动标题不能为空",
  "path": "/api/admin/activities"
}
```

## Step 3: POST /api/admin/activities images 为空数组

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-b534-7f1a-b139-5a2c08aa030c","images":[],"title":"空图活动"}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "活动图片至少 1 张",
  "path": "/api/admin/activities"
}
```

## Step 4: POST /api/admin/activities cityId 不存在

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"5e3be1e0-ed67-4182-944c-091051651ef9","images":["images/190b507c-8e90-4c25-a8b4-ae30127b5d3a.png","images/4ce6036d-954a-44c4-b730-ee5ca83eb8e8.jpg"],"title":"坏城活动-162755","tags":["露营","观星"],"periods":["FOLLICULAR","OVULATION"],"level":"L2","introduction":"海岛露营介绍","editorNote":"编辑寄语","gatheringPlace":"成都天府机场","dismissalPlace":"市区解散","transportation":"大巴接驳","visa":"无需签证","itinerary":[{"title":"Day1","content":"到成都天府机场集合"},{"title":"Day2","content":"露营与观星"}],"detailHtml":"<p>活动详情段落文本</p>","online":true}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "所属城市不存在：5e3be1e0-ed67-4182-944c-091051651ef9",
  "path": "/api/admin/activities"
}
```

## Step 5: GET /api/admin/activities/page?cityId= 确认均未创建

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/activities/page?cityId=01a00b66-b534-7f1a-b139-5a2c08aa030c&page=0&size=50" -H "Authorization: Bearer $TOKEN"
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

