#!/usr/bin/env bash
# 本地同时启动 love-space-admin（Spring Boot）和 love-space-web（Vite），Ctrl+C 一起停。
# admin 用 application*.yml 的默认配置；web 由 Vite 自动读取 love-space-web/.env*。
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

trap 'kill 0' EXIT

# ~/.bashrc 里的 SERVER_PORT / SPRING_DATASOURCE_URL 属于别的项目，会覆盖 yml，这里剔掉
(cd "$ROOT/love-space-admin" && env -u SERVER_PORT -u SPRING_DATASOURCE_URL ./mvnw spring-boot:run) &
(cd "$ROOT/love-space-web" && npm run dev -- --host) &

wait
