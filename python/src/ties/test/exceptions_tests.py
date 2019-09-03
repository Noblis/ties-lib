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

import unittest
from unittest import TestCase

from ties.exceptions import _jsonschema_error_message


class _TestJsonSchemaValidationError(object):

    def __init__(self, **kwargs):
        for key, value in kwargs.items():
            self.__setattr__(key, value)


class ExceptionsTests(TestCase):

    def test_jsonschema_error_message_type_null_single_type(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='type', validator_value='string', instance=None, relative_path=['foo']))
        self.assertEqual(error_message, 'property foo with null value should be of type string')

    def test_jsonschema_error_message_type_single_type(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='type', validator_value='string', instance=1, relative_path=['foo']))
        self.assertEqual(error_message, 'property type integer for property foo is not the allowed type: string')

    def test_jsonschema_error_message_type_null_multiple_types(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='type', validator_value=['integer', 'string'], instance=None, relative_path=['foo']))
        self.assertEqual(error_message, 'property foo with null value should be one of the allowed types: [integer, string]')

    def test_jsonschema_error_message_type_multiple_types(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='type', validator_value=['integer', 'string'], instance=[], relative_path=['foo']))
        self.assertEqual(error_message, 'property type array for property foo is not one of the allowed types: [integer, string]')

    def test_jsonschema_error_message_required_single_property(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='required', validator_value=['foo', 'bar'], instance={'foo': None}, relative_path=[]))
        self.assertEqual(error_message, 'required property bar is missing')

    def test_jsonschema_error_message_required_multiple_properties(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='required', validator_value=['foo', 'bar'], instance={}, relative_path=[]))
        self.assertEqual(error_message, 'required properties [bar, foo] are missing')

    def test_jsonschema_error_message_additionalProperties_single_property(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='additionalProperties', schema={'properties': {'foo': None}}, instance={'foo': None, 'bar': None}, relative_path=[]))
        self.assertEqual(error_message, 'additional property bar is not allowed')

    def test_jsonschema_error_message_additionalProperties_multiple_properties(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='additionalProperties', schema={'properties': {}}, instance={'foo': None, 'bar': None}, relative_path=[]))
        self.assertEqual(error_message, 'additional properties [bar, foo] are not allowed')

    def test_jsonschema_error_message_minimum(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='minimum', validator_value=1, instance=0, relative_path=['foo']))
        self.assertEqual(error_message, 'property value 0 for foo property is less than the minimum value of 1')

    def test_jsonschema_error_message_maximum(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='maximum', validator_value=0, instance=1, relative_path=['foo']))
        self.assertEqual(error_message, 'property value 1 for foo property is greater than the maximum value of 0')

    def test_jsonschema_error_message_minLength(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='minLength', validator_value=1, instance='', relative_path=['foo']))
        self.assertEqual(error_message, "property value '' for foo property is too short, minimum length 1")

    def test_jsonschema_error_message_maxLength(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='maxLength', validator_value=0, instance=' ', relative_path=['foo']))
        self.assertEqual(error_message, "property value ' ' for foo property is too long, maximum length 0")

    def test_jsonschema_error_message_pattern(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='pattern', validator_value='^a$', instance='', relative_path=['foo']))
        self.assertEqual(error_message, "property value '' for foo property does not match the pattern '^a$'")

    def test_jsonschema_error_message_minItems(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='minItems', validator_value=1, instance=[], relative_path=['foo']))
        self.assertEqual(error_message, 'array property foo with 0 items is too small, minimum size 1')

    def test_jsonschema_error_message_maxItems(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='maxItems', validator_value=0, instance=[None], relative_path=['foo']))
        self.assertEqual(error_message, 'array property foo with 1 items is too large, maximum size 0')

    def test_jsonschema_error_message_uniqueItems(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='uniqueItems', instance=[None, None], relative_path=['foo']))
        self.assertEqual(error_message, 'array property foo has duplicate items at index [0, 1]')

    def test_jsonschema_error_message_unknown(self):
        error_message = _jsonschema_error_message(_TestJsonSchemaValidationError(validator='UNKNOWN', message='an error message', relative_path=[]))
        self.assertEqual(error_message, 'an error message')


if __name__ == '__main__':
    unittest.main()
