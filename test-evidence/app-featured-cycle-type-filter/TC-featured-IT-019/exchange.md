# TC-featured-IT-019 组内按排序号升序 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证同 TC-016（`$TOKEN` = admin JWT，`$APP_API_KEY` = app 端 API-key，真机密脱敏）。
objectKey 均为真实 OSS 直传所得（每个条目 banner 各传一张）。前置：本用例开始时周期条目表为空。

## Step 1a: 上架城市 + 上线活动（5 个条目共用同一关联实体，保证「可见性」不构成变量）

```bash
curl -s -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"周期城019","englishName":"CycleCity019","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
# → id=01a034e3-3d74-70f5-9f90-a87c77f48cb5 (online=true)

curl -s -X POST "http://localhost:8080/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a034e3-3d74-70f5-9f90-a87c77f48cb5","images":["<ACTIVITY_IMAGE_OBJECT_KEY>"],"title":"周期活动019","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
# → id=<ACTIVITY_ID> (online=true)
```

## Step 1b: MENSTRUAL 下按 sortOrder 2 → 1 → 3 → 1 → 1 的先后顺序创建 5 个上线条目

```bash
for so in 2 1 3 1 1; do
  curl -s -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
    -d "{\"phase\":\"MENSTRUAL\",\"type\":\"ACTIVITY\",\"activityId\":\"<ACTIVITY_ID>\",\"description\":\"排序条目019-so$so-<rand>\",\"sortOrder\":$so,\"banner\":\"<各自的 objectKey>\",\"online\":true}"
done
```

五次均 HTTP 200，创建顺序与返回（id / sortOrder / createdAt / description）：

```
01a034e3-40a5-7c24-9683-73e4a6d3701a  so=2  2026-08-24T17:48:23.589700781Z  排序条目019-so2-18762
01a034e3-4454-7a0e-a8c5-fb5df9bede37  so=1  2026-08-24T17:48:24.532569176Z  排序条目019-so1-25908
01a034e3-474a-7caa-bd03-2742b5aec260  so=3  2026-08-24T17:48:25.290718180Z  排序条目019-so3-17039
01a034e3-49e4-7d2d-825f-b8d6152d55b1  so=1  2026-08-24T17:48:25.956733853Z  排序条目019-so1-29139
01a034e3-4c8a-7afa-a2ed-e243259bc6cb  so=1  2026-08-24T17:48:26.634630304Z  排序条目019-so1-8702
```

## Step 2: app 端查询，核对 MENSTRUAL 组内顺序

```bash
curl -s -o /tmp/t19.json -w 'code=%{http_code}\n' -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

`code=200`，四周期键齐全，`MENSTRUAL` 共 5 条，实际返回顺序（id + description）：

```
01a034e3-4c8a-7afa-a2ed-e243259bc6cb  排序条目019-so1-8702    (sortOrder=1, createdAt 17:48:26.634)
01a034e3-49e4-7d2d-825f-b8d6152d55b1  排序条目019-so1-29139   (sortOrder=1, createdAt 17:48:25.956)
01a034e3-4454-7a0e-a8c5-fb5df9bede37  排序条目019-so1-25908   (sortOrder=1, createdAt 17:48:24.532)
01a034e3-40a5-7c24-9683-73e4a6d3701a  排序条目019-so2-18762   (sortOrder=2)
01a034e3-474a-7caa-bd03-2742b5aec260  排序条目019-so3-17039   (sortOrder=3)
```

`FOLLICULAR` / `OVULATION` / `LUTEAL` 均为 `[]`。

## Step 3: 收尾清理

```bash
curl -s "http://localhost:8080/api/admin/featured-cycle-items/page?page=0&size=100" -H "Authorization: Bearer $TOKEN" \
  | python3 -c "import sys,json;[print(x['id']) for x in json.load(sys.stdin)['content']]" \
  | while read i; do curl -s -o /dev/null -X DELETE "http://localhost:8080/api/admin/featured-cycle-items/$i" -H "Authorization: Bearer $TOKEN"; done
```

清理后 app 端返回 `{"MENSTRUAL":[],"FOLLICULAR":[],"OVULATION":[],"LUTEAL":[]}`。
