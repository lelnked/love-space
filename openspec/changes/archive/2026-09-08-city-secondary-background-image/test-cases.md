# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/city/{it,web}.md`（living 文件，runner 独占回写状态）。

## 新增用例

- TC-city-IT-015: 创建时设置第二背景图（ADDED: city/城市第二背景图#admin 创建城市时设置第二背景图）
- TC-city-IT-016: 更新与清空第二背景图（ADDED: #admin 更新城市的第二背景图、#第二背景图可清空）
- TC-city-IT-017: 非法 objectKey 被拒绝（ADDED: #非法 objectKey 被拒绝）
- TC-city-IT-018: app 端返回第二背景图（ADDED: #app 端城市数据返回第二背景图、#未配置第二背景图时为 null）
- TC-city-WEB-005: 地图表单可维护第二背景图（ADDED: #web 后台表单维护第二背景图）

## 修改用例

（无）

## 需重测用例

- TC-city-IT-001: POST /api/admin/cities 创建城市保存编辑说（新增列后确认既有创建路径行为未变）

## 执行汇总

- IT：总数 5 ｜ ✅ 5 ｜ ❌ 0（TC-city-IT-015/016/017/018 + 回归 TC-city-IT-001，2026-09-08，存证 `test-evidence/city-secondary-background-image/`）
- 单元/集成（mvn）：admin 146 绿（含 11 IT），app 102 绿（含 21 IT），web `tsc --noEmit` 通过
- WEB：TC-city-WEB-005 未执行 —— playwright MCP（playwright-company）本会话 ConnectionRefused，web-test-runner 不可用
