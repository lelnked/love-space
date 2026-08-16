# TC-route-IT-009 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建城市 A

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"路线城9A-162725","englishName":"City1627253124","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-47d6-781b-80cd-f8cb7808dcc7",
  "chineseName": "路线城9A-162725",
  "englishName": "City1627253124",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:27.574415509Z",
  "updatedAt": "2026-08-16T16:27:27.574415509Z"
}
```

## Step 2: 前置：创建城市 B

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"路线城9B-162725","englishName":"City16272525306","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4826-7e22-9376-8bcc818d3483",
  "chineseName": "路线城9B-162725",
  "englishName": "City16272525306",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:27.654797955Z",
  "updatedAt": "2026-08-16T16:27:27.654797955Z"
}
```

## Step 3: 前置：创建上线大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/50e7a203-195a-4804-8ef2-03fecaebd461.png","name":"路线大使9-162725","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-485d-78e1-a86b-e8ac663831b6",
  "avatar": {
    "id": "bound/50e7a203-195a-4804-8ef2-03fecaebd461.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/50e7a203-195a-4804-8ef2-03fecaebd461.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=LksjZYL9%2BX7uppjJQAz0J%2BDkdcw%3D"
  },
  "name": "路线大使9-162725",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:27.70946895Z",
  "updatedAt": "2026-08-16T16:27:27.70946895Z"
}
```

## Step 4: 前置：城市 A 下创建路线（2 地点）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-47d6-781b-80cd-f8cb7808dcc7","sortOrder":2,"title":"原路线-162725","ambassadorNote":"大使推荐语","thumbnail":"images/a9865257-2f50-4155-ad9e-9d8f2d33db06.png","images":["images/6b8961a6-4b60-448d-b081-3b93c37261cd.png","images/072cce9b-4dc9-4cf5-85cc-5f7df70c2d5d.jpg"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a00b66-485d-78e1-a86b-e8ac663831b6","spots":[{"name":"S1 江畔步道","image":"images/3afc7268-6513-4d35-85c7-d3d745bf7909.png","introduction":"清晨散步"},{"name":"S2 咖啡小馆","image":"images/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png","introduction":"午后咖啡"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-488d-70be-8776-5fc0077db2d0",
  "cityId": "01a00b66-47d6-781b-80cd-f8cb7808dcc7",
  "sortOrder": 2,
  "title": "原路线-162725",
  "ambassadorNote": "大使推荐语",
  "thumbnail": {
    "id": "bound/a9865257-2f50-4155-ad9e-9d8f2d33db06.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a9865257-2f50-4155-ad9e-9d8f2d33db06.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=E4SaaelLx9psO9eb2rYsh2%2Frcgs%3D"
  },
  "images": [
    {
      "id": "bound/6b8961a6-4b60-448d-b081-3b93c37261cd.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/6b8961a6-4b60-448d-b081-3b93c37261cd.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Gs5cihiHF9ICmR54NZoqzGYL3II%3D"
    },
    {
      "id": "bound/072cce9b-4dc9-4cf5-85cc-5f7df70c2d5d.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/072cce9b-4dc9-4cf5-85cc-5f7df70c2d5d.jpg?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=wkCV%2FqiwigTauuM%2B75E0nTDBomc%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorId": "01a00b66-485d-78e1-a86b-e8ac663831b6",
  "ambassadorName": "路线大使9-162725",
  "spots": [
    {
      "name": "S1 江畔步道",
      "image": {
        "id": "bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3afc7268-6513-4d35-85c7-d3d745bf7909.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=aRTaQsNGr68qOR%2Fa%2Bb6GmO%2BZPds%3D"
      },
      "introduction": "清晨散步"
    },
    {
      "name": "S2 咖啡小馆",
      "image": {
        "id": "bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c4ffa4fa-8ec7-45de-bbc3-ea9dadd8c8e7.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6t7O36v87Qre5Izrwv9gRbZXz3I%3D"
      },
      "introduction": "午后咖啡"
    }
  ],
  "createdAt": "2026-08-16T16:27:27.756971609Z",
  "updatedAt": "2026-08-16T16:27:27.756971609Z"
}
```

## Step 5: PUT /api/admin/routes/{id} 改 title/sortOrder=9/spots 1 个新地点/cityId 传城市 B

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/routes/01a00b66-488d-70be-8776-5fc0077db2d0" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-4826-7e22-9376-8bcc818d3483","sortOrder":9,"title":"改名路线","thumbnail":"images/84461a82-9010-4162-aa38-d96a1422fb00.png","images":["images/6e3efe30-9bcd-4cd4-a9c3-16b5f065a122.png"],"ambassadorId":"01a00b66-485d-78e1-a86b-e8ac663831b6","spots":[{"name":"新地点 N1","image":"images/c73903de-5fd5-4182-b855-8e1b29a27476.png","introduction":"replace"}]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-488d-70be-8776-5fc0077db2d0",
  "cityId": "01a00b66-47d6-781b-80cd-f8cb7808dcc7",
  "sortOrder": 9,
  "title": "改名路线",
  "ambassadorNote": null,
  "thumbnail": {
    "id": "bound/84461a82-9010-4162-aa38-d96a1422fb00.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/84461a82-9010-4162-aa38-d96a1422fb00.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=O3Hp5jH3qFE%2Fw4TcZuIGEYAu0bM%3D"
  },
  "images": [
    {
      "id": "bound/6e3efe30-9bcd-4cd4-a9c3-16b5f065a122.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/6e3efe30-9bcd-4cd4-a9c3-16b5f065a122.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=bsfa6ZoQjsowqsq%2F%2BEggaHn6UC0%3D"
    }
  ],
  "travelTime": null,
  "season": null,
  "travelStatus": null,
  "ambassadorId": "01a00b66-485d-78e1-a86b-e8ac663831b6",
  "ambassadorName": "路线大使9-162725",
  "spots": [
    {
      "name": "新地点 N1",
      "image": {
        "id": "bound/c73903de-5fd5-4182-b855-8e1b29a27476.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c73903de-5fd5-4182-b855-8e1b29a27476.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=2TIKrMSyvqdurVbM2RYVGyGL9oA%3D"
      },
      "introduction": "replace"
    }
  ],
  "createdAt": "2026-08-16T16:27:27.756972Z",
  "updatedAt": "2026-08-16T16:27:27.756972Z"
}
```

## Step 6: GET 详情确认更新生效且 cityId 仍为城市 A

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/routes/01a00b66-488d-70be-8776-5fc0077db2d0" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-488d-70be-8776-5fc0077db2d0",
  "cityId": "01a00b66-47d6-781b-80cd-f8cb7808dcc7",
  "sortOrder": 9,
  "title": "改名路线",
  "ambassadorNote": null,
  "thumbnail": {
    "id": "bound/84461a82-9010-4162-aa38-d96a1422fb00.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/84461a82-9010-4162-aa38-d96a1422fb00.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=O3Hp5jH3qFE%2Fw4TcZuIGEYAu0bM%3D"
  },
  "images": [
    {
      "id": "bound/6e3efe30-9bcd-4cd4-a9c3-16b5f065a122.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/6e3efe30-9bcd-4cd4-a9c3-16b5f065a122.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=bsfa6ZoQjsowqsq%2F%2BEggaHn6UC0%3D"
    }
  ],
  "travelTime": null,
  "season": null,
  "travelStatus": null,
  "ambassadorId": "01a00b66-485d-78e1-a86b-e8ac663831b6",
  "ambassadorName": "路线大使9-162725",
  "spots": [
    {
      "name": "新地点 N1",
      "image": {
        "id": "bound/c73903de-5fd5-4182-b855-8e1b29a27476.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c73903de-5fd5-4182-b855-8e1b29a27476.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=2TIKrMSyvqdurVbM2RYVGyGL9oA%3D"
      },
      "introduction": "replace"
    }
  ],
  "createdAt": "2026-08-16T16:27:27.756972Z",
  "updatedAt": "2026-08-16T16:27:27.822975Z"
}
```

