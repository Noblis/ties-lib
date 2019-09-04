#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

if [ "$#" -lt 1 ]; then
    echo "error: missing ENV_NAME and PYTHON_VERSION parameters"
    exit 1
fi

if [ "$#" -lt 2 ]; then
    echo "error: missing PYTHON_VERSION parameter"
    exit 1
fi

if [ "$#" -gt 2 ]; then
    echo "error: too many parameters"
    exit 1
fi

env_name=$1
python_version=$2

mkdir -p build
rm -rf "build/${env_name}-virtualenv${python_version}"

virtualenv -p "python${python_version}" "build/${env_name}-virtualenv${python_version}"
source "build/${env_name}-virtualenv${python_version}/bin/activate"
