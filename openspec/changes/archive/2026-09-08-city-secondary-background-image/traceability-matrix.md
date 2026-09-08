# 追溯矩阵（交付核对）：city-secondary-background-image

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change city-secondary-background-image`

## 需求与场景
- **city/城市第二背景图**: admin 创建城市时设置第二背景图 / admin 更新城市的第二背景图 / 第二背景图可清空 / 非法 objectKey 被拒绝 / 未配置第二背景图时为 null / app 端城市数据返回第二背景图 / web 后台表单维护第二背景图

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-city-IT-001 | POST /api/admin/cities 创建城市保存编辑说 | city/地图编辑说#admin 保存编辑说 | api-spec.json#/paths/~1api~1admin~1cities/post | map-and-recommend-list | IT | `test-evidence/city-secondary-background-image/TC-city-IT-001/` | ✅ |
| TC-city-IT-015 | POST/GET /api/admin/cities 创建时设置第二背景图 | city/城市第二背景图#admin 创建城市时设置第二背景图 | api-spec.json#/paths/~1api~1admin~1cities/post | city-secondary-background-image | IT | `test-evidence/city-secondary-background-image/TC-city-IT-015/` | ✅ |
| TC-city-IT-016 | PUT /api/admin/cities/{id} 更新与清空第二背景图 | city/城市第二背景图#admin 更新城市的第二背景图 | api-spec.json#/paths/~1api~1admin~1cities~1{id}/put | city-secondary-background-image | IT | `test-evidence/city-secondary-background-image/TC-city-IT-016/` | ✅ |
| TC-city-IT-017 | POST /api/admin/cities 非法 secondaryBackgroundImage 被拒绝 | city/城市第二背景图#非法 objectKey 被拒绝 | api-spec.json#/paths/~1api~1admin~1cities/post | city-secondary-background-image | IT | `test-evidence/city-secondary-background-image/TC-city-IT-017/` | ✅ |
| TC-city-IT-018 | GET /api/app/cities 返回第二背景图 | city/城市第二背景图#app 端城市数据返回第二背景图 | api-spec.json#/paths/~1api~1app~1cities/get | city-secondary-background-image | IT | `test-evidence/city-secondary-background-image/TC-city-IT-018/` | ✅ |
| TC-city-WEB-005 | 地图表单可维护第二背景图 | city/城市第二背景图#web 后台表单维护第二背景图 | - | city-secondary-background-image | WEB | `test-evidence/regression/city/TC-city-WEB-005/` | ⬜ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：6
- ✅ 通过：5 (83.3%)
- ❌ 失败：0
- ⬜ 未测：1
