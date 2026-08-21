# operation-log WEB 用例

> 页面地址：`http://100.100.117.79:5173/love-space/logs`；登录页 `http://100.100.117.79:5173/love-space/signin`。

### TC-operation-log-WEB-001: 按操作人筛选后列表仅剩该操作人且回到第 1 页
**关联需求**: operation-log/web 端操作日志页面#按操作人筛选日志
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: Manager 账号可登录；日志中存在至少两个不同操作人（如 `admin` 与另一账号）的记录，且总条数 > 20（能翻到第 2 页）
**测试步骤**:
1. 登录后进入 /love-space/logs
2. 在分页器点击第 2 页，确认当前页码为 2
3. 在「操作人」输入框填入 `admin`，点击查询
**预期结果**: 列表刷新后所有行的「操作人」列均包含 `admin`，无其它操作人的行；分页器当前页回到第 1 页
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-WEB-002: 重置筛选后恢复全量并回到第 1 页
**关联需求**: operation-log/web 端操作日志页面#按操作人筛选日志
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**前置条件**: Manager 已登录；日志总条数 > 20
**测试步骤**:
1. 进入 /love-space/logs，在「操作人」填入 `admin` 并查询
2. 翻到第 2 页
3. 点击「重置」
**预期结果**: 筛选栏四项（操作人、模块、时间起、时间止）全部清空，列表恢复为不带筛选的全量数据，分页器回到第 1 页；页面下方分页器每页固定 20 条，**不存在每页条数切换控件**
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-WEB-003: 模块与动作按中文映射展示
**关联需求**: operation-log/web 端操作日志页面#模块与动作按中文展示
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: Manager 已登录；日志中存在 `module=city`、`action=create` 的记录（可先在「地图管理」创建一个城市制造该记录）
**测试步骤**:
1. 进入 /love-space/logs
2. 在「模块」下拉中选择「城市」并查询
3. 定位到最新一条创建记录所在行
**预期结果**: 该行「模块」列显示「城市」，「动作」列显示「创建」（不显示英文 `city` / `create`）；表格列依次为时间、操作人、模块、动作、对象
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-WEB-004: 未映射的模块/动作回落显示原始英文值
**关联需求**: operation-log/web 端操作日志页面#未映射的动作回落显示原值
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**前置条件**: Manager 已登录；日志中存在二期模块产生的记录（如 `module=route` / `module=activity` / `module=article`，其动作未在前端映射表中）。可先在对应二期管理页做一次写操作制造记录。
**测试步骤**:
1. 进入 /love-space/logs
2. 不做筛选，翻找到二期模块产生的记录所在行（模块下拉仅覆盖 8 个一期模块，无法从界面按二期模块筛选，需在列表中肉眼定位）
**预期结果**: 该行「动作」列显示原始英文值（如 `create`/`publish` 等未映射值），页面不抛错、不显示空白或 `undefined`，其余行正常渲染；「模块」下拉选项中确无该二期模块
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-WEB-005: 对象为空的创建类记录显示占位符 `-`
**关联需求**: operation-log/web 端操作日志页面#对象为空的记录显示占位符
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**前置条件**: Manager 已登录；日志中存在一条创建类记录（`target` 为 null，如 city:create）
**测试步骤**:
1. 进入 /love-space/logs
2. 「模块」下拉选择「城市」并查询
3. 定位到动作为「创建」的行，读取「对象」列
**预期结果**: 该行「对象」列显示 `-`；同表中动作为「编辑」的行「对象」列显示具体 id，二者形成对照
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**:
**最后更新**: 2026-08-21
