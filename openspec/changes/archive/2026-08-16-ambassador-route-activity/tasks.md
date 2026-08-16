# Tasks: ambassador-route-activity

## 1. DB 迁移（admin 管 schema）

- [x] 1.1 Liquibase `010-create-ambassador-route.sql`：建 `loves_ambassador`（avatar/name/tags jsonb/online + 审计列）与 `loves_route`（city_id/sort_order/title/ambassador_note/thumbnail/images jsonb/travel_time/season/travel_status/ambassador_id/spots jsonb + 审计列），rollback DROP
- [x] 1.2 Liquibase `011-create-activity.sql`：建 `loves_activity`（city_id/images jsonb/title/tags jsonb/periods jsonb/level/introduction/editor_note/gathering_place/dismissal_place/transportation/visa/itinerary jsonb/detail_html text/online + 审计列），rollback DROP

## 2. admin 后端

- [x] 2.1 `modules.ambassador`：entity/repository/dto/service/controller——CRUD + page(keyword) + online 切换；标签 ≤3 条校验；avatar 走 validateAndBind/sign。UT 锚点 @scenario: route/爱女大使管理#创建大使、#标签超过 3 条被拒绝、#大使上下线切换
- [x] 2.2 `modules.route`：entity（spots jsonb 值对象）/repository/dto/service/controller——CRUD + page(cityId,keyword) sortOrder 升序 + 城市/大使存在校验 + cityId 不可变 + images ≥1、thumbnail 必填；删除物理删（spots 随行内 jsonb 自然消失）。UT 锚点 @scenario: route/路线管理#创建路线、#缺少必填项被拒绝、#路线列表按排序号升序、#删除路线
- [x] 2.3 `modules.activity`：entity/repository/dto/service/controller——CRUD + page + online 切换 + cityId 不可变 + images ≥1；detailHtml 保存时正则提取 img src 逐个 validateAndBind 回写 bound key、读时替换为签名 URL（工具方法 + UT 覆盖多图/无图）。UT 锚点 @scenario: activity/活动管理#创建活动、#缺少必填项被拒绝、#活动上下线切换

## 3. app 后端

- [x] 3.1 `modules.route`：只读实体 + 查询 service/controller——`GET /api/app/routes?cityId=`（城市上架 ∧ 大使上线过滤，sortOrder 升序）、`GET /api/app/routes/{id}`（不可见 404，含地点与大使信息）。UT 锚点 @scenario: route/App 端路线查询#查询上架城市的路线、#大使下线后路线隐藏、#路线详情返回地点明细；city/地图下架对路线与活动级联生效#下架城市后 app 端路线与活动不可见
- [x] 3.2 `modules.activity`：只读实体 + 查询 service/controller——`GET /api/app/activities?cityId=`（城市上架 ∧ 活动上线过滤）、`GET /api/app/activities/{id}`（不可见 404，detailHtml img src 签名替换）。UT 锚点 @scenario: activity/App 端活动查询#查询上架城市的活动、#下线活动不可见、#活动详情返回富文本

## 4. web 前端

- [x] 4.1 API 客户端与类型：ambassadors/routes/activities 三组接口封装（镜像 admin DTO）
- [x] 4.2 `pages/Ambassadors`：DataTable（头像/名称/标签/状态开关/操作）+ 弹窗表单（头像上传复用既有上传组件、名称、标签动态行 ≤3）+ 删除确认；空态/loading/错误沿既有口径
- [x] 4.3 `pages/Routes`：DataTable（缩略图/主标题/所属城市/大使/排序号/操作）+ 表单（城市下拉、大使下拉、图片多传、地点子列表增删）+ 删除确认
- [x] 4.4 安装 TipTap（@tiptap/react + @tiptap/starter-kit + @tiptap/extension-image），封装富文本编辑组件（图片插入走既有上传凭证链路，src 写 objectKey、预览用签名 URL）
- [x] 4.5 `pages/Activities`：DataTable（图片/标题/所属城市/级别/状态开关/操作）+ 表单（标签动态行、周期多选、级别单选、路线子条目动态行、TipTap 富文本挂活动详情说明）+ 删除确认
- [x] 4.6 侧栏 `AppSidebar` 新增「爱女大使」「路线管理」「活动管理」入口；`App.tsx` 挂路由
- [x] 4.7 `pages/Cities` 下架确认文案补充「路线、活动」。UT/WEB 断言对应 @scenario: city/地图下架对路线与活动级联生效#web 下架确认提示包含路线与活动

## 5. 收尾

- [x] 5.1 web 大使/路线/活动页面 UT（vitest 若无则按既有测试形态，至少覆盖表单校验逻辑）或确认 WEB 用例覆盖后跳过（矩阵口径：WEB 用例或 UT 二选一）（已确认：route/activity 各 Scenario 均有 WEB 用例覆盖，按矩阵口径跳过 web UT）
- [x] 5.2 `npm run lint && npm run build`、两后端 `./mvnw test` 全绿
