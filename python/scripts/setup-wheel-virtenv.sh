#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

python_version=3.6

scripts/setup-base-virtenv.sh wheel "${python_version}"

source "build/wheel-virtualenv${python_version}/bin/activate"
pip install setuptools wheel twine
