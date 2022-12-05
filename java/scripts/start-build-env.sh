#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

docker stop ties-lib-java-build || true
docker rm ties-lib-java-build || true

docker run \
  --detach \
  --name ties-lib-java-build \
  -u "`id -u`:`id -g`" \
  -v `realpath ..`:/opt/build \
  ties-lib-java-build:latest
