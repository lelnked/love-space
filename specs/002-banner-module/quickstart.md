# Quickstart: Banner Module 本地联调

> 假定已读 `CLAUDE.md` 与 `plan.md`。本指南帮助开发者快速跑通端到端：admin 创建 banner →
> 列表启用 → 切换关联城市状态 → 移动端接口可见性同步。

## 0. 前置

- Java 25 + Maven Wrapper；PostgreSQL 本地可达。
- `unset SPRING_DATASOURCE_URL`（项目记忆：测试 shell 存在该环境变量会覆盖 yml 配置）。
- 两个后端 `pom.xml` 已加入 `hibernate-jpamodelgen` 注解处理器（本特性产出物）。
- Liquibase changelog `003-create-loves-banner.sql`、`004-drop-city-banner-sort-order.sql`
  在 `db.changelog-master.yaml` 的 include 顺序中位列既有 changelog 之后。

## 1. 启动

```bash
# Terminal A：admin 后端
cd love-space-admin
./mvnw spring-boot:run    # 会跑 Liquibase 迁移并生成 *_ metamodel 类

# Terminal B：app 后端
cd love-space-app
./mvnw spring-boot:run

# Terminal C：web 前端
cd love-space-web
npm install && npm run dev
```

启动后确认：

- `target/generated-sources/annotations/com/loves/space/modules/banner/Banner_.java` 存在。
- `target/generated-sources/annotations/com/loves/space/modules/city/City_.java` 存在。
- `loves_banner` 表已创建；`loves_city` 表不再含 `banner_sort_order` 列。

## 2. 端到端校验路径

### 2.1 创建 CITY banner

1. 浏览器登录 admin web，左侧菜单进入 **Banner**。
2. 点击"新增" → 填写名称、上传 1–2 张图片、type 选 `CITY`、在城市下拉框中搜索并选择一个
   online 城市，提交。
3. 列表页应出现新 banner，`online=false`。

### 2.2 启用 banner

1. 在列表页点开关切换为 online。
2. 调用 `GET /api/app/banners` 应返回该 banner，`data.id` 与 `data.name` 与所选城市匹配。

### 2.3 编辑页校验（FR-009）

1. 在列表页点"编辑"打开 banner。
2. 页面 MUST NOT 出现 online 开关；UI 走查通过。
3. 修改名称 / 重新选择城市后保存；`updatedAt` 刷新，`online` 状态保持。

### 2.4 City 联动（FR-016 / FR-017）

1. 在 City 列表对该城市点击下架（online → false）。
2. 几秒内（事件 AFTER_COMMIT 触发后）再次 `GET /api/app/banners` 应返回空 / 不再包含该
   banner；同时 admin 列表上对应 banner 的 online 已变为 false。
3. 重新上架城市；banner 自动恢复 online；app 端再次可见。

### 2.5 启用守卫（FR-011）

1. 选一个 city 当前 offline 的 banner，尝试在列表页启用 → 应弹出错误 toast
   `BANNER_LINKED_CITY_OFFLINE`，开关回滚。

## 3. 自动化测试

```bash
cd love-space-admin && ./mvnw -Dtest=BannerControllerTest test
cd love-space-admin && ./mvnw -Dtest=BannerEventListenerTest test
cd love-space-admin && ./mvnw -Dtest=BannerSpecificationTest test    # 校验 metamodel 引用、不含 root.get("...")
cd love-space-app && ./mvnw -Dtest=BannerControllerTest test
cd love-space-web && npm run build && npm run lint
```

## 4. 排错速查

- **`Cannot resolve symbol Banner_`**：注解处理器未跑，确认 `pom.xml` 的
  `annotationProcessorPaths` 包含 `hibernate-jpamodelgen`，并刷新 IDE 索引或运行
  `./mvnw clean compile`。
- **`column "banner_sort_order" does not exist`**：旧 SQL 残留，运行 `./mvnw
  liquibase:status` 检查 `004-drop-city-banner-sort-order.sql` 是否已执行。
- **City 下架后 banner 没联动**：检查事件监听器日志；确认 `@TransactionalEventListener` 注解
  阶段为 `AFTER_COMMIT`，事务边界正确。
