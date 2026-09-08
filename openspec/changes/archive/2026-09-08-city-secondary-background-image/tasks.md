# Tasks

## 1. 数据库
- [x] 1.1 新增 `love-space-admin/src/main/resources/db/changelog/changes/025-add-city-secondary-background-image.sql`：`ALTER TABLE loves_city ADD COLUMN secondary_background_image text;`（Liquibase formatted SQL，含 rollback），并在 master changelog 追加 include

## 2. admin 后端
- [x] 2.1 `City` 实体新增 `@Column(name = "secondary_background_image") private String secondaryBackgroundImage;`
- [x] 2.2 `CityCreateRequest` / `CityUpdateRequest` 新增可空 `secondaryBackgroundImage`，objectKey 正则校验与错误文案复刻 `backgroundImage`
- [x] 2.3 `CityItemResponse` / `CityDetailResponse` 新增 `ImageResponse secondaryBackgroundImage`，映射处复用现有签名逻辑
- [x] 2.4 `CityService` 创建/更新走同一 `bindBackgroundImage`（可空 validateAndBind），null/blank → null
- [x] 2.5 UT/IT：`CityServiceTest`、`CityReadIT` 补 `@scenario` 覆盖 TC-city-IT-015/016/017

## 3. app 后端
- [x] 3.1 `com.space.app.modules.city.entity.City` 新增同名字段与列映射
- [x] 3.2 app `CityItemResponse` 新增 `secondaryBackgroundImage`，`CityService` 映射
- [x] 3.3 app `CityReadIT` 补场景覆盖 TC-city-IT-018（有值 / null）

## 4. web 前端
- [x] 4.1 `src/api/cities.ts`：detail/item 类型加 `secondaryBackgroundImage: ImageResponse | null`，upsert 载荷加 `secondaryBackgroundImage?: string | null`
- [x] 4.2 `src/pages/Cities/Form.tsx`：在「背景图」下方加一组同款 `ImageUploader`（label「第二背景图」），独立 state，提交与回填与现有字段对称

## 5. 契约
- [x] 5.1 `contracts/api-spec.json` 城市相关 request/response schema 加 `secondaryBackgroundImage`
- [x] 5.2 `love-space-app/docs/openapi.json` 同步 app 城市响应字段
