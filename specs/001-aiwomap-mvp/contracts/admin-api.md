# Contract — love-space-admin（运营后台 API）

> Base URL：`/api/admin`
> 鉴权：除 `/auth/login` 外，全部需要 `Authorization: Bearer <JWT>`；`/users/**` 额外要求 `ROLE_ADMIN`。
> 成功响应：直接返回业务对象 / `Page<T>` / 204；失败响应：RFC 7807 `ProblemDetail`，字段级校验失败附 `errors[]`。

## 1. 通用模型

### 1.1 分页查询参数
- `page`（int，从 1 开始，默认 1）
- `size`（int，默认 20，可选 20 / 30）
- 业务字段：见各接口

### 1.2 分页响应（直接复用 Spring Data `Page` 序列化）
```json
{
  "content": [...],
  "page": 1,
  "size": 20,
  "totalElements": 123,
  "totalPages": 7
}
```

### 1.3 错误响应（示例）
```json
{
  "type": "about:blank",
  "title": "Validation Failed",
  "status": 400,
  "detail": "username 已存在",
  "instance": "/api/admin/users",
  "errors": [
    {"field": "username", "message": "不能为空"}
  ]
}
```

---

## 2. Auth

### POST `/auth/login`
- Request: `{ "username": "...", "password": "..." }`
- Response 200: `{ "token": "<JWT>", "user": { "id":"...", "username":"...", "nickname":"...", "role":"ADMIN|MEMBER" } }`
- 401：用户名/密码错误；用户已停用。

### POST `/auth/logout`
- 当前实现可为无状态（前端丢弃 token）。返回 204。

### GET `/auth/me`
- Response 200: `{ "id":"...", "username":"...", "nickname":"...", "role":"..." }`

---

## 3. Users（仅 ROLE_ADMIN）

### GET `/users` —— 分页 + 过滤
- Query：`username`（模糊）、`role`（ADMIN|MEMBER）、`enable`（true|false）、
  `createdAtFrom` / `createdAtTo`、`page`、`size`
- Response 200：`Page<UserItem>`，`UserItem` 含 id / username / nickname / role / enable / createdAt。

### POST `/users`
- Request：`{ "username":"...", "password":"...", "nickname":"..." }`
- 服务端 **强制** role=MEMBER（忽略请求中 role）。
- Response 201：`UserDetailResponse`。

### GET `/users/{id}`
- Response 200：`UserDetailResponse`。

### PUT `/users/{id}/enable` / `/users/{id}/disable`
- 无请求体。Response 204。

### PUT `/users/{id}/password`
- Request：`{ "newPassword": "..." }` —— BCrypt 后写库。
- Response 204。

---

## 4. Cities

### GET `/cities`
- Query：`name`、`online`、`page`、`size`。
- 默认按 `createdAt DESC` 排序。
- Response：`Page<CityItem>`。

### POST `/cities`
- Request：`{ chineseName, englishName, chineseProvince, englishProvince, backgroundImage?, bannerSortOrder?, online? }`
- `bannerSortOrder` 缺省 0，必须 `>= 0`（负数返回 400 字段级校验错误）。
- 400：`chineseName` 重复。

### PUT `/cities/{id}` / DELETE `/cities/{id}` / PUT `/cities/{id}/online` / `/offline` / PUT `/cities/{id}/banner-sort`
- `/banner-sort` Request：`{ "bannerSortOrder": <int >= 0> }`；`>0` 时该城市作为 App `/explore` banner 展示，
  数值越小越靠前；`=0` 则不参与 banner。该字段不影响 admin 城市列表排序。

---

## 5. Categories（MVP 预留）

- GET `/categories` —— 默认按 `createdAt DESC` 排序。
- POST `/categories` —— `{ name }`；`name` ≤10 汉字且不重名。
- PUT `/categories/{id}` —— `{ name }`。
- DELETE `/categories/{id}` —— 联动将归属此分类的商户 online=false。

---

## 6. Tags

- GET `/tags` —— 分页 + 过滤（`name`、`online`）。
- POST `/tags` —— `{ name }`；不重名，≤6 汉字。
- PUT `/tags/{id}` —— `{ name }`。
- PUT `/tags/{id}/online` / `/offline`。

---

## 7. Merchants

### GET `/merchants` —— 分页 + 过滤
- Query：`name`、`cityId`、`categoryId`、`period`、`online`、`page`、`size`。
- Response：`Page<MerchantAdminItem>`，含主要字段与四维原始分。

### POST `/merchants`
- Request `MerchantUpsertRequest`：
  ```json
  {
    "name": "≤15 汉字",
    "logo": "https://.../logo.png",
    "images": ["https://...", "..."],
    "recommendedPeriods": ["OVULATION", "LUTEAL"],
    "categoryId": "uuid|null",
    "cityId": "uuid",
    "address": "...",
    "longitude": 121.473701,
    "latitude": 31.230416,
    "tagIds": ["uuid", "uuid"],
    "safetyEnvironmentScore": 24,
    "businessRightsScore": 20,
    "experienceFriendlyScore": 20,
    "socialContributionScore": 16,
    "reviews": [
      {"nickname":"小美","title":"很棒","content":"😊..."}
    ],
    "story": "≤5000 字",
    "weight": 100
  }
  ```
- 校验：四维评分各自 ≤ 上限；`images.length ≥ 1`；`name` ≤15 汉字；`story` ≤5000 字。
- Response 201：`MerchantDetailResponse`。

### GET `/merchants/{id}` / PUT `/merchants/{id}` / DELETE `/merchants/{id}`
- PUT 复用 `MerchantUpsertRequest`。

### PUT `/merchants/{id}/online` / `/offline`

---

## 8. Files

### POST `/files/upload`
- multipart/form-data，单文件字段名 `file`，单图 ≤20MB。
- Response 200：`{ "url": "https://.../uploads/<uuid>.png" }`。
- 400：超过大小 / 类型不支持。

---

## 9. Operation Logs

### GET `/logs`
- Query：`username`、`module`、`createdAtFrom` / `createdAtTo`、`page`、`size`。
- Response：`Page<OperationLogItem>`，字段：id / username / module / action / target / createdAt。

---

## 10. 共同 HTTP 状态码语义

| 状态 | 场景 |
|---|---|
| 200 | 查询 / 更新成功 |
| 201 | 创建成功 |
| 204 | 无返回体的成功操作（启停 / 排序 / 重置密码） |
| 400 | 校验失败（含字段级错误） |
| 401 | 未登录 / token 过期 |
| 403 | 权限不足（如 MEMBER 访问 /users） |
| 404 | 资源不存在 |
| 409 | 唯一性冲突（用户名 / 城市名 / 标签名 / 分类名） |
| 413 | 文件超大（可由网关或应用层返回） |
