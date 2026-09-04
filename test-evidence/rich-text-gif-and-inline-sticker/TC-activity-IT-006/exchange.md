# TC-activity-IT-006 请求/响应存证

POST /api/admin/activities 富文本 img src 存 objectKey、admin 读时替换签名 URL

执行日期: 2026-09-04 ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，`export TOKEN=<登录返回 token>` 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key（测试 fixture，明文入存证）

## Step 1: POST /api/admin/activities detailHtml 含 2 个 images/ objectKey img

```bash
curl -s -i -X POST http://localhost:21423/api/admin/activities -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"images": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"], "title": "富文本活动06-af4f49", "tags": ["富文本"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "交通", "visa": "签证", "itinerary": [{"title": "Day1", "content": "出发"}], "detailHtml": "<p>富文本段落一</p><img src=\"images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0601.png\"><p>富文本段落二</p><img src=\"images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0602.png\">", "online": true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-163d-7b6d-b7fc-357f9366c3fe",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "富文本活动06-af4f49",
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
  "detailHtml": "<p>富文本段落一</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0601.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=JJxFk2xlz4dHkzhvmMvpFw%2Fl4w4%3D\"><p>富文本段落二</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0602.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7bTb0GruBqAJDn5TNETJ0TLlW7s%3D\">",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.028115326Z",
  "updatedAt": "2026-09-04T07:02:44.028115326Z"
}
```

## Step 2: GET /api/admin/activities/01a06b3a-163d-7b6d-b7fc-357f9366c3fe

```bash
curl -s -i -X GET http://localhost:21423/api/admin/activities/01a06b3a-163d-7b6d-b7fc-357f9366c3fe -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-163d-7b6d-b7fc-357f9366c3fe",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "富文本活动06-af4f49",
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
  "detailHtml": "<p>富文本段落一</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0601.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=JJxFk2xlz4dHkzhvmMvpFw%2Fl4w4%3D\"><p>富文本段落二</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0602.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7bTb0GruBqAJDn5TNETJ0TLlW7s%3D\">",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.028115Z",
  "updatedAt": "2026-09-04T07:02:44.028115Z"
}
```

## Step 3: PUT /api/admin/activities/01a06b3a-163d-7b6d-b7fc-357f9366c3fe detailHtml 改为纯文本

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/activities/01a06b3a-163d-7b6d-b7fc-357f9366c3fe -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"images": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"], "title": "富文本活动06-af4f49", "tags": ["富文本"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "交通", "visa": "签证", "itinerary": [{"title": "Day1", "content": "出发"}], "detailHtml": "<p>纯文本无图</p>", "online": true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-163d-7b6d-b7fc-357f9366c3fe",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "富文本活动06-af4f49",
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
  "detailHtml": "<p>纯文本无图</p>",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.028115Z",
  "updatedAt": "2026-09-04T07:02:44.028115Z"
}
```

## Step 4: GET /api/admin/activities/01a06b3a-163d-7b6d-b7fc-357f9366c3fe

```bash
curl -s -i -X GET http://localhost:21423/api/admin/activities/01a06b3a-163d-7b6d-b7fc-357f9366c3fe -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-163d-7b6d-b7fc-357f9366c3fe",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "富文本活动06-af4f49",
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
  "detailHtml": "<p>纯文本无图</p>",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.028115Z",
  "updatedAt": "2026-09-04T07:02:44.080292Z"
}
```
