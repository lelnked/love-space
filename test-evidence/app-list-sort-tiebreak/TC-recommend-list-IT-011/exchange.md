# TC-recommend-list-IT-011 请求/响应存证

用例: GET /api/app/recommend-lists 上架城市清单按 sortOrder 升序
执行日期: 2026-08-26 ｜ change: app-list-sort-tiebreak ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 `X-API-Key: test-api-key`
图片 objectKey 用 test profile 的 Stub 校验器接受的固定 key。

> 回归重测：验证 tie-break 由 ASC 翻为 DESC 后，sortOrder 主序未被带偏。


## Step 1: admin 登录取 JWT

```bash
curl -s -X POST "http://localhost:21423/api/admin/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP/1.1 200）:

```json
{"token":"$TOKEN", ...}
```

## Step 2: 创建上架城市 cityId

```bash
curl -s -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"清单升序城021653","englishName":"RLAsc021653","chineseProvince":"测试省","englishProvince":"TP","online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdc-92bf-78e5-95d2-bda0090dc476","chineseName":"清单升序城021653","online":true}  // 节选
```

## Step 3: 依次创建 sortOrder 为 5、1、3 的三个清单

```bash
curl -s -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"清单5-021653","introduction":"s5","cityId":"01a03bdc-92bf-78e5-95d2-bda0090dc476","sortOrder":5}'
curl -s -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"清单1-021653","introduction":"s1","cityId":"01a03bdc-92bf-78e5-95d2-bda0090dc476","sortOrder":1}'
curl -s -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"清单3-021653","introduction":"s3","cityId":"01a03bdc-92bf-78e5-95d2-bda0090dc476","sortOrder":3}'
```

实际响应（HTTP/1.1 200）:

```json
三次均 HTTP/1.1 200，分别返回 sortOrder=5 / 1 / 3 的清单（status=ONLINE）
```

## Step 4: app 端按城市查询清单

```bash
curl -s -i -H "X-API-Key: test-api-key" "http://localhost:8081/api/app/recommend-lists?cityId=01a03bdc-92bf-78e5-95d2-bda0090dc476"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

[
  {
    "id": "01a03bdc-92dc-7c90-849a-53f893f2a34a",
    "title": "清单1-021653",
    "introduction": "s1",
    "cityId": "01a03bdc-92bf-78e5-95d2-bda0090dc476",
    "sortOrder": 1
  },
  {
    "id": "01a03bdc-92e7-70ba-9c06-234da391b3a7",
    "title": "清单3-021653",
    "introduction": "s3",
    "cityId": "01a03bdc-92bf-78e5-95d2-bda0090dc476",
    "sortOrder": 3
  },
  {
    "id": "01a03bdc-92d1-7edd-8647-1753b79ef8f9",
    "title": "清单5-021653",
    "introduction": "s5",
    "cityId": "01a03bdc-92bf-78e5-95d2-bda0090dc476",
    "sortOrder": 5
  }
]
```
