#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../backend"
./mvnw -q spring-boot:run
