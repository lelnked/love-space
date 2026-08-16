# Design: ambassador-route-activity

## Context

沿用既有约定：admin/app 双后端同栈分离；实体继承 `BaseAuditEntity`，无外键、snake_case、表前缀 `loves_`；列表型值对象用 jsonb 列（见 `loves_merchant.images/periods`）；图片为私有桶 objectKey，写入走 `ObjectKeyValidator.validateAndBind`（images/→bound/ 重绑定）、读出走 `ImageUrlSigner.sign`；Liquibase formatted-SQL 落 admin 端；web 端 DataTable + 弹窗表单 + 自定义确认弹窗。

## Goals / Non-Goals

- Goals：爱女大使、路线、活动三个新模块贯通三端；城市下架级联扩展到路线与活动。
- Non-Goals：文章/精选（change 3）；7.2 你的周期活动（本期不做）；路线/清单互相引用；活动报名等 C 端交互（纯展示）。

## Decisions（已定决策）

1. **子列表全走 jsonb，不建子表**：大使标签、路线图片、路线地点（name/image/intro 对象数组）、活动图片/标签/周期/路线子条目（title/content 对象数组）全部 jsonb 列——与 merchant images/periods 同构。地点/子条目是纯值对象，无跨实体引用、无唯一约束需求，建表只增加 join 与维护成本。
2. **三张新表**：`loves_ambassador`（avatar/name/tags jsonb/online）、`loves_route`（city_id/sort_order/title/ambassador_note/thumbnail/images jsonb/travel_time/season/travel_status/ambassador_id/spots jsonb）、`loves_activity`（city_id/images jsonb/title/tags jsonb/periods jsonb/level/introduction/editor_note/gathering_place/dismissal_place/transportation/visa/itinerary jsonb/detail_html text/online）。均无外键，service 层校验 city/ambassador 存在。
3. **cityId 创建后不可变**：路线、活动与推荐清单同口径，更新忽略 cityId 变更。
4. **可见性靠查询过滤**：app 端路线 join 城市 online + 大使 online；活动 join 城市 online + 自身 online。不可见详情 404（app 端既有 `ResourceNotFoundException` 口径）。路线/大使无上下架状态字段之外不加冗余标记。
5. **富文本存 HTML、图片存 objectKey**：`detail_html` 内 `<img src>` 一律保存 objectKey（非签名 URL——签名会过期）。保存时服务端正则提取全部 img src 逐个 `validateAndBind` 并回写 bound key；admin/app 读详情时正则将 src 替换为 `ImageUrlSigner.sign` 结果。HTML 由我方编辑器产出、结构可控，正则改写足够。`// ponytail: 正则改写 img src，编辑器产出可控；引入 HTML parser 待格式失控再说`。
6. **web 富文本编辑器用 TipTap**（`@tiptap/react` + starter-kit + image 扩展）：React 19 兼容（react-quill 依赖已移除的 findDOMNode）；仅活动详情说明一处挂载。图片插入复用既有 OSS 上传凭证链路（`/api/admin/files/upload-credential` + PostObject），插入后 src 写 objectKey、编辑器内预览用签名 URL。
7. **富文本 HTML 不做服务端 sanitize**：内容仅运营（Manager）录入、仅展示给 app，信任边界内；app 客户端按富文本渲染组件自身机制处理。`// ponytail: 若未来开放 UGC 再加 sanitizer`。
8. **admin 资源不存在统一 400**：沿 map-and-recommend-list 决策 9（IllegalArgumentException→400 中文业务错误），404 仅 app 端。
9. **级联为新增 Requirement 而非 MODIFIED**：change 1 尚未 archive，城市级联需求还不在 living specs，本 change 以 ADDED「地图下架对路线与活动级联生效」独立成条，archive 后两条并存不冲突。
10. **域注册**：`tests/modules.md` 新增 `route`（含大使，`/api/admin/ambassadors/*`、`/api/admin/routes/*`、`/api/app/routes/*`）与 `activity`（`/api/admin/activities/*`、`/api/app/activities/*`），端均为 web。
11. **无 ui-spec 线框**：三个页面均复用既有页面模式（DataTable 列表 + 弹窗/整页表单 + 确认弹窗），UI 断言按既有口径写。

## API 设计（同步登记 contracts/api-spec.json，operation 加 x-requirement）

| 方法+路径 | 说明 | x-requirement |
|---|---|---|
| GET `/api/admin/ambassadors/page?keyword&page&size` | 大使分页 | route/爱女大使管理 |
| GET/POST/PUT/DELETE `/api/admin/ambassadors[/{id}]` | 大使 CRUD | route/爱女大使管理 |
| PUT `/api/admin/ambassadors/{id}/online` | 上下线 {online} | route/爱女大使管理 |
| GET `/api/admin/routes/page?cityId&keyword&page&size` | 路线分页（sortOrder 升序） | route/路线管理 |
| GET/POST/PUT/DELETE `/api/admin/routes[/{id}]` | 路线 CRUD（含 spots） | route/路线管理 |
| GET `/api/app/routes?cityId=` | 城市路线列表 | route/App 端路线查询 |
| GET `/api/app/routes/{id}` | 路线详情（不可见 404） | route/App 端路线查询 |
| GET `/api/admin/activities/page?cityId&keyword&page&size` | 活动分页 | activity/活动管理 |
| GET/POST/PUT/DELETE `/api/admin/activities[/{id}]` | 活动 CRUD（detailHtml 读时签图） | activity/活动管理 |
| PUT `/api/admin/activities/{id}/online` | 上下线 {online} | activity/活动管理 |
| GET `/api/app/activities?cityId=` | 城市活动列表 | activity/App 端活动查询 |
| GET `/api/app/activities/{id}` | 活动详情（不可见 404） | activity/App 端活动查询 |

## 界面实现映射

- 爱女大使：新目录 `pages/Ambassadors`——DataTable（头像/名称/标签/状态开关/操作）+ 弹窗表单（头像上传、名称、标签最多 3 条动态行）。
- 路线管理：新目录 `pages/Routes`——DataTable（缩略图/主标题/所属城市/大使/排序号/操作）+ 表单（基础字段 + 图片多传 + 地点子列表增删排序按添加顺序）+ 删除确认弹窗。
- 活动管理：新目录 `pages/Activities`——DataTable（图片/标题/所属城市/级别/状态开关/操作）+ 表单（周期多选 checkbox、级别单选、路线子条目动态行、TipTap 富文本）+ 删除确认弹窗。
- 地图管理：`pages/Cities` 下架确认文案追加「路线、活动」。
- 侧栏：`layout/AppSidebar` 新增三个入口。

## Risks / Trade-offs

- [富文本 img 正则改写漏匹配非常规写法] → 编辑器统一产出 `<img src="...">`，UT 覆盖多图/无图/嵌套场景；失控再上 parser。
- [jsonb 地点/子条目无 schema 约束] → DTO Bean Validation 在写入口强校验（地点名称必填等），DB 只存已校验数据。
- [TipTap 新依赖体积] → 仅活动表单路由懒加载处引入。

## Migration Plan

单向建表 SQL（`010-create-ambassador-route.sql`、`011-create-activity.sql`，可回滚 DROP），无数据回填；先部署 admin（管 schema），app 只读新表，无顺序风险。

## Open Questions

无——需求文档 2026-08-16 澄清稿已覆盖；❓B'（tripperclub活动）随 7.2 暂缓，不阻塞本期。
