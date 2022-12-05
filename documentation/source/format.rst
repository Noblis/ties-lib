.. _format-label:

TIES Schema Formatter
*********************


Install Schema Formatter
========================

The Java and Python implementations of the TIES Schema Formatter are distributed as part of each TIES release.

Java
----

The Java TIES formatter requires Java 1.7 or 1.8. To install the formatter:

1. Copy the \*.zip or \*.tgz archive from *TIES_RELEASE_DIR*/java/distributions to the desired install location
2. Extract the \*.zip or \*.tgz archive
3. Add the bin directory of the extracted archive to your PATH

Python
------

.. highlight:: none

The Python TIES formatter requires Python 3.6+ with pip installed.

To install the formatter::

    pip install ties-lib==1.0.0

Usage
=====

.. highlight:: none

::

    usage: ties-format [-h] [--version] EXPORT_PATH

    Formats TIES export.json files

    positional arguments:
      EXPORT_PATH  the path to the TIES JSON file or - to read from stdin

    optional arguments:
      -h, --help   show this help message and exit
      --version    prints version information

Examples
========

.. highlight:: none

Format TIES JSON from stdin, write to stdout::

    cat export.json | ties-format -

Format TIES JSON from file, write to stdout::

    ties-format export.json
