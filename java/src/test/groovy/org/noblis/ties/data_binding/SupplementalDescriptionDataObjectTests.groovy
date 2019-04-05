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

class SupplementalDescriptionDataObjectTests implements SchemaTestTrait {

    private static SupplementalDescriptionDataObject getEmptyObject() {
        return new SupplementalDescriptionDataObject()
    }

    private static SupplementalDescriptionDataObject getCompleteObject() {
        return new SupplementalDescriptionDataObject(
                assertionId: 'a',
                assertionReferenceId: 'a',
                assertionReferenceIdLabel: 'a',
                system: 'a',
                informationType: 'a',
                dataObject: [:],
                securityTag: 'a'
        )
    }

    private static String completeJson = '''\
{
    "assertionId": "a",
    "assertionReferenceId": "a",
    "assertionReferenceIdLabel": "a",
    "system": "a",
    "informationType": "a",
    "dataObject": {},
    "securityTag": "a"
}'''

    @Test
    void test_fromJson() {
        assert fromJson(completeJson, SupplementalDescription) == completeObject
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

        assert new SupplementalDescriptionDataObject(assertionId: 'a') != new SupplementalDescriptionDataObject(assertionId: 'b')
        assert new SupplementalDescriptionDataObject(assertionReferenceId: 'a') != new SupplementalDescriptionDataObject(assertionReferenceId: 'b')
        assert new SupplementalDescriptionDataObject(assertionReferenceIdLabel: 'a') != new SupplementalDescriptionDataObject(assertionReferenceIdLabel: 'b')
        assert new SupplementalDescriptionDataObject(system: 'a') != new SupplementalDescriptionDataObject(system: 'b')
        assert new SupplementalDescriptionDataObject(informationType: 'a') != new SupplementalDescriptionDataObject(informationType: 'b')
        assert new SupplementalDescriptionDataObject(dataObject: ['a': 'a']) != new SupplementalDescriptionDataObject(dataObject: ['b': 'b'])
        assert new SupplementalDescriptionDataObject(securityTag: 'a') != new SupplementalDescriptionDataObject(securityTag: 'b')
    }

    @Test
    void test_hashCode() {
        assert emptyObject.hashCode() != new Object().hashCode()

        assert emptyObject.hashCode() == emptyObject.hashCode()

        assert new SupplementalDescriptionDataObject(assertionId: 'a').hashCode() != new SupplementalDescriptionDataObject(assertionId: 'b').hashCode()
        assert new SupplementalDescriptionDataObject(assertionReferenceId: 'a').hashCode() != new SupplementalDescriptionDataObject(assertionReferenceId: 'b').hashCode()
        assert new SupplementalDescriptionDataObject(assertionReferenceIdLabel: 'a').hashCode() != new SupplementalDescriptionDataObject(assertionReferenceIdLabel: 'b').hashCode()
        assert new SupplementalDescriptionDataObject(system: 'a').hashCode() != new SupplementalDescriptionDataObject(system: 'b').hashCode()
        assert new SupplementalDescriptionDataObject(informationType: 'a').hashCode() != new SupplementalDescriptionDataObject(informationType: 'b').hashCode()
        assert new SupplementalDescriptionDataObject(dataObject: ['a': null]).hashCode() != new SupplementalDescriptionDataObject(dataObject: ['b': null]).hashCode()
        assert new SupplementalDescriptionDataObject(securityTag: 'a').hashCode() != new SupplementalDescriptionDataObject(securityTag: 'b').hashCode()
    }
}
