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
import net.sourceforge.argparse4j.inf.ArgumentParser
import net.sourceforge.argparse4j.inf.ArgumentParserException
import net.sourceforge.argparse4j.inf.Namespace
import org.noblis.ties.TiesFormatter
import org.noblis.ties.util.Version

class TiesFormatCli {

    private static ArgumentParser configureArgParser() {
        ArgumentParser parser = ArgumentParsers.newArgumentParser('ties-format')
        parser.usage('ties-format [-h] [--version] EXPORT_PATH')
        parser.description('Formats TIES export.json files')
        parser.version("TIES Schema Formatter\n${Version.versionString}")
        parser.addArgument('export_path')
                .help('the path to the TIES JSON file or - to read from stdin')
                .metavar('EXPORT_PATH')
                .dest('exportPath')
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

        exportJson = new TiesFormatter().reorderTiesJson(exportJson)
        Gson gson = new GsonBuilder().setPrettyPrinting().create()
        String outputJson = gson.toJson(exportJson)
        System.out.println(outputJson)
        System.exit(0)
    }
}
