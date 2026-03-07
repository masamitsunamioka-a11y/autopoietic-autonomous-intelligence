#!/bin/zsh
set -eu
set -o pipefail
./build_java.sh
export MAVEN_OPTS="--add-opens java.base/java.lang.reflect=ALL-UNNAMED"
mvn -pl adapters/driving/api exec:exec
echo "$0 (Exit Code: $?)"
