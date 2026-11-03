## MODIFIED Requirements

### Requirement: 生成接口输入长度限制
app 端 SHALL 校验 `inputs`：每个值（字符串原值或非字符串的 JSON 文本）≤ 2000 字符，整体 JSON 序列化后 ≤ 20000 字符（按字符数计，不按 UTF-8 字节数）；超出返回 400 并指出超长的变量名（整体超长时提示整体超限）。限制值为代码常量。

#### Scenario: 单个变量超长
- **GIVEN** 有效请求头
- **WHEN** inputs 中 `user_input` 为 2001 个字符
- **THEN** 返回 400，信息包含 `user_input`，不写记录

#### Scenario: 边界值通过
- **GIVEN** 有效请求头与可用场景
- **WHEN** inputs 中 `user_input` 恰为 2000 个字符且整体 ≤ 20000 字符
- **THEN** 通过长度校验

#### Scenario: 整体超长
- **GIVEN** 有效请求头
- **WHEN** inputs 含 11 个各 1900 字符的变量（整体 > 20000 字符）
- **THEN** 返回 400，提示整体超限，不写记录

#### Scenario: 中文按字符计
- **GIVEN** 有效请求头与可用场景
- **WHEN** inputs 中 `user_input` 为 2000 个中文字符（UTF-8 约 6000 字节，整体字符数 ≤ 20000）
- **THEN** 通过长度校验
