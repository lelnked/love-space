# Tasks: map-and-recommend-list

## 1. 数据库迁移（admin 端 Liquibase）

- [x] 1.1 新增 changes SQL：`loves_merchant` 加 `recommend_reason varchar(2000)`、`loves_city` 加 `editor_note varchar(200)`
- [x] 1.2 新增 changes SQL：建表 `loves_recommend_list`（title not null / introduction / city_id not null / sort_order not null default 0 + 审计列）与 `loves_recommend_list_merchant`（recommend_list_id / merchant_id / sort_order + 唯一约束 `(recommend_list_id, merchant_id)`），master changelog include

## 2. love-space-admin

- [x] 2.1 Merchant 实体/DTO/service 增加 `recommendReason`（`@Size(max=2000)` 中文报错），创建/更新/详情贯通
- [x] 2.2 City 实体/DTO/service 增加 `editorNote`（`@Size(max=200)` 中文报错），创建/更新/详情贯通
- [x] 2.3 新模块 `modules/recommendlist`：实体 `RecommendList`、`RecommendListMerchant` + repository
- [x] 2.4 RecommendListService：分页查询（cityId/keyword 过滤、sortOrder 升序）、详情（含商户按关联 sortOrder 升序）、创建（city 存在校验）、更新（cityId 不可变）、删除（同事务删关联）
- [x] 2.5 RecommendListService：全量替换清单商户（同城校验、重复拒绝，中文业务报错）
- [x] 2.6 RecommendListController：`/api/admin/recommend-lists` 五个接口 + `/{id}/merchants`
- [x] 2.7 UT：service 校验逻辑（@scenario 注释锚定 recommend-list/推荐清单管理#缺少必填项被拒绝、recommend-list/清单内商户维护#拒绝跨城市商户、#重复添加同一商户被拒绝、merchant/商户编辑推荐理由#推荐理由超长被拒绝、city/地图编辑说#编辑说超长被拒绝）

## 3. love-space-app

- [x] 3.1 Merchant DTO 增加 `recommendReason`（详情/列表），City DTO 增加 `editorNote`
- [x] 3.2 新模块 `modules/recommendlist`：只读实体 + repository + service（列表 join 城市 online 过滤、详情城市下架 404）+ controller `/api/app/recommend-lists`
- [x] 3.3 UT：service 级联可见性（@scenario 注释锚定 recommend-list/App 端清单查询#下架城市清单不可见）

## 4. love-space-web

- [x] 4.1 商户表单加「编辑推荐理由」textarea（maxLength 2000、非必填、编辑回显；校验错误按既有表单口径展示）
- [x] 4.2 城市表单加「编辑说」textarea（maxLength 200）；侧栏菜单与 Cities 页标题/文案「城市管理」→「地图管理」；下架确认文案追加"推荐清单"
- [x] 4.3 新页面 `pages/RecommendLists`：DataTable 列表（列：标题/所属城市/排序号/商户数/操作；城市筛选下拉；空态/loading/错误按 DataTable 既有口径）
- [x] 4.4 清单新建/编辑弹窗表单（标题必填、介绍、所属城市下拉[创建后禁用]、排序号；校验错误中文）
- [x] 4.5 清单商户维护：本城商户选择 + 每行排序号输入 + 移除；保存调 `PUT /{id}/merchants`；空态"暂无商户"
- [x] 4.6 删除确认弹窗（既有自定义弹窗组件）；路由与侧栏入口注册

## 5. 收尾

- [x] 5.1 `tests/modules.md` 域已登记（design 阶段完成，核对即可）；`contracts/api-spec.json` 已登记（核对）
- [x] 5.2 admin UT/IT 全绿（`./mvnw test` + `-Dtest='*IT'`）、app UT 全绿、web `npm run build` 通过
