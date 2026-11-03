# TC-ai-IT-061 断言明细

> inputs 整体超过 20000 字符返回 400

**结论**: 通过


| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 400 | 通过 | 400 | 400 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | message 精确 | 通过 | inputs 整体超过 20000 字符 | inputs 整体超过 20000 字符 |
| 4 | 无生成记录 | 通过 | 0 | 0 |

## 备注

- v1..v11 各 1900 个 ASCII 字符（单值均 ≤2000），模板声明 v1..v11 选填；inputs 序列化长度 20991 字符
- 契约 schema：`#/paths/~1api~1app~1ai~1generations/post` 未声明 `responses`（仅 description 文字描述 200/400 等），无响应 schema 可校验；按 description 校验字段。⚠️ 契约漂移（契约缺 responses 声明），供人工确认，不判失败。
