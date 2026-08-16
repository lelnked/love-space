# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/{domain}/{it,web}.md`（living 文件，runner 独占回写状态）。

## 新增用例

### article（tests/article/it.md、tests/article/web.md，均新建）

- TC-article-IT-001: POST /api/admin/article-categories 创建栏目（ADDED Scenario: article/文章栏目管理#创建栏目）
- TC-article-IT-002: POST /api/admin/article-categories 缺必填被拒绝（ADDED Scenario: article/文章栏目管理#缺少必填项被拒绝）
- TC-article-IT-003: PUT /api/admin/article-categories/{id} 更新栏目（写接口覆盖，挂 article/文章栏目管理#创建栏目）
- TC-article-IT-004: DELETE /api/admin/article-categories/{id} 删除栏目不影响文章数据（ADDED Scenario: article/文章栏目管理#删除栏目不影响文章数据）
- TC-article-IT-005: POST /api/admin/articles 创建关联多栏目的完整文章（ADDED Scenario: article/文章管理#创建文章）
- TC-article-IT-006: POST /api/admin/articles 缺必填或栏目不存在被拒绝（ADDED Scenario: article/文章管理#缺少必填项被拒绝）
- TC-article-IT-007: PUT /api/admin/articles/{id} 更新文章与栏目关联（写接口覆盖，挂 article/文章管理#创建文章）
- TC-article-IT-008: PUT /api/admin/articles/{id}/online 文章上下线切换（ADDED Scenario: article/文章管理#文章上下线切换）
- TC-article-IT-009: DELETE /api/admin/articles/{id} 物理删除文章（写接口覆盖，挂 article/文章管理#创建文章）
- TC-article-IT-010: POST /api/admin/articles 富文本 img src 存 objectKey、admin 读时替换签名 URL（ADDED Scenario: article/文章管理#创建文章）
- TC-article-IT-011: GET /api/app/article-categories 与 /api/app/articles 均按权重升序（ADDED Scenario: article/App 端文章查询#查询栏目与文章列表）
- TC-article-IT-012: GET /api/app/articles 下线文章不可见、详情 404（ADDED Scenario: article/App 端文章查询#下线文章不可见）
- TC-article-IT-013: GET /api/app/articles/{id} 失去所有栏目的文章不可见（ADDED Scenario: article/App 端文章查询#失去所有栏目的文章不可见）
- TC-article-IT-014: GET /api/app/articles/{id} 详情返回富文本且 img src 为签名 URL（ADDED Scenario: article/App 端文章查询#文章详情返回富文本）
- TC-article-WEB-001: 文章栏目页新增与删除（ADDED Scenario: article/web 端文章管理页面#栏目管理增删改）
- TC-article-WEB-002: 文章列表展示与上下线开关（ADDED Scenario: article/web 端文章管理页面#文章列表与上下线）
- TC-article-WEB-003: 文章表单富文本编辑与栏目多选回显（ADDED Scenario: article/web 端文章管理页面#文章表单富文本编辑）

### featured（tests/featured/it.md、tests/featured/web.md，均新建）

- TC-featured-IT-001: POST /api/admin/featured-items 创建精选推荐（ADDED Scenario: featured/精选推荐管理#创建精选推荐）
- TC-featured-IT-002: POST /api/admin/featured-items 缺 banner 或城市不存在被拒绝（ADDED Scenario: featured/精选推荐管理#缺少必填项被拒绝）
- TC-featured-IT-003: PUT /api/admin/featured-items/{id}/online 上下线切换（ADDED Scenario: featured/精选推荐管理#精选推荐上下线切换）
- TC-featured-IT-004: PUT /api/admin/featured-items/{id} 更新条目且 cityId 不可变（写接口覆盖，挂 featured/精选推荐管理#创建精选推荐）
- TC-featured-IT-005: DELETE /api/admin/featured-items/{id} 物理删除（写接口覆盖，挂 featured/精选推荐管理#创建精选推荐）
- TC-featured-IT-006: GET /api/app/featured-items 信息流仅含上线条目且按创建时间倒序（ADDED Scenario: featured/App 端精选推荐查询#查询精选推荐信息流）
- TC-featured-WEB-001: 精选推荐列表展示与上下线开关（ADDED Scenario: featured/web 端精选推荐页面#精选推荐列表与上下线）
- TC-featured-WEB-002: 弹窗表单新增精选推荐（ADDED Scenario: featured/web 端精选推荐页面#新增精选推荐）

### city（tests/city/it.md、tests/city/web.md，增量追加）

- TC-city-IT-007: 城市下架后 app 端精选推荐不可见（级联）（ADDED Scenario: city/地图下架对精选推荐级联生效#下架城市后 app 端精选推荐不可见）
- TC-city-WEB-004: 城市下架确认提示包含精选推荐级联说明（ADDED Scenario: city/地图下架对精选推荐级联生效#web 下架确认提示包含精选推荐）

## 修改用例

（无）

## 需重测用例

（无——featured 信息流与 Banner 模块独立，article 为全新域，不触及既有用例行为）

## 执行汇总

- **IT（api-test-runner，2026-08-16）**：21 / ✅ 21 / ❌ 0 / 未执行 0。首轮 2 例失败已修复复测：TC-article-IT-004（admin 详情返回悬空栏目 id——ArticleService 读取端补过滤，设计决策 11）、TC-featured-IT-001（预期修订为 admin 响应只含 cityId、城市名由 web 端映射，设计决策 12）。存证 `test-evidence/article-and-featured-feed/<TC-ID>/`。
- **WEB（web-test-runner，2026-08-16）**：6 / ✅ 6 / ❌ 0 / 未执行 0。playwright-core + headless chromium 经 http://100.100.117.79:5173/love-space/ 实测。两点说明：① test profile 无真实 OSS 绑定，签名 URL 404 属环境限制，TC-article-WEB-003 图片渲染降级为结构断言（img src 为后端签名 URL，见存证 note-env-limitation.txt）；② 文章表单/精选弹窗保存成功无 toast，以「表单跳回列表/弹窗关闭+列表刷新」作为可见成功反馈判定通过，与 delta spec 口径一致。
- **追溯矩阵**：27 用例全绿，正反向覆盖完整、无悬空用例（traceability-matrix.md，无 ⚠）。
- **质量门禁**：web lint/build/npm audit（0 漏洞）、admin UT/IT/build、app UT/IT/build 九项必选全绿。
