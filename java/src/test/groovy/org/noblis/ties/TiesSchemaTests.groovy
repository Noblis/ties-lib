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

import org.junit.Before
import org.junit.Test
import org.noblis.ties.TiesValidator
import org.noblis.ties.ValidationException

class TiesSchemaTests {

    private static Map<String, Object> objectItem
    private static Map<String, Object> objectGroup
    private static Map<String, Object> objectRelationship
    private static Map<String, Object> otherInformation
    private static Map<String, Object> ties

    @Before
    void setUp() {
        objectItem = [
                'objectId': 'a',
                'sha256Hash': 'a' * 64,
                'md5Hash': 'a' * 32,
                'authorityInformation': ['securityTag': '']
        ]
        objectGroup = [
                'groupId': 'a',
                'groupType': 'a',
                'groupMemberIds': [],
        ]
        objectRelationship = [
                'linkageMemberIds': ['a', 'a'],
                'linkageDirectionality': 'UNDIRECTED',
        ]
        otherInformation = [
                'key': 'a',
                'value': 'a',
        ]
        ties = [
                'version': '1.0',
                'id': 'a',
                'system': 'a',
                'organization': 'a',
                'time': '1970-01-01T00:00:00Z',
                'description': 'a',
                'type': 'a',
                'authorityInformation': ['securityTag': ''],
                'objectItems': [objectItem],
                'objectGroups': [objectGroup],
                'objectRelationships': [objectRelationship],
                'otherInformation': [otherInformation],
        ]
    }

    @Test
    void test_allFields() {
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_onlyRequiredFields() {
        ties.remove('id')
        ties.remove('system')
        ties.remove('organization')
        ties.remove('time')
        ties.remove('description')
        ties.remove('type')
        ties.remove('objectGroups')
        ties.remove('objectRelationships')
        ties.remove('otherInformation')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_allRequiredFieldsMissing() {
        ties.remove('version')
        ties.remove('authorityInformation')
        ties.remove('objectItems')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required properties [authorityInformation, objectItems, version] are missing'
        assert validationExceptions[0].location == '/'
    }

    @Test
    void test_additionalField() {
        ties['foo'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional property foo is not allowed'
        assert validationExceptions[0].location == '/'
    }

    @Test
    void test_additionalFields() {
        ties['foo'] = 'a'
        ties['bar'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional properties [bar, foo] are not allowed'
        assert validationExceptions[0].location == '/'
    }

    @Test
    void test_versionMissing() {
        ties.remove('version')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property version is missing'
        assert validationExceptions[0].location == '/'
    }

    @Test
    void test_versionNull() {
        ties['version'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'enum property version with value null should have one of the allowed values: [1.0]'
        assert validationExceptions[0].location == '/version'
    }

    @Test
    void test_versionTooShort() {
        ties['version'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "enum property version with value '' should have one of the allowed values: [1.0]"
        assert validationExceptions[0].location == '/version'
    }

    @Test
    void test_versionInvalidValue() {
        ties['version'] = '0.1'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "enum property version with value '0.1' should have one of the allowed values: [1.0]"
        assert validationExceptions[0].location == '/version'
    }

    @Test
    void test_idMissing() {
        ties.remove('id')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_idNull() {
        ties['id'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property id with null value should be of type string'
        assert validationExceptions[0].location == '/id'
    }

    @Test
    void test_idTooShort() {
        ties['id'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for id property is too short, minimum length 1"
        assert validationExceptions[0].location == '/id'
    }

    @Test
    void test_idTooLong() {
        ties['id'] = 'a' * 257
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '${'a' * 257}' for id property is too long, maximum length 256"
        assert validationExceptions[0].location == '/id'
    }

    @Test
    void test_systemMissing() {
        ties.remove('system')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_systemNull() {
        ties['system'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property system with null value should be of type string'
        assert validationExceptions[0].location == '/system'
    }

    @Test
    void test_systemTooShort() {
        ties['system'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for system property is too short, minimum length 1"
        assert validationExceptions[0].location == '/system'
    }

    @Test
    void test_organizationMissing() {
        ties.remove('organization')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_organizationNull() {
        ties['organization'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property organization with null value should be of type string'
        assert validationExceptions[0].location == '/organization'
    }

    @Test
    void test_organizationTooShort() {
        ties['organization'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for organization property is too short, minimum length 1"
        assert validationExceptions[0].location == '/organization'
    }

    @Test
    void test_timeMissing() {
        ties.remove('time')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_timeNull() {
        ties['time'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property time with null value should be of type string'
        assert validationExceptions[0].location == '/time'
    }

    @Test
    void test_timeBadFormat() {
        ties['time'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value 'a' for time property does not match the date-time format: [yyyy-MM-dd'T'HH:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}Z]"
        assert validationExceptions[0].location == '/time'
    }

    @Test
    void test_descriptionMissing() {
        ties.remove('description')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_descriptionNull() {
        ties['description'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property description with null value should be of type string'
        assert validationExceptions[0].location == '/description'
    }

    @Test
    void test_descriptionTooShort() {
        ties['description'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for description property is too short, minimum length 1"
        assert validationExceptions[0].location == '/description'
    }

    @Test
    void test_typeMissing() {
        ties.remove('type')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_typeNull() {
        ties['type'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property type with null value should be of type string'
        assert validationExceptions[0].location == '/type'
    }

    @Test
    void test_typeTooShort() {
        ties['type'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for type property is too short, minimum length 1"
        assert validationExceptions[0].location == '/type'
    }

    @Test
    void test_authorityInformationMissing() {
        ties.remove('authorityInformation')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property authorityInformation is missing'
        assert validationExceptions[0].location == '/'
    }

    @Test
    void test_authorityInformationNull() {
        ties['authorityInformation'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property authorityInformation with null value should be of type object'
        assert validationExceptions[0].location == '/authorityInformation'
    }

    @Test
    void test_objectItemsMissing() {
        ties.remove('objectItems')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property objectItems is missing'
        assert validationExceptions[0].location == '/'
    }

    @Test
    void test_objectItemsNull() {
        ties['objectItems'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property objectItems with null value should be of type array'
        assert validationExceptions[0].location == '/objectItems'
    }

    @Test
    void test_objectItemsTooSmall() {
        ties['objectItems'] = []
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'array property objectItems with 0 items is too small, minimum size 1'
        assert validationExceptions[0].location == '/objectItems'
    }

    @Test
    void test_objectItemsDuplicateItems() {
        ties['objectItems'] = [objectItem, objectItem]
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'array property objectItems has duplicate items at index [0, 1]'
        assert validationExceptions[0].location == '/objectItems'
    }

    @Test
    void test_objectGroupsMissing() {
        ties.remove('objectGroups')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_objectGroupsNull() {
        ties['objectGroups'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property objectGroups with null value should be of type array'
        assert validationExceptions[0].location == '/objectGroups'
    }

    @Test
    void test_objectGroupsEmptyList() {
        ties['objectGroups'] = []
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_objectGroupsDuplicateItems() {
        ties['objectGroups'] = [objectGroup, objectGroup]
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'array property objectGroups has duplicate items at index [0, 1]'
        assert validationExceptions[0].location == '/objectGroups'
    }

    @Test
    void test_objectRelationshipsMissing() {
        ties.remove('objectRelationships')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_objectRelationshipsNull() {
        ties['objectRelationships'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property objectRelationships with null value should be of type array'
        assert validationExceptions[0].location == '/objectRelationships'
    }

    @Test
    void test_objectRelationshipsEmptyList() {
        ties['objectRelationships'] = []
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_objectRelationshipsDuplicateItems() {
        def objectRelationship = [
                'linkageMemberIds': ['a', 'b'],
                'linkageDirectionality': 'DIRECTED',
        ]
        ties['objectRelationships'] = [objectRelationship, objectRelationship]
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'array property objectRelationships has duplicate items at index [0, 1]'
        assert validationExceptions[0].location == '/objectRelationships'
    }

    @Test
    void test_otherInformationMissing() {
        ties.remove('otherInformation')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_otherInformationNull() {
        ties['otherInformation'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property otherInformation with null value should be of type array'
        assert validationExceptions[0].location == '/otherInformation'
    }

    @Test
    void test_otherInformationEmptyList() {
        ties['otherInformation'] = []
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_otherInformationDuplicateItems() {
        ties['otherInformation'] = [otherInformation, otherInformation]
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'array property otherInformation has duplicate items at index [0, 1]'
        assert validationExceptions[0].location == '/otherInformation'
    }
}
