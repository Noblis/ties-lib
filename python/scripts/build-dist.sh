#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

if [ ! -f "/.dockerenv" ]; then
    scripts/run-in-build-env.sh "scripts/build-dist.sh"
    exit $?
fi

rm -rf build/dist src/build
mkdir -p build/dist

# build_number.txt and build_time.txt are read by version module, $BUILD_NUMBER and $BUILD_TIMESTAMP are provided by Jenkins
if [ -n "${BUILD_NUMBER}" ]; then
  echo "${BUILD_NUMBER}" > src/ties/util/build_number.txt
fi
if [ -n "${BUILD_TIMESTAMP}" ]; then
  echo "${BUILD_TIMESTAMP}" > src/ties/util/build_time.txt
fi


cd src
python3 setup.py build --build-base=build egg_info --egg-base=build sdist --formats=gztar,zip --dist-dir=../build/dist bdist_wheel --dist-dir=../build/dist
rm -f ties/util/build_number.txt ties/util/build_time.txt
