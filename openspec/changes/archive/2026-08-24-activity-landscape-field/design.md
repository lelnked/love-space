## 已定决策

1. **字段类型用 `text` 且可空，不加长度约束。** 景观是自由描述文本，与既有的 `transportation` / `visa` 等同类字段口径一致（都是无长度约束的可空 text），不单独为它引入校验规则。
2. **只进详情，不进列表。** app 端活动列表的定位是卡片流（图片/标题/标签/级别/周期），加字段会让列表响应变胖且无处展示；`landscape` 只出现在 `ActivityDetailResponse`。
3. **不做数据回填。** 存量活动的 `landscape` 保持 `null`，由运营按需补录；不从 `introduction` 里做任何自动抽取。
4. **迁移可加可逆。** 纯 `ADD COLUMN`，rollback 为 `DROP COLUMN`，无数据迁移与回填逻辑。
5. **web 表单位置**放在「签证」之后、路线子条目之前，与后端 DTO 的字段顺序保持一致，减少运营在表单与详情之间的对照成本。

## 接口变更

`contracts/api-spec.json` → `components.schemas.ActivityUpsertRequest.properties` 新增：

```json
"landscape": { "type": "string", "description": "景观" }
```

响应侧（admin `ActivityDetailResponse` / app `ActivityDetailResponse`）同步新增同名字段，类型 `string | null`。
