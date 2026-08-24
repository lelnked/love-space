## 1. app 后端：列表查询支持可选过滤

- [x] 1.1 `RouteRepository`：用带可选条件的查询替换 `findAllByCityNameOrderBySortOrderAsc`，签名 `List<Route> search(String cityName, UUID ambassadorId)`，两参数均可为 null，`order by sort_order asc`（沿用 `MerchantRepository.searchOnlineNative` 的 `(:p is null or col = :p)` 写法）
- [x] 1.2 `RouteQueryService.listByCity` 更名为 `list(String cityName, UUID ambassadorId)`：`cityName` 非空但城市查不到 → 直接返回空列表；否则查库后按大使 `online` 过滤
- [x] 1.3 列表项 `city` 对象改为按各路线自身 `cityName` 批量反查（`CityRepository.findAllByChineseNameIn`，需新增该方法），组 Map 填充；路线 `cityName` 为空或城市已删除 → `city` 为 null
- [x] 1.4 `RouteController.list`：参数改为 `@RequestParam(required = false) String cityName` + `@RequestParam(required = false) UUID ambassadorId`，更新类与方法 javadoc

## 2. 单元测试（带 @scenario 注释）

- [x] 2.1 `RouteQueryServiceTest`：更新既有按城市查询用例到新签名
- [x] 2.2 新增：无参数返回全部可见路线（@scenario route/App 端路线查询#不传任何过滤参数返回全部可见路线）
- [x] 2.3 新增：按 ambassadorId 过滤，且下线大使返回空（@scenario ...#按大使 ID 过滤路线）
- [x] 2.4 新增：cityName + ambassadorId 组合取交集（@scenario ...#城市名与大使 ID 组合过滤）
- [x] 2.5 新增：cityName 指向不存在城市返回空数组（@scenario ...#城市名不存在返回空数组）

## 3. 契约与文档

- [x] 3.1 `contracts/api-spec.json`：`/api/app/routes` GET 删除 `cityId`，新增可选 `cityName`、`ambassadorId`（design 阶段已完成）
- [x] 3.2 确认 `tests/route/it.md` 用例 URL 与新契约一致（TC-route-IT-012/013/015/016~019）

## 4. 交付验证

- [x] 4.1 `./mvnw test`（love-space-app）全绿
- [x] 4.2 `/run-api-test --change app-route-query-filters`
- [x] 4.3 刷新追溯矩阵 + `.quality-gate.yml` + `/opsx:verify`
