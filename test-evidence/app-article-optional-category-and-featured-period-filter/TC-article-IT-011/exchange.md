# TC-article-IT-011 GET /api/app/article-categories 与 /api/app/articles 均按权重升序 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 直接接受的占位 key（该实例不校验 OSS 对象存在），fixture 名带本轮后缀 `3u50` 防撞名。

## 前置: 栏目 A sortOrder=2

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"cat011A-3u50","icon":"images/cat011A.png","sortOrder":2}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-43c4-71e9-9725-49fc76288064","name":"cat011A-3u50","icon":{"id":"bound/cat011A.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/cat011A.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=682pRX%2Bkj8Zee7pmuhvqWUibLkI%3D"},"sortOrder":2,"createdAt":"2026-08-25T12:55:17.188043765Z","updatedAt":"2026-08-25T12:55:17.188043765Z"}
```

## 前置: 栏目 B sortOrder=1

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"cat011B-3u50","icon":"images/cat011B.png","sortOrder":1}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-43cf-79f6-998d-9049f129bd38","name":"cat011B-3u50","icon":{"id":"bound/cat011B.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/cat011B.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Z23R5UsuGlI7rsN0zEUX7eOF1u0%3D"},"sortOrder":1,"createdAt":"2026-08-25T12:55:17.199567653Z","updatedAt":"2026-08-25T12:55:17.199567653Z"}
```

## 前置: B 下文章 sortOrder=3

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-1.png","contentHtml":"<p>正文</p>","title":"文章011-权重3-3u50","subtitle":"副3","categoryIds":["01a038fd-43cf-79f6-998d-9049f129bd38"],"sortOrder":3,"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-43e2-70c9-96f5-f1e085e84393","image":{"id":"bound/art-1.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-1.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7zuGsZPhyW6YFVQm1%2FenJhHb1zo%3D"},"title":"文章011-权重3-3u50","coverTitle":null,"subtitle":"副3","intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":3,"categoryIds":["01a038fd-43cf-79f6-998d-9049f129bd38"],"online":true,"createdAt":"2026-08-25T12:55:17.212313505Z","updatedAt":"2026-08-25T12:55:17.212313505Z"}
```

## 前置: B 下文章 sortOrder=1

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/art-2.png","contentHtml":"<p>正文</p>","title":"文章011-权重1-3u50","subtitle":"副1","categoryIds":["01a038fd-43cf-79f6-998d-9049f129bd38"],"sortOrder":1,"online":true}'
```

实际响应: HTTP 200（Content-Type: application/json）

```json
{"id":"01a038fd-43f5-79ec-ad6f-e37878320497","image":{"id":"bound/art-2.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-2.png?Expires=1787664317&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=jk%2FZHIfSacEZEzhQVnJvuogP%2FSk%3D"},"title":"文章011-权重1-3u50","coverTitle":null,"subtitle":"副1","intro":null,"tags":[],"contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a038fd-43cf-79f6-998d-9049f129bd38"],"online":true,"createdAt":"2026-08-25T12:55:17.237572495Z","updatedAt":"2026-08-25T12:55:17.237572495Z"}
```

## Step 2: GET 栏目列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/article-categories" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038cf-82aa-78e6-a54e-fc8a02f8fc38","name":"栏目丁","icon":{"id":"bound/images/icon.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/icon.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=5fRxZOS1DHWPa0jZBkps6ttfsNY%3D"},"sortOrder":0},{"id":"01a038cf-82a2-7eac-9707-d246d53fd2f6","name":"栏目丙","icon":{"id":"bound/images/icon.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/icon.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=5fRxZOS1DHWPa0jZBkps6ttfsNY%3D"},"sortOrder":0},{"id":"01a038cf-829b-747f-b2d2-874df11245b4","name":"栏目乙","icon":{"id":"bound/images/icon.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/icon.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=5fRxZOS1DHWPa0jZBkps6ttfsNY%3D"},"sortOrder":0},{"id":"01a038cf-8299-7d69-9837-df226b5a03f4","name":"栏目甲","icon":{"id":"bound/images/icon.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/icon.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=5fRxZOS1DHWPa0jZBkps6ttfsNY%3D"},"sortOrder":0},{"id":"01a038cf-828b-7857-9a7f-7863ca881fa2","name":"开关栏目","icon":{"id":"bound/images/icon.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/icon.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=5fRxZOS1DHWPa0jZBkps6ttfsNY%3D"},"sortOrder":0},{"id":"01a038bb-d23d-7085-86aa-0179166450ec","name":"栏目丁","icon":{"id":"bound/images/icon.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/icon.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=5fRxZOS1DHWPa0jZBkps6ttfsNY%3D"},"sortOrder":0},{"id":"01a038bb-d235-7f59-901b-2094d5ab2ec0","name":"栏目丙","icon":{"id":"bound/images/icon.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/icon.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=5fRxZOS1DHWPa0jZBkps6ttfsNY%3D"},"sortOrder":0},{"id":"01a038bb-d22d-70a0-aec5-175262d42d27","name":"栏目乙","icon":{"id":"bound/images/icon.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/icon.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=5fRxZOS1DHWPa0jZBkps6ttfsNY%3D"},"sortOrder":0},{"id":"01a038bb-d22b-75c2-b0f4-9ed84141d300","name":"栏目甲","icon":{"id":"bound/images/icon.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/images/icon.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=5fRxZOS1DHWPa0jZBkps6ttfsNY%3D"},"sortOrder":0},{"id":"01a038b
…(截断，共 14352 字符)
```

## Step 3: GET 文章列表 categoryId=B

```bash
curl -s -i -X GET "http://localhost:8081/api/app/articles?categoryId=01a038fd-43cf-79f6-998d-9049f129bd38" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 200（Content-Type: application/json）

```json
[{"id":"01a038fd-43f5-79ec-ad6f-e37878320497","image":{"id":"bound/art-2.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/art-2.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=KV7MFZkBEr4RjrvZmtgPuazwybQ%3D"},"coverTitle":"文章011-权重1-3u50","title":"文章011-权重1-3u50","subtitle":"副1","tags":[]},{"id":"01a038fd-43e2-70c9-96f5-f1e085e84393","image":{"id":"bound/art-1.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/art-1.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signature=7VmqrEXB1LXkmmRbrRgG%2B157WkI%3D"},"coverTitle":"文章011-权重3-3u50","title":"文章011-权重3-3u50","subtitle":"副3","tags":[]}]
```
