# Tasks: article-and-featured-feed

## 1. DB 迁移（admin 管 schema）

- [x] 1.1 Liquibase `012-create-article.sql`：建 `loves_article_category`（name/icon/sort_order + 审计列）与 `loves_article`（image/title/subtitle/content_html text/sort_order/category_ids jsonb/online + 审计列），rollback DROP
- [x] 1.2 Liquibase `013-create-featured-item.sql`：建 `loves_featured_item`（city_id/banner/description/online + 审计列），rollback DROP

## 2. admin 后端

- [x] 2.1 `modules.article` 栏目：entity/repository/dto/service/controller——列表（sortOrder 升序）+ CRUD；name/icon 必填校验；icon 走 validateAndBind/sign；删除不回写文章。UT 锚点 @scenario: article/文章栏目管理#创建栏目、#缺少必填项被拒绝、#删除栏目不影响文章数据
- [x] 2.2 `modules.article` 文章：entity（category_ids jsonb）/repository/dto/service/controller——CRUD + page(categoryId,keyword) sortOrder 升序 + online 切换；image/title 必填、关联栏目存在校验；contentHtml 走 RichTextImages（保存 validateAndBind、读时签名替换）。UT 锚点 @scenario: article/文章管理#创建文章、#缺少必填项被拒绝、#文章上下线切换
- [x] 2.3 `modules.featured`：entity/repository/dto/service/controller——CRUD + page(cityId) + online 切换；cityId 必选且创建后不可变、banner 必填、城市存在校验。UT 锚点 @scenario: featured/精选推荐管理#创建精选推荐、#缺少必填项被拒绝、#精选推荐上下线切换

## 3. app 后端

- [x] 3.1 `modules.article`：只读实体 + 查询 service/controller——`GET /api/app/article-categories`（sortOrder 升序）、`GET /api/app/articles?categoryId=`（online ∧ 栏目存在过滤，sortOrder 升序）、`GET /api/app/articles/{id}`（不可见 404，contentHtml 签名替换）。UT 锚点 @scenario: article/App 端文章查询#查询栏目与文章列表、#下线文章不可见、#失去所有栏目的文章不可见、#文章详情返回富文本
- [x] 3.2 `modules.featured`：只读实体 + 查询 service/controller——`GET /api/app/featured-items`（条目上线 ∧ 城市上架，创建时间倒序，含城市 id/名称）。UT 锚点 @scenario: featured/App 端精选推荐查询#查询精选推荐信息流；city/地图下架对精选推荐级联生效#下架城市后 app 端精选推荐不可见

## 4. web 前端

- [x] 4.1 API 客户端与类型：article-categories/articles/featured-items 三组接口封装（镜像 admin DTO）
- [x] 4.2 `pages/ArticleCategories`：DataTable（icon/名称/权重/操作）+ 弹窗表单（icon 上传、名称、权重）+ 删除确认
- [x] 4.3 `pages/Articles`：DataTable（图片/标题/关联栏目/状态开关/操作）+ 整页表单（图片、标题、副标题、权重、栏目多选、RichTextEditor 挂文章内容）+ 删除确认
- [x] 4.4 `pages/FeaturedItems`：DataTable（banner/关联城市/说明/状态开关/操作）+ 弹窗表单（城市单选、banner 上传、说明）+ 删除确认
- [x] 4.5 侧栏 `AppSidebar` 新增「文章栏目」「文章管理」「精选推荐」入口；`App.tsx` 挂路由
- [x] 4.6 `pages/Cities` 下架确认文案补充「精选推荐」。WEB 断言对应 @scenario: city/地图下架对精选推荐级联生效#web 下架确认提示包含精选推荐

## 5. 收尾

- [x] 5.1 各 Scenario 覆盖核对：WEB 用例或 UT 锚点二选一，缺口补齐
- [x] 5.2 `npm run lint && npm run build`、两后端 `./mvnw test` 全绿
