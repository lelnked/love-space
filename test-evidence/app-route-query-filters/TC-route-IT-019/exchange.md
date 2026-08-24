# TC-route-IT-019 GET /api/app/routes?cityName= 城市不存在返回空数组 — 请求/响应存证

执行日期: 2026-08-24 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
app 端请求头 `X-API-Key: $APP_API_KEY`。shell 中 `export APP_API_KEY=<app API key>` 后下列 curl 可原样执行。

## Step 1a: 前置确认系统内至少有一条可见路线（不带参数）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a034b2-bb70-77f9-8d97-52853a277766",
    "title": "未上线路线015",
    "thumbnail": {
      "id": "bound/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-aa24-7bc7-ae3f-455451b7cf0b.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=q2J42WHKf8Pejbn5aeGTYBpsn2E%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "未上线大使015",
    "city": {
      "id": "01a034b2-a694-7859-8a25-e5aaf2e0cb5e",
      "name": "未上线城"
    }
  },
  {
    "id": "01a034b1-42db-7dfa-9746-c1e9d0f616ba",
    "title": "全量路线016-乙",
    "thumbnail": {
      "id": "bound/01a034b1-2e29-7726-a5f1-ac0608bb3f43.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-2e29-7726-a5f1-ac0608bb3f43.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=xdKpr%2BGharDlmaaiKvlY2auVMOQ%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "全量大使016",
    "city": {
      "id": "01a034b1-396e-7503-bce9-1f8a4a863051",
      "name": "全量城乙016"
    }
  },
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
  },
  {
    "id": "01a034b1-98ce-75aa-b23b-bdf7929f5227",
    "title": "排序路线012-1",
    "thumbnail": {
      "id": "bound/01a034b1-90a0-7def-b3ed-931fb04f0e24.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-90a0-7def-b3ed-931fb04f0e24.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=HlweJ2QVjBrhbE3u6VSj9LylMBk%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "路线大使012",
    "city": {
      "id": "01a034b1-8286-7501-a465-a8252a3228d4",
      "name": "排序城012"
    }
  },
  {
    "id": "01a034b2-4a1e-7951-a9ac-69b149cbddde",
    "title": "详情路线014",
    "thumbnail": {
      "id": "bound/01a034b2-38c1-7101-b080-ed30a4430245.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b2-38c1-7101-b080-ed30a4430245.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZpV%2BmLChafLzmSnJqzjV0HGwrHU%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "详情大使014",
    "city": {
      "id": "01a034b2-3560-7463-b1b2-94bcdd89b627",
      "name": "详情城014"
    }
  },
  {
    "id": "01a034b1-40c0-7571-81a2-e7d75ac807a6",
    "title": "全量路线016-甲",
    "thumbnail": {
      "id": "bound/01a034b1-2747-7566-ac8e-2b3e74a51418.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-2747-7566-ac8e-2b3e74a51418.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZjdKE3L0TvuxBxn1sQIw59c8y8g%3D"
    },
    "sortOrder": 2,
    "ambassadorName": "全量大使016",
    "city": {
      "id": "01a034b1-395a-7f77-92b9-3f4c5ca46d9a",
      "name": "全量城甲016"
    }
  },
  {
    "id": "01a034b3-3172-7ab5-bc6f-0784b7fcb726",
    "title": "B路线017-2",
    "thumbnail": {
      "id": "bound/01a034b3-27e4-7d39-9c37-070944fa4dac.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-27e4-7d39-9c37-070944fa4dac.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=GQn2jfHiHTDn%2F898HIEqEvbs2GA%3D"
    },
    "sortOrder": 2,
    "ambassadorName": "大使B017",
    "city": {
      "id": "01a034b3-0920-78ad-b522-4f93f447e12e",
      "name": "大使过滤城017"
    }
  },
  {
    "id": "01a034b3-ad35-793c-b0e3-9a5a812a4b54",
    "title": "乙A路线018",
    "thumbnail": {
      "id": "bound/01a034b3-a48a-7fb5-b9ca-da2d2493f393.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-a48a-7fb5-b9ca-da2d2493f393.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=I4qFDVtEvh4V5adNxh%2BHZDJytxM%3D"
    },
    "sortOrder": 2,
    "ambassadorName": "组合大使A018",
    "city": {
      "id": "01a034b3-9310-702f-a80f-62cd15cedf99",
      "name": "组合城乙018"
    }
  },
  {
    "id": "01a034b3-b4a8-70a2-9fba-222519403bd5",
    "title": "甲B路线018",
    "thumbnail": {
      "id": "bound/01a034b3-ad48-70aa-8d21-0d892195066c.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b3-ad48-70aa-8d21-0d892195066c.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=1K0uWezzayC7%2BgvoBYx7SpudBNI%3D"
    },
    "sortOrder": 3,
    "ambassadorName": "组合大使B018",
    "city": {
      "id": "01a034b3-9300-7b06-8267-f9b322dfcf44",
      "name": "组合城甲018"
    }
  },
  {
    "id": "01a034b1-a80e-77f1-b582-7d253aa701e1",
    "title": "排序路线012-3",
    "thumbnail": {
      "id": "bound/01a034b1-98e1-79d3-bb5c-5ceb40ef77f4.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-98e1-79d3-bb5c-5ceb40ef77f4.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Ei2APoY3UVUbRiIFLJrLSv9e%2Fdk%3D"
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
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a034b1-85e3-7fb6-b2bf-bb3ea285f922.png?Expires=1787592387&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7lHieAYwIm%2Fj3nu%2By%2FsfqQpqMeQ%3D"
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

## Step 2: 用不存在的城市名「不存在城」查询

```bash
curl -s -i -X GET "http://localhost:8081/api/app/routes?cityName=%E4%B8%8D%E5%AD%98%E5%9C%A8%E5%9F%8E" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[]
```

## 补充：admin 侧确认「不存在城」确无该城市记录

```bash
curl -s -i -X GET "http://localhost:8080/api/admin/cities?name=%E4%B8%8D%E5%AD%98%E5%9C%A8%E5%9F%8E" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）: `[]`（长度 0）
