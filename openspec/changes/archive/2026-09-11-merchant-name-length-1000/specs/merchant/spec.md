## ADDED Requirements

### Requirement: 商户名称长度上限
商户名称 SHALL 最长 **1000 个字符**（按 Unicode codePoint 计数，与 web 表单 `Array.from(name).length` 口径一致），
admin 端创建与更新接口（同一 upsert 路径）共用这一条规则；超限时 SHALL 返回 400 及中文错误信息
「商户名称长度不能超过 1000 个字符」，且请求涉及的商户数据不被创建或修改。

#### Scenario: 名称 1000 字边界通过
- **GIVEN** 已登录的 Manager
- **WHEN** 通过 `POST /api/admin/merchants` 或 `PUT /api/admin/merchants/{id}` 提交恰好 1000 个字符的名称
- **THEN** 返回 200，商户被保存，详情接口回读的名称与提交值长度一致

#### Scenario: 名称超过 1000 字被拒绝
- **GIVEN** 已登录的 Manager
- **WHEN** 提交 1001 个字符的名称（`POST` 创建或 `PUT` 更新）
- **THEN** 返回 400 及中文校验错误信息，商户不被创建/更新

#### Scenario: 名称超长时既有商户数据保持不变
- **GIVEN** 已存在商户 A，其名称为 N
- **WHEN** 对 A 提交超过 1000 字的名称
- **THEN** 返回 400，重新查询 A 的名称仍为 N

#### Scenario: 名称 16~1000 字可保存（原 15 字上限放开）
- **GIVEN** 已登录的 Manager
- **WHEN** 提交长度为 16~1000 之间的名称（例如 23 字的完整门店名）
- **THEN** 返回 200，商户被保存，详情接口原样返回该名称

#### Scenario: web 表单同口径校验
- **GIVEN** Manager 在 web 后台商户新建/编辑表单
- **WHEN** 输入 16~1000 字的名称并保存
- **THEN** 保存成功，重新打开表单时名称原样回显；
- **WHEN** 输入超过 1000 字并保存
- **THEN** 表单按既有校验口径提示「名称最多 1000 个字符」，不发起保存
