# love-space-admin 开发规范手册

> 运营管理后台**后端**（Spring Boot 4.0.6 / Java 25 / Maven / PostgreSQL）。根包 `com.loves.space`，主类 `LoveSpaceAdminApplication`。
> 本手册只写「怎么写这一端的代码」。业务规则以 `openspec/specs/` 为准，接口契约以 `contracts/api-spec.json` 为准，二者是真源，本文不复制。

## 1. 命令

```bash
cd love-space-admin
./mvnw spring-boot:run                  # 本地起服务（默认 8080）
./mvnw test                             # UT
./mvnw -Dtest='*IT' test                # IT，必须显式指定，默认不跑
./mvnw -Dtest=ClassName#method test
./mvnw package                          # target/*.jar
```

- 跑测试必须显式传库地址，默认的 5432/love_space 是坏库：
  `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:25432/love_space SPRING_DATASOURCE_USERNAME=iris SPRING_DATASOURCE_PASSWORD=iris ./mvnw test`
- 本机 `~/.bashrc` 里的 `SERVER_PORT` / `SPRING_DATASOURCE_URL` 属于别的项目，起服务前 `env -u SERVER_PORT -u SPRING_DATASOURCE_URL ...`。
- admin 与 app 的 IT **不要并行跑**（共享 Testcontainers reuse 容器会撞库）。

## 2. 包结构（package-by-feature）

```
com.loves.space
├── common/        annotation(@OperationLog) / dto(ImageResponse, OnlineStatusRequest)
│                  entity(BaseAuditEntity) / enums / exception(GlobalExceptionHandler)
│                  page(PageQuery, PageResponseMapper) / util
├── config/        WebMvcConfig, JpaConfig, SecurityConfig, AsyncConfig, properties/
├── security/      jwt/, userdetails/, handler/, OperatingContext
├── infrastructure/ storage/(OSS 签名、objectKey 校验) log/(OperationLogAspect)
└── modules/<feature>/{controller,service,repository,entity,dto,event}
```

新模块一律照此建目录，测试目录镜像主代码结构。

**跨模块只走 service，禁止跨模块 import 别人的 repository。** 需要解耦的级联（删除/下架影响别的模块）走 `ApplicationEventPublisher` + `modules/<feature>/event/XxxDeletedEvent`，参考 `city` → `CityDeletedEvent` / `CityOnlineChangedEvent`。

## 3. Controller

```java
@RestController
@RequestMapping("/api/admin/cities")          // 路径前缀恒为 /api/admin/**，资源名用复数
public class CityController {
    private final CityService cityService;
    public CityController(CityService cityService) { this.cityService = cityService; }   // 构造器注入，不用 @Autowired

    @GetMapping("page")                        // 分页列表：返回 PageResponse<T>
    public PageResponse<CityItemResponse> page(@RequestParam(required = false) Boolean online, Pageable pageable) { ... }

    @GetMapping                                // 全量列表：返回 List<T>（下拉框等场景）
    public List<CityItemResponse> list(...) { ... }

    @PostMapping
    @OperationLog("city:create")               // 所有写操作都要打，值形如 "<module>:<action>"
    public CityDetailResponse create(@Valid @RequestBody CityCreateRequest request) { ... }

    @PutMapping("/{id}/online")                // 状态切换用子路径 + OnlineStatusRequest
    @OperationLog("city:set-online")
    public CityDetailResponse setOnline(@PathVariable UUID id, @Valid @RequestBody OnlineStatusRequest request) { ... }
}
```

- Controller 不写业务逻辑，只做参数装配与转发。
- 每个方法要有中文 javadoc；`@param` 说明可空性。
- 主键类型一律 `UUID`。

## 4. DTO

全部用 `record`，命名固定：

| 用途 | 命名 |
|---|---|
| 创建 / 更新入参 | `XxxCreateRequest` / `XxxUpdateRequest` |
| 查询条件 | `XxxQuery` |
| 列表项出参 | `XxxItemResponse` |
| 详情出参 | `XxxDetailResponse` |

- 响应 record 自带静态工厂 `public static XxxItemResponse from(Xxx entity, ImageUrlSigner signer)`，转换逻辑不散落在 service。
- 校验注解的 `message` **写中文**（会被 `GlobalExceptionHandler` 原样返回给前端）：
  `@NotBlank(message = "中文名不能为空") @Size(max = 50, message = "中文名长度不能超过 50 个字符")`
- 字段名用完整英文单词，**禁止缩写**：✅ `safetyEnvironmentScore / longitude / backgroundImage`，❌ `scoreS / lng / bgImage`。

## 5. Entity 与数据库

```java
@Entity
@Table(name = "loves_city", uniqueConstraints = @UniqueConstraint(name = "ux_loves_city_chinese_name", columnNames = "chinese_name"))
@Getter @Setter
public class City extends BaseAuditEntity { ... }
```

- 一律 `extends BaseAuditEntity`：UUIDv7 主键（`@PrePersist` 生成）+ `createdAt` / `updatedAt`（JPA Auditing）。**不带 createdBy / updatedBy**。
- 表名加 `loves_` 前缀；列名 snake_case；类名不带 `Entity` 后缀。
- **不建外键约束**，关联靠应用层维护。
- 迁移用 Liquibase formatted SQL：新增 `src/main/resources/db/changelog/changes/NNN-描述.sql`（序号递增），master `db.changelog-master.yaml` 只做 include，不写变更。Liquibase 版本跟随 Spring Boot 默认，不在 pom 里 pin。

## 6. Repository

```java
public interface CityRepository extends JpaRepository<City, UUID>, JpaSpecificationExecutor<City> {
    boolean existsByChineseName(String chineseName);
    boolean existsByChineseNameAndIdNot(String chineseName, UUID id);   // 更新时的唯一性校验
}
```

动态过滤用 `JpaSpecificationExecutor` + `Specification`（在 service 里拼 `Predicate`），字段引用优先用 JPA 静态元模型 `City_.CHINESE_NAME` 而不是字符串。

## 7. Service

- `@Service @Transactional`，构造器注入。
- 业务校验失败抛 `IllegalArgumentException("城市中文名已存在：" + name)` → 由 `GlobalExceptionHandler` 转 400，message 直出给前端，所以**消息写中文、面向用户**。
- 找不到资源抛对应异常返回 404，不要返回 null。
- 分页：`PageQuery.normalize(pageable, sort)` 校正后再查，`PageResponseMapper.map(page, x -> XxxItemResponse.from(x, signer))` 出参。

## 8. 异常与响应约定

- 成功响应**不包装**：直接返回 DTO / `PageResponse<T>` / `List<T>` / `void`。
- 错误响应由 `common/exception/GlobalExceptionHandler` 统一产出 `{status, error, message, path}`。
  **不要再加第二个 `@RestControllerAdvice`**，要改错误行为就改这个类。
- 常见映射：`MethodArgumentNotValidException`/`IllegalArgumentException` → 400（中文 message）；`AuthenticationException` → 401；`AccessDeniedException` → 403；兜底 `Exception` → 500 且不泄漏堆栈。

## 9. 分页

- 前端传的 `page` 是 **1 基**（`WebMvcConfig#pageableResolverCustomizer` 已设 `setOneIndexedParameters(true)`）。
- `size` 白名单 **20 / 30**，其他值一律按 20 处理（`PageQuery.DEFAULT_SIZE` / `ALT_SIZE`）。
- 出参结构 `PageResponse{content, page(1 基), size, totalElements, totalPages}`，与前端 `Page<T>` 契约一一对应。

## 10. 图片 / 对象存储

- 入库存的是 **OSS objectKey**，不是完整 URL；请求 DTO 用正则约束：
  `@Pattern(regexp = "^(images|bound)/[\\w-]+\\.(png|jpg|webp)$")`，并在 service 里过 `ObjectKeyValidator.validateAndBind`。
- 出参转成 `ImageResponse{id, url}`：`ImageResponses.from(objectKey, imageUrlSigner)`，url 是带签名的临时访问地址。
- 前端直传：`POST /api/admin/files/upload-credentials` 下发 PostObject 表单签名，浏览器直传 OSS，服务端不中转文件、不下发 AccessKeySecret。

## 11. 安全

- Spring Security + JWT（`security/jwt`），密码 BCrypt。
- 规则见 `SecurityConfig`：`/api/admin/auth/login` permitAll；`/api/admin/managers/**` 需 `ROLE_ADMIN`；其余 `/api/admin/**` 需登录。
- 取当前登录人统一用 `OperatingContext`（类名固定，不要改成 `CurrentUserHolder` 之类）。
- 运营账号叫 **Manager**：实体 `Manager`、表 `loves_manager`、路径 `/api/admin/managers`、登录响应顶层字段 `manager`。**不要用 `user` 命名运营账号。**

## 12. 操作日志

写接口加 `@OperationLog("<module>:<action>")`，由 `OperationLogAspect` 异步落库，查询走 `/api/admin/logs`。新增写接口时别漏（`operation-log` 域的回归用例会查这个）。

## 13. 交付前

- 改了接口 → 同步 `contracts/api-spec.json`；行为有变化 → 必须走 OpenSpec change 流程（见 `.claude/rules/openspec-session-protocol.md`）。
- 跑 IT：`/run-api-test --change <id>`；测试用例落 `tests/{domain}/it.md`，域先在 `tests/modules.md` 登记。
- 前端要跟着改的部分见 [web-开发规范.md](web-开发规范.md)；app 端同实体的读接口见 [app-开发规范.md](app-开发规范.md)。
