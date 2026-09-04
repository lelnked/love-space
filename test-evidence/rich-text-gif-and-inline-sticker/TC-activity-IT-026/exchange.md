# TC-activity-IT-026 请求/响应存证

POST /api/admin/activities 富文本内联图超限被拒绝（3 KB 边界）

执行日期: 2026-09-04 ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，`export TOKEN=<登录返回 token>` 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key（测试 fixture，明文入存证）

## Step 1: 基线：GET /api/admin/activities/page 记录 totalElements

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/activities/page?page=0&size=1' -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [
    {
      "id": "01a06b3a-16b0-7f0e-8983-292f842f88a1",
      "cover": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
      },
      "title": "内联小图25-af4f49",
      "subtitle": null,
      "tags": [
        "富文本"
      ],
      "periods": [
        "FOLLICULAR"
      ],
      "level": "L1",
      "online": true,
      "createdAt": "2026-09-04T07:02:44.144877Z",
      "updatedAt": "2026-09-04T07:02:44.179594Z"
    },
    {
      "id": "01a06b3a-1689-743f-8eca-82d15ddc3bf4",
      "cover": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
      },
      "title": "app活动09-af4f49",
      "subtitle": null,
      "tags": [
        "富文本"
      ],
      "periods": [
        "FOLLICULAR"
      ],
      "level": "L1",
      "online": true,
      "createdAt": "2026-09-04T07:02:44.10521Z",
      "updatedAt": "2026-09-04T07:02:44.10521Z"
    },
    {
      "id": "01a06b3a-163d-7b6d-b7fc-357f9366c3fe",
      "cover": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
      },
      "title": "富文本活动06-af4f49",
      "subtitle": null,
      "tags": [
        "富文本"
      ],
      "periods": [
        "FOLLICULAR"
      ],
      "level": "L1",
      "online": true,
      "createdAt": "2026-09-04T07:02:44.028115Z",
      "updatedAt": "2026-09-04T07:02:44.080292Z"
    },
    {
      "id": "01a06b34-0408-779a-a0cb-fdbe8137c0f6",
      "cover": null,
      "title": "活动 B",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.15245Z",
      "updatedAt": "2026-09-04T06:56:06.15245Z"
    },
    {
      "id": "01a06b34-0407-7100-9acc-b5b9a60a7abe",
      "cover": null,
      "title": "活动 A",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.151033Z",
      "updatedAt": "2026-09-04T06:56:06.151033Z"
    },
    {
      "id": "01a06b34-03ea-7416-a9b5-f1036388e005",
      "cover": null,
      "title": "黄体期活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.122229Z",
      "updatedAt": "2026-09-04T06:56:06.122229Z"
    },
    {
      "id": "01a06b34-03e5-7c0d-b6af-bc382a2c90ea",
      "cover": null,
      "title": "经期活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.117724Z",
      "updatedAt": "2026-09-04T06:56:06.117724Z"
    },
    {
      "id": "01a06b34-03d3-71a7-82a6-3976ca5aee67",
      "cover": null,
      "title": "活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.099072Z",
      "updatedAt": "2026-09-04T06:56:06.099072Z"
    },
    {
      "id": "01a06b34-03bf-79a4-8aa3-70232e296de6",
      "cover": null,
      "title": "活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.079566Z",
      "updatedAt": "2026-09-04T06:56:06.079566Z"
    },
    {
      "id": "01a06b34-0391-7abb-b197-3c79cbaed9bc",
      "cover": null,
      "title": "活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.033609Z",
      "updatedAt": "2026-09-04T06:56:06.033609Z"
    },
    {
      "id": "01a06b34-037f-75f4-a617-848f072a63b0",
      "cover": null,
      "title": "活动 B",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.015345Z",
      "updatedAt": "2026-09-04T06:56:06.015345Z"
    },
    {
      "id": "01a06b34-037d-7c92-963e-1e76caf0dfeb",
      "cover": null,
      "title": "活动 A",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.013754Z",
      "updatedAt": "2026-09-04T06:56:06.013754Z"
    },
    {
      "id": "01a06b34-0370-7ada-a500-8b3596cbcc69",
      "cover": null,
      "title": "成都周末",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.000646Z",
      "updatedAt": "2026-09-04T06:56:06.000646Z"
    },
    {
      "id": "01a06b34-036a-7d61-a920-a8678493592f",
      "cover": null,
      "title": "活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.994733Z",
      "updatedAt": "2026-09-04T06:56:05.994733Z"
    },
    {
      "id": "01a06b34-0351-7050-adb0-c1c78faca3ce",
      "cover": null,
      "title": "经期活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.968991Z",
      "updatedAt": "2026-09-04T06:56:05.968991Z"
    },
    {
      "id": "01a06b34-034c-75a5-bcbe-8850b8fdec4a",
      "cover": null,
      "title": "卵泡期活动-3",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.964324Z",
      "updatedAt": "2026-09-04T06:56:05.964324Z"
    },
    {
      "id": "01a06b34-0347-72b4-8781-d40352fb2134",
      "cover": null,
      "title": "卵泡期活动-1",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.959135Z",
      "updatedAt": "2026-09-04T06:56:05.959135Z"
    },
    {
      "id": "01a06b34-033d-7b98-a952-9220685032f8",
      "cover": null,
      "title": "卵泡期活动-2",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.94968Z",
      "updatedAt": "2026-09-04T06:56:05.94968Z"
    },
    {
      "id": "01a06b34-032b-7384-99da-12ac89986645",
      "cover": null,
      "title": "活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.931179Z",
      "updatedAt": "2026-09-04T06:56:05.931179Z"
    },
    {
      "id": "01a06b34-02f7-7458-815e-d49199ace30e",
      "cover": null,
      "title": "活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.879235Z",
      "updatedAt": "2026-09-04T06:56:05.879235Z"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 239,
  "totalPages": 12
}
```

## Step 2: POST /api/admin/activities detailHtml=内联 png 4096 字节

```bash
curl -s -i -X POST http://localhost:21423/api/admin/activities -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"images": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"], "title": "超限4096-af4f49", "tags": ["富文本"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "交通", "visa": "签证", "itinerary": [{"title": "Day1", "content": "出发"}], "detailHtml": "<p>x</p><img src=\"data:image/png;base64,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\">", "online": true}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "图片对象不可用",
  "path": "/api/admin/activities"
}
```

## Step 3: POST /api/admin/activities detailHtml=内联 png 3073 字节

```bash
curl -s -i -X POST http://localhost:21423/api/admin/activities -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"images": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"], "title": "超限3073-af4f49", "tags": ["富文本"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "交通", "visa": "签证", "itinerary": [{"title": "Day1", "content": "出发"}], "detailHtml": "<p>x</p><img src=\"data:image/png;base64,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\">", "online": true}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "图片对象不可用",
  "path": "/api/admin/activities"
}
```

## Step 4: POST /api/admin/activities detailHtml=内联 png 恰 3072 字节

```bash
curl -s -i -X POST http://localhost:21423/api/admin/activities -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"images": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"], "title": "边界3072-af4f49", "tags": ["富文本"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "交通", "visa": "签证", "itinerary": [{"title": "Day1", "content": "出发"}], "detailHtml": "<p>x</p><img src=\"data:image/png;base64,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\">", "online": true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-171a-73fd-93e3-375a52acb396",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "边界3072-af4f49",
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
  "detailHtml": "<p>x</p><img src=\"data:image/png;base64,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\">",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.250201036Z",
  "updatedAt": "2026-09-04T07:02:44.250201036Z"
}
```

## Step 5: GET /api/admin/activities/01a06b3a-171a-73fd-93e3-375a52acb396

```bash
curl -s -i -X GET http://localhost:21423/api/admin/activities/01a06b3a-171a-73fd-93e3-375a52acb396 -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-171a-73fd-93e3-375a52acb396",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "边界3072-af4f49",
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
  "detailHtml": "<p>x</p><img src=\"data:image/png;base64,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\">",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.250201Z",
  "updatedAt": "2026-09-04T07:02:44.250201Z"
}
```

## Step 6: GET /api/admin/activities/page 核对数量

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/activities/page?page=0&size=1' -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [
    {
      "id": "01a06b3a-171a-73fd-93e3-375a52acb396",
      "cover": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
      },
      "title": "边界3072-af4f49",
      "subtitle": null,
      "tags": [
        "富文本"
      ],
      "periods": [
        "FOLLICULAR"
      ],
      "level": "L1",
      "online": true,
      "createdAt": "2026-09-04T07:02:44.250201Z",
      "updatedAt": "2026-09-04T07:02:44.250201Z"
    },
    {
      "id": "01a06b3a-16b0-7f0e-8983-292f842f88a1",
      "cover": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
      },
      "title": "内联小图25-af4f49",
      "subtitle": null,
      "tags": [
        "富文本"
      ],
      "periods": [
        "FOLLICULAR"
      ],
      "level": "L1",
      "online": true,
      "createdAt": "2026-09-04T07:02:44.144877Z",
      "updatedAt": "2026-09-04T07:02:44.179594Z"
    },
    {
      "id": "01a06b3a-1689-743f-8eca-82d15ddc3bf4",
      "cover": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
      },
      "title": "app活动09-af4f49",
      "subtitle": null,
      "tags": [
        "富文本"
      ],
      "periods": [
        "FOLLICULAR"
      ],
      "level": "L1",
      "online": true,
      "createdAt": "2026-09-04T07:02:44.10521Z",
      "updatedAt": "2026-09-04T07:02:44.10521Z"
    },
    {
      "id": "01a06b3a-163d-7b6d-b7fc-357f9366c3fe",
      "cover": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
      },
      "title": "富文本活动06-af4f49",
      "subtitle": null,
      "tags": [
        "富文本"
      ],
      "periods": [
        "FOLLICULAR"
      ],
      "level": "L1",
      "online": true,
      "createdAt": "2026-09-04T07:02:44.028115Z",
      "updatedAt": "2026-09-04T07:02:44.080292Z"
    },
    {
      "id": "01a06b34-0408-779a-a0cb-fdbe8137c0f6",
      "cover": null,
      "title": "活动 B",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.15245Z",
      "updatedAt": "2026-09-04T06:56:06.15245Z"
    },
    {
      "id": "01a06b34-0407-7100-9acc-b5b9a60a7abe",
      "cover": null,
      "title": "活动 A",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.151033Z",
      "updatedAt": "2026-09-04T06:56:06.151033Z"
    },
    {
      "id": "01a06b34-03ea-7416-a9b5-f1036388e005",
      "cover": null,
      "title": "黄体期活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.122229Z",
      "updatedAt": "2026-09-04T06:56:06.122229Z"
    },
    {
      "id": "01a06b34-03e5-7c0d-b6af-bc382a2c90ea",
      "cover": null,
      "title": "经期活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.117724Z",
      "updatedAt": "2026-09-04T06:56:06.117724Z"
    },
    {
      "id": "01a06b34-03d3-71a7-82a6-3976ca5aee67",
      "cover": null,
      "title": "活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.099072Z",
      "updatedAt": "2026-09-04T06:56:06.099072Z"
    },
    {
      "id": "01a06b34-03bf-79a4-8aa3-70232e296de6",
      "cover": null,
      "title": "活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.079566Z",
      "updatedAt": "2026-09-04T06:56:06.079566Z"
    },
    {
      "id": "01a06b34-0391-7abb-b197-3c79cbaed9bc",
      "cover": null,
      "title": "活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.033609Z",
      "updatedAt": "2026-09-04T06:56:06.033609Z"
    },
    {
      "id": "01a06b34-037f-75f4-a617-848f072a63b0",
      "cover": null,
      "title": "活动 B",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.015345Z",
      "updatedAt": "2026-09-04T06:56:06.015345Z"
    },
    {
      "id": "01a06b34-037d-7c92-963e-1e76caf0dfeb",
      "cover": null,
      "title": "活动 A",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.013754Z",
      "updatedAt": "2026-09-04T06:56:06.013754Z"
    },
    {
      "id": "01a06b34-0370-7ada-a500-8b3596cbcc69",
      "cover": null,
      "title": "成都周末",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:06.000646Z",
      "updatedAt": "2026-09-04T06:56:06.000646Z"
    },
    {
      "id": "01a06b34-036a-7d61-a920-a8678493592f",
      "cover": null,
      "title": "活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.994733Z",
      "updatedAt": "2026-09-04T06:56:05.994733Z"
    },
    {
      "id": "01a06b34-0351-7050-adb0-c1c78faca3ce",
      "cover": null,
      "title": "经期活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.968991Z",
      "updatedAt": "2026-09-04T06:56:05.968991Z"
    },
    {
      "id": "01a06b34-034c-75a5-bcbe-8850b8fdec4a",
      "cover": null,
      "title": "卵泡期活动-3",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.964324Z",
      "updatedAt": "2026-09-04T06:56:05.964324Z"
    },
    {
      "id": "01a06b34-0347-72b4-8781-d40352fb2134",
      "cover": null,
      "title": "卵泡期活动-1",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.959135Z",
      "updatedAt": "2026-09-04T06:56:05.959135Z"
    },
    {
      "id": "01a06b34-033d-7b98-a952-9220685032f8",
      "cover": null,
      "title": "卵泡期活动-2",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.94968Z",
      "updatedAt": "2026-09-04T06:56:05.94968Z"
    },
    {
      "id": "01a06b34-032b-7384-99da-12ac89986645",
      "cover": null,
      "title": "活动",
      "subtitle": null,
      "tags": [],
      "periods": [],
      "level": null,
      "online": true,
      "createdAt": "2026-09-04T06:56:05.931179Z",
      "updatedAt": "2026-09-04T06:56:05.931179Z"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 240,
  "totalPages": 12
}
```
