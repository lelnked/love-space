# TC-city-IT-016 请求/响应存证

执行时间：2026-09-08 12:43 UTC ｜ baseUrl `http://localhost:21423`（admin，test profile）
cityId = `01a0810b-a164-7d73-82fd-078a4c4dd5a0`
脱敏：JWT 记为 `$TOKEN`；签名 URL 的 `OSSAccessKeyId`/`Signature` 记为 `$OSS_AK`/`$SIG`。

## step 1 — 前置：创建带 backgroundImage + secondaryBackgroundImage 的城市

```bash
curl -s -X POST http://localhost:21423/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"第二背景图更新城市169523","englishName":"SecondBgUpd169523","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":"bound/bg-primary-169523.png","secondaryBackgroundImage":"bound/bg-second-169523.jpg","online":false}'
```

HTTP/1.1 200
```json
{"id":"01a0810b-a164-7d73-82fd-078a4c4dd5a0","backgroundImage":{"id":"bound/bg-primary-169523.png","url":"...&OSSAccessKeyId=$OSS_AK&Signature=$SIG"},"secondaryBackgroundImage":{"id":"bound/bg-second-169523.jpg","url":"...&OSSAccessKeyId=$OSS_AK&Signature=$SIG"},"editorNote":null,"online":false}
```

## step 2 — PUT 更新 secondaryBackgroundImage 为另一个合法 objectKey（用 images/ 前缀验证 validateAndBind）

```bash
curl -s -X PUT http://localhost:21423/api/admin/cities/01a0810b-a164-7d73-82fd-078a4c4dd5a0 -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"第二背景图更新城市169523","englishName":"SecondBgUpd169523","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":"bound/bg-primary-169523.png","secondaryBackgroundImage":"images/bg-second-new-169523.webp","editorNote":null,"online":false}'
```

HTTP/1.1 200

```bash
curl -s http://localhost:21423/api/admin/cities/01a0810b-a164-7d73-82fd-078a4c4dd5a0 -H "Authorization: Bearer $TOKEN"
```
HTTP/1.1 200
```json
{"id":"01a0810b-a164-7d73-82fd-078a4c4dd5a0","chineseName":"第二背景图更新城市169523","englishName":"SecondBgUpd169523","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":{"id":"bound/bg-primary-169523.png","url":"...Expires=1788873227&OSSAccessKeyId=$OSS_AK&Signature=$SIG"},"secondaryBackgroundImage":{"id":"bound/bg-second-new-169523.webp","url":"...Expires=1788873227&OSSAccessKeyId=$OSS_AK&Signature=$SIG"},"editorNote":null,"online":false,"createdAt":"2026-09-08T12:43:38.212776Z","updatedAt":"2026-09-08T12:43:47.423441Z"}
```

## step 3 — PUT 提交 secondaryBackgroundImage: null 清空

```bash
curl -s -X PUT http://localhost:21423/api/admin/cities/01a0810b-a164-7d73-82fd-078a4c4dd5a0 -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"第二背景图更新城市169523","englishName":"SecondBgUpd169523","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":"bound/bg-primary-169523.png","secondaryBackgroundImage":null,"editorNote":null,"online":false}'
```

HTTP/1.1 200

```bash
curl -s http://localhost:21423/api/admin/cities/01a0810b-a164-7d73-82fd-078a4c4dd5a0 -H "Authorization: Bearer $TOKEN"
```
HTTP/1.1 200
```json
{"id":"01a0810b-a164-7d73-82fd-078a4c4dd5a0","backgroundImage":{"id":"bound/bg-primary-169523.png","url":"...&OSSAccessKeyId=$OSS_AK&Signature=$SIG"},"secondaryBackgroundImage":null,"editorNote":null,"online":false,"updatedAt":"2026-09-08T12:43:47.453408Z"}
```
