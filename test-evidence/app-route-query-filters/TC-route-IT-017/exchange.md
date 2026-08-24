# TC-route-IT-017 GET /api/app/routes?ambassadorId= 按大使过滤路线 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
app 端请求头 `X-API-Key: $APP_API_KEY`。shell 中 `export TOKEN=<登录返回 token>`、`export APP_API_KEY=<app API key>` 后下列 curl 可原样执行。

## Step 1a: 创建上架城市

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"大使过滤城017","englishName":"AmbFilterCity017","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-0920-78ad-b522-4f93f447e12e",
  "chineseName": "大使过滤城017",
  "englishName": "AmbFilterCity017",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-24T16:55:43.648450043Z",
  "updatedAt": "2026-08-24T16:55:43.648450043Z"
}
```

## Step 1b: 创建 online 大使 A

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/01a034b3-0930-7c0b-8546-88763788bc31.png","name":"大使A017","tags":["A"],"online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-0ccf-790c-9353-5eac984d1be2",
  "avatar": {
    "id": "bound/01a034b3-0930-7c0b-8546-88763788bc31.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-0930-7c0b-8546-88763788bc31.png?Expires=1787592344&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=e%2BykHKtKsN86gbuUuUelpjdEgtI%3D"
  },
  "name": "大使A017",
  "tags": [
    "A"
  ],
  "online": true,
  "createdAt": "2026-08-24T16:55:44.591505875Z",
  "updatedAt": "2026-08-24T16:55:44.591505875Z"
}
```

## Step 1c: 创建 online 大使 B

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/01a034b3-0ce2-7b7f-bbfb-0ad072941a91.png","name":"大使B017","tags":["B"],"online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-0f6f-775b-bff8-e63f3c9d7c8f",
  "avatar": {
    "id": "bound/01a034b3-0ce2-7b7f-bbfb-0ad072941a91.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-0ce2-7b7f-bbfb-0ad072941a91.png?Expires=1787592345&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=rGDQ8CxbZN%2Bkw1VDsnRzeUCHsu0%3D"
  },
  "name": "大使B017",
  "tags": [
    "B"
  ],
  "online": true,
  "createdAt": "2026-08-24T16:55:45.263403367Z",
  "updatedAt": "2026-08-24T16:55:45.263403367Z"
}
```

## Step 1d-3: 大使 A 名下路线（sortOrder=3）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"大使过滤城017","sortOrder":3,"title":"A路线017-3","ambassadorNote":"语","thumbnail":"images/01a034b3-0f82-777a-b394-ec6c6df95262.png","images":["images/01a034b3-11af-7a15-85d8-d064d9010ef3.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b3-0ccf-790c-9353-5eac984d1be2","spots":[{"name":"S1","image":"images/01a034b3-17a0-7341-805e-5b908dddc1e6.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-1c9b-7af0-b718-f6e4ea1a2cb8",
  "sortOrder": 3,
  "title": "A路线017-3",
  "cityName": "大使过滤城017",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b3-0f82-777a-b394-ec6c6df95262.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-0f82-777a-b394-ec6c6df95262.png?Expires=1787592348&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Mn5CBMwU%2FybAd8MxUfWvkh%2F%2FVLA%3D"
  },
  "images": [
    {
      "id": "bound/01a034b3-11af-7a15-85d8-d064d9010ef3.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-11af-7a15-85d8-d064d9010ef3.png?Expires=1787592348&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lB8vTr1FGhHcKlUtq6LWYCDk2jM%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "大使A017",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b3-17a0-7341-805e-5b908dddc1e6.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-17a0-7341-805e-5b908dddc1e6.png?Expires=1787592348&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Fkkga%2FJDk6l%2BnztvLLG7fcCCPks%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:55:48.635626547Z",
  "updatedAt": "2026-08-24T16:55:48.635626547Z"
}
```

## Step 1d-1: 大使 A 名下路线（sortOrder=1）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"大使过滤城017","sortOrder":1,"title":"A路线017-1","ambassadorNote":"语","thumbnail":"images/01a034b3-1cae-7a40-b38a-39298e52e3d2.png","images":["images/01a034b3-1ebd-70ff-8eaa-9ca58dae2eff.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b3-0ccf-790c-9353-5eac984d1be2","spots":[{"name":"S1","image":"images/01a034b3-2194-7a10-891a-29ccbee3b955.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-27d2-7929-b69d-dad008d8cf5e",
  "sortOrder": 1,
  "title": "A路线017-1",
  "cityName": "大使过滤城017",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b3-1cae-7a40-b38a-39298e52e3d2.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-1cae-7a40-b38a-39298e52e3d2.png?Expires=1787592351&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qIvPm%2FVGLAsRkXLDWMB61NCMnYE%3D"
  },
  "images": [
    {
      "id": "bound/01a034b3-1ebd-70ff-8eaa-9ca58dae2eff.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-1ebd-70ff-8eaa-9ca58dae2eff.png?Expires=1787592351&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ftStyJkwH7GlGmuBKBDoEWCP0uQ%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "大使A017",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b3-2194-7a10-891a-29ccbee3b955.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-2194-7a10-891a-29ccbee3b955.png?Expires=1787592351&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6B4YCwPYggwbXlj5lmpGjxA%2FfGY%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:55:51.506505022Z",
  "updatedAt": "2026-08-24T16:55:51.506505022Z"
}
```

## Step 1e: 大使 B 名下路线（sortOrder=2）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"大使过滤城017","sortOrder":2,"title":"B路线017-2","ambassadorNote":"语","thumbnail":"images/01a034b3-27e4-7d39-9c37-070944fa4dac.png","images":["images/01a034b3-29dc-74d5-bbc9-307e7ccf8ee7.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b3-0f6f-775b-bff8-e63f3c9d7c8f","spots":[{"name":"S1","image":"images/01a034b3-2bdb-75b0-8873-53d349f89ced.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-3172-7ab5-bc6f-0784b7fcb726",
  "sortOrder": 2,
  "title": "B路线017-2",
  "cityName": "大使过滤城017",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b3-27e4-7d39-9c37-070944fa4dac.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-27e4-7d39-9c37-070944fa4dac.png?Expires=1787592353&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2BASxnifG3RzC%2Fw3x5s9cr2di6U4%3D"
  },
  "images": [
    {
      "id": "bound/01a034b3-29dc-74d5-bbc9-307e7ccf8ee7.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-29dc-74d5-bbc9-307e7ccf8ee7.png?Expires=1787592353&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=pXp%2F4V%2FduNCzBkvjp0icPkrrBRk%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "大使B017",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b3-2bdb-75b0-8873-53d349f89ced.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-2bdb-75b0-8873-53d349f89ced.png?Expires=1787592353&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=oZ%2F5azL%2Bx9RFZtjaL4LbEZ%2F103E%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:55:53.970588503Z",
  "updatedAt": "2026-08-24T16:55:53.970588503Z"
}
```

## Step 2: 按大使 A 过滤

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?ambassadorId=01a034b3-0ccf-790c-9353-5eac984d1be2" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a034b3-27d2-7929-b69d-dad008d8cf5e",
    "title": "A路线017-1",
    "thumbnail": {
      "id": "bound/01a034b3-1cae-7a40-b38a-39298e52e3d2.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-1cae-7a40-b38a-39298e52e3d2.png?Expires=1787592353&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6SDKKl0afMNnSXTm%2Bul7MXMIGq0%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "大使A017",
    "city": {
      "id": "01a034b3-0920-78ad-b522-4f93f447e12e",
      "name": "大使过滤城017"
    }
  },
  {
    "id": "01a034b3-1c9b-7af0-b718-f6e4ea1a2cb8",
    "title": "A路线017-3",
    "thumbnail": {
      "id": "bound/01a034b3-0f82-777a-b394-ec6c6df95262.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-0f82-777a-b394-ec6c6df95262.png?Expires=1787592353&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=q3NQfJZJwR5rk5En309GbYhM%2F0w%3D"
    },
    "sortOrder": 3,
    "ambassadorName": "大使A017",
    "city": {
      "id": "01a034b3-0920-78ad-b522-4f93f447e12e",
      "name": "大使过滤城017"
    }
  }
]
```

## Step 3a: 将大使 A 下线

```bash
curl -s -i -X PUT "http://localhost:8080/api/admin/ambassadors/01a034b3-0ccf-790c-9353-5eac984d1be2/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-0ccf-790c-9353-5eac984d1be2",
  "avatar": {
    "id": "bound/01a034b3-0930-7c0b-8546-88763788bc31.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-0930-7c0b-8546-88763788bc31.png?Expires=1787592354&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=f9owPVj8jUArdnTcR7cYLr0e2Ds%3D"
  },
  "name": "大使A017",
  "tags": [
    "A"
  ],
  "online": false,
  "createdAt": "2026-08-24T16:55:44.591506Z",
  "updatedAt": "2026-08-24T16:55:44.591506Z"
}
```

## Step 3b: 大使 A 下线后再按 A 过滤

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?ambassadorId=01a034b3-0ccf-790c-9353-5eac984d1be2" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[]
```
