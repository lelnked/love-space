# 追溯矩阵（交付核对）：article-cover-title-intro-tags

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change article-cover-title-intro-tags`

## 需求与场景
- **article/App 端文章查询**: 查询栏目与文章列表 / 未设封面标题时列表回落文章标题 / 详情返回引言与标签 / 下线文章不可见 / 失去所有栏目的文章不可见 / 文章详情返回富文本
- **article/web 端文章管理页面**: 栏目管理增删改 / 文章列表与上下线 / 文章表单富文本编辑 / 表单填写封面标题、引言与标签 / 存量文章封面标题为空时表单可正常打开
- **article/文章管理**: 创建文章 / 创建带封面标题、引言与标签的文章 / 封面标题、引言、标签均可省略 / 缺少必填项被拒绝 / 文章上下线切换

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-article-IT-001 | POST /api/admin/article-categories 创建栏目 | article/文章栏目管理#创建栏目 | api-spec.json#/paths/~1api~1admin~1article-categories/post | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-001/` | ✅ |
| TC-article-IT-005 | POST /api/admin/articles 创建关联多栏目的完整文章 | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles/post | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-005/` | ✅ |
| TC-article-IT-006 | POST /api/admin/articles 缺必填或栏目不存在被拒绝 | article/文章管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1articles/post | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-006/` | ✅ |
| TC-article-IT-007 | PUT /api/admin/articles/{id} 更新文章与栏目关联 | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles~1{id}/put | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-007/` | ✅ |
| TC-article-IT-008 | PUT /api/admin/articles/{id}/online 文章上下线切换 | article/文章管理#文章上下线切换 | api-spec.json#/paths/~1api~1admin~1articles~1{id}~1online/put | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-008/` | ✅ |
| TC-article-IT-009 | DELETE /api/admin/articles/{id} 物理删除文章 | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles~1{id}/delete | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-009/` | ✅ |
| TC-article-IT-010 | POST /api/admin/articles 富文本 img src 存 objectKey、admin 读时替换签名 URL | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles/post | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-010/` | ✅ |
| TC-article-IT-011 | GET /api/app/article-categories 与 /api/app/articles 均按权重升序 | article/App 端文章查询#查询栏目与文章列表 | api-spec.json#/paths/~1api~1app~1article-categories/get | article-cover-title-intro-tags | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-011/` | ✅ |
| TC-article-IT-012 | GET /api/app/articles 下线文章不可见、详情 404 | article/App 端文章查询#下线文章不可见 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-012/` | ✅ |
| TC-article-IT-013 | GET /api/app/articles/{id} 失去所有栏目的文章不可见 | article/App 端文章查询#失去所有栏目的文章不可见 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-013/` | ✅ |
| TC-article-IT-014 | GET /api/app/articles/{id} 详情返回富文本且 img src 为签名 URL | article/App 端文章查询#文章详情返回富文本 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-014/` | ✅ |
| TC-article-IT-015 | POST /api/admin/articles 创建带封面标题、引言与标签的文章 | article/文章管理#创建带封面标题、引言与标签的文章 | api-spec.json#/paths/~1api~1admin~1articles/post | article-cover-title-intro-tags | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-015/` | ✅ |
| TC-article-IT-016 | POST /api/admin/articles 省略封面标题、引言、标签 | article/文章管理#封面标题、引言、标签均可省略 | api-spec.json#/paths/~1api~1admin~1articles/post | article-cover-title-intro-tags | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-016/` | ✅ |
| TC-article-IT-017 | PUT /api/admin/articles/{id} 空白值按 null 存、标签空白项剔除 | article/文章管理#创建带封面标题、引言与标签的文章 | api-spec.json#/paths/~1api~1admin~1articles~1{id}/put | article-cover-title-intro-tags | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-017/` | ✅ |
| TC-article-IT-018 | GET /api/app/articles 未设封面标题时回落文章标题 | article/App 端文章查询#未设封面标题时列表回落文章标题 | api-spec.json#/paths/~1api~1app~1articles/get | article-cover-title-intro-tags | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-018/` | ✅ |
| TC-article-IT-019 | GET /api/app/articles/{id} 详情返回引言与标签 | article/App 端文章查询#详情返回引言与标签 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-cover-title-intro-tags | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-019/` | ✅ |
| TC-article-WEB-001 | 文章栏目页新增与删除 | article/web 端文章管理页面#栏目管理增删改 | - | article-and-featured-feed | WEB | `test-evidence/regression/article/TC-article-WEB-001/` | ✅ |
| TC-article-WEB-002 | 文章列表展示与上下线开关 | article/web 端文章管理页面#文章列表与上下线 | - | article-cover-title-intro-tags | WEB | `test-evidence/article-cover-title-intro-tags/TC-article-WEB-002/` | ✅ |
| TC-article-WEB-003 | 文章表单富文本编辑与栏目多选回显 | article/web 端文章管理页面#文章表单富文本编辑 | - | article-and-featured-feed | WEB | `test-evidence/article-cover-title-intro-tags/TC-article-WEB-003/` | ✅ |
| TC-article-WEB-004 | 文章表单填写封面标题、引言与多条标签并回显 | article/web 端文章管理页面#表单填写封面标题、引言与标签 | - | article-cover-title-intro-tags | WEB | `test-evidence/article-cover-title-intro-tags/TC-article-WEB-004/` | ✅ |
| TC-article-WEB-005 | 存量无封面标题与引言的文章打开编辑表单不报错 | article/web 端文章管理页面#存量文章封面标题为空时表单可正常打开 | - | article-cover-title-intro-tags | WEB | `test-evidence/article-cover-title-intro-tags/TC-article-WEB-005/` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：21
- ✅ 通过：21 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
