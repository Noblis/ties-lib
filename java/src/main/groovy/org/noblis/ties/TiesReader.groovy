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
class TiesReader {

    private final TiesValidator validator
    private final ObjectMapper mapper

    public TiesReader() {
        validator = new TiesValidator()
        mapper = new TiesObjectMapper()
    }

    public Ties read(byte[] json, boolean validate = true) throws ValidationException {
        if(validate) {
            validator.validate(json)
        }

        mapper.readValue(json, Ties)
    }

    public Ties read(String json, boolean validate = true) throws ValidationException {
        if(validate) {
            validator.validate(json)
        }

        mapper.readValue(json, Ties)
    }

    public Ties read(File json, boolean validate = true) throws ValidationException {
        if(validate) {
            validator.validate(json)
        }

        mapper.readValue(json, Ties)
    }

    public Ties read(InputStream json, boolean validate = true) throws ValidationException {
        if(validate) {
            validator.validate(json)
            json.reset()
        }

        mapper.readValue(json, Ties)
    }
}
