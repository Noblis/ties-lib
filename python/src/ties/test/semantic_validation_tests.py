################################################################################
# Copyright 2019 Noblis, Inc                                                   #
#                                                                              #
# Licensed under the Apache License, Version 2.0 (the "License");              #
# you may not use this file except in compliance with the License.             #
# You may obtain a copy of the License at                                      #
#                                                                              #
#    http://www.apache.org/licenses/LICENSE-2.0                                #
#                                                                              #
# Unless required by applicable law or agreed to in writing, software          #
# distributed under the License is distributed on an "AS IS" BASIS,            #
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     #
# See the License for the specific language governing permissions and          #
# limitations under the License.                                               #
################################################################################

from __future__ import unicode_literals

import unittest
from unittest import TestCase

from ties.semantic_validation import TiesSemanticValidator
from ties.semantic_validation import _check_duplicate_assertion_ids
from ties.semantic_validation import _check_duplicate_object_group_other_information_keys
from ties.semantic_validation import _check_duplicate_object_ids_and_group_ids
from ties.semantic_validation import _check_duplicate_object_item_other_information_keys
from ties.semantic_validation import _check_duplicate_object_item_sha256_hashes
from ties.semantic_validation import _check_duplicate_object_relationship_other_information_keys
from ties.semantic_validation import _check_duplicate_top_level_other_information_keys
from ties.semantic_validation import _check_object_relationship_linkage_assertion_ids
from ties.semantic_validation import _check_object_relationship_linkage_member_ids


class SemanticValidationTests(TestCase):

    def test_check_duplicate_object_item_sha256_hashes_no_duplicates(self):
        ties = {
            'objectItems': [
                {'sha256Hash': 'a' * 64},
                {'sha256Hash': 'b' * 64},
            ]
        }
        warnings = _check_duplicate_object_item_sha256_hashes(ties)
        self.assertEqual(warnings, [])

    def test_check_duplicate_object_item_sha256_hashes_single_duplicate(self):
        ties = {
            'objectItems': [
                {'sha256Hash': 'a' * 64},
                {'sha256Hash': 'a' * 64},
            ]
        }
        warnings = _check_duplicate_object_item_sha256_hashes(ties)
        self.assertEqual(len(warnings), 1)
        self.assertEqual(warnings[0].message, "objectItems at indexes [0, 1] have duplicate sha256Hash value ('aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa')")
        self.assertEqual(warnings[0].location, '/objectItems')

    def test_check_duplicate_object_item_sha256_hashes_multiple_duplicates(self):
        ties = {
            'objectItems': [
                {'sha256Hash': 'a' * 64},
                {'sha256Hash': 'b' * 64},
                {'sha256Hash': 'b' * 64},
                {'sha256Hash': 'c' * 64},
                {'sha256Hash': 'c' * 64},
                {'sha256Hash': 'c' * 64},
                {},
            ]
        }
        warnings = _check_duplicate_object_item_sha256_hashes(ties)
        self.assertEqual(len(warnings), 2)
        self.assertEqual(warnings[0].message, "objectItems at indexes [1, 2] have duplicate sha256Hash value ('bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb')")
        self.assertEqual(warnings[0].location, '/objectItems')
        self.assertEqual(warnings[1].message, "objectItems at indexes [3, 4, 5] have duplicate sha256Hash value ('cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc')")
        self.assertEqual(warnings[1].location, '/objectItems')

    def test_check_duplicate_object_item_other_information_keys_no_duplicates(self):
        ties = {
            'objectItems': [
                {
                    'otherInformation': [
                        {'key': 'foo'},
                        {'key': 'bar'},
                    ]
                }
            ]
        }
        warnings = _check_duplicate_object_item_other_information_keys(ties)
        self.assertEqual(warnings, [])

    def test_check_duplicate_object_item_other_information_keys_single_duplicate(self):
        ties = {
            'objectItems': [
                {
                    'otherInformation': [
                        {'key': 'foo'},
                        {'key': 'foo'},
                    ]
                }
            ]
        }
        warnings = _check_duplicate_object_item_other_information_keys(ties)
        self.assertEqual(len(warnings), 1)
        self.assertEqual(warnings[0].message, "otherInformation array contains duplicate key ('foo') at indexes [0, 1]")
        self.assertEqual(warnings[0].location, '/objectItems[0]/otherInformation')

    def test_check_duplicate_object_item_other_information_keys_multiple_duplicates(self):
        ties = {
            'objectItems': [
                {
                    'otherInformation': [
                        {'key': 'foo'},
                        {'key': 'bar'},
                        {'key': 'bar'},
                        {'key': 'baz'},
                        {'key': 'baz'},
                        {'key': 'baz'},
                    ]
                },
                {
                    'otherInformation': [
                        {'key': 'baz'},
                        {'key': 'baz'},
                        {'key': 'baz'},
                        {'key': 'bar'},
                        {'key': 'bar'},
                        {'key': 'foo'},
                    ]
                },
            ]
        }
        warnings = _check_duplicate_object_item_other_information_keys(ties)
        self.assertEqual(len(warnings), 4)
        self.assertEqual(warnings[0].message, "otherInformation array contains duplicate key ('bar') at indexes [1, 2]")
        self.assertEqual(warnings[0].location, '/objectItems[0]/otherInformation')
        self.assertEqual(warnings[1].message, "otherInformation array contains duplicate key ('baz') at indexes [3, 4, 5]")
        self.assertEqual(warnings[1].location, '/objectItems[0]/otherInformation')
        self.assertEqual(warnings[2].message, "otherInformation array contains duplicate key ('baz') at indexes [0, 1, 2]")
        self.assertEqual(warnings[2].location, '/objectItems[1]/otherInformation')
        self.assertEqual(warnings[3].message, "otherInformation array contains duplicate key ('bar') at indexes [3, 4]")
        self.assertEqual(warnings[3].location, '/objectItems[1]/otherInformation')

    def test_check_duplicate_object_group_other_information_keys_no_duplicates(self):
        ties = {
            'objectGroups': [
                {
                    'otherInformation': [
                        {'key': 'foo'},
                        {'key': 'bar'},
                    ]
                }
            ]
        }
        warnings = _check_duplicate_object_group_other_information_keys(ties)
        self.assertEqual(warnings, [])

    def test_check_duplicate_object_group_other_information_keys_single_duplicate(self):
        ties = {
            'objectGroups': [
                {
                    'otherInformation': [
                        {'key': 'foo'},
                        {'key': 'foo'},
                    ]
                }
            ]
        }
        warnings = _check_duplicate_object_group_other_information_keys(ties)
        self.assertEqual(len(warnings), 1)
        self.assertEqual(warnings[0].message, "otherInformation array contains duplicate key ('foo') at indexes [0, 1]")
        self.assertEqual(warnings[0].location, '/objectGroups[0]/otherInformation')

    def test_check_duplicate_object_group_other_information_keys_multiple_duplicates(self):
        ties = {
            'objectGroups': [
                {
                    'otherInformation': [
                        {'key': 'foo'},
                        {'key': 'bar'},
                        {'key': 'bar'},
                        {'key': 'baz'},
                        {'key': 'baz'},
                        {'key': 'baz'},
                    ]
                },
                {
                    'otherInformation': [
                        {'key': 'baz'},
                        {'key': 'baz'},
                        {'key': 'baz'},
                        {'key': 'bar'},
                        {'key': 'bar'},
                        {'key': 'foo'},
                    ]
                },
            ]
        }
        warnings = _check_duplicate_object_group_other_information_keys(ties)
        self.assertEqual(len(warnings), 4)
        self.assertEqual(warnings[0].message, "otherInformation array contains duplicate key ('bar') at indexes [1, 2]")
        self.assertEqual(warnings[0].location, '/objectGroups[0]/otherInformation')
        self.assertEqual(warnings[1].message, "otherInformation array contains duplicate key ('baz') at indexes [3, 4, 5]")
        self.assertEqual(warnings[1].location, '/objectGroups[0]/otherInformation')
        self.assertEqual(warnings[2].message, "otherInformation array contains duplicate key ('baz') at indexes [0, 1, 2]")
        self.assertEqual(warnings[2].location, '/objectGroups[1]/otherInformation')
        self.assertEqual(warnings[3].message, "otherInformation array contains duplicate key ('bar') at indexes [3, 4]")
        self.assertEqual(warnings[3].location, '/objectGroups[1]/otherInformation')

    def test_check_duplicate_object_ids_and_group_ids_no_duplicates(self):
        ties = {
            'objectItems': [
                {'objectId': 'a'},
                {'objectId': 'b'},
            ],
            'objectGroups': [
                {'groupId': 'c'},
                {'groupId': 'd'},
            ]
        }
        warnings = _check_duplicate_object_ids_and_group_ids(ties)
        self.assertEqual(warnings, [])

    def test_check_duplicate_object_ids_and_group_ids_single_duplicate(self):
        ties = {
            'objectItems': [
                {'objectId': 'a'},
                {'objectId': 'a'},
            ],
            'objectGroups': [
                {'groupId': 'b'},
                {'groupId': 'b'},
            ]
        }
        warnings = _check_duplicate_object_ids_and_group_ids(ties)
        self.assertEqual(len(warnings), 2)
        self.assertEqual(warnings[0].message, "objectItems at indexes [0, 1] have duplicate objectId value ('a')")
        self.assertEqual(warnings[0].location, '/objectItems')
        self.assertEqual(warnings[1].message, "objectGroups at indexes [0, 1] have duplicate groupId value ('b')")
        self.assertEqual(warnings[1].location, '/objectGroups')

    def test_check_duplicate_object_ids_and_group_ids_multiple_duplicates(self):
        ties = {
            'objectItems': [
                {'objectId': 'a'},
                {'objectId': 'b'},
                {'objectId': 'b'},
                {'objectId': 'c'},
                {'objectId': 'c'},
                {'objectId': 'c'},
                {'objectId': 'x'},
            ],
            'objectGroups': [
                {'groupId': 'y'},
                {'groupId': 'a'},
                {'groupId': 'b'},
                {'groupId': 'b'},
                {'groupId': 'c'},
                {'groupId': 'c'},
                {'groupId': 'c'},
            ]
        }
        warnings = _check_duplicate_object_ids_and_group_ids(ties)
        self.assertEqual(len(warnings), 7)
        self.assertEqual(warnings[0].message, "objectItems at indexes [1, 2] have duplicate objectId value ('b')")
        self.assertEqual(warnings[0].location, '/objectItems')
        self.assertEqual(warnings[1].message, "objectItems at indexes [3, 4, 5] have duplicate objectId value ('c')")
        self.assertEqual(warnings[1].location, '/objectItems')
        self.assertEqual(warnings[2].message, "objectGroups at indexes [2, 3] have duplicate groupId value ('b')")
        self.assertEqual(warnings[2].location, '/objectGroups')
        self.assertEqual(warnings[3].message, "objectGroups at indexes [4, 5, 6] have duplicate groupId value ('c')")
        self.assertEqual(warnings[3].location, '/objectGroups')
        self.assertEqual(warnings[4].message, "objectItem at index 0 and objectGroup at index 1 have duplicate objectId/groupId value ('a')")
        self.assertEqual(warnings[4].location, '/')
        self.assertEqual(warnings[5].message, "objectItems at indexes [1, 2] and objectGroups at indexes [2, 3] have duplicate objectId/groupId value ('b')")
        self.assertEqual(warnings[5].location, '/')
        self.assertEqual(warnings[6].message, "objectItems at indexes [3, 4, 5] and objectGroups at indexes [4, 5, 6] have duplicate objectId/groupId value ('c')")
        self.assertEqual(warnings[6].location, '/')

    def test_check_duplicate_object_assertion_ids_no_duplicates(self):
        ties = {
            'objectItems': [
                {
                    'objectAssertions': {
                        'annotations': [
                            {'assertionId': 'a'}
                        ],
                        'supplementalDescriptions': [
                            {'assertionId': 'b'}
                        ]
                    }
                },
                {
                    'objectAssertions': {
                        'annotations': [
                            {'assertionId': 'c'}
                        ],
                        'supplementalDescriptions': [
                            {'assertionId': 'd'}
                        ]
                    }
                },
            ],
            'objectGroups': [
                {
                    'groupAssertions': {
                        'annotations': [
                            {'assertionId': 'e'}
                        ],
                        'supplementalDescriptions': [
                            {'assertionId': 'f'}
                        ]
                    }
                },
                {
                    'groupAssertions': {
                        'annotations': [
                            {'assertionId': 'g'}
                        ],
                        'supplementalDescriptions': [
                            {'assertionId': 'h'}
                        ]
                    }
                },
            ]
        }
        warnings = _check_duplicate_assertion_ids(ties)
        self.assertEqual(len(warnings), 0)

    def test_check_duplicate_object_assertion_ids_single_duplicate(self):
        ties = {
            'objectItems': [
                {
                    'objectAssertions': {
                        'annotations': [
                            {'assertionId': 'a'}
                        ],
                        'supplementalDescriptions': [
                            {'assertionId': 'a'}
                        ]
                    }
                },
            ]
        }
        warnings = _check_duplicate_assertion_ids(ties)
        self.assertEqual(len(warnings), 2)
        self.assertEqual(warnings[0].message, "assertion has duplicate assertionId value ('a')")
        self.assertEqual(warnings[0].location, '/objectItems[0]/objectAssertions/annotations[0]/assertionId')
        self.assertEqual(warnings[1].message, "assertion has duplicate assertionId value ('a')")
        self.assertEqual(warnings[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId')

    def test_check_duplicate_object_assertion_ids_multiple_duplicates(self):
        ties = {
            'objectItems': [
                {
                    'objectAssertions': {
                        'annotations': [
                            {'assertionId': 'a'}
                        ],
                        'supplementalDescriptions': [
                            {'assertionId': 'a'}
                        ]
                    }
                },
                {
                    'objectAssertions': {
                        'annotations': [
                            {'assertionId': 'a'}
                        ],
                        'supplementalDescriptions': [
                            {'assertionId': 'a'}
                        ]
                    }
                },
            ],
            'objectGroups': [
                {
                    'groupAssertions': {
                        'annotations': [
                            {'assertionId': 'a'}
                        ],
                        'supplementalDescriptions': [
                            {'assertionId': 'a'}
                        ]
                    }
                },
                {
                    'groupAssertions': {
                        'annotations': [
                            {'assertionId': 'a'}
                        ],
                        'supplementalDescriptions': [
                            {'assertionId': 'a'}
                        ]
                    }
                },
            ]
        }
        warnings = _check_duplicate_assertion_ids(ties)
        self.assertEqual(len(warnings), 8)
        self.assertEqual(warnings[0].message, "assertion has duplicate assertionId value ('a')")
        self.assertEqual(warnings[0].location, '/objectItems[0]/objectAssertions/annotations[0]/assertionId')
        self.assertEqual(warnings[1].message, "assertion has duplicate assertionId value ('a')")
        self.assertEqual(warnings[1].location, '/objectItems[0]/objectAssertions/supplementalDescriptions[0]/assertionId')
        self.assertEqual(warnings[2].message, "assertion has duplicate assertionId value ('a')")
        self.assertEqual(warnings[2].location, '/objectItems[1]/objectAssertions/annotations[0]/assertionId')
        self.assertEqual(warnings[3].message, "assertion has duplicate assertionId value ('a')")
        self.assertEqual(warnings[3].location, '/objectItems[1]/objectAssertions/supplementalDescriptions[0]/assertionId')
        self.assertEqual(warnings[4].message, "assertion has duplicate assertionId value ('a')")
        self.assertEqual(warnings[4].location, '/objectGroups[0]/groupAssertions/annotations[0]/assertionId')
        self.assertEqual(warnings[5].message, "assertion has duplicate assertionId value ('a')")
        self.assertEqual(warnings[5].location, '/objectGroups[0]/groupAssertions/supplementalDescriptions[0]/assertionId')
        self.assertEqual(warnings[6].message, "assertion has duplicate assertionId value ('a')")
        self.assertEqual(warnings[6].location, '/objectGroups[1]/groupAssertions/annotations[0]/assertionId')
        self.assertEqual(warnings[7].message, "assertion has duplicate assertionId value ('a')")
        self.assertEqual(warnings[7].location, '/objectGroups[1]/groupAssertions/supplementalDescriptions[0]/assertionId')

    def test_check_object_relationship_linkage_member_ids_single(self):
        ties = {
            'objectItems': [
                {'objectId': 'a'}
            ],
            'objectGroups': [
                {'groupId': 'b'}
            ],
            'objectRelationships': [
                {'linkageMemberIds': ['a', 'c']}
            ]
        }
        warnings = _check_object_relationship_linkage_member_ids(ties)
        self.assertEqual(len(warnings), 1)
        self.assertEqual(warnings[0].message, "objectRelationship has a linkageMemberId ('c') that does not reference an objectItem or objectGroup in this export")
        self.assertEqual(warnings[0].location, '/objectRelationships[0]/linkageMemberIds[1]')

    def test_check_object_relationship_linkage_member_ids_multiple(self):
        ties = {
            'objectItems': [
                {'objectId': 'a'}
            ],
            'objectGroups': [
                {'groupId': 'b'}
            ],
            'objectRelationships': [
                {'linkageMemberIds': ['a', 'b']},
                {'linkageMemberIds': ['a', 'x']},
                {'linkageMemberIds': ['x', 'y']},
            ]
        }
        warnings = _check_object_relationship_linkage_member_ids(ties)
        self.assertEqual(len(warnings), 3)
        self.assertEqual(warnings[0].message, "objectRelationship has a linkageMemberId ('x') that does not reference an objectItem or objectGroup in this export")
        self.assertEqual(warnings[0].location, '/objectRelationships[1]/linkageMemberIds[1]')
        self.assertEqual(warnings[1].message, "objectRelationship has a linkageMemberId ('x') that does not reference an objectItem or objectGroup in this export")
        self.assertEqual(warnings[1].location, '/objectRelationships[2]/linkageMemberIds[0]')
        self.assertEqual(warnings[2].message, "objectRelationship has a linkageMemberId ('y') that does not reference an objectItem or objectGroup in this export")
        self.assertEqual(warnings[2].location, '/objectRelationships[2]/linkageMemberIds[1]')

    def test_check_object_relationship_linkage_assertion_ids_single(self):
        ties = {
            'objectItems': [
                {
                    'objectId': 'a',
                    'objectAssertions': {
                        'annotations': [
                            {'assertionId': 'c'}
                        ]
                    }
                }
            ],
            'objectGroups': [
                {
                    'groupId': 'b',
                    'groupAssertions': {
                        'supplementalDescriptions': [
                            {'assertionId': 'd'}
                        ]
                    }
                }
            ],
            'objectRelationships': [
                {
                    'linkageMemberIds': ['a', 'a'],
                    'linkageAssertionId': 'x',
                }
            ],
        }
        warnings = _check_object_relationship_linkage_assertion_ids(ties)
        self.assertEqual(len(warnings), 1)
        self.assertEqual(warnings[0].message, "objectRelationship has a linkageAssertionId ('x') that does not reference an assertion in this export")
        self.assertEqual(warnings[0].location, '/objectRelationships[0]/linkageAssertionId')

    def test_check_object_relationship_linkage_assertion_ids_multiple(self):
        ties = {
            'objectItems': [
                {
                    'objectId': 'a',
                    'objectAssertions': {
                        'annotations': [
                            {'assertionId': 'c'}
                        ]
                    }
                }
            ],
            'objectGroups': [
                {
                    'groupId': 'b',
                    'groupAssertions': {
                        'supplementalDescriptions': [
                            {'assertionId': 'd'}
                        ]
                    }
                }
            ],
            'objectRelationships': [
                {
                    'linkageMemberIds': ['a', 'a'],
                    'linkageAssertionId': 'x',
                },
                {
                    'linkageMemberIds': ['b', 'b'],
                    'linkageAssertionId': 'y',
                },
            ],
        }
        warnings = _check_object_relationship_linkage_assertion_ids(ties)
        self.assertEqual(len(warnings), 2)
        self.assertEqual(warnings[0].message, "objectRelationship has a linkageAssertionId ('x') that does not reference an assertion in this export")
        self.assertEqual(warnings[0].location, '/objectRelationships[0]/linkageAssertionId')
        self.assertEqual(warnings[1].message, "objectRelationship has a linkageAssertionId ('y') that does not reference an assertion in this export")
        self.assertEqual(warnings[1].location, '/objectRelationships[1]/linkageAssertionId')

    def test_check_duplicate_object_relationship_other_information_keys_no_duplicates(self):
        ties = {
            'objectRelationships': [
                {
                    'otherInformation': [
                        {'key': 'foo'},
                        {'key': 'bar'},
                    ]
                }
            ]
        }
        warnings = _check_duplicate_object_relationship_other_information_keys(ties)
        self.assertEqual(warnings, [])

    def test_check_duplicate_object_relationship_other_information_keys_one_duplicate(self):
        ties = {
            'objectRelationships': [
                {
                    'otherInformation': [
                        {'key': 'foo'},
                        {'key': 'foo'},
                    ]
                }
            ]
        }
        warnings = _check_duplicate_object_relationship_other_information_keys(ties)
        self.assertEqual(len(warnings), 1)
        self.assertEqual(warnings[0].message, "otherInformation array contains duplicate key ('foo') at indexes [0, 1]")
        self.assertEqual(warnings[0].location, '/objectRelationships[0]/otherInformation')

    def test_check_duplicate_object_relationship_other_information_keys_multiple_duplicates(self):
        ties = {
            'objectRelationships': [
                {
                    'otherInformation': [
                        {'key': 'foo'},
                        {'key': 'bar'},
                        {'key': 'bar'},
                        {'key': 'baz'},
                        {'key': 'baz'},
                        {'key': 'baz'},
                    ]
                },
                {
                    'otherInformation': [
                        {'key': 'baz'},
                        {'key': 'baz'},
                        {'key': 'baz'},
                        {'key': 'bar'},
                        {'key': 'bar'},
                        {'key': 'foo'},
                    ]
                }
            ]
        }
        warnings = _check_duplicate_object_relationship_other_information_keys(ties)
        self.assertEqual(len(warnings), 4)
        self.assertEqual(warnings[0].message, "otherInformation array contains duplicate key ('bar') at indexes [1, 2]")
        self.assertEqual(warnings[0].location, '/objectRelationships[0]/otherInformation')
        self.assertEqual(warnings[1].message, "otherInformation array contains duplicate key ('baz') at indexes [3, 4, 5]")
        self.assertEqual(warnings[1].location, '/objectRelationships[0]/otherInformation')
        self.assertEqual(warnings[2].message, "otherInformation array contains duplicate key ('baz') at indexes [0, 1, 2]")
        self.assertEqual(warnings[2].location, '/objectRelationships[1]/otherInformation')
        self.assertEqual(warnings[3].message, "otherInformation array contains duplicate key ('bar') at indexes [3, 4]")
        self.assertEqual(warnings[3].location, '/objectRelationships[1]/otherInformation')

    def test_check_duplicate_top_level_other_information_keys_no_duplicates(self):
        ties = {
            'otherInformation': [
                {'key': 'foo'},
                {'key': 'bar'},
            ]
        }
        warnings = _check_duplicate_top_level_other_information_keys(ties)
        self.assertEqual(warnings, [])

    def test_check_duplicate_top_level_other_information_keys_single_duplicate(self):
        ties = {
            'otherInformation': [
                {'key': 'foo'},
                {'key': 'foo'},
            ]
        }
        warnings = _check_duplicate_top_level_other_information_keys(ties)
        self.assertEqual(len(warnings), 1)
        self.assertEqual(warnings[0].message, "otherInformation array contains duplicate key ('foo') at indexes [0, 1]")
        self.assertEqual(warnings[0].location, '/otherInformation')

    def test_check_duplicate_top_level_other_information_keys_multiple_duplicates(self):
        ties = {
            'otherInformation': [
                {'key': 'foo'},
                {'key': 'bar'},
                {'key': 'bar'},
                {'key': 'baz'},
                {'key': 'baz'},
                {'key': 'baz'},
            ]
        }
        warnings = _check_duplicate_top_level_other_information_keys(ties)
        self.assertEqual(len(warnings), 2)
        self.assertEqual(warnings[0].message, "otherInformation array contains duplicate key ('bar') at indexes [1, 2]")
        self.assertEqual(warnings[0].location, '/otherInformation')
        self.assertEqual(warnings[1].message, "otherInformation array contains duplicate key ('baz') at indexes [3, 4, 5]")
        self.assertEqual(warnings[1].location, '/otherInformation')

    def test_validate_no_warnings(self):
        ties = {}
        warnings = TiesSemanticValidator().all_warnings(ties)
        self.assertEqual(len(warnings), 0)

    def test_validate_multiple_warnings(self):
        ties = {
            'objectItems': [
                {
                    'objectId': 'a',
                    'sha256Hash': 'a' * 64,
                    'otherInformation': [
                        {'key': 'foo'},
                        {'key': 'foo'},
                    ]
                },
                {
                    'objectId': 'a',
                    'sha256Hash': 'a' * 64,
                },
                {
                    'objectAssertions': {
                        'annotations': [
                            {'assertionId': 'c'}
                        ]
                    }
                }
            ],
            'objectGroups': [
                {
                    'groupId': 'a',
                    'otherInformation': [
                        {'key': 'foo'},
                        {'key': 'foo'},
                    ]
                },
                {'groupId': 'a'},
                {
                    'groupAssertions': {
                        'supplementalDescriptions': [
                            {'assertionId': 'c'}
                        ]
                    }
                }
            ],
            'objectRelationships': [
                {
                    'linkageMemberIds': ['a', 'b'],
                    'linkageAssertionId': 'd',
                    'otherInformation': [
                        {'key': 'foo'},
                        {'key': 'foo'},
                    ]
                }
            ],
            'otherInformation': [
                {'key': 'foo'},
                {'key': 'foo'},
            ]
        }
        warnings = TiesSemanticValidator().all_warnings(ties)
        self.assertEqual(len(warnings), 12)
        i = 0
        self.assertEqual(warnings[i].message, "objectItems at indexes [0, 1] have duplicate sha256Hash value ('aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa')")
        self.assertEqual(warnings[i].location, '/objectItems')
        i += 1
        self.assertEqual(warnings[i].message, "otherInformation array contains duplicate key ('foo') at indexes [0, 1]")
        self.assertEqual(warnings[i].location, '/objectItems[0]/otherInformation')
        i += 1
        self.assertEqual(warnings[i].message, "otherInformation array contains duplicate key ('foo') at indexes [0, 1]")
        self.assertEqual(warnings[i].location, '/objectGroups[0]/otherInformation')
        i += 1
        self.assertEqual(warnings[i].message, "objectItems at indexes [0, 1] have duplicate objectId value ('a')")
        self.assertEqual(warnings[i].location, '/objectItems')
        i += 1
        self.assertEqual(warnings[i].message, "objectGroups at indexes [0, 1] have duplicate groupId value ('a')")
        self.assertEqual(warnings[i].location, '/objectGroups')
        i += 1
        self.assertEqual(warnings[i].message, "objectItems at indexes [0, 1] and objectGroups at indexes [0, 1] have duplicate objectId/groupId value ('a')")
        self.assertEqual(warnings[i].location, '/')
        i += 1
        self.assertEqual(warnings[i].message, "assertion has duplicate assertionId value ('c')")
        self.assertEqual(warnings[i].location, '/objectItems[2]/objectAssertions/annotations[0]/assertionId')
        i += 1
        self.assertEqual(warnings[i].message, "assertion has duplicate assertionId value ('c')")
        self.assertEqual(warnings[i].location, '/objectGroups[2]/groupAssertions/supplementalDescriptions[0]/assertionId')
        i += 1
        self.assertEqual(warnings[i].message, "objectRelationship has a linkageMemberId ('b') that does not reference an objectItem or objectGroup in this export")
        self.assertEqual(warnings[i].location, '/objectRelationships[0]/linkageMemberIds[1]')
        i += 1
        self.assertEqual(warnings[i].message, "objectRelationship has a linkageAssertionId ('d') that does not reference an assertion in this export")
        self.assertEqual(warnings[i].location, '/objectRelationships[0]/linkageAssertionId')
        i += 1
        self.assertEqual(warnings[i].message, "otherInformation array contains duplicate key ('foo') at indexes [0, 1]")
        self.assertEqual(warnings[i].location, '/objectRelationships[0]/otherInformation')
        i += 1
        self.assertEqual(warnings[i].message, "otherInformation array contains duplicate key ('foo') at indexes [0, 1]")
        self.assertEqual(warnings[i].location, '/otherInformation')


if __name__ == '__main__':
    unittest.main()
