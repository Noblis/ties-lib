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
import unittest
from tempfile import mkstemp
from unittest import TestCase

import six

from ties.cli.ties_format import main
from ties.util.testing import cli_test

short_usage = """\
usage: ties-format [-h] [--version] EXPORT_PATH"""

long_usage = """\
{}

Formats TIES export.json files

positional arguments:
  EXPORT_PATH  the path to the TIES JSON file or - to read from stdin

optional arguments:
  -h, --help   show this help message and exit
  --version    prints version information
""".format(short_usage)

test_input = """\
{
    "objectItems": [
        {
            "authorityInformation": {
                "securityTag": "UNCLASSIFIED"
            },
            "md5Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "sha256Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "objectId": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        }
    ],
    "securityTag": "UNCLASSIFIED",
    "version": "0.9"
}"""

test_output = """\
{
  "version": "0.9",
  "securityTag": "UNCLASSIFIED",
  "objectItems": [
    {
      "objectId": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "sha256Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "md5Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "authorityInformation": {
        "securityTag": "UNCLASSIFIED"
      }
    }
  ]
}"""


class TiesFormatTests(TestCase):

    def setUp(self):
        fd, self._input_file_path = mkstemp()
        with os.fdopen(fd, 'w') as f:
            f.write(test_input)

    def tearDown(self):
        try:
            os.remove(self._input_file_path)
        except Exception:  # pylint: disable=broad-except
            pass

    def test_no_args(self):
        with cli_test(self, main) as t:
            t.args([])
            t.return_code(2)
            t.stdout_text()
            t.stderr(short_usage)
            if six.PY3:
                t.stderr('ties-format: error: the following arguments are required: EXPORT_PATH')
            else:
                t.stderr('ties-format: error: too few arguments')
            t.stderr()

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

    def test_stdin_stdout(self):
        with cli_test(self, main) as t:
            t.args(['-'])
            t.stdin(test_input)
            t.return_code(0)
            t.stdout_json(test_output)
            t.stderr()

    def test_infile_stdout(self):
        with cli_test(self, main) as t:
            t.args([self._input_file_path])
            t.return_code(0)
            t.stdout_json(test_output)
            t.stderr()

    def test_stdin_parse_exception(self):
        with cli_test(self, main) as t:
            t.args(['-'])
            t.return_code(1)
            t.stdout_text()
            t.stderr('error: could not parse JSON from stdin')

    def test_infile_fnf(self):
        with cli_test(self, main) as t:
            t.args(['/file/not/found'])
            t.return_code(1)
            t.stdout_text()
            t.stderr('error: could not read from file: /file/not/found')

    def test_infile_parse_exception(self):
        with cli_test(self, main) as t:
            t.args(['/dev/null'])
            t.return_code(1)
            t.stdout_text()
            t.stderr('error: could not read from file: /dev/null')


if __name__ == '__main__':
    unittest.main()
