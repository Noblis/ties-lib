#!/bin/bash
set -e
set -x

# Builds the ties-lib-python-build Docker image which is used for building the
# ties-lib Python artifacts.

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

docker build \
  --build-arg username=${USER} \
  --build-arg uid=$(id -u) \
  --build-arg gid=$(id -g) \
  -t ties-lib-python-build:latest \
  -f Dockerfile .
