# TC-featured-IT-017 关联实体不可见时条目不下发 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证同 TC-016（`$TOKEN` = admin JWT，`$APP_API_KEY` = app 端 API-key，真机密脱敏）。
objectKey 为真实 OSS 直传所得（`<ARTICLE_IMAGE_OBJECT_KEY>` 处的键值因该文章在 Step 5 被删除而未留存，其余均为实际值）。前置：本用例开始时周期条目表为空。

## Step 1a: 上架城市 + 上线活动 + 上线文章

```bash
curl -s -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"周期城017","englishName":"CycleCity017","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
# → id=01a034e2-603d-7fd4-b50b-a78ec3c7658b (online=true)

curl -s -X POST "http://localhost:8080/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a034e2-603d-7fd4-b50b-a78ec3c7658b","images":["images/01a034e2-5395-7d39-b70f-4c0260868c45.png"],"title":"周期活动017","periods":["MENSTRUAL"],"level":"L1","introduction":"介绍","online":true}'
# → id=01a034e2-60ff-7d12-960f-976f789d8171 (online=true)

curl -s -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"<ARTICLE_IMAGE_OBJECT_KEY>","title":"周期文章017","contentHtml":"<p>正文</p>","online":true}'
# → id=01a034e2-61be-7ab5-8a60-f8031465fece (online=true)
```

三次均 HTTP 200。

## Step 1b: MENSTRUAL 下建两个上线条目（ACTIVITY + ARTICLE）

```bash
curl -s -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","activityId":"01a034e2-60ff-7d12-960f-976f789d8171","description":"活动条目017","banner":"images/01a034e2-5c35-7125-959e-e2abbe888a0b.png","online":true}'
# → id=01a034e2-6287-7711-9209-7ecbe7eb7e18

curl -s -X POST "http://localhost:8080/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ARTICLE","articleId":"01a034e2-61be-7ab5-8a60-f8031465fece","title":"文章条目017","banner":"images/01a034e2-5e69-7643-a423-f3c4ee9e6e2c.png","online":true}'
# → id=01a034e2-634e-7744-a192-b831ed662f52
```

## Step 1c: 基线查询（两条都在）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

HTTP 200:

```json
{"MENSTRUAL":[{"id":"01a034e2-634e-7744-a192-b831ed662f52","type":"ARTICLE","banner":{"id":"bound/01a034e2-5e69-7643-a423-f3c4ee9e6e2c.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034e2-5e69-7643-a423-f3c4ee9e6e2c.png?Expires=1787595446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=OpmoE%2FeKhB1wFoSgS3IVJJ6B374%3D"},"activityId":null,"routeId":null,"articleId":"01a034e2-61be-7ab5-8a60-f8031465fece","title":"文章条目017","subtitle":null,"description":null,"note":null},{"id":"01a034e2-6287-7711-9209-7ecbe7eb7e18","type":"ACTIVITY","banner":{"id":"bound/01a034e2-5c35-7125-959e-e2abbe888a0b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034e2-5c35-7125-959e-e2abbe888a0b.png?Expires=1787595446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6g0%2FOivlsXYU0xBvARyc4azvEG8%3D"},"activityId":"01a034e2-60ff-7d12-960f-976f789d8171","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目017","note":null}],"FOLLICULAR":[],"OVULATION":[],"LUTEAL":[]}
```

## Step 2: 活动下线 → ACTIVITY 条目消失

```bash
curl -s -X PUT "http://localhost:8080/api/admin/activities/01a034e2-60ff-7d12-960f-976f789d8171/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
curl -s -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

HTTP 200:

```json
{"MENSTRUAL":[{"id":"01a034e2-634e-7744-a192-b831ed662f52","type":"ARTICLE","banner":{"id":"bound/01a034e2-5e69-7643-a423-f3c4ee9e6e2c.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034e2-5e69-7643-a423-f3c4ee9e6e2c.png?Expires=1787595447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=PShEwg%2Ba6LkGRDVNa%2BuxhJt2Lis%3D"},"activityId":null,"routeId":null,"articleId":"01a034e2-61be-7ab5-8a60-f8031465fece","title":"文章条目017","subtitle":null,"description":null,"note":null}],"FOLLICULAR":[],"OVULATION":[],"LUTEAL":[]}
```

## Step 3: 恢复活动上线、改把所属城市下架 → ACTIVITY 条目仍不下发

```bash
curl -s -X PUT "http://localhost:8080/api/admin/activities/01a034e2-60ff-7d12-960f-976f789d8171/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
curl -s -X PUT "http://localhost:8080/api/admin/cities/01a034e2-603d-7fd4-b50b-a78ec3c7658b/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
curl -s -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

HTTP 200，body 与 Step 2 相同（仅剩 ARTICLE 条目 `01a034e2-634e-...662f52`）。

## Step 4: 恢复城市上架、改把文章下线 → ARTICLE 条目消失，ACTIVITY 条目回归

```bash
curl -s -X PUT "http://localhost:8080/api/admin/cities/01a034e2-603d-7fd4-b50b-a78ec3c7658b/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
curl -s -X PUT "http://localhost:8080/api/admin/articles/01a034e2-61be-7ab5-8a60-f8031465fece/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
curl -s -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
```

HTTP 200:

```json
{"MENSTRUAL":[{"id":"01a034e2-6287-7711-9209-7ecbe7eb7e18","type":"ACTIVITY","banner":{"id":"bound/01a034e2-5c35-7125-959e-e2abbe888a0b.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034e2-5c35-7125-959e-e2abbe888a0b.png?Expires=1787595447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=KeUr%2BMg83R%2Fm%2BGSbV2MKSwODyt0%3D"},"activityId":"01a034e2-60ff-7d12-960f-976f789d8171","routeId":null,"articleId":null,"title":null,"subtitle":null,"description":"活动条目017","note":null}],"FOLLICULAR":[],"OVULATION":[],"LUTEAL":[]}
```

## Step 5: 恢复文章上线后直接删除文章 → ARTICLE 条目仍不下发，接口不 500

```bash
curl -s -X PUT "http://localhost:8080/api/admin/articles/01a034e2-61be-7ab5-8a60-f8031465fece/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
curl -s -o /dev/null -w '%{http_code}\n' -X DELETE "http://localhost:8080/api/admin/articles/01a034e2-61be-7ab5-8a60-f8031465fece" -H "Authorization: Bearer $TOKEN"
# → 200
curl -s -o /dev/null -w '%{http_code}\n' -X GET "http://localhost:8081/api/app/featured-cycle-items" -H "X-API-Key: $APP_API_KEY"
# → 200
```

HTTP 200，body 与 Step 4 相同（仅剩 ACTIVITY 条目），未因关联文章被删而 500。

## Step 6: 收尾清理

```bash
for id in 01a034e2-6287-7711-9209-7ecbe7eb7e18 01a034e2-634e-7744-a192-b831ed662f52; do
  curl -s -o /dev/null -w '%{http_code}\n' -X DELETE "http://localhost:8080/api/admin/featured-cycle-items/$id" -H "Authorization: Bearer $TOKEN"
done
# → 200 200
```
