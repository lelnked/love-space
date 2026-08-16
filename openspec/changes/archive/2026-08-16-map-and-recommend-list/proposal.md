# Proposal: map-and-recommend-list

## Why

二期需求（见 `二期需求开发文档.md` §二/§三）要求把现有「城市」升级为「地图」概念：新版 App 以地图为入口聚合内容。本 change 落地其中互相强耦合的第一块：商户新增编辑推荐理由、城市（地图）新增编辑说、以及全新的「推荐清单」实体（地图下运营编辑的商户清单）。

## What Changes

- **商户**：`loves_merchant` 新增 `recommend_reason`（编辑推荐理由，纯文本 ≤2000 字，非必填）；admin 端 CRUD 支持该字段，app 端商户详情/列表返回该字段；web 商户表单增加输入项。
- **城市（地图）**：`loves_city` 新增 `editor_note`（编辑说，文本 ≤200 字）；admin 端 CRUD 支持该字段，app 端城市接口返回该字段；web 后台「城市管理」入口更名为「地图管理」（路由与代码标识不改，仅文案）。
- **推荐清单（新实体）**：新表 `loves_recommend_list`（标题、介绍、所属城市、排序号）与关联表 `loves_recommend_list_merchant`（清单-商户 + 排序号）。
  - admin 端 CRUD：创建/编辑/删除清单（物理删除，无上下架）、维护清单内商户（仅限本城市商户，带排序号）。
  - app 端查询：按城市取清单列表（按 sortOrder 升序）与清单详情（商户按关联表 sortOrder 升序）；仅上架城市可见（城市下架即级联不可见，无需清单自身状态）。
- web 端新增「推荐清单」管理页面（挂在地图管理域下）。

## Capabilities

> openspec/specs/ 当前为空（工具链刚迁移），以下均为首建 spec；内容只覆盖本 change 引入/变更的行为，不回溯补写存量行为。

### New Capabilities
- `merchant`: 商户编辑推荐理由字段的录入与展示行为
- `city`: 地图（城市）编辑说字段、地图命名口径、下架级联对新实体的作用
- `recommend-list`: 推荐清单的管理（admin）与查询（app）行为

### Modified Capabilities

（无——尚无 living specs）

## Impact

- **DB**（admin 端 Liquibase）：`loves_merchant` +1 列、`loves_city` +1 列、新表 `loves_recommend_list`、`loves_recommend_list_merchant`。
- **love-space-admin**：merchant/city 模块 DTO+service 扩展；新模块 `modules/recommendlist`（controller/service/repository/entity/dto）。
- **love-space-app**：merchant/city DTO 扩展；新模块 `modules/recommendlist`（只读查询）。
- **love-space-web**：Merchants/Cities 表单扩展；侧栏文案「城市管理」→「地图管理」；新页面 `pages/RecommendLists`。
- **契约**：`contracts/api-spec.json` 首建，登记本 change 全部新/改接口。
- **测试注册表**：`tests/modules.md` 新增 `recommend-list` 域。
