# TC-featured-IT-004 PUT /api/admin/featured-items/{id} 更新条目且 cityId 不可变 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> 前置：城市 A（id=01a00b98-524e-7a4a-ba99-d12f1e7e3297）下条目（id=01a00b98-5274-719c-8619-76dc30c748df）；token 复用本轮统一登录

## Step 1: 前置：POST /api/admin/cities 创建城市 B

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"精选城B172204","englishName":"FeatCityB172204","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-53d7-7822-afdb-98dcac5e82c5",
  "chineseName": "精选城B172204",
  "englishName": "FeatCityB172204",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T17:22:07.447416605Z",
  "updatedAt": "2026-08-16T17:22:07.447416605Z"
}
```

## Step 2: PUT /api/admin/featured-items/{id} 改 description/banner，cityId 传城市 B

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-items/01a00b98-5274-719c-8619-76dc30c748df" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a00b98-53d7-7822-afdb-98dcac5e82c5","banner":"images/feat004-new-172204.png","description":"更新后的推荐说明","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5274-719c-8619-76dc30c748df",
  "cityId": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
  "banner": {
    "id": "bound/feat004-new-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat004-new-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=89P9FHm7dLZAC9083YfAjfH0bU0%3D"
  },
  "description": "更新后的推荐说明",
  "online": true,
  "createdAt": "2026-08-16T17:22:07.091985Z",
  "updatedAt": "2026-08-16T17:22:07.372746Z"
}
```

## Step 3: GET /api/admin/featured-items/{id}

```bash
curl -s -i "http://localhost:21423/api/admin/featured-items/01a00b98-5274-719c-8619-76dc30c748df" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5274-719c-8619-76dc30c748df",
  "cityId": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
  "banner": {
    "id": "bound/feat004-new-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat004-new-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=89P9FHm7dLZAC9083YfAjfH0bU0%3D"
  },
  "description": "更新后的推荐说明",
  "online": true,
  "createdAt": "2026-08-16T17:22:07.091985Z",
  "updatedAt": "2026-08-16T17:22:07.483848Z"
}
```
