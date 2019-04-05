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
import com.github.fge.jsonschema.core.report.ProcessingMessage
import groovy.transform.CompileStatic

@CompileStatic
class ProcessingMessageUtils {

    public static List<String> getInstancePointer(ProcessingMessage processingMessage) {
        return SchemaUtils.parseJsonPointer(getInstancePointerString(processingMessage))
    }

    public static String getInstancePointerString(ProcessingMessage processingMessage) {
        return SchemaUtils.findNode(processingMessage.asJson(), '/instance/pointer').asText()
    }

    public static String getInstancePathString(ProcessingMessage processingMessage) {
        return SchemaUtils.unparseJsonPath(getInstancePointer(processingMessage))
    }

    public static ProcessingMessage fromJson(JsonNode json) {
        ProcessingMessage processingMessage = new ProcessingMessage()
        json.fields().each { Map.Entry<String, JsonNode> field ->
            processingMessage.put(field.key, field.value)
        }
        return processingMessage
    }
}
