# TC-featured-IT-007 断言明细

结果: ✅ 通过

契约: `FeaturedCycleItemUpsertRequest` schema 仍为 activityId/routeId/articleId 三字段，`FeaturedCycleItemResponse` 未声明（⚠️ 已知契约缺口，tasks 6.1 待做），故不做 schema 断言，仅做响应体断言。

- ✅ 创建返回 200 — 200
- ✅ Content-Type 含 application/json — application/json
- ✅ phase=MENSTRUAL — MENSTRUAL
- ✅ type=ACTIVITY — ACTIVITY
- ✅ targetId 等于该活动 id — 01a04880-e252-7623-b3c6-d8cb3a33352e
- ✅ relatedTitle 回显活动标题 — act007-g8k
- ✅ description 原样 — 经期慢下来
- ✅ note 原样 — 周末两日
- ✅ banner 为签名 URL（http 开头、非裸 objectKey） — {'id': 'bound/b007.png', 'url': 'http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b007.png?Expires=1787924601&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE'}
- ✅ sortOrder=1 — 1
- ✅ online=false（未传默认下线） — False
- ✅ title/subtitle 为 null — (None, None)
- ✅ 响应不再出现 activityId/routeId/articleId — ['banner', 'createdAt', 'description', 'id', 'note', 'online', 'phase', 'relatedTitle', 'sortOrder', 'subtitle', 'targetId', 'title', 'type', 'updatedAt']
