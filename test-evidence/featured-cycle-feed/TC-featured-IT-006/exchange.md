# TC-featured-IT-006 GET /api/app/featured-items 信息流仅含上线条目且按创建时间倒序 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 前置：创建上线条目 1（先建）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId": "01a01f6d-0ee0-7af5-aec6-7618ae5f783d", "banner": "images/60437924-3a05-4fb9-84f5-5e679e0de8c2.png", "description": "信息流条目一", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0ee6-74a0-971b-e48eeb563531",
  "cityId": "01a01f6d-0ee0-7af5-aec6-7618ae5f783d",
  "banner": {
    "id": "bound/60437924-3a05-4fb9-84f5-5e679e0de8c2.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/60437924-3a05-4fb9-84f5-5e679e0de8c2.png?Expires=1787235436&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=0cifU%2B1W%2FOlNVFND6qKARNjIVjE%3D"
  },
  "description": "信息流条目一",
  "online": true,
  "createdAt": "2026-08-20T13:47:16.070212245Z",
  "updatedAt": "2026-08-20T13:47:16.070212245Z"
}
```

## Step 2: 前置：创建上线条目 2（后建）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId": "01a01f6d-0ee0-7af5-aec6-7618ae5f783d", "banner": "images/341f65e8-b384-4371-851e-b9ee9423d3c2.png", "description": "信息流条目二", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0eeb-76b3-a65d-2dd092c6f232",
  "cityId": "01a01f6d-0ee0-7af5-aec6-7618ae5f783d",
  "banner": {
    "id": "bound/341f65e8-b384-4371-851e-b9ee9423d3c2.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/341f65e8-b384-4371-851e-b9ee9423d3c2.png?Expires=1787235436&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=SUPwI97yS%2FFa2Iz9CPW2AHXxd1I%3D"
  },
  "description": "信息流条目二",
  "online": true,
  "createdAt": "2026-08-20T13:47:16.075389466Z",
  "updatedAt": "2026-08-20T13:47:16.075389466Z"
}
```

## Step 3: 前置：创建下线条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId": "01a01f6d-0ee0-7af5-aec6-7618ae5f783d", "banner": "images/ea9278d6-6d2e-43f2-aa85-4f9bb845cb88.png", "description": "下线条目", "online": false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0ef0-7b58-a485-b2ab8ec494b5",
  "cityId": "01a01f6d-0ee0-7af5-aec6-7618ae5f783d",
  "banner": {
    "id": "bound/ea9278d6-6d2e-43f2-aa85-4f9bb845cb88.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/ea9278d6-6d2e-43f2-aa85-4f9bb845cb88.png?Expires=1787235436&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Wr3SiWOdK1nqoxcHSyXSA2gEf0s%3D"
  },
  "description": "下线条目",
  "online": false,
  "createdAt": "2026-08-20T13:47:16.08067597Z",
  "updatedAt": "2026-08-20T13:47:16.08067597Z"
}
```

## Step 4: GET /api/app/featured-items（X-API-Key）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a01f6d-0eeb-76b3-a65d-2dd092c6f232",
    "banner": {
      "id": "bound/341f65e8-b384-4371-851e-b9ee9423d3c2.png",
      "url": "https://love-space-test.oss-test.example.com/bound/341f65e8-b384-4371-851e-b9ee9423d3c2.png?Expires=1787235436&OSSAccessKeyId=test-oss-ak&Signature=fHth5sKcWLqTmYuqvcRdln64S%2F0%3D"
    },
    "description": "信息流条目二",
    "city": {
      "id": "01a01f6d-0ee0-7af5-aec6-7618ae5f783d",
      "name": "精选城R006"
    }
  },
  {
    "id": "01a01f6d-0ee6-74a0-971b-e48eeb563531",
    "banner": {
      "id": "bound/60437924-3a05-4fb9-84f5-5e679e0de8c2.png",
      "url": "https://love-space-test.oss-test.example.com/bound/60437924-3a05-4fb9-84f5-5e679e0de8c2.png?Expires=1787235436&OSSAccessKeyId=test-oss-ak&Signature=jsj7NZm8t7wH%2BcXuqUDXoq4ETTc%3D"
    },
    "description": "信息流条目一",
    "city": {
      "id": "01a01f6d-0ee0-7af5-aec6-7618ae5f783d",
      "name": "精选城R006"
    }
  }
]
```

