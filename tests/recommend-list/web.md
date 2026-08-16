# recommend-list WEB 用例

### TC-recommend-list-WEB-001: 推荐清单列表与城市筛选
**关联需求**: recommend-list/web 端推荐清单管理页面#清单列表与筛选
**来源**: map-and-recommend-list
**优先级**: P1
**前置条件**: Manager 账号可登录 http://100.100.117.79:5173/love-space/signin；城市 A 下已存在多个清单（sortOrder 各异），城市 B 下无清单或数量不同
**测试步骤**:
1. 登录后台，导航至 /love-space/recommend-lists
2. 在城市筛选控件选择城市 A
3. 切换筛选为城市 B
**预期结果**: DataTable 展示城市 A 的清单，列含标题、所属城市、排序号、商户数，行按 sortOrder 升序；切到城市 B 后列表行数随之变化，仅显示城市 B 的清单
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-WEB-001/`
**最后更新**: 2026-08-16

### TC-recommend-list-WEB-002: 清单编辑界面维护商户（仅本城市可选）
**关联需求**: recommend-list/web 端推荐清单管理页面#维护清单商户
**来源**: map-and-recommend-list
**优先级**: P1
**前置条件**: Manager 已登录；存在清单（属城市 A）；城市 A 下有商户 M1/M2，城市 B 下有商户 Mx
**测试步骤**:
1. 打开该清单的编辑界面，展开商户添加下拉
2. 核对下拉选项范围
3. 添加 M1（排序号 2）与 M2（排序号 1）并保存
**预期结果**: 下拉仅出现城市 A 的商户（M1/M2），不出现 Mx；保存成功后清单商户列表按排序号升序回显为 M2、M1
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-WEB-002/`
**最后更新**: 2026-08-16

### TC-recommend-list-WEB-003: 删除清单需确认（确认删除、取消保留）
**关联需求**: recommend-list/web 端推荐清单管理页面#删除清单需确认
**来源**: map-and-recommend-list
**优先级**: P1
**前置条件**: Manager 已登录；列表中存在至少两个可删除的清单
**测试步骤**:
1. 在 /love-space/recommend-lists 对清单 X 点击删除，在确认弹窗点「取消」
2. 对清单 X 再次点击删除，在确认弹窗点「确认」
**预期结果**: 步骤 1 后弹窗关闭、清单 X 仍在列表中；步骤 2 后清单 X 从 DataTable 消失，列表行数减 1
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-WEB-003/`
**最后更新**: 2026-08-16
