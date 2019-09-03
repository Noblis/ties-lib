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

from copy import deepcopy


def convert(ties, security_tag=None):
    _0_dot_2_to_0_dot_3(ties, security_tag)
    _0_dot_3_to_0_dot_4(ties)
    _0_dot_4_to_0_dot_5(ties)
    _0_dot_5_to_0_dot_6(ties)
    _0_dot_6_to_0_dot_7(ties)
    _0_dot_7_to_0_dot_8(ties)
    _0_dot_8_to_0_dot_9(ties)


def _0_dot_2_to_0_dot_3(ties, security_tag):
    warnings = []

    if ties['version'] not in ['0.1.8', '0.2']:
        return warnings

    if security_tag is None or security_tag == '':
        raise ValueError("security_tag is required to convert from version {} to version 0.3".format(ties['version']))

    # Change top level schema version
    ties['version'] = '0.3'

    # Added securityTag property to top-level object
    if 'securityTag' not in ties:
        ties['securityTag'] = security_tag

    # Change format of all date-time properties to be ISO 8601 dates
    if 'time' in ties:
        ties['time'] = ties['time'].replace('T:', 'T')

    # Change objectItem property to objectItems
    ties['objectItems'] = ties.pop('objectItem', {})

    for object_item in ties['objectItems']:
        # change relativeURI property to relativeUri in objectItems
        if 'relativeURI' in object_item:
            object_item['relativeUri'] = object_item.pop('relativeURI')

        # change systemIdentifier property to systemId in objectItems
        if 'systemIdentifier' in object_item:
            object_item['systemId'] = object_item.pop('systemIdentifier')

        # adding authorityInformation if it doesn't exist
        object_item['authorityInformation'] = object_item.get('authorityInformation', {})
        authority_information = object_item['authorityInformation']
        # adding securityTag if it doesn't exist
        authority_information['securityTag'] = authority_information.get('securityTag', security_tag)
        # fix format of registrationDate
        if 'registrationDate' in object_item['authorityInformation']:
            authority_information['registrationDate'] = authority_information['registrationDate'].replace('T:', 'T')
        # fix format of expirationDate
        if 'expirationDate' in object_item['authorityInformation']:
            authority_information['expirationDate'] = authority_information['expirationDate'].replace('T:', 'T')

        for object_relationship in object_item.get('objectRelationships', []):
            # change linkageAssertId property to linkageAssertionId
            if 'linkageAssertId' in object_relationship:
                object_relationship['linkageAssertionId'] = object_relationship.pop('linkageAssertId')

        for annotation in object_item.get('objectAssertions', {}).get('annotations', []):
            # adding securityTag if it doesn't exist
            annotation['securityTag'] = annotation.get('securityTag', security_tag)
            # change value property to have a minimum length of 1
            if len(annotation.get('value', '')) == 0:
                annotation['value'] = ' '
            # fix format of time
            if 'time' in annotation:
                annotation['time'] = annotation['time'].replace('T:', 'T')
            # fix format of itemActionTime
            if 'itemActionTime' in annotation:
                annotation['itemActionTime'] = annotation['itemActionTime'].replace('T:', 'T')

        for ssd in object_item.get('objectAssertions', {}).get('systemSupplementalDescriptions', []):
            # adding securityTag if it doesn't exist
            ssd['securityTag'] = ssd.get('securityTag', security_tag)
            # adding informationType if it doesn't exist
            ssd['informationType'] = ssd.get('informationType', 'triageSupplemental')
            # change dataHash property to sha256DataHash
            if 'dataHash' in ssd:
                ssd['sha256DataHash'] = ssd.pop('dataHash')

    return warnings


def _0_dot_3_to_0_dot_4(ties):
    warnings = []

    if ties['version'] != '0.3':
        return warnings

    # Change top level schema version
    ties['version'] = '0.4'

    # change top level id from integer to string
    if 'id' in ties:
        ties['id'] = str(ties['id'])

    for object_item in ties.get('objectItems', []):
        for ssd in object_item.get('objectAssertions', {}).get('systemSupplementalDescriptions', []):
            # remove description field if it exists
            if 'description' in ssd:
                del ssd['description']

    return warnings


def _0_dot_4_to_0_dot_5(ties):
    warnings = []

    if ties['version'] != '0.4':
        return warnings

    # Change top level schema version
    ties['version'] = '0.5'

    for object_item in ties.get('objectItems', []):
        for annotation in object_item.get('objectAssertions', {}).get('annotations', []):
            if annotation.get('key') in ['Tag', 'UserDescribed']:
                # Move contents of key field to new annotationType field
                annotation['annotationType'] = annotation['key']
                del annotation['key']
            else:
                # Populate annotationType field with Unknown
                annotation['annotationType'] = 'Unknown'

    return warnings


def _0_dot_5_to_0_dot_6(ties):
    warnings = []

    if ties['version'] != '0.5':
        return warnings

    # Change top level schema version
    ties['version'] = '0.6'

    for object_item in ties.get('objectItems', []):
        authority_information = object_item.get('authorityInformation', {})
        if 'collectionIdDescription' in authority_information:
            # Move contents of collectionIdDescription field to collectionIdLabel field
            authority_information['collectionIdLabel'] = authority_information['collectionIdDescription']
            del authority_information['collectionIdDescription']
        if 'subCollectionIdDescription' in authority_information:
            # Move contents of subCollectionIdDescription field to subCollectionIdLabel field
            authority_information['subCollectionIdLabel'] = authority_information['subCollectionIdDescription']
            del authority_information['subCollectionIdDescription']

    return warnings


def _0_dot_6_to_0_dot_7(ties):
    warnings = []

    if ties['version'] != '0.6':
        return warnings

    # Change top level schema version
    ties['version'] = '0.7'

    for object_item in ties.get('objectItems', []):
        system_id = object_item.get('systemId')
        if not system_id:
            sha256_hash = object_item.get('sha256Hash')
            if sha256_hash:
                object_item['systemId'] = sha256_hash

    for object_item in ties.get('objectItems', []):
        for object_relationship in object_item.get('objectRelationships', [])[:]:
            linkage_sha256_hash = object_relationship.get('linkageSha256Hash')
            if linkage_sha256_hash:
                # get a list of all systemIds that are associated with the linkageSha256Hash
                linked_system_ids = [oi.get('systemId') for oi in ties.get('objectItems', []) if oi.get('sha256Hash') == linkage_sha256_hash]
                linked_system_ids = [system_id for system_id in linked_system_ids if system_id is not None]
                linked_system_ids = sorted(list(set(linked_system_ids)))
                if len(linked_system_ids) == 0:
                    # set linkageSystemId to linkageSha256Hash value if there isn't a corresponding systemId
                    object_relationship['linkageSystemId'] = object_relationship['linkageSha256Hash']
                if len(linked_system_ids) == 1:
                    # set linkageSystemId to the corresponding systemId if there is only one corresponding systemId
                    object_relationship['linkageSystemId'] = linked_system_ids[0]
                if len(linked_system_ids) > 1:
                    # set linkageSystemId to the first corresponding systemId
                    object_relationship['linkageSystemId'] = linked_system_ids[0]
                    for system_id in linked_system_ids[1:]:
                        # copy the original relationship and create a new relationship for each additional systemId
                        new_object_relationship = deepcopy(object_relationship)
                        new_object_relationship['linkageSystemId'] = system_id
                        del new_object_relationship['linkageSha256Hash']
                        object_item['objectRelationships'].append(new_object_relationship)
            try:
                # remove linkageSha256Hash field
                del object_relationship['linkageSha256Hash']
            except KeyError:
                pass

    return warnings


def _0_dot_7_to_0_dot_8(ties):
    warnings = []

    if ties['version'] != '0.7':
        return warnings

    ties['version'] = '0.8'

    for object_item in ties.get('objectItems', []):
        for object_relationship in object_item.get('objectRelationships', []):
            ties['objectRelationships'] = ties.get('objectRelationships', [])
            ties['objectRelationships'].append(object_relationship)
            object_relationship['linkageDirectionality'] = 'DIRECTED'
            object_relationship['linkageSystemIds'] = [object_item['systemId'], object_relationship['linkageSystemId']]
            del object_relationship['linkageSystemId']
        if 'objectRelationships' in object_item:
            del object_item['objectRelationships']

    return warnings


def _0_dot_8_to_0_dot_9(ties):
    warnings = []

    if ties['version'] != '0.8':
        return warnings

    ties['version'] = '0.9'

    for object_item in ties.get('objectItems', []):
        if 'systemId' in object_item:
            # rename systemId to objectId
            object_item['objectId'] = object_item['systemId']
            del object_item['systemId']

        if 'otherIds' in object_item:
            # rename otherIds to otherInformation
            object_item['otherInformation'] = object_item['otherIds']
            del object_item['otherIds']

        object_assertions = object_item.get('objectAssertions', {})
        annotations = object_assertions.get('annotations', [])
        for annotation in annotations:
            if 'systemUniqueId' in annotation:
                # rename systemUniqueId to assertionReferenceId
                annotation['assertionReferenceId'] = annotation['systemUniqueId']
                del annotation['systemUniqueId']
            if 'systemName' in annotation:
                # rename systemName to system
                annotation['system'] = annotation['systemName']
                del annotation['systemName']
        supplemental_descriptions = object_assertions.get('systemSupplementalDescriptions', [])
        for supplemental_description in supplemental_descriptions:
            if 'systemExportId' in supplemental_description:
                # rename systemExportId to assertionReferenceId
                supplemental_description['assertionReferenceId'] = supplemental_description['systemExportId']
                del supplemental_description['systemExportId']
            if 'systemExportIdCallTag' in supplemental_description:
                # rename systemExportIdCallTag to assertionReferenceIdLabel
                supplemental_description['assertionReferenceIdLabel'] = supplemental_description['systemExportIdCallTag']
                del supplemental_description['systemExportIdCallTag']
            if 'systemName' in supplemental_description:
                # rename systemName to system
                supplemental_description['system'] = supplemental_description['systemName']
                del supplemental_description['systemName']

        if 'systemSupplementalDescriptions' in object_assertions:
            # rename systemSupplementalDescriptions to supplementalDescriptions
            object_assertions['supplementalDescriptions'] = object_assertions['systemSupplementalDescriptions']
            del object_assertions['systemSupplementalDescriptions']

    for object_relationship in ties.get('objectRelationships', []):
        if 'linkageSystemIds' in object_relationship:
            # rename linkageSystemIds to linkageMemberIds
            object_relationship['linkageMemberIds'] = object_relationship['linkageSystemIds']
            del object_relationship['linkageSystemIds']

    return warnings


if __name__ == '__main__':
    pass
