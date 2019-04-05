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

class AuthorityInformationTests implements SchemaTestTrait {

    private static AuthorityInformation getEmptyObject() {
        return new AuthorityInformation()
    }

    private static AuthorityInformation getCompleteObject() {
        return new AuthorityInformation(
                collectionId: 'a',
                collectionIdLabel: 'a',
                collectionIdAlias: 'a',
                collectionDescription: 'a',
                subCollectionId: 'a',
                subCollectionIdLabel: 'a',
                subCollectionIdAlias: 'a',
                subCollectionDescription: 'a',
                registrationDate: new Date(0),
                expirationDate: new Date(0),
                owner: 'a',
                securityTag: 'a'
        )
    }

    private static String completeJson = '''\
{
    "collectionId": "a",
    "collectionIdLabel": "a",
    "collectionIdAlias": "a",
    "collectionDescription": "a",
    "subCollectionId": "a",
    "subCollectionIdLabel": "a",
    "subCollectionIdAlias": "a",
    "subCollectionDescription": "a",
    "registrationDate": "1970-01-01T00:00:00.000Z",
    "expirationDate": "1970-01-01T00:00:00.000Z",
    "owner": "a",
    "securityTag": "a"
}'''

    @Test
    void test_fromJson() {
        assert fromJson(completeJson, AuthorityInformation) == completeObject
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

        assert new AuthorityInformation(collectionId: 'a') != new AuthorityInformation(collectionId: 'b')
        assert new AuthorityInformation(collectionIdLabel: 'a') != new AuthorityInformation(collectionIdLabel: 'b')
        assert new AuthorityInformation(collectionIdAlias: 'a') != new AuthorityInformation(collectionIdAlias: 'b')
        assert new AuthorityInformation(collectionDescription: 'a') != new AuthorityInformation(collectionDescription: 'b')
        assert new AuthorityInformation(subCollectionId: 'a') != new AuthorityInformation(subCollectionId: 'b')
        assert new AuthorityInformation(subCollectionIdLabel: 'a') != new AuthorityInformation(subCollectionIdLabel: 'b')
        assert new AuthorityInformation(subCollectionIdAlias: 'a') != new AuthorityInformation(subCollectionIdAlias: 'b')
        assert new AuthorityInformation(subCollectionDescription: 'a') != new AuthorityInformation(subCollectionDescription: 'b')
        assert new AuthorityInformation(registrationDate: new Date(0)) != new AuthorityInformation(registrationDate: new Date(1))
        assert new AuthorityInformation(expirationDate: new Date(0)) != new AuthorityInformation(expirationDate: new Date(1))
        assert new AuthorityInformation(owner: 'a') != new AuthorityInformation(owner: 'b')
        assert new AuthorityInformation(securityTag: 'a') != new AuthorityInformation(securityTag: 'b')
    }

    @Test
    void test_hashCode() {
        assert emptyObject.hashCode() != new Object().hashCode()

        assert emptyObject.hashCode() == emptyObject.hashCode()

        assert new AuthorityInformation(collectionId: 'a').hashCode() != new AuthorityInformation(collectionId: 'b').hashCode()
        assert new AuthorityInformation(collectionIdLabel: 'a').hashCode() != new AuthorityInformation(collectionIdLabel: 'b').hashCode()
        assert new AuthorityInformation(collectionIdAlias: 'a').hashCode() != new AuthorityInformation(collectionIdAlias: 'b').hashCode()
        assert new AuthorityInformation(collectionDescription: 'a').hashCode() != new AuthorityInformation(collectionDescription: 'b').hashCode()
        assert new AuthorityInformation(subCollectionId: 'a').hashCode() != new AuthorityInformation(subCollectionId: 'b').hashCode()
        assert new AuthorityInformation(subCollectionIdLabel: 'a').hashCode() != new AuthorityInformation(subCollectionIdLabel: 'b').hashCode()
        assert new AuthorityInformation(subCollectionIdAlias: 'a').hashCode() != new AuthorityInformation(subCollectionIdAlias: 'b').hashCode()
        assert new AuthorityInformation(subCollectionDescription: 'a').hashCode() != new AuthorityInformation(subCollectionDescription: 'b').hashCode()
        assert new AuthorityInformation(registrationDate: new Date(0)).hashCode() != new AuthorityInformation(registrationDate: new Date(1)).hashCode()
        assert new AuthorityInformation(expirationDate: new Date(0)).hashCode() != new AuthorityInformation(expirationDate: new Date(1)).hashCode()
        assert new AuthorityInformation(owner: 'a').hashCode() != new AuthorityInformation(owner: 'b').hashCode()
        assert new AuthorityInformation(securityTag: 'a').hashCode() != new AuthorityInformation(securityTag: 'b').hashCode()
    }
}
