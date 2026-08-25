# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/article/{it,web}.md`（article 域「端」列为 web，无 app 端用例）。

## 新增用例

- TC-article-IT-015: POST /api/admin/articles 创建带封面标题、引言与标签的文章（ADDED Scenario: article/文章管理#创建带封面标题、引言与标签的文章）— P0 / happy
- TC-article-IT-016: POST /api/admin/articles 省略封面标题、引言、标签时详情返回 null/null/[]（ADDED Scenario: article/文章管理#封面标题、引言、标签均可省略）— P0 / boundary
- TC-article-IT-017: PUT /api/admin/articles/{id} 更新三字段，标签空白项被剔除、清空封面标题与引言存 null（ADDED Scenario: article/文章管理#创建带封面标题、引言与标签的文章）— P1 / boundary
- TC-article-IT-018: GET app 文章列表 未设封面标题时回落文章标题、已设时返回封面标题（ADDED Scenario: article/App 端文章查询#未设封面标题时列表回落文章标题）— P0 / state
- TC-article-IT-019: GET app 文章详情 返回引言与标签，未设时为 null 与空数组（ADDED Scenario: article/App 端文章查询#详情返回引言与标签）— P0 / happy+boundary
- TC-article-WEB-004: 文章表单填写封面标题、引言与多条标签并回显、删除标签（ADDED Scenario: article/web 端文章管理页面#表单填写封面标题、引言与标签）— P0 / happy
- TC-article-WEB-005: 存量无封面标题与引言的文章打开编辑表单不报错、其余字段正常回显（ADDED Scenario: article/web 端文章管理页面#存量文章封面标题为空时表单可正常打开）— P1 / boundary

## 修改用例

- TC-article-IT-011: app 端栏目与文章列表按权重升序（MODIFIED: 列表响应新增 coverTitle、tags，断言字段集需补这两项）
- TC-article-WEB-002: 文章列表展示与上下线开关（MODIFIED: 列表标题列拆为「封面标题」「文章标题」两列，layout 断言需按新列口径）

## 需重测用例

- TC-article-IT-001 ~ TC-article-IT-010: 文章 CRUD 与富文本既有 IT 用例——行为未变，但 `Article` 实体、Upsert 请求与两个 Response DTO 均有改动，需回归确认无字段映射回归
- TC-article-IT-012 ~ TC-article-IT-014: app 端可见性与富文本用例——app 端 `Article` 实体与两个 Response DTO 有改动，需回归
- TC-article-WEB-003: 文章表单富文本编辑与栏目多选回显——表单新增三个输入块，确认原有控件与保存流程未受影响

## 执行汇总

**IT（2026-08-25，api-test-runner，admin `http://localhost:8080` / app `http://localhost:8081`）**：总数 19 / ✅ 19 / ❌ 0 / ⚠️ 0 / 未执行 0。
新增 IT-015~019 全通过；修改 IT-011 通过；重测 IT-001~010、012~014 全通过，无字段映射回归。
存证：`test-evidence/article-cover-title-intro-tags/TC-article-IT-0{01..19}/`。
契约说明：api-spec.json 的 article operation 只声明 request schema、不声明 response，故响应 schema 校验记「契约未声明，跳过」——属契约既有粒度，非漂移。

**WEB（2026-08-25，web-test-runner，前端 `http://100.100.117.79:5174/love-space/`，后端 `http://100.100.117.79:21423`）**：总数 4 / ✅ 4 / ❌ 0 / 未执行 0。
新增 WEB-004、005 通过；重测 WEB-002 通过（列表列头实测 `["图片","封面标题","文章标题","关联栏目","权重","状态","操作"]`）；回归 WEB-003 通过。
表单 fieldset 顺序实测 `["基础信息","文章标签","关联栏目（多选）","文章内容"]`，必填星号仅在文章图片与文章标题上。
存证：`test-evidence/article-cover-title-intro-tags/TC-article-WEB-00{2..5}/`。
执行偏差：`playwright-company` MCP 不可达，改用本机 playwright-core headless chromium 直驱，访问地址与断言口径不变。

**追溯矩阵**：21/21 ✅（100%），正反向覆盖完整、无悬空用例。
