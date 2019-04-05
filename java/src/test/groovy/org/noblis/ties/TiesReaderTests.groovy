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
import org.noblis.ties.TiesReader
import org.noblis.ties.ValidationException
import org.noblis.ties.data_binding.AuthorityInformation
import org.noblis.ties.data_binding.ObjectItem
import org.noblis.ties.data_binding.SupplementalDescriptionDataFile
import org.noblis.ties.data_binding.SupplementalDescriptionDataObject
import org.noblis.ties.data_binding.Ties

class TiesReaderTests {

    private TiesReader reader

    private static final String minimalValidJson = """\
{
  "version": "0.9",
  "securityTag": "a",
  "objectItems": [
    {
      "objectId": "a",
      "sha256Hash": "${'a' * 64}",
      "md5Hash": "${'a' * 32}",
      "authorityInformation": {
        "securityTag":"a"
      }
    }
  ]
}"""
    private static final String minimalInvalidJson = '{}'

    private static final Ties comparisonValidTies = new Ties(
            version: '0.9',
            securityTag: 'a',
            objectItems: [
                    new ObjectItem(
                            objectId: 'a',
                            sha256Hash: 'a' * 64,
                            md5Hash: 'a' * 32,
                            authorityInformation: new AuthorityInformation(securityTag: 'a'))])
    private static final Ties comparisonInvalidTies = new Ties()

    @Before
    void setUp() {
        reader = new TiesReader()
    }

    @Test
    void test_readBytesValidValidation() {
        assert reader.read(minimalValidJson.bytes) == comparisonValidTies
    }

    @Test
    void test_readBytesValidNoValidation() {
        assert reader.read(minimalValidJson.bytes, false) == comparisonValidTies
    }

    @Test(expected = ValidationException)
    void test_readBytesInvalidValidation() {
        reader.read(minimalInvalidJson.bytes)
    }

    @Test
    void test_readBytesInvalidNoValidation() {
        assert reader.read(minimalInvalidJson.bytes, false) == comparisonInvalidTies
    }

    @Test
    void test_readStringValidValidation() {
        assert reader.read(minimalValidJson) == comparisonValidTies
    }

    @Test
    void test_readStringValidNoValidation() {
        assert reader.read(minimalValidJson, false) == comparisonValidTies
    }

    @Test(expected = ValidationException)
    void test_readStringInvalidValidation() {
        reader.read(minimalInvalidJson)
    }

    @Test
    void test_readStringInvalidNoValidation() {
        assert reader.read(minimalInvalidJson, false) == comparisonInvalidTies
    }

    @Test
    void test_readFileValidValidation() {
        def jsonFile = File.createTempFile('test_readFileValidValidation', '.json')
        jsonFile.deleteOnExit()
        jsonFile << minimalValidJson
        assert reader.read(jsonFile) == comparisonValidTies
    }

    @Test
    void test_readFileValidNoValidation() {
        def jsonFile = File.createTempFile('test_readFileValidNoValidation', '.json')
        jsonFile.deleteOnExit()
        jsonFile << minimalValidJson
        assert reader.read(jsonFile, false) == comparisonValidTies
    }

    @Test(expected = ValidationException)
    void test_readFileInvalidValidation() {
        def jsonFile = File.createTempFile('test_readFileInvalidValidation', '.json')
        jsonFile.deleteOnExit()
        jsonFile << minimalInvalidJson
        reader.read(jsonFile)
    }

    @Test
    void test_readFileInvalidNoValidation() {
        def jsonFile = File.createTempFile('test_readFileInvalidNoValidation', '.json')
        jsonFile.deleteOnExit()
        jsonFile << minimalInvalidJson
        assert reader.read(jsonFile, false) == comparisonInvalidTies
    }

    @Test
    void test_readInputStreamValidValidation() {
        assert reader.read(new ByteArrayInputStream(minimalValidJson.bytes)) == comparisonValidTies
    }

    @Test
    void test_readInputStreamValidNoValidation() {
        assert reader.read(new ByteArrayInputStream(minimalValidJson.bytes), false) == comparisonValidTies
    }

    @Test(expected = ValidationException)
    void test_readInputStreamInvalidValidation() {
        reader.read(new ByteArrayInputStream(minimalInvalidJson.bytes))
    }

    @Test
    void test_readInputStreamInvalidNoValidation() {
        assert reader.read(new ByteArrayInputStream(minimalInvalidJson.bytes), false) == comparisonInvalidTies
    }

    @Test
    void test_dateTimeConversion() {
        def json = """\
{
  "version": "0.9",
  "time": "1970-01-01T00:00:00Z",
  "securityTag": "a",
  "objectItems": [
    {
      "objectId": "a",
      "sha256Hash": "${'a' * 64}",
      "md5Hash": "${'a' * 32}",
      "authorityInformation": {
        "securityTag": "a"
      }
    }
  ]
}"""
        assert reader.read(json, true).time == new Date(0)

        json = """\
{
  "version": "0.9",
  "time": "1970-01-01T00:00:00.000Z",
  "securityTag": "a",
  "objectItems": [
    {
      "objectId": "a",
      "sha256Hash": "${'a' * 64}",
      "md5Hash": "${'a' * 32}",
      "authorityInformation": {
        "securityTag": "a"
      }
    }
  ]
}"""
        assert reader.read(json, true).time == new Date(0)
    }

    @Test
    void test_otherInformationMultipleDataTypes() {
        def json = """\
{
  "version": "0.9",
  "time": "1970-01-01T00:00:00.000Z",
  "securityTag": "a",
  "objectItems": [
    {
      "objectId": "a",
      "sha256Hash": "${'a' * 64}",
      "md5Hash": "${'a' * 32}",
      "authorityInformation": {
        "securityTag": "a"
      }
    }
  ],
  "otherInformation": [
    {
      "key": "stringValue",
      "value": "a"
    },
    {
      "key": "integerValue",
      "value": 1
    },
    {
      "key": "numberValue",
      "value": 1.1
    },
    {
      "key": "booleanValue",
      "value": true
    }
  ]
}"""
        assert reader.read(json, true).otherInformation[0].value == 'a'
        assert reader.read(json, true).otherInformation[1].value == 1
        assert reader.read(json, true).otherInformation[2].value == 1.1
        assert reader.read(json, true).otherInformation[3].value == true
    }

    @Test
    void test_supplementalDescriptionPolymorphic() {
        def json = """\
{
  "version": "0.9",
  "securityTag": "a",
  "objectItems": [
    {
      "objectId": "a",
      "sha256Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "md5Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "authorityInformation": {
        "securityTag": "a"
      },
      "objectAssertions": {
        "supplementalDescriptions": [
          {
            "assertionId": "a",
            "informationType": "a",
            "sha256DataHash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "dataSize": 0,
            "securityTag": "a"
          },
          {
            "assertionId": "a",
            "informationType": "a",
            "dataObject": {},
            "securityTag": "a"
          }
        ]
      }
    }
  ]
}"""
        assert reader.read(json, true).objectItems[0].objectAssertions.supplementalDescriptions[0] == new SupplementalDescriptionDataFile(assertionId: 'a', informationType: 'a', sha256DataHash: 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', dataSize: 0, securityTag: 'a')
        assert reader.read(json, true).objectItems[0].objectAssertions.supplementalDescriptions[1] == new SupplementalDescriptionDataObject(assertionId: 'a', informationType: 'a', dataObject: [:], securityTag: 'a')
    }
}
