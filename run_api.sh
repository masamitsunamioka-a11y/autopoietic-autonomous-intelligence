#!/bin/zsh
set -eu
set -o pipefail
export ANTHROPIC_API_KEY="${ANTHROPIC_API_KEY}"
./build_java.sh
mkdir -p filesystem/neural/{areas,neurons} logs
mvn wildfly-jar:package -f adapters/driving/api/pom.xml -Dmaven.test.skip=true
java -jar adapters/driving/api/target/api-1.0.0-SNAPSHOT-bootable.jar
echo "$0 (Exit Code: $?)"
