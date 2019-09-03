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

import json
from collections import OrderedDict, deque

from ties.util import indent


class ValidationWarning(Exception):

    def __init__(self, message, location, *args, **kwargs):
        super(ValidationWarning, self).__init__(*args, **kwargs)
        self.message = message
        self.location = location

    def __repr__(self):
        return "ValidationWarning({}, {})".format(repr(self.message), repr(self.location))

    def __str__(self):
        return "{}\nlocation: {}".format(self.message, self.location)

    def __eq__(self, other):
        if not isinstance(other, ValidationWarning):
            return False
        return self.message == other.message and self.location == other.location

    def __hash__(self):
        return hash((self.message, self.location))


class ValidationError(Exception):

    def __init__(self, message, location, causes, *args, **kwargs):
        super(ValidationError, self).__init__(*args, **kwargs)
        self.message = message
        self.location = location
        if causes is None:
            self.causes = ()
        else:
            self.causes = tuple(causes)

    def __repr__(self):
        return "ValidationError({}, {}, {})".format(repr(self.message), repr(self.location), repr(self.causes))

    def __str__(self):
        if self.causes:
            return "{}\npossible causes:\n{}".format(self.message, '\n'.join([indent(str(cause), ' ' * 4) for cause in self.causes]))
        else:
            return "{}\nlocation: {}".format(self.message, self.location)

    def __eq__(self, other):
        if not isinstance(other, ValidationError):
            return False
        return self.message == other.message and self.location == other.location and self.causes == other.causes

    def __hash__(self):
        return hash((self.message, self.location, self.causes))


def make_validation_error(jsonschema_error):
    return ValidationError(_jsonschema_error_message(jsonschema_error), _jsonschema_error_schema_path(jsonschema_error), _jsonschema_error_causes(jsonschema_error))


def _jsonschema_error_message(jsonschema_error):
    if jsonschema_error.validator == 'type':
        return _type_error_message(jsonschema_error)
    if jsonschema_error.validator == 'required':
        return _required_error_message(jsonschema_error)
    if jsonschema_error.validator == 'additionalProperties':
        return _additional_properties_error_message(jsonschema_error)
    if jsonschema_error.validator == 'minimum':
        return _minimum_error_message(jsonschema_error)
    if jsonschema_error.validator == 'maximum':
        return _maximum_error_message(jsonschema_error)
    if jsonschema_error.validator == 'minLength':
        return _min_length_error_message(jsonschema_error)
    if jsonschema_error.validator == 'maxLength':
        return _max_length_error_message(jsonschema_error)
    if jsonschema_error.validator == 'pattern':
        return _pattern_error_message(jsonschema_error)
    if jsonschema_error.validator == 'enum':
        return _enum_error_message(jsonschema_error)
    if jsonschema_error.validator == 'minItems':
        return _min_items_error_message(jsonschema_error)
    if jsonschema_error.validator == 'maxItems':
        return _max_items_error_message(jsonschema_error)
    if jsonschema_error.validator == 'uniqueItems':
        return _unique_items_error_message(jsonschema_error)
    if jsonschema_error.validator == 'anyOf':
        return _anyof_error_message(jsonschema_error)
    return _unknown_error_message(jsonschema_error)


def _type_error_message(jsonschema_error):
    property_name = jsonschema_error.relative_path[-1]
    found_type = _json_type_for_instance(jsonschema_error.instance)
    if isinstance(jsonschema_error.validator_value, list):
        expected_types = ', '.join(jsonschema_error.validator_value)
        if found_type == 'null':
            return "property {} with null value should be one of the allowed types: [{}]".format(property_name, expected_types)
        else:
            return "property type {} for property {} is not one of the allowed types: [{}]".format(found_type, property_name, expected_types)
    else:
        expected_type = jsonschema_error.validator_value
        if found_type == 'null':
            return "property {} with null value should be of type {}".format(property_name, expected_type)
        else:
            return "property type {} for property {} is not the allowed type: {}".format(found_type, property_name, expected_type)


def _required_error_message(jsonschema_error):
    missing_properties = sorted(set(jsonschema_error.validator_value) - set(jsonschema_error.instance.keys()))
    if len(missing_properties) == 1:
        return "required property {} is missing".format(missing_properties[0])
    else:
        return "required properties [{}] are missing".format(', '.join(missing_properties))


def _additional_properties_error_message(jsonschema_error):
    extra_properties = sorted(set(jsonschema_error.instance.keys()) - set(jsonschema_error.schema.get('properties', {}).keys()))
    if len(extra_properties) == 1:
        return "additional property {} is not allowed".format(extra_properties[0])
    else:
        return "additional properties [{}] are not allowed".format(', '.join(extra_properties))


def _minimum_error_message(jsonschema_error):
    property_value = jsonschema_error.instance
    minimum = jsonschema_error.validator_value
    property_name = jsonschema_error.relative_path[-1]
    try:
        int(property_name)
        property_index = property_name
        property_name = jsonschema_error.relative_path[-2]
        return "property value {} for element at index {} in {} is less than the minimum value of {}".format(property_value, property_index, property_name, minimum)
    except ValueError:
        return "property value {} for {} property is less than the minimum value of {}".format(property_value, property_name, minimum)


def _maximum_error_message(jsonschema_error):
    property_value = jsonschema_error.instance
    maximum = jsonschema_error.validator_value
    property_name = jsonschema_error.relative_path[-1]
    try:
        int(property_name)
        property_index = property_name
        property_name = jsonschema_error.relative_path[-2]
        return "property value {} for element at index {} in {} is greater than the maximum value of {}".format(property_value, property_index, property_name, maximum)
    except ValueError:
        return "property value {} for {} property is greater than the maximum value of {}".format(property_value, property_name, maximum)


def _min_length_error_message(jsonschema_error):
    property_value = jsonschema_error.instance
    min_length = jsonschema_error.validator_value
    property_name = jsonschema_error.relative_path[-1]
    try:
        int(property_name)
        property_index = property_name
        property_name = jsonschema_error.relative_path[-2]
        return "property value {} for element at index {} in {} is too short, minimum length {}".format(_quote_value(property_value), property_index, property_name, min_length)
    except ValueError:
        return "property value {} for {} property is too short, minimum length {}".format(_quote_value(property_value), property_name, min_length)


def _max_length_error_message(jsonschema_error):
    property_value = jsonschema_error.instance
    max_length = jsonschema_error.validator_value
    property_name = jsonschema_error.relative_path[-1]
    try:
        int(property_name)
        property_index = property_name
        property_name = jsonschema_error.relative_path[-2]
        return "property value {} for element at index {} in {} is too long, maximum length {}".format(_quote_value(property_value), property_index, property_name, max_length)
    except ValueError:
        return "property value {} for {} property is too long, maximum length {}".format(_quote_value(property_value), property_name, max_length)


def _pattern_error_message(jsonschema_error):
    property_value = jsonschema_error.instance
    pattern = jsonschema_error.validator_value
    property_name = jsonschema_error.relative_path[-1]
    try:
        int(property_name)
        property_index = property_name
        property_name = jsonschema_error.relative_path[-2]
        return "property value {} for element at index {} in {} does not match the pattern '{}'".format(_quote_value(property_value), property_index, property_name, pattern)
    except ValueError:
        return "property value {} for {} property does not match the pattern '{}'".format(_quote_value(property_value), property_name, pattern)


def _enum_error_message(jsonschema_error):
    property_value = jsonschema_error.instance
    allowed_values = jsonschema_error.validator_value
    property_name = jsonschema_error.relative_path[-1]
    try:
        int(property_name)
        property_index = property_name
        property_name = jsonschema_error.relative_path[-2]
        return "property value {} for element at index {} in {} should have one of the allowed values: [{}]".format(_quote_value(property_value), property_index, property_name, ', '.join(allowed_values))
    except ValueError:
        return "enum property {} with value {} should have one of the allowed values: [{}]".format(property_name, _quote_value(property_value), ', '.join(allowed_values))


def _min_items_error_message(jsonschema_error):
    property_name = jsonschema_error.relative_path[-1]
    item_count = len(jsonschema_error.instance)
    min_items = jsonschema_error.validator_value
    return "array property {} with {} items is too small, minimum size {}".format(property_name, item_count, min_items)


def _max_items_error_message(jsonschema_error):
    property_name = jsonschema_error.relative_path[-1]
    item_count = len(jsonschema_error.instance)
    max_items = jsonschema_error.validator_value
    return "array property {} with {} items is too large, maximum size {}".format(property_name, item_count, max_items)


def _unique_items_error_message(jsonschema_error):
    property_name = jsonschema_error.relative_path[-1]
    item_index = OrderedDict()
    for item, i in zip(jsonschema_error.instance, range(len(jsonschema_error.instance))):
        item_str = json.dumps(item, sort_keys=True)
        item_index[item_str] = item_index.get(item_str, []) + [i]

    for item_str in item_index:
        duplicate_indexes = item_index[item_str]
        if len(duplicate_indexes) > 1:
            return "array property {} has duplicate items at index {}".format(property_name, list(duplicate_indexes))
    return "array property {} has duplicate items".format(property_name)


def _anyof_error_message(jsonschema_error):
    property_name = jsonschema_error.relative_path[-1]
    try:
        int(property_name)
        property_index = property_name
        property_name = jsonschema_error.relative_path[-2]
        return "content for array property at index {} in {} does not match any of the possible schema definitions".format(property_index, property_name)
    except ValueError:
        return "content for property {} does not match any of the possible schema definitions".format(property_name)


def _unknown_error_message(jsonschema_error):
    return jsonschema_error.message


def _jsonschema_error_schema_path(jsonschema_error):
    path_components = []
    for p in jsonschema_error.relative_path:
        if isinstance(p, int):
            path_components.append("[{}]".format(p))
        else:
            path_components.append("/{}".format(p))

    if len(path_components) == 0:
        return '/'
    else:
        return ''.join(path_components)


def _jsonschema_error_causes(jsonschema_error):
    causes = []
    for error_cause in sorted(jsonschema_error.context, key=lambda x: x.validator):
        error_cause.relative_path = deque(list(jsonschema_error.relative_path) + list(error_cause.relative_path))
        causes.append(ValidationError(_jsonschema_error_message(error_cause), _jsonschema_error_schema_path(error_cause), _jsonschema_error_causes(error_cause)))
    # sorted is stable, so validation errors will be sorted by location and validator
    return sorted(list(OrderedDict.fromkeys(causes)), key=lambda x: x.location)


def _json_type_for_instance(instance):
    if instance is None:
        return 'null'
    if isinstance(instance, bool):
        return 'boolean'
    if isinstance(instance, int):
        return 'integer'
    if isinstance(instance, float):
        return 'number'
    if isinstance(instance, str):
        return 'string'
    if isinstance(instance, list):
        return 'array'
    if isinstance(instance, dict):
        return 'object'
    return 'unknown'


def _quote_value(value):
    if value is None:
        return 'null'
    else:
        return "'{}'".format(value)
