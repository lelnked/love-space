#!/usr/bin/env bash
# 环境变量加载器：被 deploy-*.sh source。
# 用法：DEPLOY_ENV=prod ./deploy-all.sh 1.2.0   （DEPLOY_ENV 取值即 .env.<环境> 的后缀）
# 已经存在的环境变量优先级最高（.env 文件里用 ${X:-} 兜底不覆盖），方便临时覆盖单项。

_ENV_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOY_ENV="${DEPLOY_ENV:-}"
if [ -z "$DEPLOY_ENV" ]; then
  echo "[错误] 未指定 DEPLOY_ENV。用法：DEPLOY_ENV=prod $0 ...  可用环境：" >&2
  ls -1 "$_ENV_DIR"/.env.* 2>/dev/null | grep -v '\.env\.example$' | sed 's#.*/\.env\.#  - #' >&2
  exit 1
fi

_ENV_FILE="$_ENV_DIR/.env.$DEPLOY_ENV"
[ -f "$_ENV_FILE" ] || { echo "[错误] 找不到环境文件 $_ENV_FILE（参考 .env.example 新建）" >&2; exit 1; }

set -a
# shellcheck disable=SC1090
. "$_ENV_FILE"
set +a
echo "[env] 使用环境 $DEPLOY_ENV（$_ENV_FILE）"
