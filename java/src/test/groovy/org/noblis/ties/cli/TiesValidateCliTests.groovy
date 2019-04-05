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

package org.noblis.ties.cli

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.contrib.java.lang.system.TextFromStandardInputStream

class TiesValidateCliTests implements ITemplateTest {

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog()

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog()

    @Rule
    public final TextFromStandardInputStream systemInRule = TextFromStandardInputStream.emptyStandardInputStream()

    @Rule
    public final ExpectedSystemExit exitRule = ExpectedSystemExit.none()

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables()

    private static final String shortUsage = 'usage: ties-validate [-h] [--version] [FILE]...'

    private static final String longUsage = """\
${shortUsage}

Validate FILE(s), or standard input, against the TIES 0.9 schema.

positional arguments:
  FILE                   the path to the JSON file(s) to be validated against the schema or - to read from stdin

named arguments:
  -h, --help             show this help message and exit
  --version              prints version information

If FILE arguments are provided, attempts to validate all files. FILE arguments may be provided as either file paths or shell globs.

If no FILE arguments are provided, attempts to read a single JSON object from stdin and validate it.

Returns non-zero exit code if one or more input files fail to validate successfully.
"""

    private static final String minimalValidJson = "{\"version\":\"0.9\",\"securityTag\":\"a\",\"objectItems\":[{\"objectId\":\"a\",\"sha256Hash\":\"${'a' * 64}\",\"md5Hash\":\"${'a' * 32}\",\"authorityInformation\":{\"securityTag\":\"a\"}}]}"
    private static final String minimalInvalidJson = '{}'

    private static final String statusDone(String s) {
        return "${s}${'.' * (160 - s.size() - 5)}done"
    }

    private static final String statusError(String s) {
        return "${s}${'.' * (160 - s.size() - 5)}ERROR"
    }

    private static final String path(String s) {
        return new File(s).canonicalPath
    }

    @Before
    void setUp() {
        // ArgParse4j uses the COLUMNS env variable to determine where to wrap the help output, so we set it explicitly
        // here to make the tests work regardless of the size of the terminal.
        environmentVariables.set('COLUMNS', '160')

        systemOutRule.clearLog()
        systemOutRule.enableLog()
        systemErrRule.clearLog()
        systemErrRule.enableLog()

        new File('build/TiesValidateCliTests').mkdirs()
        new File('build/TiesValidateCliTests/success1.json').with {
            createNewFile()
            write(minimalValidJson)
        }
        new File('build/TiesValidateCliTests/success2.json').with {
            createNewFile()
            write(minimalValidJson)
        }
        new File('build/TiesValidateCliTests/failure1.json').with {
            createNewFile()
            write(minimalInvalidJson)
        }
        new File('build/TiesValidateCliTests/failure2.json').with {
            createNewFile()
            write(minimalInvalidJson)
        }
    }

    @After
    void tearDown() {
        new File('build/TiesValidateCliTests').deleteDir()
    }

    @Override
    SystemOutRule getSystemOutRule() {
        return systemOutRule
    }

    @Override
    SystemErrRule getSystemErrRule() {
        return systemErrRule
    }

    @Override
    TextFromStandardInputStream getSystemInRule() {
        return systemInRule
    }

    @Override
    ExpectedSystemExit getExitRule() {
        return exitRule
    }

    @Override
    void runMain(String[] args) {
        TiesValidateCli.main(args)
    }

    @Test
    void test_help_short() {
        new TemplateTest(this).with {
            args = ['-h']
            expectedExitCode = 2
            stdout longUsage
            stderr ''
            execute()
        }
    }

    @Test
    void test_help_long() {
        new TemplateTest(this).with {
            args = ['--help']
            expectedExitCode = 2
            stdout longUsage
            stderr ''
            execute()
        }
    }

    @Test
    void test_no_args_success() {
        new TemplateTest(this).with {
            args = []
            stdin minimalValidJson
            expectedExitCode = 0
            stdout statusDone('Validating stdin')
            stderr ''
            execute()
        }
    }

    @Test
    void test_no_args_failure() {
        new TemplateTest(this).with {
            args = []
            stdin minimalInvalidJson
            expectedExitCode = 1
            stdout statusError('Validating stdin')
            stdout 'Schema validation was unsuccessful:'
            stdout 'error:'
            stdout '    required properties [objectItems, securityTag, version] are missing'
            stdout '    location: /'
            stderr ''
            execute()
        }
    }

    @Test
    void test_stdin_success() {
        new TemplateTest(this).with {
            args = ['-']
            stdin minimalValidJson
            expectedExitCode = 0
            stdout statusDone('Validating stdin')
            stderr ''
            execute()
        }
    }

    @Test
    void test_stdin_failure() {
        new TemplateTest(this).with {
            args = ['-']
            stdin minimalInvalidJson
            expectedExitCode = 1
            stdout statusError('Validating stdin')
            stdout 'Schema validation was unsuccessful:'
            stdout 'error:'
            stdout '    required properties [objectItems, securityTag, version] are missing'
            stdout '    location: /'
            stderr ''
            execute()
        }
    }

    @Test
    void test_one_file_success() {
        new TemplateTest(this).with {
            args = ['build/TiesValidateCliTests/success1.json']
            expectedExitCode = 0
            stdout statusDone("Validating ${path('build/TiesValidateCliTests/success1.json')}")
            stderr ''
            execute()
        }
    }

    @Test
    void test_one_file_failure() {
        new TemplateTest(this).with {
            args = ['build/TiesValidateCliTests/failure1.json']
            expectedExitCode = 1
            stdout statusError("Validating ${path('build/TiesValidateCliTests/failure1.json')}")
            stdout 'Schema validation was unsuccessful:'
            stdout 'error:'
            stdout '    required properties [objectItems, securityTag, version] are missing'
            stdout '    location: /'
            stderr ''
            execute()
        }
    }

    @Test
    void test_two_files_success() {
        new TemplateTest(this).with {
            args = ['build/TiesValidateCliTests/success1.json', 'build/TiesValidateCliTests/success2.json']
            expectedExitCode = 0
            stdout statusDone("Validating ${path('build/TiesValidateCliTests/success1.json')}")
            stdout statusDone("Validating ${path('build/TiesValidateCliTests/success2.json')}")
            stderr ''
            execute()
        }
    }

    @Test
    void test_two_files_failure() {
        new TemplateTest(this).with {
            args = ['build/TiesValidateCliTests/failure1.json', 'build/TiesValidateCliTests/failure2.json']
            expectedExitCode = 1
            stdout statusError("Validating ${path('build/TiesValidateCliTests/failure1.json')}")
            stdout 'Schema validation was unsuccessful:'
            stdout 'error:'
            stdout '    required properties [objectItems, securityTag, version] are missing'
            stdout '    location: /'
            stdout statusError("Validating ${path('build/TiesValidateCliTests/failure2.json')}")
            stdout 'Schema validation was unsuccessful:'
            stdout 'error:'
            stdout '    required properties [objectItems, securityTag, version] are missing'
            stdout '    location: /'
            stderr ''
            execute()
        }
    }

    @Test
    void test_one_glob_success() {
        new TemplateTest(this).with {
            args = ['build/TiesValidateCliTests/success*.json']
            expectedExitCode = 0
            stdout statusDone("Validating ${path('build/TiesValidateCliTests/success1.json')}")
            stdout statusDone("Validating ${path('build/TiesValidateCliTests/success2.json')}")
            stderr ''
            execute()
        }
    }

    @Test
    void test_one_glob_failure() {
        new TemplateTest(this).with {
            args = ['build/TiesValidateCliTests/failure*.json']
            expectedExitCode = 1
            stdout statusError("Validating ${path('build/TiesValidateCliTests/failure1.json')}")
            stdout 'Schema validation was unsuccessful:'
            stdout 'error:'
            stdout '    required properties [objectItems, securityTag, version] are missing'
            stdout '    location: /'
            stdout statusError("Validating ${path('build/TiesValidateCliTests/failure2.json')}")
            stdout 'Schema validation was unsuccessful:'
            stdout 'error:'
            stdout '    required properties [objectItems, securityTag, version] are missing'
            stdout '    location: /'
            stderr ''
            execute()
        }
    }

    @Test
    void test_two_globs_success() {
        new TemplateTest(this).with {
            args = ['build/TiesValidateCliTests/success1.*', 'build/TiesValidateCliTests/success2.*']
            expectedExitCode = 0
            stdout statusDone("Validating ${path('build/TiesValidateCliTests/success1.json')}")
            stdout statusDone("Validating ${path('build/TiesValidateCliTests/success2.json')}")
            stderr ''
            execute()
        }
    }

    @Test
    void test_two_globs_failure() {
        new TemplateTest(this).with {
            args = ['build/TiesValidateCliTests/failure1.*', 'build/TiesValidateCliTests/failure2.*']
            expectedExitCode = 1
            stdout statusError("Validating ${path('build/TiesValidateCliTests/failure1.json')}")
            stdout 'Schema validation was unsuccessful:'
            stdout 'error:'
            stdout '    required properties [objectItems, securityTag, version] are missing'
            stdout '    location: /'
            stdout statusError("Validating ${path('build/TiesValidateCliTests/failure2.json')}")
            stdout 'Schema validation was unsuccessful:'
            stdout 'error:'
            stdout '    required properties [objectItems, securityTag, version] are missing'
            stdout '    location: /'
            stderr ''
            execute()
        }
    }

    @Test
    void test_file_fnf() {
        new TemplateTest(this).with {
            args = ['build/TiesValidateCliTests/fnf.json']
            expectedExitCode = 1
            stdout statusError("Validating ${path('build/TiesValidateCliTests/fnf.json')}")
            stdout 'Schema validation was unsuccessful:'
            stdout "${path('build/TiesValidateCliTests/fnf.json')} (No such file or directory)"
            stderr ''
            execute()
        }
    }

    @Test
    void test_glob_fnf() {
        new TemplateTest(this).with {
            args = ['build/TiesValidateCliTests/fnf*.json']
            expectedExitCode = 1
            stdout statusError("Validating ${path('build/TiesValidateCliTests/fnf*.json')}")
            stdout 'Schema validation was unsuccessful:'
            stdout "${path('build/TiesValidateCliTests/fnf*.json')} (No such file or directory)"
            stderr ''
            execute()
        }
    }
}
