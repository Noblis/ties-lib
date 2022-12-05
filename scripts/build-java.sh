#!/bin/bash
set -x
set -e

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

cd java/scripts
./build-docker-image.sh
./start-build-env.sh
./run-in-build-env.sh ./gradlew clean build dist
