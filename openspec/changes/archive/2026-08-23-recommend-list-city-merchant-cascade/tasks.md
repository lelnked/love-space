# Tasks: recommend-list-city-merchant-cascade

## 1. 数据库迁移（admin 端 Liquibase）

- [x] 1.1 新增 changes SQL：`loves_recommend_list` 新增 `status varchar not null default 'ONLINE'`；master changelog include

## 2. love-space-admin

- [x] 2.1 RecommendList 实体/DTO 增加 `status` 枚举：`ONLINE`/`OFFLINE`，默认 `ONLINE`
- [x] 2.2 RecommendListService：创建/更新支持 `cityId` 可变，保存时校验同城；若城市变更导致已有商户不属新城市，返回 400 中文错误
- [x] 2.3 RecommendListService：商户维护全量替换时增加“已下架商户”兜底校验，拒绝并返回 400 中文错误
- [x] 2.4 RecommendListService：商户下架时级联查询并批量置关联清单为 `OFFLINE`；记录审计
- [x] 2.5 RecommendListService：提供清单恢复为 `ONLINE` 的校验逻辑（当前无已下架商户才允许）
- [x] 2.6 RecommendListController：列表/详情/创建/更新/删除接口扩展 `status`；新增 `POST /{id}/online` 恢复接口
- [x] 2.7 UT：service 校验逻辑（@scenario 注释锚定 recommend-list-req1 各 scenario）

## 3. love-space-app

- [x] 3.1 RecommendList 只读查询统一加 `status = ONLINE` 过滤（列表/详情）
- [x] 3.2 UT：service 过滤逻辑（@scenario 注释锚定 recommend-list-req1#1.3）

## 4. love-space-web

- [x] 4.1 推荐清单列表页增加 `status` 列展示
- [x] 4.2 新建/编辑弹窗：所属城市下拉可改；商户下拉随城市联动且仅展示未下架商户；状态单选项
- [x] 4.3 编辑时若已有商户被下架，表单内展示提示文案与移除按钮
- [x] 4.4 城市变更时若已有商户不属新城市，保存失败后前端展示中文错误并提示清理
- [x] 4.5 路由与侧栏入口不变，沿用既有 `pages/RecommendLists`

## 5. 收尾

- [x] 5.1 `tests/modules.md` 核对/补充（如有新路径）
- [x] 5.2 `contracts/api-spec.json` 已登记（核对）
- [x] 5.3 admin UT/IT 全绿、app UT 全绿、web `npm run build` 通过
