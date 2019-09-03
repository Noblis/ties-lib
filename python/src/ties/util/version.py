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

import argparse
from os.path import abspath, isfile

from pkg_resources import resource_filename


class VersionAction(argparse.Action):
    def __init__(self, option_strings, dest, version=None, **kwargs):
        kwargs['nargs'] = 0
        self._version = version
        super(VersionAction, self).__init__(option_strings, dest, **kwargs)

    def __call__(self, parser, namespace, values, option_string=None):
        parser.exit(message="{}\n".format(self._version))


def _get_version_number():
    return '0.9.1'


def _get_build_number():
    resource_version_path = abspath(resource_filename(__name__, 'build_number.txt'))
    if isfile(resource_version_path):
        with open(resource_version_path, 'r') as f:
            build_number = f.read().strip()
            if build_number:
                return build_number
            else:
                return None
    else:
        return None


def _get_build_time():
    resource_version_path = abspath(resource_filename(__name__, 'build_time.txt'))
    if isfile(resource_version_path):
        with open(resource_version_path, 'r') as f:
            build_time = f.read().strip()
            if build_time:
                return build_time
            else:
                return None
    else:
        return None


def version_string():
    version_number = _get_version_number()
    build_number = _get_build_number()
    build_time = _get_build_time()

    version = "version {}".format(version_number)
    if build_number is not None:
        version += "\nbuild {}".format(build_number)
    if build_time is not None:
        version += "\nbuilt on {}".format(build_time)
    return version
