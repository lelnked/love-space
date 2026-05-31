#!/usr/bin/env bash
# 用 docker 部署共用的 PostgreSQL（admin 与 app 共享同一个库 love_space）。
# 数据存命名卷，删容器不丢数据；带 healthcheck，方便 admin/app 启动前等待就绪。

set -euo pipefail

# ===== 配置（按需修改）=====
RESTART_POLICY=unless-stopped       # 随 docker 守护进程开机自启
PG_IMAGE=postgres:17
PG_CONTAINER=love-space-postgres
PG_DB=love_space
PG_USER=love_space
PG_PASSWORD=love_space
PG_VOLUME=love-space-pgdata         # 命名数据卷

# 用 host 网络模式：容器直接复用宿主机网络栈，postgres 监听宿主机 5432，
# admin/app 通过 localhost:5432 连库，无需自建 docker 网络或端口映射。

command -v docker >/dev/null 2>&1 || { echo "[错误] 未找到 docker，请先安装 Docker。" >&2; exit 1; }

# 删除同名旧容器（幂等重新部署）。
if docker ps -a --format '{{.Names}}' | grep -qx "$PG_CONTAINER"; then
  echo "[清理] 移除已存在的容器 $PG_CONTAINER"; docker rm -f "$PG_CONTAINER" >/dev/null
fi

echo "[postgres] 启动容器 $PG_CONTAINER（镜像 $PG_IMAGE，host 网络，监听 5432）"
docker run -d \
  --name "$PG_CONTAINER" \
  --network host \
  --restart "$RESTART_POLICY" \
  -e POSTGRES_DB="$PG_DB" \
  -e POSTGRES_USER="$PG_USER" \
  -e POSTGRES_PASSWORD="$PG_PASSWORD" \
  -v "${PG_VOLUME}:/var/lib/postgresql/data" \
  --health-cmd="pg_isready -U $PG_USER -d $PG_DB" \
  --health-interval=5s \
  --health-timeout=3s \
  --health-retries=10 \
  "$PG_IMAGE" >/dev/null

echo "[postgres] 等待数据库就绪..."
for _ in $(seq 1 30); do
  status="$(docker inspect -f '{{.State.Health.Status}}' "$PG_CONTAINER" 2>/dev/null || echo starting)"
  if [ "$status" = "healthy" ]; then
    echo "[postgres] 就绪 ✓ 容器名=$PG_CONTAINER 库=$PG_DB"
    exit 0
  fi
  sleep 2
done

echo "[postgres][警告] 等待超时，请检查：docker logs $PG_CONTAINER" >&2
exit 1
