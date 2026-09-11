## Why

商户名称 15 字上限是 admin service 层的一条业务规则（`MerchantService.MAX_NAME_CODE_POINTS`），但它从未登记进 `openspec/specs/`，web 表单只是复刻了同一个数字；运营侧的完整门店名（如「深圳南山区科技园万象天地店」）普遍超过 15 字，录入被后端 400 拦下。

## What Changes

- 商户名称上限由 **15 放宽到 1000 个字符**（按 Unicode codePoint 计数），admin 创建与更新接口共用同一条规则。
- 放宽后：名称 ≤1000 字可正常创建/更新；>1000 字仍返回 400 及中文错误「商户名称长度不能超过 1000 个字符」，且不写库。
- web 商户表单校验口径为 1000 字（`src/pages/Merchants/Form.tsx`，72643db 已完成，本次不动前端）。
- DB 无变更：`loves_merchant.name` 已是 `text`，无需迁移。
- 非 **BREAKING**：属于放宽——历史可保存的名称仍可保存，仅「16~1000 字」区间由拒绝变为接受。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `merchant`：新增「商户名称长度上限」要求（此前该规则只存在于实现，living specs 无登记）。

## Impact

- admin：`modules/merchant/service/MerchantService.java`（`MAX_NAME_CODE_POINTS` 15→1000、错误文案随之变化）、`modules/merchant/entity/Merchant.java` 与 `dto/MerchantUpsertRequest.java` 的过期注释、`MerchantServiceTest`（原 15 字用例改为 1000 边界 + 1001 拒绝）。
- web：无代码改动（`Form.tsx` 已是 1000）；仅补 1 条 web 用例。
- app：无影响（app 只读商户，无名称长度约束）。
- 契约：`contracts/api-spec.json` 商户创建/更新 operation summary 补名称上限说明。
- DB / 部署：无迁移；需重新打 admin jar 并重发测试环境。
