# TC-article-IT-011 GET /api/app/article-categories 与 /api/app/articles 均按权重升序 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> token 复用本轮统一登录

## Step 1: 前置 1/4：创建栏目 A（sortOrder=2）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"权重靠后栏目","icon":"images/it011-a-172204.png","sortOrder":2}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4fa0-7dd5-8aab-def6e6a05d1a",
  "name": "权重靠后栏目",
  "icon": {
    "id": "bound/it011-a-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-a-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=P9MbbqWoAYP5xNaM%2BDEBa2xGpOU%3D"
  },
  "sortOrder": 2,
  "createdAt": "2026-08-16T17:22:06.368790331Z",
  "updatedAt": "2026-08-16T17:22:06.368790331Z"
}
```

## Step 2: 前置 2/4：创建栏目 B（sortOrder=1）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"权重靠前栏目","icon":"images/it011-b-172204.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4fc2-77d8-8cc4-3a7d210602bc",
  "name": "权重靠前栏目",
  "icon": {
    "id": "bound/it011-b-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-b-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=IT3vZF2lEPVr%2F7SSTw%2FY9IxY7cs%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-16T17:22:06.402386601Z",
  "updatedAt": "2026-08-16T17:22:06.402386601Z"
}
```

## Step 3: 前置 3/4：B 下创建上线文章（sortOrder=3）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it011-x-172204.png","title":"文章权重3","subtitle":"权重3副标题","contentHtml":"<p>正文</p>","sortOrder":3,"categoryIds":["01a00b98-4fc2-77d8-8cc4-3a7d210602bc"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4fe5-7ffd-8ddb-7efdf855f8e0",
  "image": {
    "id": "bound/it011-x-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-x-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=a0aaF65KvvkAtZ1bp%2FPnzgdghwk%3D"
  },
  "title": "文章权重3",
  "subtitle": "权重3副标题",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 3,
  "categoryIds": [
    "01a00b98-4fc2-77d8-8cc4-3a7d210602bc"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:06.437902816Z",
  "updatedAt": "2026-08-16T17:22:06.437902816Z"
}
```

## Step 4: 前置 4/4：B 下创建上线文章（sortOrder=1）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it011-y-172204.png","title":"文章权重1","subtitle":"权重1副标题","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a00b98-4fc2-77d8-8cc4-3a7d210602bc"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5009-71d8-a0bf-5925e6dc8f63",
  "image": {
    "id": "bound/it011-y-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-y-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2BSmIO%2FjrTr1oBNenwTh0vCrNUFA%3D"
  },
  "title": "文章权重1",
  "subtitle": "权重1副标题",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4fc2-77d8-8cc4-3a7d210602bc"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:06.473010424Z",
  "updatedAt": "2026-08-16T17:22:06.473010424Z"
}
```

## Step 5: GET /api/app/article-categories（X-API-Key）

```bash
curl -s -i "http://localhost:8081/api/app/article-categories" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a00b98-4fc2-77d8-8cc4-3a7d210602bc",
    "name": "权重靠前栏目",
    "icon": {
      "id": "bound/it011-b-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-b-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=IT3vZF2lEPVr%2F7SSTw%2FY9IxY7cs%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a00b98-4fa0-7dd5-8aab-def6e6a05d1a",
    "name": "权重靠后栏目",
    "icon": {
      "id": "bound/it011-a-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-a-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=P9MbbqWoAYP5xNaM%2BDEBa2xGpOU%3D"
    },
    "sortOrder": 2
  },
  {
    "id": "01a00b98-4acc-787f-9be2-c8d843d7ec05",
    "name": "美食攻略",
    "icon": {
      "id": "bound/it003-icon-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it003-icon-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=LOdjSc3I%2F4ghbqiXcipC1Z6aRZo%3D"
    },
    "sortOrder": 5
  },
  {
    "id": "01a00b98-4be1-7d73-8c88-4ffd50cde113",
    "name": "栏目B-004",
    "icon": {
      "id": "bound/it004-b-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004-b-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mmizcFKskUcgo0esozOknJnaM6U%3D"
    },
    "sortOrder": 12
  },
  {
    "id": "01a00b98-4c76-7393-a249-e392b12b12e2",
    "name": "栏目A-005",
    "icon": {
      "id": "bound/it005-a-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-a-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=aImrBKg082WvPexEx71%2FkEHqRe8%3D"
    },
    "sortOrder": 21
  },
  {
    "id": "01a00b98-4c92-70b3-be6d-42e449c9bdfb",
    "name": "栏目B-005",
    "icon": {
      "id": "bound/it005-b-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-b-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=X28a%2FtY9bCk8dPJgbkvZxUFRaAM%3D"
    },
    "sortOrder": 22
  },
  {
    "id": "01a00b98-4d86-7f01-9a93-17502d34993b",
    "name": "栏目A-007",
    "icon": {
      "id": "bound/it007-a-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-a-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=FpL055tjFhguQ0hDCK1Tdm%2BN8IQ%3D"
    },
    "sortOrder": 31
  },
  {
    "id": "01a00b98-4da3-77be-8a25-7118fbe9b7df",
    "name": "栏目B-007",
    "icon": {
      "id": "bound/it007-b-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-b-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=J8e2Ty%2BGfKmnCJlCivzt0rf1M%2B4%3D"
    },
    "sortOrder": 32
  }
]
```

## Step 6: GET /api/app/articles?categoryId={B}（X-API-Key）

```bash
curl -s -i "http://localhost:8081/api/app/articles?categoryId=01a00b98-4fc2-77d8-8cc4-3a7d210602bc" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a00b98-5009-71d8-a0bf-5925e6dc8f63",
    "image": {
      "id": "bound/it011-y-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-y-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2BSmIO%2FjrTr1oBNenwTh0vCrNUFA%3D"
    },
    "title": "文章权重1",
    "subtitle": "权重1副标题"
  },
  {
    "id": "01a00b98-4fe5-7ffd-8ddb-7efdf855f8e0",
    "image": {
      "id": "bound/it011-x-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-x-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=a0aaF65KvvkAtZ1bp%2FPnzgdghwk%3D"
    },
    "title": "文章权重3",
    "subtitle": "权重3副标题"
  }
]
```
