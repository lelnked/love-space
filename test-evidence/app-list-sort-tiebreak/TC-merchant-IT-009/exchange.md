# TC-merchant-IT-009 请求/响应存证

用例: GET /api/app/merchants/{merchantId}/reviews 同排序号评价按创建时间倒序
执行日期: 2026-08-26 ｜ change: app-list-sort-tiebreak ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 `X-API-Key: test-api-key`
图片 objectKey 用 test profile 的 Stub 校验器接受的固定 key。

> 说明：HTTP 连续创建的 `created_at` 相差仅数十毫秒，为让 tie-break 断言有可辨识的时间差，对**先创建**的一条直接连库把 `created_at` 前移 2 小时（该步骤如实记录在下方 Step 中），不改任何业务字段，等价于「先后创建」的真实场景。


## Step 1: admin 登录取 JWT

```bash
curl -s -X POST "http://localhost:21423/api/admin/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP/1.1 200）:

```json
{"token":"$TOKEN", ...}
```

## Step 2: 创建上架城市

```bash
curl -s -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"排序测试城021653","englishName":"SortCity021653","chineseProvince":"测试省","englishProvince":"TP","online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id": "01a03bdb-28ed-763c-a0ed-0e1852d2347e", "chineseName": "排序测试城021653", "englishName": "SortCity021653", "chineseProvince": "测试省", "englishProvince": "TP", "online": true, "createdAt": "2026-08-26T02:16:53.729119169Z"}
```

## Step 3: 创建上架商户 M

```bash
curl -s -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"评价商户021653","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.3,"latitude":30.59,"cityId":"01a03bdb-28ed-763c-a0ed-0e1852d2347e","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":10,"online":true,"images":["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdc-36c1-7c18-bd59-f076b74a95f1","name":"评价商户021653","online":true, ...}  // 节选：logo/images 签名 URL 略
```

## Step 4: 先创建评价 A（sortOrder=0）

```bash
curl -s -X POST "http://localhost:21423/api/admin/merchants/01a03bdc-36c1-7c18-bd59-f076b74a95f1/reviews" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"nickname":"评A","title":"标题A","content":"内容A","sortOrder":0,"recommended":false}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdc-36fd-7545-a7ce-fa344421756f","merchantId":"01a03bdc-36c1-7c18-bd59-f076b74a95f1","nickname":"评A","title":"标题A","content":"内容A","sortOrder":0,"recommended":false,"createdAt":"2026-08-26T02:18:02.876232383Z"}
```

## Step 5: 后创建评价 B（sortOrder=0）

```bash
curl -s -X POST "http://localhost:21423/api/admin/merchants/01a03bdc-36c1-7c18-bd59-f076b74a95f1/reviews" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"nickname":"评B","title":"标题B","content":"内容B","sortOrder":0,"recommended":false}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdc-370a-7eb8-8454-786298e4f81e","merchantId":"01a03bdc-36c1-7c18-bd59-f076b74a95f1","nickname":"评B","title":"标题B","content":"内容B","sortOrder":0,"recommended":false,"createdAt":"2026-08-26T02:18:02.890856622Z"}
```

## Step 6: 连库拉开 created_at（评 A 前移 2 小时）

```bash
psql "postgresql://iris:$PGPASSWORD@localhost:25432/love_space" -c "update loves_merchant_review set created_at = created_at - interval '2 hour' where id='01a03bdc-36fd-7545-a7ce-fa344421756f';"
```

实际响应（psql）:

```json
UPDATE 1
评A|0|2026-08-26 00:18:02.876232+00
评B|0|2026-08-26 02:18:02.890857+00
```

## Step 7: app 端查询商户评价

```bash
curl -s -i -H "X-API-Key: test-api-key" "http://localhost:8081/api/app/merchants/01a03bdc-36c1-7c18-bd59-f076b74a95f1/reviews"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

[
  {
    "nickname": "评B",
    "title": "标题B",
    "content": "内容B"
  },
  {
    "nickname": "评A",
    "title": "标题A",
    "content": "内容A"
  }
]
```
