# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单，也就是交付验证的执行范围。
> 用例本体在 `tests/ai/it.md`。

## 新增用例

- TC-ai-IT-103: 2000 个中文字符通过长度校验（ADDED Scenario: ai/生成接口输入长度限制#中文按字符计）

## 修改用例

- TC-ai-IT-059: 单个变量 2001 字符返回 400（MODIFIED：上限 500 → 2000）
- TC-ai-IT-060: 单个变量恰 2000 字符通过长度校验（MODIFIED：上限 500 → 2000）
- TC-ai-IT-061: inputs 整体超过 20000 字符返回 400（MODIFIED：4KB 字节 → 20000 字符）

## 需重测用例

（无）

## 执行汇总

2026-11-03 交付轮（app `http://localhost:8081` + 假方舟 mock-ok）：总数 4 / ✅ 4 / ❌ 0 / 未执行 0。存证在 `test-evidence/ai-generation-input-limits/`。
⚠️ 契约漂移（这次之前就存在）：`/api/app/ai/generations` 的 post 没有声明 `responses`，不算失败。
