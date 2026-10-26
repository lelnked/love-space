#!/usr/bin/env bash
# 本地同时启动 love-space-admin（Spring Boot）和 love-space-web（Vite），Ctrl+C 一起停。
# admin 用 local profile（application-local.yml：本机 PostgreSQL + AI 环境变量占位）；web 由 Vite 自动读取 love-space-web/.env*。
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

trap 'kill 0' EXIT

# ~/.bashrc 里的 SERVER_PORT / SPRING_DATASOURCE_URL 属于别的项目，会覆盖 yml，这里剔掉
# SPRING_PROFILES_ACTIVE=local：显式激活 local profile。application.yml 默认是 prod，不激活不会读 application-local.yml
(cd "$ROOT/love-space-admin" && env -u SERVER_PORT -u SPRING_DATASOURCE_URL SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run) &
(cd "$ROOT/love-space-web" && npm run dev -- --host) &

wait
