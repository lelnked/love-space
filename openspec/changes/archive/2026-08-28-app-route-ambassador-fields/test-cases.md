# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/route/it.md`（route 域「端」列为 web，App 客户端界面不在本仓库，故本次只落 IT）。

## 新增用例

- TC-route-IT-025: GET /api/app/routes 列表项返回 ambassadorNote（ADDED Scenario: `route/App 端路线查询#路线列表返回爱女大使说`）— P1，happy + boundary（未填为 null）
- TC-route-IT-026: GET /api/app/routes/{id} 详情 ambassador 含 id 且可反查列表（ADDED Scenario: `route/App 端路线查询#路线详情返回大使 id`）— P1，happy + state（用返回的 id 请求列表能查到本路线）

写入 `tests/route/it.md` 的用例块：

```markdown
### TC-route-IT-025: GET /api/app/routes 列表项返回 ambassadorNote
**关联需求**: route/App 端路线查询#路线列表返回爱女大使说
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get
**来源**: app-route-ambassador-fields
**优先级**: P1
**测试步骤**:
1. admin 端创建上线大使 A，并在其名下建两条路线：路线甲填爱女大使说「跟着我逛老城区」，路线乙不填
2. app 端 GET /api/app/routes（带 API key）
**预期结果**: 200；路线甲的 `ambassadorNote` == "跟着我逛老城区"；路线乙的 `ambassadorNote` == null；两条的 id/title/thumbnail/sortOrder/ambassadorName/city 字段与改动前一致
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/route/TC-route-IT-025/`
**最后更新**: -

### TC-route-IT-026: GET /api/app/routes/{id} 详情 ambassador 含 id
**关联需求**: route/App 端路线查询#路线详情返回大使 id
**关联契约**: api-spec.json#/paths/~1api~1app~1routes~1{id}/get
**来源**: app-route-ambassador-fields
**优先级**: P1
**测试步骤**:
1. admin 端创建上线大使 A 及其名下一条路线 R
2. app 端 GET /api/app/routes/{R.id}
3. 取响应 `ambassador.id`，再 GET /api/app/routes?ambassadorId={该 id}
**预期结果**: 步骤 2 返回 200 且 `ambassador.id` == 大使 A 的 id，`ambassador` 的 name/avatar/tags 仍在；步骤 3 返回 200 且结果含路线 R
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/route/TC-route-IT-026/`
**最后更新**: -
```

## 修改用例

（无——既有用例的断言字段均未变更）

## 需重测用例

- TC-route-IT-016 ~ TC-route-IT-019、TC-route-IT-024：路线列表/详情响应结构被扩展，回归确认老字段与过滤、排序行为未受影响

## 执行汇总

总数 2 新增 + 5 重测 = 7；✅ 7；❌ 0；未执行 0。

- TC-route-IT-025 ✅（`test-evidence/regression/route/TC-route-IT-025/`）
- TC-route-IT-026 ✅（`test-evidence/regression/route/TC-route-IT-026/`）
- 重测 TC-route-IT-016~019、024：列表老字段完整、city 为 null 口径不变、sortOrder 升序、按大使/组合过滤条数正确、详情字段集完整 ✅
- app 端 mvn test 85 UT 全绿、`-Dtest=*IT` 19 IT 全绿（含新增 `RouteQueryServiceTest#listReturnsAmbassadorNote` / `#detailReturnsAmbassadorId`）
