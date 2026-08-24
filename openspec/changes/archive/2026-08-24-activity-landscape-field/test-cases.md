## IT

- **TC-activity-IT-020**（P1）活动景观字段贯通 admin 写入与 admin/app 查询
  - 关联 Scenario：`activity/活动管理#景观字段可写可改可空`、`activity/App 端活动查询#活动详情返回景观`
  - 落位：`tests/activity/it.md`

## WEB

- **TC-activity-WEB-003**（P2）活动表单填写景观并回显
  - 关联 Scenario：`activity/web 端活动管理页面#活动表单填写景观并回显`
  - 落位：`tests/activity/web.md`

## 不覆盖

- 景观字段为空/超长的边界：字段无校验规则（与 transportation/visa 同口径），无独立断言价值。
- app 端活动**列表**不含 landscape：属于「未新增」的既有行为，由既有列表用例的字段断言兜底。
