# TC-recommend-list-IT-019 请求/响应存证

用例: GET /api/app/recommend-lists 同排序号清单按创建时间倒序
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

## Step 2: 创建上架城市 cityId

```bash
curl -s -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"清单排序城021653","englishName":"RLCity021653","chineseProvince":"测试省","englishProvince":"TP","online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdc-66b5-7d4c-a153-80cc7ee85a44","chineseName":"清单排序城021653","online":true}  // 节选
```

## Step 3: 先创建清单 A（sortOrder=0，ONLINE）

```bash
curl -s -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"清单A021653","introduction":"iA","cityId":"01a03bdc-66b5-7d4c-a153-80cc7ee85a44","sortOrder":0}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdc-66ce-74a9-97f3-465bdb7133ed","title":"清单A021653","cityId":"01a03bdc-66b5-7d4c-a153-80cc7ee85a44","sortOrder":0,"status":"ONLINE","createdAt":"2026-08-26T02:18:15.116704316Z"}
```

## Step 4: 后创建清单 B（sortOrder=0，ONLINE）

```bash
curl -s -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"清单B021653","introduction":"iB","cityId":"01a03bdc-66b5-7d4c-a153-80cc7ee85a44","sortOrder":0}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdc-66e5-790b-82fc-fffb04df7fbc","title":"清单B021653","cityId":"01a03bdc-66b5-7d4c-a153-80cc7ee85a44","sortOrder":0,"status":"ONLINE","createdAt":"2026-08-26T02:18:15.141509842Z"}
```

## Step 5: 连库拉开 created_at（清单 A 前移 2 小时）

```bash
psql "postgresql://iris:$PGPASSWORD@localhost:25432/love_space" -c "update loves_recommend_list set created_at = created_at - interval '2 hour' where id='01a03bdc-66ce-74a9-97f3-465bdb7133ed';"
```

实际响应（psql）:

```json
UPDATE 1
清单A021653|0|ONLINE|2026-08-26 00:18:15.116704+00
清单B021653|0|ONLINE|2026-08-26 02:18:15.14151+00
```

## Step 6: app 端按城市查询清单

```bash
curl -s -i -H "X-API-Key: test-api-key" "http://localhost:8081/api/app/recommend-lists?cityId=01a03bdc-66b5-7d4c-a153-80cc7ee85a44"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

[
  {
    "id": "01a03bdc-66e5-790b-82fc-fffb04df7fbc",
    "title": "清单B021653",
    "introduction": "iB",
    "cityId": "01a03bdc-66b5-7d4c-a153-80cc7ee85a44",
    "sortOrder": 0
  },
  {
    "id": "01a03bdc-66ce-74a9-97f3-465bdb7133ed",
    "title": "清单A021653",
    "introduction": "iA",
    "cityId": "01a03bdc-66b5-7d4c-a153-80cc7ee85a44",
    "sortOrder": 0
  }
]
```
