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

import groovy.transform.CompileStatic

@CompileStatic
class TiesSubSchemaValidator extends SchemaValidator {

    private TiesSubSchemaValidator(String jsonPointer) {
        super('resource:/schemata/ties-base.json', jsonPointer)
    }

    public static TiesSubSchemaValidator getAnnotationValidator() {
        return new TiesSubSchemaValidator('/definitions/annotation-object')
    }

    public static TiesSubSchemaValidator getAssertionValidator() {
        return new TiesSubSchemaValidator('/definitions/assertions-object')
    }

    public static TiesSubSchemaValidator getAuthorityInformationValidator() {
        return new TiesSubSchemaValidator('/definitions/authorityInformation-object')
    }

    public static TiesSubSchemaValidator getObjectGroupValidator() {
        return new TiesSubSchemaValidator('/definitions/objectGroup-object')
    }

    public static TiesSubSchemaValidator getObjectItemValidator() {
        return new TiesSubSchemaValidator('/definitions/objectItem-object')
    }

    public static TiesSubSchemaValidator getObjectRelationshipValidator() {
        return new TiesSubSchemaValidator('/definitions/objectRelationship-object')
    }

    public static TiesSubSchemaValidator getOtherInformationValidator() {
        return new TiesSubSchemaValidator('/definitions/otherInformation-object')
    }

    public static TiesSubSchemaValidator getSupplementalDescriptionDataFileValidator() {
        return new TiesSubSchemaValidator('/definitions/supplementalDescriptionDataFile-object')
    }

    public static TiesSubSchemaValidator getSupplementalDescriptionDataObjectValidator() {
        return new TiesSubSchemaValidator('/definitions/supplementalDescriptionDataObject-object')
    }
}
