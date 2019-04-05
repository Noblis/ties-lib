#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${here}

if [ "$#" -lt 1 ]; then
    echo "error: missing PYTHON_VERSION parameter"
    exit 1
fi

if [ "$#" -gt 1 ]; then
    echo "error: too many parameters"
    exit 1
fi

python_version=$1

./setup-install-virtenv.sh ${python_version}

source ../build/install-virtualenv${python_version}/bin/activate
pip install ../build/dist/*.whl
