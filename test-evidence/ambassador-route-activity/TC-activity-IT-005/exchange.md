# TC-activity-IT-005 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"活动城5-162755","englishName":"City16275516756","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b7a7-7923-92b1-7bebf09cee97",
  "chineseName": "活动城5-162755",
  "englishName": "City16275516756",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:56.199505689Z",
  "updatedAt": "2026-08-16T16:27:56.199505689Z"
}
```

## Step 2: 前置：创建活动

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-b7a7-7923-92b1-7bebf09cee97","images":["images/7c71c0b1-a791-4eae-ad64-a7dd2126baa8.png","images/446c22a8-8dd6-48d0-b34a-505bb4c2fe16.jpg"],"title":"待删活动-162755","tags":["露营","观星"],"periods":["FOLLICULAR","OVULATION"],"level":"L2","introduction":"海岛露营介绍","editorNote":"编辑寄语","gatheringPlace":"成都天府机场","dismissalPlace":"市区解散","transportation":"大巴接驳","visa":"无需签证","itinerary":[{"title":"Day1","content":"到成都天府机场集合"},{"title":"Day2","content":"露营与观星"}],"detailHtml":"<p>活动详情段落文本</p>","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b7ce-7d3b-82ae-25e5d875bb35",
  "cityId": "01a00b66-b7a7-7923-92b1-7bebf09cee97",
  "images": [
    {
      "id": "bound/7c71c0b1-a791-4eae-ad64-a7dd2126baa8.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/7c71c0b1-a791-4eae-ad64-a7dd2126baa8.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=1HQOPFsDk3PSBs8yJVg4ocDB2so%3D"
    },
    {
      "id": "bound/446c22a8-8dd6-48d0-b34a-505bb4c2fe16.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/446c22a8-8dd6-48d0-b34a-505bb4c2fe16.jpg?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=gJMGSsuI5%2BUT6OAD9sUxJkd36Hg%3D"
    }
  ],
  "title": "待删活动-162755",
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
  "createdAt": "2026-08-16T16:27:56.23876824Z",
  "updatedAt": "2026-08-16T16:27:56.23876824Z"
}
```

## Step 3: DELETE /api/admin/activities/{id}

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/activities/01a00b66-b7ce-7d3b-82ae-25e5d875bb35" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200



```

## Step 4: GET 已删除活动详情（应 400 中文口径）

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/activities/01a00b66-b7ce-7d3b-82ae-25e5d875bb35" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "活动不存在：01a00b66-b7ce-7d3b-82ae-25e5d875bb35",
  "path": "/api/admin/activities/01a00b66-b7ce-7d3b-82ae-25e5d875bb35"
}
```

## Step 5: GET /api/admin/activities/page?cityId= 确认列表不含该活动

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/activities/page?cityId=01a00b66-b7a7-7923-92b1-7bebf09cee97&page=0&size=50" -H "Authorization: Bearer $TOKEN"
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

