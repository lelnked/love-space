# TC-recommend-list-IT-012 请求/响应存证

用例: GET /api/app/recommend-lists/{id} 详情按清单保存顺序返回上架商户四字段
执行日期: 2026-08-26 ｜ change: app-list-sort-tiebreak ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 `X-API-Key: test-api-key`
图片 objectKey 用 test profile 的 Stub 校验器接受的固定 key。

> 回归重测：确认 tie-break 改动未影响清单内商户的「保存顺序」语义。


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
curl -s -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"清单详情城021653","englishName":"RLDet021653","chineseProvince":"测试省","englishProvince":"TP","online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdc-dca9-70d4-9492-8ebed01ca592","chineseName":"清单详情城021653","online":true}  // 节选
```

## Step 3: 创建上架商户 甲(weight=1) / 乙(weight=9) / 丙(weight=5)

```bash
curl -s -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"甲021653","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.3,"latitude":30.59,"cityId":"01a03bdc-dca9-70d4-9492-8ebed01ca592","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":1,"online":true,"images":["bound/img-test-1.png"]}'
curl -s -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"乙021653","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.3,"latitude":30.59,"cityId":"01a03bdc-dca9-70d4-9492-8ebed01ca592","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":9,"online":true,"images":["bound/img-test-1.png"]}'
curl -s -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"丙021653","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.3,"latitude":30.59,"cityId":"01a03bdc-dca9-70d4-9492-8ebed01ca592","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":5,"online":true,"images":["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```json
甲 id=01a03bdc-dcbd-7d35-8153-bd3e03b7ab8d (weight=1)
乙 id=01a03bdc-dcd2-770d-858f-171b2359c689 (weight=9)
丙 id=01a03bdc-dce7-7f19-a856-d68a4130dd5c (weight=5)
三者均 online=true
```

## Step 4: 创建清单，merchantIds 顺序为 甲→乙→丙（甲 weight 低于 乙）

```bash
curl -s -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"详情清单021653","introduction":"详情介绍","cityId":"01a03bdc-dca9-70d4-9492-8ebed01ca592","sortOrder":2,"merchantIds":["01a03bdc-dcbd-7d35-8153-bd3e03b7ab8d","01a03bdc-dcd2-770d-858f-171b2359c689","01a03bdc-dce7-7f19-a856-d68a4130dd5c"]}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdc-dcfb-7fdc-8601-3c019a10fdc3","title":"详情清单021653","introduction":"详情介绍","cityId":"01a03bdc-dca9-70d4-9492-8ebed01ca592","sortOrder":2,"merchants":[甲,乙,丙 按 merchantIds 顺序],"status":"ONLINE"}  // 节选
```

## Step 5: 把清单内的商户 丙 下架

```bash
curl -s -X PUT "http://localhost:21423/api/admin/merchants/01a03bdc-dce7-7f19-a856-d68a4130dd5c/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":false}'
```

实际响应（HTTP/1.1 200）:

```json
HTTP/1.1 200，丙 online=false
```

## Step 6: app 端查询清单详情

```bash
curl -s -i -H "X-API-Key: test-api-key" "http://localhost:8081/api/app/recommend-lists/01a03bdc-dcfb-7fdc-8601-3c019a10fdc3"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

{
  "id": "01a03bdc-dcfb-7fdc-8601-3c019a10fdc3",
  "title": "详情清单021653",
  "introduction": "详情介绍",
  "cityId": "01a03bdc-dca9-70d4-9492-8ebed01ca592",
  "sortOrder": 2,
  "merchants": [
    {
      "id": "01a03bdc-dcbd-7d35-8153-bd3e03b7ab8d",
      "name": "甲021653",
      "address": "测试路 1 号",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787712525&OSSAccessKeyId=placeholder&Signature=mJFuDPWYfHJtvk6KVpw4%2B7J3RIM%3D"
      }
    },
    {
      "id": "01a03bdc-dcd2-770d-858f-171b2359c689",
      "name": "乙021653",
      "address": "测试路 1 号",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787712525&OSSAccessKeyId=placeholder&Signature=mJFuDPWYfHJtvk6KVpw4%2B7J3RIM%3D"
      }
    }
  ]
}
```
