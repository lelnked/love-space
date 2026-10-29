# TC-ai-IT-038 断言明细

> POST /api/app/ai/generations 生成成功

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 返回 200 | 通过 | 200 |
| 2 | status=SUCCESS | 通过 | SUCCESS |
| 3 | content 为 mock 输出 JSON | 通过 | {'echo': '年龄28'} |
| 4 | requestId 非空 | 通过 | 01a1865e-8ce8-77ec-8afe-2266aad9bd75 |
| 5 | sceneId 与请求一致 | 通过 | t_3e640c8d |
| 6 | clientRequestId 回显 | 通过 | c-5639583e-76a8-4189-9db5-95c810a354f0 |

**结论**: 通过
