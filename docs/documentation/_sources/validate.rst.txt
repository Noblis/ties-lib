.. _validate-label:

TIES Schema Validator
*********************


Install Schema Validator
========================

The Java and Python implementations of the TIES Schema Validator are distributed as part of each TIES release.

Java
----

The Java TIES validator requires Java 1.7 or 1.8. To install the validator:

1. Copy the \*.zip or \*.tgz archive from *TIES_RELEASE_DIR*/java/distributions to the desired install location
2. Extract the \*.zip or \*.tgz archive
3. Add the bin directory of the extracted archive to your PATH

Python
------

The Python TIES validator requires Python 2.7 or Python 3.4+ with pip installed.

To install the validator:

    pip install ties-lib==0.9.1

Usage
=====

.. highlight:: none

::

    usage: ties-validate [-h] [--version] [FILE]...

    Validate FILE(s), or standard input, against the TIES 0.9 schema.

    positional arguments:
      FILE        the path to the JSON file(s) to be validated against the schema
                  or - to read from stdin

    optional arguments:
      -h, --help  show this help message and exit
      --version   prints version information

    If FILE arguments are provided, attempts to validate all files. FILE arguments may be provided as either file paths or shell globs.

    If no FILE arguments are provided, attempts to read a single JSON object from stdin and validate it.

    Returns non-zero exit code if one or more input files fail to validate successfully.

Examples
========

.. highlight:: none

Validate a TIES JSON from stdin::

    cat export.json | ties-validate -
    cat export.json | ties-validate

Validate a TIES JSON from a file::

    ties-validate export.json

Validate multiple TIES JSON files::

    ties-validate export1.json export2.json

Validate multiple TIES JSON files with a shell glob (wildcard)::

    ties-validate *.json

