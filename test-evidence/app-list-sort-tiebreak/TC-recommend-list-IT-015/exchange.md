# TC-recommend-list-IT-015 请求/响应存证

用例: GET /api/app/merchants/page 商户列表不受清单影响
执行日期: 2026-08-26 ｜ change: app-list-sort-tiebreak ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 `X-API-Key: test-api-key`
图片 objectKey 用 test profile 的 Stub 校验器接受的固定 key。

> 回归重测：锚定 `weight DESC, createdAt DESC` 口径，本 change 对该路径零改动，验证未回退。


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
curl -s -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"商户列表城021653","englishName":"MList021653","chineseProvince":"测试省","englishProvince":"TP","online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdd-1d29-7469-8c5a-76b7a1d7bfb6","chineseName":"商户列表城021653","online":true}  // 节选
```

## Step 3: 创建 3 个上架商户，weight 各异

```bash
curl -s -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"甲2021653","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.3,"latitude":30.59,"cityId":"01a03bdd-1d29-7469-8c5a-76b7a1d7bfb6","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":1,"online":true,"images":["bound/img-test-1.png"]}'
curl -s -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"乙2021653","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.3,"latitude":30.59,"cityId":"01a03bdd-1d29-7469-8c5a-76b7a1d7bfb6","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":9,"online":true,"images":["bound/img-test-1.png"]}'
curl -s -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"丙2021653","logo":"bound/logo-test.png","address":"测试路 1 号","longitude":114.3,"latitude":30.59,"cityId":"01a03bdd-1d29-7469-8c5a-76b7a1d7bfb6","safetyEnvironmentScore":25,"businessRightsScore":20,"experienceFriendlyScore":20,"socialContributionScore":15,"weight":5,"online":true,"images":["bound/img-test-1.png"]}'
```

实际响应（HTTP/1.1 200）:

```json
甲2 id=01a03bdd-1d3d-728b-bf9d-8e31e1a97a61 (weight=1)
乙2 id=01a03bdd-1d54-79c6-b859-816353ae5bf7 (weight=9)
丙2 id=01a03bdd-1d6a-7070-9c40-7eafbf8118a7 (weight=5)
三者均 online=true
```

## Step 4: 创建清单 L，merchantIds=[甲2(weight 低), 乙2(weight 高)]

```bash
curl -s -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"L15-021653","introduction":"i","cityId":"01a03bdd-1d29-7469-8c5a-76b7a1d7bfb6","sortOrder":0,"merchantIds":["01a03bdd-1d3d-728b-bf9d-8e31e1a97a61","01a03bdd-1d54-79c6-b859-816353ae5bf7"]}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdd-1d7d-78c8-b85a-9ed0536a3919","merchants":[甲2, 乙2]}  // 清单内顺序与 weight 排序相反
```

## Step 5: app 端商户列表（不带 recommendListId）

```bash
curl -s -i -H "X-API-Key: test-api-key" "http://localhost:8081/api/app/merchants/page?cityId=01a03bdd-1d29-7469-8c5a-76b7a1d7bfb6"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

{
  "content": [
    {
      "id": "01a03bdd-1d54-79c6-b859-816353ae5bf7",
      "name": "乙2021653",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787712541&OSSAccessKeyId=placeholder&Signature=xcBRB3yWSY3UD1AOerj%2FzaTFkvE%3D"
      },
      "address": "测试路 1 号",
      "tags": [],
      "scores": {
        "safetyEnvironmentPercent": 83,
        "businessRightsPercent": 80,
        "experienceFriendlyPercent": 80,
        "socialContributionPercent": 75
      },
      "loveIndex": {
        "total": 80,
        "level": 8
      }
    },
    {
      "id": "01a03bdd-1d6a-7070-9c40-7eafbf8118a7",
      "name": "丙2021653",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787712541&OSSAccessKeyId=placeholder&Signature=xcBRB3yWSY3UD1AOerj%2FzaTFkvE%3D"
      },
      "address": "测试路 1 号",
      "tags": [],
      "scores": {
        "safetyEnvironmentPercent": 83,
        "businessRightsPercent": 80,
        "experienceFriendlyPercent": 80,
        "socialContributionPercent": 75
      },
      "loveIndex": {
        "total": 80,
        "level": 8
      }
    },
    {
      "id": "01a03bdd-1d3d-728b-bf9d-8e31e1a97a61",
      "name": "甲2021653",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787712541&OSSAccessKeyId=placeholder&Signature=xcBRB3yWSY3UD1AOerj%2FzaTFkvE%3D"
      },
      "address": "测试路 1 号",
      "tags": [],
      "scores": {
        "safetyEnvironmentPercent": 83,
        "businessRightsPercent": 80,
        "experienceFriendlyPercent": 80,
        "socialContributionPercent": 75
      },
      "loveIndex": {
        "total": 80,
        "level": 8
      }
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 3,
  "totalPages": 1
}
```

## Step 6: app 端商户列表（带 recommendListId，应被忽略）

```bash
curl -s -i -H "X-API-Key: test-api-key" "http://localhost:8081/api/app/merchants/page?cityId=01a03bdd-1d29-7469-8c5a-76b7a1d7bfb6&recommendListId=01a03bdd-1d7d-78c8-b85a-9ed0536a3919"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

{
  "content": [
    {
      "id": "01a03bdd-1d54-79c6-b859-816353ae5bf7",
      "name": "乙2021653",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787712541&OSSAccessKeyId=placeholder&Signature=xcBRB3yWSY3UD1AOerj%2FzaTFkvE%3D"
      },
      "address": "测试路 1 号",
      "tags": [],
      "scores": {
        "safetyEnvironmentPercent": 83,
        "businessRightsPercent": 80,
        "experienceFriendlyPercent": 80,
        "socialContributionPercent": 75
      },
      "loveIndex": {
        "total": 80,
        "level": 8
      }
    },
    {
      "id": "01a03bdd-1d6a-7070-9c40-7eafbf8118a7",
      "name": "丙2021653",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787712541&OSSAccessKeyId=placeholder&Signature=xcBRB3yWSY3UD1AOerj%2FzaTFkvE%3D"
      },
      "address": "测试路 1 号",
      "tags": [],
      "scores": {
        "safetyEnvironmentPercent": 83,
        "businessRightsPercent": 80,
        "experienceFriendlyPercent": 80,
        "socialContributionPercent": 75
      },
      "loveIndex": {
        "total": 80,
        "level": 8
      }
    },
    {
      "id": "01a03bdd-1d3d-728b-bf9d-8e31e1a97a61",
      "name": "甲2021653",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1787712541&OSSAccessKeyId=placeholder&Signature=xcBRB3yWSY3UD1AOerj%2FzaTFkvE%3D"
      },
      "address": "测试路 1 号",
      "tags": [],
      "scores": {
        "safetyEnvironmentPercent": 83,
        "businessRightsPercent": 80,
        "experienceFriendlyPercent": 80,
        "socialContributionPercent": 75
      },
      "loveIndex": {
        "total": 80,
        "level": 8
      }
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 3,
  "totalPages": 1
}
```
