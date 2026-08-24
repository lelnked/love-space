# TC-featured-IT-018 大使下线连带隐藏路线类条目 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证同 TC-016（`$TOKEN` = admin JWT，`$APP_API_KEY` = app 端 API-key，真机密脱敏）。
objectKey 均为真实 OSS 直传所得。前置：本用例开始时周期条目表为空。

## Step 1a: 上架城市 + 上线大使

```bash
curl -s -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"周期城018","englishName":"CycleCity018","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
# → id=01a034e2-f75d-7116-8ea7-c66cf0beb6f4 (online=true)

curl -s -X POST "http://localhost:8080/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/01a034e2-ec59-793b-b538-798910185171.png","name":"大使018","tags":["户外"],"online":true}'
# → id=01a034e2-f822-7f07-9dab-a2741685566c (online=true)
```

## Step 1b: 创建路线（关联该上线大使）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"title":"路线018","cityName":"周期城018","thumbnail":"images/01a034e2-f1b4-75ea-a00a-8e3ec6e0aad3.png","images":["images/01a034e2-f368-7f16-831d-5a0428c322fc.png"],"ambassadorId":"01a034e2-f822-7f07-9dab-a2741685566c","ambassadorNote":"大使说","travelTime":"3天","season":"春","travelStatus":"轻松"}'
```

实际响应（HTTP 200）:

```json
{"id":"01a034e2-faa7-7299-8368-ba68290e2692","sortOrder":0,"title":"路线018","cityName":"周期城018","ambassadorNote":"大使说","thumbnail":{"id":"bound/01a034e2-f1b4-75ea-a00a-8e3ec6e0aad3.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034e2-f1b4-75ea-a00a-8e3ec6e0aad3.png?Expires=1787595485&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zhvFYMEoMOTGEJK2P0gyGnw%2F02k%3D"},"images":[{"id":"bound/01a034e2-f368-7f16-831d-5a0428c322fc.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034e2-f368-7f16-831d-5a0428c322fc.png?Expires=1787595485&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7iTU78P2hMgfJXfRuSQGHM6TylI%3D"}],"travelTime":"3天","season":"春","travelStatus":"轻松","ambassadorName":"大使018","spots":[],"createdAt":"2026-08-24T17:48:05.669751074Z","updatedAt":"2026-08-24T17:48:05.669751074Z"}
```

> 注：路线已按 `route-decouple-city-online` 解除与城市实体的关联，请求字段为 `cityName`（文本），响应无 `cityId`。

## Step 1c: OVULATION 下建上线 ROUTE 条目

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"OVULATION","type":"ROUTE","routeId":"01a034e2-faa7-7299-8368-ba68290e2692","title":"路线条目018","subtitle":"副标题018","description":"路线推荐018","banner":"images/01a034e2-f56c-7b26-8d35-41f953e0b9c2.png","online":true}'
```

实际响应（HTTP 200）:

```json
{"id":"01a034e2-fb6d-7e02-88f8-9d186973b327","phase":"OVULATION","type":"ROUTE","sortOrder":0,"online":true,"activityId":null,"routeId":"01a034e2-faa7-7299-8368-ba68290e2692","articleId":null,"relatedTitle":"路线018","title":"路线条目018","subtitle":"副标题018","description":"路线推荐018","note":null,"banner":{"id":"bound/01a034e2-f56c-7b26-8d35-41f953e0b9c2.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034e2-f56c-7b26-8d35-41f953e0b9c2.png?Expires=1787595485&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qtfIkg6tDYj1%2FDHTr44wLrrC%2Bf8%3D"},"createdAt":"2026-08-24T17:48:05.869817316Z","updatedAt":"2026-08-24T17:48:05.869817316Z"}
```

## Step 2: 基线查询（条目在 OVULATION 分组）

```bash
curl -s -o /dev/null -w 'code=%{http_code}\n' -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

`code=200`；四周期键齐全；`OVULATION` = 1 条：id `01a034e2-fb6d-...b327`，type `ROUTE`，routeId `01a034e2-faa7-7299-8368-ba68290e2692`。

## Step 3: 大使下线 → 条目从 OVULATION 消失

```bash
curl -s -X PUT "http://localhost:8080/api/admin/ambassadors/01a034e2-f822-7f07-9dab-a2741685566c/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
curl -s -o /dev/null -w 'code=%{http_code}\n' -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

`code=200`；四周期键齐全；`OVULATION` = `[]`。

## Step 4: 大使恢复上线 → 条目重新出现

```bash
curl -s -X PUT "http://localhost:8080/api/admin/ambassadors/01a034e2-f822-7f07-9dab-a2741685566c/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
curl -s -o /dev/null -w 'code=%{http_code}\n' -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

`code=200`；`OVULATION` = 1 条，id/type/routeId 与 Step 2 完全一致。

## Step 5: 收尾清理

```bash
curl -s -o /dev/null -w '%{http_code}\n' -X DELETE "http://localhost:8080/api/admin/featured-cycle-items/01a034e2-fb6d-7e02-88f8-9d186973b327" -H "Authorization: Bearer $TOKEN"
# → 200
```
