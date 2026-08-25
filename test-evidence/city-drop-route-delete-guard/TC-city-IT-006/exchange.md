# TC-city-IT-006 请求/响应存证

- 执行日期：2026-08-25
- change：city-drop-route-delete-guard
- admin baseUrl：http://localhost:8080（JWT）
- app baseUrl：http://localhost:8081（X-API-Key）
- 脱敏：`$TOKEN` = admin 登录 JWT，`$APIKEY` = app 端 API Key（导出后下列 curl 可原样执行）

```bash
export TOKEN=$(curl -s -X POST http://localhost:8080/api/admin/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}' | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')
export APIKEY=<app-api-key>
export AID=01a038bb-d304-7295-a941-ee48da8726b5
```

## Step 1 前置：确认存在上线活动与上架城市

```bash
curl -s "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN"
```
结果：城市总数 40，其中 online=true 38 个（满足「存在至少 1 个上架城市」）。
38 个上架城市 id 已记录，用于测试后恢复（见 _s3_offline.txt / _s6_restore.txt）。

上线活动直接复用库内既有数据，未新建夹具（活动自 activity-drop-city-link 起已无城市关联）：
选定 activityId = `01a038bb-d304-7295-a941-ee48da8726b5`（title=活动）。

## Step 2 下架前 GET /api/app/activities

```bash
curl -s -i http://localhost:8081/api/app/activities -H "X-API-Key: $APIKEY"
```

响应头：
```
HTTP/1.1 200 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json
Content-Length: 1323
Date: Tue, 25 Aug 2026 12:00:11 GMT
```
响应体：
```json
[
    {
        "id": "01a038bb-d304-7295-a941-ee48da8726b5",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a038bb-d2f9-7dfb-b023-22f222b5ef96",
        "title": "成都周末",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a038bb-d2db-759c-a2e9-9bb1684aef8e",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a038bb-d2c7-7f70-81ca-d9ebe42806f6",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a038bb-d2bf-777d-b892-f4433f5d169a",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a03851-b2ca-78ea-9e9a-e73e6ea7ae7b",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a03851-b2bf-78fd-ae95-0f6fb5480ab7",
        "title": "成都周末",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a03851-b297-7935-b1ca-a3340cb4fac7",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a03851-b287-7059-b1c5-00d5ee2d721f",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a03851-b27f-77d2-bfe4-653a62f1c206",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    }
]
```
断言：列表含 `01a038bb-d304-7295-a941-ee48da8726b5` → 是（共 10 条）。

## Step 3 admin 侧将系统中全部城市下架

```bash
for id in $(cat online_cities.txt); do
  curl -s -o /dev/null -w "%{http_code}\n" -X PUT "http://localhost:8080/api/admin/cities/$id/online" \
    -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
done
```
结果：38 个城市全部返回 200（逐条见 `_s3_offline.txt`）。

下架生效核验：
```bash
curl -s http://localhost:8081/api/app/cities -H "X-API-Key: $APIKEY"   # => []（0 条）
curl -s http://localhost:8080/api/admin/cities -H "Authorization: Bearer $TOKEN"  # => 40 条，online=true 计 0
```

## Step 4 下架后 GET /api/app/activities

```bash
curl -s -i http://localhost:8081/api/app/activities -H "X-API-Key: $APIKEY"
```

响应头：
```
HTTP/1.1 200 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json
Content-Length: 1323
Date: Tue, 25 Aug 2026 12:00:44 GMT
```
响应体：
```json
[
    {
        "id": "01a038bb-d304-7295-a941-ee48da8726b5",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a038bb-d2f9-7dfb-b023-22f222b5ef96",
        "title": "成都周末",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a038bb-d2db-759c-a2e9-9bb1684aef8e",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a038bb-d2c7-7f70-81ca-d9ebe42806f6",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a038bb-d2bf-777d-b892-f4433f5d169a",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a03851-b2ca-78ea-9e9a-e73e6ea7ae7b",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a03851-b2bf-78fd-ae95-0f6fb5480ab7",
        "title": "成都周末",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a03851-b297-7935-b1ca-a3340cb4fac7",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a03851-b287-7059-b1c5-00d5ee2d721f",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    },
    {
        "id": "01a03851-b27f-77d2-bfe4-653a62f1c206",
        "title": "活动",
        "images": [],
        "tags": [],
        "periods": [],
        "level": null,
        "introduction": null
    }
]
```
断言：200；列表仍含 `01a038bb-d304-7295-a941-ee48da8726b5`；条数与下架前一致（10 → 10）。

## Step 5 下架后 GET /api/app/activities/{activityId}

```bash
curl -s -i "http://localhost:8081/api/app/activities/$AID" -H "X-API-Key: $APIKEY"
```

响应头：
```
HTTP/1.1 200 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json
Content-Length: 276
Date: Tue, 25 Aug 2026 12:00:45 GMT
```
响应体：
```json
{
    "id": "01a038bb-d304-7295-a941-ee48da8726b5",
    "images": [],
    "title": "活动",
    "tags": [],
    "periods": [],
    "level": null,
    "introduction": null,
    "editorNote": null,
    "gatheringPlace": null,
    "dismissalPlace": null,
    "transportation": null,
    "visa": null,
    "landscape": null,
    "itinerary": [],
    "detailHtml": null
}
```
断言：200，id 与请求一致。

## Step 6 环境恢复（非用例步骤）

```bash
for id in $(cat online_cities.txt); do
  curl -s -o /dev/null -w "%{http_code}\n" -X PUT "http://localhost:8080/api/admin/cities/$id/online" \
    -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
done
```
结果：38 个城市全部返回 200（逐条见 `_s6_restore.txt`）；核验 admin 城市 40 条 / online=38，app 端 /api/app/cities 返回 38 条 —— 与测试前完全一致。
本轮未创建任何夹具数据（尝试新建活动被 `图片对象不可用` 400 拒绝，未落库，遂改用既有上线活动），无需删除。
