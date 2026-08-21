# 追溯矩阵（交付核对）：featured-cycle-feed

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change featured-cycle-feed`

## 需求与场景
- **featured/App 端周期推荐查询**: 查询四个周期的推荐列表 / 关联实体不可见时条目不下发 / 大使下线连带隐藏路线类条目 / 组内按排序号升序
- **featured/web 端周期推荐页面**: 周期 Tab 切换与列表展示 / 表单按类型切换字段 / 文章类型自动带出主标题 / 新增周期推荐 / 周期推荐上下线与删除
- **featured/周期推荐条目管理**: 创建活动类周期推荐 / 创建路线类周期推荐 / 创建文章类周期推荐 / 缺少类型必填项被拒绝 / 关联实体不存在被拒绝 / 周期与类型创建后不可变 / 按周期过滤列表 / 周期推荐上下线切换

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-featured-IT-001 | POST /api/admin/featured-items 创建精选推荐 | featured/精选推荐管理#创建精选推荐 | api-spec.json#/paths/~1api~1admin~1featured-items/post | article-and-featured-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-001/` | ✅ |
| TC-featured-IT-006 | GET /api/app/featured-items 信息流仅含上线条目且按创建时间倒序 | featured/App 端精选推荐查询#查询精选推荐信息流 | api-spec.json#/paths/~1api~1app~1featured-items/get | article-and-featured-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-006/` | ✅ |
| TC-featured-IT-007 | POST /api/admin/featured-cycle-items 创建活动类周期推荐 | featured/周期推荐条目管理#创建活动类周期推荐 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-007/` | ✅ |
| TC-featured-IT-008 | POST /api/admin/featured-cycle-items 创建路线类周期推荐 | featured/周期推荐条目管理#创建路线类周期推荐 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-008/` | ✅ |
| TC-featured-IT-009 | POST /api/admin/featured-cycle-items 创建文章类周期推荐 | featured/周期推荐条目管理#创建文章类周期推荐 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-009/` | ✅ |
| TC-featured-IT-010 | POST /api/admin/featured-cycle-items 类型必填项缺失被拒绝 | featured/周期推荐条目管理#缺少类型必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-010/` | ✅ |
| TC-featured-IT-011 | POST /api/admin/featured-cycle-items 关联实体不存在被拒绝 | featured/周期推荐条目管理#关联实体不存在被拒绝 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-011/` | ✅ |
| TC-featured-IT-012 | PUT /api/admin/featured-cycle-items/{id} 周期与类型创建后不可变 | featured/周期推荐条目管理#周期与类型创建后不可变 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/put | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-012/` | ✅ |
| TC-featured-IT-013 | GET /api/admin/featured-cycle-items/page 按周期过滤并按排序号升序 | featured/周期推荐条目管理#按周期过滤列表 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1page/get | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-013/` | ✅ |
| TC-featured-IT-014 | PUT /api/admin/featured-cycle-items/{id}/online 上下线切换 | featured/周期推荐条目管理#周期推荐上下线切换 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}~1online/put | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-014/` | ✅ |
| TC-featured-IT-015 | DELETE /api/admin/featured-cycle-items/{id} 物理删除 | featured/周期推荐条目管理 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/delete | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-015/` | ✅ |
| TC-featured-IT-016 | GET /api/app/featured-cycle-items 四周期分组齐全且只含上线条目 | featured/App 端周期推荐查询#查询四个周期的推荐列表 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-016/` | ✅ |
| TC-featured-IT-017 | GET /api/app/featured-cycle-items 关联实体不可见时条目不下发 | featured/App 端周期推荐查询#关联实体不可见时条目不下发 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-017/` | ✅ |
| TC-featured-IT-018 | GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目 | featured/App 端周期推荐查询#大使下线连带隐藏路线类条目 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-018/` | ✅ |
| TC-featured-IT-019 | GET /api/app/featured-cycle-items 组内按排序号升序 | featured/App 端周期推荐查询#组内按排序号升序 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-019/` | ✅ |
| TC-featured-WEB-001 | 精选推荐列表展示与上下线开关 | featured/web 端精选推荐页面#精选推荐列表与上下线 | - | article-and-featured-feed | WEB | `test-evidence/article-and-featured-feed/TC-featured-WEB-001/` | ✅ |
| TC-featured-WEB-002 | 弹窗表单新增精选推荐 | featured/web 端精选推荐页面#新增精选推荐 | - | article-and-featured-feed | WEB | `test-evidence/article-and-featured-feed/TC-featured-WEB-002/` | ✅ |
| TC-featured-WEB-003 | 周期推荐页四周期 Tab 切换与列表展示 | featured/web 端周期推荐页面#周期 Tab 切换与列表展示 | - | featured-cycle-feed | WEB | `test-evidence/regression/featured/TC-featured-WEB-003/` | ⬜ |
| TC-featured-WEB-004 | 新增弹窗按内容类型切换字段块 | featured/web 端周期推荐页面#表单按类型切换字段 | - | featured-cycle-feed | WEB | `test-evidence/regression/featured/TC-featured-WEB-004/` | ⬜ |
| TC-featured-WEB-005 | 周期生活法选中文章后自动带出主标题 | featured/web 端周期推荐页面#文章类型自动带出主标题 | - | featured-cycle-feed | WEB | `test-evidence/regression/featured/TC-featured-WEB-005/` | ⬜ |
| TC-featured-WEB-006 | 弹窗表单新增周期推荐 | featured/web 端周期推荐页面#新增周期推荐 | - | featured-cycle-feed | WEB | `test-evidence/regression/featured/TC-featured-WEB-006/` | ⬜ |
| TC-featured-WEB-007 | 周期推荐上下线切换与删除确认 | featured/web 端周期推荐页面#周期推荐上下线与删除 | - | featured-cycle-feed | WEB | `test-evidence/regression/featured/TC-featured-WEB-007/` | ⬜ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：22
- ✅ 通过：17 (77.3%)
- ❌ 失败：0
- ⬜ 未测：5

## 相关提交
- `57c8379` admin,app,web: 精选·你的周期活动推荐（change: featured-cycle-feed）
