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

import org.junit.After
import org.junit.Before
import org.junit.Test

class GlobTests implements GlobTrait {

    @Before
    void setUp() {
        new File('build/GlobTests/abc').mkdirs()
        new File('build/GlobTests/abc/file1.abc').createNewFile()
        new File('build/GlobTests/abc/file2.abc').createNewFile()
        new File('build/GlobTests/def').mkdirs()
        new File('build/GlobTests/def/file1.def').createNewFile()
        new File('build/GlobTests/def/file2.def').createNewFile()
        new File('build/GlobTests/file1.abc').createNewFile()
        new File('build/GlobTests/file2.abc').createNewFile()
        new File('build/GlobTests/file1.def').createNewFile()
        new File('build/GlobTests/file2.def').createNewFile()
    }

    @After
    void tearDown() {
        new File('build/GlobTests').deleteDir()
    }

    @Test
    void test_globPaths_absolute_path_wildcard() {
        assert globPaths("${new File('build/GlobTests').absolutePath}/*", 'build') == [new File('build/GlobTests/abc').absolutePath,
                                                                                                 new File('build/GlobTests/def').absolutePath,
                                                                                                 new File('build/GlobTests/file1.abc').absolutePath,
                                                                                                 new File('build/GlobTests/file1.def').absolutePath,
                                                                                                 new File('build/GlobTests/file2.abc').absolutePath,
                                                                                                 new File('build/GlobTests/file2.def').absolutePath,]
    }

    @Test
    void test_globPaths_relative_path_wildcard() {
        assert globPaths('GlobTests/*', 'build') == ['GlobTests/abc',
                                                               'GlobTests/def',
                                                               'GlobTests/file1.abc',
                                                               'GlobTests/file1.def',
                                                               'GlobTests/file2.abc',
                                                               'GlobTests/file2.def',]
    }

    @Test
    void test_globPaths_absolute_path_directory() {
        assert globPaths("${new File('build/GlobTests/').absolutePath}", 'build') == [new File('build/GlobTests').absolutePath]
    }

    @Test
    void test_globPaths_relative_path_directory() {
        assert globPaths('GlobTests/', 'build') == ['GlobTests']
    }

    @Test
    void test_globPaths_absolute_path_file() {
        assert globPaths("${new File('build/GlobTests/file1.abc').absolutePath}", 'build') == [new File('build/GlobTests/file1.abc').absolutePath]
    }

    @Test
    void test_globPaths_relative_path_file() {
        assert globPaths('GlobTests/file1.abc', 'build') == ['GlobTests/file1.abc']
    }

    @Test
    void test_globPaths_nonexistent_path() {
        assert globPaths('GlobTests/fail', 'build') == []
    }

    @Test
    void test_globPaths_no_prefix_path() {
        assert globPaths('*', 'build/GlobTests') == ['abc',
                                                               'def',
                                                               'file1.abc',
                                                               'file1.def',
                                                               'file2.abc',
                                                               'file2.def',]
    }
}
