#!/usr/bin/env bash
# 构建并部署 admin 与 app 两个后端。
#
# 用法：./deploy-apps.sh <版本>
#   <版本>  必填，作为镜像 tag，例如 1.2.0 / 20260531 / git-sha。
#           最终镜像为 ${ADMIN_IMAGE_NAME}:<版本> 与 ${APP_IMAGE_NAME}:<版本>。
#
# 流程：docker build（admin、app 各一次） -> 部署容器（连 postgres、挂日志、设自启）。
# 前置：先跑 deploy-postgres.sh；各项目 target/ 下已用 ./mvnw package 打好 jar。

. "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/_common.sh"

# ---- 校验必填的版本入参 ----
VERSION="${1:-}"
if [ -z "$VERSION" ]; then
  echo "[错误] 缺少版本入参。用法：$0 <版本>  （例如 $0 1.2.0）" >&2
  exit 1
fi

ADMIN_IMAGE="${ADMIN_IMAGE_NAME}:${VERSION}"
APP_IMAGE="${APP_IMAGE_NAME}:${VERSION}"

ensure_network

# ---- 构建镜像 ----
echo "[构建] admin 镜像 $ADMIN_IMAGE"
docker build -t "$ADMIN_IMAGE" "$REPO_ROOT/love-space-admin"

echo "[构建] app 镜像 $APP_IMAGE"
docker build -t "$APP_IMAGE" "$REPO_ROOT/love-space-app"

# ---- 部署 admin（端口 8080，日志挂宿主机）----
remove_if_exists "$ADMIN_CONTAINER"
ADMIN_LOG_DIR="$LOG_BASE_DIR/admin"
prepare_log_dir "$ADMIN_LOG_DIR"

echo "[admin] 启动容器 $ADMIN_CONTAINER（$ADMIN_IMAGE，端口 $ADMIN_HOST_PORT，日志 $ADMIN_LOG_DIR）"
docker run -d \
  --name "$ADMIN_CONTAINER" \
  --network "$NETWORK" \
  --restart "$RESTART_POLICY" \
  -p "${ADMIN_HOST_PORT}:8080" \
  -v "${ADMIN_LOG_DIR}:/app/logs" \
  -e ADMIN_DB_URL="jdbc:postgresql://${PG_CONTAINER}:5432/${PG_DB}" \
  -e ADMIN_DB_USERNAME="$PG_USER" \
  -e ADMIN_DB_PASSWORD="$PG_PASSWORD" \
  -e ADMIN_JWT_SECRET="$ADMIN_JWT_SECRET" \
  -e ALIYUN_OSS_REGION="${ALIYUN_OSS_REGION:-}" \
  -e ALIYUN_OSS_ENDPOINT="${ALIYUN_OSS_ENDPOINT:-}" \
  -e ALIYUN_OSS_BUCKET="${ALIYUN_OSS_BUCKET:-}" \
  -e ALIYUN_OSS_ACCESS_KEY_ID="${ALIYUN_OSS_ACCESS_KEY_ID:-}" \
  -e ALIYUN_OSS_ACCESS_KEY_SECRET="${ALIYUN_OSS_ACCESS_KEY_SECRET:-}" \
  -e ALIYUN_STS_ROLE_ARN="${ALIYUN_STS_ROLE_ARN:-}" \
  "$ADMIN_IMAGE" >/dev/null
echo "[admin] 已启动 ✓ http://localhost:${ADMIN_HOST_PORT}  日志: tail -f ${ADMIN_LOG_DIR}/app.log"

# ---- 部署 app（端口 8081，日志挂宿主机；admin 先起以完成 Liquibase 建表）----
remove_if_exists "$APP_CONTAINER"
APP_LOG_DIR="$LOG_BASE_DIR/app"
prepare_log_dir "$APP_LOG_DIR"

echo "[app] 启动容器 $APP_CONTAINER（$APP_IMAGE，端口 $APP_HOST_PORT，日志 $APP_LOG_DIR）"
docker run -d \
  --name "$APP_CONTAINER" \
  --network "$NETWORK" \
  --restart "$RESTART_POLICY" \
  -p "${APP_HOST_PORT}:8081" \
  -v "${APP_LOG_DIR}:/app/logs" \
  -e APP_DB_URL="jdbc:postgresql://${PG_CONTAINER}:5432/${PG_DB}" \
  -e APP_DB_USERNAME="$PG_USER" \
  -e APP_DB_PASSWORD="$PG_PASSWORD" \
  -e APP_SECURITY_API_KEYS="$APP_SECURITY_API_KEYS" \
  -e ALIYUN_OSS_REGION="${ALIYUN_OSS_REGION:-}" \
  -e ALIYUN_OSS_ENDPOINT="${ALIYUN_OSS_ENDPOINT:-}" \
  -e ALIYUN_OSS_BUCKET="${ALIYUN_OSS_BUCKET:-}" \
  -e ALIYUN_OSS_ACCESS_KEY_ID="${ALIYUN_OSS_ACCESS_KEY_ID:-}" \
  -e ALIYUN_OSS_ACCESS_KEY_SECRET="${ALIYUN_OSS_ACCESS_KEY_SECRET:-}" \
  "$APP_IMAGE" >/dev/null
echo "[app] 已启动 ✓ http://localhost:${APP_HOST_PORT}  日志: tail -f ${APP_LOG_DIR}/app.log"
