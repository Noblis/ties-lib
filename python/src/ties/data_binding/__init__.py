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

from attr import attrib, attrs

from ties.data_binding._data_binding import TiesData
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

    assertion_id = attrib(type=str, default=None, kw_only=True)
    assertion_reference_id = attrib(type=str, default=None, kw_only=True)
    assertion_reference_id_label = attrib(type=str, default=None, kw_only=True)
    system = attrib(type=str, default=None, kw_only=True)
    creator = attrib(type=str, default=None, kw_only=True)
    time = attrib(type=str, default=None, kw_only=True)
    annotation_type = attrib(type=str, default=None, kw_only=True)
    key = attrib(type=str, default=None, kw_only=True)
    value = attrib(type=str, default=None, kw_only=True)
    item_action = attrib(type=str, default=None, kw_only=True)
    item_action_time = attrib(type=str, default=None, kw_only=True)
    security_tag = attrib(type=str, default=None, kw_only=True)

    _validator = AnnotationSchemaValidator()


@attrs(slots=True)
class Assertions(TiesData):

    annotations = attrib(type=List[Annotation], default=None, converter=_annotation_json_converter, kw_only=True)
    supplemental_descriptions = attrib(type=List[Union["SupplementalDescriptionDataFile", "SupplementalDescriptionDataObject"]], default=None, converter=_supplemental_description_json_converter, kw_only=True)

    _validator = AssertionsSchemaValidator()


@attrs(slots=True)
class AuthorityInformation(TiesData):

    collection_id = attrib(type=str, default=None, kw_only=True)
    collection_id_label = attrib(type=str, default=None, kw_only=True)
    collection_id_alias = attrib(type=str, default=None, kw_only=True)
    collection_description = attrib(type=str, default=None, kw_only=True)
    sub_collection_id = attrib(type=str, default=None, kw_only=True)
    sub_collection_id_label = attrib(type=str, default=None, kw_only=True)
    sub_collection_id_alias = attrib(type=str, default=None, kw_only=True)
    sub_collection_description = attrib(type=str, default=None, kw_only=True)
    registration_date = attrib(type=str, default=None, kw_only=True)
    expiration_date = attrib(type=str, default=None, kw_only=True)
    owner = attrib(type=str, default=None, kw_only=True)
    security_tag = attrib(type=str, default=None, kw_only=True)

    _validator = AuthorityInformationSchemaValidator()


@attrs(slots=True)
class ObjectGroup(TiesData):

    group_id = attrib(type=str, default=None, kw_only=True)
    group_type = attrib(type=str, default=None, kw_only=True)
    group_description = attrib(type=str, default=None, kw_only=True)
    group_member_ids = attrib(type=List[str], default=None, kw_only=True)
    group_assertions = attrib(type=Assertions, default=None, converter=_assertions_json_converter, kw_only=True)
    other_information = attrib(type=List["OtherInformation"], default=None, converter=_other_information_json_converter, kw_only=True)

    _validator = ObjectGroupSchemaValidator()


@attrs(slots=True)
class ObjectItem(TiesData):

    object_id = attrib(type=str, default=None, kw_only=True)
    sha256_hash = attrib(type=str, default=None, kw_only=True)
    md5_hash = attrib(type=str, default=None, kw_only=True)
    size = attrib(type=int, default=None, kw_only=True)
    mime_type = attrib(type=str, default=None, kw_only=True)
    relative_uri = attrib(type=str, default=None, kw_only=True)
    original_path = attrib(type=str, default=None, kw_only=True)
    authority_information = attrib(type=AuthorityInformation, default=None, converter=_authority_information_json_converter, kw_only=True)
    object_assertions = attrib(type=Assertions, default=None, converter=_assertions_json_converter, kw_only=True)
    other_information = attrib(type=List["OtherInformation"], default=None, converter=_other_information_json_converter, kw_only=True)

    _validator = ObjectItemSchemaValidator()


@attrs(slots=True)
class ObjectRelationship(TiesData):

    linkage_member_ids = attrib(type=List[str], default=None, kw_only=True)
    linkage_directionality = attrib(type=str, default=None, kw_only=True)
    linkage_type = attrib(type=str, default=None, kw_only=True)
    linkage_assertion_id = attrib(type=str, default=None, kw_only=True)
    other_information = attrib(type=List["OtherInformation"], default=None, converter=_other_information_json_converter, kw_only=True)

    _validator = ObjectRelationshipSchemaValidator()


@attrs(slots=True)
class OtherInformation(TiesData):

    key = attrib(type=str, default=None, kw_only=True)
    value = attrib(type=Union[str, bool, int, float], default=None, kw_only=True)

    _validator = OtherInformationSchemaValidator()


@attrs(slots=True)
class SupplementalDescriptionDataFile(TiesData):

    assertion_id = attrib(type=str, default=None, kw_only=True)
    assertion_reference_id = attrib(type=str, default=None, kw_only=True)
    assertion_reference_id_label = attrib(type=str, default=None, kw_only=True)
    system = attrib(type=str, default=None, kw_only=True)
    information_type = attrib(type=str, default=None, kw_only=True)
    sha256_data_hash = attrib(type=str, default=None, kw_only=True)
    data_size = attrib(type=int, default=None, kw_only=True)
    data_relative_uri = attrib(type=str, default=None, kw_only=True)
    security_tag = attrib(type=str, default=None, kw_only=True)

    _validator = SupplementalDescriptionDataFileSchemaValidator()


@attrs(slots=True)
class SupplementalDescriptionDataObject(TiesData):

    assertion_id = attrib(type=str, default=None, kw_only=True)
    assertion_reference_id = attrib(type=str, default=None, kw_only=True)
    assertion_reference_id_label = attrib(type=str, default=None, kw_only=True)
    system = attrib(type=str, default=None, kw_only=True)
    information_type = attrib(type=str, default=None, kw_only=True)
    data_object = attrib(type=dict, default=None, kw_only=True)
    security_tag = attrib(type=str, default=None, kw_only=True)

    _validator = SupplementalDescriptionDataObjectSchemaValidator()


@attrs(slots=True)
class Ties(TiesData):

    version = attrib(type=str, default=None, kw_only=True)
    id = attrib(type=str, default=None, kw_only=True)
    system = attrib(type=str, default=None, kw_only=True)
    organization = attrib(type=str, default=None, kw_only=True)
    time = attrib(type=str, default=None, kw_only=True)
    description = attrib(type=str, default=None, kw_only=True)
    type = attrib(type=str, default=None, kw_only=True)
    security_tag = attrib(type=str, default=None, kw_only=True)
    object_items = attrib(type=List[ObjectItem], default=None, converter=_object_item_json_converter, kw_only=True)
    object_groups = attrib(type=List[ObjectGroup], default=None, converter=_object_group_json_converter, kw_only=True)
    object_relationships = attrib(type=List[ObjectRelationship], default=None, converter=_object_relationship_json_converter, kw_only=True)
    other_information = attrib(type=List[OtherInformation], default=None, converter=_other_information_json_converter, kw_only=True)

    _validator = TiesSchemaValidator()
