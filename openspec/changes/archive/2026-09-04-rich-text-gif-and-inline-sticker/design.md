## Context

图片白名单目前散落在 admin（`UploadCredentialRequest` 正则、`FileService` MIME→ext、两个 `ObjectKeyValidator` 的 key 正则与 MIME 集合）与 web（`ossUpload.ts`、`ImageUploader`、`ImageUploaderList`、`RichTextEditor` 各自的 ACCEPT 常量）多处；富文本 img src 由 admin/app 各自的 `RichTextImages.rewriteSrc` 正则改写，保存时归一为 objectKey 逐个 `validateAndBind`，读取时逐个签名。Quill 默认剪贴板会把粘贴图片转成 base64 data URL 直接插入，绕开上传链路。

## Goals / Non-Goals

- Goals：白名单加 gif；富文本粘贴/拖入图片纳入上传链路；非白名单类型前端拦截并提示；≤ 3 KB 小图以 data URL 内联，后端放行且读取透传。
- Non-Goals：不加 svg；不引入 HTML 解析/净化库；不为已有存量数据做迁移；不改 App 客户端（data URL 由 `<img>` 原生渲染）。

## Decisions

1. **内联判定放在 `RichTextImages`，由 `rewriteSrc` 的调用方透明获得**
   admin 端在 `RichTextImages` 新增 `isInlineImage(src)`：正则匹配 `^data:image/(png|jpeg|webp|gif);base64,` 且 base64 解码字节数 ≤ 3072。`rewriteSrc` 对命中 `isInlineImage` 的 src **不调用 fn，原样输出**；对以 `data:` 开头但不命中的 src 直接抛 `IllegalArgumentException("图片对象不可用")`。这样 `ActivityService` / `ArticleService` 的保存与读取代码零改动，app 端 `RichTextImages.rewriteSrc` 只需同样跳过 `data:` 前缀（app 只读，不校验大小）。
   备选：在各 Service 的 lambda 里判断——两处重复，且读取路径也要各改一次。否决。
   备选：入库前把 data URL 也上传 OSS——违背用户「不占 OSS 对象」的诉求。否决。

2. **阈值 3 KB 写成常量 `INLINE_MAX_BYTES = 3 * 1024`，前后端各一份**，不做配置项（用户拍板值，无环境差异）。前端用 `file.size` 判断，后端用解码字节数；两者对同一文件相等。

3. **白名单以文案「仅支持 png/jpeg/webp/gif 图片」统一**，前端 `ossUpload.ts` 抛错文案与后端 400 文案一致，`RichTextEditor` 拦截提示复用同一文案。

4. **粘贴/拖入拦截沿用已落地的 root 事件监听**（本 change 前已在 `RichTextEditor` 加了 paste/drop 监听走 `insertImage`）。本 change 在其上补两条分支：非白名单文件 → `toast.error` 并 `preventDefault`（不落入 Quill 默认剪贴板）；≤ 3 KB → 只插 data URL、不上传、不注册 keyMap。`insertImage` 内部按 `file.size` 分流即可，工具栏按钮选图同样受益。

5. **StubObjectKeyValidator 与 Aliyun 实现同步扩展正则**，保证 test profile 的 IT 能覆盖 gif objectKey。

6. **contracts/api-spec.json** 仅改上传凭证接口 summary/描述与 400 文案；富文本字段类型不变，无 schema 变化。

## Risks / Trade-offs

- [data URL 使正则改写的 `[^"]+` 捕获超长串] → 3 KB 上限使 base64 约 4 KB，正则线性扫描无回溯风险。
- [正文塞大量小图导致 HTML 膨胀] → 每张 ≤ 4 KB，实际表情包用量下可忽略；若失控，后续在 Service 层加总量上限即可。
- [gif 动图在 OSS 侧 Content-Type 为 image/gif] → 直传凭证按 contentType 锁定，headObject 校验 MIME 白名单同步扩展，无额外风险。

## Migration Plan

无 schema 变更；admin、app 可各自独立部署。顺序建议：先 admin（放行 data URL 与 gif），再 web（否则前端内联小图会被旧后端拒绝），app 任意时机（旧 app 对 data URL 会错误签名，仅影响含内联小图的新数据展示）。回滚：直接回退对应端代码。

## Open Questions

无。已定决策：不加 svg（安全取舍）；内联阈值 3 KB。
