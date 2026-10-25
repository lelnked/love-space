# TC-ai-IT-074 请求/响应存证

> GET /api/admin/ai/generations/page 列表条目仅十一个字段、不含正文
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 app 生成造数

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-gr-43af9424' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_3f05e38d", "clientRequestId": "c-70b18192-3a2b-4802-a5fd-72019fbd8481", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```
{
 "requestId": "01a17341-36cb-70cb-a741-a2158da196ed",
 "clientRequestId": "c-70b18192-3a2b-4802-a5fd-72019fbd8481",
 "sceneId": "gr_3f05e38d",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 2 GET requestId=<requestId>

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?requestId=01a17341-36cb-70cb-a741-a2158da196ed&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "01a17341-36cb-70cb-a741-a2158da196ed",
   "createdAt": "2026-10-25T13:30:16.117394Z",
   "anonymousUserId": "anon-gr-43af9424",
   "sceneId": "gr_3f05e38d",
   "sceneName": "生成记录场景S",
   "templateId": "01a17341-31b2-7887-a69c-5ae04440c5fb",
   "templateSeq": 1,
   "finalModel": "mock-ok",
   "status": "SUCCESS",
   "latencyMs": 74,
   "rating": null
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```
