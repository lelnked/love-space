# Tasks

## 1. admin 后端
- [x] 1.1 `MerchantService.MAX_NAME_CODE_POINTS` 15 → 1000，注释注明与 web 表单口径一致
- [x] 1.2 同步 `Merchant` 实体与 `MerchantUpsertRequest` 里「≤ 15 个字符」的过期注释
- [x] 1.3 UT：`MerchantServiceTest` 原 15 字用例改为「1000 字边界通过」+「1001 字拒绝」两条，带 `@scenario`

## 2. web 前端
- [x] 2.1 确认表单校验口径已是 1000（72643db：`src/pages/Merchants/Form.tsx:141`），本次无须改动

## 3. 契约
- [x] 3.1 `contracts/api-spec.json`：商户创建/更新 operation summary 补「名称 ≤1000 字」

## 4. 验证与交付
- [x] 4.1 单测未跑（用户明确跳过测试）；以编译 + 部署后真机 API 实测为准
- [x] 4.2 重打 admin jar 并部署测试环境：jar md5 `396bc8fbb5efd95e2c9b708d00e0960e`，镜像 tag `love-space-admin:20260911-name1000`（2026-09-11 12:35 起容器 Up）
- [x] 4.3 测试环境实测（真机 PUT /api/admin/merchants/{id}，验完已恢复原名）：
  - 1001 字 → 400「商户名称长度不能超过 1000 个字符」✅
  - 16 字 → 200，回存名称 16 字 ✅（旧 15 上限下必定 400，即本 change 的核心场景）
  - 1000 字 → 200，回存长度 1000 ✅
  - 既有商户（深圳商户2）超长提交后名称未变 ✅
