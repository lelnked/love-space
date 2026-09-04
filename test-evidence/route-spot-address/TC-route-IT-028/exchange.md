# TC-route-IT-028 请求/响应存证

执行日期: 2026-09-04 ｜ change: route-spot-address ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key（本轮值 test-api-key，下文以 $APP_API_KEY 代指）。shell 中 export TOKEN=... APP_API_KEY=... 后 curl 可原样执行。
图片 objectKey: 按 file 域约定使用上传凭证前缀 `images/<uuid>.png`（test 档位上传凭证不可实跑，见 TC-file-IT-001；业务保存时由后端改写为 `bound/`，见 TC-file-IT-004），未臆造其他前缀。
共享前置（本轮一次创建，供 5 条用例复用）: 城市 A 01a06b0e-40ca-749f-940c-1fdb2558b7c6「地址城A-…」、城市 B 01a06b0e-40f5-7bed-9e7b-946c202d4e36「地址城B-…」、上线大使 01a06b0e-4121-7aa9-a4e7-339d17f8bc6d（含头像 + 2 标签）——完整请求/响应见 TC-route-IT-006/exchange.md「共享前置」节。
⚠️ 契约漂移（非本 change 引入）: api-spec.json `RouteUpsertRequest` 仍声明 `cityId`(uuid, required)，实现与 living spec（route/路线管理「所属地图自由输入」）为 `cityName`(string, required)；本轮请求按实现发 `cityName`。另 api-spec.json 路线各 operation 未声明 `responses`，响应侧 schema 无法核对，仅按用例预期与 `RouteSpot` schema（address: string, nullable）核对地点结构。

路线 R28=01a06b0e-41e8-7eab-9e19-4f4bd28d4a94

## Step 1: POST /api/admin/routes：S1 带 address，S2 不带 address key

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityName":"地址城A-061451","sortOrder":2,"title":"地址路线-061451","ambassadorNote":"大使推荐语","thumbnail":"images/65b1dcb8-8150-4887-960c-ce57fd2ddd9e.png","images":["images/43925487-671f-40b9-8753-1d0fc6f8fb15.png","images/3c70579a-9636-43dc-8bd0-6310704799a8.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a06b0e-4121-7aa9-a4e7-339d17f8bc6d","spots":[{"name":"S1 宽窄巷子","image":"images/62155226-d70c-4d81-ab2d-e1cab76e3dbd.png","introduction":"S1 介绍","address":"成都市青羊区宽窄巷子"},{"name":"S2 无址","image":"images/d8eebc57-1f51-451c-8371-a984af355f33.png","introduction":"S2 介绍"}]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b0e-41e8-7eab-9e19-4f4bd28d4a94",
  "sortOrder": 2,
  "title": "地址路线-061451",
  "cityName": "地址城A-061451",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/65b1dcb8-8150-4887-960c-ce57fd2ddd9e.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/65b1dcb8-8150-4887-960c-ce57fd2ddd9e.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=O0Q8Kbsq1rVdGYfE3%2Fc1UplNxH0%3D"
  },
  "images": [
    {
      "id": "bound/43925487-671f-40b9-8753-1d0fc6f8fb15.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/43925487-671f-40b9-8753-1d0fc6f8fb15.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=nVUTW7Pt%2FJHALpnZ2SQIgXAkV2c%3D"
    },
    {
      "id": "bound/3c70579a-9636-43dc-8bd0-6310704799a8.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3c70579a-9636-43dc-8bd0-6310704799a8.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=JVkGgara%2B7oMLS06IIRlxr5VAvY%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a06b0e-4121-7aa9-a4e7-339d17f8bc6d",
  "ambassadorName": "地址大使-061451",
  "spots": [
    {
      "name": "S1 宽窄巷子",
      "image": {
        "id": "bound/62155226-d70c-4d81-ab2d-e1cab76e3dbd.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/62155226-d70c-4d81-ab2d-e1cab76e3dbd.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NcDciVWBFv9litX0OwlXUDk%2BZ%2F0%3D"
      },
      "introduction": "S1 介绍",
      "address": "成都市青羊区宽窄巷子"
    },
    {
      "name": "S2 无址",
      "image": {
        "id": "bound/d8eebc57-1f51-451c-8371-a984af355f33.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d8eebc57-1f51-451c-8371-a984af355f33.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Vjunz%2BmuZzT1DFSUqBPKJCETPes%3D"
      },
      "introduction": "S2 介绍",
      "address": null
    }
  ],
  "createdAt": "2026-09-04T06:14:51.62484512Z",
  "updatedAt": "2026-09-04T06:14:51.62484512Z"
}
```

## Step 2: GET /api/admin/routes/{id}

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/routes/01a06b0e-41e8-7eab-9e19-4f4bd28d4a94" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b0e-41e8-7eab-9e19-4f4bd28d4a94",
  "sortOrder": 2,
  "title": "地址路线-061451",
  "cityName": "地址城A-061451",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/65b1dcb8-8150-4887-960c-ce57fd2ddd9e.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/65b1dcb8-8150-4887-960c-ce57fd2ddd9e.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=O0Q8Kbsq1rVdGYfE3%2Fc1UplNxH0%3D"
  },
  "images": [
    {
      "id": "bound/43925487-671f-40b9-8753-1d0fc6f8fb15.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/43925487-671f-40b9-8753-1d0fc6f8fb15.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=nVUTW7Pt%2FJHALpnZ2SQIgXAkV2c%3D"
    },
    {
      "id": "bound/3c70579a-9636-43dc-8bd0-6310704799a8.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3c70579a-9636-43dc-8bd0-6310704799a8.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=JVkGgara%2B7oMLS06IIRlxr5VAvY%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a06b0e-4121-7aa9-a4e7-339d17f8bc6d",
  "ambassadorName": "地址大使-061451",
  "spots": [
    {
      "name": "S1 宽窄巷子",
      "image": {
        "id": "bound/62155226-d70c-4d81-ab2d-e1cab76e3dbd.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/62155226-d70c-4d81-ab2d-e1cab76e3dbd.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NcDciVWBFv9litX0OwlXUDk%2BZ%2F0%3D"
      },
      "introduction": "S1 介绍",
      "address": "成都市青羊区宽窄巷子"
    },
    {
      "name": "S2 无址",
      "image": {
        "id": "bound/d8eebc57-1f51-451c-8371-a984af355f33.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d8eebc57-1f51-451c-8371-a984af355f33.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Vjunz%2BmuZzT1DFSUqBPKJCETPes%3D"
      },
      "introduction": "S2 介绍",
      "address": null
    }
  ],
  "createdAt": "2026-09-04T06:14:51.624845Z",
  "updatedAt": "2026-09-04T06:14:51.624845Z"
}
```

## Step 3: PUT /api/admin/routes/{id}：S1 address 改春熙路，S2 显式 address:null

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/routes/01a06b0e-41e8-7eab-9e19-4f4bd28d4a94" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityName":"地址城A-061451","sortOrder":2,"title":"地址路线-061451","ambassadorNote":"大使推荐语","thumbnail":"images/fa48f5ad-5cc4-499f-b9b2-5c60dd293005.png","images":["images/d66818b5-d21b-4ad2-8978-8b4121c183f2.png","images/262f02cc-521c-46f8-8b0b-6ddb7b286b22.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a06b0e-4121-7aa9-a4e7-339d17f8bc6d","spots":[{"name":"S1 宽窄巷子","image":"images/62155226-d70c-4d81-ab2d-e1cab76e3dbd.png","introduction":"S1 介绍","address":"成都市锦江区春熙路"},{"name":"S2 无址","image":"images/d8eebc57-1f51-451c-8371-a984af355f33.png","introduction":"S2 介绍","address":null}]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b0e-41e8-7eab-9e19-4f4bd28d4a94",
  "sortOrder": 2,
  "title": "地址路线-061451",
  "cityName": "地址城A-061451",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/fa48f5ad-5cc4-499f-b9b2-5c60dd293005.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/fa48f5ad-5cc4-499f-b9b2-5c60dd293005.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=s4EGWeFp1X%2B0r68jGWPfXBjMzb4%3D"
  },
  "images": [
    {
      "id": "bound/d66818b5-d21b-4ad2-8978-8b4121c183f2.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d66818b5-d21b-4ad2-8978-8b4121c183f2.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=nKtyUGJBRaZzR3HNSE5oEArfSSE%3D"
    },
    {
      "id": "bound/262f02cc-521c-46f8-8b0b-6ddb7b286b22.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/262f02cc-521c-46f8-8b0b-6ddb7b286b22.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=i8pienxLDkakTiTM47enAXzpEZg%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a06b0e-4121-7aa9-a4e7-339d17f8bc6d",
  "ambassadorName": "地址大使-061451",
  "spots": [
    {
      "name": "S1 宽窄巷子",
      "image": {
        "id": "bound/62155226-d70c-4d81-ab2d-e1cab76e3dbd.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/62155226-d70c-4d81-ab2d-e1cab76e3dbd.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NcDciVWBFv9litX0OwlXUDk%2BZ%2F0%3D"
      },
      "introduction": "S1 介绍",
      "address": "成都市锦江区春熙路"
    },
    {
      "name": "S2 无址",
      "image": {
        "id": "bound/d8eebc57-1f51-451c-8371-a984af355f33.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d8eebc57-1f51-451c-8371-a984af355f33.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Vjunz%2BmuZzT1DFSUqBPKJCETPes%3D"
      },
      "introduction": "S2 介绍",
      "address": null
    }
  ],
  "createdAt": "2026-09-04T06:14:51.624845Z",
  "updatedAt": "2026-09-04T06:14:51.624845Z"
}
```

## Step 4: GET /api/admin/routes/{id}

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/routes/01a06b0e-41e8-7eab-9e19-4f4bd28d4a94" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b0e-41e8-7eab-9e19-4f4bd28d4a94",
  "sortOrder": 2,
  "title": "地址路线-061451",
  "cityName": "地址城A-061451",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/fa48f5ad-5cc4-499f-b9b2-5c60dd293005.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/fa48f5ad-5cc4-499f-b9b2-5c60dd293005.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=s4EGWeFp1X%2B0r68jGWPfXBjMzb4%3D"
  },
  "images": [
    {
      "id": "bound/d66818b5-d21b-4ad2-8978-8b4121c183f2.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d66818b5-d21b-4ad2-8978-8b4121c183f2.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=nKtyUGJBRaZzR3HNSE5oEArfSSE%3D"
    },
    {
      "id": "bound/262f02cc-521c-46f8-8b0b-6ddb7b286b22.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/262f02cc-521c-46f8-8b0b-6ddb7b286b22.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=i8pienxLDkakTiTM47enAXzpEZg%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a06b0e-4121-7aa9-a4e7-339d17f8bc6d",
  "ambassadorName": "地址大使-061451",
  "spots": [
    {
      "name": "S1 宽窄巷子",
      "image": {
        "id": "bound/62155226-d70c-4d81-ab2d-e1cab76e3dbd.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/62155226-d70c-4d81-ab2d-e1cab76e3dbd.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NcDciVWBFv9litX0OwlXUDk%2BZ%2F0%3D"
      },
      "introduction": "S1 介绍",
      "address": "成都市锦江区春熙路"
    },
    {
      "name": "S2 无址",
      "image": {
        "id": "bound/d8eebc57-1f51-451c-8371-a984af355f33.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d8eebc57-1f51-451c-8371-a984af355f33.png?Expires=1788504291&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Vjunz%2BmuZzT1DFSUqBPKJCETPes%3D"
      },
      "introduction": "S2 介绍",
      "address": null
    }
  ],
  "createdAt": "2026-09-04T06:14:51.624845Z",
  "updatedAt": "2026-09-04T06:14:51.691677Z"
}
```
