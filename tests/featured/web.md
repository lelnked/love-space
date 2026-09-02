# featured WEB 用例

### TC-featured-WEB-001: 精选推荐列表展示与上下线开关
**关联需求**: featured/web 端精选推荐页面#精选推荐列表与上下线
**来源**: article-and-featured-feed
**优先级**: P1
**前置条件**: Manager 账号可登录 http://100.93.172.18:5173/love-space/signin；已存在至少一个推荐条目（含 banner、关联城市、说明）
**测试步骤**:
1. 登录后台，导航至 /love-space/featured-items
2. 核对 DataTable 列内容
3. 切换某条目的状态开关
**预期结果**: DataTable 展示 banner 图、关联城市、推荐说明、状态开关与操作列；切换后该行状态即时更新且出现成功提示，刷新页面后状态保持
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/featured/TC-featured-WEB-001/`
**阻塞说明**: 无阻塞。已通过 Playwright 验证精选推荐列表页展示与上下线操作正常。
**最后更新**: 2026-08-21

### TC-featured-WEB-002: 弹窗表单新增精选推荐
**关联需求**: featured/web 端精选推荐页面#新增精选推荐
**来源**: article-and-featured-feed
**优先级**: P1
**前置条件**: Manager 已登录；存在至少一个上架城市
**测试步骤**:
1. 进入 /love-space/featured-items，打开新增弹窗表单
2. 单选一个城市、上传 banner 图片、填写推荐说明后提交
**预期结果**: 保存成功有提示；列表即时刷新出现新条目，展示所选城市与填写的说明
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/featured/TC-featured-WEB-002/`
**阻塞说明**: 无阻塞。已通过 Playwright 完成新增（城市选择、banner 上传、推荐说明）并在列表页验证数据回显。
**最后更新**: 2026-08-21

### TC-featured-WEB-003: 周期推荐页单列表展示与投放周期标签
**关联需求**: featured/web 端周期推荐页面#周期 Tab 切换与列表展示
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P1
**前置条件**: Manager 账号可登录 http://100.93.172.18:5173/love-space/signin；存在一条 `phases` 为「经期+黄体期」的条目与一条 `phases` 为「卵泡期」的条目（sortOrder 不同，各关联不同实体）
**测试步骤**:
1. 登录后台，导航至 /love-space/featured-cycle-items
2. 核对区域① 标题与「新增周期推荐」按钮、区域③ DataTable 的列内容
3. 检查页面上是否还存在经期/卵泡期/排卵期/黄体期四个周期 Tab
**预期结果**: 区域③ 为**单一 DataTable**，一张列表同时展示两条条目（不再按周期分表）；列含 banner 缩略图、内容类型徽标、标题、关联实体名、**投放周期**、排序号、状态开关与操作列，行按排序号升序；第一条的「投放周期」列以标签形式展示「经期」「黄体期」两个标签（按 经期/卵泡期/排卵期/黄体期 顺序），第二条展示「卵泡期」一个标签；步骤 3 页面上**没有**任何周期 Tab 控件
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/featured/TC-featured-WEB-003/`
**最后更新**: -

### TC-featured-WEB-004: 新增表单页按内容类型切换字段块
**关联需求**: featured/web 端周期推荐页面#表单按类型切换字段
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P1
**前置条件**: Manager 已登录；库中存在至少一个活动、一条路线、一篇文章，且均未被已有周期推荐引用
**测试步骤**:
1. 进入 /love-space/featured-cycle-items，点「新增周期推荐」进入 /featured-cycle-items/create
2. 在区域④ 勾选「经期」与「黄体期」
3. 区域⑤ 内容类型选「tripperclub活动」，核对字段块
4. 内容类型切到「路线体验」，核对字段块与区域④ 的勾选状态
5. 内容类型切到「周期生活法」，核对字段块与区域④ 的勾选状态
**预期结果**: 表单页「基础信息」分组展示区域④ 周期多选勾选框组、区域⑤ 内容类型选择器、banner 上传与排序号；选活动时字段块为 活动下拉 + 推荐说明 + 活动说明（标注选填）；切到路线体验后活动专属字段消失，出现 路线下拉 + 主标题 + 副标题 + 推荐说明；切到周期生活法后仅剩 文章下拉 + 主标题；每次切换下方字段块内容被清空，不残留上一类型已填内容；**区域④ 已勾选的「经期」「黄体期」在三次类型切换后保持不变**（周期勾选独立于内容类型）
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/featured/TC-featured-WEB-004/`
**最后更新**: -

### TC-featured-WEB-005: 周期生活法选中文章后自动带出主标题
**关联需求**: featured/web 端周期推荐页面#文章类型自动带出主标题
**来源**: featured-cycle-feed
**优先级**: P1
**前置条件**: Manager 已登录；库中存在至少一篇标题可识别的文章
**测试步骤**:
1. 进入 /love-space/featured-cycle-items，点「新增周期推荐」进入 /featured-cycle-items/create
2. 内容类型选「周期生活法」
3. 在文章下拉中选中一篇文章
4. 在主标题输入框末尾追加文字
**预期结果**: 选中文章后主标题输入框自动填入该文章标题；输入框仍可编辑，追加文字生效且不被下拉选择重置
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/featured/TC-featured-WEB-005/`
**最后更新**: -

### TC-featured-WEB-006: 表单页新增多周期周期推荐
**关联需求**: featured/web 端周期推荐页面#新增周期推荐
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P1
**前置条件**: Manager 已登录；库中存在至少一个**未被任何周期推荐引用**的活动
**测试步骤**:
1. 进入 /love-space/featured-cycle-items（无 Tab，直接是单列表），点「新增周期推荐」进入 /featured-cycle-items/create
2. 在区域④ **同时勾选「经期」与「黄体期」**
3. 内容类型选「tripperclub活动」，选一个未被引用的活动，填推荐说明，上传 banner，填排序号后提交
**预期结果**: 保存成功有提示；页面跳回列表，单列表出现新条目，展示所上传 banner、类型徽标「tripperclub活动」、所选活动名与填写的排序号；该行「投放周期」列展示「经期」「黄体期」两个标签
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/featured/TC-featured-WEB-006/`
**最后更新**: -

### TC-featured-WEB-007: 周期推荐上下线切换与删除确认
**关联需求**: featured/web 端周期推荐页面#周期推荐上下线与删除
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P1
**前置条件**: Manager 已登录；单列表中至少存在 2 个条目
**测试步骤**:
1. 进入 /love-space/featured-cycle-items（单列表，无周期 Tab）
2. 切换某条目的状态开关
3. 对另一条目点击删除，在确认弹窗中确认
**预期结果**: 状态开关切换后该行状态即时更新并有成功提示；删除需经确认弹窗二次确认，确认后该条目从列表消失并出现成功提示；两步操作均不受周期筛选下拉当前选项影响
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/featured/TC-featured-WEB-007/`
**最后更新**: -

### TC-featured-WEB-008: 周期筛选下拉过滤列表
**关联需求**: featured/web 端周期推荐页面#周期筛选下拉
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**前置条件**: Manager 已登录；存在一条 `phases` 含「卵泡期」的条目（如 `["卵泡期","黄体期"]`）与一条仅含「经期」的条目，各关联不同实体
**测试步骤**:
1. 进入 /love-space/featured-cycle-items，核对区域② 周期筛选下拉的默认值
2. 在区域② 的周期筛选下拉中选择「卵泡期」
3. 将下拉切回「全部周期」
**预期结果**: 步骤 1 区域② 存在一个周期筛选下拉，默认选中「全部周期」，此时区域③ 展示两条条目；步骤 2 区域③ 仅展示 `phases` 含卵泡期的那条（多周期条目在其任一周期的筛选下都会出现），另一条不展示；步骤 3 切回后两条重新全部展示
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-WEB-008/`
**最后更新**: -

### TC-featured-WEB-009: 未勾选周期无法提交
**关联需求**: featured/web 端周期推荐页面#未勾选周期无法提交
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**前置条件**: Manager 已登录；库中存在至少一个未被引用的活动
**测试步骤**:
1. 进入 /love-space/featured-cycle-items，记录当前列表条目数，点「新增周期推荐」进入 /featured-cycle-items/create
2. 除区域④ 外的必填项全部填好（内容类型、关联活动、推荐说明、banner）
3. 区域④ 四个周期勾选框**一个都不勾**，点击「保存」
4. 补勾一个周期后再次提交
**预期结果**: 步骤 3 提交被阻止——保存按钮为禁用态，或点击后弹出「请至少选择一个周期」类中文提示；停留在表单页，列表条目数不变（未新增条目）；步骤 4 勾选后可正常保存成功，列表新增一条
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-WEB-009/`
**最后更新**: -

### TC-featured-WEB-010: 编辑时修改周期
**关联需求**: featured/web 端周期推荐页面#编辑时修改周期
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**前置条件**: Manager 已登录；存在一条 `phases` 仅为「经期」的条目
**测试步骤**:
1. 进入 /love-space/featured-cycle-items，点击该条目的「编辑」
2. 核对区域④ 勾选框的回显状态
3. 取消勾选「经期」，勾选「排卵期」与「黄体期」，保存
4. 刷新页面复查该行
**预期结果**: 步骤 2 区域④ 中「经期」为已勾选、其余三个未勾选，且四个勾选框均**可编辑**（周期已由创建后不可变放宽为可修改）；步骤 3 保存成功有提示；该行「投放周期」列变为「排卵期」「黄体期」两个标签且不再有「经期」；步骤 4 刷新后展示保持一致
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-WEB-010/`
**最后更新**: -

### TC-featured-WEB-011: 关联实体重复时展示后端中文业务错误
**关联需求**: featured/web 端周期推荐页面#关联实体重复时展示错误
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**前置条件**: Manager 已登录；某个活动 A 已存在一条周期推荐条目
**测试步骤**:
1. 进入 /love-space/featured-cycle-items，记录当前列表条目数，点「新增周期推荐」进入 /featured-cycle-items/create
2. 勾选任一周期，内容类型选「tripperclub活动」，关联活动选**同一个活动 A**，填推荐说明并上传 banner
3. 点击「保存」
**预期结果**: 提交后页面展示后端返回的中文业务错误提示「该活动已存在周期推荐」（不是英文报错、不是 500 白屏）；**停留在表单页**且已填内容保留；列表条目数不变，未新增重复条目
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-WEB-011/`
**最后更新**: -
