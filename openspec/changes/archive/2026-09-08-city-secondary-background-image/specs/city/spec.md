## ADDED Requirements

### Requirement: 城市第二背景图
城市（地图）SHALL 支持第二张背景图 `secondaryBackgroundImage`：可空、可新增、可编辑、可清空，取值规则与既有 `backgroundImage` 一致（仅接受 OSS objectKey，形如 `images/<id>.<ext>` 或 `bound/<id>.<ext>`）。两个背景图字段相互独立，任一字段的取值 SHALL NOT 影响另一字段。

#### Scenario: admin 创建城市时设置第二背景图
- **GIVEN** 已登录的 Manager
- **WHEN** 调用 admin 城市创建接口并提交合法 objectKey 的 `secondaryBackgroundImage`
- **THEN** 返回 200，城市详情接口返回 `secondaryBackgroundImage` 为 `{ id, url }` 结构，且 `backgroundImage` 保持原值

#### Scenario: admin 更新城市的第二背景图
- **GIVEN** 一个已有第二背景图的城市
- **WHEN** Manager 通过更新接口提交新的 `secondaryBackgroundImage` objectKey
- **THEN** 返回 200，详情接口返回新图

#### Scenario: 第二背景图可清空
- **GIVEN** 一个已有第二背景图的城市
- **WHEN** Manager 提交 `secondaryBackgroundImage` 为 null 或空串
- **THEN** 返回 200，详情接口该字段为 `null`，`backgroundImage` 不受影响

#### Scenario: 非法 objectKey 被拒绝
- **GIVEN** 已登录的 Manager
- **WHEN** 提交的 `secondaryBackgroundImage` 不是合法 OSS objectKey（如完整 http URL）
- **THEN** 返回 400 及中文校验错误信息

#### Scenario: 未配置第二背景图时为 null
- **GIVEN** 一个从未设置过第二背景图的城市
- **WHEN** 查询 admin 城市列表与详情
- **THEN** `secondaryBackgroundImage` 为 `null`

#### Scenario: app 端城市数据返回第二背景图
- **GIVEN** 某上架城市已配置第二背景图
- **WHEN** App 调用城市列表接口
- **THEN** 响应包含 `secondaryBackgroundImage` 的 `{ id, url }`；未配置的城市该字段为 `null`

#### Scenario: web 后台表单维护第二背景图
- **GIVEN** Manager 在 web 地图管理的新增/编辑表单
- **WHEN** 查看表单
- **THEN** 「背景图」下方存在独立的「第二背景图」上传控件，可上传、替换与清空，保存后回显所选图片
