# TC-route-IT-018 GET /api/app/routes?cityName=&ambassadorId= 组合过滤取交集 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
app 端请求头 `X-API-Key: $APP_API_KEY`。shell 中 `export TOKEN=<登录返回 token>`、`export APP_API_KEY=<app API key>` 后下列 curl 可原样执行。

## Step 1a: 创建城市甲

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"组合城甲018","englishName":"MixCityA018","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-9300-7b06-8267-f9b322dfcf44",
  "chineseName": "组合城甲018",
  "englishName": "MixCityA018",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-24T16:56:18.944639224Z",
  "updatedAt": "2026-08-24T16:56:18.944639224Z"
}
```

## Step 1b: 创建城市乙

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"组合城乙018","englishName":"MixCityB018","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-9310-702f-a80f-62cd15cedf99",
  "chineseName": "组合城乙018",
  "englishName": "MixCityB018",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-24T16:56:18.959960496Z",
  "updatedAt": "2026-08-24T16:56:18.959960496Z"
}
```

## Step 1c: 创建 online 大使 A

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/01a034b3-931e-7b20-ae89-337b136e7ca5.png","name":"组合大使A018","tags":["A"],"online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-968b-748e-882b-9326250fae58",
  "avatar": {
    "id": "bound/01a034b3-931e-7b20-ae89-337b136e7ca5.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-931e-7b20-ae89-337b136e7ca5.png?Expires=1787592379&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=uwBzAmMDwSq8u%2FExjlrI9Q%2BOi3w%3D"
  },
  "name": "组合大使A018",
  "tags": [
    "A"
  ],
  "online": true,
  "createdAt": "2026-08-24T16:56:19.851227879Z",
  "updatedAt": "2026-08-24T16:56:19.851227879Z"
}
```

## Step 1d: 创建 online 大使 B

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"avatar":"images/01a034b3-969e-7e20-bc2a-ce46f09d7675.png","name":"组合大使B018","tags":["B"],"online":true}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-98f1-7c77-9f7f-fd596314d181",
  "avatar": {
    "id": "bound/01a034b3-969e-7e20-bc2a-ce46f09d7675.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-969e-7e20-bc2a-ce46f09d7675.png?Expires=1787592380&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=y9gztmwfVIt7kX%2F4XnCv9wfbKxY%3D"
  },
  "name": "组合大使B018",
  "tags": [
    "B"
  ],
  "online": true,
  "createdAt": "2026-08-24T16:56:20.46572222Z",
  "updatedAt": "2026-08-24T16:56:20.46572222Z"
}
```

## Step 1e: 城市甲 + 大使 A 的路线

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"组合城甲018","sortOrder":1,"title":"甲A路线018","ambassadorNote":"语","thumbnail":"images/01a034b3-9904-7ad5-adbb-44155e53b906.png","images":["images/01a034b3-9c82-7793-8e5e-66c982a89bd9.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b3-968b-748e-882b-9326250fae58","spots":[{"name":"S1","image":"images/01a034b3-9e8a-7ea0-b144-4e579ad0aee7.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-a479-7074-bce4-4a504e5592e5",
  "sortOrder": 1,
  "title": "甲A路线018",
  "cityName": "组合城甲018",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b3-9904-7ad5-adbb-44155e53b906.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-9904-7ad5-adbb-44155e53b906.png?Expires=1787592383&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2FqZkKllLvyR031sZ633NcM8XC5U%3D"
  },
  "images": [
    {
      "id": "bound/01a034b3-9c82-7793-8e5e-66c982a89bd9.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-9c82-7793-8e5e-66c982a89bd9.png?Expires=1787592383&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Gg7Pw%2By5p%2FMcEXkDQcSQuHB%2Fs0E%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "组合大使A018",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b3-9e8a-7ea0-b144-4e579ad0aee7.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-9e8a-7ea0-b144-4e579ad0aee7.png?Expires=1787592383&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=rgu1QNeIcj8blC0%2Bs0kPgO%2Blosk%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:56:23.416955592Z",
  "updatedAt": "2026-08-24T16:56:23.416955592Z"
}
```

## Step 1f: 城市乙 + 大使 A 的路线

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"组合城乙018","sortOrder":2,"title":"乙A路线018","ambassadorNote":"语","thumbnail":"images/01a034b3-a48a-7fb5-b9ca-da2d2493f393.png","images":["images/01a034b3-a794-7833-b569-1e1b00914b0a.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b3-968b-748e-882b-9326250fae58","spots":[{"name":"S1","image":"images/01a034b3-a96f-7e12-847f-d01076d09477.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-ad35-793c-b0e3-9a5a812a4b54",
  "sortOrder": 2,
  "title": "乙A路线018",
  "cityName": "组合城乙018",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b3-a48a-7fb5-b9ca-da2d2493f393.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-a48a-7fb5-b9ca-da2d2493f393.png?Expires=1787592385&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=2dBfZC2dqQIR4GG48a3JJ0ANjgA%3D"
  },
  "images": [
    {
      "id": "bound/01a034b3-a794-7833-b569-1e1b00914b0a.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-a794-7833-b569-1e1b00914b0a.png?Expires=1787592385&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=wo%2FAeZciHZk44B9GAipYYI%2Fzs4g%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "组合大使A018",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b3-a96f-7e12-847f-d01076d09477.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-a96f-7e12-847f-d01076d09477.png?Expires=1787592385&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=9kiIyg9rHFg3sNfqlxDg%2FErG6tA%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:56:25.653521066Z",
  "updatedAt": "2026-08-24T16:56:25.653521066Z"
}
```

## Step 1g: 城市甲 + 大使 B 的路线

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityName":"组合城甲018","sortOrder":3,"title":"甲B路线018","ambassadorNote":"语","thumbnail":"images/01a034b3-ad48-70aa-8d21-0d892195066c.png","images":["images/01a034b3-af2b-7820-9837-494510a23d4d.png"],"travelTime":"1 天","season":"春秋","travelStatus":"轻松","ambassadorId":"01a034b3-98f1-7c77-9f7f-fd596314d181","spots":[{"name":"S1","image":"images/01a034b3-b0f7-7b22-82e4-9d8e43bae70e.png","introduction":"i1"}]}'
```

实际响应（HTTP 200）:

```json
{
  "id": "01a034b3-b4a8-70a2-9fba-222519403bd5",
  "sortOrder": 3,
  "title": "甲B路线018",
  "cityName": "组合城甲018",
  "ambassadorNote": "语",
  "thumbnail": {
    "id": "bound/01a034b3-ad48-70aa-8d21-0d892195066c.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-ad48-70aa-8d21-0d892195066c.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=1K0uWezzayC7%2BgvoBYx7SpudBNI%3D"
  },
  "images": [
    {
      "id": "bound/01a034b3-af2b-7820-9837-494510a23d4d.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-af2b-7820-9837-494510a23d4d.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=57RlkJMFeXcLJd2%2B%2FK2VkxjMTJc%3D"
    }
  ],
  "travelTime": "1 天",
  "season": "春秋",
  "travelStatus": "轻松",
  "ambassadorName": "组合大使B018",
  "spots": [
    {
      "name": "S1",
      "image": {
        "id": "bound/01a034b3-b0f7-7b22-82e4-9d8e43bae70e.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-b0f7-7b22-82e4-9d8e43bae70e.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=hoKtUTeBNvtB3BggSIyTogQqViU%3D"
      },
      "introduction": "i1"
    }
  ],
  "createdAt": "2026-08-24T16:56:27.559960277Z",
  "updatedAt": "2026-08-24T16:56:27.559960277Z"
}
```

## Step 2: 城市甲 + 大使 A 组合过滤

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityName=%E7%BB%84%E5%90%88%E5%9F%8E%E7%94%B2018&ambassadorId=01a034b3-968b-748e-882b-9326250fae58" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a034b3-a479-7074-bce4-4a504e5592e5",
    "title": "甲A路线018",
    "thumbnail": {
      "id": "bound/01a034b3-9904-7ad5-adbb-44155e53b906.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-9904-7ad5-adbb-44155e53b906.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=SFDVsiR86r0B30L0DJ1utBrc58M%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "组合大使A018",
    "city": {
      "id": "01a034b3-9300-7b06-8267-f9b322dfcf44",
      "name": "组合城甲018"
    }
  }
]
```
