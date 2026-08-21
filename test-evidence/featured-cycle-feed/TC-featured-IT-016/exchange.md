# TC-featured-IT-016 GET /api/app/featured-cycle-items 四周期分组齐全且只含上线条目 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 前置：MENSTRUAL 建 1 个上线 ACTIVITY 条目（活动上线、城市上架）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "01a01f6b-51fc-72d2-bdd2-878755212665", "description": "经期活动", "banner": "images/58e61a27-ccab-41a6-b147-915f725e3c99.png", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6b-520a-76bf-b99a-95cc884a7d90",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": true,
  "activityId": "01a01f6b-51fc-72d2-bdd2-878755212665",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T016",
  "title": null,
  "subtitle": null,
  "description": "经期活动",
  "note": null,
  "banner": {
    "id": "bound/58e61a27-ccab-41a6-b147-915f725e3c99.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/58e61a27-ccab-41a6-b147-915f725e3c99.png?Expires=1787235322&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=gKOKjHfulQjpWkPFIP6ZGWbR7Qg%3D"
  },
  "createdAt": "2026-08-20T13:45:22.18637217Z",
  "updatedAt": "2026-08-20T13:45:22.18637217Z"
}
```

## Step 2: 前置：OVULATION 建 1 个上线 ARTICLE 条目（文章上线）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "OVULATION", "type": "ARTICLE", "articleId": "01a01f6b-5203-79fd-b344-1180a4fc1237", "title": "排卵期读物", "banner": "images/b640b388-763a-4df7-8e04-a11ec6c995ff.png", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6b-5211-7bcc-95e5-65d24c20a35c",
  "phase": "OVULATION",
  "type": "ARTICLE",
  "sortOrder": 0,
  "online": true,
  "activityId": null,
  "routeId": null,
  "articleId": "01a01f6b-5203-79fd-b344-1180a4fc1237",
  "relatedTitle": "文章T016",
  "title": "排卵期读物",
  "subtitle": null,
  "description": null,
  "note": null,
  "banner": {
    "id": "bound/b640b388-763a-4df7-8e04-a11ec6c995ff.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b640b388-763a-4df7-8e04-a11ec6c995ff.png?Expires=1787235322&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=x2JmX7gIZ1ZpcmDuslkdOpoiJck%3D"
  },
  "createdAt": "2026-08-20T13:45:22.193689263Z",
  "updatedAt": "2026-08-20T13:45:22.193689263Z"
}
```

## Step 3: 前置：LUTEAL 建 1 个下线条目；FOLLICULAR 不建条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "LUTEAL", "type": "ACTIVITY", "activityId": "01a01f6b-51fc-72d2-bdd2-878755212665", "description": "黄体期下线条目", "banner": "images/22fec578-20b6-4bbe-ab7d-1a9436be21a8.png", "online": false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6b-5218-7f94-a163-bfea7bb0fec9",
  "phase": "LUTEAL",
  "type": "ACTIVITY",
  "sortOrder": 0,
  "online": false,
  "activityId": "01a01f6b-51fc-72d2-bdd2-878755212665",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T016",
  "title": null,
  "subtitle": null,
  "description": "黄体期下线条目",
  "note": null,
  "banner": {
    "id": "bound/22fec578-20b6-4bbe-ab7d-1a9436be21a8.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/22fec578-20b6-4bbe-ab7d-1a9436be21a8.png?Expires=1787235322&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=liU%2BPFiV%2F2w1CUJnJeHHq0KeDgE%3D"
  },
  "createdAt": "2026-08-20T13:45:22.200925204Z",
  "updatedAt": "2026-08-20T13:45:22.200925204Z"
}
```

## Step 4: GET /api/app/featured-cycle-items（X-API-Key）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01f6b-520a-76bf-b99a-95cc884a7d90",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/58e61a27-ccab-41a6-b147-915f725e3c99.png",
        "url": "https://love-space-test.oss-test.example.com/bound/58e61a27-ccab-41a6-b147-915f725e3c99.png?Expires=1787235322&OSSAccessKeyId=test-oss-ak&Signature=ETY9NeQdEjitEYdZmH5DTppU%2B%2FA%3D"
      },
      "activityId": "01a01f6b-51fc-72d2-bdd2-878755212665",
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
      "id": "01a01f6b-5211-7bcc-95e5-65d24c20a35c",
      "type": "ARTICLE",
      "banner": {
        "id": "bound/b640b388-763a-4df7-8e04-a11ec6c995ff.png",
        "url": "https://love-space-test.oss-test.example.com/bound/b640b388-763a-4df7-8e04-a11ec6c995ff.png?Expires=1787235322&OSSAccessKeyId=test-oss-ak&Signature=yejZsblHvOoGdBspPWkSuLVp1WU%3D"
      },
      "activityId": null,
      "routeId": null,
      "articleId": "01a01f6b-5203-79fd-b344-1180a4fc1237",
      "title": "排卵期读物",
      "subtitle": null,
      "description": null,
      "note": null
    }
  ],
  "LUTEAL": []
}
```

