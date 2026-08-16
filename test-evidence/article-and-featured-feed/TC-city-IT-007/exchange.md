# TC-city-IT-007 城市下架后 app 端精选推荐不可见（级联） — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> 前置数据复用 TC-featured-IT-006：上架城市「精选城172204」（id=01a00b98-524e-7a4a-ba99-d12f1e7e3297）下两条上线条目（id=01a00b98-5496-7871-9f38-146dde409188、01a00b98-54b4-7e01-b004-225b720f27a6）；token 复用本轮统一登录

## Step 1: 前置确认：GET /api/app/featured-items 返回该城市条目

```bash
curl -s -i "http://localhost:8081/api/app/featured-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a00b98-54b4-7e01-b004-225b720f27a6",
    "banner": {
      "id": "bound/feat006-two-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-two-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=C8Rhtf0mIRURuL7tVoKSwT7MqtQ%3D"
    },
    "description": "信息流条目二",
    "city": {
      "id": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
      "name": "精选城172204"
    }
  },
  {
    "id": "01a00b98-5496-7871-9f38-146dde409188",
    "banner": {
      "id": "bound/feat006-one-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-one-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ag69GqEwOtdYQJD4WFGLwMX2fp4%3D"
    },
    "description": "信息流条目一",
    "city": {
      "id": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
      "name": "精选城172204"
    }
  }
]
```

## Step 2: PUT /api/admin/cities/{id}/online {"online": false}

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a00b98-524e-7a4a-ba99-d12f1e7e3297/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
  "chineseName": "精选城172204",
  "englishName": "FeatCity172204",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-16T17:22:07.053051Z",
  "updatedAt": "2026-08-16T17:22:07.053051Z"
}
```

## Step 3: GET /api/app/featured-items（X-API-Key）

```bash
curl -s -i "http://localhost:8081/api/app/featured-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[]
```
