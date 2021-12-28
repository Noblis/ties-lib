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

class Version {

    private static final String versionNumber = '0.9.3'

    private static String getBuildNumber() {
        InputStream resource = Version.getResourceAsStream('/version/build_number.txt')
        if (resource == null) {
            return null
        } else {
            String buildNumber = resource.text
            return buildNumber ?: null
        }
    }

    private static String getBuildTime() {
        InputStream resource = Version.getResourceAsStream('/version/build_time.txt')
        if (resource == null) {
            return null
        } else {
            String buildTime = resource.text
            return buildTime ?: null
        }
    }

    public static String getVersionString() {
        String buildNumber = getBuildNumber()
        String buildTime = getBuildTime()

        String version = "version ${versionNumber}"
        if (buildNumber != null) {
            version += "\nbuild ${buildNumber}"
        }
        if (buildTime != null) {
            version += "\nbuilt on ${buildTime}"
        }
        return version
    }
}
