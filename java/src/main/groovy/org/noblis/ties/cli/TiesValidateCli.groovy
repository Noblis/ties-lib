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

import com.fasterxml.jackson.core.JsonParseException
import groovy.json.JsonSlurper
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.impl.Arguments
import net.sourceforge.argparse4j.inf.ArgumentParser
import net.sourceforge.argparse4j.inf.ArgumentParserException
import net.sourceforge.argparse4j.inf.Namespace
import org.noblis.ties.SemanticValidator
import org.noblis.ties.TiesValidator
import org.noblis.ties.ValidationException
import org.noblis.ties.ValidationWarning
import org.noblis.ties.util.GlobTrait
import org.noblis.ties.util.Version

public class TiesValidateCli implements GlobTrait {

    private static String indent(String text, String prefix) {
        if (text.size() == 0) {
            return text
        }
        return prefix + text.replace('\n', "\n${prefix}")
    }

    private static boolean doValidate(InputStream inputStream) {
        try {
            Map json = new JsonSlurper().parse(inputStream, 'UTF-8') as Map
            List<ValidationException> validationExceptions = new TiesValidator().allErrors(json)
            if (validationExceptions.size() > 0) {
                println 'ERROR'
                println 'Schema validation was unsuccessful:'
                validationExceptions.each {
                    println 'error:'
                    println indent(it.toString(), ' ' * 4)
                }
                return true
            }
            List<ValidationWarning> validationWarnings = new SemanticValidator().allWarnings(json)
            if (validationWarnings.size() > 0) {
                println 'WARNING'
                println 'Schema validation completed with warnings:'
                validationWarnings.each {
                    println 'warning:'
                    println indent(it.toString(), ' ' * 4)
                }
                return true
            }
            println 'done'
            return false
        } catch(JsonParseException ex) {
            println 'ERROR'
            println 'Schema validation was unsuccessful:'
            println ex.message
            return true
        } catch(Exception ex) {
            println 'ERROR'
            println 'Schema validation was unsuccessful:'
            ex.printStackTrace()
            return true
        }
    }

    private static boolean validate(InputStream inputStream) {
        printStatus('Validating stdin')
        return doValidate(inputStream)
    }

    private static boolean validate(File inputFile) {
        try {
            printStatus("Validating ${inputFile.canonicalPath}")
            inputFile.withInputStream {
                return doValidate(it)
            }
        } catch(FileNotFoundException ex) {
            println 'ERROR'
            println 'Schema validation was unsuccessful:'
            println ex.message
            return true
        }
    }

    private static void printStatus(String s) {
        print "${s}${'.' * (160 - s.size() - 5)}"
    }

    private static ArgumentParser configureArgParser() {
        ArgumentParser parser = ArgumentParsers.newArgumentParser('ties-validate')
        parser.usage('ties-validate [-h] [--version] [FILE]...')
        parser.description('Validate FILE(s), or standard input, against the TIES 1.0 schema.')
        parser.epilog('''\
If FILE arguments are provided, attempts to validate all files. FILE arguments may be provided as either file paths or shell globs.

If no FILE arguments are provided, attempts to read a single JSON object from stdin and validate it.

Returns non-zero exit code if one or more input files fail to validate successfully.''')
        parser.version("TIES Schema Validator\n${Version.versionString}")
        parser.addArgument('files')
                .help('the path to the JSON file(s) to be validated against the schema or - to read from stdin')
                .metavar('FILE')
                .dest('files')
                .nargs('*')
        parser.addArgument('--version')
                .help('prints version information')
                .action(Arguments.version())
        return parser
    }

    private static List<File> findInputFiles(List<String> globPatterns) {
        //attempt to expand each path as a shell glob, if nothing matches the glob, add the glob pattern to the list of input files
        List<File> inputFiles = globPatterns.collectMany { globFiles(it) ?: [new File(it)] }
        //convert paths to canonical paths
        inputFiles = inputFiles.collect { new File(it.canonicalPath) }
        //remove duplicate paths
        inputFiles = inputFiles.unique({ it.canonicalPath })
        //sort paths
        inputFiles = inputFiles.sort({ it.canonicalPath })
        return inputFiles
    }

    public static void main(String[] args) {
        ArgumentParser parser = configureArgParser()
        Namespace parsedArgs
        try {
            parsedArgs = parser.parseArgs(args)
        } catch(ArgumentParserException e) {
            parser.handleError(e)
            System.exit(2)
        }

        List<String> files = parsedArgs.getList('files')

        boolean hasError
        if(!files || files == ['-']) {
            //no args were provided, look for input on stdin
            hasError = validate(System.in)
        } else {
            //a list of paths or shell globs was provided
            hasError = findInputFiles(files).collect({ validate(it) }).any()
        }

        if(hasError) {
            //return non-zero exit code if there was an error validating one or more input files
            System.exit(1)
        } else {
            //return zero exit code if all input files validated successfully
            System.exit(0)
        }
    }
}
