# activity WEB 用例

### TC-activity-WEB-001: 活动列表展示与上下线开关
**关联需求**: activity/web 端活动管理页面#活动列表与上下线
**来源**: ambassador-route-activity → activity-drop-city-link
**优先级**: P1
**前置条件**: Manager 账号可登录 http://100.93.172.18:5173/love-space/signin；已存在至少一个上线活动（含图片、级别）
**测试步骤**:
1. 登录后台，导航至 /love-space/activities
2. 核对 DataTable 列内容
3. 切换某上线活动的状态开关为下线
**预期结果**: DataTable 展示图片、标题、级别、状态开关与操作列，**不含「所属城市」列**，筛选区**不含地图（城市）下拉**；切换后该行状态即时变为下线且出现成功提示，刷新页面后状态保持
**状态**: ⬜ 未测试（activity-drop-city-link 变更后需重测）
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-WEB-001/`
**最后更新**: 2026-08-19

### TC-activity-WEB-002: 活动表单富文本编辑并回显
**关联需求**: activity/web 端活动管理页面#活动表单富文本编辑
**来源**: ambassador-route-activity → activity-drop-city-link
**优先级**: P1
**前置条件**: Manager 已登录；活动表单可打开（新建或编辑）
**测试步骤**:
1. 进入 /love-space/activities，打开活动表单，填写必填字段（标题、图片），勾选周期多选与级别单选，添加 1 条路线子条目
2. 在「活动详情说明」富文本编辑器中输入一段文本并插入 1 张图片，保存
3. 重新打开该活动的编辑表单
**预期结果**: 保存成功有提示；重新打开后富文本编辑器回显此前录入的文本与图片（图片正常渲染），周期/级别/路线子条目与录入一致
**状态**: ⚠️ 环境阻塞（activity-drop-city-link 变更后需重测）
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-WEB-002/`
**阻塞说明**: 本地 OSS 仅配占位符，后端要求图片 objectKey 格式为 `images/<id>.<ext>`；前端保存前校验“至少 1 张图片”失败，富文本保存/回显链路无法完成。Playwright MCP 在当前会话中多次出现 unreachable/duplicate outputs，无法可靠完成图片上传与富文本编辑验证。
**最后更新**: 2026-08-21

### TC-activity-WEB-003: 活动表单填写景观并回显
**关联需求**: activity/web 端活动管理页面#活动表单填写景观并回显
**来源**: activity-landscape-field → activity-drop-city-link
**优先级**: P2
**前置条件**: Manager 已登录；活动表单可打开（新建或编辑）
**测试步骤**:
1. 进入 /love-space/activities，打开活动新增表单，在「景观」输入框填写「海岸线景观」，连同必填字段一起保存
2. 重新打开该活动的编辑表单，把「景观」改为「火山地貌」并保存
3. 再次打开该活动的编辑表单
**预期结果**: 步骤 1 保存成功；步骤 2 打开时「景观」回显「海岸线景观」，改后保存成功；步骤 3 回显「火山地貌」
**状态**: ⬜ 未测试（activity-drop-city-link 变更后需重测）
**执行方式**: web-test-runner（Playwright 本地 Chromium；@playwright/mcp 远程服务 100.103.199.95:9233 不可达）
**执行存证**: `test-evidence/activity-landscape-field/TC-activity-WEB-003/`
**最后更新**: 2026-08-24

### TC-activity-WEB-004: 活动表单无地图选项即可保存
**关联需求**: activity/web 端活动管理页面#活动表单无地图选项即可保存
**来源**: activity-drop-city-link
**优先级**: P1
**前置条件**: Manager 已登录 http://100.93.172.18:5173/love-space/
**测试步骤**:
1. 进入 /love-space/activities，点击新增打开活动表单
2. 检查表单字段区，确认不存在「所属地图」/「所属城市」下拉控件
3. 填写标题与至少 1 张图片后保存
**预期结果**: 步骤 2 表单中无地图（城市）下拉项；步骤 3 保存成功、出现成功提示并返回列表，新活动出现在列表中
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/activity/TC-activity-WEB-004/`
**最后更新**: -

### TC-activity-WEB-005: 活动表单填写副标题并回显
**关联需求**: activity/web 端活动管理页面#活动表单填写副标题并回显
**来源**: activity-subtitle
**优先级**: P2
**前置条件**: Manager 已登录 http://100.93.172.18:5173/love-space/；活动表单可打开（新建或编辑）
**测试步骤**:
1. 进入 /love-space/activities，打开活动新增表单，确认「活动标题」输入框下方存在「副标题」单行输入框
2. 在「副标题」填写「一日徒步」，连同必填字段（标题、≥1 张图片）一起保存
3. 重新打开该活动的编辑表单，核对「副标题」回显，改为「两日徒步」并保存
4. 再次打开该活动的编辑表单，清空「副标题」并保存，第三次打开编辑表单
**预期结果**: 步骤 1 表单字段区在标题下方有「副标题」单行输入框（非必填、无红星）；步骤 2 保存成功有提示并返回列表；步骤 3 打开时回显「一日徒步」，改后保存成功；步骤 4 第二次打开回显「两日徒步」，清空保存成功，第三次打开「副标题」为空（可清空）
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/activity/TC-activity-WEB-005/`
**最后更新**: -

### TC-activity-WEB-006: 富文本粘贴大图（> 3 KB）走 OSS 上传链路
**关联需求**: file/图片上传的界面交互#富文本粘贴大图走 OSS 上传
**来源**: rich-text-gif-and-inline-sticker
**优先级**: P1
**前置条件**: Manager 已登录 http://100.93.172.18:5173/love-space/；活动表单可打开；后端 OSS 直传链路可用（本地仅占位符时 `POST /api/admin/files/upload-credentials` 不返回 200，本用例退化为只断言「已发起凭证请求 + 无内联 data URL」，并注明环境阻塞）；准备一张 > 3 KB 的 png（如 8 KB）
**测试步骤**:
1. 进入 /love-space/activities，打开活动新增表单，填写标题与 ≥1 张图片
2. 聚焦「活动详情说明」富文本编辑区，通过 `page.evaluate` 构造 `ClipboardEvent('paste')`（`clipboardData` 含该 png `File`）派发到编辑器 root；同时用 `page.waitForRequest` 监听 `/api/admin/files/upload-credentials`
3. 保存；重新打开该活动的编辑表单
**预期结果**: 步骤 2 编辑器立即出现该图预览，且捕获到一次 `POST /api/admin/files/upload-credentials`（body `contentType` 为 `image/png`）；编辑器 HTML 中该 img 的 src **不是** `data:` 前缀（走上传，非内联）；步骤 3 保存成功有提示，重新打开后该图以 http 签名地址回显、正常渲染
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/activity/TC-activity-WEB-006/`
**最后更新**: -

### TC-activity-WEB-007: 富文本粘贴小表情（≤ 3 KB gif）内联为 data URL，不发起上传
**关联需求**: file/图片上传的界面交互#富文本粘贴小表情内联
**来源**: rich-text-gif-and-inline-sticker
**优先级**: P1
**前置条件**: Manager 已登录 http://100.93.172.18:5173/love-space/；活动表单可打开；准备一张 ≤ 3 KB 的 gif（如 1.5 KB，`GIF89a` 头即可）
**测试步骤**:
1. 进入 /love-space/activities，打开活动新增表单，填写标题与 ≥1 张图片
2. 记录当前网络请求数；向「活动详情说明」编辑器 root 派发 `paste` 事件（`clipboardData` 含该 gif `File`）
3. 保存；重新打开该活动的编辑表单
**预期结果**: 步骤 2 编辑器立即出现该图，其 img src 以 `data:image/gif;base64,` 开头；**未**发起任何 `POST /api/admin/files/upload-credentials` 请求；步骤 3 保存成功（后端 200，不弹「图片对象不可用」），重新打开后该图仍以同一 `data:image/gif;base64,` 地址回显并正常渲染
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/activity/TC-activity-WEB-007/`
**最后更新**: -

### TC-activity-WEB-008: 富文本粘贴非白名单类型（svg）被拦并提示
**关联需求**: file/图片上传的界面交互#富文本粘贴非白名单类型被拦
**来源**: rich-text-gif-and-inline-sticker
**优先级**: P1
**前置条件**: Manager 已登录 http://100.93.172.18:5173/love-space/；活动表单可打开；准备一个 svg 文件（`image/svg+xml`，1 KB）与一段纯文本
**测试步骤**:
1. 进入 /love-space/activities，打开活动表单，在「活动详情说明」编辑器输入「基线文本」，记录编辑器 innerHTML
2. 向编辑器 root 派发 `paste` 事件（`clipboardData` 含该 svg `File`）
3. 再派发一次 `paste` 事件，`clipboardData` 仅含纯文本「附加文本」（不含文件）
**预期结果**: 步骤 2 弹出全局提示「仅支持 png/jpeg/webp/gif 图片」，编辑器 innerHTML 与步骤 1 记录一致（无新增 `<img>`、无 data URL），未发起任何 `/api/admin/files/upload-credentials` 请求；步骤 3 编辑器出现「附加文本」——不含图片文件的粘贴保持编辑器默认行为，不被误拦
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/activity/TC-activity-WEB-008/`
**最后更新**: -
