#!/bin/bash
set -e
set -x

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${here}/.."

java/scripts/validate-examples.sh
python/scripts/validate-examples.sh
