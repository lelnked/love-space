# TC-city-IT-001 断言明细

| # | 断言层 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 状态码 | POST /api/admin/cities | 200 | 200 | ✅ |
| 2 | body | 创建响应 editorNote | 江城夜景是这座城市的灵魂 | 逐字一致 | ✅ |
| 3 | 状态码 | GET /api/admin/cities/{id} | 200 | 200 | ✅ |
| 4 | body | 详情 editorNote 与提交值逐字一致 | 江城夜景是这座城市的灵魂 | 逐字一致 | ✅ |
| 5 | 响应头 | Content-Type | application/json | application/json | ✅ |

**结论**: ✅ 通过（删除 TC-009/010 后无断链遗留，创建路径不受本 change 影响）
