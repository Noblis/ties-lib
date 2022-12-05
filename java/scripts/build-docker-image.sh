#!/bin/bash
set -e
set -x

# Builds the ties-lib-java-build Docker image which is used for building the
# ties-lib Java artifacts.

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

docker build \
  --build-arg username=${USER} \
  --build-arg uid=$(id -u) \
  --build-arg gid=$(id -g) \
  -t ties-lib-java-build:latest \
  -f Dockerfile .
