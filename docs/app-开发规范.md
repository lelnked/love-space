# love-space-app 开发规范手册

> 移动端 App **后端**（Spring Boot 4.0.6 / Java 25 / Maven / PostgreSQL）。根包 `com.space.app`，主类 `LoveSpaceAppApplication`，默认端口 8081。
> 定位：**面向客户端的只读 API**。写操作、运营维护全部在 admin 端，不要在这里开写接口。
> 业务规则以 `openspec/specs/` 为准，接口契约以 `contracts/api-spec.json` + `love-space-app/docs/openapi.json` 为准。

## 1. 命令

```bash
cd love-space-app
./mvnw spring-boot:run
./mvnw test                              # 必须带 API key 环境变量，见下
./mvnw -Dtest='*IT' test                 # IT 必须显式指定，默认不跑
```

- **跑测试必须带 `APP_SECURITY_API_KEYS=<TEST_API_KEY 的 SHA-256 hash>`**，否则全量 `Failed to load ApplicationContext`；传字面量 `TEST_API_KEY` 会全量 401。
- 测试库是独占的 `love_space_app_test`（`create-drop`），别指到业务库上，会被清空。
- admin 与 app 的 IT **不要并行跑**。
- 本机 `~/.bashrc` 的 `SERVER_PORT` / `SPRING_DATASOURCE_URL` 属于别的项目，起服务前 `env -u`。

## 2. 包结构

```
com.space.app
├── common/        dto(ImageResponse) / entity(BaseAuditEntity) / enums(Period)
│                  exception(GlobalExceptionHandler, ResourceNotFoundException)
│                  page(PageQuery, PageResponse) / util
├── config/        WebMvcConfig, JpaConfig, SecurityConfig, properties/ApiKeyProperties
├── security/      ApiKeyAuthFilter
├── infrastructure/storage/      OSS 签名（只读，出图用）
└── modules/<feature>/{controller,service,repository,entity,dto}
```

- 与 admin **刻意不共享代码**：实体、枚举、工具在两端各写一份。体量小，复制比维护父 POM 划算（共享实体超过 5 个再讨论抽 `love-space-core`）。
- app 端模块通常没有 `event/`，也没有 `@OperationLog`——**app 不写业务操作日志**。

## 3. Controller

```java
@RestController
@RequestMapping("/api/app/cities")        // 路径前缀恒为 /api/app/**
public class CityController {
    private final CityService cityService;
    public CityController(CityService cityService) { this.cityService = cityService; }

    /** 获取所有上架城市。 */
    @GetMapping
    public List<CityItemResponse> list() { return cityService.listOnline(); }
}
```

- **只有 GET**（除非 spec 明确要求，例如埋点上报）。
- **只返回上架/已发布数据**：未上架、已下架的资源在列表里不出现，按 id 直取时返回 **404**（不是 403、不是空对象）。
- 类与方法的中文 javadoc 是硬要求，见第 7 节（openapi.json 从 javadoc 生成）。

## 4. 排序口径（强约束）

凡返回 list 的接口，只要实体带排序号字段，排序一律「排序号 + `createdAt DESC`」：

- `sortOrder` **升序**（第几位，小的靠前）
- `weight` **降序**（权重，大的靠前）
- 同序号一律 `createdAt` **倒序**（新的在前）

```java
Sort.by(Sort.Order.asc(Banner_.SORT_ORDER), Sort.Order.desc(Banner_.CREATED_AT));
Sort.by(Sort.Order.desc("weight"), Sort.Order.desc("createdAt"));
// 原生 SQL 同理：order by m.weight desc, m.created_at desc
```

**tie-break 不可省略**——否则同序号的顺序由 DB 返回顺序决定，App 一刷新就漂。实体没有排序号字段的列表不受此约束，沿用各自口径。

## 5. DTO / 分页

- DTO 全部 `record`，出参命名 `XxxItemResponse` / `XxxDetailResponse`，静态工厂 `from(entity, ...)`。
- 分页出参用 `common/page/PageResponse.of(page)`，结构 `{content, page(1 基), size, totalElements, totalPages}`，与 admin 端一致。
- 字段名不缩写，与 admin 端同名字段保持同名（前端/客户端按同一套字典对接）。

## 6. 数据库

- **app 端不做 schema 迁移**：表结构由 admin 的 Liquibase 建。这里的实体只映射已有表，改表结构去 admin 加 changelog。
- 实体同样 `extends BaseAuditEntity`（UUIDv7 主键 + createdAt/updatedAt），表名 `loves_` 前缀。
- 只读场景优先 `@Transactional(readOnly = true)`。

## 7. OpenAPI 文档（必做）

`love-space-app/docs/openapi.json` 由脚本从 **Java 源码 + javadoc** 生成，不依赖 springdoc：

```bash
node scripts/generate-app-openapi.js            # 生成
node scripts/generate-app-openapi.js --check    # 只校验不落盘
```

所以：**改了 controller / DTO record / 枚举后必须重跑生成脚本并提交 openapi.json**。
生成器的信息来源就是 javadoc，因此：

- controller 类与方法要有 javadoc（方法 javadoc 第一句 = 接口摘要）
- 方法 javadoc 的 `@param` = 参数说明
- record 的 `@param` = 字段说明；enum 常量的 javadoc = 枚举说明
- 缺说明的字段会在脚本末尾汇总打印，**不要留着不管**

## 8. 安全：API Key

- 所有 `/api/app/**` 经 `ApiKeyAuthFilter`：读请求头 `X-API-Key`，与 `APP_SECURITY_API_KEYS` 白名单做**常量时间比较**。
- 失败一律 401 ProblemDetail，**不区分「缺失」与「不匹配」**，避免信息泄露。
- 日志里**严禁**出现 key 明文或完整摘要，只记 SHA-256 前 6 位脱敏指纹。
- 白名单存的是 hash，配置走环境变量（部署见 `deploy/.env.<环境>`），不写进代码或配置文件。
- MVP 无账号体系，没有用户态；需要个性化时先走 OpenSpec change 讨论。

## 9. 异常

同 admin：成功不包装，错误由 `common/exception/GlobalExceptionHandler` 统一处理；资源不存在抛 `ResourceNotFoundException` → 404。不要新增第二个 advice。

## 10. 交付前

- 改接口 → 同步 `contracts/api-spec.json` + 重跑 `generate-app-openapi.js`。
- 行为有变化 → 走 OpenSpec change（见 `.claude/rules/openspec-session-protocol.md`）。
- 跑 IT：`/run-api-test --change <id>`（app 后端 baseUrl `http://localhost:8081`，请求带 `X-API-Key`）。
- 同一实体的运营侧写接口见 [admin-开发规范.md](admin-开发规范.md)。
