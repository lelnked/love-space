# TC-article-IT-011 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## 前置: 创建栏目 IT011栏目A（sortOrder=2）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT011栏目A","icon":"images/it011-a.png","sortOrder":2}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-4e53-785e-88f0-3703b08c7fe1",
  "name": "IT011栏目A",
  "icon": {
    "id": "bound/it011-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-a.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=3NdovVIuFrRl8n%2FQ9vUx5H9NdEY%3D"
  },
  "sortOrder": 2,
  "createdAt": "2026-08-25T09:24:31.443446161Z",
  "updatedAt": "2026-08-25T09:24:31.443446161Z"
}
```

## 前置: 创建栏目 IT011栏目B（sortOrder=1）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT011栏目B","icon":"images/it011-b.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-4e70-7499-8160-3bb706c5926d",
  "name": "IT011栏目B",
  "icon": {
    "id": "bound/it011-b.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-b.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=bE2%2B20sloUQBABnNMxpp5fDGodA%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:24:31.472199951Z",
  "updatedAt": "2026-08-25T09:24:31.472199951Z"
}
```

## 前置: 栏目B下文章甲 sortOrder=3（带 coverTitle/tags）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it011-1.png","title":"IT011权重3","coverTitle":"IT011封面3","subtitle":"副标题3","tags":["约会"],"contentHtml":"<p>正文</p>","sortOrder":3,"categoryIds":["01a0383c-4e70-7499-8160-3bb706c5926d"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-4e89-7102-b174-e957d986b6f1",
  "image": {
    "id": "bound/it011-1.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-1.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=93%2FPWPlDY8jrsZpMME1ly9GLZJc%3D"
  },
  "title": "IT011权重3",
  "coverTitle": "IT011封面3",
  "subtitle": "副标题3",
  "intro": null,
  "tags": [
    "约会"
  ],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 3,
  "categoryIds": [
    "01a0383c-4e70-7499-8160-3bb706c5926d"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:24:31.497022431Z",
  "updatedAt": "2026-08-25T09:24:31.497022431Z"
}
```

## 前置: 栏目B下文章乙 sortOrder=1（带 coverTitle/tags）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it011-2.png","title":"IT011权重1","coverTitle":"IT011封面1","subtitle":"副标题1","tags":["周末","户外"],"contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a0383c-4e70-7499-8160-3bb706c5926d"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-4e9f-721d-8725-81e16c384f86",
  "image": {
    "id": "bound/it011-2.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it011-2.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=c%2BPiSJrygs60zySveDPvFRb50p8%3D"
  },
  "title": "IT011权重1",
  "coverTitle": "IT011封面1",
  "subtitle": "副标题1",
  "intro": null,
  "tags": [
    "周末",
    "户外"
  ],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383c-4e70-7499-8160-3bb706c5926d"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:24:31.519088086Z",
  "updatedAt": "2026-08-25T09:24:31.519088086Z"
}
```

## Step 2: GET /api/app/article-categories

```bash
curl -s -i "http://localhost:8081/api/app/article-categories" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
[
  {
    "id": "01a0383c-4e70-7499-8160-3bb706c5926d",
    "name": "IT011栏目B",
    "icon": {
      "id": "bound/it011-b.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it011-b.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=Cdy9xMiVP3hxRiQEGLUjR6LsIEg%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383c-3610-78c5-9f26-64fbf341fc34",
    "name": "IT019栏目",
    "icon": {
      "id": "bound/it019-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it019-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=Z2wQQf4B9Cb%2Bn9YNjXD%2BaZzuMhw%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383c-35a5-78a5-9c09-cc4a8eb604c8",
    "name": "IT018栏目",
    "icon": {
      "id": "bound/it018-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it018-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=uQLaVpsap5FHkV9T8PAKO1EQ%2BGw%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383c-3553-7245-bad3-48c85ce88ba8",
    "name": "IT014栏目",
    "icon": {
      "id": "bound/it014-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it014-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=G12O0spuLRGJdZRvYescSimlb6c%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383c-33c1-744c-9c8d-d338bfd46833",
    "name": "IT012栏目",
    "icon": {
      "id": "bound/it012-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it012-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=FhkJyByWV%2FQ4V36FU12efqQjIkU%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383c-328d-7499-8ac1-71488da53737",
    "name": "IT011栏目B",
    "icon": {
      "id": "bound/it011-b.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it011-b.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=Cdy9xMiVP3hxRiQEGLUjR6LsIEg%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383b-76e3-71c2-a86c-60a715b0a528",
    "name": "IT017栏目",
    "icon": {
      "id": "bound/it017-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it017-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=3Cc2zVgW3GIRzg37pPt%2B27rrNYM%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383b-7677-7027-b6ab-cb82492c4d95",
    "name": "IT016栏目",
    "icon": {
      "id": "bound/it016-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it016-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=N3G9nQKqN9V1%2FBoV4wKOBf3EzZ8%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383b-75f7-70f3-bc45-5c5dca552b76",
    "name": "IT010栏目",
    "icon": {
      "id": "bound/it010-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it010-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=fjIPnxQLikOBTX1jdTRlWTDeWMs%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383b-7562-71d8-a9d7-776c897873ee",
    "name": "IT009栏目",
    "icon": {
      "id": "bound/it009-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it009-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=LVYqwoRbSpyuSi9M%2FQf%2Fc8cmI0E%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383b-74c3-74e1-8980-96f6b01dfd77",
    "name": "IT008栏目",
    "icon": {
      "id": "bound/it008-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it008-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=WdSEiOAISPYK2XTp5KvBpoYHyfk%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383b-7432-744e-9fa7-ca73c500c48c",
    "name": "IT007栏目A",
    "icon": {
      "id": "bound/it007-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it007-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=qxJmBSystErl8XqtIy0B8q22eOg%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383a-d6b8-7523-a3ab-68fffb104604",
    "name": "IT005栏目A",
    "icon": {
      "id": "bound/it005-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it005-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=Jp8TXKhNm5ZFOucddLtLO2eNBtc%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383a-d51a-7b64-bc09-95fe39882697",
    "name": "行程攻略",
    "icon": {
      "id": "bound/it001-icon.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it001-icon.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=IPhK%2Bco6o3Q2nkT%2Bpr2pmB4r6lc%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a03839-e266-7cd8-bf57-e3b11d986c45",
    "name": "IT015栏目",
    "icon": {
      "id": "bound/it015-icon.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it015-icon.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=3049lsW8fIVnsEBxdz59ftrUaBg%3D"
    },
    "sortOrder": 1
  },
  {
    "id": "01a0383c-4e53-785e-88f0-3703b08c7fe1",
    "name": "IT011栏目A",
    "icon": {
      "id": "bound/it011-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it011-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=09NZrwTp3W2SaI8WudrfhDOdLVY%3D"
    },
    "sortOrder": 2
  },
  {
    "id": "01a0383c-326d-7573-b585-d6eb6b744cc2",
    "name": "IT011栏目A",
    "icon": {
      "id": "bound/it011-a.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it011-a.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=09NZrwTp3W2SaI8WudrfhDOdLVY%3D"
    },
    "sortOrder": 2
  },
  {
    "id": "01a0383b-7455-75bb-8cd9-615192b0e940",
    "name": "IT007栏目B",
    "icon": {
      "id": "bound/it007-b.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it007-b.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=bDX4bMfxTJxv7xGYiT5dV3jUdvA%3D"
    },
    "sortOrder": 2
  },
  {
    "id": "01a0383a-d6d4-7424-8fc1-93a5af080a9f",
    "name": "IT005栏目B",
    "icon": {
      "id": "bound/it005-b.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it005-b.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=tkFz4dM1OOdKJRpAo3RW%2F9N%2Fg3M%3D"
    },
    "sortOrder": 2
  },
  {
    "id": "01a0383a-d61b-7c12-ad4f-c906e5ed7051",
    "name": "IT004栏目B",
    "icon": {
      "id": "bound/it004-b.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it004-b.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=i%2Blx9cFlUWhHcibYh6dwDkNrRiM%3D"
    },
    "sortOrder": 2
  },
  {
    "id": "01a0383a-d5ab-7119-9a32-e0c5e7c4f6a1",
    "name": "IT003美食攻略",
    "icon": {
      "id": "bound/it003-icon-new.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it003-icon-new.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=8MIUITNQSDxgeslFSJ3vy2nlgss%3D"
    },
    "sortOrder": 5
  }
]
```

## Step 3: GET /api/app/articles?categoryId={B}

```bash
curl -s -i "http://localhost:8081/api/app/articles?categoryId=01a0383c-4e70-7499-8160-3bb706c5926d" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
[
  {
    "id": "01a0383c-4e9f-721d-8725-81e16c384f86",
    "image": {
      "id": "bound/it011-2.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it011-2.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=Bx2jwDN5sR%2BG3kzr8AKJJCE1r6Q%3D"
    },
    "coverTitle": "IT011封面1",
    "title": "IT011权重1",
    "subtitle": "副标题1",
    "tags": [
      "周末",
      "户外"
    ]
  },
  {
    "id": "01a0383c-4e89-7102-b174-e957d986b6f1",
    "image": {
      "id": "bound/it011-1.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it011-1.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=lVPbndkIGeXY56J2T4K%2BZNldFWY%3D"
    },
    "coverTitle": "IT011封面3",
    "title": "IT011权重3",
    "subtitle": "副标题3",
    "tags": [
      "约会"
    ]
  }
]
```
