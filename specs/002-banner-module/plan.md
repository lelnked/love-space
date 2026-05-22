# Implementation Plan: Banner Module

**Branch**: `002-banner-module` | **Date**: 2026-05-22 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/002-banner-module/spec.md`

## Summary

将 banner 从 `City` 实体内嵌字段（`bannerSortOrder` + 复用 `backgroundImage`）剥离为独立的
`Banner` 模块：admin 端提供完整 CRUD + 上下线管理，app 端用新的 `/api/app/banners` 接口替换
旧的 `/api/app/explore`，并通过 Spring 应用事件（`CityOnlineChangedEvent` →
`BannerEventListener`，事务后置阶段）实现 City 上下线对其关联 CITY banner 的状态联动。所有
JPA Criteria/Specification 查询通过 `hibernate-jpamodelgen` 生成的 metamodel（`Banner_`、
`City_`）访问字段，禁止字符串字面量字段名（宪法 VI）。

## Technical Context

**Language/Version**: Java 25（admin / app 两个后端，`<java.version>25</java.version>`）；
TypeScript 5 + React 19（`love-space-web`）。

**Primary Dependencies**:
- 后端：Spring Boot 4.0.6（Web MVC + Data JPA + Security）、Lombok、Liquibase（formatted SQL
  changelogs）、`com.github.f4b6a3:uuid-creator`（UUIDv7）、**新增**
  `org.hibernate.orm:hibernate-jpamodelgen`（注解处理器，annotationProcessorPaths）
- 前端：Vite 6、Tailwind CSS v4、react-router v7、TailAdmin 组件基线

**Storage**: PostgreSQL；新表 `loves_banner`；image 列存为 `jsonb`（字符串数组）。

**Testing**:
- 后端 `./mvnw test`：JUnit 5 + MockMvc + Spring Boot Test（test profile 已在 yaml；启动前
  unset `SPRING_DATASOURCE_URL` 见用户记忆 `project_shell_env_spring_datasource_override`）
- 前端 `npm run build`（含 `tsc -b`）+ `npm run lint`，必要时人工浏览器验证

**Target Platform**: Linux 服务端（两个 Spring Boot 应用）、现代浏览器（admin web）、移动端
（消费 app 后端，本仓库不含客户端代码）。

**Project Type**: 多项目工作区（admin 后端 + app 后端 + admin 前端）。

**Performance Goals**: app 端 banner 列表接口 P95 不劣化于原 explore 接口；admin 列表分页 ≤
200ms（10k banner 量级）。

**Constraints**:
- 宪法 II：UUIDv7 主键 + 无外键 + UUID 列。
- 宪法 III：字段不缩写（`imageUrls`、`linkedEntityId`，不使用 `imgs`、`linkId`）。
- 宪法 VI：Specification/Criteria 查询 MUST 走 metamodel，禁止字符串字段名。
- 旧 explore 模块需删除；旧 `City.bannerSortOrder` 需移除；admin/app 双侧涉及的 city
  字段需同步更新。
- App 端鉴权遵循 `X-API-Key`（用户记忆 `project_app_auth_api_key`）。

**Scale/Scope**:
- 数据规模：CITY banner 预估 < 1k 条，未来扩展类型后 < 10k；图片每条 1–5 张。
- 代码改动面：admin 后端新增 `banner` 模块（controller/service/repo/entity/dto/spec/listener）+
  改 `city` 模块；app 后端删除 `explore` 模块新增 `banner` 模块；web 新增 `pages/Banners`
  与 API 客户端、改 `pages/Cities`；Liquibase 新增 changelog（建表 + 字段迁移 + 删除旧字段）。

## Constitution Check

逐条对照 `/.specify/memory/constitution.md` v1.1.0：

| # | 原则 | 本特性遵循方式 | 状态 |
|---|------|----------------|------|
| I | 中文 JavaDoc | 新增 Banner 实体/DTO/Controller/Service/Listener 的所有字段与方法 JavaDoc 中文撰写；CityService 新增的事件发布逻辑同步补 JavaDoc | ✅ |
| II | UUIDv7 + 无外键 | `Banner.id` UUIDv7；`linkedEntityId` 仅存 UUID 值，不建 FK；Liquibase changelog 不写 `REFERENCES` | ✅ |
| III | 命名不缩写 | 字段 `imageUrls`（不是 `imgs`）、`linkedEntityId`（替代用户输入的 `link`，更具语义；JSON 仍序列化为 `link` 以贴近规格契约），事件名 `CityOnlineChangedEvent` 保持用户输入 | ⚠ 见 Complexity Tracking |
| IV | 双后端隔离 | admin 与 app 各自维护独立 `Banner` 实体/DTO，不共享包 | ✅ |
| V | 测试 & 本地可运行 | service 层单测 + controller MockMvc + 前端 `tsc -b` 通过 | ✅ |
| VI | JPA Metamodel | admin `BannerSpecification` 使用 `Banner_.name`/`Banner_.online`/`Banner_.type` 等；新增 hibernate-jpamodelgen 注解处理器到两个 `pom.xml` | ✅ |

技术与工具栈约束：包根不变；Liquibase formatted SQL；UUIDv7；无外键。✅

**初步评估结论**：除字段命名一项的轻度澄清外，无违反；可进入 Phase 0。

## Project Structure

### Documentation (this feature)

```text
specs/002-banner-module/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── admin-banner-api.md
│   └── app-banner-api.md
├── checklists/
│   └── requirements.md
└── spec.md
```

### Source Code (repository root)

```text
love-space-admin/
├── pom.xml                                          # 改：新增 hibernate-jpamodelgen
└── src/main/
    ├── java/com/loves/space/modules/
    │   ├── banner/                                  # 新增
    │   │   ├── controller/BannerController.java
    │   │   ├── service/BannerService.java
    │   │   ├── repository/BannerRepository.java
    │   │   ├── repository/BannerSpecifications.java # metamodel-only
    │   │   ├── entity/Banner.java
    │   │   ├── entity/BannerType.java               # enum CITY
    │   │   ├── dto/BannerCreateRequest.java
    │   │   ├── dto/BannerUpdateRequest.java
    │   │   ├── dto/BannerListItemResponse.java
    │   │   ├── dto/BannerDetailResponse.java
    │   │   ├── dto/BannerOnlineRequest.java
    │   │   └── event/BannerEventListener.java       # @TransactionalEventListener(AFTER_COMMIT)
    │   └── city/
    │       ├── entity/City.java                     # 改：删 bannerSortOrder
    │       ├── service/CityService.java             # 改：发布 CityOnlineChangedEvent；删 setBannerSort
    │       ├── event/CityOnlineChangedEvent.java    # 新增（位于 city 模块下，banner 监听）
    │       ├── controller/CityController.java       # 改：删 banner-sort endpoint
    │       └── dto/                                 # 改：删 bannerSortOrder 字段
    └── resources/db/changelog/changes/
        ├── 003-create-loves-banner.sql              # 新增
        └── 004-drop-city-banner-sort-order.sql      # 新增

love-space-app/
├── pom.xml                                          # 改：新增 hibernate-jpamodelgen
└── src/main/java/com/space/app/modules/
    ├── banner/                                      # 新增
    │   ├── controller/BannerController.java         # GET /api/app/banners
    │   ├── service/BannerQueryService.java
    │   ├── repository/BannerRepository.java
    │   ├── entity/Banner.java
    │   ├── entity/BannerType.java
    │   └── dto/BannerItemResponse.java              # { id, name, type, image[], data{} }
    ├── city/                                        # 改：删 bannerSortOrder 映射
    └── explore/                                     # 删除整模块
        ├── controller/
        ├── service/
        └── dto/

love-space-web/src/
├── pages/Banners/                                   # 新增
│   ├── BannerList.tsx
│   ├── BannerForm.tsx                               # 新增/编辑共用；编辑无 online 开关
│   └── components/CitySelect.tsx                    # 可搜索下拉，仅 online 城市
├── pages/Cities/                                    # 改：删 bannerSortOrder 表单字段
├── services/banners.ts                              # API 客户端
├── services/cities.ts                               # 改
├── layout/AppSidebar.tsx 或 routes                  # 改：新增 Banner 菜单项
└── App.tsx                                          # 改：新增 /banners 路由
```

**Structure Decision**: 沿用既有多项目布局；本特性新增一个 admin 后端模块、一个 app 后端模块、
一组前端页面，并对 city 模块做"瘦身式"修改。Liquibase changelog 用两个独立 SQL 文件分别承担
建表与字段下线，方便回滚。

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|--------------------------------------|
| 实体字段名 `linkedEntityId` 与 spec 中沿用用户原词 `link` 不一致（宪法 III 命名清晰） | 用户输入用 `link` 是为了对接前端契约直觉；但 `link` 在 Java 字段语义上模糊，可能被读者误解为 URL；选 `linkedEntityId` 增加可读性，并通过 `@JsonProperty("link")` 在 admin API JSON 层保留 `link` 名以贴近原始规格 | 直接命名 `link` 字段：会触发"命名不缩写/不模糊"原则的灰区；评审会反复争议，长期维护成本更高 |

无其他违反；除上一项语义化命名外不引入额外复杂度。
