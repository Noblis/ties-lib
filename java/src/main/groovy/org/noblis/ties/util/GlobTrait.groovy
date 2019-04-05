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

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

trait GlobTrait implements ExceptionUtilsTrait {

    private static List<String> globPaths(String globPattern) throws IllegalArgumentException {
        return globPaths(globPattern, '.')
    }

    private static List<String> globPaths(String globPattern, String basePath) throws IllegalArgumentException {
        checkParams {
            assert globPattern != null
            assert basePath != null
        }

        File glob = new File(globPattern)
        String fileGlob = glob.name
        String globPath = glob.parent ?: ''
        String searchPath = glob.isAbsolute() ? globPath : new File(basePath, globPath).path
        return Files.newDirectoryStream(Paths.get(searchPath), fileGlob).collect({ Path path ->
            globPath ? new File(globPath, path.fileName.toString()).path : path.fileName.toString()
        }).sort()
    }

    private static List<File> globFiles(String globPattern) throws IllegalArgumentException {
        return globFiles(globPattern, '.')
    }

    private static List<File> globFiles(String globPattern, String basePath) throws IllegalArgumentException {
        checkParams {
            assert globPattern != null
            assert basePath != null
        }

        return globPaths(globPattern, basePath).collect { new File(it) }
    }
}
