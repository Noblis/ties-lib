################################################################################
# Copyright 2019 Noblis, Inc                                                   #
#                                                                              #
# Licensed under the Apache License, Version 2.0 (the "License");              #
# you may not use this file except in compliance with the License.             #
# You may obtain a copy of the License at                                      #
#                                                                              #
#    http://www.apache.org/licenses/LICENSE-2.0                                #
#                                                                              #
# Unless required by applicable law or agreed to in writing, software          #
# distributed under the License is distributed on an "AS IS" BASIS,            #
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     #
# See the License for the specific language governing permissions and          #
# limitations under the License.                                               #
################################################################################

from __future__ import print_function, unicode_literals

import json
import sys
from argparse import ArgumentParser
from os.path import abspath, expanduser

from ties.schema_order import reorder_ties_json
from ties.util.version import VersionAction, version_string


def _configure_arg_parser():
    parser = ArgumentParser(prog='ties-format')
    parser.description = 'Formats TIES export.json files'
    parser.add_argument('export_path', metavar='EXPORT_PATH', help='the path to the TIES JSON file or - to read from stdin')
    parser.add_argument('--version', action=VersionAction, version="TIES Schema Formatter\n{}".format(version_string()), help='prints version information')
    return parser


def main(argv=None):
    args = _configure_arg_parser().parse_args(argv)

    if args.export_path == '-':
        # read input from stdin
        try:
            export_json = json.load(sys.stdin)
        except Exception:  # pylint: disable=broad-except
            print('error: could not parse JSON from stdin', file=sys.stderr)
            return 1
    else:
        # read input from a file
        try:
            args.export_path = abspath(expanduser(args.export_path))
            with open(args.export_path, 'r') as f:
                export_json = json.load(f)
        except Exception:  # pylint: disable=broad-except
            print("error: could not read from file: {}".format(args.export_path), file=sys.stderr)
            return 1

    export_json = reorder_ties_json(export_json)
    json.dump(export_json, sys.stdout, indent=2)
    return 0


if __name__ == '__main__':
    sys.exit(main())
