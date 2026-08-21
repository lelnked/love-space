# TC-featured-IT-020 GET /api/app/featured-cycle-items 城市未上架不影响路线类条目 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

> 前置清理：删除历史遗留的周期推荐条目，使四分组初始为空（周期推荐 feed 为全局，不按城市过滤）。

## Step 1a: 创建下架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"下架城020-R3","englishName":"OfflineCity020XR3","chineseProvince":"测试省","englishProvince":"Test Province","online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb9-c5ec-7488-ad33-f38e80ffeb19",
  "chineseName": "下架城020-R3",
  "englishName": "OfflineCity020XR3",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-20T15:11:03.660228033Z",
  "updatedAt": "2026-08-20T15:11:03.660228033Z"
}
```

## Step 1b: 创建 online=true 的大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/it020-avatar.png","name":"路线大使020R3","tags":["向导"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb9-c627-7638-afcd-bc4dadff3888",
  "avatar": {
    "id": "bound/it020-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it020-avatar.png?Expires=1787240463&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=d6FlTDmYOLuX4KSoNq23Z1c%2Fm1I%3D"
  },
  "name": "路线大使020R3",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:11:03.719329254Z",
  "updatedAt": "2026-08-20T15:11:03.719329254Z"
}
```

## Step 1c: 在下架城市下创建路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb9-c5ec-7488-ad33-f38e80ffeb19","sortOrder":1,"title":"下架城路线020R3","ambassadorNote":"语","thumbnail":"images/it020-thumb.png","images":["images/it020-img1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a01fb9-c627-7638-afcd-bc4dadff3888","spots":[{"name":"S1","image":"images/it020-s1.png","introduction":"i1"}]}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb9-c664-787c-947e-976fba92525b",
  "cityId": "01a01fb9-c5ec-7488-ad33-f38e80ffeb19",
  "sortOrder": 1,
  "title": "下架城路线020R3",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/it020-thumb.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it020-thumb.png?Expires=1787240463&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=CmRqd9K6N%2F3YStEoOK%2FJJ3A7nHE%3D"
  },
  "images": [
    {
      "id": "bound/it020-img1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it020-img1.png?Expires=1787240463&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=yHyLUPXDLBFubXv5AhMj0aFptXA%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a01fb9-c627-7638-afcd-bc4dadff3888",
  "ambassadorName": "路线大使020R3",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/it020-s1.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it020-s1.png?Expires=1787240463&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=FzD6sf5WblElokv5HDr0zNLj39A%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-20T15:11:03.78047Z",
  "updatedAt": "2026-08-20T15:11:03.78047Z"
}
```

## Step 1d: 在同一下架城市下创建上线活动

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb9-c5ec-7488-ad33-f38e80ffeb19","images":["images/it020-a1.png"],"title":"下架城活动020R3","tags":["露营"],"periods":["OVULATION"],"level":"L2","introduction":"介绍","editorNote":"寄语","gatheringPlace":"集合","dismissalPlace":"解散","transportation":"大巴","visa":"无需签证","itinerary":[{"title":"Day1","content":"集合"}],"detailHtml":"<p>详情</p>","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb9-c6a2-7d31-89e0-56933604f79e",
  "cityId": "01a01fb9-c5ec-7488-ad33-f38e80ffeb19",
  "images": [
    {
      "id": "bound/it020-a1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it020-a1.png?Expires=1787240463&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=15IM%2Fo1ZtzM4Em%2Ft2mvXeZSI%2FX0%3D"
    }
  ],
  "title": "下架城活动020R3",
  "tags": [
    "露营"
  ],
  "periods": [
    "OVULATION"
  ],
  "level": "L2",
  "introduction": "介绍",
  "editorNote": "寄语",
  "gatheringPlace": "集合",
  "dismissalPlace": "解散",
  "transportation": "大巴",
  "visa": "无需签证",
  "itinerary": [
    {
      "title": "Day1",
      "content": "集合"
    }
  ],
  "detailHtml": "<p>详情</p>",
  "online": true,
  "createdAt": "2026-08-20T15:11:03.842761221Z",
  "updatedAt": "2026-08-20T15:11:03.842761221Z"
}
```

## Step 1e: OVULATION 下建上线 ROUTE 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"OVULATION","type":"ROUTE","routeId":"01a01fb9-c664-787c-947e-976fba92525b","title":"路线条目020","subtitle":"副标题","description":"路线推荐说明","banner":"images/it020-b1.png","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb9-c6e8-7419-a8bb-1b5d3ce053ae",
  "phase": "OVULATION",
  "type": "ROUTE",
  "sortOrder": 0,
  "online": true,
  "activityId": null,
  "routeId": "01a01fb9-c664-787c-947e-976fba92525b",
  "articleId": null,
  "relatedTitle": "下架城路线020R3",
  "title": "路线条目020",
  "subtitle": "副标题",
  "description": "路线推荐说明",
  "note": null,
  "banner": {
    "id": "bound/it020-b1.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it020-b1.png?Expires=1787240463&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=2dRz%2FWoZCp2hPrAocISEwhBZw0I%3D"
  },
  "createdAt": "2026-08-20T15:11:03.912157515Z",
  "updatedAt": "2026-08-20T15:11:03.912157515Z"
}
```

## Step 1f: 同分组下建上线 ACTIVITY 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"OVULATION","type":"ACTIVITY","activityId":"01a01fb9-c6a2-7d31-89e0-56933604f79e","description":"活动条目020","banner":"images/it020-b2.png","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb9-c737-736d-8458-fa82d323ec1d",
  "phase": "OVULATION",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": true,
  "activityId": "01a01fb9-c6a2-7d31-89e0-56933604f79e",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "下架城活动020R3",
  "title": null,
  "subtitle": null,
  "description": "活动条目020",
  "note": null,
  "banner": {
    "id": "bound/it020-b2.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it020-b2.png?Expires=1787240463&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mIc309mEd97lkrLP2fadKUWQs3M%3D"
  },
  "createdAt": "2026-08-20T15:11:03.991109874Z",
  "updatedAt": "2026-08-20T15:11:03.991109874Z"
}
```

## Step 2: app 端查询周期推荐（城市仍下架）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [],
  "FOLLICULAR": [],
  "OVULATION": [
    {
      "id": "01a01fb9-c6e8-7419-a8bb-1b5d3ce053ae",
      "type": "ROUTE",
      "banner": {
        "id": "bound/it020-b1.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it020-b1.png?Expires=1787240464&OSSAccessKeyId=test-oss-ak&Signature=9F19GyG%2BwXLvP408EevdwN2YBjU%3D"
      },
      "activityId": null,
      "routeId": "01a01fb9-c664-787c-947e-976fba92525b",
      "articleId": null,
      "title": "路线条目020",
      "subtitle": "副标题",
      "description": "路线推荐说明",
      "note": null
    }
  ],
  "LUTEAL": []
}
```

## Step 3a: 将该城市上架

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a01fb9-c5ec-7488-ad33-f38e80ffeb19/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb9-c5ec-7488-ad33-f38e80ffeb19",
  "chineseName": "下架城020-R3",
  "englishName": "OfflineCity020XR3",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:11:03.660228Z",
  "updatedAt": "2026-08-20T15:11:03.660228Z"
}
```

## Step 3b: 城市上架后再次查询

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [],
  "FOLLICULAR": [],
  "OVULATION": [
    {
      "id": "01a01fb9-c737-736d-8458-fa82d323ec1d",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/it020-b2.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it020-b2.png?Expires=1787240464&OSSAccessKeyId=test-oss-ak&Signature=S90d6YJxWoLApoeMDl1VUGnwzas%3D"
      },
      "activityId": "01a01fb9-c6a2-7d31-89e0-56933604f79e",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "活动条目020",
      "note": null
    },
    {
      "id": "01a01fb9-c6e8-7419-a8bb-1b5d3ce053ae",
      "type": "ROUTE",
      "banner": {
        "id": "bound/it020-b1.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it020-b1.png?Expires=1787240464&OSSAccessKeyId=test-oss-ak&Signature=9F19GyG%2BwXLvP408EevdwN2YBjU%3D"
      },
      "activityId": null,
      "routeId": "01a01fb9-c664-787c-947e-976fba92525b",
      "articleId": null,
      "title": "路线条目020",
      "subtitle": "副标题",
      "description": "路线推荐说明",
      "note": null
    }
  ],
  "LUTEAL": []
}
```
