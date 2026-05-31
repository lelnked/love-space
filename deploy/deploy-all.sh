#!/usr/bin/env bash
# 一键部署：postgres -> 构建并部署 admin/app。
#
# 用法：./deploy-all.sh <版本>
#   <版本>  必填，作为 admin/app 镜像 tag，透传给 deploy-apps.sh。

set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

VERSION="${1:-}"
if [ -z "$VERSION" ]; then
  echo "[错误] 缺少版本入参。用法：$0 <版本>  （例如 $0 1.2.0）" >&2
  exit 1
fi

bash "$DIR/deploy-postgres.sh"
bash "$DIR/deploy-apps.sh" "$VERSION"

echo
echo "全部部署完成 ✓"
docker ps --filter "network=${NETWORK:-love-space-net}" --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'
