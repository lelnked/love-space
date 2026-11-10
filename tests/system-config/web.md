# system-config WEB 用例

> 前端入口 `http://100.93.172.18:5173/love-space/`（Vite `--host` 启动）；造数经 admin 接口（见 it.md 头注）。圈号①–⑪对应 proposal F1 线框。

### TC-system-config-WEB-001: 侧栏「系统配置」菜单位置与列表区布局
**关联需求**: system-config/系统配置管理页面#新增成功后列表刷新
**来源**: add-system-config
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录
**测试步骤**:
1. 观察侧栏一级菜单顺序
2. 点击「系统配置」
**预期结果**: 「系统配置」一级菜单位于「AI 管理」与「操作日志」之间；点击后进入 /love-space/system-configs，菜单高亮；列表区顶部有①搜索框（占位「搜索 key / 名称」）与②「+ 新增配置」按钮；表格列依次为 key、名称、value（预览）、更新时间、操作（无「说明」列）；底部有⑤分页（20/200 条每页）；无数据时显示空态
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-001/`
**最后更新**: -

### TC-system-config-WEB-002: 非法 JSON 被前端拦截且不发请求
**关联需求**: system-config/系统配置管理页面#非法 JSON 被前端拦截
**来源**: add-system-config
**优先级**: P0
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录；开启网络请求监听
**测试步骤**:
1. 进入 /love-space/system-configs，点击②「+ 新增配置」
2. ⑥key 填 `web.k_<s>`，⑦名称填「网页配置」
3. ⑨value 输入 `{text: hi}`，点击⑪保存
**预期结果**: ⑨value 文本框下方显示红字「value 必须是合法的 JSON」；弹窗不关闭；网络请求中没有 `POST /api/admin/system-configs`
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-002/`
**最后更新**: -

### TC-system-config-WEB-003: value 为空 / null 与失焦校验
**关联需求**: system-config/系统配置管理页面#非法 JSON 被前端拦截
**来源**: add-system-config
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录；开启网络请求监听
**测试步骤**:
1. 打开新增弹窗，填好⑥key `web.k_<s>`、⑦名称
2. ⑨value 输入 `[1,` 后点击⑦名称框使 value 失焦
3. 清空⑨value，点击⑪保存
4. ⑨value 输入 `null`，点击⑪保存
5. ⑨value 改为 `{"a":1}`，使其失焦
**预期结果**: 步骤 2 失焦即显示「value 必须是合法的 JSON」（未点保存）；步骤 3 与步骤 4 显示「value 不能为空」且未发出 POST 请求；步骤 5 错误提示消失
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-003/`
**最后更新**: -

### TC-system-config-WEB-004: 格式化合法 JSON 与非法 JSON
**关联需求**: system-config/系统配置管理页面#格式化合法 JSON
**来源**: add-system-config
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录
**测试步骤**:
1. 打开新增弹窗，⑨value 输入 `{"a":1,"b":[1,2]}`
2. 点击⑧「格式化」
3. ⑨value 改为 `{"a":1,`，点击⑧「格式化」
**预期结果**: 步骤 2 后⑨文本框内容变为 2 空格缩进的多行 JSON（等于 `JSON.stringify(v,null,2)`）；步骤 3 显示「value 必须是合法的 JSON」，文本框内容保持 `{"a":1,` 不变
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-004/`
**最后更新**: -

### TC-system-config-WEB-005: 新增成功后弹窗关闭、列表刷新
**关联需求**: system-config/系统配置管理页面#新增成功后列表刷新
**来源**: add-system-config
**优先级**: P0
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录
**测试步骤**:
1. 点击②「+ 新增配置」，确认弹窗内常驻提示
2. ⑥key 填 `web.k_<s>`，⑦名称填「网页配置<s>」，⑨value 填 `{"text":"hi"}`，⑩说明填「网页说明」
3. 点击⑪保存
4. 收尾：删除该配置
**预期结果**: 步骤 1 弹窗内可见提示「App 可读取全部配置，请勿填写密钥、密码等机密」；步骤 3 后弹窗关闭，列表区出现 `web.k_<s>` 行，名称「网页配置<s>」，value 列显示单行预览 `{"text":"hi"}`，说明「网页说明」，更新时间非空
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-005/`
**最后更新**: -

### TC-system-config-WEB-006: value 预览超 40 字符截断、悬停显示全文
**关联需求**: system-config/系统配置管理页面#新增成功后列表刷新
**来源**: add-system-config
**优先级**: P2
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录；经接口造数 `web.long_<s>`，value 为单行序列化后长度 > 40 的对象（如 `{"text":"abcdefghijklmnopqrstuvwxyz0123456789ABCD"}`）；另造 `web.short_<s>`，value `28`
**测试步骤**:
1. 进入 /love-space/system-configs，①搜索 `web.`+`<s>` 相关 key
2. 悬停 `web.long_<s>` 行 value 预览
3. 收尾：删除两个配置
**预期结果**: `web.long_<s>` 的 value 列显示前 40 字符 + 省略号；悬停显示完整单行 JSON；`web.short_<s>` 的 value 列完整显示 `28`，无省略号
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-006/`
**最后更新**: -

### TC-system-config-WEB-007: 编辑时 key 只读、字段回填并保存
**关联需求**: system-config/系统配置管理页面#编辑时 key 只读
**来源**: add-system-config
**优先级**: P0
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录；经接口造数 `home.notice_<s>`，名称「首页公告」、value `{"text":"旧"}`、说明「顶部」
**测试步骤**:
1. 点击该行③「编辑」
2. 尝试修改⑥key
3. ⑦名称改为「首页公告-改」，⑨value 改为 `{"text":"新"}`，点击⑪保存
4. 收尾：删除该配置
**预期结果**: 步骤 1 弹窗中⑥key 显示 `home.notice_<s>` 且不可编辑（readOnly 或 disabled），⑦名称、⑨value（`{"text":"旧"}` 的 JSON 文本）、⑩说明已回填；步骤 2 无法改动；步骤 3 弹窗关闭，该行名称变为「首页公告-改」，value 预览为 `{"text":"新"}`，key 不变
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-007/`
**最后更新**: -

### TC-system-config-WEB-008: key 重复时弹窗内提示且不关闭
**关联需求**: system-config/系统配置管理页面#key 重复时弹窗内提示
**来源**: add-system-config
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录；经接口造数 `home.notice_<s>`
**测试步骤**:
1. 点击②新增，⑥key 填 `home.notice_<s>`，⑦名称任意，⑨value `1`
2. 点击⑪保存
3. 收尾：删除前置配置
**预期结果**: 弹窗不关闭，弹窗内显示「配置 key 已存在」；列表无新增重复行
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-008/`
**最后更新**: -

### TC-system-config-WEB-009: 删除需二次确认（取消 / 确认）
**关联需求**: system-config/系统配置管理页面#删除需二次确认
**来源**: add-system-config
**优先级**: P0
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录；经接口造数 `home.notice_<s>`
**测试步骤**:
1. 点击该行④「删除」，在确认框点「取消」
2. 再次点击④「删除」，在确认框点「确认」
**预期结果**: 确认框文案含「删除后 App 将无法读取该配置」；步骤 1 后该行仍在；步骤 2 后该行从列表区消失，`totalElements` 减 1
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-009/`
**最后更新**: -

### TC-system-config-WEB-010: 搜索 key / 名称与分页
**关联需求**: system-config/系统配置管理页面#新增成功后列表刷新
**来源**: add-system-config
**优先级**: P2
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录；经接口造数 `web.a_<s>`（名称「甲<s>」）与 `web.b_<s>`（名称「乙<s>」）
**测试步骤**:
1. ①搜索框输入 `web.a_<s>`
2. ①搜索框改为「乙<s>」
3. ①搜索框改为 `zz_nomatch_<s>`
4. 清空搜索，⑤每页条数切换为 200
5. 收尾：删除两个配置
**预期结果**: 步骤 1 列表仅 `web.a_<s>`；步骤 2 仅 `web.b_<s>`（按名称命中）；步骤 3 显示空态；步骤 4 请求 `size=200`，分页信息「共 N 条」与接口 totalElements 一致
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/system-config/TC-system-config-WEB-010/`
**最后更新**: -
