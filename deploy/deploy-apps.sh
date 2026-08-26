#!/usr/bin/env bash
# 构建并部署 admin 与 app 两个后端。
#
# 用法：DEPLOY_ENV=<prod|test> ./deploy-apps.sh <版本>
#   DEPLOY_ENV 决定读哪份 .env.<环境>（测试/生产只在那里有差异）。
#   <版本>  必填，作为镜像 tag，例如 1.2.0 / 20260531 / git-sha。
#           最终镜像为 ${ADMIN_IMAGE_NAME}:<版本> 与 ${APP_IMAGE_NAME}:<版本>。
#
# 流程：docker build（admin、app 各一次） -> 部署容器（连 postgres、挂日志、设自启）。
# 前置：先跑 deploy-postgres.sh；各项目 target/ 下已用 ./mvnw package 打好 jar。

set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"   # docker build 上下文的父目录

# 加载 .env.<DEPLOY_ENV>：测试/生产的差异全在那里，本脚本只留默认值。
. "$SCRIPT_DIR/load-env.sh"

# ===== 通用配置 =====
RESTART_POLICY="${RESTART_POLICY:-unless-stopped}"   # 随 docker 守护进程开机自启
LOG_BASE_DIR="${LOG_BASE_DIR:-/app/loveSpace/logs}"  # admin/app 日志分别挂到 $LOG_BASE_DIR/{admin,app}
JVM_MAX_HEAP="${JVM_MAX_HEAP:-1000m}"                # 每个后端 JVM 最大堆（-Xmx）

# 用 host 网络模式：容器复用宿主机网络栈，端口即应用自身监听端口。

# ===== PostgreSQL 连接（与 deploy-postgres.sh 共用同一份 .env）=====
PG_DB="${PG_DB:-love_space}"
PG_USER="${PG_USER:-love_space}"
PG_PASSWORD="${PG_PASSWORD:-love_space}"
DB_HOST="${DB_HOST:-172.26.150.96}"
DB_PORT="${DB_PORT:-8954}"

# ===== admin 后端环境变量 =====
ADMIN_IMAGE_NAME="${ADMIN_IMAGE_NAME:-love-space-admin}"
ADMIN_CONTAINER="${ADMIN_CONTAINER:-love-space-admin}"
ADMIN_HOST_PORT="${ADMIN_HOST_PORT:-8080}"
ADMIN_DB_URL="${ADMIN_DB_URL:-jdbc:postgresql://${DB_HOST}:${DB_PORT}/${PG_DB}}"
ADMIN_DB_USERNAME="${ADMIN_DB_USERNAME:-$PG_USER}"
ADMIN_DB_PASSWORD="${ADMIN_DB_PASSWORD:-$PG_PASSWORD}"
ADMIN_JWT_SECRET="${ADMIN_JWT_SECRET:-}"   # openssl rand -base64 32

# ===== app 后端环境变量 =====
APP_IMAGE_NAME="${APP_IMAGE_NAME:-love-space-app}"
APP_CONTAINER="${APP_CONTAINER:-love-space-app}"
APP_HOST_PORT="${APP_HOST_PORT:-8081}"
APP_DB_URL="${APP_DB_URL:-jdbc:postgresql://${DB_HOST}:${DB_PORT}/${PG_DB}}"
APP_DB_USERNAME="${APP_DB_USERNAME:-$PG_USER}"
APP_DB_PASSWORD="${APP_DB_PASSWORD:-$PG_PASSWORD}"
APP_SECURITY_API_KEYS="${APP_SECURITY_API_KEYS:-}"   # openssl rand -hex 32；多 key 用英文逗号分隔

# ===== 阿里云 OSS（admin 与 app 共用）=====
ALIYUN_OSS_REGION="${ALIYUN_OSS_REGION:-}"
ALIYUN_OSS_ENDPOINT="${ALIYUN_OSS_ENDPOINT:-}"
ALIYUN_OSS_BUCKET="${ALIYUN_OSS_BUCKET:-}"
ALIYUN_OSS_ACCESS_KEY_ID="${ALIYUN_OSS_ACCESS_KEY_ID:-}"
ALIYUN_OSS_ACCESS_KEY_SECRET="${ALIYUN_OSS_ACCESS_KEY_SECRET:-}"
ALIYUN_STS_ROLE_ARN="${ALIYUN_STS_ROLE_ARN:-}"   # admin 专用 STS（如使用 STS 直传）

# ---- 必填项校验 ----
for _v in ADMIN_JWT_SECRET APP_SECURITY_API_KEYS; do
  [ -n "${!_v}" ] || { echo "[错误] $_v 为空，请在 $SCRIPT_DIR/.env.$DEPLOY_ENV 中配置。" >&2; exit 1; }
done

# ---- 校验必填的版本入参 ----
VERSION="${1:-}"
if [ -z "$VERSION" ]; then
  echo "[错误] 缺少版本入参。用法：$0 <版本>  （例如 $0 1.2.0）" >&2
  exit 1
fi

command -v docker >/dev/null 2>&1 || { echo "[错误] 未找到 docker，请先安装 Docker。" >&2; exit 1; }

ADMIN_IMAGE="${ADMIN_IMAGE_NAME}:${VERSION}"
APP_IMAGE="${APP_IMAGE_NAME}:${VERSION}"

# ---- 构建镜像 ----
echo "[构建] admin 镜像 $ADMIN_IMAGE"
docker build -t "$ADMIN_IMAGE" "$REPO_ROOT/admin"

echo "[构建] app 镜像 $APP_IMAGE"
docker build -t "$APP_IMAGE" "$REPO_ROOT/app"

# ---- 部署 admin（端口 8080，日志挂宿主机）----
docker ps -a --format '{{.Names}}' | grep -qx "$ADMIN_CONTAINER" && docker rm -f "$ADMIN_CONTAINER" >/dev/null
ADMIN_LOG_DIR="$LOG_BASE_DIR/admin"
mkdir -p "$ADMIN_LOG_DIR" && chmod 777 "$ADMIN_LOG_DIR"   # 容器内以非 root 用户运行，放开写权限

echo "[admin] 启动容器 $ADMIN_CONTAINER（$ADMIN_IMAGE，端口 $ADMIN_HOST_PORT，日志 $ADMIN_LOG_DIR）"
docker run -d \
  --name "$ADMIN_CONTAINER" \
  --network host \
  --restart "$RESTART_POLICY" \
  -v "${ADMIN_LOG_DIR}:/app/logs" \
  -e JAVA_TOOL_OPTIONS="-Xmx${JVM_MAX_HEAP}" \
  -e ADMIN_DB_URL="$ADMIN_DB_URL" \
  -e ADMIN_DB_USERNAME="$ADMIN_DB_USERNAME" \
  -e ADMIN_DB_PASSWORD="$ADMIN_DB_PASSWORD" \
  -e ADMIN_JWT_SECRET="$ADMIN_JWT_SECRET" \
  -e ALIYUN_OSS_REGION="$ALIYUN_OSS_REGION" \
  -e ALIYUN_OSS_ENDPOINT="$ALIYUN_OSS_ENDPOINT" \
  -e ALIYUN_OSS_BUCKET="$ALIYUN_OSS_BUCKET" \
  -e ALIYUN_OSS_ACCESS_KEY_ID="$ALIYUN_OSS_ACCESS_KEY_ID" \
  -e ALIYUN_OSS_ACCESS_KEY_SECRET="$ALIYUN_OSS_ACCESS_KEY_SECRET" \
  -e ALIYUN_STS_ROLE_ARN="$ALIYUN_STS_ROLE_ARN" \
  "$ADMIN_IMAGE" >/dev/null
echo "[admin] 已启动 ✓ http://localhost:${ADMIN_HOST_PORT}  日志: tail -f ${ADMIN_LOG_DIR}/app.log"

# ---- 部署 app（端口 8081，日志挂宿主机；admin 先起以完成 Liquibase 建表）----
docker ps -a --format '{{.Names}}' | grep -qx "$APP_CONTAINER" && docker rm -f "$APP_CONTAINER" >/dev/null
APP_LOG_DIR="$LOG_BASE_DIR/app"
mkdir -p "$APP_LOG_DIR" && chmod 777 "$APP_LOG_DIR"

echo "[app] 启动容器 $APP_CONTAINER（$APP_IMAGE，端口 $APP_HOST_PORT，日志 $APP_LOG_DIR）"
docker run -d \
  --name "$APP_CONTAINER" \
  --network host \
  --restart "$RESTART_POLICY" \
  -v "${APP_LOG_DIR}:/app/logs" \
  -e JAVA_TOOL_OPTIONS="-Xmx${JVM_MAX_HEAP}" \
  -e APP_DB_URL="$APP_DB_URL" \
  -e APP_DB_USERNAME="$APP_DB_USERNAME" \
  -e APP_DB_PASSWORD="$APP_DB_PASSWORD" \
  -e APP_SECURITY_API_KEYS="$APP_SECURITY_API_KEYS" \
  -e ALIYUN_OSS_REGION="$ALIYUN_OSS_REGION" \
  -e ALIYUN_OSS_ENDPOINT="$ALIYUN_OSS_ENDPOINT" \
  -e ALIYUN_OSS_BUCKET="$ALIYUN_OSS_BUCKET" \
  -e ALIYUN_OSS_ACCESS_KEY_ID="$ALIYUN_OSS_ACCESS_KEY_ID" \
  -e ALIYUN_OSS_ACCESS_KEY_SECRET="$ALIYUN_OSS_ACCESS_KEY_SECRET" \
  "$APP_IMAGE" >/dev/null
echo "[app] 已启动 ✓ http://localhost:${APP_HOST_PORT}  日志: tail -f ${APP_LOG_DIR}/app.log"
