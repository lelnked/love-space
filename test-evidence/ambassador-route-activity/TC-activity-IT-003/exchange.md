# TC-activity-IT-003 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"活动城3-162755","englishName":"City16275528380","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b5fb-7f6d-83a3-068b4374366f",
  "chineseName": "活动城3-162755",
  "englishName": "City16275528380",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:55.771896199Z",
  "updatedAt": "2026-08-16T16:27:55.771896199Z"
}
```

## Step 2: 前置：创建 online=true 的活动

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-b5fb-7f6d-83a3-068b4374366f","images":["images/c8f25673-7eb3-430b-ab2a-9cb16a9b7996.png","images/422e97c4-1e29-4573-b162-33108b3bacc6.jpg"],"title":"切换活动-162755","tags":["露营","观星"],"periods":["FOLLICULAR","OVULATION"],"level":"L2","introduction":"海岛露营介绍","editorNote":"编辑寄语","gatheringPlace":"成都天府机场","dismissalPlace":"市区解散","transportation":"大巴接驳","visa":"无需签证","itinerary":[{"title":"Day1","content":"到成都天府机场集合"},{"title":"Day2","content":"露营与观星"}],"detailHtml":"<p>活动详情段落文本</p>","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b62a-7350-b015-6cc73247813f",
  "cityId": "01a00b66-b5fb-7f6d-83a3-068b4374366f",
  "images": [
    {
      "id": "bound/c8f25673-7eb3-430b-ab2a-9cb16a9b7996.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c8f25673-7eb3-430b-ab2a-9cb16a9b7996.png?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=icfXWHDptGESvdVmomZeWSWn8ns%3D"
    },
    {
      "id": "bound/422e97c4-1e29-4573-b162-33108b3bacc6.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/422e97c4-1e29-4573-b162-33108b3bacc6.jpg?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RSnR09HbkemqRoUMbGREnkQmKp8%3D"
    }
  ],
  "title": "切换活动-162755",
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
  "createdAt": "2026-08-16T16:27:55.818147105Z",
  "updatedAt": "2026-08-16T16:27:55.818147105Z"
}
```

## Step 3: PUT /online 下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a00b66-b62a-7350-b015-6cc73247813f/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":false}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b62a-7350-b015-6cc73247813f",
  "cityId": "01a00b66-b5fb-7f6d-83a3-068b4374366f",
  "images": [
    {
      "id": "bound/c8f25673-7eb3-430b-ab2a-9cb16a9b7996.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c8f25673-7eb3-430b-ab2a-9cb16a9b7996.png?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=icfXWHDptGESvdVmomZeWSWn8ns%3D"
    },
    {
      "id": "bound/422e97c4-1e29-4573-b162-33108b3bacc6.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/422e97c4-1e29-4573-b162-33108b3bacc6.jpg?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RSnR09HbkemqRoUMbGREnkQmKp8%3D"
    }
  ],
  "title": "切换活动-162755",
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
  "createdAt": "2026-08-16T16:27:55.818147Z",
  "updatedAt": "2026-08-16T16:27:55.818147Z"
}
```

## Step 4: GET 详情确认 online=false

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/activities/01a00b66-b62a-7350-b015-6cc73247813f" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b62a-7350-b015-6cc73247813f",
  "cityId": "01a00b66-b5fb-7f6d-83a3-068b4374366f",
  "images": [
    {
      "id": "bound/c8f25673-7eb3-430b-ab2a-9cb16a9b7996.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c8f25673-7eb3-430b-ab2a-9cb16a9b7996.png?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=icfXWHDptGESvdVmomZeWSWn8ns%3D"
    },
    {
      "id": "bound/422e97c4-1e29-4573-b162-33108b3bacc6.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/422e97c4-1e29-4573-b162-33108b3bacc6.jpg?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RSnR09HbkemqRoUMbGREnkQmKp8%3D"
    }
  ],
  "title": "切换活动-162755",
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
  "createdAt": "2026-08-16T16:27:55.818147Z",
  "updatedAt": "2026-08-16T16:27:55.857654Z"
}
```

## Step 5: PUT /online 重新上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a00b66-b62a-7350-b015-6cc73247813f/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b62a-7350-b015-6cc73247813f",
  "cityId": "01a00b66-b5fb-7f6d-83a3-068b4374366f",
  "images": [
    {
      "id": "bound/c8f25673-7eb3-430b-ab2a-9cb16a9b7996.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c8f25673-7eb3-430b-ab2a-9cb16a9b7996.png?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=icfXWHDptGESvdVmomZeWSWn8ns%3D"
    },
    {
      "id": "bound/422e97c4-1e29-4573-b162-33108b3bacc6.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/422e97c4-1e29-4573-b162-33108b3bacc6.jpg?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RSnR09HbkemqRoUMbGREnkQmKp8%3D"
    }
  ],
  "title": "切换活动-162755",
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
  "createdAt": "2026-08-16T16:27:55.818147Z",
  "updatedAt": "2026-08-16T16:27:55.857654Z"
}
```

## Step 6: GET 详情确认 online=true（可往返）

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/activities/01a00b66-b62a-7350-b015-6cc73247813f" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b62a-7350-b015-6cc73247813f",
  "cityId": "01a00b66-b5fb-7f6d-83a3-068b4374366f",
  "images": [
    {
      "id": "bound/c8f25673-7eb3-430b-ab2a-9cb16a9b7996.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c8f25673-7eb3-430b-ab2a-9cb16a9b7996.png?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=icfXWHDptGESvdVmomZeWSWn8ns%3D"
    },
    {
      "id": "bound/422e97c4-1e29-4573-b162-33108b3bacc6.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/422e97c4-1e29-4573-b162-33108b3bacc6.jpg?Expires=1786899475&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RSnR09HbkemqRoUMbGREnkQmKp8%3D"
    }
  ],
  "title": "切换活动-162755",
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
  "createdAt": "2026-08-16T16:27:55.818147Z",
  "updatedAt": "2026-08-16T16:27:55.918412Z"
}
```

