# TC-featured-IT-018 GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

> 前置清理：删除历史遗留的周期推荐条目，使四分组初始为空。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"精选城018","englishName":"FeatCity018X","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-6fd5-7372-af0f-c06e3fe6505c",
  "chineseName": "精选城018",
  "englishName": "FeatCity018X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:11:47.1571224Z",
  "updatedAt": "2026-08-20T15:11:47.1571224Z"
}
```

## Step 1b: 创建 online=true 的大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/it018-avatar.png","name":"路线大使018","tags":["向导"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-7014-7a7b-8b32-993d0c7705cd",
  "avatar": {
    "id": "bound/it018-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it018-avatar.png?Expires=1787240507&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=brZaMje1bovOWx%2FDL5bMk3nIjF0%3D"
  },
  "name": "路线大使018",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:11:47.220587915Z",
  "updatedAt": "2026-08-20T15:11:47.220587915Z"
}
```

## Step 1c: 在该城市下创建路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fba-6fd5-7372-af0f-c06e3fe6505c","sortOrder":1,"title":"精选路线018","ambassadorNote":"语","thumbnail":"images/it018-thumb.png","images":["images/it018-img1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a01fba-7014-7a7b-8b32-993d0c7705cd","spots":[{"name":"S1","image":"images/it018-s1.png","introduction":"i1"}]}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-7051-7613-b1a0-724204d914e0",
  "cityId": "01a01fba-6fd5-7372-af0f-c06e3fe6505c",
  "sortOrder": 1,
  "title": "精选路线018",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/it018-thumb.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it018-thumb.png?Expires=1787240507&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=oYNBJ76jKQDQA%2BEU%2BRU1SSySHbw%3D"
  },
  "images": [
    {
      "id": "bound/it018-img1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it018-img1.png?Expires=1787240507&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VTESdMI%2BbCm4n5bZ1EXuEALoe8M%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a01fba-7014-7a7b-8b32-993d0c7705cd",
  "ambassadorName": "路线大使018",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/it018-s1.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it018-s1.png?Expires=1787240507&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=GRkIYOOL%2BCR0d2vzeXjtSWBxP0Y%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-20T15:11:47.281320231Z",
  "updatedAt": "2026-08-20T15:11:47.281320231Z"
}
```

## Step 1d: OVULATION 下建 1 个上线 ROUTE 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"OVULATION","type":"ROUTE","routeId":"01a01fba-7051-7613-b1a0-724204d914e0","title":"路线条目018","subtitle":"副标题","description":"路线推荐说明","banner":"images/it018-banner.png","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-7092-7dbd-82a2-da4d2c1690a0",
  "phase": "OVULATION",
  "type": "ROUTE",
  "sortOrder": 0,
  "online": true,
  "activityId": null,
  "routeId": "01a01fba-7051-7613-b1a0-724204d914e0",
  "articleId": null,
  "relatedTitle": "精选路线018",
  "title": "路线条目018",
  "subtitle": "副标题",
  "description": "路线推荐说明",
  "note": null,
  "banner": {
    "id": "bound/it018-banner.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it018-banner.png?Expires=1787240507&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=2OxxsGXSC1luI0Z9mggixI%2BargU%3D"
  },
  "createdAt": "2026-08-20T15:11:47.346768648Z",
  "updatedAt": "2026-08-20T15:11:47.346768648Z"
}
```

## Step 2: app 端查询，确认条目在 OVULATION 分组

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
      "id": "01a01fba-7092-7dbd-82a2-da4d2c1690a0",
      "type": "ROUTE",
      "banner": {
        "id": "bound/it018-banner.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it018-banner.png?Expires=1787240507&OSSAccessKeyId=test-oss-ak&Signature=4sjVLeNfdMz18Rm5s3F8OamtdxI%3D"
      },
      "activityId": null,
      "routeId": "01a01fba-7051-7613-b1a0-724204d914e0",
      "articleId": null,
      "title": "路线条目018",
      "subtitle": "副标题",
      "description": "路线推荐说明",
      "note": null
    }
  ],
  "LUTEAL": []
}
```

## Step 3a: 将该大使下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a01fba-7014-7a7b-8b32-993d0c7705cd/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-7014-7a7b-8b32-993d0c7705cd",
  "avatar": {
    "id": "bound/it018-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it018-avatar.png?Expires=1787240507&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=brZaMje1bovOWx%2FDL5bMk3nIjF0%3D"
  },
  "name": "路线大使018",
  "tags": [
    "向导"
  ],
  "online": false,
  "createdAt": "2026-08-20T15:11:47.220588Z",
  "updatedAt": "2026-08-20T15:11:47.220588Z"
}
```

## Step 3b: 大使下线后查询

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

## Step 4a: 恢复大使上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a01fba-7014-7a7b-8b32-993d0c7705cd/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fba-7014-7a7b-8b32-993d0c7705cd",
  "avatar": {
    "id": "bound/it018-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it018-avatar.png?Expires=1787240507&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=brZaMje1bovOWx%2FDL5bMk3nIjF0%3D"
  },
  "name": "路线大使018",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:11:47.220588Z",
  "updatedAt": "2026-08-20T15:11:47.451929Z"
}
```

## Step 4b: 恢复上线后查询

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
      "id": "01a01fba-7092-7dbd-82a2-da4d2c1690a0",
      "type": "ROUTE",
      "banner": {
        "id": "bound/it018-banner.png",
        "url": "https://love-space-test.oss-test.example.com/bound/it018-banner.png?Expires=1787240507&OSSAccessKeyId=test-oss-ak&Signature=4sjVLeNfdMz18Rm5s3F8OamtdxI%3D"
      },
      "activityId": null,
      "routeId": "01a01fba-7051-7613-b1a0-724204d914e0",
      "articleId": null,
      "title": "路线条目018",
      "subtitle": "副标题",
      "description": "路线推荐说明",
      "note": null
    }
  ],
  "LUTEAL": []
}
```
