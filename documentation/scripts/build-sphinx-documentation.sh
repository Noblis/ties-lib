#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

if [ ! -f "/.dockerenv" ]; then
    ../python/scripts/run-in-build-env.sh "../documentation/scripts/build-sphinx-documentation.sh"
    exit $?
fi

rm -rf build
mkdir -p build

make html
