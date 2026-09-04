# TC-activity-IT-009 请求/响应存证

GET /api/app/activities/{id} 详情返回富文本且 img src 为签名 URL

执行日期: 2026-09-04 ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，`export TOKEN=<登录返回 token>` 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key（测试 fixture，明文入存证）

## Step 1: 前置：POST /api/admin/activities（online=true，detailHtml 含图与文本）

```bash
curl -s -i -X POST http://localhost:21423/api/admin/activities -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"images": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"], "title": "app活动09-af4f49", "tags": ["富文本"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "交通", "visa": "签证", "itinerary": [{"title": "Day1", "content": "出发"}], "detailHtml": "<p>app详情段落</p><img src=\"images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0901.png\">", "online": true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-1689-743f-8eca-82d15ddc3bf4",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "app活动09-af4f49",
  "subtitle": null,
  "tags": [
    "富文本"
  ],
  "periods": [
    "FOLLICULAR"
  ],
  "level": "L1",
  "introduction": "简介",
  "editorNote": "编辑说",
  "gatheringPlace": "集合地",
  "dismissalPlace": "解散地",
  "transportation": "交通",
  "visa": "签证",
  "landscape": null,
  "itinerary": [
    {
      "title": "Day1",
      "content": "出发"
    }
  ],
  "detailHtml": "<p>app详情段落</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0901.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=PXbxuHUEgh82HPGeNBon2FjjcLI%3D\">",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.105210466Z",
  "updatedAt": "2026-09-04T07:02:44.105210466Z"
}
```

## Step 2: GET http://localhost:8081/api/app/activities/01a06b3a-1689-743f-8eca-82d15ddc3bf4

```bash
curl -s -i -X GET http://localhost:8081/api/app/activities/01a06b3a-1689-743f-8eca-82d15ddc3bf4 -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-1689-743f-8eca-82d15ddc3bf4",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "https://test.oss-cn-test.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=x&Signature=EXgeP3K%2BWuFXE36nKt6h5OmzFxQ%3D"
    }
  ],
  "title": "app活动09-af4f49",
  "subtitle": null,
  "tags": [
    "富文本"
  ],
  "periods": [
    "FOLLICULAR"
  ],
  "level": "L1",
  "introduction": "简介",
  "editorNote": "编辑说",
  "gatheringPlace": "集合地",
  "dismissalPlace": "解散地",
  "transportation": "交通",
  "visa": "签证",
  "landscape": null,
  "itinerary": [
    {
      "title": "Day1",
      "content": "出发"
    }
  ],
  "detailHtml": "<p>app详情段落</p><img src=\"https://test.oss-cn-test.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0901.png?Expires=1788507164&OSSAccessKeyId=x&Signature=htxhlCEoITyoX1OqzCl%2FF94%2BzTg%3D\">"
}
```
