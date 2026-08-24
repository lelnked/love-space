# TC-featured-IT-021 GET /api/app/featured-cycle-items?type= 按内容类型过滤 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证同 TC-016（`$TOKEN` = admin JWT，`$APP_API_KEY` = app 端 API-key，真机密脱敏）。
objectKey 均为真实 OSS 直传所得（POST /api/admin/files/upload-credentials → 表单直传，带 `success_action_status=200`）。
前置：本用例开始时周期条目表为空（app 四分组均为 `[]`）。

## Step 1a: 上架城市 + 上线活动 + 上线大使 + 路线 + 上线文章

```bash
curl -s -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"周期城021","englishName":"CycleCity021","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
# → id=01a034e4-04a4-783b-977b-b7ee30cc5f8e (online=true)

curl -s -X POST "http://localhost:8080/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a034e4-04a4-783b-977b-b7ee30cc5f8e","images":["<ACTIVITY_IMAGE_OBJECT_KEY>"],"title":"活动021","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
# → id=01a034e4-0562-7a64-85df-530c7c692442 (online=true)

curl -s -X POST "http://localhost:8080/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/01a034e3-f75a-719e-a3f9-91faf7e91a9e.png","name":"大使021","online":true}'
# → id=01a034e4-061f-7434-86af-4f1d87b24096 (online=true)

curl -s -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"title":"路线021","cityName":"周期城021","thumbnail":"<ROUTE_THUMB_OBJECT_KEY>","images":["<ROUTE_IMAGE_OBJECT_KEY>"],"ambassadorId":"01a034e4-061f-7434-86af-4f1d87b24096"}'
# → id=01a034e4-0782-7bea-8047-de3ec49ec4b4

curl -s -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"<ARTICLE_IMAGE_OBJECT_KEY>","title":"文章021","contentHtml":"<p>正文</p>","online":true}'
# → id=01a034e4-0840-7ac8-8694-fb103018e302 (online=true)
```

## Step 1b: MENSTRUAL 下各建 1 个上线条目（ACTIVITY / ROUTE / ARTICLE）

```bash
curl -s -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a034e4-0562-7a64-85df-530c7c692442","description":"活动条目021","banner":"<BANNER_A>","online":true}'
# → id=01a034e4-08fe-7a36-bba0-36f8fa2cec87

curl -s -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ROUTE","routeId":"01a034e4-0782-7bea-8047-de3ec49ec4b4","title":"路线条目021","subtitle":"副021","description":"路线推荐021","banner":"<BANNER_R>","online":true}'
# → id=01a034e4-09aa-737d-8647-6efc8dfc1d3b

curl -s -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ARTICLE","articleId":"01a034e4-0840-7ac8-8694-fb103018e302","title":"文章条目021","banner":"images/01a034e4-02a3-714d-aedf-cd7f8cc10fa4.png","online":true}'
# → id=01a034e4-0a56-7922-a178-d53e2f076a51
```

三次均 HTTP 200。

## Step 2: 按 type=ARTICLE 过滤

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?type=ARTICLE" -H "X-API-Key: $APP_API_KEY"
```

`HTTP 200`，实际响应体:

```json
{"MENSTRUAL":[{"id":"01a034e4-0a56-7922-a178-d53e2f076a51","type":"ARTICLE","banner":{"id":"bound/01a034e4-02a3-714d-aedf-cd7f8cc10fa4.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034e4-02a3-714d-aedf-cd7f8cc10fa4.png?Expires=1787595555&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=bSeTxThUWistRta4icpEfu65sQc%3D"},"activityId":null,"routeId":null,"articleId":"01a034e4-0840-7ac8-8694-fb103018e302","title":"文章条目021","subtitle":null,"description":null,"note":null}],"FOLLICULAR":[],"OVULATION":[],"LUTEAL":[]}
```

## Step 3: 不带 type，同一批数据（回归：行为不变）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

`HTTP 200`，`MENSTRUAL` 共 3 条，(type, activityId, routeId, articleId) 依次为:

```
('ARTICLE',  None,                                   None,                                   '01a034e4-0840-7ac8-8694-fb103018e302')
('ROUTE',    None,                                   '01a034e4-0782-7bea-8047-de3ec49ec4b4', None)
('ACTIVITY', '01a034e4-0562-7a64-85df-530c7c692442', None,                                   None)
```

`FOLLICULAR` / `OVULATION` / `LUTEAL` 均为 `[]`。
