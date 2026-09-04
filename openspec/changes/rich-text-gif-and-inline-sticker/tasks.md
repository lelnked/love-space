## 1. admin 后端

- [x] 1.1 `UploadCredentialRequest` contentType 正则加 `gif`，文案改「仅支持 png/jpeg/webp/gif 图片」；`FileService` MIME→ext 映射加 `image/gif → gif`
- [x] 1.2 `AliyunOssObjectKeyValidator` / `StubObjectKeyValidator` key 正则后缀加 `gif`，`ALLOWED_CONTENT_TYPES` 加 `image/gif`；补 UT（gif key 通过、svg 仍拒）
- [x] 1.3 `RichTextImages` 新增 `INLINE_MAX_BYTES=3072` 与 `isInlineImage(src)`；`rewriteSrc` 对内联小图跳过 fn 原样输出，对不合规 `data:` src 抛「图片对象不可用」；`RichTextImagesTest` 补 ≤3KB 放行 / 4KB 拒绝 / svg 拒绝 / 读取透传四条 UT（带 `@scenario`）
- [x] 1.4 `mvn test` 绿；`contracts/api-spec.json` 上传凭证接口 summary 与 400 文案同步

## 2. app 后端

- [x] 2.1 `RichTextImages.rewriteSrc` 对 `data:` 前缀 src 原样透传不调用 fn；补 UT
- [x] 2.2 `mvn test` 绿

## 3. web 前端

- [x] 3.1 白名单加 `image/gif`：`ossUpload.ts`（含错误文案）、`ImageUploader.tsx`、`ImageUploaderList.tsx`、`types/image.ts` 注释、`RichTextEditor.tsx` ACCEPT
- [x] 3.2 `RichTextEditor` 粘贴/拖入：非白名单文件 preventDefault + toast「仅支持 png/jpeg/webp/gif 图片」，不插入；`insertImage` 按 `file.size ≤ 3072` 分流——内联仅插 data URL 不上传不注册 keyMap
- [x] 3.3 `tsc --noEmit` 与 eslint 绿

## 4. 交付验证

- [x] 4.1 `/run-api-test --change rich-text-gif-and-inline-sticker`
- [ ] 4.2 `/run-web-test --change rich-text-gif-and-inline-sticker`（活动表单粘贴三场景）——2026-09-04 环境阻塞：playwright MCP 连接超时，待恢复后执行
- [x] 4.3 `node scripts/generate-traceability-matrix.js --change rich-text-gif-and-inline-sticker`；`openspec validate`
