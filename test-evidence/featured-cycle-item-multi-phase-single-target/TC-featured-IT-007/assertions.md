# TC-featured-IT-007 断言明细

执行日期: 2026-09-02

- ✅ 创建返回 200　— 实际 200
- ✅ Content-Type 含 application/json　— application/json
- ✅ 详情返回 200　— 实际 200
- ✅ phases 为 JSON 数组 ["MENSTRUAL"]　— ['MENSTRUAL']
- ✅ 响应不再出现单值 phase 字段　— None
- ✅ type=ACTIVITY　— ACTIVITY
- ✅ targetId 等于该活动 id
- ✅ relatedTitle 回显活动标题　— act-m9p1
- ✅ description 原样
- ✅ note 原样
- ✅ banner 为签名 URL（http 开头、非裸 objectKey）　— http://love-space-test-0524.oss-cn-hangz
- ✅ sortOrder=1　— 1
- ✅ online=false（未传默认下线）　— False
- ✅ title/subtitle 为 null
- ✅ 响应无 activityId/routeId/articleId
- ✅ 请求体满足契约 required(phases,type,banner,targetId)

结论: ✅ 通过（16/16 断言通过）
