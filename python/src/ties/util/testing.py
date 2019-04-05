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
import sys

import six


class cli_test(object):

    def __init__(self, test_case, main):
        self._test_case = test_case
        self._main = main
        self._args = []
        self._stdin_text = None
        self._expected_return_code = 0
        self._expected_stdout_text = None
        self._expected_stdout_json = None
        self._expected_stderr = []

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.execute()

    def args(self, arg_list):
        self._args = arg_list

    def stdin(self, input_text):
        self._stdin_text = input_text

    def return_code(self, code):
        self._expected_return_code = code

    def stdout_text(self, line=''):
        if self._expected_stdout_text is None:
            self._expected_stdout_text = []
        self._expected_stdout_text.append(line)

    def stdout_json(self, json_output):
        self._expected_stdout_json = json.loads(json_output)

    def stderr(self, line=''):
        self._expected_stderr.append(line)

    def execute(self):
        sys_stdin = sys.stdin
        sys_stdout = sys.stdout
        sys_stderr = sys.stderr
        captured_stdout = six.StringIO()
        captured_stderr = six.StringIO()
        sys.stdin = six.StringIO(self._stdin_text)
        sys.stdout = captured_stdout
        sys.stderr = captured_stderr
        try:
            return_code = self._main(argv=self._args)
        except SystemExit as e:
            return_code = e.code
        sys.stdin = sys_stdin
        sys.stdout = sys_stdout
        sys.stderr = sys_stderr
        try:
            assert return_code == self._expected_return_code
        except AssertionError:
            message = '\n'.join([
                "Return code {} expected, got {}".format(self._expected_return_code, return_code),
                '-' * 80,
                'stdout:',
                captured_stdout.getvalue().strip(),
                '-' * 80,
                'stderr:',
                captured_stderr.getvalue().strip(),
                '-' * 80,
                ])
            raise AssertionError('\n' + message)
        try:
            if self._expected_stdout_text is not None:
                assert captured_stdout.getvalue().strip() == '\n'.join(self._expected_stdout_text).strip()
            if self._expected_stdout_json is not None:
                assert json.loads(captured_stdout.getvalue()) == self._expected_stdout_json
            assert captured_stderr.getvalue().strip() == '\n'.join(self._expected_stderr).strip()
        except AssertionError:
            if self._expected_stdout_text is not None:
                expected_stdout = '\n'.join(self._expected_stdout_text)
            elif self._expected_stdout_json is not None:
                expected_stdout = json.dumps(self._expected_stdout_json, indent=2)
            else:
                expected_stdout = ''
            message = '\n'.join([
                '-' * 80,
                'Expected stdout:',
                expected_stdout.strip(),
                '-' * 80,
                'Actual stdout:',
                captured_stdout.getvalue().strip(),
                '-' * 80,
                'Expected stderr:',
                '\n'.join(self._expected_stderr).strip(),
                '-' * 80,
                'Actual stderr:',
                captured_stderr.getvalue().strip(),
                '-' * 80,
                ])
            raise AssertionError('\n' + message)


if __name__ == '__main__':
    pass
