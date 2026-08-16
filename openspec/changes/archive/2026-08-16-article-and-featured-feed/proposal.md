# Proposal: article-and-featured-feed

## Why

二期需求第六、七章：客户端文章内容目前为写死内容，需改为后台配置（栏目 + 文章）；同时上线【精选】推荐信息流的「地图上新推荐」（7.1）。7.2「你的周期活动」本期明确不做。当前系统没有这两个模块，运营无法配置。

## What Changes

- **文章栏目（新建）**：admin CRUD——栏目名称、icon 图片 1 张、栏目权重（排序）。删除栏目后：已关联文章数据不受影响，但失去所有栏目的文章在 app 端暂不可见。
- **文章（新建）**：admin CRUD——文章图片 1 张、标题、副标题、文章内容（富文本存 HTML，图片走 objectKey 绑定/读时签名，同活动详情方案）、文章权重（排序）、关联栏目**多选**、上线/下线。
- **精选·地图上新推荐（新建）**：admin CRUD——关联地图（城市）单选（创建必选）、banner 图片 1 张（比例运营自控，CMS 不校验）、推荐说明文本、上线/下线。与现有 Banner 模块共存、互不影响。
- **app 端只读查询**：栏目列表（按权重）、按栏目查文章列表 + 文章详情（富文本签名替换）；精选推荐信息流列表（对所有用户生效，下发含关联城市数据，跳转由 App 端自行决定）。
- **地图下架级联扩展**：城市下架后，关联该城市的精选推荐在 app 端不可见；web 下架确认提示补充「精选推荐」。
- web 端新增「文章栏目」「文章管理」「精选推荐」三个后台页面；文章内容复用现有 RichTextEditor 组件。

## Capabilities

### New Capabilities

- `article`: 文章栏目与文章——admin 维护栏目与文章（含富文本、多栏目关联、上下架），app 端按栏目只读查询。
- `featured`: 精选·地图上新推荐——admin 维护推荐条目（关联城市、banner 图、说明、上下架），app 端只读信息流查询。

### Modified Capabilities

- `city`: 下架级联范围从「商户、Banner、推荐清单、路线、活动」扩展为再含「精选推荐」（app 端不可见 + web 确认提示口径）。

## Impact

- **DB（admin 管 schema）**：新表 `loves_article_category`、`loves_article`（categoryIds 走 jsonb 列）、`loves_featured_item`（Liquibase formatted-SQL）。
- **admin 后端**：新模块 `modules.article`、`modules.featured`（controller/service/repository/entity/dto），复用 ObjectKeyValidator/ImageUrlSigner/RichTextImages 图片与富文本链路。
- **app 后端**：只读查询接口 `/api/app/article-categories`、`/api/app/articles/*`、`/api/app/featured-items`，实体只读映射新表。
- **web 前端**：新页面目录 `pages/ArticleCategories`、`pages/Articles`、`pages/FeaturedItems`；城市下架确认文案更新。
- **契约与域**：`contracts/api-spec.json` 登记全部新接口；`tests/modules.md` 注册 `article`、`featured` 两个新域（端 web）。

## Out of Scope

- 7.2「你的周期活动」（含 tripperclub活动/路线体验/周期生活法推荐）：产品未确认，本期不开发。
