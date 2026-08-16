# TC-activity-IT-006 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"活动城6-162755","englishName":"City16275528088","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b84e-7f77-8b99-5b126b590940",
  "chineseName": "活动城6-162755",
  "englishName": "City16275528088",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:56.366893163Z",
  "updatedAt": "2026-08-16T16:27:56.366893163Z"
}
```

## Step 2: POST /api/admin/activities detailHtml 含 2 个 img（src 为 images/ 前缀 objectKey）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-b84e-7f77-8b99-5b126b590940","images":["images/78c7732c-4fa3-435c-8772-d0f36acb120e.png","images/887d318b-0270-4339-860e-7fd65af060ff.jpg"],"title":"富文本活动-162755","tags":["露营","观星"],"periods":["FOLLICULAR","OVULATION"],"level":"L2","introduction":"海岛露营介绍","editorNote":"编辑寄语","gatheringPlace":"成都天府机场","dismissalPlace":"市区解散","transportation":"大巴接驳","visa":"无需签证","itinerary":[{"title":"Day1","content":"到成都天府机场集合"},{"title":"Day2","content":"露营与观星"}],"detailHtml":"<p>富文本段落一</p><img src=\"images/0875cf27-0866-4b10-a401-0f7c33d12a65.png\"><p>富文本段落二</p><img src=\"images/47833cab-21d7-40ac-83c8-4989d83b34f5.png\">","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b87f-73ea-b022-70651200145b",
  "cityId": "01a00b66-b84e-7f77-8b99-5b126b590940",
  "images": [
    {
      "id": "bound/78c7732c-4fa3-435c-8772-d0f36acb120e.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/78c7732c-4fa3-435c-8772-d0f36acb120e.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VSyuC4EcdKOCWLx%2F8DDE0hGGpvg%3D"
    },
    {
      "id": "bound/887d318b-0270-4339-860e-7fd65af060ff.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/887d318b-0270-4339-860e-7fd65af060ff.jpg?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=vt3PQQF2Mf7uI9Q5RHghaA7pB%2FE%3D"
    }
  ],
  "title": "富文本活动-162755",
  "tags": [
    "露营",
    "观星"
  ],
  "periods": [
    "FOLLICULAR",
    "OVULATION"
  ],
  "level": "L2",
  "introduction": "海岛露营介绍",
  "editorNote": "编辑寄语",
  "gatheringPlace": "成都天府机场",
  "dismissalPlace": "市区解散",
  "transportation": "大巴接驳",
  "visa": "无需签证",
  "itinerary": [
    {
      "title": "Day1",
      "content": "到成都天府机场集合"
    },
    {
      "title": "Day2",
      "content": "露营与观星"
    }
  ],
  "detailHtml": "<p>富文本段落一</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0875cf27-0866-4b10-a401-0f7c33d12a65.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=SDeKJJdHzx1O0RMzLJgM72giUwk%3D\"><p>富文本段落二</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/47833cab-21d7-40ac-83c8-4989d83b34f5.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2FdofF1WHyJC1RK3VIJ%2FDnRNJpVE%3D\">",
  "online": true,
  "createdAt": "2026-08-16T16:27:56.415175962Z",
  "updatedAt": "2026-08-16T16:27:56.415175962Z"
}
```

## Step 3: GET 详情：img src 应替换为签名 URL

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/activities/01a00b66-b87f-73ea-b022-70651200145b" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b87f-73ea-b022-70651200145b",
  "cityId": "01a00b66-b84e-7f77-8b99-5b126b590940",
  "images": [
    {
      "id": "bound/78c7732c-4fa3-435c-8772-d0f36acb120e.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/78c7732c-4fa3-435c-8772-d0f36acb120e.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VSyuC4EcdKOCWLx%2F8DDE0hGGpvg%3D"
    },
    {
      "id": "bound/887d318b-0270-4339-860e-7fd65af060ff.jpg",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/887d318b-0270-4339-860e-7fd65af060ff.jpg?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=vt3PQQF2Mf7uI9Q5RHghaA7pB%2FE%3D"
    }
  ],
  "title": "富文本活动-162755",
  "tags": [
    "露营",
    "观星"
  ],
  "periods": [
    "FOLLICULAR",
    "OVULATION"
  ],
  "level": "L2",
  "introduction": "海岛露营介绍",
  "editorNote": "编辑寄语",
  "gatheringPlace": "成都天府机场",
  "dismissalPlace": "市区解散",
  "transportation": "大巴接驳",
  "visa": "无需签证",
  "itinerary": [
    {
      "title": "Day1",
      "content": "到成都天府机场集合"
    },
    {
      "title": "Day2",
      "content": "露营与观星"
    }
  ],
  "detailHtml": "<p>富文本段落一</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0875cf27-0866-4b10-a401-0f7c33d12a65.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=SDeKJJdHzx1O0RMzLJgM72giUwk%3D\"><p>富文本段落二</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/47833cab-21d7-40ac-83c8-4989d83b34f5.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2FdofF1WHyJC1RK3VIJ%2FDnRNJpVE%3D\">",
  "online": true,
  "createdAt": "2026-08-16T16:27:56.415176Z",
  "updatedAt": "2026-08-16T16:27:56.415176Z"
}
```

## Step 4: PUT detailHtml 改为不含 img 的纯文本

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/activities/01a00b66-b87f-73ea-b022-70651200145b" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"cityId":"01a00b66-b84e-7f77-8b99-5b126b590940","images":["images/93ec3017-b5b4-444b-ad6b-067597c93bb7.png"],"title":"富文本活动改","detailHtml":"<p>纯文本，无图片</p>"}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b87f-73ea-b022-70651200145b",
  "cityId": "01a00b66-b84e-7f77-8b99-5b126b590940",
  "images": [
    {
      "id": "bound/93ec3017-b5b4-444b-ad6b-067597c93bb7.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/93ec3017-b5b4-444b-ad6b-067597c93bb7.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6k86FaOvhoPkDiGpw3tdHJ43k5Q%3D"
    }
  ],
  "title": "富文本活动改",
  "tags": [],
  "periods": [],
  "level": null,
  "introduction": null,
  "editorNote": null,
  "gatheringPlace": null,
  "dismissalPlace": null,
  "transportation": null,
  "visa": null,
  "itinerary": [],
  "detailHtml": "<p>纯文本，无图片</p>",
  "online": false,
  "createdAt": "2026-08-16T16:27:56.415176Z",
  "updatedAt": "2026-08-16T16:27:56.415176Z"
}
```

## Step 5: GET 详情：无 img HTML 原样往返

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/activities/01a00b66-b87f-73ea-b022-70651200145b" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-b87f-73ea-b022-70651200145b",
  "cityId": "01a00b66-b84e-7f77-8b99-5b126b590940",
  "images": [
    {
      "id": "bound/93ec3017-b5b4-444b-ad6b-067597c93bb7.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/93ec3017-b5b4-444b-ad6b-067597c93bb7.png?Expires=1786899476&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6k86FaOvhoPkDiGpw3tdHJ43k5Q%3D"
    }
  ],
  "title": "富文本活动改",
  "tags": [],
  "periods": [],
  "level": null,
  "introduction": null,
  "editorNote": null,
  "gatheringPlace": null,
  "dismissalPlace": null,
  "transportation": null,
  "visa": null,
  "itinerary": [],
  "detailHtml": "<p>纯文本，无图片</p>",
  "online": false,
  "createdAt": "2026-08-16T16:27:56.415176Z",
  "updatedAt": "2026-08-16T16:27:56.498041Z"
}
```

