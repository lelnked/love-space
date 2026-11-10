# system-config WEB 用例

> 前端入口 `http://100.93.172.18:5173/love-space/`（Vite `--host` 启动）。圈号①–⑨对应 proposal F1 线框。

### TC-system-config-WEB-001: 列表页展示预置配置且无新增与删除入口
**关联需求**: system-config/系统配置管理页面#列表页展示预置配置且无新增与删除入口
**来源**: add-system-config
**优先级**: P0
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录
**测试步骤**:
1. 观察侧栏一级菜单顺序，点击「系统配置」进入 `/love-space/system-configs`
2. 查看列表页顶部工具栏、表格列与 `ios_llm_popup` 行的操作列
**预期结果**: 「系统配置」一级菜单位于「AI 管理」与「操作日志」之间；列表页展示 `ios_llm_popup`（名称「iOS LLM 弹窗开关」），页面上不存在「+ 新增配置」按钮，操作列仅有「编辑」按钮而无「删除」按钮
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-001/`
**最后更新**: -

### TC-system-config-WEB-002: 进入 ios_llm_popup 专属表单页回填并切换开关保存
**关联需求**: system-config/系统配置管理页面#进入 ios_llm_popup 专属表单页回填并切换开关保存
**来源**: add-system-config
**优先级**: P0
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录；`ios_llm_popup` 当前 value 为 `{"enabled": true}`
**测试步骤**:
1. 在 `/love-space/system-configs` 点击 `ios_llm_popup` 行的「编辑」按钮
2. 观察编辑页 `/love-space/system-configs/{id}/edit` 的字段回填与④ key 只读状态
3. 将⑦「是否需要弹窗」切换为关闭，点击⑧「保存」
4. 收尾：再次点击「编辑」，将「是否需要弹窗」切换回开启并保存
**预期结果**: 步骤 2 编辑页中 key 输入框只读显示 `ios_llm_popup` 且附「系统预置配置，key 不可修改」，名称与「是否需要弹窗」已回填；步骤 3 保存后提示「已保存，立即生效」并跳转回 `/love-space/system-configs`，该行 value 预览更新为 `{"enabled":false}`
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-002/`
**最后更新**: -

### TC-system-config-WEB-003: 编辑页名称为空时前端拦截
**关联需求**: system-config/系统配置管理页面#编辑页名称为空时前端拦截
**来源**: add-system-config
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录；已进入 `ios_llm_popup` 编辑页
**测试步骤**:
1. 清空⑤「名称」输入框
2. 点击⑧「保存」
**预期结果**: 名称输入框下方显示「配置名称不能为空」，且未发出 `PUT /api/admin/system-configs/{id}` 请求
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-003/`
**最后更新**: -
