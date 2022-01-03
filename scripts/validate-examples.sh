#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}"

cd ../java
./gradlew installDist
build/install/ties-lib/bin/ties-validate ../examples/*.json

cd ../python
scripts/install-wheel.sh 3.6
source build/install-virtualenv3.6/bin/activate
ties-validate ../examples/*.json
