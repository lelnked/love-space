# TC-activity-IT-009 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"活动城9-162755","englishName":"City162755774","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-ba86-7e76-b789-cd12ee53bddc",
  "chineseName": "活动城9-162755",
  "englishName": "City162755774",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:56.934823902Z",
  "updatedAt": "2026-08-16T16:27:56.934823902Z"
}
```

## Step 2: 前置：创建上线活动（detailHtml 含图片与文本）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-ba86-7e76-b789-cd12ee53bddc","images":["images/2587b357-af5a-443a-bef3-d0b440c975bd.png","images/7e48c38f-0350-4c03-9299-341472acd98f.jpg"],"title":"详情活动-162755","tags":["露营","观星"],"periods":["FOLLICULAR","OVULATION"],"level":"L2","introduction":"海岛露营介绍","editorNote":"编辑寄语","gatheringPlace":"成都天府机场","dismissalPlace":"市区解散","transportation":"大巴接驳","visa":"无需签证","itinerary":[{"title":"Day1","content":"到成都天府机场集合"},{"title":"Day2","content":"露营与观星"}],"detailHtml":"<p>行前须知文本</p><img src=\"images/ee3a432d-108d-4df6-b4f1-eff089a4bd1e.png\"><p>结尾文本</p>","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-baad-755b-a8ea-92184c4f3ac5",
  "cityId": "01a00b66-ba86-7e76-b789-cd12ee53bddc",
  "images": [
    {
      "id": "bound/2587b357-af5a-443a-bef3-d0b440c975bd.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/2587b357-af5a-443a-bef3-d0b440c975bd.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=G8mN0vZEKh53IgYyjCi0yxSYi70%3D"
    },
    {
      "id": "bound/7e48c38f-0350-4c03-9299-341472acd98f.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/7e48c38f-0350-4c03-9299-341472acd98f.jpg?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RfvUGZsqFNCNJT%2FOuU4L8fkg%2B20%3D"
    }
  ],
  "title": "详情活动-162755",
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
  "detailHtml": "<p>行前须知文本</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/ee3a432d-108d-4df6-b4f1-eff089a4bd1e.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Rk4DFYfVz9ytqn484C2Thh1JakY%3D\"><p>结尾文本</p>",
  "online": true,
  "createdAt": "2026-08-16T16:27:56.973275826Z",
  "updatedAt": "2026-08-16T16:27:56.973275826Z"
}
```

## Step 3: GET /api/app/activities/{id} app 端活动详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/activities/01a00b66-baad-755b-a8ea-92184c4f3ac5" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-baad-755b-a8ea-92184c4f3ac5",
  "cityId": "01a00b66-ba86-7e76-b789-cd12ee53bddc",
  "images": [
    {
      "id": "bound/2587b357-af5a-443a-bef3-d0b440c975bd.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/2587b357-af5a-443a-bef3-d0b440c975bd.png?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=iCuwf1l0lUyOr4zxS3yephGvzJ8%3D"
    },
    {
      "id": "bound/7e48c38f-0350-4c03-9299-341472acd98f.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/7e48c38f-0350-4c03-9299-341472acd98f.jpg?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=I01gRf2xbO3SVBWQHXWu8lKbs%2BY%3D"
    }
  ],
  "title": "详情活动-162755",
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
  "detailHtml": "<p>行前须知文本</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/ee3a432d-108d-4df6-b4f1-eff089a4bd1e.png?Expires=1786899477&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mgBYLrSrxC2eMVbOVDsn6aA8D74%3D\"><p>结尾文本</p>"
}
```

