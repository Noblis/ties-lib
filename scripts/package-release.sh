#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${here}

if [ "$#" -lt 1 ]; then
    echo "error: missing VERSION parameter"
    exit 1
fi

if [ "$#" -gt 1 ]; then
    echo "error: too many parameters"
    exit 1
fi

version=$1

rm -rf ../build/release
mkdir -p ../build/release

rm -rf ../build/release-dist
mkdir -p ../build/release-dist

mkdir -p ../build/release/java
cp -r ../java/build/distributions ../build/release/java/
cp -r ../java/build/docs ../build/release/java/
cp -r ../java/build/libs ../build/release/java/

mkdir -p ../build/release/python/distributions
cp ../python/build/dist/* ../build/release/python/distributions/

cp -r ../schemata ../build/release/
cp -r ../examples ../build/release/

mkdir -p ../build/release/documentation
cp -r ../documentation/build/html/* ../build/release/documentation/

cp -r ../build/release ../build/release-dist/ties-${version}
tar czf ../build/release-dist/ties-${version}.tgz -C ../build/release-dist ties-${version}
cd ../build/release-dist && zip -r ties-${version}.zip ties-${version} && cd -
rm -rf ../build/release-dist/ties-${version}
