# TC-featured-IT-009 断言明细

结果: ✅ 通过

契约: `FeaturedCycleItemUpsertRequest` schema 仍为 activityId/routeId/articleId 三字段，`FeaturedCycleItemResponse` 未声明（⚠️ 已知契约缺口，tasks 6.1 待做），故不做 schema 断言，仅做响应体断言。

- ✅ 创建返回 200 — 200
- ✅ Content-Type 含 application/json — application/json
- ✅ type=ARTICLE — ARTICLE
- ✅ targetId 等于该文章 id — 01a04880-e295-7a16-8a63-159af5b1c3b3
- ✅ relatedTitle 回显文章标题 — art009-g8k
- ✅ title 为提交值 — 黄体期生活法
- ✅ banner 为签名 URL — {'id': 'bound/b009.png', 'url': 'http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b009.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE'}
- ✅ subtitle/description/note 均为 null — (None, None, None)
- ✅ 响应不再出现 activityId/routeId/articleId — ['banner', 'createdAt', 'description', 'id', 'note', 'online', 'phase', 'relatedTitle', 'sortOrder', 'subtitle', 'targetId', 'title', 'type', 'updatedAt']
