# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/featured/{it,web}.md`（featured 域已在 `tests/modules.md` 登记，端为 `web`；
> 本 change 已同步补登该域的新接口路径前缀与页面域）。

## 新增用例

### IT（`tests/featured/it.md`）

- TC-featured-IT-007: POST /api/admin/featured-cycle-items 创建活动类周期推荐（ADDED: featured/周期推荐条目管理#创建活动类周期推荐）
- TC-featured-IT-008: POST /api/admin/featured-cycle-items 创建路线类周期推荐（ADDED: featured/周期推荐条目管理#创建路线类周期推荐）
- TC-featured-IT-009: POST /api/admin/featured-cycle-items 创建文章类周期推荐（ADDED: featured/周期推荐条目管理#创建文章类周期推荐）
- TC-featured-IT-010: POST /api/admin/featured-cycle-items 类型必填项缺失被拒绝（ADDED: featured/周期推荐条目管理#缺少类型必填项被拒绝）
- TC-featured-IT-011: POST /api/admin/featured-cycle-items 关联实体不存在被拒绝（ADDED: featured/周期推荐条目管理#关联实体不存在被拒绝）
- TC-featured-IT-012: PUT /api/admin/featured-cycle-items/{id} 周期与类型创建后不可变（ADDED: featured/周期推荐条目管理#周期与类型创建后不可变）
- TC-featured-IT-013: GET /api/admin/featured-cycle-items/page 按周期过滤并按排序号升序（ADDED: featured/周期推荐条目管理#按周期过滤列表）
- TC-featured-IT-014: PUT /api/admin/featured-cycle-items/{id}/online 上下线切换（ADDED: featured/周期推荐条目管理#周期推荐上下线切换）
- TC-featured-IT-015: DELETE /api/admin/featured-cycle-items/{id} 物理删除（新增写接口覆盖，Requirement: featured/周期推荐条目管理）
- TC-featured-IT-016: GET /api/app/featured-cycle-items 四周期分组齐全且只含上线条目（ADDED: featured/App 端周期推荐查询#查询四个周期的推荐列表）
- TC-featured-IT-017: GET /api/app/featured-cycle-items 关联实体不可见时条目不下发（ADDED: featured/App 端周期推荐查询#关联实体不可见时条目不下发）
- TC-featured-IT-018: GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目（ADDED: featured/App 端周期推荐查询#大使下线连带隐藏路线类条目）
- TC-featured-IT-019: GET /api/app/featured-cycle-items 组内按排序号升序（ADDED: featured/App 端周期推荐查询#组内按排序号升序）

### WEB（`tests/featured/web.md`）

- TC-featured-WEB-003: 周期推荐页四周期 Tab 切换与列表展示（ADDED: featured/web 端周期推荐页面#周期 Tab 切换与列表展示）
- TC-featured-WEB-004: 新增弹窗按内容类型切换字段块（ADDED: featured/web 端周期推荐页面#表单按类型切换字段）
- TC-featured-WEB-005: 周期生活法选中文章后自动带出主标题（ADDED: featured/web 端周期推荐页面#文章类型自动带出主标题）
- TC-featured-WEB-006: 弹窗表单新增周期推荐（ADDED: featured/web 端周期推荐页面#新增周期推荐）
- TC-featured-WEB-007: 周期推荐上下线切换与删除确认（ADDED: featured/web 端周期推荐页面#周期推荐上下线与删除）

四象限覆盖：happy = IT-007/008/009/013/016/019；boundary = IT-013/019（排序与同序号并列）、IT-015（重复删除）；error = IT-010/011/015；state = IT-012/014/017/018。

## 修改用例

（无。本 change 只对 `featured` spec 做 ADDED，既有「地图上新推荐」三条 Requirement 未改，
TC-featured-IT-001~006 与 TC-featured-WEB-001~002 的行为与断言不变。）

## 需重测用例

行为未变，但本 change 在同一域内新增模块、并动了 `tests/modules.md` 的 featured 行与
`contracts/api-spec.json`，交付轮一并回归确认既有「地图上新推荐」未被带坏：

- TC-featured-IT-001 ~ TC-featured-IT-006（admin/app 精选推荐接口未受新表与新端点影响）
- TC-featured-WEB-001、TC-featured-WEB-002（侧栏新增「周期推荐」入口后，原精选推荐页路由与页面不变）

## 执行汇总

<!-- runner 跑完后由编排 skill 填写：总数 / ✅ / ❌ / 未执行 -->
