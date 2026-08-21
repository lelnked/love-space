# operation-log Specification

## Purpose
运营操作审计：@OperationLog 横切留痕（覆盖 16 个模块共 60 个写接口）、切面取值与敏感字段脱敏、异步落库与失败隔离，以及日志分页查询。
## Requirements
### Requirement: 运营写操作留痕
admin 端 SHALL 通过 `@OperationLog("<module>:<action>")` 方法注解对运营写操作留痕，覆盖全部 16 个业务模块共 60 个写接口（城市、分类、标签、商户、商户评价、Banner、推荐清单、大使、路线、活动、文章栏目、文章、精选推荐、周期推荐、账号管理、上传凭证，以及登出）。

`POST /api/admin/auth/login` SHALL NOT 留痕——登录成功时安全上下文尚未填充，切面取不到操作人。这是当前唯一不留痕的写接口。

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

### Requirement: 留痕字段取值与敏感信息脱敏
留痕切面 SHALL 按以下规则取值：

- **module / action**：由注解值按第一个冒号拆分；无冒号时 module 取整串、action 为空串。
- **操作人**：从安全上下文取当前 Manager 的 id 与用户名，两者一并落库（用户名冗余存储，使账号删除后日志仍可读）。
- **target**：取方法入参中**第一个 UUID 类型参数**的字符串值；无 UUID 入参时为 null。
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

### Requirement: 操作日志查询
admin 端 SHALL 提供 `GET /api/admin/logs/page` 分页查询操作日志。该接口**不限 ADMIN 角色**，任何已登录账号均可访问。该域仅此一个接口，无详情、无导出、无删除。

过滤参数：`username`（**模糊匹配**，去除首尾空白，空白视为不传）、`module`（**精确匹配**，同样去空白）、`createdAtFrom` / `createdAtTo`（创建时间，**均含边界**）。无 action、target、操作人 id 的过滤。

排序 SHALL 固定为创建时间倒序，客户端指定的排序参数无效。分页遵循项目统一口径：`page` 从 1 开始，`size` 仅接受 20 与 30，其余值校正为 20。

响应条目字段为 `{id, username, module, action, target, createdAt}`——**不含 payload**。payload 只写不读，无任何接口可查询。

日志 SHALL 永久保留，当前无留存期、归档或清理策略。

#### Scenario: 按操作人与模块组合过滤
- **GIVEN** 日志中存在 admin 操作 city 与 banner 模块的多条记录
- **WHEN** 以 `username=admin` 且 `module=city` 查询
- **THEN** 返回 200，仅含 city 模块记录

#### Scenario: 操作人过滤为模糊匹配
- **GIVEN** 存在用户名 `admin` 与 `admin2` 的操作记录
- **WHEN** 以 `username=admin` 查询
- **THEN** 两个账号的记录都出现在结果中

#### Scenario: 时间区间含边界
- **GIVEN** 一条创建时间恰为 T 的日志
- **WHEN** 以 `createdAtFrom=T` 且 `createdAtTo=T` 查询
- **THEN** 该条记录出现在结果中

#### Scenario: 响应不含 payload
- **GIVEN** 日志中存在带 payload 的记录
- **WHEN** 查询日志列表
- **THEN** 返回条目仅含 id、username、module、action、target、createdAt 六个字段

#### Scenario: 非 ADMIN 角色可查询日志
- **GIVEN** 以 MEMBER 角色账号登录
- **WHEN** 请求日志列表
- **THEN** 返回 200（不返回 403）

### Requirement: web 端操作日志页面
web 端 SHALL 在 `/logs` 提供操作日志页，导航入口对**所有已登录角色可见**（不按角色隐藏）。

筛选栏含四项：「操作人」（文本，模糊匹配）、「模块」（下拉）、「时间起」、「时间止」（日期选择）。日期筛选 SHALL 按本地日历日取整——起始日转为当日零点、截止日转为当日 23:59:59。

列表列为时间、操作人、模块、动作、对象（空显示 `-`），其中模块与动作按中文映射展示，未命中映射时回落显示原始英文值。

分页器位于表格下方，默认每页 20 条；**页面不提供每页条数切换控件**。筛选与重置操作均回到第 1 页。

模块下拉当前仅覆盖 8 个一期模块（管理员、城市、分类、标签、商户、商户评价、Banner、认证），二期模块（路线、活动、文章、精选推荐等）不在选项中，无法从界面按其筛选。

#### Scenario: 按操作人筛选日志
- **GIVEN** 已登录且日志中有多个操作人的记录
- **WHEN** 在操作人输入框填入某账号名并查询
- **THEN** 列表仅展示该操作人的记录，分页回到第 1 页

#### Scenario: 模块与动作按中文展示
- **GIVEN** 日志中存在 `module=city`、`action=create` 的记录
- **WHEN** 进入操作日志页
- **THEN** 该行模块列显示「城市」、动作列显示「创建」

#### Scenario: 未映射的动作回落显示原值
- **GIVEN** 日志中存在二期模块产生的记录（其动作未在前端映射表中）
- **WHEN** 进入操作日志页
- **THEN** 该行动作列显示原始英文值，页面不报错

#### Scenario: 对象为空的记录显示占位符
- **GIVEN** 日志中存在一条创建类记录（target 为 null）
- **WHEN** 查看该行
- **THEN** 对象列显示 `-`
