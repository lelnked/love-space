# TC-city-IT-010 DELETE /api/admin/cities/{id} 路线清空后可正常删除城市 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

> 前置：承接 TC-city-IT-009 的城市（`01a01fb5-7729-784e-a42d-c718c64ce655`）与其下唯一路线（`01a01fb5-77a2-7512-87b1-1dda01ba1f0e`）。

## Step 2: 删除该城市下唯一路线

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/routes/01a01fb5-77a2-7512-87b1-1dda01ba1f0e" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，无 Content-Type，Content-Length: 0）:

```
(空响应体)
```

## Step 3: 再次删除该城市

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/cities/01a01fb5-7729-784e-a42d-c718c64ce655" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，无 Content-Type，Content-Length: 0）:

```
(空响应体)
```

## Step 4: 查询已删除城市详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/cities/01a01fb5-7729-784e-a42d-c718c64ce655" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "城市不存在：01a01fb5-7729-784e-a42d-c718c64ce655",
  "path": "/api/admin/cities/01a01fb5-7729-784e-a42d-c718c64ce655"
}
```
