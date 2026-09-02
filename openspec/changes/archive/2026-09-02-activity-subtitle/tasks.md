## 1. 数据库

- [x] 1.1 新增 `love-space-admin/src/main/resources/db/changelog/changes/023-add-activity-subtitle.sql`：`ALTER TABLE loves_activity ADD COLUMN subtitle text;`，含 `--comment` 与 `--rollback DROP COLUMN`（照 `019-add-activity-landscape.sql` 写法）
- [x] 1.2 在 `db.changelog-master.yaml` 末尾 include 该 changeset

## 2. admin 后端

- [x] 2.1 `activity/entity/Activity.java` 增 `@Column(name = "subtitle") private String subtitle;`（紧邻 title）
- [x] 2.2 `activity/dto/ActivityUpsertRequest.java` 增 `String subtitle`（无校验注解，javadoc 注明「副标题，可空」）
- [x] 2.3 `activity/dto/ActivityDetailResponse.java` 与 `ActivityItemResponse.java` 增 `String subtitle`，位置紧跟 `title`
- [x] 2.4 `activity/service/ActivityService.java` 在 create/update 写入与响应装配处补 `subtitle`（原样保存，与既有可空文本字段同口径）
- [x] 2.5 UT：活动 subtitle 写入/修改/清空（`@scenario activity/活动管理#副标题可写可改可空`）

## 3. app 后端

- [x] 3.1 `modules/activity/entity/Activity.java` 增 `subtitle` 映射（只读，同列名）
- [x] 3.2 `modules/activity/dto/ActivityItemResponse.java` 与 `ActivityDetailResponse.java` 增 `String subtitle`（紧跟 `title`）
- [x] 3.3 `modules/activity/service/ActivityQueryService.java` 装配处直出 `activity.getSubtitle()`，不回落为标题
- [x] 3.4 `modules/featuredcycle/dto/FeaturedCycleItemTargetResponse.java`：`ActivityTarget(UUID id, String title, String subtitle, ImageResponse cover, String level)`，javadoc 注明与条目手填 `subtitle` 相互独立
- [x] 3.5 `modules/featuredcycle/service/FeaturedCycleItemQueryService.java` 装配 ActivityTarget 时带上 `subtitle`（复用已在内存的活动实体 Map，查询次数不变）
- [x] 3.6 UT：活动列表/详情下发 subtitle 且未填时为 null（`@scenario activity/App 端活动查询#活动副标题下发且未填时为 null`）
- [x] 3.7 UT：ACTIVITY 类条目 target 含 subtitle；活动未填 subtitle 时 `target.subtitle` 为 null 而条目自身手填 `subtitle` 不变（`@scenario featured/App 端周期推荐查询#活动类条目下发活动基础信息`、`@scenario featured/App 端周期推荐查询#活动未填副标题时 target.subtitle 为 null`）

## 4. web 前端

- [x] 4.1 `src/api/activities.ts`：详情/列表类型与 upsert 请求类型各增 `subtitle: string | null` / `subtitle?: string | null`
- [x] 4.2 `src/pages/Activities/Form.tsx` 的「基础信息」fieldset（← 标题所在的两列 grid）内、标题右侧新增「副标题」`<Label>+<Input>`：state `subtitle`、编辑态回显 `d.subtitle ?? ""`、提交 `subtitle.trim() || null`；非必填故无 error/hint 落点，加载态沿用整表 `loading ? 加载中...`，保存失败沿用既有整表错误提示

## 5. 契约

- [x] 5.1 `contracts/api-spec.json`：admin 面 `ActivityUpsertRequest` / `ActivityDetailResponse` / `ActivityItemResponse`，app 面活动列表项与详情、`FeaturedCycleItemResponse` 的 ACTIVITY target 形状，各增 `subtitle`（nullable），受影响 operation 的 `x-requirement` 反链核对/补齐
- [x] 5.2 `love-space-app/docs/openapi.json` 同步上述 app 面字段

## 6. 验证

- [x] 6.1 admin `mvn test -Dtest='*Test'` 与 app `mvn test` 跑绿（按各端已知环境变量要求）
- [x] 6.2 IT 已跑：`/run-api-test --change activity-subtitle` 10/10 ✅；WEB 未跑：playwright MCP 本会话 CONNECT_TIMEOUT，TC-activity-WEB-004/005 待补
