#!/bin/bash
set -x
set -e

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/../java"

./gradlew clean build
./gradlew dist
