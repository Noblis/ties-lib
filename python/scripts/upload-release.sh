#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${here}/..

scripts/setup-wheel-virtenv.sh
source build/wheel-virtualenv3/bin/activate
twine upload build/dist/ties-lib-*.tar.gz build/dist/ties_lib-*.whl
