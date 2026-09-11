# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/merchant/{it,web}.md`（living 文件，runner 独占回写状态）。

## 新增用例

- TC-merchant-IT-010: 名称 1000 字边界通过（ADDED: merchant/商户名称长度上限#名称 1000 字边界通过）
- TC-merchant-IT-011: 名称 1001 字被拒绝（ADDED: #名称超过 1000 字被拒绝）
- TC-merchant-IT-012: 名称 16~1000 字可保存（ADDED: #名称 16~1000 字可保存（原 15 字上限放开））
- TC-merchant-IT-013: 名称超长时既有数据不变（ADDED: #名称超长时既有商户数据保持不变）
- TC-merchant-WEB-003: 商户表单名称 16~1000 字可保存回显、超 1000 字表单拦截（ADDED: #web 表单同口径校验）

## 修改用例

（无）

## 需重测用例

- TC-merchant-IT-001: 创建商户（名称校验改动后确认创建路径未回归）
- TC-merchant-IT-002: 更新商户（同上，走同一 upsert）
- TC-merchant-WEB-001: 商户表单录入并回显（名称字段改动后确认表单未回归）

## 执行汇总

- IT：未跑 api-test-runner。改用真机直连测试环境 admin（`http://47.109.27.132:8080`）手工验证，2026-09-11：
  - `PUT /api/admin/merchants/01a04090-…` name=1001 字 → 400「商户名称长度不能超过 1000 个字符」（TC-merchant-IT-011/013 ✅）
  - 同接口 name=16 字 → 200、回存 16 字（TC-merchant-IT-012 ✅）；name=1000 字 → 200、回存长度 1000（TC-merchant-IT-010 ✅）
  - 验完已把该商户名称恢复为原值「深圳商户2」
- 单测：`MerchantServiceTest` 已按新规则改写为 3 条覆盖全部 4 个 admin 场景（1000 边界 / 16~1000 放开 / 1001 拒绝 + 数据不变），本会话按用户要求未执行
- WEB：TC-merchant-WEB-003 未执行，由用户在测试环境自测
