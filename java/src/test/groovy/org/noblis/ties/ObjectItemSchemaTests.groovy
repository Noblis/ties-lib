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

class ObjectItemSchemaTests {

    private Map<String, Object> objectItem
    private Map<String, Object> ties

    @Before
    void setUp() {
        objectItem = [
                'objectId': 'a',
                'sha256Hash': 'a' * 64,
                'md5Hash': 'a' * 32,
                'size': 0,
                'mimeType': 'a',
                'relativeUri': 'a',
                'originalPath': 'a',
                'authorityInformation': ['securityTag': ''],
                'objectAssertions': [:],
                'otherInformation': [],
        ]
        ties = [
                'version': '0.9',
                'securityTag': 'a',
                'objectItems': [objectItem]
        ]
    }

    @Test
    void test_allFields() {
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_onlyRequiredFields() {
        objectItem.remove('size')
        objectItem.remove('mimeType')
        objectItem.remove('relativeUri')
        objectItem.remove('originalPath')
        objectItem.remove('objectAssertions')
        objectItem.remove('otherInformation')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_allRequiredFieldsMissing() {
        objectItem.remove('objectId')
        objectItem.remove('sha256Hash')
        objectItem.remove('md5Hash')
        objectItem.remove('authorityInformation')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required properties [authorityInformation, md5Hash, objectId, sha256Hash] are missing'
        assert validationExceptions[0].location == '/objectItems[0]'
    }

    @Test
    void test_additionalField() {
        objectItem['foo'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional property foo is not allowed'
        assert validationExceptions[0].location == '/objectItems[0]'
    }

    @Test
    void test_additionalFields() {
        objectItem['foo'] = 'a'
        objectItem['bar'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional properties [bar, foo] are not allowed'
        assert validationExceptions[0].location == '/objectItems[0]'
    }

    @Test
    void test_objectIdMissing() {
        objectItem.remove('objectId')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property objectId is missing'
        assert validationExceptions[0].location == '/objectItems[0]'
    }

    @Test
    void test_objectIdTooShort() {
        objectItem['objectId'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for objectId property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/objectId'
    }

    @Test
    void test_objectIdTooLong() {
        objectItem['objectId'] = 'a' * 257
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '${'a' * 257}' for objectId property is too long, maximum length 256"
        assert validationExceptions[0].location == '/objectItems[0]/objectId'
    }

    @Test
    void test_sha256HashMissing() {
        objectItem.remove('sha256Hash')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property sha256Hash is missing'
        assert validationExceptions[0].location == '/objectItems[0]'
    }

    @Test
    void test_sha256HashTooShort() {
        objectItem['sha256Hash'] = 'a' * 63
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 2
        assert validationExceptions[0].message == "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256Hash property is too short, minimum length 64"
        assert validationExceptions[0].location == '/objectItems[0]/sha256Hash'
        assert validationExceptions[1].message == "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256Hash property does not match the pattern '^[a-fA-F0-9]{64}\$'"
        assert validationExceptions[1].location == '/objectItems[0]/sha256Hash'
    }

    @Test
    void test_sha256HashTooLong() {
        objectItem['sha256Hash'] = 'a' * 65
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 2
        assert validationExceptions[0].message == "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256Hash property is too long, maximum length 64"
        assert validationExceptions[0].location == '/objectItems[0]/sha256Hash'
        assert validationExceptions[1].message == "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256Hash property does not match the pattern '^[a-fA-F0-9]{64}\$'"
        assert validationExceptions[1].location == '/objectItems[0]/sha256Hash'
    }

    @Test
    void test_sha256HashBadFormat() {
        objectItem['sha256Hash'] = 'z' * 64
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value 'zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz' for sha256Hash property does not match the pattern '^[a-fA-F0-9]{64}\$'"
        assert validationExceptions[0].location == '/objectItems[0]/sha256Hash'
    }

    @Test
    void test_md5HashMissing() {
        objectItem.remove('md5Hash')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property md5Hash is missing'
        assert validationExceptions[0].location == '/objectItems[0]'
    }

    @Test
    void test_md5HashTooShort() {
        objectItem['md5Hash'] = 'a' * 31
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 2
        assert validationExceptions[0].message == "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for md5Hash property is too short, minimum length 32"
        assert validationExceptions[0].location == '/objectItems[0]/md5Hash'
        assert validationExceptions[1].message == "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for md5Hash property does not match the pattern '^[a-fA-F0-9]{32}\$'"
        assert validationExceptions[1].location == '/objectItems[0]/md5Hash'
    }

    @Test
    void test_md5HashTooLong() {
        objectItem['md5Hash'] = 'a' * 33
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 2
        assert validationExceptions[0].message == "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for md5Hash property is too long, maximum length 32"
        assert validationExceptions[0].location == '/objectItems[0]/md5Hash'
        assert validationExceptions[1].message == "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for md5Hash property does not match the pattern '^[a-fA-F0-9]{32}\$'"
        assert validationExceptions[1].location == '/objectItems[0]/md5Hash'
    }

    @Test
    void test_md5HashBadFormat() {
        objectItem['md5Hash'] = 'z' * 32
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value 'zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz' for md5Hash property does not match the pattern '^[a-fA-F0-9]{32}\$'"
        assert validationExceptions[0].location == '/objectItems[0]/md5Hash'
    }

    @Test
    void test_sizeMissing() {
        objectItem.remove('size')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_sizeTooSmall() {
        objectItem['size'] = -1
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property value -1 for size property is less than the minimum value of 0'
        assert validationExceptions[0].location == '/objectItems[0]/size'
    }

    @Test
    void test_mimeTypeMissing() {
        objectItem.remove('mimeType')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_mimeTypeTooShort() {
        objectItem['mimeType'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for mimeType property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/mimeType'
    }

    @Test
    void test_relativeUriMissing() {
        objectItem.remove('relativeUri')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_relativeUriTooShort() {
        objectItem['relativeUri'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for relativeUri property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/relativeUri'
    }

    @Test
    void test_originalPathMissing() {
        objectItem.remove('originalPath')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_originalPathTooShort() {
        objectItem['originalPath'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for originalPath property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/originalPath'
    }

    @Test
    void test_authorityInformationMissing() {
        objectItem.remove('authorityInformation')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property authorityInformation is missing'
        assert validationExceptions[0].location == '/objectItems[0]'
    }

    @Test
    void test_objectAssertionsMissing() {
        objectItem.remove('objectAssertions')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_otherInformationMissing() {
        objectItem.remove('otherInformation')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_otherInformationNull() {
        objectItem['otherInformation'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property otherInformation with null value should be of type array'
        assert validationExceptions[0].location == '/objectItems[0]/otherInformation'
    }

    @Test
    void test_otherInformationTooSmall() {
        objectItem['otherInformation'] = []
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_otherInformationDuplicateItems() {
        def otherInformation = ['key': 'a', 'value': 'a']
        objectItem['otherInformation'] = [otherInformation, otherInformation]
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'array property otherInformation has duplicate items at index [0, 1]'
        assert validationExceptions[0].location == '/objectItems[0]/otherInformation'
    }
}
