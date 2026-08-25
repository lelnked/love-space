## 1. 数据库

- [x] 1.1 新增 `love-space-admin/src/main/resources/db/changelog/changes/021-add-article-cover-title-intro-tags.sql`：`ALTER TABLE loves_article ADD COLUMN cover_title text`、`ADD COLUMN intro text`、`ADD COLUMN tags jsonb NOT NULL DEFAULT '[]'::jsonb`；`--rollback` 三条 DROP COLUMN
- [x] 1.2 在 `db.changelog-master.yaml` include 该 changeset

## 2. admin 后端

- [x] 2.1 `Article` 实体（`com.loves.space.modules.article.entity`）加 `coverTitle`、`intro` 两个 `@Column`，`tags` 用 `@JdbcTypeCode(SqlTypes.JSON)` + `columnDefinition = "jsonb"`，默认 `new ArrayList<>()`（照抄 `Ambassador.tags` 写法）
- [x] 2.2 `ArticleUpsertRequest` 加 `String coverTitle`、`String intro`、`List<String> tags` 三个可选字段（不加 `@NotBlank`）
- [x] 2.3 `ArticleItemResponse` 与 `ArticleDetailResponse` 加三字段
- [x] 2.4 `ArticleService` 写入映射：空白 `coverTitle`/`intro` 存 null；`tags` 为 null 时存空数组，非 null 时逐项 trim 并剔除空白；读取映射按原值返回（admin 端不做回落）
- [x] 2.5 admin UT 覆盖 `article/文章管理#创建带封面标题、引言与标签的文章`、`article/文章管理#封面标题、引言、标签均可省略`（测试方法加 `@scenario` 注释）

## 3. app 后端

- [x] 3.1 `Article` 实体（`com.space.app.modules.article.entity`）同步加 `coverTitle`、`intro`、`tags` 三字段，映射口径与 admin 端一致
- [x] 3.2 `ArticleItemResponse`（app）加 `String coverTitle`、`List<String> tags`
- [x] 3.3 `ArticleDetailResponse`（app）加 `String intro`、`List<String> tags`
- [x] 3.4 `ArticleQueryService` 列表映射实现封面标题回落：`coverTitle` 非空白用其本身，否则用 `title`；详情映射直接透传 `intro`、`tags`
- [x] 3.5 app UT 覆盖 `article/App 端文章查询#未设封面标题时列表回落文章标题`、`article/App 端文章查询#详情返回引言与标签`（加 `@scenario` 注释）

## 4. 契约

- [x] 4.1 `contracts/api-spec.json`：admin 文章 request schema 加 `coverTitle`/`intro`/`tags`。（偏差：该契约文件全局不描述 response——零 `responses` 段，故 response 字段无处可落，改为在相关 operation 的 summary 中点明返回字段）
- [x] 4.2 `contracts/api-spec.json`：app 文章列表与详情 operation 的 summary 补充返回 `coverTitle`/`tags`/`intro` 与回落规则；`x-requirement` 反链确认仍为 `article/文章管理`、`article/App 端文章查询`

## 5. web 前端

- [x] 5.1 `src/api/articles.ts`：`ArticleItem`、`ArticleDetail`、`ArticleUpsertRequest` 三个类型加 `coverTitle`/`intro`（`string | null`）与 `tags`（`string[]`）
- [x] 5.2 `Articles/Form.tsx` 表单区域「封面标题」：在现有文章标题输入块之前插入 `Input`，绑定 `coverTitle` state，可空无校验，提交时 `trim() || null`
- [x] 5.3 `Articles/Form.tsx` 表单区域「文章引言」：在副标题输入块之后插入 `Input`，绑定 `intro` state，可空无校验，提交时 `trim() || null`
- [x] 5.4 `Articles/Form.tsx` 表单区域「文章标签」：在引言之后、富文本编辑器之前，复用 `Ambassadors/List.tsx:316-350` 的标签编辑结构（每条 `Input` + `type="button"` 的「删除」`Button`，下方常显「添加标签」`Button`，不设条数上限），提交前 `map(trim).filter(Boolean)`
- [x] 5.5 `Articles/Form.tsx` 编辑态回显：`setCoverTitle(d.coverTitle ?? "")`、`setIntro(d.intro ?? "")`、`setTags(d.tags ?? [])`，存量文章（三者为空）打开表单不报错
- [x] 5.6 `Articles/List.tsx` 列表列：原 `title` 列拆为「封面标题」（`it.coverTitle || "-"`）与「文章标题」（`it.title`）两列
- [x] 5.7 `npm run build`（`NODE_ENV=development`）通过，`npm run lint` 无新增告警

## 6. 测试用例落盘

- [x] 6.1 按 `test-cases.md` 清单把新增 IT 用例写入 `tests/article/it.md`、新增/修改 WEB 用例写入 `tests/article/web.md`（增量合并，不覆盖 runner 已回写的状态字段）
