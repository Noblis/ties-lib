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
public class ValidationException extends Exception {

    private final String message
    private final String location
    private final List<ValidationException> causes

    public ValidationException(String message, String location, List<ValidationException> causes) {
        super(message)
        this.message = message
        this.location = location
        this.causes = causes
    }

    public String getLocation() {
        return location
    }

    public List<ValidationException> getCauses() {
        return causes.asImmutable()
    }

    @Override
    public String toString() {
        if (causes) {
            return "${message}\npossible causes:\n${causes.collect({ indent(it.toString(), ' ' * 4) }).join('\n')}"
        } else {
            return "${message}\nlocation: ${location}"
        }
    }

    private static String indent(String text, String prefix) {
        if (text.size() == 0) {
            return text
        } else {
            return "${prefix}${text.replace('\n', "\n${prefix}")}"
        }
    }
}
