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

import org.junit.contrib.java.lang.system.Assertion
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.contrib.java.lang.system.TextFromStandardInputStream

class TemplateTest {

    List<String> args
    String stdinText
    int expectedExitCode
    String expectedStdout
    String expectedStderr

    ITemplateTest testInstance

    public TemplateTest(ITemplateTest testInstance) {
        this.testInstance = testInstance
    }

    void stdin(String line) {
        if(stdinText == null) {
            stdinText = ''
        }
        stdinText += line
        stdinText += '\n'
    }

    void stdout(String line) {
        if(expectedStdout == null) {
            expectedStdout = ''
        }
        expectedStdout += line
        expectedStdout += '\n'
    }

    void stderr(String line) {
        if(expectedStderr == null) {
            expectedStderr = ''
        }
        expectedStderr += line
        expectedStderr += '\n'
    }

    void execute() {
        testInstance.exitRule.expectSystemExitWithStatus(expectedExitCode)

        testInstance.exitRule.checkAssertionAfterwards({
            if(expectedStdout != null) {
                try {
                    assert testInstance.systemOutRule.log.trim() == expectedStdout.trim()
                } catch(AssertionError ignore) {
                    String message = '\n' + [
                            '-' * 80,
                            'Expected stdout:',
                            expectedStdout.trim(),
                            '-' * 80,
                            'Actual stdout:',
                            testInstance.systemOutRule.log.trim(),
                            '-' * 80,
                    ].join('\n')
                    throw new AssertionError(message)
                }
            }
            if(expectedStderr != null) {
                try {
                    assert testInstance.systemErrRule.log.trim() == expectedStderr.trim()
                } catch(AssertionError ignore) {
                    String message = '\n' + [
                            '-' * 80,
                            'Expected stderr:',
                            expectedStderr.trim(),
                            '-' * 80,
                            'Actual stderr:',
                            testInstance.systemErrRule.log.trim(),
                            '-' * 80,
                    ].join('\n')
                    throw new AssertionError(message)
                }
            }
        } as Assertion)

        if(stdinText != null) {
            testInstance.systemInRule.provideLines(stdinText.split('\n'))
        }
        testInstance.runMain(args as String[])
    }
}

public interface ITemplateTest {

    SystemOutRule getSystemOutRule()

    SystemErrRule getSystemErrRule()

    TextFromStandardInputStream getSystemInRule()

    ExpectedSystemExit getExitRule()

    void runMain(String[] args)
}
