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
import copy

import inflection
import six

from ties.schema_validation import AnnotationSchemaValidator
from ties.schema_validation import AssertionsSchemaValidator
from ties.schema_validation import AuthorityInformationSchemaValidator
from ties.schema_validation import ObjectItemSchemaValidator
from ties.schema_validation import ObjectGroupSchemaValidator
from ties.schema_validation import ObjectRelationshipSchemaValidator
from ties.schema_validation import OtherInformationSchemaValidator
from ties.schema_validation import SupplementalDescriptionDataFileSchemaValidator
from ties.schema_validation import SupplementalDescriptionDataObjectSchemaValidator
from ties.schema_validation import TiesSchemaValidator


def _property_value_to_json(property_value):
    if isinstance(property_value, TiesData):
        return property_value.to_json()
    else:
        return property_value


def _property_value_list_to_json(property_value_list):
    return [_property_value_to_json(property_value) for property_value in property_value_list]


def _json_to_property_value(value, property_types):
    property_value = None
    for property_type in property_types:
        if issubclass(property_type, TiesData):
            try:
                property_value = property_type.from_json(value)
                break
            except (AttributeError, ValueError):
                continue
        elif isinstance(value, property_type):
            property_value = value
            break
    if property_value is None:
        raise ValueError("value {} could not be converted to the following type(s): {}".format(value, property_types))
    return property_value


def _json_to_property_value_list(value_list, property_types):
    if not isinstance(value_list, list):
        raise ValueError("value {} should be a list".format(value_list))
    return [_json_to_property_value(value, property_types[list]) for value in value_list]


class TiesData(object):

    def __init__(self, property_defs, validator, **kwargs):
        self.__dict__['_property_defs'] = copy.deepcopy(property_defs)
        self.__dict__['_validator'] = validator
        self.__dict__['_property_dict'] = {}
        for property_name, property_types in self._property_defs.items():
            if isinstance(property_types, type):
                self._property_defs[property_name] = tuple([property_types])
            elif isinstance(property_types, dict):
                if isinstance(property_types[list], type):
                    property_types[list] = tuple([property_types[list]])
        for property_name, property_value in kwargs.items():
            self.__setattr__(property_name, property_value)

    def __repr__(self):
        return "{}({})".format(self.__class__.__name__, repr(self._property_dict))

    def __str__(self):
        return str(self._property_dict)

    def __unicode__(self):
        return six.text_type(self._property_dict)

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self._property_dict == other._property_dict  # pylint: disable=protected-access

    def __ne__(self, other):
        return not self.__eq__(other)

    def __getattr__(self, property_name):
        property_name = inflection.camelize(property_name, False)
        if property_name not in self._property_defs:
            raise AttributeError("{} object has no property {}".format(self.__class__.__name__, property_name))
        return self._property_dict.get(property_name)

    def __setattr__(self, property_name, property_value):
        property_name = inflection.camelize(property_name, False)
        self._is_valid_property_value(property_name, property_value)
        if property_value is None and property_name in self._property_dict:
            del self._property_dict[property_name]
        else:
            self._property_dict[property_name] = property_value

    def _is_valid_property_value(self, property_name, property_value):
        if property_name not in self._property_defs:
            raise AttributeError("{} object has no property {}".format(self.__class__.__name__, property_name))
        if property_value is None:
            return
        property_types = self._property_defs[property_name]
        if type(property_value) not in property_types:
            raise AttributeError("incorrect type for property {} on {} object, expected: {}".format(property_name, self.__class__.__name__, property_types))
        if isinstance(property_value, list):
            for item in property_value:
                if type(item) not in property_types[list]:
                    raise AttributeError("incorrect type for property {} on {} object, expected: {}".format(property_name, self.__class__.__name__, property_types[list]))

    def validate(self):
        self._validator.validate(self.to_json())

    def all_errors(self):
        return self._validator.all_errors(self.to_json())

    def property_names(self):
        return self._property_defs.keys()

    def to_json(self):
        d = {}
        for property_name in self._property_defs:
            if property_name in self._property_dict:
                property_value = self._property_dict.get(property_name)
                if isinstance(property_value, list):
                    d[property_name] = _property_value_list_to_json(property_value)
                else:
                    d[property_name] = _property_value_to_json(property_value)
        return d

    @classmethod
    def from_json(cls, d):
        obj = cls()  # pylint: disable=no-value-for-parameter
        for key, value in d.items():
            property_name = inflection.camelize(key, False)
            if property_name not in obj.property_names():
                raise AttributeError("{} object has no property {}".format(cls.__name__, key))
            property_types = obj._property_defs[property_name]  # pylint: disable=protected-access
            if isinstance(property_types, dict):
                setattr(obj, property_name, _json_to_property_value_list(value, property_types))
            else:
                setattr(obj, property_name, _json_to_property_value(value, property_types))
        return obj


class Annotation(TiesData):

    def __init__(self, **kwargs):
        properties = OrderedDict({
            'assertionId': (str, six.text_type),
            'assertionReferenceId': (str, six.text_type),
            'assertionReferenceIdLabel': (str, six.text_type),
            'system': (str, six.text_type),
            'creator': (str, six.text_type),
            'time': (str, six.text_type),
            'annotationType': (str, six.text_type),
            'key': (str, six.text_type),
            'value': (str, six.text_type),
            'itemAction': (str, six.text_type),
            'itemActionTime': (str, six.text_type),
            'securityTag': (str, six.text_type),
        })
        validator = AnnotationSchemaValidator()
        TiesData.__init__(self, properties, validator, **kwargs)


class Assertions(TiesData):

    def __init__(self, **kwargs):
        properties = OrderedDict({
            'annotations': {list: Annotation},
            'supplementalDescriptions': {list: (SupplementalDescriptionDataFile, SupplementalDescriptionDataObject)},
        })
        validator = AssertionsSchemaValidator()
        TiesData.__init__(self, properties, validator, **kwargs)


class AuthorityInformation(TiesData):

    def __init__(self, **kwargs):
        properties = OrderedDict({
            'collectionId': (str, six.text_type),
            'collectionIdLabel': (str, six.text_type),
            'collectionIdAlias': (str, six.text_type),
            'collectionDescription': (str, six.text_type),
            'subCollectionId': (str, six.text_type),
            'subCollectionIdLabel': (str, six.text_type),
            'subCollectionIdAlias': (str, six.text_type),
            'subCollectionDescription': (str, six.text_type),
            'registrationDate': (str, six.text_type),
            'expirationDate': (str, six.text_type),
            'owner': (str, six.text_type),
            'securityTag': (str, six.text_type),
        })
        validator = AuthorityInformationSchemaValidator()
        TiesData.__init__(self, properties, validator, **kwargs)


class ObjectGroup(TiesData):

    def __init__(self, **kwargs):
        properties = OrderedDict({
            'groupId': (str, six.text_type),
            'groupType': (str, six.text_type),
            'groupDescription': (str, six.text_type),
            'groupMemberIds': {list: (str, six.text_type)},
            'groupAssertions': Assertions,
            'otherInformation': {list: OtherInformation},
        })
        validator = ObjectGroupSchemaValidator()
        TiesData.__init__(self, properties, validator, **kwargs)


class ObjectItem(TiesData):

    def __init__(self, **kwargs):
        properties = OrderedDict({
            'objectId': (str, six.text_type),
            'sha256Hash': (str, six.text_type),
            'md5Hash': (str, six.text_type),
            'size': int,
            'mimeType': (str, six.text_type),
            'relativeUri': (str, six.text_type),
            'originalPath': (str, six.text_type),
            'authorityInformation': AuthorityInformation,
            'objectAssertions': Assertions,
            'otherInformation': {list: OtherInformation},
        })
        validator = ObjectItemSchemaValidator()
        TiesData.__init__(self, properties, validator, **kwargs)


class ObjectRelationship(TiesData):

    def __init__(self, **kwargs):
        properties = OrderedDict({
            'linkageMemberIds': {list: (str, six.text_type)},
            'linkageDirectionality': (str, six.text_type),
            'linkageType': (str, six.text_type),
            'linkageAssertionId': (str, six.text_type),
            'otherInformation': {list: OtherInformation},
        })
        validator = ObjectRelationshipSchemaValidator()
        TiesData.__init__(self, properties, validator, **kwargs)


class OtherInformation(TiesData):

    def __init__(self, **kwargs):
        properties = OrderedDict({
            'key': (str, six.text_type),
            'value': (str, six.text_type, bool, int, float),
        })
        validator = OtherInformationSchemaValidator()
        TiesData.__init__(self, properties, validator, **kwargs)


class SupplementalDescriptionDataFile(TiesData):

    def __init__(self, **kwargs):
        properties = OrderedDict({
            'assertionId': (str, six.text_type),
            'assertionReferenceId': (str, six.text_type),
            'assertionReferenceIdLabel': (str, six.text_type),
            'system': (str, six.text_type),
            'informationType': (str, six.text_type),
            'sha256DataHash': (str, six.text_type),
            'dataSize': int,
            'dataRelativeUri': (str, six.text_type),
            'securityTag': (str, six.text_type),
        })
        validator = SupplementalDescriptionDataFileSchemaValidator()
        TiesData.__init__(self, properties, validator, **kwargs)


class SupplementalDescriptionDataObject(TiesData):

    def __init__(self, **kwargs):
        properties = OrderedDict({
            'assertionId': (str, six.text_type),
            'assertionReferenceId': (str, six.text_type),
            'assertionReferenceIdLabel': (str, six.text_type),
            'system': (str, six.text_type),
            'informationType': (str, six.text_type),
            'dataObject': dict,
            'securityTag': (str, six.text_type),
        })
        validator = SupplementalDescriptionDataObjectSchemaValidator()
        TiesData.__init__(self, properties, validator, **kwargs)


class Ties(TiesData):

    def __init__(self, **kwargs):
        properties = OrderedDict({
            'version': (str, six.text_type),
            'id': (str, six.text_type),
            'system': (str, six.text_type),
            'organization': (str, six.text_type),
            'time': (str, six.text_type),
            'description': (str, six.text_type),
            'type': (str, six.text_type),
            'securityTag': (str, six.text_type),
            'objectItems': {list: ObjectItem},
            'objectGroups': {list: ObjectGroup},
            'objectRelationships': {list: ObjectRelationship},
            'otherInformation': {list: OtherInformation},
        })
        validator = TiesSchemaValidator()
        TiesData.__init__(self, properties, validator, **kwargs)


if __name__ == '__main__':
    pass
