# Quickstart: 阿里 OSS STS 直传 & ImageResponse 统一

本特性的本地联调步骤。

## 0. 一次性云资源准备

按 `contracts/bucket-lifecycle.md`：
- 创建 OSS bucket，开启 CORS（允许 PUT、GET、HEAD from `http://100.100.117.79:21424` 与 `http://localhost:5173`），并配置 `images/` 前缀 1 天过期的 lifecycle 规则。
- 创建 RAM Role `LoveSpaceOssUploader`，挂载策略只允许 `oss:PutObject` 到 `<bucket>/images/*`。记录 `roleArn`。
- 创建主账号 / 子账号 AK，用于：
  - 服务端 OSS SDK（CopyObject / DeleteObject / GetObjectMetadata / 签名 GET URL）
  - 服务端 STS SDK（AssumeRole）

## 1. 准备本地配置

环境变量（**禁止**写入 `application*.yml`）：

```bash
# OSS 与 STS 共用同一对凭证与 region（admin 端合并后顶层共享）
export ALIYUN_OSS_REGION=cn-shanghai
export ALIYUN_OSS_ACCESS_KEY_ID=…
export ALIYUN_OSS_ACCESS_KEY_SECRET=…

export ALIYUN_OSS_ENDPOINT=https://oss-cn-shanghai.aliyuncs.com
export ALIYUN_OSS_BUCKET=love-space-dev

export ALIYUN_STS_ROLE_ARN=acs:ram::123456:role/LoveSpaceOssUploader
export ALIYUN_STS_ROLE_SESSION_NAME=love-space-admin
```

> 该凭证需同时具备 OSS 读写（head / copy / 签名）与 `sts:AssumeRole` 权限。STS 接入点由 SDK 依据 `ALIYUN_OSS_REGION` 自行解析，无需单独配置。

可选覆写：`ALIYUN_OSS_URL_EXPIRATION_SECONDS`（默认 1800）、`ALIYUN_STS_DURATION_SECONDS`（默认 900）。

> ⚠️ 已移除（admin 合并后不再使用）：`ALIYUN_STS_ENDPOINT`、`ALIYUN_STS_REGION_ID`、`ALIYUN_STS_ACCESS_KEY_ID`、`ALIYUN_STS_ACCESS_KEY_SECRET`。

## 2. 启动 admin 后端

```bash
unset SPRING_DATASOURCE_URL  # 见 memory project_shell_env_spring_datasource_override.md
cd love-space-admin
SPRING_PROFILES_ACTIVE=test ./mvnw spring-boot:run
```

任何 OSS / STS 必填项缺失，启动 fail-fast 并打印 `OssProperties` / `StsProperties` validation failed。

## 3. 启动 web 前端

```bash
cd love-space-web
npm install
npm run dev -- --mode test  # 端口 21424
```

或一键：`/skill lan-all-dev-company`。

## 4. 端到端验证（手工）

1. 浏览器打开 `http://100.100.117.79:21424/`，登录 admin。
2. 进入 Banner 管理 → 新建：选图 → 前端 `POST /api/admin/files/upload-credentials { contentType: "image/png" }` 拿到 PostObject 表单签名 + `objectKey=images/<uuid>.png`。
3. 前端用 `multipart/form-data` 表单 POST 到 `host` 直传到 OSS（**不经过 admin 后端，浏览器不接触 accessKeySecret**）；成功后表单内暂存 `images/<uuid>.png`。
4. 提交 banner 创建：`POST /api/admin/banners { imageUrls: ["images/<uuid>.png", ...] }`。
5. 服务端校验：
   - DTO 正则通过
   - `ObjectKeyValidator.validateAndBind`：head OSS 对象、校验 MIME / size → copy 到 `bound/<uuid>.png` → delete `images/<uuid>.png`
   - 业务表写入 `bound/<uuid>.png`
6. 列表 / 详情接口返回 `ImageResponse[]`，每个 `url` 是签名 URL；新标签页打开应能显示图片。
7. 同上验证 Merchant（logo + images）与 City（backgroundImage）。
8. 复制 url 去掉 `Signature` query → 应得 `AccessDenied`，满足 SC-002。
9. 直传后**不**提交业务 → 24h 后 OSS 控制台确认 `images/<uuid>.png` 已被 lifecycle 删除，满足 SC-007。
10. 用旧 objectKey（已被绑定后从 `images/` 删除）再次提交 → 应得 422 "图片对象不可用"，满足 SC-005。

## 5. 测试

```bash
cd love-space-admin && ./mvnw test
cd love-space-app   && ./mvnw test
cd love-space-web   && npm run lint && npm run build
```

所有测试 MUST 在无真实 OSS / STS 凭据情况下通过（`StsCredentialIssuer` / `ObjectKeyValidator` / `ImageUrlSigner` 均以 stub 注入）。

## 6. 常见问题

| 现象 | 排查 |
|---|---|
| 启动失败 `OssProperties …` / `StsProperties …` 校验错 | 检查环境变量是否齐备；不要写进 yml。 |
| 前端直传 403 InvalidAccessKeyId | STS 过期（>15min）；重新调 `upload-credentials`。 |
| 前端直传 403 AccessDenied | RAM Role 策略未覆盖该 objectKey；检查 inline policy 拼接是否正确。 |
| 提交业务 422 "图片对象不可用" | objectKey 不存在 / MIME 错 / 太大 / 重复提交已绑定 key。 |
| 列表 url 401 / 403 | 签名过期（默认 30min）→ 重新拉取列表 / 详情。 |
| 前端直传 CORS 报错 | 在 OSS bucket → CORS 配置中加入前端 origin，允许 PUT/GET/HEAD + 常用 header。 |
