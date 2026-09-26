# 受影响测试用例清单

> 用例本体在 `tests/route/it.md`（living 文件，runner 独占回写状态）。

## 新增用例

（无）

## 修改用例

- TC-route-IT-020: 不传 limit 返回全部上线大使（原「默认返回权重最高的 3 位」）→ MODIFIED: route/app 端爱女大使只读查询#不传 limit 返回全部上线大使
- TC-route-IT-021: limit 生效、上限 2000、非法值回落缺省（原「上限 20、非法值回落 3」）→ MODIFIED: #limit 生效并在 2000 处收敛 + #limit 非法值回落缺省

## 需重测用例

- TC-route-IT-022: GET /api/app/ambassadors/{id} 详情与 404 口径（同类改动确认详情未回归）
