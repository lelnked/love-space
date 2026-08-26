# TC-banner-IT-012 请求/响应存证

用例: GET /api/app/banners 按展示位返回上架 Banner 并按排序号升序
执行日期: 2026-08-26 ｜ change: app-list-sort-tiebreak ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 `X-API-Key: test-api-key`
图片 objectKey 用 test profile 的 Stub 校验器接受的固定 key。


## Step 1: admin 登录取 JWT

```bash
curl -s -X POST "http://localhost:21423/api/admin/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP/1.1 200）:

```json
{"token":"$TOKEN","manager":{"id":"019794b6-b400-7000-8000-000000000001","username":"admin","nickname":"管理员","role":"ADMIN"}}
```

## Step 2: 创建上架城市 cityId

```bash
curl -s -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"排序测试城021653","englishName":"SortCity021653","chineseProvince":"测试省","englishProvince":"TP","online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id": "01a03bdb-28ed-763c-a0ed-0e1852d2347e", "chineseName": "排序测试城021653", "englishName": "SortCity021653", "chineseProvince": "测试省", "englishProvince": "TP", "online": true, "createdAt": "2026-08-26T02:16:53.729119169Z"}
```

## Step 3: 创建 Banner A（sortOrder=1）

```bash
curl -s -X POST "http://localhost:21423/api/admin/banners" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"BannerA021653","positionCode":"APP_HOME_TOP","type":"CITY","imageUrls":["images/0197aaaa-bbbb-7000-8000-000000000004.png"],"link":"01a03bdb-28ed-763c-a0ed-0e1852d2347e","sortOrder":1}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdb-6edf-78b2-8164-fa51d750a64e","name":"BannerA021653","positionCode":"APP_HOME_TOP","type":"CITY","link":"01a03bdb-28ed-763c-a0ed-0e1852d2347e","online":false,"sortOrder":1}  // 节选：imageUrls/时间戳略
```

## Step 4: 创建 Banner B（sortOrder=0）

```bash
curl -s -X POST "http://localhost:21423/api/admin/banners" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"BannerB021653","positionCode":"APP_HOME_TOP","type":"CITY","imageUrls":["images/0197aaaa-bbbb-7000-8000-000000000004.png"],"link":"01a03bdb-28ed-763c-a0ed-0e1852d2347e","sortOrder":0}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdb-6f02-7ba8-b04e-189aec3569fc","name":"BannerB021653","positionCode":"APP_HOME_TOP","type":"CITY","link":"01a03bdb-28ed-763c-a0ed-0e1852d2347e","online":false,"sortOrder":0}  // 节选
```

## Step 5: 分别上架 A、B

```bash
curl -s -X POST "http://localhost:21423/api/admin/banners/01a03bdb-6edf-78b2-8164-fa51d750a64e/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":true}'
curl -s -X POST "http://localhost:21423/api/admin/banners/01a03bdb-6f02-7ba8-b04e-189aec3569fc/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":true}'
```

实际响应（HTTP/1.1 200）:

```json
两次均 HTTP/1.1 200，online 置为 true
```

## Step 6: app 端按展示位查询

```bash
curl -s -i -H "X-API-Key: test-api-key" "http://localhost:8081/api/app/banners?positionCode=APP_HOME_TOP"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

[
  {
    "id": "01a03bdb-6f02-7ba8-b04e-189aec3569fc",
    "name": "BannerB021653",
    "type": "CITY",
    "image": [
      {
        "id": "bound/0197aaaa-bbbb-7000-8000-000000000004.png",
        "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000004.png?Expires=1787712431&OSSAccessKeyId=placeholder&Signature=l%2BttnDLGdPLXWp%2FXc1Z5hNkumc8%3D"
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
    "id": "01a03bdb-6edf-78b2-8164-fa51d750a64e",
    "name": "BannerA021653",
    "type": "CITY",
    "image": [
      {
        "id": "bound/0197aaaa-bbbb-7000-8000-000000000004.png",
        "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/0197aaaa-bbbb-7000-8000-000000000004.png?Expires=1787712431&OSSAccessKeyId=placeholder&Signature=l%2BttnDLGdPLXWp%2FXc1Z5hNkumc8%3D"
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
