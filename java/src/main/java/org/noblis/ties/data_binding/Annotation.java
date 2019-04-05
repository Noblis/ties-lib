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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"assertionId", "assertionReferenceId", "assertionReferenceIdLabel", "system", "creator", "time", "annotationType", "key", "value", "itemAction", "itemActionTime", "securityTag"})
public class Annotation {
    @JsonProperty("assertionId")
    private String assertionId;
    @JsonProperty("assertionReferenceId")
    private String assertionReferenceId;
    @JsonProperty("assertionReferenceIdLabel")
    private String assertionReferenceIdLabel;
    @JsonProperty("system")
    private String system;
    @JsonProperty("creator")
    private String creator;
    @JsonProperty("time")
    private Date time;
    @JsonProperty("annotationType")
    private String annotationType;
    @JsonProperty("key")
    private String key;
    @JsonProperty("value")
    private String value;
    @JsonProperty("itemAction")
    private String itemAction;
    @JsonProperty("itemActionTime")
    private Date itemActionTime;
    @JsonProperty("securityTag")
    private String securityTag;

    @java.lang.SuppressWarnings("all")
    public Annotation() {
    }

    @java.lang.SuppressWarnings("all")
    public Annotation(final String assertionId, final String assertionReferenceId, final String assertionReferenceIdLabel, final String system, final String creator, final Date time, final String annotationType, final String key, final String value, final String itemAction, final Date itemActionTime, final String securityTag) {
        this.assertionId = assertionId;
        this.assertionReferenceId = assertionReferenceId;
        this.assertionReferenceIdLabel = assertionReferenceIdLabel;
        this.system = system;
        this.creator = creator;
        this.time = time;
        this.annotationType = annotationType;
        this.key = key;
        this.value = value;
        this.itemAction = itemAction;
        this.itemActionTime = itemActionTime;
        this.securityTag = securityTag;
    }

    @java.lang.SuppressWarnings("all")
    public String getAssertionId() {
        return this.assertionId;
    }

    @java.lang.SuppressWarnings("all")
    public String getAssertionReferenceId() {
        return this.assertionReferenceId;
    }

    @java.lang.SuppressWarnings("all")
    public String getAssertionReferenceIdLabel() {
        return this.assertionReferenceIdLabel;
    }

    @java.lang.SuppressWarnings("all")
    public String getSystem() {
        return this.system;
    }

    @java.lang.SuppressWarnings("all")
    public String getCreator() {
        return this.creator;
    }

    @java.lang.SuppressWarnings("all")
    public Date getTime() {
        return this.time;
    }

    @java.lang.SuppressWarnings("all")
    public String getAnnotationType() {
        return this.annotationType;
    }

    @java.lang.SuppressWarnings("all")
    public String getKey() {
        return this.key;
    }

    @java.lang.SuppressWarnings("all")
    public String getValue() {
        return this.value;
    }

    @java.lang.SuppressWarnings("all")
    public String getItemAction() {
        return this.itemAction;
    }

    @java.lang.SuppressWarnings("all")
    public Date getItemActionTime() {
        return this.itemActionTime;
    }

    @java.lang.SuppressWarnings("all")
    public String getSecurityTag() {
        return this.securityTag;
    }

    @java.lang.SuppressWarnings("all")
    public void setAssertionId(final String assertionId) {
        this.assertionId = assertionId;
    }

    @java.lang.SuppressWarnings("all")
    public void setAssertionReferenceId(final String assertionReferenceId) {
        this.assertionReferenceId = assertionReferenceId;
    }

    @java.lang.SuppressWarnings("all")
    public void setAssertionReferenceIdLabel(final String assertionReferenceIdLabel) {
        this.assertionReferenceIdLabel = assertionReferenceIdLabel;
    }

    @java.lang.SuppressWarnings("all")
    public void setSystem(final String system) {
        this.system = system;
    }

    @java.lang.SuppressWarnings("all")
    public void setCreator(final String creator) {
        this.creator = creator;
    }

    @java.lang.SuppressWarnings("all")
    public void setTime(final Date time) {
        this.time = time;
    }

    @java.lang.SuppressWarnings("all")
    public void setAnnotationType(final String annotationType) {
        this.annotationType = annotationType;
    }

    @java.lang.SuppressWarnings("all")
    public void setKey(final String key) {
        this.key = key;
    }

    @java.lang.SuppressWarnings("all")
    public void setValue(final String value) {
        this.value = value;
    }

    @java.lang.SuppressWarnings("all")
    public void setItemAction(final String itemAction) {
        this.itemAction = itemAction;
    }

    @java.lang.SuppressWarnings("all")
    public void setItemActionTime(final Date itemActionTime) {
        this.itemActionTime = itemActionTime;
    }

    @java.lang.SuppressWarnings("all")
    public void setSecurityTag(final String securityTag) {
        this.securityTag = securityTag;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Annotation)) return false;
        final Annotation other = (Annotation) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$assertionId = this.getAssertionId();
        final java.lang.Object other$assertionId = other.getAssertionId();
        if (this$assertionId == null ? other$assertionId != null : !this$assertionId.equals(other$assertionId)) return false;
        final java.lang.Object this$assertionReferenceId = this.getAssertionReferenceId();
        final java.lang.Object other$assertionReferenceId = other.getAssertionReferenceId();
        if (this$assertionReferenceId == null ? other$assertionReferenceId != null : !this$assertionReferenceId.equals(other$assertionReferenceId)) return false;
        final java.lang.Object this$assertionReferenceIdLabel = this.getAssertionReferenceIdLabel();
        final java.lang.Object other$assertionReferenceIdLabel = other.getAssertionReferenceIdLabel();
        if (this$assertionReferenceIdLabel == null ? other$assertionReferenceIdLabel != null : !this$assertionReferenceIdLabel.equals(other$assertionReferenceIdLabel)) return false;
        final java.lang.Object this$system = this.getSystem();
        final java.lang.Object other$system = other.getSystem();
        if (this$system == null ? other$system != null : !this$system.equals(other$system)) return false;
        final java.lang.Object this$creator = this.getCreator();
        final java.lang.Object other$creator = other.getCreator();
        if (this$creator == null ? other$creator != null : !this$creator.equals(other$creator)) return false;
        final java.lang.Object this$time = this.getTime();
        final java.lang.Object other$time = other.getTime();
        if (this$time == null ? other$time != null : !this$time.equals(other$time)) return false;
        final java.lang.Object this$annotationType = this.getAnnotationType();
        final java.lang.Object other$annotationType = other.getAnnotationType();
        if (this$annotationType == null ? other$annotationType != null : !this$annotationType.equals(other$annotationType)) return false;
        final java.lang.Object this$key = this.getKey();
        final java.lang.Object other$key = other.getKey();
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) return false;
        final java.lang.Object this$value = this.getValue();
        final java.lang.Object other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
        final java.lang.Object this$itemAction = this.getItemAction();
        final java.lang.Object other$itemAction = other.getItemAction();
        if (this$itemAction == null ? other$itemAction != null : !this$itemAction.equals(other$itemAction)) return false;
        final java.lang.Object this$itemActionTime = this.getItemActionTime();
        final java.lang.Object other$itemActionTime = other.getItemActionTime();
        if (this$itemActionTime == null ? other$itemActionTime != null : !this$itemActionTime.equals(other$itemActionTime)) return false;
        final java.lang.Object this$securityTag = this.getSecurityTag();
        final java.lang.Object other$securityTag = other.getSecurityTag();
        if (this$securityTag == null ? other$securityTag != null : !this$securityTag.equals(other$securityTag)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Annotation;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $assertionId = this.getAssertionId();
        result = result * PRIME + ($assertionId == null ? 43 : $assertionId.hashCode());
        final java.lang.Object $assertionReferenceId = this.getAssertionReferenceId();
        result = result * PRIME + ($assertionReferenceId == null ? 43 : $assertionReferenceId.hashCode());
        final java.lang.Object $assertionReferenceIdLabel = this.getAssertionReferenceIdLabel();
        result = result * PRIME + ($assertionReferenceIdLabel == null ? 43 : $assertionReferenceIdLabel.hashCode());
        final java.lang.Object $system = this.getSystem();
        result = result * PRIME + ($system == null ? 43 : $system.hashCode());
        final java.lang.Object $creator = this.getCreator();
        result = result * PRIME + ($creator == null ? 43 : $creator.hashCode());
        final java.lang.Object $time = this.getTime();
        result = result * PRIME + ($time == null ? 43 : $time.hashCode());
        final java.lang.Object $annotationType = this.getAnnotationType();
        result = result * PRIME + ($annotationType == null ? 43 : $annotationType.hashCode());
        final java.lang.Object $key = this.getKey();
        result = result * PRIME + ($key == null ? 43 : $key.hashCode());
        final java.lang.Object $value = this.getValue();
        result = result * PRIME + ($value == null ? 43 : $value.hashCode());
        final java.lang.Object $itemAction = this.getItemAction();
        result = result * PRIME + ($itemAction == null ? 43 : $itemAction.hashCode());
        final java.lang.Object $itemActionTime = this.getItemActionTime();
        result = result * PRIME + ($itemActionTime == null ? 43 : $itemActionTime.hashCode());
        final java.lang.Object $securityTag = this.getSecurityTag();
        result = result * PRIME + ($securityTag == null ? 43 : $securityTag.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Annotation(assertionId=" + this.getAssertionId() + ", assertionReferenceId=" + this.getAssertionReferenceId() + ", assertionReferenceIdLabel=" + this.getAssertionReferenceIdLabel() + ", system=" + this.getSystem() + ", creator=" + this.getCreator() + ", time=" + this.getTime() + ", annotationType=" + this.getAnnotationType() + ", key=" + this.getKey() + ", value=" + this.getValue() + ", itemAction=" + this.getItemAction() + ", itemActionTime=" + this.getItemActionTime() + ", securityTag=" + this.getSecurityTag() + ")";
    }
}
