# TC-activity-IT-025 请求/响应存证

POST/PUT /api/admin/activities 富文本内联小图放行，admin/app 读取原样透传

执行日期: 2026-09-04 ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，`export TOKEN=<登录返回 token>` 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key（测试 fixture，明文入存证）

## Step 1: POST /api/admin/activities detailHtml=内联 gif(2048B) + objectKey 混排

```bash
curl -s -i -X POST http://localhost:21423/api/admin/activities -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"images": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"], "title": "内联小图25-af4f49", "tags": ["富文本"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "交通", "visa": "签证", "itinerary": [{"title": "Day1", "content": "出发"}], "detailHtml": "<p>表情</p><img src=\"data:image/gif;base64,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\"><img src=\"images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff2501.png\"><p>结束</p>", "online": true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-16b0-7f0e-8983-292f842f88a1",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "内联小图25-af4f49",
  "subtitle": null,
  "tags": [
    "富文本"
  ],
  "periods": [
    "FOLLICULAR"
  ],
  "level": "L1",
  "introduction": "简介",
  "editorNote": "编辑说",
  "gatheringPlace": "集合地",
  "dismissalPlace": "解散地",
  "transportation": "交通",
  "visa": "签证",
  "landscape": null,
  "itinerary": [
    {
      "title": "Day1",
      "content": "出发"
    }
  ],
  "detailHtml": "<p>表情</p><img src=\"data:image/gif;base64,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\"><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff2501.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6fP26u7NIA3rDiXe84hCzNgcE4Q%3D\"><p>结束</p>",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.144876888Z",
  "updatedAt": "2026-09-04T07:02:44.144876888Z"
}
```

## Step 2: GET /api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1

```bash
curl -s -i -X GET http://localhost:21423/api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1 -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-16b0-7f0e-8983-292f842f88a1",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "内联小图25-af4f49",
  "subtitle": null,
  "tags": [
    "富文本"
  ],
  "periods": [
    "FOLLICULAR"
  ],
  "level": "L1",
  "introduction": "简介",
  "editorNote": "编辑说",
  "gatheringPlace": "集合地",
  "dismissalPlace": "解散地",
  "transportation": "交通",
  "visa": "签证",
  "landscape": null,
  "itinerary": [
    {
      "title": "Day1",
      "content": "出发"
    }
  ],
  "detailHtml": "<p>表情</p><img src=\"data:image/gif;base64,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\"><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff2501.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=6fP26u7NIA3rDiXe84hCzNgcE4Q%3D\"><p>结束</p>",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.144877Z",
  "updatedAt": "2026-09-04T07:02:44.144877Z"
}
```

## Step 3: GET http://localhost:8081/api/app/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1

```bash
curl -s -i -X GET http://localhost:8081/api/app/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1 -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-16b0-7f0e-8983-292f842f88a1",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "https://test.oss-cn-test.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=x&Signature=EXgeP3K%2BWuFXE36nKt6h5OmzFxQ%3D"
    }
  ],
  "title": "内联小图25-af4f49",
  "subtitle": null,
  "tags": [
    "富文本"
  ],
  "periods": [
    "FOLLICULAR"
  ],
  "level": "L1",
  "introduction": "简介",
  "editorNote": "编辑说",
  "gatheringPlace": "集合地",
  "dismissalPlace": "解散地",
  "transportation": "交通",
  "visa": "签证",
  "landscape": null,
  "itinerary": [
    {
      "title": "Day1",
      "content": "出发"
    }
  ],
  "detailHtml": "<p>表情</p><img src=\"data:image/gif;base64,Vkd5g+xZ4Wcjukp5ZmgZ/7Gdvo5X6Nn8xYPp7bVv7Mq/dMi16/BKfqng6IhXF61fAXbzXgDVI/O5GnvBdCJMrXiq8LGJjAfULYaKcNbS8EIbnz6j6L4hjrhCGvePUYjOUy0wKZxPhOV4VMWbwl3fjFHUQFj5nOyxH37YU76KYWHWCqDrKngJHtv/1GajB9ni6A9GdOu4CDsKFfzyHvEIbRv3WIi+6jCs6phlidKUrmqKf83gZ5UaKJpzaeUdylUVGCsIsE6QRVl/zS2JK4INM0v+eotTPmjATvKYBxqLy/cFoFujGFXJg2VjnlSDGAEPgI6nKXf2tcJjBj5R9zSoAAdBiq6plYcIA82SnQJtPlFqGhBMmXPZycod1oeKfiA7htTigbDej7woU2tmso70lZb1265mgjZW0FROqSb0rLwIcCsFVmAUZqz9W0BgKIFrz2kjIPisZ8O2FrFxeuvOEpFQWNNE3G/QnNvZX7+B1JCm2VpNZPa+MKt21BKrYETy2n+8VFQoCL7E2N8wuOIbTKJCmlS1Bb3PKjJXKZK41TWKacsCGDMSHJja6NQ7my9yMbLCajPy70mV3xZPJzBQ5827aqHTgqog8LAfy5NEMR9q3mUod77Qy4vwoYFCxFvSJ2+BYbTBIfeAje8mhrtjBPCy+i1pFSJKKRFNsgH7ZCRlpLY+a9CFroFgNA1PSOdCDwqfxOsMPKyRQzseQzjjdiAnTPh8scB0L87aebP/oq/wRRbYIWydbAs/xxK8qaxVeMGp8Qmw6RwsTxMV559BodhEr//iED5qorgrp8aM8EortsmUnxlniCuLljpv+CLfVwaDT71NqCVcEM7JFMf68SrBbP+YYd5drPIsbKrzKuSr0l7d3bnv/19TWW4wy8R3pDt2mzQUisvmbNSX2u/lxc7550Si+mII4fuB1ONgBf+op3epTgVhZqzMuP/GFx+anrUkpE8VlkHHLCxN1c1+/znsUKIYArY8x3QjEpQEXgCWExvBqi2sXCWMVwZ6TqiCrANBB7ReUqqj15lFVKqnaEP2Y3+Sgux0ZWhVCZetqLwCrcr5W1o3QRxMX9b2nSwHFba6rauvQ6dfZLtICcMPbvifyhfiprrmv3HX1bLm4lwOtHvhT/DdhtqWi7QwIxfM6ahtfu9aHOVq01b9TZ+I5vbfh83b73yFCT1fX96j/IVFw+QZX+U0p/tH3G9N08PPJTPe60eI03nK0wZjoUGSjQzL7rYPKqhmn6MrFavMh/dfrI2i3yS3HQizRErS6y8DJOvYwv3lfNUmZ4lXGTFv6gV+zPMFO93D8/r/hLEvDRNYG9OoX12436ZBSQyxUPmVHzKIUhjf0hFTilunQxGtg7syGWQ00t8b7u5dREoPWiziOs7z89DoeWXi/lctuy3RKBh+46RVdtxxkjmkWCv0yq9YkLytOrcao4NqQEBymWY3pz6hQnk3UwaRpoRDIbKVOKhK9VMLXGZhwH5oMlTY/Kwxbr2IGGs14j7LUyA9PG26A4L4SVNbfuRz2vP1jTm8jkqa5rxYTIDKgVzVQvtTj0p5KjXYamUnUTKVvN1vfT9i42fp4lj8gToSegeBAzplEK0OCOQIZesukJWa6XueCPwpVJHZ7MQSbo+Rkq/i12Oc3dtPvH7iIZK80zIvWDO4KqwXYPsMl3U4sq1NREO48O4QMswbJokx1nUxaT9u/Z3Z/kBn960mXrHurfr4uiUVulOkeqwvgRBsHKyOhCH6zbrKbY0QvT1EuXhLgKJVXnPyLTj61KadhQnerrd63PXj2ibiz8DU3kwwUJINNdT6z+2gjw7Q5vfJ5ADx+mkDV+xB/deCW2vHRKkK1C4CPqLnah7J2qLUB6Uh7dKn14ctfWlL7pBM+YWxUZUR4uWu7bFVUbtpa2dC4Wd99DwgXaz1xfQnmz35QECCIIORKG1EKeQKurFitkHVGyc6z/xU39bCLT8kuBmNYaal2+IftrJA0gsuRroVCiBJCW/hQMX0WtG5zauARl8qgKIYRtTg2TR+nmdkXAQcaImRfCHImUuZf+Ql4nU7BRNY/4kCsm+zdXbK9joKJd0cQ/otBwOPD2gAbRCvXMhhs3ih1IZlCnzF/2iH2DaKa/FfdPfhlaM9FlRQdVM3mW7WIgBX/xVsjdLIE9RhSvOWuK5eMss2TNElEtUUmbA9UlS7OI9VRH4WFVcZ6yjtrrkPCCR1e++HjHrbz9DiUGjF14Gr5db0XAz4HwSss6xQ0U46mXIxCKEd13jRsW4NdlXlmakYAoqa1q0q8nqMHa+ziWzgTitlpn4ZuGrBg37nThb7cbvt1Oi7axGaSWf37MUK3jHbZZ2mYpLmqEim2ajEx8mq7CJJBWp9BHM6cyP6ib9547azffF8YJZEnFFyfr+UGugoSPeWP5/6MvoTSJG6u05dCDbi0H4QKj05jdczZdNozSPdpFdYKh0wQSzmJJ1Xk8VFlsXi+fyPEpNnF4+OV/YlrOnCfQooo/CrcVBAIKYqPNWAbiDfHsteLWpmWlE/zE8AeZQSAtwNgSRNJ7+5IChK7yCoo1RoJ472L2GPoWl/0jOjlm95zkObv1ah8Kn6QLzfNCiuE02ckjJH35Ghqr36MowUeelZyoOndKzTZDgXbtsZiGOTU4s730qoUdLuSig6LpRqvXTygVr11TTv4UzGtdFiWd9JfbfjBYJKnryW/LoYE0P3eNUte8DWjmKT8cEBaEj12ro=\"><img src=\"https://test.oss-cn-test.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff2501.png?Expires=1788507164&OSSAccessKeyId=x&Signature=HB0fxfK10o4WDJqyTI%2BC%2Famj1YY%3D\"><p>结束</p>"
}
```

## Step 4: PUT /api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1 detailHtml=<p>改</p><img src=D1>

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1 -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"images": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"], "title": "内联小图25-af4f49", "tags": ["富文本"], "periods": ["FOLLICULAR"], "level": "L1", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合地", "dismissalPlace": "解散地", "transportation": "交通", "visa": "签证", "itinerary": [{"title": "Day1", "content": "出发"}], "detailHtml": "<p>改</p><img src=\"data:image/gif;base64,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\">", "online": true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-16b0-7f0e-8983-292f842f88a1",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "内联小图25-af4f49",
  "subtitle": null,
  "tags": [
    "富文本"
  ],
  "periods": [
    "FOLLICULAR"
  ],
  "level": "L1",
  "introduction": "简介",
  "editorNote": "编辑说",
  "gatheringPlace": "集合地",
  "dismissalPlace": "解散地",
  "transportation": "交通",
  "visa": "签证",
  "landscape": null,
  "itinerary": [
    {
      "title": "Day1",
      "content": "出发"
    }
  ],
  "detailHtml": "<p>改</p><img src=\"data:image/gif;base64,Vkd5g+xZ4Wcjukp5ZmgZ/7Gdvo5X6Nn8xYPp7bVv7Mq/dMi16/BKfqng6IhXF61fAXbzXgDVI/O5GnvBdCJMrXiq8LGJjAfULYaKcNbS8EIbnz6j6L4hjrhCGvePUYjOUy0wKZxPhOV4VMWbwl3fjFHUQFj5nOyxH37YU76KYWHWCqDrKngJHtv/1GajB9ni6A9GdOu4CDsKFfzyHvEIbRv3WIi+6jCs6phlidKUrmqKf83gZ5UaKJpzaeUdylUVGCsIsE6QRVl/zS2JK4INM0v+eotTPmjATvKYBxqLy/cFoFujGFXJg2VjnlSDGAEPgI6nKXf2tcJjBj5R9zSoAAdBiq6plYcIA82SnQJtPlFqGhBMmXPZycod1oeKfiA7htTigbDej7woU2tmso70lZb1265mgjZW0FROqSb0rLwIcCsFVmAUZqz9W0BgKIFrz2kjIPisZ8O2FrFxeuvOEpFQWNNE3G/QnNvZX7+B1JCm2VpNZPa+MKt21BKrYETy2n+8VFQoCL7E2N8wuOIbTKJCmlS1Bb3PKjJXKZK41TWKacsCGDMSHJja6NQ7my9yMbLCajPy70mV3xZPJzBQ5827aqHTgqog8LAfy5NEMR9q3mUod77Qy4vwoYFCxFvSJ2+BYbTBIfeAje8mhrtjBPCy+i1pFSJKKRFNsgH7ZCRlpLY+a9CFroFgNA1PSOdCDwqfxOsMPKyRQzseQzjjdiAnTPh8scB0L87aebP/oq/wRRbYIWydbAs/xxK8qaxVeMGp8Qmw6RwsTxMV559BodhEr//iED5qorgrp8aM8EortsmUnxlniCuLljpv+CLfVwaDT71NqCVcEM7JFMf68SrBbP+YYd5drPIsbKrzKuSr0l7d3bnv/19TWW4wy8R3pDt2mzQUisvmbNSX2u/lxc7550Si+mII4fuB1ONgBf+op3epTgVhZqzMuP/GFx+anrUkpE8VlkHHLCxN1c1+/znsUKIYArY8x3QjEpQEXgCWExvBqi2sXCWMVwZ6TqiCrANBB7ReUqqj15lFVKqnaEP2Y3+Sgux0ZWhVCZetqLwCrcr5W1o3QRxMX9b2nSwHFba6rauvQ6dfZLtICcMPbvifyhfiprrmv3HX1bLm4lwOtHvhT/DdhtqWi7QwIxfM6ahtfu9aHOVq01b9TZ+I5vbfh83b73yFCT1fX96j/IVFw+QZX+U0p/tH3G9N08PPJTPe60eI03nK0wZjoUGSjQzL7rYPKqhmn6MrFavMh/dfrI2i3yS3HQizRErS6y8DJOvYwv3lfNUmZ4lXGTFv6gV+zPMFO93D8/r/hLEvDRNYG9OoX12436ZBSQyxUPmVHzKIUhjf0hFTilunQxGtg7syGWQ00t8b7u5dREoPWiziOs7z89DoeWXi/lctuy3RKBh+46RVdtxxkjmkWCv0yq9YkLytOrcao4NqQEBymWY3pz6hQnk3UwaRpoRDIbKVOKhK9VMLXGZhwH5oMlTY/Kwxbr2IGGs14j7LUyA9PG26A4L4SVNbfuRz2vP1jTm8jkqa5rxYTIDKgVzVQvtTj0p5KjXYamUnUTKVvN1vfT9i42fp4lj8gToSegeBAzplEK0OCOQIZesukJWa6XueCPwpVJHZ7MQSbo+Rkq/i12Oc3dtPvH7iIZK80zIvWDO4KqwXYPsMl3U4sq1NREO48O4QMswbJokx1nUxaT9u/Z3Z/kBn960mXrHurfr4uiUVulOkeqwvgRBsHKyOhCH6zbrKbY0QvT1EuXhLgKJVXnPyLTj61KadhQnerrd63PXj2ibiz8DU3kwwUJINNdT6z+2gjw7Q5vfJ5ADx+mkDV+xB/deCW2vHRKkK1C4CPqLnah7J2qLUB6Uh7dKn14ctfWlL7pBM+YWxUZUR4uWu7bFVUbtpa2dC4Wd99DwgXaz1xfQnmz35QECCIIORKG1EKeQKurFitkHVGyc6z/xU39bCLT8kuBmNYaal2+IftrJA0gsuRroVCiBJCW/hQMX0WtG5zauARl8qgKIYRtTg2TR+nmdkXAQcaImRfCHImUuZf+Ql4nU7BRNY/4kCsm+zdXbK9joKJd0cQ/otBwOPD2gAbRCvXMhhs3ih1IZlCnzF/2iH2DaKa/FfdPfhlaM9FlRQdVM3mW7WIgBX/xVsjdLIE9RhSvOWuK5eMss2TNElEtUUmbA9UlS7OI9VRH4WFVcZ6yjtrrkPCCR1e++HjHrbz9DiUGjF14Gr5db0XAz4HwSss6xQ0U46mXIxCKEd13jRsW4NdlXlmakYAoqa1q0q8nqMHa+ziWzgTitlpn4ZuGrBg37nThb7cbvt1Oi7axGaSWf37MUK3jHbZZ2mYpLmqEim2ajEx8mq7CJJBWp9BHM6cyP6ib9547azffF8YJZEnFFyfr+UGugoSPeWP5/6MvoTSJG6u05dCDbi0H4QKj05jdczZdNozSPdpFdYKh0wQSzmJJ1Xk8VFlsXi+fyPEpNnF4+OV/YlrOnCfQooo/CrcVBAIKYqPNWAbiDfHsteLWpmWlE/zE8AeZQSAtwNgSRNJ7+5IChK7yCoo1RoJ472L2GPoWl/0jOjlm95zkObv1ah8Kn6QLzfNCiuE02ckjJH35Ghqr36MowUeelZyoOndKzTZDgXbtsZiGOTU4s730qoUdLuSig6LpRqvXTygVr11TTv4UzGtdFiWd9JfbfjBYJKnryW/LoYE0P3eNUte8DWjmKT8cEBaEj12ro=\">",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.144877Z",
  "updatedAt": "2026-09-04T07:02:44.144877Z"
}
```

## Step 5: GET /api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1

```bash
curl -s -i -X GET http://localhost:21423/api/admin/activities/01a06b3a-16b0-7f0e-8983-292f842f88a1 -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-16b0-7f0e-8983-292f842f88a1",
  "images": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=115LYOSJn%2BMTLUdz6eNqxGl5bPU%3D"
    }
  ],
  "title": "内联小图25-af4f49",
  "subtitle": null,
  "tags": [
    "富文本"
  ],
  "periods": [
    "FOLLICULAR"
  ],
  "level": "L1",
  "introduction": "简介",
  "editorNote": "编辑说",
  "gatheringPlace": "集合地",
  "dismissalPlace": "解散地",
  "transportation": "交通",
  "visa": "签证",
  "landscape": null,
  "itinerary": [
    {
      "title": "Day1",
      "content": "出发"
    }
  ],
  "detailHtml": "<p>改</p><img src=\"data:image/gif;base64,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\">",
  "online": true,
  "createdAt": "2026-09-04T07:02:44.144877Z",
  "updatedAt": "2026-09-04T07:02:44.179594Z"
}
```
