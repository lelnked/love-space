# TC-article-IT-004 DELETE /api/admin/article-categories/{id} 删除栏目不影响文章数据 — 请求/响应存证

执行日期: 2026-08-16（失败修复后复测）｜ admin=http://localhost:21423
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）
> token 复用本轮统一登录

## Step 1: 前置 1/3：POST /api/admin/article-categories 创建栏目 A

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"栏目A-004R-172742","icon":"images/it004r-a-172742.png","sortOrder":21}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b9d-70f6-754b-8cfe-c7f5225c9071",
  "name": "栏目A-004R-172742",
  "icon": {
    "id": "bound/it004r-a-172742.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004r-a-172742.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=a1q6Ser7kMtwscjQH2NLlEz0u%2BY%3D"
  },
  "sortOrder": 21,
  "createdAt": "2026-08-16T17:27:42.557388771Z",
  "updatedAt": "2026-08-16T17:27:42.557388771Z"
}
```

## Step 2: 前置 2/3：POST /api/admin/article-categories 创建栏目 B

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"栏目B-004R-172742","icon":"images/it004r-b-172742.png","sortOrder":22}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b9d-7133-7997-9046-7d373253476e",
  "name": "栏目B-004R-172742",
  "icon": {
    "id": "bound/it004r-b-172742.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004r-b-172742.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NhMrROi63Zq2y3JJ9NAIr%2FXGvPQ%3D"
  },
  "sortOrder": 22,
  "createdAt": "2026-08-16T17:27:42.643511297Z",
  "updatedAt": "2026-08-16T17:27:42.643511297Z"
}
```

## Step 3: 前置 3/3：POST /api/admin/articles 创建同时关联 A、B 的文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it004r-cover-172742.png","title":"删除栏目验证文章R","subtitle":"关联 A、B 双栏目","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a00b9d-70f6-754b-8cfe-c7f5225c9071","01a00b9d-7133-7997-9046-7d373253476e"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b9d-7168-7db8-b8b9-834757b8e32b",
  "image": {
    "id": "bound/it004r-cover-172742.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004r-cover-172742.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=pTxuuY%2FbcudsmfLOCkslO9TB5l4%3D"
  },
  "title": "删除栏目验证文章R",
  "subtitle": "关联 A、B 双栏目",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b9d-70f6-754b-8cfe-c7f5225c9071",
    "01a00b9d-7133-7997-9046-7d373253476e"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:27:42.695157661Z",
  "updatedAt": "2026-08-16T17:27:42.695157661Z"
}
```

## Step 4: DELETE /api/admin/article-categories/{A}

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/article-categories/01a00b9d-70f6-754b-8cfe-c7f5225c9071" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，无 Content-Type）:

```json

```

## Step 5: GET /api/admin/article-categories 确认栏目 A 已不在列表

```bash
curl -s -i "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a00b98-4fc2-77d8-8cc4-3a7d210602bc",
    "name": "权重靠前栏目",
    "icon": {
      "id": "bound/it011-b-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-b-172204.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=IAOXAq18bHZrmy%2BVaDVmr335NR4%3D"
    },
    "sortOrder": 1,
    "createdAt": "2026-08-16T17:22:06.402387Z",
    "updatedAt": "2026-08-16T17:22:06.402387Z"
  },
  {
    "id": "01a00b98-4fa0-7dd5-8aab-def6e6a05d1a",
    "name": "权重靠后栏目",
    "icon": {
      "id": "bound/it011-a-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-a-172204.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=uxoK3TQaTnr%2FgUqCtAUazMshz5w%3D"
    },
    "sortOrder": 2,
    "createdAt": "2026-08-16T17:22:06.36879Z",
    "updatedAt": "2026-08-16T17:22:06.36879Z"
  },
  {
    "id": "01a00b98-4acc-787f-9be2-c8d843d7ec05",
    "name": "美食攻略",
    "icon": {
      "id": "bound/it003-icon-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it003-icon-172204.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=y7MJjNDiBQqoYxIujRrhVZz3MwE%3D"
    },
    "sortOrder": 5,
    "createdAt": "2026-08-16T17:22:05.132459Z",
    "updatedAt": "2026-08-16T17:22:05.317892Z"
  },
  {
    "id": "01a00b98-4be1-7d73-8c88-4ffd50cde113",
    "name": "栏目B-004",
    "icon": {
      "id": "bound/it004-b-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004-b-172204.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=E8Mb%2FNI6%2FtQrFgRccBPGtV34JXA%3D"
    },
    "sortOrder": 12,
    "createdAt": "2026-08-16T17:22:05.409775Z",
    "updatedAt": "2026-08-16T17:22:05.409775Z"
  },
  {
    "id": "01a00b98-4c76-7393-a249-e392b12b12e2",
    "name": "栏目A-005",
    "icon": {
      "id": "bound/it005-a-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-a-172204.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=CJn8Tbt6hT%2BqvDOXNhV4x04%2Br6M%3D"
    },
    "sortOrder": 21,
    "createdAt": "2026-08-16T17:22:05.558157Z",
    "updatedAt": "2026-08-16T17:22:05.558157Z"
  },
  {
    "id": "01a00b9d-7133-7997-9046-7d373253476e",
    "name": "栏目B-004R-172742",
    "icon": {
      "id": "bound/it004r-b-172742.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004r-b-172742.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NhMrROi63Zq2y3JJ9NAIr%2FXGvPQ%3D"
    },
    "sortOrder": 22,
    "createdAt": "2026-08-16T17:27:42.643511Z",
    "updatedAt": "2026-08-16T17:27:42.643511Z"
  },
  {
    "id": "01a00b98-4c92-70b3-be6d-42e449c9bdfb",
    "name": "栏目B-005",
    "icon": {
      "id": "bound/it005-b-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-b-172204.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=fZf%2FFr7WuXDYn%2FGyx%2BYVQvtGE8E%3D"
    },
    "sortOrder": 22,
    "createdAt": "2026-08-16T17:22:05.58596Z",
    "updatedAt": "2026-08-16T17:22:05.58596Z"
  },
  {
    "id": "01a00b98-4d86-7f01-9a93-17502d34993b",
    "name": "栏目A-007",
    "icon": {
      "id": "bound/it007-a-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-a-172204.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2BoNNj01ZDfISoP2M7vUnkbXX43Q%3D"
    },
    "sortOrder": 31,
    "createdAt": "2026-08-16T17:22:05.830862Z",
    "updatedAt": "2026-08-16T17:22:05.830862Z"
  },
  {
    "id": "01a00b98-4da3-77be-8a25-7118fbe9b7df",
    "name": "栏目B-007",
    "icon": {
      "id": "bound/it007-b-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-b-172204.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=fA3jKR3xfoWuK66iZIKQJ1fhJ%2BE%3D"
    },
    "sortOrder": 32,
    "createdAt": "2026-08-16T17:22:05.85942Z",
    "updatedAt": "2026-08-16T17:22:05.85942Z"
  }
]
```

## Step 6: GET /api/admin/articles/{articleId} — 详情 categoryIds 已按仍存在栏目过滤（修复验证点）

```bash
curl -s -i "http://localhost:21423/api/admin/articles/01a00b9d-7168-7db8-b8b9-834757b8e32b" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b9d-7168-7db8-b8b9-834757b8e32b",
  "image": {
    "id": "bound/it004r-cover-172742.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it004r-cover-172742.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=pTxuuY%2FbcudsmfLOCkslO9TB5l4%3D"
  },
  "title": "删除栏目验证文章R",
  "subtitle": "关联 A、B 双栏目",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b9d-7133-7997-9046-7d373253476e"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:27:42.695158Z",
  "updatedAt": "2026-08-16T17:27:42.695158Z"
}
```

## Step 7: 存储层核验 — 文章存储的 categoryIds 不被回写

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris -d love_space -t -A -c "SELECT category_ids FROM loves_article WHERE id='01a00b9d-7168-7db8-b8b9-834757b8e32b'"
```

实际输出（test 库 loves_article.category_ids 原样保留 A、B）:

```json
["01a00b9d-70f6-754b-8cfe-c7f5225c9071", "01a00b9d-7133-7997-9046-7d373253476e"]
```

存储含已删除的 A（01a00b9d-70f6-754b-8cfe-c7f5225c9071）与 B，未回写；而 Step 6 详情仅返回 B——过滤发生在查询端，符合预期。
