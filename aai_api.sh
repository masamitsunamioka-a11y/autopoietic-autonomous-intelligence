#!/bin/zsh
set -eu
set -o pipefail
export ANTHROPIC_API_KEY="${ANTHROPIC_API_KEY}"
./build_java.sh
mkdir -p filesystem/neural/{areas,neurons} logs
export MAVEN_OPTS="--add-opens java.base/java.lang.reflect=ALL-UNNAMED"
mvn -pl adapters/driving/api exec:exec
echo "$0 (Exit Code: $?)"
