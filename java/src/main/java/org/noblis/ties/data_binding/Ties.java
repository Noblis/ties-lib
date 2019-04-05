// Generated by delombok at Tue Apr 30 16:25:30 EDT 2019
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
package org.noblis.ties.data_binding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"version", "id", "system", "organization", "time", "description", "type", "securityTag", "objectItems", "objectGroups", "objectRelationships", "otherInformation"})
public class Ties {
    @JsonProperty("version")
    private String version;
    @JsonProperty("id")
    private String id;
    @JsonProperty("system")
    private String system;
    @JsonProperty("organization")
    private String organization;
    @JsonProperty("time")
    private Date time;
    @JsonProperty("description")
    private String description;
    @JsonProperty("type")
    private String type;
    @JsonProperty("securityTag")
    private String securityTag;
    @JsonProperty("objectItems")
    private List<ObjectItem> objectItems = null;
    @JsonProperty("objectGroups")
    private List<ObjectGroup> objectGroups = null;
    @JsonProperty("objectRelationships")
    private List<ObjectRelationship> objectRelationships = null;
    @JsonProperty("otherInformation")
    private List<OtherInformation> otherInformation = null;

    @java.lang.SuppressWarnings("all")
    public Ties() {
    }

    @java.lang.SuppressWarnings("all")
    public Ties(final String version, final String id, final String system, final String organization, final Date time, final String description, final String type, final String securityTag, final List<ObjectItem> objectItems, final List<ObjectGroup> objectGroups, final List<ObjectRelationship> objectRelationships, final List<OtherInformation> otherInformation) {
        this.version = version;
        this.id = id;
        this.system = system;
        this.organization = organization;
        this.time = time;
        this.description = description;
        this.type = type;
        this.securityTag = securityTag;
        this.objectItems = objectItems;
        this.objectGroups = objectGroups;
        this.objectRelationships = objectRelationships;
        this.otherInformation = otherInformation;
    }

    @java.lang.SuppressWarnings("all")
    public String getVersion() {
        return this.version;
    }

    @java.lang.SuppressWarnings("all")
    public String getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public String getSystem() {
        return this.system;
    }

    @java.lang.SuppressWarnings("all")
    public String getOrganization() {
        return this.organization;
    }

    @java.lang.SuppressWarnings("all")
    public Date getTime() {
        return this.time;
    }

    @java.lang.SuppressWarnings("all")
    public String getDescription() {
        return this.description;
    }

    @java.lang.SuppressWarnings("all")
    public String getType() {
        return this.type;
    }

    @java.lang.SuppressWarnings("all")
    public String getSecurityTag() {
        return this.securityTag;
    }

    @java.lang.SuppressWarnings("all")
    public List<ObjectItem> getObjectItems() {
        return this.objectItems;
    }

    @java.lang.SuppressWarnings("all")
    public List<ObjectGroup> getObjectGroups() {
        return this.objectGroups;
    }

    @java.lang.SuppressWarnings("all")
    public List<ObjectRelationship> getObjectRelationships() {
        return this.objectRelationships;
    }

    @java.lang.SuppressWarnings("all")
    public List<OtherInformation> getOtherInformation() {
        return this.otherInformation;
    }

    @java.lang.SuppressWarnings("all")
    public void setVersion(final String version) {
        this.version = version;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final String id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setSystem(final String system) {
        this.system = system;
    }

    @java.lang.SuppressWarnings("all")
    public void setOrganization(final String organization) {
        this.organization = organization;
    }

    @java.lang.SuppressWarnings("all")
    public void setTime(final Date time) {
        this.time = time;
    }

    @java.lang.SuppressWarnings("all")
    public void setDescription(final String description) {
        this.description = description;
    }

    @java.lang.SuppressWarnings("all")
    public void setType(final String type) {
        this.type = type;
    }

    @java.lang.SuppressWarnings("all")
    public void setSecurityTag(final String securityTag) {
        this.securityTag = securityTag;
    }

    @java.lang.SuppressWarnings("all")
    public void setObjectItems(final List<ObjectItem> objectItems) {
        this.objectItems = objectItems;
    }

    @java.lang.SuppressWarnings("all")
    public void setObjectGroups(final List<ObjectGroup> objectGroups) {
        this.objectGroups = objectGroups;
    }

    @java.lang.SuppressWarnings("all")
    public void setObjectRelationships(final List<ObjectRelationship> objectRelationships) {
        this.objectRelationships = objectRelationships;
    }

    @java.lang.SuppressWarnings("all")
    public void setOtherInformation(final List<OtherInformation> otherInformation) {
        this.otherInformation = otherInformation;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Ties)) return false;
        final Ties other = (Ties) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$version = this.getVersion();
        final java.lang.Object other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$system = this.getSystem();
        final java.lang.Object other$system = other.getSystem();
        if (this$system == null ? other$system != null : !this$system.equals(other$system)) return false;
        final java.lang.Object this$organization = this.getOrganization();
        final java.lang.Object other$organization = other.getOrganization();
        if (this$organization == null ? other$organization != null : !this$organization.equals(other$organization)) return false;
        final java.lang.Object this$time = this.getTime();
        final java.lang.Object other$time = other.getTime();
        if (this$time == null ? other$time != null : !this$time.equals(other$time)) return false;
        final java.lang.Object this$description = this.getDescription();
        final java.lang.Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) return false;
        final java.lang.Object this$type = this.getType();
        final java.lang.Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final java.lang.Object this$securityTag = this.getSecurityTag();
        final java.lang.Object other$securityTag = other.getSecurityTag();
        if (this$securityTag == null ? other$securityTag != null : !this$securityTag.equals(other$securityTag)) return false;
        final java.lang.Object this$objectItems = this.getObjectItems();
        final java.lang.Object other$objectItems = other.getObjectItems();
        if (this$objectItems == null ? other$objectItems != null : !this$objectItems.equals(other$objectItems)) return false;
        final java.lang.Object this$objectGroups = this.getObjectGroups();
        final java.lang.Object other$objectGroups = other.getObjectGroups();
        if (this$objectGroups == null ? other$objectGroups != null : !this$objectGroups.equals(other$objectGroups)) return false;
        final java.lang.Object this$objectRelationships = this.getObjectRelationships();
        final java.lang.Object other$objectRelationships = other.getObjectRelationships();
        if (this$objectRelationships == null ? other$objectRelationships != null : !this$objectRelationships.equals(other$objectRelationships)) return false;
        final java.lang.Object this$otherInformation = this.getOtherInformation();
        final java.lang.Object other$otherInformation = other.getOtherInformation();
        if (this$otherInformation == null ? other$otherInformation != null : !this$otherInformation.equals(other$otherInformation)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Ties;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $version = this.getVersion();
        result = result * PRIME + ($version == null ? 43 : $version.hashCode());
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $system = this.getSystem();
        result = result * PRIME + ($system == null ? 43 : $system.hashCode());
        final java.lang.Object $organization = this.getOrganization();
        result = result * PRIME + ($organization == null ? 43 : $organization.hashCode());
        final java.lang.Object $time = this.getTime();
        result = result * PRIME + ($time == null ? 43 : $time.hashCode());
        final java.lang.Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final java.lang.Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final java.lang.Object $securityTag = this.getSecurityTag();
        result = result * PRIME + ($securityTag == null ? 43 : $securityTag.hashCode());
        final java.lang.Object $objectItems = this.getObjectItems();
        result = result * PRIME + ($objectItems == null ? 43 : $objectItems.hashCode());
        final java.lang.Object $objectGroups = this.getObjectGroups();
        result = result * PRIME + ($objectGroups == null ? 43 : $objectGroups.hashCode());
        final java.lang.Object $objectRelationships = this.getObjectRelationships();
        result = result * PRIME + ($objectRelationships == null ? 43 : $objectRelationships.hashCode());
        final java.lang.Object $otherInformation = this.getOtherInformation();
        result = result * PRIME + ($otherInformation == null ? 43 : $otherInformation.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Ties(version=" + this.getVersion() + ", id=" + this.getId() + ", system=" + this.getSystem() + ", organization=" + this.getOrganization() + ", time=" + this.getTime() + ", description=" + this.getDescription() + ", type=" + this.getType() + ", securityTag=" + this.getSecurityTag() + ", objectItems=" + this.getObjectItems() + ", objectGroups=" + this.getObjectGroups() + ", objectRelationships=" + this.getObjectRelationships() + ", otherInformation=" + this.getOtherInformation() + ")";
    }
}