# 需求N. <中文名>（<English Name 可选>）

<业务描述:这个需求让谁、做什么、产出什么价值。点明关键约束,例如"某字段只读,只能经 XX 操作变更""采用软删除"等。>

## 范围与边界（保证需求独立)

本需求只覆盖 <X>。以下内容属于其它独立需求,本需求**不实现**,仅在数据表/表单预留扩展位:

| 内容 | 归属需求 |
|------|----------|
| <属于别的需求的能力A> | 需求X |
| <属于别的需求的能力B> | 需求Y |

> 因此本需求的创建/编辑接口请求体**仅含本需求字段**;其它字段由对应需求各自扩展同一接口。

## 数据模型（<表名>)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | UUID | 主键 | |
| project_id | UUID | 必填 | 所属项目 |
| <field> | <type> | 必填/可空 | <说明> |
| created_at / created_by / updated_at / updated_by | | | 审计字段 |

**唯一性规则**:<如 同项目下 active 的某编号唯一>。
**权限点**:`<资源>:read / create / edit / delete`。所有写操作记入审计日志。

---

## N.1 <功能点名>

<一句话:这个功能点做什么。>

**实施方案**：

1. 创建一个接口 `POST /api/projects/{projectId}/<resource>`,需要 `<资源>:create` 权限。
   `@RequestBody <Xxx>CreateRequest`
   ```jsonc
   {
     "fieldA": "value",     // 必填, 最长100, <唯一/枚举等约束>
     "fieldB": 50           // 可空, 非负整数
   }
   ```
2. <校验/业务规则;引用其它功能点编号,如"编号唯一校验见 N.2">。
3. <持久化要点;只读字段说明>。
4. 写入审计日志(动作:创建)。
5. 返回响应数据 `<Xxx>Detail`(结构见 N.3):
   ```jsonc
   { "id": "uuid", "fieldA": "value", "fieldB": 50 }
   ```

## N.2 <功能点名>

<描述>

**实施方案**：

1. ...
2. 错误时返回 `400`,字段级错误定位到 `<field>`:
   ```json
   { "fieldErrors": { "<field>": "<message>" } }
   ```

## N.x <列表类功能点示例>

<描述：分页/过滤/搜索/状态标识等,通常各自独立成节,但共享同一个 GET 列表接口。>

**实施方案**：

1. 创建一个接口 `GET /api/projects/{projectId}/<resource>`,需要 `<资源>:read` 权限,查询参数接收分页 `page`/`size`。
2. 过滤/搜索为可选查询参数,叠加生效:
   ```
   ?search=<kw>&<filterField>=<v>&page=0&size=20
   ```
3. 返回分页响应 `PageResponse<<Xxx>ListItem>`:
   ```jsonc
   {
     "content": [ { "id": "uuid", "fieldA": "value" } ],
     "pageNumber": 0, "pageSize": 20, "totalElements": 1, "totalPages": 1
   }
   ```

---

## 附录:本需求接口一览

| 编号 | 方法 | 路径 | 权限 | 请求体 | 响应 |
|------|------|------|------|--------|------|
| N.1 | POST | `/api/projects/{projectId}/<resource>` | `<资源>:create` | `<Xxx>CreateRequest` | `<Xxx>Detail` |
| N.3 | GET | `/api/projects/{projectId}/<resource>/{id}` | `<资源>:read` | — | `<Xxx>Detail` |
| N.x | GET | `/api/projects/{projectId}/<resource>` | `<资源>:read` | — (`page/size/search/...`) | `PageResponse<<Xxx>ListItem>` |

---

> 真实范例参考:同目录 `example-tag-req1.md`(项目标签管理)是按本模板产出的完整小样板,部件齐全(范围与边界 / 数据模型 / 唯一性 / 权限点 / 写接口 + 列表接口 + 软删除 / 附录),可对照其 1.1–1.3 的写法。
