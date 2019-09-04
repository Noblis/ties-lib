#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

if [ "$#" -lt 1 ]; then
    echo "error: missing VERSION parameter"
    exit 1
fi

if [ "$#" -gt 1 ]; then
    echo "error: too many parameters"
    exit 1
fi

version=$1

# create directory to assemble release components
rm -rf build/release
mkdir -p build/release

# create directory for release .tgz and .zip files
rm -rf build/release-dist
mkdir -p build/release-dist

# copy java artifacts to release directory
mkdir -p build/release/java
# copy java distribution .tar and .zip files
cp -r java/build/distributions build/release/java/
# copy groovydocs directory
cp -r java/build/docs build/release/java/
# copy .jar files
cp -r java/build/libs build/release/java/

# copy python artifacts to release directory
mkdir -p build/release/python/distributions
# copy python distribution .tar.gz, .zip, and .whl files
cp python/build/dist/* build/release/python/distributions/

# copy schema and example .json files to release directory
cp -r schemata build/release/
cp -r examples build/release/

# copy Sphinx-generated documentation to release directory
mkdir -p build/release/documentation
cp -r documentation/build/html/* build/release/documentation/

# create release .tgz and .zip files
cp -r build/release "build/release-dist/ties-lib-${version}"
tar czf "build/release-dist/ties-lib-${version}.tgz" -C build/release-dist "ties-lib-${version}"
cd build/release-dist && zip -r "ties-lib-${version}.zip" "ties-lib-${version}" && cd -
rm -rf "build/release-dist/ties-lib-${version}"
