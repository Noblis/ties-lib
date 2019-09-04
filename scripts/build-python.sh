#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/../python"

scripts/run-tests.sh 3.6
scripts/run-tests.sh 2.7
scripts/build-dist.sh
