# TC-featured-IT-019 GET /api/app/featured-cycle-items 组内按排序号升序 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 前置：MENSTRUAL 建上线条目 A（sortOrder=2）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2", "description": "条目A", "banner": "images/3bff6e9a-5b3c-4f4f-9a3b-416981c46e45.png", "sortOrder": 2, "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6c-2cd5-7f46-b617-8823233295a7",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 2,
  "online": true,
  "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019",
  "title": null,
  "subtitle": null,
  "description": "条目A",
  "note": null,
  "banner": {
    "id": "bound/3bff6e9a-5b3c-4f4f-9a3b-416981c46e45.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3bff6e9a-5b3c-4f4f-9a3b-416981c46e45.png?Expires=1787235378&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lLSnVhIVjgHkj8VJazgPZjTQl4U%3D"
  },
  "createdAt": "2026-08-20T13:46:18.197917168Z",
  "updatedAt": "2026-08-20T13:46:18.197917168Z"
}
```

## Step 2: 前置：MENSTRUAL 建上线条目 B（sortOrder=1）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2", "description": "条目B", "banner": "images/9b08bff8-745e-497b-b921-6a47f39ed4ad.png", "sortOrder": 1, "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6c-2cdc-7ba3-912e-d9e31016518c",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 1,
  "online": true,
  "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019",
  "title": null,
  "subtitle": null,
  "description": "条目B",
  "note": null,
  "banner": {
    "id": "bound/9b08bff8-745e-497b-b921-6a47f39ed4ad.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/9b08bff8-745e-497b-b921-6a47f39ed4ad.png?Expires=1787235378&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=2IgmU%2F5CZX1nr%2BW8cznmGA8JWt0%3D"
  },
  "createdAt": "2026-08-20T13:46:18.204697739Z",
  "updatedAt": "2026-08-20T13:46:18.204697739Z"
}
```

## Step 3: 前置：MENSTRUAL 建上线条目 C（sortOrder=3）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2", "description": "条目C", "banner": "images/0a30074d-ffa1-426f-8653-dca0deca9d08.png", "sortOrder": 3, "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6c-2ce3-739f-9975-a4a6427ff3ab",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 3,
  "online": true,
  "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019",
  "title": null,
  "subtitle": null,
  "description": "条目C",
  "note": null,
  "banner": {
    "id": "bound/0a30074d-ffa1-426f-8653-dca0deca9d08.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0a30074d-ffa1-426f-8653-dca0deca9d08.png?Expires=1787235378&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=0QdnD53hLf%2FGp%2BEAQBfLVKjattY%3D"
  },
  "createdAt": "2026-08-20T13:46:18.211186625Z",
  "updatedAt": "2026-08-20T13:46:18.211186625Z"
}
```

## Step 4: 前置：MENSTRUAL 建上线条目 D（sortOrder=1）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2", "description": "条目D", "banner": "images/0d54b1d1-ccde-4577-ba95-0f5d2814aa4c.png", "sortOrder": 1, "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6c-2cea-70d1-b351-d2934942aa51",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 1,
  "online": true,
  "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019",
  "title": null,
  "subtitle": null,
  "description": "条目D",
  "note": null,
  "banner": {
    "id": "bound/0d54b1d1-ccde-4577-ba95-0f5d2814aa4c.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0d54b1d1-ccde-4577-ba95-0f5d2814aa4c.png?Expires=1787235378&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NIJD6u1wcTV1RR87nruo%2Fkxxeck%3D"
  },
  "createdAt": "2026-08-20T13:46:18.218012956Z",
  "updatedAt": "2026-08-20T13:46:18.218012956Z"
}
```

## Step 5: 前置：MENSTRUAL 建上线条目 E（sortOrder=1）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2", "description": "条目E", "banner": "images/d65e9b59-4781-4703-89f5-e2ecb50ce638.png", "sortOrder": 1, "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6c-2cef-797c-a470-84ea69d779d0",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 1,
  "online": true,
  "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T019",
  "title": null,
  "subtitle": null,
  "description": "条目E",
  "note": null,
  "banner": {
    "id": "bound/d65e9b59-4781-4703-89f5-e2ecb50ce638.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d65e9b59-4781-4703-89f5-e2ecb50ce638.png?Expires=1787235378&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=aF%2B5yaongf4f0K48ZLNmR9Fmj6c%3D"
  },
  "createdAt": "2026-08-20T13:46:18.223564041Z",
  "updatedAt": "2026-08-20T13:46:18.223564041Z"
}
```

## Step 6: GET /api/app/featured-cycle-items

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "MENSTRUAL": [
    {
      "id": "01a01f6c-2cef-797c-a470-84ea69d779d0",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/d65e9b59-4781-4703-89f5-e2ecb50ce638.png",
        "url": "https://love-space-test.oss-test.example.com/bound/d65e9b59-4781-4703-89f5-e2ecb50ce638.png?Expires=1787235378&OSSAccessKeyId=test-oss-ak&Signature=W2kHX%2FTePVN%2BghGPAihpewEouc8%3D"
      },
      "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "条目E",
      "note": null
    },
    {
      "id": "01a01f6c-2cea-70d1-b351-d2934942aa51",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/0d54b1d1-ccde-4577-ba95-0f5d2814aa4c.png",
        "url": "https://love-space-test.oss-test.example.com/bound/0d54b1d1-ccde-4577-ba95-0f5d2814aa4c.png?Expires=1787235378&OSSAccessKeyId=test-oss-ak&Signature=VqX8%2FlmsFnAHFL1QLesQou0XTZY%3D"
      },
      "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "条目D",
      "note": null
    },
    {
      "id": "01a01f6c-2cdc-7ba3-912e-d9e31016518c",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/9b08bff8-745e-497b-b921-6a47f39ed4ad.png",
        "url": "https://love-space-test.oss-test.example.com/bound/9b08bff8-745e-497b-b921-6a47f39ed4ad.png?Expires=1787235378&OSSAccessKeyId=test-oss-ak&Signature=M1mFlZNykrJCy1t%2BAZ7ydTswD2k%3D"
      },
      "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "条目B",
      "note": null
    },
    {
      "id": "01a01f6c-2cd5-7f46-b617-8823233295a7",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/3bff6e9a-5b3c-4f4f-9a3b-416981c46e45.png",
        "url": "https://love-space-test.oss-test.example.com/bound/3bff6e9a-5b3c-4f4f-9a3b-416981c46e45.png?Expires=1787235378&OSSAccessKeyId=test-oss-ak&Signature=SFNE3Zw7DkDIVm8NvEta7YWbpMQ%3D"
      },
      "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "条目A",
      "note": null
    },
    {
      "id": "01a01f6c-2ce3-739f-9975-a4a6427ff3ab",
      "type": "ACTIVITY",
      "banner": {
        "id": "bound/0a30074d-ffa1-426f-8653-dca0deca9d08.png",
        "url": "https://love-space-test.oss-test.example.com/bound/0a30074d-ffa1-426f-8653-dca0deca9d08.png?Expires=1787235378&OSSAccessKeyId=test-oss-ak&Signature=ojuhT%2BxoagfW1fi6nMAPBHURjLg%3D"
      },
      "activityId": "01a01f6c-2cce-7249-bc5c-082116a400c2",
      "routeId": null,
      "articleId": null,
      "title": null,
      "subtitle": null,
      "description": "条目C",
      "note": null
    }
  ],
  "FOLLICULAR": [],
  "OVULATION": [],
  "LUTEAL": []
}
```

