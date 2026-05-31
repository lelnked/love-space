#!/usr/bin/env bash
# 各 deploy-*.sh 共用的初始化逻辑：加载配置、校验 docker、确保网络存在。
# 不单独执行，被其它脚本 source。

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# 仓库根目录（deploy/ 的上一级），用作 docker build 的上下文路径。
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# 加载默认值，再用 deploy.env 覆盖（存在时）。
set -a
# shellcheck disable=SC1091
[ -f "$SCRIPT_DIR/deploy.env.example" ] && . "$SCRIPT_DIR/deploy.env.example"
# shellcheck disable=SC1091
[ -f "$SCRIPT_DIR/deploy.env" ] && . "$SCRIPT_DIR/deploy.env"
set +a

command -v docker >/dev/null 2>&1 || { echo "[错误] 未找到 docker，请先安装 Docker。" >&2; exit 1; }

# 确保互联网络存在。
ensure_network() {
  if ! docker network inspect "$NETWORK" >/dev/null 2>&1; then
    echo "[网络] 创建 docker 网络 $NETWORK"
    docker network create "$NETWORK" >/dev/null
  fi
}

# 删除同名旧容器（实现幂等重新部署）。
remove_if_exists() {
  local name="$1"
  if docker ps -a --format '{{.Names}}' | grep -qx "$name"; then
    echo "[清理] 移除已存在的容器 $name"
    docker rm -f "$name" >/dev/null
  fi
}

# 准备宿主机日志目录并放开写权限（容器内以非 root 的 spring 用户运行）。
prepare_log_dir() {
  local dir="$1"
  mkdir -p "$dir"
  chmod 777 "$dir"
}
