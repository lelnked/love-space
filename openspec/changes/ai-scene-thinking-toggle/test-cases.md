# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/ai/it.md`、`tests/ai/web.md`（ai 域端：IT + web；living 文件，runner 独占回写状态）。
> 方舟请求 body 层面的 `thinking` 断言（enabled / disabled / 连接测试不传、主/重试/备用同值、admin 提示词测试读场景开关）IT 黑盒不可观测，由 love-space-ai / admin / app UT 覆盖（`@scenario` 注释指向下列 Scenario）；IT 通过 `GET /api/admin/ai/generations/{id}` 的 `requestParams.thinkingEnabled` 与场景详情断言。

## 新增用例

- TC-ai-IT-098: PUT /api/admin/ai/scenes/{id}/models 开启深度思考（ADDED Scenario: ai/场景模型配置#开启深度思考）
- TC-ai-IT-099: PUT /api/admin/ai/scenes/{id}/models 未传或 null 的 thinkingEnabled 视为关闭（ADDED Scenario: ai/场景模型配置#未传深度思考视为关闭）
- TC-ai-IT-100: 场景关闭深度思考时生成参数快照 thinkingEnabled=false（ADDED Scenario: ai/生成调用深度思考参数#关闭时显式传 disabled；body 断言由 UT 覆盖）
- TC-ai-IT-101: 场景开启深度思考且切备用时参数快照 thinkingEnabled=true（ADDED Scenario: ai/生成调用深度思考参数#开启时传 enabled 且切备用沿用；body 断言由 UT 覆盖）
- TC-ai-IT-102: POST /api/admin/ai/models/test-connection 场景开启深度思考时连接测试行为不变（ADDED Scenario: ai/生成调用深度思考参数#连接测试不传 thinking；body 断言由 UT 覆盖）
- TC-ai-WEB-034: 配置弹窗开启深度思考并保存回显（ADDED Scenario: ai/模型配置页面#配置深度思考）

## 修改用例

- TC-ai-IT-002: POST /api/admin/ai/scenes 创建场景成功（MODIFIED: ai/业务场景管理#创建场景，SceneDetail 新增断言 `thinkingEnabled=false`）
- TC-ai-IT-040: 生成记录请求快照不含密钥与认证头（MODIFIED: ai/生成记录与调用明细落库，参数快照键集合新增 `thinkingEnabled`）

## 需重测用例

- TC-ai-IT-009 / 010 / 011 / 012: 场景模型配置既有校验（请求体与 DTO 新增字段，回归原行为）
- TC-ai-IT-013: 连接测试（ArkClient.chat 签名变更，确认 ping 不受影响）
- TC-ai-IT-023: 提示词模板测试成功（调用点新增读取场景开关）
- TC-ai-IT-038 / 039 / 048: App 生成成功、完整记录、重试切备用明细（AiGenerator.generate 增参）
- TC-ai-IT-075: 生成记录详情（requestParams 多一个键）
- TC-ai-WEB-006 / 007 / 008: 模型配置页列表、测试连接、保存模型配置（弹窗新增开关）

## 执行汇总

- IT（2026-10-29，admin :8080 / app :8081 / mock-ark :18080）：17 条，✅ 17 / ❌ 0 / 未执行 0；TC-ai-IT-002、098 报的契约漂移（Scene 响应描述缺 thinkingEnabled）已修正 api-spec.json。存证 `test-evidence/ai-scene-thinking-toggle/`。
- WEB：7 条（TC-ai-WEB-034、006–008 等）未执行——playwright-company MCP 连接超时。
- UT：love-space-ai 18、admin 全量、app 全量均绿（含新增 @scenario 用例）。
