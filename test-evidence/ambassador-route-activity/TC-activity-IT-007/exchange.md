# TC-activity-IT-007 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"活动城7-162755","englishName":"City16275522463","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b914-70e6-a659-08092aa04076",
  "chineseName": "活动城7-162755",
  "englishName": "City16275522463",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:56.56398121Z",
  "updatedAt": "2026-08-16T16:27:56.56398121Z"
}
```

## Step 2: 前置：创建上线活动（含图片/标签/级别/周期）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-b914-70e6-a659-08092aa04076","images":["images/f64e221a-61e5-4bda-ba1b-008fe7858b4d.png","images/9847ba9c-d783-4a45-a689-2c0e78ad5f5a.jpg"],"title":"列表活动-162755","tags":["露营","观星"],"periods":["FOLLICULAR","OVULATION"],"level":"L2","introduction":"海岛露营介绍","editorNote":"编辑寄语","gatheringPlace":"成都天府机场","dismissalPlace":"市区解散","transportation":"大巴接驳","visa":"无需签证","itinerary":[{"title":"Day1","content":"到成都天府机场集合"},{"title":"Day2","content":"露营与观星"}],"detailHtml":"<p>活动详情段落文本</p>","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b941-76ba-815b-d67acb5fae60",
  "cityId": "01a00b66-b914-70e6-a659-08092aa04076",
  "images": [
    {
      "id": "bound/f64e221a-61e5-4bda-ba1b-008fe7858b4d.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/f64e221a-61e5-4bda-ba1b-008fe7858b4d.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=kf0mIPA35r7jMYzMjQ2%2BBtBnXNs%3D"
    },
    {
      "id": "bound/9847ba9c-d783-4a45-a689-2c0e78ad5f5a.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/9847ba9c-d783-4a45-a689-2c0e78ad5f5a.jpg?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=1zJ02CRRH4jhIHTKwrRkwNiF77I%3D"
    }
  ],
  "title": "列表活动-162755",
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
  "createdAt": "2026-08-16T16:27:56.609342686Z",
  "updatedAt": "2026-08-16T16:27:56.609342686Z"
}
```

## Step 3: GET /api/app/activities?cityId= app 端活动列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/activities?cityId=01a00b66-b914-70e6-a659-08092aa04076" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

[
  {
    "id": "01a00b66-b941-76ba-815b-d67acb5fae60",
    "title": "列表活动-162755",
    "images": [
      {
        "id": "bound/f64e221a-61e5-4bda-ba1b-008fe7858b4d.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/f64e221a-61e5-4bda-ba1b-008fe7858b4d.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=kf0mIPA35r7jMYzMjQ2%2BBtBnXNs%3D"
      },
      {
        "id": "bound/9847ba9c-d783-4a45-a689-2c0e78ad5f5a.jpg",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/9847ba9c-d783-4a45-a689-2c0e78ad5f5a.jpg?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=1zJ02CRRH4jhIHTKwrRkwNiF77I%3D"
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

