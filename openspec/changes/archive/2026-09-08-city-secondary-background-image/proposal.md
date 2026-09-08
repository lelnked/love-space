## Why

城市（地图）目前只有一张背景图 `backgroundImage`，App 端在不同展示位需要第二张背景图，现有单图不够用。原字段的语义与取值不能动，否则会影响线上已配置的城市。

## What Changes

- 城市新增可空的第二张背景图字段 `secondaryBackgroundImage`，与既有 `backgroundImage` 完全独立、互不影响。
- admin 端城市创建/更新接口接收 `secondaryBackgroundImage`（OSS objectKey，可空），列表/详情返回 `ImageResponse`（无图为 `null`）。
- web 后台地图表单新增一个「第二背景图」上传控件，与现有「背景图」控件同款（`ImageUploader`），可新增、可编辑、可清空。
- App 端城市接口在响应中一并返回 `secondaryBackgroundImage`（`ImageResponse`，无图为 `null`）。
- 数据库 `loves_city` 新增可空列 `secondary_background_image`；既有数据不迁移，默认 `NULL`。
- 非 BREAKING：老字段行为、老请求体（不带新字段）与老响应字段全部保持不变。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `city`: 新增「城市第二背景图」要求——admin 可维护、app 可读取的第二张可空背景图。

## Impact

- DB：`loves_city` 新增列 `secondary_background_image text`（新增 Liquibase changelog 025）。
- admin：`City` 实体、`CityCreateRequest`/`CityUpdateRequest`（objectKey 白名单校验同现有背景图）、`CityItemResponse`/`CityDetailResponse`、`CityService`（复用可空 validateAndBind 逻辑）。
- app：`City` 实体、`CityItemResponse`、`CityService` 映射。
- web：`src/api/cities.ts` 类型、`src/pages/Cities/Form.tsx` 表单控件。
- 契约：`contracts/api-spec.json` 与 `love-space-app/docs/openapi.json` 的城市相关 schema。
