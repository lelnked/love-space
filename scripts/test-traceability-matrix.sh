#!/usr/bin/env bash
# 追溯矩阵脚本自检：沙箱夹具覆盖 正向未覆盖/反向悬空/状态存疑/change 模式 delta 叠加 四条核心逻辑
set -euo pipefail
SCRIPT="$(cd "$(dirname "$0")" && pwd)/generate-traceability-matrix.js"
SB=$(mktemp -d)
trap 'rm -rf "$SB"' EXIT
cd "$SB"

mkdir -p openspec/specs/auth openspec/changes/add-user-login/specs/auth tests/auth contracts
cat > openspec/specs/auth/spec.md <<'EOF'
### Requirement: 用户登录
#### Scenario: 成功登录
#### Scenario: 密码错误
EOF
cat > openspec/changes/add-user-login/specs/auth/spec.md <<'EOF'
## ADDED Requirements
### Requirement: 账号锁定
#### Scenario: 连续失败锁定
EOF
cat > openspec/changes/add-user-login/test-cases.md <<'EOF'
## 新增用例
- TC-auth-IT-001
EOF
cat > tests/auth/it.md <<'EOF'
### TC-auth-IT-001: POST /auth/login 登录成功
**关联需求**: auth/用户登录#成功登录
**关联契约**: api-spec.json#/paths/~1auth~1login/post
**来源**: add-user-login
**状态**: ✅ 通过
**执行存证**: `test-evidence/regression/auth/TC-auth-IT-001/`
EOF
cat > tests/auth/web.md <<'EOF'
### TC-auth-WEB-001: 登录成功跳转
**关联需求**: auth/用户登录#成功登录
**来源**: add-user-login
**状态**: ⬜ 未测试
### TC-auth-WEB-002: 悬空用例
**关联需求**: auth/不存在的需求#随便
**来源**: add-user-login
**状态**: ⬜ 未测试
EOF
cat > contracts/api-spec.json <<'EOF'
{ "paths": { "/auth/login": { "post": { "x-requirement": "auth/用户登录" } },
             "/auth/reset": { "post": { "x-requirement": "auth/用户登录" } } } }
EOF

node "$SCRIPT" > /dev/null
M=traceability-matrix.md
grep -q '未覆盖：auth/用户登录#密码错误' $M            # 正向：Scenario 无 WEB/APP/UT
grep -q '未覆盖：POST /auth/reset 无 IT 用例' $M       # 正向：写操作无 IT
grep -q '悬空用例：TC-auth-WEB-002' $M                 # 反向：关联需求不存在
grep -q '状态存疑：TC-auth-IT-001' $M                  # ✅ 但存证目录不存在
! grep -q '悬空用例：TC-auth-IT-001' $M                # 正常用例不误报

node "$SCRIPT" --change add-user-login > /dev/null
CM=openspec/changes/add-user-login/traceability-matrix.md
grep -q '账号锁定' $CM                                  # delta ADDED 叠加进锚点表
grep -q '未覆盖：auth/账号锁定#连续失败锁定' $CM        # delta 场景纳入正向核对
grep -q 'TC-auth-IT-001' $CM                            # change 清单点名的 TC 在范围内

echo '✅ 全部断言通过'
