# TC-route-IT-019 请求/响应存证

用例: GET /api/app/routes?cityName= 城市表无同名城市时仍返回路线且 city 为 null
执行日期: 2026-08-26 ｜ change: app-list-sort-tiebreak ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 `X-API-Key: test-api-key`
图片 objectKey 用 test profile 的 Stub 校验器接受的固定 key。

> 回归重测：本 change 改动了 `RouteRepository` 的排序，需确认「城市名不做城市库校验、city 可为 null」的语义未被带偏。


## Step 1: admin 登录取 JWT

```bash
curl -s -X POST "http://localhost:21423/api/admin/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP/1.1 200）:

```json
{"token":"$TOKEN", ...}
```

## Step 2: 创建上线大使

```bash
curl -s -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/aaf89b6f-436c-477e-ac82-fde385411f4a.png","name":"排序大使021653","tags":["古着"],"online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdd-5798-77c2-81ca-672d360f8da7","name":"排序大使021653","online":true}  // 节选
```

## Step 3: 核对前置——城市表无「不存在城」

```bash
psql "postgresql://iris:$PGPASSWORD@localhost:25432/love_space" -c "select count(*) from loves_city where chinese_name='不存在城';"
```

实际响应（psql）:

```json
0  // 城市表中确无同名城市
```

## Step 4: 创建 cityName=「不存在城」的路线

```bash
curl -s -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityName":"不存在城","sortOrder":0,"title":"无城路线021653","thumbnail":"images/8b23da6f-63c7-4260-87e1-3a549b3216f4.png","images":["images/38f13c69-fe1a-4a61-90aa-c20b01995d2f.png"],"ambassadorId":"01a03bdd-5798-77c2-81ca-672d360f8da7"}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03be3-7e16-735a-867c-6a6b7fadc92c","sortOrder":0,"title":"无城路线021653","cityName":"不存在城", ...}  // 节选
```

## Step 5: app 端按该城市名查询路线

```bash
curl -s -i -H "X-API-Key: test-api-key" --get --data-urlencode "cityName=不存在城" "http://localhost:8081/api/app/routes"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

[
  {
    "id": "01a03be3-7e16-735a-867c-6a6b7fadc92c",
    "title": "无城路线021653",
    "thumbnail": {
      "id": "bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png",
      "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png?Expires=1787712959&OSSAccessKeyId=placeholder&Signature=QICCuzFSSCNoUD8LA7iYfrmmXcw%3D"
    },
    "sortOrder": 0,
    "ambassadorName": "排序大使021653",
    "city": null
  }
]
```
