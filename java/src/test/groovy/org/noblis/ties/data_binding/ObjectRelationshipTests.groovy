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

class ObjectRelationshipTests implements SchemaTestTrait {

    private static ObjectRelationship getEmptyObject() {
        return new ObjectRelationship()
    }

    private static ObjectRelationship getCompleteObject() {
        return new ObjectRelationship(
                linkageMemberIds: ['a', 'b'],
                linkageDirectionality: ObjectRelationship.LinkageDirectionality.DIRECTED,
                linkageType: 'a',
                linkageAssertionId: 'a',
                otherInformation: [
                        new OtherInformation(key: 'a', value: 'a')
                ]
        )
    }

    private static String completeJson = '''\
{
    "linkageMemberIds": [
        "a",
        "b"
    ],
    "linkageDirectionality": "DIRECTED",
    "linkageType": "a",
    "linkageAssertionId": "a",
    "otherInformation": [
        {
            "key": "a",
            "value": "a"
        }
    ]
}'''

    @Test
    void test_fromJson() {
        assert fromJson(completeJson, ObjectRelationship) == completeObject
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

        assert new ObjectRelationship(linkageMemberIds: ['a', 'b']) != new ObjectRelationship(linkageMemberIds: ['c', 'd'])
        assert new ObjectRelationship(linkageDirectionality: ObjectRelationship.LinkageDirectionality.DIRECTED) != new ObjectRelationship(linkageDirectionality: ObjectRelationship.LinkageDirectionality.UNDIRECTED)
        assert new ObjectRelationship(linkageType: 'a') != new ObjectRelationship(linkageType: 'b')
        assert new ObjectRelationship(linkageAssertionId: 'a') != new ObjectRelationship(linkageAssertionId: 'b')
        assert new ObjectRelationship(otherInformation: [new OtherInformation(key: 'a')]) != new ObjectRelationship(otherInformation: [new OtherInformation(key: 'b')])
    }

    @Test
    void test_hashCode() {
        assert emptyObject.hashCode() != new Object().hashCode()

        assert emptyObject.hashCode() == emptyObject.hashCode()

        assert new ObjectRelationship(linkageMemberIds: ['a', 'b']).hashCode() != new ObjectRelationship(linkageMemberIds: ['c', 'd']).hashCode()
        assert new ObjectRelationship(linkageDirectionality: ObjectRelationship.LinkageDirectionality.DIRECTED).hashCode() != new ObjectRelationship(linkageDirectionality: ObjectRelationship.LinkageDirectionality.UNDIRECTED).hashCode()
        assert new ObjectRelationship(linkageType: 'a').hashCode() != new ObjectRelationship(linkageType: 'b').hashCode()
        assert new ObjectRelationship(linkageAssertionId: 'a').hashCode() != new ObjectRelationship(linkageAssertionId: 'b').hashCode()
        assert new ObjectRelationship(otherInformation: [new OtherInformation(key: 'a')]).hashCode() != new ObjectRelationship(otherInformation: [new OtherInformation(key: 'b')]).hashCode()
    }
}
