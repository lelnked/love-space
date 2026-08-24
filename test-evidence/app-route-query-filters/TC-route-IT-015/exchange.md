# TC-route-IT-015 未上架城市的路线仍可见且详情返回 cityName — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
app 端请求头 `X-API-Key: $APP_API_KEY`。shell 中 `export TOKEN=<登录返回 token>`、`export APP_API_KEY=<app API key>` 后下列 curl 可原样执行。

## Step 1a: 创建下架城市「未上线城」

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"未上线城","englishName":"OfflineCity015","chineseProvince":"测试省","englishProvince":"Test Province","online":false}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b2-a694-7859-8a25-e5aaf2e0cb5e",
  "chineseName": "未上线城",
  "englishName": "OfflineCity015",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-24T16:55:18.420459048Z",
  "updatedAt": "2026-08-24T16:55:18.420459048Z"
}
```

## Step 1b: 创建 online=true 的大使

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/01a034b2-a6a7-7110-9dad-422b4069fbf8.png","name":"未上线大使015","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b2-aa11-74b0-9236-c4bae4818f26",
  "avatar": {
    "id": "bound/01a034b2-a6a7-7110-9dad-422b4069fbf8.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-a6a7-7110-9dad-422b4069fbf8.png?Expires=1787592319&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=xMZgni%2BAm7ZsabwJ%2FnOCl7cjDns%3D"
  },
  "name": "未上线大使015",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-24T16:55:19.31320636Z",
  "updatedAt": "2026-08-24T16:55:19.31320636Z"
}
```

## Step 1c: 该城市下创建路线

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"未上线城","sortOrder":1,"title":"未上线路线015","ambassadorNote":"语","thumbnail":"images/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png","images":["images/01a034b2-ac95-7bc4-b538-764a962dda39.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b2-aa11-74b0-9236-c4bae4818f26","spots":[{"name":"S1","image":"images/01a034b2-b721-706a-8a51-083aa40533b5.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b2-bb70-77f9-8d97-52853a277766",
  "sortOrder": 1,
  "title": "未上线路线015",
  "cityName": "未上线城",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=hGsOM7KzQsL9jihs3eSEGZtdFMQ%3D"
  },
  "images": [
    {
      "id": "bound/01a034b2-ac95-7bc4-b538-764a962dda39.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-ac95-7bc4-b538-764a962dda39.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=bSUR9DB3A8p3wO%2Be1MJvr4YQv7s%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "未上线大使015",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b2-b721-706a-8a51-083aa40533b5.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-b721-706a-8a51-083aa40533b5.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=kEpWZ5rOdEkwC4yNE%2BDNIgRIN84%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:55:23.760440246Z",
  "updatedAt": "2026-08-24T16:55:23.760440246Z"
}
```

## Step 2: 城市下架时 app 端按城市名列路线

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityName=%E6%9C%AA%E4%B8%8A%E7%BA%BF%E5%9F%8E" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a034b2-bb70-77f9-8d97-52853a277766",
    "title": "未上线路线015",
    "thumbnail": {
      "id": "bound/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=hGsOM7KzQsL9jihs3eSEGZtdFMQ%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "未上线大使015",
    "city": {
      "id": "01a034b2-a694-7859-8a25-e5aaf2e0cb5e",
      "name": "未上线城"
    }
  }
]
```

## Step 3: 城市下架时 app 端路线详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a034b2-bb70-77f9-8d97-52853a277766" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "cityName": "未上线城",
  "sortOrder": 1,
  "title": "未上线路线015",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=hGsOM7KzQsL9jihs3eSEGZtdFMQ%3D"
  },
  "images": [
    {
      "id": "bound/01a034b2-ac95-7bc4-b538-764a962dda39.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-ac95-7bc4-b538-764a962dda39.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=bSUR9DB3A8p3wO%2Be1MJvr4YQv7s%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassador": {
    "name": "未上线大使015",
    "avatar": {
      "id": "bound/01a034b2-a6a7-7110-9dad-422b4069fbf8.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-a6a7-7110-9dad-422b4069fbf8.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=k1S1imCWrVfhgADk%2BpxPTQs6wr4%3D"
    },
    "tags": [
      "向导"
    ]
  },
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b2-b721-706a-8a51-083aa40533b5.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-b721-706a-8a51-083aa40533b5.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=kEpWZ5rOdEkwC4yNE%2BDNIgRIN84%3D"
      },
      "introduction": "i1"
    }
  ],
  "city": {
    "id": "01a034b2-a694-7859-8a25-e5aaf2e0cb5e",
    "name": "未上线城"
  },
  "createdAt": "2026-08-24T16:55:23.76044Z",
  "updatedAt": "2026-08-24T16:55:23.76044Z"
}
```

## Step 4a: 将该城市上架

```bash
curl -s -i -X PUT "http://localhost:8080/api/admin/cities/01a034b2-a694-7859-8a25-e5aaf2e0cb5e/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b2-a694-7859-8a25-e5aaf2e0cb5e",
  "chineseName": "未上线城",
  "englishName": "OfflineCity015",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-24T16:55:18.420459Z",
  "updatedAt": "2026-08-24T16:55:18.420459Z"
}
```

## Step 4b: 城市上架后 app 端按城市名列路线

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityName=%E6%9C%AA%E4%B8%8A%E7%BA%BF%E5%9F%8E" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a034b2-bb70-77f9-8d97-52853a277766",
    "title": "未上线路线015",
    "thumbnail": {
      "id": "bound/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=hGsOM7KzQsL9jihs3eSEGZtdFMQ%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "未上线大使015",
    "city": {
      "id": "01a034b2-a694-7859-8a25-e5aaf2e0cb5e",
      "name": "未上线城"
    }
  }
]
```

## Step 4c: 城市上架后 app 端路线详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a034b2-bb70-77f9-8d97-52853a277766" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "cityName": "未上线城",
  "sortOrder": 1,
  "title": "未上线路线015",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=hGsOM7KzQsL9jihs3eSEGZtdFMQ%3D"
  },
  "images": [
    {
      "id": "bound/01a034b2-ac95-7bc4-b538-764a962dda39.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-ac95-7bc4-b538-764a962dda39.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=bSUR9DB3A8p3wO%2Be1MJvr4YQv7s%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassador": {
    "name": "未上线大使015",
    "avatar": {
      "id": "bound/01a034b2-a6a7-7110-9dad-422b4069fbf8.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-a6a7-7110-9dad-422b4069fbf8.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=k1S1imCWrVfhgADk%2BpxPTQs6wr4%3D"
    },
    "tags": [
      "向导"
    ]
  },
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b2-b721-706a-8a51-083aa40533b5.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-b721-706a-8a51-083aa40533b5.png?Expires=1787592323&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=kEpWZ5rOdEkwC4yNE%2BDNIgRIN84%3D"
      },
      "introduction": "i1"
    }
  ],
  "city": {
    "id": "01a034b2-a694-7859-8a25-e5aaf2e0cb5e",
    "name": "未上线城"
  },
  "createdAt": "2026-08-24T16:55:23.76044Z",
  "updatedAt": "2026-08-24T16:55:23.76044Z"
}
```
