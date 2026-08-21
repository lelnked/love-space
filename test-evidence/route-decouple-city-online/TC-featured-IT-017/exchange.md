# TC-featured-IT-017 GET /api/app/featured-cycle-items 关联实体不可见时条目不下发 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

> 前置清理：删除历史遗留的周期推荐条目，使四分组初始为空。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"精选城017R","englishName":"FeatCity017R","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbb-7c95-746d-9b6a-cfc1da1b8dff",
  "chineseName": "精选城017R",
  "englishName": "FeatCity017R",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:12:55.957219996Z",
  "updatedAt": "2026-08-20T15:12:55.957219996Z"
}
```

## Step 1b: 创建上线活动

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fbb-7c95-746d-9b6a-cfc1da1b8dff","images":["images/it017r-a1.png"],"title":"活动T017R","tags":["露营"],"periods":["MENSTRUAL"],"level":"L2","introduction":"介绍","editorNote":"寄语","gatheringPlace":"集合","dismissalPlace":"解散","transportation":"大巴","visa":"无需签证","itinerary":[{"title":"Day1","content":"集合"}],"detailHtml":"<p>详情</p>","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbb-7cd5-73d8-bd48-07f7c3c66cd1",
  "cityId": "01a01fbb-7c95-746d-9b6a-cfc1da1b8dff",
  "images": [
    {
      "id": "bound/it017r-a1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017r-a1.png?Expires=1787240576&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Hoq%2FSONp1kFNMdL9HERAyCBPdh8%3D"
    }
  ],
  "title": "活动T017R",
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
  "createdAt": "2026-08-20T15:12:56.021175764Z",
  "updatedAt": "2026-08-20T15:12:56.021175764Z"
}
```

## Step 1c: 创建文章栏目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"栏目017R","icon":"images/it017r-cat.png","sortOrder":92}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbb-7d16-7b2f-a3f6-79de2b5f0cb6",
  "name": "栏目017R",
  "icon": {
    "id": "bound/it017r-cat.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017r-cat.png?Expires=1787240576&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=CmUvNp%2BcHzMmu8hODWtStxol4fE%3D"
  },
  "sortOrder": 92,
  "createdAt": "2026-08-20T15:12:56.086639846Z",
  "updatedAt": "2026-08-20T15:12:56.086639846Z"
}
```

## Step 1d: 创建上线文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it017r-cover.png","title":"文章T017R","subtitle":"x","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a01fbb-7d16-7b2f-a3f6-79de2b5f0cb6"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbb-7d56-7572-adfd-5966b24f2b8b",
  "image": {
    "id": "bound/it017r-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017r-cover.png?Expires=1787240576&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=1brqUHMKuchRd9K60uZx37hCa4w%3D"
  },
  "title": "文章T017R",
  "subtitle": "x",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a01fbb-7d16-7b2f-a3f6-79de2b5f0cb6"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:12:56.150281746Z",
  "updatedAt": "2026-08-20T15:12:56.150281746Z"
}
```

## Step 1e: MENSTRUAL 建上线 ACTIVITY 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a01fbb-7cd5-73d8-bd48-07f7c3c66cd1","description":"活动条目","banner":"images/it017r-b1.png","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbb-7d98-7fd9-9d22-a4972eab6c2d",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": true,
  "activityId": "01a01fbb-7cd5-73d8-bd48-07f7c3c66cd1",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T017R",
  "title": null,
  "subtitle": null,
  "description": "活动条目",
  "note": null,
  "banner": {
    "id": "bound/it017r-b1.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017r-b1.png?Expires=1787240576&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=hf4vve5fsb9kIwNCRshVsSdWKfc%3D"
  },
  "createdAt": "2026-08-20T15:12:56.216885678Z",
  "updatedAt": "2026-08-20T15:12:56.216885678Z"
}
```

## Step 1f: MENSTRUAL 建上线 ARTICLE 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ARTICLE","articleId":"01a01fbb-7d56-7572-adfd-5966b24f2b8b","title":"文章条目","banner":"images/it017r-b2.png","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbb-7dde-71a3-bb9b-9004e29c0d0f",
  "phase": "MENSTRUAL",
  "type": "ARTICLE",
  "sortOrder": 0,
  "online": true,
  "activityId": null,
  "routeId": null,
  "articleId": "01a01fbb-7d56-7572-adfd-5966b24f2b8b",
  "relatedTitle": "文章T017R",
  "title": "文章条目",
  "subtitle": null,
  "description": null,
  "note": null,
  "banner": {
    "id": "bound/it017r-b2.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017r-b2.png?Expires=1787240576&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=MD77b0ZV5JURR3CQ7JRXUJvkQcA%3D"
  },
  "createdAt": "2026-08-20T15:12:56.286038457Z",
  "updatedAt": "2026-08-20T15:12:56.286038457Z"
}
```

## Step 1g: 前置确认两条均在 MENSTRUAL

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01fbb-7dde-71a3-bb9b-9004e29c0d0f",
      "type": "ARTICLE",
      "banner": {
        "id": "bound/it017r-b2.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it017r-b2.png?Expires=1787240576&OSSAccessKeyId=test-oss-ak&Signature=NSmYsnLCN48Rq%2Bem53lYO%2BnheQw%3D"
      },
      "activityId": null,
      "routeId": null,
      "articleId": "01a01fbb-7d56-7572-adfd-5966b24f2b8b",
      "title": "文章条目",
      "subtitle": null,
      "description": null,
      "note": null
    },
    {
      "id": "01a01fbb-7d98-7fd9-9d22-a4972eab6c2d",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/it017r-b1.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it017r-b1.png?Expires=1787240576&OSSAccessKeyId=test-oss-ak&Signature=3yTJDy7C%2BypzZgzb2wcGr9Qkq0U%3D"
      },
      "activityId": "01a01fbb-7cd5-73d8-bd48-07f7c3c66cd1",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "活动条目",
      "note": null
    }
  ],
  "FOLLICULAR": [],
  "OVULATION": [],
  "LUTEAL": []
}
```

## Step 2a: 将该活动下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a01fbb-7cd5-73d8-bd48-07f7c3c66cd1/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbb-7cd5-73d8-bd48-07f7c3c66cd1",
  "cityId": "01a01fbb-7c95-746d-9b6a-cfc1da1b8dff",
  "images": [
    {
      "id": "bound/it017r-a1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017r-a1.png?Expires=1787240576&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Hoq%2FSONp1kFNMdL9HERAyCBPdh8%3D"
    }
  ],
  "title": "活动T017R",
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
  "online": false,
  "createdAt": "2026-08-20T15:12:56.021176Z",
  "updatedAt": "2026-08-20T15:12:56.021176Z"
}
```

## Step 2b: 活动下线后查询

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01fbb-7dde-71a3-bb9b-9004e29c0d0f",
      "type": "ARTICLE",
      "banner": {
        "id": "bound/it017r-b2.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it017r-b2.png?Expires=1787240576&OSSAccessKeyId=test-oss-ak&Signature=NSmYsnLCN48Rq%2Bem53lYO%2BnheQw%3D"
      },
      "activityId": null,
      "routeId": null,
      "articleId": "01a01fbb-7d56-7572-adfd-5966b24f2b8b",
      "title": "文章条目",
      "subtitle": null,
      "description": null,
      "note": null
    }
  ],
  "FOLLICULAR": [],
  "OVULATION": [],
  "LUTEAL": []
}
```

## Step 3a: 恢复活动上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a01fbb-7cd5-73d8-bd48-07f7c3c66cd1/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbb-7cd5-73d8-bd48-07f7c3c66cd1",
  "cityId": "01a01fbb-7c95-746d-9b6a-cfc1da1b8dff",
  "images": [
    {
      "id": "bound/it017r-a1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017r-a1.png?Expires=1787240576&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Hoq%2FSONp1kFNMdL9HERAyCBPdh8%3D"
    }
  ],
  "title": "活动T017R",
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
  "createdAt": "2026-08-20T15:12:56.021176Z",
  "updatedAt": "2026-08-20T15:12:56.403125Z"
}
```

## Step 3b: 将活动所属城市下架

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a01fbb-7c95-746d-9b6a-cfc1da1b8dff/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbb-7c95-746d-9b6a-cfc1da1b8dff",
  "chineseName": "精选城017R",
  "englishName": "FeatCity017R",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-20T15:12:55.95722Z",
  "updatedAt": "2026-08-20T15:12:55.95722Z"
}
```

## Step 3c: 城市下架后查询

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01fbb-7dde-71a3-bb9b-9004e29c0d0f",
      "type": "ARTICLE",
      "banner": {
        "id": "bound/it017r-b2.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it017r-b2.png?Expires=1787240576&OSSAccessKeyId=test-oss-ak&Signature=NSmYsnLCN48Rq%2Bem53lYO%2BnheQw%3D"
      },
      "activityId": null,
      "routeId": null,
      "articleId": "01a01fbb-7d56-7572-adfd-5966b24f2b8b",
      "title": "文章条目",
      "subtitle": null,
      "description": null,
      "note": null
    }
  ],
  "FOLLICULAR": [],
  "OVULATION": [],
  "LUTEAL": []
}
```

## Step 4a: 恢复城市上架

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a01fbb-7c95-746d-9b6a-cfc1da1b8dff/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbb-7c95-746d-9b6a-cfc1da1b8dff",
  "chineseName": "精选城017R",
  "englishName": "FeatCity017R",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:12:55.95722Z",
  "updatedAt": "2026-08-20T15:12:56.541113Z"
}
```

## Step 4b: 将该文章下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a01fbb-7d56-7572-adfd-5966b24f2b8b/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbb-7d56-7572-adfd-5966b24f2b8b",
  "image": {
    "id": "bound/it017r-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017r-cover.png?Expires=1787240576&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=1brqUHMKuchRd9K60uZx37hCa4w%3D"
  },
  "title": "文章T017R",
  "subtitle": "x",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a01fbb-7d16-7b2f-a3f6-79de2b5f0cb6"
  ],
  "online": false,
  "createdAt": "2026-08-20T15:12:56.150282Z",
  "updatedAt": "2026-08-20T15:12:56.680613191Z"
}
```

## Step 4c: 文章下线后查询

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01fbb-7d98-7fd9-9d22-a4972eab6c2d",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/it017r-b1.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it017r-b1.png?Expires=1787240576&OSSAccessKeyId=test-oss-ak&Signature=3yTJDy7C%2BypzZgzb2wcGr9Qkq0U%3D"
      },
      "activityId": "01a01fbb-7cd5-73d8-bd48-07f7c3c66cd1",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "活动条目",
      "note": null
    }
  ],
  "FOLLICULAR": [],
  "OVULATION": [],
  "LUTEAL": []
}
```

## Step 5a: 恢复文章上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a01fbb-7d56-7572-adfd-5966b24f2b8b/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fbb-7d56-7572-adfd-5966b24f2b8b",
  "image": {
    "id": "bound/it017r-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it017r-cover.png?Expires=1787240576&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=1brqUHMKuchRd9K60uZx37hCa4w%3D"
  },
  "title": "文章T017R",
  "subtitle": "x",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a01fbb-7d16-7b2f-a3f6-79de2b5f0cb6"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:12:56.150282Z",
  "updatedAt": "2026-08-20T15:12:56.775157416Z"
}
```

## Step 5b: 删除该文章

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/articles/01a01fbb-7d56-7572-adfd-5966b24f2b8b" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，无 Content-Type，Content-Length: 0）:

```
(空响应体)
```

## Step 5c: 文章删除后查询

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01fbb-7d98-7fd9-9d22-a4972eab6c2d",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/it017r-b1.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it017r-b1.png?Expires=1787240576&OSSAccessKeyId=test-oss-ak&Signature=3yTJDy7C%2BypzZgzb2wcGr9Qkq0U%3D"
      },
      "activityId": "01a01fbb-7cd5-73d8-bd48-07f7c3c66cd1",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "活动条目",
      "note": null
    }
  ],
  "FOLLICULAR": [],
  "OVULATION": [],
  "LUTEAL": []
}
```
