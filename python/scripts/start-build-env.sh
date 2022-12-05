#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

docker stop ties-lib-python-build || true
docker rm ties-lib-python-build || true

docker run \
  --detach \
  --name ties-lib-python-build \
  -u "`id -u`:`id -g`" \
  -v `realpath ..`:/opt/build \
  ties-lib-python-build:latest
