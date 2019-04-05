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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.json.JsonException
import groovy.json.JsonSlurper
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.impl.Arguments
import net.sourceforge.argparse4j.impl.action.StoreTrueArgumentAction
import net.sourceforge.argparse4j.inf.ArgumentParser
import net.sourceforge.argparse4j.inf.ArgumentParserException
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup
import net.sourceforge.argparse4j.inf.Namespace
import org.noblis.ties.TiesConverter
import org.noblis.ties.TiesFormatter
import org.noblis.ties.util.Version

class TiesConvertCli {

    private static ArgumentParser configureArgParser() {
        ArgumentParser parser = ArgumentParsers.newArgumentParser('ties-convert')
        parser.usage('ties-convert [-h] [--version] [--classification-level SECURITY_TAG] [--output-file OUTPUT_FILE | --in-place] EXPORT_PATH')
        parser.description('Converts TIES export.json files from older versions of the schema (0.1.8, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8) to the current version (0.9).')
        parser.version("TIES Schema Converter\n${Version.versionString}")
        parser.addArgument('export_path')
                .help('the path to the TIES JSON file or - to read from stdin')
                .metavar('EXPORT_PATH')
                .dest('exportPath')
        parser.addArgument('--classification-level', '-c')
                .help('the classification level of the TIES JSON, required for TIES JSON from pre-0.3 versions of the schema')
                .metavar('SECURITY_TAG')
                .dest('securityTag')
        MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup()
        group.addArgument('--output-file', '-f')
                .help('the output file path for the converted TIES JSON')
                .metavar('OUTPUT_FILE')
                .dest('outputFile')
        group.addArgument('--in-place', '-i')
                .help('modifies the input file in-place, overwriting it with the converted JSON data')
                .action(new StoreTrueArgumentAction())
                .dest('inPlace')
        parser.addArgument('--version')
                .help('prints version information')
                .action(Arguments.version())
        return parser
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

        String exportPath = parsedArgs.getString('exportPath')
        String securityTag = parsedArgs.getString('securityTag')
        String outputFile = parsedArgs.getString('outputFile')
        Boolean inPlace = parsedArgs.getBoolean('inPlace')

        Map exportJson
        if (exportPath == '-') {
            // read input from stdin
            try {
                exportJson = new JsonSlurper().parse(System.in, 'UTF-8') as Map
            } catch(JsonException ignore) {
                System.err.println("error: could not parse JSON from stdin")
                System.exit(1)
            }
        } else {
            try {
                // read input from a file
                new File(exportPath).withInputStream {
                    exportJson = new JsonSlurper().parse(it, 'UTF-8') as Map
                }
            } catch(IOException ignore) {
                System.err.println("error: could not read from file: ${exportPath}")
                System.exit(1)
            } catch(JsonException ignore) {
                System.err.println("error: could not parse JSON from file: ${exportPath}")
                System.exit(1)
            }
        }

        new TiesConverter().convert(exportJson, securityTag)
        Gson gson = new GsonBuilder().setPrettyPrinting().create()
        String outputJson = gson.toJson(TiesFormatter.reorderTiesJson(exportJson))

        if (outputFile != null) {
            // write output to the specified file path
            try {
                new File(outputFile).write(outputJson, 'UTF-8')
                System.exit(0)
            } catch(IOException ignore) {
                System.err.println("error: could not write to file: ${outputFile}")
                System.exit(1)
            }
        } else if (inPlace) {
            if (exportPath == '-') {
                // input came from stdin, write output to stdout
                System.out.println(outputJson)
                System.exit(0)
            } else {
                // input came from a file, write output back to the same file
                try {
                    new File(exportPath).write(outputJson, 'UTF-8')
                    System.exit(0)
                } catch(IOException ignore) {
                    System.err.println("error: could not write to file: ${exportPath}")
                    System.exit(1)
                }
            }
        } else {
            // no output file and not in-place, write output to stdout
            System.out.println(outputJson)
            System.exit(0)
        }
    }
}
