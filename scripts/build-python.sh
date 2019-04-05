#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${here}

../python/scripts/run-tests.sh 3.5
../python/scripts/run-tests.sh 2.7
../python/scripts/build-dist.sh
