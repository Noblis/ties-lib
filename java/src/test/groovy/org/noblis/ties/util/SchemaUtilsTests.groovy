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

package org.noblis.ties.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test

class SchemaUtilsTests {

    @Test(expected = IllegalArgumentException)
    void test_parseJsonPointerNull() {
        SchemaUtils.parseJsonPointer(null)
    }

    @Test
    void test_parseJsonPointerRoot() {
        assert SchemaUtils.parseJsonPointer('') == []
    }

    @Test(expected = IllegalArgumentException)
    void test_parseJsonPointerInvalidFormat() {
        SchemaUtils.parseJsonPointer('/foo[0]')
    }

    @Test
    void test_parseJsonPointer() {
        assert SchemaUtils.parseJsonPointer('/foo') == ['foo']
        assert SchemaUtils.parseJsonPointer('/foo/bar') == ['foo', 'bar']
        assert SchemaUtils.parseJsonPointer('/foo/bar/0') == ['foo', 'bar', '0']
        assert SchemaUtils.parseJsonPointer('/foo/bar/0/baz') == ['foo', 'bar', '0', 'baz']
    }

    @Test(expected = IllegalArgumentException)
    void test_unparseJsonPointerNull() {
        SchemaUtils.unparseJsonPointer(null)
    }

    @Test
    void test_unparseJsonPointerEmptyList() {
        assert SchemaUtils.unparseJsonPointer([]) == ''
    }

    @Test(expected = IllegalArgumentException)
    void test_unparseJsonPointerInvalidFormat() {
        SchemaUtils.unparseJsonPointer([''])
    }

    @Test
    void test_unparseJsonPointer() {
        assert SchemaUtils.unparseJsonPointer(['foo']) == '/foo'
        assert SchemaUtils.unparseJsonPointer(['foo', 'bar']) == '/foo/bar'
        assert SchemaUtils.unparseJsonPointer(['foo', 'bar', '0']) == '/foo/bar/0'
        assert SchemaUtils.unparseJsonPointer(['foo', 'bar', '0', 'baz']) == '/foo/bar/0/baz'
        assert SchemaUtils.unparseJsonPointer(['foo', 'bar0']) == '/foo/bar0'
        assert SchemaUtils.unparseJsonPointer(['foo', 'bar0', 'baz']) == '/foo/bar0/baz'
    }

    @Test(expected = IllegalArgumentException)
    void test_parseJsonPathNull() {
        SchemaUtils.parseJsonPath(null)
    }

    @Test
    void test_parseJsonPathRoot() {
        assert SchemaUtils.parseJsonPath('/') == []
    }

    @Test(expected = IllegalArgumentException)
    void test_parseJsonPathInvalidFormat() {
        SchemaUtils.parseJsonPath('/[0]foo')
    }

    @Test
    void test_parseJsonPath() {
        assert SchemaUtils.parseJsonPath('/foo') == ['foo']
        assert SchemaUtils.parseJsonPath('/foo/bar') == ['foo', 'bar']
        assert SchemaUtils.parseJsonPath('/foo/bar[0]') == ['foo', 'bar', '0']
        assert SchemaUtils.parseJsonPath('/foo/bar[0]/baz') == ['foo', 'bar', '0', 'baz']
        assert SchemaUtils.parseJsonPath('/foo/bar0') == ['foo', 'bar0']
        assert SchemaUtils.parseJsonPath('/foo/bar0/baz') == ['foo', 'bar0', 'baz']
    }

    @Test(expected = IllegalArgumentException)
    void test_unparseJsonPathNull() {
        SchemaUtils.unparseJsonPath(null)
    }

    @Test
    void test_unparseJsonPathEmptyList() {
        assert SchemaUtils.unparseJsonPath([]) == '/'
    }

    @Test(expected = IllegalArgumentException)
    void test_unparseJsonPathInvalidFormat() {
        SchemaUtils.unparseJsonPath([''])
    }

    @Test
    void test_unparseJsonPath() {
        assert SchemaUtils.unparseJsonPath(['foo']) == '/foo'
        assert SchemaUtils.unparseJsonPath(['foo', 'bar']) == '/foo/bar'
        assert SchemaUtils.unparseJsonPath(['foo', 'bar', '0']) == '/foo/bar[0]'
        assert SchemaUtils.unparseJsonPath(['foo', 'bar', '0', 'baz']) == '/foo/bar[0]/baz'
        assert SchemaUtils.unparseJsonPath(['foo', 'bar0']) == '/foo/bar0'
        assert SchemaUtils.unparseJsonPath(['foo', 'bar0', 'baz']) == '/foo/bar0/baz'
    }

    @Test(expected = IllegalArgumentException)
    void test_findNodeNullJsonNodeList() {
        SchemaUtils.findNode(null, [])
    }

    @Test(expected = IllegalArgumentException)
    void test_findNodeNullJsonNodeString() {
        SchemaUtils.findNode(null, '/')
    }

    @Test(expected = IllegalArgumentException)
    void test_findNodeNullJsonPointerList() {
        def foo = ['foo': ['bar': [['baz': 'abc']]]]
        JsonNode rootNode = new ObjectMapper().convertValue(foo, JsonNode.class)
        SchemaUtils.findNode(rootNode, (List<String>) null)
    }

    @Test(expected = IllegalArgumentException)
    void test_findNodeNullJsonPointerString() {
        def foo = ['foo': ['bar': [['baz': 'abc']]]]
        JsonNode rootNode = new ObjectMapper().convertValue(foo, JsonNode.class)
        SchemaUtils.findNode(rootNode, (String) null)
    }

    @Test
    void test_findNodeList() {
        def foo = ['foo': ['bar': [['baz': 'abc']]]]
        JsonNode rootNode = new ObjectMapper().convertValue(foo, JsonNode.class)
        assert SchemaUtils.findNode(rootNode, []) == rootNode
        assert SchemaUtils.findNode(rootNode, ['foo']) == rootNode.path('foo')
        assert SchemaUtils.findNode(rootNode, ['foo', 'bar']) == rootNode.path('foo').path('bar')
        assert SchemaUtils.findNode(rootNode, ['foo', 'bar', '0']) == rootNode.path('foo').path('bar').path(0)
        assert SchemaUtils.findNode(rootNode, ['foo', 'bar', '0', 'baz']) == rootNode.path('foo').path('bar').path(0).path('baz')
        assert SchemaUtils.findNode(rootNode, ['bar']).isMissingNode()
    }

    @Test
    void test_findNodeString() {
        def foo = ['foo': ['bar': [['baz': 'abc']]]]
        JsonNode rootNode = new ObjectMapper().convertValue(foo, JsonNode.class)
        assert SchemaUtils.findNode(rootNode, []) == rootNode
        assert SchemaUtils.findNode(rootNode, '/foo') == rootNode.path('foo')
        assert SchemaUtils.findNode(rootNode, '/foo/bar') == rootNode.path('foo').path('bar')
        assert SchemaUtils.findNode(rootNode, '/foo/bar/0') == rootNode.path('foo').path('bar').path(0)
        assert SchemaUtils.findNode(rootNode, '/foo/bar/0/baz') == rootNode.path('foo').path('bar').path(0).path('baz')
        assert SchemaUtils.findNode(rootNode, '/bar').isMissingNode()
    }
}
