## Why

运营在活动/文章富文本里 Ctrl+V 粘贴图片时，Quill 默认剪贴板把 base64 data URL 直接写进 HTML，绕开 OSS 上传链路，保存时被后端 objectKey 校验拒绝（400「图片对象不可用」），且编辑器对不支持的类型（gif 等）既不拦也不提示，用户只有在保存时才发现失败。同时运营希望能在正文里贴小表情包，这类图片体积极小，逐个走 OSS 直传/绑定既繁琐又产生大量碎对象。

## What Changes

- 图片类型白名单新增 `image/gif`（扩展名 `gif`），三端一致：上传凭证接口、objectKey 绑定校验、前端所有上传控件与富文本编辑器。**不加 svg**（可内嵌脚本，安全取舍）。
- 富文本编辑器拦截粘贴/拖入：白名单内的图片文件走既有 OSS 上传链路；非白名单文件**不插入编辑器**并以全局提示告知「仅支持 png/jpeg/webp/gif 图片」。
- 富文本新增「内联小图」规则：白名单内且 **≤ 3 KB** 的图片以 data URL 直接内联在 HTML 中，不上传 OSS；超过 3 KB 仍走 OSS。后端保存校验对满足条件的 `data:image/(png|jpeg|webp|gif);base64,...` 放行，不满足（类型不符或解码后超 3 KB）仍返回 400「图片对象不可用」。
- admin 读取与 app 读取富文本时，data URL 的 img src 原样透传，不做签名替换。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `file`: 上传凭证与绑定校验的图片白名单扩为 png/jpeg/webp/gif；富文本图片绑定校验新增 ≤ 3 KB data URL 内联放行；前端白名单过滤在富文本粘贴/拖入场景也生效并给出提示。

## Impact

- **admin**：`UploadCredentialRequest` 校验正则与提示文案、`FileService` MIME→ext 映射、`AliyunOssObjectKeyValidator` / `StubObjectKeyValidator` 的 key 正则与 MIME 集合、`RichTextImages`（新增 data URL 判定与放行），`ActivityService` / `ArticleService` 保存与读取的 img src 改写。
- **app**：`RichTextImages.rewriteSrc` 读取时跳过 data URL，避免签名器把 data URL 当 objectKey。
- **web**：`ossUpload.ts`、`ImageUploader.tsx`、`ImageUploaderList.tsx`、`types/image.ts` 白名单；`RichTextEditor.tsx` 粘贴/拖入拦截、非白名单提示、≤ 3 KB 内联分流。
- **契约**：`contracts/api-spec.json` 上传凭证接口的 contentType 枚举与 400 文案；`love-space-app/docs/openapi.json` 无字段变化。
- **数据**：无 schema 变更；富文本列已为大文本类型，内联小图增量可忽略。
- **无新增依赖、无环境变量。**
