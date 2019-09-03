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

import sys
import unittest
from os.path import dirname, join

import xmlrunner

here = dirname(__file__)


# returns number of tests failed
def run_all_tests():
    all_tests = unittest.TestLoader().discover('.', pattern='*_tests.py')
    test_result = xmlrunner.XMLTestRunner(output=join(here, '..', '..', '..', 'build', 'test-results')).run(all_tests)
    return len(test_result.errors) + len(test_result.failures)


if __name__ == '__main__':
    sys.exit(run_all_tests())
