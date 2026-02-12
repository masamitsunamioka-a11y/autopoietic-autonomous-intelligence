#!/bin/bash
set -eu
set -o pipefail
rm -rf $HOME/.m2/repository/xxxxx/yyyyy/zzzzz/self-evolving-autonomous-agent
mvn clean install -DskipTests
echo "$0 (Exit Code: $?)"
