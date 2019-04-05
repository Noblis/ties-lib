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

import groovy.json.JsonOutput
import org.junit.Before
import org.junit.Test
import org.noblis.ties.TiesWriter
import org.noblis.ties.ValidationException
import org.noblis.ties.data_binding.Assertions
import org.noblis.ties.data_binding.AuthorityInformation
import org.noblis.ties.data_binding.ObjectItem
import org.noblis.ties.data_binding.OtherInformation
import org.noblis.ties.data_binding.SupplementalDescriptionDataFile
import org.noblis.ties.data_binding.SupplementalDescriptionDataObject
import org.noblis.ties.data_binding.Ties

class TiesWriterTests {

    private TiesWriter writer

    private static final Ties minimalValidTies = new Ties(
            version: '0.9',
            securityTag: 'a',
            objectItems: [
                    new ObjectItem(
                            objectId: 'a',
                            sha256Hash: 'a' * 64,
                            md5Hash: 'a' * 32,
                            authorityInformation: new AuthorityInformation(securityTag: 'a'))])
    private static final Ties minimalInvalidTies = new Ties()

    private static final String comparisonValidJson = JsonOutput.prettyPrint("""\
{
  "version": "0.9",
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
}""")
    private static final String comparisonInvalidJson = JsonOutput.prettyPrint("{}")

    @Before
    void setUp() {
        writer = new TiesWriter()
    }

    @Test
    void test_writeBytesValidValidation() {
        assert JsonOutput.prettyPrint(new String(writer.writeBytes(minimalValidTies))) == comparisonValidJson
    }

    @Test
    void test_writeBytesValidNoValidation() {
        assert JsonOutput.prettyPrint(new String(writer.writeBytes(minimalValidTies, false))) == comparisonValidJson
    }

    @Test(expected = ValidationException)
    void test_writeBytesInvalidValidation() {
        writer.writeBytes(minimalInvalidTies)
    }

    @Test
    void test_writeBytesInvalidNoValidation() {
        assert JsonOutput.prettyPrint(new String(writer.writeBytes(minimalInvalidTies, false))) == comparisonInvalidJson
    }

    @Test
    void test_writeStringValidValidation() {
        assert JsonOutput.prettyPrint(writer.writeString(minimalValidTies)) == comparisonValidJson
    }

    @Test
    void test_writeStringValidNoValidation() {
        assert JsonOutput.prettyPrint(writer.writeString(minimalValidTies, false)) == comparisonValidJson
    }

    @Test(expected = ValidationException)
    void test_writeStringInvalidValidation() {
        writer.writeString(minimalInvalidTies)
    }

    @Test
    void test_writeStringInvalidNoValidation() {
        assert JsonOutput.prettyPrint(writer.writeString(minimalInvalidTies, false)) == comparisonInvalidJson
    }

    @Test
    void test_writeFileValidValidation() {
        def file = File.createTempFile('test_writeFileValidValidation', '.json')
        file.deleteOnExit()
        writer.writeFile(minimalValidTies, file) == comparisonValidJson
        assert JsonOutput.prettyPrint(file.text) == comparisonValidJson
    }

    @Test
    void test_writeFileValidNoValidation() {
        def file = File.createTempFile('test_writeFileValidNoValidation', '.json')
        file.deleteOnExit()
        writer.writeFile(minimalValidTies, file, false)
        assert JsonOutput.prettyPrint(file.text) == comparisonValidJson
    }

    @Test(expected = ValidationException)
    void test_writeFileInvalidValidation() {
        def file = File.createTempFile('test_writeFileInvalidValidation', '.json')
        file.deleteOnExit()
        writer.writeFile(minimalInvalidTies, file)
    }

    @Test
    void test_writeFileInvalidNoValidation() {
        def file = File.createTempFile('test_writeFileInvalidNoValidation', '.json')
        file.deleteOnExit()
        writer.writeFile(minimalInvalidTies, file, false)
        assert JsonOutput.prettyPrint(file.text) == comparisonInvalidJson
    }

    @Test
    void test_writeOutputStreamValidValidation() {
        def outputStream = new ByteArrayOutputStream()
        writer.writeOutputStream(minimalValidTies, outputStream)
        assert JsonOutput.prettyPrint(outputStream.toString()) == comparisonValidJson
    }

    @Test
    void test_writeOutputStreamValidNoValidation() {
        def outputStream = new ByteArrayOutputStream()
        writer.writeOutputStream(minimalValidTies, outputStream, false)
        assert JsonOutput.prettyPrint(outputStream.toString()) == comparisonValidJson
    }

    @Test(expected = ValidationException)
    void test_writeOutputStreamInvalidValidation() {
        def outputStream = new ByteArrayOutputStream()
        writer.writeOutputStream(minimalInvalidTies, outputStream)
    }

    @Test
    void test_writeOutputStreamInvalidNoValidation() {
        def outputStream = new ByteArrayOutputStream()
        writer.writeOutputStream(minimalInvalidTies, outputStream, false)
        assert JsonOutput.prettyPrint(outputStream.toString()) == comparisonInvalidJson
    }

    @Test
    void test_dateTimeConversion() {
        def ties = new Ties(
                version: '0.9',
                time: new Date(0),
                securityTag: 'a',
                objectItems: [
                        new ObjectItem(
                                objectId: 'a',
                                sha256Hash: 'a' * 64,
                                md5Hash: 'a' * 32,
                                authorityInformation: new AuthorityInformation(securityTag: 'a'))])
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
  ]
}"""
        assert JsonOutput.prettyPrint(writer.writeString(ties, true)) == JsonOutput.prettyPrint(json)
    }

    @Test
    void test_otherInformationMultipleDataTypes() {
        def ties = new Ties(
                version: '0.9',
                time: new Date(0),
                securityTag: 'a',
                objectItems: [
                        new ObjectItem(
                                objectId: 'a',
                                sha256Hash: 'a' * 64,
                                md5Hash: 'a' * 32,
                                authorityInformation: new AuthorityInformation(securityTag: 'a'))],
                otherInformation: [
                        new OtherInformation(key: 'stringValue', value: 'a'),
                        new OtherInformation(key: 'integerValue', value: 1),
                        new OtherInformation(key: 'floatValue', value: 1.1F),
                        new OtherInformation(key: 'doubleValue', value: 1.1),
                        new OtherInformation(key: 'booleanValue', value: true)])
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
      "key": "floatValue",
      "value": 1.1
    },
    {
      "key": "doubleValue",
      "value": 1.1
    },
    {
      "key": "booleanValue",
      "value": true
    }
  ]
}"""
        assert JsonOutput.prettyPrint(writer.writeString(ties, true)) == JsonOutput.prettyPrint(json)
    }

    @Test
    void test_systemSupplementalDescriptionPolymorphic() {
        def ties = new Ties(
                version: '0.9',
                securityTag: 'a',
                objectItems: [
                        new ObjectItem(
                                objectId: 'a',
                                sha256Hash: 'a' * 64,
                                md5Hash: 'a' * 32,
                                authorityInformation: new AuthorityInformation(securityTag: 'a'),
                                objectAssertions: new Assertions(
                                        supplementalDescriptions: [
                                                new SupplementalDescriptionDataFile(
                                                        assertionId: 'a',
                                                        informationType: 'a',
                                                        sha256DataHash: 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa',
                                                        dataSize: 0, securityTag: 'a'),
                                                new SupplementalDescriptionDataObject(
                                                        assertionId: 'a',
                                                        informationType: 'a',
                                                        dataObject: [:],
                                                        securityTag: 'a')]))])
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
        assert JsonOutput.prettyPrint(writer.writeString(ties, true)) == JsonOutput.prettyPrint(json)
    }
}
