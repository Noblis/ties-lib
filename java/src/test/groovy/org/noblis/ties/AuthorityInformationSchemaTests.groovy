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

class AuthorityInformationSchemaTests {

    private Map<String, Object> authorityInformation
    private Map<String, Object> objectItem
    private Map<String, Object> ties

    @Before
    void setUp() {
        authorityInformation = [
                'collectionId': 'a',
                'collectionIdLabel': 'a',
                'collectionIdAlias': 'a',
                'collectionDescription': 'a',
                'subCollectionId': 'a',
                'subCollectionIdLabel': 'a',
                'subCollectionIdAlias': 'a',
                'subCollectionDescription': 'a',
                'registrationDate': '1970-01-01T00:00:00Z',
                'expirationDate': '1970-01-01T00:00:00Z',
                'owner': 'a',
                'securityTag': '',
        ]
        objectItem = [
                'objectId': 'a',
                'sha256Hash': 'a' * 64,
                'md5Hash': 'a' * 32,
                'authorityInformation': authorityInformation
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
        authorityInformation.remove('collectionId')
        authorityInformation.remove('collectionIdLabel')
        authorityInformation.remove('collectionIdAlias')
        authorityInformation.remove('collectionDescription')
        authorityInformation.remove('subCollectionId')
        authorityInformation.remove('subCollectionIdLabel')
        authorityInformation.remove('subCollectionIdAlias')
        authorityInformation.remove('subCollectionDescription')
        authorityInformation.remove('registrationDate')
        authorityInformation.remove('expirationDate')
        authorityInformation.remove('owner')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_allRequiredFieldsMissing() {
        authorityInformation.remove('securityTag')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property securityTag is missing'
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation'
    }

    @Test
    void test_additionalField() {
        authorityInformation['foo'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional property foo is not allowed'
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation'
    }

    @Test
    void test_additionalFields() {
        authorityInformation['foo'] = 'a'
        authorityInformation['bar'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'additional properties [bar, foo] are not allowed'
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation'
    }

    @Test
    void test_collectionIdMissing() {
        authorityInformation.remove('collectionId')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_collectionIdTooShort() {
        authorityInformation['collectionId'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for collectionId property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation/collectionId'
    }

    @Test
    void test_collectionIdLabelMissing() {
        authorityInformation.remove('collectionIdLabel')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_collectionIdLabelTooShort() {
        authorityInformation['collectionIdLabel'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for collectionIdLabel property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation/collectionIdLabel'
    }

    @Test
    void test_collectionIdAliasMissing() {
        authorityInformation.remove('collectionIdAlias')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_collectionIdAliasTooShort() {
        authorityInformation['collectionIdAlias'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for collectionIdAlias property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation/collectionIdAlias'
    }

    @Test
    void test_collectionDescriptionMissing() {
        authorityInformation.remove('collectionDescription')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_collectionDescriptionTooShort() {
        authorityInformation['collectionDescription'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for collectionDescription property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation/collectionDescription'
    }

    @Test
    void test_subCollectionIdMissing() {
        authorityInformation.remove('subCollectionId')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_subCollectionIdTooShort() {
        authorityInformation['subCollectionId'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for subCollectionId property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation/subCollectionId'
    }

    @Test
    void test_subCollectionIdLabelMissing() {
        authorityInformation.remove('subCollectionIdLabel')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_subCollectionIdLabelTooShort() {
        authorityInformation['subCollectionIdLabel'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for subCollectionIdLabel property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation/subCollectionIdLabel'
    }

    @Test
    void test_subCollectionIdAliasMissing() {
        authorityInformation.remove('subCollectionIdAlias')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_subCollectionIdAliasTooShort() {
        authorityInformation['subCollectionIdAlias'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for subCollectionIdAlias property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation/subCollectionIdAlias'
    }

    @Test
    void test_subCollectionDescriptionMissing() {
        authorityInformation.remove('subCollectionDescription')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_subCollectionDescriptionTooShort() {
        authorityInformation['subCollectionDescription'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for subCollectionDescription property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation/subCollectionDescription'
    }

    @Test
    void test_registrationDateMissing() {
        authorityInformation.remove('registrationDate')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_registrationDateBadFormat() {
        authorityInformation['registrationDate'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value 'a' for registrationDate property does not match the date-time format: [yyyy-MM-dd'T'HH:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}Z]"
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation/registrationDate'
    }

    @Test
    void test_expirationDateMissing() {
        authorityInformation.remove('expirationDate')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_expirationDateBadFormat() {
        authorityInformation['expirationDate'] = 'a'
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value 'a' for expirationDate property does not match the date-time format: [yyyy-MM-dd'T'HH:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}Z]"
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation/expirationDate'
    }

    @Test
    void test_ownerMissing() {
        authorityInformation.remove('owner')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }

    @Test
    void test_ownerTooShort() {
        authorityInformation['owner'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == "property value '' for owner property is too short, minimum length 1"
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation/owner'
    }

    @Test
    void test_securityTagMissing() {
        authorityInformation.remove('securityTag')
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 1
        assert validationExceptions[0].message == 'required property securityTag is missing'
        assert validationExceptions[0].location == '/objectItems[0]/authorityInformation'
    }

    @Test
    void test_securityTagTooShort() {
        authorityInformation['securityTag'] = ''
        List<ValidationException> validationExceptions = new TiesValidator().allErrors(ties)
        assert validationExceptions.size() == 0
    }
}
