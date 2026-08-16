# featured Specification

## Purpose
精选·地图上新推荐：admin 端维护城市推荐条目，app 端只读信息流展示（需求文档 §7.1）。
## Requirements
### Requirement: 精选推荐管理
admin 端 SHALL 提供精选·地图上新推荐 CRUD：关联地图（城市）单选（创建时必选，创建后不可变）、banner 图片 1 张（必填，比例不做校验）、推荐说明（文本）、上线/下线状态。删除为物理删除。与既有 Banner 模块相互独立。

#### Scenario: 创建精选推荐
- **GIVEN** 存在上架城市
- **WHEN** 提交关联该城市、含 banner 图与推荐说明的推荐条目
- **THEN** 返回 200，详情含关联城市、banner 签名 URL、说明与状态

#### Scenario: 缺少必填项被拒绝
- **GIVEN** 已登录 admin
- **WHEN** 提交缺 banner 图或城市不存在的推荐条目
- **THEN** 返回 400 及中文业务错误

#### Scenario: 精选推荐上下线切换
- **GIVEN** 一个上线推荐条目
- **WHEN** 将其下线
- **THEN** 返回 200，详情 online=false

### Requirement: App 端精选推荐查询
app 端 SHALL 提供只读的精选推荐信息流列表：对所有用户生效，按创建时间倒序，条目含 banner 签名 URL、推荐说明与关联城市数据（id 与名称，供 App 端自行决定跳转）。仅当条目上线**且**关联城市上架时可见。

#### Scenario: 查询精选推荐信息流
- **GIVEN** 上架城市下有一条上线推荐、一条下线推荐
- **WHEN** app 端查精选推荐列表
- **THEN** 返回 200，仅含上线条目，含 banner、说明与关联城市 id/名称

### Requirement: web 端精选推荐页面
web 端 SHALL 提供「精选推荐」后台页面：DataTable 列表（banner 图/关联城市/推荐说明/状态/操作）+ 弹窗表单（城市单选、banner 上传、说明文本）+ 上下线开关 + 删除确认弹窗。

#### Scenario: 精选推荐列表与上下线
- **GIVEN** 已登录后台且存在推荐条目
- **WHEN** 进入精选推荐页并切换某条目状态开关
- **THEN** 列表展示 banner、关联城市、说明，切换后状态即时更新并有成功提示

#### Scenario: 新增精选推荐
- **GIVEN** 已登录后台
- **WHEN** 在弹窗表单选择城市、上传 banner、填写说明并提交
- **THEN** 保存成功，列表出现新条目
