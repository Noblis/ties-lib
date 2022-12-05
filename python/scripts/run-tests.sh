#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

if [ ! -f "/.dockerenv" ]; then
    scripts/run-in-build-env.sh "scripts/run-tests.sh"
    exit $?
fi

cd src
coverage run --branch --source ties -m ties.test
coverage xml -o ../build/coverage-reports/xml/coverage.xml --omit "ties/util/testing.py,*/test/*,*/__*__.py"
coverage html -d ../build/coverage-reports --omit "ties/util/testing.py,*/test/*,*/__*__.py" --title "ties coverage"
coverage report -m --omit "ties/util/testing.py,*/test/*,*/__*__.py"

pylint -d C,R ties
