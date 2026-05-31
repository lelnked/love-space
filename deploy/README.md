# 部署脚本

用 docker 部署 love-space 的 PostgreSQL、admin、app 三个容器。镜像需自行构建。

## 文件

| 脚本 | 作用 |
| --- | --- |
| `deploy.env.example` | 配置示例，`cp` 成 `deploy.env` 后修改（脚本自动加载） |
| `deploy-postgres.sh` | 部署共用 PostgreSQL（库 `love_space`，命名卷持久化，带健康检查） |
| `deploy-apps.sh <版本>` | **构建并部署** admin 与 app（一个脚本两件事；版本入参作镜像 tag） |
| `deploy-all.sh <版本>` | 按 postgres → admin/app 顺序一键部署 |
| `_common.sh` | 共用逻辑，被其它脚本 source，不单独执行 |

## 使用步骤

```bash
# 1. 打好 jar（构建 image 已并入部署脚本，无需手动 docker build）
cd love-space-admin && ./mvnw package -DskipTests && cd ..
cd love-space-app   && ./mvnw package -DskipTests && cd ..

# 2. 准备配置
cd deploy
cp deploy.env.example deploy.env
vim deploy.env            # 改密码、JWT、API key、OSS 等

# 3. 部署（版本入参必填，作为 admin/app 镜像 tag）
./deploy-all.sh 1.2.0
# 仅重建/更新 admin、app（postgres 不动）：
./deploy-apps.sh 1.2.0
```

> 版本入参会生成镜像 `love-space-admin:<版本>`、`love-space-app:<版本>`，缺省则脚本报错退出。

## 要点

- **自启动**：所有容器用 `--restart unless-stopped`，docker 守护进程开机自启时容器会一起拉起。
  需开机自启请确保 docker 自身已设开机启动：`sudo systemctl enable docker`。
- **日志挂载**：admin/app 容器内日志目录 `/app/logs` 挂到宿主机
  `${LOG_BASE_DIR}/admin`、`${LOG_BASE_DIR}/app`（默认 `/opt/love-space/logs/...`）。
  容器内以非 root 用户运行，脚本会把宿主机日志目录设为 `777` 以保证可写。
- **共用数据库**：admin 与 app 连同一个 `love_space` 库；admin 负责跑 Liquibase 建表，
  所以**首次部署务必让 admin 先于 app 起来**（`deploy-all.sh` 已保证顺序）。
- **容器互联**：三者在 docker 网络 `love-space-net` 内，admin/app 通过容器名
  `love-space-postgres` 连库，无需依赖宿主机端口。
- **数据持久化**：PostgreSQL 数据存命名卷 `love-space-pgdata`，`docker rm` 容器不丢数据。
- **幂等**：重复执行脚本会先删同名旧容器再重建，可直接用于更新部署。
