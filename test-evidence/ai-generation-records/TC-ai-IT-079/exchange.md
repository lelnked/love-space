# TC-ai-IT-079 请求/响应存证

> PUT /api/admin/ai/generations/{id}/rating remark 恰 500 字符与全部标签通过
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 造数未评价 SUCCESS R（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-599caa84'"'"', '"'"'anon-gr-ffae9ff1'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now(), now()) RETURNING id;'
```

```
eda230f6-62dc-43f9-aa74-3d9e16566785
INSERT 0 1
```

## step 2 PUT 评价（8 标签 + 500 字中文 remark）

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/generations/eda230f6-62dc-43f9-aa74-3d9e16566785/rating' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"rating": "NEEDS_IMPROVEMENT", "tags": ["IGNORED_INPUT", "WEAK_CYCLE_LINK", "BAD_TONE", "VAGUE", "INAPPLICABLE_ADVICE", "OVER_MEDICALIZED", "FORMAT_ERROR", "OTHER"], "remark": "评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注"}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 3 GET 详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/eda230f6-62dc-43f9-aa74-3d9e16566785' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "eda230f6-62dc-43f9-aa74-3d9e16566785",
 "clientRequestId": "gr-599caa84",
 "anonymousUserId": "anon-gr-ffae9ff1",
 "sceneId": "gr_3f05e38d",
 "sceneName": "生成记录场景S",
 "templateId": "01a17341-31b2-7887-a69c-5ae04440c5fb",
 "templateSeq": 1,
 "templateName": "模板-gr_3f05e38d",
 "appVersion": null,
 "inputs": {
  "age": 28
 },
 "requestMessages": [],
 "requestParams": {},
 "status": "SUCCESS",
 "errorCode": null,
 "errorMessage": null,
 "finalModel": "mock-ok",
 "attemptCount": 1,
 "rawOutput": null,
 "content": null,
 "promptTokens": null,
 "completionTokens": null,
 "latencyMs": 1200,
 "createdAt": "2026-10-25T13:30:18.658274Z",
 "completedAt": "2026-10-25T13:30:18.658274Z",
 "rating": "NEEDS_IMPROVEMENT",
 "ratingTags": [
  "IGNORED_INPUT",
  "WEAK_CYCLE_LINK",
  "BAD_TONE",
  "VAGUE",
  "INAPPLICABLE_ADVICE",
  "OVER_MEDICALIZED",
  "FORMAT_ERROR",
  "OTHER"
 ],
 "ratingRemark": "评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注内容测试评价备注",
 "ratedBy": "admin",
 "ratedAt": "2026-10-25T13:30:18.671177Z",
 "attempts": []
}
```
