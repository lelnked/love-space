# TC-route-IT-009 请求/响应存证

执行日期: 2026-09-04 ｜ change: route-spot-address ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key（本轮值 test-api-key，下文以 $APP_API_KEY 代指）。shell 中 export TOKEN=... APP_API_KEY=... 后 curl 可原样执行。
图片 objectKey: 按 file 域约定使用上传凭证前缀 `images/<uuid>.png`（test 档位上传凭证不可实跑，见 TC-file-IT-001；业务保存时由后端改写为 `bound/`，见 TC-file-IT-004），未臆造其他前缀。
共享前置（本轮一次创建，供 5 条用例复用）: 城市 A 01a06b0e-40ca-749f-940c-1fdb2558b7c6「地址城A-…」、城市 B 01a06b0e-40f5-7bed-9e7b-946c202d4e36「地址城B-…」、上线大使 01a06b0e-4121-7aa9-a4e7-339d17f8bc6d（含头像 + 2 标签）——完整请求/响应见 TC-route-IT-006/exchange.md「共享前置」节。
⚠️ 契约漂移（非本 change 引入）: api-spec.json `RouteUpsertRequest` 仍声明 `cityId`(uuid, required)，实现与 living spec（route/路线管理「所属地图自由输入」）为 `cityName`(string, required)；本轮请求按实现发 `cityName`。另 api-spec.json 路线各 operation 未声明 `responses`，响应侧 schema 无法核对，仅按用例预期与 `RouteSpot` schema（address: string, nullable）核对地点结构。

重测轮（admin 修复「更新时 cityName 被改写」后重启）：在城市 A 下新建路线 R6=01a06b12-743a-7733-809e-78a836749237（上一轮路线已被改写不复用）；用例定义中的 cityId 按实现口径映射为 cityName（路线已改为城市名自由文本，living spec 仍规定「创建后不可变」）

## Step 1: 前置：在城市 A 下创建路线（2 个地点，沿用共享前置大使；admin 修复重启后重建）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityName":"地址城A-061451","sortOrder":2,"title":"江畔一日线-重测-061451","ambassadorNote":"大使推荐语","thumbnail":"images/298ffd5a-04e4-4d9a-8898-5d94f2abffd9.png","images":["images/fe157845-2816-48f0-955f-5cc89b05aaec.png","images/0d817fa7-1e8e-4354-b414-9ad2d318ceb3.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a06b0e-4121-7aa9-a4e7-339d17f8bc6d","spots":[{"name":"S1 江畔步道","image":"images/aa7d12fe-cc1d-49cd-a9e7-91213ecad871.png","introduction":"清晨沿江散步"},{"name":"S2 咖啡小馆","image":"images/c34c6bd7-72f8-41d3-929d-48d3a8f35ed5.png","introduction":"午后咖啡歇脚"}]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b12-743a-7733-809e-78a836749237",
  "sortOrder": 2,
  "title": "江畔一日线-重测-061451",
  "cityName": "地址城A-061451",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/298ffd5a-04e4-4d9a-8898-5d94f2abffd9.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/298ffd5a-04e4-4d9a-8898-5d94f2abffd9.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=iX8VeJMBEUaVvJUVMGfLgCE01dE%3D"
  },
  "images": [
    {
      "id": "bound/fe157845-2816-48f0-955f-5cc89b05aaec.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/fe157845-2816-48f0-955f-5cc89b05aaec.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=R35Hu2gcDfHfnPME0ujGUYj7jLc%3D"
    },
    {
      "id": "bound/0d817fa7-1e8e-4354-b414-9ad2d318ceb3.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0d817fa7-1e8e-4354-b414-9ad2d318ceb3.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=hx1SL38%2Fv3gP5oOAs%2FLO81KKtPY%3D"
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
        "id": "bound/aa7d12fe-cc1d-49cd-a9e7-91213ecad871.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/aa7d12fe-cc1d-49cd-a9e7-91213ecad871.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=uPP%2B6PfGMZ%2Fsy%2BaSGYAKVOvbFiM%3D"
      },
      "introduction": "清晨沿江散步",
      "address": null
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/c34c6bd7-72f8-41d3-929d-48d3a8f35ed5.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c34c6bd7-72f8-41d3-929d-48d3a8f35ed5.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=F3UpawrSn3NrR%2BAPjw8ko%2F1I54c%3D"
      },
      "introduction": "午后咖啡歇脚",
      "address": null
    }
  ],
  "createdAt": "2026-09-04T06:19:26.637070022Z",
  "updatedAt": "2026-09-04T06:19:26.637070022Z"
}
```

## Step 2: PUT /api/admin/routes/{id} 改名、sortOrder=9、spots 改 1 个新地点、cityName 传城市 B

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/routes/01a06b12-743a-7733-809e-78a836749237" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityName":"地址城B-061451","sortOrder":9,"title":"江畔一日线-改-061451","ambassadorNote":"大使推荐语","thumbnail":"images/d9976147-446c-45ff-8a44-813f13bceb63.png","images":["images/161910c3-3c46-48cd-8821-2c8740f13f3f.png","images/40d92291-a4b3-4321-b227-6ca9f4b715bc.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a06b0e-4121-7aa9-a4e7-339d17f8bc6d","spots":[{"name":"S3 新地点","image":"images/9f67e16b-aa92-4fa1-aaaf-38b6f8799119.png","introduction":"新地点介绍"}]}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b12-743a-7733-809e-78a836749237",
  "sortOrder": 9,
  "title": "江畔一日线-改-061451",
  "cityName": "地址城A-061451",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/d9976147-446c-45ff-8a44-813f13bceb63.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d9976147-446c-45ff-8a44-813f13bceb63.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=MoksKbZ8Mrx7BgmgPeE9fDjEm0I%3D"
  },
  "images": [
    {
      "id": "bound/161910c3-3c46-48cd-8821-2c8740f13f3f.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/161910c3-3c46-48cd-8821-2c8740f13f3f.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=rDyTT2zhv5L0%2BiMFeL9fewKPGH0%3D"
    },
    {
      "id": "bound/40d92291-a4b3-4321-b227-6ca9f4b715bc.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/40d92291-a4b3-4321-b227-6ca9f4b715bc.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=KZ75N5hJlfDJ9%2FP%2F29QOD9rlapY%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a06b0e-4121-7aa9-a4e7-339d17f8bc6d",
  "ambassadorName": "地址大使-061451",
  "spots": [
    {
      "name": "S3 新地点",
      "image": {
        "id": "bound/9f67e16b-aa92-4fa1-aaaf-38b6f8799119.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/9f67e16b-aa92-4fa1-aaaf-38b6f8799119.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=HKv%2BjyQlWQ6b530p3IfxF3qv57w%3D"
      },
      "introduction": "新地点介绍",
      "address": null
    }
  ],
  "createdAt": "2026-09-04T06:19:26.63707Z",
  "updatedAt": "2026-09-04T06:19:26.63707Z"
}
```

## Step 3: GET /api/admin/routes/{id}

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/routes/01a06b12-743a-7733-809e-78a836749237" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b12-743a-7733-809e-78a836749237",
  "sortOrder": 9,
  "title": "江畔一日线-改-061451",
  "cityName": "地址城A-061451",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/d9976147-446c-45ff-8a44-813f13bceb63.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/d9976147-446c-45ff-8a44-813f13bceb63.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=MoksKbZ8Mrx7BgmgPeE9fDjEm0I%3D"
  },
  "images": [
    {
      "id": "bound/161910c3-3c46-48cd-8821-2c8740f13f3f.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/161910c3-3c46-48cd-8821-2c8740f13f3f.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=rDyTT2zhv5L0%2BiMFeL9fewKPGH0%3D"
    },
    {
      "id": "bound/40d92291-a4b3-4321-b227-6ca9f4b715bc.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/40d92291-a4b3-4321-b227-6ca9f4b715bc.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=KZ75N5hJlfDJ9%2FP%2F29QOD9rlapY%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a06b0e-4121-7aa9-a4e7-339d17f8bc6d",
  "ambassadorName": "地址大使-061451",
  "spots": [
    {
      "name": "S3 新地点",
      "image": {
        "id": "bound/9f67e16b-aa92-4fa1-aaaf-38b6f8799119.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/9f67e16b-aa92-4fa1-aaaf-38b6f8799119.png?Expires=1788504566&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=HKv%2BjyQlWQ6b530p3IfxF3qv57w%3D"
      },
      "introduction": "新地点介绍",
      "address": null
    }
  ],
  "createdAt": "2026-09-04T06:19:26.63707Z",
  "updatedAt": "2026-09-04T06:19:26.762939Z"
}
```
