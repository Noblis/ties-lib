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

class OtherInformationSchemaTests {

    private Map<String, Object> otherInformation
    private Map<String, Object> objectItem
    private Map<String, Object> ties

    @Before
    void setUp() {
        otherInformation = [
                'key': 'a',
                'value': 'a'
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
                'otherInformation': [otherInformation]
        ]
    }

    @Test
    void test_allFields() {
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_onlyRequiredFields() {
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_allRequiredFieldsMissing() {
        otherInformation.remove('key')
        otherInformation.remove('value')
                List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required properties [key, value] are missing'
        assert validationExceptions[0].location == '/otherInformation[0]'
    }

    @Test
    void test_additionalField() {
        otherInformation['foo'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional property foo is not allowed'
        assert validationExceptions[0].location == '/otherInformation[0]'
    }

    @Test
    void test_additionalFields() {
        otherInformation['foo'] = 'a'
        otherInformation['bar'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional properties [bar, foo] are not allowed'
        assert validationExceptions[0].location == '/otherInformation[0]'
    }

    @Test
    void test_keyMissing() {
        otherInformation.remove('key')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property key is missing'
        assert validationExceptions[0].location == '/otherInformation[0]'
    }

    @Test
    void test_keyTooShort() {
        otherInformation['key'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for key property is too short, minimum length 1"
        assert validationExceptions[0].location == '/otherInformation[0]/key'
    }

    @Test
    void test_valueMissing() {
        otherInformation.remove('value')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property value is missing'
        assert validationExceptions[0].location == '/otherInformation[0]'
    }

    @Test
    void test_valueBoolean() {
        otherInformation['value'] = true
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_valueInteger() {
        otherInformation['value'] = 1
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_valueNumber() {
        otherInformation['value'] = 1.1
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_valueString() {
        otherInformation['value'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_valueArray() {
        otherInformation['value'] = []
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property type array for property value is not one of the allowed types: [boolean, integer, number, string]'
        assert validationExceptions[0].location == '/otherInformation[0]/value'
    }

    @Test
    void test_valueObject() {
        otherInformation['value'] = [:]
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'property type object for property value is not one of the allowed types: [boolean, integer, number, string]'
        assert validationExceptions[0].location == '/otherInformation[0]/value'
    }
}
