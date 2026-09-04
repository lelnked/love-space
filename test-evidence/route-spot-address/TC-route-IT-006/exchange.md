# TC-route-IT-006 请求/响应存证

执行日期: 2026-09-04 ｜ change: route-spot-address ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key（本轮值 test-api-key，下文以 $APP_API_KEY 代指）。shell 中 export TOKEN=... APP_API_KEY=... 后 curl 可原样执行。
图片 objectKey: 按 file 域约定使用上传凭证前缀 `images/<uuid>.png`（test 档位上传凭证不可实跑，见 TC-file-IT-001；业务保存时由后端改写为 `bound/`，见 TC-file-IT-004），未臆造其他前缀。
共享前置（本轮一次创建，供 5 条用例复用）: 城市 A 01a06b0e-40ca-749f-940c-1fdb2558b7c6「地址城A-…」、城市 B 01a06b0e-40f5-7bed-9e7b-946c202d4e36「地址城B-…」、上线大使 01a06b0e-4121-7aa9-a4e7-339d17f8bc6d（含头像 + 2 标签）——完整请求/响应见 TC-route-IT-006/exchange.md「共享前置」节。
⚠️ 契约漂移（非本 change 引入）: api-spec.json `RouteUpsertRequest` 仍声明 `cityId`(uuid, required)，实现与 living spec（route/路线管理「所属地图自由输入」）为 `cityName`(string, required)；本轮请求按实现发 `cityName`。另 api-spec.json 路线各 operation 未声明 `responses`，响应侧 schema 无法核对，仅按用例预期与 `RouteSpot` schema（address: string, nullable）核对地点结构。

路线 R6=01a06b0e-4161-74db-8925-9071e94a2614（供 TC-route-IT-009 复用）

## 共享前置 1: 创建上架城市 A

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"地址城A-061451","englishName":"AddrCityA061451","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b0e-40ca-749f-940c-1fdb2558b7c6",
  "chineseName": "地址城A-061451",
  "englishName": "AddrCityA061451",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-09-04T06:14:51.338215525Z",
  "updatedAt": "2026-09-04T06:14:51.338215525Z"
}
```

## 共享前置 2: 创建上架城市 B

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"地址城B-061451","englishName":"AddrCityB061451","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b0e-40f5-7bed-9e7b-946c202d4e36",
  "chineseName": "地址城B-061451",
  "englishName": "AddrCityB061451",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-09-04T06:14:51.381686203Z",
  "updatedAt": "2026-09-04T06:14:51.381686203Z"
}
```

## 共享前置 3: 创建上线大使（含头像/标签）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/e1b58e2b-a440-4f0a-99f2-148555c98e36.png","name":"地址大使-061451","tags":["向导","咖啡"],"online":true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b0e-4121-7aa9-a4e7-339d17f8bc6d",
  "avatar": {
    "id": "bound/e1b58e2b-a440-4f0a-99f2-148555c98e36.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/e1b58e2b-a440-4f0a-99f2-148555c98e36.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=oKQMWp0CJLneiwlHQ3lbhywVXc4%3D"
  },
  "name": "地址大使-061451",
  "tags": [
    "向导",
    "咖啡"
  ],
  "weight": 0,
  "online": true,
  "createdAt": "2026-09-04T06:14:51.425599927Z",
  "updatedAt": "2026-09-04T06:14:51.425599927Z"
}
```

## Step 1: POST /api/admin/routes 全字段，spots S1→S2（不带 address）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityName":"地址城A-061451","sortOrder":2,"title":"江畔一日线-061451","ambassadorNote":"大使推荐语","thumbnail":"images/0f119330-2446-49c4-ab87-685def9edfeb.png","images":["images/4a24e3aa-d8d7-4575-b0ba-9d0133f07a09.png","images/badfb38d-c8ee-4d85-8661-f53d4d0b01d2.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a06b0e-4121-7aa9-a4e7-339d17f8bc6d","spots":[{"name":"S1 江畔步道","image":"images/e9594d00-30cf-4ef2-94c9-281d8037312e.png","introduction":"清晨沿江散步"},{"name":"S2 咖啡小馆","image":"images/9b7976c8-acda-4745-bcae-ec556b457878.png","introduction":"午后咖啡歇脚"}]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b0e-4161-74db-8925-9071e94a2614",
  "sortOrder": 2,
  "title": "江畔一日线-061451",
  "cityName": "地址城A-061451",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/0f119330-2446-49c4-ab87-685def9edfeb.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0f119330-2446-49c4-ab87-685def9edfeb.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ow1hBeI9DE%2BysLTBruQL7yPq8zo%3D"
  },
  "images": [
    {
      "id": "bound/4a24e3aa-d8d7-4575-b0ba-9d0133f07a09.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/4a24e3aa-d8d7-4575-b0ba-9d0133f07a09.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cdmZ2%2FdvqZI%2F9Av0j7fyQCUeItI%3D"
    },
    {
      "id": "bound/badfb38d-c8ee-4d85-8661-f53d4d0b01d2.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/badfb38d-c8ee-4d85-8661-f53d4d0b01d2.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=gU6%2BpokMgHJCj4ZmfuluHlB%2B8Ek%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a06b0e-4121-7aa9-a4e7-339d17f8bc6d",
  "ambassadorName": "地址大使-061451",
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/e9594d00-30cf-4ef2-94c9-281d8037312e.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/e9594d00-30cf-4ef2-94c9-281d8037312e.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=rtWB1Uv%2Bo7EU7F9o5sSoJ6qOFCk%3D"
      },
      "introduction": "清晨沿江散步",
      "address": null
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/9b7976c8-acda-4745-bcae-ec556b457878.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/9b7976c8-acda-4745-bcae-ec556b457878.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=IQs7sIEQFV69jB%2BopSO%2F8C006zE%3D"
      },
      "introduction": "午后咖啡歇脚",
      "address": null
    }
  ],
  "createdAt": "2026-09-04T06:14:51.487917764Z",
  "updatedAt": "2026-09-04T06:14:51.487917764Z"
}
```

## Step 2: GET /api/admin/routes/{id}

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/routes/01a06b0e-4161-74db-8925-9071e94a2614" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b0e-4161-74db-8925-9071e94a2614",
  "sortOrder": 2,
  "title": "江畔一日线-061451",
  "cityName": "地址城A-061451",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/0f119330-2446-49c4-ab87-685def9edfeb.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0f119330-2446-49c4-ab87-685def9edfeb.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ow1hBeI9DE%2BysLTBruQL7yPq8zo%3D"
  },
  "images": [
    {
      "id": "bound/4a24e3aa-d8d7-4575-b0ba-9d0133f07a09.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/4a24e3aa-d8d7-4575-b0ba-9d0133f07a09.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cdmZ2%2FdvqZI%2F9Av0j7fyQCUeItI%3D"
    },
    {
      "id": "bound/badfb38d-c8ee-4d85-8661-f53d4d0b01d2.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/badfb38d-c8ee-4d85-8661-f53d4d0b01d2.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=gU6%2BpokMgHJCj4ZmfuluHlB%2B8Ek%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a06b0e-4121-7aa9-a4e7-339d17f8bc6d",
  "ambassadorName": "地址大使-061451",
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/e9594d00-30cf-4ef2-94c9-281d8037312e.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/e9594d00-30cf-4ef2-94c9-281d8037312e.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=rtWB1Uv%2Bo7EU7F9o5sSoJ6qOFCk%3D"
      },
      "introduction": "清晨沿江散步",
      "address": null
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/9b7976c8-acda-4745-bcae-ec556b457878.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/9b7976c8-acda-4745-bcae-ec556b457878.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=IQs7sIEQFV69jB%2BopSO%2F8C006zE%3D"
      },
      "introduction": "午后咖啡歇脚",
      "address": null
    }
  ],
  "createdAt": "2026-09-04T06:14:51.487918Z",
  "updatedAt": "2026-09-04T06:14:51.487918Z"
}
```
