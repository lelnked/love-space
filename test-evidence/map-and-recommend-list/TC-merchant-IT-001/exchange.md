# TC-merchant-IT-001 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

> 补跑说明：首轮因 test 实例 OSS 不可用标记未执行；现 admin test 实例已启用 test-profile StubObjectKeyValidator（正则校验后直接返回 bound key），本轮补跑。城市夹具复用首轮：城市 A=01a00b34-a31b-726c-8c15-95d9ccbae26b（上架）、城市 B=01a00b34-a4cb-7575-bafe-8b3fc7e32473（上架）。

## Step 1: POST /api/admin/auth/login 获取 JWT

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsb3ZlLXNwYWNlLWFkbWluIiwic3ViIjoiMDE5Nzk0YjYtYjQwMC03MDAwLTgwMDAtMDAwMDAwMDAwMDAxIiwidXNlcm5hbWUiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc4Njg5NDk1OSwiZXhwIjoxNzg2OTM4MTU5fQ.F-M9fdHy0VyqjvDG8u8HuNyj7_H3rskP2HX8uBz75ck",
  "manager": {
    "id": "019794b6-b400-7000-8000-000000000001",
    "username": "admin",
    "nickname": "管理员",
    "role": "ADMIN"
  }
}
```

## Step 2: POST /api/admin/merchants 创建商户（含 recommendReason）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "name": "商户甲154239",
  "logo": "images/logo-test.png",
  "address": "测试路 1 号",
  "longitude": 114.30,
  "latitude": 30.59,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "safetyEnvironmentScore": 25,
  "businessRightsScore": 20,
  "experienceFriendlyScore": 20,
  "socialContributionScore": 15,
  "weight": 0,
  "online": false,
  "images": [
    "images/img-test-1.png"
  ],
  "recommendReason": "步行五分钟即达江景，适合傍晚约会"
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3d-447c-77c9-b935-4199b807e58b",
  "name": "商户甲154239",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896759&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=oUUn7zNt3q2TWZ5B3FnEYsCiHhI%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.30,
  "latitude": 30.59,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "categoryId": null,
  "safetyEnvironmentScore": 25,
  "businessRightsScore": 20,
  "experienceFriendlyScore": 20,
  "socialContributionScore": 15,
  "story": null,
  "recommendReason": "步行五分钟即达江景，适合傍晚约会",
  "weight": 0,
  "online": false,
  "periods": [],
  "tagIds": [],
  "images": [
    {
      "id": "bound/img-test-1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1786896759&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RjeW4%2FO599QOTe3TJ7%2FCBc1eQIA%3D"
    }
  ],
  "createdAt": "2026-08-16T15:42:39.693076466Z",
  "updatedAt": "2026-08-16T15:42:39.693076466Z"
}
```

## Step 3: GET /api/admin/merchants/{id}

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/merchants/01a00b3d-447c-77c9-b935-4199b807e58b" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3d-447c-77c9-b935-4199b807e58b",
  "name": "商户甲154239",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896759&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=oUUn7zNt3q2TWZ5B3FnEYsCiHhI%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.300000,
  "latitude": 30.590000,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "categoryId": null,
  "safetyEnvironmentScore": 25,
  "businessRightsScore": 20,
  "experienceFriendlyScore": 20,
  "socialContributionScore": 15,
  "story": null,
  "recommendReason": "步行五分钟即达江景，适合傍晚约会",
  "weight": 0,
  "online": false,
  "periods": [],
  "tagIds": [],
  "images": [
    {
      "id": "bound/img-test-1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1786896759&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RjeW4%2FO599QOTe3TJ7%2FCBc1eQIA%3D"
    }
  ],
  "createdAt": "2026-08-16T15:42:39.693076Z",
  "updatedAt": "2026-08-16T15:42:39.693076Z"
}
```
