# TC-featured-IT-011 POST /api/admin/featured-cycle-items 关联实体不存在被拒绝 — 请求/响应存证

执行日期: 2026-08-28 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 占位 key；fixture 名带本轮后缀 `g8k` 防撞名。清理请求（DELETE）不入存证。

## 前置: 文章 art011

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art011.png","contentHtml":"<p>正文</p>","title":"art011-g8k","online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a04880-e30b-75ff-ae67-ed1f01f717d5","image":{"id":"bound/art011.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art011.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE"},"title":"art011-g8k","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":0,"categoryIds":[],"online":true,"createdAt":"2026-08-28T13:13:21.419327571Z","updatedAt":"2026-08-28T13:13:21.419327571Z"}
```

## 前置: GET page 基线计数

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=1&size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"content":[],"page":1,"size":30,"totalElements":0,"totalPages":0}
```

## Step 1: ACTIVITY targetId 不存在

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"a270de73-d79e-4845-8b7d-dea50cbac199","description":"d","banner":"images/b.png"}'
```

实际响应: HTTP 400（Content-Type: application/json）

```json
{"status":400,"error":"Bad Request","message":"关联活动不存在：a270de73-d79e-4845-8b7d-dea50cbac199","path":"/api/admin/featured-cycle-items"}
```

## Step 2: ROUTE targetId 不存在

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"OVULATION","type":"ROUTE","targetId":"a270de73-d79e-4845-8b7d-dea50cbac199","title":"t","subtitle":"s","description":"d","banner":"images/b.png"}'
```

实际响应: HTTP 400（Content-Type: application/json）

```json
{"status":400,"error":"Bad Request","message":"关联路线不存在：a270de73-d79e-4845-8b7d-dea50cbac199","path":"/api/admin/featured-cycle-items"}
```

## Step 3: ARTICLE targetId 不存在

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"LUTEAL","type":"ARTICLE","targetId":"a270de73-d79e-4845-8b7d-dea50cbac199","title":"t","banner":"images/b.png"}'
```

实际响应: HTTP 400（Content-Type: application/json）

```json
{"status":400,"error":"Bad Request","message":"关联文章不存在：a270de73-d79e-4845-8b7d-dea50cbac199","path":"/api/admin/featured-cycle-items"}
```

## Step 4: ACTIVITY targetId 传已存在文章 id（跨表不命中）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase":"MENSTRUAL","type":"ACTIVITY","targetId":"01a04880-e30b-75ff-ae67-ed1f01f717d5","description":"d","banner":"images/b.png"}'
```

实际响应: HTTP 400（Content-Type: application/json）

```json
{"status":400,"error":"Bad Request","message":"关联活动不存在：01a04880-e30b-75ff-ae67-ed1f01f717d5","path":"/api/admin/featured-cycle-items"}
```

## 复核: GET page 计数

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?page=1&size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"content":[],"page":1,"size":30,"totalElements":0,"totalPages":0}
```
