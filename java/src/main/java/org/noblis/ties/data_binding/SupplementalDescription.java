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

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SupplementalDescription {
    @JsonProperty("assertionId")
    protected String assertionId;
    @JsonProperty("assertionReferenceId")
    protected String assertionReferenceId;
    @JsonProperty("assertionReferenceIdLabel")
    protected String assertionReferenceIdLabel;
    @JsonProperty("system")
    protected String system;
    @JsonProperty("informationType")
    protected String informationType;
    @JsonProperty("securityTag")
    protected String securityTag;

    @java.lang.SuppressWarnings("all")
    public SupplementalDescription() {
    }

    @java.lang.SuppressWarnings("all")
    public SupplementalDescription(final String assertionId, final String assertionReferenceId, final String assertionReferenceIdLabel, final String system, final String informationType, final String securityTag) {
        this.assertionId = assertionId;
        this.assertionReferenceId = assertionReferenceId;
        this.assertionReferenceIdLabel = assertionReferenceIdLabel;
        this.system = system;
        this.informationType = informationType;
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
    public String getInformationType() {
        return this.informationType;
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
    public void setInformationType(final String informationType) {
        this.informationType = informationType;
    }

    @java.lang.SuppressWarnings("all")
    public void setSecurityTag(final String securityTag) {
        this.securityTag = securityTag;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof SupplementalDescription)) return false;
        final SupplementalDescription other = (SupplementalDescription) o;
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
        final java.lang.Object this$informationType = this.getInformationType();
        final java.lang.Object other$informationType = other.getInformationType();
        if (this$informationType == null ? other$informationType != null : !this$informationType.equals(other$informationType)) return false;
        final java.lang.Object this$securityTag = this.getSecurityTag();
        final java.lang.Object other$securityTag = other.getSecurityTag();
        if (this$securityTag == null ? other$securityTag != null : !this$securityTag.equals(other$securityTag)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof SupplementalDescription;
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
        final java.lang.Object $informationType = this.getInformationType();
        result = result * PRIME + ($informationType == null ? 43 : $informationType.hashCode());
        final java.lang.Object $securityTag = this.getSecurityTag();
        result = result * PRIME + ($securityTag == null ? 43 : $securityTag.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "SupplementalDescription(assertionId=" + this.getAssertionId() + ", assertionReferenceId=" + this.getAssertionReferenceId() + ", assertionReferenceIdLabel=" + this.getAssertionReferenceIdLabel() + ", system=" + this.getSystem() + ", informationType=" + this.getInformationType() + ", securityTag=" + this.getSecurityTag() + ")";
    }
}
