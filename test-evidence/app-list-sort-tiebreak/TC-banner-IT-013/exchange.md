# TC-banner-IT-013 请求/响应存证

用例: GET /api/app/banners 排序号并列时按创建时间倒序
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

## Step 2: 创建上架城市 cityId

```bash
curl -s -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"排序测试城021653","englishName":"SortCity021653","chineseProvince":"测试省","englishProvince":"TP","online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id": "01a03bdb-28ed-763c-a0ed-0e1852d2347e", "chineseName": "排序测试城021653", "englishName": "SortCity021653", "chineseProvince": "测试省", "englishProvince": "TP", "online": true, "createdAt": "2026-08-26T02:16:53.729119169Z"}
```

## Step 3: 先创建 Banner C（sortOrder=5）

```bash
curl -s -X POST "http://localhost:21423/api/admin/banners" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"BannerC021653","positionCode":"APP_TIE_ORDER","type":"CITY","imageUrls":["images/0197aaaa-bbbb-7000-8000-000000000004.png"],"link":"01a03bdb-28ed-763c-a0ed-0e1852d2347e","sortOrder":5}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdb-9cc7-70fe-bd8e-1711764eda5e","name":"BannerC021653","positionCode":"APP_TIE_ORDER","sortOrder":5,"online":false}  // 节选
```

## Step 4: 后创建 Banner D（sortOrder=5）

```bash
curl -s -X POST "http://localhost:21423/api/admin/banners" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"BannerD021653","positionCode":"APP_TIE_ORDER","type":"CITY","imageUrls":["images/0197aaaa-bbbb-7000-8000-000000000004.png"],"link":"01a03bdb-28ed-763c-a0ed-0e1852d2347e","sortOrder":5}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdb-9ce9-7087-a885-f19a9a9d3277","name":"BannerD021653","positionCode":"APP_TIE_ORDER","sortOrder":5,"online":false}  // 节选
```

## Step 5: 依次上架 C、D

```bash
curl -s -X POST "http://localhost:21423/api/admin/banners/01a03bdb-9cc7-70fe-bd8e-1711764eda5e/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":true}'
curl -s -X POST "http://localhost:21423/api/admin/banners/01a03bdb-9ce9-7087-a885-f19a9a9d3277/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":true}'
```

实际响应（HTTP/1.1 200）:

```json
两次均 HTTP/1.1 200
```

## Step 6: 连库拉开 created_at（C 前移 2 小时），确认 C 早于 D

```bash
psql "postgresql://iris:$PGPASSWORD@localhost:25432/love_space" -c "update loves_banner set created_at = created_at - interval '2 hour' where id='01a03bdb-9cc7-70fe-bd8e-1711764eda5e';"
psql "postgresql://iris:$PGPASSWORD@localhost:25432/love_space" -c "select name,sort_order,created_at from loves_banner where position_code='APP_TIE_ORDER' order by created_at;"
```

实际响应（psql）:

```json
UPDATE 1
BannerC021653|5|2026-08-26 00:17:23.398985+00
BannerD021653|5|2026-08-26 02:17:23.432979+00
```

## Step 7: app 端查询该展示位

```bash
curl -s -i -H "X-API-Key: test-api-key" "http://localhost:8081/api/app/banners?positionCode=APP_TIE_ORDER"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

[
  {
    "id": "01a03bdb-9ce9-7087-a885-f19a9a9d3277",
    "name": "BannerD021653",
    "type": "CITY",
    "image": [
      {
        "id": "bound/0197aaaa-bbbb-7000-8000-000000000004.png",
        "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000004.png?Expires=1787712443&OSSAccessKeyId=placeholder&Signature=sZWC%2Fn7KFyDPi5KBbXKxU6%2F1z2A%3D"
      }
    ],
    "data": {
      "id": "01a03bdb-28ed-763c-a0ed-0e1852d2347e",
      "chineseName": "排序测试城021653",
      "englishName": "SortCity021653",
      "chineseProvince": "测试省",
      "englishProvince": "TP"
    }
  },
  {
    "id": "01a03bdb-9cc7-70fe-bd8e-1711764eda5e",
    "name": "BannerC021653",
    "type": "CITY",
    "image": [
      {
        "id": "bound/0197aaaa-bbbb-7000-8000-000000000004.png",
        "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000004.png?Expires=1787712443&OSSAccessKeyId=placeholder&Signature=sZWC%2Fn7KFyDPi5KBbXKxU6%2F1z2A%3D"
      }
    ],
    "data": {
      "id": "01a03bdb-28ed-763c-a0ed-0e1852d2347e",
      "chineseName": "排序测试城021653",
      "englishName": "SortCity021653",
      "chineseProvince": "测试省",
      "englishProvince": "TP"
    }
  }
]
```
