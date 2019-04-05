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

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import org.noblis.ties.data_binding.SupplementalDescription

class SupplementalDescriptionDeserializer extends StdDeserializer<SupplementalDescription> {

    private Map<String, Class<? extends SupplementalDescription>> registry = [:]

    public SupplementalDescriptionDeserializer() {
        super(SupplementalDescription.class)
    }

    public void registerSupplementalDescription(String uniqueAttribute, Class<? extends SupplementalDescription> cls) {
        registry[uniqueAttribute] = cls
    }

    @Override
    public SupplementalDescription deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = jp.getCodec() as ObjectMapper
        ObjectNode root = mapper.readTree(jp) as ObjectNode
        Iterator<Map.Entry<String, JsonNode>> elementsIterator = root.fields()
        while (elementsIterator.hasNext()) {
            Map.Entry<String, JsonNode> element = elementsIterator.next()
            String name = element.key
            if (name in registry) {
                return mapper.readValue(root.traverse(), registry[name])
            }
        }
        return null
    }
}
