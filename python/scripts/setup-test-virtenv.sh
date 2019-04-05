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

./setup-base-virtenv.sh test ${python_version}

source ../build/test-virtualenv${python_version}/bin/activate
pip install inflection==0.3.1
pip install jsonschema==3.0.1
pip install six==1.12.0

pip install coverage
pip install pylint
pip install unittest-xml-reporting
