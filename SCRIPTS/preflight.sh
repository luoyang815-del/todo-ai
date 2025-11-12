#!/usr/bin/env bash
set -euo pipefail
./gradlew --version
./gradlew lint || true
./gradlew :app:assembleRelease -x test