# Contract — love-space-app（移动端只读 API）

> Base URL：`/api/app`
> 鉴权：**API Key**。客户端 MUST 在每个请求头中带 `X-API-Key: <key>`；
> 后端维护允许的 key 列表（配置项 `app.security.api-keys`），命中任一即放行，否则 401。
> MVP 无用户 / 账号系统，所有接口只读。CORS 允许移动端域名。
> 成功响应直接返回业务对象 / `Page<T>`；错误响应使用 ProblemDetail。

## 0. 鉴权

### 请求头

| Header | 必填 | 说明 |
|---|---|---|
| `X-API-Key` | 是 | 预共享 API Key；任一命中 `app.security.api-keys` 列表即通过 |

### 401 示例

```json
{
  "type": "about:blank",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Invalid or missing API key",
  "instance": "/api/app/merchants"
}
```

校验失败 MUST NOT 透露具体原因（缺失 / 不匹配 / 已失效），统一返回上述结构。

---

## 1. GET `/explore`

聚合"探索"页首屏数据。

- Query：`cityId`（可选；缺省时由后端按"最近一个已上线城市"或默认配置返回）。
- Response 200:
  ```json
  {
    "city": {
      "id": "uuid",
      "chineseName": "上海",
      "englishName": "Shanghai",
      "chineseProvince": "上海",
      "englishProvince": "Shanghai",
      "backgroundImage": "https://..."
    },
    "banners": [
      { "cityId": "uuid", "chineseName": "上海", "backgroundImage": "https://...", "bannerSortOrder": 1 }
    ],
    "empty": false
  }
  ```
- 空状态：`banners=[]` 且 `empty=true`，仍返回 200。
- 数据源：`banners` **直接复用 City 表**——所有 `online=true` 且 `banner_sort_order > 0` 的城市即为 banner，按
  `banner_sort_order ASC` 排序；banner 展示字段（背景图、中文名等）取自该 City；不存在独立 banner 实体。

---

## 2. GET `/cities`

- Response 200:
  ```json
  [
    { "id": "uuid", "chineseName": "上海", "englishName": "Shanghai",
      "chineseProvince": "上海", "englishProvince": "Shanghai",
      "backgroundImage": "https://...", "bannerSortOrder": 1 }
  ]
  ```
- 仅返回 `online=true` 的城市，按 `createdAt DESC` 排序；`bannerSortOrder` 仅作展示字段，不参与列表排序。

---

## 3. GET `/merchants`

商户列表。

- Query：
  - `cityId`（必填）
  - `period`（可选；MENSTRUAL / FOLLICULAR / OVULATION / LUTEAL）
  - `categoryId`（可选，MVP 预留）
  - `page`（默认 1）、`size`（默认 20）
- 排序：`weight DESC, createdAt DESC`。
- Response 200:
  ```json
  {
    "content": [
      {
        "id": "uuid",
        "name": "...",
        "logo": "https://...",
        "address": "...",
        "tags": [{ "id":"uuid", "name":"温馨" }],
        "scores": {
          "safetyEnvironmentPercent": 80,
          "businessRightsPercent": 80,
          "experienceFriendlyPercent": 80,
          "socialContributionPercent": 80
        },
        "loveIndex": { "total": 80, "level": 8 }
      }
    ],
    "page": 1, "size": 20, "totalElements": 35, "totalPages": 2
  }
  ```
- 仅返回 `online=true` 的商户；标签只返回 `online=true` 的。

---

## 4. GET `/merchants/{id}`

商户详情。

- Response 200:
  ```json
  {
    "id": "uuid",
    "name": "...",
    "logo": "https://...",
    "images": ["https://..."],
    "address": "...",
    "longitude": null,
    "latitude": null,
    "recommendedPeriods": ["OVULATION", "LUTEAL"],
    "tags": [{ "id":"uuid", "name":"温馨" }],
    "scores": {
      "safetyEnvironmentPercent": 80,
      "businessRightsPercent": 80,
      "experienceFriendlyPercent": 80,
      "socialContributionPercent": 80
    },
    "loveIndex": { "total": 80, "level": 8 },
    "reviews": [
      { "nickname":"小美", "title":"很棒", "content":"😊 体验超棒" }
    ],
    "story": "≤5000 字纯文本"
  }
  ```
- 404：商户不存在或已下架。
- 标签：仅返回 `online=true` 的；下架标签隐藏但商户本身不下架。

---

## 5. 通用错误

| 状态 | 场景 |
|---|---|
| 200 | 成功（含空状态） |
| 400 | 参数缺失或非法（如 `cityId` 为空） |
| 401 | 缺失或无效 `X-API-Key` |
| 404 | `merchants/{id}` 不存在或已下架 |
| 500 | 系统异常（统一 ProblemDetail） |

---

## 6. 业务规则备注

- 百分制换算：`percent = round(raw * 100 / max)`（四舍五入到整数）。
- 爱女指数：`total = S + L + E + I`（满分 100）；`level = clamp(ceil(total/10), 1, 10)`。
- 客户端固定五星展示评价，**不取后台评分**。
- 经纬度可空；前端按空值容错渲染。
