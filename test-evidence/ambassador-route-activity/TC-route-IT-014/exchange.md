# TC-route-IT-014 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"路线城14-162725","englishName":"City1627258771","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-5030-7783-a3ad-69fb02eb1b9e",
  "chineseName": "路线城14-162725",
  "englishName": "City1627258771",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:29.712380517Z",
  "updatedAt": "2026-08-16T16:27:29.712380517Z"
}
```

## Step 2: 前置：创建上线大使（头像/名称/标签）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/85b73af3-12f4-4d60-89de-701788d67e52.png","name":"详情大使-162725","tags":["古着","咖啡"],"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-5055-78f8-baca-48be35642c25",
  "avatar": {
    "id": "bound/85b73af3-12f4-4d60-89de-701788d67e52.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/85b73af3-12f4-4d60-89de-701788d67e52.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=PHsxa7z2t8Plj6vBN1UvZ1yuyFI%3D"
  },
  "name": "详情大使-162725",
  "tags": [
    "古着",
    "咖啡"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:29.749497024Z",
  "updatedAt": "2026-08-16T16:27:29.749497024Z"
}
```

## Step 3: 前置：创建含 2 地点路线

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-5030-7783-a3ad-69fb02eb1b9e","sortOrder":1,"title":"详情路线-162725","ambassadorNote":"大使推荐语","thumbnail":"images/7b5991ff-21be-451e-bca6-5b45f4cd747d.png","images":["images/92608dc8-d767-4ced-9b12-29a20933a3fd.png","images/a5e819be-934d-4c07-a197-5f4cea23c161.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a00b66-5055-78f8-baca-48be35642c25","spots":[{"name":"S1 江畔步道","image":"images/3afc7268-6513-4d35-85c7-d3d745bf7909.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-5086-7752-b713-3fa57a57c6f4",
  "cityId": "01a00b66-5030-7783-a3ad-69fb02eb1b9e",
  "sortOrder": 1,
  "title": "详情路线-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/7b5991ff-21be-451e-bca6-5b45f4cd747d.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/7b5991ff-21be-451e-bca6-5b45f4cd747d.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VzMyWgFgmhFOr7TtUjxxmaMpkNU%3D"
  },
  "images": [
    {
      "id": "bound/92608dc8-d767-4ced-9b12-29a20933a3fd.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/92608dc8-d767-4ced-9b12-29a20933a3fd.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=OP2J16eEPTlvZiofic5wzm8hEAA%3D"
    },
    {
      "id": "bound/a5e819be-934d-4c07-a197-5f4cea23c161.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a5e819be-934d-4c07-a197-5f4cea23c161.jpg?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=KmDkTHzhQmj6mWJey98LsJVDFc0%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a00b66-5055-78f8-baca-48be35642c25",
  "ambassadorName": "详情大使-162725",
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Edj83bv9q5XLMs6PBvV56uAVvBk%3D"
      },
      "introduction": "清晨散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=sutl5Dp5AjG%2BYwRl%2FlIq%2Fbrrygo%3D"
      },
      "introduction": "午后咖啡"
    }
  ],
  "createdAt": "2026-08-16T16:27:29.79839343Z",
  "updatedAt": "2026-08-16T16:27:29.79839343Z"
}
```

## Step 4: GET /api/app/routes/{id} app 端路线详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a00b66-5086-7752-b713-3fa57a57c6f4" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-5086-7752-b713-3fa57a57c6f4",
  "cityId": "01a00b66-5030-7783-a3ad-69fb02eb1b9e",
  "sortOrder": 1,
  "title": "详情路线-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/7b5991ff-21be-451e-bca6-5b45f4cd747d.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/7b5991ff-21be-451e-bca6-5b45f4cd747d.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VzMyWgFgmhFOr7TtUjxxmaMpkNU%3D"
  },
  "images": [
    {
      "id": "bound/92608dc8-d767-4ced-9b12-29a20933a3fd.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/92608dc8-d767-4ced-9b12-29a20933a3fd.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=OP2J16eEPTlvZiofic5wzm8hEAA%3D"
    },
    {
      "id": "bound/a5e819be-934d-4c07-a197-5f4cea23c161.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a5e819be-934d-4c07-a197-5f4cea23c161.jpg?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=KmDkTHzhQmj6mWJey98LsJVDFc0%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassador": {
    "name": "详情大使-162725",
    "avatar": {
      "id": "bound/85b73af3-12f4-4d60-89de-701788d67e52.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/85b73af3-12f4-4d60-89de-701788d67e52.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=PHsxa7z2t8Plj6vBN1UvZ1yuyFI%3D"
    },
    "tags": [
      "古着",
      "咖啡"
    ]
  },
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Edj83bv9q5XLMs6PBvV56uAVvBk%3D"
      },
      "introduction": "清晨散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png?Expires=1786899449&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=sutl5Dp5AjG%2BYwRl%2FlIq%2Fbrrygo%3D"
      },
      "introduction": "午后咖啡"
    }
  ]
}
```

