# TC-banner-IT-015 请求/响应存证

用例: GET /api/app/banners 关联城市下架时条目被剔除
执行日期: 2026-08-26 ｜ change: app-list-sort-tiebreak ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 `X-API-Key: test-api-key`
图片 objectKey 用 test profile 的 Stub 校验器接受的固定 key。

> 说明：admin 侧城市下架会**同步**级联把关联 Banner 置为 `online=false`，无法靠时间窗构造用例要求的状态；故按用例步骤 2 允许的「直接在数据库构造」方式，连库把 Banner 的 `online` 改回 `true`（城市仍为下架），得到「Banner online=true + 关联城市 offline」的组合，用以单独验证 app 端查询这道第三重防线。


## Step 1: admin 登录取 JWT

```bash
curl -s -X POST "http://localhost:21423/api/admin/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP/1.1 200）:

```json
{"token":"$TOKEN", ...}
```

## Step 2: 创建上架城市 cityId

```bash
curl -s -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"级联剔除城021653","englishName":"CascOff021653","chineseProvince":"测试省","englishProvince":"TP","online":true}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03be3-2cce-766f-b281-840dbd4e213e","chineseName":"级联剔除城021653","online":true}  // 节选
```

## Step 3: 创建关联该城市的 Banner

```bash
curl -s -X POST "http://localhost:21423/api/admin/banners" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"城下架Banner021653","positionCode":"APP_CITY_OFF","type":"CITY","imageUrls":["images/0197aaaa-bbbb-7000-8000-000000000004.png"],"link":"01a03be3-2cce-766f-b281-840dbd4e213e","sortOrder":0}'
```

实际响应（HTTP/1.1 200）:

```json
{"id":"01a03be3-2ce0-7f77-af2e-9e37ac158465","positionCode":"APP_CITY_OFF","link":"01a03be3-2cce-766f-b281-840dbd4e213e","online":false}  // 节选
```

## Step 4: 上架该 Banner

```bash
curl -s -X POST "http://localhost:21423/api/admin/banners/01a03be3-2ce0-7f77-af2e-9e37ac158465/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":true}'
```

实际响应（HTTP/1.1 200）:

```json
HTTP/1.1 200，online=true
```

## Step 5: 对照基线——城市上架时 app 端可见

```bash
curl -s -H "X-API-Key: test-api-key" "http://localhost:8081/api/app/banners?positionCode=APP_CITY_OFF"
```

实际响应（HTTP/1.1 200）:

```json
数组长度 = 1（城市仍上架时该 Banner 可见，作为对照基线）
```

## Step 6: 下架该城市

```bash
curl -s -X PUT "http://localhost:21423/api/admin/cities/01a03be3-2cce-766f-b281-840dbd4e213e" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"级联剔除城021653","englishName":"CascOff021653","chineseProvince":"测试省","englishProvince":"TP","online":false}'
```

实际响应（HTTP/1.1 200）:

```json
HTTP/1.1 200；随后连库核对：
城下架Banner021653|banner_online=f|级联剔除城021653|city_online=f
（级联已把 Banner 一并下架）
```

## Step 7: 连库把 Banner 强制改回 online=true，构造第三重防线待验状态

```bash
psql "postgresql://iris:$PGPASSWORD@localhost:25432/love_space" -c "update loves_banner set online=true where id='01a03be3-2ce0-7f77-af2e-9e37ac158465';"
psql "postgresql://iris:$PGPASSWORD@localhost:25432/love_space" -c "select b.online as banner_online, c.online as city_online from loves_banner b join loves_city c on c.id=b.linked_entity_id where b.id='01a03be3-2ce0-7f77-af2e-9e37ac158465';"
```

实际响应（psql）:

```json
UPDATE 1
banner_online=t | city_online=f
（已构造出「Banner online=true 但关联城市已下架」的状态）
```

## Step 8: app 端按该展示位查询

```bash
curl -s -i -H "X-API-Key: test-api-key" "http://localhost:8081/api/app/banners?positionCode=APP_CITY_OFF"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: Content-Type: application/json

[]
```
