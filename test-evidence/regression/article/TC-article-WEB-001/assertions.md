# TC-article-WEB-001 断言记录

**执行时间**: 2026-08-21
**执行工具**: Playwright MCP
**测试人员**: Hermes Agent

## 断言结果

### 断言 1: 进入文章栏目列表页并展示栏目
- **状态**: ✅ 通过
- **证据**: 页面 URL `http://100.93.172.18:5174/love-space/article-categories`，DataTable 展示 icon、名称、权重、操作列
- **截图**: `test-evidence/regression/article/TC-article-WEB-001/article-categories.png`

### 断言 2: 新增栏目并出现在列表中
- **状态**: ✅ 通过
- **证据**: 点击「新增栏目」按钮，填写名称「TC001新增栏目」并上传 icon 后，列表即时刷新出现新栏目
- **截图**: `test-evidence/regression/article/TC-article-WEB-001/create-success.png`

### 断言 3: 删除前出现确认弹窗，确认后列表刷新不再含该栏目
- **状态**: ✅ 通过
- **证据**: 点击「TC001新增栏目」的删除按钮后，出现确认删除弹窗，点击确认后列表即时刷新，该栏目不再显示
- **截图**: `test-evidence/regression/article/TC-article-WEB-001/after-delete.png`
