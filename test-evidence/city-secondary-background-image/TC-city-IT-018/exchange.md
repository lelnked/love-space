# TC-city-IT-018 请求/响应存证

执行时间：2026-09-08 12:45 UTC ｜ baseUrl `http://localhost:8081`（app 后端）
夹具：TC-city-IT-015 的上架城市 `01a0810b-5053-721b-af67-67bfa356cfdc`（配置了 secondaryBackgroundImage）
      + 新建上架城市 `01a0810c-2a2e-78a4-8c05-2aa660ec0e6d`（只配 backgroundImage，未配第二背景图）

⚠️ 环境说明：本轮开始时 app 进程以 `ALIYUN_OSS_BUCKET=x` 启动，OSS 预签名因 bucket 名非法
（"bucket name must be 3-63 chars…"）导致 `GET /api/app/cities` 整体 400——与本 change 无关的环境配置问题。
已用合法占位 bucket 重启 app 后执行本用例：
`ALIYUN_OSS_ENDPOINT=http://oss-cn-hangzhou.aliyuncs.com ALIYUN_OSS_BUCKET=love-space-test-0524 ALIYUN_OSS_ACCESS_KEY_ID=test-oss-ak ALIYUN_OSS_ACCESS_KEY_SECRET=test-oss-sk`
（预签名为本地 HMAC 计算，不访问真实 OSS 对象。）

## step 1 — 前置：创建"未配置第二背景图"的上架城市（admin 侧）

```bash
curl -s -X POST http://localhost:21423/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"无第二背景图城市1812123","englishName":"NoSecondBg1812123","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":"bound/bg-only-primary-1812123.png","online":true}'
```
HTTP/1.1 200，`secondaryBackgroundImage: null`

## step 2 — 带 API key 调 app 城市列表

```bash
curl -s -i http://localhost:8081/api/app/cities -H "X-API-Key: test-api-key"
```

HTTP/1.1 200 ｜ Content-Type: application/json
```json
[{"id":"01a0810c-2a2e-78a4-8c05-2aa660ec0e6d","chineseName":"无第二背景图城市1812123","englishName":"NoSecondBg1812123","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":{"id":"bound/bg-only-primary-1812123.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/bg-only-primary-1812123.png?Expires=1788873311&OSSAccessKeyId=test-oss-ak&Signature=$SIG"},"secondaryBackgroundImage":null,"editorNote":null},
 {"id":"01a0810b-5053-721b-af67-67bfa356cfdc","chineseName":"第二背景图城市92331558","englishName":"SecondBgCity92331558","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":{"id":"bound/bg-primary-92331558.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/bg-primary-92331558.png?Expires=1788873311&OSSAccessKeyId=test-oss-ak&Signature=$SIG"},"secondaryBackgroundImage":{"id":"bound/bg-second-92331558.jpg","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/bg-second-92331558.jpg?Expires=1788873311&OSSAccessKeyId=test-oss-ak&Signature=$SIG"},"editorNote":null},
 {"id":"01a08107-c682-7901-8afb-b1463424b8a4","chineseName":"上海-6d37a95d…","englishName":"shanghai-it","backgroundImage":{"id":"bound/bg.png","url":"…"},"secondaryBackgroundImage":{"id":"bound/sec.png","url":"…"},"editorNote":null}]
```
