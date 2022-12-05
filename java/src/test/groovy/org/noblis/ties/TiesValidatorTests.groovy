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

class TiesValidatorTests {

    private static final String minimalValidJson = "{\"version\":\"1.0\",\"authorityInformation\":{\"securityTag\":\"a\"},\"objectItems\":[{\"objectId\":\"a\",\"sha256Hash\":\"${'a' * 64}\",\"md5Hash\":\"${'a' * 32}\",\"authorityInformation\":{\"securityTag\":\"a\"}}]}"
    private static final String minimalInvalidJson = '{}'

    private TiesValidator validator

    @Before
    void setUp() {
        validator = new TiesValidator()
    }

    @Test
    void test_validateByteArrayValid() {
        validator.validate(minimalValidJson.bytes)
    }

    @Test(expected = ValidationException)
    void test_validateByteArrayInvalid() {
        validator.validate(minimalInvalidJson.bytes)
    }

    @Test
    void test_validateStringValid() {
        validator.validate(minimalValidJson)
    }

    @Test(expected = ValidationException)
    void test_validateStringInvalid() {
        validator.validate(minimalInvalidJson)
    }

    @Test
    void test_validateFileValid() {
        def jsonFile = File.createTempFile('test_validateFileValid', '.json')
        jsonFile.deleteOnExit()
        jsonFile << minimalValidJson
        validator.validate(jsonFile)
    }

    @Test(expected = ValidationException)
    void test_validateFileInvalid() {
        def jsonFile = File.createTempFile('test_validateFileInvalid', '.json')
        jsonFile.deleteOnExit()
        jsonFile << minimalInvalidJson
        validator.validate(jsonFile)
    }

    @Test
    void test_validateInputStreamValid() {
        validator.validate(new ByteArrayInputStream(minimalValidJson.bytes))
    }

    @Test(expected = ValidationException)
    void test_validateInputStreamInvalid() {
        validator.validate(new ByteArrayInputStream(minimalInvalidJson.bytes))
    }

    @Test
    void test_validateExampleByteArray() {
        byte[] json = this.class.getResourceAsStream('/examples/example-export.json').bytes
        validator.validate(json)
    }

    @Test
    void test_validateExampleString() {
        String json = this.class.getResourceAsStream('/examples/example-export.json').text
        validator.validate(json)
    }

    @Test
    void test_validateExampleFile() {
        File json = File.createTempFile('test_validateExampleFile', '.json')
        json.deleteOnExit()
        json << this.class.getResourceAsStream('/examples/example-export.json')
        validator.validate(json)
    }

    @Test
    void test_validateExampleInputStream() {
        validator.validate(this.class.getResourceAsStream('/examples/example-export.json'))
    }
}
