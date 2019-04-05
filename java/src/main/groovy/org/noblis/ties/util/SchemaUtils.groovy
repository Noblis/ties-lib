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
import groovy.transform.CompileStatic

@CompileStatic
class SchemaUtils implements ExceptionUtilsTrait {

    public static List<String> parseJsonPointer(String jsonPointer) {
        checkParams {
            assert jsonPointer != null
            assert jsonPointer == '' || jsonPointer.matches('(/)|(/\\w+)+')
        }

        if (jsonPointer == '') {
            return []
        }
        return jsonPointer.stripMargin('/').split('/').toList()
    }

    public static String unparseJsonPointer(List<String> pointerComponents) {
        checkParams {
            assert pointerComponents != null
            assert pointerComponents.every { it.matches('\\w+') }
        }

        if (pointerComponents == []) {
            return ''
        }
        return '/' + pointerComponents.join('/')
    }

    public static List<String> parseJsonPath(String jsonPath) {
        checkParams {
            assert jsonPath != null
            assert jsonPath.matches('(/)|(/\\w+(\\[\\d+\\])?)+')
        }

        if (jsonPath == '/') {
            return []
        }
        return parseJsonPointer(jsonPath.replaceAll('/(\\w+)\\[(\\d+)\\]', { List<String> it -> "/${it[1]}/${it[2]}" }))
    }

    public static String unparseJsonPath(List<String> pathComponents) {
        checkParams {
            assert pathComponents != null
            assert pathComponents.every { it.matches('\\w+') }
        }

        if (pathComponents == []) {
            return '/'
        }
        unparseJsonPointer(pathComponents).replaceAll('/(\\d+)', { List<String> it -> "[${it[1]}]" })
    }

    public static JsonNode findNode(JsonNode rootNode, List<String> jsonPointer) {
        checkParams {
            assert rootNode != null
            assert jsonPointer != null
        }

        return jsonPointer.inject(rootNode) { JsonNode node, String p ->
            if (p.isInteger()) {
                return node.path(p.toInteger())
            } else {
                return node.path(p)
            }
        } as JsonNode
    }

    public static JsonNode findNode(JsonNode rootNode, String jsonPointer) {
        checkParams {
            assert rootNode != null
            assert jsonPointer != null
        }

        return findNode(rootNode, parseJsonPointer(jsonPointer))
    }
}
