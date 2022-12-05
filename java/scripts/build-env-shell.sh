#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

docker exec \
  -it \
  -u "`id -u`:`id -g`" \
  -e BUILD_NUMBER -e BUILD_TIMESTAMP \
  -w /opt/build/java \
  ties-lib-java-build bash
