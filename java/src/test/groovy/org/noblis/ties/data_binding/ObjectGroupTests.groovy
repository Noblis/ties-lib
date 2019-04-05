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

class ObjectGroupTests implements SchemaTestTrait {

    private static ObjectGroup getEmptyObject() {
        return new ObjectGroup()
    }

    private static ObjectGroup getCompleteObject() {
        return new ObjectGroup(
                groupId: 'a',
                groupType: 'a',
                groupDescription: 'a',
                groupMemberIds: ['a'],
                groupAssertions: [:],
                otherInformation: [
                        new OtherInformation(key: 'a', value: 'a')
                ]
        )
    }

    private static String completeJson = '''\
{
    "groupId": "a",
    "groupType": "a",
    "groupDescription": "a",
    "groupMemberIds": [
        "a"
    ],
    "groupAssertions": {
        
    },
    "otherInformation": [
        {
            "key": "a",
            "value": "a"
        }
    ]
}'''

    @Test
    void test_fromJson() {
        assert fromJson(completeJson, ObjectGroup) == completeObject
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

        assert new ObjectGroup(groupId: 'a') != new ObjectGroup(groupId: 'b')
        assert new ObjectGroup(groupType: 'a') != new ObjectGroup(groupType: 'b')
        assert new ObjectGroup(groupDescription: 'a') != new ObjectGroup(groupDescription: 'b')
        assert new ObjectGroup(groupMemberIds: ['a']) != new ObjectGroup(groupMemberIds: ['b'])
        assert new ObjectGroup(groupAssertions: new Assertions(annotations: [])) != new ObjectGroup(groupAssertions: new Assertions(supplementalDescriptions: []))
        assert new ObjectGroup(otherInformation: [new OtherInformation(key: 'a', value: 'a')]) != new ObjectGroup(otherInformation: [new OtherInformation(key: 'b', value: 'b')])
    }

    @Test
    void test_hashCode() {
        assert emptyObject.hashCode() != new Object().hashCode()

        assert emptyObject.hashCode() == emptyObject.hashCode()

        assert new ObjectGroup(groupId: 'a').hashCode() != new ObjectGroup(groupId: 'b').hashCode()
        assert new ObjectGroup(groupType: 'a').hashCode() != new ObjectGroup(groupType: 'b').hashCode()
        assert new ObjectGroup(groupDescription: 'a').hashCode() != new ObjectGroup(groupDescription: 'b').hashCode()
        assert new ObjectGroup(groupMemberIds: ['a']).hashCode() != new ObjectGroup(groupMemberIds: ['b']).hashCode()
        assert new ObjectGroup(groupAssertions: new Assertions(annotations: [])).hashCode() != new ObjectGroup(groupAssertions: new Assertions(supplementalDescriptions: [])).hashCode()
        assert new ObjectGroup(otherInformation: [new OtherInformation(key: 'a', value: 'a')]).hashCode() != new ObjectGroup(otherInformation: [new OtherInformation(key: 'b', value: 'b')]).hashCode()
    }
}
