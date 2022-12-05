/*
 * Copyright 2019 Noblis, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.noblis.ties

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class TiesConverter {

    public void convert(Map ties, String securityTag = null) {
        _0Dot2To0Dot3(ties, securityTag)
        _0Dot3To0Dot4(ties)
        _0Dot4To0Dot5(ties)
        _0Dot5To0Dot6(ties)
        _0Dot6To0Dot7(ties)
        _0Dot7To0Dot8(ties)
        _0Dot8To0Dot9(ties)
        _0Dot9To1Dot0(ties)
    }

    private static void _0Dot2To0Dot3(Map ties, String securityTag) {
        String version = ties['version']
        if (!(version in ['0.1.8', '0.2'])) {
            return
        }

        if (!securityTag) {
            throw new IllegalArgumentException("securityTag is required to convert from version ${version} to version 0.3")
        }

        // change top level schema version
        ties['version'] = '0.3'

        // add securityTag property to top level object
        if (!('securityTag' in ties.keySet())) {
            ties['securityTag'] = securityTag
        }

        // change format of all date-time properties to be ISO 8601 dates
        if ('time' in ties.keySet()) {
            ties['time'] = (ties['time'] as String).replace('T:', 'T')
        }

        // change objectItem property to objectItems
        ties['objectItems'] = ties.remove('objectItem')

        List objectItems = ties.get('objectItems') as List ?: []
        objectItems.each { Map objectItem ->
            // change relativeURI property to relativeUri in objectItems
            if ('relativeURI' in objectItem.keySet()) {
                objectItem['relativeUri'] = objectItem.remove('relativeURI')
            }

            // change systemIdentifier property to systemId in objectItems
            if ('systemIdentifier' in objectItem.keySet()) {
                objectItem['systemId'] = objectItem.remove('systemIdentifier')
            }

            // add authorityInformation if it doesn't exist
            objectItem['authorityInformation'] = objectItem.get('authorityInformation') ?: [:]
            Map authorityInformation = objectItem['authorityInformation'] as Map
            // add securityTag if it doesn't exist
            authorityInformation['securityTag'] = authorityInformation.get('securityTag') ?: securityTag
            // fix format of registrationDate
            if ('registrationDate' in authorityInformation.keySet()) {
                authorityInformation['registrationDate'] = (authorityInformation['registrationDate'] as String).replace('T:', 'T')
            }
            // fix format of expirationDate
            if ('expirationDate' in authorityInformation.keySet()) {
                authorityInformation['expirationDate'] = (authorityInformation['expirationDate'] as String).replace('T:', 'T')
            }

            List objectRelationships = objectItem.get('objectRelationships') as List ?: []
            objectRelationships.each { Map objectRelationship ->
                // change linkageAssertId property to linkageAssertionId
                if ('linkageAssertId' in objectRelationship.keySet()) {
                    objectRelationship['linkageAssertionId'] = objectRelationship.remove('linkageAssertId')
                }
            }

            List annotations = (objectItem.get('objectAssertions') as Map ?: [:]).get('annotations') as List ?: []
            annotations.each { Map annotation ->
                // adding securityTag if it doesn't exist
                annotation['securityTag'] = annotation.get('securityTag') ?: securityTag
                // change value property to have a minimum length of 1
                if ((annotation.get('value') as String ?: '').size() == 0) {
                    annotation['value'] = ' '
                }
                // fix format of time
                if ('time' in annotation.keySet()) {
                    annotation['time'] = (annotation['time'] as String).replace('T:', 'T')
                }
                // fix format of itemActionTime
                if ('itemActionTime' in annotation.keySet()) {
                    annotation['itemActionTime'] = (annotation['itemActionTime'] as String).replace('T:', 'T')
                }
            }

            List systemSupplementalDescriptions = (objectItem.get('objectAssertions') as Map ?: [:]).get('systemSupplementalDescriptions') as List ?: []
            systemSupplementalDescriptions.each { Map ssd ->
                // adding securityTag if it doesn't exist
                ssd['securityTag'] = ssd.get('securityTag') ?: securityTag
                // adding informationType if it doesn't exist
                ssd['informationType'] = ssd.get('informationType') ?: 'triageSupplemental'
                // change dataHash property to sha256DataHash
                if ('dataHash' in ssd.keySet()) {
                    ssd['sha256DataHash'] = ssd.remove('dataHash')
                }
            }
        }
    }

    private static void _0Dot3To0Dot4(Map ties) {
        if (ties['version'] != '0.3') {
            return
        }

        // change top level schema version
        ties['version'] = '0.4'

        // change top level id from integer to string
        if ('id' in ties.keySet()) {
            ties['id'] = ties['id'] as String
        }

        List objectItems = ties.get('objectItems') as List ?: []
        objectItems.each { Map objectItem ->
            List systemSupplementalDescriptions = (objectItem.get('objectAssertions') as Map ?: [:]).get('systemSupplementalDescriptions') as List ?: []
            systemSupplementalDescriptions.each { Map ssd ->
                // remove description field if it exists
                if ('description' in ssd.keySet()) {
                    ssd.remove('description')
                }
            }
        }
    }

    private static void _0Dot4To0Dot5(Map ties) {
        if (ties['version'] != '0.4') {
            return
        }

        // change top level schema version
        ties['version'] = '0.5'

        List objectItems = ties.get('objectItems') as List ?: []
        objectItems.each { Map objectItem ->
            List annotations = (objectItem.get('objectAssertions') as Map ?: [:]).get('annotations') as List ?: []
            annotations.each { Map annotation ->
                if ((annotation.get('key') as String) in ['Tag', 'UserDescribed']) {
                    // move contents of key field to new annotationType field
                    annotation['annotationType'] = annotation.remove('key')
                } else {
                    // populate annotationType field with Unknown
                    annotation['annotationType'] = 'Unknown'
                }
            }
        }
    }

    private static void _0Dot5To0Dot6(Map ties) {
        if (ties['version'] != '0.5') {
            return
        }

        // change top level schema version
        ties['version'] = '0.6'

        List objectItems = ties.get('objectItems') as List ?: []
        objectItems.each { Map objectItem ->
            Map authorityInformation = objectItem.get('authorityInformation') as Map ?: [:]
            if ('collectionIdDescription' in authorityInformation.keySet()) {
                // move contents of collectionIdDescription field to collectionIdLabel field
                authorityInformation['collectionIdLabel'] = authorityInformation.remove('collectionIdDescription')
            }
            if ('subCollectionIdDescription' in authorityInformation.keySet()) {
                // move contents of subCollectionIdDescription field to subCollectionIdLabel field
                authorityInformation['subCollectionIdLabel'] = authorityInformation.remove('subCollectionIdDescription')
            }
        }
    }

    private static void _0Dot6To0Dot7(Map ties) {
        if (ties['version'] != '0.6') {
            return
        }

        // change top level schema version
        ties['version'] = '0.7'

        List objectItems = ties.get('objectItems') as List ?: []
        objectItems.each { Map objectItem ->
            String systemId = objectItem.get('systemId')
            if (!systemId) {
                String sha256Hash = objectItem.get('sha256Hash')
                if (sha256Hash) {
                    objectItem['systemId'] = sha256Hash
                }
            }
        }

        objectItems.each { Map objectItem ->
            List objectRelationships = objectItem.get('objectRelationships') as List ?: []
            objectRelationships.toList().each { Map objectRelationship ->
                String linkageSha256Hash = objectRelationship.get('linkageSha256Hash')
                if (linkageSha256Hash) {
                    // get a list of all systemIds that are associated with the linkageSha256Hash
                    List<String> linkedSystemIds = objectItems.findAll({ Map oi -> oi.get('sha256Hash') == linkageSha256Hash }).collect { Map oi -> oi.get('systemId') as String }
                    linkedSystemIds = linkedSystemIds.findAll { String systemId -> systemId != null }
                    linkedSystemIds = linkedSystemIds.unique()
                    if (linkedSystemIds.size() == 0) {
                        // set linkageSystemId to linkageSha256Hash value if there isn't a corresponding systemId
                        objectRelationship['linkageSystemId'] = objectRelationship['linkageSha256Hash']
                    }
                    if (linkedSystemIds.size() == 1) {
                        // set linkageSystemId to the corresponding systemId if there is only one corresponding systemId
                        objectRelationship['linkageSystemId'] = linkedSystemIds[0]
                    }
                    if (linkedSystemIds.size() > 1) {
                        // set linkageSystemId to the first corresponding systemId
                        objectRelationship['linkageSystemId'] = linkedSystemIds[0]
                        linkedSystemIds.tail().each { String systemId ->
                            // copy the original relationship and create a new relationship for each additional systemId
                            Map newObjectRelationship = new JsonSlurper().parseText(JsonOutput.toJson(objectRelationship)) as Map
                            newObjectRelationship['linkageSystemId'] = systemId
                            newObjectRelationship.remove('linkageSha256Hash')
                            objectItem['objectRelationships'] << newObjectRelationship
                        }
                    }
                }
                objectRelationship.remove('linkageSha256Hash')
            }
        }
    }

    private static void _0Dot7To0Dot8(Map ties) {
        if (ties['version'] != '0.7') {
            return
        }

        // change top level schema version
        ties['version'] = '0.8'

        List objectItems = ties.get('objectItems') as List ?: []
        objectItems.each { Map objectItem ->
            List objectRelationships = objectItem.get('objectRelationships') as List ?: []
            objectRelationships.each { Map objectRelationship ->
                ties.get('objectRelationships', []) << objectRelationship
                objectRelationship['linkageDirectionality'] = 'DIRECTED'
                objectRelationship['linkageSystemIds'] = [objectItem['systemId'], objectRelationship.remove('linkageSystemId')]
            }
            objectItem.remove('objectRelationships')
        }
    }

    private static void _0Dot8To0Dot9(Map ties) {
        if (ties['version'] != '0.8') {
            return
        }

        // change top level schema version
        ties['version'] = '0.9'

        List objectItems = ties.get('objectItems') as List ?: []
        objectItems.each { Map objectItem ->
            // rename systemId to objectId
            if ('systemId' in objectItem.keySet()) {
                objectItem['objectId'] = objectItem.remove('systemId')
            }

            // rename otherIds to otherInformation
            if ('otherIds' in objectItem.keySet()) {
                objectItem['otherInformation'] = objectItem.remove('otherIds')
            }

            Map objectAssertions = objectItem.get('objectAssertions') as Map ?: [:]
            List annotations = objectAssertions.get('annotations') as List ?: []
            annotations.each { Map annotation ->
                // rename systemUniqueId to assertionReferenceId
                if ('systemUniqueId' in annotation.keySet()) {
                    annotation['assertionReferenceId'] = annotation.remove('systemUniqueId')
                }
                // rename systemName to system
                if ('systemName' in annotation.keySet()) {
                    annotation['system'] = annotation.remove('systemName')
                }
            }
            List supplementalDescriptions = objectAssertions.get('systemSupplementalDescriptions') as List ?: []
            supplementalDescriptions.each { Map supplementalDescription ->
                // rename systemExportId to assertionReferenceId
                if ('systemExportId' in supplementalDescription.keySet()) {
                    supplementalDescription['assertionReferenceId'] = supplementalDescription.remove('systemExportId')
                }
                // rename systemExportIdCallTag to assertionReferenceIdLabel
                if ('systemExportIdCallTag' in supplementalDescription.keySet()) {
                    supplementalDescription['assertionReferenceIdLabel'] = supplementalDescription.remove('systemExportIdCallTag')
                }
                // rename systemName to system
                if ('systemName' in supplementalDescription.keySet()) {
                    supplementalDescription['system'] = supplementalDescription.remove('systemName')
                }
            }

            // rename systemSupplementalDescriptions to supplementalDescriptions
            if ('systemSupplementalDescriptions' in objectAssertions.keySet()) {
                objectAssertions['supplementalDescriptions'] = objectAssertions.remove('systemSupplementalDescriptions')
            }
        }

        List objectRelationships = ties.get('objectRelationships') as List ?: []
        objectRelationships.each { Map objectRelationship ->
            // rename linkageSystemIds to linkageMemberIds
            if ('linkageSystemIds' in objectRelationship.keySet()) {
                objectRelationship['linkageMemberIds'] = objectRelationship.remove('linkageSystemIds')
            }
        }
    }

    private static void _0Dot9To1Dot0(Map ties) {
        if (ties['version'] != '0.9') {
            return
        }

        // change top level schema version
        ties['version'] = '1.0'

        // move top-level securityTag to new top-level authorityInformation object
        if ("securityTag" in ties.keySet()) {
            ties['authorityInformation'] = ['securityTag': ties.remove('securityTag')]
        }
    }
}
