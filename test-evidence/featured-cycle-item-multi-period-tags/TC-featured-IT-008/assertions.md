# TC-featured-IT-008 断言明细

结果: ✅ 通过

契约: `FeaturedCycleItemUpsertRequest` schema 仍为 activityId/routeId/articleId 三字段，`FeaturedCycleItemResponse` 未声明（⚠️ 已知契约缺口，tasks 6.1 待做），故不做 schema 断言，仅做响应体断言。

- ✅ 创建返回 200 — 200
- ✅ Content-Type 含 application/json — application/json
- ✅ type=ROUTE — ROUTE
- ✅ targetId 等于该路线 id — 01a04882-0b4e-78d4-a5d5-db7bd9d9ed43
- ✅ title 为手填值 — 排卵期就该出门
- ✅ subtitle 为手填值 — 三天两夜
- ✅ title 不等于路线实体主标题 — 排卵期就该出门 vs 路线主标题008-b3dacc
- ✅ description 原样 — 体力最好的几天
- ✅ banner 为签名 URL — {'id': 'bound/b008.png', 'url': 'http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b008.png?Expires=1787924677&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE'}
- ✅ note 为 null — None
- ✅ 响应不再出现 activityId/routeId/articleId — ['banner', 'createdAt', 'description', 'id', 'note', 'online', 'phase', 'relatedTitle', 'sortOrder', 'subtitle', 'targetId', 'title', 'type', 'updatedAt']
