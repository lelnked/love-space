# TC-route-IT-016 GET /api/app/routes 不带任何参数返回全部可见路线 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
app 端请求头 `X-API-Key: $APP_API_KEY`。shell 中 `export TOKEN=<登录返回 token>`、`export APP_API_KEY=<app API key>` 后下列 curl 可原样执行。
前置：本轮开始时 GET /api/app/routes 返回空数组（DB 无可见路线），故全量断言可精确到条数与顺序。
图片 objectKey 由 POST /api/admin/files/upload-credentials 申请后直传 OSS 得到（真实 OSS 校验，非 test profile 桩）。

## Step 1a: 创建城市甲（上架）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"全量城甲016","englishName":"AllCityA016","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-395a-7f77-92b9-3f4c5ca46d9a",
  "chineseName": "全量城甲016",
  "englishName": "AllCityA016",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-24T16:53:44.922883739Z",
  "updatedAt": "2026-08-24T16:53:44.922883739Z"
}
```

## Step 1b: 创建城市乙（上架）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"全量城乙016","englishName":"AllCityB016","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-396e-7503-bce9-1f8a4a863051",
  "chineseName": "全量城乙016",
  "englishName": "AllCityB016",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-24T16:53:44.942254876Z",
  "updatedAt": "2026-08-24T16:53:44.942254876Z"
}
```

## Step 1c: 创建 online=true 的大使

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/01a034b1-238e-78c1-bc60-5c335fc5117d.png","name":"全量大使016","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-3eaa-7fda-88dc-3aae4d5d0d9c",
  "avatar": {
    "id": "bound/01a034b1-238e-78c1-bc60-5c335fc5117d.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-238e-78c1-bc60-5c335fc5117d.png?Expires=1787592226&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=R0Nnm3WXTsUHCfDxz3E2LFmphtk%3D"
  },
  "name": "全量大使016",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-24T16:53:46.281573456Z",
  "updatedAt": "2026-08-24T16:53:46.281573456Z"
}
```

## Step 1d: 城市甲下创建 sortOrder=2 的路线

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"全量城甲016","sortOrder":2,"title":"全量路线016-甲","ambassadorNote":"语","thumbnail":"images/01a034b1-2747-7566-ac8e-2b3e74a51418.png","images":["images/01a034b1-292b-7a87-a48b-f31c2a1d86cf.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b1-3eaa-7fda-88dc-3aae4d5d0d9c","spots":[{"name":"S1","image":"images/01a034b1-2c34-7036-a5ed-7de16790461d.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-40c0-7571-81a2-e7d75ac807a6",
  "sortOrder": 2,
  "title": "全量路线016-甲",
  "cityName": "全量城甲016",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b1-2747-7566-ac8e-2b3e74a51418.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-2747-7566-ac8e-2b3e74a51418.png?Expires=1787592226&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=MzF0LZL5IyCkN%2FBqHQcjog2eqqY%3D"
  },
  "images": [
    {
      "id": "bound/01a034b1-292b-7a87-a48b-f31c2a1d86cf.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-292b-7a87-a48b-f31c2a1d86cf.png?Expires=1787592226&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Ley4sEo3cQf2dcDn2EpRMhgdBqc%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "全量大使016",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b1-2c34-7036-a5ed-7de16790461d.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-2c34-7036-a5ed-7de16790461d.png?Expires=1787592226&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cxPgJMDHgJfdxrTlcnFkLsJAVAM%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:53:46.814753257Z",
  "updatedAt": "2026-08-24T16:53:46.814753257Z"
}
```

## Step 1e: 城市乙下创建 sortOrder=1 的路线

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"全量城乙016","sortOrder":1,"title":"全量路线016-乙","ambassadorNote":"语","thumbnail":"images/01a034b1-2e29-7726-a5f1-ac0608bb3f43.png","images":["images/01a034b1-303d-7900-8a96-b965038ffafa.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b1-3eaa-7fda-88dc-3aae4d5d0d9c","spots":[{"name":"S1","image":"images/01a034b1-3340-7e00-b614-f794ac9cfee2.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-42db-7dfa-9746-c1e9d0f616ba",
  "sortOrder": 1,
  "title": "全量路线016-乙",
  "cityName": "全量城乙016",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b1-2e29-7726-a5f1-ac0608bb3f43.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-2e29-7726-a5f1-ac0608bb3f43.png?Expires=1787592227&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=YUbKf3%2F8liymug2uKXGqqXlqS1Y%3D"
  },
  "images": [
    {
      "id": "bound/01a034b1-303d-7900-8a96-b965038ffafa.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-303d-7900-8a96-b965038ffafa.png?Expires=1787592227&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=GvWwzuTJ4K9ATHPdqB3bHnL9LqM%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "全量大使016",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b1-3340-7e00-b614-f794ac9cfee2.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-3340-7e00-b614-f794ac9cfee2.png?Expires=1787592227&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=biEBli7BCEYLPdCTM5vTOg6a%2FBw%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:53:47.355806087Z",
  "updatedAt": "2026-08-24T16:53:47.355806087Z"
}
```

## Step 2: app 端不带任何查询参数列路线

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a034b1-42db-7dfa-9746-c1e9d0f616ba",
    "title": "全量路线016-乙",
    "thumbnail": {
      "id": "bound/01a034b1-2e29-7726-a5f1-ac0608bb3f43.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-2e29-7726-a5f1-ac0608bb3f43.png?Expires=1787592227&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=YUbKf3%2F8liymug2uKXGqqXlqS1Y%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "全量大使016",
    "city": {
      "id": "01a034b1-396e-7503-bce9-1f8a4a863051",
      "name": "全量城乙016"
    }
  },
  {
    "id": "01a034b1-40c0-7571-81a2-e7d75ac807a6",
    "title": "全量路线016-甲",
    "thumbnail": {
      "id": "bound/01a034b1-2747-7566-ac8e-2b3e74a51418.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-2747-7566-ac8e-2b3e74a51418.png?Expires=1787592227&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZkVDhat1DvTtPG2x3MMdhL56wOs%3D"
    },
    "sortOrder": 2,
    "ambassadorName": "全量大使016",
    "city": {
      "id": "01a034b1-395a-7f77-92b9-3f4c5ca46d9a",
      "name": "全量城甲016"
    }
  }
]
```
