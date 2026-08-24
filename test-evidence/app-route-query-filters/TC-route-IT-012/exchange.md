# TC-route-IT-012 GET /api/app/routes?cityName= 按城市名查路线列表并按 sortOrder 升序 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
app 端请求头 `X-API-Key: $APP_API_KEY`。shell 中 `export TOKEN=<登录返回 token>`、`export APP_API_KEY=<app API key>` 后下列 curl 可原样执行。
图片 objectKey 由 POST /api/admin/files/upload-credentials 申请后直传 OSS 得到。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"排序城012","englishName":"SortCity012","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-8286-7501-a465-a8252a3228d4",
  "chineseName": "排序城012",
  "englishName": "SortCity012",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-24T16:54:03.654258636Z",
  "updatedAt": "2026-08-24T16:54:03.654258636Z"
}
```

## Step 1b: 创建 online=true 的大使

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/01a034b1-8298-7be5-ac83-d197b9e79935.png","name":"路线大使012","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-85cf-77be-9617-6385e104fcd4",
  "avatar": {
    "id": "bound/01a034b1-8298-7be5-ac83-d197b9e79935.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-8298-7be5-ac83-d197b9e79935.png?Expires=1787592244&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VsrQidl8Q3lE8%2F%2BXOnGuuwLKAmg%3D"
  },
  "name": "路线大使012",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-24T16:54:04.495422932Z",
  "updatedAt": "2026-08-24T16:54:04.495422932Z"
}
```

## Step 1c-5: 创建 sortOrder=5 的路线

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"排序城012","sortOrder":5,"title":"排序路线012-5","ambassadorNote":"语","thumbnail":"images/01a034b1-85e3-7fb6-b2bf-bb3ea285f922.png","images":["images/01a034b1-8841-7201-a7cd-cdbfc72917d2.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b1-85cf-77be-9617-6385e104fcd4","spots":[{"name":"S1","image":"images/01a034b1-8cc7-78af-8e66-ad85d00f4f81.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-908e-76e9-af1a-2ef66c12eaf5",
  "sortOrder": 5,
  "title": "排序路线012-5",
  "cityName": "排序城012",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b1-85e3-7fb6-b2bf-bb3ea285f922.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-85e3-7fb6-b2bf-bb3ea285f922.png?Expires=1787592247&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=S2Qglj6rt7NFlSZUr%2FEm1Qy4xps%3D"
  },
  "images": [
    {
      "id": "bound/01a034b1-8841-7201-a7cd-cdbfc72917d2.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-8841-7201-a7cd-cdbfc72917d2.png?Expires=1787592247&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=EoV6J5BVfwyu7f5v8Lp1ZMg33hk%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "路线大使012",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b1-8cc7-78af-8e66-ad85d00f4f81.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-8cc7-78af-8e66-ad85d00f4f81.png?Expires=1787592247&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=jLVnVhw4zkHCpld7bGyTS36zl2E%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:54:07.24636812Z",
  "updatedAt": "2026-08-24T16:54:07.24636812Z"
}
```

## Step 1c-1: 创建 sortOrder=1 的路线

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"排序城012","sortOrder":1,"title":"排序路线012-1","ambassadorNote":"语","thumbnail":"images/01a034b1-90a0-7def-b3ed-931fb04f0e24.png","images":["images/01a034b1-928d-7c9b-b2fa-3926a625fc20.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b1-85cf-77be-9617-6385e104fcd4","spots":[{"name":"S1","image":"images/01a034b1-9521-71d5-ac47-d1e1310f934d.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-98ce-75aa-b23b-bdf7929f5227",
  "sortOrder": 1,
  "title": "排序路线012-1",
  "cityName": "排序城012",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b1-90a0-7def-b3ed-931fb04f0e24.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-90a0-7def-b3ed-931fb04f0e24.png?Expires=1787592249&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lRH1i%2BaK2fcHivfRwF1mewZvJBE%3D"
  },
  "images": [
    {
      "id": "bound/01a034b1-928d-7c9b-b2fa-3926a625fc20.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-928d-7c9b-b2fa-3926a625fc20.png?Expires=1787592249&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tiS9wv%2F5bqWk67S2T82VWOMBKhY%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "路线大使012",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b1-9521-71d5-ac47-d1e1310f934d.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-9521-71d5-ac47-d1e1310f934d.png?Expires=1787592249&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=kl%2BiHQFrA1axWTEDZ0h4Ucn%2FCFk%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:54:09.358291593Z",
  "updatedAt": "2026-08-24T16:54:09.358291593Z"
}
```

## Step 1c-3: 创建 sortOrder=3 的路线

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"排序城012","sortOrder":3,"title":"排序路线012-3","ambassadorNote":"语","thumbnail":"images/01a034b1-98e1-79d3-bb5c-5ceb40ef77f4.png","images":["images/01a034b1-a055-7e05-aaad-109d6cdc4a06.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b1-85cf-77be-9617-6385e104fcd4","spots":[{"name":"S1","image":"images/01a034b1-a2f6-7782-842c-9891ef67262c.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-a80e-77f1-b582-7d253aa701e1",
  "sortOrder": 3,
  "title": "排序路线012-3",
  "cityName": "排序城012",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b1-98e1-79d3-bb5c-5ceb40ef77f4.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-98e1-79d3-bb5c-5ceb40ef77f4.png?Expires=1787592253&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ibBlUujKbMI2epU9Df%2F1likA%2Bes%3D"
  },
  "images": [
    {
      "id": "bound/01a034b1-a055-7e05-aaad-109d6cdc4a06.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-a055-7e05-aaad-109d6cdc4a06.png?Expires=1787592253&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=gfNNkmjPF456czGwsQa6HzT5x%2FI%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "路线大使012",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b1-a2f6-7782-842c-9891ef67262c.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-a2f6-7782-842c-9891ef67262c.png?Expires=1787592253&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=9a9ZVKSaKDuv0TV8k13ySekTQIY%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:54:13.262431329Z",
  "updatedAt": "2026-08-24T16:54:13.262431329Z"
}
```

## Step 2: app 端按城市名查询路线列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityName=%E6%8E%92%E5%BA%8F%E5%9F%8E012" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a034b1-98ce-75aa-b23b-bdf7929f5227",
    "title": "排序路线012-1",
    "thumbnail": {
      "id": "bound/01a034b1-90a0-7def-b3ed-931fb04f0e24.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-90a0-7def-b3ed-931fb04f0e24.png?Expires=1787592253&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=eawg9DYIcnTjqjeYrl5fVtDyDHU%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "路线大使012",
    "city": {
      "id": "01a034b1-8286-7501-a465-a8252a3228d4",
      "name": "排序城012"
    }
  },
  {
    "id": "01a034b1-a80e-77f1-b582-7d253aa701e1",
    "title": "排序路线012-3",
    "thumbnail": {
      "id": "bound/01a034b1-98e1-79d3-bb5c-5ceb40ef77f4.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-98e1-79d3-bb5c-5ceb40ef77f4.png?Expires=1787592253&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ibBlUujKbMI2epU9Df%2F1likA%2Bes%3D"
    },
    "sortOrder": 3,
    "ambassadorName": "路线大使012",
    "city": {
      "id": "01a034b1-8286-7501-a465-a8252a3228d4",
      "name": "排序城012"
    }
  },
  {
    "id": "01a034b1-908e-76e9-af1a-2ef66c12eaf5",
    "title": "排序路线012-5",
    "thumbnail": {
      "id": "bound/01a034b1-85e3-7fb6-b2bf-bb3ea285f922.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-85e3-7fb6-b2bf-bb3ea285f922.png?Expires=1787592253&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7%2FdhdAb6%2B1HUvF%2BIKsWM80G19TI%3D"
    },
    "sortOrder": 5,
    "ambassadorName": "路线大使012",
    "city": {
      "id": "01a034b1-8286-7501-a465-a8252a3228d4",
      "name": "排序城012"
    }
  }
]
```
