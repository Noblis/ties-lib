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

package org.noblis.ties

import org.junit.Before
import org.junit.Test
import org.noblis.ties.SemanticValidator
import org.noblis.ties.ValidationWarning

class SemanticValidatorTests {

    private SemanticValidator validator

    @Before
    void setUp() {
        validator = new SemanticValidator()
    }

    @Test
    void test_checkDuplicateObjectItemSha256HashesNoDuplicates() {
        def ties = [
                objectItems: [
                        [sha256Hash: 'a' * 64],
                        [sha256Hash: 'b' * 64],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectItemSha256Hashes(ties)
        assert warnings == []
    }

    @Test
    void test_checkDuplicateObjectItemSha256HashesSingleDuplicate() {
        def ties = [
                objectItems: [
                        [sha256Hash: 'a' * 64],
                        [sha256Hash: 'a' * 64],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectItemSha256Hashes(ties)
        assert warnings.size() == 1
        assert warnings[0].message == "objectItems at indexes [0, 1] have duplicate sha256Hash value ('aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa')"
        assert warnings[0].location == '/objectItems'
    }

    @Test
    void test_checkDuplicateObjectItemSha256HashesMultipleDuplicates() {
        def ties = [
                objectItems: [
                        [sha256Hash: 'a' * 64],
                        [sha256Hash: 'b' * 64],
                        [sha256Hash: 'b' * 64],
                        [sha256Hash: 'c' * 64],
                        [sha256Hash: 'c' * 64],
                        [sha256Hash: 'c' * 64],
                        [:]
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectItemSha256Hashes(ties)
        assert warnings.size() == 2
        assert warnings[0].message == "objectItems at indexes [1, 2] have duplicate sha256Hash value ('bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb')"
        assert warnings[0].location == '/objectItems'
        assert warnings[1].message == "objectItems at indexes [3, 4, 5] have duplicate sha256Hash value ('cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc')"
        assert warnings[1].location == '/objectItems'
    }

    @Test
    void test_checkDuplicateObjectItemOtherInformationKeysNoDuplicates() {
        def ties = [
                objectItems: [
                        [
                                otherInformation: [
                                        [key: 'foo'],
                                        [key: 'bar'],
                                ]
                        ]
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectItemOtherInformationKeys(ties)
        assert warnings.size() == 0
    }

    @Test
    void test_checkDuplicateObjectItemOtherInformationKeysSingleDuplicate() {
        def ties = [
                objectItems: [
                        [
                                otherInformation: [
                                        [key: 'foo'],
                                        [key: 'foo'],
                                ]
                        ]
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectItemOtherInformationKeys(ties)
        assert warnings.size() == 1
        assert warnings[0].message == "otherInformation array contains duplicate key ('foo') at indexes [0, 1]"
        assert warnings[0].location == '/objectItems[0]/otherInformation'
    }

    @Test
    void test_checkDuplicateObjectItemOtherInformationKeysMultipleDuplicates() {
        def ties = [
                objectItems: [
                        [
                                otherInformation: [
                                        [key: 'foo'],
                                        [key: 'bar'],
                                        [key: 'bar'],
                                        [key: 'baz'],
                                        [key: 'baz'],
                                        [key: 'baz'],
                                ]
                        ],
                        [
                                otherInformation: [
                                        [key: 'baz'],
                                        [key: 'baz'],
                                        [key: 'baz'],
                                        [key: 'bar'],
                                        [key: 'bar'],
                                        [key: 'foo'],
                                ]
                        ],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectItemOtherInformationKeys(ties)
        assert warnings.size() == 4
        assert warnings[0].message == "otherInformation array contains duplicate key ('bar') at indexes [1, 2]"
        assert warnings[0].location == '/objectItems[0]/otherInformation'
        assert warnings[1].message == "otherInformation array contains duplicate key ('baz') at indexes [3, 4, 5]"
        assert warnings[1].location == '/objectItems[0]/otherInformation'
        assert warnings[2].message == "otherInformation array contains duplicate key ('baz') at indexes [0, 1, 2]"
        assert warnings[2].location == '/objectItems[1]/otherInformation'
        assert warnings[3].message == "otherInformation array contains duplicate key ('bar') at indexes [3, 4]"
        assert warnings[3].location == '/objectItems[1]/otherInformation'
    }

    @Test
    void test_checkDuplicateObjectGroupOtherInformationKeysNoDuplicates() {
        def ties = [
                objectGroups: [
                        [
                                otherInformation: [
                                        [key: 'foo'],
                                        [key: 'bar'],
                                ]
                        ]
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectGroupOtherInformationKeys(ties)
        assert warnings.size() == 0
    }

    @Test
    void test_checkDuplicateObjectGroupOtherInformationKeysSingleDuplicate() {
        def ties = [
                objectGroups: [
                        [
                                otherInformation: [
                                        [key: 'foo'],
                                        [key: 'foo'],
                                ]
                        ]
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectGroupOtherInformationKeys(ties)
        assert warnings.size() == 1
        assert warnings[0].message == "otherInformation array contains duplicate key ('foo') at indexes [0, 1]"
        assert warnings[0].location == '/objectGroups[0]/otherInformation'
    }

    @Test
    void test_checkDuplicateObjectGroupOtherInformationKeysMultipleDuplicates() {
        def ties = [
                objectGroups: [
                        [
                                otherInformation: [
                                        [key: 'foo'],
                                        [key: 'bar'],
                                        [key: 'bar'],
                                        [key: 'baz'],
                                        [key: 'baz'],
                                        [key: 'baz'],
                                ]
                        ],
                        [
                                otherInformation: [
                                        [key: 'baz'],
                                        [key: 'baz'],
                                        [key: 'baz'],
                                        [key: 'bar'],
                                        [key: 'bar'],
                                        [key: 'foo'],
                                ]
                        ],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectGroupOtherInformationKeys(ties)
        assert warnings.size() == 4
        assert warnings[0].message == "otherInformation array contains duplicate key ('bar') at indexes [1, 2]"
        assert warnings[0].location == '/objectGroups[0]/otherInformation'
        assert warnings[1].message == "otherInformation array contains duplicate key ('baz') at indexes [3, 4, 5]"
        assert warnings[1].location == '/objectGroups[0]/otherInformation'
        assert warnings[2].message == "otherInformation array contains duplicate key ('baz') at indexes [0, 1, 2]"
        assert warnings[2].location == '/objectGroups[1]/otherInformation'
        assert warnings[3].message == "otherInformation array contains duplicate key ('bar') at indexes [3, 4]"
        assert warnings[3].location == '/objectGroups[1]/otherInformation'
    }

    @Test
    void test_checkDuplicateObjectIdsAndGroupIdsNoDuplicates() {
        def ties = [
                objectItems: [
                        [objectId: 'a'],
                        [objectId: 'b'],
                ],
                objectGroups: [
                        [groupId: 'c'],
                        [groupId: 'd'],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectIdsAndGroupIds(ties)
        assert warnings == []
    }

    @Test
    void test_checkDuplicateObjectIdsAndGroupIdsSingleDuplicate() {
        def ties = [
                objectItems: [
                        [objectId: 'a'],
                        [objectId: 'a'],
                ],
                objectGroups: [
                        [groupId: 'b'],
                        [groupId: 'b'],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectIdsAndGroupIds(ties)
        assert warnings.size() == 2
        assert warnings[0].message == "objectItems at indexes [0, 1] have duplicate objectId value ('a')"
        assert warnings[0].location == '/objectItems'
        assert warnings[1].message == "objectGroups at indexes [0, 1] have duplicate groupId value ('b')"
        assert warnings[1].location == '/objectGroups'
    }

    @Test
    void test_checkDuplicateObjectIdsAndGroupIdsMultipleDuplicates() {
        def ties = [
                objectItems: [
                        [objectId: 'a'],
                        [objectId: 'b'],
                        [objectId: 'b'],
                        [objectId: 'c'],
                        [objectId: 'c'],
                        [objectId: 'c'],
                        [objectId: 'x'],
                ],
                objectGroups: [
                        [groupId: 'y'],
                        [groupId: 'a'],
                        [groupId: 'b'],
                        [groupId: 'b'],
                        [groupId: 'c'],
                        [groupId: 'c'],
                        [groupId: 'c'],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectIdsAndGroupIds(ties)
        assert warnings.size() == 7
        assert warnings[0].message == "objectItems at indexes [1, 2] have duplicate objectId value ('b')"
        assert warnings[0].location == '/objectItems'
        assert warnings[1].message == "objectItems at indexes [3, 4, 5] have duplicate objectId value ('c')"
        assert warnings[1].location == '/objectItems'
        assert warnings[2].message == "objectGroups at indexes [2, 3] have duplicate groupId value ('b')"
        assert warnings[2].location == '/objectGroups'
        assert warnings[3].message == "objectGroups at indexes [4, 5, 6] have duplicate groupId value ('c')"
        assert warnings[3].location == '/objectGroups'
        assert warnings[4].message == "objectItem at index 0 and objectGroup at index 1 have duplicate objectId/groupId value ('a')"
        assert warnings[4].location == '/'
        assert warnings[5].message == "objectItems at indexes [1, 2] and objectGroups at indexes [2, 3] have duplicate objectId/groupId value ('b')"
        assert warnings[5].location == '/'
        assert warnings[6].message == "objectItems at indexes [3, 4, 5] and objectGroups at indexes [4, 5, 6] have duplicate objectId/groupId value ('c')"
        assert warnings[6].location == '/'
    }

    @Test
    void test_checkDuplicateAssertionIdsNoDuplicates() {
        def ties = [
                objectItems: [
                        [
                                objectAssertions: [
                                        annotations: [
                                                [assertionId: 'a']
                                        ],
                                        supplementalDescriptions: [
                                                [assertionId: 'b']
                                        ]
                                ]
                        ],
                        [
                                objectAssertions: [
                                        annotations: [
                                                [assertionId: 'c']
                                        ],
                                        supplementalDescriptions: [
                                                [assertionId: 'd']
                                        ]
                                ]
                        ],
                ],
                objectGroups: [
                        [
                                groupAssertions: [
                                        annotations: [
                                                [assertionId: 'e']
                                        ],
                                        supplementalDescriptions: [
                                                [assertionId: 'f']
                                        ]
                                ]
                        ],
                        [
                                groupAssertions: [
                                        annotations: [
                                                [assertionId: 'g']
                                        ],
                                        supplementalDescriptions: [
                                                [assertionId: 'h']
                                        ]
                                ]
                        ],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateAssertionIds(ties)
        assert warnings == []
    }

    @Test
    void test_checkDuplicateAssertionIdsSingleDuplicate() {
        def ties = [
                objectItems: [
                        [
                                objectAssertions: [
                                        annotations: [
                                                [assertionId: 'a']
                                        ],
                                        supplementalDescriptions: [
                                                [assertionId: 'a']
                                        ]
                                ]
                        ],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateAssertionIds(ties)
        assert warnings.size() == 2
        assert warnings[0].message == "assertion has duplicate assertionId value ('a')"
        assert warnings[0].location == '/objectItems[0]/objectAssertions/annotations[0]/assertionId'
        assert warnings[1].message == "assertion has duplicate assertionId value ('a')"
        assert warnings[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId'
    }

    @Test
    void test_checkDuplicateAssertionIdsMultipleDuplicates() {
        def ties = [
                objectItems: [
                        [
                                objectAssertions: [
                                        annotations: [
                                                [assertionId: 'a']
                                        ],
                                        supplementalDescriptions: [
                                                [assertionId: 'a']
                                        ]
                                ]
                        ],
                        [
                                objectAssertions: [
                                        annotations: [
                                                [assertionId: 'a']
                                        ],
                                        supplementalDescriptions: [
                                                [assertionId: 'a']
                                        ]
                                ]
                        ],
                ],
                objectGroups: [
                        [
                                groupAssertions: [
                                        annotations: [
                                                [assertionId: 'a']
                                        ],
                                        supplementalDescriptions: [
                                                [assertionId: 'a']
                                        ]
                                ]
                        ],
                        [
                                groupAssertions: [
                                        annotations: [
                                                [assertionId: 'a']
                                        ],
                                        supplementalDescriptions: [
                                                [assertionId: 'a']
                                        ]
                                ]
                        ],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateAssertionIds(ties)
        assert warnings.size() == 8
        assert warnings[0].message == "assertion has duplicate assertionId value ('a')"
        assert warnings[0].location == '/objectItems[0]/objectAssertions/annotations[0]/assertionId'
        assert warnings[1].message == "assertion has duplicate assertionId value ('a')"
        assert warnings[1].location == '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId'
        assert warnings[2].message == "assertion has duplicate assertionId value ('a')"
        assert warnings[2].location == '/objectItems[1]/objectAssertions/annotations[0]/assertionId'
        assert warnings[3].message == "assertion has duplicate assertionId value ('a')"
        assert warnings[3].location == '/objectItems[1]/objectAssertions/supplementalDescriptions[0]/assertionId'
        assert warnings[4].message == "assertion has duplicate assertionId value ('a')"
        assert warnings[4].location == '/objectGroups[0]/groupAssertions/annotations[0]/assertionId'
        assert warnings[5].message == "assertion has duplicate assertionId value ('a')"
        assert warnings[5].location == '/objectGroups[0]/groupAssertions/supplementalDescriptions[0]/assertionId'
        assert warnings[6].message == "assertion has duplicate assertionId value ('a')"
        assert warnings[6].location == '/objectGroups[1]/groupAssertions/annotations[0]/assertionId'
        assert warnings[7].message == "assertion has duplicate assertionId value ('a')"
        assert warnings[7].location == '/objectGroups[1]/groupAssertions/supplementalDescriptions[0]/assertionId'
    }

    @Test
    void test_checkObjectRelationshipLinkageMemberIdsSingle() {
        def ties = [
                objectItems: [
                        [objectId: 'a']
                ],
                objectGroups: [
                        [groupId: 'b']
                ],
                objectRelationships: [
                        [linkageMemberIds: ['a', 'c']]
                ]
        ]
        List<ValidationWarning> warnings = validator.checkObjectRelationshipLinkageMemberIds(ties)
        assert warnings.size() == 1
        assert warnings[0].message == "objectRelationship has a linkageMemberId ('c') that does not reference an objectItem or objectGroup in this export"
        assert warnings[0].location == '/objectRelationships[0]/linkageMemberIds[1]'
    }

    @Test
    void test_checkObjectRelationshipLinkageMemberIdsMultiple() {
        def ties = [
                objectItems: [
                        [objectId: 'a']
                ],
                objectGroups: [
                        [groupId: 'b']
                ],
                objectRelationships: [
                        [linkageMemberIds: ['a', 'b']],
                        [linkageMemberIds: ['a', 'x']],
                        [linkageMemberIds: ['x', 'y']],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkObjectRelationshipLinkageMemberIds(ties)
        assert warnings.size() == 3
        assert warnings[0].message == "objectRelationship has a linkageMemberId ('x') that does not reference an objectItem or objectGroup in this export"
        assert warnings[0].location == '/objectRelationships[1]/linkageMemberIds[1]'
        assert warnings[1].message == "objectRelationship has a linkageMemberId ('x') that does not reference an objectItem or objectGroup in this export"
        assert warnings[1].location == '/objectRelationships[2]/linkageMemberIds[0]'
        assert warnings[2].message == "objectRelationship has a linkageMemberId ('y') that does not reference an objectItem or objectGroup in this export"
        assert warnings[2].location == '/objectRelationships[2]/linkageMemberIds[1]'
    }

    @Test
    void test_checkObjectRelationshipLinkageAssertionIdSingle() {
        def ties = [
                objectItems: [
                        [
                                objectId: 'a',
                                objectAssertions: [
                                        annotations: [
                                                [assertionId: 'c']
                                        ]
                                ],
                        ]
                ],
                objectGroups: [
                        [
                                groupId: 'b',
                                groupAssertions: [
                                        supplementalDescriptions: [
                                                [assertionId: 'd']
                                        ]
                                ],
                        ]
                ],
                objectRelationships: [
                        [
                                linkageMemberIds: ['a', 'a'],
                                linkageAssertionId: 'x',
                        ]
                ]
        ]
        List<ValidationWarning> warnings = validator.checkObjectRelationshipLinkageAssertionIds(ties)
        assert warnings.size() == 1
        assert warnings[0].message == "objectRelationship has a linkageAssertionId ('x') that does not reference an assertion in this export"
        assert warnings[0].location == '/objectRelationships[0]/linkageAssertionId'
    }

    @Test
    void test_checkObjectRelationshipLinkageAssertionIdMultiple() {
        def ties = [
                objectItems: [
                        [
                                objectId: 'a',
                                objectAssertions: [
                                        annotations: [
                                                [assertionId: 'c']
                                        ]
                                ],
                        ]
                ],
                objectGroups: [
                        [
                                groupId: 'b',
                                groupAssertions: [
                                        supplementalDescriptions: [
                                                [assertionId: 'd']
                                        ]
                                ],
                        ]
                ],
                objectRelationships: [
                        [
                                linkageMemberIds: ['a', 'a'],
                                linkageAssertionId: 'x',
                        ],
                        [
                                linkageMemberIds: ['b', 'b'],
                                linkageAssertionId: 'y',
                        ],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkObjectRelationshipLinkageAssertionIds(ties)
        assert warnings.size() == 2
        assert warnings[0].message == "objectRelationship has a linkageAssertionId ('x') that does not reference an assertion in this export"
        assert warnings[0].location == '/objectRelationships[0]/linkageAssertionId'
        assert warnings[1].message == "objectRelationship has a linkageAssertionId ('y') that does not reference an assertion in this export"
        assert warnings[1].location == '/objectRelationships[1]/linkageAssertionId'
    }

    @Test
    void test_checkDuplicateObjectRelationshipOtherInformationKeysNoDuplicates() {
        def ties = [
                objectRelationships: [
                        [
                                otherInformation: [
                                        [key: 'foo'],
                                        [key: 'bar'],
                                ]
                        ]
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectRelationshipOtherInformationKeys(ties)
        assert warnings.size() == 0
    }

    @Test
    void test_checkDuplicateObjectRelationshipOtherInformationKeysSingleDuplicate() {
        def ties = [
                objectRelationships: [
                        [
                                otherInformation: [
                                        [key: 'foo'],
                                        [key: 'foo'],
                                ]
                        ]
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectRelationshipOtherInformationKeys(ties)
        assert warnings.size() == 1
        assert warnings[0].message == "otherInformation array contains duplicate key ('foo') at indexes [0, 1]"
        assert warnings[0].location == '/objectRelationships[0]/otherInformation'
    }

    @Test
    void test_checkDuplicateObjectRelationshipOtherInformationKeysMultipleDuplicates() {
        def ties = [
                objectRelationships: [
                        [
                                otherInformation: [
                                        [key: 'foo'],
                                        [key: 'bar'],
                                        [key: 'bar'],
                                        [key: 'baz'],
                                        [key: 'baz'],
                                        [key: 'baz'],
                                ]
                        ],
                        [
                                otherInformation: [
                                        [key: 'baz'],
                                        [key: 'baz'],
                                        [key: 'baz'],
                                        [key: 'bar'],
                                        [key: 'bar'],
                                        [key: 'foo'],
                                ]
                        ]
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateObjectRelationshipOtherInformationKeys(ties)
        assert warnings.size() == 4
        assert warnings[0].message == "otherInformation array contains duplicate key ('bar') at indexes [1, 2]"
        assert warnings[0].location == '/objectRelationships[0]/otherInformation'
        assert warnings[1].message == "otherInformation array contains duplicate key ('baz') at indexes [3, 4, 5]"
        assert warnings[1].location == '/objectRelationships[0]/otherInformation'
        assert warnings[2].message == "otherInformation array contains duplicate key ('baz') at indexes [0, 1, 2]"
        assert warnings[2].location == '/objectRelationships[1]/otherInformation'
        assert warnings[3].message == "otherInformation array contains duplicate key ('bar') at indexes [3, 4]"
        assert warnings[3].location == '/objectRelationships[1]/otherInformation'
    }

    @Test
    void test_checkDuplicateTopLevelOtherInformationKeysNoDuplicates() {
        def ties = [
                otherInformation: [
                        [key: 'foo'],
                        [key: 'bar'],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateTopLevelOtherInformationKeys(ties)
        assert warnings.size() == 0
    }

    @Test
    void test_checkDuplicateTopLevelOtherInformationKeysSingleDuplicate() {
        def ties = [
                otherInformation: [
                        [key: 'foo'],
                        [key: 'foo'],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateTopLevelOtherInformationKeys(ties)
        assert warnings.size() == 1
        assert warnings[0].message == "otherInformation array contains duplicate key ('foo') at indexes [0, 1]"
        assert warnings[0].location == '/otherInformation'
    }

    @Test
    void test_checkDuplicateTopLevelOtherInformationKeysMultipleDuplicates() {
        def ties = [
                otherInformation: [
                        [key: 'foo'],
                        [key: 'bar'],
                        [key: 'bar'],
                        [key: 'baz'],
                        [key: 'baz'],
                        [key: 'baz'],
                ]
        ]
        List<ValidationWarning> warnings = validator.checkDuplicateTopLevelOtherInformationKeys(ties)
        assert warnings.size() == 2
        assert warnings[0].message == "otherInformation array contains duplicate key ('bar') at indexes [1, 2]"
        assert warnings[0].location == '/otherInformation'
        assert warnings[1].message == "otherInformation array contains duplicate key ('baz') at indexes [3, 4, 5]"
        assert warnings[1].location == '/otherInformation'
    }

    @Test
    void test_validateNoWarnings() {
        def ties = [:]
        List<ValidationWarning> warnings = validator.allWarnings(ties)
        assert warnings.size() == 0
    }

    @Test
    void test_validateMultipleWarnings() {
        def ties = [
                objectItems: [
                        [
                                objectId: 'a',
                                sha256Hash: 'a' * 64,
                                otherInformation: [
                                        [key: 'foo'],
                                        [key: 'foo'],
                                ]
                        ],
                        [
                                objectId: 'a',
                                sha256Hash: 'a' * 64,
                        ],
                        [
                                objectAssertions: [
                                        annotations: [
                                                [assertionId: 'c']
                                        ]
                                ]
                        ],
                ],
                objectGroups: [
                        [
                                groupId: 'a',
                                otherInformation: [
                                        [key: 'foo'],
                                        [key: 'foo'],
                                ]
                        ],
                        [groupId: 'a'],
                        [
                                groupAssertions: [
                                        supplementalDescriptions: [
                                                [assertionId: 'c']
                                        ]
                                ]
                        ],
                ],
                objectRelationships: [
                        [
                                linkageMemberIds: ['a', 'b'],
                                linkageAssertionId: 'd',
                                otherInformation: [
                                        [key: 'foo'],
                                        [key: 'foo'],
                                ]
                        ]
                ],
                otherInformation: [
                        [key: 'foo'],
                        [key: 'foo'],
                ]
        ]
        List<ValidationWarning> warnings = validator.allWarnings(ties)
        assert warnings.size() == 12
        int i = 0
        assert warnings[i].message == "objectItems at indexes [0, 1] have duplicate sha256Hash value ('aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa')"
        assert warnings[i].location == '/objectItems'
        i++
        assert warnings[i].message == "otherInformation array contains duplicate key ('foo') at indexes [0, 1]"
        assert warnings[i].location == '/objectItems[0]/otherInformation'
        i++
        assert warnings[i].message == "otherInformation array contains duplicate key ('foo') at indexes [0, 1]"
        assert warnings[i].location == '/objectGroups[0]/otherInformation'
        i++
        assert warnings[i].message == "objectItems at indexes [0, 1] have duplicate objectId value ('a')"
        assert warnings[i].location == '/objectItems'
        i++
        assert warnings[i].message == "objectGroups at indexes [0, 1] have duplicate groupId value ('a')"
        assert warnings[i].location == '/objectGroups'
        i++
        assert warnings[i].message == "objectItems at indexes [0, 1] and objectGroups at indexes [0, 1] have duplicate objectId/groupId value ('a')"
        assert warnings[i].location == '/'
        i++
        assert warnings[i].message == "assertion has duplicate assertionId value ('c')"
        assert warnings[i].location == '/objectItems[2]/objectAssertions/annotations[0]/assertionId'
        i++
        assert warnings[i].message == "assertion has duplicate assertionId value ('c')"
        assert warnings[i].location == '/objectGroups[2]/groupAssertions/supplementalDescriptions[0]/assertionId'
        i++
        assert warnings[i].message == "objectRelationship has a linkageMemberId ('b') that does not reference an objectItem or objectGroup in this export"
        assert warnings[i].location == '/objectRelationships[0]/linkageMemberIds[1]'
        i++
        assert warnings[i].message == "objectRelationship has a linkageAssertionId ('d') that does not reference an assertion in this export"
        assert warnings[i].location == '/objectRelationships[0]/linkageAssertionId'
        i++
        assert warnings[i].message == "otherInformation array contains duplicate key ('foo') at indexes [0, 1]"
        assert warnings[i].location == '/objectRelationships[0]/otherInformation'
        i++
        assert warnings[i].message == "otherInformation array contains duplicate key ('foo') at indexes [0, 1]"
        assert warnings[i].location == '/otherInformation'
    }
}
