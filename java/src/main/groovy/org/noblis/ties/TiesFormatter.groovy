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

class TiesFormatter {

    private static LinkedHashMap<String, ?> reorderAnnotation(Map<String, ?> json) {
        List<String> keyOrder = [
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
        return reorderJsonKeys(json, keyOrder)
    }

    private static LinkedHashMap<String, ?> reorderAssertions(Map<String, ?> json) {
        List<String> keyOrder = [
                'annotations',
                'supplementalDescriptions',
        ]
        LinkedHashMap<String, ?> orderedJson = reorderJsonKeys(json, keyOrder)
        reorderJsonList(orderedJson, 'annotations', TiesFormatter.&reorderAnnotation)
        reorderJsonList(orderedJson, 'supplementalDescriptions', TiesFormatter.&reorderSupplementalDescription)
        return orderedJson
    }

    private static LinkedHashMap<String, ?> reorderAuthorityInformation(Map<String, ?> json) {
        List<String> keyOrder = [
                'collectionId',
                'collectionUuid',
                'collectionIdLabel',
                'collectionIdAlias',
                'collectionDescription',
                'subCollectionId',
                'subCollectionUuid',
                'subCollectionIdLabel',
                'subCollectionIdAlias',
                'subCollectionDescription',
                'registrationDate',
                'expirationDate',
                'owner',
                'securityTag',
        ]
        return reorderJsonKeys(json, keyOrder)
    }

    private static LinkedHashMap<String, ?> reorderObjectGroup(Map<String, ?> json) {
        List<String> keyOrder = [
                'groupId',
                'groupType',
                'groupDescription',
                'groupMemberIds',
                'groupAssertions',
                'otherInformation',
        ]
        LinkedHashMap<String, ?> orderedJson = reorderJsonKeys(json, keyOrder)
        if ('groupAssertions' in orderedJson.keySet()) {
            orderedJson['groupAssertions'] = reorderAssertions(orderedJson['groupAssertions'] as Map<String, ?>)
        }
        reorderJsonList(orderedJson, 'otherInformation', TiesFormatter.&reorderOtherInformation)
        return orderedJson
    }

    private static LinkedHashMap<String, ?> reorderObjectItem(Map<String, ?> json) {
        List<String> keyOrder = [
                'objectId',
                'sha256Hash',
                'md5Hash',
                'size',
                'mimeType',
                'absoluteUri',
                'relativeUri',
                'originalPath',
                'authorityInformation',
                'objectAssertions',
                'otherInformation',
        ]
        LinkedHashMap<String, ?> orderedJson = reorderJsonKeys(json, keyOrder)
        if ('authorityInformation' in orderedJson.keySet()) {
            orderedJson['authorityInformation'] = reorderAuthorityInformation(orderedJson['authorityInformation'] as Map<String, ?>)
        }
        if ('objectAssertions' in orderedJson.keySet()) {
            orderedJson['objectAssertions'] = reorderAssertions(orderedJson['objectAssertions'] as Map<String, ?>)
        }
        reorderJsonList(orderedJson, 'otherInformation', TiesFormatter.&reorderOtherInformation)
        return orderedJson
    }

    private static LinkedHashMap<String, ?> reorderObjectRelationship(Map<String, ?> json) {
        List<String> keyOrder = [
                'linkageMemberIds',
                'linkageDirectionality',
                'linkageType',
                'linkageAssertionId',
                'otherInformation',
        ]
        LinkedHashMap<String, ?> orderedJson = reorderJsonKeys(json, keyOrder)
        reorderJsonList(orderedJson, 'otherInformation', TiesFormatter.&reorderOtherInformation)
        return orderedJson
    }

    private static LinkedHashMap<String, ?> reorderOtherInformation(Map<String, ?> json) {
        List<String> keyOrder = [
                'key',
                'value',
        ]
        return reorderJsonKeys(json, keyOrder)
    }

    private static LinkedHashMap<String, ?> reorderSupplementalDescription(Map<String, ?> json) {
        List<String> keyOrder = [
                'assertionId',
                'assertionReferenceId',
                'assertionReferenceIdLabel',
                'system',
                'informationType',
                'sha256DataHash',
                'dataSize',
                'dataAbsoluteUri',
                'dataRelativeUri',
                'dataObject',
                'securityTag',
        ]
        return reorderJsonKeys(json, keyOrder)
    }

    public static LinkedHashMap<String, ?> reorderTiesJson(Map<String, ?> json) {
        List<String> keyOrder = [
                'version',
                'id',
                'system',
                'organization',
                'time',
                'description',
                'type',
                'authorityInformation',
                'objectItems',
                'objectGroups',
                'objectRelationships',
                'otherInformation',
        ]
        LinkedHashMap<String, ?> orderedJson = reorderJsonKeys(json, keyOrder)
        if ('authorityInformation' in orderedJson.keySet()) {
            orderedJson['authorityInformation'] = reorderAuthorityInformation(orderedJson['authorityInformation'] as Map<String, ?>)
        }
        reorderJsonList(orderedJson, 'objectItems', TiesFormatter.&reorderObjectItem)
        reorderJsonList(orderedJson, 'objectGroups', TiesFormatter.&reorderObjectGroup)
        reorderJsonList(orderedJson, 'objectRelationships', TiesFormatter.&reorderObjectRelationship)
        reorderJsonList(orderedJson, 'otherInformation', TiesFormatter.&reorderOtherInformation)
        return orderedJson
    }

    private static LinkedHashMap<String, ?> reorderJsonKeys(Map<String, ?> json, List<String> keyOrder) {
        LinkedHashMap<String, ?> orderedJson = new LinkedHashMap<String, ?>()
        keyOrder.each { String key ->
            if (key in json.keySet()) {
                orderedJson[key] = json[key]
            }
        }
        (json.keySet() - keyOrder.toSet()).sort().each { String key ->
            orderedJson[key] = json[key]
        }
        return orderedJson
    }

    private static void reorderJsonList(LinkedHashMap<String, ?> json, String key, Closure<LinkedHashMap<String, ?>> reorderFn) {
        if (key in json.keySet()) {
            json[key] = json[key].collect { reorderFn(it) }
        }
    }
}
