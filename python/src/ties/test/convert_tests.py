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

from ties.convert import _0_dot_2_to_0_dot_3, _0_dot_3_to_0_dot_4, _0_dot_4_to_0_dot_5, _0_dot_5_to_0_dot_6, _0_dot_6_to_0_dot_7, _0_dot_7_to_0_dot_8, _0_dot_8_to_0_dot_9, convert


class ConvertTests(TestCase):

    def test_version_unknown_version(self):
        ties_json = {'version': '0.1'}
        convert(ties_json, 'U')
        self.assertEqual(ties_json['version'], '0.1')

    def test_version_0_dot_1_dot_8(self):
        ties_json = {'version': '0.1.8'}
        convert(ties_json, 'U')
        self.assertEqual(ties_json['version'], '0.9')

    def test_version_0_dot_2(self):
        ties_json = {'version': '0.2'}
        convert(ties_json, 'U')
        self.assertEqual(ties_json['version'], '0.9')

    def test_version_0_dot_3(self):
        ties_json = {'version': '0.3'}
        convert(ties_json)
        self.assertEqual(ties_json['version'], '0.9')

    def test_version_0_dot_4(self):
        ties_json = {'version': '0.4'}
        convert(ties_json)
        self.assertEqual(ties_json['version'], '0.9')

    def test_version_0_dot_5(self):
        ties_json = {'version': '0.5'}
        convert(ties_json)
        self.assertEqual(ties_json['version'], '0.9')

    def test_version_0_dot_6(self):
        ties_json = {'version': '0.6'}
        convert(ties_json)
        self.assertEqual(ties_json['version'], '0.9')

    def test_version_0_dot_7(self):
        ties_json = {'version': '0.7'}
        convert(ties_json)
        self.assertEqual(ties_json['version'], '0.9')

    def test_version_0_dot_8(self):
        ties_json = {'version': '0.8'}
        convert(ties_json)
        self.assertEqual(ties_json['version'], '0.9')

    def test_version_0_dot_9(self):
        ties_json = {'version': '0.9'}
        convert(ties_json)
        self.assertEqual(ties_json['version'], '0.9')

    def test_security_tag_none_0_dot_1_dot_8(self):
        ties_json = {'version': '0.1.8'}
        with self.assertRaises(ValueError):
            _0_dot_2_to_0_dot_3(ties_json, None)

    def test_security_tag_none_0_dot_2(self):
        ties_json = {'version': '0.2'}
        with self.assertRaises(ValueError):
            _0_dot_2_to_0_dot_3(ties_json, None)

    def test_security_tag_blank_0_dot_1_dot_8(self):
        ties_json = {'version': '0.1.8', 'securityTag': ''}
        with self.assertRaises(ValueError):
            _0_dot_2_to_0_dot_3(ties_json, None)

    def test_security_tag_blank_0_dot_2(self):
        ties_json = {'version': '0.2', 'securityTag': ''}
        with self.assertRaises(ValueError):
            _0_dot_2_to_0_dot_3(ties_json, None)

    def test_security_tag_missing(self):
        ties_json = {'version': '0.1.8'}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(ties_json['securityTag'], 'U')

    def test_security_tag_not_missing_0_1_8(self):
        ties_json = {'version': '0.1.8', 'securityTag': 'UNCLASSIFIED'}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(ties_json['securityTag'], 'UNCLASSIFIED')

    def test_security_tag_not_missing(self):
        ties_json = {'version': '0.3', 'securityTag': 'UNCLASSIFIED'}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(ties_json['securityTag'], 'UNCLASSIFIED')

    def test_time_bad_format(self):
        ties_json = {'version': '0.1.8', 'time': '1970-01-01T:00:00:00Z'}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(ties_json['time'], '1970-01-01T00:00:00Z')

    def test_time_missing(self):
        ties_json = {'version': '0.1.8'}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertTrue('time' not in ties_json)

    def test_object_item_rename(self):
        ties_json = {'version': '0.1.8', 'objectItem': []}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertTrue('objectItem' not in ties_json)
        self.assertTrue('objectItems' in ties_json)

    def test_object_item_relative_uri_rename(self):
        object_item_1_json = {'relativeURI': ''}
        object_item_2_json = {}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertTrue('relativeURI' not in object_item_1_json)
        self.assertTrue('relativeUri' in object_item_1_json)
        self.assertTrue('relativeURI' not in object_item_2_json)
        self.assertTrue('relativeUri' not in object_item_2_json)

    def test_object_item_system_identifier_rename(self):
        object_item_1_json = {'systemIdentifier': ''}
        object_item_2_json = {}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertTrue('systemIdentifier' not in object_item_1_json)
        self.assertTrue('systemId' in object_item_1_json)
        self.assertTrue('systemIdentifier' not in object_item_2_json)
        self.assertTrue('systemId' not in object_item_2_json)

    def test_object_relationship_linkage_assertion_id_rename(self):
        object_item_1_json = {'objectRelationships': [{'linkageAssertId': ''}]}
        object_item_2_json = {'objectRelationships': [{}]}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertTrue('linkageAssertId' not in object_item_1_json['objectRelationships'][0])
        self.assertTrue('linkageAssertionId' in object_item_1_json['objectRelationships'][0])
        self.assertTrue('linkageAssertId' not in object_item_2_json['objectRelationships'][0])
        self.assertTrue('linkageAssertionId' not in object_item_2_json['objectRelationships'][0])

    def test_ssd_data_hash_rename(self):
        object_item_1_json = {'objectAssertions': {'systemSupplementalDescriptions': [{'dataHash': ''}]}}
        object_item_2_json = {'objectAssertions': {'systemSupplementalDescriptions': [{}]}}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertTrue('dataHash' not in object_item_1_json['objectAssertions']['systemSupplementalDescriptions'][0])
        self.assertTrue('sha256DataHash' in object_item_1_json['objectAssertions']['systemSupplementalDescriptions'][0])
        self.assertTrue('dataHash' not in object_item_2_json['objectAssertions']['systemSupplementalDescriptions'][0])
        self.assertTrue('sha256DataHash' not in object_item_2_json['objectAssertions']['systemSupplementalDescriptions'][0])

    def test_object_item_security_tag(self):
        object_item_1_json = {'authorityInformation': {'securityTag': 'UNCLASSIFIED'}}
        object_item_2_json = {'authorityInformation': {}}
        object_item_3_json = {}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json, object_item_3_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(object_item_1_json['authorityInformation']['securityTag'], 'UNCLASSIFIED')
        self.assertEqual(object_item_2_json['authorityInformation']['securityTag'], 'U')
        self.assertEqual(object_item_3_json['authorityInformation']['securityTag'], 'U')

    def test_object_item_registration_date_bad_format(self):
        object_item_1_json = {'authorityInformation': {'registrationDate': '1970-01-01T:00:00:00Z'}}
        object_item_2_json = {'authorityInformation': {}}
        object_item_3_json = {}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json, object_item_3_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(object_item_1_json['authorityInformation']['registrationDate'], '1970-01-01T00:00:00Z')
        self.assertTrue('registrationDate' not in object_item_2_json['authorityInformation'])
        self.assertTrue('registrationDate' not in object_item_3_json['authorityInformation'])

    def test_object_item_expiration_date_bad_format(self):
        object_item_1_json = {'authorityInformation': {'expirationDate': '1970-01-01T:00:00:00Z'}}
        object_item_2_json = {'authorityInformation': {}}
        object_item_3_json = {}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json, object_item_3_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(object_item_1_json['authorityInformation']['expirationDate'], '1970-01-01T00:00:00Z')
        self.assertTrue('expirationDate' not in object_item_2_json['authorityInformation'])
        self.assertTrue('expirationDate' not in object_item_3_json['authorityInformation'])

    def test_annotation_security_tag(self):
        object_item_1_json = {'objectAssertions': {'annotations': [{'securityTag': 'UNCLASSIFIED'}]}}
        object_item_2_json = {'objectAssertions': {'annotations': [{}]}}
        object_item_3_json = {'objectAssertions': {'annotations': []}}
        object_item_4_json = {'objectAssertions': {}}
        object_item_5_json = {}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json, object_item_3_json, object_item_4_json, object_item_5_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(object_item_1_json['objectAssertions']['annotations'][0]['securityTag'], 'UNCLASSIFIED')
        self.assertEqual(object_item_2_json['objectAssertions']['annotations'][0]['securityTag'], 'U')
        self.assertEqual(len(object_item_3_json['objectAssertions']['annotations']), 0)
        self.assertTrue('annotations' not in object_item_4_json['objectAssertions'])
        self.assertTrue('objectAssertions' not in object_item_5_json)

    def test_annotation_value_minimum_length(self):
        object_item_1_json = {'objectAssertions': {'annotations': [{'value': ''}]}}
        object_item_2_json = {'objectAssertions': {'annotations': [{'value': 'other'}]}}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(object_item_1_json['objectAssertions']['annotations'][0]['value'], ' ')
        self.assertEqual(object_item_2_json['objectAssertions']['annotations'][0]['value'], 'other')

    def test_annotation_time_bad_format(self):
        object_item_1_json = {'objectAssertions': {'annotations': [{'time': '1970-01-01T:00:00:00Z'}]}}
        object_item_2_json = {'objectAssertions': {'annotations': [{}]}}
        object_item_3_json = {'objectAssertions': {'annotations': []}}
        object_item_4_json = {'objectAssertions': {}}
        object_item_5_json = {}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json, object_item_3_json, object_item_4_json, object_item_5_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(object_item_1_json['objectAssertions']['annotations'][0]['time'], '1970-01-01T00:00:00Z')
        self.assertTrue('time' not in object_item_2_json['objectAssertions']['annotations'][0])
        self.assertEqual(len(object_item_3_json['objectAssertions']['annotations']), 0)
        self.assertTrue('annotations' not in object_item_4_json['objectAssertions'])
        self.assertTrue('objectAssertions' not in object_item_5_json)

    def test_annotation_item_action_time_bad_format(self):
        object_item_1_json = {'objectAssertions': {'annotations': [{'itemActionTime': '1970-01-01T:00:00:00Z'}]}}
        object_item_2_json = {'objectAssertions': {'annotations': [{}]}}
        object_item_3_json = {'objectAssertions': {'annotations': []}}
        object_item_4_json = {'objectAssertions': {}}
        object_item_5_json = {}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json, object_item_3_json, object_item_4_json, object_item_5_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(object_item_1_json['objectAssertions']['annotations'][0]['itemActionTime'], '1970-01-01T00:00:00Z')
        self.assertTrue('itemActionTime' not in object_item_2_json['objectAssertions']['annotations'][0])
        self.assertEqual(len(object_item_3_json['objectAssertions']['annotations']), 0)
        self.assertTrue('annotations' not in object_item_4_json['objectAssertions'])
        self.assertTrue('objectAssertions' not in object_item_5_json)

    def test_ssd_security_tag(self):
        object_item_1_json = {'objectAssertions': {'systemSupplementalDescriptions': [{'securityTag': 'UNCLASSIFIED'}]}}
        object_item_2_json = {'objectAssertions': {'systemSupplementalDescriptions': [{}]}}
        object_item_3_json = {'objectAssertions': {'systemSupplementalDescriptions': []}}
        object_item_4_json = {'objectAssertions': {}}
        object_item_5_json = {}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json, object_item_3_json, object_item_4_json, object_item_5_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(object_item_1_json['objectAssertions']['systemSupplementalDescriptions'][0]['securityTag'], 'UNCLASSIFIED')
        self.assertEqual(object_item_2_json['objectAssertions']['systemSupplementalDescriptions'][0]['securityTag'], 'U')
        self.assertEqual(len(object_item_3_json['objectAssertions']['systemSupplementalDescriptions']), 0)
        self.assertTrue('systemSupplementalDescriptions' not in object_item_4_json['objectAssertions'])
        self.assertTrue('objectAssertions' not in object_item_5_json)

    def test_ssd_information_type(self):
        object_item_1_json = {'objectAssertions': {'systemSupplementalDescriptions': [{}]}}
        object_item_2_json = {'objectAssertions': {'systemSupplementalDescriptions': [{'informationType': 'other'}]}}
        ties_json = {'version': '0.1.8', 'objectItem': [object_item_1_json, object_item_2_json]}
        _0_dot_2_to_0_dot_3(ties_json, 'U')
        self.assertEqual(object_item_1_json['objectAssertions']['systemSupplementalDescriptions'][0]['informationType'], 'triageSupplemental')
        self.assertEqual(object_item_2_json['objectAssertions']['systemSupplementalDescriptions'][0]['informationType'], 'other')

    def test_id_int_to_string_missing(self):
        ties_json = {'version': '0.3'}
        _0_dot_3_to_0_dot_4(ties_json)
        self.assertTrue('id' not in ties_json)

    def test_id_int_to_string_not_missing(self):
        ties_json = {'version': '0.3', 'id': 1}
        _0_dot_3_to_0_dot_4(ties_json)
        self.assertEqual(ties_json['id'], '1')

    def test_remove_ssd_description(self):
        object_item_json = {'objectAssertions': {'systemSupplementalDescriptions': [{'description': ''}]}}
        ties_json = {'version': '0.3', 'objectItems': [object_item_json]}
        _0_dot_3_to_0_dot_4(ties_json)
        self.assertTrue('description' not in object_item_json['objectAssertions']['systemSupplementalDescriptions'][0])

    def test_add_annotationType_Tag(self):
        object_item_json = {'objectAssertions': {'annotations': [{'key': 'Tag'}]}}
        ties_json = {'version': '0.4', 'objectItems': [object_item_json]}
        _0_dot_4_to_0_dot_5(ties_json)
        self.assertTrue('key' not in object_item_json['objectAssertions']['annotations'][0])
        self.assertEqual(object_item_json['objectAssertions']['annotations'][0]['annotationType'], 'Tag')

    def test_add_annotationType_UserDescribed(self):
        object_item_json = {'objectAssertions': {'annotations': [{'key': 'UserDescribed'}]}}
        ties_json = {'version': '0.4', 'objectItems': [object_item_json]}
        _0_dot_4_to_0_dot_5(ties_json)
        self.assertTrue('key' not in object_item_json['objectAssertions']['annotations'][0])
        self.assertEqual(object_item_json['objectAssertions']['annotations'][0]['annotationType'], 'UserDescribed')

    def test_add_annotationType_Unknown(self):
        object_item_json = {'objectAssertions': {'annotations': [{'key': 'other'}]}}
        ties_json = {'version': '0.4', 'objectItems': [object_item_json]}
        _0_dot_4_to_0_dot_5(ties_json)
        self.assertEqual(object_item_json['objectAssertions']['annotations'][0]['key'], 'other')
        self.assertEqual(object_item_json['objectAssertions']['annotations'][0]['annotationType'], 'Unknown')

    def test_collectionIdDescription_to_collectionIdLabel(self):
        object_item_json = {'authorityInformation': {'collectionIdDescription': 'a'}}
        ties_json = {'version': '0.5', 'objectItems': [object_item_json]}
        _0_dot_5_to_0_dot_6(ties_json)
        self.assertTrue('collectionIdDescription' not in object_item_json)
        self.assertEqual(object_item_json['authorityInformation']['collectionIdLabel'], 'a')

    def test_subCollectionIdDescription_to_subCollectionIdLabel(self):
        object_item_json = {'authorityInformation': {'subCollectionIdDescription': 'a'}}
        ties_json = {'version': '0.5', 'objectItems': [object_item_json]}
        _0_dot_5_to_0_dot_6(ties_json)
        self.assertTrue('subCollectionIdDescription' not in object_item_json)
        self.assertEqual(object_item_json['authorityInformation']['subCollectionIdLabel'], 'a')

    def test_replace_missing_systemId_with_sha256Hash(self):
        object_item_json = {'sha256Hash': 'a' * 64}
        ties_json = {'version': '0.6', 'objectItems': [object_item_json]}
        _0_dot_6_to_0_dot_7(ties_json)
        self.assertEqual(object_item_json['sha256Hash'], 'a' * 64)
        self.assertEqual(object_item_json['systemId'], 'a' * 64)

    def test_replace_null_systemId_with_sha256Hash(self):
        object_item_json = {
            'systemId': None,
            'sha256Hash': 'a' * 64
        }
        ties_json = {'version': '0.6', 'objectItems': [object_item_json]}
        _0_dot_6_to_0_dot_7(ties_json)
        self.assertEqual(object_item_json['sha256Hash'], 'a' * 64)
        self.assertEqual(object_item_json['systemId'], 'a' * 64)

    def test_replace_blank_systemId_with_sha256Hash(self):
        object_item_json = {
            'systemId': '',
            'sha256Hash': 'a' * 64
        }
        ties_json = {'version': '0.6', 'objectItems': [object_item_json]}
        _0_dot_6_to_0_dot_7(ties_json)
        self.assertEqual(object_item_json['sha256Hash'], 'a' * 64)
        self.assertEqual(object_item_json['systemId'], 'a' * 64)

    def test_bad_systemId_bad_sha256Hash(self):
        object_item_json_1 = {}
        object_item_json_2 = {
            'sha256Hash': None
        }
        object_item_json_3 = {
            'sha256Hash': ''
        }
        ties_json = {'version': '0.6', 'objectItems': [object_item_json_1, object_item_json_2, object_item_json_3]}
        _0_dot_6_to_0_dot_7(ties_json)
        self.assertTrue('sha256Hash' not in object_item_json_1)
        self.assertTrue('systemId' not in object_item_json_1)
        self.assertEqual(object_item_json_2['sha256Hash'], None)
        self.assertTrue('systemId' not in object_item_json_2)
        self.assertEqual(object_item_json_3['sha256Hash'], '')
        self.assertTrue('systemId' not in object_item_json_3)

    def test_linkageSha256Hash_to_linkageSystemId_no_value(self):
        object_relationships = [{}]
        object_item_json = {'objectRelationships': object_relationships}
        ties_json = {'version': '0.6', 'objectItems': [object_item_json]}
        _0_dot_6_to_0_dot_7(ties_json)
        self.assertTrue('linkageSha256Hash' not in object_relationships[0])
        self.assertTrue('linkageSystemId' not in object_relationships[0])

    def test_linkageSha256Hash_to_linkageSystemId_no_systemId(self):
        object_relationships = [{'linkageSha256Hash': 'a' * 64}]
        object_item_json = {'objectRelationships': object_relationships}
        ties_json = {'version': '0.6', 'objectItems': [object_item_json]}
        _0_dot_6_to_0_dot_7(ties_json)
        self.assertTrue('linkageSha256Hash' not in object_relationships[0])
        self.assertEqual(object_relationships[0]['linkageSystemId'], 'a' * 64)

    def test_linkageSha256Hash_to_linkageSystemId_single_systemId(self):
        object_relationships_json = [{'linkageSha256Hash': 'b' * 64}]
        object_item_json_1 = {
            'systemId': 'a',
            'sha256Hash': 'a' * 64,
            'objectRelationships': object_relationships_json
        }
        object_item_json_2 = {
            'systemId': 'b',
            'sha256Hash': 'b' * 64,
            'objectRelationships': object_relationships_json
        }
        ties_json = {'version': '0.6', 'objectItems': [object_item_json_1, object_item_json_2]}
        _0_dot_6_to_0_dot_7(ties_json)
        self.assertEqual(len(object_relationships_json), 1)
        self.assertTrue('linkageSha256Hash' not in object_relationships_json[0])
        self.assertEqual(object_relationships_json[0]['linkageSystemId'], 'b')

    def test_linkageSha256Hash_to_linkageSystemId_multiple_systemIds(self):
        object_relationships_json = [{'linkageSha256Hash': 'a' * 64}]
        object_item_json_1 = {
            'systemId': 'a',
            'sha256Hash': 'a' * 64,
            'objectRelationships': object_relationships_json
        }
        object_item_json_2 = {
            'systemId': 'b',
            'sha256Hash': 'a' * 64,
        }
        ties_json = {'version': '0.6', 'objectItems': [object_item_json_1, object_item_json_2]}
        _0_dot_6_to_0_dot_7(ties_json)
        self.assertEqual(len(object_relationships_json), 2)
        self.assertTrue('linkageSha256Hash' not in object_relationships_json[0])
        self.assertTrue('linkageSha256Hash' not in object_relationships_json[1])
        self.assertEqual(object_relationships_json[0]['linkageSystemId'], 'a')
        self.assertEqual(object_relationships_json[1]['linkageSystemId'], 'b')

    def test_move_objectRelationships_no_relationships(self):
        object_item_json_1 = {'systemId': 'a'}
        object_item_json_2 = {'systemId': 'b'}
        ties_json = {'version': '0.7', 'objectItems': [object_item_json_1, object_item_json_2]}
        _0_dot_7_to_0_dot_8(ties_json)
        self.assertTrue('objectRelationships' not in ties_json)

    def test_move_objectRelationships_single_relationship(self):
        object_item_json_1 = {
            'systemId': 'a',
            'objectRelationships': [{'linkageSystemId': 'b'}]
        }
        object_item_json_2 = {'systemId': 'b'}
        ties_json = {'version': '0.7', 'objectItems': [object_item_json_1, object_item_json_2]}
        _0_dot_7_to_0_dot_8(ties_json)
        self.assertTrue('objectRelationships' not in object_item_json_1)
        self.assertTrue('objectRelationships' not in object_item_json_2)
        self.assertEqual(len(ties_json['objectRelationships']), 1)
        self.assertEqual(ties_json['objectRelationships'][0]['linkageDirectionality'], 'DIRECTED')
        self.assertEqual(ties_json['objectRelationships'][0]['linkageSystemIds'], ['a', 'b'])

    def test_move_objectRelationships_multiple_relationships(self):
        object_item_json_1 = {
            'systemId': 'a',
            'objectRelationships': [{'linkageSystemId': 'b'}, {'linkageSystemId': 'c'}]
        }
        object_item_json_2 = {
            'systemId': 'b',
            'objectRelationships': [{'linkageSystemId': 'a'}, {'linkageSystemId': 'c'}]
        }
        object_item_json_3 = {
            'systemId': 'c',
            'objectRelationships': [{'linkageSystemId': 'a'}, {'linkageSystemId': 'b'}]
        }
        ties_json = {'version': '0.7', 'objectItems': [object_item_json_1, object_item_json_2, object_item_json_3]}
        _0_dot_7_to_0_dot_8(ties_json)
        self.assertTrue('objectRelationships' not in object_item_json_1)
        self.assertTrue('objectRelationships' not in object_item_json_2)
        self.assertTrue('objectRelationships' not in object_item_json_3)
        self.assertEqual(len(ties_json['objectRelationships']), 6)
        self.assertEqual(ties_json['objectRelationships'][0]['linkageDirectionality'], 'DIRECTED')
        self.assertEqual(ties_json['objectRelationships'][0]['linkageSystemIds'], ['a', 'b'])
        self.assertEqual(ties_json['objectRelationships'][1]['linkageDirectionality'], 'DIRECTED')
        self.assertEqual(ties_json['objectRelationships'][1]['linkageSystemIds'], ['a', 'c'])
        self.assertEqual(ties_json['objectRelationships'][2]['linkageDirectionality'], 'DIRECTED')
        self.assertEqual(ties_json['objectRelationships'][2]['linkageSystemIds'], ['b', 'a'])
        self.assertEqual(ties_json['objectRelationships'][3]['linkageDirectionality'], 'DIRECTED')
        self.assertEqual(ties_json['objectRelationships'][3]['linkageSystemIds'], ['b', 'c'])
        self.assertEqual(ties_json['objectRelationships'][4]['linkageDirectionality'], 'DIRECTED')
        self.assertEqual(ties_json['objectRelationships'][4]['linkageSystemIds'], ['c', 'a'])
        self.assertEqual(ties_json['objectRelationships'][5]['linkageDirectionality'], 'DIRECTED')
        self.assertEqual(ties_json['objectRelationships'][5]['linkageSystemIds'], ['c', 'b'])


class TiesConvert_0_dot_9Tests(TestCase):

    def test_rename_systemId(self):
        object_item = {'systemId': 'a'}
        ties_json = {'version': '0.8', 'objectItems': [object_item]}
        _0_dot_8_to_0_dot_9(ties_json)
        self.assertEqual(object_item['objectId'], 'a')
        self.assertFalse('systemId' in object_item)

    def test_rename_otherIds(self):
        object_item = {'otherIds': []}
        ties_json = {'version': '0.8', 'objectItems': [object_item]}
        _0_dot_8_to_0_dot_9(ties_json)
        self.assertEqual(object_item['otherInformation'], [])
        self.assertFalse('otherIds' in object_item)

    def test_rename_annotation_systemUniqueId(self):
        annotation = {'systemUniqueId': 'a'}
        object_assertions = {'annotations': [annotation]}
        object_item = {'objectAssertions': object_assertions}
        ties_json = {'version': '0.8', 'objectItems': [object_item]}
        _0_dot_8_to_0_dot_9(ties_json)
        self.assertEqual(annotation['assertionReferenceId'], 'a')
        self.assertFalse('systemUniqueId' in annotation)

    def test_rename_annotation_systemName(self):
        annotation = {'systemName': 'a'}
        object_assertions = {'annotations': [annotation]}
        object_item = {'objectAssertions': object_assertions}
        ties_json = {'version': '0.8', 'objectItems': [object_item]}
        _0_dot_8_to_0_dot_9(ties_json)
        self.assertEqual(annotation['system'], 'a')
        self.assertFalse('systemName' in annotation)

    def test_rename_supplementalDescription_systemExportId(self):
        supplemental_description = {'systemExportId': 'a'}
        object_assertions = {'systemSupplementalDescriptions': [supplemental_description]}
        object_item = {'objectAssertions': object_assertions}
        ties_json = {'version': '0.8', 'objectItems': [object_item]}
        _0_dot_8_to_0_dot_9(ties_json)
        self.assertEqual(supplemental_description['assertionReferenceId'], 'a')
        self.assertFalse('systemExportId' in supplemental_description)

    def test_rename_supplementalDescription_systemExportIdCallTag(self):
        supplemental_description = {'systemExportIdCallTag': 'a'}
        object_assertions = {'systemSupplementalDescriptions': [supplemental_description]}
        object_item = {'objectAssertions': object_assertions}
        ties_json = {'version': '0.8', 'objectItems': [object_item]}
        _0_dot_8_to_0_dot_9(ties_json)
        self.assertEqual(supplemental_description['assertionReferenceIdLabel'], 'a')
        self.assertFalse('systemExportIdCallTag' in supplemental_description)

    def test_rename_supplementalDescription_systemName(self):
        supplemental_description = {'systemName': 'a'}
        object_assertions = {'systemSupplementalDescriptions': [supplemental_description]}
        object_item = {'objectAssertions': object_assertions}
        ties_json = {'version': '0.8', 'objectItems': [object_item]}
        _0_dot_8_to_0_dot_9(ties_json)
        self.assertEqual(supplemental_description['system'], 'a')
        self.assertFalse('systemName' in supplemental_description)

    def test_rename_systemSupplementalDescriptions(self):
        object_assertions = {'systemSupplementalDescriptions': []}
        object_item = {'objectAssertions': object_assertions}
        ties_json = {'version': '0.8', 'objectItems': [object_item]}
        _0_dot_8_to_0_dot_9(ties_json)
        self.assertEqual(object_assertions['supplementalDescriptions'], [])
        self.assertFalse('systemSupplementalDescriptions' in object_assertions)

    def test_rename_objectRelationship_linkageSystemIds(self):
        object_relationship_1 = {'linkageSystemIds': ['a', 'a']}
        object_relationship_2 = {}
        ties_json = {'version': '0.8', 'objectRelationships': [object_relationship_1, object_relationship_2]}
        _0_dot_8_to_0_dot_9(ties_json)
        self.assertEqual(object_relationship_1['linkageMemberIds'], ['a', 'a'])
        self.assertFalse('linkageSystemIds' in object_relationship_1)
        self.assertFalse('linkageMemberIds' in object_relationship_2)
        self.assertFalse('linkageSystemIds' in object_relationship_2)


if __name__ == '__main__':
    unittest.main()
