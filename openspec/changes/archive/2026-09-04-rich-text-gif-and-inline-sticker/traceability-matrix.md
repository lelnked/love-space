# 追溯矩阵（交付核对）：rich-text-gif-and-inline-sticker

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change rich-text-gif-and-inline-sticker`

## 需求与场景
- **file/objectKey 两段式生命周期与绑定校验**: 未绑定图片在业务保存时被绑定 / 已绑定图片重复提交不再复制 / 非法 objectKey 格式被拒绝 / 业务保存失败后源图仍可重试 / 富文本内联小图放行 / 富文本内联图超限被拒绝 / 富文本内联图类型不符被拒绝
- **file/图片上传凭证签发**: 签发合法图片类型的上传凭证 / 非图片类型被拒绝 / 未登录无法获取凭证 / 签发 gif 类型的上传凭证
- **file/图片上传的界面交互**: 单图控件三态切换 / 多图并发上传 / 非图片类型在选择阶段被拦 / 上传失败不阻塞表单 / 富文本粘贴大图走 OSS 上传 / 富文本粘贴小表情内联 / 富文本粘贴非白名单类型被拦

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-activity-IT-006 | POST /api/admin/activities 富文本 img src 存 objectKey、admin 读时替换签名 URL | activity/活动管理#创建活动 | api-spec.json#/paths/~1api~1admin~1activities/post | ambassador-route-activity → rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-activity-IT-006/` | ✅ |
| TC-activity-IT-009 | GET /api/app/activities/{id} 详情返回富文本且 img src 为签名 URL | activity/App 端活动查询#活动详情返回富文本 | api-spec.json#/paths/~1api~1app~1activities~1{id}/get | ambassador-route-activity → activity-drop-city-link → rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-activity-IT-009/` | ✅ |
| TC-activity-IT-025 | POST/PUT /api/admin/activities 富文本内联小图放行，admin/app 读取原样透传 | file/objectKey 两段式生命周期与绑定校验#富文本内联小图放行 | api-spec.json#/paths/~1api~1admin~1activities/post | rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-activity-IT-025/` | ✅ |
| TC-activity-IT-026 | POST /api/admin/activities 富文本内联图超限被拒绝（3 KB 边界） | file/objectKey 两段式生命周期与绑定校验#富文本内联图超限被拒绝 | api-spec.json#/paths/~1api~1admin~1activities/post | rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-activity-IT-026/` | ✅ |
| TC-activity-IT-027 | POST /api/admin/activities 富文本内联图类型不符被拒绝 | file/objectKey 两段式生命周期与绑定校验#富文本内联图类型不符被拒绝 | api-spec.json#/paths/~1api~1admin~1activities/post | rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-activity-IT-027/` | ✅ |
| TC-activity-WEB-002 | 活动表单富文本编辑并回显 | activity/web 端活动管理页面#活动表单富文本编辑 | - | ambassador-route-activity → activity-drop-city-link | WEB | `test-evidence/ambassador-route-activity/TC-activity-WEB-002/` | ⬜ |
| TC-activity-WEB-006 | 富文本粘贴大图（> 3 KB）走 OSS 上传链路 | file/图片上传的界面交互#富文本粘贴大图走 OSS 上传 | - | rich-text-gif-and-inline-sticker | WEB | `test-evidence/regression/activity/TC-activity-WEB-006/` | ⬜ |
| TC-activity-WEB-007 | 富文本粘贴小表情（≤ 3 KB gif）内联为 data URL，不发起上传 | file/图片上传的界面交互#富文本粘贴小表情内联 | - | rich-text-gif-and-inline-sticker | WEB | `test-evidence/regression/activity/TC-activity-WEB-007/` | ⬜ |
| TC-activity-WEB-008 | 富文本粘贴非白名单类型（svg）被拦并提示 | file/图片上传的界面交互#富文本粘贴非白名单类型被拦 | - | rich-text-gif-and-inline-sticker | WEB | `test-evidence/regression/activity/TC-activity-WEB-008/` | ⬜ |
| TC-article-IT-010 | POST /api/admin/articles 富文本 img src 存 objectKey、admin 读时替换签名 URL | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles/post | article-and-featured-feed → rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-article-IT-010/` | ✅ |
| TC-article-IT-014 | GET /api/app/articles/{id} 详情返回富文本且 img src 为签名 URL | article/App 端文章查询#文章详情返回富文本 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed → rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-article-IT-014/` | ✅ |
| TC-article-IT-021 | POST/PUT /api/admin/articles 富文本内联小图放行，admin/app 读取原样透传 | file/objectKey 两段式生命周期与绑定校验#富文本内联小图放行 | api-spec.json#/paths/~1api~1admin~1articles/post | rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-article-IT-021/` | ✅ |
| TC-article-IT-022 | POST /api/admin/articles 富文本内联图超限或类型不符被拒绝 | file/objectKey 两段式生命周期与绑定校验#富文本内联图超限被拒绝 | api-spec.json#/paths/~1api~1admin~1articles/post | rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-article-IT-022/` | ✅ |
| TC-file-IT-001 | 签发合法图片类型的上传凭证（测试档位不可实跑） | file/图片上传凭证签发#签发合法图片类型的上传凭证 | api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post | baseline-auth-manager-banner-log-file → rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-001/` | ✅ |
| TC-file-IT-002 | 非图片 contentType 返回 400 | file/图片上传凭证签发#非图片类型被拒绝 | api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post | baseline-auth-manager-banner-log-file → rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-002/` | ✅ |
| TC-file-IT-003 | 未登录请求上传凭证返回 401 | file/图片上传凭证签发#未登录无法获取凭证 | api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-004 | 未绑定图片在业务保存时被改写为 bound/ 前缀 | file/objectKey 两段式生命周期与绑定校验#未绑定图片在业务保存时被绑定 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-004/` | ✅ |
| TC-file-IT-005 | 已绑定图片原样回传不再复制，objectKey 保持不变 | file/objectKey 两段式生命周期与绑定校验#已绑定图片重复提交不再复制 | api-spec.json#/paths/~1api~1admin~1banners~1{id}/put | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-006 | 非白名单前缀的 objectKey 被拒绝 | file/objectKey 两段式生命周期与绑定校验#非法 objectKey 格式被拒绝 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-006/` | ✅ |
| TC-file-IT-007 | 非白名单后缀与路径穿越的 objectKey 被拒绝 | file/objectKey 两段式生命周期与绑定校验#非法 objectKey 格式被拒绝 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-007/` | ✅ |
| TC-file-IT-008 | 业务保存失败后同一 objectKey 可重试成功 | file/objectKey 两段式生命周期与绑定校验#业务保存失败后源图仍可重试 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-014 | 签发 gif 类型的上传凭证（测试档位不可实跑） | file/图片上传凭证签发#签发 gif 类型的上传凭证 | api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post | rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-014/` | ✅ |
| TC-file-IT-015 | gif 后缀 objectKey 通过绑定校验，svg 后缀仍被拒绝 | file/objectKey 两段式生命周期与绑定校验#未绑定图片在业务保存时被绑定 | api-spec.json#/paths/~1api~1admin~1banners/post | rich-text-gif-and-inline-sticker | IT | `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-015/` | ✅ |

## 覆盖核对

- ⚠ 未覆盖：file/图片上传的界面交互#单图控件三态切换 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：file/图片上传的界面交互#多图并发上传 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：file/图片上传的界面交互#非图片类型在选择阶段被拦 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：file/图片上传的界面交互#上传失败不阻塞表单 无 WEB/APP 用例且无 UT(@scenario) 覆盖

## 测试统计
- 总数：23
- ✅ 通过：16 (69.6%)
- ❌ 失败：0
- ⬜ 未测：7
