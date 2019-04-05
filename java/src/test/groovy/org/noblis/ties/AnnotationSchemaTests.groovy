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

class AnnotationSchemaTests {

    private Map<String, Object> annotation
    private Map<String, Object> objectItem
    private Map<String, Object> ties

    @Before
    void setUp() {
        annotation = [
                'assertionId': 'a',
                'assertionReferenceId': 'a',
                'assertionReferenceIdLabel': 'a',
                'system': 'a',
                'creator': 'a',
                'time': '1970-01-01T00:00:00Z',
                'annotationType': 'a',
                'key': 'a',
                'value': 'a',
                'itemAction': 'a',
                'itemActionTime': '1970-01-01T00:00:00Z',
                'securityTag': ''
        ]
        objectItem = [
                'objectId': 'a',
                'sha256Hash': 'a' * 64,
                'md5Hash': 'a' * 32,
                'authorityInformation': ['securityTag': ''],
                'objectAssertions': ['annotations': [annotation]]
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
        annotation.remove('assertionReferenceId')
        annotation.remove('assertionReferenceIdLabel')
        annotation.remove('system')
        annotation.remove('creator')
        annotation.remove('time')
        annotation.remove('key')
        annotation.remove('itemAction')
        annotation.remove('itemActionTime')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_allRequiredFieldsMissing() {
        annotation.remove('assertionId')
        annotation.remove('annotationType')
        annotation.remove('value')
        annotation.remove('securityTag')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required properties [annotationType, assertionId, securityTag, value] are missing'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]'
    }

    @Test
    void test_additionalField() {
        annotation['foo'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional property foo is not allowed'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]'
    }

    @Test
    void test_additionalFields() {
        annotation['foo'] = 'a'
        annotation['bar'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional properties [bar, foo] are not allowed'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]'
    }

    @Test
    void test_assertionIdMissing() {
        annotation.remove('assertionId')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property assertionId is missing'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]'
    }

    @Test
    void test_assertionIdTooShort() {
        annotation['assertionId'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for assertionId property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]/assertionId'
    }

    @Test
    void test_assertionIdTooLong() {
        annotation['assertionId'] = 'a' * 257
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '${'a' * 257}' for assertionId property is too long, maximum length 256"
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]/assertionId'
    }

    @Test
    void test_assertionReferenceIdMissing() {
        annotation.remove('assertionReferenceId')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_assertionReferenceIdTooShort() {
        annotation['assertionReferenceId'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for assertionReferenceId property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]/assertionReferenceId'
    }

    @Test
    void test_assertionReferenceIdLabelMissing() {
        annotation.remove('assertionReferenceIdLabel')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_assertionReferenceIdLabelTooShort() {
        annotation['assertionReferenceIdLabel'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for assertionReferenceIdLabel property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]/assertionReferenceIdLabel'
    }

    @Test
    void test_systemMissing() {
        annotation.remove('system')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_systemEmptyString() {
        annotation['system'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for system property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]/system'
    }

    @Test
    void test_creatorMissing() {
        annotation.remove('creator')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_creatorEmptyString() {
        annotation['creator'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for creator property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]/creator'
    }

    @Test
    void test_timeMissing() {
        annotation.remove('time')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_timeBadFormat() {
        annotation['time'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value 'a' for time property does not match the date-time format: [yyyy-MM-dd'T'HH:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}Z]"
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]/time'
    }

    @Test
    void test_annotationTypeMissing() {
        annotation.remove('annotationType')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property annotationType is missing'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]'
    }

    @Test
    void test_annotationTypeEmptyString() {
        annotation['annotationType'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for annotationType property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]/annotationType'
    }

    @Test
    void test_keyMissing() {
        annotation.remove('key')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_keyEmptyString() {
        annotation['key'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for key property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]/key'
    }

    @Test
    void test_valueMissing() {
        annotation.remove('value')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property value is missing'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]'
    }

    @Test
    void test_valueEmptyString() {
        annotation['value'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for value property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]/value'
    }

    @Test
    void test_itemActionMissing() {
        annotation.remove('itemAction')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_itemActionEmptyString() {
        annotation['itemAction'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for itemAction property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]/itemAction'
    }

    @Test
    void test_itemActionTimeMissing() {
        annotation.remove('itemActionTime')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_itemActionTimeBadFormat() {
        annotation['itemActionTime'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value 'a' for itemActionTime property does not match the date-time format: [yyyy-MM-dd'T'HH:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}Z]"
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]/itemActionTime'
    }

    @Test
    void test_securityTagMissing() {
        annotation.remove('securityTag')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property securityTag is missing'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/annotations[0]'
    }

    @Test
    void test_securityTagEmptyString() {
        annotation['securityTag'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }
}
