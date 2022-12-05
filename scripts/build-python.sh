#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

cd python/scripts
./build-docker-image.sh
./start-build-env.sh
./run-in-build-env.sh scripts/run-tests.sh
./run-in-build-env.sh scripts/build-dist.sh
