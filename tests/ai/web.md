# ai WEB 用例

### TC-ai-WEB-001: 侧栏「AI 管理」菜单及子菜单顺序
**关联需求**: ai/AI 管理权限与菜单#侧栏菜单
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；存在 MEMBER 角色账号
**测试步骤**:
1. 以 MEMBER 登录后台
2. 在侧栏展开「AI 管理」
**预期结果**: 「AI 管理」一级菜单可见，子菜单依次为「业务配置」「模型配置」「提示词管理」，点击分别进入 /love-space/ai/scenes、/ai/models、/ai/prompts
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-001/`
**最后更新**: -

### TC-ai-WEB-002: 业务配置页新增场景弹窗
**关联需求**: ai/业务配置页面#新增场景弹窗
**来源**: ai-management
**优先级**: P0
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录
**测试步骤**:
1. 进入 /love-space/ai/scenes
2. 点击③「+ 新增场景」，⑦scene_id 填 `web_<随机后缀>`，⑧名称填「网页新增」
3. 点⑩保存
**预期结果**: 弹窗关闭，列表出现 `web_<后缀>` 新行，状态列显示「启用」，创建时间非空
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-002/`
**最后更新**: -

### TC-ai-WEB-003: 编辑场景时 scene_id 只读
**关联需求**: ai/业务配置页面#编辑时 scene_id 只读
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；列表中存在场景 `cycle_advice`（或任一场景，说明非空）
**测试步骤**:
1. 点击该行④编辑
**预期结果**: 弹窗中⑦scene_id 输入框只读并显示该 sceneId，⑧名称、⑨说明回填当前值；修改⑧后保存，列表名称更新
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-003/`
**最后更新**: -

### TC-ai-WEB-004: 重复 scene_id 保存提示
**关联需求**: ai/业务配置页面#重复 scene_id 提示
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；已存在场景 S
**测试步骤**:
1. 点击③新增，⑦填 S、⑧填任意名称
2. 点⑩保存
**预期结果**: 弹窗保持打开，toast 显示「scene_id 已存在」，列表无新增行
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-004/`
**最后更新**: -

### TC-ai-WEB-005: 禁用场景需二次确认
**关联需求**: ai/业务配置页面#禁用需确认
**来源**: ai-management
**优先级**: P0
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；存在一个启用中的场景
**测试步骤**:
1. 点击该行⑤禁用，在确认框点「取消」
2. 再次点击⑤禁用，在确认框点「确认」
3. ②状态下拉选「禁用」
**预期结果**: 步骤 1 后状态仍为「启用」；步骤 2 后该行状态列变为「禁用」、操作列按钮变为「启用」；步骤 3 列表含该场景；列表为空时显示空态
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-005/`
**最后更新**: -

### TC-ai-WEB-006: 模型配置页未配置主模型提示
**关联需求**: ai/模型配置页面#未配置主模型提示
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；场景 `mood_reply`（或任一新建场景）未配置主模型
**测试步骤**:
1. 进入 /love-space/ai/models
2. ①搜索该场景
**预期结果**: 该行主模型列显示「未配置（⚠ 不可生成）」，备用模型列显示「—」
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-006/`
**最后更新**: -

### TC-ai-WEB-007: 配置弹窗测试连接显示结果
**关联需求**: ai/模型配置页面#测试连接显示结果
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；admin 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；Manager 已登录
**测试步骤**:
1. 在 /love-space/ai/models 点击某行②配置
2. ③主模型下拉选一个模型（选项来自 AI_MODELS）
3. 点击其旁④测试连接
**预期结果**: 按钮旁显示「✓ 可达」与毫秒耗时；弹窗内可见固定提示「连接成功只代表服务可达，不代表生成质量」
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-007/`
**最后更新**: -

### TC-ai-WEB-008: 保存模型配置（备用不使用）及主备相同提示
**关联需求**: ai/模型配置页面#保存模型配置
**来源**: ai-management
**优先级**: P0
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录；AI_MODELS 至少 2 个模型
**测试步骤**:
1. 打开某场景②配置弹窗
2. ③主模型选 model-a，⑤备用模型也选 model-a
3. ⑤改为「不使用」，点⑥保存
**预期结果**: 步骤 2 前端即时提示主备不能相同；步骤 3 弹窗关闭，该行主模型列显示 model-a、备用模型列显示「—」
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-008/`
**最后更新**: -

### TC-ai-WEB-009: 提示词场景列表显示未发布
**关联需求**: ai/提示词管理页面#场景列表显示未发布
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；场景 `mood_reply`（或任一场景）无生效模板
**测试步骤**:
1. 进入 /love-space/ai/prompts
**预期结果**: 该行当前生效版本列显示「未发布（⚠ 不可生成）」；已发布场景显示 `V{seq} {name}` 及发布人、发布时间
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-009/`
**最后更新**: -

### TC-ai-WEB-010: 模板列表对比按钮需勾选 2~3 个
**关联需求**: ai/提示词管理页面#对比按钮需勾选 2~3 个
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；某场景下有 4 个模板
**测试步骤**:
1. 在 /love-space/ai/prompts 点该场景②管理模板进入页面 2
2. ⑤勾选 1 个
3. 再勾选到 2 个
4. 再勾选到 4 个
**预期结果**: 页面 2 标题含场景名与 sceneId，列表按版本倒序；③「对比所选(n)」依次为不可用、可用、不可用
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-010/`
**最后更新**: -

### TC-ai-WEB-011: 未测试模板发布按钮置灰
**关联需求**: ai/提示词管理页面#未测试模板发布置灰
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；页面 2 中模板 V2 `tested=false` 且未生效
**测试步骤**:
1. 查看 V2 行操作列，悬停⑧发布
**预期结果**: V2 测试列显示「✗未测」，⑧发布置灰，悬停提示「请先测试」；生效中行不显示⑧发布，⑥显示为「查看」
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-011/`
**最后更新**: -

### TC-ai-WEB-012: 发布二次确认后切换生效
**关联需求**: ai/提示词管理页面#发布二次确认后切换生效
**来源**: ai-management
**优先级**: P0
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；admin 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；V3 生效、V4 已测试
**测试步骤**:
1. 在页面 2 点击 V4 行⑧发布
2. 核对确认框文案后点「确认」
**预期结果**: 确认框文案为「发布后 V4 立即对新请求生效，V3 失效」；列表刷新后 V4 状态「●生效中」，V3「未生效」
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-012/`
**最后更新**: -

### TC-ai-WEB-013: 复制为新模板
**关联需求**: ai/提示词管理页面#复制为新模板
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；页面 2 中存在 V3
**测试步骤**:
1. 点击 V3 行⑦复制
2. 核对预填内容后点⑮保存
**预期结果**: 进入新建页，⑨名称、⑪系统提示词、⑫输入模板、⑭变量表预填 V3 内容；保存后跳到编辑路由，返回页面 2 出现新的最大 seq 版本
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-013/`
**最后更新**: -

### TC-ai-WEB-014: 编辑页未声明变量实时提示
**关联需求**: ai/模板编辑与测试页面#未声明变量实时提示
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；打开一个未生效模板编辑页，变量表只有 `age`
**测试步骤**:
1. 在⑫输入模板中输入 `{{mood}}`
**预期结果**: ⑬显示「输入模板引用了未声明变量 {{mood}}」，⑰运行测试置灰并提示原因；在⑭添加变量 mood 后提示消失
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-014/`
**最后更新**: -

### TC-ai-WEB-015: 运行测试前自动保存
**关联需求**: ai/模板编辑与测试页面#运行测试前自动保存
**来源**: ai-management
**优先级**: P0
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；admin 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；场景已配主模型；未生效模板
**测试步骤**:
1. 修改⑪系统提示词（不保存）
2. 在⑯填好变量值（必填带 *）
3. 点击⑰运行测试
**预期结果**: 先保存成功（版本列表中该模板更新时间刷新），再在⑱显示测试结果（时间、测试人、成功/失败、耗时、tokens、输出 JSON 或错误）；底部状态随结果更新（成功为「已测试」），⑲发布随之可用
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-015/`
**最后更新**: -

### TC-ai-WEB-016: 生效中模板只读可测试
**关联需求**: ai/模板编辑与测试页面#生效中模板只读可测试
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；admin 进程配置指向本地假方舟服务（ARK_BASE_URL 指向 mock）或真实测试 Key；场景生效模板 V3
**测试步骤**:
1. 打开 V3（/love-space/ai/prompt-templates/:id）
**预期结果**: 左栏控件只读、无⑮保存按钮；右栏⑰运行测试可用；底部状态显示「生效中」
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-016/`
**最后更新**: -

### TC-ai-WEB-017: 未测试时底部发布置灰
**关联需求**: ai/模板编辑与测试页面#未测试时发布置灰
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；一个 `tested=false` 的未生效模板
**测试步骤**:
1. 打开该模板编辑页，查看底部
**预期结果**: ⑲发布置灰，提示「请先运行测试」；修改任一内容后（未保存）⑲同样置灰
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-017/`
**最后更新**: -

### TC-ai-WEB-018: 并排对比两个模板
**关联需求**: ai/模板对比页面#并排对比
**来源**: ai-management
**优先级**: P1
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；页面 2 中 V4 未生效、V3 生效中，两者系统提示词不同
**测试步骤**:
1. 勾选 V4 与 V3
2. 点击③对比所选
3. 点击底部「返回」
**预期结果**: 进入 /ai/prompts/:sceneId/compare，两列分别显示 V4 与 V3，V3 标题含「●生效中」，各列含系统提示词、输入模板、变量、最后测试输入、⑳最后测试输出，不同文本被高亮；「返回」回到页面 2
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-018/`
**最后更新**: -

### TC-ai-WEB-019: 对比页 ids 数量非法
**关联需求**: ai/模板对比页面#ids 数量非法
**来源**: ai-management
**优先级**: P2
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；Manager 已登录；某场景 sceneId 已知
**测试步骤**:
1. 直接访问 /love-space/ai/prompts/<sceneId>/compare?ids=<单个 id>
**预期结果**: 提示需选择 2~3 个模板，并回到页面 2
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-019/`
**最后更新**: -

### TC-ai-WEB-020: 保存版本冲突不覆盖本地编辑
**关联需求**: ai/模板编辑与测试页面#运行测试前自动保存
**来源**: ai-management
**优先级**: P2
**前置条件**: 登录入口 http://100.93.172.18:5173/love-space/signin；同一未生效模板在两个标签页打开
**测试步骤**:
1. 标签页 A 修改名称并保存
2. 标签页 B 修改系统提示词后点⑮保存
**预期结果**: 标签页 B toast「内容已被他人更新，请刷新」，左栏仍保留 B 的本地编辑内容
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/ai/TC-ai-WEB-020/`
**最后更新**: -
