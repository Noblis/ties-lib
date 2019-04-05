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

class SupplementalDescriptionDataFileSchemaTests {

    private Map<String, Object> supplementalDescription
    private Map<String, Object> objectItem
    private Map<String, Object> ties

    @Before
    void setUp() {
        supplementalDescription = [
                'assertionId': 'a',
                'assertionReferenceId': 'a',
                'assertionReferenceIdLabel': 'a',
                'system': 'a',
                'informationType': 'a',
                'sha256DataHash': 'a' * 64,
                'dataSize': 0,
                'dataRelativeUri': 'a',
                'securityTag': ''
        ]
        objectItem = [
                'objectId': 'a',
                'sha256Hash': 'a' * 64,
                'md5Hash': 'a' * 32,
                'authorityInformation': ['securityTag': ''],
                'objectAssertions': ['supplementalDescriptions': [supplementalDescription]]
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
        supplementalDescription.remove('assertionReferenceId')
        supplementalDescription.remove('assertionReferenceIdLabel')
        supplementalDescription.remove('system')
        supplementalDescription.remove('dataRelativeUri')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_allRequiredFieldsMissing() {
        supplementalDescription.remove('assertionId')
        supplementalDescription.remove('informationType')
        supplementalDescription.remove('sha256DataHash')
        supplementalDescription.remove('dataSize')
        supplementalDescription.remove('securityTag')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'required properties [assertionId, dataSize, informationType, securityTag, sha256DataHash] are missing'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'additional property dataRelativeUri is not allowed'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'required properties [assertionId, dataObject, informationType, securityTag] are missing'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[2].causes.size() == 0

    }

    @Test
    void test_additionalField() {
        supplementalDescription['foo'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional property foo is not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'additional properties [dataRelativeUri, dataSize, foo, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_additionalFields() {
        supplementalDescription['foo'] = 'a'
        supplementalDescription['bar'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [bar, foo] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'additional properties [bar, dataRelativeUri, dataSize, foo, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_assertionIdMissing() {
        supplementalDescription.remove('assertionId')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'required property assertionId is missing'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'required properties [assertionId, dataObject] are missing'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_assertionIdNull() {
        supplementalDescription['assertionId'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'property assertionId with null value should be of type string'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_assertionIdTooShort() {
        supplementalDescription['assertionId'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == "property value '' for assertionId property is too short, minimum length 1"
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_assertionIdTooLong() {
        supplementalDescription['assertionId'] = 'a' * 257
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == "property value '${'a' * 257}' for assertionId property is too long, maximum length 256"
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_assertionReferenceIdMissing() {
        supplementalDescription.remove('assertionReferenceId')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_assertionReferenceIdNull() {
        supplementalDescription['assertionReferenceId'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'property assertionReferenceId with null value should be of type string'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionReferenceId'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_assertionReferenceIdTooShort() {
        supplementalDescription['assertionReferenceId'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == "property value '' for assertionReferenceId property is too short, minimum length 1"
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionReferenceId'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_assertionReferenceIdLabelMissing() {
        supplementalDescription.remove('assertionReferenceIdLabel')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_assertionReferenceIdLabelNull() {
        supplementalDescription['assertionReferenceIdLabel'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'property assertionReferenceIdLabel with null value should be of type string'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionReferenceIdLabel'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_assertionReferenceIdLabelTooShort() {
        supplementalDescription['assertionReferenceIdLabel'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == "property value '' for assertionReferenceIdLabel property is too short, minimum length 1"
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionReferenceIdLabel'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_systemMissing() {
        supplementalDescription.remove('system')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_systemNull() {
        supplementalDescription['system'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'property system with null value should be of type string'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/system'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_systemTooShort() {
        supplementalDescription['system'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == "property value '' for system property is too short, minimum length 1"
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/system'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_informationTypeMissing() {
        supplementalDescription.remove('informationType')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'required property informationType is missing'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'required properties [dataObject, informationType] are missing'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_informationTypeNull() {
        supplementalDescription['informationType'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'property informationType with null value should be of type string'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/informationType'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_informationTypeTooShort() {
        supplementalDescription['informationType'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == "property value '' for informationType property is too short, minimum length 1"
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/informationType'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_sha256DataHashMissing() {
        supplementalDescription.remove('sha256DataHash')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'required property sha256DataHash is missing'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'additional properties [dataRelativeUri, dataSize] are not allowed'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_sha256DataHashNull() {
        supplementalDescription['sha256DataHash'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'property sha256DataHash with null value should be of type string'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/sha256DataHash'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_sha256DataHashTooShort() {
        supplementalDescription['sha256DataHash'] = 'a' * 63
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 4
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256DataHash property is too short, minimum length 64"
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/sha256DataHash'
        assert validationExceptions[0].causes[2].causes.size() == 0
        assert validationExceptions[0].causes[3].message == "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256DataHash property does not match the pattern '^[a-fA-F0-9]{64}\$'"
        assert validationExceptions[0].causes[3].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/sha256DataHash'
        assert validationExceptions[0].causes[3].causes.size() == 0
    }

    @Test
    void test_sha256DataHashTooLong() {
        supplementalDescription['sha256DataHash'] = 'a' * 65
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 4
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256DataHash property is too long, maximum length 64"
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/sha256DataHash'
        assert validationExceptions[0].causes[2].causes.size() == 0
        assert validationExceptions[0].causes[3].message == "property value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' for sha256DataHash property does not match the pattern '^[a-fA-F0-9]{64}\$'"
        assert validationExceptions[0].causes[3].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/sha256DataHash'
        assert validationExceptions[0].causes[3].causes.size() == 0
    }

    @Test
    void test_sha256DataHashBadFormat() {
        supplementalDescription['sha256DataHash'] = 'z' * 64
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == "property value 'zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz' for sha256DataHash property does not match the pattern '^[a-fA-F0-9]{64}\$'"
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/sha256DataHash'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_dataSizeMissing() {
        supplementalDescription.remove('dataSize')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'required property dataSize is missing'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'additional properties [dataRelativeUri, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_dataSizeNull() {
        supplementalDescription['dataSize'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'property dataSize with null value should be of type integer'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/dataSize'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_dataSizeTooSmall() {
        supplementalDescription['dataSize'] = -1
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'property value -1 for dataSize property is less than the minimum value of 0'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/dataSize'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_dataRelativeUriMissing() {
        supplementalDescription.remove('dataRelativeUri')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_dataRelativeUriNull() {
        supplementalDescription['dataRelativeUri'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'property dataRelativeUri with null value should be of type string'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/dataRelativeUri'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_dataRelativeUriTooShort() {
        supplementalDescription['dataRelativeUri'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == "property value '' for dataRelativeUri property is too short, minimum length 1"
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/dataRelativeUri'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_securityTagMissing() {
        supplementalDescription.remove('securityTag')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'required property securityTag is missing'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'required properties [dataObject, securityTag] are missing'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_securityTagNull() {
        supplementalDescription['securityTag'] = null
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'content for array property at index 0 in supplementalDescriptions does not match any of the possible schema definitions'
        assert validationExceptions[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes.size() == 3
        assert validationExceptions[0].causes[0].message == 'additional properties [dataRelativeUri, dataSize, sha256DataHash] are not allowed'
        assert validationExceptions[0].causes[0].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[0].causes.size() == 0
        assert validationExceptions[0].causes[1].message == 'required property dataObject is missing'
        assert validationExceptions[0].causes[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]'
        assert validationExceptions[0].causes[1].causes.size() == 0
        assert validationExceptions[0].causes[2].message == 'property securityTag with null value should be of type string'
        assert validationExceptions[0].causes[2].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/securityTag'
        assert validationExceptions[0].causes[2].causes.size() == 0
    }

    @Test
    void test_securityTagEmptyString() {
        supplementalDescription['securityTag'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }
}
