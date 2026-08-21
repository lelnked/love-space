# TC-featured-IT-013 GET /api/admin/featured-cycle-items/page 按周期过滤并按排序号升序 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 前置：FOLLICULAR 建 sortOrder=2 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "FOLLICULAR", "type": "ACTIVITY", "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa", "description": "卵泡期2", "banner": "images/cd044f39-1932-4e49-86b4-ea105133a226.png", "sortOrder": 2}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6a-16cd-79db-a130-dc2bbbb576a0",
  "phase": "FOLLICULAR",
  "type": "ACTIVITY",
  "sortOrder": 2,
  "online": false,
  "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T013",
  "title": null,
  "subtitle": null,
  "description": "卵泡期2",
  "note": null,
  "banner": {
    "id": "bound/cd044f39-1932-4e49-86b4-ea105133a226.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/cd044f39-1932-4e49-86b4-ea105133a226.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7Q1DCswOeYsqXXXSZu4GpW3ADLg%3D"
  },
  "createdAt": "2026-08-20T13:44:01.485535557Z",
  "updatedAt": "2026-08-20T13:44:01.485535557Z"
}
```

## Step 2: 前置：FOLLICULAR 建 sortOrder=1 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "FOLLICULAR", "type": "ACTIVITY", "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa", "description": "卵泡期1", "banner": "images/3ee85fa5-7dc5-448f-8b07-a4ef111f2a64.png", "sortOrder": 1}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6a-16d6-7df4-bb61-08e413b98f0c",
  "phase": "FOLLICULAR",
  "type": "ACTIVITY",
  "sortOrder": 1,
  "online": false,
  "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T013",
  "title": null,
  "subtitle": null,
  "description": "卵泡期1",
  "note": null,
  "banner": {
    "id": "bound/3ee85fa5-7dc5-448f-8b07-a4ef111f2a64.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3ee85fa5-7dc5-448f-8b07-a4ef111f2a64.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zBmJzZ1ACBDNc6Wd5rYH1no2cTs%3D"
  },
  "createdAt": "2026-08-20T13:44:01.494812006Z",
  "updatedAt": "2026-08-20T13:44:01.494812006Z"
}
```

## Step 3: 前置：FOLLICULAR 建 sortOrder=3 条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "FOLLICULAR", "type": "ACTIVITY", "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa", "description": "卵泡期3", "banner": "images/77d3652a-07f9-422f-8618-227d5be13152.png", "sortOrder": 3}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6a-16df-7e00-88e8-75393ec0df18",
  "phase": "FOLLICULAR",
  "type": "ACTIVITY",
  "sortOrder": 3,
  "online": false,
  "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T013",
  "title": null,
  "subtitle": null,
  "description": "卵泡期3",
  "note": null,
  "banner": {
    "id": "bound/77d3652a-07f9-422f-8618-227d5be13152.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/77d3652a-07f9-422f-8618-227d5be13152.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mNFBVzdezS3vCAL6Ku8QJ9aG0U0%3D"
  },
  "createdAt": "2026-08-20T13:44:01.503814915Z",
  "updatedAt": "2026-08-20T13:44:01.503814915Z"
}
```

## Step 4: 前置：MENSTRUAL 建 1 个条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa", "description": "经期一条", "banner": "images/48c88960-f298-4eb4-95c7-c8ec26a0c913.png", "sortOrder": 9}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6a-16e9-757c-8c2e-f23c4fb2a8e4",
  "phase": "MENSTRUAL",
  "type": "ACTIVITY",
  "sortOrder": 9,
  "online": false,
  "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa",
  "routeId": null,
  "articleId": null,
  "relatedTitle": "活动T013",
  "title": null,
  "subtitle": null,
  "description": "经期一条",
  "note": null,
  "banner": {
    "id": "bound/48c88960-f298-4eb4-95c7-c8ec26a0c913.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/48c88960-f298-4eb4-95c7-c8ec26a0c913.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=3ECJmUoeubJAmj6zCWfSmVWxHXM%3D"
  },
  "createdAt": "2026-08-20T13:44:01.513281531Z",
  "updatedAt": "2026-08-20T13:44:01.513281531Z"
}
```

## Step 5: GET page?phase=FOLLICULAR

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?phase=FOLLICULAR&size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "content": [
    {
      "id": "01a01f6a-16d6-7df4-bb61-08e413b98f0c",
      "phase": "FOLLICULAR",
      "type": "ACTIVITY",
      "sortOrder": 1,
      "online": false,
      "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa",
      "routeId": null,
      "articleId": null,
      "relatedTitle": "活动T013",
      "title": null,
      "subtitle": null,
      "description": "卵泡期1",
      "note": null,
      "banner": {
        "id": "bound/3ee85fa5-7dc5-448f-8b07-a4ef111f2a64.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3ee85fa5-7dc5-448f-8b07-a4ef111f2a64.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zBmJzZ1ACBDNc6Wd5rYH1no2cTs%3D"
      },
      "createdAt": "2026-08-20T13:44:01.494812Z",
      "updatedAt": "2026-08-20T13:44:01.494812Z"
    },
    {
      "id": "01a01f6a-16cd-79db-a130-dc2bbbb576a0",
      "phase": "FOLLICULAR",
      "type": "ACTIVITY",
      "sortOrder": 2,
      "online": false,
      "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa",
      "routeId": null,
      "articleId": null,
      "relatedTitle": "活动T013",
      "title": null,
      "subtitle": null,
      "description": "卵泡期2",
      "note": null,
      "banner": {
        "id": "bound/cd044f39-1932-4e49-86b4-ea105133a226.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/cd044f39-1932-4e49-86b4-ea105133a226.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7Q1DCswOeYsqXXXSZu4GpW3ADLg%3D"
      },
      "createdAt": "2026-08-20T13:44:01.485536Z",
      "updatedAt": "2026-08-20T13:44:01.485536Z"
    },
    {
      "id": "01a01f6a-16df-7e00-88e8-75393ec0df18",
      "phase": "FOLLICULAR",
      "type": "ACTIVITY",
      "sortOrder": 3,
      "online": false,
      "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa",
      "routeId": null,
      "articleId": null,
      "relatedTitle": "活动T013",
      "title": null,
      "subtitle": null,
      "description": "卵泡期3",
      "note": null,
      "banner": {
        "id": "bound/77d3652a-07f9-422f-8618-227d5be13152.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/77d3652a-07f9-422f-8618-227d5be13152.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mNFBVzdezS3vCAL6Ku8QJ9aG0U0%3D"
      },
      "createdAt": "2026-08-20T13:44:01.503815Z",
      "updatedAt": "2026-08-20T13:44:01.503815Z"
    }
  ],
  "page": 1,
  "size": 30,
  "totalElements": 3,
  "totalPages": 1
}
```

## Step 6: GET page（不带 phase）

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "content": [
    {
      "id": "01a01f6a-16d6-7df4-bb61-08e413b98f0c",
      "phase": "FOLLICULAR",
      "type": "ACTIVITY",
      "sortOrder": 1,
      "online": false,
      "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa",
      "routeId": null,
      "articleId": null,
      "relatedTitle": "活动T013",
      "title": null,
      "subtitle": null,
      "description": "卵泡期1",
      "note": null,
      "banner": {
        "id": "bound/3ee85fa5-7dc5-448f-8b07-a4ef111f2a64.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/3ee85fa5-7dc5-448f-8b07-a4ef111f2a64.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zBmJzZ1ACBDNc6Wd5rYH1no2cTs%3D"
      },
      "createdAt": "2026-08-20T13:44:01.494812Z",
      "updatedAt": "2026-08-20T13:44:01.494812Z"
    },
    {
      "id": "01a01f6a-16cd-79db-a130-dc2bbbb576a0",
      "phase": "FOLLICULAR",
      "type": "ACTIVITY",
      "sortOrder": 2,
      "online": false,
      "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa",
      "routeId": null,
      "articleId": null,
      "relatedTitle": "活动T013",
      "title": null,
      "subtitle": null,
      "description": "卵泡期2",
      "note": null,
      "banner": {
        "id": "bound/cd044f39-1932-4e49-86b4-ea105133a226.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/cd044f39-1932-4e49-86b4-ea105133a226.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7Q1DCswOeYsqXXXSZu4GpW3ADLg%3D"
      },
      "createdAt": "2026-08-20T13:44:01.485536Z",
      "updatedAt": "2026-08-20T13:44:01.485536Z"
    },
    {
      "id": "01a01f6a-16df-7e00-88e8-75393ec0df18",
      "phase": "FOLLICULAR",
      "type": "ACTIVITY",
      "sortOrder": 3,
      "online": false,
      "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa",
      "routeId": null,
      "articleId": null,
      "relatedTitle": "活动T013",
      "title": null,
      "subtitle": null,
      "description": "卵泡期3",
      "note": null,
      "banner": {
        "id": "bound/77d3652a-07f9-422f-8618-227d5be13152.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/77d3652a-07f9-422f-8618-227d5be13152.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mNFBVzdezS3vCAL6Ku8QJ9aG0U0%3D"
      },
      "createdAt": "2026-08-20T13:44:01.503815Z",
      "updatedAt": "2026-08-20T13:44:01.503815Z"
    },
    {
      "id": "01a01f6a-16e9-757c-8c2e-f23c4fb2a8e4",
      "phase": "MENSTRUAL",
      "type": "ACTIVITY",
      "sortOrder": 9,
      "online": false,
      "activityId": "01a01f6a-16c4-760a-b4e4-84024d1949aa",
      "routeId": null,
      "articleId": null,
      "relatedTitle": "活动T013",
      "title": null,
      "subtitle": null,
      "description": "经期一条",
      "note": null,
      "banner": {
        "id": "bound/48c88960-f298-4eb4-95c7-c8ec26a0c913.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/48c88960-f298-4eb4-95c7-c8ec26a0c913.png?Expires=1787235241&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=3ECJmUoeubJAmj6zCWfSmVWxHXM%3D"
      },
      "createdAt": "2026-08-20T13:44:01.513282Z",
      "updatedAt": "2026-08-20T13:44:01.513282Z"
    }
  ],
  "page": 1,
  "size": 30,
  "totalElements": 4,
  "totalPages": 1
}
```

