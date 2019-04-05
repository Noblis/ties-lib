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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.noblis.ties.data_binding.SupplementalDescription
import org.noblis.ties.data_binding.SupplementalDescriptionDataFile
import org.noblis.ties.data_binding.SupplementalDescriptionDataObject

import java.text.DateFormat
import java.text.SimpleDateFormat

class TiesObjectMapper extends ObjectMapper {

    public TiesObjectMapper() {
        super()

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone('UTC')
        this.dateFormat = dateFormat

        DateDeserializer dateDeserializer = new DateDeserializer()
        SimpleModule dateModule = new SimpleModule('DateDeserializerModule', new com.fasterxml.jackson.core.Version(1, 0, 0, null))
        dateModule.addDeserializer(Date.class, dateDeserializer)
        this.registerModule(dateModule)

        SupplementalDescriptionDeserializer supplementalDescriptionDeserializer = new SupplementalDescriptionDeserializer()
        supplementalDescriptionDeserializer.registerSupplementalDescription('sha256DataHash', SupplementalDescriptionDataFile.class)
        supplementalDescriptionDeserializer.registerSupplementalDescription('dataObject', SupplementalDescriptionDataObject.class)
        SimpleModule supplementalDescriptionModule = new SimpleModule('PolymorphicSupplementalDescriptionDeserializerModule', new com.fasterxml.jackson.core.Version(1, 0, 0, null))
        supplementalDescriptionModule.addDeserializer(SupplementalDescription.class, supplementalDescriptionDeserializer)
        this.registerModule(supplementalDescriptionModule)
    }
}
