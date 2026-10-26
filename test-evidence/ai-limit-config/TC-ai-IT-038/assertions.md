# TC-ai-IT-038 断言明细

> POST /api/app/ai/generations 生成成功

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 200 | 通过 | 200 | 200 |
| 2 | 响应头 Content-Type 含 application/json（生成） | 通过 | "application/json" | "application/json" |
| 3 | status=SUCCESS | 通过 | "SUCCESS" | "SUCCESS" |
| 4 | content 为 mock 输出 JSON | 通过 | {"echo": "年龄28"} | {"echo": "年龄28"} |
| 5 | requestId 非空 | 通过 | "非空" | "01a17740-8dcf-7ba5-a9d4-8569452fdfd0" |
| 6 | clientRequestId/sceneId 与请求一致 | 通过 | ["c-426f3f4b-978c-4bca-a5e2-ad2cc3ec589a", "t_lcc015d5fa"] | ["c-426f3f4b-978c-4bca-a5e2-ad2cc3ec589a", "t_lcc015d5fa"] |
| 7 | 契约 GenerationResponse 字段（生成响应） | 通过 | ["clientRequestId", "content", "requestId", "sceneId", "status"] | ["clientRequestId", "content", "requestId", "sceneId", "status"] |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
