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

class ObjectRelationshipSchemaTests {

    private Map<String, Object> objectRelationship
    private Map<String, Object> objectItem
    private Map<String, Object> ties

    @Before
    void setUp() {
        objectRelationship = [
                'linkageMemberIds': ['a', 'b'],
                'linkageDirectionality': 'DIRECTED',
                'linkageType': 'a',
                'linkageAssertionId': 'a',
                'otherInformation': []
        ]
        objectItem = [
                'objectId': 'a',
                'sha256Hash': 'a' * 64,
                'md5Hash': 'a' * 32,
                'authorityInformation': ['securityTag': '']
        ]
        ties = [
                'version': '1.0',
                'authorityInformation': ['securityTag': ''],
                'objectItems': [objectItem],
                'objectRelationships': [objectRelationship]
        ]
    }

    @Test
    void test_allFields() {
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_onlyRequiredFields() {
        objectRelationship.remove('linkageType')
        objectRelationship.remove('linkageAssertionId')
        objectRelationship.remove('otherInformation')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_allRequiredFieldsMissing() {
        objectRelationship.remove('linkageMemberIds')
        objectRelationship.remove('linkageDirectionality')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required properties [linkageDirectionality, linkageMemberIds] are missing'
        assert validationExceptions[0].location == '/objectRelationships[0]'
    }

    @Test
    void test_additionalField() {
        objectRelationship['foo'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional property foo is not allowed'
        assert validationExceptions[0].location == '/objectRelationships[0]'
    }

    @Test
    void test_additionalFields() {
        objectRelationship['foo'] = 'a'
        objectRelationship['bar'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional properties [bar, foo] are not allowed'
        assert validationExceptions[0].location == '/objectRelationships[0]'
    }

    @Test
    void test_linkageMemberIdsMissing() {
        objectRelationship.remove('linkageMemberIds')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property linkageMemberIds is missing'
        assert validationExceptions[0].location == '/objectRelationships[0]'
    }

    @Test
    void test_linkageMemberIdsTooSmall() {
        objectRelationship['linkageMemberIds'] = ['a']
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'array property linkageMemberIds with 1 items is too small, minimum size 2'
        assert validationExceptions[0].location == '/objectRelationships[0]/linkageMemberIds'
    }

    @Test
    void test_linkageMemberIdsTooLarge() {
        objectRelationship['linkageMemberIds'] = ['a', 'b', 'c']
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'array property linkageMemberIds with 3 items is too large, maximum size 2'
        assert validationExceptions[0].location == '/objectRelationships[0]/linkageMemberIds'
    }

    @Test
    void test_linkageMemberIdTooShort() {
        objectRelationship['linkageMemberIds'] = ['a', '']
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for element at index 1 in linkageMemberIds is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectRelationships[0]/linkageMemberIds[1]'
    }

    @Test
    void test_linkageMemberIdTooLong() {
        objectRelationship['linkageMemberIds'] = ['a', 'a' * 257]
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '${'a' * 257}' for element at index 1 in linkageMemberIds is too long, maximum length 256"
        assert validationExceptions[0].location == '/objectRelationships[0]/linkageMemberIds[1]'
    }

    @Test
    void test_linkageDirectionalityMissing() {
        objectRelationship.remove('linkageDirectionality')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property linkageDirectionality is missing'
        assert validationExceptions[0].location == '/objectRelationships[0]'
    }

    @Test
    void test_linkageDirectionalityInvalidValue() {
        objectRelationship['linkageDirectionality'] = 'INVALID'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "enum property linkageDirectionality with value 'INVALID' should have one of the allowed values: [DIRECTED, BIDIRECTED, UNDIRECTED]"
        assert validationExceptions[0].location == '/objectRelationships[0]/linkageDirectionality'
    }

    @Test
    void test_linkageTypeMissing() {
        objectRelationship.remove('linkageType')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_linkageTypeTooShort() {
        objectRelationship['linkageType'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for linkageType property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectRelationships[0]/linkageType'
    }

    @Test
    void test_linkageAssertionIdMissing() {
        objectRelationship.remove('linkageAssertionId')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_linkageAssertionIdTooShort() {
        objectRelationship['linkageAssertionId'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for linkageAssertionId property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectRelationships[0]/linkageAssertionId'
    }

    @Test
    void test_otherInformationMissing() {
        objectRelationship.remove('otherInformation')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_otherInformationEmptyList() {
        objectRelationship['otherInformation'] = []
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_otherInformationDuplicateItems() {
        def otherInformation = ['key': 'a', 'value': 'a']
        objectRelationship['otherInformation'] = [otherInformation, otherInformation]
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'array property otherInformation has duplicate items at index [0, 1]'
        assert validationExceptions[0].location == '/objectRelationships[0]/otherInformation'
    }
}
