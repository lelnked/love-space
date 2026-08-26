#!/usr/bin/env bash
# 一键部署：postgres -> 构建并部署 admin/app。
#
# 用法：DEPLOY_ENV=<prod|test> ./deploy-all.sh <版本>
#   DEPLOY_ENV  必填，决定读 deploy/.env.<环境>（测试与生产的差异全在那份文件里）。
#   <版本>  必填，作为 admin/app 镜像 tag，透传给 deploy-apps.sh。

set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 加载环境（缺 DEPLOY_ENV 直接在这里报错，不用等子脚本）
. "$DIR/load-env.sh"

VERSION="${1:-}"
if [ -z "$VERSION" ]; then
  echo "[错误] 缺少版本入参。用法：$0 <版本>  （例如 $0 1.2.0）" >&2
  exit 1
fi

bash "$DIR/deploy-postgres.sh"
bash "$DIR/deploy-apps.sh" "$VERSION"

echo
echo "全部部署完成 ✓"
docker ps \
  --filter "name=${PG_CONTAINER:-love-space-postgres}" \
  --filter "name=${ADMIN_CONTAINER:-love-space-admin}" \
  --filter "name=${APP_CONTAINER:-love-space-app}" \
  --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'
