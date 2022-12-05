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

class TiesConvertCliTests implements ITemplateTest {

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

    private static final String shortUsage = 'usage: ties-convert [-h] [--version] [--classification-level SECURITY_TAG] [--output-file OUTPUT_FILE | --in-place] EXPORT_PATH'

    private static final String longUsage = """\
${shortUsage}

Converts TIES export.json files from older versions of the schema (0.1.8, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9) to the current version (1.0).

positional arguments:
  EXPORT_PATH            the path to the TIES JSON file or - to read from stdin

named arguments:
  -h, --help             show this help message and exit
  --classification-level SECURITY_TAG, -c SECURITY_TAG
                         the classification level of the TIES JSON, required for TIES JSON from pre-0.3 versions of the schema
  --output-file OUTPUT_FILE, -f OUTPUT_FILE
                         the output file path for the converted TIES JSON
  --in-place, -i         modifies the input file in-place, overwriting it with the converted JSON data
  --version              prints version information
"""

    private static final String testInput = """\
{
    "version": "0.1.8",
    "objectItem": [
        {
            "sha256Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "md5Hash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        }
    ]
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
    private static File outputFile

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
        outputFile = File.createTempFile('output', '.json')
        outputFile.deleteOnExit()
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
        TiesConvertCli.main(args)
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
    void test_no_args() {
        new TemplateTest(this).with {
            args = []
            expectedExitCode = 2
            stdout ''
            stderr shortUsage
            stderr 'ties-convert: error: too few arguments'
            execute()
        }
    }

    @Test
    void test_stdin_stdout() {
        new TemplateTest(this).with {
            args = ['-', '--classification-level', 'UNCLASSIFIED']
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
            args = [inputFile.absolutePath, '--classification-level', 'UNCLASSIFIED']
            expectedExitCode = 0
            stdout testOutput
            execute()
        }
    }

    @Test
    void test_stdin_outfile() {
        new TemplateTest(this).with {
            args = ['-', '-f', outputFile.absolutePath, '--classification-level', 'UNCLASSIFIED']
            expectedExitCode = 0
            stdin testInput
            stdout ''
            execute()
        }
        assert outputFile.text == testOutput
    }

    @Test
    void test_infile_outfile() {
        new TemplateTest(this).with {
            args = [inputFile.absolutePath, '-f', outputFile.absolutePath, '--classification-level', 'UNCLASSIFIED']
            expectedExitCode = 0
            stdout ''
            execute()
        }
        assert outputFile.text == testOutput
    }

    @Test
    void test_inplace() {
        new TemplateTest(this).with {
            args = ['-i', inputFile.absolutePath, '--classification-level', 'UNCLASSIFIED']
            expectedExitCode = 0
            execute()
        }
        assert inputFile.text == testOutput
    }

    @Test
    void test_inplace_stdin() {
        new TemplateTest(this).with {
            args = ['-i', '-', '--classification-level', 'UNCLASSIFIED']
            expectedExitCode = 0
            stdin testInput
            stdout testOutput
            execute()
        }
    }

    @Test
    void test_inplace_outfile_error() {
        new TemplateTest(this).with {
            args = ['-i', '-f', outputFile.absolutePath, inputFile.absolutePath, '--classification-level', 'UNCLASSIFIED']
            expectedExitCode = 2
            stderr shortUsage
            stderr 'ties-convert: error: argument --output-file/-f: not allowed with argument --in-place/-i'
            execute()
        }
    }

    @Test
    void test_inplace_write_error() {
        inputFile.setWritable(false)
        new TemplateTest(this).with {
            args = ['-i', inputFile.absolutePath, '--classification-level', 'UNCLASSIFIED']
            expectedExitCode = 1
            stdout ''
            stderr "error: could not write to file: ${inputFile.absolutePath}"
            execute()
        }
    }

    @Test
    void test_stdin_parse_exception() {
        new TemplateTest(this).with {
            args = ['-', '--classification-level', 'UNCLASSIFIED']
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
            args = ['/file/not/found', '--classification-level', 'UNCLASSIFIED']
            expectedExitCode = 1
            stdout ''
            stderr 'error: could not read from file: /file/not/found'
            execute()
        }
    }

    @Test
    void test_infile_parse_exception() {
        new TemplateTest(this).with {
            args = ['/dev/null', '--classification-level', 'UNCLASSIFIED']
            expectedExitCode = 1
            stdout ''
            stderr 'error: could not parse JSON from file: /dev/null'
            execute()
        }
    }

    @Test
    void test_outfile_fnf() {
        new TemplateTest(this).with {
            args = ['-f', '/dev/full', inputFile.absolutePath, '--classification-level', 'UNCLASSIFIED']
            expectedExitCode = 1
            stdout ''
            stderr 'error: could not write to file: /dev/full'
            execute()
        }
    }
}
