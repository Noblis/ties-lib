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

package org.noblis.ties

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.LogLevel
import com.github.fge.jsonschema.core.report.ProcessingMessage
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchema
import com.github.fge.jsonschema.main.JsonSchemaFactory
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import org.noblis.ties.util.ProcessingMessageUtils
import org.noblis.ties.util.SchemaUtils

@CompileStatic
abstract class SchemaValidator {

    protected final JsonSchema schema

    protected SchemaValidator(String schemaPath) {
        this.schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaPath)
    }

    protected SchemaValidator(String schemaPath, String jsonPointer) {
        this("${schemaPath}#${jsonPointer}")
    }

    public List<ValidationException> allErrors(byte[] json) {
        return allErrors(new String(json, 'UTF-8'))
    }

    public List<ValidationException> allErrors(String json) {
        return validateInstance(JsonLoader.fromString(json))
    }

    public List<ValidationException> allErrors(File json) {
        return validateInstance(JsonLoader.fromFile(json))
    }

    public List<ValidationException> allErrors(InputStream json) {
        return validateInstance(JsonLoader.fromReader(new InputStreamReader(json, 'UTF-8')))
    }

    public List<ValidationException> allErrors(Map json) {
        return validateInstance(JsonLoader.fromString(JsonOutput.toJson(json)))
    }

    public void validate(byte[] json) throws ValidationException {
        validate(new String(json, 'UTF-8'))
    }

    public void validate(String json) throws ValidationException {
        List<ValidationException> validationExceptions = allErrors(json)
        if (validationExceptions) {
            throw validationExceptions.first()
        }
    }

    public void validate(File json) throws ValidationException {
        List<ValidationException> validationExceptions = allErrors(json)
        if (validationExceptions) {
            throw validationExceptions.first()
        }
    }

    public void validate(InputStream json) throws ValidationException {
        List<ValidationException> validationExceptions = allErrors(json)
        if (validationExceptions) {
            throw validationExceptions.first()
        }
    }

    public void validate(Map json) throws ValidationException {
        List<ValidationException> validationExceptions = allErrors(json)
        if (validationExceptions) {
            throw validationExceptions.first()
        }
    }

    private List<ValidationException> validateInstance(JsonNode json) {
        ProcessingReport report = schema.validate(json)
        if(!report.isSuccess()) {
            List<ProcessingMessage> validationMessages = report.findAll { ProcessingMessage m ->
                m.logLevel in [LogLevel.ERROR, LogLevel.FATAL]
            } as List<ProcessingMessage>
            // sort validation messages by validator
            validationMessages = validationMessages.sort { ProcessingMessage pm -> pm.asJson().get('validator') }
            List<ValidationException> validationErrors = validationMessages.collect {
                new ValidationException(validationErrorMessage(it, json), ProcessingMessageUtils.getInstancePathString(it), validationErrorCauses(it))
            }
            // sort validation errors by location; sort is stable, so they will be sorted by location and by validator
            return validationErrors.sort { ValidationException e -> e.location }
        }
        return []
    }

    private static String validationErrorMessage(ProcessingMessage processingMessage, JsonNode json) {
        String keyword = processingMessage.asJson().get('keyword').asText()
        if (keyword == 'type') {
            return typeErrorMessage(processingMessage)
        }
        if (keyword == 'required') {
            return requiredErrorMessage(processingMessage)
        }
        if (keyword == 'additionalProperties') {
            return additionalPropertiesErrorMessage(processingMessage)
        }
        if (keyword == 'minimum') {
            return minimumErrorMessage(processingMessage)
        }
        if (keyword == 'maximum') {
            return maximumErrorMessage(processingMessage)
        }
        if (keyword == 'minLength') {
            return minLengthErrorMessage(processingMessage)
        }
        if (keyword == 'maxLength') {
            return maxLengthErrorMessage(processingMessage)
        }
        if (keyword == 'format') {
            return formatErrorMessage(processingMessage)
        }
        if (keyword == 'pattern') {
            return patternErrorMessage(processingMessage)
        }
        if (keyword == 'enum') {
            return enumErrorMessage(processingMessage)
        }
        if (keyword == 'minItems') {
            return minItemsErrorMessage(processingMessage)
        }
        if (keyword == 'maxItems') {
            return maxItemsErrorMessage(processingMessage)
        }
        if (keyword == 'uniqueItems') {
            return uniqueItemsErrorMessage(processingMessage, json)
        }
        if (keyword == 'anyOf') {
            return anyOfErrorMessage(processingMessage)
        }
        return unknownErrorMessage(processingMessage)
    }

    private static String typeErrorMessage(ProcessingMessage processingMessage) {
        List<String> instancePointer = SchemaUtils.parseJsonPointer(SchemaUtils.findNode(processingMessage.asJson(), '/instance/pointer').asText())
        String propertyName = instancePointer.last()
        String foundType = SchemaUtils.findNode(processingMessage.asJson(), '/found').asText()
        List<String> expectedTypes = SchemaUtils.findNode(processingMessage.asJson(), '/expected').asList()*.asText()
        if (expectedTypes.size() == 1) {
            if (foundType == 'null') {
                return "property ${propertyName} with null value should be of type ${expectedTypes.first()}"
            } else {
                return "property type ${foundType} for property ${propertyName} is not the allowed type: ${expectedTypes.first()}"
            }
        } else {
            if (foundType == 'null') {
                return "property ${propertyName} with null value should be one of the allowed types: ${expectedTypes}"
            } else {
                return "property type ${foundType} for property ${propertyName} is not one of the allowed types: ${expectedTypes}"
            }
        }
    }

    private static String requiredErrorMessage(ProcessingMessage processingMessage) {
        List<String> missingProperties = processingMessage.asJson().get('missing').asList()*.asText()
        if (missingProperties.size() == 1) {
            return "required property ${missingProperties.first()} is missing"
        } else {
            return "required properties ${missingProperties} are missing"
        }
    }

    private static String additionalPropertiesErrorMessage(ProcessingMessage processingMessage) {
        List<String> extraProperties = processingMessage.asJson().get('unwanted').asList()*.asText()
        if (extraProperties.size() == 1) {
            return "additional property ${extraProperties.first()} is not allowed"
        } else {
            return "additional properties ${extraProperties} are not allowed"
        }
    }

    private static String minimumErrorMessage(ProcessingMessage processingMessage) {
        String propertyValue = processingMessage.asJson().get('found').asText()
        int minimum = processingMessage.asJson().get('minimum').asInt()
        String propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/').last()
        if (propertyName.isInteger()) {
            String propertyIndex = propertyName
            propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/')[-2]
            return "property value ${propertyValue} for element at index ${propertyIndex} in ${propertyName} is less than the minimum value of ${minimum}"
        } else {
            return "property value ${propertyValue} for ${propertyName} property is less than the minimum value of ${minimum}"
        }
    }

    private static String maximumErrorMessage(ProcessingMessage processingMessage) {
        String propertyValue = processingMessage.asJson().get('found').asText()
        int maximum = processingMessage.asJson().get('maximum').asInt()
        String propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/').last()
        if (propertyName.isInteger()) {
            String propertyIndex = propertyName
            propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/')[-2]
            return "property value ${propertyValue} for element at index ${propertyIndex} in ${propertyName} is greater than the maximum value of ${maximum}"
        } else {
            return "property value ${propertyValue} for ${propertyName} property is greater than the maximum value of ${maximum}"
        }
    }

    private static String minLengthErrorMessage(ProcessingMessage processingMessage) {
        String propertyValue = getPropertyAsText(processingMessage.asJson(), 'value')
        int minLength = processingMessage.asJson().get('minLength').asInt()
        String propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/').last()
        if (propertyName.isInteger()) {
            String propertyIndex = propertyName
            propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/')[-2]
            return "property value ${quoteValue(propertyValue)} for element at index ${propertyIndex} in ${propertyName} is too short, minimum length ${minLength}"
        } else {
            return "property value ${quoteValue(propertyValue)} for ${propertyName} property is too short, minimum length ${minLength}"
        }
    }

    private static String maxLengthErrorMessage(ProcessingMessage processingMessage) {
        String propertyValue = getPropertyAsText(processingMessage.asJson(), 'value')
        int maxLength = processingMessage.asJson().get('maxLength').asInt()
        String propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/').last()
        if (propertyName.isInteger()) {
            String propertyIndex = propertyName
            propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/')[-2]
            return "property value ${quoteValue(propertyValue)} for element at index ${propertyIndex} in ${propertyName} is too long, maximum length ${maxLength}"
        } else {
            return "property value ${quoteValue(propertyValue)} for ${propertyName} property is too long, maximum length ${maxLength}"
        }
    }

    private static String formatErrorMessage(ProcessingMessage processingMessage) {
        String propertyValue = getPropertyAsText(processingMessage.asJson(), 'value')
        String attribute = processingMessage.asJson().get('attribute').asText()
        String expected = processingMessage.asJson().get('expected').asList()*.asText()
        String propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/').last()
        if (propertyName.isInteger()) {
            String propertyIndex = propertyName
            propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/')[-2]
            return "property value ${quoteValue(propertyValue)} for element at index ${propertyIndex} in ${propertyName} does not match the ${attribute} format: ${expected}"
        } else {
            return "property value ${quoteValue(propertyValue)} for ${propertyName} property does not match the ${attribute} format: ${expected}"
        }
    }

    private static String patternErrorMessage(ProcessingMessage processingMessage) {
        String propertyValue = getPropertyAsText(processingMessage.asJson(), 'string')
        String regex = processingMessage.asJson().get('regex').asText()
        String propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/').last()
        if (propertyName.isInteger()) {
            String propertyIndex = propertyName
            propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/')[-2]
            return "property value ${quoteValue(propertyValue)} for element at index ${propertyIndex} in ${propertyName} does not match the pattern '${regex}'"
        } else {
            return "property value ${quoteValue(propertyValue)} for ${propertyName} property does not match the pattern '${regex}'"
        }
    }

    private static String enumErrorMessage(ProcessingMessage processingMessage) {
        String propertyValue = getPropertyAsText(processingMessage.asJson(), 'value')
        List<String> allowedValues = processingMessage.asJson().get('enum').asList()*.asText()
        String propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/').last()
        if (propertyName.isInteger()) {
            String propertyIndex = propertyName
            propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/')[-2]
            return "enum property value ${quoteValue(propertyValue)} for element at index ${propertyIndex} in ${propertyName} should have one of the allowed values: ${allowedValues}"
        } else {
            return "enum property ${propertyName} with value ${quoteValue(propertyValue)} should have one of the allowed values: ${allowedValues}"
        }
    }

    private static String minItemsErrorMessage(ProcessingMessage processingMessage) {
        String propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/').last()
        int itemCount = processingMessage.asJson().get('found').asInt()
        int minItems = processingMessage.asJson().get('minItems').asInt()
        return "array property ${propertyName} with ${itemCount} items is too small, minimum size ${minItems}"
    }

    private static String maxItemsErrorMessage(ProcessingMessage processingMessage) {
        String propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/').last()
        int itemCount = processingMessage.asJson().get('found').asInt()
        int maxItems = processingMessage.asJson().get('maxItems').asInt()
        return "array property ${propertyName} with ${itemCount} items is too large, maximum size ${maxItems}"
    }

    private static String uniqueItemsErrorMessage(ProcessingMessage processingMessage, JsonNode json) {
        List<String> instancePointer = SchemaUtils.parseJsonPointer(SchemaUtils.findNode(processingMessage.asJson(), '/instance/pointer').asText())
        String propertyName = instancePointer.last()
        JsonNode instance = SchemaUtils.findNode(json, instancePointer)
        Map<String, List<Integer>> itemIndex = [:]
        List<String> itemIndexOrder = []
        instance.asList().eachWithIndex { JsonNode item, int i ->
            String itemStr = item.toString()
            if (!(itemStr in itemIndexOrder)) {
                itemIndexOrder << itemStr
            }
            itemIndex[itemStr] = itemIndex.get(itemStr, []) + [i]
        }
        for (String itemStr : itemIndexOrder) {
            List<Integer> duplicateIndexes = itemIndex[itemStr]
            if (duplicateIndexes.size() > 1) {
                return "array property ${propertyName} has duplicate items at index ${duplicateIndexes}"
            }
        }
        return "array property ${propertyName} has duplicate items"
    }

    private static String anyOfErrorMessage(ProcessingMessage processingMessage) {
        String propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/').last()
        if (propertyName.isInteger()) {
            String propertyIndex = propertyName
            propertyName = processingMessage.asJson().get('instance').get('pointer').asText().split('/')[-2]
            return "content for array property at index ${propertyIndex} in ${propertyName} does not match any of the possible schema definitions"
        } else {
            return "content for property ${propertyName} does not match any of the possible schema definitions"
        }
    }

    private static String unknownErrorMessage(ProcessingMessage processingMessage) {
        return processingMessage.message
    }

    private static List<ValidationException> validationErrorCauses(ProcessingMessage processingMessage) {
        if (!processingMessage.asJson().hasNonNull('reports')) {
            return []
        }

        List<ProcessingMessage> validationMessages = []
        processingMessage.asJson().get('reports').fields().each { Map.Entry<String, JsonNode> entry ->
            entry.value.elements().each { JsonNode causeJson ->
                if (causeJson.get('level').asText() == 'error') {
                    validationMessages << ProcessingMessageUtils.fromJson(causeJson)
                }
            }
        }
        // sort validation messages by validator
        validationMessages = validationMessages.sort { it.asJson().get('validator') }

        List<ValidationException> causes = validationMessages.collect { ProcessingMessage errorCause ->
            return new ValidationException(validationErrorMessage(errorCause, errorCause.asJson()), ProcessingMessageUtils.getInstancePathString(errorCause), validationErrorCauses(errorCause))
        }
        // sort validation errors by location; sort is stable, so they will be sorted by location and by validator
        return causes.sort { it.location }
    }

    private static String getPropertyAsText(JsonNode node, String propertyName) {
        if (node.get(propertyName) instanceof NullNode) {
            return null
        } else {
            return node.get(propertyName).asText()
        }
    }

    private static String quoteValue(String value) {
        if (value == null) {
            return 'null'
        } else {
            return "'${value}'"
        }
    }
}
