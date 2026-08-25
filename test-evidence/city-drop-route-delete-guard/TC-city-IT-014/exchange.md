# TC-city-IT-014 请求/响应存证

执行时间：2026-08-25 11:46 UTC ｜ baseUrl: `http://localhost:8080`
公共变量：`export TOKEN=$TOKEN`（同 TC-013 登录所得） ｜ cityId=01a038bd-ff5f-7af9-a8d7-6cd4d8026da5 ｜ ambassadorId=01a038bd-ff86-7ab2-b4c2-522e2eefced4 ｜ routeId=01a038be-1cc1-7a0a-8782-98bd62ec02a2

## step 1a — 创建城市

```bash
curl -s -X POST http://localhost:8080/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"路线地图1787658319","englishName":"RouteCity1787658319","chineseProvince":"测试省","englishProvince":"TestProv","online":true}'
```

HTTP/1.1 200
```json
{"id":"01a038bd-ff5f-7af9-a8d7-6cd4d8026da5","chineseName":"路线地图1787658319","englishName":"RouteCity1787658319","chineseProvince":"测试省","englishProvince":"TestProv","backgroundImage":null,"editorNote":null,"online":true,"createdAt":"2026-08-25T11:46:10.911619653Z","updatedAt":"2026-08-25T11:46:10.911619653Z"}
```

## step 1b — 创建大使（路线必填关联）

```bash
curl -s -X POST http://localhost:8080/api/admin/ambassadors -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"avatar":"images/0197aaaa-bbbb-7000-8000-000000000005.png","name":"测试大使1787658319","tags":["温柔"],"weight":0,"online":true}'
```

HTTP/1.1 200
```json
{"id":"01a038bd-ff86-7ab2-b4c2-522e2eefced4","avatar":{"id":"bound/0197aaaa-bbbb-7000-8000-000000000005.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000005.png?Expires=1787660170&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lyYhBr8HcDM5YG%2Fccd8YKy3PNvg%3D"},"name":"测试大使1787658319","tags":["温柔"],"weight":0,"online":true,"createdAt":"2026-08-25T11:46:10.949048295Z","updatedAt":"2026-08-25T11:46:10.949048295Z"}
```

## step 1c — 创建路线（cityName 为自由文本，与城市无外键）

```bash
curl -s -X POST http://localhost:8080/api/admin/routes -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"sortOrder":0,"title":"测试路线1787658319","cityName":"路线地图1787658319","ambassadorNote":"大使说","thumbnail":"images/0197aaaa-bbbb-7000-8000-000000000006.png","images":["images/0197aaaa-bbbb-7000-8000-000000000007.png"],"travelTime":"2天","season":"春","travelStatus":"轻松","ambassadorId":"01a038bd-ff86-7ab2-b4c2-522e2eefced4","spots":[{"name":"地点A","image":"images/0197aaaa-bbbb-7000-8000-000000000008.png","introduction":"介绍A"}]}'
```

HTTP/1.1 200（响应体无 cityId 字段，只有自由文本 cityName）
```json
{"id":"01a038be-1cc1-7a0a-8782-98bd62ec02a2","sortOrder":0,"title":"测试路线1787658319","cityName":"路线地图1787658319","ambassadorNote":"大使说","thumbnail":{"id":"bound/0197aaaa-bbbb-7000-8000-000000000006.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000006.png?Expires=1787660178&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Qetplhbs4n8LL20AM0goAvTQsDc%3D"},"images":[{"id":"bound/0197aaaa-bbbb-7000-8000-000000000007.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000007.png?Expires=1787660178&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZqGKr%2B2bj2UAZJ5SnMtgioiCqAY%3D"}],"travelTime":"2天","season":"春","travelStatus":"轻松","ambassadorId":"01a038bd-ff86-7ab2-b4c2-522e2eefced4","ambassadorName":"测试大使1787658319","spots":[{"name":"地点A","image":{"id":"bound/0197aaaa-bbbb-7000-8000-000000000008.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000008.png?Expires=1787660178&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=3Z%2FlD9V0ON1hjvKdAze408%2Br4h4%3D"},"introduction":"介绍A"}],"createdAt":"2026-08-25T11:46:18.432405161Z","updatedAt":"2026-08-25T11:46:18.432405161Z"}
```

## step 2 — 删除城市（系统内存在路线）

```bash
curl -s -i -X DELETE http://localhost:8080/api/admin/cities/01a038bd-ff5f-7af9-a8d7-6cd4d8026da5 -H "Authorization: Bearer $TOKEN"
```

HTTP/1.1 200，空响应体——**未被路线存在性拦截**。

## step 3 — 查路线（不受影响）

```bash
curl -s -i http://localhost:8080/api/admin/routes/01a038be-1cc1-7a0a-8782-98bd62ec02a2 -H "Authorization: Bearer $TOKEN"
```

HTTP/1.1 200，Content-Type: application/json
```json
{"id":"01a038be-1cc1-7a0a-8782-98bd62ec02a2","sortOrder":0,"title":"测试路线1787658319","cityName":"路线地图1787658319","ambassadorNote":"大使说","thumbnail":{"id":"bound/0197aaaa-bbbb-7000-8000-000000000006.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000006.png?Expires=1787660185&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=LzNKrUriOnXGStFVwaics%2F023g0%3D"},"images":[{"id":"bound/0197aaaa-bbbb-7000-8000-000000000007.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000007.png?Expires=1787660185&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZYRCZn4%2FjWaS%2FWendGemQ%2Fw0%2Fpc%3D"}],"travelTime":"2天","season":"春","travelStatus":"轻松","ambassadorId":"01a038bd-ff86-7ab2-b4c2-522e2eefced4","ambassadorName":"测试大使1787658319","spots":[{"name":"地点A","image":{"id":"bound/0197aaaa-bbbb-7000-8000-000000000008.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000008.png?Expires=1787660185&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=5d5toMWmKHcQFybu52xzqFG6FPs%3D"},"introduction":"介绍A"}],"createdAt":"2026-08-25T11:46:18.432405Z","updatedAt":"2026-08-25T11:46:18.432405Z"}
```

## 清理

删除本轮夹具：routes/01a038be-1cc1-7a0a-8782-98bd62ec02a2、ambassadors/01a038bd-ff86-7ab2-b4c2-522e2eefced4 均返回 200。
