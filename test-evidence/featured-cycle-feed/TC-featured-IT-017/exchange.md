# TC-featured-IT-017 GET /api/app/featured-cycle-items 关联实体不可见时条目不下发 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName": "周期城T017", "englishName": "CycleCityT017", "chineseProvince": "测试省", "englishProvince": "TP", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6b-babe-7c6d-b687-3d760fe34482",
  "chineseName": "周期城T017",
  "englishName": "CycleCityT017",
  "chineseProvince": "测试省",
  "englishProvince": "TP",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T13:45:48.990727854Z",
  "updatedAt": "2026-08-20T13:45:48.990727854Z"
}
```

## Step 2: 前置：MENSTRUAL 建上线 ACTIVITY 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "01a01f6b-bac5-7966-a0b4-8a37bd158d33", "description": "活动条目", "banner": "images/9c06bc24-a92a-46bf-8297-287bde5a6040.png", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6b-bad0-756f-b224-f5afc777684d",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": true,
  "activityId": "01a01f6b-bac5-7966-a0b4-8a37bd158d33",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T017",
  "title": null,
  "subtitle": null,
  "description": "活动条目",
  "note": null,
  "banner": {
    "id": "bound/9c06bc24-a92a-46bf-8297-287bde5a6040.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/9c06bc24-a92a-46bf-8297-287bde5a6040.png?Expires=1787235349&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=LKHkqh4DuCuC1ImfQSptA7ySxSk%3D"
  },
  "createdAt": "2026-08-20T13:45:49.008306315Z",
  "updatedAt": "2026-08-20T13:45:49.008306315Z"
}
```

## Step 3: 前置：MENSTRUAL 建上线 ARTICLE 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ARTICLE", "articleId": "01a01f6b-baca-7b18-a49d-a68e5ffd86be", "title": "文章条目", "banner": "images/e3b15bd8-3a8e-4a5c-b9c7-ef93f7fbbcbc.png", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6b-bad7-7022-99e2-3bcf5a4a90b8",
  "phase": "MENSTRUAL",
  "type": "ARTICLE",
  "sortOrder": 0,
  "online": true,
  "activityId": null,
  "routeId": null,
  "articleId": "01a01f6b-baca-7b18-a49d-a68e5ffd86be",
  "relatedTitle": "文章T017",
  "title": "文章条目",
  "subtitle": null,
  "description": null,
  "note": null,
  "banner": {
    "id": "bound/e3b15bd8-3a8e-4a5c-b9c7-ef93f7fbbcbc.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/e3b15bd8-3a8e-4a5c-b9c7-ef93f7fbbcbc.png?Expires=1787235349&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Aa32W0DnNWA%2BoKUdOJtbzsI4cXY%3D"
  },
  "createdAt": "2026-08-20T13:45:49.014969069Z",
  "updatedAt": "2026-08-20T13:45:49.014969069Z"
}
```

## Step 4: 步骤1：GET app 接口确认两条均在

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01f6b-bad7-7022-99e2-3bcf5a4a90b8",
      "type": "ARTICLE",
      "banner": {
        "id": "bound/e3b15bd8-3a8e-4a5c-b9c7-ef93f7fbbcbc.png",
        "url": "https://love-space-test.oss-test.example.com/bound/e3b15bd8-3a8e-4a5c-b9c7-ef93f7fbbcbc.png?Expires=1787235349&OSSAccessKeyId=test-oss-ak&Signature=KI4fA3n2RnZuUjTr%2BbT3dKaJfAA%3D"
      },
      "activityId": null,
      "routeId": null,
      "articleId": "01a01f6b-baca-7b18-a49d-a68e5ffd86be",
      "title": "文章条目",
      "subtitle": null,
      "description": null,
      "note": null
    },
    {
      "id": "01a01f6b-bad0-756f-b224-f5afc777684d",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/9c06bc24-a92a-46bf-8297-287bde5a6040.png",
        "url": "https://love-space-test.oss-test.example.com/bound/9c06bc24-a92a-46bf-8297-287bde5a6040.png?Expires=1787235349&OSSAccessKeyId=test-oss-ak&Signature=Ruxnzil4V4rdivL7ZUrDEuT1apM%3D"
      },
      "activityId": "01a01f6b-bac5-7966-a0b4-8a37bd158d33",
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

## Step 5: 步骤2：admin 将活动下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a01f6b-bac5-7966-a0b4-8a37bd158d33/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6b-bac5-7966-a0b4-8a37bd158d33",
  "cityId": "01a01f6b-babe-7c6d-b687-3d760fe34482",
  "images": [
    {
      "id": "bound/7ede7753-fa8e-416f-b169-99af7bf9999a.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/7ede7753-fa8e-416f-b169-99af7bf9999a.png?Expires=1787235349&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2BxjYenOKC1hBpWqm3D9RvQVqnuA%3D"
    }
  ],
  "title": "活动T017",
  "tags": [],
  "periods": [],
  "level": null,
  "introduction": null,
  "editorNote": null,
  "gatheringPlace": null,
  "dismissalPlace": null,
  "transportation": null,
  "visa": null,
  "itinerary": [],
  "detailHtml": null,
  "online": false,
  "createdAt": "2026-08-20T13:45:48.997545Z",
  "updatedAt": "2026-08-20T13:45:48.997545Z"
}
```

## Step 6: 步骤2：GET app 接口

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01f6b-bad7-7022-99e2-3bcf5a4a90b8",
      "type": "ARTICLE",
      "banner": {
        "id": "bound/e3b15bd8-3a8e-4a5c-b9c7-ef93f7fbbcbc.png",
        "url": "https://love-space-test.oss-test.example.com/bound/e3b15bd8-3a8e-4a5c-b9c7-ef93f7fbbcbc.png?Expires=1787235349&OSSAccessKeyId=test-oss-ak&Signature=KI4fA3n2RnZuUjTr%2BbT3dKaJfAA%3D"
      },
      "activityId": null,
      "routeId": null,
      "articleId": "01a01f6b-baca-7b18-a49d-a68e5ffd86be",
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

## Step 7: 步骤3：恢复活动上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a01f6b-bac5-7966-a0b4-8a37bd158d33/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6b-bac5-7966-a0b4-8a37bd158d33",
  "cityId": "01a01f6b-babe-7c6d-b687-3d760fe34482",
  "images": [
    {
      "id": "bound/7ede7753-fa8e-416f-b169-99af7bf9999a.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/7ede7753-fa8e-416f-b169-99af7bf9999a.png?Expires=1787235349&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2BxjYenOKC1hBpWqm3D9RvQVqnuA%3D"
    }
  ],
  "title": "活动T017",
  "tags": [],
  "periods": [],
  "level": null,
  "introduction": null,
  "editorNote": null,
  "gatheringPlace": null,
  "dismissalPlace": null,
  "transportation": null,
  "visa": null,
  "itinerary": [],
  "detailHtml": null,
  "online": true,
  "createdAt": "2026-08-20T13:45:48.997545Z",
  "updatedAt": "2026-08-20T13:45:49.044072Z"
}
```

## Step 8: 步骤3：将活动所属城市下架

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a01f6b-babe-7c6d-b687-3d760fe34482" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName": "周期城T017", "englishName": "CycleCityT017", "chineseProvince": "测试省", "englishProvince": "TP", "online": false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6b-babe-7c6d-b687-3d760fe34482",
  "chineseName": "周期城T017",
  "englishName": "CycleCityT017",
  "chineseProvince": "测试省",
  "englishProvince": "TP",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-20T13:45:48.990728Z",
  "updatedAt": "2026-08-20T13:45:48.990728Z"
}
```

## Step 9: 步骤3：GET app 接口

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01f6b-bad7-7022-99e2-3bcf5a4a90b8",
      "type": "ARTICLE",
      "banner": {
        "id": "bound/e3b15bd8-3a8e-4a5c-b9c7-ef93f7fbbcbc.png",
        "url": "https://love-space-test.oss-test.example.com/bound/e3b15bd8-3a8e-4a5c-b9c7-ef93f7fbbcbc.png?Expires=1787235349&OSSAccessKeyId=test-oss-ak&Signature=KI4fA3n2RnZuUjTr%2BbT3dKaJfAA%3D"
      },
      "activityId": null,
      "routeId": null,
      "articleId": "01a01f6b-baca-7b18-a49d-a68e5ffd86be",
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

## Step 10: 步骤4：恢复城市上架

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a01f6b-babe-7c6d-b687-3d760fe34482" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName": "周期城T017", "englishName": "CycleCityT017", "chineseProvince": "测试省", "englishProvince": "TP", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6b-babe-7c6d-b687-3d760fe34482",
  "chineseName": "周期城T017",
  "englishName": "CycleCityT017",
  "chineseProvince": "测试省",
  "englishProvince": "TP",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T13:45:48.990728Z",
  "updatedAt": "2026-08-20T13:45:49.093314Z"
}
```

## Step 11: 步骤4：将文章下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a01f6b-baca-7b18-a49d-a68e5ffd86be/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6b-baca-7b18-a49d-a68e5ffd86be",
  "image": {
    "id": "bound/cfc58884-9ecb-406a-8083-cd413b11428c.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/cfc58884-9ecb-406a-8083-cd413b11428c.png?Expires=1787235349&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=X3YobqLzD3FH6h3Rcue1gwKXwEc%3D"
  },
  "title": "文章T017",
  "subtitle": null,
  "contentHtml": null,
  "sortOrder": 0,
  "categoryIds": [],
  "online": false,
  "createdAt": "2026-08-20T13:45:49.002633Z",
  "updatedAt": "2026-08-20T13:45:49.002633Z"
}
```

## Step 12: 步骤4：GET app 接口

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01f6b-bad0-756f-b224-f5afc777684d",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/9c06bc24-a92a-46bf-8297-287bde5a6040.png",
        "url": "https://love-space-test.oss-test.example.com/bound/9c06bc24-a92a-46bf-8297-287bde5a6040.png?Expires=1787235349&OSSAccessKeyId=test-oss-ak&Signature=Ruxnzil4V4rdivL7ZUrDEuT1apM%3D"
      },
      "activityId": "01a01f6b-bac5-7966-a0b4-8a37bd158d33",
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

## Step 13: 步骤5：恢复文章上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a01f6b-baca-7b18-a49d-a68e5ffd86be/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6b-baca-7b18-a49d-a68e5ffd86be",
  "image": {
    "id": "bound/cfc58884-9ecb-406a-8083-cd413b11428c.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/cfc58884-9ecb-406a-8083-cd413b11428c.png?Expires=1787235349&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=X3YobqLzD3FH6h3Rcue1gwKXwEc%3D"
  },
  "title": "文章T017",
  "subtitle": null,
  "contentHtml": null,
  "sortOrder": 0,
  "categoryIds": [],
  "online": true,
  "createdAt": "2026-08-20T13:45:49.002633Z",
  "updatedAt": "2026-08-20T13:45:49.149723Z"
}
```

## Step 14: 步骤5：admin 删除该文章

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/articles/01a01f6b-baca-7b18-a49d-a68e5ffd86be" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: ）:

```json
null
```

## Step 15: 步骤5：GET app 接口

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01f6b-bad0-756f-b224-f5afc777684d",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/9c06bc24-a92a-46bf-8297-287bde5a6040.png",
        "url": "https://love-space-test.oss-test.example.com/bound/9c06bc24-a92a-46bf-8297-287bde5a6040.png?Expires=1787235349&OSSAccessKeyId=test-oss-ak&Signature=Ruxnzil4V4rdivL7ZUrDEuT1apM%3D"
      },
      "activityId": "01a01f6b-bac5-7966-a0b4-8a37bd158d33",
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

