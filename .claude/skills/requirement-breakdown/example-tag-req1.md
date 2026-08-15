# 需求1. 项目标签管理（Project Tag）

让项目成员为项目内的业务对象打「标签」,用于分类和检索。本需求覆盖标签本身的**创建、查询、删除**(软删除),产出一套可被其它模块复用的标签字典。标签名在同项目下不可重复;删除采用软删除(`active=false`),历史引用不受影响。

## 范围与边界（保证需求独立）

本需求只覆盖**标签实体的维护**。以下内容属于其它独立需求,本需求**不实现**,仅在数据表预留扩展位:

| 内容 | 归属需求 |
|------|----------|
| 标签颜色 / 图标等展示主题 | 需求2（标签外观) |
| 把标签挂到业务对象上(打标签/取消打标签) | 需求3(对象标签关联) |
| 按标签聚合统计 | 需求4(标签报表) |

> 因此本需求的创建/编辑接口请求体**仅含本需求字段**(`name`);`color` 等字段由对应需求各自扩展同一接口。

## 数据模型（tags）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | UUID | 主键 | |
| project_id | UUID | 必填 | 所属项目 |
| name | VARCHAR(50) | 必填 | 标签名;同项目下 active 唯一 |
| active | BOOLEAN | 必填,默认 true | 软删除标志,false 表示已删除 |
| created_at / created_by / updated_at / updated_by | | | 审计字段 |

**唯一性规则**:同一 `project_id` 下,`active=true` 的 `name` 唯一(大小写不敏感)。
**权限点**:`tag:read / tag:create / tag:delete`。所有写操作记入审计日志。

---

## 1.1 创建标签

在当前项目下新建一个标签,名称在项目内唯一。

**实施方案**：

1. 创建一个接口 `POST /api/projects/{projectId}/tags`,需要 `tag:create` 权限。
   `@RequestBody TagCreateRequest`
   ```jsonc
   {
     "name": "紧急"   // 必填, 1–50 字符, 同项目 active 内唯一(大小写不敏感)
   }
   ```
2. 名称唯一校验:若同项目下已存在同名 active 标签,返回 `400`,字段级错误定位到 `name`:
   ```json
   { "fieldErrors": { "name": "标签名已存在" } }
   ```
3. 持久化:`active` 固定写 true,`project_id` 取自路径,调用方不可传入。
4. 写入审计日志(动作:创建)。
5. 返回响应数据 `TagDetail`:
   ```jsonc
   {
     "id": "uuid",
     "name": "紧急",
     "active": true
   }
   ```

## 1.2 标签列表（分页 + 搜索）

分页查询当前项目下的标签,支持按名称模糊搜索;默认只返回 active 标签。

**实施方案**：

1. 创建一个接口 `GET /api/projects/{projectId}/tags`,需要 `tag:read` 权限,查询参数接收分页 `page`/`size`。
2. 过滤/搜索为可选查询参数,叠加生效:
   ```
   ?search=<kw>&includeDeleted=false&page=0&size=20
   ```
   - `search`:对 `name` 做大小写不敏感的子串匹配,可空。
   - `includeDeleted`:默认 false 只返回 active;true 时连同已软删除的一并返回。
3. 返回分页响应 `PageResponse<TagListItem>`:
   ```jsonc
   {
     "content": [
       { "id": "uuid", "name": "紧急", "active": true }
     ],
     "pageNumber": 0, "pageSize": 20, "totalElements": 1, "totalPages": 1
   }
   ```

## 1.3 删除标签（软删除）

删除一个标签,采用软删除,不物理移除,以免影响其它需求中已建立的引用。

**实施方案**：

1. 创建一个接口 `DELETE /api/projects/{projectId}/tags/{id}`,需要 `tag:delete` 权限。
2. 校验:标签不存在或不属于当前项目,返回 `404`。
3. 业务规则:将 `active` 置为 false(软删除);已是 false 时幂等返回成功,不报错。删除后该 `name` 可被 1.1 重新创建(唯一性只约束 active 行)。
4. 写入审计日志(动作:删除)。
5. 返回响应数据 **无**(`204 No Content`)。

---

## 附录:本需求接口一览

| 编号 | 方法 | 路径 | 权限 | 请求体 | 响应 |
|------|------|------|------|--------|------|
| 1.1 | POST | `/api/projects/{projectId}/tags` | `tag:create` | `TagCreateRequest` | `TagDetail` |
| 1.2 | GET | `/api/projects/{projectId}/tags` | `tag:read` | —(`page/size/search/includeDeleted`) | `PageResponse<TagListItem>` |
| 1.3 | DELETE | `/api/projects/{projectId}/tags/{id}` | `tag:delete` | — | —（`204`) |
