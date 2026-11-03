# Proposal: ai-generation-input-limits（放宽 AI 生成接口 inputs 长度限制）

## Why

App 调 `POST /api/app/ai/generations` 时，现在的限制是单个变量 ≤500 字符、`inputs` 整体 ≤4KB（4096 字节）。用户输入稍长就会被 400 拒掉，所以要把单个变量放宽到 2000 字符、整体放宽到 2 万字符。整体的计量方式也从 UTF-8 字节数改成字符数，这样中文内容不会因为 1 个字占 3 字节被提前卡住。

## What Changes

| # | 功能 | 端 | 后台菜单 |
|---|---|---|---|
| F1 | 生成接口 inputs 长度限制放宽：单值 ≤2000 字符，整体序列化后 ≤20000 字符 | app | — |

明确**不做**：
- 不把限制值做成可配置项，仍是代码常量。
- 不改 `X-Anonymous-Id`、`clientRequestId`、`appVersion` 的长度限制。
- 不改 admin 端提示词测试接口（`last_test_inputs`）的校验。
- 不改 nginx 的 body 大小限制：默认 1MB，足够容纳 2 万字符。

---

### F1 生成接口 inputs 长度限制放宽

**规则**
- 单个值的计量方式不变：字符串取原值，非字符串取 JSON 文本，按字符数计，上限从 **500 改为 2000**。超出返回 400「变量 {key} 超过 2000 字符」。
- 整体 `inputs` JSON 序列化后的长度，计量方式**从 UTF-8 字节数改为字符数**，上限从 **4096 字节改为 20000 字符**。超出返回 400「inputs 整体超过 20000 字符」。
- 校验顺序不变：先查请求头，再逐个查值，最后查整体。都在查场景、查额度之前，所以校验失败不会写生成记录。
- 这是**放宽**限制，以前合法的请求现在仍然合法，对客户端不构成破坏性变更。

**表**：没有新表。`loves_ai_generation.inputs` 和 `request_messages` 是 `jsonb` 类型，没有长度上限，不需要迁移。

**API**

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| POST | `/api/app/ai/generations` | body：`inputs?`，每个值 ≤2000 字符，整体序列化后 ≤20000 字符（其余字段不变） | 不变；超长返回 400，文案见上 |

---

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `ai`：改「生成接口输入长度限制」这条需求的上限值和整体计量方式，同步改 3 个场景的边界数据。

## Impact

- **数据库**：无变化
- **love-space-admin**：无变化
- **love-space-app**：改 `AiGenerationService` 的常量和整体校验（字节数改字符数）、`AiGenerationCreateRequest` 的 javadoc、`docs/openapi.json` 中 inputs 的描述，以及对应 UT
- **love-space-web**：无变化
- **契约**：`contracts/api-spec.json` 改 1 个接口的字段描述（`/api/app/ai/generations` 的 `inputs`）
- **测试**：更新 `tests/ai/it.md` 中 TC-ai-IT-060/061 的边界数据
- **部署/环境变量/外部依赖**：无
