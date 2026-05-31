#!/usr/bin/env bash
# 用 docker 部署共用的 PostgreSQL（admin 与 app 共享同一个库 love_space）。
# 数据存命名卷，删容器不丢数据；带 healthcheck，方便 admin/app 启动前等待就绪。

. "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/_common.sh"

ensure_network
remove_if_exists "$PG_CONTAINER"

# 端口映射：PG_HOST_PORT 非空时仅绑定到本机回环，避免数据库直接对公网暴露。
PORT_ARGS=()
if [ -n "${PG_HOST_PORT:-}" ]; then
  PORT_ARGS=(-p "127.0.0.1:${PG_HOST_PORT}:5432")
fi

echo "[postgres] 启动容器 $PG_CONTAINER（镜像 $PG_IMAGE）"
docker run -d \
  --name "$PG_CONTAINER" \
  --network "$NETWORK" \
  --restart "$RESTART_POLICY" \
  -e POSTGRES_DB="$PG_DB" \
  -e POSTGRES_USER="$PG_USER" \
  -e POSTGRES_PASSWORD="$PG_PASSWORD" \
  -v "${PG_VOLUME}:/var/lib/postgresql/data" \
  "${PORT_ARGS[@]}" \
  --health-cmd="pg_isready -U $PG_USER -d $PG_DB" \
  --health-interval=5s \
  --health-timeout=3s \
  --health-retries=10 \
  "$PG_IMAGE" >/dev/null

echo "[postgres] 等待数据库就绪..."
for i in $(seq 1 30); do
  status="$(docker inspect -f '{{.State.Health.Status}}' "$PG_CONTAINER" 2>/dev/null || echo starting)"
  if [ "$status" = "healthy" ]; then
    echo "[postgres] 就绪 ✓ 容器名=$PG_CONTAINER 库=$PG_DB"
    exit 0
  fi
  sleep 2
done

echo "[postgres][警告] 等待超时，请检查：docker logs $PG_CONTAINER" >&2
exit 1
