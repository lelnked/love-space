# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/{domain}/{it,web}.md`（living 文件，runner 独占回写状态）。
> 落点裁决（按 `tests/modules.md`）：`file` 为纯 API 域，只有 `tests/file/it.md`；富文本相关的 IT 场景借活动/文章写接口落 `tests/activity/it.md`、`tests/article/it.md`（读取透传同时断言 app 端 `/api/app/{activities,articles}/{id}`）；富文本粘贴的 WEB 场景归 activity 域 `tests/activity/web.md`（活动表单页 `/love-space/activities`）。
> 契约：`contracts/api-spec.json` 中 `/api/admin/files/upload-credentials` 的 summary 仍写「仅接受 image/png|jpeg|webp」，按 tasks 1.4 于 apply 阶段同步为含 gif 与新 400 文案；富文本字段无 schema 变化，用例按既有路径关联，无 ⚠️ 待补契约项。

## 新增用例

- TC-file-IT-014: 签发 gif 类型的上传凭证（ADDED Scenario: file/图片上传凭证签发#签发 gif 类型的上传凭证；test profile 无 STS 桩时最低断言「不再命中入参 400」）
- TC-file-IT-015: gif 后缀 objectKey 通过绑定校验，svg 后缀仍被拒绝（MODIFIED Requirement「objectKey 两段式生命周期与绑定校验」后缀白名单加 gif；借 banner 写接口）
- TC-activity-IT-025: POST/PUT /api/admin/activities 富文本内联小图放行，admin/app 读取原样透传（ADDED Scenario: file/objectKey 两段式生命周期与绑定校验#富文本内联小图放行）
- TC-activity-IT-026: POST /api/admin/activities 富文本内联图超限被拒绝，含 3072/3073/4096 字节边界（ADDED Scenario: file/objectKey 两段式生命周期与绑定校验#富文本内联图超限被拒绝）
- TC-activity-IT-027: POST /api/admin/activities 富文本内联图类型不符被拒绝（ADDED Scenario: file/objectKey 两段式生命周期与绑定校验#富文本内联图类型不符被拒绝）
- TC-article-IT-021: POST/PUT /api/admin/articles 富文本内联小图放行，admin/app 读取原样透传（ADDED Scenario: file/objectKey 两段式生命周期与绑定校验#富文本内联小图放行）
- TC-article-IT-022: POST /api/admin/articles 富文本内联图超限或类型不符被拒绝（ADDED Scenario: file/objectKey 两段式生命周期与绑定校验#富文本内联图超限被拒绝 + #富文本内联图类型不符被拒绝）
- TC-activity-WEB-006: 富文本粘贴大图（> 3 KB）走 OSS 上传链路（ADDED Scenario: file/图片上传的界面交互#富文本粘贴大图走 OSS 上传）
- TC-activity-WEB-007: 富文本粘贴小表情（≤ 3 KB gif）内联为 data URL，不发起上传（ADDED Scenario: file/图片上传的界面交互#富文本粘贴小表情内联）
- TC-activity-WEB-008: 富文本粘贴非白名单类型（svg）被拦并提示（ADDED Scenario: file/图片上传的界面交互#富文本粘贴非白名单类型被拦；附带断言纯文本粘贴保持默认行为）

## 修改用例

- TC-file-IT-002: 非图片 contentType 返回 400（MODIFIED: `image/gif` 进入白名单不再作拒绝样本，改用 `image/svg+xml`、`image/bmp`；400 文案改「仅支持 png/jpeg/webp/gif 图片」）
- TC-file-IT-001: 签发合法图片类型的上传凭证（MODIFIED: 预期结果补 `image/gif → gif` 后缀映射；仍受 STS 桩缺失前置约束）

## 需重测用例

行为未变但受本 change 实现影响（admin 两个 `ObjectKeyValidator` key 正则与 MIME 集合改动；admin/app `RichTextImages.rewriteSrc` 增加 data URL 分支），需回归确认：

- TC-file-IT-004: 未绑定图片在业务保存时被改写为 bound/ 前缀（png key 经改后正则仍通过）
- TC-file-IT-006: 非白名单前缀的 objectKey 被拒绝
- TC-file-IT-007: 非白名单后缀与路径穿越的 objectKey 被拒绝（`.exe`、穿越、空值仍拒）
- TC-activity-IT-006: 富文本 img src 存 objectKey、admin 读时替换签名 URL（objectKey 分支不受 data URL 分支影响）
- TC-activity-IT-009: GET /api/app/activities/{id} 详情 img src 为签名 URL（app 端 rewriteSrc 改动）
- TC-article-IT-010: 富文本 img src 存 objectKey、admin 读时替换签名 URL
- TC-article-IT-014: GET /api/app/articles/{id} 详情 img src 为签名 URL（app 端 rewriteSrc 改动）
- TC-activity-WEB-002: 活动表单富文本编辑并回显（RichTextEditor 粘贴/拖入拦截与 insertImage 分流改动后，工具栏插图链路回归；既有状态为环境阻塞）

## 执行汇总

- IT（2026-09-04，admin `http://localhost:21423` test profile / app `http://localhost:8081`）：总数 16 ｜ ✅ 16 ｜ ❌ 0 ｜ ⚠️ 1 ｜ 未执行 0。
  - 首轮 TC-file-IT-015 ❌：Banner/City/Merchant 五个 DTO 元素级 objectKey 正则漏加 gif，已修（与 validator 同步）并重跑 ✅。
  - ⚠️ TC-file-IT-007：空值消息为 `@NotBlank` 与 `@Pattern` 两段拼接，既有 DTO 行为，本 change 未改，待人工裁决。
  - 存证：`test-evidence/rich-text-gif-and-inline-sticker/<TC>/`。
- WEB（TC-activity-WEB-006/007/008、重测 WEB-002）：**未执行**——本会话 playwright-company MCP 连接超时，远程浏览器不可用；前端已起在 `http://100.100.117.79:5173/love-space/`，环境恢复后跑 `/run-web-test --change rich-text-gif-and-inline-sticker`。
