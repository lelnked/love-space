## 1. admin：数据库与接口

- [x] 1.1 Liquibase `028-create-ai-limit.sql`：建 `loves_ai_limit`（limit_key PK、name、limit_value CHECK、updated_by、updated_at）+ 插入 `PER_ANON_DAILY`/50、`GLOBAL_DAILY`/5000 + rollback；master include
- [x] 1.2 `modules/ai/entity/AiLimit` + `AiLimitRepository`（`JpaRepository<AiLimit, String>`）
- [x] 1.3 DTO：`AiLimitResponse(key, name, value, updatedBy, updatedAt)`（`from(entity)`）、`AiLimitUpdateRequest(BigDecimal value)`（`@NotNull @Digits(fraction=0) @Min(-1) @Max(Integer.MAX_VALUE)`），中文 message「取值须为 -1 或正整数」
- [x] 1.4 `AiLimitService`：`list()` 按 key 升序；`update(key, request)`：404、value=0 → 400、写 updatedBy/updatedAt，只改该行
- [x] 1.5 `AiLimitController`：`GET /api/admin/ai/limits`、`PUT /api/admin/ai/limits/{key}` + `@OperationLog("ai-limit:update")`，中文 javadoc
- [x] 1.6 `OperationLogAspect.extractTarget`：无 UUID 时取第一个 String 入参
- [x] 1.7 UT `AiLimitTest`（`@scenario ai/生成额度配置#初始数据`、`#修改单项只记录该项修改人`、`#非法取值被拒绝`、`#不存在的限制`；`ai/AI 管理权限与菜单#MEMBER 可修改限流配置`），测试后恢复两行初始值
- [x] 1.8 UT `AiOperationLogTest` 补 `@scenario ai/AI 管理写操作留痕#修改限流配置留痕`、`operation-log/运营写操作留痕#AI 限流配置修改留痕`、`operation-log/留痕字段取值与敏感信息脱敏#字符串路径参数作为 target`

## 2. app：额度改读表

- [x] 2.1 `modules/ai/entity/AiLimit`（只读映射）+ `AiLimitRepository`
- [x] 2.2 `AiGenerationService.checkQuota`：读两行，缺行抛 `IllegalStateException`（500），-1 跳过该项且不计数
- [x] 2.3 删除 `AiLimitProperties`、`AiGenerationConfig` 中的 `@EnableConfigurationProperties`、`application.yml` 的 `love-space.ai-limit` 段
- [x] 2.4 测试：`AiGenerationTestSupport` 提供 `setLimits(perAnonDaily, globalDaily)`；`AiGenerationTest` / `AiGenerationGlobalLimitTest` 去掉 `@TestPropertySource`，`@BeforeEach` 写表；更新原额度用例 `@scenario`（`ai/生成接口每日额度#匿名 ID 超出每日次数`、`#全站超出每日总量`、`#昨日记录不计入`、`#去重不占额度`）
- [x] 2.5 UT 新增 `@scenario ai/生成接口每日额度#-1 不限制`、`#修改即时生效`、`#配置行缺失`

## 3. 部署与契约

- [x] 3.1 `deploy/deploy-apps.sh`、`deploy/.env.example` 删除 `AI_LIMIT_PER_ANON_DAILY` / `AI_LIMIT_GLOBAL_DAILY`
- [x] 3.2 `contracts/api-spec.json` 新增两个接口 + `AiLimit` / `AiLimitUpdateRequest` schema，`x-requirement: ai/生成额度配置`

## 4. web：限流配置页

- [x] 4.1 `src/api/ai.ts`：`AiLimit` 类型、`listAiLimits()`、`updateAiLimit(key, value)`
- [x] 4.2 `src/pages/AiLimits/List.tsx`：列表（名称 / 上限 / 最后修改人 / 修改时间 / 操作），② -1 显示「不限制」否则「N 次」，修改人/时间为空显示「—」；loading 走 DataTable，加载失败 toast；底部提示行
- [x] 4.3 `src/pages/AiLimits/LimitEditModal.tsx`：① 打开回填，③ 输入校验 `^(-1|[1-9]\d*)$` 非法提示「取值须为 -1 或正整数」，④ 非法/提交中置灰，成功 toast「已保存，立即生效」+ 关闭 + 刷新，失败 toast detail
- [x] 4.4 `App.tsx` 注册 `/ai/limits`；`AppSidebar.tsx`「AI 管理」末尾加「限流配置」
- [x] 4.5 `npm run build` 通过

## 5. 验证

- [x] 5.1 admin `./mvnw test` 全绿
- [x] 5.2 app `./mvnw test` 全绿
