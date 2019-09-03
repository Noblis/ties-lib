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
from collections import OrderedDict
from os.path import abspath, isfile

import jsonschema
from jsonschema import Draft4Validator, RefResolver
from pkg_resources import resource_filename

from ties.exceptions import make_validation_error

annotation_pointer = '/definitions/annotation-object'
assertions_pointer = '/definitions/assertions-object'
authority_information_pointer = '/definitions/authorityInformation-object'
object_group_pointer = '/definitions/objectGroup-object'
object_item_pointer = '/definitions/objectItem-object'
object_relationship_pointer = '/definitions/objectRelationship-object'
other_information_pointer = '/definitions/otherInformation-object'
supplemental_description_data_file_pointer = '/definitions/supplementalDescriptionDataFile-object'
supplemental_description_data_object_pointer = '/definitions/supplementalDescriptionDataObject-object'
ties_pointer = ''


def load_schema(json_pointer=''):
    schema_path = abspath(resource_filename(__name__, 'schemata/ties-base.json'))

    if not isfile(schema_path):
        raise Exception('could not find schema')

    with open(schema_path, 'r') as f:
        schema = json.load(f)

    for p in json_pointer.strip('/').split('/'):
        if p != '':
            schema = schema[p]

    return schema


class SchemaValidator(object):

    def __init__(self, json_pointer=''):
        schema = load_schema(json_pointer=json_pointer)
        resolver = RefResolver.from_schema(load_schema())
        self.validator = Draft4Validator(schema, resolver=resolver)

    def validate(self, instance):
        try:
            instance = json.loads(instance)
        except Exception:  # pylint: disable=broad-except
            pass
        try:
            instance = json.load(instance)
        except Exception:  # pylint: disable=broad-except
            pass

        try:
            self.validator.validate(instance)
        except jsonschema.ValidationError as e:
            raise make_validation_error(e)

    def all_errors(self, instance):
        try:
            instance = json.loads(instance)
        except Exception:  # pylint: disable=broad-except
            pass
        try:
            instance = json.load(instance)
        except Exception:  # pylint: disable=broad-except
            pass

        validation_errors = [make_validation_error(e) for e in sorted(self.validator.iter_errors(instance), key=lambda x: x.validator)]
        # sorted is stable, so validation errors will be sorted by location and validator
        return sorted(list(OrderedDict.fromkeys(validation_errors)), key=lambda x: x.location)


def _validator_factory(json_pointer):
    class Validator(SchemaValidator):
        def __init__(self):
            SchemaValidator.__init__(self, json_pointer=json_pointer)
    return Validator


AnnotationSchemaValidator = _validator_factory(annotation_pointer)
AssertionsSchemaValidator = _validator_factory(assertions_pointer)
AuthorityInformationSchemaValidator = _validator_factory(authority_information_pointer)
ObjectGroupSchemaValidator = _validator_factory(object_group_pointer)
ObjectItemSchemaValidator = _validator_factory(object_item_pointer)
ObjectRelationshipSchemaValidator = _validator_factory(object_relationship_pointer)
OtherInformationSchemaValidator = _validator_factory(other_information_pointer)
SupplementalDescriptionDataFileSchemaValidator = _validator_factory(supplemental_description_data_file_pointer)
SupplementalDescriptionDataObjectSchemaValidator = _validator_factory(supplemental_description_data_object_pointer)
TiesSchemaValidator = _validator_factory(ties_pointer)


if __name__ == '__main__':
    pass
