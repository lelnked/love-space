# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/ai/{it,web}.md`、`tests/operation-log/it.md`（living 文件，runner 独占回写状态）。
> 额度设置统一走 admin 后端 `PUT /api/admin/ai/limits/{key}`，用例结束恢复 `PER_ANON_DAILY=50`、`GLOBAL_DAILY=5000`；TC-ai-IT-088/089/097 与 TC-ai-WEB-031 需 psql 直接改 `loves_ai_limit`（恢复「从未修改」状态或删行）。

## 新增用例

- TC-ai-IT-088: GET /api/admin/ai/limits 初始数据（ADDED Scenario: ai/生成额度配置#初始数据）
- TC-ai-IT-089: PUT /api/admin/ai/limits/{key} 修改单项只记录该项修改人（ADDED Scenario: ai/生成额度配置#修改单项只记录该项修改人）
- TC-ai-IT-090: PUT /api/admin/ai/limits/{key} 非法取值返回 400 且数据不变（ADDED Scenario: ai/生成额度配置#非法取值被拒绝）
- TC-ai-IT-091: PUT /api/admin/ai/limits/{key} 取值边界 1 与 2147483647 通过（ADDED Requirement: ai/生成额度配置，boundary）
- TC-ai-IT-092: PUT /api/admin/ai/limits/{key} 不存在的限制返回 404（ADDED Scenario: ai/生成额度配置#不存在的限制）
- TC-ai-IT-093: MEMBER 角色可查看与修改限流配置，未登录返回 401（MODIFIED Scenario: ai/AI 管理权限与菜单#MEMBER 可修改限流配置）
- TC-ai-IT-094: 修改限流配置产生 ai-limit:update 留痕（MODIFIED Scenario: ai/AI 管理写操作留痕#修改限流配置留痕）
- TC-ai-IT-095: 两项额度均为 -1 时不做额度检查（MODIFIED Scenario: ai/生成接口每日额度#-1 不限制）
- TC-ai-IT-096: 后台修改额度后下一次请求即生效（MODIFIED Scenario: ai/生成接口每日额度#修改即时生效）
- TC-ai-IT-097: 配置行缺失时生成接口返回 500 且不写记录（MODIFIED Scenario: ai/生成接口每日额度#配置行缺失；需 psql 删行并恢复）
- TC-ai-WEB-031: 限流配置列表展示（ADDED Scenario: ai/限流配置页面#列表展示）
- TC-ai-WEB-032: 编辑限流配置并保存（ADDED Scenario: ai/限流配置页面#编辑保存）
- TC-ai-WEB-033: 非法输入禁止保存（ADDED Scenario: ai/限流配置页面#非法输入禁止保存）
- TC-operation-log-IT-018: 修改 AI 限流配置后异步产生 ai-limit:update 留痕（MODIFIED Scenario: operation-log/运营写操作留痕#AI 限流配置修改留痕）
- TC-operation-log-IT-019: 字符串路径参数作为 target（MODIFIED Scenario: operation-log/留痕字段取值与敏感信息脱敏#字符串路径参数作为 target）

## 修改用例

- TC-ai-IT-055: 匿名 ID 超出每日次数返回 429（MODIFIED: 额度改为经 PUT /api/admin/ai/limits/PER_ANON_DAILY 设置，不再用 AI_LIMIT_PER_ANON_DAILY）
- TC-ai-IT-056: 全站超出每日总量返回 429（MODIFIED: 额度改为经 PUT /api/admin/ai/limits/GLOBAL_DAILY 设置，不再用 AI_LIMIT_GLOBAL_DAILY）
- TC-ai-IT-057: 北京时间昨日记录不计入今日额度（MODIFIED: 同 055，额度来源改表）
- TC-ai-IT-058: 去重命中不占用额度（MODIFIED: 同 055，额度来源改表）
- TC-ai-WEB-001: 侧栏「AI 管理」菜单及子菜单顺序（MODIFIED: 子菜单新增第 5 项「限流配置」/ai/limits）

## 需重测用例

- TC-ai-IT-038: 生成成功（额度检查改读 `loves_ai_limit`，确认默认额度下正常放行）
- TC-ai-IT-050: 重复提交返回原结果且不再调用模型（去重路径与额度检查顺序）
- TC-ai-IT-036: MEMBER 角色可访问 AI 管理接口（权限范围沿用 `/api/admin/ai/**`）
- TC-operation-log-IT-004: 创建运营账号的 payload 中 password 被脱敏（留痕取值规则修改）
- TC-operation-log-IT-005: 创建类操作的 target 为 null（target 规则新增字符串参数回落，需确认创建类仍为 null）
- TC-operation-log-IT-006: 更新类操作的 target 为目标城市 id（UUID 优先不变）
- TC-operation-log-IT-007: 嵌套资源（商户评价）的 target 取父级商户 id（UUID 优先不变）

## 执行汇总

2026-10-26 交付轮：

| 端 | 总数 | ✅ | ❌ | 未执行 | 说明 |
|---|---|---|---|---|---|
| IT | 23 | 23 | 0 | 0 | admin `localhost:21423`（test profile）+ app `localhost:8081` + 假方舟；首轮 TC-ai-IT-090 发现 `{"value":1.5}` 被截断保存，已修复（`value` 改 BigDecimal + `@Digits`）后重跑通过 |
| WEB | 3+1 | 0 | 0 | 4 | TC-ai-WEB-001/031/032/033 未执行：Playwright MCP（playwright-company）连接超时，环境不可用 |

存证：`test-evidence/ai-limit-config/<TC完整ID>/`。
