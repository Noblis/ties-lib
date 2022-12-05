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

class TiesTests implements SchemaTestTrait {

    private static Ties getEmptyObject() {
        return new Ties()
    }

    private static Ties getCompleteObject() {
        return new Ties(
                version: '1.0',
                id: 'a',
                system: 'a',
                organization: 'a',
                time: new Date(0),
                description: 'a',
                type: 'a',
                authorityInformation: new AuthorityInformation(securityTag: 'a'),
                objectItems: [
                        new ObjectItem(
                                objectId: 'a',
                                sha256Hash: 'a' * 64,
                                md5Hash: 'a' * 32,
                                authorityInformation: new AuthorityInformation(securityTag: 'a'))
                ],
                objectGroups: [
                        new ObjectGroup(
                                groupId: 'a',
                                groupType: 'a',
                                groupMemberIds: [])
                ],
                objectRelationships: [
                        new ObjectRelationship(
                                'linkageMemberIds': ['a', 'a'],
                                'linkageDirectionality': 'UNDIRECTED')
                ],
                otherInformation: [
                        new OtherInformation(
                                key: 'a',
                                value: 'a')
                ]
        )
    }

    private static String completeJson = '''\
{
    "version": "1.0",
    "id": "a",
    "system": "a",
    "organization": "a",
    "time": "1970-01-01T00:00:00.000Z",
    "description": "a",
    "type": "a",
    "authorityInformation": {
        "securityTag": "a"
    },
    "objectItems": [
        {
            "objectId": "a",
            "sha256Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "md5Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "authorityInformation": {
                "securityTag": "a"
            }
        }
    ],
    "objectGroups": [
        {
            "groupId": "a",
            "groupType": "a",
            "groupMemberIds": [

            ]
        }
    ],
    "objectRelationships": [
        {
            "linkageMemberIds": [
                "a",
                "a"
            ],
            "linkageDirectionality": "UNDIRECTED"
        }
    ],
    "otherInformation": [
        {
            "key": "a",
            "value": "a"
        }
    ]
}'''

    @Test
    void test_fromJson() {
        assert fromJson(completeJson, Ties) == completeObject
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

        assert new Ties(version: 'a') != new Ties(version: 'b')
        assert new Ties(id: 'a') != new Ties(id: 'b')
        assert new Ties(system: 'a') != new Ties(system: 'b')
        assert new Ties(organization: 'a') != new Ties(organization: 'b')
        assert new Ties(time: new Date(0)) != new Ties(time: new Date(1))
        assert new Ties(description: 'a') != new Ties(description: 'b')
        assert new Ties(type: 'a') != new Ties(type: 'b')
        assert new Ties(authorityInformation: new AuthorityInformation(securityTag: 'a')) != new Ties(authorityInformation: new AuthorityInformation(securityTag: 'b'))
        assert new Ties(objectItems: [new ObjectItem(objectId: 'a', sha256Hash: 'a' * 64, md5Hash: 'a' * 32, authorityInformation: new AuthorityInformation(securityTag: 'a'))]) != new Ties(objectItems: [new ObjectItem(sha256Hash: 'b' * 64, md5Hash: 'b' * 32, authorityInformation: new AuthorityInformation(securityTag: 'a'))])
        assert new Ties(objectGroups: [new ObjectGroup(groupId: 'a', groupType: 'a', groupMemberIds: [])]) != new Ties(objectGroups: [new ObjectGroup(groupId: 'b', groupType: 'b', groupMemberIds: [])])
        assert new Ties(objectRelationships: [new ObjectRelationship('linkageMemberIds': ['a', 'a'], 'linkageDirectionality': 'UNDIRECTED')]) != new Ties(objectRelationships: [new ObjectRelationship('linkageMemberIds': ['b', 'b'], 'linkageDirectionality': 'UNDIRECTED')])
        assert new Ties(otherInformation: [new OtherInformation(key: 'a', value: 'a')]) != new Ties(otherInformation: [new OtherInformation(key: 'b', value: 'b')])
    }

    @Test
    void test_hashCode() {
        assert emptyObject.hashCode() != new Object().hashCode()

        assert emptyObject.hashCode() == emptyObject.hashCode()

        assert new Ties(version: 'a').hashCode() != new Ties(version: 'b').hashCode()
        assert new Ties(id: 'a').hashCode() != new Ties(id: 'b').hashCode()
        assert new Ties(system: 'a').hashCode() != new Ties(system: 'b').hashCode()
        assert new Ties(organization: 'a').hashCode() != new Ties(organization: 'b').hashCode()
        assert new Ties(time: new Date(0)).hashCode() != new Ties(time: new Date(1)).hashCode()
        assert new Ties(description: 'a').hashCode() != new Ties(description: 'b').hashCode()
        assert new Ties(type: 'a').hashCode() != new Ties(type: 'b').hashCode()
        assert new Ties(authorityInformation: new AuthorityInformation(securityTag: 'a')).hashCode() != new Ties(authorityInformation: new AuthorityInformation(securityTag: 'b')).hashCode()
        assert new Ties(objectItems: [new ObjectItem(objectId: 'a', sha256Hash: 'a' * 64, md5Hash: 'a' * 32, authorityInformation: new AuthorityInformation(securityTag: 'a'))]).hashCode() != new Ties(objectItems: [new ObjectItem(sha256Hash: 'b' * 64, md5Hash: 'b' * 32, authorityInformation: new AuthorityInformation(securityTag: 'a'))]).hashCode()
        assert new Ties(objectGroups: [new ObjectGroup(groupId: 'a', groupType: 'a', groupMemberIds: [])]).hashCode() != new Ties(objectGroups: [new ObjectGroup(groupId: 'b', groupType: 'b', groupMemberIds: [])]).hashCode()
        assert new Ties(objectRelationships: [new ObjectRelationship('linkageMemberIds': ['a', 'a'], 'linkageDirectionality': 'UNDIRECTED')]).hashCode() != new Ties(objectRelationships: [new ObjectRelationship('linkageMemberIds': ['b', 'b'], 'linkageDirectionality': 'UNDIRECTED')]).hashCode()
        assert new Ties(otherInformation: [new OtherInformation(key: 'a', value: 'a')]).hashCode() != new Ties(otherInformation: [new OtherInformation(key: 'b', value: 'b')]).hashCode()
    }
}
