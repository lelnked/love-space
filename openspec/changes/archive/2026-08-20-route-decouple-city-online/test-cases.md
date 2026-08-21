# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/route/{it,web}.md`、`tests/city/{it,web}.md`、`tests/featured/it.md`
> （route / city / featured 三域均已在 `tests/modules.md` 登记，端为 `web`；本 change 不新增域、不改注册表）。

## 新增用例

### IT

- TC-route-IT-015: GET /api/app/routes 未上架城市的路线仍可见且详情返回 `cityName`（ADDED: route/App 端路线查询#未上架城市的路线仍可见）
- TC-city-IT-008: 城市下架后 app 端路线仍可见（不再级联）（ADDED: city/地图下架对活动级联生效#下架城市后 app 端路线仍可见）
- TC-city-IT-009: DELETE /api/admin/cities/{id} 城市下存在路线时拒绝删除（ADDED: city/城市下存在路线时禁止删除#有路线的城市不能删除）
- TC-city-IT-010: DELETE /api/admin/cities/{id} 路线清空后可正常删除城市（ADDED: city/城市下存在路线时禁止删除#路线清空后可删除城市）
- TC-featured-IT-020: GET /api/app/featured-cycle-items 城市未上架不影响路线类条目（ADDED: featured/App 端周期推荐查询#城市未上架不影响路线类条目）

### WEB

- TC-route-WEB-004: 路线表单所属城市下拉列出全部城市（下架带「（已下架）」）并可保存（ADDED: route/web 端大使与路线管理页面#路线表单可选未上架城市）

四象限覆盖：
- **happy** = TC-route-IT-015（下架城市路线列表 + 详情 200/`cityName`）、TC-city-IT-010（无引用时删除放行）、TC-route-WEB-004（下拉选中下架城市并保存成功）
- **boundary** = TC-featured-IT-020（同一下架城市下 ROUTE 可见 / ACTIVITY 不可见的分界）、TC-city-IT-006（级联范围收缩到只剩活动）
- **error** = TC-city-IT-009（400 + 中文业务错误，城市未被删）
- **state** = TC-city-IT-008（城市上架↔下架、大使上线↔下线的状态往返）、TC-featured-IT-018（大使下线后恢复上线，条目消失再出现）、TC-route-IT-015 步骤 4（城市上架前后结果一致）

未建用例的场景（不可经 API 构造，仅存量数据可达）：`route/App 端路线查询` 中「所属城市记录已被删除时 `cityName` 为 `null`」——本 change 的 D3 已禁止删除仍有路线的城市，该状态无法通过公开接口造出，改由 app 端 `RouteQueryService` 的 UT 以 mock 仓储覆盖（`@scenario route/App 端路线查询#未上架城市的路线仍可见`）。

## 修改用例

均为语义比对后**原地更新**，保留原 TC ID，「来源」改为 `route-decouple-city-online`，行为已变故状态回置 ⬜ 未测试并清空存证：

- TC-city-IT-006: 原「城市下架后 app 端路线与活动不可见（级联）」→ 收缩为「城市下架后 app 端活动不可见（级联），路线不受影响」；关联需求由已 REMOVED 的 `city/地图下架对路线与活动级联生效#…` 改挂 `city/地图下架对活动级联生效#下架城市后 app 端活动不可见`；断言删去「路线列表为空、路线详情 404」。
- TC-city-WEB-003: 原「确认提示包含路线与活动级联说明」→ 改为「包含活动级联说明且不含路线」；关联需求改挂 `city/地图下架对活动级联生效#web 下架确认提示不含路线`。
- TC-city-WEB-004: 精选推荐级联提示用例的预期文案去掉「路线」（关联需求名不变，仍为 `city/地图下架对精选推荐级联生效#web 下架确认提示包含精选推荐`）。
- TC-featured-IT-018: 删去原步骤 4「路线所属城市下架后条目消失」的反向断言，改为「恢复大使上线后条目重新出现」；新口径由 TC-featured-IT-020 承载。

追溯说明：`city` 域 requirement 走 REMOVED +（改名式）ADDED，`tests/city/` 中所有引用旧名「地图下架对路线与活动级联生效」的用例已全部改挂新名，grep 校验 `tests/` 下已无旧名残留，归档后不会出现悬空用例。

## 删除用例

（无。旧 requirement「地图下架对路线与活动级联生效」下的两条用例 TC-city-IT-006 / TC-city-WEB-003 的活动侧行为在新 requirement 中原样保留，故按语义比对**改挂 + 收窄断言**处理，而非删除重编。TC ID 不复用、不重编。）

## 需重测用例

行为未变，但所属 Requirement 被 MODIFIED 或同一查询路径的过滤条件被改动，交付轮一并回归确认未被带坏：

- TC-route-IT-012（上架城市路线列表排序）、TC-route-IT-013（大使下线后隐藏、详情 404）、TC-route-IT-014（详情地点与大使信息）——`route/App 端路线查询` 的可见性条件与详情 DTO 均有改动
- TC-route-WEB-002、TC-route-WEB-003——路线表单取数源由 `listOnlineCities()` 改为 `listCities()`，确认地点子列表与删除确认未受影响
- TC-city-IT-007（城市下架后精选推荐不可见）——所属 Requirement 文案 MODIFIED，行为不变
- TC-featured-IT-016、TC-featured-IT-017、TC-featured-IT-019——`featured/App 端周期推荐查询` 的 ROUTE 可见性过滤被改，需确认 ACTIVITY / ARTICLE 口径与分组排序未回归

`tests/featured/web.md` 本次无变化（周期推荐后台页面行为未改），不纳入执行范围。

## 执行汇总

<!-- runner 跑完后由编排 skill 填写：总数 / ✅ / ❌ / 未执行 -->
