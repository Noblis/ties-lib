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
from collections import OrderedDict
from typing import List, Union
from unittest import TestCase

import six
from attr import attrib, attrs

from ties.data_binding import Annotation
from ties.data_binding import Assertions
from ties.data_binding import AuthorityInformation
from ties.data_binding import ObjectGroup
from ties.data_binding import ObjectItem
from ties.data_binding import ObjectRelationship
from ties.data_binding import OtherInformation
from ties.data_binding import StringType
from ties.data_binding import SupplementalDescriptionDataFile
from ties.data_binding import SupplementalDescriptionDataObject
from ties.data_binding import Ties
from ties.data_binding import TiesData


class TestDataSchemaValidator(object):

    def validate(self, instance):
        pass

    def all_errors(self, instance):  # pylint: disable=unused-argument
        return []


@attrs(slots=True)
class TestDataClass(TiesData):

    foo = attrib(type=StringType, default=None, kw_only=six.PY3)
    bar = attrib(type=Union[StringType, bool, int, float], default=None, kw_only=six.PY3)
    baz = attrib(type=List[StringType], default=None, kw_only=six.PY3)
    xyzzy = attrib(type=List[Union[StringType, bool, int, float]], default=None, kw_only=six.PY3)

    _validator = TestDataSchemaValidator()


class TiesDataTests(TestCase):

    def test_repr(self):
        self.assertEqual(repr(TestDataClass()), 'TestDataClass(foo=None, bar=None, baz=None, xyzzy=None)')
        if six.PY3:
            self.assertEqual(repr(TestDataClass(foo='a')), "TestDataClass(foo='a', bar=None, baz=None, xyzzy=None)")
        else:
            self.assertEqual(repr(TestDataClass(foo='a')), "TestDataClass(foo=u'a', bar=None, baz=None, xyzzy=None)")

    def test_str(self):
        self.assertEqual(str(TestDataClass()), str('TestDataClass(foo=None, bar=None, baz=None, xyzzy=None)'))
        if six.PY3:
            self.assertEqual(str(TestDataClass(foo='a')), str("TestDataClass(foo='a', bar=None, baz=None, xyzzy=None)"))
        else:
            self.assertEqual(str(TestDataClass(foo='a')), str("TestDataClass(foo=u'a', bar=None, baz=None, xyzzy=None)"))

    def test_eq(self):
        obj = TestDataClass()
        self.assertEqual(obj, obj)
        self.assertEqual(TestDataClass(), TestDataClass())

        other_information_1 = TestDataClass(foo='a', bar='a', baz=['a'], xyzzy=['a'])
        other_information_2 = TestDataClass(foo='a', bar='a', baz=['a'], xyzzy=['a'])
        self.assertEqual(other_information_1, other_information_2)

    def test_ne(self):
        self.assertNotEqual(TestDataClass(), None)

        other_information_1 = TestDataClass(foo='a', bar='a', baz=['a'], xyzzy=['a'])
        other_information_2 = TestDataClass(foo='b', bar='b', baz=['b'], xyzzy=['b'])
        self.assertNotEqual(other_information_1, other_information_2)

    def test_getattr(self):
        obj = TestDataClass()
        obj.bar = 'a'
        self.assertEqual(obj.bar, 'a')
        obj.bar = True
        self.assertEqual(obj.bar, True)
        obj.bar = 1
        self.assertEqual(obj.bar, 1)
        obj.bar = 1.1
        self.assertEqual(obj.bar, 1.1)

    def test_getattr_not_set(self):
        self.assertIsNone(TestDataClass().foo)

    def test_getattr_bad_name(self):
        with self.assertRaises(AttributeError, msg="'TestDataClass' object has no property 'abc'"):
            abc = TestDataClass().abc  # pylint: disable=unused-variable

    def test_setattr(self):
        obj = TestDataClass()
        obj.foo = 'a'
        self.assertEqual(obj.foo, 'a')
        if not six.PY3:
            obj.foo = six.text_type('a')
            self.assertEqual(obj.foo, 'a')
            obj.foo = str('a')
            self.assertEqual(obj.foo, 'a')

    def test_setattr_None(self):
        obj = TestDataClass(foo='a')
        obj.foo = None
        self.assertIsNone(obj.foo)

    def test_setattr_bad_name(self):
        with self.assertRaises(AttributeError, msg="'TestDataClass' object has no attribute 'abc'"):
            TestDataClass().abc = 'a'  # pylint: disable=attribute-defined-outside-init

    def test_to_json(self):
        obj = TestDataClass()
        self.assertEqual(obj.to_json(), {})
        obj.foo = 'a'
        obj.bar = 'a'
        obj.baz = ['a']
        self.assertEqual(obj.to_json(), {'foo': 'a', 'bar': 'a', 'baz': ['a']})

    def test_from_json(self):
        self.assertEqual(TestDataClass.from_json({}), TestDataClass())
        self.assertEqual(TestDataClass.from_json({'foo': 'a', 'bar': 'a', 'baz': ['a']}), TestDataClass(foo='a', bar='a', baz=['a']))

    def test_from_json_error(self):
        with self.assertRaises(TypeError, msg="__init__() got an unexpected keyword argument 'abc'"):
            TestDataClass.from_json({'abc': None})


class TiesTests(TestCase):

    def setUp(self):
        self.other_information = OtherInformation(
            key='a',
            value='a'
        )
        self.other_information_json = OrderedDict([
            ('key', 'a'),
            ('value', 'a'),
        ])
        self.authority_information = AuthorityInformation(
            collection_id='a',
            collection_id_label='a',
            collection_id_alias='a',
            collection_description='a',
            sub_collection_id='a',
            sub_collection_id_label='a',
            sub_collection_id_alias='a',
            sub_collection_description='a',
            registration_date='1970-01-01T00:00:00',
            expiration_date='1970-01-01T00:00:00',
            owner='a',
            security_tag=''
        )
        self.authority_information_json = OrderedDict([
            ('collectionId', 'a'),
            ('collectionIdLabel', 'a'),
            ('collectionIdAlias', 'a'),
            ('collectionDescription', 'a'),
            ('subCollectionId', 'a'),
            ('subCollectionIdLabel', 'a'),
            ('subCollectionIdAlias', 'a'),
            ('subCollectionDescription', 'a'),
            ('registrationDate', '1970-01-01T00:00:00'),
            ('expirationDate', '1970-01-01T00:00:00'),
            ('owner', 'a'),
            ('securityTag', ''),
        ])
        self.annotation = Annotation(
            assertion_id='a',
            assertion_reference_id='a',
            assertion_reference_id_label='a',
            system='a',
            creator='a',
            time='a',
            annotation_type='a',
            key='a',
            value='a',
            item_action='a',
            item_action_time='a',
            security_tag='',
        )
        self.annotation_json = OrderedDict([
            ('assertionId', 'a'),
            ('assertionReferenceId', 'a'),
            ('assertionReferenceIdLabel', 'a'),
            ('system', 'a'),
            ('creator', 'a'),
            ('time', 'a'),
            ('annotationType', 'a'),
            ('key', 'a'),
            ('value', 'a'),
            ('itemAction', 'a'),
            ('itemActionTime', 'a'),
            ('securityTag', ''),
        ])
        self.supplemental_description_data_file = SupplementalDescriptionDataFile(
            assertion_id='a',
            assertion_reference_id='a',
            assertion_reference_id_label='a',
            system='a',
            information_type='a',
            sha256_data_hash='a' * 64,
            data_size=0,
            data_relative_uri='a',
            security_tag='',
        )
        self.supplemental_description_data_file_json = OrderedDict([
            ('assertionId', 'a'),
            ('assertionReferenceId', 'a'),
            ('assertionReferenceIdLabel', 'a'),
            ('system', 'a'),
            ('informationType', 'a'),
            ('sha256DataHash', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'),
            ('dataSize', 0),
            ('dataRelativeUri', 'a'),
            ('securityTag', ''),
        ])
        self.supplemental_description_data_object = SupplementalDescriptionDataObject(
            assertion_id='a',
            assertion_reference_id='a',
            assertion_reference_id_label='a',
            system='a',
            information_type='a',
            data_object={},
            security_tag='',
        )
        self.supplemental_description_data_object_json = OrderedDict([
            ('assertionId', 'a'),
            ('assertionReferenceId', 'a'),
            ('assertionReferenceIdLabel', 'a'),
            ('system', 'a'),
            ('informationType', 'a'),
            ('dataObject', {}),
            ('securityTag', ''),
        ])
        self.assertions = Assertions(
            annotations=[self.annotation],
            supplemental_descriptions=[self.supplemental_description_data_file, self.supplemental_description_data_object]
        )
        self.assertions_json = OrderedDict([
            ('annotations', [self.annotation_json]),
            ('supplementalDescriptions', [
                self.supplemental_description_data_file_json,
                self.supplemental_description_data_object_json
            ]),
        ])
        self.object_item = ObjectItem(
            object_id='a',
            sha256_hash='a' * 64,
            md5_hash='a' * 32,
            size=0,
            mime_type='a',
            relative_uri='a',
            original_path='a',
            authority_information=self.authority_information,
            object_assertions=self.assertions,
            other_information=[self.other_information],
        )
        self.object_item_json = OrderedDict([
            ('objectId', 'a'),
            ('sha256Hash', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'),
            ('md5Hash', 'a' * 32),
            ('size', 0),
            ('mimeType', 'a'),
            ('relativeUri', 'a'),
            ('originalPath', 'a'),
            ('authorityInformation', self.authority_information_json),
            ('objectAssertions', self.assertions_json),
            ('otherInformation', [self.other_information_json]),
        ])
        self.object_group = ObjectGroup(
            group_id='a',
            group_type='a',
            group_description='a',
            group_member_ids=['a'],
            group_assertions=self.assertions,
            other_information=[self.other_information],
        )
        self.object_group_json = OrderedDict([
            ('groupId', 'a'),
            ('groupType', 'a'),
            ('groupDescription', 'a'),
            ('groupMemberIds', ['a']),
            ('groupAssertions', self.assertions_json),
            ('otherInformation', [self.other_information_json]),
        ])
        self.object_relationship = ObjectRelationship(
            linkage_member_ids=['a', 'a'],
            linkage_directionality='DIRECTED',
            linkage_type='a',
            linkage_assertion_id='a',
            other_information=[self.other_information],
        )
        self.object_relationship_json = OrderedDict([
            ('linkageMemberIds', ['a', 'a']),
            ('linkageDirectionality', 'DIRECTED'),
            ('linkageType', 'a'),
            ('linkageAssertionId', 'a'),
            ('otherInformation', [self.other_information_json]),
        ])
        self.ties = Ties(
            version='0.9',
            id='a',
            system='a',
            organization='a',
            time='1970-01-01T00:00:00',
            description='a',
            type='a',
            security_tag='',
            object_items=[self.object_item],
            object_groups=[self.object_group],
            object_relationships=[self.object_relationship],
            other_information=[self.other_information],
        )
        self.ties_json = OrderedDict([
            ('version', '0.9'),
            ('id', 'a'),
            ('system', 'a'),
            ('organization', 'a'),
            ('time', '1970-01-01T00:00:00'),
            ('description', 'a'),
            ('type', 'a'),
            ('securityTag', ''),
            ('objectItems', [self.object_item_json]),
            ('objectGroups', [self.object_group_json]),
            ('objectRelationships', [self.object_relationship_json]),
            ('otherInformation', [self.other_information_json]),
        ])

    def test_all_errors(self):
        self.assertEqual(self.annotation.all_errors(), [])
        self.assertEqual(self.assertions.all_errors(), [])
        self.assertEqual(self.authority_information.all_errors(), [])
        self.assertEqual(self.object_group.all_errors(), [])
        self.assertEqual(self.object_item.all_errors(), [])
        self.assertEqual(self.object_relationship.all_errors(), [])
        self.assertEqual(self.other_information.all_errors(), [])
        self.assertEqual(self.supplemental_description_data_file.all_errors(), [])
        self.assertEqual(self.supplemental_description_data_object.all_errors(), [])
        self.assertEqual(self.ties.all_errors(), [])

    def test_to_json(self):
        self.assertEqual(self.annotation.to_json(), self.annotation_json)
        self.assertEqual(self.assertions.to_json(), self.assertions_json)
        self.assertEqual(self.authority_information.to_json(), self.authority_information_json)
        self.assertEqual(self.object_group.to_json(), self.object_group_json)
        self.assertEqual(self.object_item.to_json(), self.object_item_json)
        self.assertEqual(self.object_relationship.to_json(), self.object_relationship_json)
        self.assertEqual(self.other_information.to_json(), self.other_information_json)
        self.assertEqual(self.supplemental_description_data_file.to_json(), self.supplemental_description_data_file_json)
        self.assertEqual(self.supplemental_description_data_object.to_json(), self.supplemental_description_data_object_json)
        self.assertEqual(self.ties.to_json(), self.ties_json)

    def test_from_json(self):
        self.assertEqual(Annotation.from_json(self.annotation_json), self.annotation)
        self.assertEqual(Assertions.from_json(self.assertions_json), self.assertions)
        self.assertEqual(AuthorityInformation.from_json(self.authority_information_json), self.authority_information)
        self.assertEqual(ObjectGroup.from_json(self.object_group_json), self.object_group)
        self.assertEqual(ObjectItem.from_json(self.object_item_json), self.object_item)
        self.assertEqual(ObjectRelationship.from_json(self.object_relationship_json), self.object_relationship)
        self.assertEqual(OtherInformation.from_json(self.other_information_json), self.other_information)
        self.assertEqual(SupplementalDescriptionDataFile.from_json(self.supplemental_description_data_file_json), self.supplemental_description_data_file)
        self.assertEqual(SupplementalDescriptionDataObject.from_json(self.supplemental_description_data_object_json), self.supplemental_description_data_object)
        self.assertEqual(Ties.from_json(self.ties_json), self.ties)


if __name__ == '__main__':
    unittest.main()
