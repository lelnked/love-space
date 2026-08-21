# TC-featured-IT-016 GET /api/app/featured-cycle-items 四周期分组齐全且只含上线条目 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

> 前置清理：删除历史遗留的周期推荐条目，使四分组初始为空。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"精选城016R","englishName":"FeatCity016R","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-eb50-711f-ac06-da980c7775ad",
  "chineseName": "精选城016R",
  "englishName": "FeatCity016R",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:12:18.768013181Z",
  "updatedAt": "2026-08-20T15:12:18.768013181Z"
}
```

## Step 1b: 创建上线活动（城市已上架）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fba-eb50-711f-ac06-da980c7775ad","images":["images/it016r-a1.png"],"title":"活动T016R","tags":["露营"],"periods":["MENSTRUAL"],"level":"L2","introduction":"介绍","editorNote":"寄语","gatheringPlace":"集合","dismissalPlace":"解散","transportation":"大巴","visa":"无需签证","itinerary":[{"title":"Day1","content":"集合"}],"detailHtml":"<p>详情</p>","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-eb90-7c61-be5b-55765484a4b9",
  "cityId": "01a01fba-eb50-711f-ac06-da980c7775ad",
  "images": [
    {
      "id": "bound/it016r-a1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it016r-a1.png?Expires=1787240538&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=8259lVmhGLN%2BOtHa0pZiybrqsKM%3D"
    }
  ],
  "title": "活动T016R",
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
  "createdAt": "2026-08-20T15:12:18.832715334Z",
  "updatedAt": "2026-08-20T15:12:18.832715334Z"
}
```

## Step 1c: 创建文章栏目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"栏目016R","icon":"images/it016r-cat.png","sortOrder":91}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-ebd6-7f00-bc08-26b697c7e5b5",
  "name": "栏目016R",
  "icon": {
    "id": "bound/it016r-cat.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it016r-cat.png?Expires=1787240538&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=moKTHALwovaC2hsY2M27jKKdeow%3D"
  },
  "sortOrder": 91,
  "createdAt": "2026-08-20T15:12:18.901579695Z",
  "updatedAt": "2026-08-20T15:12:18.901579695Z"
}
```

## Step 1d: 创建上线文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it016r-cover.png","title":"文章T016R","subtitle":"x","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a01fba-ebd6-7f00-bc08-26b697c7e5b5"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-ec27-70cd-ba5d-649dac4449f6",
  "image": {
    "id": "bound/it016r-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it016r-cover.png?Expires=1787240538&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=T7zt6iFFAesAcdUdwh2h7CsWa58%3D"
  },
  "title": "文章T016R",
  "subtitle": "x",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a01fba-ebd6-7f00-bc08-26b697c7e5b5"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:12:18.981412156Z",
  "updatedAt": "2026-08-20T15:12:18.981412156Z"
}
```

## Step 1e: MENSTRUAL 建 1 个上线 ACTIVITY 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a01fba-eb90-7c61-be5b-55765484a4b9","description":"经期活动","banner":"images/it016r-b1.png","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-ec70-7a16-82b8-041e4f83b3aa",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": true,
  "activityId": "01a01fba-eb90-7c61-be5b-55765484a4b9",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T016R",
  "title": null,
  "subtitle": null,
  "description": "经期活动",
  "note": null,
  "banner": {
    "id": "bound/it016r-b1.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it016r-b1.png?Expires=1787240539&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=oKOMrHxu1OemYeUuH075492Uanc%3D"
  },
  "createdAt": "2026-08-20T15:12:19.056503864Z",
  "updatedAt": "2026-08-20T15:12:19.056503864Z"
}
```

## Step 1f: OVULATION 建 1 个上线 ARTICLE 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"OVULATION","type":"ARTICLE","articleId":"01a01fba-ec27-70cd-ba5d-649dac4449f6","title":"排卵期读物","banner":"images/it016r-b2.png","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-ecb5-74af-8fcb-2b5b7280fba2",
  "phase": "OVULATION",
  "type": "ARTICLE",
  "sortOrder": 0,
  "online": true,
  "activityId": null,
  "routeId": null,
  "articleId": "01a01fba-ec27-70cd-ba5d-649dac4449f6",
  "relatedTitle": "文章T016R",
  "title": "排卵期读物",
  "subtitle": null,
  "description": null,
  "note": null,
  "banner": {
    "id": "bound/it016r-b2.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it016r-b2.png?Expires=1787240539&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=D6%2Flzz896UfCdDU4AHmD7H43q4g%3D"
  },
  "createdAt": "2026-08-20T15:12:19.125235041Z",
  "updatedAt": "2026-08-20T15:12:19.125235041Z"
}
```

## Step 1g: LUTEAL 建 1 个下线条目；FOLLICULAR 不建条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"LUTEAL","type":"ACTIVITY","activityId":"01a01fba-eb90-7c61-be5b-55765484a4b9","description":"黄体期下线条目","banner":"images/it016r-b3.png","online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-ecf9-7d43-aa63-89df89daf3fe",
  "phase": "LUTEAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": false,
  "activityId": "01a01fba-eb90-7c61-be5b-55765484a4b9",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T016R",
  "title": null,
  "subtitle": null,
  "description": "黄体期下线条目",
  "note": null,
  "banner": {
    "id": "bound/it016r-b3.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it016r-b3.png?Expires=1787240539&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=3wMNQ4ImPQftG%2FT5APfp9b0yD8U%3D"
  },
  "createdAt": "2026-08-20T15:12:19.193772127Z",
  "updatedAt": "2026-08-20T15:12:19.193772127Z"
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
      "id": "01a01fba-ec70-7a16-82b8-041e4f83b3aa",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/it016r-b1.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it016r-b1.png?Expires=1787240539&OSSAccessKeyId=test-oss-ak&Signature=Bzf%2B%2BLsyY91RWtMHpKPj6r7Zhw4%3D"
      },
      "activityId": "01a01fba-eb90-7c61-be5b-55765484a4b9",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "经期活动",
      "note": null
    }
  ],
  "FOLLICULAR": [],
  "OVULATION": [
    {
      "id": "01a01fba-ecb5-74af-8fcb-2b5b7280fba2",
      "type": "ARTICLE",
      "banner": {
        "id": "bound/it016r-b2.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it016r-b2.png?Expires=1787240539&OSSAccessKeyId=test-oss-ak&Signature=PSZCuKaUiEJXNg1nspxeRG2JUjw%3D"
      },
      "activityId": null,
      "routeId": null,
      "articleId": "01a01fba-ec27-70cd-ba5d-649dac4449f6",
      "title": "排卵期读物",
      "subtitle": null,
      "description": null,
      "note": null
    }
  ],
  "LUTEAL": []
}
```
