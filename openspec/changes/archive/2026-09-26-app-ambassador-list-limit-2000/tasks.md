# Tasks

## 1. app 后端
- [x] 1.1 `AmbassadorQueryService`：`DEFAULT_LIMIT` 3 → 2000、`MAX_LIMIT` 20 → 2000，更新 javadoc
- [x] 1.2 `AmbassadorController` javadoc 同步新口径
- [x] 1.3 IT：`AmbassadorReadIT` 默认条数与上限断言改写（不传 limit → 全量；limit=9999 → 全量；limit=0 → 全量；limit=5 → 5 条），带 `@scenario`

## 2. 契约
- [x] 2.1 `contracts/api-spec.json`：`/api/app/ambassadors` 的 `limit` default 3→2000、maximum 20→2000，summary 同步
- [x] 2.2 手改 `love-space-app/docs/openapi.json` + 生成脚本 `scripts/generate-app-openapi.js` 的 limit 说明字典（生成脚本在 HEAD 上已因 `FeaturedCycleItemTargetResponse` 解析失败而跑不通，与本 change 无关，未修）

## 3. 测试用例
- [x] 3.1 `tests/route/it.md`：TC-route-IT-020 / TC-route-IT-021 预期值改写为新口径

## 4. 验证
- [x] 4.1 跑 app IT：`AmbassadorReadIT` 4/4 通过（2026-09-26）
- [x] 4.2 刷新追溯矩阵
