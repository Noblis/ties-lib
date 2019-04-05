#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${here}

cd ../java
./gradlew installDist
build/install/ties-lib/bin/ties-validate ../examples/*.json

cd ../python
scripts/install-wheel.sh 2.7
source build/install-virtualenv2.7/bin/activate
ties-validate ../examples/*.json
deactivate

scripts/install-wheel.sh 3
source build/install-virtualenv3/bin/activate
ties-validate ../examples/*.json
