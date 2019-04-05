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

class AssertionsSchemaTests {

    private Map<String, Object> annotation
    private Map<String, Object> supplementalDescription
    private Map<String, Object> objectAssertions
    private Map<String, Object> objectItem
    private Map<String, Object> ties

    @Before
    void setUp() {
        annotation = [
                'assertionId': 'a',
                'annotationType': 'a',
                'value': 'a',
                'securityTag': ''
        ]
        supplementalDescription = [
                'assertionId': 'a',
                'informationType': 'a',
                'sha256DataHash': 'a' * 64,
                'dataSize': 0,
                'securityTag': ''
        ]
        objectAssertions = [
                'annotations': [annotation],
                'supplementalDescriptions': [supplementalDescription]
        ]
        objectItem = [
                'objectId': 'a',
                'sha256Hash': 'a' * 64,
                'md5Hash': 'a' * 32,
                'authorityInformation': ['securityTag': ''],
                'objectAssertions': objectAssertions
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
        objectAssertions.remove('annotations')
        objectAssertions.remove('supplementalDescriptions')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_allRequiredFieldsMissing() {
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_additionalField() {
        objectAssertions['foo'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional property foo is not allowed'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions'
    }

    @Test
    void test_additionalFields() {
        objectAssertions['foo'] = 'a'
        objectAssertions['bar'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional properties [bar, foo] are not allowed'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions'
    }

    @Test
    void test_annotationsMissing() {
        objectAssertions.remove('annotations')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_annotationsTooSmall() {
        objectAssertions['annotations'] = []
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_annotationsDuplicateItems() {
        objectAssertions['annotations'] << annotation
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'array property annotations has duplicate items at index [0, 1]'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations'
    }

    @Test
    void test_supplementalDescriptionsMissing() {
        objectAssertions.remove('supplementalDescriptions')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_supplementalDescriptionsTooSmall() {
        objectAssertions['supplementalDescriptions'] = []
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_supplementalDescriptionsDuplicateItems() {
        objectAssertions['supplementalDescriptions'] << supplementalDescription
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'array property supplementalDescriptions has duplicate items at index [0, 1]'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions'
    }
}
