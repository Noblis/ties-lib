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

from __future__ import unicode_literals

from collections import OrderedDict
from typing import TypeVar

import attr
import six
from attr import attrs
from inflection import camelize, underscore


StringType = TypeVar("StringType", str, six.text_type)


def _json_dict_converter(d):
    return {underscore(k): v for k, v in d.items()}


@attrs(slots=True)
class TiesData(object):

    _validator = None

    def validate(self):
        self._validator.validate(self.to_json())

    def all_errors(self):
        return self._validator.all_errors(self.to_json())

    def to_json(self):
        d = OrderedDict()
        for a in attr.fields(self.__class__):  # pylint: disable=not-an-iterable
            attr_value = getattr(self, a.name)
            if attr_value is None:
                continue
            json_attr_name = camelize(a.name, uppercase_first_letter=False)
            if isinstance(attr_value, TiesData):
                d[json_attr_name] = attr_value.to_json()
            elif isinstance(attr_value, list):
                d[json_attr_name] = [v.to_json() if isinstance(v, TiesData) else v for v in attr_value]
            else:
                d[json_attr_name] = attr_value
        return d

    @classmethod
    def from_json(cls, d):
        return cls(**_json_dict_converter(d))
