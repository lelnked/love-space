## Why

文章目前只有一个 `title`，列表（封面）与详情页共用同一个标题，运营无法为两处分别措辞；同时缺少「引言」和「标签」两个内容字段，客户端文章详情无法展示导语和标签行。需求文档 §6.2 的字段表未覆盖这三项，属于文章模块的字段补齐。

## What Changes

- 文章新增 **封面标题 `coverTitle`**（可空文本）：用于列表/封面展示。现有 `title` 语义保持不变，继续作为**文章标题（详情页标题）**。
- 文章新增 **文章引言 `intro`**（可空文本）：详情页正文之前的导语。现有 `subtitle`（副标题）**保留不变**，与引言并存、互不影响。
- 文章新增 **文章标签 `tags`**（字符串数组，jsonb）：表单内输入即建，无独立标签表、无标签管理页。
- **不迁移历史数据**：存量文章 `coverTitle` / `intro` 为 NULL，`tags` 为空数组。app 端列表在 `coverTitle` 为空时回落展示 `title`，保证客户端零断裂。
- admin 端文章创建/更新请求与列表/详情响应、app 端文章列表/详情响应、web 端文章表单与列表列同步这三个字段。
- 非 BREAKING：三个字段均为可选新增，现有请求体与响应字段全部保留。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `article`: 「文章管理」「App 端文章查询」「web 端文章管理页面」三条 Requirement 的字段集扩充——文章新增封面标题、引言、标签三字段；app 列表改为返回封面标题（空则回落文章标题）与标签，app 详情新增引言与标签。

## Impact

- **DB**：`loves_article` 新增 `cover_title text`、`intro text`、`tags jsonb NOT NULL DEFAULT '[]'::jsonb`；新增 Liquibase changeset `021-add-article-cover-title-intro-tags.sql`（admin 端统一管理 schema）。
- **admin 后端**（`com.loves.space.modules.article`）：`Article` 实体、`ArticleUpsertRequest`、`ArticleItemResponse`、`ArticleDetailResponse`、`ArticleService` 的写入与映射。
- **app 后端**（`com.space.app.modules.article`）：`Article` 实体、`ArticleItemResponse`（+coverTitle、+tags）、`ArticleDetailResponse`（+intro、+tags）、`ArticleQueryService` 映射与回落逻辑。
- **web**（`love-space-web`）：`src/api/articles.ts` 类型、`src/pages/Articles/Form.tsx`（封面标题、引言两个输入 + 标签输入控件）、`src/pages/Articles/List.tsx`（列表标题列展示封面标题）。
- **契约**：`contracts/api-spec.json` 中 admin 与 app 的文章 schema。
- **测试**：`tests/article/{it,web}.md` 新增用例；域注册表 `tests/modules.md` 无需改动（仍属 `article` 域）。
