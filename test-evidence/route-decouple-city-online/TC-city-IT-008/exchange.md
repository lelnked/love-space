# TC-city-IT-008 城市下架后 app 端路线仍可见（不再级联） — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"级联城008","englishName":"CascadeCity008X","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb5-00cd-7dc9-8a69-815cf788c796",
  "chineseName": "级联城008",
  "englishName": "CascadeCity008X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:05:51.053787034Z",
  "updatedAt": "2026-08-20T15:05:51.053787034Z"
}
```

## Step 1b: 创建 online=true 的大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/it008c-avatar.png","name":"路线大使c008","tags":["向导"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb5-0109-78f1-9e9e-785f3abdc23b",
  "avatar": {
    "id": "bound/it008c-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008c-avatar.png?Expires=1787240151&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qezlNtgXt%2FFFNENx3Rby5GPW10g%3D"
  },
  "name": "路线大使c008",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:05:51.113486362Z",
  "updatedAt": "2026-08-20T15:05:51.113486362Z"
}
```

## Step 1c: 在该城市下创建路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb5-00cd-7dc9-8a69-815cf788c796","sortOrder":1,"title":"级联路线c008","ambassadorNote":"大使推荐语","thumbnail":"images/it008c-thumb.png","images":["images/it008c-img1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a01fb5-0109-78f1-9e9e-785f3abdc23b","spots":[{"name":"S1","image":"images/it008c-s1.png","introduction":"i1"}]}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb5-0148-73b3-b985-8f7a5a58bce6",
  "cityId": "01a01fb5-00cd-7dc9-8a69-815cf788c796",
  "sortOrder": 1,
  "title": "级联路线c008",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/it008c-thumb.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008c-thumb.png?Expires=1787240151&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=EeRz%2FPAYYm9mpDIXJqafkdA%2BSX0%3D"
  },
  "images": [
    {
      "id": "bound/it008c-img1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008c-img1.png?Expires=1787240151&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=FW5W1elpB06ly0hrR14%2FERLNgM4%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a01fb5-0109-78f1-9e9e-785f3abdc23b",
  "ambassadorName": "路线大使c008",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/it008c-s1.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008c-s1.png?Expires=1787240151&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=V%2FZx0k6NiKDzX9ofSzOyX%2BWtOQg%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-20T15:05:51.176162321Z",
  "updatedAt": "2026-08-20T15:05:51.176162321Z"
}
```

## Step 1d: 城市上架时 app 列表能查到该路线（前置确认）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a01fb5-00cd-7dc9-8a69-815cf788c796" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a01fb5-0148-73b3-b985-8f7a5a58bce6",
    "title": "级联路线c008",
    "thumbnail": {
      "id": "bound/it008c-thumb.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it008c-thumb.png?Expires=1787240151&OSSAccessKeyId=test-oss-ak&Signature=34IXm0zSpIJYbYUhEihpbj8JSMQ%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "路线大使c008"
  }
]
```

## Step 2: admin 侧将该城市下架

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a01fb5-00cd-7dc9-8a69-815cf788c796/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb5-00cd-7dc9-8a69-815cf788c796",
  "chineseName": "级联城008",
  "englishName": "CascadeCity008X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-20T15:05:51.053787Z",
  "updatedAt": "2026-08-20T15:05:51.053787Z"
}
```

## Step 3: 城市下架后 app 路线列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a01fb5-00cd-7dc9-8a69-815cf788c796" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a01fb5-0148-73b3-b985-8f7a5a58bce6",
    "title": "级联路线c008",
    "thumbnail": {
      "id": "bound/it008c-thumb.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it008c-thumb.png?Expires=1787240151&OSSAccessKeyId=test-oss-ak&Signature=34IXm0zSpIJYbYUhEihpbj8JSMQ%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "路线大使c008"
  }
]
```

## Step 4: 城市下架后 app 路线详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a01fb5-0148-73b3-b985-8f7a5a58bce6" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb5-0148-73b3-b985-8f7a5a58bce6",
  "cityId": "01a01fb5-00cd-7dc9-8a69-815cf788c796",
  "cityName": "级联城008",
  "sortOrder": 1,
  "title": "级联路线c008",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/it008c-thumb.png",
    "url": "https://love-space-test.oss-test.example.com/bound/it008c-thumb.png?Expires=1787240151&OSSAccessKeyId=test-oss-ak&Signature=34IXm0zSpIJYbYUhEihpbj8JSMQ%3D"
  },
  "images": [
    {
      "id": "bound/it008c-img1.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it008c-img1.png?Expires=1787240151&OSSAccessKeyId=test-oss-ak&Signature=BGucRQy4wZSJcaDiIzpjrHSFeec%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassador": {
    "name": "路线大使c008",
    "avatar": {
      "id": "bound/it008c-avatar.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it008c-avatar.png?Expires=1787240151&OSSAccessKeyId=test-oss-ak&Signature=WEAHEcJGkm%2B2pFLZ%2Fno7yuBWrNE%3D"
    },
    "tags": [
      "向导"
    ]
  },
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/it008c-s1.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it008c-s1.png?Expires=1787240151&OSSAccessKeyId=test-oss-ak&Signature=zGZ%2BFwm0Bjb5Mqr6w1ulJtK2UWI%3D"
      },
      "introduction": "i1"
    }
  ]
}
```

## Step 5a: 将该大使下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a01fb5-0109-78f1-9e9e-785f3abdc23b/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb5-0109-78f1-9e9e-785f3abdc23b",
  "avatar": {
    "id": "bound/it008c-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008c-avatar.png?Expires=1787240151&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qezlNtgXt%2FFFNENx3Rby5GPW10g%3D"
  },
  "name": "路线大使c008",
  "tags": [
    "向导"
  ],
  "online": false,
  "createdAt": "2026-08-20T15:05:51.113486Z",
  "updatedAt": "2026-08-20T15:05:51.113486Z"
}
```

## Step 5b: 大使下线后 app 路线列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a01fb5-00cd-7dc9-8a69-815cf788c796" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[]
```

## Step 5c: 大使下线后 app 路线详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a01fb5-0148-73b3-b985-8f7a5a58bce6" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 404，Content-Type: application/json）:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "route not found: 01a01fb5-0148-73b3-b985-8f7a5a58bce6",
  "path": "/api/app/routes/01a01fb5-0148-73b3-b985-8f7a5a58bce6"
}
```
