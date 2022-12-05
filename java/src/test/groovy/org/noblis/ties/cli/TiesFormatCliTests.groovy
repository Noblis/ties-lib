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

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.contrib.java.lang.system.TextFromStandardInputStream

class TiesFormatCliTests implements ITemplateTest {

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

    private static final String shortUsage = 'usage: ties-format [-h] [--version] EXPORT_PATH'

    private static final String longUsage = """\
${shortUsage}

Formats TIES export.json files

positional arguments:
  EXPORT_PATH            the path to the TIES JSON file or - to read from stdin

named arguments:
  -h, --help             show this help message and exit
  --version              prints version information
"""

    private static final String testInput = """\
{
    "objectItems": [
        {
            "authorityInformation": {
                "securityTag": "UNCLASSIFIED"
            },
            "md5Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "sha256Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "objectId": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        }
    ],
    "authorityInformation": {
      "securityTag": "UNCLASSIFIED"
    },
    "version": "1.0"
}"""

    private static final String testOutput = """\
{
  "version": "1.0",
  "authorityInformation": {
    "securityTag": "UNCLASSIFIED"
  },
  "objectItems": [
    {
      "objectId": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "sha256Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "md5Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "authorityInformation": {
        "securityTag": "UNCLASSIFIED"
      }
    }
  ]
}"""

    private static File inputFile

    @Before
    void setUp() {
        // ArgParse4j uses the COLUMNS env variable to determine where to wrap the help output, so we set it explicitly
        // here to make the tests work regardless of the size of the terminal.
        environmentVariables.set('COLUMNS', '160')

        systemOutRule.clearLog()
        systemOutRule.enableLog()
        systemErrRule.clearLog()
        systemErrRule.enableLog()

        inputFile = File.createTempFile('input', '.json')
        inputFile.deleteOnExit()
        inputFile.write(testInput)
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
        TiesFormatCli.main(args)
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
    void test_stdin_stdout() {
        new TemplateTest(this).with {
            args = ['-']
            expectedExitCode = 0
            stdin testInput
            stdout testOutput
            stderr ''
            execute()
        }
    }

    @Test
    void test_infile_stdout() {
        new TemplateTest(this).with {
            args = [inputFile.absolutePath]
            expectedExitCode = 0
            stdout testOutput
            execute()
        }
    }

    @Test
    void test_stdin_parse_exception() {
        new TemplateTest(this).with {
            args = ['-']
            expectedExitCode = 1
            stdin ''
            stdout ''
            stderr 'error: could not parse JSON from stdin'
            execute()
        }
    }

    @Test
    void test_infile_fnf() {
        new TemplateTest(this).with {
            args = ['/file/not/found']
            expectedExitCode = 1
            stdout ''
            stderr 'error: could not read from file: /file/not/found'
            execute()
        }
    }

    @Test
    void test_infile_parse_exception() {
        new TemplateTest(this).with {
            args = ['/dev/null']
            expectedExitCode = 1
            stdout ''
            stderr 'error: could not parse JSON from file: /dev/null'
            execute()
        }
    }
}
