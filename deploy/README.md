# 部署脚本

用 docker 部署 love-space 的 PostgreSQL、admin、app 三个容器。镜像需自行构建。

## 文件

| 脚本 | 作用 |
| --- | --- |
| `deploy-postgres.sh` | 部署共用 PostgreSQL（库 `love_space`，命名卷持久化，带健康检查） |
| `deploy-apps.sh <版本>` | **构建并部署** admin 与 app（一个脚本两件事；版本入参作镜像 tag） |
| `deploy-all.sh <版本>` | 按 postgres → admin/app 顺序一键部署 |
| `load-env.sh` | 被上面三个脚本 source，按 `DEPLOY_ENV` 加载 `.env.<环境>` |
| `.env.example` | 环境变量模板（无密钥，入 git） |

> 配置（DB 地址、密码、JWT、API key、OSS、端口、容器名）全部在 `.env.<环境>` 里，
> 脚本顶部只有默认值。**`.env.<环境>` 属于所在的那台服务器**：在服务器上按
> `.env.example` 创建并填值，不入 git，也不要在打包机上留生产密钥的副本。
> `DEPLOY_ENV` 必填，不传脚本直接报错并列出可用环境，防止把测试配置发到生产。

## 使用步骤

```bash
# 1. 打好 jar（构建 image 已并入部署脚本，无需手动 docker build）
cd love-space-admin && ./mvnw package -DskipTests && cd ..
cd love-space-app   && ./mvnw package -DskipTests && cd ..

# 2. 首次：在本机（部署目标就是本机时）或目标服务器上准备环境变量文件
#    cp .env.example .env.prod && vi .env.prod

# 3. 部署（版本入参必填，作为 admin/app 镜像 tag）
cd deploy
DEPLOY_ENV=prod ./deploy-all.sh 1.2.0
# 仅重建/更新 admin、app（postgres 不动）：
DEPLOY_ENV=prod ./deploy-apps.sh 1.2.0
```

> 版本入参会生成镜像 `love-space-admin:<版本>`、`love-space-app:<版本>`，缺省则脚本报错退出。

## 要点

- **自启动**：所有容器用 `--restart unless-stopped`，docker 守护进程开机自启时容器会一起拉起。
  需开机自启请确保 docker 自身已设开机启动：`sudo systemctl enable docker`。
- **日志挂载**：admin/app 容器内日志目录 `/app/logs` 挂到宿主机
  `${LOG_BASE_DIR}/admin`、`${LOG_BASE_DIR}/app`（默认 `/app/loveSpace/logs/...`，
  脚本启动前 `mkdir -p` 自动创建）。每个目录下含 `app.log`、`error.log` 及按天滚动归档。
  容器内以非 root 用户运行，脚本会把宿主机日志目录设为 `777` 以保证可写。
  postgres 日志落在 `/app/loveSpace/pgdata/logs`（按天滚动），或用 `docker logs love-space-postgres` 查看。
- **共用数据库**：admin 与 app 连同一个 `love_space` 库；admin 负责跑 Liquibase 建表，
  所以**首次部署务必让 admin 先于 app 起来**（`deploy-all.sh` 已保证顺序）。
- **容器互联**：三者均用 host 网络模式（`--network host`），直接复用宿主机网络栈，
  admin/app 通过 `.env.<环境>` 里的 `DB_HOST:DB_PORT` 连库；admin 监听 8080、
  app 监听 8081，无需自建 docker 网络，也无需 `-p` 端口映射。
- **数据持久化**：PostgreSQL 挂载统一收在宿主机 `/app/loveSpace/pgdata` 下，分两个子目录
  （脚本启动前 `mkdir -p` 自动创建）：`pgdata/data`（数据 -> 容器 `/var/lib/postgresql/data`）、
  `pgdata/logs`（日志 -> 容器 `/var/log/postgresql`，已开启 `logging_collector` 按天滚动）。
  `docker rm` 容器不丢数据。
- **幂等**：重复执行脚本会先删同名旧容器再重建，可直接用于更新部署。
