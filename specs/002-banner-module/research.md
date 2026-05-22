# Phase 0 — Research: Banner Module

针对计划阶段识别的未知点 / 关键依赖，给出决策、理由、考虑过的替代方案。

## R1. `image` 列在 PostgreSQL 中的存储形态

- **Decision**: 在 `loves_banner` 表中使用 `image_urls jsonb NOT NULL DEFAULT '[]'::jsonb`
  存放有序字符串数组；JPA 实体用 `List<String> imageUrls` + Hibernate 6 内置 JSON 列支持
  （`@JdbcTypeCode(SqlTypes.JSON)`）。
- **Rationale**:
  - 与已有图片字段（如 merchant 模块图片列表）保持一致的存储风格，便于后续统一查询/导出。
  - jsonb 比"独立图片关联表"实现成本低，banner 单条图片量小（1–5 张），无独立 CRUD 需求。
  - 顺序保留靠 JSON 数组天然有序，无需排序列。
- **Alternatives Considered**:
  - 独立 `loves_banner_image` 表：写入/读取需 join，列表查询性能更差；好处仅在图片元数据丰富
    时显现，本特性用不到。
  - `text` 列 + 应用层 `String.join(",", urls)`：URL 可能含逗号需转义，脆弱。

## R2. City → Banner 状态联动的事件机制

- **Decision**: `CityService` 在 online 字段写入成功后于同一事务内 `publishEvent(new
  CityOnlineChangedEvent(cityId, oldOnline, newOnline))`；`BannerEventListener` 使用
  `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` 处理：仅当
  `oldOnline != newOnline` 时执行；通过 `BannerRepository.updateOnlineByCityLink(linkedEntityId,
  newOnline)` 批量 UPDATE。失败用 `try/catch + log.error` 包裹，不抛出。
- **Rationale**:
  - 满足 FR-018：联动在事务提交之后；联动失败不回滚城市状态。
  - 单条 UPDATE 比逐条 load-then-save 更省 IO，且无需触发 banner 实体级事件。
  - `AFTER_COMMIT` 自动避开"城市事务回滚后 banner 已被改"的脏写。
- **Alternatives Considered**:
  - 同事务内联级更新（`AFTER_COMPLETION` 之外的相位）：违反 FR-018 的"事务提交后"硬要求。
  - 通过 DB 触发器：违背宪法 II "数据库外键/触发器交由应用层" 的隐性原则，且 jsonb 列触发器
    复杂度高。
  - Repository 方法签名加 `@Modifying @Query` 用 JPQL 字符串：本身合规（JPQL 由 Hibernate 校验
    语法），不违反宪法 VI；但项目其它批量更新更倾向 Specification + `CriteriaUpdate`。为最大化
    metamodel 覆盖，监听器内使用 `CriteriaUpdate` + `Banner_.online`/`Banner_.linkedEntityId`。

## R3. `hibernate-jpamodelgen` 接入方式

- **Decision**: 在 admin / app 两个 `pom.xml` 的 `maven-compiler-plugin` 现有
  `annotationProcessorPaths` 中追加：

  ```xml
  <path>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-jpamodelgen</artifactId>
  </path>
  ```

  版本由 Spring Boot 4 BOM 管理（与项目对 Liquibase 不显式 pin 同样策略）。生成产物路径
  `target/generated-sources/annotations/`。`.gitignore` 已默认忽略 `target/`，无需追加。
- **Rationale**: 用 `annotationProcessorPaths` 而非 compile 依赖能避免运行时类路径污染，符合
  hibernate-jpamodelgen 官方建议。
- **Alternatives Considered**:
  - 作为 `<dependency><scope>provided</scope>`：能跑但运行时类路径仍出现 metamodel jar，
    部署包变大；且 IDE 自动注解处理识别会两侧重复。
  - `kapt`/`apt` 类工具：项目用纯 Maven，引入 Gradle 风格工具不合算。

## R4. Specification 设计：避免字符串字段名（宪法 VI）

- **Decision**: `BannerSpecifications` 类提供静态工厂方法：

  ```java
  Specification<Banner> nameContains(String keyword)   // root.get(Banner_.name)
  Specification<Banner> hasType(BannerType type)       // root.get(Banner_.type)
  Specification<Banner> onlineEquals(Boolean online)   // root.get(Banner_.online)
  Specification<Banner> linkedTo(UUID cityId)          // root.get(Banner_.linkedEntityId)
  ```

  排序使用 `Sort.by(Banner_.UPDATED_AT).descending()`（hibernate-jpamodelgen 生成的
  `public static final String UPDATED_AT = "updatedAt"` 常量，由 metamodel 自身导出，
  视作 metamodel 引用而非"我们写的字符串字面量"）。
- **Rationale**: 完全消除业务代码中的属性名字符串字面量；评审只需 grep
  `\.get\("` 在 specification 包内应为零结果。
- **Alternatives Considered**:
  - QueryDSL：能力更强但需引入新依赖与代码生成器，超出本特性范围。
  - Spring Data 派生查询（`findAllByOnlineTrueAndType...`）：方法名仍以字段名为标识符，但
    Hibernate 启动期校验属性存在，不违反宪法 VI；用作"简单过滤"的补充，复杂动态过滤仍走
    Specification。

## R5. 旧 City `bannerSortOrder` 数据迁移

- **Decision**: 一次性 Liquibase changelog `003-create-loves-banner.sql` 内附 `INSERT INTO
  loves_banner (id, name, online, type, image_urls, linked_entity_id, created_at, updated_at)
  SELECT uuidv7(), c.chinese_name, c.online, 'CITY', jsonb_build_array(c.background_image),
  c.id, now(), now() FROM loves_city c WHERE c.banner_sort_order > 0 AND c.background_image
  IS NOT NULL;`，确保历史可见 banner 一对一迁入；随后 `004-drop-city-banner-sort-order.sql`
  删除字段与相关索引。
- **Rationale**: SC-003 要求迁移丢失率 0%；与旧 `ExploreService` 取数逻辑一致
  （`banner_sort_order > 0` + `background_image` 非空 = 历史可见 banner）。
- **Alternatives Considered**:
  - 不做数据迁移、要求运营手动重建：违反 SC-003。
  - 在应用启动钩子里跑：可重入性差、跨环境难追踪；Liquibase 自带幂等 & 审计。

## R6. 移动端 `data` 字段的多态结构

- **Decision**: app 端 DTO `BannerItemResponse` 的 `data` 字段类型为
  `Map<String, Object>`，序列化为任意 JSON 对象；对 `BannerType.CITY` 由
  `BannerQueryService` 内联组装 `Map.of("id", cityId, "name", cityName)`。`type` 字段为
  枚举字符串。
- **Rationale**: FR-014 要求结构允许任意 JSON、便于未来扩展；`Map<String,Object>` 简单且
  Jackson 默认序列化正确。
- **Alternatives Considered**:
  - 多态子类 + `@JsonTypeInfo`：扩展性强但首期只有一种类型，过度设计。
  - 强类型 `CityBannerData` record：类型清晰但每加一种 type 都要改 DTO 接口，与"无破坏式扩展"
    冲突。

## R7. 前端"可搜索城市下拉框"

- **Decision**: 复用项目已有 React 19 + Tailwind 风格，自实现轻量组件 `CitySelect`：本地搜索
  （首屏请求全部 online 城市，运营场景城市量级 ≤ 200，无需远程检索）；输入即过滤
  `chineseName` / `englishName`。无第三方下拉库依赖。
- **Rationale**: 体量小避免引入 `react-select` 等重型库；TailAdmin 默认风格自洽。
- **Alternatives Considered**:
  - `react-select` / `cmdk`：能力强但增加 bundle；本场景过度。

## R8. 列表页 online 切换的乐观/校验顺序

- **Decision**: 列表行的 online 切换调用 `POST /api/admin/banners/{id}/online`，请求体
  `{online: boolean}`；服务端在 `online=true` 且 `type=CITY` 时校验关联城市当前 online，
  否则 400 `BANNER_LINKED_CITY_OFFLINE`。前端展示 toast 并回滚开关 UI。
- **Rationale**: FR-011；与现有 city 上下架接口风格一致（独立 endpoint + 单一动作）。
- **Alternatives Considered**:
  - 把 online 字段塞到 `PATCH /banners/{id}`：违反 FR-006/009（编辑页不能改 online）。

## 总结

所有 spec 中遗留的设计未知点已收敛，没有未解 `[NEEDS CLARIFICATION]`。可进入 Phase 1。
