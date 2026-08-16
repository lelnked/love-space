# 追溯矩阵（交付核对）：article-and-featured-feed

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change article-and-featured-feed`

## 需求与场景
- **article/App 端文章查询**: 查询栏目与文章列表 / 下线文章不可见 / 失去所有栏目的文章不可见 / 文章详情返回富文本
- **article/web 端文章管理页面**: 栏目管理增删改 / 文章列表与上下线 / 文章表单富文本编辑
- **article/文章栏目管理**: 创建栏目 / 缺少必填项被拒绝 / 删除栏目不影响文章数据
- **article/文章管理**: 创建文章 / 缺少必填项被拒绝 / 文章上下线切换
- **city/地图下架对精选推荐级联生效**: 下架城市后 app 端精选推荐不可见 / web 下架确认提示包含精选推荐
- **featured/App 端精选推荐查询**: 查询精选推荐信息流
- **featured/web 端精选推荐页面**: 精选推荐列表与上下线 / 新增精选推荐
- **featured/精选推荐管理**: 创建精选推荐 / 缺少必填项被拒绝 / 精选推荐上下线切换

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-article-IT-001 | POST /api/admin/article-categories 创建栏目 | article/文章栏目管理#创建栏目 | api-spec.json#/paths/~1api~1admin~1article-categories/post | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-001/` | ✅ |
| TC-article-IT-002 | POST /api/admin/article-categories 缺必填被拒绝 | article/文章栏目管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1article-categories/post | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-002/` | ✅ |
| TC-article-IT-003 | PUT /api/admin/article-categories/{id} 更新栏目 | article/文章栏目管理#创建栏目 | api-spec.json#/paths/~1api~1admin~1article-categories~1{id}/put | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-003/` | ✅ |
| TC-article-IT-004 | DELETE /api/admin/article-categories/{id} 删除栏目不影响文章数据 | article/文章栏目管理#删除栏目不影响文章数据 | api-spec.json#/paths/~1api~1admin~1article-categories~1{id}/delete | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-004/` | ✅ |
| TC-article-IT-005 | POST /api/admin/articles 创建关联多栏目的完整文章 | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles/post | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-005/` | ✅ |
| TC-article-IT-006 | POST /api/admin/articles 缺必填或栏目不存在被拒绝 | article/文章管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1articles/post | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-006/` | ✅ |
| TC-article-IT-007 | PUT /api/admin/articles/{id} 更新文章与栏目关联 | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles~1{id}/put | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-007/` | ✅ |
| TC-article-IT-008 | PUT /api/admin/articles/{id}/online 文章上下线切换 | article/文章管理#文章上下线切换 | api-spec.json#/paths/~1api~1admin~1articles~1{id}~1online/put | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-008/` | ✅ |
| TC-article-IT-009 | DELETE /api/admin/articles/{id} 物理删除文章 | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles~1{id}/delete | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-009/` | ✅ |
| TC-article-IT-010 | POST /api/admin/articles 富文本 img src 存 objectKey、admin 读时替换签名 URL | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles/post | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-010/` | ✅ |
| TC-article-IT-011 | GET /api/app/article-categories 与 /api/app/articles 均按权重升序 | article/App 端文章查询#查询栏目与文章列表 | api-spec.json#/paths/~1api~1app~1article-categories/get | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-011/` | ✅ |
| TC-article-IT-012 | GET /api/app/articles 下线文章不可见、详情 404 | article/App 端文章查询#下线文章不可见 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-012/` | ✅ |
| TC-article-IT-013 | GET /api/app/articles/{id} 失去所有栏目的文章不可见 | article/App 端文章查询#失去所有栏目的文章不可见 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-013/` | ✅ |
| TC-article-IT-014 | GET /api/app/articles/{id} 详情返回富文本且 img src 为签名 URL | article/App 端文章查询#文章详情返回富文本 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-article-IT-014/` | ✅ |
| TC-article-WEB-001 | 文章栏目页新增与删除 | article/web 端文章管理页面#栏目管理增删改 | - | article-and-featured-feed | WEB | `test-evidence/article-and-featured-feed/TC-article-WEB-001/` | ✅ |
| TC-article-WEB-002 | 文章列表展示与上下线开关 | article/web 端文章管理页面#文章列表与上下线 | - | article-and-featured-feed | WEB | `test-evidence/article-and-featured-feed/TC-article-WEB-002/` | ✅ |
| TC-article-WEB-003 | 文章表单富文本编辑与栏目多选回显 | article/web 端文章管理页面#文章表单富文本编辑 | - | article-and-featured-feed | WEB | `test-evidence/article-and-featured-feed/TC-article-WEB-003/` | ✅ |
| TC-city-IT-007 | 城市下架后 app 端精选推荐不可见（级联） | city/地图下架对精选推荐级联生效#下架城市后 app 端精选推荐不可见 | api-spec.json#/paths/~1api~1app~1featured-items/get | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-city-IT-007/` | ✅ |
| TC-city-WEB-004 | 城市下架确认提示包含精选推荐级联说明 | city/地图下架对精选推荐级联生效#web 下架确认提示包含精选推荐 | - | article-and-featured-feed | WEB | `test-evidence/article-and-featured-feed/TC-city-WEB-004/` | ✅ |
| TC-featured-IT-001 | POST /api/admin/featured-items 创建精选推荐 | featured/精选推荐管理#创建精选推荐 | api-spec.json#/paths/~1api~1admin~1featured-items/post | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-featured-IT-001/` | ✅ |
| TC-featured-IT-002 | POST /api/admin/featured-items 缺 banner 或城市不存在被拒绝 | featured/精选推荐管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1featured-items/post | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-featured-IT-002/` | ✅ |
| TC-featured-IT-003 | PUT /api/admin/featured-items/{id}/online 上下线切换 | featured/精选推荐管理#精选推荐上下线切换 | api-spec.json#/paths/~1api~1admin~1featured-items~1{id}~1online/put | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-featured-IT-003/` | ✅ |
| TC-featured-IT-004 | PUT /api/admin/featured-items/{id} 更新条目且 cityId 不可变 | featured/精选推荐管理#创建精选推荐 | api-spec.json#/paths/~1api~1admin~1featured-items~1{id}/put | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-featured-IT-004/` | ✅ |
| TC-featured-IT-005 | DELETE /api/admin/featured-items/{id} 物理删除 | featured/精选推荐管理#创建精选推荐 | api-spec.json#/paths/~1api~1admin~1featured-items~1{id}/delete | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-featured-IT-005/` | ✅ |
| TC-featured-IT-006 | GET /api/app/featured-items 信息流仅含上线条目且按创建时间倒序 | featured/App 端精选推荐查询#查询精选推荐信息流 | api-spec.json#/paths/~1api~1app~1featured-items/get | article-and-featured-feed | IT | `test-evidence/article-and-featured-feed/TC-featured-IT-006/` | ✅ |
| TC-featured-WEB-001 | 精选推荐列表展示与上下线开关 | featured/web 端精选推荐页面#精选推荐列表与上下线 | - | article-and-featured-feed | WEB | `test-evidence/article-and-featured-feed/TC-featured-WEB-001/` | ✅ |
| TC-featured-WEB-002 | 弹窗表单新增精选推荐 | featured/web 端精选推荐页面#新增精选推荐 | - | article-and-featured-feed | WEB | `test-evidence/article-and-featured-feed/TC-featured-WEB-002/` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：27
- ✅ 通过：27 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
