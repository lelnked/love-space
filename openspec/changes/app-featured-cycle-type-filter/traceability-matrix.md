# 追溯矩阵（交付核对）：app-featured-cycle-type-filter

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change app-featured-cycle-type-filter`

## 需求与场景
- **featured/App 端周期推荐查询**: 查询四个周期的推荐列表 / 按内容类型过滤 / 类型过滤后周期为空仍返回空数组 / 非法类型值被拒绝 / 关联实体不可见时条目不下发 / 城市未上架不影响路线类条目 / 大使下线连带隐藏路线类条目 / 组内按排序号升序

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-featured-IT-016 | GET /api/app/featured-cycle-items 四周期分组齐全且只含上线条目 | featured/App 端周期推荐查询#查询四个周期的推荐列表 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-feed | IT | `test-evidence/app-featured-cycle-type-filter/TC-featured-IT-016/` | ✅ |
| TC-featured-IT-017 | GET /api/app/featured-cycle-items 关联实体不可见时条目不下发 | featured/App 端周期推荐查询#关联实体不可见时条目不下发 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-feed | IT | `test-evidence/app-featured-cycle-type-filter/TC-featured-IT-017/` | ✅ |
| TC-featured-IT-018 | GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目 | featured/App 端周期推荐查询#大使下线连带隐藏路线类条目 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | route-decouple-city-online | IT | `test-evidence/app-featured-cycle-type-filter/TC-featured-IT-018/` | ✅ |
| TC-featured-IT-019 | GET /api/app/featured-cycle-items 组内按排序号升序 | featured/App 端周期推荐查询#组内按排序号升序 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-feed | IT | `test-evidence/app-featured-cycle-type-filter/TC-featured-IT-019/` | ✅ |
| TC-featured-IT-020 | GET /api/app/featured-cycle-items 城市未上架不影响路线类条目 | featured/App 端周期推荐查询#城市未上架不影响路线类条目 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | route-decouple-city-online | IT | `test-evidence/app-featured-cycle-type-filter/TC-featured-IT-020/` | ✅ |
| TC-featured-IT-021 | GET /api/app/featured-cycle-items?type= 按内容类型过滤 | featured/App 端周期推荐查询#按内容类型过滤 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-featured-cycle-type-filter | IT | `test-evidence/app-featured-cycle-type-filter/TC-featured-IT-021/` | ✅ |
| TC-featured-IT-022 | GET /api/app/featured-cycle-items?type= 过滤后周期为空仍返回空数组 | featured/App 端周期推荐查询#类型过滤后周期为空仍返回空数组 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-featured-cycle-type-filter | IT | `test-evidence/app-featured-cycle-type-filter/TC-featured-IT-022/` | ✅ |
| TC-featured-IT-023 | GET /api/app/featured-cycle-items?type= 非法类型值返回 400 | featured/App 端周期推荐查询#非法类型值被拒绝 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-featured-cycle-type-filter | IT | `test-evidence/app-featured-cycle-type-filter/TC-featured-IT-023/` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：8
- ✅ 通过：8 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
