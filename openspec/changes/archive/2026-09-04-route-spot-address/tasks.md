## 1. admin 后端

- [x] 1.1 `route/entity/RouteSpot.java` record 增 `String address`（末尾，javadoc「地点地址，可空」）
- [x] 1.2 `route/dto/RouteSpotRequest.java` 增 `String address`（无校验注解）；`RouteSpotResponse.java` 增 `String address`
- [x] 1.3 `route/service/RouteService.java` 两处地点装配（写入与响应）透传 `address`
- [x] 1.4 UT：地点地址可写可改可空（`@scenario route/路线管理#地点地址可写可改可空`）

## 2. app 后端

- [x] 2.1 `modules/route/entity/RouteSpot.java` record 增 `String address`
- [x] 2.2 `modules/route/dto/RouteSpotItemResponse.java` 增 `String address`；`RouteQueryService.java` 装配处透传
- [x] 2.3 UT：详情地点项下发 address 且未填时为 null（`@scenario route/App 端路线查询#地点地址下发且未填时为 null`）

## 3. web 前端

- [x] 3.1 `src/api/routes.ts`：`RouteSpot.address: string | null`、`RouteSpotRequest.address?: string | null`
- [x] 3.2 `src/pages/Routes/Form.tsx`：`SpotRow` 增 `address`；地点子项在「介绍」上方加「地址」`<Label>+<Input>`（非必填，无星号）；编辑回显 `s.address ?? ""`；提交 `address.trim() || null`；「添加地点」初始值补 `address: ""`

## 4. 契约

- [x] 4.1 `contracts/api-spec.json`：`RouteSpot` schema 与 app 路线详情地点项 schema 各增 `address`（string, nullable, 描述「地点地址」），不加进 `required`
- [x] 4.2 `love-space-app/docs/openapi.json`：`RouteSpotItemResponse` 增 `address`

## 5. 验证

- [x] 5.1 admin `mvn test -Dtest='*Test'` 与 app `mvn test` 跑绿；web `npx tsc --noEmit`
- [x] 5.2 `/run-api-test --change route-spot-address` 5/5 ✅；`/run-web-test` 本会话 playwright 不可用，WEB-005/WEB-002 待补
