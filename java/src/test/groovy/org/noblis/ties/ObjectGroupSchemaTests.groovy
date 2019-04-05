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

class ObjectGroupSchemaTests {

    private Map<String, Object> objectGroup
    private Map<String, Object> objectItem
    private Map<String, Object> ties

    @Before
    void setUp() {
        objectGroup = [
                'groupId': 'a',
                'groupType': 'a',
                'groupDescription': 'a',
                'groupMemberIds': [],
                'groupAssertions': [:],
                'otherInformation': [],
        ]
        objectItem = [
                'objectId': 'a',
                'sha256Hash': 'a' * 64,
                'md5Hash': 'a' * 32,
                'authorityInformation': ['securityTag': '']
        ]
        ties = [
                'version': '0.9',
                'securityTag': 'a',
                'objectItems': [objectItem],
                'objectGroups': [objectGroup],
        ]
    }

    @Test
    void test_allFields() {
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_onlyRequiredFields() {
        objectGroup.remove('groupDescription')
        objectGroup.remove('groupAssertions')
        objectGroup.remove('otherInformation')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_allRequiredFieldsMissing() {
        objectGroup.remove('groupId')
        objectGroup.remove('groupType')
        objectGroup.remove('groupMemberIds')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required properties [groupId, groupMemberIds, groupType] are missing'
        assert validationExceptions[0].location == '/objectGroups[0]'
    }

    @Test
    void test_additionalField() {
        objectGroup['foo'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional property foo is not allowed'
        assert validationExceptions[0].location == '/objectGroups[0]'
    }

    @Test
    void test_additionalFields() {
        objectGroup['foo'] = 'a'
        objectGroup['bar'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional properties [bar, foo] are not allowed'
        assert validationExceptions[0].location == '/objectGroups[0]'
    }

    @Test
    void test_groupIdMissing() {
        objectGroup.remove('groupId')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property groupId is missing'
        assert validationExceptions[0].location == '/objectGroups[0]'
    }

    @Test
    void test_groupIdTooShort() {
        objectGroup['groupId'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for groupId property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectGroups[0]/groupId'
    }

    @Test
    void test_groupIdTooLong() {
        objectGroup['groupId'] = 'a' * 257
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '${'a' * 257}' for groupId property is too long, maximum length 256"
        assert validationExceptions[0].location == '/objectGroups[0]/groupId'
    }

    @Test
    void test_groupTypeMissing() {
        objectGroup.remove('groupType')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property groupType is missing'
        assert validationExceptions[0].location == '/objectGroups[0]'
    }

    @Test
    void test_groupTypeTooShort() {
        objectGroup['groupType'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for groupType property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectGroups[0]/groupType'
    }

    @Test
    void test_groupDescriptionMissing() {
        objectGroup.remove('groupDescription')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_groupDescriptionTooShort() {
        objectGroup['groupDescription'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for groupDescription property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectGroups[0]/groupDescription'
    }

    @Test
    void test_groupMemberIdsMissing() {
        objectGroup.remove('groupMemberIds')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property groupMemberIds is missing'
        assert validationExceptions[0].location == '/objectGroups[0]'
    }

    @Test
    void test_groupMemberIdsEmptyList() {
        objectGroup['groupMemberIds'] = []
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_groupMemberIdTooShort() {
        objectGroup['groupMemberIds'] = ['a', '']
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for element at index 1 in groupMemberIds is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectGroups[0]/groupMemberIds[1]'
    }

    @Test
    void test_groupMemberIdTooLong() {
        objectGroup['groupMemberIds'] = ['a', 'a' * 257]
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '${'a' * 257}' for element at index 1 in groupMemberIds is too long, maximum length 256"
        assert validationExceptions[0].location == '/objectGroups[0]/groupMemberIds[1]'
    }

    @Test
    void test_groupAssertionsMissing() {
        objectGroup.remove('groupAssertions')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_otherInformationMissing() {
        objectGroup.remove('otherInformation')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_otherInformationNull() {
        objectGroup['otherInformation'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property otherInformation with null value should be of type array'
        assert validationExceptions[0].location == '/objectGroups[0]/otherInformation'
    }

    @Test
    void test_otherInformationTooSmall() {
        objectGroup['otherInformation'] = []
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_otherInformationDuplicateItems() {
        def otherInformation = ['key': 'a', 'value': 'a']
        objectGroup['otherInformation'] = [otherInformation, otherInformation]
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'array property otherInformation has duplicate items at index [0, 1]'
        assert validationExceptions[0].location == '/objectGroups[0]/otherInformation'
    }
}
