# TC-featured-IT-018 GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName": "周期城T018", "englishName": "CycleCityT018", "chineseProvince": "测试省", "englishProvince": "TP", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6c-2c45-7a68-9d8f-1b42673973e1",
  "chineseName": "周期城T018",
  "englishName": "CycleCityT018",
  "chineseProvince": "测试省",
  "englishProvince": "TP",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T13:46:18.05359455Z",
  "updatedAt": "2026-08-20T13:46:18.05359455Z"
}
```

## Step 2: 前置：创建 online=true 的爱女大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar": "images/e2869474-1c2c-43d2-8916-aca5354f45b9.png", "name": "大使T018", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6c-2c4a-7f3a-9ac2-fe3756d587e0",
  "avatar": {
    "id": "bound/e2869474-1c2c-43d2-8916-aca5354f45b9.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/e2869474-1c2c-43d2-8916-aca5354f45b9.png?Expires=1787235378&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=xftIU00lgY6%2BXpucNgR5367QXbo%3D"
  },
  "name": "大使T018",
  "tags": [],
  "online": true,
  "createdAt": "2026-08-20T13:46:18.058884661Z",
  "updatedAt": "2026-08-20T13:46:18.058884661Z"
}
```

## Step 3: 前置：该城市下创建路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId": "01a01f6c-2c45-7a68-9d8f-1b42673973e1", "title": "路线T018", "thumbnail": "images/17903c52-960e-47de-92cc-49ba1dd9be36.png", "images": ["images/f760f8a8-db4b-4647-b834-03af9c02a85a.png"], "ambassadorId": "01a01f6c-2c4a-7f3a-9ac2-fe3756d587e0"}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6c-2c51-7879-b58c-88d2e09bacfd",
  "cityId": "01a01f6c-2c45-7a68-9d8f-1b42673973e1",
  "sortOrder": 0,
  "title": "路线T018",
  "ambassadorNote": null,
  "thumbnail": {
    "id": "bound/17903c52-960e-47de-92cc-49ba1dd9be36.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/17903c52-960e-47de-92cc-49ba1dd9be36.png?Expires=1787235378&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cXrO971yI03%2FnGbFiOKm%2FITxyKg%3D"
  },
  "images": [
    {
      "id": "bound/f760f8a8-db4b-4647-b834-03af9c02a85a.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/f760f8a8-db4b-4647-b834-03af9c02a85a.png?Expires=1787235378&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2B4mveHKmFDRd1TL5RN%2FRZBId5LA%3D"
    }
  ],
  "travelTime": null,
  "season": null,
  "travelStatus": null,
  "ambassadorId": "01a01f6c-2c4a-7f3a-9ac2-fe3756d587e0",
  "ambassadorName": "大使T018",
  "spots": [],
  "createdAt": "2026-08-20T13:46:18.065487545Z",
  "updatedAt": "2026-08-20T13:46:18.065487545Z"
}
```

## Step 4: 前置：OVULATION 建上线 ROUTE 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "OVULATION", "type": "ROUTE", "routeId": "01a01f6c-2c51-7879-b58c-88d2e09bacfd", "title": "排卵期路线", "subtitle": "三天两夜", "description": "说明", "banner": "images/2ac4e2a1-aed6-4d2c-b9df-72cded933f6f.png", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6c-2c59-76dd-babd-44a72b1155ca",
  "phase": "OVULATION",
  "type": "ROUTE",
  "sortOrder": 0,
  "online": true,
  "activityId": null,
  "routeId": "01a01f6c-2c51-7879-b58c-88d2e09bacfd",
  "articleId": null,
  "relatedTitle": "路线T018",
  "title": "排卵期路线",
  "subtitle": "三天两夜",
  "description": "说明",
  "note": null,
  "banner": {
    "id": "bound/2ac4e2a1-aed6-4d2c-b9df-72cded933f6f.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/2ac4e2a1-aed6-4d2c-b9df-72cded933f6f.png?Expires=1787235378&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=gLME5hkqfbWAx9dzCz4QHbwEtf8%3D"
  },
  "createdAt": "2026-08-20T13:46:18.073363725Z",
  "updatedAt": "2026-08-20T13:46:18.073363725Z"
}
```

## Step 5: 步骤2：GET app 接口

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
      "id": "01a01f6c-2c59-76dd-babd-44a72b1155ca",
      "type": "ROUTE",
      "banner": {
        "id": "bound/2ac4e2a1-aed6-4d2c-b9df-72cded933f6f.png",
        "url": "https://love-space-test.oss-test.example.com/bound/2ac4e2a1-aed6-4d2c-b9df-72cded933f6f.png?Expires=1787235378&OSSAccessKeyId=test-oss-ak&Signature=JfLAjysapyh%2BxbpaPpih%2FeAhbDo%3D"
      },
      "activityId": null,
      "routeId": "01a01f6c-2c51-7879-b58c-88d2e09bacfd",
      "articleId": null,
      "title": "排卵期路线",
      "subtitle": "三天两夜",
      "description": "说明",
      "note": null
    }
  ],
  "LUTEAL": []
}
```

## Step 6: 步骤3：admin 将大使下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a01f6c-2c4a-7f3a-9ac2-fe3756d587e0/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6c-2c4a-7f3a-9ac2-fe3756d587e0",
  "avatar": {
    "id": "bound/e2869474-1c2c-43d2-8916-aca5354f45b9.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/e2869474-1c2c-43d2-8916-aca5354f45b9.png?Expires=1787235378&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=xftIU00lgY6%2BXpucNgR5367QXbo%3D"
  },
  "name": "大使T018",
  "tags": [],
  "online": false,
  "createdAt": "2026-08-20T13:46:18.058885Z",
  "updatedAt": "2026-08-20T13:46:18.058885Z"
}
```

## Step 7: 步骤3：GET app 接口

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [],
  "FOLLICULAR": [],
  "OVULATION": [],
  "LUTEAL": []
}
```

## Step 8: 步骤4：恢复大使上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a01f6c-2c4a-7f3a-9ac2-fe3756d587e0/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6c-2c4a-7f3a-9ac2-fe3756d587e0",
  "avatar": {
    "id": "bound/e2869474-1c2c-43d2-8916-aca5354f45b9.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/e2869474-1c2c-43d2-8916-aca5354f45b9.png?Expires=1787235378&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=xftIU00lgY6%2BXpucNgR5367QXbo%3D"
  },
  "name": "大使T018",
  "tags": [],
  "online": true,
  "createdAt": "2026-08-20T13:46:18.058885Z",
  "updatedAt": "2026-08-20T13:46:18.094573Z"
}
```

## Step 9: 步骤4：将路线所属城市下架

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a01f6c-2c45-7a68-9d8f-1b42673973e1" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName": "周期城T018", "englishName": "CycleCityT018", "chineseProvince": "测试省", "englishProvince": "TP", "online": false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6c-2c45-7a68-9d8f-1b42673973e1",
  "chineseName": "周期城T018",
  "englishName": "CycleCityT018",
  "chineseProvince": "测试省",
  "englishProvince": "TP",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-20T13:46:18.053595Z",
  "updatedAt": "2026-08-20T13:46:18.053595Z"
}
```

## Step 10: 步骤4：GET app 接口

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [],
  "FOLLICULAR": [],
  "OVULATION": [],
  "LUTEAL": []
}
```

