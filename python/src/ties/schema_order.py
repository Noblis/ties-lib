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


def _reorder_annotation(json_object):
    key_order = [
        'assertionId',
        'assertionReferenceId',
        'assertionReferenceIdLabel',
        'system',
        'creator',
        'time',
        'annotationType',
        'key',
        'value',
        'itemAction',
        'itemActionTime',
        'securityTag',
    ]
    return _reorder_json_keys(json_object, key_order)


def _reorder_assertions(json_object):
    key_order = [
        'annotations',
        'supplementalDescriptions',
    ]
    ordered_json_object = _reorder_json_keys(json_object, key_order)
    _reorder_json_list(ordered_json_object, 'annotations', _reorder_annotation)
    _reorder_json_list(ordered_json_object, 'supplementalDescriptions', _reorder_supplemental_description)
    return ordered_json_object


def _reorder_authority_information(json_object):
    key_order = [
        'collectionId',
        'collectionIdLabel',
        'collectionIdAlias',
        'collectionDescription',
        'subCollectionId',
        'subCollectionIdLabel',
        'subCollectionIdAlias',
        'subCollectionDescription',
        'registrationDate',
        'expirationDate',
        'owner',
        'securityTag',
    ]
    return _reorder_json_keys(json_object, key_order)


def _reorder_object_group(json_object):
    key_order = [
        'groupId',
        'groupType',
        'groupDescription',
        'groupMemberIds',
        'groupAssertions',
        'otherInformation',
    ]
    ordered_json_object = _reorder_json_keys(json_object, key_order)
    if 'groupAssertions' in ordered_json_object:
        ordered_json_object['groupAssertions'] = _reorder_assertions(ordered_json_object['groupAssertions'])
    _reorder_json_list(ordered_json_object, 'otherInformation', _reorder_other_information)
    return ordered_json_object


def _reorder_object_item(json_object):
    key_order = [
        'objectId',
        'sha256Hash',
        'md5Hash',
        'size',
        'mimeType',
        'relativeUri',
        'originalPath',
        'authorityInformation',
        'objectAssertions',
        'otherInformation',
    ]
    ordered_json_object = _reorder_json_keys(json_object, key_order)
    if 'authorityInformation' in ordered_json_object:
        ordered_json_object['authorityInformation'] = _reorder_authority_information(ordered_json_object['authorityInformation'])
    if 'objectAssertions' in ordered_json_object:
        ordered_json_object['objectAssertions'] = _reorder_assertions(ordered_json_object['objectAssertions'])
    _reorder_json_list(ordered_json_object, 'otherInformation', _reorder_other_information)
    return ordered_json_object


def _reorder_object_relationship(json_object):
    key_order = [
        'linkageMemberIds',
        'linkageDirectionality',
        'linkageType',
        'linkageAssertionId',
        'otherInformation',
    ]
    ordered_json_object = _reorder_json_keys(json_object, key_order)
    _reorder_json_list(ordered_json_object, 'otherInformation', _reorder_other_information)
    return ordered_json_object


def _reorder_other_information(json_object):
    key_order = [
        'key',
        'value',
    ]
    return _reorder_json_keys(json_object, key_order)


def _reorder_supplemental_description(json_object):
    key_order = [
        'assertionId',
        'assertionReferenceId',
        'assertionReferenceIdLabel',
        'system',
        'informationType',
        'sha256DataHash',
        'dataSize',
        'dataRelativeUri',
        'dataObject',
        'securityTag',
    ]
    return _reorder_json_keys(json_object, key_order)


def reorder_ties_json(json_object):
    key_order = [
        'version',
        'id',
        'system',
        'organization',
        'time',
        'description',
        'type',
        'securityTag',
        'objectItems',
        'objectGroups',
        'objectRelationships',
        'otherInformation',
    ]
    ordered_json_object = _reorder_json_keys(json_object, key_order)
    _reorder_json_list(ordered_json_object, 'objectItems', _reorder_object_item)
    _reorder_json_list(ordered_json_object, 'objectGroups', _reorder_object_group)
    _reorder_json_list(ordered_json_object, 'objectRelationships', _reorder_object_relationship)
    _reorder_json_list(ordered_json_object, 'otherInformation', _reorder_other_information)
    return ordered_json_object


def _reorder_json_keys(json_object, key_order):
    ordered_json_object = OrderedDict()
    for key in key_order:
        if key in json_object:
            ordered_json_object[key] = json_object[key]
    for key in sorted(set(json_object.keys()) - set(key_order)):
        ordered_json_object[key] = json_object[key]
    return ordered_json_object


def _reorder_json_list(json_object, key, reorder_fn):
    if key in json_object:
        json_object[key] = [reorder_fn(list_object) for list_object in json_object[key]]


if __name__ == '__main__':
    pass
