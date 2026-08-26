# TC-banner-IT-016 断言明细

执行日期: 2026-08-26 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1banners/get

结果: ✅ 通过（5/5 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 缺 key 时状态码 401 | ✅ | `HTTP/1.1 401` |
| 2 | 错误 key 时状态码 401 | ✅ | `HTTP/1.1 401` |
| 3 | Content-Type 为 application/problem+json | ✅ | `Content-Type: application/problem+json` / `Content-Type: application/problem+json` |
| 4 | 两种情况响应体完全一致（不泄漏 key 是否存在） | ✅ | `True —— detail="Invalid or missing API key", title="Unauthorized", status=401` |
| 5 | 响应体不含任何 key 值或 key 存在性提示 | ✅ | `detail 固定为 "Invalid or missing API key"` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1banners/get` 未声明 401 响应；实现返回 RFC7807 `application/problem+json`。
- ⚠️ **契约缺口（既有）**：契约未登记 app 端 API-key 鉴权失败的 401 响应形状。属文档滞后，实现行为符合用例预期，故不判失败。
