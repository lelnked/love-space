# TC-city-IT-006 城市下架后 app 端活动不可见（级联），路线不受影响 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"级联城006","englishName":"CascadeCity006X","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb6-42de-70db-a7eb-0803a7fa3c40",
  "chineseName": "级联城006",
  "englishName": "CascadeCity006X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:07:13.50197623Z",
  "updatedAt": "2026-08-20T15:07:13.50197623Z"
}
```

## Step 1b: 创建上线活动

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb6-42de-70db-a7eb-0803a7fa3c40","images":["images/it006-a1.png"],"title":"级联活动006","tags":["露营"],"periods":["FOLLICULAR"],"level":"L2","introduction":"介绍","editorNote":"编辑寄语","gatheringPlace":"集合点","dismissalPlace":"解散点","transportation":"大巴","visa":"无需签证","itinerary":[{"title":"Day1","content":"集合"}],"detailHtml":"<p>详情</p>","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb6-4325-7811-9c58-5b49585c0d35",
  "cityId": "01a01fb6-42de-70db-a7eb-0803a7fa3c40",
  "images": [
    {
      "id": "bound/it006-a1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it006-a1.png?Expires=1787240233&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=0hkVQlLcaJ5b1PITbnVyKG%2FjMJM%3D"
    }
  ],
  "title": "级联活动006",
  "tags": [
    "露营"
  ],
  "periods": [
    "FOLLICULAR"
  ],
  "level": "L2",
  "introduction": "介绍",
  "editorNote": "编辑寄语",
  "gatheringPlace": "集合点",
  "dismissalPlace": "解散点",
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
  "createdAt": "2026-08-20T15:07:13.571917061Z",
  "updatedAt": "2026-08-20T15:07:13.571917061Z"
}
```

## Step 1c: 创建 online=true 的大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/it006-avatar.png","name":"路线大使006","tags":["向导"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb6-4365-7158-b54c-e656e1d4724e",
  "avatar": {
    "id": "bound/it006-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it006-avatar.png?Expires=1787240233&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=xFRSPS4K%2BqpRwUM6GbiNYr%2BpQ7E%3D"
  },
  "name": "路线大使006",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:07:13.636996419Z",
  "updatedAt": "2026-08-20T15:07:13.636996419Z"
}
```

## Step 1d: 在该城市下创建可见路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb6-42de-70db-a7eb-0803a7fa3c40","sortOrder":1,"title":"级联路线006","ambassadorNote":"语","thumbnail":"images/it006-thumb.png","images":["images/it006-img1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a01fb6-4365-7158-b54c-e656e1d4724e","spots":[{"name":"S1","image":"images/it006-s1.png","introduction":"i1"}]}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb6-43a1-748f-acdf-94f063ac81a1",
  "cityId": "01a01fb6-42de-70db-a7eb-0803a7fa3c40",
  "sortOrder": 1,
  "title": "级联路线006",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/it006-thumb.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it006-thumb.png?Expires=1787240233&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=vU5GPqPwLzhdR6H6YYti3aJhBnU%3D"
  },
  "images": [
    {
      "id": "bound/it006-img1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it006-img1.png?Expires=1787240233&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=SfBn8IqEcZyCl6w7I6IbR0%2FL5MY%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a01fb6-4365-7158-b54c-e656e1d4724e",
  "ambassadorName": "路线大使006",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/it006-s1.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it006-s1.png?Expires=1787240233&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DfqQwuMFXzwbxYiNToE0FSTHjis%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-20T15:07:13.697211722Z",
  "updatedAt": "2026-08-20T15:07:13.697211722Z"
}
```

## Step 1e: 前置确认 app 活动列表能查到

```bash
curl -s -i -X GET "http://localhost:8081/api/app/activities?cityId=01a01fb6-42de-70db-a7eb-0803a7fa3c40" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a01fb6-4325-7811-9c58-5b49585c0d35",
    "title": "级联活动006",
    "images": [
      {
        "id": "bound/it006-a1.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it006-a1.png?Expires=1787240233&OSSAccessKeyId=test-oss-ak&Signature=Gbg89GG7QJ64C%2BNadCimudF2zck%3D"
      }
    ],
    "tags": [
      "露营"
    ],
    "periods": [
      "FOLLICULAR"
    ],
    "level": "L2",
    "introduction": "介绍"
  }
]
```

## Step 1f: 前置确认 app 路线列表能查到

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a01fb6-42de-70db-a7eb-0803a7fa3c40" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a01fb6-43a1-748f-acdf-94f063ac81a1",
    "title": "级联路线006",
    "thumbnail": {
      "id": "bound/it006-thumb.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it006-thumb.png?Expires=1787240233&OSSAccessKeyId=test-oss-ak&Signature=wuzTex7dwtv%2Bpdu9HIeKNi%2BCCtY%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "路线大使006"
  }
]
```

## Step 2: admin 侧将该城市下架

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a01fb6-42de-70db-a7eb-0803a7fa3c40/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb6-42de-70db-a7eb-0803a7fa3c40",
  "chineseName": "级联城006",
  "englishName": "CascadeCity006X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-20T15:07:13.501976Z",
  "updatedAt": "2026-08-20T15:07:13.501976Z"
}
```

## Step 3: 下架后 app 活动列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/activities?cityId=01a01fb6-42de-70db-a7eb-0803a7fa3c40" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[]
```

## Step 4: 下架后 app 活动详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/activities/01a01fb6-4325-7811-9c58-5b49585c0d35" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 404，Content-Type: application/json）:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "activity not found: 01a01fb6-4325-7811-9c58-5b49585c0d35",
  "path": "/api/app/activities/01a01fb6-4325-7811-9c58-5b49585c0d35"
}
```
