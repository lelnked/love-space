# TC-activity-IT-004 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建城市 A

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"活动城4A-162755","englishName":"City1627557934","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b6d5-70db-bcfe-8b8a5b72c232",
  "chineseName": "活动城4A-162755",
  "englishName": "City1627557934",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:55.988979948Z",
  "updatedAt": "2026-08-16T16:27:55.988979948Z"
}
```

## Step 2: 前置：创建城市 B

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"活动城4B-162755","englishName":"City16275515472","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b6fb-78ef-b711-65df2b54c228",
  "chineseName": "活动城4B-162755",
  "englishName": "City16275515472",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:56.02749471Z",
  "updatedAt": "2026-08-16T16:27:56.02749471Z"
}
```

## Step 3: 前置：城市 A 下创建活动

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-b6d5-70db-bcfe-8b8a5b72c232","images":["images/1e7f2698-6629-4fc0-83df-c97fd0cf9894.png","images/940fb0fa-6cd3-4234-be84-b27627813b0b.jpg"],"title":"原活动-162755","tags":["露营","观星"],"periods":["FOLLICULAR","OVULATION"],"level":"L2","introduction":"海岛露营介绍","editorNote":"编辑寄语","gatheringPlace":"成都天府机场","dismissalPlace":"市区解散","transportation":"大巴接驳","visa":"无需签证","itinerary":[{"title":"Day1","content":"到成都天府机场集合"},{"title":"Day2","content":"露营与观星"}],"detailHtml":"<p>活动详情段落文本</p>","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b721-7615-8b7e-7b3028b365c7",
  "cityId": "01a00b66-b6d5-70db-bcfe-8b8a5b72c232",
  "images": [
    {
      "id": "bound/1e7f2698-6629-4fc0-83df-c97fd0cf9894.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/1e7f2698-6629-4fc0-83df-c97fd0cf9894.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=2UPLu9LzrxlQdWul6P2lLMQ2S3w%3D"
    },
    {
      "id": "bound/940fb0fa-6cd3-4234-be84-b27627813b0b.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/940fb0fa-6cd3-4234-be84-b27627813b0b.jpg?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RY6zVFybfD98jcDbSBYgQtMUXW4%3D"
    }
  ],
  "title": "原活动-162755",
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
  "createdAt": "2026-08-16T16:27:56.065277113Z",
  "updatedAt": "2026-08-16T16:27:56.065277113Z"
}
```

## Step 4: PUT /api/admin/activities/{id} 改 title/level=L3/periods=[MENSTRUAL]/itinerary 1 条/cityId 传城市 B

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a00b66-b721-7615-8b7e-7b3028b365c7" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-b6fb-78ef-b711-65df2b54c228","images":["images/b9a81a85-3e5a-40cd-a456-0f6cf1708689.png"],"title":"改名活动","tags":["露营"],"periods":["MENSTRUAL"],"level":"L3","itinerary":[{"title":"NewDay","content":"新行程仅一条"}],"detailHtml":"<p>更新后</p>"}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b721-7615-8b7e-7b3028b365c7",
  "cityId": "01a00b66-b6d5-70db-bcfe-8b8a5b72c232",
  "images": [
    {
      "id": "bound/b9a81a85-3e5a-40cd-a456-0f6cf1708689.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b9a81a85-3e5a-40cd-a456-0f6cf1708689.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=TF0M%2FwZBU6chwJouiPaCHRYBs3g%3D"
    }
  ],
  "title": "改名活动",
  "tags": [
    "露营"
  ],
  "periods": [
    "MENSTRUAL"
  ],
  "level": "L3",
  "introduction": null,
  "editorNote": null,
  "gatheringPlace": null,
  "dismissalPlace": null,
  "transportation": null,
  "visa": null,
  "itinerary": [
    {
      "title": "NewDay",
      "content": "新行程仅一条"
    }
  ],
  "detailHtml": "<p>更新后</p>",
  "online": false,
  "createdAt": "2026-08-16T16:27:56.065277Z",
  "updatedAt": "2026-08-16T16:27:56.065277Z"
}
```

## Step 5: GET 详情确认更新生效且 cityId 仍为城市 A

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/activities/01a00b66-b721-7615-8b7e-7b3028b365c7" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b721-7615-8b7e-7b3028b365c7",
  "cityId": "01a00b66-b6d5-70db-bcfe-8b8a5b72c232",
  "images": [
    {
      "id": "bound/b9a81a85-3e5a-40cd-a456-0f6cf1708689.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b9a81a85-3e5a-40cd-a456-0f6cf1708689.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=TF0M%2FwZBU6chwJouiPaCHRYBs3g%3D"
    }
  ],
  "title": "改名活动",
  "tags": [
    "露营"
  ],
  "periods": [
    "MENSTRUAL"
  ],
  "level": "L3",
  "introduction": null,
  "editorNote": null,
  "gatheringPlace": null,
  "dismissalPlace": null,
  "transportation": null,
  "visa": null,
  "itinerary": [
    {
      "title": "NewDay",
      "content": "新行程仅一条"
    }
  ],
  "detailHtml": "<p>更新后</p>",
  "online": false,
  "createdAt": "2026-08-16T16:27:56.065277Z",
  "updatedAt": "2026-08-16T16:27:56.114229Z"
}
```

