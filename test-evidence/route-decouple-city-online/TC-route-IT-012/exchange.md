# TC-route-IT-012 GET /api/app/routes 上架城市路线列表按 sortOrder 升序 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"排序城012","englishName":"SortCity012X","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-0fac-7a7e-a2de-80d56cb86491",
  "chineseName": "排序城012",
  "englishName": "SortCity012X",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T15:08:05.932592039Z",
  "updatedAt": "2026-08-20T15:08:05.932592039Z"
}
```

## Step 1b: 创建 online=true 的大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/it012-avatar.png","name":"路线大使012","tags":["向导"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-0fe5-784e-92e0-065f8df9407d",
  "avatar": {
    "id": "bound/it012-avatar.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-avatar.png?Expires=1787240285&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=40PQ66x2n926Ae7e9xTlrnHRTUM%3D"
  },
  "name": "路线大使012",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-20T15:08:05.989453938Z",
  "updatedAt": "2026-08-20T15:08:05.989453938Z"
}
```

## Step 1c-5: 创建 sortOrder=5 的路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb7-0fac-7a7e-a2de-80d56cb86491","sortOrder":5,"title":"排序路线012-5","ambassadorNote":"语","thumbnail":"images/it012-thumb-5.png","images":["images/it012-img-5.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a01fb7-0fe5-784e-92e0-065f8df9407d","spots":[{"name":"S1","image":"images/it012-s1-5.png","introduction":"i1"}]}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-1022-7128-99e9-0a4446a24525",
  "cityId": "01a01fb7-0fac-7a7e-a2de-80d56cb86491",
  "sortOrder": 5,
  "title": "排序路线012-5",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/it012-thumb-5.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-thumb-5.png?Expires=1787240286&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=A5XaiArAXtBdeiBrwOu0fGBHJ5I%3D"
  },
  "images": [
    {
      "id": "bound/it012-img-5.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-img-5.png?Expires=1787240286&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=PjJnCemHEl9mP3Xt9IVDRz1eKEY%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a01fb7-0fe5-784e-92e0-065f8df9407d",
  "ambassadorName": "路线大使012",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/it012-s1-5.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-s1-5.png?Expires=1787240286&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=TloqTsc3cmZjSWb9dpQjK%2FoWQP0%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-20T15:08:06.049991593Z",
  "updatedAt": "2026-08-20T15:08:06.049991593Z"
}
```

## Step 1c-1: 创建 sortOrder=1 的路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb7-0fac-7a7e-a2de-80d56cb86491","sortOrder":1,"title":"排序路线012-1","ambassadorNote":"语","thumbnail":"images/it012-thumb-1.png","images":["images/it012-img-1.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a01fb7-0fe5-784e-92e0-065f8df9407d","spots":[{"name":"S1","image":"images/it012-s1-1.png","introduction":"i1"}]}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-104b-7f6f-86c4-434b4d640d3c",
  "cityId": "01a01fb7-0fac-7a7e-a2de-80d56cb86491",
  "sortOrder": 1,
  "title": "排序路线012-1",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/it012-thumb-1.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-thumb-1.png?Expires=1787240286&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=3YKq6JseXE4RgMrydQJ%2Fu1pXtr4%3D"
  },
  "images": [
    {
      "id": "bound/it012-img-1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-img-1.png?Expires=1787240286&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Ii8XyAUBU78%2F%2FW6lBtk7o7hSeE4%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a01fb7-0fe5-784e-92e0-065f8df9407d",
  "ambassadorName": "路线大使012",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/it012-s1-1.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-s1-1.png?Expires=1787240286&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=bjv22CkD80cMs%2B1OeFz%2FmAMv6%2BU%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-20T15:08:06.091871147Z",
  "updatedAt": "2026-08-20T15:08:06.091871147Z"
}
```

## Step 1c-3: 创建 sortOrder=3 的路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a01fb7-0fac-7a7e-a2de-80d56cb86491","sortOrder":3,"title":"排序路线012-3","ambassadorNote":"语","thumbnail":"images/it012-thumb-3.png","images":["images/it012-img-3.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a01fb7-0fe5-784e-92e0-065f8df9407d","spots":[{"name":"S1","image":"images/it012-s1-3.png","introduction":"i1"}]}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01fb7-1074-7c15-987e-9efa79f8e23b",
  "cityId": "01a01fb7-0fac-7a7e-a2de-80d56cb86491",
  "sortOrder": 3,
  "title": "排序路线012-3",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/it012-thumb-3.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-thumb-3.png?Expires=1787240286&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=PVICj5hz6LwCMxDz9kmgH35EG%2Fo%3D"
  },
  "images": [
    {
      "id": "bound/it012-img-3.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-img-3.png?Expires=1787240286&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=3IC%2B6exagJm8H2jxtdfXf6sB06k%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a01fb7-0fe5-784e-92e0-065f8df9407d",
  "ambassadorName": "路线大使012",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/it012-s1-3.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-s1-3.png?Expires=1787240286&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Sbgl9H1MBFnuaflxtWs0n3qiVbY%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-20T15:08:06.132695575Z",
  "updatedAt": "2026-08-20T15:08:06.132695575Z"
}
```

## Step 2: app 端查询该城市路线列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityId=01a01fb7-0fac-7a7e-a2de-80d56cb86491" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a01fb7-104b-7f6f-86c4-434b4d640d3c",
    "title": "排序路线012-1",
    "thumbnail": {
      "id": "bound/it012-thumb-1.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it012-thumb-1.png?Expires=1787240286&OSSAccessKeyId=test-oss-ak&Signature=4apC86Xo7N%2Fnf6EfQvXlZj5Tbag%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "路线大使012"
  },
  {
    "id": "01a01fb7-1074-7c15-987e-9efa79f8e23b",
    "title": "排序路线012-3",
    "thumbnail": {
      "id": "bound/it012-thumb-3.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it012-thumb-3.png?Expires=1787240286&OSSAccessKeyId=test-oss-ak&Signature=J7oszAt16P1BehnSvofQok0eH6I%3D"
    },
    "sortOrder": 3,
    "ambassadorName": "路线大使012"
  },
  {
    "id": "01a01fb7-1022-7128-99e9-0a4446a24525",
    "title": "排序路线012-5",
    "thumbnail": {
      "id": "bound/it012-thumb-5.png",
      "url": "https://love-space-test.oss-test.example.com/bound/it012-thumb-5.png?Expires=1787240286&OSSAccessKeyId=test-oss-ak&Signature=Fu9MgbOOtV0LS0rqhf0dWPKyDTs%3D"
    },
    "sortOrder": 5,
    "ambassadorName": "路线大使012"
  }
]
```
