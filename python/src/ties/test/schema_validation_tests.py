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
import os
import unittest
from tempfile import mkstemp
from unittest import TestCase

from ties.schema_validation import SchemaValidator, TiesSchemaValidator, load_schema, object_relationship_pointer

test_input_str = """\
{
  "version": "0.9",
  "securityTag": "UNCLASSIFIED",
  "objectItems": [
    {
      "objectId": "a",
      "sha256Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "md5Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "authorityInformation": {
        "securityTag": "UNCLASSIFIED"
      }
    }
  ]
}"""


class SchemaValidatorTests(TestCase):

    def setUp(self):
        self._test_input_str = test_input_str
        fd, self._test_input_file_path = mkstemp()
        with os.fdopen(fd, 'w') as f:
            f.write(self._test_input_str)
        self._test_input_file = open(self._test_input_file_path, 'r')
        self._test_input_dict = json.loads(self._test_input_str)
        self._schema_validator = SchemaValidator()

    def tearDown(self):
        self._test_input_file.close()
        try:
            os.remove(self._test_input_file_path)
        except Exception:  # pylint: disable=broad-except
            pass

    def test_load_schema_ties(self):
        schema = load_schema()
        self.assertSetEqual(set(schema['properties'].keys()), {'version', 'id', 'system', 'organization', 'time', 'description', 'type', 'securityTag', 'objectItems', 'objectGroups', 'objectRelationships', 'otherInformation'})

    def test_load_schema_sub_schema(self):
        schema = load_schema(json_pointer=object_relationship_pointer)
        self.assertSetEqual(set(schema['properties'].keys()), {'linkageMemberIds', 'linkageDirectionality', 'linkageType', 'linkageAssertionId', 'otherInformation'})

    def test_validate_json_str(self):
        self._schema_validator.validate(self._test_input_str)

    def test_validate_json_file(self):
        self._schema_validator.validate(self._test_input_file)

    def test_validate_json_dict(self):
        self._schema_validator.validate(self._test_input_dict)

    def test_all_errors_json_str(self):
        errors = self._schema_validator.all_errors(self._test_input_str)
        self.assertEqual(errors, [])

    def test_all_errors_json_file(self):
        errors = self._schema_validator.all_errors(self._test_input_file)
        self.assertEqual(errors, [])

    def test_all_errors_json_dict(self):
        errors = self._schema_validator.all_errors(self._test_input_dict)
        self.assertEqual(errors, [])


class AnnotationSchemaTests(TestCase):

    def setUp(self):
        self.annotation = {
            'assertionId': 'a',
            'assertionReferenceId': 'a',
            'assertionReferenceIdLabel': 'a',
            'time': 'a',
            'annotationType': 'a',
            'key': 'a',
            'value': 'a',
            'itemAction': 'a',
            'itemActionTime': 'a',
            'creator': 'a',
            'system': 'a',
            'securityTag': '',
        }
        self.object_item = {
            'objectId': 'a',
            'sha256Hash': 'a' * 64,
            'md5Hash': 'a' * 32,
            'authorityInformation': {
                'securityTag': 'a'
            },
            'objectAssertions': {
                'annotations': [self.annotation]
            }
        }
        self.ties = {
            'version': '0.9',
            'securityTag': 'a',
            'objectItems': [self.object_item]
        }

    def test_all_fields(self):
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_only_required_fields(self):
        del self.annotation['assertionReferenceId']
        del self.annotation['assertionReferenceIdLabel']
        del self.annotation['time']
        del self.annotation['key']
        del self.annotation['itemAction']
        del self.annotation['itemActionTime']
        del self.annotation['creator']
        del self.annotation['system']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_all_required_fields_missing(self):
        del self.annotation['assertionId']
        del self.annotation['annotationType']
        del self.annotation['value']
        del self.annotation['securityTag']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required properties [annotationType, assertionId, securityTag, value] are missing')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]')

    def test_additional_field(self):
        self.annotation['foo'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional property foo is not allowed')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]')

    def test_additional_fields(self):
        self.annotation['foo'] = 'a'
        self.annotation['bar'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional properties [bar, foo] are not allowed')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]')

    def test_assertion_id_missing(self):
        del self.annotation['assertionId']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property assertionId is missing')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]')

    def test_assertion_id_too_short(self):
        self.annotation['assertionId'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for assertionId property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]/assertionId')

    def test_assertion_id_too_long(self):
        self.annotation['assertionId'] = 'a' * 257
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '{}' for assertionId property is too long, maximum length 256".format('a' * 257))
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]/assertionId')

    def test_assertion_reference_id_missing(self):
        del self.annotation['assertionReferenceId']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_assertion_reference_id_too_short(self):
        self.annotation['assertionReferenceId'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for assertionReferenceId property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]/assertionReferenceId')

    def test_assertion_reference_id_label_missing(self):
        del self.annotation['assertionReferenceIdLabel']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_assertion_reference_id_label_too_short(self):
        self.annotation['assertionReferenceIdLabel'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for assertionReferenceIdLabel property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]/assertionReferenceIdLabel')

    def test_time_missing(self):
        del self.annotation['time']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_annotationType_missing(self):
        del self.annotation['annotationType']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property annotationType is missing')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]')

    def test_annotationType_too_short(self):
        self.annotation['annotationType'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for annotationType property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]/annotationType')

    def test_key_missing(self):
        del self.annotation['key']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_key_too_short(self):
        self.annotation['key'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for key property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]/key')

    def test_value_missing(self):
        del self.annotation['value']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property value is missing')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]')

    def test_value_too_short(self):
        self.annotation['value'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for value property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]/value')

    def test_item_action_missing(self):
        del self.annotation['itemAction']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_item_action_too_short(self):
        self.annotation['itemAction'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for itemAction property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]/itemAction')

    def test_item_action_time_missing(self):
        del self.annotation['itemActionTime']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_creator_missing(self):
        del self.annotation['creator']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_creator_too_short(self):
        self.annotation['creator'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for creator property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]/creator')

    def test_system_missing(self):
        del self.annotation['system']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_system_too_short(self):
        self.annotation['system'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for system property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]/system')

    def test_security_tag_missing(self):
        del self.annotation['securityTag']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property securityTag is missing')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations[0]')


class AuthorityInformationSchemaTests(TestCase):

    def setUp(self):
        self.authority_information = {
            'collectionId': 'a',
            'collectionIdLabel': 'a',
            'collectionIdAlias': 'a',
            'collectionDescription': 'a',
            'subCollectionId': 'a',
            'subCollectionIdLabel': 'a',
            'subCollectionIdAlias': 'a',
            'subCollectionDescription': 'a',
            'registrationDate': 'a',
            'expirationDate': 'a',
            'securityTag': '',
            'owner': 'a',
        }
        self.object_item = {
            'objectId': 'a',
            'sha256Hash': 'a' * 64,
            'md5Hash': 'a' * 32,
            'authorityInformation': self.authority_information,
        }
        self.ties = {
            'version': '0.9',
            'securityTag': 'a',
            'objectItems': [self.object_item]
        }

    def test_all_fields(self):
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_only_required_fields(self):
        del self.authority_information['collectionId']
        del self.authority_information['collectionIdLabel']
        del self.authority_information['collectionIdAlias']
        del self.authority_information['collectionDescription']
        del self.authority_information['subCollectionId']
        del self.authority_information['subCollectionIdLabel']
        del self.authority_information['subCollectionIdAlias']
        del self.authority_information['subCollectionDescription']
        del self.authority_information['registrationDate']
        del self.authority_information['expirationDate']
        del self.authority_information['owner']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_all_required_fields_missing(self):
        del self.authority_information['securityTag']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property securityTag is missing')
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation')

    def test_additional_field(self):
        self.authority_information['foo'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional property foo is not allowed')
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation')

    def test_additional_fields(self):
        self.authority_information['foo'] = 'a'
        self.authority_information['bar'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional properties [bar, foo] are not allowed')
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation')

    def test_collection_id_missing(self):
        del self.authority_information['collectionId']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_collection_id_too_short(self):
        self.authority_information['collectionId'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for collectionId property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation/collectionId')

    def test_collection_id_label_missing(self):
        del self.authority_information['collectionIdLabel']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_collection_id_label_too_short(self):
        self.authority_information['collectionIdLabel'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for collectionIdLabel property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation/collectionIdLabel')

    def test_collection_id_alias_missing(self):
        del self.authority_information['collectionIdAlias']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_collection_id_alias_too_short(self):
        self.authority_information['collectionIdAlias'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for collectionIdAlias property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation/collectionIdAlias')

    def test_collection_description_missing(self):
        del self.authority_information['collectionDescription']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_collection_description_too_short(self):
        self.authority_information['collectionDescription'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for collectionDescription property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation/collectionDescription')

    def test_sub_collection_id_missing(self):
        del self.authority_information['subCollectionId']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_sub_collection_id_too_short(self):
        self.authority_information['subCollectionId'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for subCollectionId property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation/subCollectionId')

    def test_sub_collection_id_label_missing(self):
        del self.authority_information['subCollectionIdLabel']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_sub_collection_id_label_too_short(self):
        self.authority_information['subCollectionIdLabel'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for subCollectionIdLabel property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation/subCollectionIdLabel')

    def test_sub_collection_id_alias_missing(self):
        del self.authority_information['subCollectionIdAlias']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_sub_collection_id_alias_too_short(self):
        self.authority_information['subCollectionIdAlias'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for subCollectionIdAlias property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation/subCollectionIdAlias')

    def test_sub_collection_description_missing(self):
        del self.authority_information['subCollectionDescription']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_sub_collection_description_too_short(self):
        self.authority_information['subCollectionDescription'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for subCollectionDescription property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation/subCollectionDescription')

    def test_registration_date_missing(self):
        del self.authority_information['registrationDate']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_expiration_date_missing(self):
        del self.authority_information['expirationDate']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_security_tag_missing(self):
        del self.authority_information['securityTag']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property securityTag is missing')
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation')

    def test_security_tag_too_short(self):
        self.authority_information['securityTag'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_owner_missing(self):
        del self.authority_information['owner']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_owner_too_short(self):
        self.authority_information['owner'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for owner property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/authorityInformation/owner')


class ObjectAssertionsSchemaTests(TestCase):

    def setUp(self):
        self.annotation = {
            'assertionId': 'a',
            'annotationType': 'a',
            'value': 'a',
            'securityTag': '',
        }
        self.supplemental_description = {
            'assertionId': 'a',
            'informationType': 'a',
            'sha256DataHash': 'a' * 64,
            'dataSize': 0,
            'securityTag': '',
        }
        self.object_assertions = {
            'annotations': [self.annotation],
            'supplementalDescriptions': [self.supplemental_description],
        }
        self.object_item = {
            'objectId': 'a',
            'sha256Hash': 'a' * 64,
            'md5Hash': 'a' * 32,
            'authorityInformation': {
                'securityTag': '',
            },
            'objectAssertions': self.object_assertions,
        }
        self.ties = {
            'version': '0.9',
            'securityTag': 'a',
            'objectItems': [self.object_item]
        }

    def test_all_fields(self):
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_only_required_fields(self):
        del self.object_assertions['annotations']
        del self.object_assertions['supplementalDescriptions']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_all_required_fields_missing(self):
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_additional_field(self):
        self.object_assertions['foo'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional property foo is not allowed')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions')

    def test_additional_fields(self):
        self.object_assertions['foo'] = 'a'
        self.object_assertions['bar'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional properties [bar, foo] are not allowed')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions')

    def test_annotations_missing(self):
        del self.object_assertions['annotations']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_annotations_list_too_short(self):
        self.object_assertions['annotations'] = []
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_annotations_list_duplicate_items(self):
        self.object_assertions['annotations'].append(self.object_assertions['annotations'][0])
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'array property annotations has duplicate items at index [0, 1]')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/annotations')

    def test_supplemental_descriptions_missing(self):
        del self.object_assertions['supplementalDescriptions']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_supplemental_descriptions_list_too_short(self):
        self.object_assertions['supplementalDescriptions'] = []
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_supplemental_descriptions_list_duplicate_items(self):
        self.object_assertions['supplementalDescriptions'].append(self.object_assertions['supplementalDescriptions'][0])
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'array property supplementalDescriptions has duplicate items at index [0, 1]')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions')


class ObjectItemSchemaTests(TestCase):

    def setUp(self):
        self.object_item = {
            'objectId': 'a',
            'mimeType': 'a',
            'sha256Hash': 'a' * 64,
            'md5Hash': 'a' * 32,
            'size': 0,
            'originalPath': 'a',
            'relativeUri': 'a',
            'authorityInformation': {
                'securityTag': '',
            },
            'objectAssertions': {},
            'otherInformation': [],
        }
        self.ties = {
            'version': '0.9',
            'securityTag': 'a',
            'objectItems': [self.object_item]
        }

    def test_all_fields(self):
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_only_required_fields(self):
        del self.object_item['mimeType']
        del self.object_item['size']
        del self.object_item['originalPath']
        del self.object_item['relativeUri']
        del self.object_item['objectAssertions']
        del self.object_item['otherInformation']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_all_required_fields_missing(self):
        del self.object_item['objectId']
        del self.object_item['sha256Hash']
        del self.object_item['md5Hash']
        del self.object_item['authorityInformation']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required properties [authorityInformation, md5Hash, objectId, sha256Hash] are missing')
        self.assertEqual(errors[0].location, '/objectItems[0]')

    def test_additional_field(self):
        self.object_item['foo'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional property foo is not allowed')
        self.assertEqual(errors[0].location, '/objectItems[0]')

    def test_additional_fields(self):
        self.object_item['foo'] = 'a'
        self.object_item['bar'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional properties [bar, foo] are not allowed')
        self.assertEqual(errors[0].location, '/objectItems[0]')

    def test_object_id_missing(self):
        del self.object_item['objectId']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property objectId is missing')
        self.assertEqual(errors[0].location, '/objectItems[0]')

    def test_object_id_too_short(self):
        self.object_item['objectId'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for objectId property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/objectId')

    def test_object_id_too_long(self):
        self.object_item['objectId'] = 'a' * 257
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '{}' for objectId property is too long, maximum length 256".format('a' * 257))
        self.assertEqual(errors[0].location, '/objectItems[0]/objectId')

    def test_mime_type_missing(self):
        del self.object_item['mimeType']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_mime_type_too_short(self):
        self.object_item['mimeType'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for mimeType property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/mimeType')

    def test_sha256_hash_missing(self):
        del self.object_item['sha256Hash']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property sha256Hash is missing')
        self.assertEqual(errors[0].location, '/objectItems[0]')

    def test_sha256_hash_too_short(self):
        self.object_item['sha256Hash'] = 'a' * 63
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 2)
        self.assertEqual(errors[0].message, "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256Hash property is too short, minimum length 64")
        self.assertEqual(errors[0].location, '/objectItems[0]/sha256Hash')
        self.assertEqual(errors[1].message, "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256Hash property does not match the pattern '^[a-fA-F0-9]{64}$'")
        self.assertEqual(errors[1].location, '/objectItems[0]/sha256Hash')

    def test_sha256_hash_too_long(self):
        self.object_item['sha256Hash'] = 'a' * 65
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 2)
        self.assertEqual(errors[0].message, "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256Hash property is too long, maximum length 64")
        self.assertEqual(errors[0].location, '/objectItems[0]/sha256Hash')
        self.assertEqual(errors[1].message, "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256Hash property does not match the pattern '^[a-fA-F0-9]{64}$'")
        self.assertEqual(errors[1].location, '/objectItems[0]/sha256Hash')

    def test_sha256_hash_bad_format(self):
        self.object_item['sha256Hash'] = 'z' * 64
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value 'zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz' for sha256Hash property does not match the pattern '^[a-fA-F0-9]{64}$'")
        self.assertEqual(errors[0].location, '/objectItems[0]/sha256Hash')

    def test_md5_hash_missing(self):
        del self.object_item['md5Hash']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property md5Hash is missing')
        self.assertEqual(errors[0].location, '/objectItems[0]')

    def test_md5_hash_too_short(self):
        self.object_item['md5Hash'] = 'a' * 31
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 2)
        self.assertEqual(errors[0].message, "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for md5Hash property is too short, minimum length 32")
        self.assertEqual(errors[0].location, '/objectItems[0]/md5Hash')
        self.assertEqual(errors[1].message, "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for md5Hash property does not match the pattern '^[a-fA-F0-9]{32}$'")
        self.assertEqual(errors[1].location, '/objectItems[0]/md5Hash')

    def test_md5_hash_too_long(self):
        self.object_item['md5Hash'] = 'a' * 33
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 2)
        self.assertEqual(errors[0].message, "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for md5Hash property is too long, maximum length 32")
        self.assertEqual(errors[0].location, '/objectItems[0]/md5Hash')
        self.assertEqual(errors[1].message, "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for md5Hash property does not match the pattern '^[a-fA-F0-9]{32}$'")
        self.assertEqual(errors[1].location, '/objectItems[0]/md5Hash')

    def test_md5_hash_bad_format(self):
        self.object_item['md5Hash'] = 'z' * 32
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value 'zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz' for md5Hash property does not match the pattern '^[a-fA-F0-9]{32}$'")
        self.assertEqual(errors[0].location, '/objectItems[0]/md5Hash')

    def test_size_missing(self):
        del self.object_item['size']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_size_too_small(self):
        self.object_item['size'] = -1
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'property value -1 for size property is less than the minimum value of 0')
        self.assertEqual(errors[0].location, '/objectItems[0]/size')

    def test_original_path_missing(self):
        del self.object_item['originalPath']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_original_path_too_short(self):
        self.object_item['originalPath'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for originalPath property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/originalPath')

    def test_relative_uri_missing(self):
        del self.object_item['relativeUri']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_relative_uri_too_short(self):
        self.object_item['relativeUri'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for relativeUri property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectItems[0]/relativeUri')

    def test_authority_information_missing(self):
        del self.object_item['authorityInformation']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property authorityInformation is missing')
        self.assertEqual(errors[0].location, '/objectItems[0]')

    def test_object_assertions_missing(self):
        del self.object_item['objectAssertions']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_other_information_missing(self):
        del self.object_item['otherInformation']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_other_information_empty_list(self):
        self.object_item['otherInformation'] = []
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_other_information_duplicate_items(self):
        other_info = {'key': 'a', 'value': 'a'}
        self.object_item['otherInformation'] = [other_info, other_info]
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'array property otherInformation has duplicate items at index [0, 1]')
        self.assertEqual(errors[0].location, '/objectItems[0]/otherInformation')


class ObjectRelationshipSchemaTests(TestCase):

    def setUp(self):
        self.object_relationship = {
            'linkageMemberIds': ['a', 'b'],
            'linkageDirectionality': 'DIRECTED',
            'linkageType': 'a',
            'linkageAssertionId': 'a',
            'otherInformation': [],
        }
        self.object_item = {
            'objectId': 'a',
            'sha256Hash': 'a' * 64,
            'md5Hash': 'a' * 32,
            'authorityInformation': {
                'securityTag': '',
            },
        }
        self.ties = {
            'version': '0.9',
            'securityTag': 'a',
            'objectItems': [self.object_item],
            'objectRelationships': [self.object_relationship],
        }

    def test_all_fields(self):
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_only_required_fields(self):
        del self.object_relationship['linkageType']
        del self.object_relationship['linkageAssertionId']
        del self.object_relationship['otherInformation']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_all_required_fields_missing(self):
        del self.object_relationship['linkageMemberIds']
        del self.object_relationship['linkageDirectionality']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required properties [linkageDirectionality, linkageMemberIds] are missing')
        self.assertEqual(errors[0].location, '/objectRelationships[0]')

    def test_additional_field(self):
        self.object_relationship['foo'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional property foo is not allowed')
        self.assertEqual(errors[0].location, '/objectRelationships[0]')

    def test_additional_fields(self):
        self.object_relationship['foo'] = 'a'
        self.object_relationship['bar'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional properties [bar, foo] are not allowed')
        self.assertEqual(errors[0].location, '/objectRelationships[0]')

    def test_linkage_member_ids_missing(self):
        del self.object_relationship['linkageMemberIds']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property linkageMemberIds is missing')
        self.assertEqual(errors[0].location, '/objectRelationships[0]')

    def test_linkage_member_ids_too_small(self):
        self.object_relationship['linkageMemberIds'] = ['a']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "array property linkageMemberIds with 1 items is too small, minimum size 2")
        self.assertEqual(errors[0].location, '/objectRelationships[0]/linkageMemberIds')

    def test_linkage_member_ids_too_large(self):
        self.object_relationship['linkageMemberIds'] = ['a', 'b', 'c']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "array property linkageMemberIds with 3 items is too large, maximum size 2")
        self.assertEqual(errors[0].location, '/objectRelationships[0]/linkageMemberIds')

    def test_linkage_member_id_too_short(self):
        self.object_relationship['linkageMemberIds'] = ['a', '']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for element at index 1 in linkageMemberIds is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectRelationships[0]/linkageMemberIds[1]')

    def test_linkage_member_id_too_long(self):
        self.object_relationship['linkageMemberIds'] = ['a', 'a' * 257]
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '{}' for element at index 1 in linkageMemberIds is too long, maximum length 256".format('a' * 257))
        self.assertEqual(errors[0].location, '/objectRelationships[0]/linkageMemberIds[1]')

    def test_linkage_directionality_missing(self):
        del self.object_relationship['linkageDirectionality']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property linkageDirectionality is missing')
        self.assertEqual(errors[0].location, '/objectRelationships[0]')

    def test_linkage_directionality_invalid_value(self):
        self.object_relationship['linkageDirectionality'] = 'INVALID'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "enum property linkageDirectionality with value 'INVALID' should have one of the allowed values: [DIRECTED, BIDIRECTED, UNDIRECTED]")
        self.assertEqual(errors[0].location, '/objectRelationships[0]/linkageDirectionality')

    def test_linkage_type_missing(self):
        del self.object_relationship['linkageType']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_linkage_type_too_short(self):
        self.object_relationship['linkageType'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for linkageType property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectRelationships[0]/linkageType')

    def test_linkage_assertion_id_missing(self):
        del self.object_relationship['linkageAssertionId']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_linkage_assertion_id_too_short(self):
        self.object_relationship['linkageAssertionId'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for linkageAssertionId property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectRelationships[0]/linkageAssertionId')

    def test_other_information_missing(self):
        del self.object_relationship['otherInformation']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_other_information_empty_list(self):
        self.object_relationship['otherInformation'] = []
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_other_information_duplicate_items(self):
        other_info = {'key': 'a', 'value': 'a'}
        self.object_relationship['otherInformation'] = [other_info, other_info]
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'array property otherInformation has duplicate items at index [0, 1]')
        self.assertEqual(errors[0].location, '/objectRelationships[0]/otherInformation')


class OtherInformationSchemaTests(TestCase):

    def setUp(self):
        self.object_item = {
            'objectId': 'a',
            'sha256Hash': 'a' * 64,
            'md5Hash': 'a' * 32,
            'authorityInformation': {
                'securityTag': '',
            },
        }
        self.other_information = {
            'key': 'a',
            'value': 'a',
        }
        self.object_relationship = {
            'linkageMemberIds': ['a', 'b'],
            'linkageDirectionality': 'DIRECTED',
            'otherInformation': [self.other_information],
        }
        self.ties = {
            'version': '0.9',
            'securityTag': 'a',
            'objectItems': [self.object_item],
            'objectRelationships': [self.object_relationship],
        }

    def test_all_fields(self):
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_all_required_fields_missing(self):
        del self.other_information['key']
        del self.other_information['value']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required properties [key, value] are missing')
        self.assertEqual(errors[0].location, '/objectRelationships[0]/otherInformation[0]')

    def test_additional_field(self):
        self.other_information['foo'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional property foo is not allowed')
        self.assertEqual(errors[0].location, '/objectRelationships[0]/otherInformation[0]')

    def test_additional_fields(self):
        self.other_information['foo'] = 'a'
        self.other_information['bar'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional properties [bar, foo] are not allowed')
        self.assertEqual(errors[0].location, '/objectRelationships[0]/otherInformation[0]')

    def test_key_missing(self):
        del self.other_information['key']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property key is missing')
        self.assertEqual(errors[0].location, '/objectRelationships[0]/otherInformation[0]')

    def test_key_too_short(self):
        self.other_information['key'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for key property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/objectRelationships[0]/otherInformation[0]/key')

    def test_value_missing(self):
        del self.other_information['value']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property value is missing')
        self.assertEqual(errors[0].location, '/objectRelationships[0]/otherInformation[0]')

    def test_value_boolean(self):
        self.other_information['value'] = True
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_value_integer(self):
        self.other_information['value'] = 1
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_value_number(self):
        self.other_information['value'] = 1.1
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_value_string(self):
        self.other_information['value'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_value_array(self):
        self.other_information['value'] = []
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'property type array for property value is not one of the allowed types: [string, boolean, integer, number]')
        self.assertEqual(errors[0].location, '/objectRelationships[0]/otherInformation[0]/value')

    def test_value_object(self):
        self.other_information['value'] = {}
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'property type object for property value is not one of the allowed types: [string, boolean, integer, number]')
        self.assertEqual(errors[0].location, '/objectRelationships[0]/otherInformation[0]/value')


class SupplementalDescriptionDataFileSchemaTests(TestCase):

    def setUp(self):
        self.supplemental_description = {
            'assertionId': 'a',
            'assertionReferenceId': 'a',
            'assertionReferenceIdLabel': 'a',
            'system': 'a',
            'informationType': 'a',
            'sha256DataHash': 'a' * 64,
            'dataSize': 0,
            'dataRelativeUri': 'a',
            'securityTag': '',
        }
        self.object_item = {
            'objectId': 'a',
            'sha256Hash': 'a' * 64,
            'md5Hash': 'a' * 32,
            'authorityInformation': {
                'securityTag': ''
            },
            'objectAssertions': {
                'supplementalDescriptions': [self.supplemental_description]
            }
        }
        self.ties = {
            'version': '0.9',
            'securityTag': '',
            'objectItems': [self.object_item]
        }

    def test_all_fields(self):
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_only_required_fields(self):
        del self.supplemental_description['assertionReferenceId']
        del self.supplemental_description['assertionReferenceIdLabel']
        del self.supplemental_description['system']
        del self.supplemental_description['dataRelativeUri']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_all_required_fields_missing(self):
        del self.supplemental_description['assertionId']
        del self.supplemental_description['informationType']
        del self.supplemental_description['sha256DataHash']
        del self.supplemental_description['dataSize']
        del self.supplemental_description['securityTag']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataRelativeUri is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [assertionId, dataSize, informationType, securityTag, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required properties [assertionId, dataObject, informationType, securityTag] are missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_additional_field(self):
        self.supplemental_description['foo'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property foo is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'additional properties [dataRelativeUri, dataSize, foo, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_additional_fields(self):
        self.supplemental_description['foo'] = 'a'
        self.supplemental_description['bar'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [bar, foo] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'additional properties [bar, dataRelativeUri, dataSize, foo, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_id_missing(self):
        del self.supplemental_description['assertionId']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property assertionId is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required properties [assertionId, dataObject] are missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_id_null(self):
        self.supplemental_description['assertionId'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property assertionId with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_id_too_short(self):
        self.supplemental_description['assertionId'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '' for assertionId property is too short, minimum length 1")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_id_too_long(self):
        self.supplemental_description['assertionId'] = 'a' * 257
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '{}' for assertionId property is too long, maximum length 256".format('a' * 257))
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_reference_id_missing(self):
        del self.supplemental_description['assertionReferenceId']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_assertion_reference_id_null(self):
        self.supplemental_description['assertionReferenceId'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property assertionReferenceId with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionReferenceId')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_reference_id_too_short(self):
        self.supplemental_description['assertionReferenceId'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '' for assertionReferenceId property is too short, minimum length 1")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionReferenceId')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_reference_id_label_missing(self):
        del self.supplemental_description['assertionReferenceIdLabel']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_assertion_reference_id_label_null(self):
        self.supplemental_description['assertionReferenceIdLabel'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property assertionReferenceIdLabel with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionReferenceIdLabel')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_reference_id_label_too_short(self):
        self.supplemental_description['assertionReferenceIdLabel'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '' for assertionReferenceIdLabel property is too short, minimum length 1")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionReferenceIdLabel')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_system_missing(self):
        del self.supplemental_description['system']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_system_null(self):
        self.supplemental_description['system'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property system with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/system')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_system_too_short(self):
        self.supplemental_description['system'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '' for system property is too short, minimum length 1")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/system')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_information_type_missing(self):
        del self.supplemental_description['informationType']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property informationType is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required properties [dataObject, informationType] are missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_information_type_null(self):
        self.supplemental_description['informationType'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property informationType with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/informationType')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_information_type_too_short(self):
        self.supplemental_description['informationType'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '' for informationType property is too short, minimum length 1")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/informationType')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_sha256_data_hash_missing(self):
        del self.supplemental_description['sha256DataHash']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property sha256DataHash is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_sha256_data_hash_null(self):
        self.supplemental_description['sha256DataHash'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property sha256DataHash with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/sha256DataHash')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_sha256_data_hash_too_short(self):
        self.supplemental_description['sha256DataHash'] = 'a' * 63
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 4)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256DataHash property is too short, minimum length 64")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/sha256DataHash')
        self.assertEqual(len(errors[0].causes[2].causes), 0)
        self.assertEqual(errors[0].causes[3].message, "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256DataHash property does not match the pattern '^[a-fA-F0-9]{64}$'")
        self.assertEqual(errors[0].causes[3].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/sha256DataHash')
        self.assertEqual(len(errors[0].causes[3].causes), 0)

    def test_sha256_data_hash_too_long(self):
        self.supplemental_description['sha256DataHash'] = 'a' * 65
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 4)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256DataHash property is too long, maximum length 64")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/sha256DataHash')
        self.assertEqual(len(errors[0].causes[2].causes), 0)
        self.assertEqual(errors[0].causes[3].message, "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256DataHash property does not match the pattern '^[a-fA-F0-9]{64}$'")
        self.assertEqual(errors[0].causes[3].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/sha256DataHash')
        self.assertEqual(len(errors[0].causes[3].causes), 0)

    def test_sha256_hash_bad_format(self):
        self.supplemental_description['sha256DataHash'] = 'z' * 64
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value 'zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz' for sha256DataHash property does not match the pattern '^[a-fA-F0-9]{64}$'")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/sha256DataHash')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_data_size_missing(self):
        del self.supplemental_description['dataSize']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataSize is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_data_size_null(self):
        self.supplemental_description['dataSize'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property dataSize with null value should be of type integer')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/dataSize')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_data_size_too_small(self):
        self.supplemental_description['dataSize'] = -1
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property value -1 for dataSize property is less than the minimum value of 0')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/dataSize')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_data_relative_uri_missing(self):
        del self.supplemental_description['dataRelativeUri']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_data_relative_uri_null(self):
        self.supplemental_description['dataRelativeUri'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property dataRelativeUri with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/dataRelativeUri')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_data_relative_uri_too_short(self):
        self.supplemental_description['dataRelativeUri'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '' for dataRelativeUri property is too short, minimum length 1")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/dataRelativeUri')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_security_tag_missing(self):
        del self.supplemental_description['securityTag']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property securityTag is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required properties [dataObject, securityTag] are missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_security_tag_null(self):
        self.supplemental_description['securityTag'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property securityTag with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/securityTag')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_security_tag_empty_string(self):
        self.supplemental_description['securityTag'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)


class SupplementalDescriptionDataObjectSchemaTests(TestCase):

    def setUp(self):
        self.supplemental_description = {
            'assertionId': 'a',
            'assertionReferenceId': 'a',
            'assertionReferenceIdLabel': 'a',
            'system': 'a',
            'informationType': 'a',
            'dataObject': {},
            'securityTag': '',
        }
        self.object_item = {
            'objectId': 'a',
            'sha256Hash': 'a' * 64,
            'md5Hash': 'a' * 32,
            'authorityInformation': {
                'securityTag': ''
            },
            'objectAssertions': {
                'supplementalDescriptions': [self.supplemental_description]
            }
        }
        self.ties = {
            'version': '0.9',
            'securityTag': '',
            'objectItems': [self.object_item]
        }

    def test_all_fields(self):
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_only_required_fields(self):
        del self.supplemental_description['assertionReferenceId']
        del self.supplemental_description['assertionReferenceIdLabel']
        del self.supplemental_description['system']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_all_required_fields_missing(self):
        del self.supplemental_description['assertionId']
        del self.supplemental_description['informationType']
        del self.supplemental_description['dataObject']
        del self.supplemental_description['securityTag']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 2)
        self.assertEqual(errors[0].causes[0].message, 'required properties [assertionId, dataSize, informationType, securityTag, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [assertionId, dataObject, informationType, securityTag] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)

    def test_additional_field(self):
        self.supplemental_description['foo'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [dataObject, foo] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'additional property foo is not allowed')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_additional_fields(self):
        self.supplemental_description['foo'] = 'a'
        self.supplemental_description['bar'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional properties [bar, dataObject, foo] are not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'additional properties [bar, foo] are not allowed')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_id_missing(self):
        del self.supplemental_description['assertionId']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [assertionId, dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required property assertionId is missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_id_null(self):
        self.supplemental_description['assertionId'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property assertionId with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_id_too_short(self):
        self.supplemental_description['assertionId'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '' for assertionId property is too short, minimum length 1")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_id_too_long(self):
        self.supplemental_description['assertionId'] = 'a' * 257
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '{}' for assertionId property is too long, maximum length 256".format('a' * 257))
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_reference_id_missing(self):
        del self.supplemental_description['assertionReferenceId']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_assertion_reference_id_null(self):
        self.supplemental_description['assertionReferenceId'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property assertionReferenceId with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionReferenceId')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_reference_id_too_short(self):
        self.supplemental_description['assertionReferenceId'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '' for assertionReferenceId property is too short, minimum length 1")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionReferenceId')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_reference_id_label_missing(self):
        del self.supplemental_description['assertionReferenceIdLabel']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_assertion_reference_id_label_null(self):
        self.supplemental_description['assertionReferenceIdLabel'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property assertionReferenceIdLabel with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionReferenceIdLabel')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_assertion_reference_id_label_too_short(self):
        self.supplemental_description['assertionReferenceIdLabel'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '' for assertionReferenceIdLabel property is too short, minimum length 1")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionReferenceIdLabel')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_system_missing(self):
        del self.supplemental_description['system']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_system_null(self):
        self.supplemental_description['system'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property system with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/system')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_system_too_short(self):
        self.supplemental_description['system'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '' for system property is too short, minimum length 1")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/system')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_information_type_missing(self):
        del self.supplemental_description['informationType']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, informationType, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required property informationType is missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_information_type_null(self):
        self.supplemental_description['informationType'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property informationType with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/informationType')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_information_type_too_short(self):
        self.supplemental_description['informationType'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, "property value '' for informationType property is too short, minimum length 1")
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/informationType')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_data_object_missing(self):
        del self.supplemental_description['dataObject']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 2)
        self.assertEqual(errors[0].causes[0].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required property dataObject is missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)

    def test_data_object_null(self):
        self.supplemental_description['dataObject'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property dataObject with null value should be of type object')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/dataObject')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_data_object(self):
        self.supplemental_description['dataObject'] = {
            'string': '',
            'int': 1,
            'number': 1.1,
            'object': {},
            'array': [],
            'boolean': True,
            'null': None,
        }
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_security_tag_missing(self):
        del self.supplemental_description['securityTag']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, securityTag, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'required property securityTag is missing')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_security_tag_null(self):
        self.supplemental_description['securityTag'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions')
        self.assertEqual(errors[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes), 3)
        self.assertEqual(errors[0].causes[0].message, 'additional property dataObject is not allowed')
        self.assertEqual(errors[0].causes[0].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[0].causes), 0)
        self.assertEqual(errors[0].causes[1].message, 'required properties [dataSize, sha256DataHash] are missing')
        self.assertEqual(errors[0].causes[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]')
        self.assertEqual(len(errors[0].causes[1].causes), 0)
        self.assertEqual(errors[0].causes[2].message, 'property securityTag with null value should be of type string')
        self.assertEqual(errors[0].causes[2].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/securityTag')
        self.assertEqual(len(errors[0].causes[2].causes), 0)

    def test_security_tag_empty_string(self):
        self.supplemental_description['securityTag'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)


class TiesSchemaTests(TestCase):

    def setUp(self):
        self.ties = {
            'version': '0.9',
            'id': 'a',
            'system': 'a',
            'organization': 'a',
            'time': 'a',
            'description': 'a',
            'type': 'a',
            'securityTag': '',
            'objectItems': [
                {
                    'objectId': 'a',
                    'sha256Hash': 'a' * 64,
                    'md5Hash': 'a' * 32,
                    'authorityInformation': {
                        'securityTag': '',
                    },
                }
            ],
            'objectRelationships': [],
            'otherInformation': [],
        }

    def test_all_fields(self):
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_only_required_fields(self):
        del self.ties['id']
        del self.ties['system']
        del self.ties['organization']
        del self.ties['time']
        del self.ties['description']
        del self.ties['type']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_all_required_fields_missing(self):
        del self.ties['version']
        del self.ties['securityTag']
        del self.ties['objectItems']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required properties [objectItems, securityTag, version] are missing')
        self.assertEqual(errors[0].location, '/')

    def test_additional_field(self):
        self.ties['foo'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional property foo is not allowed')
        self.assertEqual(errors[0].location, '/')

    def test_additional_fields(self):
        self.ties['foo'] = 'a'
        self.ties['bar'] = 'a'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'additional properties [bar, foo] are not allowed')
        self.assertEqual(errors[0].location, '/')

    def test_version_missing(self):
        del self.ties['version']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property version is missing')
        self.assertEqual(errors[0].location, '/')

    def test_version_null(self):
        self.ties['version'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "enum property version with value null should have one of the allowed values: [0.9]")
        self.assertEqual(errors[0].location, '/version')

    def test_version_empty_string(self):
        self.ties['version'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "enum property version with value '' should have one of the allowed values: [0.9]")
        self.assertEqual(errors[0].location, '/version')

    def test_version_invalid_value(self):
        self.ties['version'] = '0.1'
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "enum property version with value '0.1' should have one of the allowed values: [0.9]")
        self.assertEqual(errors[0].location, '/version')

    def test_id_missing(self):
        del self.ties['id']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_id_null(self):
        self.ties['id'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'property id with null value should be of type string')
        self.assertEqual(errors[0].location, '/id')

    def test_id_too_short(self):
        self.ties['id'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for id property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/id')

    def test_id_too_long(self):
        self.ties['id'] = 'a' * 257
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '{}' for id property is too long, maximum length 256".format('a' * 257))
        self.assertEqual(errors[0].location, '/id')

    def test_system_missing(self):
        del self.ties['system']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_system_null(self):
        self.ties['system'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'property system with null value should be of type string')
        self.assertEqual(errors[0].location, '/system')

    def test_system_too_short(self):
        self.ties['system'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for system property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/system')

    def test_organization_missing(self):
        del self.ties['organization']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_organization_null(self):
        self.ties['organization'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'property organization with null value should be of type string')
        self.assertEqual(errors[0].location, '/organization')

    def test_organization_too_short(self):
        self.ties['organization'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for organization property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/organization')

    def test_time_missing(self):
        del self.ties['time']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_time_null(self):
        self.ties['time'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'property time with null value should be of type string')
        self.assertEqual(errors[0].location, '/time')

    def test_description_missing(self):
        del self.ties['description']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_description_null(self):
        self.ties['description'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'property description with null value should be of type string')
        self.assertEqual(errors[0].location, '/description')

    def test_description_too_short(self):
        self.ties['description'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for description property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/description')

    def test_type_missing(self):
        del self.ties['type']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_type_null(self):
        self.ties['type'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'property type with null value should be of type string')
        self.assertEqual(errors[0].location, '/type')

    def test_type_too_short(self):
        self.ties['type'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, "property value '' for type property is too short, minimum length 1")
        self.assertEqual(errors[0].location, '/type')

    def test_security_tag_missing(self):
        del self.ties['securityTag']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property securityTag is missing')
        self.assertEqual(errors[0].location, '/')

    def test_security_tag_null(self):
        self.ties['securityTag'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'property securityTag with null value should be of type string')
        self.assertEqual(errors[0].location, '/securityTag')

    def test_security_tag_empty_string(self):
        self.ties['securityTag'] = ''
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_object_items_missing(self):
        del self.ties['objectItems']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'required property objectItems is missing')
        self.assertEqual(errors[0].location, '/')

    def test_object_items_null(self):
        self.ties['objectItems'] = None
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'property objectItems with null value should be of type array')
        self.assertEqual(errors[0].location, '/objectItems')

    def test_object_items_too_small(self):
        self.ties['objectItems'] = []
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'array property objectItems with 0 items is too small, minimum size 1')
        self.assertEqual(errors[0].location, '/objectItems')

    def test_object_items_duplicate_items(self):
        obj_item = {
            'objectId': 'a',
            'sha256Hash': 'a' * 64,
            'md5Hash': 'a' * 32,
            'authorityInformation': {
                'securityTag': '',
            },
        }
        self.ties['objectItems'] = [obj_item, obj_item]
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'array property objectItems has duplicate items at index [0, 1]')
        self.assertEqual(errors[0].location, '/objectItems')

    def test_object_relationships_missing(self):
        del self.ties['objectRelationships']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_object_relationships_empty_list(self):
        self.ties['objectRelationships'] = []
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_object_relationships_duplicate_items(self):
        object_relationship = {
            'linkageMemberIds': ['a', 'b'],
            'linkageDirectionality': 'DIRECTED',
        }
        self.ties['objectRelationships'] = [object_relationship, object_relationship]
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'array property objectRelationships has duplicate items at index [0, 1]')
        self.assertEqual(errors[0].location, '/objectRelationships')

    def test_other_information_missing(self):
        del self.ties['otherInformation']
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_other_information_empty_list(self):
        self.ties['otherInformation'] = []
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 0)

    def test_other_information_duplicate_items(self):
        other_info = {'key': 'a', 'value': 'a'}
        self.ties['otherInformation'] = [other_info, other_info]
        errors = TiesSchemaValidator().all_errors(json.dumps(self.ties))
        self.assertEqual(len(errors), 1)
        self.assertEqual(errors[0].message, 'array property otherInformation has duplicate items at index [0, 1]')
        self.assertEqual(errors[0].location, '/otherInformation')


if __name__ == '__main__':
    unittest.main()
