.. _schema-label:

TIES Schema Objects
*******************

`TIES 0.9 JSON Schema <../../schemata/ties-base.json>`_

.. _top-level-label:

**Top-level Object**
====================

version **[required]**
----------------------

*enum* : ["0.9"]

TIES Schema version that the export conforms to

id
--

*string*

Unique identifier of the export (scoped to the exporting system)

system
------

*string*

Name or identifier of the exporting system

organization
------------

*string*

Name or identifier of the organization that created the export

time
----

*date-time*

Date and time that the export was created

The date-time value should be formatted as an ISO 8601 datetime of YYYY-MM-DDThh:mm:ssZ or YYYY-MM-DDThh:mm:ss.sssZ in
UTC time.

description
-----------

*string*

Human-readable free-form text description of the export

type
----

*string*

Identifier indicating the type of export

securityTag **[required]**
--------------------------

*string*

Security markings associated with the export

.. _top-level-object-items-label:

objectItems **[required]**
--------------------------

*array* : :ref:`object-item-label`

Array of multimedia object items

.. _top-level-object-groups-label:

objectGroups
------------

*array* : :ref:`object-group-label`

Array of object groups

objectRelationships
-------------------

*array* : :ref:`object-relationship-label`

Array of object item and object group relationships

otherInformation
----------------

*array* : :ref:`other-information-label`

Array of key/value pairs containing additional information about the export


.. _object-item-label:

**objectItem**
==============

.. _object-item-object-id-label:

objectId **[required]**
-----------------------

*string*

Unique identifier of the exported object (scoped to the exporting system)

sha256Hash **[required]**
-------------------------

*string*

SHA-256 file hash of the exported object

md5Hash **[required]**
----------------------

*string*

MD5 file hash of the exported object

size
----

*integer*

Size of the exported object in bytes

mimeType
--------

*string*

MIME type of the exported object

relativeUri
-----------

*string*

Uniform Resource Identifier (URI) describing the location of the exported object within the export package

The relativeUri property should be included if and only if the exported object is included in the export package as a
file. The relativeUri property should be included if the exported object exists as a file in the export package, and
should not be included if the exported object does not exist as a file in the export package.

originalPath
------------

*string*

Absolute file path of the exported object within the originating filesystem

authorityInformation **[required]**
-----------------------------------

*object* : :ref:`authority-information-label`

Object containing authority information metadata about the exported object

.. _object-item-object-assertions-label:

objectAssertions
----------------

*object* : :ref:`assertions-label`

Object containing collections of metadata assertions about the exported object

otherInformation
----------------

*array* : :ref:`other-information-label`

Array of key/value pairs containing additional information about the exported object


.. _object-group-label:

**objectGroup**
===============

.. _object-group-group-id-label:

groupId
-------

*string*

Unique identifier of the group (scoped to the exporting system)

groupType
---------

*string*

Identifier indicating the type of group

groupDescription
----------------

*string*

Human-readable free-form text description of the group

groupMemberIds
--------------

*array* : string

List of :ref:`objectIds <object-item-object-id-label>` of exported objects and/or
:ref:`groupIds <object-group-group-id-label>` of other groups that are members of the group

Members of a group can either be :ref:`objectItems <object-item-label>` referenced by
:ref:`objectId <object-item-object-id-label>` or :ref:`objectGroups <object-group-label>` referenced by
:ref:`groupId <object-group-group-id-label>`. A single group may contain both :ref:`objectItems <object-item-label>` and
:ref:`objectGroups <object-group-label>`.

All :ref:`objectItems <object-item-label>` and :ref:`objectGroups <object-group-label>` that are members of a group
should be present in the export.

.. _object-group-group-assertions-label:

groupAssertions
---------------

*object* : :ref:`assertions-label`

Object containing collections of metadata assertions about the group

otherInformation
----------------

*array* : :ref:`other-information-label`

Array of key/value pairs containing additional information about the group


.. _object-relationship-label:

**objectRelationship**
======================

.. _object-relationship-linkage-system-ids-label:

linkageMemberIds **[required]**
-------------------------------

*array* : string

Pair of :ref:`objectIds <object-item-object-id-label>` of exported objects and/or
:ref:`groupIds <object-group-group-id-label>` of groups that are connected by this relationship

Members of a relationship can either be :ref:`objectItems <object-item-label>` referenced by
:ref:`objectId <object-item-object-id-label>` or :ref:`objectGroups <object-group-label>` referenced by
:ref:`groupId <object-group-group-id-label>`. A single relationship may connect two
:ref:`objectItems <object-item-label>`, two :ref:`objectGroups <object-group-label>`, or an
:ref:`objectItem <object-item-label>` and an :ref:`objectGroup <object-group-label>`.

All :ref:`objectItems <object-item-label>` or :ref:`objectGroups <object-group-label>` that are connected by a
relationship should be present in the export.

linkageDirectionality **[required]**
------------------------------------

*enum* : ["DIRECTED", "BIDIRECTED", "UNDIRECTED"]

Enumeration value indicating the directionality of the relationship

A DIRECTED linkage indicates a relationship from the first
:ref:`linkageMemberId <object-relationship-linkage-system-ids-label>` to the second. A BIDIRECTED linkage indicates a
relationship from the first :ref:`linkageMemberId <object-relationship-linkage-system-ids-label>` to the second, and
from the second :ref:`linkageMemberId <object-relationship-linkage-system-ids-label>` to the first. An UNDIRECTED
linkage indicates a relationship with no directionality.

linkageType
-----------

*string*

Identifier indicating the type of relationship

linkageAssertionId
------------------

*string*

assertionId of an assertion that is associated with this relationship

The linkageAssertionId can be used to associate an :ref:`objectRelationship <object-relationship-label>` with an
:ref:`assertion <assertions-label>` (either an :ref:`annotation <annotation-label>` or a supplementalDescription
(:ref:`[Data File]<supplemental-description-data-file-label>`,
:ref:`[Data Object] <supplemental-description-data-object-label>`) by referencing the
:ref:`assertion's <assertions-label>` assertionId property.

The referenced assertion should be present within the :ref:`objectAssertions <object-item-object-assertions-label>` of
an :ref:`objectItem <object-item-label>` or the :ref:`groupAssertions <object-group-group-assertions-label>` of an
:ref:`objectGroup <object-group-label>` connected by this relationship.

otherInformation
----------------

*array* : :ref:`other-information-label`

Array of key/value pairs containing additional information about the relationship


.. _other-information-label:

**otherInformation**
====================

key **[required]**
------------------

*string*

value **[required]**
--------------------

*string*, *boolean*, *integer*, *number*


.. _authority-information-label:

**authorityInformation**
========================

.. _authority-information-collection-id-label:

collectionId
------------

*string*

Unique identifier of the collection the exported object is associated with (scoped to the exporting system)

collectionIdLabel
-----------------

*string*

Descriptor indicating the type of collection

collectionIdAlias
-----------------

*string*

Human-readable alias for the :ref:`collectionId <authority-information-collection-id-label>`

collectionDescription
---------------------

*string*

Human-readable free-form text description of the collection

.. _authority-information-sub-collection-id-label:

subCollectionId
---------------

*string*

Unique identifier of the sub-collection the exported object is associated with (scoped to the collection)

subCollectionIdLabel
--------------------

*string*

Descriptor indicating the type of sub-collection

subCollectionIdAlias
--------------------

*string*

Human-readable alias for the :ref:`subCollectionId <authority-information-sub-collection-id-label>`

subCollectionDescription
------------------------

*string*

Human-readable free-form text description of the sub-collection

registrationDate
----------------

*date-time*

Date and time that the exported object was brought into system control

The date-time value should be formatted as an ISO 8601 datetime of YYYY-MM-DDThh:mm:ssZ or YYYY-MM-DDThh:mm:ss.sssZ in
UTC time.

expirationDate
--------------

*date-time*

Date and time that the retention of the exported object expires

The date-time value should be formatted as an ISO 8601 datetime of YYYY-MM-DDThh:mm:ssZ or YYYY-MM-DDThh:mm:ss.sssZ in
UTC time.

owner
-----

*string*

Name or identifier of the owner of the exported object

securityTag **[required]**
--------------------------

*string*

Security markings associated with the exported object


.. _assertions-label:

**assertions**
==============

annotations
-----------

*array* : :ref:`annotation-label`

Array of objects containing metadata annotations about an exported object or group of objects

supplementalDescriptions
------------------------

*array* : :ref:`supplemental-description-data-file-label`, :ref:`supplemental-description-data-object-label`

Array of objects containing metadata about an exported object or group of objects, generated by the exporting system or
other systems


.. _annotation-label:

**annotation**
==============

assertionId **[required]**
--------------------------

*string*

Unique identifier of the assertion (scoped to the exporting system)

.. _annotation-assertion-reference-id-label:

assertionReferenceId
--------------------

*string*

Unique identifier of the assertion within the system that generated it (scoped to the generating system)

The assertionReferenceId can be used to identify the assertion in the system that generated it, when the system that
generated the assertion is not the same as the exporting system.

assertionReferenceIdLabel
-------------------------

*string*

Descriptor indicating the type of :ref:`assertionReferenceId <annotation-assertion-reference-id-label>`

system
------

*string*

Name or identifier of the system that created the assertion

creator
-------

*string*

Name or identifier of the user that created the annotation

time
----

*date-time*

Date and time that the annotation was created

The date-time value should be formatted as an ISO 8601 datetime of YYYY-MM-DDThh:mm:ssZ or YYYY-MM-DDThh:mm:ss.sssZ in
UTC time.

annotationType **[required]**
-----------------------------

*string*

Identifier indicating the type of annotation

key
---

*string*

Key of the annotation, for annotations that represent key/value pairs

value **[required]**
--------------------

*string*

Value of the annotation

itemAction
----------

*string*

Identifier indicating the action or event that is being annotated

itemActionTime
--------------

*date-time*

Date and time that the action or event that is being annotated occurred

The date-time value should be formatted as an ISO 8601 datetime of YYYY-MM-DDThh:mm:ssZ or YYYY-MM-DDThh:mm:ss.sssZ in
UTC time.

securityTag **[required]**
--------------------------

*string*

Security markings associated with the assertion


.. _supplemental-description-data-file-label:

**supplementalDescription (Data File)**
=======================================

assertionId **[required]**
--------------------------

*string*

Unique identifier of the assertion (scoped to the exporting system)

.. _supplemental-description-data-file-assertion-reference-id-label:

assertionReferenceId
--------------------

*string*

Unique identifier of the assertion within the system that generated it (scoped to the generating system)

The assertionReferenceId can be used to identify the assertion in the system that generated it, when the system that
generated the assertion is not the same as the exporting system.

assertionReferenceIdLabel
-------------------------

*string*

Descriptor indicating the type of
:ref:`assertionReferenceId <supplemental-description-data-file-assertion-reference-id-label>`

system
------

*string*

Name or identifier of the system that created the assertion

informationType **[required]**
------------------------------

*string*

Identifier indicating the type of supplemental description

sha256DataHash **[required]**
-----------------------------

*string*

SHA-256 file hash of the supplemental description data file

dataSize **[required]**
-----------------------

*integer*

Size of the supplemental description data file in bytes

dataRelativeUri
---------------

*string*

Uniform Resource Identifier (URI) describing the location of the supplemental description data file within the export
package

securityTag **[required]**
--------------------------

*string*

Security markings associated with the assertion


.. _supplemental-description-data-object-label:

**supplementalDescription (Data Object)**
=========================================

assertionId **[required]**
--------------------------

*string*

Unique identifier of the assertion (scoped to the exporting system)

.. _supplemental-description-data-object-assertion-reference-id-label:

assertionReferenceId
--------------------

*string*

Unique identifier of the assertion within the system that generated it (scoped to the generating system)

The assertionReferenceId can be used to identify the assertion in the system that generated it, when the system that
generated the assertion is not the same as the exporting system.

assertionReferenceIdLabel
-------------------------

*string*

Descriptor indicating the type of
:ref:`assertionReferenceId <supplemental-description-data-object-assertion-reference-id-label>`

system
------

*string*

Name or identifier of the system that created the assertion

informationType **[required]**
------------------------------

*string*

Identifier indicating the type of supplemental description

dataObject **[required]**
-------------------------

*object*

JSON object containing the supplemental description data

Supplemental description data can be embedded directly in the TIES export as the content of the dataObject field. This
can be used as an alternative to :ref:`supplemental-description-data-file-label` to store small amounts of JSON data.

securityTag **[required]**
--------------------------

*string*

Security markings associated with the assertion
