# 追溯矩阵（交付核对）：activity-subtitle

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change activity-subtitle`

## 需求与场景
- **activity/App 端活动查询**: 查询上架城市的活动 / 下线活动不可见 / 城市上架状态不影响活动详情可见性 / 活动详情返回富文本 / 活动副标题下发且未填时为 null
- **activity/web 端活动管理页面**: 活动列表与上下线 / 活动表单无地图选项即可保存 / 活动表单富文本编辑 / 活动表单填写景观并回显 / 活动表单填写副标题并回显
- **activity/活动管理**: 创建活动 / 缺少必填项被拒绝 / 请求体携带 cityId 不影响创建 / 活动列表不按城市过滤 / 活动上下线切换 / 副标题可写可改可空 / 景观字段可写可改可空
- **featured/App 端周期推荐查询**: 查询四个周期的推荐列表 / 同一 target 跨周期时下发全部周期 / 按周期过滤时 period 数组仍含其他周期 / 类型过滤不影响 period 数组 / 不可下发条目不贡献周期 / 不同 target 的周期集合互不影响 / 按周期过滤 / 周期与类型同时过滤 / 按内容类型过滤 / 类型过滤后周期为空仍返回空数组 / 周期过滤后无条目返回空数组 / 非法类型值被拒绝 / 非法周期值被拒绝 / 关联实体不可见时条目不下发 / 城市未上架不影响路线类条目 / 大使下线连带隐藏路线类条目 / 组内按排序号升序 / 活动类条目下发活动基础信息 / 活动未填副标题时 target.subtitle 为 null / 路线类条目下发路线基础信息且不覆盖手填文案 / 文章类条目下发文章基础信息 / 活动无图片时 cover 为 null

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-activity-IT-001 | POST /api/admin/activities 创建完整活动 | activity/活动管理#创建活动 | api-spec.json#/paths/~1api~1admin~1activities/post | ambassador-route-activity → activity-drop-city-link | IT | `test-evidence/activity-subtitle/TC-activity-IT-001/` | ✅ |
| TC-activity-IT-002 | POST /api/admin/activities 缺必填被拒绝 | activity/活动管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1activities/post | ambassador-route-activity → activity-drop-city-link | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-002/` | ⬜ |
| TC-activity-IT-003 | PUT /api/admin/activities/{id}/online 活动上下线切换 | activity/活动管理#活动上下线切换 | api-spec.json#/paths/~1api~1admin~1activities~1{id}~1online/put | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-003/` | ✅ |
| TC-activity-IT-004 | PUT /api/admin/activities/{id} 更新活动，请求体 cityId 被忽略 | activity/活动管理#请求体携带 cityId 不影响创建 | api-spec.json#/paths/~1api~1admin~1activities~1{id}/put | ambassador-route-activity → activity-drop-city-link | IT | `test-evidence/activity-subtitle/TC-activity-IT-004/` | ✅ |
| TC-activity-IT-005 | DELETE /api/admin/activities/{id} 物理删除活动 | activity/活动管理#创建活动 | api-spec.json#/paths/~1api~1admin~1activities~1{id}/delete | ambassador-route-activity → activity-drop-city-link | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-005/` | ⬜ |
| TC-activity-IT-006 | POST /api/admin/activities 富文本 img src 存 objectKey、admin 读时替换签名 URL | activity/活动管理#创建活动 | api-spec.json#/paths/~1api~1admin~1activities/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-006/` | ✅ |
| TC-activity-IT-007 | GET /api/app/activities 全局上线活动列表 | activity/App 端活动查询#查询上架城市的活动 | api-spec.json#/paths/~1api~1app~1activities/get | ambassador-route-activity → activity-drop-city-link → activity-subtitle | IT | `test-evidence/activity-subtitle/TC-activity-IT-007/` | ✅ |
| TC-activity-IT-008 | GET /api/app/activities 下线活动不可见、详情 404 | activity/App 端活动查询#下线活动不可见 | api-spec.json#/paths/~1api~1app~1activities~1{id}/get | ambassador-route-activity → activity-drop-city-link | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-008/` | ⬜ |
| TC-activity-IT-009 | GET /api/app/activities/{id} 详情返回富文本且 img src 为签名 URL | activity/App 端活动查询#活动详情返回富文本 | api-spec.json#/paths/~1api~1app~1activities~1{id}/get | ambassador-route-activity → activity-drop-city-link | IT | `test-evidence/activity-subtitle/TC-activity-IT-009/` | ✅ |
| TC-activity-IT-020 | 活动景观字段贯通 admin 写入与 admin/app 查询 | activity/活动管理#景观字段可写可改可空 | api-spec.json#/components/schemas/ActivityUpsertRequest | activity-landscape-field → activity-drop-city-link | IT | `test-evidence/activity-subtitle/TC-activity-IT-020/` | ✅ |
| TC-activity-IT-021 | GET /api/admin/activities/page 携带 cityId 不收窄结果 | activity/活动管理#活动列表不按城市过滤 | api-spec.json#/paths/~1api~1admin~1activities~1page/get | activity-drop-city-link | IT | `test-evidence/regression/activity/TC-activity-IT-021/` | ⬜ |
| TC-activity-IT-022 | GET /api/app/activities/{id} 详情不受城市上架状态影响 | activity/App 端活动查询#城市上架状态不影响活动详情可见性 | api-spec.json#/paths/~1api~1app~1activities~1{id}/get | activity-drop-city-link | IT | `test-evidence/regression/activity/TC-activity-IT-022/` | ⬜ |
| TC-activity-IT-023 | 活动副标题可写可改可清空（admin 侧） | activity/活动管理#副标题可写可改可空 | api-spec.json#/components/schemas/ActivityUpsertRequest | activity-subtitle | IT | `test-evidence/activity-subtitle/TC-activity-IT-023/` | ✅ |
| TC-activity-IT-024 | GET /api/app/activities 列表与详情下发 subtitle，未填时为 null | activity/App 端活动查询#活动副标题下发且未填时为 null | api-spec.json#/paths/~1api~1app~1activities/get | activity-subtitle | IT | `test-evidence/activity-subtitle/TC-activity-IT-024/` | ✅ |
| TC-activity-WEB-001 | 活动列表展示与上下线开关 | activity/web 端活动管理页面#活动列表与上下线 | - | ambassador-route-activity → activity-drop-city-link | WEB | `test-evidence/ambassador-route-activity/TC-activity-WEB-001/` | ⬜ |
| TC-activity-WEB-002 | 活动表单富文本编辑并回显 | activity/web 端活动管理页面#活动表单富文本编辑 | - | ambassador-route-activity → activity-drop-city-link | WEB | `test-evidence/ambassador-route-activity/TC-activity-WEB-002/` | ⬜ |
| TC-activity-WEB-003 | 活动表单填写景观并回显 | activity/web 端活动管理页面#活动表单填写景观并回显 | - | activity-landscape-field → activity-drop-city-link | WEB | `test-evidence/activity-landscape-field/TC-activity-WEB-003/` | ⬜ |
| TC-activity-WEB-004 | 活动表单无地图选项即可保存 | activity/web 端活动管理页面#活动表单无地图选项即可保存 | - | activity-drop-city-link | WEB | `test-evidence/regression/activity/TC-activity-WEB-004/` | ⬜ |
| TC-activity-WEB-005 | 活动表单填写副标题并回显 | activity/web 端活动管理页面#活动表单填写副标题并回显 | - | activity-subtitle | WEB | `test-evidence/regression/activity/TC-activity-WEB-005/` | ⬜ |
| TC-featured-IT-016 | GET /api/app/featured-cycle-items 扁平数组带 period 周期数组且只含上线条目 | featured/App 端周期推荐查询#查询四个周期的推荐列表 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-016/` | ✅ |
| TC-featured-IT-017 | GET /api/app/featured-cycle-items 关联实体不可见时条目不下发 | featured/App 端周期推荐查询#关联实体不可见时条目不下发 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-017/` | ✅ |
| TC-featured-IT-018 | GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目 | featured/App 端周期推荐查询#大使下线连带隐藏路线类条目 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-018/` | ✅ |
| TC-featured-IT-019 | GET /api/app/featured-cycle-items 按排序号升序 | featured/App 端周期推荐查询#组内按排序号升序 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-019/` | ✅ |
| TC-featured-IT-020 | GET /api/app/featured-cycle-items 城市未上架不影响路线类条目 | featured/App 端周期推荐查询#城市未上架不影响路线类条目 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-020/` | ✅ |
| TC-featured-IT-021 | GET /api/app/featured-cycle-items?type= 按内容类型过滤 | featured/App 端周期推荐查询#按内容类型过滤 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-021/` | ✅ |
| TC-featured-IT-022 | GET /api/app/featured-cycle-items?type= 类型过滤后无条目返回空数组 | featured/App 端周期推荐查询#类型过滤后周期为空仍返回空数组 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-022/` | ✅ |
| TC-featured-IT-023 | GET /api/app/featured-cycle-items?type= 非法类型值返回 400 | featured/App 端周期推荐查询#非法类型值被拒绝 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-023/` | ✅ |
| TC-featured-IT-024 | GET /api/app/featured-cycle-items?period= 按周期过滤 | featured/App 端周期推荐查询#按周期过滤 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-024/` | ✅ |
| TC-featured-IT-025 | GET /api/app/featured-cycle-items?period=&type= 周期与类型同时过滤 | featured/App 端周期推荐查询#周期与类型同时过滤 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-025/` | ✅ |
| TC-featured-IT-026 | GET /api/app/featured-cycle-items?period= 周期过滤后无条目返回空数组 | featured/App 端周期推荐查询#周期过滤后无条目返回空数组 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-026/` | ✅ |
| TC-featured-IT-027 | GET /api/app/featured-cycle-items?period= 非法周期值返回 400 | featured/App 端周期推荐查询#非法周期值被拒绝 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-027/` | ✅ |
| TC-featured-IT-028 | GET /api/app/featured-cycle-items 同一 target 跨周期时两条均下发全部周期 | featured/App 端周期推荐查询#同一 target 跨周期时下发全部周期 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-028/` | ✅ |
| TC-featured-IT-029 | GET /api/app/featured-cycle-items?period= 过滤后 period 数组仍含其他周期 | featured/App 端周期推荐查询#按周期过滤时 period 数组仍含其他周期 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-029/` | ✅ |
| TC-featured-IT-030 | GET /api/app/featured-cycle-items?type=&period= 类型过滤不影响 period 数组 | featured/App 端周期推荐查询#类型过滤不影响 period 数组 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-030/` | ✅ |
| TC-featured-IT-031 | GET /api/app/featured-cycle-items 不可下发条目不贡献周期 | featured/App 端周期推荐查询#不可下发条目不贡献周期 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-031/` | ✅ |
| TC-featured-IT-032 | GET /api/app/featured-cycle-items?period= 不同 target 的周期集合互不影响 | featured/App 端周期推荐查询#不同 target 的周期集合互不影响 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-032/` | ✅ |
| TC-featured-IT-034 | GET /api/app/featured-cycle-items 活动类条目下发活动基础信息 | featured/App 端周期推荐查询#活动类条目下发活动基础信息 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-target-basic-info → activity-subtitle | IT | `test-evidence/activity-subtitle/TC-featured-IT-034/` | ✅ |
| TC-featured-IT-035 | GET /api/app/featured-cycle-items 路线类条目下发路线基础信息且不覆盖手填文案 | featured/App 端周期推荐查询#路线类条目下发路线基础信息且不覆盖手填文案 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-target-basic-info | IT | `test-evidence/regression/featured/TC-featured-IT-035/` | ⬜ |
| TC-featured-IT-036 | GET /api/app/featured-cycle-items 文章类条目下发文章基础信息 | featured/App 端周期推荐查询#文章类条目下发文章基础信息 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-target-basic-info | IT | `test-evidence/regression/featured/TC-featured-IT-036/` | ⬜ |
| TC-featured-IT-037 | GET /api/app/featured-cycle-items 活动无图片时 target.cover 为 null | featured/App 端周期推荐查询#活动无图片时 cover 为 null | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-target-basic-info | IT | `test-evidence/activity-subtitle/TC-featured-IT-037/` | ✅ |
| TC-featured-IT-038 | GET /api/app/featured-cycle-items 活动未填副标题时 target.subtitle 为 null | featured/App 端周期推荐查询#活动未填副标题时 target.subtitle 为 null | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | activity-subtitle | IT | `test-evidence/activity-subtitle/TC-featured-IT-038/` | ✅ |

## 覆盖核对

- ⚠ 未覆盖：activity/活动管理#缺少必填项被拒绝 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：activity/活动管理#请求体携带 cityId 不影响创建 无 WEB/APP 用例且无 UT(@scenario) 覆盖

## 测试统计
- 总数：41
- ✅ 通过：29 (70.7%)
- ❌ 失败：0
- ⬜ 未测：12
