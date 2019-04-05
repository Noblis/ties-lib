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

class AnnotationTests implements SchemaTestTrait {

    private static Annotation getEmptyObject() {
        return new Annotation()
    }

    private static Annotation getCompleteObject() {
        return new Annotation(
                assertionId: 'a',
                assertionReferenceId: 'a',
                assertionReferenceIdLabel: 'a',
                system: 'a',
                creator: 'a',
                time: new Date(0),
                annotationType: 'a',
                key: 'a',
                value: 'a',
                itemAction: 'a',
                itemActionTime: new Date(0),
                securityTag: 'a'
        )
    }

    private static String completeJson = '''\
{
    "assertionId": "a",
    "assertionReferenceId": "a",
    "assertionReferenceIdLabel": "a",
    "system": "a",
    "creator": "a",
    "time": "1970-01-01T00:00:00.000Z",
    "annotationType": "a",
    "key": "a",
    "value": "a",
    "itemAction": "a",
    "itemActionTime": "1970-01-01T00:00:00.000Z",
    "securityTag": "a"
}'''

    @Test
    void test_fromJson() {
        assert fromJson(completeJson, Annotation) == completeObject
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

        assert new Annotation(assertionId: 'a') != new Annotation(assertionId: 'b')
        assert new Annotation(assertionReferenceId: 'a') != new Annotation(assertionReferenceId: 'b')
        assert new Annotation(assertionReferenceIdLabel: 'a') != new Annotation(assertionReferenceIdLabel: 'b')
        assert new Annotation(system: 'a') != new Annotation(system: 'b')
        assert new Annotation(creator: 'a') != new Annotation(creator: 'b')
        assert new Annotation(time: new Date(0)) != new Annotation(time: new Date(1))
        assert new Annotation(annotationType: 'a') != new Annotation(annotationType: 'b')
        assert new Annotation(key: 'a') != new Annotation(key: 'b')
        assert new Annotation(value: 'a') != new Annotation(value: 'b')
        assert new Annotation(itemAction: 'a') != new Annotation(itemAction: 'b')
        assert new Annotation(itemActionTime: new Date(0)) != new Annotation(itemActionTime: new Date(1))
        assert new Annotation(securityTag: 'a') != new Annotation(securityTag: 'b')
    }

    @Test
    void test_hashCode() {
        assert emptyObject.hashCode() != new Object().hashCode()

        assert emptyObject.hashCode() == emptyObject.hashCode()

        assert new Annotation(assertionId: 'a').hashCode() != new Annotation(assertionId: 'b').hashCode()
        assert new Annotation(assertionReferenceId: 'a').hashCode() != new Annotation(assertionReferenceId: 'b').hashCode()
        assert new Annotation(assertionReferenceIdLabel: 'a').hashCode() != new Annotation(assertionReferenceIdLabel: 'b').hashCode()
        assert new Annotation(system: 'a').hashCode() != new Annotation(system: 'b').hashCode()
        assert new Annotation(creator: 'a').hashCode() != new Annotation(creator: 'b').hashCode()
        assert new Annotation(time: new Date(0)).hashCode() != new Annotation(time: new Date(1)).hashCode()
        assert new Annotation(annotationType: 'a').hashCode() != new Annotation(annotationType: 'b').hashCode()
        assert new Annotation(key: 'a').hashCode() != new Annotation(key: 'b').hashCode()
        assert new Annotation(value: 'a').hashCode() != new Annotation(value: 'b').hashCode()
        assert new Annotation(itemAction: 'a').hashCode() != new Annotation(itemAction: 'b').hashCode()
        assert new Annotation(itemActionTime: new Date(0)).hashCode() != new Annotation(itemActionTime: new Date(1)).hashCode()
        assert new Annotation(securityTag: 'a').hashCode() != new Annotation(securityTag: 'b').hashCode()
    }
}
