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

from ties.exceptions import ValidationWarning


class TiesSemanticValidator(object):

    def all_warnings(self, ties):
        warnings = []
        warnings += _check_duplicate_object_item_sha256_hashes(ties)
        warnings += _check_duplicate_object_item_other_information_keys(ties)
        warnings += _check_duplicate_object_group_other_information_keys(ties)
        warnings += _check_duplicate_object_ids_and_group_ids(ties)
        warnings += _check_duplicate_assertion_ids(ties)
        warnings += _check_object_relationship_linkage_member_ids(ties)
        warnings += _check_object_relationship_linkage_assertion_ids(ties)
        warnings += _check_duplicate_object_relationship_other_information_keys(ties)
        warnings += _check_duplicate_top_level_other_information_keys(ties)
        return warnings


def _all_object_ids(ties):
    object_ids = [object_item.get('objectId') for object_item in ties.get('objectItems', [])]
    return set([object_id for object_id in object_ids if object_id is not None])


def _all_group_ids(ties):
    group_ids = [object_group.get('groupId') for object_group in ties.get('objectGroups', [])]
    return set([group_id for group_id in group_ids if group_id is not None])


def _all_assertion_ids(ties):
    assertion_ids = []
    object_items = ties.get('objectItems', [])
    for object_item in object_items:
        assertions = object_item.get('objectAssertions', {})
        for annotation in assertions.get('annotations', []):
            assertion_ids.append(annotation.get('assertionId'))
        for supplemental_description in assertions.get('supplementalDescriptions', []):
            assertion_ids.append(supplemental_description.get('assertionId'))
    object_groups = ties.get('objectGroups', [])
    for object_group in object_groups:
        assertions = object_group.get('groupAssertions', {})
        for annotation in assertions.get('annotations', []):
            assertion_ids.append(annotation.get('assertionId'))
        for supplemental_description in assertions.get('supplementalDescriptions', []):
            assertion_ids.append(supplemental_description.get('assertionId'))
    return set([assertion_id for assertion_id in assertion_ids if assertion_id is not None])


def _check_duplicate_object_item_sha256_hashes(ties):
    object_item_index = OrderedDict()
    object_items = ties.get('objectItems', [])
    for object_item, i in zip(object_items, range(len(object_items))):
        sha256_hash = object_item.get('sha256Hash')
        object_item_index[sha256_hash] = object_item_index.get(sha256_hash, []) + [i]

    warnings = []
    for sha256_hash in object_item_index:
        duplicate_indexes = object_item_index[sha256_hash]
        if len(duplicate_indexes) > 1:
            message = "objectItems at indexes {} have duplicate sha256Hash value ('{}')".format(duplicate_indexes, sha256_hash)
            location = '/objectItems'
            warnings.append(ValidationWarning(message, location))
    return warnings


def _check_duplicate_object_item_other_information_keys(ties):
    warnings = []
    object_items = ties.get('objectItems', [])
    for object_item, object_item_index in zip(object_items, range(len(object_items))):
        other_information = object_item.get('otherInformation', [])
        location = "/objectItems[{}]/otherInformation".format(object_item_index)
        warnings.extend(_check_duplicate_other_information_keys(other_information, location))
    return warnings


def _check_duplicate_object_group_other_information_keys(ties):
    warnings = []
    object_groups = ties.get('objectGroups', [])
    for object_group, object_group_index in zip(object_groups, range(len(object_groups))):
        other_information = object_group.get('otherInformation', [])
        location = "/objectGroups[{}]/otherInformation".format(object_group_index)
        warnings.extend(_check_duplicate_other_information_keys(other_information, location))
    return warnings


def _check_duplicate_object_ids_and_group_ids(ties):
    object_id_index = OrderedDict()
    object_items = ties.get('objectItems', [])
    for object_item, object_item_index in zip(object_items, range(len(object_items))):
        object_id = object_item.get('objectId')
        if object_id is not None:
            object_id_index[object_id] = object_id_index.get(object_id, []) + [object_item_index]
    group_id_index = OrderedDict()
    object_groups = ties.get('objectGroups', [])
    for object_group, object_group_index in zip(object_groups, range(len(object_groups))):
        group_id = object_group.get('groupId')
        if group_id is not None:
            group_id_index[group_id] = group_id_index.get(group_id, []) + [object_group_index]

    warnings = []
    for object_id in object_id_index:
        duplicate_indexes = object_id_index[object_id]
        if len(duplicate_indexes) > 1:
            message = "objectItems at indexes {} have duplicate objectId value ('{}')".format(duplicate_indexes, object_id)
            location = '/objectItems'
            warnings.append(ValidationWarning(message, location))
    for group_id in group_id_index:
        duplicate_indexes = group_id_index[group_id]
        if len(duplicate_indexes) > 1:
            message = "objectGroups at indexes {} have duplicate groupId value ('{}')".format(duplicate_indexes, group_id)
            location = '/objectGroups'
            warnings.append(ValidationWarning(message, location))
    for object_id in object_id_index:
        if object_id in group_id_index:
            object_item_indexes = object_id_index[object_id]
            object_group_indexes = group_id_index[object_id]
            if len(object_item_indexes) == 1:
                message = "objectItem at index {} ".format(object_item_indexes[0])
            else:
                message = "objectItems at indexes {} ".format(object_item_indexes)
            if len(object_group_indexes) == 1:
                message += "and objectGroup at index {} ".format(object_group_indexes[0])
            else:
                message += "and objectGroups at indexes {} ".format(object_group_indexes)
            message += "have duplicate objectId/groupId value ('{}')".format(object_id)
            location = '/'
            warnings.append(ValidationWarning(message, location))
    return warnings


def _check_duplicate_assertion_ids(ties):
    assertion_id_location_index = OrderedDict()
    object_items = ties.get('objectItems', [])
    for object_item, object_item_index in zip(object_items, range(len(object_items))):
        assertions = object_item.get('objectAssertions', {})
        annotations = assertions.get('annotations', [])
        for annotation, annotation_index in zip(annotations, range(len(annotations))):
            assertion_id = annotation.get('assertionId')
            if assertion_id is not None and assertion_id:
                location = "/objectItems[{}]/objectAssertions/annotations[{}]/assertionId".format(object_item_index, annotation_index)
                assertion_id_location_index[assertion_id] = assertion_id_location_index.get(assertion_id, []) + [location]
        supplemental_descriptions = assertions.get('supplementalDescriptions', [])
        for supplemental_description, supplemental_description_index in zip(supplemental_descriptions, range(len(supplemental_descriptions))):
            assertion_id = supplemental_description.get('assertionId')
            if assertion_id is not None and assertion_id:
                location = "/objectItems[{}]/objectAssertions/supplementalDescriptions[{}]/assertionId".format(object_item_index, supplemental_description_index)
                assertion_id_location_index[assertion_id] = assertion_id_location_index.get(assertion_id, []) + [location]
    object_groups = ties.get('objectGroups', [])
    for object_group, object_group_index in zip(object_groups, range(len(object_groups))):
        assertions = object_group.get('groupAssertions', {})
        annotations = assertions.get('annotations', [])
        for annotation, annotation_index in zip(annotations, range(len(annotations))):
            assertion_id = annotation.get('assertionId')
            if assertion_id is not None and assertion_id:
                location = "/objectGroups[{}]/groupAssertions/annotations[{}]/assertionId".format(object_group_index, annotation_index)
                assertion_id_location_index[assertion_id] = assertion_id_location_index.get(assertion_id, []) + [location]
        supplemental_descriptions = assertions.get('supplementalDescriptions', [])
        for supplemental_description, supplemental_description_index in zip(supplemental_descriptions, range(len(supplemental_descriptions))):
            assertion_id = supplemental_description.get('assertionId')
            if assertion_id is not None and assertion_id:
                location = "/objectGroups[{}]/groupAssertions/supplementalDescriptions[{}]/assertionId".format(object_group_index, supplemental_description_index)
                assertion_id_location_index[assertion_id] = assertion_id_location_index.get(assertion_id, []) + [location]

    warnings = []
    for assertion_id in assertion_id_location_index:
        locations = assertion_id_location_index[assertion_id]
        if len(locations) > 1:
            for location in locations:
                message = "assertion has duplicate assertionId value ('{}')".format(assertion_id)
                warnings.append(ValidationWarning(message, location))
    return warnings


def _check_object_relationship_linkage_member_ids(ties):
    object_ids = _all_object_ids(ties)
    group_ids = _all_group_ids(ties)
    warnings = []
    object_relationships = ties.get('objectRelationships', [])
    for object_relationship, object_relationship_index in zip(object_relationships, range(len(object_relationships))):
        linkage_member_ids = object_relationship.get('linkageMemberIds', [])
        for linkage_member_id, linkage_member_id_index in zip(linkage_member_ids, range(len(linkage_member_ids))):
            if linkage_member_id is not None and linkage_member_id not in object_ids and linkage_member_id not in group_ids:
                message = "objectRelationship has a linkageMemberId ('{}') that does not reference an objectItem or objectGroup in this export".format(linkage_member_id)
                location = "/objectRelationships[{}]/linkageMemberIds[{}]".format(object_relationship_index, linkage_member_id_index)
                warnings.append(ValidationWarning(message, location))
    return warnings


def _check_object_relationship_linkage_assertion_ids(ties):
    assertion_ids = _all_assertion_ids(ties)
    warnings = []
    object_relationships = ties.get('objectRelationships', [])
    for object_relationship, object_relationship_index in zip(object_relationships, range(len(object_relationships))):
        linkage_assertion_id = object_relationship.get('linkageAssertionId')
        if linkage_assertion_id is not None and linkage_assertion_id not in assertion_ids:
            message = "objectRelationship has a linkageAssertionId ('{}') that does not reference an assertion in this export".format(linkage_assertion_id)
            location = "/objectRelationships[{}]/linkageAssertionId".format(object_relationship_index)
            warnings.append(ValidationWarning(message, location))
    return warnings


def _check_duplicate_object_relationship_other_information_keys(ties):
    warnings = []
    object_relationships = ties.get('objectRelationships', [])
    for object_relationship, object_relationship_index in zip(object_relationships, range(len(object_relationships))):
        other_information = object_relationship.get('otherInformation', [])
        location = "/objectRelationships[{}]/otherInformation".format(object_relationship_index)
        warnings.extend(_check_duplicate_other_information_keys(other_information, location))
    return warnings


def _check_duplicate_top_level_other_information_keys(ties):
    other_information = ties.get('otherInformation', [])
    return _check_duplicate_other_information_keys(other_information, '/otherInformation')


def _check_duplicate_other_information_keys(other_information, location):
    key_index = OrderedDict()
    for key_value, i in zip(other_information, range(len(other_information))):
        key = key_value.get('key')
        if key is not None:
            key_index[key] = key_index.get(key, []) + [i]

    warnings = []
    for key in key_index:
        duplicate_indexes = key_index[key]
        if len(duplicate_indexes) > 1:
            message = "otherInformation array contains duplicate key ('{}') at indexes {}".format(key, duplicate_indexes)
            warnings.append(ValidationWarning(message, location))
    return warnings


if __name__ == '__main__':
    pass
