# TC-route-IT-012 请求/响应存证

用例: GET /api/app/routes?cityName= 按城市名查路线列表并按 sortOrder 升序
执行日期: 2026-08-26 ｜ change: app-list-sort-tiebreak ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 `X-API-Key: test-api-key`
图片 objectKey 用 test profile 的 Stub 校验器接受的固定 key。

> 回归重测：验证 tie-break 由 ASC 翻为 DESC 后，sortOrder 主序未被带偏。


## Step 1: admin 登录取 JWT

```bash
curl -s -X POST "http://localhost:21423/api/admin/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP/1.1 200）:

```json
{"token":"$TOKEN", ...}
```

## Step 2: 创建上线大使

```bash
curl -s -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/aaf89b6f-436c-477e-ac82-fde385411f4a.png","name":"排序大使021653","tags":["古着"],"online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdd-5798-77c2-81ca-672d360f8da7","name":"排序大使021653","online":true}  // 节选
```

## Step 3: 创建上架城市

```bash
curl -s -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"路线升序城021653","englishName":"RtAsc021653","chineseProvince":"测试省","englishProvince":"TP","online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03bdd-57aa-73ba-b79c-0eb81d528129","chineseName":"路线升序城021653","online":true}  // 节选
```

## Step 4: 依次创建 sortOrder 为 5、1、3 的三条路线

```bash
curl -s -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityName":"路线升序城021653","sortOrder":5,"title":"路线5-021653","thumbnail":"images/8b23da6f-63c7-4260-87e1-3a549b3216f4.png","images":["images/38f13c69-fe1a-4a61-90aa-c20b01995d2f.png"],"ambassadorId":"01a03bdd-5798-77c2-81ca-672d360f8da7"}'
curl -s -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityName":"路线升序城021653","sortOrder":1,"title":"路线1-021653","thumbnail":"images/8b23da6f-63c7-4260-87e1-3a549b3216f4.png","images":["images/38f13c69-fe1a-4a61-90aa-c20b01995d2f.png"],"ambassadorId":"01a03bdd-5798-77c2-81ca-672d360f8da7"}'
curl -s -X POST "http://localhost:21423/api/admin/routes" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityName":"路线升序城021653","sortOrder":3,"title":"路线3-021653","thumbnail":"images/8b23da6f-63c7-4260-87e1-3a549b3216f4.png","images":["images/38f13c69-fe1a-4a61-90aa-c20b01995d2f.png"],"ambassadorId":"01a03bdd-5798-77c2-81ca-672d360f8da7"}'
```

实际响应（HTTP/1.1 200）:

```json
三次均 HTTP/1.1 200，分别返回 sortOrder=5 / 1 / 3 的路线，均关联该大使
```

## Step 5: app 端按城市名查询路线

```bash
curl -s -i -H "X-API-Key: test-api-key" --get --data-urlencode "cityName=路线升序城021653" "http://localhost:8081/api/app/routes"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

[
  {
    "id": "01a03bdd-a0b4-7ae9-8403-826e82a85eda",
    "title": "路线1-021653",
    "thumbnail": {
      "id": "bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png",
      "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png?Expires=1787712575&OSSAccessKeyId=placeholder&Signature=xvuJlaeRyLJjr1zRRA%2BSimXSFFE%3D"
    },
    "sortOrder": 1,
    "ambassadorName": "排序大使021653",
    "city": {
      "id": "01a03bdd-57aa-73ba-b79c-0eb81d528129",
      "name": "路线升序城021653"
    }
  },
  {
    "id": "01a03bdd-a0c1-7b6a-8985-eeb72167c0a6",
    "title": "路线3-021653",
    "thumbnail": {
      "id": "bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png",
      "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png?Expires=1787712575&OSSAccessKeyId=placeholder&Signature=xvuJlaeRyLJjr1zRRA%2BSimXSFFE%3D"
    },
    "sortOrder": 3,
    "ambassadorName": "排序大使021653",
    "city": {
      "id": "01a03bdd-57aa-73ba-b79c-0eb81d528129",
      "name": "路线升序城021653"
    }
  },
  {
    "id": "01a03bdd-a08e-7d47-bb08-f5a28fd07243",
    "title": "路线5-021653",
    "thumbnail": {
      "id": "bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png",
      "url": "https://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/8b23da6f-63c7-4260-87e1-3a549b3216f4.png?Expires=1787712575&OSSAccessKeyId=placeholder&Signature=xvuJlaeRyLJjr1zRRA%2BSimXSFFE%3D"
    },
    "sortOrder": 5,
    "ambassadorName": "排序大使021653",
    "city": {
      "id": "01a03bdd-57aa-73ba-b79c-0eb81d528129",
      "name": "路线升序城021653"
    }
  }
]
```
