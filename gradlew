#!/usr/bin/env sh
DIR=$(cd "$(dirname "$0")" && pwd)
sh "$DIR/gradlew" "$@"
