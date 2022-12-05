#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

if [ ! -f "/.dockerenv" ]; then
    scripts/run-in-build-env.sh "scripts/validate-examples.sh"
    exit $?
fi

scripts/install-wheel.sh
ties-validate ../examples/*.json
