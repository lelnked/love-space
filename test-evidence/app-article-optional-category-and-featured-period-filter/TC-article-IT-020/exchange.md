# TC-article-IT-020 GET /api/app/articles 不传 categoryId 返回全部可见文章 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 直接接受的占位 key（该实例不校验 OSS 对象存在），fixture 名带本轮后缀 `3u50` 防撞名。

## 前置: 栏目 A

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"cat020A-3u50","icon":"images/cat020A.png","sortOrder":1}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-44c2-7958-8bae-412a0e0d1a05","name":"cat020A-3u50","icon":{"id":"bound/cat020A.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/cat020A.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qJ4bHFzPVjUErmJltT36UeiNe7Y%3D"},"sortOrder":1,"createdAt":"2026-08-25T12:55:17.442526613Z","updatedAt":"2026-08-25T12:55:17.442526613Z"}
```

## 前置: 栏目 B

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"cat020B-3u50","icon":"images/cat020B.png","sortOrder":2}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-44cd-76cf-8a23-68b8b7ab9a34","name":"cat020B-3u50","icon":{"id":"bound/cat020B.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/cat020B.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=sK5USinUPiDOsHdPONe5fhuirQY%3D"},"sortOrder":2,"createdAt":"2026-08-25T12:55:17.45337275Z","updatedAt":"2026-08-25T12:55:17.45337275Z"}
```

## 前置: 栏目 C

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"cat020C-3u50","icon":"images/cat020C.png","sortOrder":3}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-44d7-7edb-948b-10afb55b2c7a","name":"cat020C-3u50","icon":{"id":"bound/cat020C.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/cat020C.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=nK%2BW%2FLA327aZCq1w3E57NcqBfPs%3D"},"sortOrder":3,"createdAt":"2026-08-25T12:55:17.463845025Z","updatedAt":"2026-08-25T12:55:17.463845025Z"}
```

## 前置: 甲 关联 A sortOrder=2 online

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-6.png","contentHtml":"<p>正文</p>","title":"甲020-3u50","coverTitle":"封面甲020","subtitle":"副甲","tags":["t1"],"categoryIds":["01a038fd-44c2-7958-8bae-412a0e0d1a05"],"sortOrder":2,"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-44e2-7f66-8f0f-44bee342db5e","image":{"id":"bound/art-6.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-6.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DwQ0ErBmRQ2kOUZyN4iU4m2cEww%3D"},"title":"甲020-3u50","coverTitle":"封面甲020","subtitle":"副甲","intro":null,"tags":["t1"],"contentHtml":"<p>正文</p>","sortOrder":2,"categoryIds":["01a038fd-44c2-7958-8bae-412a0e0d1a05"],"online":true,"createdAt":"2026-08-25T12:55:17.474916913Z","updatedAt":"2026-08-25T12:55:17.474916913Z"}
```

## 前置: 乙 关联 B sortOrder=1 online

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-7.png","contentHtml":"<p>正文</p>","title":"乙020-3u50","subtitle":"副乙","categoryIds":["01a038fd-44cd-76cf-8a23-68b8b7ab9a34"],"sortOrder":1,"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-44ee-76ac-8fc0-00bb760316fb","image":{"id":"bound/art-7.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-7.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dl%2FU3SqMEUiLJVkqSaoxIgiY2Xc%3D"},"title":"乙020-3u50","coverTitle":null,"subtitle":"副乙","intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a038fd-44cd-76cf-8a23-68b8b7ab9a34"],"online":true,"createdAt":"2026-08-25T12:55:17.48637722Z","updatedAt":"2026-08-25T12:55:17.48637722Z"}
```

## 前置: 丙 关联 A online=false

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-8.png","contentHtml":"<p>正文</p>","title":"丙020-3u50","categoryIds":["01a038fd-44c2-7958-8bae-412a0e0d1a05"],"sortOrder":1,"online":false}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-44f9-7f2c-ba05-f791ccedeae9","image":{"id":"bound/art-8.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-8.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Jqe79FKDkSGfRhovV1J8P81tXFo%3D"},"title":"丙020-3u50","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a038fd-44c2-7958-8bae-412a0e0d1a05"],"online":false,"createdAt":"2026-08-25T12:55:17.497882525Z","updatedAt":"2026-08-25T12:55:17.497882525Z"}
```

## 前置: 丁 仅关联 C online

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-9.png","contentHtml":"<p>正文</p>","title":"丁020-3u50","categoryIds":["01a038fd-44d7-7edb-948b-10afb55b2c7a"],"sortOrder":1,"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-4505-7535-8dd8-cc882233447c","image":{"id":"bound/art-9.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-9.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VHAT2j1Pow4fI5uPE1esAD2duHc%3D"},"title":"丁020-3u50","coverTitle":null,"subtitle":null,"intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a038fd-44d7-7edb-948b-10afb55b2c7a"],"online":true,"createdAt":"2026-08-25T12:55:17.509284618Z","updatedAt":"2026-08-25T12:55:17.509284618Z"}
```

## 前置: DELETE 栏目 C

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/article-categories/01a038fd-44d7-7edb-948b-10afb55b2c7a" -H "Authorization: Bearer $TOKEN"
```

实际响应: HTTP 200（Content-Type: -）

```json
(空)
```

## Step 2: GET /api/app/articles（不带 categoryId）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/articles" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038cf-82ac-7d36-b944-9804b6179213","image":{"id":"bound/images/cover.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=hNJZZnlyomlDRIrykTHiTxVTaXo%3D"},"coverTitle":"只有标题","title":"只有标题","subtitle":null,"tags":["甲","乙"]},{"id":"01a038cf-82a5-770b-b4bf-6b3cf464a6f6","image":{"id":"bound/images/cover.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=hNJZZnlyomlDRIrykTHiTxVTaXo%3D"},"coverTitle":"封面标题","title":"详情页标题","subtitle":"副标题","tags":["约会","周末"]},{"id":"01a038cf-828e-738c-a1f2-8dddd12ea7ac","image":{"id":"bound/images/a.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/a.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=oZU%2BSo0eKzAfr2PKdAxfVmHcQEY%3D"},"coverTitle":"开关文章","title":"开关文章","subtitle":null,"tags":[]},{"id":"01a038bb-d23e-7d7c-9d55-e3784880ef32","image":{"id":"bound/images/cover.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=hNJZZnlyomlDRIrykTHiTxVTaXo%3D"},"coverTitle":"只有标题","title":"只有标题","subtitle":null,"tags":["甲","乙"]},{"id":"01a038bb-d238-7747-b1d9-710d9492f6cf","image":{"id":"bound/images/cover.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=hNJZZnlyomlDRIrykTHiTxVTaXo%3D"},"coverTitle":"封面标题","title":"详情页标题","subtitle":"副标题","tags":["约会","周末"]},{"id":"01a038bb-d21d-7146-8897-b63101258cc2","image":{"id":"bound/images/a.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/a.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=oZU%2BSo0eKzAfr2PKdAxfVmHcQEY%3D"},"coverTitle":"开关文章","title":"开关文章","subtitle":null,"tags":[]},{"id":"01a03851-b203-7469-9bd2-2c16f7fbbde2","image":{"id":"bound/images/cover.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=hNJZZnlyomlDRIrykTHiTxVTaXo%3D"},"coverTitle":"只有标题","title":"只有标题","subtitle":null,"tags":["甲","乙"]},{"id":"01a03851-b1fc-7856-ace1-1192bef4676d","image":{"id":"bound/images/cover.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=hNJZZnlyomlDRIrykTHiTxVTaXo%3D"},"coverTitle":"封面标题","title
…(截断，共 13047 字符)
```

## Step 3: GET /api/app/articles?categoryId=A

```bash
curl -s -i -X GET "http://localhost:8081/api/app/articles?categoryId=01a038fd-44c2-7958-8bae-412a0e0d1a05" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-44e2-7f66-8f0f-44bee342db5e","image":{"id":"bound/art-6.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/art-6.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=dYHQfE1HA89P56GMqDqHIoSSvII%3D"},"coverTitle":"封面甲020","title":"甲020-3u50","subtitle":"副甲","tags":["t1"]}]
```
