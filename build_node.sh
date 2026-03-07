#!/bin/zsh
set -eu
set -o pipefail
pushd adapters/driving/web
npm install
npm run build
popd
echo "$0 (Exit Code: $?)"
