# TC-featured-IT-020 城市未上架不影响路线类条目 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证同 TC-016（`$TOKEN` = admin JWT，`$APP_API_KEY` = app 端 API-key，真机密脱敏）。
objectKey 均为真实 OSS 直传所得。前置：本用例开始时周期条目表为空。

## Step 1a: 创建**下架**城市

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"下架城020","englishName":"OfflineCity020","chineseProvince":"测试省","englishProvince":"Test Province","online":false}'
```

实际响应（HTTP 200）:

```json
{"id":"01a034e3-9457-7c06-9631-f197d5346280","chineseName":"下架城020","englishName":"OfflineCity020","chineseProvince":"测试省","englishProvince":"Test Province","backgroundImage":null,"editorNote":null,"online":false,"createdAt":"2026-08-24T17:48:45.015695538Z","updatedAt":"2026-08-24T17:48:45.015695538Z"}
```

## Step 1b: 上线大使 + 该城市名下的路线 + 该城市下的上线活动

```bash
curl -s -X POST "http://localhost:8080/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/01a034e3-8856-7234-acfe-3b64335fe413.png","name":"大使020","online":true}'
# → id=01a034e3-9512-7ac5-ad76-6cec37d10c26 (online=true)

curl -s -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"title":"路线020","cityName":"下架城020","thumbnail":"<ROUTE_THUMB_OBJECT_KEY>","images":["<ROUTE_IMAGE_OBJECT_KEY>"],"ambassadorId":"01a034e3-9512-7ac5-ad76-6cec37d10c26"}'
# → id=01a034e3-9673-76ba-9893-e5279eba473a

curl -s -X POST "http://localhost:8080/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a034e3-9457-7c06-9631-f197d5346280","images":["<ACTIVITY_IMAGE_OBJECT_KEY>"],"title":"活动020","periods":["OVULATION"],"level":"L1","introduction":"介绍","online":true}'
# → id=01a034e3-9734-7f95-ae9e-c678fbb839ba (online=true，但所属城市 online=false)
```

> 路线已解除城市实体关联，`cityName` 为纯文本；活动仍通过 `cityId` 关联城市实体。

## Step 1c: OVULATION 下建 1 个上线 ROUTE 条目 + 1 个上线 ACTIVITY 条目

```bash
curl -s -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"OVULATION","type":"ROUTE","routeId":"01a034e3-9673-76ba-9893-e5279eba473a","title":"路线条目020","subtitle":"副020","description":"路线推荐020","banner":"<BANNER_1>","online":true}'
# → id=01a034e3-97f2-76bf-98f3-df17e2bdf0be

curl -s -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"OVULATION","type":"ACTIVITY","activityId":"01a034e3-9734-7f95-ae9e-c678fbb839ba","description":"活动条目020","banner":"<BANNER_2>","online":true}'
# → id=01a034e3-98b0-7ba4-8dc1-61294de7994c
```

## Step 2: 城市仍下架时查询

```bash
curl -s -o /tmp/t20_2.json -w 'code=%{http_code}\n' -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

`code=200`，`OVULATION` 实际内容（id 尾段 / type / title / description）:

```
[('bdf0be', 'ROUTE', '路线条目020', '路线推荐020')]
```

即 ROUTE 条目正常下发；同城市的 ACTIVITY 条目 `...e7994c` **不出现**。

## Step 3: 城市上架后再次查询

```bash
curl -s -X PUT "http://localhost:8080/api/admin/cities/01a034e3-9457-7c06-9631-f197d5346280/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
curl -s -o /tmp/t20_3.json -w 'code=%{http_code}\n' -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

`code=200`，`OVULATION` 实际内容:

```
[('e7994c', 'ACTIVITY', None, '活动条目020'), ('bdf0be', 'ROUTE', '路线条目020', '路线推荐020')]
```

两条均出现；ROUTE 条目 `...bdf0be` 的 id/type/title/description 与 Step 2 完全一致。

## Step 4: 收尾清理

```bash
curl -s "http://localhost:8080/api/admin/featured-cycle-items/page?page=0&size=100" -H "Authorization: Bearer $TOKEN" \
  | python3 -c "import sys,json;[print(x['id']) for x in json.load(sys.stdin)['content']]" \
  | while read i; do curl -s -o /dev/null -X DELETE "http://localhost:8080/api/admin/featured-cycle-items/$i" -H "Authorization: Bearer $TOKEN"; done
```

清理后 app 端返回 `{"MENSTRUAL":[],"FOLLICULAR":[],"OVULATION":[],"LUTEAL":[]}`。
