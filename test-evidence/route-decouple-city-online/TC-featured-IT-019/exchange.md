# TC-featured-IT-019 GET /api/app/featured-cycle-items 组内按排序号升序 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

> 前置清理：删除历史遗留的周期推荐条目，使四分组初始为空。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"排序城019R","englishName":"SortCity019R","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbc-0d56-7662-931b-1bd543c24ad2",
  "chineseName": "排序城019R",
  "englishName": "SortCity019R",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:13:33.014342925Z",
  "updatedAt": "2026-08-20T15:13:33.014342925Z"
}
```

## Step 1b: 创建上线活动（关联实体可见）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fbc-0d56-7662-931b-1bd543c24ad2","images":["images/it019r-a1.png"],"title":"活动T019R","tags":["露营"],"periods":["MENSTRUAL"],"level":"L2","introduction":"介绍","editorNote":"寄语","gatheringPlace":"集合","dismissalPlace":"解散","transportation":"大巴","visa":"无需签证","itinerary":[{"title":"Day1","content":"集合"}],"detailHtml":"<p>详情</p>","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbc-0d93-7e19-99c3-a63e320daf11",
  "cityId": "01a01fbc-0d56-7662-931b-1bd543c24ad2",
  "images": [
    {
      "id": "bound/it019r-a1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it019r-a1.png?Expires=1787240613&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=eqmwokZPwvQqhJm7vdRSzTUr%2FSI%3D"
    }
  ],
  "title": "活动T019R",
  "tags": [
    "露营"
  ],
  "periods": [
    "MENSTRUAL"
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
  "createdAt": "2026-08-20T15:13:33.075825826Z",
  "updatedAt": "2026-08-20T15:13:33.075825826Z"
}
```

## Step 1c-2: 创建 sortOrder=2 的上线条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a01fbc-0d93-7e19-99c3-a63e320daf11","sortOrder":2,"description":"排序条目-2","banner":"images/it019r-b2.png","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbc-0dd3-72b6-96cc-0a882e09f09d",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 2,
  "online": true,
  "activityId": "01a01fbc-0d93-7e19-99c3-a63e320daf11",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019R",
  "title": null,
  "subtitle": null,
  "description": "排序条目-2",
  "note": null,
  "banner": {
    "id": "bound/it019r-b2.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it019r-b2.png?Expires=1787240613&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=5JvQ%2FKD7W1jsTXmGhs7do3U%2FVPU%3D"
  },
  "createdAt": "2026-08-20T15:13:33.139116324Z",
  "updatedAt": "2026-08-20T15:13:33.139116324Z"
}
```

## Step 1c-1: 创建 sortOrder=1 的上线条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a01fbc-0d93-7e19-99c3-a63e320daf11","sortOrder":1,"description":"排序条目-1","banner":"images/it019r-b1.png","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbc-0dfd-7333-87f8-67e8e127361a",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 1,
  "online": true,
  "activityId": "01a01fbc-0d93-7e19-99c3-a63e320daf11",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019R",
  "title": null,
  "subtitle": null,
  "description": "排序条目-1",
  "note": null,
  "banner": {
    "id": "bound/it019r-b1.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it019r-b1.png?Expires=1787240613&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ry8QnTnLih%2BzXddInjxwkwqB%2BcI%3D"
  },
  "createdAt": "2026-08-20T15:13:33.181141022Z",
  "updatedAt": "2026-08-20T15:13:33.181141022Z"
}
```

## Step 1c-3: 创建 sortOrder=3 的上线条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a01fbc-0d93-7e19-99c3-a63e320daf11","sortOrder":3,"description":"排序条目-3","banner":"images/it019r-b3.png","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbc-0e26-70d8-aa3f-8b133d10ad7f",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 3,
  "online": true,
  "activityId": "01a01fbc-0d93-7e19-99c3-a63e320daf11",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019R",
  "title": null,
  "subtitle": null,
  "description": "排序条目-3",
  "note": null,
  "banner": {
    "id": "bound/it019r-b3.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it019r-b3.png?Expires=1787240613&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=A9nYeTQrSKTvd8S2KL%2FyEoVK%2Bgw%3D"
  },
  "createdAt": "2026-08-20T15:13:33.221984355Z",
  "updatedAt": "2026-08-20T15:13:33.221984355Z"
}
```

## Step 1d: 创建并列 sortOrder=1 的条目 1A（先创建）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a01fbc-0d93-7e19-99c3-a63e320daf11","sortOrder":1,"description":"并列条目-1A","banner":"images/it019r-b1a.png","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbc-0e4f-718a-8e48-4c38a51c8462",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 1,
  "online": true,
  "activityId": "01a01fbc-0d93-7e19-99c3-a63e320daf11",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019R",
  "title": null,
  "subtitle": null,
  "description": "并列条目-1A",
  "note": null,
  "banner": {
    "id": "bound/it019r-b1a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it019r-b1a.png?Expires=1787240613&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6qzFaIwMnNAxmlfW1D7lelwzIP0%3D"
  },
  "createdAt": "2026-08-20T15:13:33.263040741Z",
  "updatedAt": "2026-08-20T15:13:33.263040741Z"
}
```

## Step 1e: 创建并列 sortOrder=1 的条目 1B（后创建）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a01fbc-0d93-7e19-99c3-a63e320daf11","sortOrder":1,"description":"并列条目-1B","banner":"images/it019r-b1b.png","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbc-0e8d-7116-8b52-0d74bbea24f4",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 1,
  "online": true,
  "activityId": "01a01fbc-0d93-7e19-99c3-a63e320daf11",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019R",
  "title": null,
  "subtitle": null,
  "description": "并列条目-1B",
  "note": null,
  "banner": {
    "id": "bound/it019r-b1b.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it019r-b1b.png?Expires=1787240613&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dm2bS4EY0dqWHM%2FX4Cc3Hn6b%2FpM%3D"
  },
  "createdAt": "2026-08-20T15:13:33.325007187Z",
  "updatedAt": "2026-08-20T15:13:33.325007187Z"
}
```

## Step 2: app 端查询周期推荐

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01fbc-0e8d-7116-8b52-0d74bbea24f4",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/it019r-b1b.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it019r-b1b.png?Expires=1787240613&OSSAccessKeyId=test-oss-ak&Signature=%2Fx1GQ9FzZpSlufQQXMtXJIU%2BsFI%3D"
      },
      "activityId": "01a01fbc-0d93-7e19-99c3-a63e320daf11",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "并列条目-1B",
      "note": null
    },
    {
      "id": "01a01fbc-0e4f-718a-8e48-4c38a51c8462",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/it019r-b1a.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it019r-b1a.png?Expires=1787240613&OSSAccessKeyId=test-oss-ak&Signature=F1KqB5nJxFVu6OwQoKlwcKfYllk%3D"
      },
      "activityId": "01a01fbc-0d93-7e19-99c3-a63e320daf11",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "并列条目-1A",
      "note": null
    },
    {
      "id": "01a01fbc-0dfd-7333-87f8-67e8e127361a",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/it019r-b1.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it019r-b1.png?Expires=1787240613&OSSAccessKeyId=test-oss-ak&Signature=oWg%2FHH%2B4csQyvZS2yd37aUIz8nc%3D"
      },
      "activityId": "01a01fbc-0d93-7e19-99c3-a63e320daf11",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "排序条目-1",
      "note": null
    },
    {
      "id": "01a01fbc-0dd3-72b6-96cc-0a882e09f09d",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/it019r-b2.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it019r-b2.png?Expires=1787240613&OSSAccessKeyId=test-oss-ak&Signature=W%2FlgKXvBZ9fJMPdp945dud%2F2KWA%3D"
      },
      "activityId": "01a01fbc-0d93-7e19-99c3-a63e320daf11",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "排序条目-2",
      "note": null
    },
    {
      "id": "01a01fbc-0e26-70d8-aa3f-8b133d10ad7f",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/it019r-b3.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it019r-b3.png?Expires=1787240613&OSSAccessKeyId=test-oss-ak&Signature=8UyTnoUeg9rNtMv4HX8iiROMOnA%3D"
      },
      "activityId": "01a01fbc-0d93-7e19-99c3-a63e320daf11",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "排序条目-3",
      "note": null
    }
  ],
  "FOLLICULAR": [],
  "OVULATION": [],
  "LUTEAL": []
}
```
