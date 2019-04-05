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

import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
class SemanticValidator {

    public List<ValidationWarning> allWarnings(byte[] json) throws ValidationException {
        return allWarnings(new JsonSlurper().parse(json, 'UTF-8') as Map<String, ?>)
    }

    public List<ValidationWarning> allWarnings(String json) throws ValidationException {
        return allWarnings(new JsonSlurper().parseText(json) as Map<String, ?>)
    }

    public List<ValidationWarning> allWarnings(File json) throws ValidationException {
        return allWarnings(new JsonSlurper().parse(json, 'UTF-8') as Map<String, ?>)
    }

    public List<ValidationWarning> allWarnings(InputStream json) throws ValidationException {
        return allWarnings(new JsonSlurper().parse(json, 'UTF-8') as Map<String, ?>)
    }

    public List<ValidationWarning> allWarnings(Map json) throws ValidationException {
        List<ValidationWarning> warnings = []
        warnings += checkDuplicateObjectItemSha256Hashes(json)
        warnings += checkDuplicateObjectItemOtherInformationKeys(json)
        warnings += checkDuplicateObjectGroupOtherInformationKeys(json)
        warnings += checkDuplicateObjectIdsAndGroupIds(json)
        warnings += checkDuplicateAssertionIds(json)
        warnings += checkObjectRelationshipLinkageMemberIds(json)
        warnings += checkObjectRelationshipLinkageAssertionIds(json)
        warnings += checkDuplicateObjectRelationshipOtherInformationKeys(json)
        warnings += checkDuplicateTopLevelOtherInformationKeys(json)
        return warnings
    }

    @CompileDynamic
    private static Set<String> allObjectIds(Map<String, ?> ties) {
        List<String> objectIds = ties.get('objectItems', []).collect { Map<String, ?> objectItem -> objectItem.get('objectId') as String }
        return objectIds.findAll({ it != null }).toSet()
    }

    @CompileDynamic
    private static Set<String> allGroupIds(Map<String, ?> ties) {
        List<String> groupIds = ties.get('objectGroups', []).collect { Map<String, ?> objectGroup -> objectGroup.get('groupId') as String }
        return groupIds.findAll({ it != null }).toSet()
    }

    @CompileDynamic
    private static Set<String> allAssertionIds(Map<String, ?> ties) {
        List<String> assertionIds = []
        ties.get('objectItems', []).each { Map<String, ?> objectItem ->
            Map<String, ?> assertions = objectItem.get('objectAssertions', [:]) as Map<String, ?>
            assertions.get('annotations', []).each { Map<String, ?> annotation ->
                assertionIds.add(annotation.get('assertionId') as String)
            }
            assertions.get('supplementalDescriptions', []).each { Map<String, ?> supplementalDescription ->
                assertionIds.add(supplementalDescription.get('assertionId') as String)
            }
        }
        ties.get('objectGroups', []).each { Map<String, ?> objectGroup ->
            Map<String, ?> assertions = objectGroup.get('groupAssertions', [:]) as Map<String, ?>
            assertions.get('annotations', []).each { Map<String, ?> annotation ->
                assertionIds.add(annotation.get('assertionId') as String)
            }
            assertions.get('supplementalDescriptions', []).each { Map<String, ?> supplementalDescription ->
                assertionIds.add(supplementalDescription.get('assertionId') as String)
            }
        }
        return assertionIds.findAll({ it != null }).toSet()
    }

    @CompileDynamic
    private static List<ValidationWarning> checkDuplicateObjectItemSha256Hashes(Map<String, ?> ties) {
        Map<String, List<Integer>> objectItemIndex = [:]
        List<String> objectItemIndexOrder = []
        ties.get('objectItems', []).eachWithIndex { Map<String, ?> objectItem, int i ->
            String sha256Hash = objectItem.get('sha256Hash')
            if (!(sha256Hash in objectItemIndexOrder)) {
                objectItemIndexOrder << sha256Hash
            }
            objectItemIndex[sha256Hash] = objectItemIndex.get(sha256Hash, []) + [i]
        }

        List<ValidationWarning> warnings = []
        objectItemIndexOrder.each { String sha256Hash ->
            List<Integer> duplicateIndexes = objectItemIndex[sha256Hash]
            if (duplicateIndexes.size() > 1) {
                String message = "objectItems at indexes ${duplicateIndexes} have duplicate sha256Hash value ('${sha256Hash}')"
                String location = '/objectItems'
                warnings << new ValidationWarning(message, location)
            }
        }
        return warnings
    }

    @CompileDynamic
    private static List<ValidationWarning> checkDuplicateObjectItemOtherInformationKeys(Map<String, ?> ties) {
        List<ValidationWarning> warnings = []
        ties.get('objectItems', []).eachWithIndex { Map<String, ?> objectItem, int objectItemIndex ->
            List<Map<String, ?>> otherInformation = objectItem.get('otherInformation', []) as List
            String location = "/objectItems[${objectItemIndex}]/otherInformation"
            warnings.addAll(checkDuplicateOtherInformationKeys(otherInformation, location))
        }
        return warnings
    }

    @CompileDynamic
    private static List<ValidationWarning> checkDuplicateObjectGroupOtherInformationKeys(Map<String, ?> ties) {
        List<ValidationWarning> warnings = []
        ties.get('objectGroups', []).eachWithIndex { Map<String, ?> objectGroup, int objectGroupIndex ->
            List<Map<String, ?>> otherInformation = objectGroup.get('otherInformation', []) as List
            String location = "/objectGroups[${objectGroupIndex}]/otherInformation"
            warnings.addAll(checkDuplicateOtherInformationKeys(otherInformation, location))
        }
        return warnings
    }

    @CompileDynamic
    private static List<ValidationWarning> checkDuplicateObjectIdsAndGroupIds(Map<String, ?> ties) {
        Map<String, List<Integer>> objectItemIndex = [:]
        List<String> objectItemIndexOrder = []
        ties.get('objectItems', []).eachWithIndex { Map<String, ?> objectItem, int i ->
            String objectId = objectItem.get('objectId')
            if (objectId != null) {
                if (!(objectId in objectItemIndexOrder)) {
                    objectItemIndexOrder << objectId
                }
                objectItemIndex[objectId] = objectItemIndex.get(objectId, []) + [i]
            }
        }
        Map<String, List<Integer>> objectGroupIndex = [:]
        List<String> objectGroupIndexOrder = []
        ties.get('objectGroups', []).eachWithIndex { Map<String, ?> objectGroup, int i ->
            String groupId = objectGroup.get('groupId')
            if (groupId != null) {
                if (!(groupId in objectGroupIndexOrder)) {
                    objectGroupIndexOrder << groupId
                }
                objectGroupIndex[groupId] = objectGroupIndex.get(groupId, []) + [i]
            }
        }

        List<ValidationWarning> warnings = []
        objectItemIndexOrder.each { String objectId ->
            List<Integer> duplicateIndexes = objectItemIndex[objectId]
            if (duplicateIndexes.size() > 1) {
                String message = "objectItems at indexes ${duplicateIndexes} have duplicate objectId value ('${objectId}')"
                String location = '/objectItems'
                warnings << new ValidationWarning(message, location)
            }
        }
        objectGroupIndexOrder.each { String groupId ->
            List<Integer> duplicateIndexes = objectGroupIndex[groupId]
            if (duplicateIndexes.size() > 1) {
                String message = "objectGroups at indexes ${duplicateIndexes} have duplicate groupId value ('${groupId}')"
                String location = '/objectGroups'
                warnings << new ValidationWarning(message, location)
            }
        }
        objectItemIndexOrder.each { String objectId ->
            if (objectId in objectGroupIndex) {
                List<Integer> objectItemIndexes = objectItemIndex[objectId]
                List<Integer> objectGroupIndexes = objectGroupIndex[objectId]
                String message
                if (objectItemIndexes.size() == 1) {
                    message = "objectItem at index ${objectItemIndexes[0]} "
                } else {
                    message = "objectItems at indexes ${objectItemIndexes} "
                }
                if (objectGroupIndexes.size() == 1) {
                    message += "and objectGroup at index ${objectGroupIndexes[0]} "
                } else {
                    message += "and objectGroups at indexes ${objectGroupIndexes} "
                }
                message += "have duplicate objectId/groupId value ('${objectId}')"
                String location = '/'
                warnings << new ValidationWarning(message, location)
            }
        }
        return warnings
    }

    @CompileDynamic
    private static List<ValidationWarning> checkDuplicateAssertionIds(Map<String, ?> ties) {
        List<String> assertionIdIndexOrder = []
        Map<String, List<String>> assertionIdLocationIndex = [:]
        ties.get('objectItems', []).eachWithIndex { Map<String, ?> objectItem, int objectItemIndex ->
            Map<String, ?> assertions = objectItem.get('objectAssertions', [:]) as Map<String, ?>
            assertions.get('annotations', []).eachWithIndex { Map<String, ?> annotation, int annotationIndex ->
                String assertionId = annotation.get('assertionId') as String
                if (assertionId != null && !(assertionId in assertionIdIndexOrder)) {
                    assertionIdIndexOrder << assertionId
                }
                String location = "/objectItems[${objectItemIndex}]/objectAssertions/annotations[${annotationIndex}]/assertionId"
                assertionIdLocationIndex[assertionId] = assertionIdLocationIndex.get(assertionId, []) + [location]
            }
            assertions.get('supplementalDescriptions', []).eachWithIndex { Map<String, ?> supplementalDescription, int supplementalDescriptionIndex ->
                String assertionId = supplementalDescription.get('assertionId') as String
                if (assertionId != null && !(assertionId in assertionIdIndexOrder)) {
                    assertionIdIndexOrder << assertionId
                }
                String location = "/objectItems[${objectItemIndex}]/objectAssertions/supplementalDescriptions[${supplementalDescriptionIndex}]/assertionId"
                assertionIdLocationIndex[assertionId] = assertionIdLocationIndex.get(assertionId, []) + [location]
            }
        }
        ties.get('objectGroups', []).eachWithIndex { Map<String, ?> objectGroup, int objectGroupIndex ->
            Map<String, ?> assertions = objectGroup.get('groupAssertions', [:]) as Map<String, ?>
            assertions.get('annotations', []).eachWithIndex { Map<String, ?> annotation, int annotationIndex ->
                String assertionId = annotation.get('assertionId') as String
                if (assertionId != null && !(assertionId in assertionIdIndexOrder)) {
                    assertionIdIndexOrder << assertionId
                }
                String location = "/objectGroups[${objectGroupIndex}]/groupAssertions/annotations[${annotationIndex}]/assertionId"
                assertionIdLocationIndex[assertionId] = assertionIdLocationIndex.get(assertionId, []) + [location]
            }
            assertions.get('supplementalDescriptions', []).eachWithIndex { Map<String, ?> supplementalDescription, int supplementalDescriptionIndex ->
                String assertionId = supplementalDescription.get('assertionId') as String
                if (assertionId != null && !(assertionId in assertionIdIndexOrder)) {
                    assertionIdIndexOrder << assertionId
                }
                String location = "/objectGroups[${objectGroupIndex}]/groupAssertions/supplementalDescriptions[${supplementalDescriptionIndex}]/assertionId"
                assertionIdLocationIndex[assertionId] = assertionIdLocationIndex.get(assertionId, []) + [location]
            }
        }

        List<ValidationWarning> warnings = []
        assertionIdIndexOrder.each { String assertionId ->
            List<String> locations = assertionIdLocationIndex[assertionId]
            if (locations.size() > 1) {
                locations.each { String location ->
                    String message = "assertion has duplicate assertionId value ('${assertionId}')"
                    warnings << new ValidationWarning(message, location)
                }
            }
        }
        return warnings
    }

    @CompileDynamic
    private static List<ValidationWarning> checkObjectRelationshipLinkageMemberIds(Map<String, ?> ties) {
        Set<String> objectIds = allObjectIds(ties)
        Set<String> groupIds = allGroupIds(ties)
        List<ValidationWarning> warnings = []
        ties.get('objectRelationships', []).eachWithIndex { Map objectRelationship, int objectRelationshipIdx ->
            List<String> linkageMemberIds = objectRelationship.get('linkageMemberIds', []) as List
            linkageMemberIds.eachWithIndex { String linkageMemberId, int linkageMemberIdIdx ->
                if (linkageMemberId != null && !(linkageMemberId in objectIds) && !(linkageMemberId in groupIds)) {
                    String message = "objectRelationship has a linkageMemberId ('${linkageMemberId}') that does not reference an objectItem or objectGroup in this export"
                    String location = "/objectRelationships[${objectRelationshipIdx}]/linkageMemberIds[${linkageMemberIdIdx}]"
                    warnings << new ValidationWarning(message, location)
                }
            }
        }
        return warnings
    }

    @CompileDynamic
    private static List<ValidationWarning> checkObjectRelationshipLinkageAssertionIds(Map<String, ?> ties) {
        Set<String> assertionIds = allAssertionIds(ties)
        List<ValidationWarning> warnings = []
        ties.get('objectRelationships', []).eachWithIndex { Map objectRelationship, int objectRelationshipIdx ->
            String linkageAssertionId = objectRelationship.get('linkageAssertionId')
            if (linkageAssertionId != null && !(linkageAssertionId in assertionIds)) {
                String message = "objectRelationship has a linkageAssertionId ('${linkageAssertionId}') that does not reference an assertion in this export"
                String location = "/objectRelationships[${objectRelationshipIdx}]/linkageAssertionId"
                warnings << new ValidationWarning(message, location)
            }
        }
        return warnings
    }

    @CompileDynamic
    private static List<ValidationWarning> checkDuplicateObjectRelationshipOtherInformationKeys(Map<String, ?> ties) {
        List<ValidationWarning> warnings = []
        ties.get('objectRelationships', []).eachWithIndex { Map<String, ?> objectRelationship, int objectRelationshipIndex ->
            List<Map<String, ?>> otherInformation = objectRelationship.get('otherInformation', []) as List
            String location = "/objectRelationships[${objectRelationshipIndex}]/otherInformation"
            warnings.addAll(checkDuplicateOtherInformationKeys(otherInformation, location))
        }
        return warnings
    }

    @CompileDynamic
    private static List<ValidationWarning> checkDuplicateTopLevelOtherInformationKeys(Map<String, ?> ties) {
        List<Map<String, ?>> otherInformation = ties.get('otherInformation', []) as List
        return checkDuplicateOtherInformationKeys(otherInformation, '/otherInformation')
    }

    @CompileDynamic
    private static List<ValidationWarning> checkDuplicateOtherInformationKeys(Object otherInformation, String location) {
        Map<String, List<Integer>> keyIndex = [:]
        List<String> keyIndexOrder = []
        otherInformation.eachWithIndex { Map<String, ?> item, int i ->
            if (item.get('key') != null) {
                String key = item.get('key')
                if (!(key in keyIndexOrder)) {
                    keyIndexOrder << key
                }
                keyIndex[key] = keyIndex.get(key, []) + [i]
            }
        }

        List<ValidationWarning> warnings = []
        keyIndexOrder.each { String key ->
            List<Integer> duplicateIndexes = keyIndex[key]
            if (duplicateIndexes.size() > 1) {
                String message = "otherInformation array contains duplicate key ('${key}') at indexes ${duplicateIndexes}"
                warnings << new ValidationWarning(message, location)
            }
        }
        return warnings
    }
}
