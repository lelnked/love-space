# TC-city-IT-007 城市下架后 app 端精选推荐不可见（级联） — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"精选城007","englishName":"FeatCity007X","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb6-b9f0-75ae-a200-0f2dec0a7727",
  "chineseName": "精选城007",
  "englishName": "FeatCity007X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:07:43.984273606Z",
  "updatedAt": "2026-08-20T15:07:43.984273606Z"
}
```

## Step 1b: 创建上线精选推荐条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb6-b9f0-75ae-a200-0f2dec0a7727","banner":"images/it007-banner.png","description":"地图上新007","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb6-ba2f-74ef-a2b3-4da6eb87b82d",
  "cityId": "01a01fb6-b9f0-75ae-a200-0f2dec0a7727",
  "banner": {
    "id": "bound/it007-banner.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-banner.png?Expires=1787240264&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cSk%2FSTQ34HBJJbQRtndhjDGV6fQ%3D"
  },
  "description": "地图上新007",
  "online": true,
  "createdAt": "2026-08-20T15:07:44.046086398Z",
  "updatedAt": "2026-08-20T15:07:44.046086398Z"
}
```

## Step 1c: 前置确认 app 信息流含该条目

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a01fb6-ba2f-74ef-a2b3-4da6eb87b82d",
    "banner": {
      "id": "bound/it007-banner.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it007-banner.png?Expires=1787240264&OSSAccessKeyId=test-oss-ak&Signature=fvmhMPW3Kz0Bk66eanhftjwBUzo%3D"
    },
    "description": "地图上新007",
    "city": {
      "id": "01a01fb6-b9f0-75ae-a200-0f2dec0a7727",
      "name": "精选城007"
    }
  },
  {
    "id": "01a01f6d-0eeb-76b3-a65d-2dd092c6f232",
    "banner": {
      "id": "bound/341f65e8-b384-4371-851e-b9ee9423d3c2.png",
      "url": "https://love-space-test.oss-test.example.com/bound/341f65e8-b384-4371-851e-b9ee9423d3c2.png?Expires=1787240264&OSSAccessKeyId=test-oss-ak&Signature=zj0jzF0T%2Fy6wst0V2EKkJp%2B6iE8%3D"
    },
    "description": "信息流条目二",
    "city": {
      "id": "01a01f6d-0ee0-7af5-aec6-7618ae5f783d",
      "name": "精选城R006"
    }
  },
  {
    "id": "01a01f6d-0ee6-74a0-971b-e48eeb563531",
    "banner": {
      "id": "bound/60437924-3a05-4fb9-84f5-5e679e0de8c2.png",
      "url": "https://love-space-test.oss-test.example.com/bound/60437924-3a05-4fb9-84f5-5e679e0de8c2.png?Expires=1787240264&OSSAccessKeyId=test-oss-ak&Signature=ukCwwbpakLoawNyLWxSulvc1ngw%3D"
    },
    "description": "信息流条目一",
    "city": {
      "id": "01a01f6d-0ee0-7af5-aec6-7618ae5f783d",
      "name": "精选城R006"
    }
  }
]
```

## Step 2: admin 侧将该城市下架

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a01fb6-b9f0-75ae-a200-0f2dec0a7727/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb6-b9f0-75ae-a200-0f2dec0a7727",
  "chineseName": "精选城007",
  "englishName": "FeatCity007X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-20T15:07:43.984274Z",
  "updatedAt": "2026-08-20T15:07:43.984274Z"
}
```

## Step 3: 下架后再次查询 app 信息流

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a01f6d-0eeb-76b3-a65d-2dd092c6f232",
    "banner": {
      "id": "bound/341f65e8-b384-4371-851e-b9ee9423d3c2.png",
      "url": "https://love-space-test.oss-test.example.com/bound/341f65e8-b384-4371-851e-b9ee9423d3c2.png?Expires=1787240264&OSSAccessKeyId=test-oss-ak&Signature=zj0jzF0T%2Fy6wst0V2EKkJp%2B6iE8%3D"
    },
    "description": "信息流条目二",
    "city": {
      "id": "01a01f6d-0ee0-7af5-aec6-7618ae5f783d",
      "name": "精选城R006"
    }
  },
  {
    "id": "01a01f6d-0ee6-74a0-971b-e48eeb563531",
    "banner": {
      "id": "bound/60437924-3a05-4fb9-84f5-5e679e0de8c2.png",
      "url": "https://love-space-test.oss-test.example.com/bound/60437924-3a05-4fb9-84f5-5e679e0de8c2.png?Expires=1787240264&OSSAccessKeyId=test-oss-ak&Signature=ukCwwbpakLoawNyLWxSulvc1ngw%3D"
    },
    "description": "信息流条目一",
    "city": {
      "id": "01a01f6d-0ee0-7af5-aec6-7618ae5f783d",
      "name": "精选城R006"
    }
  }
]
```
