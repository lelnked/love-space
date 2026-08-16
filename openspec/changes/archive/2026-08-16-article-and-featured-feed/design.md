# Design: article-and-featured-feed

## Context

沿用既有约定：admin/app 双后端同栈分离；实体继承 `BaseAuditEntity`，无外键、snake_case、表前缀 `loves_`；列表型值对象用 jsonb 列；图片为私有桶 objectKey，写入走 `ObjectKeyValidator.validateAndBind`、读出走 `ImageUrlSigner.sign`；富文本沿 activity 方案（`RichTextImages` img src 存 objectKey、读时签名替换）；Liquibase formatted-SQL 落 admin 端；web 端 DataTable + 弹窗/整页表单 + 确认弹窗，富文本复用既有 `RichTextEditor`（TipTap）。

## Goals / Non-Goals

- Goals：文章栏目、文章、精选·地图上新推荐三个模块贯通三端；城市下架级联扩展到精选推荐。
- Non-Goals：7.2 你的周期活动（本期不做）；文章评论/点赞等 C 端交互（纯展示）；文章与城市的关联（文章不挂地图）。

## Decisions（已定决策）

1. **三张新表**：`loves_article_category`（name/icon/sort_order）、`loves_article`（image/title/subtitle/content_html text/sort_order/category_ids jsonb/online）、`loves_featured_item`（city_id/banner/description/online）。文章↔栏目多对多走 `category_ids` jsonb 数组，不建关联表——与既有 jsonb 口径同构，查询端 jsonb 包含判断足够。`// ponytail: jsonb 数组存关联，文章量大到需按栏目索引再建关联表`。
2. **删除栏目不回写文章**：删除仅删栏目行，文章 `category_ids` 里的悬空 id 保留；可见性在查询时按「存在的栏目」过滤——需求明说文章数据不受影响，悬空 id 在栏目重建场景无害。
3. **文章可见性 = online ∧ 至少一个关联栏目仍存在**；不可见详情 404（app 端既有口径）。栏目列表/文章列表均按 sortOrder 升序（与路线/清单同口径）。
4. **精选推荐 cityId 创建后不可变**：与推荐清单/路线/活动同口径，更新忽略 cityId 变更。
5. **精选推荐无排序字段**：需求未提权重，信息流按创建时间倒序（「上新」语义，新条目靠前）。`// ponytail: 需要人工排序时再加 sortOrder`。
6. **精选推荐可见性 = 条目上线 ∧ 关联城市上架**：与 Banner/路线/活动级联口径一致；app 列表接口不带 cityId 参数（全量信息流，对所有用户生效），条目内下发城市 id/名称供 App 端决定跳转。
7. **富文本沿用 RichTextImages**：文章 content_html 保存时 img src validateAndBind、读时签名替换，admin/app 两端复用既有工具类，零新机制。
8. **级联为新增 Requirement**：city delta 以 ADDED「地图下架对精选推荐级联生效」独立成条，与既有两条级联需求并存。
9. **域注册**：`tests/modules.md` 新增 `article`（`/api/admin/article-categories/*`、`/api/admin/articles/*`、`/api/app/article-categories`、`/api/app/articles/*`）与 `featured`（`/api/admin/featured-items/*`、`/api/app/featured-items`），端均为 web。
10. **无 ui-spec 线框**：三个页面均复用既有页面模式（栏目/精选=弹窗表单，文章=整页表单+富文本），UI 断言按既有口径写。
11. **悬空栏目 id 在 admin 读取端同样过滤**（IT 轮发现补定）：`ArticleService` 列表/详情的 `categoryIds` 只返回仍存在的栏目——与决策 2「查询端过滤」口径对齐，避免前端展示已删栏目。存储不回写不变。
12. **admin 精选推荐响应只含 cityId、不冗余城市名称**（IT 轮裁决）：城市名称由 web 端经城市列表映射展示，与 Activity/Banner 一贯口径一致；app 端信息流才下发 `city{id,name}`。对应用例 TC-featured-IT-001 预期已修订。

## API 设计（同步登记 contracts/api-spec.json，operation 加 x-requirement）

| 方法+路径 | 说明 | x-requirement |
|---|---|---|
| GET `/api/admin/article-categories` | 栏目列表（sortOrder 升序） | article/文章栏目管理 |
| POST/PUT/DELETE `/api/admin/article-categories[/{id}]` | 栏目 CRUD | article/文章栏目管理 |
| GET `/api/admin/articles/page?categoryId&keyword&page&size` | 文章分页 | article/文章管理 |
| GET/POST/PUT/DELETE `/api/admin/articles[/{id}]` | 文章 CRUD（contentHtml 读时签图） | article/文章管理 |
| PUT `/api/admin/articles/{id}/online` | 上下线 {online} | article/文章管理 |
| GET `/api/app/article-categories` | App 栏目列表 | article/App 端文章查询 |
| GET `/api/app/articles?categoryId=` | App 按栏目查文章列表 | article/App 端文章查询 |
| GET `/api/app/articles/{id}` | App 文章详情（不可见 404） | article/App 端文章查询 |
| GET `/api/admin/featured-items/page?cityId&page&size` | 精选推荐分页 | featured/精选推荐管理 |
| GET/POST/PUT/DELETE `/api/admin/featured-items[/{id}]` | 精选推荐 CRUD | featured/精选推荐管理 |
| PUT `/api/admin/featured-items/{id}/online` | 上下线 {online} | featured/精选推荐管理 |
| GET `/api/app/featured-items` | App 精选信息流（上线∧城市上架，创建时间倒序） | featured/App 端精选推荐查询 |

## 界面实现映射

- 文章栏目：新目录 `pages/ArticleCategories`——DataTable（icon/名称/权重/操作）+ 弹窗表单（icon 上传、名称、权重）。
- 文章管理：新目录 `pages/Articles`——DataTable（图片/标题/关联栏目/状态开关/操作）+ 整页表单（图片、标题、副标题、权重、栏目多选 checkbox、RichTextEditor）+ 删除确认弹窗。
- 精选推荐：新目录 `pages/FeaturedItems`——DataTable（banner/关联城市/说明/状态开关/操作）+ 弹窗表单（城市单选、banner 上传、说明）。
- 地图管理：`pages/Cities` 下架确认文案追加「精选推荐」。
- 侧栏：`layout/AppSidebar` 新增三个入口。

## Risks / Trade-offs

- [悬空 category_ids 误判] → 查询端按存在栏目过滤 + UT 覆盖「删栏目后不可见」场景。
- [jsonb 数组按栏目查文章的性能] → 数据量为运营配置级（百级），顺序扫描无压力；见决策 1 升级路径。

## Migration Plan

单向建表 SQL（`012-create-article.sql`、`013-create-featured-item.sql`，可回滚 DROP），无数据回填；先部署 admin（管 schema），app 只读新表，无顺序风险。

## Open Questions

无——需求文档 2026-08-16 澄清稿已覆盖；7.2 及其关联问题 B' 随暂缓，不阻塞本期。
