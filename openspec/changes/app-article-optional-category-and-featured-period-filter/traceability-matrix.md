# 追溯矩阵（交付核对）：app-article-optional-category-and-featured-period-filter

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change app-article-optional-category-and-featured-period-filter`

## 需求与场景
- **article/App 端文章查询**: 查询栏目与文章列表 / 不传栏目返回全部可见文章 / 未设封面标题时列表回落文章标题 / 详情返回引言与标签 / 下线文章不可见 / 失去所有栏目的文章不可见 / 文章详情返回富文本
- **featured/App 端周期推荐查询**: 查询四个周期的推荐列表 / 按周期过滤 / 周期与类型同时过滤 / 按内容类型过滤 / 类型过滤后周期为空仍返回空数组 / 周期过滤后无条目返回空数组 / 非法类型值被拒绝 / 非法周期值被拒绝 / 关联实体不可见时条目不下发 / 城市未上架不影响路线类条目 / 大使下线连带隐藏路线类条目 / 组内按排序号升序

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-article-IT-011 | GET /api/app/article-categories 与 /api/app/articles 均按权重升序 | article/App 端文章查询#查询栏目与文章列表 | api-spec.json#/paths/~1api~1app~1article-categories/get | article-cover-title-intro-tags | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-article-IT-011/` | ✅ |
| TC-article-IT-012 | GET /api/app/articles 下线文章不可见、详情 404 | article/App 端文章查询#下线文章不可见 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-article-IT-012/` | ✅ |
| TC-article-IT-013 | GET /api/app/articles/{id} 失去所有栏目的文章不可见 | article/App 端文章查询#失去所有栏目的文章不可见 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-013/` | ✅ |
| TC-article-IT-014 | GET /api/app/articles/{id} 详情返回富文本且 img src 为签名 URL | article/App 端文章查询#文章详情返回富文本 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-014/` | ✅ |
| TC-article-IT-018 | GET /api/app/articles 未设封面标题时回落文章标题 | article/App 端文章查询#未设封面标题时列表回落文章标题 | api-spec.json#/paths/~1api~1app~1articles/get | article-cover-title-intro-tags | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-article-IT-018/` | ✅ |
| TC-article-IT-019 | GET /api/app/articles/{id} 详情返回引言与标签 | article/App 端文章查询#详情返回引言与标签 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-cover-title-intro-tags | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-019/` | ✅ |
| TC-article-IT-020 | GET /api/app/articles 不传 categoryId 返回全部可见文章 | article/App 端文章查询#不传栏目返回全部可见文章 | api-spec.json#/paths/~1api~1app~1articles/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-article-IT-020/` | ✅ |
| TC-featured-IT-016 | GET /api/app/featured-cycle-items 扁平数组带 period 字段且只含上线条目 | featured/App 端周期推荐查询#查询四个周期的推荐列表 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-016/` | ✅ |
| TC-featured-IT-017 | GET /api/app/featured-cycle-items 关联实体不可见时条目不下发 | featured/App 端周期推荐查询#关联实体不可见时条目不下发 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-017/` | ✅ |
| TC-featured-IT-018 | GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目 | featured/App 端周期推荐查询#大使下线连带隐藏路线类条目 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-018/` | ✅ |
| TC-featured-IT-019 | GET /api/app/featured-cycle-items 按排序号升序 | featured/App 端周期推荐查询#组内按排序号升序 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-019/` | ✅ |
| TC-featured-IT-020 | GET /api/app/featured-cycle-items 城市未上架不影响路线类条目 | featured/App 端周期推荐查询#城市未上架不影响路线类条目 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-020/` | ✅ |
| TC-featured-IT-021 | GET /api/app/featured-cycle-items?type= 按内容类型过滤 | featured/App 端周期推荐查询#按内容类型过滤 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-021/` | ✅ |
| TC-featured-IT-022 | GET /api/app/featured-cycle-items?type= 类型过滤后无条目返回空数组 | featured/App 端周期推荐查询#类型过滤后周期为空仍返回空数组 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-022/` | ✅ |
| TC-featured-IT-023 | GET /api/app/featured-cycle-items?type= 非法类型值返回 400 | featured/App 端周期推荐查询#非法类型值被拒绝 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-023/` | ✅ |
| TC-featured-IT-024 | GET /api/app/featured-cycle-items?period= 按周期过滤 | featured/App 端周期推荐查询#按周期过滤 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-024/` | ✅ |
| TC-featured-IT-025 | GET /api/app/featured-cycle-items?period=&type= 周期与类型同时过滤 | featured/App 端周期推荐查询#周期与类型同时过滤 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-025/` | ✅ |
| TC-featured-IT-026 | GET /api/app/featured-cycle-items?period= 周期过滤后无条目返回空数组 | featured/App 端周期推荐查询#周期过滤后无条目返回空数组 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-026/` | ✅ |
| TC-featured-IT-027 | GET /api/app/featured-cycle-items?period= 非法周期值返回 400 | featured/App 端周期推荐查询#非法周期值被拒绝 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-027/` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：19
- ✅ 通过：19 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
