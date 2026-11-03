## Context

`AiGenerationService.validate` 现在校验单个值 `PromptRenderer.toText(v).length() ≤ 500`，整体校验 `writeValueAsString(inputs).getBytes(UTF_8).length ≤ 4096`。存储列 `inputs`、`request_messages` 都是 `jsonb`，没有长度上限。

## Goals / Non-Goals

**Goals:** 单个值上限改成 2000 字符；整体上限改成 20000 字符，计量方式从字节改为字符。

**Non-Goals:** 限制值不做成可配置项；不改其他字段的限制；不改 admin 端提示词测试接口；不改 nginx。

## Decisions

- **整体按 `String.length()` 计**：和单个值的计法保持一致，用户说的是「字符」。不按 `codePointCount` 计，理由是现有单值校验已经用 `length()`，两边口径要统一。代价是 emoji 这类代理对会算 2 个字符，影响可以忽略。
- **保留代码常量**：`MAX_INPUT_VALUE_CHARS = 2000`，`MAX_INPUTS_CHARS = 20000`（原来的 `MAX_INPUTS_BYTES` 改名，因为计量单位变了）。报错文案里的上限数字从常量拼出来，不再写死「4KB」。
- **契约**：`contracts/api-spec.json` 的 `AiGenerationCreateRequest.inputs` 只改 description。这个接口已经有 `x-requirement: ai/App 生成接口` 反链，不新增 operation。
- **已定决策**：用户已确认「整体 2 万字符」按字符数计，不按字节数计（2026-11-03）。

## Risks / Trade-offs

- [单次请求的 prompt 变长，模型 token 消耗和延迟会上升] → 每日额度和 nginx IP 限流不变，可以继续兜底；超时参数由后台的模型配置控制。
- [请求体最大约 20000 字符 × 3 字节，约 60KB] → 远低于 nginx 默认 1MB 的 body 限制。

## Migration Plan

无数据迁移。直接部署 app 即可；要回滚就部署上一个版本。
