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

import six


# textwrap.indent() doesn't exist in Python 2
if six.PY3:
    from textwrap import indent  # pylint: disable=no-name-in-module
else:
    def indent(text, prefix):
        if len(text) == 0:
            return text
        return prefix + text.replace('\n', "\n{}".format(prefix))


if __name__ == '__main__':
    pass
