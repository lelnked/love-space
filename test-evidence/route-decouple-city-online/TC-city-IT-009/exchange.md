# TC-city-IT-009 DELETE /api/admin/cities/{id} 城市下存在路线时拒绝删除 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1a: 创建城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"待删城009","englishName":"DelCity009X","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb5-7729-784e-a42d-c718c64ce655",
  "chineseName": "待删城009",
  "englishName": "DelCity009X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:06:21.353449995Z",
  "updatedAt": "2026-08-20T15:06:21.353449995Z"
}
```

## Step 1b: 创建大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/it009-avatar.png","name":"路线大使009","tags":["向导"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb5-7764-748f-9a4a-81beeb58b53f",
  "avatar": {
    "id": "bound/it009-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it009-avatar.png?Expires=1787240181&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=yNvTy9S%2BMwVmAD2XBj3r7jOTHnU%3D"
  },
  "name": "路线大使009",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:06:21.412200633Z",
  "updatedAt": "2026-08-20T15:06:21.412200633Z"
}
```

## Step 1c: 在该城市下创建 1 条路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb5-7729-784e-a42d-c718c64ce655","sortOrder":1,"title":"占位路线009","ambassadorNote":"大使推荐语","thumbnail":"images/it009-thumb.png","images":["images/it009-img1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a01fb5-7764-748f-9a4a-81beeb58b53f","spots":[{"name":"S1","image":"images/it009-s1.png","introduction":"i1"}]}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb5-77a2-7512-87b1-1dda01ba1f0e",
  "cityId": "01a01fb5-7729-784e-a42d-c718c64ce655",
  "sortOrder": 1,
  "title": "占位路线009",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/it009-thumb.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it009-thumb.png?Expires=1787240181&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VhLV3btCbJ5T5cEoJu1ONQCs6Jc%3D"
  },
  "images": [
    {
      "id": "bound/it009-img1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it009-img1.png?Expires=1787240181&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=WY%2Fi1k2Sv%2BgMMdxXoJVrAbm3W8w%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a01fb5-7764-748f-9a4a-81beeb58b53f",
  "ambassadorName": "路线大使009",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/it009-s1.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it009-s1.png?Expires=1787240181&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=GFzHk3kU5vgMQQP169xOmRShcq4%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-20T15:06:21.474232312Z",
  "updatedAt": "2026-08-20T15:06:21.474232312Z"
}
```

## Step 2: 删除该城市（其下仍有路线）

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/cities/01a01fb5-7729-784e-a42d-c718c64ce655" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "该城市下仍有路线，请先删除这些路线后再删除城市：01a01fb5-7729-784e-a42d-c718c64ce655",
  "path": "/api/admin/cities/01a01fb5-7729-784e-a42d-c718c64ce655"
}
```

## Step 3: 查询城市详情确认未被删除

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/cities/01a01fb5-7729-784e-a42d-c718c64ce655" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb5-7729-784e-a42d-c718c64ce655",
  "chineseName": "待删城009",
  "englishName": "DelCity009X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:06:21.35345Z",
  "updatedAt": "2026-08-20T15:06:21.35345Z"
}
```
