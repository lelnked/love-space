## Context

文章模块（`article` 域）当前字段来自需求文档 §6.2：图片、标题、副标题、富文本内容、权重、关联栏目、上下线。列表与详情共用同一个 `title`，且没有引言与标签。本次补齐三个字段，不改变文章的可见性规则、栏目关联模型与富文本 objectKey↔签名 URL 的既有机制。

schema 由 admin 端 Liquibase 统一管理（app 端 liquibase 关闭），两端各有一份 `Article` 实体映射同一张 `loves_article` 表。

## Goals / Non-Goals

**Goals:**
- `loves_article` 增列 `cover_title`、`intro`、`tags`，两端实体与 DTO、web 表单/列表、api-spec 契约同步。
- 存量数据零迁移的前提下，app 客户端列表不出现空标题。

**Non-Goals:**
- 不建标签表、不做标签管理页、不做标签检索/筛选/聚合。
- 不动 `subtitle`（副标题保留，与引言并存）。
- 不改文章可见性规则、排序规则、富文本处理。
- 不为封面标题/引言/标签做任何存量数据回填。

## Decisions

### 1. 标题拆分：新增 `coverTitle`，`title` 语义不变
现有 `title` 在实体、两端 DTO、web、app 客户端已被广泛引用，且其语义（"文章标题"）本就对应详情页标题。新增 `coverTitle` 供列表/封面使用，改动面最小、无语义漂移。

**备选（已否决）**：把 `title` 改当封面标题、新增 `detailTitle`——会静默改变已发布 app 端字段含义，客户端需同步改读，风险不对称。

### 2. 不迁移存量数据，回落在读取侧
用户明确要求不迁移。因此 `cover_title` 与 `intro` 可空，`tags` 用 `NOT NULL DEFAULT '[]'::jsonb` 让存量行自动得到空数组。

存量文章 `coverTitle` 为 NULL 会让 app 列表出现空标题，故在 **app 端 `ArticleQueryService` 的列表映射处**回落：`coverTitle` 非空白则用它，否则用 `title`。回落只在 app 读取侧发生——admin 端列表与详情一律返回**原值**（可为 null），保证后台编辑表单如实反映"这篇文章没设封面标题"，不会把回落值误存回库。

### 3. 标签存 jsonb 字符串数组，复用形象大使既有模式
`Ambassador` 实体已有 `@JdbcTypeCode(SqlTypes.JSON) List<String> tags`，`Ambassadors/List.tsx` 已有对应的标签编辑 UI（每条一个 Input + 「删除」按钮 + 下方「添加标签」按钮）。文章标签直接沿用这两处的写法，不新建实体、不新建组件、不引依赖。

与形象大使的唯一差异：**文章标签不设条数上限**（形象大使限 3 条是其展示位约束，文章无此约束），故不带 `tags.length < 3` 的按钮隐藏条件。保存前统一 `map(trim).filter(Boolean)` 剔除空白项，与形象大使一致。

**备选（已否决）**：独立 `loves_article_tag` 表 + 管理页——多一个模块、一套 CRUD 接口和一个后台页面，而当前需求只要"文章上挂几个词"，没有跨文章统一改名/复用的诉求。

### 4. 字段可选性
`coverTitle`、`intro`、`tags` 三者在 `ArticleUpsertRequest` 中**全部可选**，无 `@NotBlank`。`title`、`image` 的必填校验不变。空字符串按 null 存（web 侧 `trim() || null`，与现有 `subtitle` 处理一致）。

### 5. 契约与接口
不新增端点，只扩充既有 operation 的 schema，四处：
- `POST /api/admin/articles`、`PUT /api/admin/articles/{id}`（request：+coverTitle、+intro、+tags）
- `GET /api/admin/articles/page`、`GET /api/admin/articles/{id}`（response：+三字段）
- app 端文章列表（response：+coverTitle、+tags）
- app 端文章详情（response：+intro、+tags）

`contracts/api-spec.json` 中这些 operation 的 `x-requirement` 反链维持 `article/文章管理` 与 `article/App 端文章查询`。

## 界面实现映射

文章表单页 `love-space-web/src/pages/Articles/Form.tsx`，在既有字段块中插入：

| 表单区域 | 组件 | 位置 |
|---|---|---|
| 封面标题（可空） | `Input`（`components/form/input`） | 现有「文章标题」输入块**之前** |
| 文章标题（必填，不变） | `Input` | 原位 |
| 文章副标题（不变） | `Input` | 原位 |
| 文章引言（可空） | `Input` | 副标题块**之后** |
| 文章标签（可空，多条） | `Input` + `Button`（复用 `Ambassadors/List.tsx:316-350` 的结构） | 引言块之后、富文本编辑器之前 |

文章列表页 `love-space-web/src/pages/Articles/List.tsx`：现有 `key: "title"` 列拆为「封面标题」与「文章标题」两列，封面标题为空时该单元格显示 `-`（沿用列表其他可空列的既有空值口径）。

## Risks / Trade-offs

- **存量文章封面标题为空** → app 列表侧回落 `title`，客户端零感知；后台列表显示 `-`，运营可自行补齐。
- **标签无上限，运营可能加过多标签撑破客户端展示** → 客户端展示裁剪由 app 端自行处理；后台不设限，避免拍一个没有依据的数字。若日后客户端提出硬约束，加一条 `@Size` 校验即可，改动局限在 `ArticleUpsertRequest`。
- **标签是自由文本，同义标签会散落**（"约会" / "约会攻略"）→ 已知取舍，本次不做归一；升级路径是后补标签表并按文本回填。
- **两端 `Article` 实体需同时加字段**，漏一端会在 app 查询时缺列 → tasks 中 admin/app 实体作为同一条任务的两个子项，IT 用例覆盖 app 详情返回引言与标签。

## Migration Plan

1. 新增 `021-add-article-cover-title-intro-tags.sql`：三条 `ALTER TABLE loves_article ADD COLUMN`，`tags` 带 `NOT NULL DEFAULT '[]'::jsonb`（存量行自动补空数组，无需 UPDATE）。
2. 部署顺序：admin 先起（跑 Liquibase 建列），app 后起。新列全部可空或带默认值，旧版本 app 读旧列不受影响，**可先行部署 DB 而不停服**。
3. 回滚：changeset 的 `--rollback` 为三条 `DROP COLUMN`；已录入的封面标题/引言/标签会随之丢失，回滚前需确认运营是否已开始录入。

## Open Questions

无。三个决策点（标题拆分方式、引言字段归属、标签存储模型）已由用户确认。
