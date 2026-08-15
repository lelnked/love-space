# 域注册表（闸门）

> 本文件是测试域的**唯一注册表**：新域必须先在此登记，test-cases artifact 与各端 runner
> 都以它裁决归属。三处目录同 slug 镜像：`openspec/specs/{domain}/`、`tests/{domain}/`、
> `test-evidence/regression/{domain}/`。域数量 5~15 个为宜，按业务能力划分（非按技术层）。
> 「端」列是测试端插拔的登记点：IT 恒有不登记；`web`/`app` 登记了才产出对应用例（留空 = 纯 API 域）。

## 已注册域

| 域 slug | 职责 | 接口路径前缀 | 端 | 页面域 | 用例文件 |
|---|---|---|---|---|---|
| auth | 运营账号（Manager）登录认证与会话 | `/api/admin/auth/*` | web | `/love-space/signin`、后台外壳（顶栏/侧栏） | `tests/auth/{it,web}.md` |
| manager | 运营账号管理 | `/api/admin/managers/*` | web | `/love-space/managers` | `tests/manager/{it,web}.md` |
| city | 城市管理（含级联上下架） | `/api/admin/cities/*`、`/api/app/cities/*` | web | `/love-space/cities` | `tests/city/{it,web}.md` |
| merchant | 商户管理（含分类/标签/图片/营业时段/评价） | `/api/admin/merchants/*`、`/api/admin/categories/*`、`/api/admin/tags/*`、`/api/app/merchants/*`、`/api/app/categories/*` | web | `/love-space/merchants`、`/love-space/categories`、`/love-space/tags` | `tests/merchant/{it,web}.md` |
| banner | Banner 管理（含排序） | `/api/admin/banners/*`、`/api/app/banners/*` | web | `/love-space/banners` | `tests/banner/{it,web}.md` |

跨域全链路 smoke（极少数）落 `tests/flows/web.md`，不注册为业务域。
（二期新模块——地图/推荐清单/路线/大使/活动/文章/精选信息流——落地时在此先登记新域再产用例。）

## baseUrl 白名单

runner 只允许对下列地址发请求/导航（回归轮必校验；不在名单内 → 拒绝执行并提示登记）：

| 用途 | 地址 | 说明 |
|---|---|---|
| admin 后端 IT（api-test-runner） | `http://localhost:8080` | love-space-admin，本机直连，路径 `/api/admin/*`，JWT 认证 |
| admin 后端 IT（test profile） | `http://localhost:21423` | `application-test.yml` 实例，e2e 专用库 |
| admin 后端 IT（备用，Tailscale） | `http://100.100.117.79:8080` | 远程浏览器侧核对网络请求用（test profile 为 `:21423`） |
| app 后端 IT（api-test-runner） | `http://localhost:8081` | love-space-app，路径 `/api/app/*`，请求头 API-key 认证（`APP_SECURITY_API_KEYS`） |
| 前端 WEB（web-test-runner） | `http://100.100.117.79:5173/love-space/` | 远程 Playwright 浏览器不可达 localhost，必须用本机 Tailscale IP；Vite 需 `--host` 启动 |

⚠️ 干扰项（绝不要测）：本机其他端口挂着的别的项目实例；执行前先确认目标前端实例的
`VITE_ADMIN_API_BASE` 指向上表 admin 后端地址（探测方法见 run-web-test skill）。

## 存证口径（沿用 spec-kit 版 §8.1 三条）

1. **默认明文 fixture**：测试夹具数据（用户名/密码等）默认明文入存证，便于复现；
2. **真机密脱敏**：真实机密一律记 `$变量名`（如 `$PROD_TOKEN`），不落明文；redact 默认脱敏 password/token/authorization；
3. **baseUrl 白名单**：存证中的请求地址必须命中上表；回归轮开始前 runner 先校验。

存证目录：交付轮 `test-evidence/{change-id}/{TC完整ID}/`，回归轮 `test-evidence/regression/{domain}/{TC完整ID}/`
（目录段用完整 TC ID，如 `TC-auth-IT-001`，规避短编号撞名老坑）。每轮回归覆盖旧存证，历史靠 git。
