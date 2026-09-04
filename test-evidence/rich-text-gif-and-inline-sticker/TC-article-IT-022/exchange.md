# TC-article-IT-022 请求/响应存证

POST /api/admin/articles 富文本内联图超限或类型不符被拒绝

执行日期: 2026-09-04 ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，`export TOKEN=<登录返回 token>` 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key（测试 fixture，明文入存证）

## Step 1: 基线：GET /api/admin/articles/page 记录 totalElements

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/articles/page?page=0&size=1' -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [
    {
      "id": "01a06b3a-17fb-74d3-bbba-473d29c9cbe7",
      "image": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zo6YzmZhKxqEWLtwCZOoHASz3QU%3D"
      },
      "title": "内联小图21-af4f49",
      "coverTitle": null,
      "subtitle": "副标题",
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b34-0262-7574-87b4-259859cf92d1"
      ],
      "online": true,
      "createdAt": "2026-09-04T07:02:44.475246Z",
      "updatedAt": "2026-09-04T07:02:44.511418Z"
    },
    {
      "id": "01a06b3a-17c7-77c7-ba11-a2e2578b0889",
      "image": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zo6YzmZhKxqEWLtwCZOoHASz3QU%3D"
      },
      "title": "app文章14-af4f49",
      "coverTitle": null,
      "subtitle": "副标题",
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b34-0262-7574-87b4-259859cf92d1"
      ],
      "online": true,
      "createdAt": "2026-09-04T07:02:44.423452Z",
      "updatedAt": "2026-09-04T07:02:44.423452Z"
    },
    {
      "id": "01a06b3a-179a-7ab4-9d81-b6498a685d3e",
      "image": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zo6YzmZhKxqEWLtwCZOoHASz3QU%3D"
      },
      "title": "富文本文章10-af4f49",
      "coverTitle": null,
      "subtitle": "副标题",
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b34-0262-7574-87b4-259859cf92d1"
      ],
      "online": true,
      "createdAt": "2026-09-04T07:02:44.377297Z",
      "updatedAt": "2026-09-04T07:02:44.405302Z"
    },
    {
      "id": "01a06b34-03f5-7a44-9530-de8ad56f0114",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:56:06.133617Z",
      "updatedAt": "2026-09-04T06:56:06.133617Z"
    },
    {
      "id": "01a06b34-03d4-7877-8b6a-927045b178f8",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:56:06.100495Z",
      "updatedAt": "2026-09-04T06:56:06.100495Z"
    },
    {
      "id": "01a06b34-0336-77f6-9d1c-d44b00add0ea",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:56:05.942456Z",
      "updatedAt": "2026-09-04T06:56:05.942456Z"
    },
    {
      "id": "01a06b34-02e8-73ea-8307-3507cee74ac7",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "黄体期怎么吃",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:56:05.864199Z",
      "updatedAt": "2026-09-04T06:56:05.864199Z"
    },
    {
      "id": "01a06b34-0264-7f07-9e17-936b5558a74c",
      "image": {
        "id": "bound/images/cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=31Wd%2BY4sHwrYX5B59ju3rohk7yo%3D"
      },
      "title": "只有标题",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [
        "甲",
        "乙"
      ],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b34-0262-7574-87b4-259859cf92d1"
      ],
      "online": true,
      "createdAt": "2026-09-04T06:56:05.732876Z",
      "updatedAt": "2026-09-04T06:56:05.738266Z"
    },
    {
      "id": "01a06b34-025c-7213-80bc-f97b26a5f815",
      "image": {
        "id": "bound/images/cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=31Wd%2BY4sHwrYX5B59ju3rohk7yo%3D"
      },
      "title": "详情页标题",
      "coverTitle": "封面标题",
      "subtitle": "副标题",
      "intro": "这是引言",
      "tags": [
        "约会",
        "周末"
      ],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b34-0258-7891-888d-2c272e0f8feb"
      ],
      "online": true,
      "createdAt": "2026-09-04T06:56:05.7241Z",
      "updatedAt": "2026-09-04T06:56:05.7241Z"
    },
    {
      "id": "01a06b34-0240-776c-b2d9-fff2ec271735",
      "image": {
        "id": "bound/images/a.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/a.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=T7WGc%2BqB3bBHE7kYA1i%2FaFI5aG8%3D"
      },
      "title": "开关文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b34-023d-725a-abe0-09921079de00"
      ],
      "online": true,
      "createdAt": "2026-09-04T06:56:05.696419Z",
      "updatedAt": "2026-09-04T06:56:05.70514Z"
    },
    {
      "id": "01a06b34-0203-74fe-8bdc-e469c44a6b3a",
      "image": {
        "id": "bound/images/a.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/a.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=T7WGc%2BqB3bBHE7kYA1i%2FaFI5aG8%3D"
      },
      "title": "标题",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:56:05.633377Z",
      "updatedAt": "2026-09-04T06:56:05.633377Z"
    },
    {
      "id": "01a06b0a-add6-75b4-b99c-e3f5dc55e9ce",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:10:57.110324Z",
      "updatedAt": "2026-09-04T06:10:57.110324Z"
    },
    {
      "id": "01a06b0a-adae-7627-9a03-60eca57a6b05",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:10:57.07035Z",
      "updatedAt": "2026-09-04T06:10:57.07035Z"
    },
    {
      "id": "01a06b0a-ad0e-7a59-8bc9-43083134c71f",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:10:56.910606Z",
      "updatedAt": "2026-09-04T06:10:56.910606Z"
    },
    {
      "id": "01a06b0a-acbb-7bff-8aac-855683daff8d",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "黄体期怎么吃",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:10:56.827708Z",
      "updatedAt": "2026-09-04T06:10:56.827708Z"
    },
    {
      "id": "01a06b0a-ac50-7004-b52a-50488cbfadf3",
      "image": {
        "id": "bound/images/cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=31Wd%2BY4sHwrYX5B59ju3rohk7yo%3D"
      },
      "title": "只有标题",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [
        "甲",
        "乙"
      ],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b0a-ac4e-711d-91f7-1e05f30cfd1e"
      ],
      "online": true,
      "createdAt": "2026-09-04T06:10:56.719972Z",
      "updatedAt": "2026-09-04T06:10:56.724316Z"
    },
    {
      "id": "01a06b0a-ac49-7800-9b86-70cf54acf1d3",
      "image": {
        "id": "bound/images/cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=31Wd%2BY4sHwrYX5B59ju3rohk7yo%3D"
      },
      "title": "详情页标题",
      "coverTitle": "封面标题",
      "subtitle": "副标题",
      "intro": "这是引言",
      "tags": [
        "约会",
        "周末"
      ],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b0a-ac47-70e1-9f7a-3a5f2dd52ca5"
      ],
      "online": true,
      "createdAt": "2026-09-04T06:10:56.713472Z",
      "updatedAt": "2026-09-04T06:10:56.713472Z"
    },
    {
      "id": "01a06b0a-ac33-7495-a25e-d2a56f68793c",
      "image": {
        "id": "bound/images/a.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/a.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=T7WGc%2BqB3bBHE7kYA1i%2FaFI5aG8%3D"
      },
      "title": "开关文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b0a-ac30-7860-8227-54dff8fd18d2"
      ],
      "online": true,
      "createdAt": "2026-09-04T06:10:56.691251Z",
      "updatedAt": "2026-09-04T06:10:56.698197Z"
    },
    {
      "id": "01a06b0a-abff-74ac-99e4-b1198253fb14",
      "image": {
        "id": "bound/images/a.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/a.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=T7WGc%2BqB3bBHE7kYA1i%2FaFI5aG8%3D"
      },
      "title": "标题",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:10:56.637967Z",
      "updatedAt": "2026-09-04T06:10:56.637967Z"
    },
    {
      "id": "01a0622f-dcd7-7496-a019-762e8efbe4dd",
      "image": {
        "id": "bound/art-m9pf43c20.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-m9pf43c20.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=sBxtdjq5dzpXMapyk9t0ecTbWnQ%3D"
      },
      "title": "art-m9pf43c20",
      "coverTitle": "封面标题036",
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-02T12:54:59.031265Z",
      "updatedAt": "2026-09-02T12:54:59.031265Z"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 144,
  "totalPages": 8
}
```

## Step 2: POST /api/admin/articles contentHtml=<img src=D4K jpeg 4096B>

```bash
curl -s -i -X POST http://localhost:21423/api/admin/articles -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"image": "images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png", "title": "拒绝D4K -af4f49", "subtitle": "副标题", "contentHtml": "<p>x</p><img src=\"data:image/jpeg;base64,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\">", "sortOrder": 0, "categoryIds": ["01a06b34-0262-7574-87b4-259859cf92d1"], "online": true}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "图片对象不可用",
  "path": "/api/admin/articles"
}
```

## Step 3: POST /api/admin/articles contentHtml=<img src=DSVG svg 1024B>

```bash
curl -s -i -X POST http://localhost:21423/api/admin/articles -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"image": "images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png", "title": "拒绝DSVG-af4f49", "subtitle": "副标题", "contentHtml": "<p>x</p><img src=\"data:image/svg+xml;base64,8DCUuT1HdViVUrG05+FwMfbUvaXth7xXCRVQpVt+CIf/aHKA18Yrft8brtSxdcMhC98RPE8fPPTulYkPN0yIM9H4vIGIwdgTvlts9IORaRx61+BWExW1GDeOJ1wFOcTfo1rduw22ucNqKZVV4vY4e/wBSm3D9ar2uu31RcdUz/M6ZYp/iiiQ4nB41PUiHMjejbfTo8/WQzpI4y7Yu0Q7GM0OVU0N1bcAPW8ZIhyAG5zQw2Ch1gxnheYHVzLfvjW6qcXNd90dBYSNcBWnSewczwlr11zy7zUn3ExGTSGD+ofIaiVPPlDw06OVZ+fhJAI7b/hRwAmfmpP3cpQ30CxhSvkQUNQsqXbV8nBpj7k8dIbKcssgr8aGdWJlbIBZ3Ku4Nf+cOBbg4D9ROJH9RJ8qAINtOCkvOLsBXPqhtoXiaZOoSOlhshd/zl9hrP5XPssVml2hDNQgoRRKN1DyJ0PZua0TnWrKGSrm/J8lIcSCdDoq9zLWivI6o5RmA8YH8Me1ZyhmBO9EH4OkT0UgamFhFVy664Tv+cHnWHiVKMTq3FX0HrFL/0HMANYmC7s4030pP/1b6VLOUqNuKfAx2fpJUYpbbtG5X8/HCsL0nEZQnRKkHH1YhoHEgOBqKyqDLCHmDt6EFTyBgCdZrcdB2q3zcMMPhrJlVcpjJUgu4JAtI0t8NF6TK9tRsY3cyctx7xr+PgedOXGxQmFJEV8J8tWKgOilK8GO2CAUYXoSlNRw8fuzW2Yl1K3Ha1OPkOWt7GGKUppapYPngp8d/3olAK9JHRvmA1o/N2yoifRzhcWwKOGXxcZDypD87YBp8kamAM28MlhlR3dj8070ZrANtq/tYeXREcHT5Qmy1RByUGZxvkdKXqz2W4s02wAcOf281A9X1fUFChm2KC87bPVveyI49eafLhSFG6XM0QWuBjY/WZelxTCO1ORS1Js+Zd3Ijrj4pBrjB5kneBaTkpRu3usjjACWW65cHuC3qCbmv7to62XeWFEJthN6LG3zJcwnLQsQyGvlz0UG5+RTnGe6U58VaN/bYPC314kPtJtH5zJBzzZMWBF3iqcVC6J41dXlkjH8qm2Hp6iFq/tYyWToSViecOWADl1pbAVbnKlBuxdm4AKWNDeJYW0vK4fT/f5AwiiWICUsQeyxY5yT//AdFZKDXlI2XSEaTdneVrmerxUplsdMxqYE+Ih2JCTe8c5U1mPyVg0JUb+6FbQ0Xe3RH6qqnoLACn4imwcOWw74RTNoicA/a8RVOOrK7xU8nJD6zO8Pvc39Swm+liT+umSCU+GCBb00EsrKM8ueKY2LdxJvzxyU9H/k+bRkftjWeNLRzmd2+ysZy3NjqNdEbTvCH+596Q==\">", "sortOrder": 0, "categoryIds": ["01a06b34-0262-7574-87b4-259859cf92d1"], "online": true}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "图片对象不可用",
  "path": "/api/admin/articles"
}
```

## Step 4: GET /api/admin/articles/page 核对数量

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/articles/page?page=0&size=1' -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [
    {
      "id": "01a06b3a-17fb-74d3-bbba-473d29c9cbe7",
      "image": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zo6YzmZhKxqEWLtwCZOoHASz3QU%3D"
      },
      "title": "内联小图21-af4f49",
      "coverTitle": null,
      "subtitle": "副标题",
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b34-0262-7574-87b4-259859cf92d1"
      ],
      "online": true,
      "createdAt": "2026-09-04T07:02:44.475246Z",
      "updatedAt": "2026-09-04T07:02:44.511418Z"
    },
    {
      "id": "01a06b3a-17c7-77c7-ba11-a2e2578b0889",
      "image": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zo6YzmZhKxqEWLtwCZOoHASz3QU%3D"
      },
      "title": "app文章14-af4f49",
      "coverTitle": null,
      "subtitle": "副标题",
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b34-0262-7574-87b4-259859cf92d1"
      ],
      "online": true,
      "createdAt": "2026-09-04T07:02:44.423452Z",
      "updatedAt": "2026-09-04T07:02:44.423452Z"
    },
    {
      "id": "01a06b3a-179a-7ab4-9d81-b6498a685d3e",
      "image": {
        "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zo6YzmZhKxqEWLtwCZOoHASz3QU%3D"
      },
      "title": "富文本文章10-af4f49",
      "coverTitle": null,
      "subtitle": "副标题",
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b34-0262-7574-87b4-259859cf92d1"
      ],
      "online": true,
      "createdAt": "2026-09-04T07:02:44.377297Z",
      "updatedAt": "2026-09-04T07:02:44.405302Z"
    },
    {
      "id": "01a06b34-03f5-7a44-9530-de8ad56f0114",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:56:06.133617Z",
      "updatedAt": "2026-09-04T06:56:06.133617Z"
    },
    {
      "id": "01a06b34-03d4-7877-8b6a-927045b178f8",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:56:06.100495Z",
      "updatedAt": "2026-09-04T06:56:06.100495Z"
    },
    {
      "id": "01a06b34-0336-77f6-9d1c-d44b00add0ea",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:56:05.942456Z",
      "updatedAt": "2026-09-04T06:56:05.942456Z"
    },
    {
      "id": "01a06b34-02e8-73ea-8307-3507cee74ac7",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "黄体期怎么吃",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:56:05.864199Z",
      "updatedAt": "2026-09-04T06:56:05.864199Z"
    },
    {
      "id": "01a06b34-0264-7f07-9e17-936b5558a74c",
      "image": {
        "id": "bound/images/cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=31Wd%2BY4sHwrYX5B59ju3rohk7yo%3D"
      },
      "title": "只有标题",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [
        "甲",
        "乙"
      ],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b34-0262-7574-87b4-259859cf92d1"
      ],
      "online": true,
      "createdAt": "2026-09-04T06:56:05.732876Z",
      "updatedAt": "2026-09-04T06:56:05.738266Z"
    },
    {
      "id": "01a06b34-025c-7213-80bc-f97b26a5f815",
      "image": {
        "id": "bound/images/cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=31Wd%2BY4sHwrYX5B59ju3rohk7yo%3D"
      },
      "title": "详情页标题",
      "coverTitle": "封面标题",
      "subtitle": "副标题",
      "intro": "这是引言",
      "tags": [
        "约会",
        "周末"
      ],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b34-0258-7891-888d-2c272e0f8feb"
      ],
      "online": true,
      "createdAt": "2026-09-04T06:56:05.7241Z",
      "updatedAt": "2026-09-04T06:56:05.7241Z"
    },
    {
      "id": "01a06b34-0240-776c-b2d9-fff2ec271735",
      "image": {
        "id": "bound/images/a.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/a.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=T7WGc%2BqB3bBHE7kYA1i%2FaFI5aG8%3D"
      },
      "title": "开关文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b34-023d-725a-abe0-09921079de00"
      ],
      "online": true,
      "createdAt": "2026-09-04T06:56:05.696419Z",
      "updatedAt": "2026-09-04T06:56:05.70514Z"
    },
    {
      "id": "01a06b34-0203-74fe-8bdc-e469c44a6b3a",
      "image": {
        "id": "bound/images/a.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/a.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=T7WGc%2BqB3bBHE7kYA1i%2FaFI5aG8%3D"
      },
      "title": "标题",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:56:05.633377Z",
      "updatedAt": "2026-09-04T06:56:05.633377Z"
    },
    {
      "id": "01a06b0a-add6-75b4-b99c-e3f5dc55e9ce",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:10:57.110324Z",
      "updatedAt": "2026-09-04T06:10:57.110324Z"
    },
    {
      "id": "01a06b0a-adae-7627-9a03-60eca57a6b05",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:10:57.07035Z",
      "updatedAt": "2026-09-04T06:10:57.07035Z"
    },
    {
      "id": "01a06b0a-ad0e-7a59-8bc9-43083134c71f",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:10:56.910606Z",
      "updatedAt": "2026-09-04T06:10:56.910606Z"
    },
    {
      "id": "01a06b0a-acbb-7bff-8aac-855683daff8d",
      "image": {
        "id": "images/article.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/images/article.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=dZWrErb3p4FOX8N4xtHt7JHrY1w%3D"
      },
      "title": "黄体期怎么吃",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:10:56.827708Z",
      "updatedAt": "2026-09-04T06:10:56.827708Z"
    },
    {
      "id": "01a06b0a-ac50-7004-b52a-50488cbfadf3",
      "image": {
        "id": "bound/images/cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=31Wd%2BY4sHwrYX5B59ju3rohk7yo%3D"
      },
      "title": "只有标题",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [
        "甲",
        "乙"
      ],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b0a-ac4e-711d-91f7-1e05f30cfd1e"
      ],
      "online": true,
      "createdAt": "2026-09-04T06:10:56.719972Z",
      "updatedAt": "2026-09-04T06:10:56.724316Z"
    },
    {
      "id": "01a06b0a-ac49-7800-9b86-70cf54acf1d3",
      "image": {
        "id": "bound/images/cover.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/cover.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=31Wd%2BY4sHwrYX5B59ju3rohk7yo%3D"
      },
      "title": "详情页标题",
      "coverTitle": "封面标题",
      "subtitle": "副标题",
      "intro": "这是引言",
      "tags": [
        "约会",
        "周末"
      ],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b0a-ac47-70e1-9f7a-3a5f2dd52ca5"
      ],
      "online": true,
      "createdAt": "2026-09-04T06:10:56.713472Z",
      "updatedAt": "2026-09-04T06:10:56.713472Z"
    },
    {
      "id": "01a06b0a-ac33-7495-a25e-d2a56f68793c",
      "image": {
        "id": "bound/images/a.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/a.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=T7WGc%2BqB3bBHE7kYA1i%2FaFI5aG8%3D"
      },
      "title": "开关文章",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [
        "01a06b0a-ac30-7860-8227-54dff8fd18d2"
      ],
      "online": true,
      "createdAt": "2026-09-04T06:10:56.691251Z",
      "updatedAt": "2026-09-04T06:10:56.698197Z"
    },
    {
      "id": "01a06b0a-abff-74ac-99e4-b1198253fb14",
      "image": {
        "id": "bound/images/a.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/images/a.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=T7WGc%2BqB3bBHE7kYA1i%2FaFI5aG8%3D"
      },
      "title": "标题",
      "coverTitle": null,
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-04T06:10:56.637967Z",
      "updatedAt": "2026-09-04T06:10:56.637967Z"
    },
    {
      "id": "01a0622f-dcd7-7496-a019-762e8efbe4dd",
      "image": {
        "id": "bound/art-m9pf43c20.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/art-m9pf43c20.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=sBxtdjq5dzpXMapyk9t0ecTbWnQ%3D"
      },
      "title": "art-m9pf43c20",
      "coverTitle": "封面标题036",
      "subtitle": null,
      "intro": null,
      "tags": [],
      "sortOrder": 0,
      "categoryIds": [],
      "online": true,
      "createdAt": "2026-09-02T12:54:59.031265Z",
      "updatedAt": "2026-09-02T12:54:59.031265Z"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 144,
  "totalPages": 8
}
```
