# TC-route-IT-008 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"路线城8-162725","englishName":"City1627251148","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-471f-71e1-8fe8-40e9257f81dd",
  "chineseName": "路线城8-162725",
  "englishName": "City1627251148",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:27.391037862Z",
  "updatedAt": "2026-08-16T16:27:27.391037862Z"
}
```

## Step 2: 前置：创建上线大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/54ee7995-5617-4b30-a2ed-986a0a97921f.png","name":"路线大使8-162725","tags":["向导"],"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4745-7350-a38a-fccc6fa1c8f9",
  "avatar": {
    "id": "bound/54ee7995-5617-4b30-a2ed-986a0a97921f.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/54ee7995-5617-4b30-a2ed-986a0a97921f.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=cuLEMg%2BrgBVCoK3tYdXUpNDgeVo%3D"
  },
  "name": "路线大使8-162725",
  "tags": [
    "向导"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:27.429125433Z",
  "updatedAt": "2026-08-16T16:27:27.429125433Z"
}
```

## Step 3: POST /api/admin/routes images 恰好 1 张

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-471f-71e1-8fe8-40e9257f81dd","title":"单图路线","thumbnail":"images/8c4a2c6f-d857-48a6-bb6c-33ff34243473.png","images":["images/b836f7b7-5f46-4612-90ec-baa84349ded1.png"],"ambassadorId":"01a00b66-4745-7350-a38a-fccc6fa1c8f9"}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4774-7a86-aa7a-48010b3561cd",
  "cityId": "01a00b66-471f-71e1-8fe8-40e9257f81dd",
  "sortOrder": 0,
  "title": "单图路线",
  "ambassadorNote": null,
  "thumbnail": {
    "id": "bound/8c4a2c6f-d857-48a6-bb6c-33ff34243473.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/8c4a2c6f-d857-48a6-bb6c-33ff34243473.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=e74TKIZirkQ8uBfovwIRNZupMXI%3D"
  },
  "images": [
    {
      "id": "bound/b836f7b7-5f46-4612-90ec-baa84349ded1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b836f7b7-5f46-4612-90ec-baa84349ded1.png?Expires=1786899447&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ccaqZyPwqV5ay%2BDkcQDu8FtL%2Fl8%3D"
    }
  ],
  "travelTime": null,
  "season": null,
  "travelStatus": null,
  "ambassadorId": "01a00b66-4745-7350-a38a-fccc6fa1c8f9",
  "ambassadorName": "路线大使8-162725",
  "spots": [],
  "createdAt": "2026-08-16T16:27:27.476583198Z",
  "updatedAt": "2026-08-16T16:27:27.476583198Z"
}
```

## Step 4: POST /api/admin/routes images 为空数组（应拒绝）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-471f-71e1-8fe8-40e9257f81dd","title":"空图路线","thumbnail":"images/99213ac0-9471-467b-ad80-6ad0ab63abc6.png","images":[],"ambassadorId":"01a00b66-4745-7350-a38a-fccc6fa1c8f9"}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "路线图片至少 1 张",
  "path": "/api/admin/routes"
}
```

