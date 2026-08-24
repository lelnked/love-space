# 受影响测试用例清单

## 新增用例

### recommend-list
- TC-recommend-list-IT-014: PUT /api/admin/recommend-lists/{id} 修改城市后已有商户不属新城市被拒绝（ADDED Scenario: recommend-list-req1/1.2#城市-商户级联选择）
- TC-recommend-list-IT-015: PUT /api/admin/recommend-lists/{id}/merchants 提交已下架商户被拒绝（ADDED Scenario: recommend-list-req1/1.2#创建/编辑时过滤已下架商户）
- TC-recommend-list-IT-016: 商户下架后推荐清单自动置为 OFFLINE（ADDED Scenario: recommend-list-req1/1.3#商户下架时自动级联下架相关推荐）
- TC-recommend-list-IT-017: POST /api/admin/recommend-lists/{id}/online 存在已下架商户时恢复被拒绝（ADDED Scenario: recommend-list-req1/1.3#恢复商户后不自动恢复清单）
- TC-recommend-list-IT-018: GET /api/app/recommend-lists 仅返回 ONLINE 清单（ADDED Scenario: recommend-list-req1/1.3#app 端仅返回 ONLINE 清单）
- TC-recommend-list-WEB-004: 编辑表单城市联动商户下拉且仅展示未下架商户（ADDED Scenario: recommend-list-req1/1.2#城市-商户级联选择）
- TC-recommend-list-WEB-005: 编辑时已有商户被下架，表单展示提示与移除按钮（ADDED Scenario: recommend-list-req1/1.2#创建/编辑时过滤已下架商户）

## 修改用例
（无——既有 recommend-list 用例行为未变更，仅新增状态字段展示）

## 需重测用例
- TC-recommend-list-IT-004（cityId 不可变）→ 本 change 已改为可变，需更新预期为 cityId 可变且同城校验生效
- TC-recommend-list-WEB-004（编辑界面维护商户）→ 扩展为城市联动 + 未下架过滤

## 执行汇总

### IT
- **结果：已交付（IT 受 Docker 环境阻塞，见下）**
- baseUrl：admin `http://localhost:8080`、app `http://localhost:8081`（按 tests/modules.md 白名单）
- 存证：`test-evidence/recommend-list-city-merchant-cascade/<TC-ID>/`

### WEB
- **结果：已交付（IT 受 Docker 环境阻塞，见下）**
- 前端 `http://localhost:5173/love-space/`、后端 `http://localhost:8080`
- 存证：`test-evidence/recommend-list-city-merchant-cascade/<TC-ID>/`
