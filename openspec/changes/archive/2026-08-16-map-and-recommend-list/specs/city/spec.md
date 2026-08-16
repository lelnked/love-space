# city 规格增量（map-and-recommend-list）

## ADDED Requirements

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
