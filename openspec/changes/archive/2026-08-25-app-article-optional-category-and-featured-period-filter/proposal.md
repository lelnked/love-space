## Why

移动端两处只读接口的查询形态与客户端实际用法不匹配：文章列表必须传栏目才能查（客户端"全部文章"入口无法直接调用）；周期推荐接口只能整包下发四周期分组，客户端已在本地判定周期后仍要拉回四组再自选，且分组结构对只关心单周期的页面是多余的。

## What Changes

- `GET /api/app/articles` 的 `categoryId` 由必填改为可选：传入时行为不变（该栏目下可见文章）；不传时返回全部可见文章（上线 ∧ 至少关联一个仍存在的栏目），排序口径不变（`sortOrder` 升序、同序号创建时间倒序）。
- `GET /api/app/featured-cycle-items` 新增可选查询参数 `period`（`MENSTRUAL` / `FOLLICULAR` / `OVULATION` / `LUTEAL`）：传入时仅下发该周期条目；不传时下发全部周期。非法值返回 400。与已有可选参数 `type` 可同时使用。
- **BREAKING** `GET /api/app/featured-cycle-items` 响应由"四周期分组 Map"改为**扁平数组**；每个条目新增 `period` 字段标识所属周期（不传 `period` 过滤时客户端据此区分）。可见性规则与组内排序口径不变（`sortOrder` 升序、同序号创建时间倒序，跨周期混排时同样按此全局排序）。
- `contracts/api-spec.json` 同步：`/api/app/articles` 的 `categoryId` 改 `required: false`；`/api/app/featured-cycle-items` 新增 `period` 参数并改写响应描述。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `article`: App 端文章查询——按栏目查文章列表的栏目条件改为可选，不传时返回全部可见文章。
- `featured`: App 端周期推荐查询——新增按周期过滤的可选条件；响应由四周期分组改为带 `period` 字段的扁平列表。

## Impact

- 代码：`love-space-app` article 模块（Controller / QueryService / Repository 新增查询）、featuredcycle 模块（Controller / QueryService / Response DTO）。
- 契约：`contracts/api-spec.json` 两条 GET 的参数与响应描述。
- 测试：`tests/article/it.md` 新增"不传 categoryId 返回全部可见文章"用例；`tests/featured/it.md` 新增按 period 过滤、非法 period、扁平结构含 period 字段用例，既有 TC-featured-IT-016~022 的"四周期键齐全 / 分组"断言需改写为扁平列表口径。
- 客户端：周期推荐接口响应结构变化，移动端需同步适配（本仓库无移动端代码）。
- 前端/admin：无影响。
- 依赖前置：本 change 叠在待归档的 `app-featured-cycle-type-filter`（`type` 参数）之上，delta spec 以其合入后的 living spec 为基线。
