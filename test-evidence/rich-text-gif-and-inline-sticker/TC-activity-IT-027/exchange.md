# TC-activity-IT-027 请求/响应存证

POST /api/admin/activities 富文本内联图类型不符被拒绝

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

## Step 2: POST /api/admin/activities detailHtml=<img src=DSVG>

```bash
curl -s -i -X POST http://localhost:21423/api/admin/activities -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"images": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"], "title": "类型不符DSVG-af4f49", "tags": ["富文本"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "交通", "visa": "签证", "itinerary": [{"title": "Day1", "content": "出发"}], "detailHtml": "<p>x</p><img src=\"data:image/svg+xml;base64,rlSFYPi9cROCfJqkEj0Dq55Xxry52aIQ6QttDuSDFlzSiCC2/7HCNRELRMCuK15d5aVgwIj+6/ZfktaDdWFigMMunCxdZupTV4c4A/+mS/YELn7yV/tkz7KAbqOiJgP1/89/Pk0mePzP9ijStSKprGgF+RbfQbhtIJVzSEw+7EmC2rjvgnDREmamCSE3ku/waL6J2fNf0LknO46aPanQ4so5k+28D3ly1BLQXBT4AQ19BuyILFbz9nHCn3xn07KChiHwds0U5LFMFCUAQ/8h3P2zw0d1cOz5U1ZZ7//0NwCmaJn5jic6xf/e6v+W4WNd9bHOViWSKlatuBdVGVzoKGzkJgJ3+ecXSBbL07ehuKCVInmqSg6Ub4eEPYvlsfGRxOrBU3UnwfcbV/THd8lvZqYHauoyo5RbJ+VKLDibiB3WFUcU2or6yrvo0HsKYuW7qmDP939TGq0XGmJmIS3YN73fnXPwBJWRpx1+VFjXhbAxHZci+CIYiYg3ATgXikVZOeJqQGv0f0wd6BnvNV7dR/BvtdL0GVIUnwz1GDhixmYDW4Wk4+p712rjZej+h1bGknfqN6kmMxcbaQ8oyuvxC1jUEHT9AX+P/it1FK0wwSBfPmdL+ZK5bHog/ZmWObEBxJglQPNmn3q8b2tRDCPzoVFR1zw5avSqeZY+xbWTUIRkyqZcriXNi5saDLVeEIeogQsTH1rLiBIrSwkoewmP+SUZRrzFX7SJGxLv5YLQSyYB/Mf94gdOJg3TjMnr8WbzbCRVmChY0mTmgmpKHKDlU1bzr1WUZNFObwgnquLE5je6hveWVVPnf0GrfrXMNs9XUTfg42PByC0HRaN8Nt3DFSxtAwWj6/VF/WGH/R+B29WWd06jpEUL8XiQb8bQ+pom8z3R128kPM+j7ZXW+c6YesFzBuYfjFVNuXO7zQKjcfh2eGnN8D/jDdZszfZD3UKU7stPxSyw3ficUMR5nlsO5RFDvrymXI3HuAu4Gasj2SBrNYLug/2mepgdutZ8HrhmpDR/ghUiDFXZDu50faXv54vH53h94gYdq+0anXj/jtGlW0MvC3PjA9cXpJfI8dFeBucx5AbdNcsbL3uO+oVM+k14scyq+o1mk6PWQJDiAfAKbCgU9auQQ1gYUUMv4187vC0c9KDkJjX+IokJwRE9LDIEsqKaQVu6CZFsC/F6Wb9qsuQK7uQmnE3T4Ab9YC4CB4hjQ0m8DjGDJ+ANuMUyPJdZGTxqkhiGBh4F67sByqiizJg+zs/lxDyc2SPyaIlvyJYjKBIEnFvIPAIzVutyr8gQ3DKbHB7lRf1i0geEqrOZLapyVfEkZCtwcoUmGlYn8lOfEO2p9dajDgxg7WoLqw==\">", "online": true}'
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

## Step 3: POST /api/admin/activities detailHtml=<img src=DTXT>

```bash
curl -s -i -X POST http://localhost:21423/api/admin/activities -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"images": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"], "title": "类型不符DTXT-af4f49", "tags": ["富文本"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "交通", "visa": "签证", "itinerary": [{"title": "Day1", "content": "出发"}], "detailHtml": "<p>x</p><img src=\"data:text/plain;base64,aGVsbG8=\">", "online": true}'
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

## Step 4: POST /api/admin/activities detailHtml=<img src=DNB>

```bash
curl -s -i -X POST http://localhost:21423/api/admin/activities -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"images": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"], "title": "类型不符DNB-af4f49", "tags": ["富文本"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "交通", "visa": "签证", "itinerary": [{"title": "Day1", "content": "出发"}], "detailHtml": "<p>x</p><img src=\"data:image/png,rawbytes\">", "online": true}'
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

## Step 5: GET /api/admin/activities/page 核对数量

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

## Step 6: GET /api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1 记录更新前 detailHtml

```bash
curl -s -i -X GET http://localhost:21423/api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1 -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-16b0-7f0e-8983-292f842f88a1",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "内联小图25-af4f49",
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
  "detailHtml": "<p>改</p><img src=\"data:image/gif;base64,Vkd5g+xZ4Wcjukp5ZmgZ/7Gdvo5X6Nn8xYPp7bVv7Mq/dMi16/BKfqng6IhXF61fAXbzXgDVI/O5GnvBdCJMrXiq8LGJjAfULYaKcNbS8EIbnz6j6L4hjrhCGvePUYjOUy0wKZxPhOV4VMWbwl3fjFHUQFj5nOyxH37YU76KYWHWCqDrKngJHtv/1GajB9ni6A9GdOu4CDsKFfzyHvEIbRv3WIi+6jCs6phlidKUrmqKf83gZ5UaKJpzaeUdylUVGCsIsE6QRVl/zS2JK4INM0v+eotTPmjATvKYBxqLy/cFoFujGFXJg2VjnlSDGAEPgI6nKXf2tcJjBj5R9zSoAAdBiq6plYcIA82SnQJtPlFqGhBMmXPZycod1oeKfiA7htTigbDej7woU2tmso70lZb1265mgjZW0FROqSb0rLwIcCsFVmAUZqz9W0BgKIFrz2kjIPisZ8O2FrFxeuvOEpFQWNNE3G/QnNvZX7+B1JCm2VpNZPa+MKt21BKrYETy2n+8VFQoCL7E2N8wuOIbTKJCmlS1Bb3PKjJXKZK41TWKacsCGDMSHJja6NQ7my9yMbLCajPy70mV3xZPJzBQ5827aqHTgqog8LAfy5NEMR9q3mUod77Qy4vwoYFCxFvSJ2+BYbTBIfeAje8mhrtjBPCy+i1pFSJKKRFNsgH7ZCRlpLY+a9CFroFgNA1PSOdCDwqfxOsMPKyRQzseQzjjdiAnTPh8scB0L87aebP/oq/wRRbYIWydbAs/xxK8qaxVeMGp8Qmw6RwsTxMV559BodhEr//iED5qorgrp8aM8EortsmUnxlniCuLljpv+CLfVwaDT71NqCVcEM7JFMf68SrBbP+YYd5drPIsbKrzKuSr0l7d3bnv/19TWW4wy8R3pDt2mzQUisvmbNSX2u/lxc7550Si+mII4fuB1ONgBf+op3epTgVhZqzMuP/GFx+anrUkpE8VlkHHLCxN1c1+/znsUKIYArY8x3QjEpQEXgCWExvBqi2sXCWMVwZ6TqiCrANBB7ReUqqj15lFVKqnaEP2Y3+Sgux0ZWhVCZetqLwCrcr5W1o3QRxMX9b2nSwHFba6rauvQ6dfZLtICcMPbvifyhfiprrmv3HX1bLm4lwOtHvhT/DdhtqWi7QwIxfM6ahtfu9aHOVq01b9TZ+I5vbfh83b73yFCT1fX96j/IVFw+QZX+U0p/tH3G9N08PPJTPe60eI03nK0wZjoUGSjQzL7rYPKqhmn6MrFavMh/dfrI2i3yS3HQizRErS6y8DJOvYwv3lfNUmZ4lXGTFv6gV+zPMFO93D8/r/hLEvDRNYG9OoX12436ZBSQyxUPmVHzKIUhjf0hFTilunQxGtg7syGWQ00t8b7u5dREoPWiziOs7z89DoeWXi/lctuy3RKBh+46RVdtxxkjmkWCv0yq9YkLytOrcao4NqQEBymWY3pz6hQnk3UwaRpoRDIbKVOKhK9VMLXGZhwH5oMlTY/Kwxbr2IGGs14j7LUyA9PG26A4L4SVNbfuRz2vP1jTm8jkqa5rxYTIDKgVzVQvtTj0p5KjXYamUnUTKVvN1vfT9i42fp4lj8gToSegeBAzplEK0OCOQIZesukJWa6XueCPwpVJHZ7MQSbo+Rkq/i12Oc3dtPvH7iIZK80zIvWDO4KqwXYPsMl3U4sq1NREO48O4QMswbJokx1nUxaT9u/Z3Z/kBn960mXrHurfr4uiUVulOkeqwvgRBsHKyOhCH6zbrKbY0QvT1EuXhLgKJVXnPyLTj61KadhQnerrd63PXj2ibiz8DU3kwwUJINNdT6z+2gjw7Q5vfJ5ADx+mkDV+xB/deCW2vHRKkK1C4CPqLnah7J2qLUB6Uh7dKn14ctfWlL7pBM+YWxUZUR4uWu7bFVUbtpa2dC4Wd99DwgXaz1xfQnmz35QECCIIORKG1EKeQKurFitkHVGyc6z/xU39bCLT8kuBmNYaal2+IftrJA0gsuRroVCiBJCW/hQMX0WtG5zauARl8qgKIYRtTg2TR+nmdkXAQcaImRfCHImUuZf+Ql4nU7BRNY/4kCsm+zdXbK9joKJd0cQ/otBwOPD2gAbRCvXMhhs3ih1IZlCnzF/2iH2DaKa/FfdPfhlaM9FlRQdVM3mW7WIgBX/xVsjdLIE9RhSvOWuK5eMss2TNElEtUUmbA9UlS7OI9VRH4WFVcZ6yjtrrkPCCR1e++HjHrbz9DiUGjF14Gr5db0XAz4HwSss6xQ0U46mXIxCKEd13jRsW4NdlXlmakYAoqa1q0q8nqMHa+ziWzgTitlpn4ZuGrBg37nThb7cbvt1Oi7axGaSWf37MUK3jHbZZ2mYpLmqEim2ajEx8mq7CJJBWp9BHM6cyP6ib9547azffF8YJZEnFFyfr+UGugoSPeWP5/6MvoTSJG6u05dCDbi0H4QKj05jdczZdNozSPdpFdYKh0wQSzmJJ1Xk8VFlsXi+fyPEpNnF4+OV/YlrOnCfQooo/CrcVBAIKYqPNWAbiDfHsteLWpmWlE/zE8AeZQSAtwNgSRNJ7+5IChK7yCoo1RoJ472L2GPoWl/0jOjlm95zkObv1ah8Kn6QLzfNCiuE02ckjJH35Ghqr36MowUeelZyoOndKzTZDgXbtsZiGOTU4s730qoUdLuSig6LpRqvXTygVr11TTv4UzGtdFiWd9JfbfjBYJKnryW/LoYE0P3eNUte8DWjmKT8cEBaEj12ro=\">",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.144877Z",
  "updatedAt": "2026-09-04T07:02:44.179594Z"
}
```

## Step 7: PUT /api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1 detailHtml=<img src=DSVG>

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1 -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"images": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"], "title": "内联小图25-af4f49", "tags": ["富文本"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "交通", "visa": "签证", "itinerary": [{"title": "Day1", "content": "出发"}], "detailHtml": "<p>x</p><img src=\"data:image/svg+xml;base64,rlSFYPi9cROCfJqkEj0Dq55Xxry52aIQ6QttDuSDFlzSiCC2/7HCNRELRMCuK15d5aVgwIj+6/ZfktaDdWFigMMunCxdZupTV4c4A/+mS/YELn7yV/tkz7KAbqOiJgP1/89/Pk0mePzP9ijStSKprGgF+RbfQbhtIJVzSEw+7EmC2rjvgnDREmamCSE3ku/waL6J2fNf0LknO46aPanQ4so5k+28D3ly1BLQXBT4AQ19BuyILFbz9nHCn3xn07KChiHwds0U5LFMFCUAQ/8h3P2zw0d1cOz5U1ZZ7//0NwCmaJn5jic6xf/e6v+W4WNd9bHOViWSKlatuBdVGVzoKGzkJgJ3+ecXSBbL07ehuKCVInmqSg6Ub4eEPYvlsfGRxOrBU3UnwfcbV/THd8lvZqYHauoyo5RbJ+VKLDibiB3WFUcU2or6yrvo0HsKYuW7qmDP939TGq0XGmJmIS3YN73fnXPwBJWRpx1+VFjXhbAxHZci+CIYiYg3ATgXikVZOeJqQGv0f0wd6BnvNV7dR/BvtdL0GVIUnwz1GDhixmYDW4Wk4+p712rjZej+h1bGknfqN6kmMxcbaQ8oyuvxC1jUEHT9AX+P/it1FK0wwSBfPmdL+ZK5bHog/ZmWObEBxJglQPNmn3q8b2tRDCPzoVFR1zw5avSqeZY+xbWTUIRkyqZcriXNi5saDLVeEIeogQsTH1rLiBIrSwkoewmP+SUZRrzFX7SJGxLv5YLQSyYB/Mf94gdOJg3TjMnr8WbzbCRVmChY0mTmgmpKHKDlU1bzr1WUZNFObwgnquLE5je6hveWVVPnf0GrfrXMNs9XUTfg42PByC0HRaN8Nt3DFSxtAwWj6/VF/WGH/R+B29WWd06jpEUL8XiQb8bQ+pom8z3R128kPM+j7ZXW+c6YesFzBuYfjFVNuXO7zQKjcfh2eGnN8D/jDdZszfZD3UKU7stPxSyw3ficUMR5nlsO5RFDvrymXI3HuAu4Gasj2SBrNYLug/2mepgdutZ8HrhmpDR/ghUiDFXZDu50faXv54vH53h94gYdq+0anXj/jtGlW0MvC3PjA9cXpJfI8dFeBucx5AbdNcsbL3uO+oVM+k14scyq+o1mk6PWQJDiAfAKbCgU9auQQ1gYUUMv4187vC0c9KDkJjX+IokJwRE9LDIEsqKaQVu6CZFsC/F6Wb9qsuQK7uQmnE3T4Ab9YC4CB4hjQ0m8DjGDJ+ANuMUyPJdZGTxqkhiGBh4F67sByqiizJg+zs/lxDyc2SPyaIlvyJYjKBIEnFvIPAIzVutyr8gQ3DKbHB7lRf1i0geEqrOZLapyVfEkZCtwcoUmGlYn8lOfEO2p9dajDgxg7WoLqw==\">", "online": true}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "图片对象不可用",
  "path": "/api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1"
}
```

## Step 8: GET /api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1 核对未变

```bash
curl -s -i -X GET http://localhost:21423/api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1 -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-16b0-7f0e-8983-292f842f88a1",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "内联小图25-af4f49",
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
  "detailHtml": "<p>改</p><img src=\"data:image/gif;base64,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\">",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.144877Z",
  "updatedAt": "2026-09-04T07:02:44.179594Z"
}
```
