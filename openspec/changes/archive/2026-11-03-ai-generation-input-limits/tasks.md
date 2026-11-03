## 1. app 实现

- [x] 1.1 `AiGenerationService`：`MAX_INPUT_VALUE_CHARS` 改为 2000；`MAX_INPUTS_BYTES` 改名为 `MAX_INPUTS_CHARS = 20000`，整体校验改为 `writeValueAsString(inputs).length()`，文案改为「inputs 整体超过 20000 字符」（由常量拼出）
- [x] 1.2 `AiGenerationCreateRequest` javadoc 改为「每个值 ≤2000 字符，整体序列化后 ≤20000 字符」

## 2. 契约与文档

- [x] 2.1 `contracts/api-spec.json` 中 `inputs` 的 description 改为新上限
- [x] 2.2 `love-space-app/docs/openapi.json` 中 `inputs` 的 description 改为新上限

## 3. UT（AiGenerationTest）

- [x] 3.1 `valueTooLong` 改为 2001 字符（@scenario: ai/生成接口输入长度限制#单个变量超长）
- [x] 3.2 `valueAtLimitPasses` 改为 2000 字符（@scenario: ai/生成接口输入长度限制#边界值通过）
- [x] 3.3 `inputsTooLarge` 改为 11 × 1900 字符，并断言 message 提示整体超限（@scenario: ai/生成接口输入长度限制#整体超长）
- [x] 3.4 新增 `chineseCountedByChars`：2000 个中文字符通过（@scenario: ai/生成接口输入长度限制#中文按字符计）
- [x] 3.5 跑 app 端 `AiGenerationTest` 全绿
