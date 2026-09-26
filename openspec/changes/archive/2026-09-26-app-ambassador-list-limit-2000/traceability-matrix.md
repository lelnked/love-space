# 追溯矩阵（交付核对）：app-ambassador-list-limit-2000

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change app-ambassador-list-limit-2000`

## 需求与场景
- **route/app 端爱女大使只读查询**: 不传 limit 返回全部上线大使 / limit 生效并在 2000 处收敛 / limit 非法值回落缺省 / 大使详情可见性不变

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-route-IT-020 | GET /api/app/ambassadors 不传 limit 返回全部上线大使 | route/爱女大使管理 | api-spec.json#/paths/~1api~1app~1ambassadors/get | change app-ambassador-list-limit-2000（MODIFIED，原直接实现） | IT | `love-space-app/target/surefire-reports/com.space.app.modules.ambassador.controller.AmbassadorReadIT.txt` | ✅ |
| TC-route-IT-021 | GET /api/app/ambassadors?limit= 生效且上限 2000、非法值回落缺省 | route/爱女大使管理 | api-spec.json#/paths/~1api~1app~1ambassadors/get | change app-ambassador-list-limit-2000（MODIFIED，原直接实现） | IT | `love-space-app/target/surefire-reports/com.space.app.modules.ambassador.controller.AmbassadorReadIT.txt` | ✅ |
| TC-route-IT-022 | GET /api/app/ambassadors/{id} 详情与 404 口径 | route/爱女大使管理 | api-spec.json#/paths/~1api~1app~1ambassadors~1{id}/get | 直接实现（未走 change） | IT | `love-space-app/target/surefire-reports/com.space.app.modules.ambassador.controller.AmbassadorReadIT.txt` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：3
- ✅ 通过：3 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
