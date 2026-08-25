# TC-recommend-list-IT-012 断言明细

执行日期: 2026-08-25 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get

结果: ✅ 通过（11/11 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | Content-Type application/json | ✅ | `application/json` |
| 3 | 清单字段 title/introduction/sortOrder | ✅ | `["详情清单140600","含商户明细",2]` |
| 4 | merchants 为数组 | ✅ | |
| 5 | merchants 顺序 甲→乙(清单保存顺序，与 weight 10<30 无关) | ✅ | `["商户甲140600","商户乙140600"]` |
| 6 | 下架商户 丙 不出现 | ✅ | |
| 7 | merchants[0] 恰好四个 key id/name/address/logo | ✅ | `["id","name","address","logo"]` |
| 8 | merchants[1] 恰好四个 key id/name/address/logo | ✅ | `["id","name","address","logo"]` |
| 9 | 不含 recommendReason/sortOrder/merchantId/recommendSortOrder | ✅ | |
| 10 | 字段类型: id uuid / name,address string / logo {id,url} | ✅ | `{"id":"bound/logo-test.png","url":"http://placeholder.oss-cn-placeholder.aliyuncs.com/bound/logo-test.png?Expires=1787668560&OSSAccessKeyId=placeholder&Signature=hRhRM7TG9enuWN4fzXuSxuF4YWg%3D"}` |
| 11 | [前置] admin 创建清单返回 merchants 含 甲乙丙 且顺序一致 | ✅ | `["商户甲140600","商户乙140600","商户丙140600"]` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get` 在 contracts/api-spec.json 中仅声明 summary/parameters（无 responses schema），无法做响应 schema 逐字段校验；已按 summary 语义与用例预期做字段级断言（见上表），未发现契约漂移。
- 请求参数自检：query 参数与契约 parameters 一致。
