# TC-city-IT-013 请求/响应存证

执行时间：2026-08-25 11:45 UTC ｜ baseUrl: `http://localhost:8080`（admin, test profile）
公共变量：`export TOKEN=<login 返回 JWT，脱敏为 $TOKEN>` ｜ cityId=01a038bd-5ec2-75f6-afe5-f1f90aa794ae ｜ merchantId=01a038bd-7b72-7428-96d4-c9e446c19086 ｜ bannerId=01a038bd-9423-7406-b4b0-a7ce5a0759e8

## step 1 — 登录取 token

```bash
curl -s -X POST http://localhost:8080/api/admin/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

HTTP/1.1 200
```json
{"token":"$TOKEN","manager":{"id":"019794b6-b400-7000-8000-000000000001","username":"admin","nickname":"管理员","role":"ADMIN"}}
```

## step 2a — 创建上架城市

```bash
curl -s -X POST http://localhost:8080/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"测试地图1787658319","englishName":"TestCity1787658319","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":"images/0197aaaa-bbbb-7000-8000-000000000001.png","editorNote":"删除级联用例","online":true}'
```

HTTP/1.1 200
```json
{"id":"01a038bd-5ec2-75f6-afe5-f1f90aa794ae","chineseName":"测试地图1787658319","englishName":"TestCity1787658319","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":{"id":"bound/0197aaaa-bbbb-7000-8000-000000000001.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000001.png?Expires=1787660129&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DTQoSdLQ28RpF9%2BLAUj1Ey%2BveNE%3D"},"editorNote":"删除级联用例","online":true,"createdAt":"2026-08-25T11:45:29.781935331Z","updatedAt":"2026-08-25T11:45:29.781935331Z"}
```

## step 2b — 该城市下创建上架商户

```bash
curl -s -X POST http://localhost:8080/api/admin/merchants -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"name":"测试商户1787658319","logo":"images/0197aaaa-bbbb-7000-8000-000000000002.png","address":"测试地址1号","cityId":"01a038bd-5ec2-75f6-afe5-f1f90aa794ae","safetyEnvironmentScore":10,"businessRightsScore":10,"experienceFriendlyScore":10,"socialContributionScore":10,"online":true,"images":["images/0197aaaa-bbbb-7000-8000-000000000003.png"]}'
```

HTTP/1.1 200（online=true, cityId=01a038bd-5ec2-75f6-afe5-f1f90aa794ae）
```json
{"id":"01a038bd-7b72-7428-96d4-c9e446c19086","name":"测试商户1787658319","logo":{"id":"bound/0197aaaa-bbbb-7000-8000-000000000002.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000002.png?Expires=1787660137&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=pI1uh3b5duGUuXaU8x7Zi3owsI8%3D"},"address":"测试地址1号","longitude":null,"latitude":null,"cityId":"01a038bd-5ec2-75f6-afe5-f1f90aa794ae","categoryId":null,"safetyEnvironmentScore":10,"businessRightsScore":10,"experienceFriendlyScore":10,"socialContributionScore":10,"story":null,"recommendReason":null,"weight":0,"online":true,"periods":[],"tagIds":[],"images":[{"id":"bound/0197aaaa-bbbb-7000-8000-000000000003.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000003.png?Expires=1787660137&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=CqOouTG7z7kC8K0rx0zJnuDffSM%3D"}],"createdAt":"2026-08-25T11:45:37.136694669Z","updatedAt":"2026-08-25T11:45:37.136694669Z"}
```

## step 2c — 创建 CITY 类型 Banner 并上架

```bash
curl -s -X POST http://localhost:8080/api/admin/banners -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"name":"测试Banner1787658319","positionCode":"HOME_TOP","type":"CITY","imageUrls":["images/0197aaaa-bbbb-7000-8000-000000000004.png"],"link":"01a038bd-5ec2-75f6-afe5-f1f90aa794ae","sortOrder":0}'
curl -s -X POST http://localhost:8080/api/admin/banners/01a038bd-9423-7406-b4b0-a7ce5a0759e8/online -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

创建 HTTP/1.1 200（强制 online=false）：
```json
{"id":"01a038bd-9423-7406-b4b0-a7ce5a0759e8","name":"测试Banner1787658319","positionCode":"HOME_TOP","type":"CITY","imageUrls":[{"id":"bound/0197aaaa-bbbb-7000-8000-000000000004.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000004.png?Expires=1787660143&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=428yQZA16IM6fQC4ZOJ1TfgkpQQ%3D"}],"link":"01a038bd-5ec2-75f6-afe5-f1f90aa794ae","linkedCityName":"测试地图1787658319","online":false,"sortOrder":0,"createdAt":"2026-08-25T11:45:43.458099665Z","updatedAt":"2026-08-25T11:45:43.458099665Z"}
```

上架 HTTP/1.1 200（online=true）：
```json
{"id":"01a038bd-9423-7406-b4b0-a7ce5a0759e8","name":"测试Banner1787658319","positionCode":"HOME_TOP","type":"CITY","imageUrls":[{"id":"bound/0197aaaa-bbbb-7000-8000-000000000004.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000004.png?Expires=1787660152&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=soNPWjPYKzQai3rXbmbN8mSKtRc%3D"}],"link":"01a038bd-5ec2-75f6-afe5-f1f90aa794ae","linkedCityName":"测试地图1787658319","online":true,"sortOrder":0,"createdAt":"2026-08-25T11:45:43.4581Z","updatedAt":"2026-08-25T11:45:43.4581Z"}
```

## step 3 — 删除城市

```bash
curl -s -i -X DELETE http://localhost:8080/api/admin/cities/01a038bd-5ec2-75f6-afe5-f1f90aa794ae -H "Authorization: Bearer $TOKEN"
```

HTTP/1.1 200，空响应体（无 Content-Type）。

## step 4 — 再查城市详情

```bash
curl -s -i http://localhost:8080/api/admin/cities/01a038bd-5ec2-75f6-afe5-f1f90aa794ae -H "Authorization: Bearer $TOKEN"
```

HTTP/1.1 400，Content-Type: application/json
```json
{"status":400,"error":"Bad Request","message":"城市不存在：01a038bd-5ec2-75f6-afe5-f1f90aa794ae","path":"/api/admin/cities/01a038bd-5ec2-75f6-afe5-f1f90aa794ae"}
```

## step 5a — 查商户（记录仍在，已下架）

```bash
curl -s -i http://localhost:8080/api/admin/merchants/01a038bd-7b72-7428-96d4-c9e446c19086 -H "Authorization: Bearer $TOKEN"
```

HTTP/1.1 200，Content-Type: application/json
```json
{"id":"01a038bd-7b72-7428-96d4-c9e446c19086","name":"测试商户1787658319","logo":{"id":"bound/0197aaaa-bbbb-7000-8000-000000000002.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000002.png?Expires=1787660160&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=5LF56CpXxWrKim7C%2FhRN7DG15a0%3D"},"address":"测试地址1号","longitude":null,"latitude":null,"cityId":"01a038bd-5ec2-75f6-afe5-f1f90aa794ae","categoryId":null,"safetyEnvironmentScore":10,"businessRightsScore":10,"experienceFriendlyScore":10,"socialContributionScore":10,"story":null,"recommendReason":null,"weight":0,"online":false,"periods":[],"tagIds":[],"images":[{"id":"bound/0197aaaa-bbbb-7000-8000-000000000003.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000003.png?Expires=1787660160&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DTvfuvbvbN1ABOW6Y88oDEG0SIE%3D"}],"createdAt":"2026-08-25T11:45:37.136695Z","updatedAt":"2026-08-25T11:45:37.136695Z"}
```

## step 5b — 查 Banner（记录仍在，已下架）

```bash
curl -s -i http://localhost:8080/api/admin/banners/01a038bd-9423-7406-b4b0-a7ce5a0759e8 -H "Authorization: Bearer $TOKEN"
```

HTTP/1.1 200，Content-Type: application/json
```json
{"id":"01a038bd-9423-7406-b4b0-a7ce5a0759e8","name":"测试Banner1787658319","positionCode":"HOME_TOP","type":"CITY","imageUrls":[{"id":"bound/0197aaaa-bbbb-7000-8000-000000000004.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000004.png?Expires=1787660160&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=i3W%2BSaGUa5Z%2B7kp6wfq2BVbGAKA%3D"}],"link":"01a038bd-5ec2-75f6-afe5-f1f90aa794ae","linkedCityName":null,"online":false,"sortOrder":0,"createdAt":"2026-08-25T11:45:43.4581Z","updatedAt":"2026-08-25T11:45:52.675531Z"}
```

## 清理

删除本轮夹具：merchants/01a038bd-7b72-7428-96d4-c9e446c19086、banners/01a038bd-9423-7406-b4b0-a7ce5a0759e8 均返回 200。
