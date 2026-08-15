#!/usr/bin/env bash
# Surface the active OpenSpec change and its next unticked task to the Claude
# Code status bar, so session-start does not need to re-scan changes/ every turn.
# Paired with .claude/rules/openspec-session-protocol.md §1.
set -eo pipefail

cd "$(dirname "$0")/.."

changes_dir="openspec/changes"
[ -d "$changes_dir" ] || { echo "Change: (no openspec/changes)"; exit 0; }

# Active changes = top-level dirs under changes/, excluding archive/
mapfile -t actives < <(find "$changes_dir" -mindepth 1 -maxdepth 1 -type d ! -name archive | sort)

if [ "${#actives[@]}" -eq 0 ]; then
  echo "Change: (none active)"
  exit 0
fi

active="${actives[0]}"
id=$(basename "$active")
suffix=""
[ "${#actives[@]}" -gt 1 ] && suffix=" (+$(( ${#actives[@]} - 1 )) more)"

tasks="$active/tasks.md"
if [ ! -f "$tasks" ]; then
  echo "Change: $id$suffix | (artifacts 未齐 — /opsx:ff)"
  exit 0
fi

next=$(grep -m1 '^- \[ \]' "$tasks" 2>/dev/null | sed 's/^- \[ \] *//' || true)
if [ -z "$next" ]; then
  echo "Change: $id$suffix | (tasks 全勾 — 交付验证/verify/archive)"
else
  echo "Change: $id$suffix | Next: ${next:0:80}"
fi
