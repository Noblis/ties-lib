.. _convert-label:

TIES Schema Converter
*********************


Install Schema Converter
========================

The Java and Python implementations of the TIES Schema Converter are distributed as part of each TIES release.

Java
----

The Java TIES converter requires Java 1.7 or 1.8. To install the converter:

1. Copy the \*.zip or \*.tgz archive from *TIES_RELEASE_DIR*/java/distributions to the desired install location
2. Extract the \*.zip or \*.tgz archive
3. Add the bin directory of the extracted archive to your PATH

Python
------

.. highlight:: none

The Python TIES converter requires Python 3.6+ with pip installed.

To install the converter::

    pip install ties-lib==0.9.1.1

Usage
=====

.. highlight:: none

::

    usage: ties-convert [-h] [--version] [--classification-level SECURITY_TAG] [--output-file OUTPUT_FILE | --in-place] EXPORT_PATH

    Converts TIES export.json files from older versions of the schema (0.1.8, 0.2,
    0.3, 0.4, 0.5, 0.6, 0.7, 0.8) to the current version (0.9).

    positional arguments:
      EXPORT_PATH           the path to the TIES JSON file or - to read from stdin

    optional arguments:
      -h, --help            show this help message and exit
      --classification-level SECURITY_TAG, -c SECURITY_TAG
                            the classification level of the TIES JSON, required
                            for TIES JSON from pre-0.3 versions of the schema
      --output-file OUTPUT_FILE, -f OUTPUT_FILE
                            the output file path for the converted TIES JSON
      --in-place, -i        modifies the input file in-place, overwriting it with
                            the converted JSON data
      --version             prints version information

Examples
========

.. highlight:: none

Convert TIES JSON from stdin, write to stdout::

    cat export.json | ties-convert -

Convert TIES JSON (without top-level securityTag field) from stdin, write to stdout::

    cat export.json | ties-convert - -c UNCLASSIFIED

Convert TIES JSON from stdin, write to a file::

    cat export.json | ties-convert - -f export.converted.json

Convert TIES JSON (without top-level securityTag field) from stdin, write to a file::

    cat export.json | ties-convert - -c UNCLASSIFIED -f export.converted.json

Convert TIES JSON from file, write to stdout::

    ties-convert export.json

Convert TIES JSON (without top-level securityTag field) from file, write to stdout::

    ties-convert export.json -c UNCLASSIFIED

Convert TIES JSON from file, write to a different file::

    ties-convert export.json -f export.converted.json

Convert TIES JSON (without top-level securityTag field) from file, write to a different file::

    ties-convert export.json -f export.converted.json -c UNCLASSIFIED

Convert TIES JSON from file, write to the same file::

    ties-convert export.json -i

Convert TIES JSON (without top-level securityTag field) from file, write to the same file::

    ties-convert export.json -i -c UNCLASSIFIED
