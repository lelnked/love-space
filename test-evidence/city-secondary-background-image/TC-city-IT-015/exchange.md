# TC-city-IT-015 请求/响应存证

执行时间：2026-09-08 12:43 UTC ｜ baseUrl `http://localhost:21423`（admin，test profile）
cityId = `01a0810b-5053-721b-af67-67bfa356cfdc`
脱敏：JWT 记为 `$TOKEN`；签名 URL 中的 `OSSAccessKeyId`/`Signature` 记为 `$OSS_AK`/`$SIG`。

## step 1 — 登录取 token

```bash
TOKEN=$(curl -s -X POST http://localhost:21423/api/admin/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}' | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')
```

HTTP/1.1 200，token 为三段式 JWT（`eyJhbGciOiJIUzI1NiJ9...`）

## step 2 — 创建城市（同时带 backgroundImage 与 secondaryBackgroundImage）

```bash
curl -s -i -X POST http://localhost:21423/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"第二背景图城市92331558","englishName":"SecondBgCity92331558","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":"bound/bg-primary-92331558.png","secondaryBackgroundImage":"bound/bg-second-92331558.jpg","online":true}'
```

HTTP/1.1 200 ｜ Content-Type: application/json
```json
{"id":"01a0810b-5053-721b-af67-67bfa356cfdc","chineseName":"第二背景图城市92331558","englishName":"SecondBgCity92331558","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":{"id":"bound/bg-primary-92331558.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/bg-primary-92331558.png?Expires=1788873197&OSSAccessKeyId=$OSS_AK&Signature=$SIG"},"secondaryBackgroundImage":{"id":"bound/bg-second-92331558.jpg","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/bg-second-92331558.jpg?Expires=1788873197&OSSAccessKeyId=$OSS_AK&Signature=$SIG"},"editorNote":null,"online":true,"createdAt":"2026-09-08T12:43:17.441486349Z","updatedAt":"2026-09-08T12:43:17.441486349Z"}
```

## step 3 — GET 详情

```bash
curl -s -i http://localhost:21423/api/admin/cities/01a0810b-5053-721b-af67-67bfa356cfdc -H "Authorization: Bearer $TOKEN"
```

HTTP/1.1 200 ｜ Content-Type: application/json
```json
{"id":"01a0810b-5053-721b-af67-67bfa356cfdc","chineseName":"第二背景图城市92331558","englishName":"SecondBgCity92331558","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":{"id":"bound/bg-primary-92331558.png","url":"...?Expires=1788873205&OSSAccessKeyId=$OSS_AK&Signature=$SIG"},"secondaryBackgroundImage":{"id":"bound/bg-second-92331558.jpg","url":"...?Expires=1788873205&OSSAccessKeyId=$OSS_AK&Signature=$SIG"},"editorNote":null,"online":true,"createdAt":"2026-09-08T12:43:17.441486Z","updatedAt":"2026-09-08T12:43:17.441486Z"}
```

> 备注：本轮提交的是 `bound/` 前缀 objectKey，validateAndBind 幂等返回同 key，故 `id` 与提交值逐字一致；
> `images/` → `bound/` 的绑定转换在 TC-city-IT-016 step 2 单独覆盖。
