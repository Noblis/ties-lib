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

package org.noblis.ties.data_binding

import groovy.json.JsonOutput
import org.junit.Test
import org.noblis.ties.test.SchemaTestTrait

class AssertionsTests implements SchemaTestTrait {

    private static Assertions getEmptyObject() {
        return new Assertions()
    }

    private static Assertions getCompleteObject() {
        return new Assertions(
                annotations: [new Annotation(annotationType: 'a', value: 'a', securityTag: 'a')],
                supplementalDescriptions: [
                        new SupplementalDescriptionDataFile(assertionId: 'a', informationType: 'a', sha256DataHash: 'a' * 64, dataSize: 0, securityTag: 'a'),
                        new SupplementalDescriptionDataObject(assertionId: 'a', informationType: 'a', dataObject: [:], securityTag: 'a')
                ])
    }

    private static String completeJson = '''\
{
  "annotations": [
    {
      "annotationType": "a",
      "value": "a",
      "securityTag": "a"
    }
  ],
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
}'''

    @Test
    void test_fromJson() {
        assert fromJson(completeJson, Assertions) == completeObject
    }

    @Test
    void test_toJson() {
        assert toJson(completeObject) == JsonOutput.prettyPrint(completeJson)
    }

    @Test
    void test_equals() {
        def o = emptyObject
        assert o.equals(o)

        assert emptyObject != new Object()

        assert emptyObject == emptyObject

        assert new Assertions(annotations: [new Annotation(assertionId: 'a')]) != new Assertions(annotations: [new Annotation(assertionId: 'b')])
        assert new Assertions(supplementalDescriptions: [new SupplementalDescriptionDataFile(assertionId: 'a')]) != new Assertions(supplementalDescriptions: [new SupplementalDescriptionDataFile(assertionId: 'b')])
    }

    @Test
    void test_hashCode() {
        assert emptyObject.hashCode() != new Object().hashCode()

        assert emptyObject.hashCode() == emptyObject.hashCode()

        assert new Assertions(annotations: [new Annotation(assertionId: 'a')]).hashCode() != new Assertions(annotations: [new Annotation(assertionId: 'b')]).hashCode()
        assert new Assertions(supplementalDescriptions: [new SupplementalDescriptionDataFile(assertionId: 'a')]).hashCode() != new Assertions(supplementalDescriptions: [new SupplementalDescriptionDataFile(assertionId: 'b')]).hashCode()
    }
}
