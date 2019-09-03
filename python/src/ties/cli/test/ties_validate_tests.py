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

import os
import shutil
import unittest
from os.path import abspath
from unittest import TestCase

from ties.cli.ties_validate import main
from ties.util.testing import cli_test

short_usage = 'usage: ties-validate [-h] [--version] [FILE]...'

long_usage = """\
{}

Validate FILE(s), or standard input, against the TIES 0.9 schema.

positional arguments:
  FILE        the path to the JSON file(s) to be validated against the schema
              or - to read from stdin

optional arguments:
  -h, --help  show this help message and exit
  --version   prints version information

If FILE arguments are provided, attempts to validate all files. FILE arguments may be provided as either file paths or shell globs.

If no FILE arguments are provided, attempts to read a single JSON object from stdin and validate it.

Returns non-zero exit code if one or more input files fail to validate successfully.
""".format(short_usage)

minimal_valid_json = """\
{
  "version": "0.9",
  "securityTag": "UNCLASSIFIED",
  "objectItems": [
    {
      "objectId": "a",
      "sha256Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "md5Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "authorityInformation": {
        "securityTag": "UNCLASSIFIED"
      }
    }
  ]
}"""

minimal_invalid_json = '{}'


def _make_status(message, status):
    return "{}{}{}".format(message, '.' * (160 - len(message) - 7), status)


class TiesValidateTests(TestCase):

    def setUp(self):
        os.mkdir('../build/ties_validate_tests')
        with open('../build/ties_validate_tests/success1.json', 'w') as f:
            f.write(minimal_valid_json)
        with open('../build/ties_validate_tests/success2.json', 'w') as f:
            f.write(minimal_valid_json)
        with open('../build/ties_validate_tests/failure1.json', 'w') as f:
            f.write(minimal_invalid_json)
        with open('../build/ties_validate_tests/failure2.json', 'w') as f:
            f.write(minimal_invalid_json)

    def tearDown(self):
        shutil.rmtree('../build/ties_validate_tests', ignore_errors=True)

    def test_help_short(self):
        with cli_test(self, main) as t:
            t.args(['-h'])
            t.return_code(0)
            t.stdout_text(long_usage)
            t.stderr()

    def test_help_long(self):
        with cli_test(self, main) as t:
            t.args(['--help'])
            t.return_code(0)
            t.stdout_text(long_usage)
            t.stderr()

    def test_no_args_success(self):
        with cli_test(self, main) as t:
            t.args([])
            t.return_code(0)
            t.stdin(minimal_valid_json)
            t.stdout_text(_make_status('Validating stdin', 'done'))
            t.stderr()

    def test_no_args_failure(self):
        with cli_test(self, main) as t:
            t.args([])
            t.return_code(1)
            t.stdin(minimal_invalid_json)
            t.stdout_text(_make_status('Validating stdin', 'ERROR'))
            t.stderr('Schema validation was unsuccessful:')
            t.stderr('error:')
            t.stderr('    required properties [objectItems, securityTag, version] are missing')
            t.stderr('    location: /')

    def test_stdin_success(self):
        with cli_test(self, main) as t:
            t.args(['-'])
            t.return_code(0)
            t.stdin(minimal_valid_json)
            t.stdout_text(_make_status('Validating stdin', 'done'))
            t.stderr()

    def test_stdin_failure(self):
        with cli_test(self, main) as t:
            t.args(['-'])
            t.return_code(1)
            t.stdin(minimal_invalid_json)
            t.stdout_text(_make_status('Validating stdin', 'ERROR'))
            t.stderr('Schema validation was unsuccessful:')
            t.stderr('error:')
            t.stderr('    required properties [objectItems, securityTag, version] are missing')
            t.stderr('    location: /')

    def test_one_file_success(self):
        with cli_test(self, main) as t:
            t.args(['../build/ties_validate_tests/success1.json'])
            t.return_code(0)
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/success1.json')), 'done'))
            t.stderr()

    def test_one_file_failure(self):
        with cli_test(self, main) as t:
            t.args(['../build/ties_validate_tests/failure1.json'])
            t.return_code(1)
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/failure1.json')), 'ERROR'))
            t.stderr('Schema validation was unsuccessful:')
            t.stderr('error:')
            t.stderr('    required properties [objectItems, securityTag, version] are missing')
            t.stderr('    location: /')

    def test_two_files_success(self):
        with cli_test(self, main) as t:
            t.args(['../build/ties_validate_tests/success1.json', '../build/ties_validate_tests/success2.json'])
            t.return_code(0)
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/success1.json')), 'done'))
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/success2.json')), 'done'))
            t.stderr()

    def test_two_files_failure(self):
        with cli_test(self, main) as t:
            t.args(['../build/ties_validate_tests/failure1.json', '../build/ties_validate_tests/failure2.json'])
            t.return_code(1)
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/failure1.json')), 'ERROR'))
            t.stderr('Schema validation was unsuccessful:')
            t.stderr('error:')
            t.stderr('    required properties [objectItems, securityTag, version] are missing')
            t.stderr('    location: /')
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/failure2.json')), 'ERROR'))
            t.stderr('Schema validation was unsuccessful:')
            t.stderr('error:')
            t.stderr('    required properties [objectItems, securityTag, version] are missing')
            t.stderr('    location: /')

    def test_one_glob_success(self):
        with cli_test(self, main) as t:
            t.args(['../build/ties_validate_tests/success*.json'])
            t.return_code(0)
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/success1.json')), 'done'))
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/success2.json')), 'done'))
            t.stderr()

    def test_one_glob_failure(self):
        with cli_test(self, main) as t:
            t.args(['../build/ties_validate_tests/failure*.json'])
            t.return_code(1)
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/failure1.json')), 'ERROR'))
            t.stderr('Schema validation was unsuccessful:')
            t.stderr('error:')
            t.stderr('    required properties [objectItems, securityTag, version] are missing')
            t.stderr('    location: /')
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/failure2.json')), 'ERROR'))
            t.stderr('Schema validation was unsuccessful:')
            t.stderr('error:')
            t.stderr('    required properties [objectItems, securityTag, version] are missing')
            t.stderr('    location: /')

    def test_two_globs_success(self):
        with cli_test(self, main) as t:
            t.args(['../build/ties_validate_tests/success1.*', '../build/ties_validate_tests/success2.*'])
            t.return_code(0)
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/success1.json')), 'done'))
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/success2.json')), 'done'))
            t.stderr()

    def test_two_globs_failure(self):
        with cli_test(self, main) as t:
            t.args(['../build/ties_validate_tests/failure1.*', '../build/ties_validate_tests/failure2.*'])
            t.return_code(1)
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/failure1.json')), 'ERROR'))
            t.stderr('Schema validation was unsuccessful:')
            t.stderr('error:')
            t.stderr('    required properties [objectItems, securityTag, version] are missing')
            t.stderr('    location: /')
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/failure2.json')), 'ERROR'))
            t.stderr('Schema validation was unsuccessful:')
            t.stderr('error:')
            t.stderr('    required properties [objectItems, securityTag, version] are missing')
            t.stderr('    location: /')

    def test_file_fnf(self):
        with cli_test(self, main) as t:
            t.args(['../build/ties_validate_tests/fnf.json'])
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/fnf.json')), 'ERROR'))
            t.stderr('Schema validation was unsuccessful:')
            t.stderr('error:')
            t.stderr("[Errno 2] No such file or directory: '{}'".format(abspath('../build/ties_validate_tests/fnf.json')))

    def test_glob_fnf(self):
        with cli_test(self, main) as t:
            t.args(['../build/ties_validate_tests/fnf*.json'])
            t.stdout_text(_make_status("Validating {}".format(abspath('../build/ties_validate_tests/fnf*.json')), 'ERROR'))
            t.stderr('Schema validation was unsuccessful:')
            t.stderr('error:')
            t.stderr("[Errno 2] No such file or directory: '{}'".format(abspath('../build/ties_validate_tests/fnf*.json')))


if __name__ == '__main__':
    unittest.main()
