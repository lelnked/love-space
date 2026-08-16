# TC-featured-IT-001 断言明细（2026-08-16 复测，预期已修订）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约仅声明请求体 schema（FeaturedItemUpsertRequest：required cityId、banner），请求体自检通过；响应 schema 契约未声明，记跳过。
本轮预期按修订后口径：admin 详情只需含 cityId，城市名称由 web 端经城市列表映射展示（与 Activity/Banner 口径一致）。

## Step 1 登录

- ✅ 状态码 = 200，token 为三段式 JWT

## Step 2 前置：创建上架城市

- ✅ 状态码 = 200（实际 200），online = true

## Step 3 创建精选推荐

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 响应含 id（01a00b9d-7209-7e5d-be6b-33f64275ff19）

## Step 4 详情确认

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ cityId = 01a00b9d-71e4-7135-aa5d-4a7b6c907065（与创建时提交一致；按修订口径不要求响应含城市名称）
- ✅ banner 为签名 URL（http 开头、含 OSS 签名参数，非裸 objectKey）
- ✅ description = 「地图上新」
- ✅ online = true

## 契约 schema

- ⏭️ 响应 schema：契约未声明，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
