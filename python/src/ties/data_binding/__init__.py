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

from typing import List, Union

import six
from attr import attrib, attrs

from ties.data_binding._data_binding import StringType, TiesData
from ties.schema_validation import AnnotationSchemaValidator
from ties.schema_validation import AssertionsSchemaValidator
from ties.schema_validation import AuthorityInformationSchemaValidator
from ties.schema_validation import ObjectGroupSchemaValidator
from ties.schema_validation import ObjectItemSchemaValidator
from ties.schema_validation import ObjectRelationshipSchemaValidator
from ties.schema_validation import OtherInformationSchemaValidator
from ties.schema_validation import SupplementalDescriptionDataFileSchemaValidator
from ties.schema_validation import SupplementalDescriptionDataObjectSchemaValidator
from ties.schema_validation import TiesSchemaValidator


def _annotation_json_converter(value):
    if isinstance(value, list):
        return [_annotation_json_converter(v) for v in value]
    if isinstance(value, dict):
        return Annotation.from_json(value)
    return value


def _assertions_json_converter(value):
    if isinstance(value, dict):
        return Assertions.from_json(value)
    return value


def _authority_information_json_converter(value):
    if isinstance(value, dict):
        return AuthorityInformation.from_json(value)
    return value


def _object_group_json_converter(value):
    if isinstance(value, list):
        return [_object_group_json_converter(v) for v in value]
    if isinstance(value, dict):
        return ObjectGroup.from_json(value)
    return value


def _object_item_json_converter(value):
    if isinstance(value, list):
        return [_object_item_json_converter(v) for v in value]
    if isinstance(value, dict):
        return ObjectItem.from_json(value)
    return value


def _object_relationship_json_converter(value):
    if isinstance(value, list):
        return [_object_relationship_json_converter(v) for v in value]
    if isinstance(value, dict):
        return ObjectRelationship.from_json(value)
    return value


def _other_information_json_converter(value):
    if isinstance(value, list):
        return [_other_information_json_converter(v) for v in value]
    if isinstance(value, dict):
        return OtherInformation.from_json(value)
    return value


def _supplemental_description_json_converter(value):
    if isinstance(value, list):
        return [_supplemental_description_json_converter(v) for v in value]
    if isinstance(value, dict):
        try:
            return SupplementalDescriptionDataFile.from_json(value)
        except TypeError:
            pass
        try:
            return SupplementalDescriptionDataObject.from_json(value)
        except TypeError:
            pass
    return value


@attrs(slots=True)
class Annotation(TiesData):

    assertion_id = attrib(type=StringType, default=None, kw_only=six.PY3)
    assertion_reference_id = attrib(type=StringType, default=None, kw_only=six.PY3)
    assertion_reference_id_label = attrib(type=StringType, default=None, kw_only=six.PY3)
    system = attrib(type=StringType, default=None, kw_only=six.PY3)
    creator = attrib(type=StringType, default=None, kw_only=six.PY3)
    time = attrib(type=StringType, default=None, kw_only=six.PY3)
    annotation_type = attrib(type=StringType, default=None, kw_only=six.PY3)
    key = attrib(type=StringType, default=None, kw_only=six.PY3)
    value = attrib(type=StringType, default=None, kw_only=six.PY3)
    item_action = attrib(type=StringType, default=None, kw_only=six.PY3)
    item_action_time = attrib(type=StringType, default=None, kw_only=six.PY3)
    security_tag = attrib(type=StringType, default=None, kw_only=six.PY3)

    _validator = AnnotationSchemaValidator()


@attrs(slots=True)
class Assertions(TiesData):

    annotations = attrib(type=List[Annotation], default=None, converter=_annotation_json_converter, kw_only=six.PY3)
    supplemental_descriptions = attrib(type=List[Union["SupplementalDescriptionDataFile", "SupplementalDescriptionDataObject"]], default=None, converter=_supplemental_description_json_converter, kw_only=six.PY3)

    _validator = AssertionsSchemaValidator()


@attrs(slots=True)
class AuthorityInformation(TiesData):

    collection_id = attrib(type=StringType, default=None, kw_only=six.PY3)
    collection_id_label = attrib(type=StringType, default=None, kw_only=six.PY3)
    collection_id_alias = attrib(type=StringType, default=None, kw_only=six.PY3)
    collection_description = attrib(type=StringType, default=None, kw_only=six.PY3)
    sub_collection_id = attrib(type=StringType, default=None, kw_only=six.PY3)
    sub_collection_id_label = attrib(type=StringType, default=None, kw_only=six.PY3)
    sub_collection_id_alias = attrib(type=StringType, default=None, kw_only=six.PY3)
    sub_collection_description = attrib(type=StringType, default=None, kw_only=six.PY3)
    registration_date = attrib(type=StringType, default=None, kw_only=six.PY3)
    expiration_date = attrib(type=StringType, default=None, kw_only=six.PY3)
    owner = attrib(type=StringType, default=None, kw_only=six.PY3)
    security_tag = attrib(type=StringType, default=None, kw_only=six.PY3)

    _validator = AuthorityInformationSchemaValidator()


@attrs(slots=True)
class ObjectGroup(TiesData):

    group_id = attrib(type=StringType, default=None, kw_only=six.PY3)
    group_type = attrib(type=StringType, default=None, kw_only=six.PY3)
    group_description = attrib(type=StringType, default=None, kw_only=six.PY3)
    group_member_ids = attrib(type=List[StringType], default=None, kw_only=six.PY3)
    group_assertions = attrib(type=Assertions, default=None, converter=_assertions_json_converter, kw_only=six.PY3)
    other_information = attrib(type=List["OtherInformation"], default=None, converter=_other_information_json_converter, kw_only=six.PY3)

    _validator = ObjectGroupSchemaValidator()


@attrs(slots=True)
class ObjectItem(TiesData):

    object_id = attrib(type=StringType, default=None, kw_only=six.PY3)
    sha256_hash = attrib(type=StringType, default=None, kw_only=six.PY3)
    md5_hash = attrib(type=StringType, default=None, kw_only=six.PY3)
    size = attrib(type=int, default=None, kw_only=six.PY3)
    mime_type = attrib(type=StringType, default=None, kw_only=six.PY3)
    relative_uri = attrib(type=StringType, default=None, kw_only=six.PY3)
    original_path = attrib(type=StringType, default=None, kw_only=six.PY3)
    authority_information = attrib(type=AuthorityInformation, default=None, converter=_authority_information_json_converter, kw_only=six.PY3)
    object_assertions = attrib(type=Assertions, default=None, converter=_assertions_json_converter, kw_only=six.PY3)
    other_information = attrib(type=List["OtherInformation"], default=None, converter=_other_information_json_converter, kw_only=six.PY3)

    _validator = ObjectItemSchemaValidator()


@attrs(slots=True)
class ObjectRelationship(TiesData):

    linkage_member_ids = attrib(type=List[StringType], default=None, kw_only=six.PY3)
    linkage_directionality = attrib(type=StringType, default=None, kw_only=six.PY3)
    linkage_type = attrib(type=StringType, default=None, kw_only=six.PY3)
    linkage_assertion_id = attrib(type=StringType, default=None, kw_only=six.PY3)
    other_information = attrib(type=List["OtherInformation"], default=None, converter=_other_information_json_converter, kw_only=six.PY3)

    _validator = ObjectRelationshipSchemaValidator()


@attrs(slots=True)
class OtherInformation(TiesData):

    key = attrib(type=StringType, default=None, kw_only=six.PY3)
    value = attrib(type=Union[StringType, bool, int, float], default=None, kw_only=six.PY3)

    _validator = OtherInformationSchemaValidator()


@attrs(slots=True)
class SupplementalDescriptionDataFile(TiesData):

    assertion_id = attrib(type=StringType, default=None, kw_only=six.PY3)
    assertion_reference_id = attrib(type=StringType, default=None, kw_only=six.PY3)
    assertion_reference_id_label = attrib(type=StringType, default=None, kw_only=six.PY3)
    system = attrib(type=StringType, default=None, kw_only=six.PY3)
    information_type = attrib(type=StringType, default=None, kw_only=six.PY3)
    sha256_data_hash = attrib(type=StringType, default=None, kw_only=six.PY3)
    data_size = attrib(type=int, default=None, kw_only=six.PY3)
    data_relative_uri = attrib(type=StringType, default=None, kw_only=six.PY3)
    security_tag = attrib(type=StringType, default=None, kw_only=six.PY3)

    _validator = SupplementalDescriptionDataFileSchemaValidator()


@attrs(slots=True)
class SupplementalDescriptionDataObject(TiesData):

    assertion_id = attrib(type=StringType, default=None, kw_only=six.PY3)
    assertion_reference_id = attrib(type=StringType, default=None, kw_only=six.PY3)
    assertion_reference_id_label = attrib(type=StringType, default=None, kw_only=six.PY3)
    system = attrib(type=StringType, default=None, kw_only=six.PY3)
    information_type = attrib(type=StringType, default=None, kw_only=six.PY3)
    data_object = attrib(type=dict, default=None, kw_only=six.PY3)
    security_tag = attrib(type=StringType, default=None, kw_only=six.PY3)

    _validator = SupplementalDescriptionDataObjectSchemaValidator()


@attrs(slots=True)
class Ties(TiesData):

    version = attrib(type=StringType, default=None, kw_only=six.PY3)
    id = attrib(type=StringType, default=None, kw_only=six.PY3)
    system = attrib(type=StringType, default=None, kw_only=six.PY3)
    organization = attrib(type=StringType, default=None, kw_only=six.PY3)
    time = attrib(type=StringType, default=None, kw_only=six.PY3)
    description = attrib(type=StringType, default=None, kw_only=six.PY3)
    type = attrib(type=StringType, default=None, kw_only=six.PY3)
    security_tag = attrib(type=StringType, default=None, kw_only=six.PY3)
    object_items = attrib(type=List[ObjectItem], default=None, converter=_object_item_json_converter, kw_only=six.PY3)
    object_groups = attrib(type=List[ObjectGroup], default=None, converter=_object_group_json_converter, kw_only=six.PY3)
    object_relationships = attrib(type=List[ObjectRelationship], default=None, converter=_object_relationship_json_converter, kw_only=six.PY3)
    other_information = attrib(type=List[OtherInformation], default=None, converter=_other_information_json_converter, kw_only=six.PY3)

    _validator = TiesSchemaValidator()
