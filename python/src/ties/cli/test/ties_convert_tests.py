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

import json
import os
import unittest
from stat import S_IRUSR
from tempfile import mkstemp
from unittest import TestCase

import six

from ties.cli.ties_convert import main
from ties.util.testing import cli_test

short_usage = """\
usage: ties-convert [-h] [--classification-level SECURITY_TAG]
                    [--output-file OUTPUT_FILE | --in-place] [--version]
                    EXPORT_PATH"""

long_usage = """\
{}

Converts TIES export.json files from older versions of the schema (0.1.8, 0.2,
0.3, 0.4, 0.5, 0.6, 0.7, 0.8) to the current version (0.9).

positional arguments:
  EXPORT_PATH           the path to the TIES JSON file or - to read from stdin

optional arguments:
  -h, --help            show this help message and exit
  --classification-level SECURITY_TAG, -c SECURITY_TAG
                        the classification level of the TIES JSON, required
                        for TIES JSON from pre-0.3 versions of the schema
  --output-file OUTPUT_FILE, -f OUTPUT_FILE
                        the output file path for the converted TIES JSON
  --in-place, -i        modifies the input file in-place, overwriting it with
                        the converted JSON data
  --version             prints version information
""".format(short_usage)

test_input = """\
{
    "version": "0.1.8",
    "objectItem": [
        {
            "sha256Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "md5Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        }
    ]
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


class TiesConvertTests(TestCase):

    def setUp(self):
        self._default_args = ['--classification-level', 'UNCLASSIFIED']
        fd, self._input_file_path = mkstemp()
        with os.fdopen(fd, 'w') as f:
            f.write(test_input)
        fd, self._output_file_path = mkstemp()
        with os.fdopen(fd, 'w') as f:
            f.write(test_output)

    def tearDown(self):
        try:
            os.remove(self._input_file_path)
        except Exception:  # pylint: disable=broad-except
            pass
        try:
            os.remove(self._output_file_path)
        except Exception:  # pylint: disable=broad-except
            pass

    def _check_input_file_json(self, expected_json):
        with open(self._input_file_path, 'r') as f:
            self.assertEqual(json.load(f), json.loads(expected_json))

    def _check_output_file_json(self, expected_json):
        with open(self._output_file_path, 'r') as f:
            self.assertEqual(json.load(f), json.loads(expected_json))

    def test_no_args(self):
        with cli_test(self, main) as t:
            t.args([])
            t.return_code(2)
            t.stdout_text()
            t.stderr(short_usage)
            if six.PY3:
                t.stderr('ties-convert: error: the following arguments are required: EXPORT_PATH')
            else:
                t.stderr('ties-convert: error: too few arguments')
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
            t.args(self._default_args + ['-'])
            t.stdin(test_input)
            t.return_code(0)
            t.stdout_json(test_output)
            t.stderr()

    def test_infile_stdout(self):
        with cli_test(self, main) as t:
            t.args(self._default_args + [self._input_file_path])
            t.return_code(0)
            t.stdout_json(test_output)
            t.stderr()
        self._check_input_file_json(test_input)

    def test_stdin_outfile(self):
        with cli_test(self, main) as t:
            t.args(self._default_args + ['-f', self._output_file_path, '-'])
            t.stdin(test_input)
            t.return_code(0)
            t.stdout_text()
            t.stderr()
        self._check_output_file_json(test_output)

    def test_infile_outfile(self):
        with cli_test(self, main) as t:
            t.args(self._default_args + ['-f', self._output_file_path, self._input_file_path])
            t.return_code(0)
            t.stdout_text()
            t.stderr()
        self._check_input_file_json(test_input)
        self._check_output_file_json(test_output)

    def test_inplace(self):
        with cli_test(self, main) as t:
            t.args(self._default_args + ['-i', self._input_file_path])
            t.return_code(0)
            t.stdout_text()
            t.stderr()
        self._check_input_file_json(test_output)

    def test_inplace_stdin(self):
        with cli_test(self, main) as t:
            t.args(self._default_args + ['-i', '-'])
            t.stdin(test_input)
            t.return_code(0)
            t.stdout_json(test_output)
            t.stderr()

    def test_inplace_outfile_error(self):
        with cli_test(self, main) as t:
            t.args(self._default_args + ['-i', '-f', self._output_file_path, self._input_file_path])
            t.return_code(2)
            t.stdout_text()
            t.stderr(short_usage)
            t.stderr('ties-convert: error: argument --output-file/-f: not allowed with argument --in-place/-i')

    def test_inplace_write_error(self):
        os.chmod(self._input_file_path, S_IRUSR)
        with cli_test(self, main) as t:
            t.args(self._default_args + ['-i', self._input_file_path])
            t.return_code(1)
            t.stdout_text()
            t.stderr("error: could not write to file: {}".format(self._input_file_path))

    def test_stdin_parse_exception(self):
        with cli_test(self, main) as t:
            t.args(self._default_args + ['-'])
            t.return_code(1)
            t.stdout_text()
            t.stderr('error: could not parse JSON from stdin')

    def test_infile_fnf(self):
        with cli_test(self, main) as t:
            t.args(self._default_args + ['/file/not/found'])
            t.return_code(1)
            t.stdout_text()
            t.stderr('error: could not read from file: /file/not/found')

    def test_infile_parse_exception(self):
        with cli_test(self, main) as t:
            t.args(self._default_args + ['/dev/null'])
            t.return_code(1)
            t.stdout_text()
            t.stderr('error: could not read from file: /dev/null')

    def test_outfile_fnf(self):
        with cli_test(self, main) as t:
            t.args(self._default_args + ['-f', '/dev/full', self._input_file_path])
            t.return_code(1)
            t.stdout_text()
            t.stderr('error: could not write to file: /dev/full')


if __name__ == '__main__':
    unittest.main()
