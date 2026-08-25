# TC-article-IT-018 GET /api/app/articles 未设封面标题时回落文章标题 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 直接接受的占位 key（该实例不校验 OSS 对象存在），fixture 名带本轮后缀 `3u50` 防撞名。

## 前置: 栏目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"cat018-3u50","icon":"images/cat018.png","sortOrder":5}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4492-7f4a-bb89-19681698fc2a","name":"cat018-3u50","icon":{"id":"bound/cat018.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/cat018.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7hSX3ENxoiRI2IEjkYdGDeYBn%2FM%3D"},"sortOrder":5,"createdAt":"2026-08-25T12:55:17.394863018Z","updatedAt":"2026-08-25T12:55:17.394863018Z"}
```

## 前置: 甲 coverTitle=封面甲 tags=[约会]

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-4.png","contentHtml":"<p>正文</p>","title":"文章甲-3u50","coverTitle":"封面甲","tags":["约会"],"subtitle":"副甲","categoryIds":["01a038fd-4492-7f4a-bb89-19681698fc2a"],"sortOrder":1,"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-449e-7d92-9778-4bf5c7f777c6","image":{"id":"bound/art-4.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-4.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=A8YluMq7RZY%2FAtX%2BnKGfTQTVh6w%3D"},"title":"文章甲-3u50","coverTitle":"封面甲","subtitle":"副甲","intro":null,"tags":["约会"],"contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a038fd-4492-7f4a-bb89-19681698fc2a"],"online":true,"createdAt":"2026-08-25T12:55:17.406799036Z","updatedAt":"2026-08-25T12:55:17.406799036Z"}
```

## 前置: 乙 不设 coverTitle

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-5.png","contentHtml":"<p>正文</p>","title":"文章乙","subtitle":"副乙","categoryIds":["01a038fd-4492-7f4a-bb89-19681698fc2a"],"sortOrder":2,"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-44ab-7225-b511-52da36346710","image":{"id":"bound/art-5.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-5.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=UQHLdcrlzQ8xyXjtSt6%2FOWCOM%2Bs%3D"},"title":"文章乙","coverTitle":null,"subtitle":"副乙","intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":2,"categoryIds":["01a038fd-4492-7f4a-bb89-19681698fc2a"],"online":true,"createdAt":"2026-08-25T12:55:17.419085202Z","updatedAt":"2026-08-25T12:55:17.419085202Z"}
```

## Step 2: GET 列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/articles?categoryId=01a038fd-4492-7f4a-bb89-19681698fc2a" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-449e-7d92-9778-4bf5c7f777c6","image":{"id":"bound/art-4.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/art-4.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=4IvJZymioUed5PXoibZY8nPuEtg%3D"},"coverTitle":"封面甲","title":"文章甲-3u50","subtitle":"副甲","tags":["约会"]},{"id":"01a038fd-44ab-7225-b511-52da36346710","image":{"id":"bound/art-5.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/art-5.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=cMMLyq6TNrTdAFd%2BK2ZKaFsnZlA%3D"},"coverTitle":"文章乙","title":"文章乙","subtitle":"副乙","tags":[]}]
```
