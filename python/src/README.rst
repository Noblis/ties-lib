Triage Import Export Schema (TIES)
**********************************

Overview
========

The Triage Import Export Schema (TIES) specifies a data interchange format for bidirectional sharing of data. The schema
captures object items, relationships, and assertion information via a generic and flexible approach.

At its highest level, it consists of:

* An array of exported multimedia object items (videos, images, documents) and associated metadata, not including the raw multimedia data
* An array of groups of related object items
* An array of analyst annotations relating to object items included in the export
* An array of supplemental descriptions relating to object items included in the export
* Relationships between object items, annotations, and supplemental descriptions
* An array of key/value pairs containing user-defined metadata

License
=======

Copyright 2019 Noblis, Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

The above license applies to all of the Triage Import Export Schema (TIES)
source code in the TIES repository.

Python Reference Implementation
===============================

The TIES Python reference implementation can be installed using pip::

    pip install ties-lib==0.9.1

Git Repository
==============

To clone the TIES Git repository::

    git clone https://github.com/Noblis/ties-lib.git
