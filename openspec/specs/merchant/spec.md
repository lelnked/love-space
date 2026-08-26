# merchant Specification

## Purpose
TBD - created by archiving change map-and-recommend-list. Update Purpose after archive.
## Requirements
### Requirement: 商户编辑推荐理由
商户 SHALL 支持「编辑推荐理由」字段：纯文本、最长 2000 字、非必填，仅供新版客户端展示。

#### Scenario: admin 创建/更新商户时保存推荐理由
- **GIVEN** 已登录的 Manager
- **WHEN** 通过 admin 商户创建或更新接口提交 `recommendReason`（≤2000 字）
- **THEN** 字段被保存，商户详情接口原样返回该字段

#### Scenario: 推荐理由超长被拒绝
- **GIVEN** 已登录的 Manager
- **WHEN** 提交的 `recommendReason` 超过 2000 字
- **THEN** 返回 400 及中文校验错误信息，商户不被保存

#### Scenario: 推荐理由可为空
- **GIVEN** 已登录的 Manager
- **WHEN** 创建商户时不填 `recommendReason`
- **THEN** 创建成功，详情返回的 `recommendReason` 为空

#### Scenario: app 端商户详情返回推荐理由
- **GIVEN** 某上架商户已配置推荐理由
- **WHEN** App 调用商户详情接口
- **THEN** 响应包含 `recommendReason` 字段

#### Scenario: web 商户表单录入推荐理由
- **GIVEN** Manager 在 web 后台商户新建/编辑表单
- **WHEN** 在「编辑推荐理由」多行文本框输入内容并保存
- **THEN** 保存成功后重新打开编辑表单回显该内容；输入超过 2000 字时表单按既有校验口径提示错误

### Requirement: App 端带排序号列表的排序口径
app 端凡返回列表且实体带排序号字段的接口，排序 SHALL 统一为「排序号 + `createdAt DESC` tie-break」：
`sortOrder` 型排序号按**升序**（数值小的靠前），`weight` 型排序号按**降序**（权重大的靠前），
两者同序号时一律按 `createdAt` **倒序**（新创建的靠前），以保证同序号记录的相对顺序确定、可重现。
实体无排序号字段的列表不受本要求约束，沿用各自既有口径。

#### Scenario: 分类列表同序号按创建时间倒序
- **GIVEN** 两个上架分类 A、B 的 `sortOrder` 均为 0，B 的创建时间晚于 A
- **WHEN** App 请求 `GET /api/app/categories/page`
- **THEN** 返回 200，B 排在 A 之前

#### Scenario: 商户评价同序号按创建时间倒序
- **GIVEN** 某上架商户下两条评价 A、B 的 `sortOrder` 均为 0，B 的创建时间晚于 A
- **WHEN** App 请求该商户的评价列表
- **THEN** 返回 200，B 排在 A 之前

#### Scenario: 排序号不同时以排序号为准
- **GIVEN** 两个上架分类 A（`sortOrder=1`，创建较晚）与 B（`sortOrder=0`，创建较早）
- **WHEN** App 请求 `GET /api/app/categories/page`
- **THEN** 返回 200，B 排在 A 之前（排序号优先于创建时间）

#### Scenario: weight 型排序号维持降序且已符合口径
- **GIVEN** 某城市下两个上架商户 A、B 的 `weight` 相同，B 的创建时间晚于 A
- **WHEN** App 请求 `GET /api/app/merchants/page`
- **THEN** 返回 200，按 `weight DESC, createdAt DESC` 排列，B 排在 A 之前
