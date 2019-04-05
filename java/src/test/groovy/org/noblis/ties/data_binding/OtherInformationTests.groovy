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

class OtherInformationTests implements SchemaTestTrait {

    private static OtherInformation getEmptyObject() {
        return new OtherInformation()
    }

    private static OtherInformation getCompleteObject() {
        return new OtherInformation(
                key: 'a',
                value: 'a'
        )
    }

    private static String completeJson = '''\
{
    "key": "a",
    "value": "a"
}'''

    @Test
    void test_fromJson() {
        assert fromJson(completeJson, OtherInformation) == completeObject
    }

    @Test
    void test_toJson() {
        assert toJson(completeObject) == JsonOutput.prettyPrint(completeJson)
    }

    @Test
    void test_valueStringFromJson() {
        def json = '''\
{
    "key": "a",
    "value": "a"
}'''
        fromJson(json, OtherInformation) == new OtherInformation(key: 'a', value: 'a')
    }

    @Test
    void test_valueIntegerFromJson() {
        def json = '''\
{
    "key": "a",
    "value": 1
}'''
        fromJson(json, OtherInformation) == new OtherInformation(key: 'a', value: '1')
    }

    @Test
    void test_valueNumberFromJson() {
        def json = '''\
{
    "key": "a",
    "value": 1.1
}'''
        fromJson(json, OtherInformation) == new OtherInformation(key: 'a', value: '1.1')
    }

    @Test
    void test_valueBooleanFromJson() {
        def json = '''\
{
    "key": "a",
    "value": true
}'''
        fromJson(json, OtherInformation) == new OtherInformation(key: 'a', value: 'true')
    }

    @Test
    void test_valueStringToJson() {
        assert toJson(new OtherInformation(key: 'a', value: 'a')) == '''\
{
    "key": "a",
    "value": "a"
}'''
    }

    @Test
    void test_valueIntegerToJson() {
        assert toJson(new OtherInformation(key: 'a', value: 1)) == '''\
{
    "key": "a",
    "value": 1
}'''
    }

    @Test
    void test_valueFloatToJson() {
        assert toJson(new OtherInformation(key: 'a', value: 1.1f)) == '''\
{
    "key": "a",
    "value": 1.1
}'''
    }

    @Test
    void test_valueDoubleToJson() {
        assert toJson(new OtherInformation(key: 'a', value: 1.1d)) == '''\
{
    "key": "a",
    "value": 1.1
}'''
    }

    @Test
    void test_valueBooleanToJson() {
        assert toJson(new OtherInformation(key: 'a', value: true)) == '''\
{
    "key": "a",
    "value": true
}'''
    }

    @Test
    void test_equals() {
        def o = emptyObject
        assert o.equals(o)

        assert emptyObject != new Object()

        assert emptyObject == emptyObject

        assert new OtherInformation(key: 'a') != new OtherInformation(key: 'b')
        assert new OtherInformation(value: 'a') != new OtherInformation(value: 'b')
    }

    @Test
    void test_hashCode() {
        assert emptyObject.hashCode() != new Object().hashCode()

        assert emptyObject.hashCode() == emptyObject.hashCode()

        assert new OtherInformation(key: 'a').hashCode() != new OtherInformation(key: 'b').hashCode()
        assert new OtherInformation(value: 'a').hashCode() != new OtherInformation(value: 'b').hashCode()
    }
}
