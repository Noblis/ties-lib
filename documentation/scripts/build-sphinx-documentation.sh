#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${here}

python_version=3.5

rm -rf ../build
mkdir -p ../build

../../python/scripts/setup-base-virtenv.sh sphinx ${python_version}
source ../../python/build/sphinx-virtualenv${python_version}/bin/activate
pip install sphinx

cd ..
make html
