# TC-activity-IT-009 请求/响应存证

> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，库 `jdbc:postgresql://localhost:25432/love_space`
> 执行时间: 2026-09-02　执行器: api-test-runner
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；
> app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> objectKey 为 test profile 下的直传绑定键（`images/xxx.png` → 绑定后 `bound/xxx.png`）。

## step 0 登录（所有 admin 步骤共用）

```bash
curl -s -X POST "http://localhost:21423/api/admin/auth/login" -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

HTTP 200，返回体含三段式 JWT `token`（长度 251）与 `manager`，记为 `$TOKEN`。

## step 1 前置：创建一个可见活动，detailHtml 含图片标签与文本

```bash
curl -s -X POST "http://localhost:21423/api/admin/activities" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"images": ["images/a009-1.png"], "title": "富文本活动-0902", "subtitle": "图文详情", "tags": ["富文本"], "periods": ["OVULATION"], "level": "L3", "introduction": "简介", "editorNote": "编辑说", "gatheringPlace": "集合", "dismissalPlace": "解散", "transportation": "火车", "visa": "落地签", "landscape": "山地", "itinerary": [{"title": "D1", "content": "内容1"}, {"title": "D2", "content": "内容2"}], "detailHtml": "<p>第一段文字</p><img src=\"images/a009-inline.png\"><p>第二段文字</p>", "online": true}'
```

HTTP 200。admin 侧回读 detailHtml：
```
<p>第一段文字</p><img src="http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a009-inline.png?Expires=1788328211&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BWEFSSv8sJBszbMgs3yZOYyvV4Q%3D"><p>第二段文字</p>
```

## step 2 app 端查询详情

```bash
curl -s "http://localhost:8081/api/app/activities/01a0608f-7e29-7e03-b08e-b6b1eb8bd0b7" -H "X-API-Key: $APP_API_KEY"
```

HTTP 200 / `Content-Type: application/json`
```json
{
 "id": "01a0608f-7e29-7e03-b08e-b6b1eb8bd0b7",
 "images": [
  {
   "id": "bound/a009-1.png",
   "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a009-1.png?Expires=1788328211&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=0NkcBZuaViEBgw0bhZQn%2F%2B0xd7c%3D"
  }
 ],
 "title": "富文本活动-0902",
 "subtitle": "图文详情",
 "tags": [
  "富文本"
 ],
 "periods": [
  "OVULATION"
 ],
 "level": "L3",
 "introduction": "简介",
 "editorNote": "编辑说",
 "gatheringPlace": "集合",
 "dismissalPlace": "解散",
 "transportation": "火车",
 "visa": "落地签",
 "landscape": "山地",
 "itinerary": [
  {
   "title": "D1",
   "content": "内容1"
  },
  {
   "title": "D2",
   "content": "内容2"
  }
 ],
 "detailHtml": "<p>第一段文字</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a009-inline.png?Expires=1788328211&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=BWEFSSv8sJBszbMgs3yZOYyvV4Q%3D\"><p>第二段文字</p>"
}
```
