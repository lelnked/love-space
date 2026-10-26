# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/{domain}/{it,web}.md`（`ai` 域端 = web；living 文件，runner 独占回写状态）。
> 生成记录 IT 的造数口径（psql 直插 / app 生成 + 假方舟 `scripts/mock-ark.mjs`）见 `tests/ai/it.md` TC-ai-IT-065 前的说明块。

## 新增用例

### IT（tests/ai/it.md）
- TC-ai-IT-065: 不传日期默认最近 7 天（ADDED Scenario: ai/生成记录列表查询#默认最近 7 天）
- TC-ai-IT-066: 日期区间含两端整日，区间外紧邻记录不返回（ADDED Scenario: ai/生成记录列表查询#日期区间含两端整日）
- TC-ai-IT-067: 只传一端按单边过滤（ADDED Scenario: ai/生成记录列表查询#日期区间含两端整日，boundary）
- TC-ai-IT-068: 开始晚于结束被拒绝（ADDED Scenario: ai/生成记录列表查询#开始晚于结束被拒绝）
- TC-ai-IT-069: rating=NONE 仅返回未评价（ADDED Scenario: ai/生成记录列表查询#按未评价筛选）
- TC-ai-IT-070: tag=BAD_TONE 按标签包含筛选（ADDED Scenario: ai/生成记录列表查询#按问题标签筛选）
- TC-ai-IT-071: 场景 + 状态组合筛选且倒序，含 PROCESSING 状态筛选（ADDED Scenario: ai/生成记录列表查询#组合筛选与倒序）
- TC-ai-IT-072: requestId / templateId / model / anonymousUserId 精确匹配（ADDED Scenario: ai/生成记录列表查询#组合筛选与倒序）
- TC-ai-IT-073: 非法日期与枚举返回 400（ADDED Scenario: ai/生成记录列表查询#非法枚举被拒绝）
- TC-ai-IT-074: 列表条目仅十一个字段（ADDED Scenario: ai/生成记录列表查询#列表不含正文）
- TC-ai-IT-075: 详情含 INITIAL/RETRY 调用明细且查看不留痕（ADDED Scenario: ai/生成记录详情#详情含调用明细）
- TC-ai-IT-076: 未评价记录评价字段为空（ADDED Scenario: ai/生成记录详情#未评价记录的评价字段为空）
- TC-ai-IT-077: 详情不存在 404（ADDED Scenario: ai/生成记录详情#记录不存在）
- TC-ai-IT-078: 首次评价 204（ADDED Scenario: ai/生成记录人工评价#首次评价，happy）
- TC-ai-IT-079: remark 恰 500 字符与 8 个标签通过（ADDED Scenario: ai/生成记录人工评价#评价参数非法，boundary）
- TC-ai-IT-080: 再次评价整体覆盖、不保留历史（ADDED Scenario: ai/生成记录人工评价#再次评价覆盖原评价，state）
- TC-ai-IT-081: 已评价记录不能清除评价（ADDED Scenario: ai/生成记录人工评价#评价参数非法，state）
- TC-ai-IT-082: PROCESSING 记录评价返回 409（ADDED Scenario: ai/生成记录人工评价#生成中的记录不能评价，error）
- TC-ai-IT-083: FORMAT_ERROR 记录可以评价（ADDED Scenario: ai/生成记录人工评价#失败记录可以评价）
- TC-ai-IT-084: 5 种非法参数均 400 且评价不变（ADDED Scenario: ai/生成记录人工评价#评价参数非法，error）
- TC-ai-IT-085: 评价不存在记录返回 404（ADDED Scenario: ai/生成记录人工评价#评价参数非法，error——404 无独立 Scenario）
- TC-ai-IT-086: MEMBER 可评价、未登录 401（MODIFIED 新增 Scenario: ai/AI 管理权限与菜单#MEMBER 可评价生成记录）
- TC-ai-IT-087: 评价产生 ai-generation:rate 留痕，payload 含评价内容（ADDED Scenario: ai/生成记录人工评价#评价留痕）

### IT（tests/operation-log/it.md）
- TC-operation-log-IT-017: 评价 AI 生成记录产生 ai-generation:rate 留痕（MODIFIED 新增 Scenario: operation-log/运营写操作留痕#AI 生成记录评价留痕）

### WEB（tests/ai/web.md）
- TC-ai-WEB-021: 列表默认展示最近 7 天（ADDED Scenario: ai/生成记录列表页面#默认展示最近 7 天）
- TC-ai-WEB-022: 时间范围互相限制 + 重置回默认（ADDED Scenario: ai/生成记录列表页面#时间范围互相限制）
- TC-ai-WEB-023: 模板版本依赖场景、切场景清空（ADDED Scenario: ai/生成记录列表页面#模板版本依赖场景）
- TC-ai-WEB-024: 按未评价筛选回到第 1 页（ADDED Scenario: ai/生成记录列表页面#按未评价筛选）
- TC-ai-WEB-025: 点击行进入详情（ADDED Scenario: ai/生成记录列表页面#点击行进入详情）
- TC-ai-WEB-026: 未评价记录保存评价（ADDED Scenario: ai/生成记录详情页面#未评价记录保存评价）
- TC-ai-WEB-027: 修改已有评价、无评价历史（ADDED Scenario: ai/生成记录详情页面#修改已有评价）
- TC-ai-WEB-028: 生成中记录评价区置灰（ADDED Scenario: ai/生成记录详情页面#生成中记录评价区置灰）
- TC-ai-WEB-029: 展开调用原始返回（ADDED Scenario: ai/生成记录详情页面#展开调用原始返回）
- TC-ai-WEB-030: 模板链接跳转模板详情（ADDED Scenario: ai/生成记录详情页面#未评价记录保存评价——附属 Requirement 条款）

## 修改用例

- TC-ai-WEB-001: 侧栏「AI 管理」菜单及子菜单顺序（MODIFIED: ai/AI 管理权限与菜单#侧栏菜单——子菜单由 3 个增为 4 个，新增「生成记录」→ /ai/generations；来源改 ai-generation-records，定义变化需重测）

## 需重测用例

- TC-ai-WEB-001: 侧栏菜单（定义变化，见上）
- TC-ai-IT-034: 发布模板产生 ai-prompt:publish 留痕（MODIFIED Requirement: ai/AI 管理写操作留痕，行为未变）
- TC-ai-IT-035: 测试模板不产生留痕（同上）
- TC-ai-IT-036: MEMBER 可访问 AI 管理接口（MODIFIED Requirement: ai/AI 管理权限与菜单，行为未变）
- TC-ai-IT-037: 未登录访问 AI 管理接口返回 401（同上）
- TC-operation-log-IT-001: 创建城市后异步留痕（MODIFIED Requirement: operation-log/运营写操作留痕，模块/接口计数变化）
- TC-operation-log-IT-002: 业务方法失败时不留痕（同上）
- TC-operation-log-IT-015: AI 场景创建留痕（同上）
- TC-operation-log-IT-016: AI 测试类调用不留痕（同上）
- TC-ai-IT-038: App 生成成功（027 迁移给 loves_ai_generation 加列，确认 app 端写入不受影响）
- TC-ai-IT-039: 生成成功写完整记录与调用明细（同上）

## 执行汇总

<!-- runner 跑完后由编排 skill 填写：总数 / ✅ / ❌ / 未执行 -->

**IT（2026-10-25，admin `http://localhost:21423` + app `http://localhost:8081` + 假方舟 `:18080`）**：34 条，✅ 34 / ❌ 0 / ⚠️ 0 / 未执行 0；存证 `test-evidence/ai-generation-records/`。

**WEB**：未执行——Playwright MCP（playwright-company）连接失败（ECONNREFUSED），待浏览器环境恢复后 `/run-web-test --change ai-generation-records`。
