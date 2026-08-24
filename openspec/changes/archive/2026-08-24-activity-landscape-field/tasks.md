## 1. 数据库

- [x] 1.1 新增 `love-space-admin/src/main/resources/db/changelog/changes/019-add-activity-landscape.sql`：`ALTER TABLE loves_activity ADD COLUMN landscape text`，rollback 为 `DROP COLUMN`
- [x] 1.2 `db.changelog-master.yaml` include 019

## 2. admin 后端

- [x] 2.1 `Activity` 实体新增 `@Column(name = "landscape") String landscape`
- [x] 2.2 `ActivityUpsertRequest` 新增 `String landscape`（可空，无校验注解，与 transportation/visa 同口径）
- [x] 2.3 `ActivityDetailResponse` 新增 `String landscape`
- [x] 2.4 `ActivityService`：apply 写入 `activity.setLandscape(request.landscape())`，toDetail 回填

## 3. app 后端

- [x] 3.1 `Activity` 实体新增 `landscape` 列映射
- [x] 3.2 `ActivityDetailResponse` 新增 `String landscape`（仅详情，列表不加）
- [x] 3.3 `ActivityQueryService` 详情映射回填 `landscape`

## 4. web 前端

- [x] 4.1 `api/activities.ts`：`ActivityDetail` 加 `landscape: string | null`，`ActivityUpsertRequest` 加 `landscape?: string | null`
- [x] 4.2 `pages/Activities/Form.tsx`：新增 `landscape` state、加载回显、提交 `landscape.trim() || null`、「签证」之后渲染「景观」输入框

## 5. 契约与测试

- [x] 5.1 `contracts/api-spec.json`：`ActivityUpsertRequest` 新增 `landscape` 属性
- [x] 5.2 `ActivityServiceTest` 构造器补 landscape 参数，新增 UT `landscapeIsWritableUpdatableAndNullable`（@scenario activity/活动管理#景观字段可写可改可空）
- [x] 5.2b `ActivityQueryServiceTest` 新增 UT `detailReturnsLandscape`（@scenario activity/App 端活动查询#活动详情返回景观）
- [x] 5.3 `tests/activity/it.md` 新增 TC-activity-IT-020（admin 写入 → admin 查询 → app 详情三段贯通）
- [x] 5.4 `tests/activity/web.md` 新增 TC-activity-WEB-003（表单填写景观并回显）

## 6. 交付验证

- [x] 6.1 IT 实跑 TC-activity-IT-020（admin :21423 + app :8081，库 localhost:25432/love_space）
- [x] 6.2 WEB 实跑 TC-activity-WEB-003
