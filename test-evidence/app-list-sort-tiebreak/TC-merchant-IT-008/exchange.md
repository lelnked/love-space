# TC-merchant-IT-008 请求/响应存证

用例: GET /api/app/categories/page 排序号优先于创建时间
执行日期: 2026-08-26 ｜ change: app-list-sort-tiebreak ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 `X-API-Key: test-api-key`
图片 objectKey 用 test profile 的 Stub 校验器接受的固定 key。

> 本用例不需要人工拉开 created_at：B 虽创建更晚，但 sortOrder 更小，断言的正是「排序号压过创建时间」。


## Step 1: admin 登录取 JWT

```bash
curl -s -X POST "http://localhost:21423/api/admin/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP/1.1 200）:

```json
{"token":"$TOKEN", ...}
```

## Step 2: 先创建分类 A（sortOrder=1，上架）

```bash
curl -s -X POST "http://localhost:21423/api/admin/categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"序A021653","sortOrder":1,"online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdc-0090-732c-bade-723ca48dce72","name":"序A021653","sortOrder":1,"online":true,"createdAt":"2026-08-26T02:17:48.944129736Z"}
```

## Step 3: 后创建分类 B（sortOrder=0，上架）

```bash
curl -s -X POST "http://localhost:21423/api/admin/categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"序B021653","sortOrder":0,"online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdc-009c-764b-af88-925f2665992f","name":"序B021653","sortOrder":0,"online":true,"createdAt":"2026-08-26T02:17:48.956353104Z"}
```

## Step 4: app 端分类分页查询

```bash
curl -s -i -H "X-API-Key: test-api-key" "http://localhost:8081/api/app/categories/page?page=0&size=20"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

{
  "content": [
    {
      "id": "01a03bdc-009c-764b-af88-925f2665992f",
      "name": "序B021653"
    },
    {
      "id": "01a03bdb-cdd2-76aa-bcbb-ea8bad0cae63",
      "name": "分B021653"
    },
    {
      "id": "01a03bdb-cdc5-7654-aee9-faa1c0bc5502",
      "name": "分A021653"
    },
    {
      "id": "01a03956-c84d-7619-a6e6-e7ba28b51ccf",
      "name": "类-7a9988f5-4d17-41c3-863b-970dc2234936"
    },
    {
      "id": "01a03956-c82b-7dc3-95da-cd0d63c98b0d",
      "name": "类-b4f75c49-52f1-459a-bfe5-62c20b746bc6"
    },
    {
      "id": "01a03956-c7f1-704b-83eb-acbec53cea9c",
      "name": "类-6cc2eb6f-878a-4108-a2fe-17e82a1ddabe"
    },
    {
      "id": "01a038cf-7bb4-7315-974f-04d8a38fe327",
      "name": "类-9dd4ba58-d2c4-4f56-8284-0d14580895db"
    },
    {
      "id": "01a038cf-7b93-7c8e-9f57-754efcab814e",
      "name": "类-f8bc70e4-f748-4fb4-9f0f-a26c4d19fe55"
    },
    {
      "id": "01a038cf-7b63-796d-8105-af5837058688",
      "name": "类-ce194e74-a2b4-4d97-902e-e792d1056799"
    },
    {
      "id": "01a038bb-cbe8-7101-b6e7-ff1a0235d096",
      "name": "类-278dc1e0-63cf-43ab-94b8-4079ad45835f"
    },
    {
      "id": "01a038bb-cbc8-7d71-be4c-027180bd4fc1",
      "name": "类-31ac947f-51da-43ac-99e8-c461ff0bcb9b"
    },
    {
      "id": "01a038bb-cb96-7331-9fe8-b61b1490f580",
      "name": "类-bb023cca-b34c-4e8e-9a6f-a63c1fa990d3"
    },
    {
      "id": "01a03851-aacc-779f-8cf8-d79de4a8f2ad",
      "name": "类-e2ed0f3f-2af1-4436-a7fa-d25d8f91cba5"
    },
    {
      "id": "01a03851-aaa9-76eb-82d6-04f2b6eedb50",
      "name": "类-3e7be1a5-f974-4da7-a25f-d6d518ebd674"
    },
    {
      "id": "01a03851-aa75-7d09-9afc-bede7a9af81d",
      "name": "类-cc904811-455b-4073-9881-f400dad2074a"
    },
    {
      "id": "01a03bdc-0090-732c-bade-723ca48dce72",
      "name": "序A021653"
    },
    {
      "id": "01a03956-c069-70b3-9c5a-0230b74f8f10",
      "name": "后-c251d6b3-6834-43ff-babc-90a954c4d3e3"
    },
    {
      "id": "01a038cf-73cc-78e6-b693-d1ddceaf00af",
      "name": "后-73c68270-8ea3-4316-820a-ae6406b2adcd"
    },
    {
      "id": "01a038bb-c441-78eb-a362-10f8d3dd54b4",
      "name": "后-c27afd0a-4fa9-4292-a207-04c9f55ec810"
    },
    {
      "id": "01a03851-a2f9-78b4-818f-b9c4bcd79cac",
      "name": "后-47cad4b7-1c5c-4171-967f-9d561ec74142"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 20,
  "totalPages": 1
}
```
