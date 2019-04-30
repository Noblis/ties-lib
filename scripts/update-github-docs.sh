#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${here}/..

documentation/scripts/build-sphinx-documentation.sh
rm -rf docs/documentation/
cp -r documentation/build/html/ docs/documentation/

cd java && ./gradlew groovydoc; cd -
rm -rf docs/groovydoc
cp -r java/build/docs/groovydoc docs/

git add docs
