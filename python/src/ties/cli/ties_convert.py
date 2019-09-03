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

from ties.convert import convert
from ties.schema_order import reorder_ties_json
from ties.util.version import VersionAction, version_string


def _configure_arg_parser():
    parser = ArgumentParser(prog='ties-convert')
    parser.description = 'Converts TIES export.json files from older versions of the schema (0.1.8, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8) to the current version (0.9).'
    parser.add_argument('export_path', metavar='EXPORT_PATH', help='the path to the TIES JSON file or - to read from stdin')
    parser.add_argument('--classification-level', '-c', dest='security_tag', required=False, default=None, help='the classification level of the TIES JSON, required for TIES JSON from pre-0.3 versions of the schema')
    group = parser.add_mutually_exclusive_group()
    group.add_argument('--output-file', '-f', dest='output_file', help='the output file path for the converted TIES JSON')
    group.add_argument('--in-place', '-i', dest='in_place', required=False, default=False, action='store_true', help='modifies the input file in-place, overwriting it with the converted JSON data')
    parser.add_argument('--version', action=VersionAction, version="TIES Schema Converter\n{}".format(version_string()), help='prints version information')
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

    convert(export_json, security_tag=args.security_tag)
    export_json = reorder_ties_json(export_json)

    if args.output_file is not None:
        # write output to the specified file path
        try:
            args.output_file = abspath(expanduser(args.output_file))
            with open(args.output_file, 'w') as f:
                json.dump(export_json, f, indent=2)
            return 0
        except Exception:  # pylint: disable=broad-except
            print("error: could not write to file: {}".format(args.output_file), file=sys.stderr)
            return 1
    elif args.in_place:
        if args.export_path == '-':
            # input came from stdin, write output to stdout
            json.dump(export_json, sys.stdout, indent=2)
            return 0
        else:
            # input came from a file, write output back to the same file
            try:
                args.export_path = abspath(expanduser(args.export_path))
                with open(args.export_path, 'w') as f:
                    json.dump(export_json, f, indent=2)
                return 0
            except Exception:  # pylint: disable=broad-except
                print("error: could not write to file: {}".format(args.export_path), file=sys.stderr)
                return 1
    else:
        # no output file and not in-place, write output to stdout
        json.dump(export_json, sys.stdout, indent=2)
        return 0


if __name__ == '__main__':
    sys.exit(main())
