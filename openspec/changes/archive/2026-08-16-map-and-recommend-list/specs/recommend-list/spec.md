# recommend-list 规格增量（map-and-recommend-list）

## ADDED Requirements

### Requirement: 推荐清单管理
admin 端 SHALL 提供推荐清单 CRUD：清单含标题（必填）、介绍、所属城市（必填、创建后不可改）、排序号 sortOrder；删除为物理删除（清单无上架/下架状态）。

#### Scenario: 创建清单
- **GIVEN** 已登录的 Manager 与一个已存在的城市
- **WHEN** 提交标题、介绍、所属城市、排序号创建清单
- **THEN** 创建成功，清单详情返回全部字段

#### Scenario: 缺少必填项被拒绝
- **GIVEN** 已登录的 Manager
- **WHEN** 创建清单时缺少标题或所属城市
- **THEN** 返回 400 及中文校验错误信息

#### Scenario: 删除清单
- **GIVEN** 一个已存在的清单（含商户关联）
- **WHEN** Manager 删除该清单
- **THEN** 清单与其商户关联关系被物理删除，商户本身不受影响

#### Scenario: 清单列表按排序号升序
- **GIVEN** 同一城市下多个清单，sortOrder 各异
- **WHEN** 查询该城市的清单列表（admin 或 app）
- **THEN** 结果按 sortOrder 从小到大排列

### Requirement: 清单内商户维护
清单 SHALL 支持添加/删除商户并为每条关联维护排序号；仅允许添加清单所属城市的商户，展示按关联排序号升序。

#### Scenario: 添加本城市商户
- **GIVEN** 一个清单与其所属城市下的商户
- **WHEN** Manager 把该商户加入清单并指定排序号
- **THEN** 添加成功，清单详情中商户按排序号升序出现

#### Scenario: 拒绝跨城市商户
- **GIVEN** 一个清单与一个属于其他城市的商户
- **WHEN** Manager 尝试把该商户加入清单
- **THEN** 返回 400 及中文业务错误信息，关联不建立

#### Scenario: 重复添加同一商户被拒绝
- **GIVEN** 清单内已含某商户
- **WHEN** 再次添加同一商户
- **THEN** 返回 400 及中文业务错误信息

#### Scenario: 从清单移除商户
- **GIVEN** 清单内已含某商户
- **WHEN** Manager 将其从清单移除
- **THEN** 关联删除，商户本身不受影响

### Requirement: App 端清单查询
app 端 SHALL 提供按城市查询清单列表与清单详情的只读接口；仅上架城市的清单可见；清单详情内商户按关联排序号升序返回。

#### Scenario: 查询上架城市的清单
- **GIVEN** 某上架城市配有多个清单
- **WHEN** App 按该城市查询清单列表
- **THEN** 返回该城市全部清单，按 sortOrder 升序

#### Scenario: 清单详情返回商户明细
- **GIVEN** 某清单含多个商户
- **WHEN** App 查询该清单详情
- **THEN** 返回清单字段与商户列表（按关联 sortOrder 升序），商户含展示所需字段（名称、图片、推荐理由等）

#### Scenario: 下架城市清单不可见
- **GIVEN** 某城市已下架且配有清单
- **WHEN** App 按该城市查询清单列表或查询其清单详情
- **THEN** 列表不返回数据，详情返回 404

### Requirement: web 端推荐清单管理页面
web 后台 SHALL 提供推荐清单管理页面：按城市筛选的清单列表（含排序号）、新建/编辑表单、清单内商户维护（仅本城市商户可选、可调排序号）、删除确认。

#### Scenario: 清单列表与筛选
- **GIVEN** Manager 已登录 web 后台
- **WHEN** 打开推荐清单页面并按城市筛选
- **THEN** 列表按既有 DataTable 口径展示该城市清单（标题、所属城市、排序号、商户数），按 sortOrder 升序

#### Scenario: 维护清单商户
- **GIVEN** Manager 在清单编辑界面
- **WHEN** 添加商户（下拉仅出现该清单所属城市的商户）并保存
- **THEN** 保存成功，清单商户列表按排序号升序回显

#### Scenario: 删除清单需确认
- **GIVEN** Manager 在清单列表点击删除
- **WHEN** 弹出确认弹窗并确认
- **THEN** 清单被删除并从列表消失；取消则不删除
