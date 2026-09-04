## MODIFIED Requirements

### Requirement: 图片上传凭证签发
admin 端 SHALL 提供 `POST /api/admin/files/upload-credentials`，为浏览器直传对象存储签发一次性凭证。任何已登录账号均可调用（不限 ADMIN 角色），并记录审计日志 `file:upload-credentials`。

请求体为 `{contentType}`，仅接受 `image/png`、`image/jpeg`、`image/webp`、`image/gif`，其余返回 400「仅支持 png/jpeg/webp/gif 图片」。

响应 SHALL 含直传所需的全部字段：上传目标地址、预分配的 `objectKey`、签名策略、签名值、签名算法标识、凭证标识、签名时间、安全令牌与过期时间。

服务端 SHALL 按 contentType 预分配 objectKey，形如 `images/<UUIDv7>.<ext>`（`image/jpeg` 映射为 `jpg`，`image/gif` 映射为 `gif`）。签发的临时凭证 SHALL 将权限收敛到**该单一 objectKey 的写入**，签名策略 SHALL 锁定 key 与文件大小上限，使客户端无法上传到其他路径或超限文件。签名有效期 SHALL NOT 超过临时凭证本身的有效期。

后端 SHALL NOT 代理上传字节流——文件由浏览器直传对象存储，服务端不接触文件内容，也不向浏览器暴露长期密钥。

#### Scenario: 签发合法图片类型的上传凭证
- **GIVEN** 以已登录身份提交 `contentType=image/png`
- **WHEN** 请求上传凭证
- **THEN** 返回 200，`objectKey` 形如 `images/<uuid>.png`，签名与安全令牌字段非空

#### Scenario: 非图片类型被拒绝
- **GIVEN** 以已登录身份提交 `contentType=application/pdf`
- **WHEN** 请求上传凭证
- **THEN** 返回 400 及消息「仅支持 png/jpeg/webp/gif 图片」

#### Scenario: 未登录无法获取凭证
- **GIVEN** 请求未携带有效身份凭证
- **WHEN** 请求上传凭证
- **THEN** 返回 401

#### Scenario: 签发 gif 类型的上传凭证
- **GIVEN** 以已登录身份提交 `contentType=image/gif`
- **WHEN** 请求上传凭证
- **THEN** 返回 200，`objectKey` 形如 `images/<uuid>.gif`

### Requirement: objectKey 两段式生命周期与绑定校验
系统 SHALL 以两段前缀区分图片状态：`images/` 为直传落地的未绑定对象，`bound/` 为已绑定业务的归档对象。**数据库中持久化的图片一律为 `bound/` 前缀**。

所有携带图片的业务写接口在持久化前 SHALL 对每个图片字段执行绑定校验，其契约为：

1. 空值拒绝
2. 格式校验——须匹配 `images/` 或 `bound/` 前缀、UUID 式文件名、`png`/`jpg`/`webp`/`gif` 后缀；不匹配则拒绝（借此拦截路径穿越与非白名单后缀）
3. 存在性校验——对象须在存储中真实存在
4. 类型校验——对象实际 MIME 须在图片白名单内
5. 大小校验——不得超过单图上限（默认 20 MiB）
6. 已是 `bound/` 前缀的对象直接返回，不再复制
7. 否则将对象复制到 `bound/` 并返回新 key

上述任一校验失败 SHALL 返回 400，且消息**统一为「图片对象不可用」**——不区分「不存在」「类型不符」「超限」，具体原因仅记录日志。

绑定 SHALL 只复制、**从不删除**源对象。目标 key 由源 key 唯一决定，因此复制幂等；调用方事务回滚后源对象仍在，同一 objectKey 可安全重试。未被任何业务绑定的 `images/` 对象由存储侧生命周期规则回收，应用不主动清理。

富文本内容中的图片 SHALL 同样经过绑定校验：保存时将正文内的图片地址归一为 objectKey 后逐个绑定，读取时再逐个换回签名地址。

富文本内的 `<img src>` 若为 data URL，SHALL 按「内联小图」规则处理而不走 objectKey 绑定：MIME 须在图片白名单内（`image/png|jpeg|webp|gif`）且 base64 解码后字节数 **≤ 3 KB（3072 字节）**，满足则原样保留在 HTML 中；不满足则返回 400「图片对象不可用」。admin 与 app 读取富文本时 SHALL 对 data URL 原样透传，不做签名替换。

#### Scenario: 未绑定图片在业务保存时被绑定
- **GIVEN** 已直传一张图片得到 `images/<uuid>.png`
- **WHEN** 以该 objectKey 提交任一带图业务的创建请求
- **THEN** 保存成功，落库与响应中的 objectKey 为 `bound/<uuid>.png`

#### Scenario: 已绑定图片重复提交不再复制
- **GIVEN** 一条已保存的业务数据，其图片为 `bound/<uuid>.png`
- **WHEN** 编辑该数据并原样回传该 objectKey
- **THEN** 保存成功，objectKey 保持不变

#### Scenario: 非法 objectKey 格式被拒绝
- **GIVEN** 以已登录身份提交任一带图业务的写请求
- **WHEN** 图片字段传入 `other/abc.png` 或 `images/abc.exe` 或含路径穿越的值
- **THEN** 返回 400 及中文业务错误

#### Scenario: 业务保存失败后源图仍可重试
- **GIVEN** 一个带图业务请求，其后续校验会失败导致事务回滚
- **WHEN** 修正数据后以**同一** objectKey 重新提交
- **THEN** 第二次提交成功，图片正常绑定

#### Scenario: 富文本内联小图放行
- **GIVEN** 富文本 HTML 含一个 `<img src="data:image/gif;base64,...">`，解码后 2 KB
- **WHEN** 提交该业务的写请求
- **THEN** 保存成功，落库与读取响应中该 img src 与提交时一致，未被替换为签名地址

#### Scenario: 富文本内联图超限被拒绝
- **GIVEN** 富文本 HTML 含一个 data URL 图片，解码后 4 KB
- **WHEN** 提交该业务的写请求
- **THEN** 返回 400「图片对象不可用」

#### Scenario: 富文本内联图类型不符被拒绝
- **GIVEN** 富文本 HTML 含 `<img src="data:image/svg+xml;base64,...">`，解码后 1 KB
- **WHEN** 提交该业务的写请求
- **THEN** 返回 400「图片对象不可用」

### Requirement: 图片上传的界面交互
web 端 SHALL 提供单图与多图两种上传控件，复用于全部带图业务表单（城市背景图、商户 LOGO 与图片、Banner 图片、路线缩略图/图片/地点图、活动图片、文章封面、大使头像、精选推荐 banner、文章栏目图标）。

单图控件 SHALL 呈现三种状态：未选择（虚线占位格，可点击或拖拽放置）、上传中（本地预览 + 百分比进度）、已上传（缩略图 + 悬停浮层，浮层含预览、替换、删除三个操作）。预览 SHALL 打开全屏遮罩，点击任意处关闭。

多图控件 SHALL 在末尾恒置「添加图片」格，支持一次多选并发上传，每个上传中的文件占一个独立进度格；已上传格的悬停浮层含预览与删除两个操作。多图控件 SHALL NOT 提供拖拽放置与拖拽排序——图片顺序即上传顺序。

上传失败 SHALL 以全局提示告知且不阻塞表单，用户重新选择文件即为重试，控件不自动重试。多图部分失败时，失败项丢弃并提示，成功项照常加入。

界面 SHALL 在文件选择阶段即按图片类型白名单（png/jpeg/webp/gif）过滤；**大小超限不在前端预检**，仅在直传阶段由存储侧拒绝后以上传失败提示暴露。

富文本编辑器 SHALL 拦截粘贴（Ctrl+V）与拖入的图片文件，不交给编辑器默认剪贴板处理：白名单内且 ≤ 3 KB 的文件直接以 data URL 内联插入；白名单内且 > 3 KB 的文件走与工具栏插图相同的 OSS 直传链路；非白名单类型 SHALL NOT 插入编辑器，并以全局提示「仅支持 png/jpeg/webp/gif 图片」告知。粘贴内容不含图片文件时 SHALL 保持编辑器默认行为。

图片数量 SHALL 由各业务自行约束下限（如「至少一张」），系统**不设统一数量上限**。

#### Scenario: 单图控件三态切换
- **GIVEN** 打开任一含单图字段的业务表单
- **WHEN** 点击占位格选择一张合法图片
- **THEN** 依次呈现上传中进度与已上传缩略图，悬停缩略图出现预览、替换、删除三个操作

#### Scenario: 多图并发上传
- **GIVEN** 打开任一含多图字段的业务表单
- **WHEN** 一次选择 3 张合法图片
- **THEN** 出现 3 个独立进度格，全部完成后按选择顺序排列

#### Scenario: 非图片类型在选择阶段被拦
- **GIVEN** 上传控件已就绪
- **WHEN** 选择一个非图片文件
- **THEN** 弹出类型不支持的提示，不发起上传请求

#### Scenario: 上传失败不阻塞表单
- **GIVEN** 多图控件中一张图片上传失败
- **WHEN** 查看表单状态
- **THEN** 失败项不加入列表并弹出错误提示，其余成功项保留，表单仍可继续编辑与提交

#### Scenario: 富文本粘贴大图走 OSS 上传
- **GIVEN** 富文本编辑器已就绪
- **WHEN** Ctrl+V 粘贴一张 > 3 KB 的 png 图片并保存
- **THEN** 编辑器立即预览该图，保存成功，重新打开表单该图以签名地址回显

#### Scenario: 富文本粘贴小表情内联
- **GIVEN** 富文本编辑器已就绪
- **WHEN** Ctrl+V 粘贴一张 ≤ 3 KB 的 gif 并保存
- **THEN** 不发起上传凭证请求，保存成功，重新打开表单该图以 data URL 回显

#### Scenario: 富文本粘贴非白名单类型被拦
- **GIVEN** 富文本编辑器已就绪
- **WHEN** Ctrl+V 粘贴一个 svg 文件
- **THEN** 弹出「仅支持 png/jpeg/webp/gif 图片」提示，编辑器内容不变，不发起上传请求
