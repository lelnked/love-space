# TC-route-IT-014 请求/响应存证

执行日期: 2026-09-04 ｜ change: route-spot-address ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key（本轮值 test-api-key，下文以 $APP_API_KEY 代指）。shell 中 export TOKEN=... APP_API_KEY=... 后 curl 可原样执行。
图片 objectKey: 按 file 域约定使用上传凭证前缀 `images/<uuid>.png`（test 档位上传凭证不可实跑，见 TC-file-IT-001；业务保存时由后端改写为 `bound/`，见 TC-file-IT-004），未臆造其他前缀。
共享前置（本轮一次创建，供 5 条用例复用）: 城市 A 01a06b0e-40ca-749f-940c-1fdb2558b7c6「地址城A-…」、城市 B 01a06b0e-40f5-7bed-9e7b-946c202d4e36「地址城B-…」、上线大使 01a06b0e-4121-7aa9-a4e7-339d17f8bc6d（含头像 + 2 标签）——完整请求/响应见 TC-route-IT-006/exchange.md「共享前置」节。
⚠️ 契约漂移（非本 change 引入）: api-spec.json `RouteUpsertRequest` 仍声明 `cityId`(uuid, required)，实现与 living spec（route/路线管理「所属地图自由输入」）为 `cityName`(string, required)；本轮请求按实现发 `cityName`。另 api-spec.json 路线各 operation 未声明 `responses`，响应侧 schema 无法核对，仅按用例预期与 `RouteSpot` schema（address: string, nullable）核对地点结构。

复用 TC-route-IT-029 创建的路线 R=01a06b0e-424e-7895-8e9f-950fa0fc718b，大使含头像/名称/2 标签

## Step 1: 前置：可见路线 R（IT-029 创建，2 spots，大使含头像/名称/标签）— admin 详情核对

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/routes/01a06b0e-424e-7895-8e9f-950fa0fc718b" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b0e-424e-7895-8e9f-950fa0fc718b",
  "sortOrder": 2,
  "title": "App地址路线-061451",
  "cityName": "地址城A-061451",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/4164642a-0e7a-4370-a202-47aaa664d329.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/4164642a-0e7a-4370-a202-47aaa664d329.png?Expires=1788504292&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=E%2BmH9ieuZE39TsEOn3Y1D4CVFDI%3D"
  },
  "images": [
    {
      "id": "bound/c892cb44-b42e-49bc-a06e-94494eddd2d1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c892cb44-b42e-49bc-a06e-94494eddd2d1.png?Expires=1788504292&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ggfC50bH29tNb%2FwR9k2%2B0BVhN2g%3D"
    },
    {
      "id": "bound/1e343a88-61e5-4a29-983f-6e6065ba74b3.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/1e343a88-61e5-4a29-983f-6e6065ba74b3.png?Expires=1788504292&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=phbPI0vAhZ7CzssAR%2FrRhugkSkU%3D"
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
        "id": "bound/0e34b7e2-35ce-421d-b7d9-82cbce97b894.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0e34b7e2-35ce-421d-b7d9-82cbce97b894.png?Expires=1788504292&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=J5%2FR%2FUG%2Fp55V8uHkn2P7mY8ptig%3D"
      },
      "introduction": "S1 介绍",
      "address": "成都市青羊区宽窄巷子"
    },
    {
      "name": "S2 无址",
      "image": {
        "id": "bound/f923f89c-f819-440a-b4b5-c8d31fe26e5a.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/f923f89c-f819-440a-b4b5-c8d31fe26e5a.png?Expires=1788504292&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=t8g4Rxl%2FeotPqzTwu7Z8B5ALkSA%3D"
      },
      "introduction": "S2 介绍",
      "address": null
    }
  ],
  "createdAt": "2026-09-04T06:14:51.72648Z",
  "updatedAt": "2026-09-04T06:14:51.72648Z"
}
```

## Step 2: GET app /api/app/routes/{id}

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a06b0e-424e-7895-8e9f-950fa0fc718b" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "cityName": "地址城A-061451",
  "sortOrder": 2,
  "title": "App地址路线-061451",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/4164642a-0e7a-4370-a202-47aaa664d329.png",
    "url": "http://test.oss-cn-test.aliyuncs.com/bound/4164642a-0e7a-4370-a202-47aaa664d329.png?Expires=1788504292&OSSAccessKeyId=x&Signature=e3vWzpECFz7koE4hreKCKlDSsk8%3D"
  },
  "images": [
    {
      "id": "bound/c892cb44-b42e-49bc-a06e-94494eddd2d1.png",
      "url": "http://test.oss-cn-test.aliyuncs.com/bound/c892cb44-b42e-49bc-a06e-94494eddd2d1.png?Expires=1788504292&OSSAccessKeyId=x&Signature=8BAB7M8p%2Fo7RlyGwYMQSn%2BeZfIk%3D"
    },
    {
      "id": "bound/1e343a88-61e5-4a29-983f-6e6065ba74b3.png",
      "url": "http://test.oss-cn-test.aliyuncs.com/bound/1e343a88-61e5-4a29-983f-6e6065ba74b3.png?Expires=1788504292&OSSAccessKeyId=x&Signature=QX3fo9g0bnkrHyNzaHBW4%2FxFC78%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassador": {
    "id": "01a06b0e-4121-7aa9-a4e7-339d17f8bc6d",
    "name": "地址大使-061451",
    "avatar": {
      "id": "bound/e1b58e2b-a440-4f0a-99f2-148555c98e36.png",
      "url": "http://test.oss-cn-test.aliyuncs.com/bound/e1b58e2b-a440-4f0a-99f2-148555c98e36.png?Expires=1788504292&OSSAccessKeyId=x&Signature=B5cPZ2I59o4l2RrczX88DnwgPSI%3D"
    },
    "tags": [
      "向导",
      "咖啡"
    ]
  },
  "spots": [
    {
      "name": "S1 宽窄巷子",
      "image": {
        "id": "bound/0e34b7e2-35ce-421d-b7d9-82cbce97b894.png",
        "url": "http://test.oss-cn-test.aliyuncs.com/bound/0e34b7e2-35ce-421d-b7d9-82cbce97b894.png?Expires=1788504292&OSSAccessKeyId=x&Signature=4EH%2FX0Xvnx88CBN0PLZcYOlglEI%3D"
      },
      "introduction": "S1 介绍",
      "address": "成都市青羊区宽窄巷子"
    },
    {
      "name": "S2 无址",
      "image": {
        "id": "bound/f923f89c-f819-440a-b4b5-c8d31fe26e5a.png",
        "url": "http://test.oss-cn-test.aliyuncs.com/bound/f923f89c-f819-440a-b4b5-c8d31fe26e5a.png?Expires=1788504292&OSSAccessKeyId=x&Signature=oSzMFqthSKDJ6RBF24ZnHIeDSAg%3D"
      },
      "introduction": "S2 介绍",
      "address": null
    }
  ],
  "city": {
    "id": "01a06b0e-40ca-749f-940c-1fdb2558b7c6",
    "name": "地址城A-061451"
  },
  "createdAt": "2026-09-04T06:14:51.72648Z",
  "updatedAt": "2026-09-04T06:14:51.72648Z"
}
```
