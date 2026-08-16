# Proposal: ambassador-route-activity

## Why

二期需求第四、五章：客户端要上线「路线」（由爱女大使创作、挂在地图下的游玩路线）与「活动」（挂在地图下、含富文本详情的运营活动）。当前系统没有这两个模块，运营无法配置，客户端无数据可拉。

## What Changes

- **爱女大使（新建）**：admin CRUD——头像、名称、标签（最多 3 条）、上线/下线；大使下线后其关联路线在 app 端整体隐藏。
- **路线（新建）**：admin CRUD——所属地图（城市）单选、排序号、主标题、爱女大使说、缩略图 1 张、路线图片 ≥1 张、旅行时间/适合季节/旅行状态（文本）、关联大使单选、地点列表（名称+图片 1 张+介绍，按添加顺序展示）。无上下架，直接删除。app 端按城市查路线列表 + 详情。
- **活动（新建）**：admin CRUD——所属地图单选、活动图片 ≥1 张、标题、标签多个、适合周期多选（经期/卵泡期/排卵期/黄体期）、级别 L1/L2/L3 单选、简介、编辑说、集合地/解散地/交通/签证（文本）、路线子条目列表（标题+内容）、活动详情说明（富文本存 HTML）、上线/下线。app 端按城市查活动列表 + 详情。
- **地图下架级联扩展**：城市下架后，该城市下的路线、活动在 app 端不可见；web 下架确认提示补充「路线、活动」。
- web 端新增「爱女大使」「路线管理」「活动管理」三个后台页面；富文本编辑器仅活动详情说明一处使用。

## Capabilities

### New Capabilities

- `route`: 爱女大使与路线——admin 维护大使与路线，app 端按城市只读查询；大使下线级联隐藏路线。
- `activity`: 活动——admin 维护活动（含富文本详情、上下架），app 端按城市只读查询。

### Modified Capabilities

- `city`: 下架级联范围从「商户、Banner、推荐清单」扩展为再含「路线、活动」（app 端不可见 + web 确认提示口径）。

## Impact

- **DB（admin 管 schema）**：新表 `loves_ambassador`、`loves_route`、`loves_activity`（子列表走 jsonb 列，Liquibase formatted-SQL）。
- **admin 后端**：新模块 `modules.ambassador`、`modules.route`、`modules.activity`（controller/service/repository/entity/dto），复用 ObjectKeyValidator/ImageUrlSigner 图片链路。
- **app 后端**：只读查询接口 `/api/app/routes/*`、`/api/app/activities/*`，实体只读映射新表。
- **web 前端**：新页面目录 `pages/Ambassadors`、`pages/Routes`、`pages/Activities`；引入一个富文本编辑器组件（仅活动详情用）；城市下架确认文案更新。
- **契约与域**：`contracts/api-spec.json` 登记全部新接口；`tests/modules.md` 注册 `route`、`activity` 两个新域（端 web）。
