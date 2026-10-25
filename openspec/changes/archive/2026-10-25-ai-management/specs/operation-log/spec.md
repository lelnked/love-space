## MODIFIED Requirements

### Requirement: 运营写操作留痕
admin 端 SHALL 通过 `@OperationLog("<module>:<action>")` 方法注解对运营写操作留痕，覆盖全部 18 个业务模块共 67 个写接口（城市、分类、标签、商户、商户评价、Banner、推荐清单、大使、路线、活动、文章栏目、文章、精选推荐、周期推荐、账号管理、上传凭证、AI 场景 `ai-scene`、AI 提示词 `ai-prompt`，以及登出）。

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
