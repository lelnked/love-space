# TC-ai-IT-103 断言明细

> 2000 个中文字符通过长度校验

**结论**: 通过


| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 200（非 400） | 通过 | 200 | 200 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | status 为生成结果枚举 | 通过 | ∈ EMPTY/FORMAT_ERROR/RATE_LIMITED/REFUSED/SUCCESS/TIMEOUT/UPSTREAM_ERROR | SUCCESS |
| 4 | clientRequestId 回显 | 通过 | c-b5e412e7-55aa-4e62-b6d7-945d220cc769 | c-b5e412e7-55aa-4e62-b6d7-945d220cc769 |
| 5 | sceneId 回显 | 通过 | t_d24a1a10 | t_d24a1a10 |
| 6 | requestId 非空 | 通过 | 非空 | 01a1a06a-a919-7a42-8a04-33b7f3528389 |
| 7 | 响应字段集合 = {requestId,clientRequestId,sceneId,status,content} | 通过 | 5 字段 | ['clientRequestId', 'content', 'requestId', 'sceneId', 'status'] |
| 8 | content 回显完整 user_input（未截断） | 通过 | echo 含完整输入 | echo 长度 2004 |

## 备注

- user_input 为 2000 个中文字符；inputs 序列化长度 2017 字符，UTF-8 6017 字节
- 契约 schema：`#/paths/~1api~1app~1ai~1generations/post` 未声明 `responses`（仅 description 文字描述 200/400 等），无响应 schema 可校验；按 description 校验字段。⚠️ 契约漂移（契约缺 responses 声明），供人工确认，不判失败。
