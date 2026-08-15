# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/{domain}/{it,web,app}.md`（按 modules.md「端」列裁决落点；living 文件，runner 独占回写状态）。
> 生成规则见 schema instruction；用例块格式如下（写入 living 文件时使用）：
>
> ```markdown
> ### TC-{domain}-IT-NNN: <METHOD /path 用例标题>
> **关联需求**: {domain}/{Requirement 名}#{Scenario 名}
> **关联契约**: api-spec.json#/paths/~1<path>/<method>
> **来源**: <change-id>
> **优先级**: P0/P1/P2
> **测试步骤**:
> 1. <请求/操作>
> **预期结果**: <状态码 + 字段断言，可验证>
> **状态**: ⬜ 未测试
> **执行方式**: api-test-runner
> **执行存证**: `test-evidence/regression/{domain}/TC-{domain}-IT-NNN/`
> **最后更新**: -
> ```
>
> WEB 用例：`执行方式: web-test-runner（@playwright/mcp）`，无 `关联契约`，
> 预期结果按线框区域口径断言（layout），多 `**前置条件**` 字段。
> APP 用例：`执行方式: app-test-runner（Maestro）`，口径同 WEB（无关联契约、layout 断言、前置条件），
> 执行形态另落 `tests/{domain}/flows/` 的 Maestro flow yaml。

## 新增用例

<!-- - TC-{domain}-IT-NNN: 标题（ADDED Scenario: {domain}/{Requirement}#{Scenario}） -->

## 修改用例

<!-- - TC-{domain}-WEB-NNN: 标题（MODIFIED: 原因） -->

## 需重测用例

<!-- 行为未变但受本 change 实现影响、需要回归确认的既有 TC -->

## 执行汇总

<!-- runner 跑完后由编排 skill 填写：总数 / ✅ / ❌ / 未执行 -->
