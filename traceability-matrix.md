# 追溯矩阵（全局核对）

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js`

## 需求与场景
- **activity/App 端活动查询**: 查询上架城市的活动 / 下线活动不可见 / 城市上架状态不影响活动详情可见性 / 活动详情返回富文本 / 活动副标题下发且未填时为 null
- **activity/web 端活动管理页面**: 活动列表与上下线 / 活动表单无地图选项即可保存 / 活动表单富文本编辑 / 活动表单填写景观并回显 / 活动表单填写副标题并回显
- **activity/活动管理**: 创建活动 / 缺少必填项被拒绝 / 请求体携带 cityId 不影响创建 / 活动列表不按城市过滤 / 活动上下线切换 / 副标题可写可改可空 / 景观字段可写可改可空
- **article/App 端文章查询**: 查询栏目与文章列表 / 不传栏目返回全部可见文章 / 未设封面标题时列表回落文章标题 / 详情返回引言与标签 / 下线文章不可见 / 失去所有栏目的文章不可见 / 文章详情返回富文本
- **article/web 端文章管理页面**: 栏目管理增删改 / 文章列表与上下线 / 文章表单富文本编辑 / 表单填写封面标题、引言与标签 / 存量文章封面标题为空时表单可正常打开
- **article/文章栏目管理**: 创建栏目 / 缺少必填项被拒绝 / 删除栏目不影响文章数据
- **article/文章管理**: 创建文章 / 创建带封面标题、引言与标签的文章 / 封面标题、引言、标签均可省略 / 缺少必填项被拒绝 / 文章上下线切换
- **auth/JWT 会话与授权链**: 无 token 访问受保护接口 / 非法 token 不影响免认证路径 / 角色不足返回 403
- **auth/web 端登录页与路由守卫**: 登录成功进入地图管理 / 两字段任一为空时无法提交 / 未登录访问后台被拦回登录页 / 非 ADMIN 角色看不到管理员管理入口
- **auth/当前登录人查询与登出**: 查询当前登录人 / 登出返回 204 且 token 仍然有效
- **auth/运营账号登录**: 内置管理员登录成功 / 密码错误被拒绝 / 停用账号无法登录 / 用户名不存在与密码错误不可区分
- **banner/App 端 Banner 查询**: 按展示位查询上架 Banner / 同排序号 Banner 按创建时间倒序 / 下架 Banner 不下发 / 关联城市下架时条目被剔除 / 缺少 API-key 返回 401
- **banner/Banner 上架前置校验**: 关联城市下架时无法上架 Banner / 关联城市上架时可正常上架 / 下架无前置条件
- **banner/Banner 管理**: 创建后默认下架 / 名称重复被拒绝 / 更新时携带上下架字段被拒绝 / 图片 objectKey 格式非法被拒绝
- **banner/web 端 Banner 管理页面**: 列表展示与状态徽标 / 上下架乐观更新失败回滚 / 删除需确认 / 表单城市下拉只列上架城市
- **banner/城市状态变更对 Banner 级联生效**: 城市下架连带 Banner 下架 / 城市重新上架连带 Banner 上架 / 删除城市只下架不删除 Banner
- **city/后台入口更名为地图管理**: 侧栏与页面标题展示地图管理
- **city/地图下架对推荐清单级联生效**: 下架城市后 app 端清单不可见 / web 下架确认提示包含清单
- **city/地图下架对精选推荐级联生效**: 下架城市后 app 端精选推荐不可见 / web 下架确认提示包含精选推荐 / 下架城市不过滤精选中的活动条目
- **city/地图下架对路线与活动均不级联**: 下架城市后 app 端路线仍可见 / 下架城市后 app 端活动仍可见 / web 下架确认提示不含路线与活动
- **city/地图删除**: 删除地图 / 有路线的地图可以直接删除 / 删除地图连带下架 Banner 与商户
- **city/地图编辑说**: admin 保存编辑说 / 编辑说超长被拒绝 / app 端城市数据返回编辑说
- **featured/App 端周期推荐查询**: 查询四个周期的推荐列表 / 同一 target 跨周期时下发全部周期 / 按周期过滤时 period 数组仍含其他周期 / 类型过滤不影响 period 数组 / 不可下发条目不贡献周期 / 不同 target 的周期集合互不影响 / 按周期过滤 / 周期与类型同时过滤 / 按内容类型过滤 / 类型过滤后周期为空仍返回空数组 / 周期过滤后无条目返回空数组 / 非法类型值被拒绝 / 非法周期值被拒绝 / 关联实体不可见时条目不下发 / 城市未上架不影响路线类条目 / 大使下线连带隐藏路线类条目 / 组内按排序号升序 / 活动类条目下发活动基础信息 / 活动未填副标题时 target.subtitle 为 null / 路线类条目下发路线基础信息且不覆盖手填文案 / 文章类条目下发文章基础信息 / 活动无图片时 cover 为 null
- **featured/App 端精选推荐查询**: 查询精选推荐信息流
- **featured/web 端周期推荐页面**: 周期 Tab 切换与列表展示 / 周期筛选下拉 / 新增周期推荐 / 未勾选周期无法提交 / 编辑时修改周期 / 关联实体重复时展示错误 / 表单按类型切换字段 / 文章类型自动带出主标题 / 周期推荐上下线与删除
- **featured/web 端精选推荐页面**: 精选推荐列表与上下线 / 新增精选推荐
- **featured/周期推荐条目管理**: 创建活动类周期推荐 / 创建多周期条目 / phases 为空被拒绝 / 创建路线类周期推荐 / 创建文章类周期推荐 / 同一关联实体重复创建被拒绝 / 下线条目同样占用唯一位 / 更新条目自身不触发唯一冲突 / 更新关联实体 / 更新指向已被占用的实体被拒绝 / 缺少类型必填项被拒绝 / 缺少 targetId 被拒绝 / 关联实体不存在被拒绝 / 周期与类型创建后不可变 / 按周期过滤列表 / 不传周期返回全部条目 / 周期推荐上下线切换
- **featured/精选推荐管理**: 创建精选推荐 / 缺少必填项被拒绝 / 精选推荐上下线切换
- **file/objectKey 两段式生命周期与绑定校验**: 未绑定图片在业务保存时被绑定 / 已绑定图片重复提交不再复制 / 非法 objectKey 格式被拒绝 / 业务保存失败后源图仍可重试
- **file/图片上传凭证签发**: 签发合法图片类型的上传凭证 / 非图片类型被拒绝 / 未登录无法获取凭证
- **file/图片上传的界面交互**: 单图控件三态切换 / 多图并发上传 / 非图片类型在选择阶段被拦 / 上传失败不阻塞表单
- **file/图片签名访问地址**: 业务详情返回签名地址 / 空图片字段不生成地址 / 多图字段保持顺序
- **file/图片链路的自动化覆盖边界**: 测试档位下绑定校验不访问存储 / 上传凭证成功分支在测试档位不可用
- **manager/web 端管理员管理页面**: 列表按角色与状态渲染 / 内置 admin 行不显示启停按钮 / 弹窗创建新账号 / 密码不足 8 位前端拦截
- **manager/账号启停与内置管理员保护**: 停用后无法登录 / 内置 admin 不可停用 / 启停可往复切换
- **manager/运营账号分页查询**: 按用户名模糊过滤 / 页大小非白名单值被校正 / 列表按创建时间倒序 / 查询不存在的账号返回 400
- **manager/运营账号管理**: 创建账号强制为 MEMBER 角色 / 用户名重复被拒绝 / 密码长度不足被拒绝 / 重置密码后旧密码失效
- **merchant/App 端带排序号列表的排序口径**: 分类列表同序号按创建时间倒序 / 商户评价同序号按创建时间倒序 / 排序号不同时以排序号为准 / weight 型排序号维持降序且已符合口径
- **merchant/商户编辑推荐理由**: admin 创建/更新商户时保存推荐理由 / 推荐理由超长被拒绝 / 推荐理由可为空 / app 端商户详情返回推荐理由 / web 商户表单录入推荐理由
- **operation-log/web 端操作日志页面**: 按操作人筛选日志 / 模块与动作按中文展示 / 未映射的动作回落显示原值 / 对象为空的记录显示占位符
- **operation-log/操作日志查询**: 按操作人与模块组合过滤 / 操作人过滤为模糊匹配 / 时间区间含边界 / 响应不含 payload / 非 ADMIN 角色可查询日志
- **operation-log/留痕字段取值与敏感信息脱敏**: 密码字段被脱敏 / 创建类操作的 target 为空 / 更新类操作记录目标 id / 嵌套资源的 target 取父级 id
- **operation-log/运营写操作留痕**: 创建城市后异步留痕 / 业务方法失败时不留痕 / 登录不产生日志
- **recommend-list/App 端清单与清单内商户查询**: 查询上架城市的清单 / 同排序号清单按创建时间倒序 / 清单详情返回商户明细 / 商户列表不受清单影响 / 下架城市清单不可见
- **recommend-list/web 端推荐清单管理页面**: 清单列表与筛选 / 维护清单商户 / 删除清单需确认
- **recommend-list/推荐清单管理**: 创建清单 / 缺少必填项被拒绝 / 修改所属城市需清单内商户同属新城市 / 人工恢复清单 / 删除清单 / 清单列表按排序号升序
- **recommend-list/清单内商户维护**: 添加本城市商户 / 拒绝跨城市商户 / 重复添加同一商户被拒绝 / 拒绝已下架商户 / 从清单移除商户
- **route/App 端路线查询**: 查询上架城市的路线 / 同排序号路线按创建时间倒序 / 不传任何过滤参数返回全部可见路线 / 按大使 ID 过滤路线 / 城市名与大使 ID 组合过滤 / 城市表中无同名城市时仍返回路线且 city 为 null / 列表项返回路线自身城市名 / 未上架城市的路线仍可见 / 大使下线后路线隐藏 / 路线详情返回地点明细 / 路线列表返回爱女大使说 / 路线详情返回大使 id
- **route/web 端大使与路线管理页面**: 大使列表与上下线 / 路线表单可选未上架城市 / 路线表单维护地点 / 删除路线需确认
- **route/爱女大使管理**: 创建大使 / 标签超过 3 条被拒绝 / 大使上下线切换
- **route/路线管理**: 创建路线 / 缺少必填项被拒绝 / 路线列表按排序号升序 / 删除路线

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-activity-IT-001 | POST /api/admin/activities 创建完整活动 | activity/活动管理#创建活动 | api-spec.json#/paths/~1api~1admin~1activities/post | ambassador-route-activity → activity-drop-city-link | IT | `test-evidence/activity-subtitle/TC-activity-IT-001/` | ✅ |
| TC-activity-IT-002 | POST /api/admin/activities 缺必填被拒绝 | activity/活动管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1activities/post | ambassador-route-activity → activity-drop-city-link | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-002/` | ⬜ |
| TC-activity-IT-003 | PUT /api/admin/activities/{id}/online 活动上下线切换 | activity/活动管理#活动上下线切换 | api-spec.json#/paths/~1api~1admin~1activities~1{id}~1online/put | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-003/` | ✅ |
| TC-activity-IT-004 | PUT /api/admin/activities/{id} 更新活动，请求体 cityId 被忽略 | activity/活动管理#请求体携带 cityId 不影响创建 | api-spec.json#/paths/~1api~1admin~1activities~1{id}/put | ambassador-route-activity → activity-drop-city-link | IT | `test-evidence/activity-subtitle/TC-activity-IT-004/` | ✅ |
| TC-activity-IT-005 | DELETE /api/admin/activities/{id} 物理删除活动 | activity/活动管理#创建活动 | api-spec.json#/paths/~1api~1admin~1activities~1{id}/delete | ambassador-route-activity → activity-drop-city-link | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-005/` | ⬜ |
| TC-activity-IT-006 | POST /api/admin/activities 富文本 img src 存 objectKey、admin 读时替换签名 URL | activity/活动管理#创建活动 | api-spec.json#/paths/~1api~1admin~1activities/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-006/` | ✅ |
| TC-activity-IT-007 | GET /api/app/activities 全局上线活动列表 | activity/App 端活动查询#查询上架城市的活动 | api-spec.json#/paths/~1api~1app~1activities/get | ambassador-route-activity → activity-drop-city-link → activity-subtitle | IT | `test-evidence/activity-subtitle/TC-activity-IT-007/` | ✅ |
| TC-activity-IT-008 | GET /api/app/activities 下线活动不可见、详情 404 | activity/App 端活动查询#下线活动不可见 | api-spec.json#/paths/~1api~1app~1activities~1{id}/get | ambassador-route-activity → activity-drop-city-link | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-008/` | ⬜ |
| TC-activity-IT-009 | GET /api/app/activities/{id} 详情返回富文本且 img src 为签名 URL | activity/App 端活动查询#活动详情返回富文本 | api-spec.json#/paths/~1api~1app~1activities~1{id}/get | ambassador-route-activity → activity-drop-city-link | IT | `test-evidence/activity-subtitle/TC-activity-IT-009/` | ✅ |
| TC-activity-IT-020 | 活动景观字段贯通 admin 写入与 admin/app 查询 | activity/活动管理#景观字段可写可改可空 | api-spec.json#/components/schemas/ActivityUpsertRequest | activity-landscape-field → activity-drop-city-link | IT | `test-evidence/activity-subtitle/TC-activity-IT-020/` | ✅ |
| TC-activity-IT-021 | GET /api/admin/activities/page 携带 cityId 不收窄结果 | activity/活动管理#活动列表不按城市过滤 | api-spec.json#/paths/~1api~1admin~1activities~1page/get | activity-drop-city-link | IT | `test-evidence/regression/activity/TC-activity-IT-021/` | ⬜ |
| TC-activity-IT-022 | GET /api/app/activities/{id} 详情不受城市上架状态影响 | activity/App 端活动查询#城市上架状态不影响活动详情可见性 | api-spec.json#/paths/~1api~1app~1activities~1{id}/get | activity-drop-city-link | IT | `test-evidence/regression/activity/TC-activity-IT-022/` | ⬜ |
| TC-activity-IT-023 | 活动副标题可写可改可清空（admin 侧） | activity/活动管理#副标题可写可改可空 | api-spec.json#/components/schemas/ActivityUpsertRequest | activity-subtitle | IT | `test-evidence/activity-subtitle/TC-activity-IT-023/` | ✅ |
| TC-activity-IT-024 | GET /api/app/activities 列表与详情下发 subtitle，未填时为 null | activity/App 端活动查询#活动副标题下发且未填时为 null | api-spec.json#/paths/~1api~1app~1activities/get | activity-subtitle | IT | `test-evidence/activity-subtitle/TC-activity-IT-024/` | ✅ |
| TC-activity-WEB-001 | 活动列表展示与上下线开关 | activity/web 端活动管理页面#活动列表与上下线 | - | ambassador-route-activity → activity-drop-city-link | WEB | `test-evidence/ambassador-route-activity/TC-activity-WEB-001/` | ⬜ |
| TC-activity-WEB-002 | 活动表单富文本编辑并回显 | activity/web 端活动管理页面#活动表单富文本编辑 | - | ambassador-route-activity → activity-drop-city-link | WEB | `test-evidence/ambassador-route-activity/TC-activity-WEB-002/` | ⬜ |
| TC-activity-WEB-003 | 活动表单填写景观并回显 | activity/web 端活动管理页面#活动表单填写景观并回显 | - | activity-landscape-field → activity-drop-city-link | WEB | `test-evidence/activity-landscape-field/TC-activity-WEB-003/` | ⬜ |
| TC-activity-WEB-004 | 活动表单无地图选项即可保存 | activity/web 端活动管理页面#活动表单无地图选项即可保存 | - | activity-drop-city-link | WEB | `test-evidence/regression/activity/TC-activity-WEB-004/` | ⬜ |
| TC-activity-WEB-005 | 活动表单填写副标题并回显 | activity/web 端活动管理页面#活动表单填写副标题并回显 | - | activity-subtitle | WEB | `test-evidence/regression/activity/TC-activity-WEB-005/` | ⬜ |
| TC-article-IT-001 | POST /api/admin/article-categories 创建栏目 | article/文章栏目管理#创建栏目 | api-spec.json#/paths/~1api~1admin~1article-categories/post | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-001/` | ✅ |
| TC-article-IT-002 | POST /api/admin/article-categories 缺必填被拒绝 | article/文章栏目管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1article-categories/post | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-002/` | ✅ |
| TC-article-IT-003 | PUT /api/admin/article-categories/{id} 更新栏目 | article/文章栏目管理#创建栏目 | api-spec.json#/paths/~1api~1admin~1article-categories~1{id}/put | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-003/` | ✅ |
| TC-article-IT-004 | DELETE /api/admin/article-categories/{id} 删除栏目不影响文章数据 | article/文章栏目管理#删除栏目不影响文章数据 | api-spec.json#/paths/~1api~1admin~1article-categories~1{id}/delete | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-004/` | ✅ |
| TC-article-IT-005 | POST /api/admin/articles 创建关联多栏目的完整文章 | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles/post | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-005/` | ✅ |
| TC-article-IT-006 | POST /api/admin/articles 缺必填或栏目不存在被拒绝 | article/文章管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1articles/post | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-006/` | ✅ |
| TC-article-IT-007 | PUT /api/admin/articles/{id} 更新文章与栏目关联 | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles~1{id}/put | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-007/` | ✅ |
| TC-article-IT-008 | PUT /api/admin/articles/{id}/online 文章上下线切换 | article/文章管理#文章上下线切换 | api-spec.json#/paths/~1api~1admin~1articles~1{id}~1online/put | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-008/` | ✅ |
| TC-article-IT-009 | DELETE /api/admin/articles/{id} 物理删除文章 | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles~1{id}/delete | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-009/` | ✅ |
| TC-article-IT-010 | POST /api/admin/articles 富文本 img src 存 objectKey、admin 读时替换签名 URL | article/文章管理#创建文章 | api-spec.json#/paths/~1api~1admin~1articles/post | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-010/` | ✅ |
| TC-article-IT-011 | GET /api/app/article-categories 与 /api/app/articles 均按权重升序 | article/App 端文章查询#查询栏目与文章列表 | api-spec.json#/paths/~1api~1app~1article-categories/get | article-cover-title-intro-tags | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-article-IT-011/` | ✅ |
| TC-article-IT-012 | GET /api/app/articles 下线文章不可见、详情 404 | article/App 端文章查询#下线文章不可见 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-article-IT-012/` | ✅ |
| TC-article-IT-013 | GET /api/app/articles/{id} 失去所有栏目的文章不可见 | article/App 端文章查询#失去所有栏目的文章不可见 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-013/` | ✅ |
| TC-article-IT-014 | GET /api/app/articles/{id} 详情返回富文本且 img src 为签名 URL | article/App 端文章查询#文章详情返回富文本 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-and-featured-feed | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-014/` | ✅ |
| TC-article-IT-015 | POST /api/admin/articles 创建带封面标题、引言与标签的文章 | article/文章管理#创建带封面标题、引言与标签的文章 | api-spec.json#/paths/~1api~1admin~1articles/post | article-cover-title-intro-tags | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-015/` | ✅ |
| TC-article-IT-016 | POST /api/admin/articles 省略封面标题、引言、标签 | article/文章管理#封面标题、引言、标签均可省略 | api-spec.json#/paths/~1api~1admin~1articles/post | article-cover-title-intro-tags | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-016/` | ✅ |
| TC-article-IT-017 | PUT /api/admin/articles/{id} 空白值按 null 存、标签空白项剔除 | article/文章管理#创建带封面标题、引言与标签的文章 | api-spec.json#/paths/~1api~1admin~1articles~1{id}/put | article-cover-title-intro-tags | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-017/` | ✅ |
| TC-article-IT-018 | GET /api/app/articles 未设封面标题时回落文章标题 | article/App 端文章查询#未设封面标题时列表回落文章标题 | api-spec.json#/paths/~1api~1app~1articles/get | article-cover-title-intro-tags | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-article-IT-018/` | ✅ |
| TC-article-IT-019 | GET /api/app/articles/{id} 详情返回引言与标签 | article/App 端文章查询#详情返回引言与标签 | api-spec.json#/paths/~1api~1app~1articles~1{id}/get | article-cover-title-intro-tags | IT | `test-evidence/article-cover-title-intro-tags/TC-article-IT-019/` | ✅ |
| TC-article-IT-020 | GET /api/app/articles 不传 categoryId 返回全部可见文章 | article/App 端文章查询#不传栏目返回全部可见文章 | api-spec.json#/paths/~1api~1app~1articles/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/app-article-optional-category-and-featured-period-filter/TC-article-IT-020/` | ✅ |
| TC-article-WEB-001 | 文章栏目页新增与删除 | article/web 端文章管理页面#栏目管理增删改 | - | article-and-featured-feed | WEB | `test-evidence/regression/article/TC-article-WEB-001/` | ✅ |
| TC-article-WEB-002 | 文章列表展示与上下线开关 | article/web 端文章管理页面#文章列表与上下线 | - | article-cover-title-intro-tags | WEB | `test-evidence/article-cover-title-intro-tags/TC-article-WEB-002/` | ✅ |
| TC-article-WEB-003 | 文章表单富文本编辑与栏目多选回显 | article/web 端文章管理页面#文章表单富文本编辑 | - | article-and-featured-feed | WEB | `test-evidence/article-cover-title-intro-tags/TC-article-WEB-003/` | ✅ |
| TC-article-WEB-004 | 文章表单填写封面标题、引言与多条标签并回显 | article/web 端文章管理页面#表单填写封面标题、引言与标签 | - | article-cover-title-intro-tags | WEB | `test-evidence/article-cover-title-intro-tags/TC-article-WEB-004/` | ✅ |
| TC-article-WEB-005 | 存量无封面标题与引言的文章打开编辑表单不报错 | article/web 端文章管理页面#存量文章封面标题为空时表单可正常打开 | - | article-cover-title-intro-tags | WEB | `test-evidence/article-cover-title-intro-tags/TC-article-WEB-005/` | ✅ |
| TC-auth-IT-001 | POST /api/admin/auth/login 内置管理员登录成功 | auth/运营账号登录#内置管理员登录成功 | api-spec.json#/paths/~1api~1admin~1auth~1login/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-002 | POST /api/admin/auth/login 密码错误返回 401 | auth/运营账号登录#密码错误被拒绝 | api-spec.json#/paths/~1api~1admin~1auth~1login/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-003 | 停用账号以正确密码登录仍返回 401 | auth/运营账号登录#停用账号无法登录 | api-spec.json#/paths/~1api~1admin~1auth~1login/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-004 | 用户名不存在与密码错误、账号停用消息不可区分 | auth/运营账号登录#用户名不存在与密码错误不可区分 | api-spec.json#/paths/~1api~1admin~1auth~1login/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-005 | 无 token 访问受保护接口返回 401 | auth/JWT 会话与授权链#无 token 访问受保护接口 | api-spec.json#/paths/~1api~1admin~1auth~1me/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-006 | 非法 token 不影响登录接口 | auth/JWT 会话与授权链#非法 token 不影响免认证路径 | api-spec.json#/paths/~1api~1admin~1auth~1login/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-007 | MEMBER 角色访问管理员接口返回 403 | auth/JWT 会话与授权链#角色不足返回 403 | api-spec.json#/paths/~1api~1admin~1managers~1page/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-008 | GET /api/admin/auth/me 返回当前登录人 | auth/当前登录人查询与登出#查询当前登录人 | api-spec.json#/paths/~1api~1admin~1auth~1me/get | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-IT-009 | 登出返回 204 且同一 token 仍可用 | auth/当前登录人查询与登出#登出返回 204 且 token 仍然有效 | api-spec.json#/paths/~1api~1admin~1auth~1logout/post | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-auth-WEB-001 | 登录成功跳转地图管理页 | auth/web 端登录页与路由守卫#登录成功进入地图管理 | - | baseline-auth-manager-banner-log-file | WEB | - | ✅ |
| TC-auth-WEB-002 | 两字段任一为空时登录按钮禁用 | auth/web 端登录页与路由守卫#两字段任一为空时无法提交 | - | baseline-auth-manager-banner-log-file | WEB | - | ✅ |
| TC-auth-WEB-003 | 未登录直接访问后台页面被重定向到登录页 | auth/web 端登录页与路由守卫#未登录访问后台被拦回登录页 | - | baseline-auth-manager-banner-log-file | WEB | - | ✅ |
| TC-auth-WEB-004 | MEMBER 角色侧栏无「管理员管理」入口 | auth/web 端登录页与路由守卫#非 ADMIN 角色看不到管理员管理入口 | - | baseline-auth-manager-banner-log-file | WEB | - | ✅ |
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
| TC-banner-IT-012 | GET /api/app/banners 按展示位返回上架 Banner 并按排序号升序 | banner/App 端 Banner 查询#按展示位查询上架 Banner | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/app-list-sort-tiebreak/TC-banner-IT-012/` | ✅ |
| TC-banner-IT-013 | GET /api/app/banners 排序号并列时按创建时间倒序 | banner/App 端 Banner 查询#同排序号 Banner 按创建时间倒序 | api-spec.json#/paths/~1api~1app~1banners/get | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-banner-IT-013/` | ✅ |
| TC-banner-IT-014 | GET /api/app/banners 下架 Banner 不下发 | banner/App 端 Banner 查询#下架 Banner 不下发 | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/app-list-sort-tiebreak/TC-banner-IT-014/` | ✅ |
| TC-banner-IT-015 | GET /api/app/banners 关联城市下架时条目被剔除 | banner/App 端 Banner 查询#关联城市下架时条目被剔除 | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/app-list-sort-tiebreak/TC-banner-IT-015/` | ✅ |
| TC-banner-IT-016 | GET /api/app/banners 缺少 API-key 返回 401 | banner/App 端 Banner 查询#缺少 API-key 返回 401 | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/app-list-sort-tiebreak/TC-banner-IT-016/` | ✅ |
| TC-banner-IT-017 | DELETE /api/admin/banners/{id} 物理删除 Banner | banner/Banner 管理#创建后默认下架 | api-spec.json#/paths/~1api~1admin~1banners~1{id}/delete | baseline-auth-manager-banner-log-file | IT | - | ⬜ |
| TC-banner-WEB-001 | Banner 列表展示与上下架徽标 | banner/web 端 Banner 管理页面#列表展示与状态徽标 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-banner-WEB-002 | 上架乐观更新失败后回滚并提示 | banner/web 端 Banner 管理页面#上下架乐观更新失败回滚 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-banner-WEB-003 | 删除 Banner 需二次确认 | banner/web 端 Banner 管理页面#删除需确认 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-banner-WEB-004 | 表单关联城市下拉只列出已上架城市 | banner/web 端 Banner 管理页面#表单城市下拉只列上架城市 | - | baseline-auth-manager-banner-log-file | WEB | - | ⬜ |
| TC-city-IT-001 | POST /api/admin/cities 创建城市保存编辑说 | city/地图编辑说#admin 保存编辑说 | api-spec.json#/paths/~1api~1admin~1cities/post | map-and-recommend-list | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-001/` | ✅ |
| TC-city-IT-002 | PUT /api/admin/cities/{id} 编辑说 200 字边界通过 | city/地图编辑说#admin 保存编辑说 | api-spec.json#/paths/~1api~1admin~1cities~1{id}/put | map-and-recommend-list | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-002/` | ✅ |
| TC-city-IT-003 | PUT /api/admin/cities/{id} 编辑说 201 字被拒绝 | city/地图编辑说#编辑说超长被拒绝 | api-spec.json#/paths/~1api~1admin~1cities~1{id}/put | map-and-recommend-list | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-003/` | ✅ |
| TC-city-IT-004 | GET /api/app/cities app 端城市列表返回编辑说 | city/地图编辑说#app 端城市数据返回编辑说 | api-spec.json#/paths/~1api~1app~1cities/get | map-and-recommend-list | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-004/` | ✅ |
| TC-city-IT-005 | 城市下架后 app 端推荐清单不可见（级联） | city/地图下架对推荐清单级联生效#下架城市后 app 端清单不可见 | api-spec.json#/paths/~1api~1app~1recommend-lists/get | map-and-recommend-list | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-005/` | ✅ |
| TC-city-IT-006 | 城市下架后 app 端活动仍可见（不再级联） | city/地图下架对路线与活动均不级联#下架城市后 app 端活动仍可见 | api-spec.json#/paths/~1api~1app~1activities/get | city-drop-route-delete-guard | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-006/` | ✅ |
| TC-city-IT-007 | 城市下架后 app 端精选推荐不可见（级联） | city/地图下架对精选推荐级联生效#下架城市后 app 端精选推荐不可见 | api-spec.json#/paths/~1api~1app~1featured-items/get | article-and-featured-feed | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-007/` | ✅ |
| TC-city-IT-008 | 城市下架后 app 端路线仍可见（不再级联） | city/地图下架对路线与活动均不级联#下架城市后 app 端路线仍可见 | api-spec.json#/paths/~1api~1app~1routes/get、api-spec.json#/paths/~1api~1app~1routes~1{id}/get | city-drop-route-delete-guard | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-008/` | ✅ |
| TC-city-IT-011 | GET /api/app/cities/{id} 返回上架城市详情 | city/地图编辑说#app 端城市数据返回编辑说 | api-spec.json#/paths/~1api~1app~1cities~1{id}/get | 直接实现（未走 change） | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-011/` | ✅ |
| TC-city-IT-012 | GET /api/app/cities/{id} 未上架或不存在返回 404 | city/地图编辑说#app 端城市数据返回编辑说 | api-spec.json#/paths/~1api~1app~1cities~1{id}/get | 直接实现（未走 change） | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-012/` | ✅ |
| TC-city-IT-013 | DELETE /api/admin/cities/{id} 删除地图并连带下架 Banner 与商户 | city/地图删除#删除地图 | api-spec.json#/paths/~1api~1admin~1cities~1{id}/delete | city-drop-route-delete-guard | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-013/` | ✅ |
| TC-city-IT-014 | DELETE /api/admin/cities/{id} 存在路线时地图仍可直接删除 | city/地图删除#有路线的地图可以直接删除 | api-spec.json#/paths/~1api~1admin~1cities~1{id}/delete | city-drop-route-delete-guard | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-014/` | ✅ |
| TC-city-WEB-001 | 侧栏与页面标题展示「地图管理」 | city/后台入口更名为地图管理#侧栏与页面标题展示地图管理 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-city-WEB-001/` | ✅ |
| TC-city-WEB-002 | 城市下架确认提示包含推荐清单级联说明 | city/地图下架对推荐清单级联生效#web 下架确认提示包含清单 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-city-WEB-002/` | ✅ |
| TC-city-WEB-003 | 城市下架确认提示的级联说明不含路线与活动 | city/地图下架对路线与活动均不级联#web 下架确认提示不含路线与活动 | - | city-drop-route-delete-guard | WEB | `test-evidence/regression/city/TC-city-WEB-003/` | ⬜ |
| TC-city-WEB-004 | 城市下架确认提示包含精选推荐级联说明 | city/地图下架对精选推荐级联生效#web 下架确认提示包含精选推荐 | - | route-decouple-city-online | WEB | `test-evidence/article-and-featured-feed/TC-city-WEB-004/` | ✅ |
| TC-featured-IT-001 | POST /api/admin/featured-items 创建精选推荐 | featured/精选推荐管理#创建精选推荐 | api-spec.json#/paths/~1api~1admin~1featured-items/post | article-and-featured-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-001/` | ✅ |
| TC-featured-IT-002 | POST /api/admin/featured-items 缺 banner 或城市不存在被拒绝 | featured/精选推荐管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1featured-items/post | article-and-featured-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-002/` | ✅ |
| TC-featured-IT-003 | PUT /api/admin/featured-items/{id}/online 上下线切换 | featured/精选推荐管理#精选推荐上下线切换 | api-spec.json#/paths/~1api~1admin~1featured-items~1{id}~1online/put | article-and-featured-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-003/` | ✅ |
| TC-featured-IT-004 | PUT /api/admin/featured-items/{id} 更新条目且 cityId 不可变 | featured/精选推荐管理#创建精选推荐 | api-spec.json#/paths/~1api~1admin~1featured-items~1{id}/put | article-and-featured-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-004/` | ✅ |
| TC-featured-IT-005 | DELETE /api/admin/featured-items/{id} 物理删除 | featured/精选推荐管理#创建精选推荐 | api-spec.json#/paths/~1api~1admin~1featured-items~1{id}/delete | article-and-featured-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-005/` | ✅ |
| TC-featured-IT-006 | GET /api/app/featured-items 信息流仅含上线条目且按创建时间倒序 | featured/App 端精选推荐查询#查询精选推荐信息流 | api-spec.json#/paths/~1api~1app~1featured-items/get | article-and-featured-feed | IT | `test-evidence/featured-cycle-feed/TC-featured-IT-006/` | ✅ |
| TC-featured-IT-007 | POST /api/admin/featured-cycle-items 创建活动类周期推荐 | featured/周期推荐条目管理#创建活动类周期推荐 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-007/` | ✅ |
| TC-featured-IT-008 | POST /api/admin/featured-cycle-items 创建路线类周期推荐 | featured/周期推荐条目管理#创建路线类周期推荐 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-008/` | ✅ |
| TC-featured-IT-009 | POST /api/admin/featured-cycle-items 创建文章类周期推荐 | featured/周期推荐条目管理#创建文章类周期推荐 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-009/` | ✅ |
| TC-featured-IT-010 | POST /api/admin/featured-cycle-items 类型必填项缺失被拒绝 | featured/周期推荐条目管理#缺少类型必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-010/` | ✅ |
| TC-featured-IT-011 | POST /api/admin/featured-cycle-items 关联实体不存在被拒绝 | featured/周期推荐条目管理#关联实体不存在被拒绝 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-011/` | ✅ |
| TC-featured-IT-012 | PUT /api/admin/featured-cycle-items/{id} phases 可改而 type 创建后不可变 | featured/周期推荐条目管理#周期与类型创建后不可变 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/put | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-012/` | ✅ |
| TC-featured-IT-013 | GET /api/admin/featured-cycle-items/page phase 参数按「包含」过滤并按排序号升序 | featured/周期推荐条目管理#按周期过滤列表 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1page/get | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-013/` | ✅ |
| TC-featured-IT-014 | PUT /api/admin/featured-cycle-items/{id}/online 上下线切换 | featured/周期推荐条目管理#周期推荐上下线切换 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}~1online/put | featured-cycle-feed | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-014/` | ✅ |
| TC-featured-IT-015 | DELETE /api/admin/featured-cycle-items/{id} 物理删除 | featured/周期推荐条目管理 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/delete | featured-cycle-feed | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-015/` | ✅ |
| TC-featured-IT-016 | GET /api/app/featured-cycle-items 扁平数组带 period 周期数组且只含上线条目 | featured/App 端周期推荐查询#查询四个周期的推荐列表 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-016/` | ✅ |
| TC-featured-IT-017 | GET /api/app/featured-cycle-items 关联实体不可见时条目不下发 | featured/App 端周期推荐查询#关联实体不可见时条目不下发 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-017/` | ✅ |
| TC-featured-IT-018 | GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目 | featured/App 端周期推荐查询#大使下线连带隐藏路线类条目 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-018/` | ✅ |
| TC-featured-IT-019 | GET /api/app/featured-cycle-items 按排序号升序 | featured/App 端周期推荐查询#组内按排序号升序 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-019/` | ✅ |
| TC-featured-IT-020 | GET /api/app/featured-cycle-items 城市未上架不影响路线类条目 | featured/App 端周期推荐查询#城市未上架不影响路线类条目 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-period-tags | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-020/` | ✅ |
| TC-featured-IT-021 | GET /api/app/featured-cycle-items?type= 按内容类型过滤 | featured/App 端周期推荐查询#按内容类型过滤 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-021/` | ✅ |
| TC-featured-IT-022 | GET /api/app/featured-cycle-items?type= 类型过滤后无条目返回空数组 | featured/App 端周期推荐查询#类型过滤后周期为空仍返回空数组 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-022/` | ✅ |
| TC-featured-IT-023 | GET /api/app/featured-cycle-items?type= 非法类型值返回 400 | featured/App 端周期推荐查询#非法类型值被拒绝 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-023/` | ✅ |
| TC-featured-IT-024 | GET /api/app/featured-cycle-items?period= 按周期过滤 | featured/App 端周期推荐查询#按周期过滤 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-024/` | ✅ |
| TC-featured-IT-025 | GET /api/app/featured-cycle-items?period=&type= 周期与类型同时过滤 | featured/App 端周期推荐查询#周期与类型同时过滤 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-025/` | ✅ |
| TC-featured-IT-026 | GET /api/app/featured-cycle-items?period= 周期过滤后无条目返回空数组 | featured/App 端周期推荐查询#周期过滤后无条目返回空数组 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-026/` | ✅ |
| TC-featured-IT-027 | GET /api/app/featured-cycle-items?period= 非法周期值返回 400 | featured/App 端周期推荐查询#非法周期值被拒绝 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | app-article-optional-category-and-featured-period-filter | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-027/` | ✅ |
| TC-featured-IT-028 | GET /api/app/featured-cycle-items 多周期条目在 period 数组中下发全部周期 | featured/App 端周期推荐查询#同一 target 跨周期时下发全部周期 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-028/` | ✅ |
| TC-featured-IT-029 | GET /api/app/featured-cycle-items?period= 过滤后 period 数组仍含其他周期 | featured/App 端周期推荐查询#按周期过滤时 period 数组仍含其他周期 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-029/` | ✅ |
| TC-featured-IT-030 | GET /api/app/featured-cycle-items?type=&period= 类型过滤不影响 period 数组 | featured/App 端周期推荐查询#类型过滤不影响 period 数组 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-030/` | ✅ |
| TC-featured-IT-031 | GET /api/app/featured-cycle-items 下线条目整条不下发 | featured/App 端周期推荐查询#不可下发条目不贡献周期 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-031/` | ✅ |
| TC-featured-IT-032 | GET /api/app/featured-cycle-items?period= 不同 target 的周期集合互不影响 | featured/App 端周期推荐查询#不同 target 的周期集合互不影响 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-032/` | ✅ |
| TC-featured-IT-033 | POST /api/admin/featured-cycle-items 缺 targetId 被拒绝 | featured/周期推荐条目管理#缺少 targetId 被拒绝 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-033/` | ✅ |
| TC-featured-IT-034 | GET /api/app/featured-cycle-items 活动类条目下发活动基础信息 | featured/App 端周期推荐查询#活动类条目下发活动基础信息 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-target-basic-info → activity-subtitle | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-034/` | ✅ |
| TC-featured-IT-035 | GET /api/app/featured-cycle-items 路线类条目下发路线基础信息且不覆盖手填文案 | featured/App 端周期推荐查询#路线类条目下发路线基础信息且不覆盖手填文案 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-target-basic-info | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-035/` | ✅ |
| TC-featured-IT-036 | GET /api/app/featured-cycle-items 文章类条目下发文章基础信息 | featured/App 端周期推荐查询#文章类条目下发文章基础信息 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-target-basic-info | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-036/` | ✅ |
| TC-featured-IT-037 | GET /api/app/featured-cycle-items 活动无图片时 target.cover 为 null | featured/App 端周期推荐查询#活动无图片时 cover 为 null | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-item-target-basic-info | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-037/` | ✅ |
| TC-featured-IT-038 | GET /api/app/featured-cycle-items 活动未填副标题时 target.subtitle 为 null | featured/App 端周期推荐查询#活动未填副标题时 target.subtitle 为 null | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | activity-subtitle | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-038/` | ✅ |
| TC-featured-IT-039 | POST /api/admin/featured-cycle-items 创建多周期条目 | featured/周期推荐条目管理#创建多周期条目 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-039/` | ✅ |
| TC-featured-IT-040 | POST /api/admin/featured-cycle-items phases 为空或缺省被拒绝 | featured/周期推荐条目管理#phases 为空被拒绝 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-040/` | ✅ |
| TC-featured-IT-041 | POST /api/admin/featured-cycle-items 同一关联实体重复创建被拒绝 | featured/周期推荐条目管理#同一关联实体重复创建被拒绝 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-041/` | ✅ |
| TC-featured-IT-042 | POST /api/admin/featured-cycle-items 下线条目同样占用唯一位 | featured/周期推荐条目管理#下线条目同样占用唯一位 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-042/` | ✅ |
| TC-featured-IT-043 | PUT /api/admin/featured-cycle-items/{id} 更新条目自身不触发唯一冲突 | featured/周期推荐条目管理#更新条目自身不触发唯一冲突 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/put | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-043/` | ✅ |
| TC-featured-IT-046 | PUT /api/admin/featured-cycle-items/{id} 更新关联实体 | featured/周期推荐条目管理#更新关联实体 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/put | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-046/` | ✅ |
| TC-featured-IT-044 | PUT /api/admin/featured-cycle-items/{id} 更新指向已被占用的实体被拒绝 | featured/周期推荐条目管理#更新指向已被占用的实体被拒绝 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/put | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-044/` | ✅ |
| TC-featured-IT-045 | GET /api/admin/featured-cycle-items/page 不传周期返回全部条目 | featured/周期推荐条目管理#不传周期返回全部条目 | api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1page/get | featured-cycle-item-multi-phase-single-target | IT | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-045/` | ✅ |
| TC-featured-WEB-001 | 精选推荐列表展示与上下线开关 | featured/web 端精选推荐页面#精选推荐列表与上下线 | - | article-and-featured-feed | WEB | `test-evidence/regression/featured/TC-featured-WEB-001/` | ✅ |
| TC-featured-WEB-002 | 弹窗表单新增精选推荐 | featured/web 端精选推荐页面#新增精选推荐 | - | article-and-featured-feed | WEB | `test-evidence/regression/featured/TC-featured-WEB-002/` | ✅ |
| TC-featured-WEB-003 | 周期推荐页单列表展示与投放周期标签 | featured/web 端周期推荐页面#周期 Tab 切换与列表展示 | - | featured-cycle-item-multi-phase-single-target | WEB | `test-evidence/regression/featured/TC-featured-WEB-003/` | ⬜ |
| TC-featured-WEB-004 | 新增表单页按内容类型切换字段块 | featured/web 端周期推荐页面#表单按类型切换字段 | - | featured-cycle-item-multi-phase-single-target | WEB | `test-evidence/regression/featured/TC-featured-WEB-004/` | ⬜ |
| TC-featured-WEB-005 | 周期生活法选中文章后自动带出主标题 | featured/web 端周期推荐页面#文章类型自动带出主标题 | - | featured-cycle-feed | WEB | `test-evidence/regression/featured/TC-featured-WEB-005/` | ⬜ |
| TC-featured-WEB-006 | 表单页新增多周期周期推荐 | featured/web 端周期推荐页面#新增周期推荐 | - | featured-cycle-item-multi-phase-single-target | WEB | `test-evidence/regression/featured/TC-featured-WEB-006/` | ⬜ |
| TC-featured-WEB-007 | 周期推荐上下线切换与删除确认 | featured/web 端周期推荐页面#周期推荐上下线与删除 | - | featured-cycle-item-multi-phase-single-target | WEB | `test-evidence/regression/featured/TC-featured-WEB-007/` | ⬜ |
| TC-featured-WEB-008 | 周期筛选下拉过滤列表 | featured/web 端周期推荐页面#周期筛选下拉 | - | featured-cycle-item-multi-phase-single-target | WEB | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-WEB-008/` | ⬜ |
| TC-featured-WEB-009 | 未勾选周期无法提交 | featured/web 端周期推荐页面#未勾选周期无法提交 | - | featured-cycle-item-multi-phase-single-target | WEB | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-WEB-009/` | ⬜ |
| TC-featured-WEB-010 | 编辑时修改周期 | featured/web 端周期推荐页面#编辑时修改周期 | - | featured-cycle-item-multi-phase-single-target | WEB | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-WEB-010/` | ⬜ |
| TC-featured-WEB-011 | 关联实体重复时展示后端中文业务错误 | featured/web 端周期推荐页面#关联实体重复时展示错误 | - | featured-cycle-item-multi-phase-single-target | WEB | `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-WEB-011/` | ⬜ |
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
| TC-manager-WEB-001 | 管理员列表按角色与状态渲染 | manager/web 端管理员管理页面#列表按角色与状态渲染 | - | baseline-auth-manager-banner-log-file | WEB | - | ✅ |
| TC-manager-WEB-002 | 内置 admin 行不显示启停按钮 | manager/web 端管理员管理页面#内置 admin 行不显示启停按钮 | - | baseline-auth-manager-banner-log-file | WEB | - | ✅ |
| TC-manager-WEB-003 | 弹窗创建新账号 | manager/web 端管理员管理页面#弹窗创建新账号 | - | baseline-auth-manager-banner-log-file | WEB | - | ✅ |
| TC-manager-WEB-004 | 密码不足 8 位前端拦截且不发请求 | manager/web 端管理员管理页面#密码不足 8 位前端拦截 | - | baseline-auth-manager-banner-log-file | WEB | - | ✅ |
| TC-merchant-IT-001 | POST /api/admin/merchants 创建商户保存推荐理由 | merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由 | api-spec.json#/paths/~1api~1admin~1merchants/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-001/` | ✅ |
| TC-merchant-IT-002 | PUT /api/admin/merchants/{id} 更新推荐理由 | merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由 | api-spec.json#/paths/~1api~1admin~1merchants~1{id}/put | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-002/` | ✅ |
| TC-merchant-IT-003 | POST /api/admin/merchants 推荐理由 2000 字边界通过 | merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由 | api-spec.json#/paths/~1api~1admin~1merchants/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-003/` | ✅ |
| TC-merchant-IT-004 | POST /api/admin/merchants 推荐理由 2001 字被拒绝 | merchant/商户编辑推荐理由#推荐理由超长被拒绝 | api-spec.json#/paths/~1api~1admin~1merchants/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-004/` | ✅ |
| TC-merchant-IT-005 | POST /api/admin/merchants 不填推荐理由创建成功 | merchant/商户编辑推荐理由#推荐理由可为空 | api-spec.json#/paths/~1api~1admin~1merchants/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-005/` | ✅ |
| TC-merchant-IT-006 | GET /api/app/merchants/{id} app 端详情返回推荐理由 | merchant/商户编辑推荐理由#app 端商户详情返回推荐理由 | api-spec.json#/paths/~1api~1app~1merchants~1{id}/get | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-006/` | ✅ |
| TC-merchant-IT-007 | GET /api/app/categories/page 同排序号分类按创建时间倒序 | merchant/App 端带排序号列表的排序口径#分类列表同序号按创建时间倒序 | ⚠️ 待补契约（api-spec.json 中缺 `/api/app/categories/page` 条目） | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-merchant-IT-007/` | ✅ |
| TC-merchant-IT-008 | GET /api/app/categories/page 排序号优先于创建时间 | merchant/App 端带排序号列表的排序口径#排序号不同时以排序号为准 | ⚠️ 待补契约（api-spec.json 中缺 `/api/app/categories/page` 条目） | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-merchant-IT-008/` | ✅ |
| TC-merchant-IT-009 | GET /api/app/merchants/{merchantId}/reviews 同排序号评价按创建时间倒序 | merchant/App 端带排序号列表的排序口径#商户评价同序号按创建时间倒序 | ⚠️ 待补契约（api-spec.json 中缺 `/api/app/merchants/{merchantId}/reviews` 条目） | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-merchant-IT-009/` | ✅ |
| TC-merchant-WEB-001 | 商户表单录入推荐理由并回显 | merchant/商户编辑推荐理由#web 商户表单录入推荐理由 | - | map-and-recommend-list | WEB | `test-evidence/regression/merchant/TC-merchant-WEB-001/` | ✅ |
| TC-merchant-WEB-002 | 推荐理由超长表单校验提示 | merchant/商户编辑推荐理由#web 商户表单录入推荐理由 | - | map-and-recommend-list | WEB | `test-evidence/regression/merchant/TC-merchant-WEB-002/` | ✅ |
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
| TC-recommend-list-IT-001 | POST /api/admin/recommend-lists 创建清单成功 | recommend-list/推荐清单管理#创建清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists/post | map-and-recommend-list | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-001/` | ✅ |
| TC-recommend-list-IT-002 | POST /api/admin/recommend-lists 缺少必填项被拒绝 | recommend-list/推荐清单管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1recommend-lists/post | map-and-recommend-list | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-002/` | ✅ |
| TC-recommend-list-IT-003 | POST /api/admin/recommend-lists 不传 sortOrder 默认 0 | recommend-list/推荐清单管理#创建清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists/post | map-and-recommend-list | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-003/` | ✅ |
| TC-recommend-list-IT-004 | PUT /api/admin/recommend-lists/{id} 修改所属城市需清单内商户同属新城市 | recommend-list/推荐清单管理#修改所属城市需清单内商户同属新城市 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-004/` | ✅ |
| TC-recommend-list-IT-005 | DELETE /api/admin/recommend-lists/{id} 物理删除含商户关联的清单 | recommend-list/推荐清单管理#删除清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/delete | map-and-recommend-list | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-005/` | ✅ |
| TC-recommend-list-IT-006 | GET /api/admin/recommend-lists/page 按 sortOrder 升序并支持过滤 | recommend-list/推荐清单管理#清单列表按排序号升序 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1page/get | map-and-recommend-list | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-006/` | ✅ |
| TC-recommend-list-IT-007 | PUT /api/admin/recommend-lists/{id} merchantIds 整体替换本城市商户并按数组顺序回显 | recommend-list/清单内商户维护#添加本城市商户 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-007/` | ✅ |
| TC-recommend-list-IT-008 | PUT /api/admin/recommend-lists/{id} merchantIds 含跨城市商户被拒绝 | recommend-list/清单内商户维护#拒绝跨城市商户 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-008/` | ✅ |
| TC-recommend-list-IT-009 | PUT /api/admin/recommend-lists/{id} merchantIds 重复商户被拒绝 | recommend-list/清单内商户维护#重复添加同一商户被拒绝 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-009/` | ✅ |
| TC-recommend-list-IT-010 | PUT /api/admin/recommend-lists/{id} merchantIds 去掉商户即移除且不影响商户本身 | recommend-list/清单内商户维护#从清单移除商户 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-010/` | ✅ |
| TC-recommend-list-IT-011 | GET /api/app/recommend-lists 上架城市清单按 sortOrder 升序 | recommend-list/App 端清单与清单内商户查询#查询上架城市的清单 | api-spec.json#/paths/~1api~1app~1recommend-lists/get | map-and-recommend-list | IT | `test-evidence/app-list-sort-tiebreak/TC-recommend-list-IT-011/` | ✅ |
| TC-recommend-list-IT-012 | GET /api/app/recommend-lists/{id} 详情按清单保存顺序返回上架商户四字段 | recommend-list/App 端清单与清单内商户查询#清单详情返回商户明细 | api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get | app-recommend-list-owns-merchant-order | IT | `test-evidence/app-list-sort-tiebreak/TC-recommend-list-IT-012/` | ✅ |
| TC-recommend-list-IT-013 | GET /api/app/recommend-lists 下架城市清单不可见、详情 404 | recommend-list/App 端清单与清单内商户查询#下架城市清单不可见 | api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get | map-and-recommend-list | IT | `test-evidence/app-recommend-list-owns-merchant-order/TC-recommend-list-IT-013/` | ✅ |
| TC-recommend-list-IT-015 | GET /api/app/merchants/page 商户列表不受清单影响 | recommend-list/App 端清单与清单内商户查询#商户列表不受清单影响 | api-spec.json#/paths/~1api~1app~1merchants~1page/get | app-recommend-list-owns-merchant-order | IT | `test-evidence/app-list-sort-tiebreak/TC-recommend-list-IT-015/` | ✅ |
| TC-recommend-list-IT-016 | PUT /api/admin/recommend-lists/{id} merchantIds 含已下架商户被拒绝 | recommend-list/清单内商户维护#拒绝已下架商户 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-016/` | ✅ |
| TC-recommend-list-IT-017 | POST /api/admin/recommend-lists status 默认 ONLINE 且可带 status/merchantIds 创建 | recommend-list/推荐清单管理#创建清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists/post | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-017/` | ✅ |
| TC-recommend-list-IT-018 | POST /api/admin/recommend-lists/{id}/online 人工恢复清单（含下架商户拒绝、成功、幂等） | recommend-list/推荐清单管理#人工恢复清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}~1online/post | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-018/` | ✅ |
| TC-recommend-list-IT-019 | GET /api/app/recommend-lists 同排序号清单按创建时间倒序 | recommend-list/App 端清单与清单内商户查询#同排序号清单按创建时间倒序 | api-spec.json#/paths/~1api~1app~1recommend-lists/get | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-recommend-list-IT-019/` | ✅ |
| TC-recommend-list-WEB-001 | 推荐清单列表与城市筛选 | recommend-list/web 端推荐清单管理页面#清单列表与筛选 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-recommend-list-WEB-001/` | ✅ |
| TC-recommend-list-WEB-002 | 清单编辑界面维护商户（仅本城市可选） | recommend-list/web 端推荐清单管理页面#维护清单商户 | - | recommend-list-align-spec-to-merchant-ids | WEB | `test-evidence/map-and-recommend-list/TC-recommend-list-WEB-002/` | ✅ |
| TC-recommend-list-WEB-003 | 删除清单需确认（确认删除、取消保留） | recommend-list/web 端推荐清单管理页面#删除清单需确认 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-recommend-list-WEB-003/` | ✅ |
| TC-route-IT-001 | POST /api/admin/ambassadors 创建大使成功且标签顺序保持 | route/爱女大使管理#创建大使 | api-spec.json#/paths/~1api~1admin~1ambassadors/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-001/` | ✅ |
| TC-route-IT-002 | POST /api/admin/ambassadors 标签边界 3 条通过、4 条拒绝 | route/爱女大使管理#标签超过 3 条被拒绝 | api-spec.json#/paths/~1api~1admin~1ambassadors/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-002/` | ✅ |
| TC-route-IT-003 | PUT /api/admin/ambassadors/{id}/online 大使上下线切换 | route/爱女大使管理#大使上下线切换 | api-spec.json#/paths/~1api~1admin~1ambassadors~1{id}~1online/put | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-003/` | ✅ |
| TC-route-IT-004 | PUT /api/admin/ambassadors/{id} 更新大使字段 | route/爱女大使管理#创建大使 | api-spec.json#/paths/~1api~1admin~1ambassadors~1{id}/put | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-004/` | ✅ |
| TC-route-IT-005 | DELETE /api/admin/ambassadors/{id} 物理删除大使 | route/爱女大使管理#创建大使 | api-spec.json#/paths/~1api~1admin~1ambassadors~1{id}/delete | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-005/` | ✅ |
| TC-route-IT-006 | POST /api/admin/routes 创建路线含 2 个地点按提交顺序返回 | route/路线管理#创建路线 | api-spec.json#/paths/~1api~1admin~1routes/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-006/` | ✅ |
| TC-route-IT-007 | POST /api/admin/routes 缺必填或大使不存在被拒绝（城市名不校验） | route/路线管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1routes/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-007/` | ⬜ |
| TC-route-IT-008 | POST /api/admin/routes 路线图片边界 1 张通过、空数组拒绝 | route/路线管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1routes/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-008/` | ✅ |
| TC-route-IT-009 | PUT /api/admin/routes/{id} 更新路线且 cityId 不可变 | route/路线管理#创建路线 | api-spec.json#/paths/~1api~1admin~1routes~1{id}/put | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-009/` | ✅ |
| TC-route-IT-010 | GET /api/admin/routes/page 按 sortOrder 升序并支持过滤 | route/路线管理#路线列表按排序号升序 | api-spec.json#/paths/~1api~1admin~1routes~1page/get | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-010/` | ✅ |
| TC-route-IT-011 | DELETE /api/admin/routes/{id} 物理删除路线连带地点 | route/路线管理#删除路线 | api-spec.json#/paths/~1api~1admin~1routes~1{id}/delete | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-011/` | ✅ |
| TC-route-IT-012 | GET /api/app/routes?cityName= 按城市名查路线列表并按 sortOrder 升序 | route/App 端路线查询#查询上架城市的路线 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-list-sort-tiebreak/TC-route-IT-012/` | ✅ |
| TC-route-IT-013 | GET /api/app/routes 大使下线后路线隐藏、详情 404 | route/App 端路线查询#大使下线后路线隐藏 | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-013/` | ✅ |
| TC-route-IT-014 | GET /api/app/routes/{id} 路线详情返回地点明细与大使信息 | route/App 端路线查询#路线详情返回地点明细 | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | ambassador-route-activity | IT | `test-evidence/app-route-query-filters/TC-route-IT-014/` | ✅ |
| TC-route-IT-015 | GET /api/app/routes 未上架城市的路线仍可见且详情返回 cityName | route/App 端路线查询#未上架城市的路线仍可见 | api-spec.json#/paths/~1api~1app~1routes/get、api-spec.json#/paths/~1api~1app~1routes~1{id}/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-015/` | ✅ |
| TC-route-IT-016 | GET /api/app/routes 不带任何参数返回全部可见路线 | route/App 端路线查询#不传任何过滤参数返回全部可见路线 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-016/` | ✅ |
| TC-route-IT-017 | GET /api/app/routes?ambassadorId= 按大使过滤路线 | route/App 端路线查询#按大使 ID 过滤路线 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-017/` | ✅ |
| TC-route-IT-018 | GET /api/app/routes?cityName=&ambassadorId= 组合过滤取交集 | route/App 端路线查询#城市名与大使 ID 组合过滤 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-018/` | ✅ |
| TC-route-IT-019 | GET /api/app/routes?cityName= 城市表无同名城市时仍返回路线且 city 为 null | route/App 端路线查询#城市表中无同名城市时仍返回路线且 city 为 null | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters / app-route-list-city-name | IT | `test-evidence/regression/route/TC-route-IT-019/` | ✅ |
| TC-route-IT-020 | GET /api/app/ambassadors 默认返回权重最高的 3 位上线大使 | route/爱女大使管理 | api-spec.json#/paths/~1api~1app~1ambassadors/get | 直接实现（未走 change） | IT | `love-space-app/target/surefire-reports/com.space.app.modules.ambassador.controller.AmbassadorReadIT.txt` | ✅ |
| TC-route-IT-021 | GET /api/app/ambassadors?limit= 生效且上限 20、非法值回落 3 | route/爱女大使管理 | api-spec.json#/paths/~1api~1app~1ambassadors/get | 直接实现（未走 change） | IT | `love-space-app/target/surefire-reports/com.space.app.modules.ambassador.controller.AmbassadorReadIT.txt` | ✅ |
| TC-route-IT-022 | GET /api/app/ambassadors/{id} 详情与 404 口径 | route/爱女大使管理 | api-spec.json#/paths/~1api~1app~1ambassadors~1{id}/get | 直接实现（未走 change） | IT | `love-space-app/target/surefire-reports/com.space.app.modules.ambassador.controller.AmbassadorReadIT.txt` | ✅ |
| TC-route-IT-023 | admin 大使创建/更新写入排序权重 | route/爱女大使管理 | api-spec.json#/paths/~1api~1admin~1ambassadors/post | 直接实现（未走 change） | IT | `love-space-admin/target/surefire-reports/com.loves.space.modules.ambassador.service.AmbassadorServiceTest.txt` | ✅ |
| TC-route-IT-024 | GET /api/app/routes 同排序号路线按创建时间倒序 | route/App 端路线查询#同排序号路线按创建时间倒序 | api-spec.json#/paths/~1api~1app~1routes/get | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-route-IT-024/` | ✅ |
| TC-route-IT-025 | GET /api/app/routes 列表项返回 ambassadorNote | route/App 端路线查询#路线列表返回爱女大使说 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-ambassador-fields | IT | `test-evidence/regression/route/TC-route-IT-025/` | ✅ |
| TC-route-IT-026 | GET /api/app/routes/{id} 详情 ambassador 含 id | route/App 端路线查询#路线详情返回大使 id | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | app-route-ambassador-fields | IT | `test-evidence/regression/route/TC-route-IT-026/` | ✅ |
| TC-route-IT-027 | GET /api/app/routes 列表项返回路线自身城市名 cityName | route/App 端路线查询#列表项返回路线自身城市名 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-list-city-name | IT | `test-evidence/regression/route/TC-route-IT-027/` | ✅ |
| TC-route-WEB-001 | 大使列表展示与上下线开关 | route/web 端大使与路线管理页面#大使列表与上下线 | - | ambassador-route-activity | WEB | `test-evidence/regression/route/TC-route-WEB-001/` | ✅ |
| TC-route-WEB-002 | 路线表单维护地点子列表并按添加顺序回显 | route/web 端大使与路线管理页面#路线表单维护地点 | - | ambassador-route-activity | WEB | `test-evidence/regression/route/TC-route-WEB-002/` | ✅ |
| TC-route-WEB-003 | 删除路线需确认（确认删除、取消保留） | route/web 端大使与路线管理页面#删除路线需确认 | - | ambassador-route-activity | WEB | `test-evidence/regression/route/TC-route-WEB-003/` | ✅ |
| TC-route-WEB-004 | 路线表单所属城市下拉列出全部城市（下架带「（已下架）」）并可保存 | route/web 端大使与路线管理页面#路线表单可选未上架城市 | - | route-decouple-city-online | WEB | `test-evidence/regression/route/TC-route-WEB-004/` | ✅ |

## 覆盖核对

- ⚠ 未覆盖：activity/活动管理#缺少必填项被拒绝 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：activity/活动管理#请求体携带 cityId 不影响创建 无 WEB/APP 用例且无 UT(@scenario) 覆盖
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
- ⚠ 未覆盖：city/地图下架对精选推荐级联生效#下架城市不过滤精选中的活动条目 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：city/地图下架对路线与活动均不级联#下架城市后 app 端路线仍可见 无 WEB/APP 用例且无 UT(@scenario) 覆盖
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
- 总数：261
- ✅ 通过：172 (65.9%)
- ❌ 失败：0
- ⬜ 未测：89
