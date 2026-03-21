#!/bin/zsh
set -eu
set -o pipefail
./build_node.sh
pushd adapters/driving/web
npm run serve
popd
echo "$0 (Exit Code: $?)"
