# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单。**本轮不执行任何用例**（用户 2026-08-21 明确），
> 清单作为后续回归轮的执行范围。用例本体在 `tests/{auth,manager,banner,operation-log}/{it,web}.md`
> 与 `tests/file/it.md`。五个域均为全新域，TC 编号从各域 001 起，不与既有域冲突。

## 新增用例

五个域全部为新建，无既有用例可修改，无用例需删除。规划编号区间如下（apply 阶段按 Scenario 逐条落编号）：

### IT

| 域 | 编号区间 | 覆盖 Requirement | 条数 |
|---|---|---|---:|
| auth | `TC-auth-IT-001` ~ `TC-auth-IT-010` | 运营账号登录（4 Scenario）、JWT 会话与授权链（3）、当前登录人查询与登出（2） | ~10 |
| manager | `TC-manager-IT-001` ~ `TC-manager-IT-011` | 运营账号管理（4）、账号启停与内置管理员保护（3）、运营账号分页查询（4） | ~11 |
| banner | `TC-banner-IT-001` ~ `TC-banner-IT-014` | Banner 管理（4）、上架前置校验（3）、城市状态变更级联（3）、App 端查询（4） | ~14 |
| operation-log | `TC-operation-log-IT-001` ~ `TC-operation-log-IT-012` | 运营写操作留痕（3）、留痕字段取值与脱敏（4）、操作日志查询（5） | ~12 |
| file | `TC-file-IT-001` ~ `TC-file-IT-009` | 图片上传凭证签发（3，其中成功分支标前置不满足）、objectKey 生命周期与绑定校验（4）、签名访问地址（3）、自动化覆盖边界（2） | ~9 |

### WEB

| 域 | 编号区间 | 覆盖 Requirement | 条数 |
|---|---|---|---:|
| auth | `TC-auth-WEB-001` ~ `TC-auth-WEB-004` | web 端登录页与路由守卫（4 Scenario） | 4 |
| manager | `TC-manager-WEB-001` ~ `TC-manager-WEB-004` | web 端管理员管理页面（4） | 4 |
| banner | `TC-banner-WEB-001` ~ `TC-banner-WEB-004` | web 端 Banner 管理页面（4） | 4 |
| operation-log | `TC-operation-log-WEB-001` ~ `TC-operation-log-WEB-004` | web 端操作日志页面（4） | 4 |

`file` 域**不产 WEB 用例**（见 design D2）：上传组件无独立路由页，真实上传字节流不可达，壳层交互断言归各业务域的 web 用例。

预计合计：IT 约 56 条、WEB 16 条。

四象限覆盖要点：

- **happy**：登录成功、创建账号、Banner 创建与上架、日志按条件查得、图片绑定改写为 `bound/`
- **boundary**：页大小非白名单值校正（manager / operation-log）、日志时间区间含边界、Banner 排序号同值并列、已绑定图片重复提交幂等
- **error**：登录三种失败合并口径、用户名重复、密码长度不足、Banner 名称重复、上架前置校验两条、非法 objectKey、非图片 contentType、无 token 401、角色不足 403
- **state**：账号启停往复、Banner 上下架往复、城市上下架双向级联、城市删除只下架不删 Banner、业务回滚后源图可重试

## 修改用例

（无。五个域此前无任何用例。）

## 删除用例

（无。）

## 需重测用例

（无。本 change 不改生产代码，既有 7 个域的行为不受影响，无需回归。）

## 本轮不执行说明

用户 2026-08-21 明确「不需要进行测试」。因此：

- 全部新建用例状态为 `⬜ 未测试`，执行存证字段留空
- 不调起 `api-test-runner` / `web-test-runner`
- 不跑 `.quality-gate.yml` 的测试项
- 仅执行 `./mvnw -q -DskipTests compile` 确认 `@scenario` 注释回填未破坏编译

首次执行本清单时的已知前置条件：

1. **WEB 用例**（16 条）需远程 Playwright `100.103.199.95:9233` 恢复。该服务在 2026-08-20 至 21 期间持续不可达，另有 8 条既有用例（featured 5 + route 1 + city 2）同样积压。
2. **`TC-file-IT-*` 中上传凭证成功分支**需给 `StsCredentialIssuer` 加 test-profile 桩（本轮不做，见 proposal）。在此之前该条标注前置条件不满足，不判失败。
3. **`operation-log` 的留痕用例**依赖异步落库，执行时需轮询等待（既有 `OperationLogAspectIT` 的做法是 20×100ms）。

## 执行汇总

<!-- 本轮不执行。后续回归轮由编排 skill 填写：总数 / ✅ / ❌ / 未执行 -->
