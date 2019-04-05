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

class ObjectItemTests implements SchemaTestTrait {

    private static ObjectItem getEmptyObject() {
        return new ObjectItem()
    }

    private static ObjectItem getCompleteObject() {
        return new ObjectItem(
                objectId: 'a',
                sha256Hash: 'a' * 64,
                md5Hash: 'a' * 32,
                size: 0,
                mimeType: 'a',
                relativeUri: 'a',
                originalPath: 'a',
                authorityInformation: new AuthorityInformation(securityTag: 'a'),
                objectAssertions: new Assertions(),
                otherInformation: [new OtherInformation(key: 'a', value: 'a')]
        )
    }

    private static String completeJson = '''\
{
    "objectId": "a",
    "sha256Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
    "md5Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
    "size": 0,
    "mimeType": "a",
    "relativeUri": "a",
    "originalPath": "a",
    "authorityInformation": {
        "securityTag": "a"
    },
    "objectAssertions": {
        
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
        assert fromJson(completeJson, ObjectItem) == completeObject
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

        assert new ObjectItem(objectId: 'a') != new ObjectItem(objectId: 'b')
        assert new ObjectItem(sha256Hash: 'a') != new ObjectItem(sha256Hash: 'b')
        assert new ObjectItem(md5Hash: 'a') != new ObjectItem(md5Hash: 'b')
        assert new ObjectItem(size: 0L) != new ObjectItem(size: 1L)
        assert new ObjectItem(mimeType: 'a') != new ObjectItem(mimeType: 'b')
        assert new ObjectItem(relativeUri: 'a') != new ObjectItem(relativeUri: 'b')
        assert new ObjectItem(originalPath: 'a') != new ObjectItem(originalPath: 'b')
        assert new ObjectItem(authorityInformation: new AuthorityInformation(collectionId: 'a')) != new ObjectItem(authorityInformation: new AuthorityInformation(collectionId: 'b'))
        assert new ObjectItem(objectAssertions: new Assertions(annotations: [new Annotation(assertionId: 'a')])) != new ObjectItem(objectAssertions: new Assertions(annotations: [new Annotation(assertionId: 'b')]))
        assert new ObjectItem(otherInformation: [new OtherInformation(key: 'a', value: 'a')]) != new ObjectItem(otherInformation: [new OtherInformation(key: 'b', value: 'b')])
    }

    @Test
    void test_hashCode() {
        assert emptyObject.hashCode() != new Object().hashCode()

        assert emptyObject.hashCode() == emptyObject.hashCode()

        assert new ObjectItem(objectId: 'a').hashCode() != new ObjectItem(objectId: 'b').hashCode()
        assert new ObjectItem(sha256Hash: 'a').hashCode() != new ObjectItem(sha256Hash: 'b').hashCode()
        assert new ObjectItem(md5Hash: 'a').hashCode() != new ObjectItem(md5Hash: 'b').hashCode()
        assert new ObjectItem(size: 0L).hashCode() != new ObjectItem(size: 1L).hashCode()
        assert new ObjectItem(mimeType: 'a').hashCode() != new ObjectItem(mimeType: 'b').hashCode()
        assert new ObjectItem(relativeUri: 'a').hashCode() != new ObjectItem(relativeUri: 'b').hashCode()
        assert new ObjectItem(originalPath: 'a').hashCode() != new ObjectItem(originalPath: 'b').hashCode()
        assert new ObjectItem(authorityInformation: new AuthorityInformation(collectionId: 'a')).hashCode() != new ObjectItem(authorityInformation: new AuthorityInformation(collectionId: 'b')).hashCode()
        assert new ObjectItem(objectAssertions: new Assertions(annotations: [new Annotation(assertionId: 'a')])).hashCode() != new ObjectItem(objectAssertions: new Assertions(annotations: [new Annotation(assertionId: 'b')])).hashCode()
        assert new ObjectItem(otherInformation: [new OtherInformation(key: 'a', value: 'a')]).hashCode() != new ObjectItem(otherInformation: [new OtherInformation(key: 'b', value: 'b')]).hashCode()
    }
}
