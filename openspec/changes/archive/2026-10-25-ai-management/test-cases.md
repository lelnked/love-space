# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/ai/{it,web}.md`（新域 ai，端 web）与 `tests/operation-log/it.md`（living 文件，runner 独占回写状态）。

## 新增用例

### ai IT（`tests/ai/it.md`，64 条）

- TC-ai-IT-001: GET /api/admin/ai/models 返回环境变量配置的模型列表（ADDED Scenario: ai/AI 调用配置来自环境变量#模型下拉来自环境变量）
- TC-ai-IT-002: POST /api/admin/ai/scenes 创建场景成功（ADDED Scenario: ai/业务场景管理#创建场景）
- TC-ai-IT-003: POST /api/admin/ai/scenes sceneId 重复返回 409（ADDED Scenario: ai/业务场景管理#sceneId 重复被拒绝）
- TC-ai-IT-004: POST /api/admin/ai/scenes sceneId 格式与长度边界校验（ADDED Scenario: ai/业务场景管理#sceneId 格式非法）
- TC-ai-IT-005: PUT /api/admin/ai/scenes/{id} 更新名称且忽略 body 中 sceneId（ADDED Scenario: ai/业务场景管理#更新不改 sceneId）
- TC-ai-IT-006: GET /api/admin/ai/scenes/page 按关键字与状态筛选（ADDED Scenario: ai/业务场景管理#按关键字与状态筛选）
- TC-ai-IT-007: GET /api/admin/ai/scenes/options 全量下拉与详情 404（ADDED Scenario: ai/业务场景管理#创建场景）
- TC-ai-IT-008: PUT /api/admin/ai/scenes/{id}/enabled 禁用场景（ADDED Scenario: ai/业务场景管理#禁用场景）
- TC-ai-IT-009: PUT /api/admin/ai/scenes/{id}/models 配置主备模型（ADDED Scenario: ai/场景模型配置#配置主备模型）
- TC-ai-IT-010: PUT /api/admin/ai/scenes/{id}/models 模型不在白名单返回 400（ADDED Scenario: ai/场景模型配置#模型不在白名单）
- TC-ai-IT-011: PUT /api/admin/ai/scenes/{id}/models 主备相同返回 400（ADDED Scenario: ai/场景模型配置#主备模型相同）
- TC-ai-IT-012: PUT /api/admin/ai/scenes/{id}/models 清除备用模型（ADDED Scenario: ai/场景模型配置#清除备用模型）
- TC-ai-IT-013: POST /api/admin/ai/models/test-connection 连接测试（ADDED Scenario: ai/生成重试、切备用与结果判定#连接测试）
- TC-ai-IT-014: POST /api/admin/ai/models/test-connection 模型不在白名单返回 400（ADDED Scenario: ai/场景模型配置#模型不在白名单）
- TC-ai-IT-015: POST /api/admin/ai/prompt-templates 新建模板自动分配 seq（ADDED Scenario: ai/提示词模板维护#新建模板自动分配 seq）
- TC-ai-IT-016: POST /api/admin/ai/prompt-templates 引用未声明变量也可保存（ADDED Scenario: ai/提示词模板维护#引用未声明变量也可保存）
- TC-ai-IT-017: POST /api/admin/ai/prompt-templates 变量声明非法或重复返回 400（ADDED Scenario: ai/提示词模板维护#变量名非法被拒绝）
- TC-ai-IT-018: POST /api/admin/ai/prompt-templates 场景不存在返回 404（ADDED Scenario: ai/提示词模板维护#新建模板自动分配 seq）
- TC-ai-IT-019: PUT /api/admin/ai/prompt-templates/{id} 修改已测试模板重置 tested（ADDED Scenario: ai/提示词模板维护#修改已测试模板重置测试标记）
- TC-ai-IT-020: PUT /api/admin/ai/prompt-templates/{id} 生效中模板不能修改（ADDED Scenario: ai/提示词模板维护#生效中模板不能修改）
- TC-ai-IT-021: PUT /api/admin/ai/prompt-templates/{id} 版本冲突返回 409（ADDED Scenario: ai/提示词模板维护#版本冲突）
- TC-ai-IT-022: GET /api/admin/ai/prompt-templates/page 按 seq 倒序且仅生效项 active（ADDED Scenario: ai/提示词模板维护#模板列表按 seq 倒序）
- TC-ai-IT-023: POST /api/admin/ai/prompt-templates/{id}/test 测试成功置已测试（ADDED Scenario: ai/提示词模板测试#测试成功置已测试）
- TC-ai-IT-024: POST /api/admin/ai/prompt-templates/{id}/test 模型返回非 JSON 测试失败（ADDED Scenario: ai/提示词模板测试#测试失败保留未测试）
- TC-ai-IT-025: POST /api/admin/ai/prompt-templates/{id}/test 存在未声明变量返回 400（ADDED Scenario: ai/提示词模板测试#存在未声明变量拒绝测试）
- TC-ai-IT-026: POST /api/admin/ai/prompt-templates/{id}/test 必填变量缺失返回 400（ADDED Scenario: ai/提示词模板测试#必填变量缺失拒绝测试）
- TC-ai-IT-027: POST /api/admin/ai/prompt-templates/{id}/test 场景未配主模型返回 409（ADDED Scenario: ai/提示词模板测试#场景未配主模型拒绝测试）
- TC-ai-IT-028: POST /api/admin/ai/prompt-templates/{id}/test 不写生成记录（ADDED Scenario: ai/提示词模板测试#测试不写生成记录）
- TC-ai-IT-029: POST /api/admin/ai/prompt-templates/{id}/publish 发布切换生效模板（ADDED Scenario: ai/提示词模板发布#发布切换生效模板）
- TC-ai-IT-030: POST /api/admin/ai/prompt-templates/{id}/publish 发布旧版本即回退（ADDED Scenario: ai/提示词模板发布#发布旧版本即回退）
- TC-ai-IT-031: POST /api/admin/ai/prompt-templates/{id}/publish 未测试不能发布（ADDED Scenario: ai/提示词模板发布#未测试不能发布）
- TC-ai-IT-032: POST /api/admin/ai/prompt-templates/{id}/publish 禁用场景也可发布（ADDED Scenario: ai/提示词模板发布#禁用场景也可发布）
- TC-ai-IT-033: POST /api/admin/ai/prompt-templates/{id}/publish 版本冲突返回 409（ADDED Scenario: ai/提示词模板发布#发布切换生效模板）
- TC-ai-IT-034: 发布模板产生 ai-prompt:publish 留痕（ADDED Scenario: ai/AI 管理写操作留痕#发布留痕）
- TC-ai-IT-035: 测试模板不产生留痕（ADDED Scenario: ai/AI 管理写操作留痕#测试不留痕）
- TC-ai-IT-036: MEMBER 角色可访问 AI 管理接口（ADDED Scenario: ai/AI 管理权限与菜单#MEMBER 可访问 AI 管理）
- TC-ai-IT-037: 未登录访问 AI 管理接口返回 401（ADDED Scenario: ai/AI 管理权限与菜单#未登录被拒绝）
- TC-ai-IT-038: POST /api/app/ai/generations 生成成功（ADDED Scenario: ai/App 生成接口#生成成功）
- TC-ai-IT-039: 生成成功写完整生成记录与调用明细（ADDED Scenario: ai/生成记录与调用明细落库#成功生成写完整记录）
- TC-ai-IT-040: 生成记录请求快照不含密钥与认证头（ADDED Scenario: ai/AI 调用配置来自环境变量#请求快照不含密钥）
- TC-ai-IT-041: POST /api/app/ai/generations 已处理的失败返回 200（ADDED Scenario: ai/App 生成接口#已处理的失败返回 200）
- TC-ai-IT-042: POST /api/app/ai/generations 场景不存在返回 404（ADDED Scenario: ai/App 生成接口#场景不存在）
- TC-ai-IT-043: POST /api/app/ai/generations 场景不可用返回 409 且不写记录（ADDED Scenario: ai/App 生成接口#场景不可用）
- TC-ai-IT-044: POST /api/app/ai/generations 必填变量缺失返回 400 且不写记录（ADDED Scenario: ai/App 生成接口#必填变量缺失）
- TC-ai-IT-045: POST /api/app/ai/generations 请求头与 body 参数校验（ADDED Scenario: ai/App 生成接口#缺少匿名 ID）
- TC-ai-IT-046: POST /api/app/ai/generations API Key 无效返回 401（ADDED Scenario: ai/App 生成接口#API Key 无效）
- TC-ai-IT-047: POST /api/app/ai/generations 响应不含调用明细（ADDED Scenario: ai/App 生成接口#响应不含调用明细）
- TC-ai-IT-048: 重试与切备用逐条写调用明细（ADDED Scenario: ai/生成记录与调用明细落库#重试与切备用逐条记明细）
- TC-ai-IT-049: 选填变量未传时生成记录 inputs 记 null（ADDED Scenario: ai/生成记录与调用明细落库#选填变量未传记 null）
- TC-ai-IT-050: 重复提交返回原结果且不再调用模型（ADDED Scenario: ai/生成请求去重#重复提交返回原结果）
- TC-ai-IT-051: 生成中重复提交返回 409（ADDED Scenario: ai/生成请求去重#生成中重复提交）
- TC-ai-IT-052: PROCESSING 超时记录就地改为 TIMEOUT（ADDED Scenario: ai/生成请求去重#PROCESSING 超时就地失败）
- TC-ai-IT-053: clientRequestId 被不同匿名 ID 复用返回 409（ADDED Scenario: ai/生成请求去重#clientRequestId 冲突）
- TC-ai-IT-054: 重复提交不受场景禁用影响（ADDED Scenario: ai/生成请求去重#重复提交不受场景状态影响）
- TC-ai-IT-055: 匿名 ID 超出每日次数返回 429（ADDED Scenario: ai/生成接口每日额度#匿名 ID 超出每日次数）
- TC-ai-IT-056: 全站超出每日总量返回 429（ADDED Scenario: ai/生成接口每日额度#全站超出每日总量）
- TC-ai-IT-057: 北京时间昨日记录不计入今日额度（ADDED Scenario: ai/生成接口每日额度#昨日记录不计入）
- TC-ai-IT-058: 去重命中不占用额度（ADDED Scenario: ai/生成接口每日额度#去重不占额度）
- TC-ai-IT-059: 单个变量 501 字符返回 400（ADDED Scenario: ai/生成接口输入长度限制#单个变量超长）
- TC-ai-IT-060: 单个变量恰 500 字符通过长度校验（ADDED Scenario: ai/生成接口输入长度限制#边界值通过）
- TC-ai-IT-061: inputs 整体超过 4KB 返回 400（ADDED Scenario: ai/生成接口输入长度限制#整体超长）
- TC-ai-IT-062: nginx 同一 IP 突发超限返回 429（ADDED Scenario: ai/生成接口 IP 限流#同一 IP 突发超限）
- TC-ai-IT-063: nginx 限流不影响非生成接口（ADDED Scenario: ai/生成接口 IP 限流#非生成接口不受影响）
- TC-ai-IT-064: 测试期间模板被他人修改则结果不写回（ADDED Scenario: ai/提示词模板测试#测试成功置已测试）

### ai WEB（`tests/ai/web.md`，20 条）

- TC-ai-WEB-001: 侧栏「AI 管理」菜单及子菜单顺序（ADDED Scenario: ai/AI 管理权限与菜单#侧栏菜单）
- TC-ai-WEB-002: 业务配置页新增场景弹窗（ADDED Scenario: ai/业务配置页面#新增场景弹窗）
- TC-ai-WEB-003: 编辑场景时 scene_id 只读（ADDED Scenario: ai/业务配置页面#编辑时 scene_id 只读）
- TC-ai-WEB-004: 重复 scene_id 保存提示（ADDED Scenario: ai/业务配置页面#重复 scene_id 提示）
- TC-ai-WEB-005: 禁用场景需二次确认（ADDED Scenario: ai/业务配置页面#禁用需确认）
- TC-ai-WEB-006: 模型配置页未配置主模型提示（ADDED Scenario: ai/模型配置页面#未配置主模型提示）
- TC-ai-WEB-007: 配置弹窗测试连接显示结果（ADDED Scenario: ai/模型配置页面#测试连接显示结果）
- TC-ai-WEB-008: 保存模型配置（备用不使用）及主备相同提示（ADDED Scenario: ai/模型配置页面#保存模型配置）
- TC-ai-WEB-009: 提示词场景列表显示未发布（ADDED Scenario: ai/提示词管理页面#场景列表显示未发布）
- TC-ai-WEB-010: 模板列表对比按钮需勾选 2~3 个（ADDED Scenario: ai/提示词管理页面#对比按钮需勾选 2~3 个）
- TC-ai-WEB-011: 未测试模板发布按钮置灰（ADDED Scenario: ai/提示词管理页面#未测试模板发布置灰）
- TC-ai-WEB-012: 发布二次确认后切换生效（ADDED Scenario: ai/提示词管理页面#发布二次确认后切换生效）
- TC-ai-WEB-013: 复制为新模板（ADDED Scenario: ai/提示词管理页面#复制为新模板）
- TC-ai-WEB-014: 编辑页未声明变量实时提示（ADDED Scenario: ai/模板编辑与测试页面#未声明变量实时提示）
- TC-ai-WEB-015: 运行测试前自动保存（ADDED Scenario: ai/模板编辑与测试页面#运行测试前自动保存）
- TC-ai-WEB-016: 生效中模板只读可测试（ADDED Scenario: ai/模板编辑与测试页面#生效中模板只读可测试）
- TC-ai-WEB-017: 未测试时底部发布置灰（ADDED Scenario: ai/模板编辑与测试页面#未测试时发布置灰）
- TC-ai-WEB-018: 并排对比两个模板（ADDED Scenario: ai/模板对比页面#并排对比）
- TC-ai-WEB-019: 对比页 ids 数量非法（ADDED Scenario: ai/模板对比页面#ids 数量非法）
- TC-ai-WEB-020: 保存版本冲突不覆盖本地编辑（ADDED Scenario: ai/模板编辑与测试页面#运行测试前自动保存）

### operation-log IT（`tests/operation-log/it.md`，2 条）

- TC-operation-log-IT-015: 创建 AI 场景后异步产生 ai-scene:create 留痕（MODIFIED Requirement 新增 Scenario: operation-log/运营写操作留痕#AI 场景创建留痕）
- TC-operation-log-IT-016: AI 测试连接与模板测试不产生留痕（MODIFIED Requirement 新增 Scenario: operation-log/运营写操作留痕#AI 测试类调用不留痕）

### 由 UT 覆盖（不产 IT）

F0 `love-space-ai` 模块内行为，由模块 UT（本地假 HTTP 服务模拟方舟）覆盖，Scenario 落点为 UT `@scenario` 注释：

- ai/生成重试、切备用与结果判定#主模型失败后重试成功
- ai/生成重试、切备用与结果判定#主模型耗尽后切备用
- ai/生成重试、切备用与结果判定#主模型首次成功
- ai/生成重试、切备用与结果判定#代码块包裹的 JSON 视为成功
- ai/提示词变量校验与渲染#变量名非法或重复为声明错误
- ai/生成重试、切备用与结果判定#各失败状态判定
- ai/提示词变量校验与渲染#必填变量缺失渲染失败
- ai/生成重试、切备用与结果判定#无备用模型时全部失败
- ai/提示词变量校验与渲染#未声明变量被指出位置
- ai/提示词变量校验与渲染#选填变量缺失替换为空串
- ai/提示词变量校验与渲染#非字符串值按 JSON 文本代入

## 修改用例

（无。operation-log 既有用例 IT-001~014 中无与新口径「18 模块 67 写接口、登录与 AI 测试类调用不留痕」冲突的文字，未原地修改。）

## 需重测用例

`运营写操作留痕` Requirement 被 MODIFIED（留痕切面覆盖面扩大），既有场景行为不变，回归确认：

- TC-operation-log-IT-001: 创建城市后异步产生 city:create 留痕（operation-log/运营写操作留痕#创建城市后异步留痕）
- TC-operation-log-IT-002: 业务校验失败（400）时不产生留痕（operation-log/运营写操作留痕#业务方法失败时不留痕）
- TC-operation-log-IT-003: 登录不产生 auth:login 日志（operation-log/运营写操作留痕#登录不产生日志）

## 执行汇总

**IT（2026-10-24，/run-api-test --change ai-management）**：69 条 —— ✅ 67 / ❌ 0 / 未执行 2。
- 未执行：TC-ai-IT-062、TC-ai-IT-063（nginx IP 限流，仅测试环境 nginx 可验证，部署后手工执行）。
- 环境：admin `http://localhost:21423`（test profile）、app `http://localhost:8081`，两端指向假方舟 `scripts/mock-ark.mjs`（:18080，AI_TIMEOUT_SECONDS=3）。
- 存证：`test-evidence/ai-management/<TC完整ID>/`。

**WEB**：未执行——Playwright MCP（playwright-company）连接失败，见会话报告。
