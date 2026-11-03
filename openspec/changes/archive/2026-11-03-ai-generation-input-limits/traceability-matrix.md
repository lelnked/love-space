# 追溯矩阵（交付核对）：ai-generation-input-limits

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change ai-generation-input-limits`

## 需求与场景
- **ai/生成接口输入长度限制**: 单个变量超长 / 边界值通过 / 整体超长 / 中文按字符计

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-ai-IT-059 | 单个变量 2001 字符返回 400 | ai/生成接口输入长度限制#单个变量超长 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-generation-input-limits | IT | `test-evidence/ai-generation-input-limits/TC-ai-IT-059/` | ✅ |
| TC-ai-IT-060 | 单个变量恰 2000 字符通过长度校验 | ai/生成接口输入长度限制#边界值通过 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-generation-input-limits | IT | `test-evidence/ai-generation-input-limits/TC-ai-IT-060/` | ✅ |
| TC-ai-IT-061 | inputs 整体超过 20000 字符返回 400 | ai/生成接口输入长度限制#整体超长 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-generation-input-limits | IT | `test-evidence/ai-generation-input-limits/TC-ai-IT-061/` | ✅ |
| TC-ai-IT-103 | 2000 个中文字符通过长度校验 | ai/生成接口输入长度限制#中文按字符计 | api-spec.json#/paths/~1api~1app~1ai~1generations/post | ai-generation-input-limits | IT | `test-evidence/ai-generation-input-limits/TC-ai-IT-103/` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：4
- ✅ 通过：4 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
