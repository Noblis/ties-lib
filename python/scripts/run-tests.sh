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

scripts/setup-test-virtenv.sh "${python_version}"

source "build/test-virtualenv${python_version}/bin/activate"
cd src
coverage run --branch --source ties -m ties.test
coverage xml -o ../build/coverage-reports/xml/coverage.xml --omit "ties/util/testing.py,*/test/*,*/__*__.py"
coverage html -d ../build/coverage-reports --omit "ties/util/testing.py,*/test/*,*/__*__.py" --title "ties coverage"
coverage report -m --omit "ties/util/testing.py,*/test/*,*/__*__.py"

pylint -d C,R ties
