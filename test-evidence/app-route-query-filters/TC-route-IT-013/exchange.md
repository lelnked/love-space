# TC-route-IT-013 GET /api/app/routes 大使下线后路线隐藏、详情 404 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
app 端请求头 `X-API-Key: $APP_API_KEY`。shell 中 `export TOKEN=<登录返回 token>`、`export APP_API_KEY=<app API key>` 后下列 curl 可原样执行。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"下线城013","englishName":"OfflineCity013","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-e798-7583-8fc0-478e26ad6a73",
  "chineseName": "下线城013",
  "englishName": "OfflineCity013",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-24T16:54:29.528286014Z",
  "updatedAt": "2026-08-24T16:54:29.528286014Z"
}
```

## Step 1b: 创建 online=true 的大使

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/01a034b1-e7a8-75da-89fa-353c4c9585a6.png","name":"下线大使013","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-eb3a-7e33-9b5c-3c5068057238",
  "avatar": {
    "id": "bound/01a034b1-e7a8-75da-89fa-353c4c9585a6.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-e7a8-75da-89fa-353c4c9585a6.png?Expires=1787592270&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=erKOUI31KjF%2Ft%2BgOzx0qj2Sk0S0%3D"
  },
  "name": "下线大使013",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-24T16:54:30.458826598Z",
  "updatedAt": "2026-08-24T16:54:30.458826598Z"
}
```

## Step 1c: 创建路线

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"下线城013","sortOrder":1,"title":"下线路线013","ambassadorNote":"语","thumbnail":"images/01a034b1-eb4e-7038-97cc-612cc85e195c.png","images":["images/01a034b1-ed55-727f-be1e-ca6e4f097410.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b1-eb3a-7e33-9b5c-3c5068057238","spots":[{"name":"S1","image":"images/01a034b1-efab-713c-8d98-fffdc10280b4.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-f57e-7b65-84db-e675be0fa80d",
  "sortOrder": 1,
  "title": "下线路线013",
  "cityName": "下线城013",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b1-eb4e-7038-97cc-612cc85e195c.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-eb4e-7038-97cc-612cc85e195c.png?Expires=1787592273&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZBXF%2FOIn7gT1tQyXZHv91oym6KE%3D"
  },
  "images": [
    {
      "id": "bound/01a034b1-ed55-727f-be1e-ca6e4f097410.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-ed55-727f-be1e-ca6e4f097410.png?Expires=1787592273&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=uDj4xVaBrQaAPsenAjCbNL8vG4U%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "下线大使013",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b1-efab-713c-8d98-fffdc10280b4.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-efab-713c-8d98-fffdc10280b4.png?Expires=1787592273&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2FaqQSEbPDNd7kdPcmeGAOCCmYMw%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:54:33.086625425Z",
  "updatedAt": "2026-08-24T16:54:33.086625425Z"
}
```

## Step 1d: 下线前 app 端列表可见该路线

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityName=%E4%B8%8B%E7%BA%BF%E5%9F%8E013" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a034b1-f57e-7b65-84db-e675be0fa80d",
    "title": "下线路线013",
    "thumbnail": {
      "id": "bound/01a034b1-eb4e-7038-97cc-612cc85e195c.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-eb4e-7038-97cc-612cc85e195c.png?Expires=1787592273&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZBXF%2FOIn7gT1tQyXZHv91oym6KE%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "下线大使013",
    "city": {
      "id": "01a034b1-e798-7583-8fc0-478e26ad6a73",
      "name": "下线城013"
    }
  }
]
```

## Step 2: 将关联大使下线

```bash
curl -s -i -X PUT "http://localhost:8080/api/admin/ambassadors/01a034b1-eb3a-7e33-9b5c-3c5068057238/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b1-eb3a-7e33-9b5c-3c5068057238",
  "avatar": {
    "id": "bound/01a034b1-e7a8-75da-89fa-353c4c9585a6.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-e7a8-75da-89fa-353c4c9585a6.png?Expires=1787592273&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=sRYYDkMfTQpViPUOUGSieCzPZQk%3D"
  },
  "name": "下线大使013",
  "tags": [
    "向导"
  ],
  "online": false,
  "createdAt": "2026-08-24T16:54:30.458827Z",
  "updatedAt": "2026-08-24T16:54:30.458827Z"
}
```

## Step 3: 下线后 app 端列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityName=%E4%B8%8B%E7%BA%BF%E5%9F%8E013" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[]
```

## Step 4: 下线后 app 端路线详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes/01a034b1-f57e-7b65-84db-e675be0fa80d" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 404，Content-Type: application/json）:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "route not found: 01a034b1-f57e-7b65-84db-e675be0fa80d",
  "path": "/api/app/routes/01a034b1-f57e-7b65-84db-e675be0fa80d"
}
```
