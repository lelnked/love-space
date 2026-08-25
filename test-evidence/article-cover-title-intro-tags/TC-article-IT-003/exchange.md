# TC-article-IT-003 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## Step 1: 前置 POST 创建栏目「行程攻略」sortOrder=1

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"name\":\"IT003行程攻略\",\"icon\":\"images/it003-icon-old.png\",\"sortOrder\":1}"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383a-d5ab-7119-9a32-e0c5e7c4f6a1",
  "name": "IT003行程攻略",
  "icon": {
    "id": "bound/it003-icon-old.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it003-icon-old.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qD%2BH7eQA2GcSeufE1enOyImSRbo%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:22:55.019003702Z",
  "updatedAt": "2026-08-25T09:22:55.019003702Z"
}
```

## Step 2: PUT /api/admin/article-categories/{id} 改名/换图/权重=5

```bash
curl -s -i -X PUT "http://localhost:8080/api/admin/article-categories/01a0383a-d5ab-7119-9a32-e0c5e7c4f6a1" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT003美食攻略","icon":"images/it003-icon-new.png","sortOrder":5}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383a-d5ab-7119-9a32-e0c5e7c4f6a1",
  "name": "IT003美食攻略",
  "icon": {
    "id": "bound/it003-icon-new.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it003-icon-new.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=yOCL3FhdAJsW3XF6%2FrrqMTr3h1U%3D"
  },
  "sortOrder": 5,
  "createdAt": "2026-08-25T09:22:55.019004Z",
  "updatedAt": "2026-08-25T09:22:55.019004Z"
}
```

## Step 3: GET /api/admin/article-categories

```bash
curl -s -i "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
[
  {
    "id": "01a0383a-d51a-7b64-bc09-95fe39882697",
    "name": "行程攻略",
    "icon": {
      "id": "bound/it001-icon.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it001-icon.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=LrbSB%2BhnWKxp3W8MfDWUiwvKOSU%3D"
    },
    "sortOrder": 1,
    "createdAt": "2026-08-25T09:22:54.874649Z",
    "updatedAt": "2026-08-25T09:22:54.874649Z"
  },
  {
    "id": "01a03839-e266-7cd8-bf57-e3b11d986c45",
    "name": "IT015栏目",
    "icon": {
      "id": "bound/it015-icon.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it015-icon.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2FdBMRlJ4Fsmk8zxqTjyGTkrzUac%3D"
    },
    "sortOrder": 1,
    "createdAt": "2026-08-25T09:21:52.729297Z",
    "updatedAt": "2026-08-25T09:21:52.729297Z"
  },
  {
    "id": "01a0383a-d5ab-7119-9a32-e0c5e7c4f6a1",
    "name": "IT003美食攻略",
    "icon": {
      "id": "bound/it003-icon-new.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it003-icon-new.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=yOCL3FhdAJsW3XF6%2FrrqMTr3h1U%3D"
    },
    "sortOrder": 5,
    "createdAt": "2026-08-25T09:22:55.019004Z",
    "updatedAt": "2026-08-25T09:22:55.048247Z"
  }
]
```
