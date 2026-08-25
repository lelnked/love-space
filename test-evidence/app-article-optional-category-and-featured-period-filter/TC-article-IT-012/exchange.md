# TC-article-IT-012 GET /api/app/articles 下线文章不可见、详情 404 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 直接接受的占位 key（该实例不校验 OSS 对象存在），fixture 名带本轮后缀 `3u50` 防撞名。

## 前置: 栏目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"cat012-3u50","icon":"images/cat012.png","sortOrder":5}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-443a-71db-95a2-924a1b3de29c","name":"cat012-3u50","icon":{"id":"bound/cat012.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/cat012.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zhJJ%2B8ogzcixykP%2BV9fZ2ehjtSA%3D"},"sortOrder":5,"createdAt":"2026-08-25T12:55:17.306048028Z","updatedAt":"2026-08-25T12:55:17.306048028Z"}
```

## 前置: 上线文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-3.png","contentHtml":"<p>正文</p>","title":"文章012-3u50","categoryIds":["01a038fd-443a-71db-95a2-924a1b3de29c"],"sortOrder":1,"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4446-7717-a7c4-f380f0ffad3f","image":{"id":"bound/art-3.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-3.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=yjLwEXyIaoLcWJy525%2BBTcjdQH4%3D"},"title":"文章012-3u50","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a038fd-443a-71db-95a2-924a1b3de29c"],"online":true,"createdAt":"2026-08-25T12:55:17.318385502Z","updatedAt":"2026-08-25T12:55:17.318385502Z"}
```

## Step 1: 列表可见

```bash
curl -s -i -X GET "http://localhost:8081/api/app/articles?categoryId=01a038fd-443a-71db-95a2-924a1b3de29c" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-4446-7717-a7c4-f380f0ffad3f","image":{"id":"bound/art-3.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/art-3.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=62rBoLUkwfgZ1yFr352Z3HBhRAE%3D"},"coverTitle":"文章012-3u50","title":"文章012-3u50","subtitle":null,"tags":[]}]
```

## Step 2: admin 下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a038fd-4446-7717-a7c4-f380f0ffad3f/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4446-7717-a7c4-f380f0ffad3f","image":{"id":"bound/art-3.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-3.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=yjLwEXyIaoLcWJy525%2BBTcjdQH4%3D"},"title":"文章012-3u50","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a038fd-443a-71db-95a2-924a1b3de29c"],"online":false,"createdAt":"2026-08-25T12:55:17.318386Z","updatedAt":"2026-08-25T12:55:17.347724503Z"}
```

## Step 3: 列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/articles?categoryId=01a038fd-443a-71db-95a2-924a1b3de29c" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[]
```

## Step 4: 详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/articles/01a038fd-4446-7717-a7c4-f380f0ffad3f" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 404（Content-Type: application/json）

```json
{"status":404,"error":"Not Found","message":"article not found: 01a038fd-4446-7717-a7c4-f380f0ffad3f","path":"/api/app/articles/01a038fd-4446-7717-a7c4-f380f0ffad3f"}
```
