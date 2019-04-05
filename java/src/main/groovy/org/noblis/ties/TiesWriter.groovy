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

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import org.noblis.ties.data_binding.Ties
import org.noblis.ties.util.TiesObjectMapper

@CompileStatic
class TiesWriter {

    private final TiesValidator validator
    private final ObjectMapper mapper

    public TiesWriter() {
        validator = new TiesValidator()
        mapper = new TiesObjectMapper()
    }

    public byte[] writeBytes(Ties ties, boolean validate = true) throws ValidationException {
        byte[] json = mapper.writeValueAsBytes(ties)

        if(validate) {
            validator.validate(json)
        }

        return json
    }

    public String writeString(Ties ties, boolean validate = true) throws ValidationException {
        String json = mapper.writeValueAsString(ties)

        if(validate) {
            validator.validate(json)
        }

        return json
    }

    public void writeFile(Ties ties, File output, boolean validate = true) throws ValidationException {
        if(validate) {
            validator.validate(mapper.writeValueAsString(ties))
        }

        mapper.writeValue(output, ties)
    }

    public void writeOutputStream(Ties ties, OutputStream output, boolean validate = true) throws ValidationException {
        if(validate) {
            validator.validate(mapper.writeValueAsString(ties))
        }

        mapper.writeValue(output, ties)
    }
}
