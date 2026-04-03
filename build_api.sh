#!/bin/zsh
source "$(dirname "$0")/scripts_options.sh"
rm -rf "$HOME/.m2/repository/xxxxx/yyyyy/zzzzz/autopoietic-autonomous-intelligence"
mvn clean install -DskipTests
echo "$0 (Exit Code: $?)"
