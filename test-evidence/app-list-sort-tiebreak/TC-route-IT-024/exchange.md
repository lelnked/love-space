# TC-route-IT-024 请求/响应存证

用例: GET /api/app/routes 同排序号路线按创建时间倒序
执行日期: 2026-08-26 ｜ change: app-list-sort-tiebreak ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 `X-API-Key: test-api-key`
图片 objectKey 用 test profile 的 Stub 校验器接受的固定 key。

> 说明：HTTP 连续创建的 `created_at` 相差仅数十毫秒，为让 tie-break 断言有可辨识的时间差，对**先创建**的一条直接连库把 `created_at` 前移 2 小时（该步骤如实记录在下方 Step 中），不改任何业务字段，等价于「先后创建」的真实场景。


## Step 1: admin 登录取 JWT

```bash
curl -s -X POST "http://localhost:21423/api/admin/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP/1.1 200）:

```json
{"token":"$TOKEN", ...}
```

## Step 2: 创建上线大使 amb

```bash
curl -s -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/aaf89b6f-436c-477e-ac82-fde385411f4a.png","name":"排序大使021653","tags":["古着"],"online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdd-5798-77c2-81ca-672d360f8da7","name":"排序大使021653","online":true}  // 节选
```

## Step 3: 创建城市「排序测试城」（供路线 cityName 匹配）

```bash
curl -s -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"排序测试城021653","englishName":"SortCity021653","chineseProvince":"测试省","englishProvince":"TP","online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id": "01a03bdb-28ed-763c-a0ed-0e1852d2347e", "chineseName": "排序测试城021653", "englishName": "SortCity021653", "chineseProvince": "测试省", "englishProvince": "TP", "online": true, "createdAt": "2026-08-26T02:16:53.729119169Z"}
```

## Step 4: 先创建路线 A（sortOrder=0）

```bash
curl -s -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityName":"排序测试城021653","sortOrder":0,"title":"路线A-021653","thumbnail":"images/8b23da6f-63c7-4260-87e1-3a549b3216f4.png","images":["images/38f13c69-fe1a-4a61-90aa-c20b01995d2f.png"],"ambassadorId":"01a03bdd-5798-77c2-81ca-672d360f8da7"}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdd-ce2b-7551-9c73-4ca7475ccffc","title":"路线A-021653","sortOrder":0,"cityName":"排序测试城021653"}  // 节选
```

## Step 5: 后创建路线 B（sortOrder=0）

```bash
curl -s -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityName":"排序测试城021653","sortOrder":0,"title":"路线B-021653","thumbnail":"images/8b23da6f-63c7-4260-87e1-3a549b3216f4.png","images":["images/38f13c69-fe1a-4a61-90aa-c20b01995d2f.png"],"ambassadorId":"01a03bdd-5798-77c2-81ca-672d360f8da7"}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdd-ce3e-702c-ad68-7d0f7dc1d2ec","title":"路线B-021653","sortOrder":0,"cityName":"排序测试城021653"}  // 节选
```

## Step 6: 连库拉开 created_at（路线 A 前移 2 小时）

```bash
psql "postgresql://iris:$PGPASSWORD@localhost:25432/love_space" -c "update loves_route set created_at = created_at - interval '2 hour' where id='01a03bdd-ce2b-7551-9c73-4ca7475ccffc';"
```

实际响应（psql）:

```json
UPDATE 1
路线A-021653|0|2026-08-26 00:19:47.115278+00
路线B-021653|0|2026-08-26 02:19:47.133947+00
```

## Step 7: app 端按城市名查询路线

```bash
curl -s -i -H "X-API-Key: test-api-key" --get --data-urlencode "cityName=排序测试城021653" "http://localhost:8081/api/app/routes"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

[
  {
    "id": "01a03bdd-ce3e-702c-ad68-7d0f7dc1d2ec",
    "title": "路线B-021653",
    "thumbnail": {
      "id": "bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png",
      "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png?Expires=1787712587&OSSAccessKeyId=placeholder&Signature=ibr3leh%2FFV5LRm2ZsKoq60H5gvM%3D"
    },
    "sortOrder": 0,
    "ambassadorName": "排序大使021653",
    "city": {
      "id": "01a03bdb-28ed-763c-a0ed-0e1852d2347e",
      "name": "排序测试城021653"
    }
  },
  {
    "id": "01a03bdd-ce2b-7551-9c73-4ca7475ccffc",
    "title": "路线A-021653",
    "thumbnail": {
      "id": "bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png",
      "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png?Expires=1787712587&OSSAccessKeyId=placeholder&Signature=ibr3leh%2FFV5LRm2ZsKoq60H5gvM%3D"
    },
    "sortOrder": 0,
    "ambassadorName": "排序大使021653",
    "city": {
      "id": "01a03bdb-28ed-763c-a0ed-0e1852d2347e",
      "name": "排序测试城021653"
    }
  }
]
```
