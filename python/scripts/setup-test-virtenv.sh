#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

if [ "$#" -lt 1 ]; then
    echo "error: missing PYTHON_VERSION parameter"
    exit 1
fi

if [ "$#" -gt 1 ]; then
    echo "error: too many parameters"
    exit 1
fi

python_version=$1

scripts/setup-base-virtenv.sh test "${python_version}"

source "build/test-virtualenv${python_version}/bin/activate"
pip install -r src/requirements.txt
pip install coverage pylint unittest-xml-reporting
