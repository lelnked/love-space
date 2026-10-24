# 追溯矩阵（交付核对）：ai-management

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change ai-management`

## 需求与场景
- **ai/AI 管理写操作留痕**: 发布留痕 / 测试不留痕
- **ai/AI 管理权限与菜单**: MEMBER 可访问 AI 管理 / 未登录被拒绝 / 侧栏菜单
- **ai/AI 调用配置来自环境变量**: 模型下拉来自环境变量 / 请求快照不含密钥
- **ai/App 生成接口**: 生成成功 / 已处理的失败返回 200 / 场景不存在 / 场景不可用 / 必填变量缺失 / 缺少匿名 ID / API Key 无效 / 响应不含调用明细
- **ai/业务场景管理**: 创建场景 / sceneId 重复被拒绝 / sceneId 格式非法 / 更新不改 sceneId / 按关键字与状态筛选 / 禁用场景
- **ai/业务配置页面**: 新增场景弹窗 / 编辑时 scene_id 只读 / 重复 scene_id 提示 / 禁用需确认
- **ai/场景模型配置**: 配置主备模型 / 模型不在白名单 / 主备模型相同 / 清除备用模型
- **ai/提示词变量校验与渲染**: 未声明变量被指出位置 / 必填变量缺失渲染失败 / 选填变量缺失替换为空串 / 非字符串值按 JSON 文本代入 / 变量名非法或重复为声明错误
- **ai/提示词模板发布**: 发布切换生效模板 / 发布旧版本即回退 / 未测试不能发布 / 禁用场景也可发布
- **ai/提示词模板测试**: 测试成功置已测试 / 测试失败保留未测试 / 存在未声明变量拒绝测试 / 必填变量缺失拒绝测试 / 场景未配主模型拒绝测试 / 测试不写生成记录
- **ai/提示词模板维护**: 新建模板自动分配 seq / 引用未声明变量也可保存 / 变量名非法被拒绝 / 修改已测试模板重置测试标记 / 生效中模板不能修改 / 版本冲突 / 模板列表按 seq 倒序
- **ai/提示词管理页面**: 场景列表显示未发布 / 对比按钮需勾选 2~3 个 / 未测试模板发布置灰 / 发布二次确认后切换生效 / 复制为新模板
- **ai/模型配置页面**: 未配置主模型提示 / 测试连接显示结果 / 保存模型配置
- **ai/模板对比页面**: 并排对比 / ids 数量非法
- **ai/模板编辑与测试页面**: 未声明变量实时提示 / 运行测试前自动保存 / 生效中模板只读可测试 / 未测试时发布置灰
- **ai/生成接口 IP 限流**: 同一 IP 突发超限 / 非生成接口不受影响
- **ai/生成接口每日额度**: 匿名 ID 超出每日次数 / 全站超出每日总量 / 昨日记录不计入 / 去重不占额度
- **ai/生成接口输入长度限制**: 单个变量超长 / 边界值通过 / 整体超长
- **ai/生成记录与调用明细落库**: 成功生成写完整记录 / 重试与切备用逐条记明细 / 选填变量未传记 null
- **ai/生成请求去重**: 重复提交返回原结果 / 生成中重复提交 / PROCESSING 超时就地失败 / clientRequestId 冲突 / 重复提交不受场景状态影响
- **ai/生成重试、切备用与结果判定**: 主模型首次成功 / 主模型失败后重试成功 / 主模型耗尽后切备用 / 无备用模型时全部失败 / 代码块包裹的 JSON 视为成功 / 各失败状态判定 / 连接测试
- **operation-log/运营写操作留痕**: 创建城市后异步留痕 / 业务方法失败时不留痕 / 登录不产生日志 / AI 场景创建留痕 / AI 测试类调用不留痕

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-ai-IT-001 | GET /api/admin/ai/models 返回环境变量配置的模型列表 | ai/AI 调用配置来自环境变量#模型下拉来自环境变量 | api-spec.json#/paths/~1api~1admin~1ai~1models/get | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-001/` | ✅ |
| TC-ai-IT-002 | POST /api/admin/ai/scenes 创建场景成功 | ai/业务场景管理#创建场景 | api-spec.json#/paths/~1api~1admin~1ai~1scenes/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-002/` | ✅ |
| TC-ai-IT-003 | POST /api/admin/ai/scenes sceneId 重复返回 409 | ai/业务场景管理#sceneId 重复被拒绝 | api-spec.json#/paths/~1api~1admin~1ai~1scenes/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-003/` | ✅ |
| TC-ai-IT-004 | POST /api/admin/ai/scenes sceneId 格式与长度边界校验 | ai/业务场景管理#sceneId 格式非法 | api-spec.json#/paths/~1api~1admin~1ai~1scenes/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-004/` | ✅ |
| TC-ai-IT-005 | PUT /api/admin/ai/scenes/{id} 更新名称且忽略 body 中 sceneId | ai/业务场景管理#更新不改 sceneId | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}/put | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-005/` | ✅ |
| TC-ai-IT-006 | GET /api/admin/ai/scenes/page 按关键字与状态筛选 | ai/业务场景管理#按关键字与状态筛选 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1page/get | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-006/` | ✅ |
| TC-ai-IT-007 | GET /api/admin/ai/scenes/options 全量下拉与详情 404 | ai/业务场景管理#创建场景 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1options/get | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-007/` | ✅ |
| TC-ai-IT-008 | PUT /api/admin/ai/scenes/{id}/enabled 禁用场景 | ai/业务场景管理#禁用场景 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1enabled/put | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-008/` | ✅ |
| TC-ai-IT-009 | PUT /api/admin/ai/scenes/{id}/models 配置主备模型 | ai/场景模型配置#配置主备模型 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-009/` | ✅ |
| TC-ai-IT-010 | PUT /api/admin/ai/scenes/{id}/models 模型不在白名单返回 400 | ai/场景模型配置#模型不在白名单 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-010/` | ✅ |
| TC-ai-IT-011 | PUT /api/admin/ai/scenes/{id}/models 主备相同返回 400 | ai/场景模型配置#主备模型相同 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-011/` | ✅ |
| TC-ai-IT-012 | PUT /api/admin/ai/scenes/{id}/models 清除备用模型 | ai/场景模型配置#清除备用模型 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1{id}~1models/put | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-012/` | ✅ |
| TC-ai-IT-013 | POST /api/admin/ai/models/test-connection 连接测试 | ai/生成重试、切备用与结果判定#连接测试 | api-spec.json#/paths/~1api~1admin~1ai~1models~1test-connection/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-013/` | ✅ |
| TC-ai-IT-014 | POST /api/admin/ai/models/test-connection 模型不在白名单返回 400 | ai/场景模型配置#模型不在白名单 | api-spec.json#/paths/~1api~1admin~1ai~1models~1test-connection/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-014/` | ✅ |
| TC-ai-IT-015 | POST /api/admin/ai/prompt-templates 新建模板自动分配 seq | ai/提示词模板维护#新建模板自动分配 seq | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-015/` | ✅ |
| TC-ai-IT-016 | POST /api/admin/ai/prompt-templates 引用未声明变量也可保存 | ai/提示词模板维护#引用未声明变量也可保存 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-016/` | ✅ |
| TC-ai-IT-017 | POST /api/admin/ai/prompt-templates 变量声明非法或重复返回 400 | ai/提示词模板维护#变量名非法被拒绝 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-017/` | ✅ |
| TC-ai-IT-018 | POST /api/admin/ai/prompt-templates 场景不存在返回 404 | ai/提示词模板维护#新建模板自动分配 seq | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-018/` | ✅ |
| TC-ai-IT-019 | PUT /api/admin/ai/prompt-templates/{id} 修改已测试模板重置 tested | ai/提示词模板维护#修改已测试模板重置测试标记 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}/put | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-019/` | ✅ |
| TC-ai-IT-020 | PUT /api/admin/ai/prompt-templates/{id} 生效中模板不能修改 | ai/提示词模板维护#生效中模板不能修改 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}/put | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-020/` | ✅ |
| TC-ai-IT-021 | PUT /api/admin/ai/prompt-templates/{id} 版本冲突返回 409 | ai/提示词模板维护#版本冲突 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}/put | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-021/` | ✅ |
| TC-ai-IT-022 | GET /api/admin/ai/prompt-templates/page 按 seq 倒序且仅生效项 active | ai/提示词模板维护#模板列表按 seq 倒序 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1page/get | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-022/` | ✅ |
| TC-ai-IT-023 | POST /api/admin/ai/prompt-templates/{id}/test 测试成功置已测试 | ai/提示词模板测试#测试成功置已测试 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-023/` | ✅ |
| TC-ai-IT-024 | POST /api/admin/ai/prompt-templates/{id}/test 模型返回非 JSON 测试失败 | ai/提示词模板测试#测试失败保留未测试 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-024/` | ✅ |
| TC-ai-IT-025 | POST /api/admin/ai/prompt-templates/{id}/test 存在未声明变量返回 400 | ai/提示词模板测试#存在未声明变量拒绝测试 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-025/` | ✅ |
| TC-ai-IT-026 | POST /api/admin/ai/prompt-templates/{id}/test 必填变量缺失返回 400 | ai/提示词模板测试#必填变量缺失拒绝测试 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-026/` | ✅ |
| TC-ai-IT-027 | POST /api/admin/ai/prompt-templates/{id}/test 场景未配主模型返回 409 | ai/提示词模板测试#场景未配主模型拒绝测试 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-027/` | ✅ |
| TC-ai-IT-028 | POST /api/admin/ai/prompt-templates/{id}/test 不写生成记录 | ai/提示词模板测试#测试不写生成记录 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-028/` | ✅ |
| TC-ai-IT-029 | POST /api/admin/ai/prompt-templates/{id}/publish 发布切换生效模板 | ai/提示词模板发布#发布切换生效模板 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1publish/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-029/` | ✅ |
| TC-ai-IT-030 | POST /api/admin/ai/prompt-templates/{id}/publish 发布旧版本即回退 | ai/提示词模板发布#发布旧版本即回退 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1publish/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-030/` | ✅ |
| TC-ai-IT-031 | POST /api/admin/ai/prompt-templates/{id}/publish 未测试不能发布 | ai/提示词模板发布#未测试不能发布 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1publish/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-031/` | ✅ |
| TC-ai-IT-032 | POST /api/admin/ai/prompt-templates/{id}/publish 禁用场景也可发布 | ai/提示词模板发布#禁用场景也可发布 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1publish/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-032/` | ✅ |
| TC-ai-IT-033 | POST /api/admin/ai/prompt-templates/{id}/publish 版本冲突返回 409 | ai/提示词模板发布#发布切换生效模板 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1publish/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-033/` | ✅ |
| TC-ai-IT-034 | 发布模板产生 ai-prompt:publish 留痕 | ai/AI 管理写操作留痕#发布留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-034/` | ✅ |
| TC-ai-IT-035 | 测试模板不产生留痕 | ai/AI 管理写操作留痕#测试不留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-035/` | ✅ |
| TC-ai-IT-036 | MEMBER 角色可访问 AI 管理接口 | ai/AI 管理权限与菜单#MEMBER 可访问 AI 管理 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1page/get | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-036/` | ✅ |
| TC-ai-IT-037 | 未登录访问 AI 管理接口返回 401 | ai/AI 管理权限与菜单#未登录被拒绝 | api-spec.json#/paths/~1api~1admin~1ai~1scenes~1page/get | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-037/` | ✅ |
| TC-ai-IT-038 | POST /api/app/ai/generations 生成成功 | ai/App 生成接口#生成成功 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-038/` | ✅ |
| TC-ai-IT-039 | 生成成功写完整生成记录与调用明细 | ai/生成记录与调用明细落库#成功生成写完整记录 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-039/` | ✅ |
| TC-ai-IT-040 | 生成记录请求快照不含密钥与认证头 | ai/AI 调用配置来自环境变量#请求快照不含密钥 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-040/` | ✅ |
| TC-ai-IT-041 | POST /api/app/ai/generations 已处理的失败返回 200 | ai/App 生成接口#已处理的失败返回 200 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-041/` | ✅ |
| TC-ai-IT-042 | POST /api/app/ai/generations 场景不存在返回 404 | ai/App 生成接口#场景不存在 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-042/` | ✅ |
| TC-ai-IT-043 | POST /api/app/ai/generations 场景不可用返回 409 且不写记录 | ai/App 生成接口#场景不可用 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-043/` | ✅ |
| TC-ai-IT-044 | POST /api/app/ai/generations 必填变量缺失返回 400 且不写记录 | ai/App 生成接口#必填变量缺失 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-044/` | ✅ |
| TC-ai-IT-045 | POST /api/app/ai/generations 请求头与 body 参数校验 | ai/App 生成接口#缺少匿名 ID | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-045/` | ✅ |
| TC-ai-IT-046 | POST /api/app/ai/generations API Key 无效返回 401 | ai/App 生成接口#API Key 无效 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-046/` | ✅ |
| TC-ai-IT-047 | POST /api/app/ai/generations 响应不含调用明细 | ai/App 生成接口#响应不含调用明细 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-047/` | ✅ |
| TC-ai-IT-048 | 重试与切备用逐条写调用明细 | ai/生成记录与调用明细落库#重试与切备用逐条记明细 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-048/` | ✅ |
| TC-ai-IT-049 | 选填变量未传时生成记录 inputs 记 null | ai/生成记录与调用明细落库#选填变量未传记 null | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-049/` | ✅ |
| TC-ai-IT-050 | 重复提交返回原结果且不再调用模型 | ai/生成请求去重#重复提交返回原结果 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-050/` | ✅ |
| TC-ai-IT-051 | 生成中重复提交返回 409 | ai/生成请求去重#生成中重复提交 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-051/` | ✅ |
| TC-ai-IT-052 | PROCESSING 超时记录就地改为 TIMEOUT | ai/生成请求去重#PROCESSING 超时就地失败 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-052/` | ✅ |
| TC-ai-IT-053 | clientRequestId 被不同匿名 ID 复用返回 409 | ai/生成请求去重#clientRequestId 冲突 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-053/` | ✅ |
| TC-ai-IT-054 | 重复提交不受场景禁用影响 | ai/生成请求去重#重复提交不受场景状态影响 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-054/` | ✅ |
| TC-ai-IT-055 | 匿名 ID 超出每日次数返回 429 | ai/生成接口每日额度#匿名 ID 超出每日次数 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-055/` | ✅ |
| TC-ai-IT-056 | 全站超出每日总量返回 429 | ai/生成接口每日额度#全站超出每日总量 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-056/` | ✅ |
| TC-ai-IT-057 | 北京时间昨日记录不计入今日额度 | ai/生成接口每日额度#昨日记录不计入 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-057/` | ✅ |
| TC-ai-IT-058 | 去重命中不占用额度 | ai/生成接口每日额度#去重不占额度 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-058/` | ✅ |
| TC-ai-IT-059 | 单个变量 501 字符返回 400 | ai/生成接口输入长度限制#单个变量超长 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-059/` | ✅ |
| TC-ai-IT-060 | 单个变量恰 500 字符通过长度校验 | ai/生成接口输入长度限制#边界值通过 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-060/` | ✅ |
| TC-ai-IT-061 | inputs 整体超过 4KB 返回 400 | ai/生成接口输入长度限制#整体超长 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-061/` | ✅ |
| TC-ai-IT-062 | nginx 同一 IP 突发超限返回 429 | ai/生成接口 IP 限流#同一 IP 突发超限 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-management | IT | - | ⬜ |
| TC-ai-IT-063 | nginx 限流不影响非生成接口 | ai/生成接口 IP 限流#非生成接口不受影响 | api-spec.json#/paths/~1api~1app~1cities/get | ai-management | IT | - | ⬜ |
| TC-ai-IT-064 | 测试期间模板被他人修改则结果不写回 | ai/提示词模板测试#测试成功置已测试 | api-spec.json#/paths/~1api~1admin~1ai~1prompt-templates~1{id}~1test/post | ai-management | IT | `test-evidence/ai-management/TC-ai-IT-064/` | ✅ |
| TC-ai-WEB-001 | 侧栏「AI 管理」菜单及子菜单顺序 | ai/AI 管理权限与菜单#侧栏菜单 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-001/` | ⬜ |
| TC-ai-WEB-002 | 业务配置页新增场景弹窗 | ai/业务配置页面#新增场景弹窗 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-002/` | ⬜ |
| TC-ai-WEB-003 | 编辑场景时 scene_id 只读 | ai/业务配置页面#编辑时 scene_id 只读 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-003/` | ⬜ |
| TC-ai-WEB-004 | 重复 scene_id 保存提示 | ai/业务配置页面#重复 scene_id 提示 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-004/` | ⬜ |
| TC-ai-WEB-005 | 禁用场景需二次确认 | ai/业务配置页面#禁用需确认 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-005/` | ⬜ |
| TC-ai-WEB-006 | 模型配置页未配置主模型提示 | ai/模型配置页面#未配置主模型提示 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-006/` | ⬜ |
| TC-ai-WEB-007 | 配置弹窗测试连接显示结果 | ai/模型配置页面#测试连接显示结果 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-007/` | ⬜ |
| TC-ai-WEB-008 | 保存模型配置（备用不使用）及主备相同提示 | ai/模型配置页面#保存模型配置 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-008/` | ⬜ |
| TC-ai-WEB-009 | 提示词场景列表显示未发布 | ai/提示词管理页面#场景列表显示未发布 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-009/` | ⬜ |
| TC-ai-WEB-010 | 模板列表对比按钮需勾选 2~3 个 | ai/提示词管理页面#对比按钮需勾选 2~3 个 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-010/` | ⬜ |
| TC-ai-WEB-011 | 未测试模板发布按钮置灰 | ai/提示词管理页面#未测试模板发布置灰 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-011/` | ⬜ |
| TC-ai-WEB-012 | 发布二次确认后切换生效 | ai/提示词管理页面#发布二次确认后切换生效 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-012/` | ⬜ |
| TC-ai-WEB-013 | 复制为新模板 | ai/提示词管理页面#复制为新模板 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-013/` | ⬜ |
| TC-ai-WEB-014 | 编辑页未声明变量实时提示 | ai/模板编辑与测试页面#未声明变量实时提示 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-014/` | ⬜ |
| TC-ai-WEB-015 | 运行测试前自动保存 | ai/模板编辑与测试页面#运行测试前自动保存 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-015/` | ⬜ |
| TC-ai-WEB-016 | 生效中模板只读可测试 | ai/模板编辑与测试页面#生效中模板只读可测试 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-016/` | ⬜ |
| TC-ai-WEB-017 | 未测试时底部发布置灰 | ai/模板编辑与测试页面#未测试时发布置灰 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-017/` | ⬜ |
| TC-ai-WEB-018 | 并排对比两个模板 | ai/模板对比页面#并排对比 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-018/` | ⬜ |
| TC-ai-WEB-019 | 对比页 ids 数量非法 | ai/模板对比页面#ids 数量非法 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-019/` | ⬜ |
| TC-ai-WEB-020 | 保存版本冲突不覆盖本地编辑 | ai/模板编辑与测试页面#运行测试前自动保存 | - | ai-management | WEB | `test-evidence/regression/ai/TC-ai-WEB-020/` | ⬜ |
| TC-operation-log-IT-001 | 创建城市后异步产生 city:create 留痕 | operation-log/运营写操作留痕#创建城市后异步留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-management/TC-operation-log-IT-001/` | ✅ |
| TC-operation-log-IT-002 | 业务校验失败（400）时不产生留痕 | operation-log/运营写操作留痕#业务方法失败时不留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-management/TC-operation-log-IT-002/` | ✅ |
| TC-operation-log-IT-003 | 登录不产生 auth:login 日志 | operation-log/运营写操作留痕#登录不产生日志 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/ai-management/TC-operation-log-IT-003/` | ✅ |
| TC-operation-log-IT-015 | 创建 AI 场景后异步产生 ai-scene:create 留痕 | operation-log/运营写操作留痕#AI 场景创建留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-management/TC-operation-log-IT-015/` | ✅ |
| TC-operation-log-IT-016 | AI 测试连接与模板测试不产生留痕 | operation-log/运营写操作留痕#AI 测试类调用不留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | ai-management | IT | `test-evidence/ai-management/TC-operation-log-IT-016/` | ✅ |

## 覆盖核对

- ⚠ 未覆盖：ai/生成接口 IP 限流#同一 IP 突发超限 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：ai/生成接口 IP 限流#非生成接口不受影响 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/运营写操作留痕#业务方法失败时不留痕 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/运营写操作留痕#登录不产生日志 无 WEB/APP 用例且无 UT(@scenario) 覆盖

## 测试统计
- 总数：89
- ✅ 通过：67 (75.3%)
- ❌ 失败：0
- ⬜ 未测：22
