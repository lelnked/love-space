# city Specification

## Purpose
TBD - created by archiving change map-and-recommend-list. Update Purpose after archive.
## Requirements
### Requirement: 地图编辑说
城市（地图）SHALL 支持「编辑说」字段：文本、最长 200 字、非必填。

#### Scenario: admin 保存编辑说
- **GIVEN** 已登录的 Manager
- **WHEN** 通过 admin 城市创建或更新接口提交 `editorNote`（≤200 字）
- **THEN** 字段被保存，城市详情接口原样返回该字段

#### Scenario: 编辑说超长被拒绝
- **GIVEN** 已登录的 Manager
- **WHEN** 提交的 `editorNote` 超过 200 字
- **THEN** 返回 400 及中文校验错误信息

#### Scenario: app 端城市数据返回编辑说
- **GIVEN** 某上架城市已配置编辑说
- **WHEN** App 调用城市列表/详情接口
- **THEN** 响应包含 `editorNote` 字段

### Requirement: 后台入口更名为地图管理
web 后台 SHALL 将「城市管理」入口与页面标题更名为「地图管理」（仅文案口径；路由、代码标识、接口路径不变）。

#### Scenario: 侧栏与页面标题展示地图管理
- **GIVEN** Manager 已登录 web 后台
- **WHEN** 查看侧栏导航与城市列表页
- **THEN** 侧栏菜单项与页面标题显示「地图管理」，路由仍为既有 `/cities` 路径

### Requirement: 地图下架对推荐清单级联生效
城市（地图）下架后，其名下推荐清单 SHALL 对 App 端不可见；web 端下架确认提示 SHALL 说明级联范围包含推荐清单。

#### Scenario: 下架城市后 app 端清单不可见
- **GIVEN** 某上架城市配有推荐清单
- **WHEN** Manager 将该城市下架，App 再查询该城市的推荐清单
- **THEN** App 端接口不返回该城市的清单（列表为空或城市不可见）

#### Scenario: web 下架确认提示包含清单
- **GIVEN** Manager 在 web 城市（地图）列表点击下架
- **WHEN** 弹出确认提示
- **THEN** 提示文案说明将级联影响商户、Banner 与推荐清单

### Requirement: 地图下架对精选推荐级联生效
城市（地图）下架后，关联该城市的精选推荐 SHALL 在 app 端信息流中不可见；web 端下架确认提示 SHALL 在既有「商户、Banner、推荐清单」口径上补充「精选推荐」。精选推荐中的活动条目 SHALL NOT 因城市下架而被过滤——活动可见性只取决于活动自身上线状态。

#### Scenario: 下架城市后 app 端精选推荐不可见
- **GIVEN** 某上架城市关联有上线的精选推荐，随后该城市被下架
- **WHEN** app 端查精选推荐信息流列表
- **THEN** 列表不含该城市的推荐条目

#### Scenario: web 下架确认提示包含精选推荐
- **GIVEN** 地图管理页存在一个上架城市
- **WHEN** 点击下架该城市
- **THEN** 确认弹窗文案包含商户、Banner、推荐清单、精选推荐的级联下架说明

#### Scenario: 下架城市不过滤精选中的活动条目
- **GIVEN** 精选信息流中有一个上线活动条目，随后系统中全部城市被下架
- **WHEN** app 端查精选推荐信息流列表
- **THEN** 列表仍含该活动条目

### Requirement: 城市下存在路线时禁止删除
admin 端删除城市前 SHALL 校验该城市下是否仍有路线；若存在关联路线，删除 SHALL 被拒绝并返回 400 及中文业务错误，提示先处理该城市下的路线。此约束防止路线的 `cityId` 悬空。

#### Scenario: 有路线的城市不能删除
- **GIVEN** 一个城市下存在至少 1 条路线
- **WHEN** admin 端删除该城市
- **THEN** 返回 400 及中文业务错误；再查该城市详情仍返回 200

#### Scenario: 路线清空后可删除城市
- **GIVEN** 一个城市下原有 1 条路线，该路线已被删除
- **WHEN** admin 端删除该城市
- **THEN** 返回 200；再查该城市详情返回 400

### Requirement: 地图下架对路线与活动均不级联
城市（地图）下架 SHALL NOT 影响该城市下路线的可见性——路线可见性只取决于其关联大使是否上线。城市下架 SHALL NOT 影响活动的可见性——活动不再关联地图，其可见性只取决于活动自身上线状态。web 端下架确认提示 SHALL 说明级联范围为商户、Banner、推荐清单、精选推荐，且**不含**路线与活动。

#### Scenario: 下架城市后 app 端路线仍可见
- **GIVEN** 一个城市下有关联大使已上线的路线，随后该城市被下架
- **WHEN** app 端按该城市查路线列表及路线详情
- **THEN** 列表仍包含该路线；详情返回 200

#### Scenario: 下架城市后 app 端活动仍可见
- **GIVEN** 存在上线活动，随后系统中全部城市被下架
- **WHEN** app 端查活动列表及活动详情
- **THEN** 列表仍包含该活动；详情返回 200

#### Scenario: web 下架确认提示不含路线与活动
- **GIVEN** 地图管理页存在一个上架城市
- **WHEN** 点击下架该城市
- **THEN** 确认弹窗文案包含商户、Banner、推荐清单、精选推荐的级联下架说明，且**不含**路线与活动
