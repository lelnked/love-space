# 追溯矩阵（交付核对）：ai-scene-thinking-toggle

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change ai-scene-thinking-toggle`

## 需求与场景
- **ai/业务场景管理**: 创建场景 / sceneId 重复被拒绝 / sceneId 格式非法 / 更新不改 sceneId / 按关键字与状态筛选 / 禁用场景
- **ai/场景模型配置**: 配置主备模型 / 模型不在白名单 / 主备模型相同 / 清除备用模型 / 开启深度思考 / 未传深度思考视为关闭
- **ai/模型配置页面**: 未配置主模型提示 / 测试连接显示结果 / 保存模型配置 / 配置深度思考
- **ai/生成记录与调用明细落库**: 成功生成写完整记录 / 重试与切备用逐条记明细 / 选填变量未传记 null
- **ai/生成调用深度思考参数**: 关闭时显式传 disabled / 开启时传 enabled 且切备用沿用 / 连接测试不传 thinking

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-ai-IT-002 | POST /api/admin/ai/scenes 创建场景成功 | ai/业务场景管理#创建场景 | api-spec.json#/paths/~1api~1admin~1ai~1scenes/post | ai-scene-thinking-toggle | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-002/` | ✅ |
| TC-ai-IT-003 | POST /api/admin/ai/scenes sceneId 重复返回 409 | ai/业务场景管理#sceneId 重复被拒绝 | api-spec.json#/paths/~1api~1admin~1ai~1scenes/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-003/` | ✅ |
| TC-ai-IT-004 | POST /api/admin/ai/scenes sceneId 格式与长度边界校验 | ai/业务场景管理#sceneId 格式非法 | api-spec.json#/paths/~1api~1admin~1ai~1scenes/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-004/` | ✅ |
| TC-ai-IT-005 | PUT /api/admin/ai/scenes/{id} 更新名称且忽略 body 中 sceneId | ai/业务场景管理#更新不改 sceneId | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}/put | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-005/` | ✅ |
| TC-ai-IT-006 | GET /api/admin/ai/scenes/page 按关键字与状态筛选 | ai/业务场景管理#按关键字与状态筛选 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1page/get | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-006/` | ✅ |
| TC-ai-IT-007 | GET /api/admin/ai/scenes/options 全量下拉与详情 404 | ai/业务场景管理#创建场景 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1options/get | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-007/` | ✅ |
| TC-ai-IT-008 | PUT /api/admin/ai/scenes/{id}/enabled 禁用场景 | ai/业务场景管理#禁用场景 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1enabled/put | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-008/` | ✅ |
| TC-ai-IT-009 | PUT /api/admin/ai/scenes/{id}/models 配置主备模型 | ai/场景模型配置#配置主备模型 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put | ai-management | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-009/` | ✅ |
| TC-ai-IT-010 | PUT /api/admin/ai/scenes/{id}/models 模型不在白名单返回 400 | ai/场景模型配置#模型不在白名单 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put | ai-management | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-010/` | ✅ |
| TC-ai-IT-011 | PUT /api/admin/ai/scenes/{id}/models 主备相同返回 400 | ai/场景模型配置#主备模型相同 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put | ai-management | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-011/` | ✅ |
| TC-ai-IT-012 | PUT /api/admin/ai/scenes/{id}/models 清除备用模型 | ai/场景模型配置#清除备用模型 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put | ai-management | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-012/` | ✅ |
| TC-ai-IT-013 | POST /api/admin/ai/models/test-connection 连接测试 | ai/生成重试、切备用与结果判定#连接测试 | api-spec.json#/paths/~1api~1admin~1ai~1models~1test-connection/post | ai-management | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-013/` | ✅ |
| TC-ai-IT-014 | POST /api/admin/ai/models/test-connection 模型不在白名单返回 400 | ai/场景模型配置#模型不在白名单 | api-spec.json#/paths/~1api~1admin~1ai~1models~1test-connection/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-014/` | ✅ |
| TC-ai-IT-023 | POST /api/admin/ai/prompt-templates/{id}/test 测试成功置已测试 | ai/提示词模板测试#测试成功置已测试 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post | ai-management | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-023/` | ✅ |
| TC-ai-IT-038 | POST /api/app/ai/generations 生成成功 | ai/App 生成接口#生成成功 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-038/` | ✅ |
| TC-ai-IT-039 | 生成成功写完整生成记录与调用明细 | ai/生成记录与调用明细落库#成功生成写完整记录 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-039/` | ✅ |
| TC-ai-IT-040 | 生成记录请求快照不含密钥与认证头 | ai/AI 调用配置来自环境变量#请求快照不含密钥 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-scene-thinking-toggle | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-040/` | ✅ |
| TC-ai-IT-048 | 重试与切备用逐条写调用明细 | ai/生成记录与调用明细落库#重试与切备用逐条记明细 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-048/` | ✅ |
| TC-ai-IT-049 | 选填变量未传时生成记录 inputs 记 null | ai/生成记录与调用明细落库#选填变量未传记 null | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-049/` | ✅ |
| TC-ai-IT-075 | GET /api/admin/ai/generations/{id} 详情含调用明细且查看不留痕 | ai/生成记录详情#详情含调用明细 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}/get | ai-generation-records | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-075/` | ✅ |
| TC-ai-IT-098 | PUT /api/admin/ai/scenes/{id}/models 开启深度思考 | ai/场景模型配置#开启深度思考 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put | ai-scene-thinking-toggle | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-098/` | ✅ |
| TC-ai-IT-099 | PUT /api/admin/ai/scenes/{id}/models 未传或 null 的 thinkingEnabled 视为关闭 | ai/场景模型配置#未传深度思考视为关闭 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put | ai-scene-thinking-toggle | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-099/` | ✅ |
| TC-ai-IT-100 | 场景关闭深度思考时生成参数快照 thinkingEnabled=false | ai/生成调用深度思考参数#关闭时显式传 disabled | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}/get | ai-scene-thinking-toggle | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-100/` | ✅ |
| TC-ai-IT-101 | 场景开启深度思考且切备用时参数快照 thinkingEnabled=true | ai/生成调用深度思考参数#开启时传 enabled 且切备用沿用 | api-spec.json#/paths/~1api~1admin~1ai~1generations~1{id}/get | ai-scene-thinking-toggle | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-101/` | ✅ |
| TC-ai-IT-102 | POST /api/admin/ai/models/test-connection 场景开启深度思考时连接测试行为不变 | ai/生成调用深度思考参数#连接测试不传 thinking | api-spec.json#/paths/~1api~1admin~1ai~1models~1test-connection/post | ai-scene-thinking-toggle | IT | `test-evidence/ai-scene-thinking-toggle/TC-ai-IT-102/` | ✅ |
| TC-ai-WEB-006 | 模型配置页未配置主模型提示 | ai/模型配置页面#未配置主模型提示 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-006/` | ✅ |
| TC-ai-WEB-007 | 配置弹窗测试连接显示结果 | ai/模型配置页面#测试连接显示结果 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-007/` | ✅ |
| TC-ai-WEB-008 | 保存模型配置（备用不使用）及主备相同提示 | ai/模型配置页面#保存模型配置 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-008/` | ✅ |
| TC-ai-WEB-034 | 配置弹窗开启深度思考并保存回显 | ai/模型配置页面#配置深度思考 | - | ai-scene-thinking-toggle | WEB | `test-evidence/ai-scene-thinking-toggle/TC-ai-WEB-034/` | ⬜ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：29
- ✅ 通过：28 (96.6%)
- ❌ 失败：0
- ⬜ 未测：1
