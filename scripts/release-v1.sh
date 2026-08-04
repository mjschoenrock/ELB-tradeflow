#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "This script must be run from inside a Git repository" >&2
  exit 1
fi

if [[ -z "${1:-}" ]]; then
  echo "Usage: $0 <release-version>" >&2
  exit 1
fi

VERSION="$1"

echo "Preparing release $VERSION"

git checkout main
git merge --no-ff develop -m "Release $VERSION"
git tag -a "$VERSION" -m "TradeFlow $VERSION"
git push origin main "$VERSION"

echo "Release $VERSION created."
