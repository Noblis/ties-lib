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

class SupplementalDescriptionDataFileTests implements SchemaTestTrait {

    private static SupplementalDescriptionDataFile getEmptyObject() {
        return new SupplementalDescriptionDataFile()
    }

    private static SupplementalDescriptionDataFile getCompleteObject() {
        return new SupplementalDescriptionDataFile(
                assertionId: 'a',
                assertionReferenceId: 'a',
                assertionReferenceIdLabel: 'a',
                system: 'a',
                informationType: 'a',
                sha256DataHash: 'a' * 64,
                dataSize: 0,
                dataRelativeUri: 'a',
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
    "sha256DataHash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
    "dataSize": 0,
    "dataRelativeUri": "a",
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

        assert new SupplementalDescriptionDataFile(assertionId: 'a') != new   SupplementalDescriptionDataFile(assertionId: 'b')
        assert new SupplementalDescriptionDataFile(assertionReferenceId: 'a') != new   SupplementalDescriptionDataFile(assertionReferenceId: 'b')
        assert new SupplementalDescriptionDataFile(assertionReferenceIdLabel: 'a') != new   SupplementalDescriptionDataFile(assertionReferenceIdLabel: 'b')
        assert new SupplementalDescriptionDataFile(system: 'a') != new   SupplementalDescriptionDataFile(system: 'b')
        assert new SupplementalDescriptionDataFile(informationType: 'a') != new   SupplementalDescriptionDataFile(informationType: 'b')
        assert new SupplementalDescriptionDataFile(sha256DataHash: 'a') != new   SupplementalDescriptionDataFile(sha256DataHash: 'b')
        assert new SupplementalDescriptionDataFile(dataSize: 0L) != new   SupplementalDescriptionDataFile(dataSize: 1L)
        assert new SupplementalDescriptionDataFile(dataRelativeUri: 'a') != new   SupplementalDescriptionDataFile(dataRelativeUri: 'b')
        assert new SupplementalDescriptionDataFile(securityTag: 'a') != new   SupplementalDescriptionDataFile(securityTag: 'b')
    }

    @Test
    void test_hashCode() {
        assert emptyObject.hashCode() != new Object().hashCode()

        assert emptyObject.hashCode() == emptyObject.hashCode()

        assert new   SupplementalDescriptionDataFile(assertionId: 'a').hashCode() != new   SupplementalDescriptionDataFile(assertionId: 'b').hashCode()
        assert new   SupplementalDescriptionDataFile(assertionReferenceId: 'a').hashCode() != new   SupplementalDescriptionDataFile(assertionReferenceId: 'b').hashCode()
        assert new   SupplementalDescriptionDataFile(assertionReferenceIdLabel: 'a').hashCode() != new   SupplementalDescriptionDataFile(assertionReferenceIdLabel: 'b').hashCode()
        assert new   SupplementalDescriptionDataFile(system: 'a').hashCode() != new   SupplementalDescriptionDataFile(system: 'b').hashCode()
        assert new   SupplementalDescriptionDataFile(informationType: 'a').hashCode() != new   SupplementalDescriptionDataFile(informationType: 'b').hashCode()
        assert new   SupplementalDescriptionDataFile(sha256DataHash: 'a').hashCode() != new   SupplementalDescriptionDataFile(sha256DataHash: 'b').hashCode()
        assert new   SupplementalDescriptionDataFile(dataSize: 0L).hashCode() != new   SupplementalDescriptionDataFile(dataSize: 1L).hashCode()
        assert new   SupplementalDescriptionDataFile(dataRelativeUri: 'a').hashCode() != new   SupplementalDescriptionDataFile(dataRelativeUri: 'b').hashCode()
        assert new   SupplementalDescriptionDataFile(securityTag: 'a').hashCode() != new   SupplementalDescriptionDataFile(securityTag: 'b').hashCode()
    }
}
