## MODIFIED Requirements

### Requirement: App 端路线查询
app 端 SHALL 提供只读查询：路线列表（sortOrder 升序，含缩略图、主标题、大使摘要）与路线详情（含图片、地点列表、大使信息、所属城市名称文字字段 `cityName`）。列表 SHALL 支持两个**可选**查询参数：`cityName`（城市中文名）与 `ambassadorId`（爱女大使 ID）；两者都不传时返回全部可见路线，同时传入时取交集。`cityName` 指向的城市记录不存在时列表 SHALL 返回空数组。路线可见性 SHALL 仅取决于关联大使 `online=true`，与所属城市是否上架无关；不可见路线详情返回 404。所属城市记录已被删除时 `cityName` 为 `null`，不影响可见性。

#### Scenario: 查询上架城市的路线
- **GIVEN** 上架城市下有大使上线的路线若干
- **WHEN** app 端按该城市名（`cityName`）查路线列表
- **THEN** 返回 200，仅含该城市路线，按 sortOrder 升序，含缩略图与大使名称

#### Scenario: 不传任何过滤参数返回全部可见路线
- **GIVEN** 多个城市下各有大使上线的路线
- **WHEN** app 端不带任何查询参数请求路线列表
- **THEN** 返回 200，含全部大使在线的路线，按 sortOrder 升序

#### Scenario: 按大使 ID 过滤路线
- **GIVEN** 大使 A 上线且名下有 2 条路线，大使 B 上线且名下有 1 条路线
- **WHEN** app 端带 `ambassadorId=A` 请求路线列表
- **THEN** 返回 200，仅含大使 A 的 2 条路线，按 sortOrder 升序

#### Scenario: 城市名与大使 ID 组合过滤
- **GIVEN** 大使 A 在城市甲有 1 条路线、在城市乙有 1 条路线
- **WHEN** app 端带 `cityName=甲` 且 `ambassadorId=A` 请求路线列表
- **THEN** 返回 200，仅含城市甲下大使 A 的那 1 条路线

#### Scenario: 城市名不存在返回空数组
- **GIVEN** 城市表中不存在名为「不存在城」的城市
- **WHEN** app 端带 `cityName=不存在城` 请求路线列表
- **THEN** 返回 200 且为空数组

#### Scenario: 未上架城市的路线仍可见
- **GIVEN** 一个**下架**城市下有一条关联大使已上线的路线
- **WHEN** app 端按该城市名查路线列表及该路线详情
- **THEN** 列表返回 200 且包含该路线；详情返回 200，`cityName` 为该城市中文名

#### Scenario: 大使下线后路线隐藏
- **GIVEN** 一条路线，其关联大使被下线
- **WHEN** app 端查路线列表（无论是否带过滤参数）及该路线详情
- **THEN** 列表不含该路线；详情返回 404

#### Scenario: 路线详情返回地点明细
- **GIVEN** 一条可见路线含 2 个地点
- **WHEN** app 端查该路线详情
- **THEN** 返回 200，地点按添加顺序返回，每个地点含名称、图片、介绍
