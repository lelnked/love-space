# TC-recommend-list-IT-011 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

> 前置：上架城市 D=01a00b34-ae04-7c00-ae5f-1188c71b301a 下 3 个清单 sortOrder 5/1/3（TC-recommend-list-IT-006 创建）

## Step 1: GET /api/app/recommend-lists?cityId=（X-API-Key）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists?cityId=01a00b34-ae04-7c00-ae5f-1188c71b301a" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

[
  {
    "id": "01a00b34-ae5d-7dee-9d98-753f586ef0d1",
    "title": "湖畔精选清单",
    "introduction": null,
    "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
    "sortOrder": 1
  },
  {
    "id": "01a00b34-ae96-7d1a-b4ad-0304dbabd417",
    "title": "清单三",
    "introduction": null,
    "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
    "sortOrder": 3
  },
  {
    "id": "01a00b34-ae2c-75b4-8b6b-fd272f9b5cc3",
    "title": "清单五",
    "introduction": null,
    "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
    "sortOrder": 5
  }
]
```
