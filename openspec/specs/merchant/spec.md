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

