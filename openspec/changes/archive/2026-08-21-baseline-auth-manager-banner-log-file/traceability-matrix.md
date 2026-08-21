# 追溯矩阵（交付核对）：baseline-auth-manager-banner-log-file

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change baseline-auth-manager-banner-log-file`

## 需求与场景
- **auth/JWT 会话与授权链**: 无 token 访问受保护接口 / 非法 token 不影响免认证路径 / 角色不足返回 403
- **auth/web 端登录页与路由守卫**: 登录成功进入地图管理 / 两字段任一为空时无法提交 / 未登录访问后台被拦回登录页 / 非 ADMIN 角色看不到管理员管理入口
- **auth/当前登录人查询与登出**: 查询当前登录人 / 登出返回 204 且 token 仍然有效
- **auth/运营账号登录**: 内置管理员登录成功 / 密码错误被拒绝 / 停用账号无法登录 / 用户名不存在与密码错误不可区分
- **banner/App 端 Banner 查询**: 按展示位查询上架 Banner / 下架 Banner 不下发 / 关联城市下架时条目被剔除 / 缺少 API-key 返回 401
- **banner/Banner 上架前置校验**: 关联城市下架时无法上架 Banner / 关联城市上架时可正常上架 / 下架无前置条件
- **banner/Banner 管理**: 创建后默认下架 / 名称重复被拒绝 / 更新时携带上下架字段被拒绝 / 图片 objectKey 格式非法被拒绝
- **banner/web 端 Banner 管理页面**: 列表展示与状态徽标 / 上下架乐观更新失败回滚 / 删除需确认 / 表单城市下拉只列上架城市
- **banner/城市状态变更对 Banner 级联生效**: 城市下架连带 Banner 下架 / 城市重新上架连带 Banner 上架 / 删除城市只下架不删除 Banner
- **file/objectKey 两段式生命周期与绑定校验**: 未绑定图片在业务保存时被绑定 / 已绑定图片重复提交不再复制 / 非法 objectKey 格式被拒绝 / 业务保存失败后源图仍可重试
- **file/图片上传凭证签发**: 签发合法图片类型的上传凭证 / 非图片类型被拒绝 / 未登录无法获取凭证
- **file/图片上传的界面交互**: 单图控件三态切换 / 多图并发上传 / 非图片类型在选择阶段被拦 / 上传失败不阻塞表单
- **file/图片签名访问地址**: 业务详情返回签名地址 / 空图片字段不生成地址 / 多图字段保持顺序
- **file/图片链路的自动化覆盖边界**: 测试档位下绑定校验不访问存储 / 上传凭证成功分支在测试档位不可用
- **manager/web 端管理员管理页面**: 列表按角色与状态渲染 / 内置 admin 行不显示启停按钮 / 弹窗创建新账号 / 密码不足 8 位前端拦截
- **manager/账号启停与内置管理员保护**: 停用后无法登录 / 内置 admin 不可停用 / 启停可往复切换
- **manager/运营账号分页查询**: 按用户名模糊过滤 / 页大小非白名单值被校正 / 列表按创建时间倒序 / 查询不存在的账号返回 400
- **manager/运营账号管理**: 创建账号强制为 MEMBER 角色 / 用户名重复被拒绝 / 密码长度不足被拒绝 / 重置密码后旧密码失效
- **operation-log/web 端操作日志页面**: 按操作人筛选日志 / 模块与动作按中文展示 / 未映射的动作回落显示原值 / 对象为空的记录显示占位符
- **operation-log/操作日志查询**: 按操作人与模块组合过滤 / 操作人过滤为模糊匹配 / 时间区间含边界 / 响应不含 payload / 非 ADMIN 角色可查询日志
- **operation-log/留痕字段取值与敏感信息脱敏**: 密码字段被脱敏 / 创建类操作的 target 为空 / 更新类操作记录目标 id / 嵌套资源的 target 取父级 id
- **operation-log/运营写操作留痕**: 创建城市后异步留痕 / 业务方法失败时不留痕 / 登录不产生日志

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-auth-IT-001 | POST /api/admin/auth/login 内置管理员登录成功 | auth/运营账号登录#内置管理员登录成功 | api-spec.json#/paths/~1api~1admin~1auth~1login/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-002 | POST /api/admin/auth/login 密码错误返回 401 | auth/运营账号登录#密码错误被拒绝 | api-spec.json#/paths/~1api~1admin~1auth~1login/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-003 | 停用账号以正确密码登录仍返回 401 | auth/运营账号登录#停用账号无法登录 | api-spec.json#/paths/~1api~1admin~1auth~1login/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-004 | 用户名不存在与密码错误、账号停用消息不可区分 | auth/运营账号登录#用户名不存在与密码错误不可区分 | api-spec.json#/paths/~1api~1admin~1auth~1login/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-005 | 无 token 访问受保护接口返回 401 | auth/JWT 会话与授权链#无 token 访问受保护接口 | api-spec.json#/paths/~1api~1admin~1auth~1me/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-006 | 非法 token 不影响登录接口 | auth/JWT 会话与授权链#非法 token 不影响免认证路径 | api-spec.json#/paths/~1api~1admin~1auth~1login/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-007 | MEMBER 角色访问管理员接口返回 403 | auth/JWT 会话与授权链#角色不足返回 403 | api-spec.json#/paths/~1api~1admin~1managers~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-008 | GET /api/admin/auth/me 返回当前登录人 | auth/当前登录人查询与登出#查询当前登录人 | api-spec.json#/paths/~1api~1admin~1auth~1me/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-009 | 登出返回 204 且同一 token 仍可用 | auth/当前登录人查询与登出#登出返回 204 且 token 仍然有效 | api-spec.json#/paths/~1api~1admin~1auth~1logout/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-WEB-001 | 登录成功跳转地图管理页 | auth/web 端登录页与路由守卫#登录成功进入地图管理 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-auth-WEB-002 | 两字段任一为空时登录按钮禁用 | auth/web 端登录页与路由守卫#两字段任一为空时无法提交 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-auth-WEB-003 | 未登录直接访问后台页面被重定向到登录页 | auth/web 端登录页与路由守卫#未登录访问后台被拦回登录页 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-auth-WEB-004 | MEMBER 角色侧栏无「管理员管理」入口 | auth/web 端登录页与路由守卫#非 ADMIN 角色看不到管理员管理入口 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-banner-IT-001 | POST /api/admin/banners 创建成功且默认下架 | banner/Banner 管理#创建后默认下架 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-002 | POST /api/admin/banners sortOrder=0 边界可创建 | banner/Banner 管理#创建后默认下架 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-003 | POST /api/admin/banners 名称重复返回 400 | banner/Banner 管理#名称重复被拒绝 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-004 | PUT /api/admin/banners/{id} 携带 online 字段返回 400 | banner/Banner 管理#更新时携带上下架字段被拒绝 | api-spec.json#/paths/~1api~1admin~1banners~1{id}/put | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-005 | POST /api/admin/banners 非法 objectKey 返回 400 | banner/Banner 管理#图片 objectKey 格式非法被拒绝 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-006 | POST /api/admin/banners/{id}/online 关联城市已下架时上架被拒 | banner/Banner 上架前置校验#关联城市下架时无法上架 Banner | api-spec.json#/paths/~1api~1admin~1banners~1{id}~1online/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-007 | POST /api/admin/banners/{id}/online 关联城市上架时可上架 | banner/Banner 上架前置校验#关联城市上架时可正常上架 | api-spec.json#/paths/~1api~1admin~1banners~1{id}~1online/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-008 | POST /api/admin/banners/{id}/online 下架无前置条件 | banner/Banner 上架前置校验#下架无前置条件 | api-spec.json#/paths/~1api~1admin~1banners~1{id}~1online/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-009 | 城市下架级联使关联 Banner 下架 | banner/城市状态变更对 Banner 级联生效#城市下架连带 Banner 下架 | api-spec.json#/paths/~1api~1admin~1cities~1{id}~1online/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-010 | 城市重新上架级联恢复关联 Banner（含手动下架的） | banner/城市状态变更对 Banner 级联生效#城市重新上架连带 Banner 上架 | api-spec.json#/paths/~1api~1admin~1cities~1{id}~1online/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-011 | 删除城市只下架不删除关联 Banner | banner/城市状态变更对 Banner 级联生效#删除城市只下架不删除 Banner | api-spec.json#/paths/~1api~1admin~1cities~1{id}/delete | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-012 | GET /api/app/banners 按展示位返回上架 Banner 并按排序号升序 | banner/App 端 Banner 查询#按展示位查询上架 Banner | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-013 | GET /api/app/banners 排序号并列时按创建时间升序 | banner/App 端 Banner 查询#按展示位查询上架 Banner | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-014 | GET /api/app/banners 下架 Banner 不下发 | banner/App 端 Banner 查询#下架 Banner 不下发 | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-015 | GET /api/app/banners 关联城市下架时条目被剔除 | banner/App 端 Banner 查询#关联城市下架时条目被剔除 | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-016 | GET /api/app/banners 缺少 API-key 返回 401 | banner/App 端 Banner 查询#缺少 API-key 返回 401 | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-IT-017 | DELETE /api/admin/banners/{id} 物理删除 Banner | banner/Banner 管理#创建后默认下架 | api-spec.json#/paths/~1api~1admin~1banners~1{id}/delete | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-WEB-001 | Banner 列表展示与上下架徽标 | banner/web 端 Banner 管理页面#列表展示与状态徽标 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-banner-WEB-002 | 上架乐观更新失败后回滚并提示 | banner/web 端 Banner 管理页面#上下架乐观更新失败回滚 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-banner-WEB-003 | 删除 Banner 需二次确认 | banner/web 端 Banner 管理页面#删除需确认 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-banner-WEB-004 | 表单关联城市下拉只列出已上架城市 | banner/web 端 Banner 管理页面#表单城市下拉只列上架城市 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-file-IT-001 | 签发合法图片类型的上传凭证（测试档位不可实跑） | file/图片上传凭证签发#签发合法图片类型的上传凭证 | api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-002 | 非图片 contentType 返回 400 | file/图片上传凭证签发#非图片类型被拒绝 | api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-003 | 未登录请求上传凭证返回 401 | file/图片上传凭证签发#未登录无法获取凭证 | api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-004 | 未绑定图片在业务保存时被改写为 bound/ 前缀 | file/objectKey 两段式生命周期与绑定校验#未绑定图片在业务保存时被绑定 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-005 | 已绑定图片原样回传不再复制，objectKey 保持不变 | file/objectKey 两段式生命周期与绑定校验#已绑定图片重复提交不再复制 | api-spec.json#/paths/~1api~1admin~1banners~1{id}/put | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-006 | 非白名单前缀的 objectKey 被拒绝 | file/objectKey 两段式生命周期与绑定校验#非法 objectKey 格式被拒绝 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-007 | 非白名单后缀与路径穿越的 objectKey 被拒绝 | file/objectKey 两段式生命周期与绑定校验#非法 objectKey 格式被拒绝 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-008 | 业务保存失败后同一 objectKey 可重试成功 | file/objectKey 两段式生命周期与绑定校验#业务保存失败后源图仍可重试 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-009 | 业务详情的图片字段为 {id, url} 结构且 url 带签名 | file/图片签名访问地址#业务详情返回签名地址 | api-spec.json#/paths/~1api~1admin~1banners~1{id}/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-010 | 空图片字段返回 null 不生成签名地址 | file/图片签名访问地址#空图片字段不生成地址 | api-spec.json#/paths/~1api~1admin~1cities/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-011 | 多图字段按保存顺序返回 | file/图片签名访问地址#多图字段保持顺序 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-012 | 测试档位下绑定校验不访问存储（对象不存在也成功） | file/图片链路的自动化覆盖边界#测试档位下绑定校验不访问存储 | api-spec.json#/paths/~1api~1admin~1banners/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-file-IT-013 | 上传凭证成功分支在测试档位不可用 | file/图片链路的自动化覆盖边界#上传凭证成功分支在测试档位不可用 | api-spec.json#/paths/~1api~1admin~1files~1upload-credentials/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-manager-IT-001 | POST /api/admin/managers 创建账号强制 MEMBER 角色 | manager/运营账号管理#创建账号强制为 MEMBER 角色 | api-spec.json#/paths/~1api~1admin~1managers/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-manager-IT-002 | 用户名重复创建返回 400 | manager/运营账号管理#用户名重复被拒绝 | api-spec.json#/paths/~1api~1admin~1managers/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-manager-IT-003 | 密码 7 位创建返回 400 | manager/运营账号管理#密码长度不足被拒绝 | api-spec.json#/paths/~1api~1admin~1managers/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-manager-IT-004 | 重置密码后旧密码失效、新密码可登录 | manager/运营账号管理#重置密码后旧密码失效 | api-spec.json#/paths/~1api~1admin~1managers~1{id}~1password/put | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-manager-IT-005 | 停用后该账号无法登录 | manager/账号启停与内置管理员保护#停用后无法登录 | api-spec.json#/paths/~1api~1admin~1managers~1{id}~1disable/put | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-manager-IT-006 | 内置 admin 不可停用但可重置密码 | manager/账号启停与内置管理员保护#内置 admin 不可停用 | api-spec.json#/paths/~1api~1admin~1managers~1{id}~1disable/put | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-manager-IT-007 | 启停往复切换最终为启用 | manager/账号启停与内置管理员保护#启停可往复切换 | api-spec.json#/paths/~1api~1admin~1managers~1{id}~1enable/put | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-manager-IT-008 | 按用户名模糊过滤 | manager/运营账号分页查询#按用户名模糊过滤 | api-spec.json#/paths/~1api~1admin~1managers~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-manager-IT-009 | 页大小非白名单值 size=25 被校正为 20 | manager/运营账号分页查询#页大小非白名单值被校正 | api-spec.json#/paths/~1api~1admin~1managers~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-manager-IT-010 | 列表按创建时间倒序 | manager/运营账号分页查询#列表按创建时间倒序 | api-spec.json#/paths/~1api~1admin~1managers~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-manager-IT-011 | 查询不存在的账号返回 400 | manager/运营账号分页查询#查询不存在的账号返回 400 | api-spec.json#/paths/~1api~1admin~1managers~1{id}/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-manager-WEB-001 | 管理员列表按角色与状态渲染 | manager/web 端管理员管理页面#列表按角色与状态渲染 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-manager-WEB-002 | 内置 admin 行不显示启停按钮 | manager/web 端管理员管理页面#内置 admin 行不显示启停按钮 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-manager-WEB-003 | 弹窗创建新账号 | manager/web 端管理员管理页面#弹窗创建新账号 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-manager-WEB-004 | 密码不足 8 位前端拦截且不发请求 | manager/web 端管理员管理页面#密码不足 8 位前端拦截 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-operation-log-IT-001 | 创建城市后异步产生 city:create 留痕 | operation-log/运营写操作留痕#创建城市后异步留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-002 | 业务校验失败（400）时不产生留痕 | operation-log/运营写操作留痕#业务方法失败时不留痕 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-003 | 登录不产生 auth:login 日志 | operation-log/运营写操作留痕#登录不产生日志 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-004 | 创建运营账号的 payload 中 password 被脱敏 | operation-log/留痕字段取值与敏感信息脱敏#密码字段被脱敏 | api-spec.json#/paths/~1api~1admin~1managers/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-005 | 创建类操作的 target 为 null | operation-log/留痕字段取值与敏感信息脱敏#创建类操作的 target 为空 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-006 | 更新类操作的 target 为目标城市 id | operation-log/留痕字段取值与敏感信息脱敏#更新类操作记录目标 id | api-spec.json#/paths/~1api~1admin~1cities~1{id}/put | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-007 | 嵌套资源（商户评价）的 target 取父级商户 id | operation-log/留痕字段取值与敏感信息脱敏#嵌套资源的 target 取父级 id | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-008 | 按操作人与模块组合过滤 | operation-log/操作日志查询#按操作人与模块组合过滤 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-009 | 操作人过滤为模糊匹配 | operation-log/操作日志查询#操作人过滤为模糊匹配 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-010 | username 传空白串视为不传 | operation-log/操作日志查询#操作人过滤为模糊匹配 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-011 | 创建时间区间过滤含上下边界 | operation-log/操作日志查询#时间区间含边界 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-012 | 响应条目不含 payload，仅六个字段 | operation-log/操作日志查询#响应不含 payload | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-013 | MEMBER 角色账号可查询日志（不返回 403） | operation-log/操作日志查询#非 ADMIN 角色可查询日志 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-IT-014 | size 非法值校正为 20 且固定按创建时间倒序 | operation-log/操作日志查询#按操作人与模块组合过滤 | api-spec.json#/paths/~1api~1admin~1logs~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-operation-log-WEB-001 | 按操作人筛选后列表仅剩该操作人且回到第 1 页 | operation-log/web 端操作日志页面#按操作人筛选日志 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-operation-log-WEB-002 | 重置筛选后恢复全量并回到第 1 页 | operation-log/web 端操作日志页面#按操作人筛选日志 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-operation-log-WEB-003 | 模块与动作按中文映射展示 | operation-log/web 端操作日志页面#模块与动作按中文展示 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-operation-log-WEB-004 | 未映射的模块/动作回落显示原始英文值 | operation-log/web 端操作日志页面#未映射的动作回落显示原值 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-operation-log-WEB-005 | 对象为空的创建类记录显示占位符 `-` | operation-log/web 端操作日志页面#对象为空的记录显示占位符 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |

## 覆盖核对

- ⚠ 未覆盖：auth/JWT 会话与授权链#非法 token 不影响免认证路径 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：auth/当前登录人查询与登出#查询当前登录人 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：auth/当前登录人查询与登出#登出返回 204 且 token 仍然有效 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：auth/运营账号登录#用户名不存在与密码错误不可区分 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：banner/App 端 Banner 查询#下架 Banner 不下发 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：banner/App 端 Banner 查询#关联城市下架时条目被剔除 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：banner/App 端 Banner 查询#缺少 API-key 返回 401 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：banner/Banner 上架前置校验#关联城市下架时无法上架 Banner 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：banner/Banner 上架前置校验#关联城市上架时可正常上架 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：banner/Banner 上架前置校验#下架无前置条件 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：banner/Banner 管理#名称重复被拒绝 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：banner/Banner 管理#更新时携带上下架字段被拒绝 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：banner/城市状态变更对 Banner 级联生效#城市下架连带 Banner 下架 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：banner/城市状态变更对 Banner 级联生效#城市重新上架连带 Banner 上架 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：file/图片上传的界面交互#单图控件三态切换 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：file/图片上传的界面交互#多图并发上传 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：file/图片上传的界面交互#非图片类型在选择阶段被拦 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：file/图片上传的界面交互#上传失败不阻塞表单 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：file/图片签名访问地址#多图字段保持顺序 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：file/图片链路的自动化覆盖边界#上传凭证成功分支在测试档位不可用 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：manager/账号启停与内置管理员保护#停用后无法登录 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：manager/运营账号分页查询#按用户名模糊过滤 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：manager/运营账号分页查询#页大小非白名单值被校正 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：manager/运营账号分页查询#查询不存在的账号返回 400 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：manager/运营账号管理#密码长度不足被拒绝 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/操作日志查询#操作人过滤为模糊匹配 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/操作日志查询#响应不含 payload 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/操作日志查询#非 ADMIN 角色可查询日志 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/留痕字段取值与敏感信息脱敏#密码字段被脱敏 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/留痕字段取值与敏感信息脱敏#创建类操作的 target 为空 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/留痕字段取值与敏感信息脱敏#更新类操作记录目标 id 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/留痕字段取值与敏感信息脱敏#嵌套资源的 target 取父级 id 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/运营写操作留痕#业务方法失败时不留痕 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：operation-log/运营写操作留痕#登录不产生日志 无 WEB/APP 用例且无 UT(@scenario) 覆盖

## 测试统计
- 总数：81
- ✅ 通过：0 (0.0%)
- ❌ 失败：0
- ⬜ 未测：81
