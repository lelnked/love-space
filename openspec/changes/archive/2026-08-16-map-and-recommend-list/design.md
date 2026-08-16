# Design: map-and-recommend-list

## Context

现有代码约定（沿用，不新造）：admin/app 双后端同栈分离；实体继承 `BaseAuditEntity`，无外键、列名 snake_case；Liquibase formatted-SQL 落在 admin 端 `db/changelog/changes/*.sql`；表前缀 `loves_`；校验错误信息中文；web 端列表用 `DataTable`、删除用自定义确认弹窗、表单 `noValidate` 统一校验风格。

## Goals / Non-Goals

- Goals：商户推荐理由、城市编辑说两个字段贯通三端；推荐清单实体全新落地（admin CRUD + app 只读 + web 页面）；「地图管理」文案更名。
- Non-Goals：路线/大使/活动/文章/精选（后续 change）；城市模块改名重构（代码标识、路由、表名均不动）；清单上下架（明确不做，物理删除）。

## Decisions（已定决策）

1. **字段落列而非扩展表**：`loves_merchant.recommend_reason varchar(2000)`、`loves_city.editor_note varchar(200)`，Bean Validation `@Size` 限长，超长 400 中文报错——与既有字段处理完全同构。
2. **清单两张表**：`loves_recommend_list`（title 必填 / introduction / city_id / sort_order，默认 0）+ `loves_recommend_list_merchant`（recommend_list_id / merchant_id / sort_order，`(recommend_list_id, merchant_id)` 唯一约束防重复添加）。无外键，约定由 service 层校验 city 存在、商户属于同城。
3. **清单商户维护用整体替换**：`PUT /api/admin/recommend-lists/{id}/merchants` 提交 `[{merchantId, sortOrder}]` 全量列表，服务端 diff 后落库——与 merchant images/periods 的 inline 维护模式一致，避免 add/remove 两个细粒度接口。
4. **cityId 创建后不可变**：更新接口忽略/拒绝 cityId 变更（清单内商户同城校验以创建时城市为准，改城市会使存量关联失效）。
5. **app 端级联可见性靠查询过滤**：清单查询 join 城市 online 状态（列表 `WHERE city.online`，详情城市下架回 404）——不给清单加状态字段。
6. **「地图管理」仅改文案**：侧栏菜单、页面标题、按钮/提示文案中「城市」口径改「地图」；路由 `/cities`、目录 `pages/Cities`、API 路径全部不动，避免无行为收益的重命名扩散。
7. **无 ui-spec 线框**：本 change 界面均为既有页面模式的复用（DataTable 列表 + 弹窗表单），不产线框，UI 断言按既有页面口径写（见 specs）。
8. **域注册**：`tests/modules.md` 新增 `recommend-list` 域（`/api/admin/recommend-lists/*`、`/api/app/recommend-lists/*`，端 web）。
9. **admin 端资源不存在统一 400**（交付验证期补记）：admin 侧 GET 不存在资源沿项目既有全局口径 `IllegalArgumentException`→400 中文业务错误（商户/城市实测同口径），不为清单单独引入 404；404 口径仅用于 app 端（下架城市详情）。TC-recommend-list-IT-005 预期已对齐。
10. **test profile OSS stub**（交付验证期补记）：联调/测试实例无真实 OSS，新增 `@Profile("test")` 的 `StubObjectKeyValidator`（正则校验后直返 bound key），`AliyunOssObjectKeyValidator` 标 `@Profile("!test")`；生产行为零变化。

## API 设计（同步登记 contracts/api-spec.json）

| 方法+路径 | 说明 | x-requirement |
|---|---|---|
| 既有 admin merchant CRUD | DTO 增加 `recommendReason` | merchant/商户编辑推荐理由 |
| 既有 admin city CRUD | DTO 增加 `editorNote` | city/地图编辑说 |
| 既有 app city/merchant 查询 | 响应增加对应字段 | 同上 |
| GET `/api/admin/recommend-lists/page?cityId&keyword&page&size` | 分页列表（sortOrder 升序） | recommend-list/推荐清单管理 |
| GET `/api/admin/recommend-lists/{id}` | 详情（含商户明细，按关联 sortOrder 升序） | recommend-list/推荐清单管理 |
| POST `/api/admin/recommend-lists` | 创建 {title, introduction, cityId, sortOrder} | recommend-list/推荐清单管理 |
| PUT `/api/admin/recommend-lists/{id}` | 更新 {title, introduction, sortOrder}（cityId 不可变） | recommend-list/推荐清单管理 |
| DELETE `/api/admin/recommend-lists/{id}` | 物理删除（连带关联） | recommend-list/推荐清单管理 |
| PUT `/api/admin/recommend-lists/{id}/merchants` | 全量替换清单商户 `[{merchantId, sortOrder}]`，同城校验 | recommend-list/清单内商户维护 |
| GET `/api/app/recommend-lists?cityId=` | 城市清单列表（仅上架城市） | recommend-list/App 端清单查询 |
| GET `/api/app/recommend-lists/{id}` | 清单详情含商户（城市下架 404） | recommend-list/App 端清单查询 |

## 界面实现映射

- 商户表单：`pages/Merchants` 表单组件加「编辑推荐理由」textarea（maxLength 2000）。
- 地图管理：`layout/AppSidebar`（或等价导航配置）与 `pages/Cities` 标题/文案改「地图管理」；城市表单加「编辑说」textarea（maxLength 200）；下架确认文案追加"推荐清单"。
- 推荐清单：新目录 `pages/RecommendLists`——列表页（城市筛选下拉[仅上架城市可选沿用既有口径？清单管理需全部城市，用全部城市]、DataTable：标题/所属城市/排序号/商户数/操作）、新建/编辑弹窗表单、商户维护界面（本城商户下拉多选 + 每行排序号输入）、删除确认弹窗。

## Risks / Trade-offs

- [无外键，关联表可能残留脏数据] → service 删除清单时同事务删关联；矩阵/IT 用例覆盖删除级联。
- [全量替换商户列表在清单很大时低效] → 清单为运营手工维护，量级几十以内，可接受（ponytail: 全量替换，量级上来再改增量）。
- [「地图」文案与代码标识不一致造成心智负担] → design 已定决策 6 说明口径，CLAUDE.md 后续如需可补一行。

## Migration Plan

单向加列/建表 SQL（Liquibase changes/*.sql，可回滚 DROP），无数据回填；先部署 admin（管 schema），app 端只读新列，无部署顺序风险。

## Open Questions

无——需求问题已全部澄清（见 二期需求开发文档.md 附录）。
