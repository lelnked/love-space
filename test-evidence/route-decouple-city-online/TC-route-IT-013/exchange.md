# TC-route-IT-013 GET /api/app/routes 大使下线后路线隐藏、详情 404 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"隐藏城013","englishName":"HideCity013X","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-5fa4-7219-89af-797b0a2dcca4",
  "chineseName": "隐藏城013",
  "englishName": "HideCity013X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:08:26.404052571Z",
  "updatedAt": "2026-08-20T15:08:26.404052571Z"
}
```

## Step 1b: 创建 online=true 的大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/it013-avatar.png","name":"路线大使013","tags":["向导"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-5fdf-7802-b431-2e3ad8f68427",
  "avatar": {
    "id": "bound/it013-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it013-avatar.png?Expires=1787240306&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=a%2ByBtLb14bO5Lw2YyeYZGZsQXL0%3D"
  },
  "name": "路线大使013",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:08:26.46341991Z",
  "updatedAt": "2026-08-20T15:08:26.46341991Z"
}
```

## Step 1c: 创建路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb7-5fa4-7219-89af-797b0a2dcca4","sortOrder":1,"title":"隐藏路线013","ambassadorNote":"语","thumbnail":"images/it013-thumb.png","images":["images/it013-img1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a01fb7-5fdf-7802-b431-2e3ad8f68427","spots":[{"name":"S1","image":"images/it013-s1.png","introduction":"i1"}]}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-601a-7d3a-8968-2b4110987c47",
  "cityId": "01a01fb7-5fa4-7219-89af-797b0a2dcca4",
  "sortOrder": 1,
  "title": "隐藏路线013",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/it013-thumb.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it013-thumb.png?Expires=1787240306&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=2dxydAN4nsh7%2BldWPQ%2FekLfVtmU%3D"
  },
  "images": [
    {
      "id": "bound/it013-img1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it013-img1.png?Expires=1787240306&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=A5niwTM2MmBamEWSQTsK1zcyIsY%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a01fb7-5fdf-7802-b431-2e3ad8f68427",
  "ambassadorName": "路线大使013",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/it013-s1.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it013-s1.png?Expires=1787240306&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=QNtjkg11Pa1HWIE9pGafs1j6Yxw%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-20T15:08:26.52276375Z",
  "updatedAt": "2026-08-20T15:08:26.52276375Z"
}
```

## Step 1d: 前置确认 app 列表能查到该路线

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a01fb7-5fa4-7219-89af-797b0a2dcca4" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a01fb7-601a-7d3a-8968-2b4110987c47",
    "title": "隐藏路线013",
    "thumbnail": {
      "id": "bound/it013-thumb.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it013-thumb.png?Expires=1787240306&OSSAccessKeyId=test-oss-ak&Signature=T3SjVEj%2Fk5YXqzaVdP9LMyHrm%2Bc%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "路线大使013"
  }
]
```

## Step 2: admin 侧将关联大使下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a01fb7-5fdf-7802-b431-2e3ad8f68427/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-5fdf-7802-b431-2e3ad8f68427",
  "avatar": {
    "id": "bound/it013-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it013-avatar.png?Expires=1787240306&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=a%2ByBtLb14bO5Lw2YyeYZGZsQXL0%3D"
  },
  "name": "路线大使013",
  "tags": [
    "向导"
  ],
  "online": false,
  "createdAt": "2026-08-20T15:08:26.46342Z",
  "updatedAt": "2026-08-20T15:08:26.46342Z"
}
```

## Step 3: 下线后 app 路线列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a01fb7-5fa4-7219-89af-797b0a2dcca4" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[]
```

## Step 4: 下线后 app 路线详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a01fb7-601a-7d3a-8968-2b4110987c47" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 404，Content-Type: application/json）:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "route not found: 01a01fb7-601a-7d3a-8968-2b4110987c47",
  "path": "/api/app/routes/01a01fb7-601a-7d3a-8968-2b4110987c47"
}
```
