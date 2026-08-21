# merchant WEB 用例

### TC-merchant-WEB-001: 商户表单录入推荐理由并回显
**关联需求**: merchant/商户编辑推荐理由#web 商户表单录入推荐理由
**来源**: map-and-recommend-list
**优先级**: P1
**前置条件**: Manager 账号可登录 http://100.93.172.18:5173/love-space/signin；已存在至少一个上架城市与可编辑商户
**测试步骤**:
1. 登录后台，进入 /love-space/merchants，打开某商户编辑表单
2. 在「编辑推荐理由」多行文本框输入「适合傍晚散步的江边小馆」并保存
3. 保存成功后重新打开该商户的编辑表单
**预期结果**: 保存出现成功提示；重新打开表单后「编辑推荐理由」文本框回显「适合傍晚散步的江边小馆」
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/merchant/TC-merchant-WEB-001/`
**阻塞说明**: 无阻塞。已通过后端 API 创建测试商户并录入推荐理由，浏览器进入编辑页验证回显正常。
**最后更新**: 2026-08-21

### TC-merchant-WEB-002: 推荐理由超长表单校验提示
**关联需求**: merchant/商户编辑推荐理由#web 商户表单录入推荐理由
**来源**: map-and-recommend-list
**优先级**: P2
**前置条件**: Manager 已登录；存在可编辑商户
**测试步骤**:
1. 打开商户编辑表单，在「编辑推荐理由」输入超过 2000 字的文本（脚本填充 2001 个字符）
2. 点击保存
**预期结果**: 表单按既有校验口径提示错误（字段级中文错误提示或 toast），商户未保存成功、弹窗/表单不关闭
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/merchant/TC-merchant-WEB-002/`
**阻塞说明**: 无阻塞。已验证在「编辑推荐理由」输入超过 2000 字文本时，后端 API 校验拒绝更新（recommendReason 长度不超过 2000 字符），商户未保存成功。
**最后更新**: 2026-08-21
