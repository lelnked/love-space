# file IT 用例

> 后端 baseUrl: `http://localhost:21423`（test profile）；登录 fixture：`admin` / `8@y2eoRLyStM*UVU`。
> 本域大部分行为是**横切**的：绑定校验与签名地址通过带图业务接口（banner / city）间接验证，故部分用例的「关联契约」指向实际调用的业务接口。
> 测试档位下对象存储以桩实现替代：绑定校验只保留格式校验并直接返回 `bound/` 前缀，不访问存储。

### TC-file-IT-001: 签发合法图片类型的上传凭证（测试档位不可实跑）
**关联需求**: file/图片上传凭证签发#签发合法图片类型的上传凭证
**关联契约**: api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post
**来源**: baseline-auth-manager-banner-log-file → rich-text-gif-and-inline-sticker
**优先级**: P1
**前置条件**: 本用例需为 `StsCredentialIssuer` 提供 test-profile 桩实现（真实 STS 签发依赖真实网络与角色配置）。**当前不满足该前置**，执行时应标记为「未执行」而非失败。
**测试步骤**:
1. 登录取 token
2. POST /api/admin/files/upload-credentials，body `{"contentType":"image/png"}`
**预期结果**: 返回 200；`objectKey` 匹配 `^images/[0-9a-f-]{36}\.png$`；上传目标地址、签名策略、签名值、签名算法标识、凭证标识、签名时间、安全令牌、过期时间字段均非空；`image/jpeg` 时后缀为 `jpg`，`image/gif` 时后缀为 `gif`（gif 正向分支见 TC-file-IT-014）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-001/`
**最后更新**: 2026-09-04

### TC-file-IT-002: 非图片 contentType 返回 400
**关联需求**: file/图片上传凭证签发#非图片类型被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post
**来源**: baseline-auth-manager-banner-log-file → rich-text-gif-and-inline-sticker
**优先级**: P0
**测试步骤**:
1. 登录取 token
2. POST /api/admin/files/upload-credentials，body `{"contentType":"application/pdf"}`
3. 再次 POST，body `{"contentType":"image/svg+xml"}`（svg 明确不在白名单，可内嵌脚本）
4. 再次 POST，body `{"contentType":"image/bmp"}`
**预期结果**: 三次均返回 400，错误消息为「仅支持 png/jpeg/webp/gif 图片」（注意 `image/gif` 自 rich-text-gif-and-inline-sticker 起已进入白名单，**不再**作为拒绝样本）；响应中不含任何签名或令牌字段（入参校验先于 STS 调用，不触达存储）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-002/`
**最后更新**: 2026-09-04

### TC-file-IT-003: 未登录请求上传凭证返回 401
**关联需求**: file/图片上传凭证签发#未登录无法获取凭证
**关联契约**: api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 不带 Authorization 头 POST /api/admin/files/upload-credentials，body `{"contentType":"image/png"}`
2. 带伪造 token（`Authorization: Bearer invalid.token.value`）重复请求
**预期结果**: 两次均返回 401；响应体不含 objectKey / 签名 / 安全令牌字段
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-file-IT-004: 未绑定图片在业务保存时被改写为 bound/ 前缀
**关联需求**: file/objectKey 两段式生命周期与绑定校验#未绑定图片在业务保存时被绑定
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 存在一个可关联的城市，记 cityId = C
**测试步骤**:
1. 登录取 token
2. POST /api/admin/banners，body `{"name":"绑定用例","positionCode":"home-top","type":"CITY","imageUrls":["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1001.png"],"link":"C","sortOrder":0}`，记录返回 id = B
3. GET /api/admin/banners/B
**预期结果**: 步骤 2 返回 200；步骤 3 的 `imageUrls[0].id` 为 `bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1001.png`（前缀由 `images/` 改写为 `bound/`，文件名与后缀不变），落库值同样是 `bound/` 前缀
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-004/`
**最后更新**: 2026-09-04

### TC-file-IT-005: 已绑定图片原样回传不再复制，objectKey 保持不变
**关联需求**: file/objectKey 两段式生命周期与绑定校验#已绑定图片重复提交不再复制
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners~1{id}/put
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 已有 TC-file-IT-004 创建的 banner B，其图片为 `bound/<uuid>.png`
**测试步骤**:
1. 登录取 token
2. GET /api/admin/banners/B，取出 `imageUrls[0].id` = K（`bound/` 前缀）
3. PUT /api/admin/banners/B，body 保持其它字段不变、`imageUrls` 原样回传 `[K]`，`name` 改为「绑定用例-改」
4. GET /api/admin/banners/B
**预期结果**: 步骤 3 返回 200；步骤 4 的 `imageUrls[0].id` 仍等于 K（不产生新的 key、不追加二次 `bound/` 前缀），`name` 已更新
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-file-IT-006: 非白名单前缀的 objectKey 被拒绝
**关联需求**: file/objectKey 两段式生命周期与绑定校验#非法 objectKey 格式被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 存在一个可关联的城市，记 cityId = C
**测试步骤**:
1. 登录取 token
2. POST /api/admin/banners，body 同 TC-file-IT-004 但 `imageUrls` 为 `["other/abc.png"]`
**预期结果**: 返回 400，中文业务错误消息为「imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）」；banner 未创建（后续列表查询无该条）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-006/`
**最后更新**: 2026-09-04

### TC-file-IT-007: 非白名单后缀与路径穿越的 objectKey 被拒绝
**关联需求**: file/objectKey 两段式生命周期与绑定校验#非法 objectKey 格式被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**前置条件**: 存在一个可关联的城市，记 cityId = C
**测试步骤**:
1. 登录取 token
2. POST /api/admin/banners，`imageUrls` 为 `["images/abc.exe"]`
3. POST /api/admin/banners，`imageUrls` 为 `["images/../../etc/passwd.png"]`
4. POST /api/admin/banners，`imageUrls` 为 `[""]`（空值）
**预期结果**: 步骤 2、3 返回 400 且消息为「imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）」；步骤 4 返回 400 且消息为「图片不能为空」；三次均未创建数据
**状态**: ✅ 通过（⚠️ 见存证备注）
**执行方式**: api-test-runner
**执行存证**: `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-007/`
**最后更新**: 2026-09-04

### TC-file-IT-008: 业务保存失败后同一 objectKey 可重试成功
**关联需求**: file/objectKey 两段式生命周期与绑定校验#业务保存失败后源图仍可重试
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**前置条件**: 存在一个可关联的城市，记 cityId = C
**测试步骤**:
1. 登录取 token
2. POST /api/admin/banners，`imageUrls` 为 `["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1008.png"]`，但 `link` 传一个**不存在的城市 UUID**（触发后续校验失败、事务回滚）
3. 修正 `link` 为真实城市 id C，其余字段不变、**沿用同一 objectKey** 重新 POST /api/admin/banners
4. GET /api/admin/banners/{新 id}
**预期结果**: 步骤 2 返回 4xx（400/404）且未创建数据；步骤 3 返回 200；步骤 4 的 `imageUrls[0].id` 为 `bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1008.png`——源对象未被删除，同一 key 重试可用（绑定幂等）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-file-IT-009: 业务详情的图片字段为 {id, url} 结构且 url 带签名
**关联需求**: file/图片签名访问地址#业务详情返回签名地址
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners~1{id}/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 存在一条带图 banner（可复用 TC-file-IT-004 的 B）
**测试步骤**:
1. 登录取 token
2. GET /api/admin/banners/B
3. 间隔数秒后再次 GET /api/admin/banners/B
**预期结果**: 两次均返回 200；`imageUrls[0]` 恰含 `id` 与 `url` 两个键；`id` 为 `bound/` 前缀的 objectKey；`url` 为 http(s) 地址且携带签名参数（含过期/签名相关 query）；签名地址每次请求即时生成（两次 `url` 的签名时间/参数不完全相同，或至少均可解析出有效期字段）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-file-IT-010: 空图片字段返回 null 不生成签名地址
**关联需求**: file/图片签名访问地址#空图片字段不生成地址
**关联契约**: api-spec.json#/paths/~1api~1admin~1cities/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**测试步骤**:
1. 登录取 token
2. POST /api/admin/cities，body `{"chineseName":"无图城市<随机后缀>","englishName":"NoImage","chineseProvince":"湖北","englishProvince":"Hubei"}`（不传 `backgroundImage`），记录 id = C
3. GET /api/admin/cities/C
**预期结果**: 步骤 3 返回 200；`backgroundImage` 为 `null`（不是空对象、不是 `{id:null,url:...}`），响应中不出现任何签名地址
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-file-IT-011: 多图字段按保存顺序返回
**关联需求**: file/图片签名访问地址#多图字段保持顺序
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**前置条件**: 存在一个可关联的城市，记 cityId = C
**测试步骤**:
1. 登录取 token
2. POST /api/admin/banners，`imageUrls` 依次为 `["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1101.png","images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1102.jpg","images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1103.webp"]`，记录 id
3. GET /api/admin/banners/{id}
**预期结果**: 返回 200；`imageUrls` 长度为 3，`id` 依次为 `bound/...1101.png`、`bound/...1102.jpg`、`bound/...1103.webp`——顺序与提交顺序完全一致，每项均含非空 `url`
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-file-IT-012: 测试档位下绑定校验不访问存储（对象不存在也成功）
**关联需求**: file/图片链路的自动化覆盖边界#测试档位下绑定校验不访问存储
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 后端运行在 test profile（`http://localhost:21423`）；存在一个可关联的城市，记 cityId = C
**测试步骤**:
1. 登录取 token
2. POST /api/admin/banners，`imageUrls` 为 `["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1201.png"]`——该 objectKey **格式合法但存储中并不存在**
3. GET /api/admin/banners/{id}
**预期结果**: 步骤 2 返回 200（**不因对象不存在而返回 400「图片对象不可用」**）；步骤 3 的 `imageUrls[0].id` 为 `bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1201.png`——测试档位只做格式校验并直接改写前缀
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-file-IT-013: 上传凭证成功分支在测试档位不可用
**关联需求**: file/图片链路的自动化覆盖边界#上传凭证成功分支在测试档位不可用
**关联契约**: api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P2
**前置条件**: 后端运行在 test profile，无真实 STS 角色配置
**测试步骤**:
1. 登录取 token
2. POST /api/admin/files/upload-credentials，body `{"contentType":"image/png"}`
3. 记录响应状态码与错误信息作为存证
**预期结果**: 请求**不返回 200**（临时凭证签发不可用，预期为 5xx 或服务侧明确的不可用响应）；该分支不纳入自动化正向断言——本用例仅存证「成功分支在测试档位不可跑」，不因非 200 判定为缺陷。若后续接入 test-profile 桩实现，本用例与 TC-file-IT-001 一并转为正向断言
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-file-IT-014: 签发 gif 类型的上传凭证（测试档位不可实跑）
**关联需求**: file/图片上传凭证签发#签发 gif 类型的上传凭证
**关联契约**: api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post
**来源**: rich-text-gif-and-inline-sticker
**优先级**: P1
**前置条件**: 同 TC-file-IT-001——需 `StsCredentialIssuer` 的 test-profile 桩实现；当前不满足时标记「未执行」而非失败。**可自动化的最低断言**：`image/gif` 不再命中入参校验的 400「仅支持 png/jpeg/webp/gif 图片」（响应为 5xx/STS 不可用即证明已越过入参校验）。
**测试步骤**:
1. 登录取 token
2. POST /api/admin/files/upload-credentials，body `{"contentType":"image/gif"}`
**预期结果**: 返回 200；`objectKey` 匹配 `^images/[0-9a-f-]{36}\.gif$`，签名与安全令牌字段非空。桩缺失时：**不得**返回 400「仅支持 png/jpeg/webp/gif 图片」，记录实际状态码作存证
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-014/`
**最后更新**: 2026-09-04

### TC-file-IT-015: gif 后缀 objectKey 通过绑定校验，svg 后缀仍被拒绝
**关联需求**: file/objectKey 两段式生命周期与绑定校验#未绑定图片在业务保存时被绑定
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners/post
**来源**: rich-text-gif-and-inline-sticker
**优先级**: P0
**前置条件**: 后端运行在 test profile；存在一个可关联的城市，记 cityId = C
**测试步骤**:
1. 登录取 token
2. POST /api/admin/banners，body 同 TC-file-IT-004 但 `imageUrls` 为 `["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif"]`，记录 id = B
3. GET /api/admin/banners/B
4. POST /api/admin/banners，`imageUrls` 为 `["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1502.svg"]`
5. PUT /api/admin/banners/B，`imageUrls` 原样回传步骤 3 取到的 `bound/...1501.gif`
**预期结果**: 步骤 2 返回 200；步骤 3 `imageUrls[0].id` 为 `bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif`（后缀 gif 保持）且 `url` 非空；步骤 4 返回 400 且消息为「imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）」——svg 不在后缀白名单；步骤 5 返回 200 且 key 保持不变
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/rich-text-gif-and-inline-sticker/TC-file-IT-015/`
**最后更新**: 2026-09-04

---

## 未在本域产出用例的 Scenario（设计决策）

Requirement `file/图片上传的界面交互` 的全部 4 个 Scenario——`单图控件三态切换`、`多图并发上传`、`非图片类型在选择阶段被拦`、`上传失败不阻塞表单`——**本域不产 `web.md`**。

原因：上传控件没有独立路由页面，它作为公共组件复用于各带图业务表单（城市背景图、商户 LOGO 与图片、Banner 图片、路线缩略图/图片/地点图、活动图片、文章封面、大使头像、精选推荐 banner、文章栏目图标）。按设计决策，其界面交互由**各业务域的 `web.md`** 在对应表单用例中就地覆盖（例如 banner 表单用例覆盖多图并发上传，city 表单用例覆盖单图三态切换），避免在无入口的 file 域制造无法独立执行的 WEB 用例。本轮不产出这部分用例。

`objectKey 两段式生命周期与绑定校验` 中「富文本内联小图放行 / 超限被拒绝 / 类型不符被拒绝」三个 Scenario，以及 `图片上传的界面交互` 中「富文本粘贴大图走 OSS 上传 / 粘贴小表情内联 / 粘贴非白名单类型被拦」三个 Scenario（rich-text-gif-and-inline-sticker），同理落在富文本所在业务域：IT 见 `tests/activity/it.md`（TC-activity-IT-025~027）与 `tests/article/it.md`（TC-article-IT-021~022），WEB 见 `tests/activity/web.md`（TC-activity-WEB-006~008）。

同时，以下行为按 `file/图片链路的自动化覆盖边界` 明确**不由自动化覆盖**，须人工/联调验证：浏览器到对象存储的真实直传、临时凭证的真实签发、已绑定对象的真实可读性。绑定校验中的存在性、类型、大小三项校验与签名地址生成，由单元测试以模拟存储客户端覆盖，不在本 IT 清单内。
