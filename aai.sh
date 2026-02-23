#!/bin/zsh
set -eu
set -o pipefail
./cleanup_workspace.sh
./build.sh
export MAVEN_OPTS="--add-opens java.base/java.lang.reflect=ALL-UNNAMED"
mvn exec:java -pl cli -Dexec.mainClass="xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli.Cli"
echo "$0 (Exit Code: $?)"
