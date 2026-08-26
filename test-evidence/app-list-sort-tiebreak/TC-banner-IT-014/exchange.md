# TC-banner-IT-014 请求/响应存证

用例: GET /api/app/banners 下架 Banner 不下发
执行日期: 2026-08-26 ｜ change: app-list-sort-tiebreak ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 `X-API-Key: test-api-key`
图片 objectKey 用 test profile 的 Stub 校验器接受的固定 key。

> 回归重测：本 change 改动了 `BannerQueryService` 的排序，需确认「仅返回 online=true」的过滤未被带偏。


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
curl -s -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"排序测试城021653","englishName":"SortCity021653","chineseProvince":"测试省","englishProvince":"TP","online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id": "01a03bdb-28ed-763c-a0ed-0e1852d2347e", "chineseName": "排序测试城021653", "online": true}
```

## Step 3: 创建关联该城市的 Banner，保持下架（创建后默认 online=false，不调 /online）

```bash
curl -s -X POST "http://localhost:21423/api/admin/banners" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"下架Banner021653","positionCode":"APP_OFFLINE_CASE","type":"CITY","imageUrls":["images/0197aaaa-bbbb-7000-8000-000000000004.png"],"link":"01a03bdb-28ed-763c-a0ed-0e1852d2347e","sortOrder":0}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03be2-fe02-703f-bc0d-459a54823be6","name":"下架Banner021653","positionCode":"APP_OFFLINE_CASE","type":"CITY","link":"01a03bdb-28ed-763c-a0ed-0e1852d2347e","online":false,"sortOrder":0}  // 节选：创建接口强制 online=false，未调用上架接口
```

## Step 4: app 端按该展示位查询

```bash
curl -s -i -H "X-API-Key: test-api-key" "http://localhost:8081/api/app/banners?positionCode=APP_OFFLINE_CASE"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

[]
```
