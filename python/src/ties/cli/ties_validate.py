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
from argparse import ArgumentParser, RawDescriptionHelpFormatter
from glob import glob
from os.path import abspath

from ties.schema_validation import TiesSchemaValidator
from ties.semantic_validation import TiesSemanticValidator
from ties.util import indent
from ties.util.version import VersionAction, version_string


def _validate(instance_file, instance_path=None):
    try:
        if instance_path:
            _print_status("Validating {}".format(instance_path))
        else:
            _print_status('Validating stdin')
        instance = json.load(instance_file)
        validation_errors = TiesSchemaValidator().all_errors(instance)
        if len(validation_errors) > 0:
            print('ERROR')
            print('Schema validation was unsuccessful:', file=sys.stderr)
            for e in validation_errors:
                print('error:', file=sys.stderr)
                print(indent(str(e), ' ' * 4), file=sys.stderr)
            return 1
        validation_warnings = TiesSemanticValidator().all_warnings(instance)
        if len(validation_warnings) > 0:
            print('WARNING')
            print('Schema validation completed with warnings:', file=sys.stderr)
            for e in validation_warnings:
                print('warning:', file=sys.stderr)
                print(indent(str(e), ' ' * 4), file=sys.stderr)
            return 1
        print('done')
        return 0
    except Exception as e:  # pylint: disable=broad-except
        print('ERROR')
        print('Schema validation was unsuccessful:', file=sys.stderr)
        print('error:', file=sys.stderr)
        print(e, file=sys.stderr)
        return 1


def _print_status(status):
    sys.stdout.write("{}{}".format(status, '.' * (160 - len(status) - 7)))


def _configure_arg_parser():
    parser = ArgumentParser(prog='ties-validate', formatter_class=RawDescriptionHelpFormatter)
    parser.usage = 'ties-validate [-h] [--version] [FILE]...'
    parser.description = 'Validate FILE(s), or standard input, against the TIES 0.9 schema.'
    parser.epilog = ('''\
If FILE arguments are provided, attempts to validate all files. FILE arguments may be provided as either file paths or shell globs.

If no FILE arguments are provided, attempts to read a single JSON object from stdin and validate it.

Returns non-zero exit code if one or more input files fail to validate successfully.
''')
    parser.add_argument('files', metavar='FILE', nargs='*', help='the path to the JSON file(s) to be validated against the schema or - to read from stdin')
    parser.add_argument('--version', action=VersionAction, version="TIES Schema Validator\n{}".format(version_string()), help='prints version information')
    return parser


def _find_input_files(glob_patterns):
    input_files = []
    # attempt to expand each path as a shell glob, if nothing matches the glob, add the glob pattern to the list of input files
    for glob_pattern in glob_patterns:
        glob_files = glob(glob_pattern)
        if len(glob_files) == 0:
            input_files.append(glob_pattern)
        else:
            input_files.extend(glob_files)
    # convert the paths to absolute paths
    input_files = [abspath(input_file) for input_file in input_files]
    # remove duplicate paths
    input_files = list(set(input_files))
    # sort paths
    return sorted(input_files)


def main(argv=None):
    args = _configure_arg_parser().parse_args(argv)

    has_errors = False
    if not args.files or args.files == ['-']:
        # no args were provided, look for input on stdin
        if _validate(sys.stdin) != 0:
            has_errors = True
    else:
        # a list of paths or shell globs was provided
        file_paths = _find_input_files(args.files)
        for file_path in file_paths:
            file_path = abspath(file_path)
            try:
                with open(file_path, 'r') as f:
                    if _validate(f, file_path) != 0:
                        has_errors = True
            except Exception as e:  # pylint: disable=broad-except
                _print_status("Validating {}".format(file_path))
                print('ERROR')
                print('Schema validation was unsuccessful:', file=sys.stderr)
                print('error:', file=sys.stderr)
                print(e, file=sys.stderr)

    if has_errors:
        return 1
    else:
        return 0


if __name__ == '__main__':
    sys.exit(main())
