#!/bin/zsh
source "$(dirname "$0")/scripts_options.sh"
pushd adapters/driving/web
    npm install
    npm run build
popd
echo "$0 (Exit Code: $?)"
