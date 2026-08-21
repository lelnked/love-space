# TC-article-WEB-002 断言记录

**执行时间**: 2026-08-21
**执行工具**: Playwright MCP
**测试人员**: Hermes Agent

## 断言结果

### 断言 1: 进入文章列表页并核对 DataTable 列内容
- **状态**: ✅ 通过
- **证据**: 页面 URL `http://100.93.172.18:5174/love-space/articles`，DataTable 展示图片、标题、关联栏目、权重、状态开关与操作列
- **截图**: `test-evidence/regression/article/TC-article-WEB-002/articles-page.png`

### 断言 2: 切换文章状态开关后状态即时更新
- **状态**: ✅ 通过
- **证据**: 文章「测试文章标题」切换状态后页面状态即时更新并出现成功提示
