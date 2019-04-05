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
import org.noblis.ties.TiesConverter

public class TiesConverterTests {

    TiesConverter converter

    @Before
    void setUp() {
        converter = new TiesConverter()
    }

    @Test
    void test_versionUnknownVersion() {
        def ties = ['version': '0.1']
        converter.convert(ties, 'U')
        assert ties['version'] == '0.1'
    }

    @Test
    void test_version0Dot1Dot8() {
        def ties = ['version': '0.1.8']
        converter.convert(ties, 'U')
        assert ties['version'] == '0.9'
    }

    @Test
    void test_version0Dot2() {
        def ties = ['version': '0.2']
        converter.convert(ties, 'U')
        assert ties['version'] == '0.9'
    }

    @Test
    void test_version0Dot3() {
        def ties = ['version': '0.3']
        converter.convert(ties)
        assert ties['version'] == '0.9'
    }

    @Test
    void test_version0Dot4() {
        def ties = ['version': '0.4']
        converter.convert(ties)
        assert ties['version'] == '0.9'
    }

    @Test
    void test_version0Dot5() {
        def ties = ['version': '0.5']
        converter.convert(ties)
        assert ties['version'] == '0.9'
    }

    @Test
    void test_version0Dot6() {
        def ties = ['version': '0.6']
        converter.convert(ties)
        assert ties['version'] == '0.9'
    }

    @Test
    void test_version0Dot7() {
        def ties = ['version': '0.7']
        converter.convert(ties)
        assert ties['version'] == '0.9'
    }

    @Test
    void test_version0Dot8() {
        def ties = ['version': '0.8']
        converter.convert(ties)
        assert ties['version'] == '0.9'
    }

    @Test
    void test_version0Dot9() {
        def ties = ['version': '0.9']
        converter.convert(ties)
        assert ties['version'] == '0.9'
    }

    @Test(expected = IllegalArgumentException)
    void test_securityTagNull0Dot1Dot8() {
        def ties = ['version': '0.1.8']
        converter._0Dot2To0Dot3(ties, null)
    }

    @Test(expected = IllegalArgumentException)
    void test_securityTagNull0Dot2() {
        def ties = ['version': '0.2']
        converter._0Dot2To0Dot3(ties, null)
    }

    @Test(expected = IllegalArgumentException)
    void test_securityTagBlank0Dot1Dot8() {
        def ties = ['version': '0.1.8']
        converter._0Dot2To0Dot3(ties, '')
    }

    @Test(expected = IllegalArgumentException)
    void test_securityTagBlank0Dot2() {
        def ties = ['version': '0.2']
        converter._0Dot2To0Dot3(ties, '')
    }

    @Test
    void test_securityTagMissing() {
        def ties = ['version': '0.1.8']
        converter._0Dot2To0Dot3(ties, 'U')
        assert ties['securityTag'] == 'U'
    }

    @Test
    void test_securityTagNotMissing0Dot1Dot8() {
        def ties = ['version': '0.1.8', 'securityTag': 'UNCLASSIFIED']
        converter._0Dot2To0Dot3(ties, 'U')
        assert ties['securityTag'] == 'UNCLASSIFIED'
    }

    @Test
    void test_securityTagNotMissing() {
        def ties = ['version': '0.3', 'securityTag': 'UNCLASSIFIED']
        converter._0Dot2To0Dot3(ties, 'U')
        assert ties['securityTag'] == 'UNCLASSIFIED'
    }

    @Test
    void test_timeBadFormat() {
        def ties = ['version': '0.1.8', 'time': '1970-01-01T:00:00:00Z']
        converter._0Dot2To0Dot3(ties, 'U')
        assert ties['time'] == '1970-01-01T00:00:00Z'
    }

    @Test
    void test_timeMissing() {
        def ties = ['version': '0.1.8']
        converter._0Dot2To0Dot3(ties, 'U')
        assert !('time' in ties.keySet())
    }

    @Test
    void test_objectItemRename() {
        def ties = ['version': '0.1.8', 'objectItem': []]
        converter._0Dot2To0Dot3(ties, 'U')
        assert !('objectItem' in ties.keySet())
        assert 'objectItems' in ties.keySet()
    }

    @Test
    void test_objectItemRelativeUriRename() {
        def objectItem1 = ['relativeURI': '']
        def objectItem2 = [:]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert !('relativeURI' in objectItem1.keySet())
        assert 'relativeUri' in objectItem1.keySet()
        assert !('relativeURI' in objectItem2.keySet())
        assert !('relativeUri' in objectItem2.keySet())
    }

    @Test
    void test_objectItemSystemIdentifierRename() {
        def objectItem1 = ['systemIdentifier': '']
        def objectItem2 = [:]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert !('systemIdentifier' in objectItem1.keySet())
        assert 'systemId' in objectItem1.keySet()
        assert !('systemIdentifier' in objectItem2.keySet())
        assert !('systemId' in objectItem2.keySet())
    }

    @Test
    void test_objectRelationshipLinkageAssertionIdRename() {
        def objectItem1 = ['objectRelationships': [['linkageAssertId': '']]]
        def objectItem2 = ['objectRelationships': [[:]]]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert !('linkageAssertId' in objectItem1['objectRelationships'][0].keySet())
        assert 'linkageAssertionId' in objectItem1['objectRelationships'][0].keySet()
        assert !('linkageAssertId' in objectItem2['objectRelationships'][0].keySet())
        assert !('linkageAssertionId' in objectItem2['objectRelationships'][0].keySet())
    }

    @Test
    void test_ssdDataHashRename() {
        def objectItem1 = ['objectAssertions': ['systemSupplementalDescriptions': [['dataHash': '']]]]
        def objectItem2 = ['objectAssertions': ['systemSupplementalDescriptions': [[:]]]]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert !('dataHash' in objectItem1['objectAssertions']['systemSupplementalDescriptions'][0].keySet())
        assert 'sha256DataHash' in objectItem1['objectAssertions']['systemSupplementalDescriptions'][0].keySet()
        assert !('dataHash' in objectItem2['objectAssertions']['systemSupplementalDescriptions'][0].keySet())
        assert !('sha256DataHash' in objectItem2['objectAssertions']['systemSupplementalDescriptions'][0].keySet())
    }

    @Test
    void test_objectItemSecurityTag() {
        def objectItem1 = ['authorityInformation': ['securityTag': 'UNCLASSIFIED']]
        def objectItem2 = ['authorityInformation': [:]]
        def objectItem3 = [:]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2, objectItem3]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert objectItem1['authorityInformation']['securityTag'] == 'UNCLASSIFIED'
        assert objectItem2['authorityInformation']['securityTag'] == 'U'
        assert objectItem3['authorityInformation']['securityTag'] == 'U'
    }

    @Test
    void test_objectItemRegistrationDateBadFormat() {
        def objectItem1 = ['authorityInformation': ['registrationDate': '1970-01-01T:00:00:00Z']]
        def objectItem2 = ['authorityInformation': [:]]
        def objectItem3 = [:]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2, objectItem3]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert objectItem1['authorityInformation']['registrationDate'] == '1970-01-01T00:00:00Z'
        assert !('registrationDate' in objectItem2['authorityInformation'].keySet())
        assert !('registrationDate' in objectItem3['authorityInformation'].keySet())
    }

    @Test
    void test_objectItemExpirationDateBadFormat() {
        def objectItem1 = ['authorityInformation': ['expirationDate': '1970-01-01T:00:00:00Z']]
        def objectItem2 = ['authorityInformation': [:]]
        def objectItem3 = [:]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2, objectItem3]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert objectItem1['authorityInformation']['expirationDate'] == '1970-01-01T00:00:00Z'
        assert !('expirationDate' in objectItem2['authorityInformation'].keySet())
        assert !('expirationDate' in objectItem3['authorityInformation'].keySet())
    }

    @Test
    void test_annotationSecurityTag() {
        def objectItem1 = ['objectAssertions': ['annotations': [['securityTag': 'UNCLASSIFIED']]]]
        def objectItem2 = ['objectAssertions': ['annotations': [[:]]]]
        def objectItem3 = ['objectAssertions': ['annotations': []]]
        def objectItem4 = ['objectAssertions': [:]]
        def objectItem5 = [:]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2, objectItem3, objectItem4, objectItem5]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert objectItem1['objectAssertions']['annotations'][0]['securityTag'] == 'UNCLASSIFIED'
        assert objectItem2['objectAssertions']['annotations'][0]['securityTag'] == 'U'
        assert objectItem3['objectAssertions']['annotations'].size() == 0
        assert !('annotations' in objectItem4['objectAssertions'].keySet())
        assert !('objectAssertions' in objectItem5.keySet())
    }

    @Test
    void test_annotationValueMinimumLength() {
        def objectItem1 = ['objectAssertions': ['annotations': [['value': '']]]]
        def objectItem2 = ['objectAssertions': ['annotations': [['value': 'other']]]]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert objectItem1['objectAssertions']['annotations'][0]['value'] == ' '
        assert objectItem2['objectAssertions']['annotations'][0]['value'] == 'other'
    }

    @Test
    void test_annotationTimeBadFormat() {
        def objectItem1 = ['objectAssertions': ['annotations': [['time': '1970-01-01T:00:00:00Z']]]]
        def objectItem2 = ['objectAssertions': ['annotations': [[:]]]]
        def objectItem3 = ['objectAssertions': ['annotations': []]]
        def objectItem4 = ['objectAssertions': [:]]
        def objectItem5 = [:]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2, objectItem3, objectItem4, objectItem5]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert objectItem1['objectAssertions']['annotations'][0]['time'] == '1970-01-01T00:00:00Z'
        assert !('time' in objectItem2['objectAssertions']['annotations'][0].keySet())
        assert objectItem3['objectAssertions']['annotations'].size() == 0
        assert !('annotations' in objectItem4['objectAssertions'].keySet())
        assert !('objectAssertions' in objectItem5.keySet())
    }

    @Test
    void test_annotationItemActionTimeBadFormat() {
        def objectItem1 = ['objectAssertions': ['annotations': [['itemActionTime': '1970-01-01T:00:00:00Z']]]]
        def objectItem2 = ['objectAssertions': ['annotations': [[:]]]]
        def objectItem3 = ['objectAssertions': ['annotations': []]]
        def objectItem4 = ['objectAssertions': [:]]
        def objectItem5 = [:]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2, objectItem3, objectItem4, objectItem5]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert objectItem1['objectAssertions']['annotations'][0]['itemActionTime'] == '1970-01-01T00:00:00Z'
        assert !('itemActionTime' in objectItem2['objectAssertions']['annotations'][0].keySet())
        assert objectItem3['objectAssertions']['annotations'].size() == 0
        assert !('annotations' in objectItem4['objectAssertions'].keySet())
        assert !('objectAssertions' in objectItem5.keySet())
    }

    @Test
    void test_ssdSecurityTag() {
        def objectItem1 = ['objectAssertions': ['systemSupplementalDescriptions': [['securityTag': 'UNCLASSIFIED']]]]
        def objectItem2 = ['objectAssertions': ['systemSupplementalDescriptions': [[:]]]]
        def objectItem3 = ['objectAssertions': ['systemSupplementalDescriptions': []]]
        def objectItem4 = ['objectAssertions': [:]]
        def objectItem5 = [:]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2, objectItem3, objectItem4, objectItem5]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert objectItem1['objectAssertions']['systemSupplementalDescriptions'][0]['securityTag'] == 'UNCLASSIFIED'
        assert objectItem2['objectAssertions']['systemSupplementalDescriptions'][0]['securityTag'] == 'U'
        assert objectItem3['objectAssertions']['systemSupplementalDescriptions'].size() == 0
        assert !('systemSupplementalDescriptions' in objectItem4['objectAssertions'].keySet())
        assert !('objectAssertions' in objectItem5.keySet())
    }

    @Test
    void test_ssdInformationType() {
        def objectItem1 = ['objectAssertions': ['systemSupplementalDescriptions': [[:]]]]
        def objectItem2 = ['objectAssertions': ['systemSupplementalDescriptions': [['informationType': 'other']]]]
        def ties = ['version': '0.1.8', 'objectItem': [objectItem1, objectItem2]]
        converter._0Dot2To0Dot3(ties, 'U')
        assert objectItem1['objectAssertions']['systemSupplementalDescriptions'][0]['informationType'] == 'triageSupplemental'
        assert objectItem2['objectAssertions']['systemSupplementalDescriptions'][0]['informationType'] == 'other'
    }

    @Test
    void test_idIntToStringMissing() {
        def ties = ['version': '0.3']
        converter._0Dot3To0Dot4(ties)
        assert !('id' in ties.keySet())
    }

    @Test
    void test_idIntToStringNotMissing() {
        def ties = ['version': '0.3', 'id': 1]
        converter._0Dot3To0Dot4(ties)
        assert ties['id'] == '1'
    }

    @Test
    void test_removeSsdDescription() {
        def objectItem = ['objectAssertions': ['systemSupplementalDescriptions': [['description': '']]]]
        def ties = ['version': '0.3', 'objectItems': [objectItem]]
        converter._0Dot3To0Dot4(ties)
        assert !('description' in objectItem['objectAssertions']['systemSupplementalDescriptions'][0].keySet())
    }

    @Test
    void test_addAnnotationTypeTag() {
        def objectItem = ['objectAssertions': ['annotations': [['key': 'Tag']]]]
        def ties = ['version': '0.4', 'objectItems': [objectItem]]
        converter._0Dot4To0Dot5(ties)
        assert !('key' in objectItem['objectAssertions']['annotations'][0].keySet())
        assert objectItem['objectAssertions']['annotations'][0]['annotationType'] == 'Tag'
    }

    @Test
    void test_addAnnotationTypeUserDescribed() {
        def objectItem = ['objectAssertions': ['annotations': [['key': 'UserDescribed']]]]
        def ties = ['version': '0.4', 'objectItems': [objectItem]]
        converter._0Dot4To0Dot5(ties)
        assert !('key' in objectItem['objectAssertions']['annotations'][0].keySet())
        assert objectItem['objectAssertions']['annotations'][0]['annotationType'] == 'UserDescribed'
    }

    @Test
    void test_addAnnotationTypeUnknown() {
        def objectItem = ['objectAssertions': ['annotations': [['key': 'other']]]]
        def ties = ['version': '0.4', 'objectItems': [objectItem]]
        converter._0Dot4To0Dot5(ties)
        assert objectItem['objectAssertions']['annotations'][0]['key'] == 'other'
        assert objectItem['objectAssertions']['annotations'][0]['annotationType'] == 'Unknown'
    }

    @Test
    void test_collectionIdDescriptionToCollectionIdLabel() {
        def objectItem = ['authorityInformation': ['collectionIdDescription': 'a']]
        def ties = ['version': '0.5', 'objectItems': [objectItem]]
        converter._0Dot5To0Dot6(ties)
        assert !('collectionIdDescription' in objectItem.keySet())
        assert objectItem['authorityInformation']['collectionIdLabel'] == 'a'
    }

    @Test
    void test_subCollectionIdDescriptionToSubCollectionIdLabel() {
        def objectItem = ['authorityInformation': ['subCollectionIdDescription': 'a']]
        def ties = ['version': '0.5', 'objectItems': [objectItem]]
        converter._0Dot5To0Dot6(ties)
        assert !('subCollectionIdDescription' in objectItem.keySet())
        assert objectItem['authorityInformation']['subCollectionIdLabel'] == 'a'
    }

    @Test
    void test_replaceMissingSystemIdWithSha256Hash() {
        def objectItem = ['sha256Hash': 'a' * 64]
        def ties = ['version': '0.6', 'objectItems': [objectItem]]
        converter._0Dot6To0Dot7(ties)
        assert objectItem['sha256Hash'] == 'a' * 64
        assert objectItem['systemId'] == 'a' * 64
    }

    @Test
    void test_replaceNullSystemIdWithSha256Hash() {
        def objectItem = ['systemId': null, 'sha256Hash': 'a' * 64]
        def ties = ['version': '0.6', 'objectItems': [objectItem]]
        converter._0Dot6To0Dot7(ties)
        assert objectItem['sha256Hash'] == 'a' * 64
        assert objectItem['systemId'] == 'a' * 64
    }

    @Test
    void test_replaceBlankSystemIdWithSha256Hash() {
        def objectItem = ['systemId': '', 'sha256Hash': 'a' * 64]
        def ties = ['version': '0.6', 'objectItems': [objectItem]]
        converter._0Dot6To0Dot7(ties)
        assert objectItem['sha256Hash'] == 'a' * 64
        assert objectItem['systemId'] == 'a' * 64
    }

    @Test
    void test_badSystemIdBadSha256Hash() {
        def objectItem1 = [:]
        def objectItem2 = ['sha256Hash': null]
        def objectItem3 = ['sha256Hash': '']
        def ties = ['version': '0.6', 'objectItems': [objectItem1, objectItem2, objectItem3]]
        converter._0Dot6To0Dot7(ties)
        assert !('sha256Hash' in objectItem1.keySet())
        assert !('systemId' in objectItem1.keySet())
        assert objectItem2['sha256Hash'] == null
        assert !('systemId' in objectItem2.keySet())
        assert objectItem3['sha256Hash'] == ''
        assert !('systemId' in objectItem3.keySet())
    }

    @Test
    void test_linkageSha256HashToLinkageSystemIdNoValue() {
        def objectRelationships = [[:]]
        def objectItem = ['objectRelationships': objectRelationships]
        def ties = ['version': '0.6', 'objectItems': [objectItem]]
        converter._0Dot6To0Dot7(ties)
        assert !('linkageSha256Hash' in objectRelationships[0].keySet())
        assert !('linkageSystemId' in objectRelationships[0].keySet())
    }

    @Test
    void test_linkageSha256HashToLinkageSystemIdNoSystemId() {
        def objectRelationships = [['linkageSha256Hash': 'a' * 64]]
        def objectItem = ['objectRelationships': objectRelationships]
        def ties = ['version': '0.6', 'objectItems': [objectItem]]
        converter._0Dot6To0Dot7(ties)
        assert !('linkageSha256Hash' in objectRelationships[0].keySet())
        assert objectRelationships[0]['linkageSystemId'] == 'a' * 64
    }

    @Test
    void test_linkageSha256HashToLinkageSystemIdSingleSystemId() {
        def objectRelationships = [['linkageSha256Hash': 'b' * 64]]
        def objectItem1 = ['systemId': 'a', 'sha256Hash': 'a' * 64, 'objectRelationships': objectRelationships]
        def objectItem2 = ['systemId': 'b', 'sha256Hash': 'b' * 64]
        def ties = ['version': '0.6', 'objectItems': [objectItem1, objectItem2]]
        converter._0Dot6To0Dot7(ties)
        assert objectRelationships.size() == 1
        assert !('linkageSha256Hash' in objectRelationships[0].keySet())
        assert objectRelationships[0]['linkageSystemId'] == 'b'
    }

    @Test
    void test_linkageSha256HashToLinkageSystemIdMultipleSystemId() {
        def objectRelationships = [['linkageSha256Hash': 'a' * 64]]
        def objectItem1 = ['systemId': 'a', 'sha256Hash': 'a' * 64, 'objectRelationships': objectRelationships]
        def objectItem2 = ['systemId': 'b', 'sha256Hash': 'a' * 64]
        def ties = ['version': '0.6', 'objectItems': [objectItem1, objectItem2]]
        converter._0Dot6To0Dot7(ties)
        assert objectRelationships.size() == 2
        assert !('linkageSha256Hash' in objectRelationships[0].keySet())
        assert !('linkageSha256Hash' in objectRelationships[1].keySet())
        assert objectRelationships[0]['linkageSystemId'] == 'a'
        assert objectRelationships[1]['linkageSystemId'] == 'b'
    }

    @Test
    void test_moveObjectRelationshipsNoRelationships() {
        def objectItem1 = ['systemId': 'a']
        def objectItem2 = ['systemId': 'b']
        def ties = ['version': '0.7', 'objectItems': [objectItem1, objectItem2]]
        converter._0Dot7To0Dot8(ties)
        assert !('objectRelationships' in ties.keySet())
    }

    @Test
    void test_moveObjectRelationshipsSingleRelationship() {
        def objectItem1 = ['systemId': 'a', 'objectRelationships': [['linkageSystemId': 'b']]]
        def objectItem2 = ['systemId': 'b']
        def ties = ['version': '0.7', 'objectItems': [objectItem1, objectItem2]]
        converter._0Dot7To0Dot8(ties)
        assert !('objectRelationships' in objectItem1.keySet())
        assert !('objectRelationships' in objectItem2.keySet())
        assert ties['objectRelationships'].size() == 1
        assert ties['objectRelationships'][0]['linkageDirectionality'] == 'DIRECTED'
        assert ties['objectRelationships'][0]['linkageSystemIds'] == ['a', 'b']
    }

    @Test
    void test_moveObjectRelationshipsMultipleRelationships() {
        def objectItem1 = ['systemId': 'a', 'objectRelationships': [['linkageSystemId': 'b'], ['linkageSystemId': 'c']]]
        def objectItem2 = ['systemId': 'b', 'objectRelationships': [['linkageSystemId': 'a'], ['linkageSystemId': 'c']]]
        def objectItem3 = ['systemId': 'c', 'objectRelationships': [['linkageSystemId': 'a'], ['linkageSystemId': 'b']]]
        def ties = ['version': '0.7', 'objectItems': [objectItem1, objectItem2, objectItem3]]
        converter._0Dot7To0Dot8(ties)
        assert !('objectRelationships' in objectItem1.keySet())
        assert !('objectRelationships' in objectItem2.keySet())
        assert !('objectRelationships' in objectItem3.keySet())
        assert ties['objectRelationships'].size() == 6
        assert ties['objectRelationships'][0]['linkageDirectionality'] == 'DIRECTED'
        assert ties['objectRelationships'][0]['linkageSystemIds'] == ['a', 'b']
        assert ties['objectRelationships'][1]['linkageDirectionality'] == 'DIRECTED'
        assert ties['objectRelationships'][1]['linkageSystemIds'] == ['a', 'c']
        assert ties['objectRelationships'][2]['linkageDirectionality'] == 'DIRECTED'
        assert ties['objectRelationships'][2]['linkageSystemIds'] == ['b', 'a']
        assert ties['objectRelationships'][3]['linkageDirectionality'] == 'DIRECTED'
        assert ties['objectRelationships'][3]['linkageSystemIds'] == ['b', 'c']
        assert ties['objectRelationships'][4]['linkageDirectionality'] == 'DIRECTED'
        assert ties['objectRelationships'][4]['linkageSystemIds'] == ['c', 'a']
        assert ties['objectRelationships'][5]['linkageDirectionality'] == 'DIRECTED'
        assert ties['objectRelationships'][5]['linkageSystemIds'] == ['c', 'b']
    }

    @Test
    void test_renameSystemId() {
        def objectItem1 = [systemId: 'a']
        def objectItem2 = [:]
        def ties = [version: '0.8', objectItems: [objectItem1, objectItem2]]
        converter._0Dot8To0Dot9(ties)
        assert objectItem1['objectId'] == 'a'
        assert !('systemId' in objectItem1.keySet())
        assert !('objectId' in objectItem2.keySet())
    }

    @Test
    void test_renameOtherIds() {
        def objectItem1 = [otherIds: []]
        def objectItem2 = [:]
        def ties = [version: '0.8', objectItems: [objectItem1, objectItem2]]
        converter._0Dot8To0Dot9(ties)
        assert objectItem1['otherInformation'] == []
        assert !('otherIds' in objectItem1.keySet())
        assert !('otherInformation' in objectItem2.keySet())
    }

    @Test
    void test_renameAnnotationSystemUniqueId() {
        def annotation1 = [systemUniqueId: 'a']
        def annotation2 = [:]
        def objectAssertions = [annotations: [annotation1, annotation2]]
        def objectItem = [objectAssertions: objectAssertions]
        def ties = [version: '0.8', objectItems: [objectItem]]
        converter._0Dot8To0Dot9(ties)
        assert annotation1['assertionReferenceId'] == 'a'
        assert !('systemUniqueId' in annotation1.keySet())
        assert !('assertionReferenceId' in annotation2.keySet())
    }

    @Test
    void test_renameAnnotationSystemName() {
        def annotation1 = ['systemName': 'a']
        def annotation2 = [:]
        def objectAssertions = [annotations: [annotation1, annotation2]]
        def objectItem = ['objectAssertions': objectAssertions]
        def ties = ['version': '0.8', 'objectItems': [objectItem]]
        converter._0Dot8To0Dot9(ties)
        assert annotation1['system'] == 'a'
        assert !('systemName' in annotation1.keySet())
        assert !('system' in annotation2.keySet())
    }

    @Test
    void test_renameSupplementalDescriptionSystemExportId() {
        def supplementalDescription1 = ['systemExportId': 'a']
        def supplementalDescription2 = [:]
        def objectAssertions = ['systemSupplementalDescriptions': [supplementalDescription1, supplementalDescription2]]
        def objectItem = ['objectAssertions': objectAssertions]
        def ties = ['version': '0.8', 'objectItems': [objectItem]]
        converter._0Dot8To0Dot9(ties)
        assert supplementalDescription1['assertionReferenceId'] == 'a'
        assert !('systemExportId' in supplementalDescription1.keySet())
        assert !('assertionReferenceId' in supplementalDescription2.keySet())
    }

    @Test
    void test_renameSupplementalDescriptionSystemExportIdCallTag() {
        def supplementalDescription1 = ['systemExportIdCallTag': 'a']
        def supplementalDescription2 = [:]
        def objectAssertions = ['systemSupplementalDescriptions': [supplementalDescription1, supplementalDescription2]]
        def objectItem = ['objectAssertions': objectAssertions]
        def ties = ['version': '0.8', 'objectItems': [objectItem]]
        converter._0Dot8To0Dot9(ties)
        assert supplementalDescription1['assertionReferenceIdLabel'] == 'a'
        assert !('systemExportIdCallTag' in supplementalDescription1.keySet())
        assert !('assertionReferenceIdLabel' in supplementalDescription2.keySet())
    }

    @Test
    void test_renameSupplementalDescriptionSystemName() {
        def supplementalDescription1 = ['systemName': 'a']
        def supplementalDescription2 = [:]
        def objectAssertions = ['systemSupplementalDescriptions': [supplementalDescription1, supplementalDescription2]]
        def objectItem = ['objectAssertions': objectAssertions]
        def ties = ['version': '0.8', 'objectItems': [objectItem]]
        converter._0Dot8To0Dot9(ties)
        assert supplementalDescription1['system'] == 'a'
        assert !('systemName' in supplementalDescription1.keySet())
        assert !('system' in supplementalDescription2.keySet())
    }

    @Test
    void test_renameSystemSupplementalDescriptions() {
        def objectAssertions1 = ['systemSupplementalDescriptions': []]
        def objectItem1 = ['objectAssertions': objectAssertions1]
        def objectAssertions2 = [:]
        def objectItem2 = ['objectAssertions': objectAssertions2]
        def ties = ['version': '0.8', 'objectItems': [objectItem1, objectItem2]]
        converter._0Dot8To0Dot9(ties)
        assert objectAssertions1['supplementalDescriptions'] == []
        assert !('systemSupplementalDescriptions' in objectAssertions1.keySet())
        assert !('supplementalDescriptions' in objectAssertions2.keySet())
    }

    @Test
    void test_renameObjectRelationshipLinkageSystemIds() {
        def objectRelationship1 = ['linkageSystemIds': ['a', 'a']]
        def objectRelationship2 = [:]
        def ties = ['version': '0.8', 'objectRelationships': [objectRelationship1, objectRelationship2]]
        converter._0Dot8To0Dot9(ties)
        assert objectRelationship1['linkageMemberIds'] == ['a', 'a']
        assert !('linkageSystemIds' in objectRelationship1.keySet())
        assert !('linkageMemberIds' in objectRelationship2.keySet())
    }
}
