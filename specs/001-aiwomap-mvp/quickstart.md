# Quickstart — 爱女地图 MVP 本地联调

## 0. 前置条件

- JDK 25
- Maven 3.9+（仓库内带 `mvnw`）
- Node.js 20+
- PostgreSQL 16（建议本机 5432，默认数据库分别为 `love_space_admin`、`love_space_app`）
- 推荐 IDE：IntelliJ IDEA 2025+（启用 Lombok 插件）、VS Code（前端）

## 1. 数据库初始化

```bash
psql -U postgres -c "CREATE DATABASE love_space_admin;"
psql -U postgres -c "CREATE DATABASE love_space_app;"
```

`application-dev.yml` 默认连接 `jdbc:postgresql://localhost:5432/love_space_admin`，用户名 / 密码通过
环境变量 `LS_DB_USER` / `LS_DB_PASSWORD` 注入。

## 2. 启动 love-space-admin

```bash
cd love-space-admin
LS_DB_USER=postgres LS_DB_PASSWORD=postgres ./mvnw spring-boot:run
```

启动日志中出现 Liquibase `002-seed-admin-manager.sql :: 002-seed-admin-manager executed` 即代表默认账号已植入
`loves_manager` 表：

- 用户名：`admin`
- 初始密码：`8@y2eoRLyStM*UVU`（首次登录后通过"重置密码"接口修改）

Liquibase 自动执行 `db/changelog/db.changelog-master.yaml`（仅 include；实际 changeset 为 formatted-SQL：
`changes/001-init-schema.sql`、`changes/002-seed-admin-manager.sql`）。所有业务表均带 `loves_` 前缀。
Liquibase 版本不显式 pin，由 Spring Boot 4.0.6 starter 携带的默认版本决定。

## 3. 启动 love-space-app

```bash
cd love-space-app
LS_DB_USER=postgres LS_DB_PASSWORD=postgres \
APP_SECURITY_API_KEYS=dev-local-key-001 \
./mvnw spring-boot:run
```

App 端通过 `X-API-Key` 鉴权；`APP_SECURITY_API_KEYS`（对应配置 `app.security.api-keys`，Spring Boot relaxed binding）支持逗号分隔的多 key（轮换 / 灰度场景）。配置为空时启动失败。
默认端口与 admin 不冲突（admin 8080 / app 8081，详见 `application.yml`）。

## 4. 启动 love-space-web

```bash
cd love-space-web
npm install         # 首次
echo "VITE_ADMIN_API_BASE=http://localhost:8080" > .env.local
npm run dev
```

访问 `http://localhost:5173/signin`，使用 admin 账号登录。

## 5. 联调最小路径（验证 P1 + P2）

1. 在 `/cities` 新建一个城市并"上线"。
2. 在 `/tags` 新建一个标签并"上架"。
3. 在 `/merchants/create`：
   - logo + 至少 1 张图片（先到 `/files/upload` 拿 URL）
   - 选择刚才的城市 + 一个推荐周期
   - 选择刚才的标签
   - 四维评分填 24/20/20/16
   - 至少 1 条评价
   - 商户故事 ≤5000 字
   - 权重 100；保存后点"上架"
4. 用 `curl` 调 App 端（注意带 API Key 头）：
   ```bash
   K="dev-local-key-001"
   curl -H "X-API-Key: $K" http://localhost:8081/api/app/cities
   curl -H "X-API-Key: $K" "http://localhost:8081/api/app/merchants?cityId=<上一步 city id>"
   curl -H "X-API-Key: $K" http://localhost:8081/api/app/merchants/<merchant id>
   ```
   预期返回百分制 80/80/80/80、爱女指数 80（10 级中第 8 级）。
   不带 `X-API-Key` 或 key 错误时返回 401。

## 6. 测试

```bash
# 后端
cd love-space-admin && ./mvnw test
cd love-space-app   && ./mvnw test

# 前端
cd love-space-web && npm run lint && npm run build
```

## 7. 常见问题

- **登录 401**：检查 token 是否在 `Authorization: Bearer ...` 头中；token 默认 8 小时过期。
- **MEMBER 看不到 /managers**：预期行为（前端按 role 过滤菜单；后端 `@PreAuthorize("hasRole('ADMIN')")`）。
- **App 端列表为空**：确认商户已"上架"、且 `cityId` 对应城市 `online=true`。
- **emoji 显示为 ?**：检查数据库连接 `client_encoding=UTF8`，PG 默认即可。

## 8. 安全巡检（首次上线）

- 默认 admin 账号密码已通过 BCrypt 哈希存储（见 `db/changelog/changes/002-seed-admin-manager.sql`），
  `loves_manager` 表中不存在明文密码。
- 首次上线后，运营须立刻通过 `PUT /api/admin/managers/{id}/password` 修改默认密码（前端 Manager 管理页
  "重置密码"按钮即可触发），并妥善保存新密码。
- 修改后建议在 `操作日志` 页核对一条 `module=manager / action=reset-password` 记录，确认审计链路通畅。
