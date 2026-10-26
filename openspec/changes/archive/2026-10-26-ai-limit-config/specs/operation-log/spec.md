## MODIFIED Requirements

### Requirement: 运营写操作留痕
admin 端 SHALL 通过 `@OperationLog("<module>:<action>")` 方法注解对运营写操作留痕，覆盖全部 20 个业务模块共 69 个写接口（城市、分类、标签、商户、商户评价、Banner、推荐清单、大使、路线、活动、文章栏目、文章、精选推荐、周期推荐、账号管理、上传凭证、AI 场景 `ai-scene`、AI 提示词 `ai-prompt`、AI 生成记录 `ai-generation`、AI 限流配置 `ai-limit`，以及登出）。

以下写接口 SHALL NOT 留痕：
- `POST /api/admin/auth/login`——登录成功时安全上下文尚未填充，切面取不到操作人；
- `POST /api/admin/ai/prompt-templates/{id}/test` 与 `POST /api/admin/ai/models/test-connection`——测试类调用，不改变业务配置。

留痕 SHALL 在目标方法**成功返回后**执行；方法抛出异常时 SHALL NOT 记录日志。未登录（取不到操作人）时 SHALL 跳过记录。

日志记录 SHALL 异步落库，且任何环节失败（组装、序列化、持久化）SHALL NOT 影响主业务返回，仅记录警告日志。

#### Scenario: 创建城市后异步留痕
- **GIVEN** 以 admin 身份登录
- **WHEN** 创建一个城市，随后查询操作日志
- **THEN** 存在一条 `module=city`、`action=create`、`username=admin` 的记录

#### Scenario: 业务方法失败时不留痕
- **GIVEN** 以 admin 身份登录
- **WHEN** 提交一个会被业务校验拒绝的写请求（返回 400）
- **THEN** 操作日志中不新增对应记录

#### Scenario: 登录不产生日志
- **GIVEN** 日志表中无 auth 模块记录
- **WHEN** 执行一次成功登录
- **THEN** 操作日志中不出现 `module=auth`、`action=login` 的记录

#### Scenario: AI 场景创建留痕
- **GIVEN** 以 admin 身份登录
- **WHEN** 创建一个 AI 场景，随后查询操作日志
- **THEN** 存在一条 `module=ai-scene`、`action=create`、`username=admin` 的记录

#### Scenario: AI 测试类调用不留痕
- **GIVEN** 以 admin 身份登录
- **WHEN** 调用模型测试连接与模板测试，随后查询操作日志
- **THEN** 操作日志中不新增 `ai-scene` / `ai-prompt` 模块的记录

#### Scenario: AI 生成记录评价留痕
- **GIVEN** 以 admin 身份登录
- **WHEN** 评价一条 AI 生成记录，随后查询操作日志
- **THEN** 存在一条 `module=ai-generation`、`action=rate`、`username=admin` 的记录

#### Scenario: AI 限流配置修改留痕
- **GIVEN** 以 admin 身份登录
- **WHEN** 修改一项 AI 限流配置，随后查询操作日志
- **THEN** 存在一条 `module=ai-limit`、`action=update`、`username=admin` 的记录

### Requirement: 留痕字段取值与敏感信息脱敏
留痕切面 SHALL 按以下规则取值：

- **module / action**：由注解值按第一个冒号拆分；无冒号时 module 取整串、action 为空串。
- **操作人**：从安全上下文取当前 Manager 的 id 与用户名，两者一并落库（用户名冗余存储，使账号删除后日志仍可读）。
- **target**：取方法入参中**第一个 UUID 类型参数**的字符串值；无 UUID 入参时取**第一个字符串类型参数**（如 AI 限流配置的 `{key}`）；两者都没有时为 null。
- **payload**：取方法入参中第一个非 null、非 UUID、非字符串/数字/布尔的参数，序列化为 JSON。

payload SHALL 递归脱敏：键名匹配 `password` / `secret` / `token`（不区分大小写）的字段，其值整体替换为 `[REDACTED]`，无论原值是标量、对象还是数组。

上述 target 取值规则导致两类**已知取值特征**，属现行为：创建类操作（POST，无 UUID 入参）的 target 恒为 null；嵌套路径下的操作（如商户评价 `/merchants/{merchantId}/reviews/{id}`）target 取到的是父级 id 而非自身 id。

#### Scenario: 密码字段被脱敏
- **GIVEN** 以 ADMIN 身份登录
- **WHEN** 创建一个运营账号（请求体含 password）
- **THEN** 对应日志记录的 payload 中 password 字段值为 `[REDACTED]`，明文不落库

#### Scenario: 创建类操作的 target 为空
- **GIVEN** 以 admin 身份登录
- **WHEN** 创建一个城市
- **THEN** 对应日志记录的 `target` 为 null

#### Scenario: 更新类操作记录目标 id
- **GIVEN** 一个已存在的城市
- **WHEN** 更新该城市
- **THEN** 对应日志记录的 `target` 为该城市 id

#### Scenario: 嵌套资源的 target 取父级 id
- **GIVEN** 一个商户及其下的一条评价
- **WHEN** 更新该条评价
- **THEN** 对应日志记录的 `target` 为**商户** id，而非评价 id

#### Scenario: 字符串路径参数作为 target
- **GIVEN** 以 admin 身份登录
- **WHEN** 请求 `PUT /api/admin/ai/limits/GLOBAL_DAILY`
- **THEN** 对应日志记录的 `target` 为 `GLOBAL_DAILY`
