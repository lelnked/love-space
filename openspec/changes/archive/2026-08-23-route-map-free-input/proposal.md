# proposal: route map free-text input

## Why
运营后台路线表单当前要求“所属城市”必须来自系统城市库，但业务上存在地图/城市尚未入库时就要规划路线的场景。

## What
把路线创建/编辑表单的所属城市改为“所属地图”自由输入；创建接口不再校验城市库存在。

## Scope
- `love-space-web/src/pages/Routes/Form.tsx`
- `love-space-admin/src/main/java/com/loves/space/modules/route/{RouteUpsertRequest.java,RouteService.java}`
- `openspec/specs/route/spec.md`

## Acceptance
- 新增/编辑路线时可直接输入任意地图名称/ID 保存。
- 不再校验城市库是否存在对应记录。
