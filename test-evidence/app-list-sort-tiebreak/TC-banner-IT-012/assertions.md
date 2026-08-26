# TC-banner-IT-012 断言明细

执行日期: 2026-08-26 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1banners/get

结果: ✅ 通过（8/8 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | Content-Type 含 application/json | ✅ | `application/json` |
| 3 | body 为数组（非分页对象） | ✅ | `type=list, len=2` |
| 4 | 顺序为 B(sortOrder=0) → A(sortOrder=1) | ✅ | `['BannerB021653', 'BannerA021653']` |
| 5 | 每项字段恰为 {id,name,type,image,data} | ✅ | `['data', 'id', 'image', 'name', 'type']` |
| 6 | 图片字段名为 image 且为数组 | ✅ | `image is list, len=1` |
| 7 | 不含 positionCode/online/sortOrder/link/时间戳 | ✅ | `已核对，均不存在` |
| 8 | data 含城市 id、中英文名称、省份 | ✅ | `['chineseName', 'chineseProvince', 'englishName', 'englishProvince', 'id']` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1banners/get` 仅声明 summary 与 query parameters（无 responses schema），故无法做响应 schema 逐字段校验；已按 summary 语义（"数组，sortOrder 升序、同序号 createdAt 倒序"）与用例预期做字段级断言。
- 请求参数自检：`positionCode` 为契约声明的必填 query 参数，取值合法。
- 未发现契约漂移。
